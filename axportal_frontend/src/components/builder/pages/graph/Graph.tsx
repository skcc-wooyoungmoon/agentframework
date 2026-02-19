import { edgesAtom, keyTableAtom, nodesAtom } from '@/components/builder/atoms/AgentAtom.ts';
import { validationStatusAtom } from '@/components/builder/atoms/ValidationAtom.ts';
import '@/components/builder/common/styles/customReactFlow.css';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { useGraphHandlers } from '@/components/builder/hooks/useGraphHandlers.ts';
import ChatTest from '@/components/builder/pages/graph/controller/ChatTest.tsx';
import { GraphController } from '@/components/builder/pages/graph/controller/GraphController.tsx';
import { ButtonEdgeSmoothStep } from '@/components/builder/pages/graph/edge/ButtonEdgeSmoothStep.tsx';
import GraphHeader from '@/components/builder/pages/graph/GraphHeader.tsx';
import { CategorizerNode } from '@/components/builder/pages/graph/node/CategorizerNode.tsx';
import { ConditionNode } from '@/components/builder/pages/graph/node/ConditionNode.tsx';
import { ReviewerNode } from '@/components/builder/pages/graph/node/ReviewerNode.tsx';
import { UnionNode } from '@/components/builder/pages/graph/node/UnionNode.tsx';
import {
  type Agent,
  type CustomEdge,
  type CustomNode,
  EXCLUDE_KEY_TABLE_TYPE,
  type InputKeyItem,
  type KeyTableData,
  type OutputKeyItem,
} from '@/components/builder/types/Agents.ts';
import { getNodeTitleByName } from '@/components/builder/utils/GraphUtils.ts';
import { hasAllRequiredData, hasAllRequiredInputs } from '@/components/builder/utils/ValidationUtils.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { Background, BackgroundVariant, ConnectionMode, Controls, ReactFlow } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { useAtom } from 'jotai';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AgentAppNode,
  CodeNode,
  CompressorNode,
  FilterNode,
  GeneratorNode,
  InputNode,
  NoteNode,
  OutputFormatterNode,
  OutputSelectorNode,
  ReRankerNode,
  RetrieverNode,
  RewriterHyDeNode,
  RewriterMultiQueryNode,
  ToolNode,
} from './node/';
// import { tracingMessagesAtom, tracingNodeIdAtom, tracingBaseInfoAtom } from '@/components/builder/atoms/messagesAtom.ts';
import { tracingBaseInfoAtom, tracingMessagesAtom, tracingNodeIdAtom } from '@/components/builder/atoms/messagesAtom.ts';
import type { UIStepperItem } from '@/components/UI/molecules';
import { api } from '@/configs/axios.config';
import { useToast } from '@/hooks/common/toast';
import { DeployAgentStep2InfoInputPopupPage, DeployAgentStep3ResAllocPopupPage } from '@/pages/deploy/agent';
import { useGetAgentBuilderById, useGetAgentDeployInfo } from '@/services/agent/builder2/agentBuilder.services';
import { useDeployAgent } from '@/stores/deploy';
import dagre from '@dagrejs/dagre';
import { useQueryClient } from '@tanstack/react-query';

const nodeTypes: any = {
  note: NoteNode,
  input__basic: InputNode,
  // output__selector: OutputSelectorNode,
  output__keys: OutputSelectorNode,
  // output__formatter: OutputFormatterNode,
  output__chat: OutputFormatterNode,
  agent__generator: GeneratorNode,
  agent__categorizer: CategorizerNode,
  agent__coder: CodeNode,
  agent__reviewer: ReviewerNode,
  agent__app: AgentAppNode,
  retriever__rewriter_hyde: RewriterHyDeNode,
  retriever__rewriter_multiquery: RewriterMultiQueryNode,
  retriever__knowledge: RetrieverNode,
  retriever__doc_reranker: ReRankerNode,
  retriever__doc_compressor: CompressorNode,
  retriever__doc_filter: FilterNode,
  tool: ToolNode,
  condition: ConditionNode,
  union: UnionNode,
};

const edgeTypes = {
  none: ButtonEdgeSmoothStep,
  case: ButtonEdgeSmoothStep,
  recursive: ButtonEdgeSmoothStep,
};

interface selectedAgentGraph {
  data: Agent;
  readOnly?: boolean;
}

const Graph = ({ data, readOnly = false }: selectedAgentGraph) => {
  // console.log('🔍 data:::::::::::::: ', data);

  const { onConnect, isValidConnection, onDrop, onDragOver, onNodesChange, onEdgesChange } = useGraphHandlers();

  // readOnly 모드일 때 핸들러 비활성화
  const handleConnect = readOnly ? undefined : onConnect;
  const handleDrop = readOnly ? undefined : onDrop;
  const handleNodesChange = readOnly ? undefined : onNodesChange;
  const handleEdgesChange = readOnly ? undefined : onEdgesChange;
  const [nodes, setNodes] = useAtom(nodesAtom);
  const [edges, setEdges] = useAtom(edgesAtom);
  const [tracingMessages] = useAtom(tracingMessagesAtom);
  const [, setTracingNodeId] = useAtom(tracingNodeIdAtom);
  const [, setTracingBaseInfo] = useAtom(tracingBaseInfoAtom);
  const [isChatVisible, setIsChatVisible] = useState(false);
  const [agentData, setAgentData] = useState<Agent>(data);

  const { addNode, updateAgent, saveAgent, addEdge } = useGraphActions();
  const { openAlert } = useModal();
  const { toast } = useToast();
  const { hasOutputKeys, hasInputKeys } = useGraphActions();
  const queryClient = useQueryClient();
  const graphId = data?.id || '';
  const { data: agentBuilderData } = useGetAgentBuilderById(graphId || '');

  const nodesInitRef = useRef(true);
  const edgesInitRef = useRef(true);
  const keyTableInitialized = useRef(false);

  const [validationStatus] = useAtom(validationStatusAtom);
  const [keyTableList, setKeyTableAtom] = useAtom(keyTableAtom);

  const navigate = useNavigate();
  const [isEdges, setIsEdges] = useState(false);
  const [edgeCheckCount, setEdgeCheckCount] = useState(0);
  const [autoLayout, setAutoLayout] = useState(false);

  const dagreGraph = new dagre.graphlib.Graph().setDefaultEdgeLabel(() => ({}));
  const nodeWidth = 800;
  const nodeHeight = 1200;

  // 배포 팝업 상태 관리
  const [deployStep, setDeployStep] = useState<number>(0);
  const { updateDeployData, resetDeployData } = useDeployAgent();
  // 배포 스테퍼 아이템
  const deployStepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '배포 정보 입력',
      step: 1,
    },
    {
      id: 'step2',
      label: '자원 할당',
      step: 2,
    },
  ];

  const { data: deployInfo, isLoading: isDeployInfoLoading } = useGetAgentDeployInfo(graphId, {
    enabled: !!graphId && deployStep >= 2, // 배포 버튼을 눌렀을 때부터 호출
  });

  // 배포 정보 확인 후 배포명 설정
  useEffect(() => {
    if (deployStep === 2 && !isDeployInfoLoading) {
      // deployInfo가 있으면 그 값을 사용, 없으면 name과 description 설정 안 함
      const nameDescUpdate: {
        name?: string;
        description?: string;
      } = {};

      nameDescUpdate.name = deployInfo?.name || '';
      nameDescUpdate.description = deployInfo?.description || '';
      updateDeployData(nameDescUpdate);
    }
  }, [deployStep, deployInfo, isDeployInfoLoading, updateDeployData]);

  const getLayoutedElements = (nodes: CustomNode[], edges: CustomEdge[]) => {
    dagreGraph.setGraph({ rankdir: 'LR' });

    nodes.forEach(node => {
      dagreGraph.setNode(node.id, { width: nodeWidth, height: nodeHeight });
    });

    edges.forEach(edge => {
      dagreGraph.setEdge(edge.source, edge.target);
    });

    dagre.layout(dagreGraph);

    const newNodes = nodes.map(node => {
      const nodeWithPosition = dagreGraph.node(node.id);
      const newNode = {
        ...node,
        target_position: 'left',
        source_position: 'right',
        position: {
          x: Math.ceil(nodeWithPosition.x - nodeWidth / 2),
          y: Math.ceil(nodeWithPosition.y - nodeHeight / 2),
        },
      };

      return newNode;
    });

    return { nodes: newNodes, edges };
  };

  useEffect(() => {
    if (autoLayout) {
      const layouted = getLayoutedElements(nodes, edges);
      setNodes(layouted.nodes);
      setEdges(layouted.edges);
      setAutoLayout(false);
    }
  }, [autoLayout]);

  useEffect(() => {
    if (edges.length > 0) {
      requestAnimationFrame(() => {
        const edgeEls = document.querySelectorAll('.react-flow__edge');

        if (edgeEls.length) {
          setEdgeCheckCount(0);
        } else {
          setIsEdges(!isEdges);
          setEdgeCheckCount(prev => prev + 1);

          if (edgeCheckCount + 1 >= 5) {
            window.location.reload();
          }
        }
      });
    }
  }, [edges, isEdges, edgeCheckCount]);

  useEffect(() => {
    if (graphId && !keyTableInitialized.current && agentBuilderData) {
      const initKeyTableList: KeyTableData[] = [];

      if (agentBuilderData?.nodes) {
        const nodes: CustomNode[] = agentBuilderData?.nodes;

        nodes.forEach(node => {
          const processKeys = (keys: (OutputKeyItem | InputKeyItem)[]) => {
            keys.forEach(key => {
              if (key) {
                const baseKeyInfo = {
                  name: `${node.data.name}_${key.name}`,
                  key: key.name,
                  value: '',
                  nodeId: node.id,
                  nodeType: node.type || '',
                  nodeName: node.data.name as string,
                  node,
                };
                // isGlobal: false — 기존 선택 유지. global(keytable_id 끝이 __global)인 경우만 name__nodeId 사용해 id 중복 방지
                const isGlobalKeytable = key?.keytable_id && String(key.keytable_id).endsWith('__global');
                initKeyTableList.push({
                  ...baseKeyInfo,
                  id: isGlobalKeytable ? (key?.name ?? '') + '__' + node.id : key?.keytable_id || (key?.name ?? '') + '__' + node.id,
                  isGlobal: false,
                });
                // isGlobal: true — 키 이름당 하나만 (id: name__global)
                initKeyTableList.push({
                  ...baseKeyInfo,
                  id: key?.name ? key.name + '__global' : '',
                  isGlobal: true,
                });
              }
            });
          };

          if (hasOutputKeys(node.data)) {
            processKeys(node.data.output_keys as OutputKeyItem[]);
          }

          // output__keys(OutputSelector), output_keys(OutputFormatter), Note의 경우 keyTable 미생성 타입은 제외 — 다른 노드 키를 참조만 함
          if (hasInputKeys(node.data) && node.type && !EXCLUDE_KEY_TABLE_TYPE.includes(node.type)) {
            processKeys(node.data.input_keys as InputKeyItem[]);
          }
        });
      }

      setKeyTableAtom(prevList => {
        const mergedList = [...prevList];
        initKeyTableList.forEach(newItem => {
          if (!mergedList.some(item => item.id === newItem.id)) {
            mergedList.push(newItem);
          }
        });
        return mergedList;
      });

      keyTableInitialized.current = true;
    }
    setTracingBaseInfo({ graphId: graphId || '', projectId: data?.project_id || '' });
  }, [agentBuilderData]);

  // 상태 업데이트 시 노드 테두리 변경
  useEffect(() => {
    if (!tracingMessages?.nodeId) return;

    // const isRun = tracingMessages?.callback === 'on_node_start';
    // const isError = tracingMessages?.callback === 'on_node_error';

    setTracingNodeId(prev => {
      return [...new Set([...prev, tracingMessages?.nodeId])];
    });

    // setNodes(prev => {
    //   return prev.map(node => {
    //     if (node?.data?.name !== tracingMessages?.nodeId) {
    //       // return node;
    //       return {
    //         ...node,
    //         data: {
    //           ...node.data,
    //           innerData: {
    //             ...node.data.innerData,
    //             isDone: node.data.innerData?.isRun ? node.data.innerData?.isRun : false,
    //           },
    //         },
    //       };
    //     }

    //     return {
    //       ...node,
    //       data: {
    //         ...node.data,
    //         innerData: {
    //           ...node.data.innerData,
    //           isRun: node.data.innerData?.isRun ? node.data.innerData?.isRun : isRun,
    //           isDone: false,
    //           isError: isError,
    //           logData: [...(node.data.innerData?.logData ?? []), tracingMessages.log],
    //         },
    //       },
    //     };
    //   });
    // });
  }, [tracingMessages, setNodes, setTracingNodeId]);

  const handleChat = async () => {
    const isSaved = await handleSave();
    if (!isSaved) return;

    setTimeout(() => {
      setIsChatVisible(prev => !prev);
    }, 1000);
  };

  const handleSave = async () => {
    const nodesWithMissingInputs = nodes.filter(node => {
      const inputKeys = node.data?.input_keys as Array<{
        name: string;
        fixed_value: string | null;
        keytable_id: string | null;
      }>;

      if (!inputKeys) return false;
      return !hasAllRequiredInputs(node.type, inputKeys) || !hasAllRequiredData(node.type, node.data);
    });

    if (nodesWithMissingInputs.length > 0) {
      openAlert({
        title: '필수 입력값 누락',
        message: nodesWithMissingInputs.map(node => `${getNodeTitleByName(node.type)}: 필수 입력값을 모두 채워주세요.`).join('\n'),
      });
      return false;
    }

    const otherValidations = validationStatus.errorNodes.filter(node => node.errors.some(error => error.type !== 'REQUIRED_FIELD' && error.type !== 'REQUIRED_INPUT_VALUE'));

    if (otherValidations.length > 0) {
      const errorMessages = otherValidations
        .map(node => {
          const errorNode = nodes.find(n => n.id === node.nodeId);
          const nodeType = errorNode?.type || '';
          return node.errors.map(error => ({
            type: nodeType,
            message: `${getNodeTitleByName(nodeType)} : ${error.message}`,
          }));
        })
        .flat()
        .filter((error, index, self) => index === self.findIndex(e => e.type === error.type && e.message === error.message));

      openAlert({
        title: '입력값 오류 발생',
        message: errorMessages.map(error => error.message).join('\n'),
      });
      return false;
    }

    const response = await saveAgent(keyTableList);
    if (response) {
      // console.log('response: ', response);
      toast.success('저장이 완료되었습니다.');
    }

    return true;
  };

  const handleLayout = () => {
    setAutoLayout(true);
  };

  const handleDeploy = async () => {
    const isSaved = await handleSave();
    if (!isSaved) return;

    // 노드에서 prompt_id 추출 및 projectId 확인
    const promptIds: string[] = [];
    nodes?.forEach((node: CustomNode) => {
      const nodeData = (node.data || {}) as any;
      if (nodeData.prompt_id && typeof nodeData.prompt_id === 'string' && nodeData.prompt_id.trim() !== '') {
        promptIds.push(nodeData.prompt_id);
      }
    });

    // prompt_id가 있는 경우 각각 조회하여 projectId 확인
    if (promptIds.length > 0) {
      try {
        const promptChecks = await Promise.all(
          promptIds.map(async promptId => {
            try {
              const response = await queryClient.fetchQuery({
                queryKey: ['GET', `/inference-prompts/${promptId}`, 'inf-prompts', 'detail', promptId],
                queryFn: async () => {
                  const response = await api.get(`/inference-prompts/${promptId}`);
                  const result = response.data?.data || response.data;
                  return result;
                },
                staleTime: 0, // 캐시 무시
                gcTime: 0, // 캐시 무시
              });

              // API 응답에서 project_id (snake_case) 또는 projectId (camelCase) 확인
              const projectId = (response as any)?.project_id;
              return { promptId, projectId };
            } catch (error) {
              console.error(`프롬프트 조회 실패 (promptId: ${promptId}):`, error);
              return { promptId, projectId: null };
            }
          })
        );

        // projectId가 '24ba585a-02fc-43d8-b9f1-f7ca9e020fe5'가 아닌 값이 있는지 확인
        const hasDifferentProjectId = promptChecks.some(check => check.projectId && check.projectId !== '24ba585a-02fc-43d8-b9f1-f7ca9e020fe5');
        if (hasDifferentProjectId) {
          await openAlert({
            title: '안내',
            message: `빌트인 프롬프트가 포함되어 있습니다. \n\n추론 프롬프트 목록에서 재생성 후 배포를 진행해주세요.`,
            confirmText: '확인',
          });
          return;
        }
      } catch (error) {
        console.error('프롬프트 projectId 확인 중 오류:', error);
      }
    }

    updateDeployData({
      targetId: graphId,
      targetType: 'agent_graph',
    });

    setDeployStep(2);
  };

  const handleDescription = async () => {
    // 빌더 조회 페이지로 이동 전 자동 저장
    if (graphId) {
      await handleSave();
      navigate(`/agent/builder/${graphId}`);
    } else {
      openAlert({
        title: '안내',
        message: '에이전트 ID가 없습니다.',
        confirmText: '확인',
      });
    }
  };

  // data prop이 변경될 때 agentData 상태 업데이트
  useEffect(() => {
    setAgentData(data);
  }, [data]);

  useEffect(() => {
    if (data && nodesInitRef.current) {
      updateAgent(data);

      if (data.nodes?.length > 0) {
        data.nodes.forEach(node => {
          addNode(node);
        });
        nodesInitRef.current = false;
      }
    }
  }, [data]);

  useEffect(() => {
    if (data) {
      if (data.edges?.length > 0 && edgesInitRef.current) {
        data.edges.forEach((edge: CustomEdge) => {
          const handleId = edge.sourceHandle || (edge.type === 'case' && edge.data?.category?.id ? `handle-${edge.data.category.id}` : undefined);

          const labelValue = edge.sourceHandle || edge.condition_label || edge.data?.category?.category || '';

          const newEdge: CustomEdge = {
            ...edge,
            id: edge.id,
            source: String(edge.source || edge.data?.send_from || ''),
            target: String(edge.target || edge.data?.send_to || ''),
            source_handle: handleId,
            label: labelValue || undefined,
            condition_label: labelValue || undefined,
            type: edge.type || 'case',
            data: {
              ...edge.data,
              send_from: edge.source || edge.data?.send_from,
              send_to: edge.target || edge.data?.send_to,
              edge_type: edge.type || 'case',
              category: edge.data?.category || {
                id: labelValue,
                category: labelValue,
                description: '',
              },
            },
          };

          addEdge(newEdge);
        });

        edgesInitRef.current = false;
      }
    }
  }, [data]);

  return (
    <div style={{ width: '100%', height: '100%' }}>
      <div className={'absolute top-0 z-10 flex w-full flex-row justify-between px-4'}>
        <GraphHeader name={agentData.name} onClickDesc={handleDescription} readOnly={readOnly} />
        <GraphController onLayoutClick={handleLayout} onSaveClick={handleSave} onChatClick={handleChat} onDeployClick={handleDeploy} readOnly={readOnly} />
      </div>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        nodeTypes={nodeTypes}
        edgeTypes={edgeTypes}
        onDragOver={readOnly ? undefined : onDragOver}
        onDrop={handleDrop}
        onConnect={handleConnect}
        panOnScroll
        isValidConnection={readOnly ? () => false : isValidConnection}
        connectionMode={ConnectionMode.Loose}
        onNodesChange={handleNodesChange}
        onEdgesChange={handleEdgesChange}
        nodesDraggable={!readOnly}
        nodesConnectable={!readOnly}
        elementsSelectable={!readOnly}
        fitView
        fitViewOptions={{
          padding: 10,
          includeHiddenNodes: false,
          maxZoom: 1,
          minZoom: 0.3,
        }}
        attributionPosition='top-right'
        snapToGrid={true}
        minZoom={0.3}
        proOptions={{ hideAttribution: true }}
      >
        <Controls position={'bottom-right'} showInteractive={!readOnly} />
        <Background bgColor='#d7dff7' variant={BackgroundVariant.Dots} />
        <ChatTest isChatVisible={isChatVisible} setIsChatVisible={() => setIsChatVisible(prev => !prev)} agentId={agentData.id} />
      </ReactFlow>

      {/* 배포 팝업 */}
      {/* Step 2. 배포 정보 입력 */}
      <DeployAgentStep2InfoInputPopupPage
        isOpen={deployStep === 2}
        stepperItems={deployStepperItems}
        onClose={() => {
          setDeployStep(0);
          resetDeployData();
        }}
        onNextStep={() => setDeployStep(3)}
        currentStep={1}
      />
      {/* Step 3. 자원 할당 */}
      <DeployAgentStep3ResAllocPopupPage
        isOpen={deployStep === 3}
        stepperItems={deployStepperItems}
        builderName={agentData.name}
        onClose={() => {
          resetDeployData();
          setDeployStep(0);
        }}
        onPreviousStep={() => setDeployStep(2)}
        onDeploySuccess={() => {
          setDeployStep(0);
          resetDeployData();
          // 배포 목록 페이지로 이동
          navigate('/deploy/agentDeploy');
        }}
        currentStep={2}
      />
    </div>
  );
};

export default Graph;
