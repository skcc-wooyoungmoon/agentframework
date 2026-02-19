import { agentAtom, edgesAtom, keyTableAtom, nodesAtom } from '@/components/builder/atoms/AgentAtom';
import {
  type Agent,
  type AgentAppDataSchema,
  type AgentGraph,
  type CategorizerDataSchema,
  type CoderDataSchema,
  type ConditionDataSchema,
  type ContextRefinerDataSchema,
  type CustomEdge,
  type CustomNode,
  type CustomNodeInnerData,
  type EdgeParams,
  EXCLUDE_KEY_TABLE_TYPE,
  type GeneratorDataSchema,
  type InputKeyItem,
  type InputNodeDataSchema,
  type KeyTableData,
  NodeType,
  type OutputKeyItem,
  type OutputKeysDataSchema,
  type QueryRewriterDataSchema,
  type ReACTorDataSchema,
  type RetrieverDataSchema,
  type ToolDataSchema,
  type UnionDataSchema,
} from '@/components/builder/types/Agents';
import { EDGE_TYPE_CURVE, generateNodeName, getNodeId, setupNodeData } from '@/components/builder/utils/GraphUtils.ts';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { useUpdateAgentBuilder } from '@/services/agent/builder2/agentBuilder.services';
import { useAtom } from 'jotai/index';
import { useEffect, useRef } from 'react';

import { ConditionalEdge, NoneConditionalEdge, RecursiveEdge } from '@/components/builder/pages/graph/edge';

export const useGraphActions = () => {
  const [nodes, setNodes] = useAtom(nodesAtom);
  const nodesUpdatedRef = useRef(false);
  const [edges, setEdges] = useAtom(edgesAtom);

  const [agent, setAgent] = useAtom(agentAtom);

  const keyTableUpdatedRef = useRef(false);
  const [keyTableList, setKeyTableList] = useAtom(keyTableAtom);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      nodesUpdatedRef.current = false;
    }

    if (keyTableUpdatedRef.current) {
      keyTableUpdatedRef.current = false;
    }
  }, [nodes, edges, keyTableList]);

  const updateAgent = (data: Agent) => {
    setAgent(data);
  };

  // useUpdateAgentBuilder 훅 사용 (putAgent 대신)
  const { mutateAsync: updateAgentBuilder } = useUpdateAgentBuilder();

  const getAgentData = (keyTableList: KeyTableData[]) => {
    if (agent) {
      const orgNodes = checkInputNodeInputKeyChange(keyTableList);

      const agentData: AgentGraph = {
        id: agent.id,
        name: agent.name,
        description: agent.description,
        graph_config: agent.graph_config,
        graph: {
          nodes: orgNodes,
          edges: edges,
        },
      };

      return agentData;
    } else {
      return null;
    }
  };

  const checkInputNodeInputKeyChange = (keyTableList: KeyTableData[]): CustomNode[] => {
    let newNodes: CustomNode[] = [];

    nodes.forEach((node: CustomNode) => {
      if (node.type === NodeType.Input.name) {
        // @ts-ignore
        if (node.data.input_keys && node.data.input_keys.length > 0) {
          // @ts-ignore
          node.data.input_keys = node.data.input_keys.filter((item: InputKeyItem) => item.name !== null && item.name !== '');
        }
        newNodes.push(node);
      } else if (node.type === NodeType.OutputFormatter.name) {
        const formatString = (node.data.format_string as string) ?? '';
        // 정규식으로 {{}}로 감싸진 값 추출
        // noinspection RegExpRedundantEscape
        const matches = formatString.match(/\{\{(.*?)\}\}/g) || [];

        // 중괄호를 제외하고 내부 값만 추출
        const extractedValues = matches.map(match => match.slice(2, -2));
        const keyList = keyTableList.map(key => key.id);
        const filteredValues = extractedValues.filter(value => !keyList.includes(value));

        let updatedFormatString = formatString;

        filteredValues.forEach(value => {
          updatedFormatString = updatedFormatString.replace(`{{${value}}}`, '');
        });
        node.data.format_string = updatedFormatString;
        newNodes.push(node);
      } else {
        newNodes.push(node);
      }
    });

    return newNodes;
  };

  const saveAgent = async (keyTableList: KeyTableData[]) => {
    const agentData: AgentGraph | null = getAgentData(keyTableList);

    if (agentData === null) {
      return null;
    }

    const response = await updateAgentBuilder({ id: agentData.id, ...agentData });

    return response.data;
  };

  const createNewNode = (type: string, position: { x: number; y: number }, currentNodes: CustomNode[]): CustomNode | undefined => {
    if (type === NodeType.Input.name && currentNodes.find(node => node.type === type)) {
      return undefined;
    }

    if (type === NodeType.OutputSelector.name || type === NodeType.OutputFormatter.name) {
      if (currentNodes.find(node => node.type === NodeType.OutputSelector.name || node.type === NodeType.OutputFormatter.name)) {
        return undefined;
      }
    }

    nodesUpdatedRef.current = true;

    const newId = getNodeId();
    const nodeName = generateNodeName(type, currentNodes);

    // nodeData 생성
    const nodeData = setupNodeData(type, nodeName, newId);

    return {
      id: newId,
      type: type,
      position: position,
      source_position: 'left',
      target_position: 'right',
      style: {},
      data: {
        ...nodeData,
        innerData: {
          isRun: false,
          isDone: false,
          isError: false,
          isToggle: false,
        },
      },
    };
  };

  const addNode = (data: CustomNode) => {
    setNodes(prevNodes => {
      const isDuplicate = prevNodes.some(node => node.id === data.id);
      if (isDuplicate) {
        return prevNodes;
      }

      // input_keys의 required 속성 보정 (백엔드에서 불러온 노드의 경우 required가 없을 수 있음)
      let normalizedNode = data;
      if (data.type && data.data?.input_keys && Array.isArray(data.data.input_keys)) {
        const nodeConfig = (keyTableData as any)[data.type];
        if (nodeConfig && nodeConfig.input_keys) {
          const normalizedInputKeys = data.data.input_keys.map((inputKey: InputKeyItem, index: number) => {
            // keyTableData.json에서 해당 인덱스의 required 값 확인
            const configInput = nodeConfig.input_keys[index];
            if (configInput && configInput.key === inputKey.name) {
              // required 속성이 없거나 다르면 keyTableData.json의 값으로 보정
              return {
                ...inputKey,
                required: configInput.required || inputKey.required || false,
              };
            }
            // 인덱스로 매칭되지 않으면 name으로 찾기
            const matchedConfig = nodeConfig.input_keys.find((config: any) => config.key === inputKey.name);
            if (matchedConfig) {
              return {
                ...inputKey,
                required: matchedConfig.required || inputKey.required || false,
              };
            }
            return inputKey;
          });

          normalizedNode = {
            ...data,
            data: {
              ...data.data,
              input_keys: normalizedInputKeys,
            },
          };
        }
      }

      return normalizedNode ? [...prevNodes, normalizedNode] : prevNodes;
    });
  };

  const addNewNode = (type: string, position: any = null) => {
    const initPosition = {
      x: 0,
      y: 0,
    };

    let createdNode: CustomNode | undefined;

    setNodes(prevNodes => {
      const newNode = createNewNode(type, position ? position : initPosition, prevNodes);
      if (newNode != undefined) {
        createdNode = newNode;
        return [...prevNodes, newNode];
      } else {
        return prevNodes;
      }
    });

    if (createdNode && !EXCLUDE_KEY_TABLE_TYPE.includes(createdNode.type)) {
      setKeyTable(createdNode);
    }

    nodesUpdatedRef.current = true;
    keyTableUpdatedRef.current = true;
  };

  // 엣지 추가
  const addEdge = (newEdge: CustomEdge) => {
    setEdges(edges => {
      const isDuplicate = edges.some(edge => edge.id === newEdge.id);
      if (isDuplicate) {
        return edges;
      }

      const existingLabel = newEdge.label || newEdge.condition_label;

      const edgeParams: EdgeParams = {
        ...newEdge,
        condition_label: existingLabel,
        data: {
          ...newEdge.data,
          category: newEdge.data?.category,
        },
      };

      let processedEdge: CustomEdge;

      if (newEdge.type === 'case') {
        if (newEdge.condition_label === 'handle-condition-pass' || newEdge.condition_label === 'handle-condition-fail') {
          processedEdge = RecursiveEdge(edgeParams);
        } else {
          processedEdge = ConditionalEdge(edgeParams, EDGE_TYPE_CURVE);
        }
      } else {
        processedEdge = NoneConditionalEdge(edgeParams, EDGE_TYPE_CURVE);
      }

      return processedEdge ? [...edges, processedEdge] : edges;
    });
  };

  // 노드 삭제
  const removeNode = (nodeId: string) => {
    // 삭제될 노드의 keyTable 정보 수집
    const nodeKeyTableEntries = keyTableList.filter(entry => entry.nodeId === nodeId);
    const keyTableIdsToRemove = nodeKeyTableEntries.map(entry => entry.id);

    setNodes(prevNodes => {
      const filteredNodes = prevNodes
        .map(node => {
          if (node.id === nodeId) return node;

          if (hasInputKeys(node.data) && node.data.input_keys) {
            const updatedInputKeys = node.data.input_keys.map(inputKey => {
              if (inputKey.keytable_id && keyTableIdsToRemove.includes(inputKey.keytable_id)) {
                return {
                  ...inputKey,
                  keytable_id: '', // keyTable 참조 제거
                  fixed_value: null,
                };
              }
              return inputKey;
            });

            return {
              ...node,
              data: {
                ...node.data,
                input_keys: updatedInputKeys,
              },
            };
          }
          return node;
        })
        .filter(node => node.id !== nodeId); // 마지막에 대상 노드를 제거

      // 노드 삭제 후 키테이블 재동기화 (글로벌 키 재계산을 위해)
      // filteredNodes를 기반으로 키테이블 재계산
      if (filteredNodes.length === 0) {
        setKeyTableList([]);
      } else {
        // 모든 노드의 키테이블을 재계산 (글로벌 키 재계산을 위해)
        setKeyTableList(() => {
          // 남은 노드들의 일반 키 재생성
          const normalKeys: KeyTableData[] = [];
          
          filteredNodes.forEach(node => {
            if (node.type && !EXCLUDE_KEY_TABLE_TYPE.includes(node.type)) {
              const nodeData = node.data || {};
              const isInputNodeType = node.data.type == 'input__basic';
              
              // input_keys가 있는 경우 처리 (InputNode)
              if (hasInputKeys(nodeData) && isInputNodeType) {
                const inputKeys: InputKeyItem[] = nodeData.input_keys ?? [];
                inputKeys
                  .filter(item => item.name !== '')
                  .forEach(item => {
                    const isGlobalKeytable = item.keytable_id && String(item.keytable_id).endsWith('__global');
                    normalKeys.push({
                      id: isGlobalKeytable ? item.name + '__' + node.id : (item.keytable_id ?? item.name + '__' + node.id),
                      name: node.data.name + '_' + item.name,
                      key: item.name,
                      isGlobal: false,
                      value: item.fixed_value ?? '',
                      nodeId: node.id,
                      nodeType: node.type || '',
                      nodeName: node.data.name as string,
                      node,
                    });
                  });
              }
              
              // output_keys가 있는 경우 처리
              if (hasOutputKeys(nodeData) && !isInputNodeType) {
                const outputKeyItems: OutputKeyItem[] = nodeData.output_keys ?? [];
                outputKeyItems
                  .filter(item => item != null && item.name !== '')
                  .forEach(item => {
                    const isGlobalKeytable = item.keytable_id && String(item.keytable_id).endsWith('__global');
                    normalKeys.push({
                      id: isGlobalKeytable ? item.name + '__' + node.id : (item.keytable_id ?? item.name + '__' + node.id),
                      name: node.data.name + '_' + item.name,
                      key: item.name,
                      value: '',
                      nodeId: node.id,
                      nodeType: node.type || '',
                      nodeName: node.data.name as string,
                      node,
                    });
                  });
              }
            }
          });
          
          // 모든 일반 키의 key 값을 수집하여 글로벌 키 생성
          const uniqueKeys = Array.from(new Set(normalKeys.map(item => item.key)));
          const globalKeyEntries = uniqueKeys.map(key => {
            const globalId = key + '__global';
            return {
              id: globalId,
              name: key + '__global',
              key: key,
              value: '',
              nodeId: '',
              nodeType: '',
              nodeName: '',
              isGlobal: true,
              node: null,
            };
          });
          
          // 일반 키 + 글로벌 키 반환
          return [...normalKeys, ...globalKeyEntries];
        });
      }

      return filteredNodes;
    });

    setEdges(edges => edges.filter(edge => edge.source !== nodeId && edge.target !== nodeId));

    nodesUpdatedRef.current = true;
    keyTableUpdatedRef.current = true;
  };

  // 엣지 삭제
  const removeEdge = (edgeId: string) => {
    setEdges(edges => edges.filter(edge => edge.id !== edgeId));
  };

  const toggleNodeView = (id: string, isToggle: boolean) => {
    setNodes(prevNodes =>
      prevNodes.map(node =>
        node.id === id
          ? {
              ...node,
              data: {
                ...node.data,
                innerData: {
                  ...node.data.innerData,
                  isToggle: isToggle,
                },
              },
            }
          : node
      )
    );
    nodesUpdatedRef.current = true;
  };

  const syncNodeData = (id: string, data: any) => {
    setNodes(prevNodes =>
      prevNodes.map(node => {
        if (node.id === id) {
          return {
            ...node,
            data: data,
          };
        }
        return node;
      })
    );
    nodesUpdatedRef.current = true;
  };

  const syncAllNodeKeyTable = () => {
    if (nodes.length === 0) {
      setKeyTableList([]);
      return;
    }

    nodes.forEach(node => {
      if (node.type && !EXCLUDE_KEY_TABLE_TYPE.includes(node.type)) {
        setKeyTable(node);
      }
    });
  };

  // input_keys가 있는지 확인하는 타입 가드
  function hasInputKeys(
    data: CustomNode['data']
  ): data is { innerData: CustomNodeInnerData } & (
    | InputNodeDataSchema
    | OutputKeysDataSchema
    | RetrieverDataSchema
    | QueryRewriterDataSchema
    | ContextRefinerDataSchema
    | GeneratorDataSchema
    | AgentAppDataSchema
    | CoderDataSchema
    | ReACTorDataSchema
    | CategorizerDataSchema
    | ToolDataSchema
    | ConditionDataSchema
    | UnionDataSchema
  ) {
    return 'input_keys' in data && Array.isArray(data.input_keys);
  }

  // output_keys가 있는지 확인하는 타입 가드
  function hasOutputKeys(
    data: CustomNode['data']
  ): data is { innerData: CustomNodeInnerData } & (
    | RetrieverDataSchema
    | QueryRewriterDataSchema
    | ContextRefinerDataSchema
    | GeneratorDataSchema
    | AgentAppDataSchema
    | CoderDataSchema
    | ReACTorDataSchema
    | CategorizerDataSchema
    | ToolDataSchema
    | ConditionDataSchema
    | UnionDataSchema
  ) {
    return 'output_keys' in data && Array.isArray(data.output_keys);
  }

  const setKeyTable = (node: CustomNode) => {
    const nodeData = node.data || {};

    if (!hasInputKeys(nodeData) && !hasOutputKeys(nodeData)) {
      return;
    }

    //input타입인 경우는 input keys를 keytable에 추가
    const isInputNodeType = node.data.type == 'input__basic';

    setKeyTableList(prevList => {
      // input_keys가 있는 경우 처리
      if (hasInputKeys(nodeData) && isInputNodeType) {
        const inputKeys: InputKeyItem[] = nodeData.input_keys ?? [];
        const newKeyEntries = inputKeys
          .filter(item => item.name !== '')
          .map(item => {
            const isGlobalKeytable = item.keytable_id && String(item.keytable_id).endsWith('__global');
            return {
              id: isGlobalKeytable ? item.name + '__' + node.id : (item.keytable_id ?? item.name + '__' + node.id),
              name: node.data.name + '_' + item.name,
              key: item.name,
              isGlobal: false,
              value: item.fixed_value ?? '',
              nodeId: node.id,
              nodeType: node.type || '',
              nodeName: node.data.name as string,
              node,
            };
          });

        const updatedList = [...prevList.filter(item => item.nodeId !== node.id && item.isGlobal !== true), ...newKeyEntries];
        const uniqueKeys = Array.from(new Set(updatedList.map(item => item.key)));
        const globalKeyEntries = uniqueKeys.map(key => {
          const globalId = (key ?? '') + '__global';
          return {
            id: globalId,
            name: key + '__global',
            key: key,
            value: '',
            nodeId: '',
            nodeType: '',
            nodeName: '',
            isGlobal: true,
            node: null,
          };
        });

        // 일반 + 글로벌 합쳐서 반환
        return [...updatedList, ...globalKeyEntries];
      }

      // output_keys가 있는 경우 처리
      if (hasOutputKeys(nodeData) && !isInputNodeType) {
        const outputKeys: OutputKeyItem[] = nodeData.output_keys ?? [];
        const newKeyEntries = outputKeys
          .filter(item => item != null && item.name !== '')
          .map(item => {
            const isGlobalKeytable = item.keytable_id && String(item.keytable_id).endsWith('__global');
            return {
              id: isGlobalKeytable ? item.name + '__' + node.id : (item.keytable_id ?? item.name + '__' + node.id),
              name: node.data.name + '_' + item.name,
              key: item.name,
              value: '',
              nodeId: node.id,
              nodeType: node.type || '',
              nodeName: node.data.name as string,
              node,
            };
          });

        const updatedList = [...prevList.filter(item => item.nodeId !== node.id && item.isGlobal !== true), ...newKeyEntries];
        const uniqueKeys = Array.from(new Set(updatedList.map(item => item.key)));
        const globalKeyEntries = uniqueKeys.map(key => {
          const globalId = (key ?? '') + '__global';
          return {
            id: globalId,
            name: key + '__global',
            key: key,
            value: '',
            nodeId: '',
            nodeType: '',
            nodeName: '',
            isGlobal: true,
            node: null,
          };
        });

        // 일반 + 글로벌 합쳐서 반환
        return [...updatedList, ...globalKeyEntries];
      }

      return prevList;
    });

    keyTableUpdatedRef.current = true;
  };

  return {
    addNode,
    addNewNode,
    addEdge,
    removeNode,
    removeEdge,
    createNewNode,
    toggleNodeView,
    nodes,
    syncNodeData,
    agent,
    updateAgent,
    getAgentData,
    saveAgent,
    setKeyTable,
    syncAllNodeKeyTable,
    hasOutputKeys,
    hasInputKeys,
  };
};