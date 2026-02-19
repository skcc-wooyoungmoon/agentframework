import { agentAtom, edgesAtom, keyTableAtom, nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import {
  type Agent,
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
  type MergerDataSchema,
  NodeType,
  type OutputKeyItem,
  type OutputKeysDataSchema,
  type QueryRewriterDataSchema,
  type ReACTorDataSchema,
  type RetrieverDataSchema,
  type ToolDataSchema,
} from '@/components/agents/builder/types/Agents';
import { EDGE_TYPE_CURVE, generateNodeName, getNodeId, setupNodeData } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useUpdateAgentBuilder } from '@/services/agent/builder/agentBuilder.services';
import { useReactFlow } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import { useCallback, useEffect, useRef } from 'react';

import { useCustomToast } from '@/components/agents/builder/common/toast/useCustomToast.tsx';
import { ConditionalEdge, NoneConditionalEdge } from '@/components/agents/builder/pages/graph/edge';

export const useGraphActions = () => {
  const [nodes, setNodes] = useAtom(nodesAtom);
  const nodesUpdatedRef = useRef(false);
  const [edges, setEdges] = useAtom(edgesAtom);

  const [agent, setAgent] = useAtom(agentAtom);
  const lastStableGraphNameRef = useRef<string>('');

  const keyTableUpdatedRef = useRef(false);
  const [keyTableList, setKeyTableList] = useAtom(keyTableAtom);
  const { showToast } = useCustomToast();
  const { getNodes, getEdges, addNodes, deleteElements, setNodes: setReactFlowNodes } = useReactFlow();

  const { mutateAsync: updateAgentBuilder } = useUpdateAgentBuilder();

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

  const getAgentData = (keyTableList: KeyTableData[], currentNodes?: any[], currentEdges?: any[]) => {
    if (agent) {
      const nodesToUse = currentNodes || nodes;
      const reactFlowEdges = getEdges();
      const edgesToUse = currentEdges || reactFlowEdges || edges;
      const orgNodes = checkInputNodeInputKeyChange(keyTableList, nodesToUse);
      const cleanNodes = orgNodes.map((node: any) => {
        if (node.position && (typeof node.position.x === 'number' || typeof node.position.y === 'number')) {
          return {
            ...node,
            position: {
              x: Math.round(node.position.x || 0),
              y: Math.round(node.position.y || 0),
            },
          };
        }
        return node;
      });

      const cleanEdges = edgesToUse.map((edge: any) => {
        const sourceNode = cleanNodes.find((n: any) => String(n.id) === String(edge.source));
        const targetNode = cleanNodes.find((n: any) => String(n.id) === String(edge.target));

        let finalSourceHandle = edge.sourceHandle || edge.source_handle || null;
        let finalConditionLabel = edge.condition_label || null;

        if (sourceNode?.type === 'input__basic' && targetNode?.type === 'condition') {
          finalSourceHandle = null;
          finalConditionLabel = null;
        }
        else if ((sourceNode?.type === 'agent__generator' || sourceNode?.type === 'union') && targetNode?.type === 'union') {
          if (!finalSourceHandle) {
            finalSourceHandle = 'gen_right';
          }
          if (!finalConditionLabel) {
            finalConditionLabel = 'gen_right';
          }
        }
        else if (sourceNode?.type === 'union' && targetNode?.type === 'agent__reviewer') {
          if (!finalSourceHandle) {
            finalSourceHandle = 'gen_right';
          }
          if (!finalConditionLabel) {
            finalConditionLabel = 'gen_right';
          }
        }
        else if (sourceNode?.type === 'condition') {
          if (!edge) return;
          const nodeId = sourceNode?.id || edge.source;

          switch (edge?.label) {
            case edge?.name:
              const conditionPart = edge?.name?.split('-').pop();
              const conditionNumber = Number(conditionPart);

              if (isNaN(conditionNumber)) {
                finalConditionLabel = `${nodeId}-condition-else`;
              } else {
                finalConditionLabel = `${nodeId}-${edge?.name}`;
              }
              break;

            case 'condition-else':
              finalConditionLabel = `${nodeId}-condition-else`;
              break;
          }

        } else if (sourceNode?.type === 'agent__reviewer') {
          const currentSourceHandle = edge.sourceHandle || edge.source_handle;

          if (currentSourceHandle === 'reviewer_pass' || currentSourceHandle === 'handle-condition-pass' || currentSourceHandle === 'handle-pass') {
            finalSourceHandle = 'handle-condition-pass';
            finalConditionLabel = 'pass';
          } else if (currentSourceHandle === 'reviewer_fail' || currentSourceHandle === 'handle-condition-fail' || currentSourceHandle === 'handle-fail') {
            finalSourceHandle = 'handle-condition-fail';
            finalConditionLabel = 'fail';
          }
        }

        let finalEdgeType = edge.type || edge.data?.edge_type || 'none';
        let finalLabel = edge.label;

        if (sourceNode?.type === 'agent__reviewer') {
          finalEdgeType = 'case';

          if (finalSourceHandle === 'handle-condition-pass') {
            finalLabel = 'condition-pass';
          } else if (finalSourceHandle === 'handle-condition-fail') {
            finalLabel = 'condition-fail';
          }
        }
        const resultEdge: any = {
          id: edge.id,
          type: finalEdgeType, // Reviewer 엣지는 "case" 타입
          label: finalLabel, // Reviewer 엣지는 "condition-pass" 또는 "condition-fail"
          source: edge.source,
          target: edge.target,
          sourceHandle: finalSourceHandle,
          source_handle: finalSourceHandle,
          target_handle: null, // 샘플 구조: target_handle은 항상 null
          condition_label: finalConditionLabel,
          style: edge.style,
          markerEnd: edge.markerEnd,
          marker_end: edge.marker_end || null,
          marker_start: edge.marker_start || null,
          reconnectable: edge.reconnectable || null,
          dragging: false,
          data: {
            category: edge.data?.category,
            send_from: edge.data?.send_from || edge.source,
            send_to: edge.data?.send_to || edge.target,
            edge_type: finalEdgeType,
          },
        };

        return resultEdge;
      });

      const finalCleanEdges = cleanEdges.map((edge: any) => {
        const cleanedEdge = { ...edge };
        if (cleanedEdge.targetHandle === null || cleanedEdge.targetHandle === undefined) {
          delete cleanedEdge.targetHandle;
        }
        return cleanedEdge;
      });

      const agentData: AgentGraph = {
        id: agent.id,
        name: agent.name,
        description: agent.description,
        ...(agent.graph_config ? { graph_config: agent.graph_config } : {}),
        graph: {
          edges: finalCleanEdges,
          nodes: cleanNodes,
        },
      };

      return agentData;
    } else {
      return null;
    }
  };

  const checkInputNodeInputKeyChange = (keyTableList: KeyTableData[], inputNodes: CustomNode[] = nodes): CustomNode[] => {
    let newNodes: CustomNode[] = [];

    inputNodes.forEach((node: CustomNode) => {
      if (node.type === NodeType.Input.name) {
        const inputKeys = node.data.input_keys as InputKeyItem[];

        if (inputKeys && inputKeys.length > 0) {
          node.data.input_keys = inputKeys.filter((item: InputKeyItem) => item.name !== null && item.name !== '');
        }
        newNodes.push(node);
      } else if (node.type === NodeType.OutputFormatter.name || node.type === 'output__chat') {
        const formatString = (node.data.format_string as string) ?? '';
        const matches = formatString.match(/\{\{(.*?)\}\}/g) || [];

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
    syncAllNodeKeyTable();
    await new Promise(resolve => setTimeout(resolve, 50));

    const currentNodes = nodes;

    const nodesWithCorrectPositions = currentNodes;

    const currentEdges = edges;
    const agentData: AgentGraph | null = getAgentData(keyTableList, nodesWithCorrectPositions, currentEdges);

    if (agentData === null || !agent?.id) {
      showToast({
        text: '저장할 데이터가 없거나 에이전트 ID가 없습니다.',
        status: 'error',
        title: '저장 실패',
      });
      return false;
    }

    try {
      const requestPayload = {
        agentId: agent.id,
        ...agentData,
      };
      const response = await updateAgentBuilder(requestPayload as any);
      const responseData = (response as any)?.data || response;

      if (responseData && (responseData.code === 1 || responseData.success === true || (response as any)?.success === true)) {
        const responseTimestamp = (response as any)?.timestamp || (responseData as any)?.timestamp || null;
        const responseUpdatedAt = (responseData as any)?.updatedAt || (responseData as any)?.updated_at || null;

        setAgent(prev => {
          if (!prev) {
            return prev;
          }

          const nextName = agentData?.name ?? prev.name;
          const nextDescription = agentData?.description ?? prev.description;
          const prevInnerData = (prev as any)?.innerData ?? {};
          const nextInnerData = {
            ...prevInnerData,
            stableGraphName: agentData?.name ?? prevInnerData?.stableGraphName ?? '',
          };

          const finalUpdatedAt = responseTimestamp || responseUpdatedAt || prev.updated_at;

          return {
            ...prev,
            name: nextName,
            description: nextDescription,
            updated_at: finalUpdatedAt,
            innerData: nextInnerData,
          } as Agent;
        });

        return true;
      } else {
        const responseData = (response as any)?.data || response;
        const errorMessage = (responseData as any)?.detail || (responseData as any)?.message || (response as any)?.message || '알 수 없는 오류';
        showToast({
          text: `오류: ${errorMessage}`,
          status: 'error',
          title: 'Agent 저장에 실패 했습니다.',
        });
        return false;
      }
    } catch (error: any) {
      let errorMessage = '알 수 없는 오류';

      if (error?.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error?.response?.data?.detail) {
        errorMessage = error.response.data.detail;
      } else if (error?.message) {
        errorMessage = error.message;
      } else if (typeof error === 'string') {
        errorMessage = error;
      }

      showToast({
        text: `오류: ${errorMessage}`,
        status: 'error',
        title: '예상치 못한 오류가 발생했습니다.',
      });

      return false;
    }
  };

  const createNewNode = (type: string, position: { x: number; y: number }, currentNodes: CustomNode[]): CustomNode | undefined => {
    if (type === NodeType.Input.name && currentNodes.find(node => node.type === type)) {
      return undefined;
    }

    nodesUpdatedRef.current = true;

    const newId = getNodeId();
    const nodeName = generateNodeName(type, currentNodes);
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
      return data ? [...prevNodes, data] : prevNodes;
    });
  };

  const addNewNode = (type: string, position: any = null) => {
    const currentNodes = getNodes();
    const isOutputNodeType = type === 'output__chat' || type === 'output__keys' || type === 'output__formatter' || type === 'output__selector';
    if (isOutputNodeType) {
      const hasExistingOutputNode = currentNodes.some(
        node => node.type === 'output__chat' || node.type === 'output__keys' || node.type === 'output__formatter' || node.type === 'output__selector'
      );
      if (hasExistingOutputNode) {
        return;
      }
    }

    const initPosition = {
      x: 0,
      y: 0,
    };

    let adjustedPosition = position ? position : initPosition;

    if (!position) {
      adjustedPosition = { x: 100, y: 100 };
    }

    const newNode = createNewNode(type, adjustedPosition, currentNodes as CustomNode[]);

    if (newNode != undefined) {
      addNodes(newNode);

      setNodes(prevNodes => {
        const existingIds = new Set(prevNodes.map(node => node.id));
        if (!existingIds.has(newNode.id)) {
          return [...prevNodes, newNode];
        }
        return prevNodes;
      });

      if (!EXCLUDE_KEY_TABLE_TYPE.includes(newNode.type)) {
        setKeyTable(newNode);
      }

      nodesUpdatedRef.current = true;
      keyTableUpdatedRef.current = true;
    }
  };

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
          send_from: newEdge.data?.send_from || newEdge.source,
          send_to: newEdge.data?.send_to || newEdge.target,
          edge_type: newEdge.data?.edge_type || newEdge.type || 'none',
        },
      };

      let processedEdge: CustomEdge;

      if (newEdge.type === 'case') {
        processedEdge = ConditionalEdge(edgeParams, EDGE_TYPE_CURVE);
      } else {
        processedEdge = NoneConditionalEdge(edgeParams, EDGE_TYPE_CURVE);
      }

      return processedEdge ? [...edges, processedEdge] : edges;
    });
  };

  const removeNode = (nodeId: string) => {
    const nodeToDelete = nodes.find(node => node.id === nodeId);

    if (!nodeToDelete) {
      return;
    }

    const nodeKeyTableEntries = keyTableList.filter(entry => entry.nodeId === nodeId);
    const keyTableIdsToRemove = nodeKeyTableEntries.map(entry => entry.id);

    deleteElements({
      nodes: [{ id: nodeId }],
    });

    setNodes(prevNodes => {
      const filteredNodes = prevNodes.filter(node => node.id !== nodeId);

      const cleanedResult = filteredNodes.map(node => {
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
      });

      return cleanedResult;
    });

    setEdges(prevEdges => {
      const filteredEdges = prevEdges.filter(edge => edge.source !== nodeId && edge.target !== nodeId);
      return filteredEdges;
    });

    setKeyTableList(prevKeyTableList => {
      const filteredList = prevKeyTableList.filter(keyTable => keyTable.nodeId !== nodeId);
      return filteredList;
    });

    nodesUpdatedRef.current = true;
    keyTableUpdatedRef.current = true;
  };

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
    setReactFlowNodes(prevNodes =>
      prevNodes.map(node =>
        node.id === id
          ? {
            ...node,
            data: {
              ...node.data,
              innerData: {
                ...(node.data.innerData && typeof node.data.innerData === 'object' ? node.data.innerData : {}),
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
    setNodes(prevNodes => {
      const updatedNodes = prevNodes.map(node => {
        if (node.id === id) {
          const updatedNode = {
            ...node,
            data: data,
          };

          return updatedNode;
        }
        return node;
      });

      return updatedNodes;
    });
    nodesUpdatedRef.current = true;
  };

  const syncAllNodeKeyTable = () => {
    const existingNodeIds = new Set(nodes.map(n => n.id));

    setKeyTableList(prevList => {
      const filteredList = prevList.filter(item => {
        if (item.isGlobal) return true;
        return existingNodeIds.has(item.nodeId);
      });

      const validKeys = filteredList.filter(item => !item.isGlobal && existingNodeIds.has(item.nodeId)).map(item => item.key);
      const uniqueKeys = Array.from(new Set(validKeys));

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

      const listWithoutGlobal = filteredList.filter(item => !item.isGlobal);
      return [...listWithoutGlobal, ...globalKeyEntries];
    });

    nodes.forEach(node => {
      if (node.type && !EXCLUDE_KEY_TABLE_TYPE.includes(node.type)) {
        setKeyTable(node);
      }
    });
  };

  function hasInputKeys(
    data: CustomNode['data']
  ): data is { innerData: CustomNodeInnerData } & (
    | InputNodeDataSchema
    | OutputKeysDataSchema
    | RetrieverDataSchema
    | QueryRewriterDataSchema
    | ContextRefinerDataSchema
    | GeneratorDataSchema
    | CoderDataSchema
    | ReACTorDataSchema
    | CategorizerDataSchema
    | ToolDataSchema
    | ConditionDataSchema
    | MergerDataSchema
  ) {
    return 'input_keys' in data && Array.isArray(data.input_keys);
  }

  function hasOutputKeys(
    data: CustomNode['data']
  ): data is { innerData: CustomNodeInnerData } & (
    | RetrieverDataSchema
    | QueryRewriterDataSchema
    | ContextRefinerDataSchema
    | GeneratorDataSchema
    | CoderDataSchema
    | ReACTorDataSchema
    | CategorizerDataSchema
    | ToolDataSchema
    | ConditionDataSchema
    | MergerDataSchema
  ) {
    return 'output_keys' in data && Array.isArray(data.output_keys);
  }

  const setKeyTable = (node: CustomNode) => {
    const nodeData = node.data || {};

    if (!hasInputKeys(nodeData) && !hasOutputKeys(nodeData)) {
      return;
    }

    const isInputNodeType = node.data.type == 'input__basic';

    setKeyTableList(prevList => {
      const existingEntries = prevList.filter(item => item.nodeId === node.id);
      const existingKeyMap = new Map(existingEntries.map(entry => [entry.key, entry.id]));

      if (hasInputKeys(nodeData) && isInputNodeType) {
        const inputKeys: InputKeyItem[] = nodeData.input_keys ?? [];
        const newKeyEntries = inputKeys
          .filter(item => item.name !== '')
          .map(item => ({
            id: item.keytable_id ?? item.name + '__' + node.id,
            name: node.data.name + '_' + item.name,
            key: item.name,
            isGlobal: false,
            value: item.fixed_value ?? '',
            nodeId: node.id,
            nodeType: node.type || '',
            nodeName: node.data.name as string,
            node,
          }));

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

        return [...updatedList, ...globalKeyEntries];
      }

      if (hasOutputKeys(nodeData) && !isInputNodeType) {
        const outputKeys: OutputKeyItem[] = nodeData.output_keys ?? [];
        const newKeyEntries = outputKeys
          .filter(item => item != null && item.name !== '')
          .map(item => {
            let keyTableId = item.keytable_id;

            if (!keyTableId || keyTableId.trim() === '') {
              keyTableId = existingKeyMap.get(item.name) || '';
            }

            if (!keyTableId || keyTableId.trim() === '') {
              keyTableId = `${item.name}_${node.id}`;
            }

            return {
              id: keyTableId,
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

        const existingNodeIds = new Set(nodes.map(n => n.id));
        const validKeys = updatedList.filter(item => !('isGlobal' in item && item.isGlobal === true) && existingNodeIds.has(item.nodeId)).map(item => item.key);
        const uniqueKeys = Array.from(new Set(validKeys));

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

        const filteredList = updatedList.filter(item => {
          if ('isGlobal' in item && item.isGlobal === true) return true;
          return existingNodeIds.has(item.nodeId);
        });
        return [...filteredList, ...globalKeyEntries];
      }

      return prevList;
    });

    keyTableUpdatedRef.current = true;
  };

  const clearModelFromNode = useCallback(
    (nodeId: string, nodeType: string) => {
      setNodes(prevNodes => {
        let hasChanges = false;

        const updatedNodes = prevNodes.map(node => {
          if (node.id !== nodeId) {
            return node;
          }

          const updatedData: any = { ...node.data };

          const resetServingFields = (target: any) => {
            if (!target || typeof target !== 'object') {
              return;
            }
            if ('serving_name' in target) {
              target.serving_name = '';
            }
            if ('serving_model' in target) {
              target.serving_model = '';
            }
          };

          resetServingFields(updatedData);

          if (nodeType === 'Hyde' || nodeType === 'retriever__rewriter_hyde') {
            if (updatedData.query_rewriter?.llm_chain?.llm_config) {
              resetServingFields(updatedData.query_rewriter.llm_chain.llm_config);
            }
          } else if (nodeType === 'Generator' || nodeType === 'agent__generator') {
          } else if (nodeType === 'Reviewer' || nodeType === 'agent__reviewer') {
          } else if (nodeType === 'retriever__doc_compressor' || nodeType === 'retriever__doc_reranker' || nodeType === 'retriever__doc_filter') {
            if (updatedData.context_refiner?.llm_chain?.llm_config) {
              resetServingFields(updatedData.context_refiner.llm_chain.llm_config);
            }
          }

          hasChanges = true;

          return {
            ...node,
            data: updatedData,
          };
        });

        if (hasChanges) {
          nodesUpdatedRef.current = true;
          return updatedNodes;
        }

        return prevNodes;
      });
    },
    [setNodes]
  );

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
    clearModelFromNode,
    getLastStableGraphName: () => lastStableGraphNameRef.current,
    setLastStableGraphName: (name: string) => {
      lastStableGraphNameRef.current = name ?? '';
    },
  };
};
