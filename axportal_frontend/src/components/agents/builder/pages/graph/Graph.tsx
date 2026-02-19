import dagre from '@dagrejs/dagre';
import { Background, BackgroundVariant, ConnectionLineType, ConnectionMode, Controls, ReactFlow, type Node } from '@xyflow/react';

import {
  agentAtom,
  builderLogState,
  clearModelEventAtom,
  edgesAtom,
  hasChatTestedAtom,
  isClearModelAtom,
  keyTableAtom,
  messagesAtom,
  nodesAtom,
  progressMessageAtom,
  selectedFewShotIdRepoAtom,
  selectedKnowledgeIdRepoAtom,
  selectedKnowledgeNameRepoAtom,
  selectedKnowledgeRepoKindAtom,
  selectedKnowledgeRetrieverIdAtom,
  selectedListAtom,
  selectedLLMRepoAtom,
  selectedPromptIdRepoAtom,
  streamingMessageAtom,
  tracingBaseInfoAtom,
  tracingMessagesAtom,
  tracingNodeIdAtom,
} from '@/components/agents/builder/atoms';
import {
  useGraphActions,
  useGraphContainer,
  useGraphDataLoader,
  useGraphHandlers,
  useGraphInitialization,
  useGraphSave,
  useGraphTracing,
  useGraphViewport,
  useStreamLogs,
} from '@/components/agents/builder/hooks';

import '@/components/agents/builder/common/styles/customReactFlow.css';

import GraphHeader from '@/components/agents/builder/pages/graph/GraphHeader.tsx';
import ChatTest from '@/components/agents/builder/pages/graph/controller/ChatTest.tsx';
import { GraphController } from '@/components/agents/builder/pages/graph/controller/GraphController.tsx';

import { NodeType, type Agent, type CustomEdge, type CustomNode, type InputKeyItem, type KeyTableData, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { DeployAgentStep2InfoInputPopupPage, DeployAgentStep3ResAllocPopupPage } from '@/pages/deploy/agent';
import '@xyflow/react/dist/style.css';
import { useAtom, useSetAtom } from 'jotai';
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react';

import type { UIStepperItem } from '@/components/UI/molecules';
import { api } from '@/configs/axios.config';
import { useGetAgentDeployInfo } from '@/services/agent/builder/agentBuilder.services';
import { useModal } from '@/stores/common/modal/useModal';
import { useDeployAgent } from '@/stores/deploy/useDeployAgent';
import { useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import {
  convertCategorizerEdge,
  convertEdgeWithAppearance,
  getCaseEdgeAppearance,
  rafUntil,
  resolveDefaultSourceHandle,
  resolveDefaultTargetHandle,
  scheduleContainerSizeUpdates,
  waitForFrames,
} from '@/components/agents/builder/pages/graph/utils';
import { EDGE_COLORS, EDGE_MARKER_SIZE, EDGE_TYPE, MAX_EDGE_LOAD_RETRIES, NODE_TYPE, TEMPLATE_AUTO_CONNECT_ENABLED } from '@/components/agents/builder/types/Graphs';

// dagre 그래프 인스턴스 생성
const dagreGraph = new dagre.graphlib.Graph().setDefaultEdgeLabel(() => ({}));

interface selectedAgentGraph {
  data: Agent;
  hasInitialData?: boolean;
  readOnly?: boolean;
}

const Graph = ({ data, hasInitialData, readOnly = false }: selectedAgentGraph) => {
  const navigate = useNavigate();
  const { onConnect, isValidConnection, onDrop, onDragOver, onNodesChange, onEdgesChange: originalOnEdgesChange } = useGraphHandlers(readOnly);

  const [nodes, setNodesRaw] = useAtom(nodesAtom);
  const [edges, setEdges] = useAtom(edgesAtom);

  const onEdgesChange = useCallback(
    (changes: any) => {
      // 삭제된 엣지 추적
      if (Array.isArray(changes)) {
        changes.forEach((change: any) => {
          if (change.type === 'remove' && change.id) {
            const edgeId = String(change.id);
            userDeletedEdgesRef.current.add(edgeId);
          }
        });
      }

      // 원본 핸들러 호출
      originalOnEdgesChange(changes);
    },
    [originalOnEdgesChange]
  );

  const setNodes = useCallback(
    (nodesOrUpdater: any) => {
      if (typeof nodesOrUpdater === 'function') {
        setNodesRaw((prev: CustomNode[]) => {
          const updated = nodesOrUpdater(prev);
          if (!Array.isArray(updated)) return updated;

          return updated.map((node: CustomNode, index: number) => {
            if (
              !node.position ||
              typeof node.position?.x !== 'number' ||
              typeof node.position?.y !== 'number' ||
              isNaN(node.position.x) ||
              isNaN(node.position.y) ||
              !isFinite(node.position.x) ||
              !isFinite(node.position.y)
            ) {
              return {
                ...node,
                position: { x: 150 + index * 800, y: 200 },
              };
            }
            return node;
          });
        });
      } else {
        if (!Array.isArray(nodesOrUpdater)) {
          setNodesRaw(nodesOrUpdater);
          return;
        }
        const validatedNodes = nodesOrUpdater.map((node: CustomNode, index: number) => {
          if (
            !node.position ||
            typeof node.position?.x !== 'number' ||
            typeof node.position?.y !== 'number' ||
            isNaN(node.position.x) ||
            isNaN(node.position.y) ||
            !isFinite(node.position.x) ||
            !isFinite(node.position.y)
          ) {
            return {
              ...node,
              position: { x: 150 + index * 800, y: 200 },
            };
          }
          return node;
        });
        setNodesRaw(validatedNodes);
      }
    },
    [setNodesRaw]
  );
  const [agentState, setAgent] = useAtom(agentAtom);
  const [tracingMessages] = useAtom(tracingMessagesAtom);
  const [, setTracingNodeId] = useAtom(tracingNodeIdAtom);
  const [, setTracingBaseInfo] = useAtom(tracingBaseInfoAtom);
  const [isChatVisible, setIsChatVisible] = useState(false);
  const [, setSelectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [, setSelectedFewShotIdRepo] = useAtom(selectedFewShotIdRepoAtom);
  const [, setSelectedToolsRepo] = useAtom(selectedListAtom);
  const [, setSelectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [, setSelectedKnowledgeIdRepo] = useAtom(selectedKnowledgeIdRepoAtom);
  const [, setSelectedKnowledgeRepoKind] = useAtom(selectedKnowledgeRepoKindAtom);
  const [, setSelectedKnowledgeRetrieverId] = useAtom(selectedKnowledgeRetrieverIdAtom);
  const [, setSelectedKnowledgeNameRepo] = useAtom(selectedKnowledgeNameRepoAtom);
  const [, setMessages] = useAtom(messagesAtom);
  const [, setTracingMessages] = useAtom(tracingMessagesAtom);
  const [, setHasChatTested] = useAtom(hasChatTestedAtom);
  const setBuilderLogState = useSetAtom(builderLogState);
  const [, setProgressMessage] = useAtom(progressMessageAtom);
  const [, setStreamingMessage] = useAtom(streamingMessageAtom);

  const { addNode: _addNode, updateAgent: _updateAgent, clearModelFromNode, hasOutputKeys, hasInputKeys, getLastStableGraphName, setLastStableGraphName } = useGraphActions();

  const [unsavedChanges, setUnsavedChanges] = useState(false);
  const [deployStep, setDeployStep] = useState<number>(0);

  const {
    handleSave,
    handleChat,
    handleDeploy: originalHandleDeploy,
    handleDescription,
  } = useGraphSave({
    data,
    setUnsavedChanges,
    setDeployStep,
    setIsChatVisible,
    isChatVisible,
  });

  const queryClient = useQueryClient();
  const { openAlert } = useModal();

  // 빌트인 프롬프트 프로젝트 체크를 포함한 handleDeploy
  const handleDeploy = useCallback(async (): Promise<void> => {
    try {
      const saveSuccess = await handleSave();
      if (!saveSuccess) {
        return;
      }

      // 노드에서 prompt_id 추출 및 projectId 확인
      const promptIds: string[] = [];
      nodes.forEach((node: CustomNode) => {
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

      // 원래 handleDeploy 로직 실행
      await originalHandleDeploy();
    } catch (error) {
      openAlert({
        title: '안내',
        message: '배포 중 오류가 발생했습니다.',
        confirmText: '확인',
      });
    }
  }, [data?.id, nodes, handleSave, queryClient, openAlert, originalHandleDeploy]);

  const [clearModelEvent, setClearModelEvent] = useAtom(clearModelEventAtom);
  const [isClearModel, setClearModel] = useAtom(isClearModelAtom);

  const keyTableInitialized = useRef(false);
  const removedAutoEdgesRef = useRef<Set<string>>(new Set());
  const previousEdgesRef = useRef<CustomEdge[]>([]);
  const templateAutoConnectDoneRef = useRef(false);
  const templateAutoSaveDoneRef = useRef(false);
  const deletedConditionEdgesRef = useRef<Set<string>>(new Set());
  const graphEntryEdgeCheckRef = useRef<string>('');
  const edgeLoadRetryCountRef = useRef<number>(0);
  const edgeLoadCompletedRef = useRef<boolean>(false);
  const prevEdgeDataIdRef = useRef<string>('');
  const emptyGraphProcessedRef = useRef<string>('');
  const isAutoLayoutingRef = useRef(false); // 🔥 노드 자동정렬 실행 중인지 추적
  const clearModelProcessedRef = useRef<string | null>(null); // 🔥 Atom을 통한 모델 초기화 무한루프 방지
  const [, setKeyTableAtom] = useAtom(keyTableAtom);
  const { clearStreamLogs } = useStreamLogs();

  useGraphTracing({
    nodes,
    tracingMessages,
    setNodes,
    hasData: !!data,
    hasDataNodes: !!data?.nodes && data.nodes.length > 0,
  });

  const currentGraphIdRef = useRef<string | null>(null);

  useEffect(() => {
    currentGraphIdRef.current = data?.id || null;
  }, [data?.id]);

  useEffect(() => {
    return () => {
      const currentGraphId = currentGraphIdRef.current;
      if (currentGraphId) {
        removeGlobalViewport(currentGraphId);
      }
    };
  }, []);

  useEffect(() => {
    if (hasInitialData && keyTableInitialized.current) {
      setUnsavedChanges(true);
    }
  }, [nodes, edges]);

  const { updateDeployData, resetDeployData } = useDeployAgent();

  const agentGraphId = data?.id || '';

  const { data: deployInfo, isLoading: isDeployInfoLoading } = useGetAgentDeployInfo(agentGraphId, {
    enabled: !!agentGraphId && deployStep >= 2, // 배포 버튼을 눌렀을 때부터 호출
  });

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

  useEffect(() => {
    if (deployStep === 2 && !isDeployInfoLoading) {
      if (deployInfo?.name || deployInfo?.description) {
        const nameDescUpdate: {
          name?: string;
          description?: string;
        } = {};
        if (deployInfo.name) {
          nameDescUpdate.name = deployInfo.name;
        }
        if (deployInfo.description) {
          nameDescUpdate.description = deployInfo.description;
        }
        updateDeployData(nameDescUpdate);
      }
    }
  }, [deployStep, deployInfo, isDeployInfoLoading, updateDeployData]);

  const [reactFlowInstance, setReactFlowInstance] = useState<any>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  const [containerSize, setContainerSizeState] = useState(() => {
    if (typeof window !== 'undefined') {
      const minWidth = Math.max(window.innerWidth || 100, 100);
      const minHeight = Math.max(window.innerHeight - 70 || 100, 100);
      return { width: minWidth, height: minHeight };
    }
    return { width: 100, height: 100 };
  });

  const { setContainerSize, safeUpdateDimensions, saveGlobalViewport, removeGlobalViewport } = useGraphContainer({ containerRef, setContainerSizeState });

  const { prevAgentIdRef, prevDataNodesRef, prevDataEdgesRef, nodesInitRef, edgesInitRef, userDeletedEdgesRef, pendingCategorizerEdgesRef } = useGraphDataLoader({
    data,
    setNodes,
    setEdges,
    setSelectedPromptIdRepo,
    setSelectedFewShotIdRepo,
    setSelectedToolsRepo,
    setSelectedLLMRepo,
    setSelectedKnowledgeIdRepo,
    setSelectedKnowledgeRepoKind,
    setSelectedKnowledgeRetrieverId,
    setSelectedKnowledgeNameRepo,
    setContainerSize,
    reactFlowInstance,
    containerRef,
  });

  useLayoutEffect(() => {
    setContainerSize();
    const refId = requestAnimationFrame(setContainerSize);

    return () => {
      cancelAnimationFrame(refId);
    };
  }, [data?.id, setContainerSize]);

  useEffect(() => {
    if (!containerRef.current) return;

    const container = containerRef.current;

    const resizeObserver = new ResizeObserver(() => {
      setContainerSize();
    });

    resizeObserver.observe(container);

    let parent = container.parentElement;
    while (parent && !parent.classList.contains('graph-wrap')) {
      parent = parent.parentElement;
    }
    if (parent) {
      resizeObserver.observe(parent);
    }

    return () => {
      resizeObserver.disconnect();
    };
  }, [setContainerSize]);

  useEffect(() => {
    let frameId;

    const update = () => {
      setContainerSize();
      if (reactFlowInstance) {
        safeUpdateDimensions(reactFlowInstance);
      }
    };

    frameId = requestAnimationFrame(update);

    return () => {
      cancelAnimationFrame(frameId);
    };
  }, [data?.id, reactFlowInstance]);

  useEffect(() => {
    const handleForceRemount = (_event: CustomEvent) => {
      setReactFlowInstance(null);
    };

    window.addEventListener('forceReactFlowRemount', handleForceRemount as EventListener);

    return () => {
      window.removeEventListener('forceReactFlowRemount', handleForceRemount as EventListener);
    };
  }, []);

  const prevGraphIdRef = useRef<string | null>(null);

  useEffect(() => {
    const currentGraphId = data?.id || 'default';
    const prevGraphId = prevGraphIdRef.current;

    if (prevGraphId && prevGraphId !== currentGraphId) {
      const reactFlowWrapper = document.querySelector('.react-flow');

      removeGlobalViewport(prevGraphId);
      setReactFlowInstance(null);

      if (reactFlowWrapper) {
        const wrapper = reactFlowWrapper as HTMLElement;

        wrapper.style.cssText = '';

        const oldEdges = wrapper.querySelectorAll('.react-flow__edge');
        oldEdges.forEach(edge => {
          const edgeElement = edge as HTMLElement;
          edgeElement.style.opacity = '0';
          edgeElement.style.visibility = 'hidden';
        });

        const reactFlowPane = wrapper.querySelector('.react-flow__pane');
        if (reactFlowPane) {
          const pane = reactFlowPane as HTMLElement;
          pane.style.transform = '';
          pane.style.willChange = '';
        }
      }

      if (window.gc) {
        window.gc();
      }
    }
    prevGraphIdRef.current = currentGraphId;
  }, [data?.id]);

  useEffect(() => {
    if (reactFlowInstance && edges.length > 0) {
      const currentGraphId = data?.id || 'default';

      const domEdges = document.querySelectorAll('.react-flow__edge');

      if (domEdges.length !== edges.length || domEdges.length === 0) {
        const forceRerender = async () => {
          const currentViewport = reactFlowInstance.getViewport();
          reactFlowInstance.setViewport({
            x: currentViewport.x + 0.01,
            y: currentViewport.y + 0.01,
            zoom: currentViewport.zoom,
          });

          await waitForFrames(2);

          reactFlowInstance.setViewport(currentViewport);
          await waitForFrames(3);

          const midCheckEdges = document.querySelectorAll('.react-flow__edge');
          if (midCheckEdges.length !== edges.length) {
            const reactFlowWrapper = document.querySelector('.react-flow');
            if (reactFlowWrapper) {
              const wrapper = reactFlowWrapper as HTMLElement;
              wrapper.style.display = 'none';
              await waitForFrames(1);
              wrapper.style.display = 'block';
              await waitForFrames(4);
            }
          }

          const finalDomEdges = document.querySelectorAll('.react-flow__edge');

          if (finalDomEdges.length === edges.length) {
            finalDomEdges.forEach(edge => {
              const edgeElement = edge as HTMLElement;
              edgeElement.style.opacity = '1';
              edgeElement.style.visibility = 'visible';
            });
          } else {
            const event = new CustomEvent('forceReactFlowRemount', {
              detail: { graphId: currentGraphId },
            });
            window.dispatchEvent(event);
          }
        };

        forceRerender();
      }
    }
  }, [reactFlowInstance, data?.id, edges.length]);

  useEffect(() => {
    if (!reactFlowInstance || edges.length === 0) return;

    let isDestroyed = false;

    const forceEdgeRendering = () => {
      if (isDestroyed) return;

      const edgeElements = document.querySelectorAll('.react-flow__edge');
      const svgPaths = document.querySelectorAll('.react-flow__edges path');

      if (edgeElements.length > 0 && svgPaths.length > 0) return;

      const reactFlowWrapper = document.querySelector('.react-flow') as HTMLElement;
      if (reactFlowWrapper) {
        reactFlowWrapper.style.transform = 'translateZ(0)';
        reactFlowWrapper.style.transform = '';
      }

      document.querySelectorAll('.react-flow__edge').forEach(edge => {
        const edgeElement = edge as HTMLElement;
        edgeElement.style.display = 'block';
        edgeElement.style.visibility = 'visible';
        edgeElement.style.opacity = '1';
        edgeElement.style.pointerEvents = 'auto';
      });

      document.querySelectorAll('.react-flow__edges').forEach(edgesContainer => {
        const container = edgesContainer as HTMLElement;
        container.style.display = 'block';
        container.style.visibility = 'visible';
        container.style.opacity = '1';
      });

      document.querySelectorAll('.react-flow__edges path').forEach(path => {
        const pathElement = path as SVGPathElement;
        pathElement.style.stroke = '#C4CADA';
        pathElement.style.strokeWidth = '3px';
        pathElement.style.fill = 'none';
        pathElement.style.display = 'block';
        pathElement.style.visibility = 'visible';
        pathElement.style.opacity = '1';
      });

      document.querySelectorAll('.react-flow__edges marker').forEach(marker => {
        const markerElement = marker as SVGMarkerElement;
        markerElement.style.display = 'block';
        markerElement.style.visibility = 'visible';
        markerElement.style.opacity = '1';
      });
    };

    const cleanup = scheduleContainerSizeUpdates(forceEdgeRendering, { immediate: false, rafCount: 3 });

    return () => {
      isDestroyed = true;
      cleanup();
    };
  }, [reactFlowInstance, edges.length]);

  const {
    fitViewApplied,
    onWheel: handleWheel,
    onNodeDragStart: handleNodeDragStart,
    onNodeDrag: handleNodeDrag,
    onNodeDragStop: handleNodeDragStop,
    onMoveStart: handleMoveStart,
    onMove: handleMove,
    onMoveEnd: handleMoveEnd,
    onViewportChange: handleViewportChange,
  } = useGraphViewport({
    graphId: data?.id || '',
    reactFlowInstance,
    saveGlobalViewport,
  });

  useGraphInitialization({
    dataId: data?.id,
    hasInitialData,
    containerRef,
    setContainerSize,
    setContainerSizeState,
    setNodes,
    setEdges,
    setReactFlowInstance,
    setSelectedPromptIdRepo,
    setSelectedFewShotIdRepo,
    setSelectedToolsRepo,
    setSelectedLLMRepo,
    setMessages,
    setTracingMessages,
    setHasChatTested,
    setBuilderLogState,
    setProgressMessage,
    setStreamingMessage,
    setTracingNodeId,
    setTracingBaseInfo,
    clearStreamLogs,
    fitViewAppliedRef: fitViewApplied,
    nodesInitRef,
    edgesInitRef,
    prevDataNodesRef,
    prevDataEdgesRef,
    templateAutoConnectDoneRef,
  });

  const hasInitialDataProcessedRef = useRef(false);

  useEffect(() => {
    if (nodes.length === 0) {
      return;
    }

    if (!reactFlowInstance || !hasInitialData || hasInitialDataProcessedRef.current) return;

    hasInitialDataProcessedRef.current = true;

    if (reactFlowInstance && (reactFlowInstance as any).updateNodeInternals && typeof (reactFlowInstance as any).updateNodeInternals === 'function') {
      const currentNodes = reactFlowInstance.getNodes();
      if (currentNodes && Array.isArray(currentNodes) && currentNodes.length > 0) {
        currentNodes.forEach((node: Node) => {
          if (node && node.id) {
            (reactFlowInstance as any).updateNodeInternals(node.id);
          }
        });
      }
    }
  }, [hasInitialData, reactFlowInstance]);

  useEffect(() => {
    if (nodes.length === 0) {
      return;
    }

    // 🔥 자동정렬 실행 중일 때는 fitView 실행하지 않음
    if (isAutoLayoutingRef.current) {
      return;
    }

    if (hasInitialData && reactFlowInstance && nodes.length > 0 && !fitViewApplied.current) {
      const allNodePositions = nodes.map(node => ({
        x: node.position?.x || 0,
        y: node.position?.y || 0,
        width: (node as any).width || 300,
        height: (node as any).height || 200,
      }));

      if (allNodePositions.length > 0) {
        const minX = Math.min(...allNodePositions.map(n => n.x));
        const maxX = Math.max(...allNodePositions.map(n => n.x + n.width));
        const minY = Math.min(...allNodePositions.map(n => n.y));
        const maxY = Math.max(...allNodePositions.map(n => n.y + n.height));

        const totalWidth = maxX - minX;
        const totalHeight = maxY - minY;

        if (totalWidth > 0 && totalHeight > 0) {
          reactFlowInstance.fitView({
            padding: 0.3,
            duration: 500,
            includeHiddenNodes: false,
            maxZoom: 1.5,
            minZoom: 0.1,
          });
          fitViewApplied.current = true;
        } else {
          reactFlowInstance.fitView({ padding: 0.3, duration: 500 });
          fitViewApplied.current = true;
        }
      } else {
        reactFlowInstance.fitView({ padding: 0.3, duration: 500 });
        fitViewApplied.current = true;
      }
    }
  }, [hasInitialData, reactFlowInstance]);

  // Auto Layout 함수 (dagre 사용)
  const getLayoutedElements = useCallback((nodes: CustomNode[], edges: CustomEdge[], direction: 'TB' | 'LR' = 'LR') => {
    const isHorizontal = direction === 'LR';
    const nodesep = 100; // 노드 간 가로 간격
    const ranksep = 150; // 레벨 간 세로 간격

    dagreGraph.setGraph({
      rankdir: direction,
      nodesep,
      ranksep,
    });

    const nodeWidth = 500;
    const nodeHeight = 400;

    nodes.forEach(node => {
      const width = node.measured?.width || node.width || nodeWidth;
      const height = node.measured?.height || node.height || nodeHeight;
      dagreGraph.setNode(node.id, { width, height });
    });

    edges.forEach(edge => {
      dagreGraph.setEdge(edge.source, edge.target);
    });

    dagre.layout(dagreGraph);

    let minX = Infinity;
    let minY = Infinity;

    const nodePositions = nodes.map(node => {
      const nodeWithPosition = dagreGraph.node(node.id);
      const width = node.measured?.width || node.width || nodeWidth;
      const height = node.measured?.height || node.height || nodeHeight;
      const centerX = nodeWithPosition.x;
      const centerY = nodeWithPosition.y;
      const x = centerX - width / 2;
      const y = centerY - height / 2;

      return {
        nodeId: node.id,
        centerY,
        x,
        y,
      };
    });

    const TOLERANCE = ranksep / 3;
    const rankGroups: Array<Array<(typeof nodePositions)[0]>> = [];

    nodePositions.forEach(pos => {
      let foundGroup = false;
      for (const group of rankGroups) {
        const groupCenterY = group[0].centerY;
        if (Math.abs(pos.centerY - groupCenterY) < TOLERANCE) {
          group.push(pos);
          foundGroup = true;
          break;
        }
      }

      if (!foundGroup) {
        rankGroups.push([pos]);
      }
    });

    const rankMinY = new Map<string, number>();
    rankGroups.forEach(nodesInRank => {
      const minYInRank = Math.min(...nodesInRank.map(p => p.y));
      nodesInRank.forEach(pos => {
        rankMinY.set(pos.nodeId, minYInRank);
      });
    });

    nodePositions.forEach(pos => {
      const alignedY = rankMinY.get(pos.nodeId) || pos.y;
      if (pos.x < minX) minX = pos.x;
      if (alignedY < minY) minY = alignedY;
    });

    const offsetX = minX < 0 ? Math.abs(minX) + 100 : 0;
    const offsetY = minY < 0 ? Math.abs(minY) + 100 : 0;

    const newNodes = nodes.map(node => {
      const nodeWithPosition = dagreGraph.node(node.id);
      const width = node.measured?.width || node.width || nodeWidth;
      const height = node.measured?.height || node.height || nodeHeight;
      const centerX = nodeWithPosition.x;
      const centerY = nodeWithPosition.y;

      const x = centerX - width / 2;
      const alignedY = rankMinY.get(node.id);
      const finalY = alignedY !== undefined ? alignedY : centerY - height / 2;

      const newNode = {
        ...node,
        targetPosition: isHorizontal ? 'left' : 'top',
        sourcePosition: isHorizontal ? 'right' : 'bottom',
        position: {
          x: Math.ceil(x + offsetX),
          y: Math.ceil(finalY + offsetY),
        },
      };
      return newNode;
    });

    return { nodes: newNodes, edges };
  }, []);

  // Auto Layout 핸들러
  const handleLayout = useCallback(
    (direction: 'TB' | 'LR' = 'LR') => {
      if (!reactFlowInstance) return;

      const savedViewport = reactFlowInstance.getViewport();
      isAutoLayoutingRef.current = true;

      const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutedElements(nodes, edges, direction);

      reactFlowInstance.setViewport(savedViewport, { duration: 0 });

      setNodes(layoutedNodes);
      setEdges(layoutedEdges);

      const restoreViewport = () => reactFlowInstance?.setViewport(savedViewport, { duration: 0 });

      requestAnimationFrame(() => {
        restoreViewport();
        requestAnimationFrame(() => {
          restoreViewport();
          setTimeout(() => {
            isAutoLayoutingRef.current = false;
          }, 300);
        });
      });
    },
    [nodes, edges, getLayoutedElements, setNodes, setEdges, reactFlowInstance]
  );

  // Atom을 통한 모델 초기화 처리 (무한루프 방지)
  useEffect(() => {
    if (isClearModel && clearModelEvent) {
      const { nodeId, nodeType, timestamp } = clearModelEvent;

      // 이미 처리한 이벤트는 건너뛰기 (무한루프 방지)
      const eventKey = `${nodeId}-${nodeType}-${timestamp}`;
      if (clearModelProcessedRef.current === eventKey) {
        return;
      }

      if (nodeId && nodeType) {
        clearModelProcessedRef.current = eventKey;

        // clearModelFromNode 호출 후 상태 초기화를 지연시켜 노드들이 처리할 시간 확보
        clearModelFromNode(nodeId, nodeType);

        setTimeout(() => {
          setClearModelEvent(null);
          setClearModel(false);
          clearModelProcessedRef.current = null;
        }, 1000);
      }
    }
  }, [isClearModel, clearModelEvent, setClearModelEvent, setClearModel]);

  useEffect(() => {
    if (!data) {
      return;
    }

    const currentAgentId = data?.id || '';
    const prevAgentId = prevAgentIdRef.current || '';

    if (currentAgentId && currentAgentId !== prevAgentId) {
      setKeyTableAtom([]);
      keyTableInitialized.current = false;
      graphEntryEdgeCheckRef.current = '';
      prevEdgeDataIdRef.current = '';
      emptyGraphProcessedRef.current = '';
    }

    if (!data?.nodes || data.nodes.length === 0) {
      if (data?.id) {
        setTracingBaseInfo({
          graphId: data.id,
          projectId: data?.project_id || '',
        });
      }
      return;
    }

    if (data.nodes && data.nodes.length > 0 && !keyTableInitialized.current) {
      const initKeyTableList: KeyTableData[] = [];
      const keyTableIdSet = new Set<string>();

      data.nodes.forEach((node: CustomNode) => {
        if (hasInputKeys(node.data)) {
          const inputKeys = node.data.input_keys as InputKeyItem[];
          inputKeys.forEach(key => {
            if (key && key.name) {
              const baseKeyInfo = {
                name: `${node.data.name}_${key.name}`,
                key: key.name,
                value: key.fixed_value || '',
                nodeId: node.id,
                nodeType: node.type || '',
                nodeName: node.data.name as string,
                node,
              };

              let localKeyTableId = key?.keytable_id;

              if (!localKeyTableId || localKeyTableId.trim() === '') {
                localKeyTableId = `${key.name}_${node.id}`;
              }

              if (!keyTableIdSet.has(localKeyTableId)) {
                initKeyTableList.push({
                  ...baseKeyInfo,
                  id: localKeyTableId,
                  isGlobal: false,
                });
                keyTableIdSet.add(localKeyTableId);
              }

              const globalId = key?.name ? `${key.name}__global` : '';
              if (globalId && !keyTableIdSet.has(globalId)) {
                initKeyTableList.push({
                  ...baseKeyInfo,
                  id: globalId,
                  isGlobal: true,
                });
                keyTableIdSet.add(globalId);
              }
            }
          });
        }

        if (hasOutputKeys(node.data)) {
          const outputKeys = node.data.output_keys as OutputKeyItem[];
          outputKeys.forEach(key => {
            if (key && key.name) {
              const baseKeyInfo = {
                name: `${node.data.name}_${key.name}`,
                key: key.name,
                value: '',
                nodeId: node.id,
                nodeType: node.type || '',
                nodeName: node.data.name as string,
                node,
              };

              let localKeyTableId = key?.keytable_id;
              if (!localKeyTableId || localKeyTableId.trim() === '') {
                localKeyTableId = `${key.name}_${node.id}`;
              }

              if (!keyTableIdSet.has(localKeyTableId)) {
                initKeyTableList.push({
                  ...baseKeyInfo,
                  id: localKeyTableId,
                  isGlobal: false,
                });
                keyTableIdSet.add(localKeyTableId);
              }

              const globalId = key?.name ? `${key.name}__global` : '';
              if (globalId && !keyTableIdSet.has(globalId)) {
                initKeyTableList.push({
                  ...baseKeyInfo,
                  id: globalId,
                  isGlobal: true,
                });
                keyTableIdSet.add(globalId);
              }
            }
          });
        }
      });

      setKeyTableAtom(initKeyTableList);
      keyTableInitialized.current = true;
    }

    if (data?.id) {
      setTracingBaseInfo({
        graphId: data.id,
        projectId: data?.project_id || '',
      });
    }
  }, [data?.id, data?.nodes?.length, data?.edges?.length]);

  useEffect(() => {
    if (!data) {
      return;
    }

    const isDataLoaded = data && data.hasOwnProperty('nodes');
    const isReallyEmpty = isDataLoaded && (!data.nodes || data.nodes.length === 0);

    const resolvedStableName =
      getLastStableGraphName() || (data as any)?.innerData?.stableGraphName || data?.name || (agentState as any)?.innerData?.stableGraphName || agentState?.name || '';

    setLastStableGraphName(resolvedStableName);

    setAgent(
      prev =>
        ({
          ...(data as any),
          name: data?.name ?? prev?.name ?? '',
          innerData: {
            ...((prev as any)?.innerData ?? {}),
            ...((data as any)?.innerData ?? {}),
            stableGraphName: resolvedStableName,
          },
        }) as any
    );

    if (isReallyEmpty) {
      const currentAgentId = data?.id || '';
      const agentIdChanged = prevAgentIdRef.current !== '' && prevAgentIdRef.current !== currentAgentId;

      if (agentIdChanged) {
        setNodes([]);
        setEdges([]);
        setSelectedPromptIdRepo({});
        setSelectedFewShotIdRepo({});
        setSelectedToolsRepo({});
        setSelectedLLMRepo({});
        setSelectedKnowledgeIdRepo({});
        setSelectedKnowledgeRepoKind({});
        setSelectedKnowledgeRetrieverId({});
        setSelectedKnowledgeNameRepo({});
        nodesInitRef.current = true;
        edgesInitRef.current = true;
        prevDataNodesRef.current = '';
        prevDataEdgesRef.current = '';
      }
      return;
    }

    if (!nodes || nodes.length === 0) {
      if (!nodesInitRef.current) {
        nodesInitRef.current = true;
        edgesInitRef.current = true;
      }
    }

    if (data.nodes && data.edges && nodesInitRef.current && (!nodes || nodes.length === 0)) {
      templateAutoConnectDoneRef.current = false;
      templateAutoSaveDoneRef.current = false;

      const arrangedNodes: CustomNode[] = (data.nodes || []) as CustomNode[];
      const validatedNodes = arrangedNodes;
      const finalValidatedNodes = validatedNodes;
      const ultraSafeNodes = finalValidatedNodes;
      const nodeMap = new Map<string, CustomNode>();

      ultraSafeNodes.forEach(node => {
        nodeMap.set(String(node.id), node as CustomNode);
      });

      const edgesToProcess = data.edges || [];
      const nodesWithMappedInputs = ultraSafeNodes;

      const promptIdUpdates: Record<string, string | null> = {};
      const fewShotIdUpdates: Record<string, string | null> = {};
      const toolsUpdates: Record<string, any[] | null> = {};

      nodesWithMappedInputs.forEach((node: any) => {
        const nodeId = String(node.id);
        const nodeData = node.data || {};

        if (nodeData.prompt_id && nodeData.prompt_id.trim() !== '') {
          promptIdUpdates[nodeId] = nodeData.prompt_id;
        } else {
          promptIdUpdates[nodeId] = null;
        }

        if (nodeData.fewshot_id && nodeData.fewshot_id.trim() !== '') {
          fewShotIdUpdates[nodeId] = nodeData.fewshot_id;
        } else {
          fewShotIdUpdates[nodeId] = null;
        }

        if (nodeData.tools && Array.isArray(nodeData.tools) && nodeData.tools.length > 0) {
          toolsUpdates[nodeId] = nodeData.tools;
        } else {
          toolsUpdates[nodeId] = null;
        }
      });

      setSelectedPromptIdRepo((prev: Record<string, string | null>) => {
        const updated = { ...prev };
        let hasChanges = false;
        Object.entries(promptIdUpdates).forEach(([nodeId, promptId]) => {
          if (updated[nodeId] !== promptId) {
            updated[nodeId] = promptId;
            hasChanges = true;
          }
        });
        return hasChanges ? updated : prev;
      });

      setSelectedFewShotIdRepo((prev: Record<string, string | null>) => {
        const updated = { ...prev };
        let hasChanges = false;
        Object.entries(fewShotIdUpdates).forEach(([nodeId, fewShotId]) => {
          if (updated[nodeId] !== fewShotId) {
            updated[nodeId] = fewShotId;
            hasChanges = true;
          }
        });
        return hasChanges ? updated : prev;
      });

      setSelectedToolsRepo((prev: Record<string, any[]>) => {
        const updated = { ...prev };
        let hasChanges = false;
        Object.entries(toolsUpdates).forEach(([nodeId, tools]) => {
          const prevTools = prev[nodeId];
          if (tools === null) {
            if (prevTools !== null && prevTools !== undefined) {
              delete updated[nodeId];
              hasChanges = true;
            }
          } else if (Array.isArray(tools)) {
            const toolsChanged = !prevTools || JSON.stringify(prevTools) !== JSON.stringify(tools);
            if (toolsChanged) {
              updated[nodeId] = tools;
              hasChanges = true;
            }
          }
        });
        return hasChanges ? updated : prev;
      });

      const finalNodes = nodesWithMappedInputs;

      setNodes(finalNodes);

      const outputChatNodes = nodesWithMappedInputs.filter(node => node.type === 'output__chat' || node.type === 'output__formatter');

      if (outputChatNodes.length > 0 && edgesToProcess.length > 0) {
        const updatedOutputChatNodes = outputChatNodes.map(outputChatNode => {
          const incomingEdges = edgesToProcess.filter((edge: any) => String(edge.target) === String(outputChatNode.id));

          if (incomingEdges.length === 0) {
            return outputChatNode;
          }

          const currentFormatString = (outputChatNode.data as any)?.format_string || '';
          const tokenRegex = /\{\{([^}]+)\}\}/g;
          const currentTokens = Array.from(currentFormatString.matchAll(tokenRegex), (match: RegExpMatchArray) => match[1]);

          let hasValidTokens = false;
          if (currentTokens.length > 0) {
            for (const token of currentTokens) {
              for (const edge of incomingEdges) {
                const sourceNode = nodesWithMappedInputs.find(n => String(n.id) === String(edge.source));
                if (sourceNode) {
                  const outputKeys = Array.isArray((sourceNode.data as any)?.output_keys) ? (sourceNode.data as any).output_keys : [];
                  const tokenExists = outputKeys.some((key: any) => key && key.keytable_id === token);
                  if (tokenExists) {
                    hasValidTokens = true;
                    break;
                  }
                }
              }
              if (hasValidTokens) break;
            }

            if (hasValidTokens) {
              return outputChatNode;
            }
          }
          if (currentTokens.length === 0 || !hasValidTokens) {
            const allOutputKeys: string[] = [];
            incomingEdges.forEach((edge: any) => {
              const sourceNode = nodesWithMappedInputs.find(n => String(n.id) === String(edge.source));
              if (sourceNode) {
                const outputKeys = Array.isArray((sourceNode.data as any)?.output_keys) ? (sourceNode.data as any).output_keys : [];
                outputKeys.forEach((key: any) => {
                  if (key && key.keytable_id) {
                    allOutputKeys.push(key.keytable_id);
                  }
                });
              }
            });

            if (allOutputKeys.length > 0) {
              const desiredFormatString = `{{${allOutputKeys[0]}}}`;

              if (!currentFormatString || currentFormatString.trim() === '' || currentFormatString.trim() === ' ') {
                return {
                  ...outputChatNode,
                  data: {
                    ...outputChatNode.data,
                    format_string: desiredFormatString,
                  },
                };
              }
            } else if (!currentFormatString || currentFormatString.trim() === '') {
              return {
                ...outputChatNode,
                data: {
                  ...outputChatNode.data,
                  format_string: ' ',
                },
              };
            }
          }

          return outputChatNode;
        });

        const hasOutputChatUpdates = updatedOutputChatNodes.some((updatedNode, index) => {
          const originalNode = outputChatNodes[index];
          return updatedNode !== originalNode;
        });

        if (hasOutputChatUpdates) {
          const finalNodes = nodesWithMappedInputs.map(node => {
            const updatedOutputChat = updatedOutputChatNodes.find(updated => updated.id === node.id);
            return updatedOutputChat || node;
          });
          setNodes(finalNodes);
        }
      }
    }
  }, [data?.id, data?.nodes?.length, data?.edges?.length]);

  useEffect(() => {
    const currentAgentId = data?.id || '';
    const agentIdChanged = prevAgentIdRef.current !== '' && prevAgentIdRef.current !== currentAgentId;

    if (!data?.nodes || data.nodes.length === 0) {
      if (agentIdChanged) {
        setEdges([]);
        edgesInitRef.current = true;
      }
      return;
    }

    if (agentIdChanged && (!data?.edges || data.edges.length === 0)) {
      if (containerRef.current) {
        setContainerSizeState({ width: 100, height: 100 });
        scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 4 });

        if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
          const container = containerRef.current;
          const width = container.offsetWidth || container.clientWidth || 0;
          const height = container.offsetHeight || container.clientHeight || 0;

          if (width > 0 && height > 0) {
            (reactFlowInstance as any).updateDimensions();
          }
        }
      }
    }

    if (nodes.length === 0) {
      return;
    }

    const nodeIdMap = new Map<string, any>();
    nodes.forEach((node: any) => {
      nodeIdMap.set(String(node.id), node);
    });

    if (data?.edges && data.edges.length > 0 && edgesInitRef.current && (!edges || edges.length === 0)) {
      const validEdges = data.edges.filter((edge: any) => {
        const sourceId = String(edge.source);
        const targetId = String(edge.target);
        const sourceExists = nodeIdMap.has(sourceId);
        const targetExists = nodeIdMap.has(targetId);

        return sourceExists && targetExists;
      });

      if (validEdges.length > 0) {
        setEdges(validEdges);
        edgesInitRef.current = false;

        if (containerRef.current) {
          setContainerSize();
          if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
            (reactFlowInstance as any).updateDimensions();
          }
        }
      }
    }

    if (agentIdChanged && (!data?.edges || data.edges.length === 0) && edges.length === 0) {
      if (containerRef.current) {
        setContainerSizeState({ width: 100, height: 100 });
        scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 4 });

        if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
          const container = containerRef.current;
          const width = container.offsetWidth || container.clientWidth || 0;
          const height = container.offsetHeight || container.clientHeight || 0;

          if (width > 0 && height > 0) {
            (reactFlowInstance as any).updateDimensions();
          }
        }
      }
    }
  }, [data?.nodes?.length, nodes.length, data?.edges?.length]);

  useEffect(() => {
    const currentAgentId = data?.id || '';
    const prevAgentId = prevAgentIdRef.current || '';
    const agentIdChanged = currentAgentId && currentAgentId !== prevAgentId;

    if (agentIdChanged) {
      graphEntryEdgeCheckRef.current = '';

      if ((!data?.edges || data.edges.length === 0) && nodes.length > 0) {
        if (containerRef.current) {
          setContainerSizeState({ width: 100, height: 100 });
          scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 4 });

          if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
            const container = containerRef.current;
            const width = container.offsetWidth || container.clientWidth || 0;
            const height = container.offsetHeight || container.clientHeight || 0;

            if (width > 0 && height > 0) {
              (reactFlowInstance as any).updateDimensions();
            }
          }
        }
      }

      graphEntryEdgeCheckRef.current = '';
      return;
    }

    if (!data?.id || nodes.length === 0 || !data?.edges || data.edges.length === 0) {
      return;
    }

    const currentNodesAgentId = nodes.length > 0 ? (nodes[0] as any)?.data?.agentId : null;

    if (currentNodesAgentId && currentNodesAgentId !== data.id) {
      return;
    }

    if (data.nodes && data.nodes.length > 0 && nodes.length !== data.nodes.length) {
      return;
    }

    const currentGraphId = `${data.id}-${nodes.length}-${data.edges.length}`;

    if (graphEntryEdgeCheckRef.current === currentGraphId) {
      return;
    }

    const nodeIdMap = new Map<string, any>();

    nodes.forEach((node: any) => {
      nodeIdMap.set(String(node.id), node);
    });

    const connectedEdges: any[] = [];

    data.edges.forEach((edge: any) => {
      const sourceId = String(edge.source);
      const targetId = String(edge.target);
      const sourceExists = nodeIdMap.has(sourceId);
      const targetExists = nodeIdMap.has(targetId);

      if (!sourceExists || !targetExists) {
        return;
      }

      connectedEdges.push(edge);
    });

    if (edges.length > 0 && data.edges.length > 0) {
      const currentEdgeKeys = new Set(edges.map((e: any) => e.id || `${e.source}-${e.target}`));

      const missingEdges = data.edges.filter((e: any) => {
        const edgeKey = e.id || `${e.source}-${e.target}`;
        return !currentEdgeKeys.has(edgeKey);
      });

      if (missingEdges.length > 0) {
        const validMissingEdges = missingEdges.filter((edge: any) => {
          const edgeId = String(edge.id);
          if (userDeletedEdgesRef.current.has(edgeId)) {
            return false;
          }

          const sourceId = String(edge.source);
          const targetId = String(edge.target);
          return nodeIdMap.has(sourceId) && nodeIdMap.has(targetId);
        });

        if (validMissingEdges.length > 0) {
          setEdges((prevEdges: any[]) => {
            const existingEdgeKeys = new Set(prevEdges.map((e: any) => e.id || `${e.source}-${e.target}`));
            const newEdges = validMissingEdges.filter((e: any) => {
              const edgeKey = e.id || `${e.source}-${e.target}`;
              return !existingEdgeKeys.has(edgeKey);
            });
            return [...prevEdges, ...newEdges];
          });
        }
      }
    }

    graphEntryEdgeCheckRef.current = currentGraphId;
  }, [data?.id, nodes.length, data?.edges?.length]);

  useEffect(() => {
    const handleAgentUpdated = (event: CustomEvent) => {
      if (event.detail && event.detail.agentId === data?.id) {
        setAgent((prevAgent: Agent | undefined) => {
          if (!prevAgent) return prevAgent;
          return {
            ...prevAgent,
            name: event.detail.name,
            description: event.detail.description,
          };
        });
      }
    };

    window.addEventListener('agent-updated', handleAgentUpdated as EventListener);

    return () => {
      window.removeEventListener('agent-updated', handleAgentUpdated as EventListener);
    };
  }, [data?.id]);

  useEffect(() => {
    const handleClearModelFromNode = (event: CustomEvent) => {
      const { nodeId, nodeType } = event.detail;

      if (nodeId && nodeType) {
        clearModelFromNode(nodeId, nodeType);
      }
    };

    window.addEventListener('clear-model-from-node', handleClearModelFromNode as EventListener);

    return () => {
      window.removeEventListener('clear-model-from-node', handleClearModelFromNode as EventListener);
    };
  }, []);

  useEffect(() => {
    if (!data?.nodes || data.nodes.length === 0) {
      edgeLoadRetryCountRef.current = 0;
      edgeLoadCompletedRef.current = true;
      emptyGraphProcessedRef.current = '';
      return;
    }

    if (nodes.length === 0) {
      return;
    }

    if (!data?.edges || data.edges.length === 0) {
      const emptyGraphKey = `${data?.id || ''}-empty`;
      if (emptyGraphProcessedRef.current === emptyGraphKey) {
        return;
      }

      edgeLoadCompletedRef.current = true;
      edgeLoadRetryCountRef.current = 0;
      edgesInitRef.current = false;
      emptyGraphProcessedRef.current = emptyGraphKey;
      scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 5 });

      if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
        scheduleContainerSizeUpdates(
          () => {
            setContainerSize();
            (reactFlowInstance as any).updateDimensions();
          },
          { immediate: true, rafCount: 4 }
        );
      }

      return;
    }

    if (emptyGraphProcessedRef.current) {
      emptyGraphProcessedRef.current = '';
    }

    if (edgeLoadRetryCountRef.current >= MAX_EDGE_LOAD_RETRIES) {
      edgesInitRef.current = false;
      edgeLoadRetryCountRef.current = 0;
      edgeLoadCompletedRef.current = true;
      return;
    }

    if (data && nodes && nodes.length > 0) {
      if (data.edges && data.edges.length > 0 && edgesInitRef.current) {
        const nodeIdMap = new Map<string, any>();
        nodes.forEach((node: any) => {
          nodeIdMap.set(String(node.id), node);
        });

        const conditionNodes = nodes.filter(node => node.type === 'condition');
        const allConditionNodesReady = conditionNodes.every(node => {
          return node.data?.conditions !== undefined;
        });

        const categorizerNodes = nodes.filter(node => node.type === 'agent__categorizer');
        const allCategorizerNodesReady = categorizerNodes.every(node => {
          return node.data?.categories !== undefined;
        });

        if (!allConditionNodesReady || !allCategorizerNodesReady) {
          edgeLoadRetryCountRef.current += 1;
          return;
        }

        edgeLoadRetryCountRef.current = 0;

        if (edgeLoadCompletedRef.current) {
          return;
        }

        const nodeCount = nodes.length;
        const maxRetries = Math.min(5, Math.max(3, Math.floor(nodeCount / 5) + 2));
        const retryDelay = Math.min(200, Math.max(100, Math.floor(nodeCount / 10) * 10));

        const ensureHandlesAndLoadEdges = (retryCount: number = 0) => {
          if (edgeLoadCompletedRef.current || retryCount >= maxRetries) {
            if (retryCount >= maxRetries && !edgeLoadCompletedRef.current) {
              loadEdges();
            }
            return;
          }

          if (reactFlowInstance && typeof reactFlowInstance.updateNodeInternals === 'function') {
            nodes.forEach(node => {
              reactFlowInstance.updateNodeInternals(node.id);
            });
          }

          if (retryCount < maxRetries - 1) {
            setTimeout(() => ensureHandlesAndLoadEdges(retryCount + 1), retryDelay);
            return;
          }
          loadEdges();
        };

        const loadEdges = () => {
          if (edgeLoadCompletedRef.current) {
            return;
          }

          const validEdges: CustomEdge[] = [];

          edgeLoadCompletedRef.current = true;

          if (validEdges.length > 0) {
            setEdges(validEdges);

            requestAnimationFrame(() => {
              if (reactFlowInstance && typeof reactFlowInstance.updateNodeInternals === 'function') {
                const reviewerNodes = nodes.filter(node => node.type === 'agent__reviewer');
                reviewerNodes.forEach(node => {
                  reactFlowInstance.updateNodeInternals(node.id);
                });

                nodes.forEach(node => {
                  reactFlowInstance.updateNodeInternals(node.id);
                });

                if (reactFlowInstance.setEdges) {
                  reactFlowInstance.setEdges(validEdges);
                }
                if (reactFlowInstance && reactFlowInstance.getEdges) {
                  const connectedEdges = reactFlowInstance.getEdges();
                  const connectedEdgeIds = new Set(connectedEdges.map((e: any) => e.id));

                  const reviewerEdges = validEdges.filter((e: any) => {
                    const sourceNode = nodeIdMap.get(String(e.source));
                    return sourceNode?.type === 'agent__reviewer';
                  });

                  reviewerEdges.forEach((edge: any) => {
                    const isConnected = connectedEdgeIds.has(edge.id);
                    if (!isConnected) {
                      const sourceNode = nodes.find(n => String(n.id) === String(edge.source));
                      const targetNode = nodes.find(n => String(n.id) === String(edge.target));

                      if (sourceNode && targetNode) {
                        reactFlowInstance.updateNodeInternals(sourceNode.id);
                        reactFlowInstance.updateNodeInternals(targetNode.id);

                        const currentEdges = reactFlowInstance.getEdges();
                        const edgeExists = currentEdges.find((e: any) => e.id === edge.id);

                        if (!edgeExists) {
                          reactFlowInstance.setEdges([...currentEdges, edge]);
                        }
                      }
                    }
                  });

                  const unconnectedEdges = validEdges.filter(edge => !connectedEdgeIds.has(edge.id));

                  if (unconnectedEdges.length > 0) {
                    const specialNodeEdges = unconnectedEdges.filter(edge => {
                      const sourceNode = nodes.find(n => String(n.id) === String(edge.source));
                      const targetNode = nodes.find(n => String(n.id) === String(edge.target));

                      return (
                        sourceNode &&
                        (sourceNode.type === 'condition' ||
                          sourceNode.type === 'agent__reviewer' ||
                          sourceNode.type === 'agent__categorizer' ||
                          (sourceNode.type === 'input__basic' && targetNode?.type === 'condition'))
                      );
                    });

                    if (specialNodeEdges.length > 0) {
                      const fixedEdges = specialNodeEdges.map((edge: any) => {
                        const sourceNode = nodes.find(n => String(n.id) === String(edge.source));
                        if (!sourceNode) {
                          return edge;
                        }

                        let fixedHandleId = edge.sourceHandle;
                        let fixedTargetHandle = edge.targetHandle || edge.target_handle;

                        if (sourceNode.type === 'condition') {
                          const conditions = (sourceNode.data?.conditions || []) as Array<{ id: string }>;
                          if (edge.sourceHandle?.includes('condition-else')) {
                            fixedHandleId = 'handle-condition-else';
                          } else if (edge.sourceHandle?.startsWith('handle-')) {
                            const conditionIdFromHandle = edge.sourceHandle.replace('handle-', '');
                            const matchedCondition = conditions.find(cond => {
                              const actualHandleId = `handle-${cond.id}`;
                              return (
                                edge.sourceHandle === actualHandleId ||
                                conditionIdFromHandle === cond.id ||
                                conditionIdFromHandle.includes(cond.id) ||
                                cond.id.includes(conditionIdFromHandle)
                              );
                            });

                            if (matchedCondition) {
                              fixedHandleId = `handle-${matchedCondition.id}`;
                            } else if (conditions.length > 0) {
                              fixedHandleId = `handle-${conditions[0].id}`;
                            }
                          }
                        } else if (sourceNode.type === 'agent__reviewer') {
                          const conditionLabel = edge.condition_label || (edge.data as any)?.condition_label || '';
                          const passConditions = ['pass', 'condition-pass', 'handle-condition-pass'];
                          const failConditions = ['fail', 'condition-fail', 'handle-condition-fail'];

                          const matchesCondition = (patterns: string[], label?: string, handle?: string) =>
                            patterns.some(p => label === p || handle === p || handle?.toLowerCase().includes(p.split('-').pop()!));

                          if (matchesCondition(passConditions, conditionLabel, edge.sourceHandle)) {
                            fixedHandleId = 'reviewer_pass';
                          } else if (matchesCondition(failConditions, conditionLabel, edge.sourceHandle)) {
                            fixedHandleId = 'reviewer_fail';
                          }

                          const targetNode = nodes.find(n => String(n.id) === String(edge.target));
                          if (targetNode) {
                            if (targetNode.type === 'condition' && (!fixedTargetHandle || fixedTargetHandle !== 'condition_left')) {
                              fixedTargetHandle = 'condition_left';
                            } else if (targetNode.type === 'output__chat' && (!fixedTargetHandle || fixedTargetHandle !== 'output_formatter_left')) {
                              fixedTargetHandle = 'output_formatter_left';
                            }
                          }
                        } else if (sourceNode.type === 'agent__categorizer') {
                          const categories = (sourceNode.data?.categories || []) as Array<{ category: string; id: string }>;
                          const categoryIdentifier = edge.condition_label || edge.sourceHandle || edge.data?.category?.category || '';

                          if (categoryIdentifier && categories.length > 0) {
                            const categoryName = categoryIdentifier.replace(/^handle-/, '').replace(/^handle-category-/, '');
                            let categoryIndex = categories.findIndex(cat => cat.id === categoryName);
                            if (categoryIndex === -1) {
                              categoryIndex = categories.findIndex(cat => cat.category === categoryName);
                            }
                            if (categoryIndex !== -1) {
                              fixedHandleId = `handle-category-${categoryIndex}`;
                            }
                          }
                        }

                        if (sourceNode.type === 'input__basic') {
                          const targetNode = nodes.find(n => String(n.id) === String(edge.target));
                          if (targetNode && targetNode.type === 'condition') {
                            if (!fixedTargetHandle || fixedTargetHandle !== 'condition_left') {
                              fixedTargetHandle = 'condition_left';
                            }
                          }
                        }

                        return {
                          ...edge,
                          sourceHandle: fixedHandleId,
                          source_handle: fixedHandleId,
                          targetHandle: fixedTargetHandle,
                          target_handle: fixedTargetHandle,
                        };
                      });

                      nodes.forEach(node => {
                        reactFlowInstance.updateNodeInternals(node.id);
                      });

                      const allEdges = [...connectedEdges, ...fixedEdges];
                      reactFlowInstance.setEdges(allEdges);
                      const finalEdges = reactFlowInstance.getEdges();
                      const finalEdgeIds = new Set(finalEdges.map((e: any) => e.id));
                      const stillUnconnected = fixedEdges.filter((e: any) => !finalEdgeIds.has(e.id));
                      if (stillUnconnected.length > 0) {
                        const reviewerNodes = nodes.filter(node => node.type === 'agent__reviewer');
                        reviewerNodes.forEach(node => {
                          reactFlowInstance.updateNodeInternals(node.id);
                        });

                        nodes.forEach(node => {
                          reactFlowInstance.updateNodeInternals(node.id);
                        });

                        reactFlowInstance.setEdges(allEdges);
                      }
                    }
                  }
                }
              }
            });
            edgesInitRef.current = false;
          }
        };

        ensureHandlesAndLoadEdges();
      }
    }

    if (data?.id && prevEdgeDataIdRef.current !== data.id) {
      if (data?.edges && data.edges.length > 0) {
        edgeLoadCompletedRef.current = false;
        edgeLoadRetryCountRef.current = 0;
      }
      prevEdgeDataIdRef.current = data.id;
    }
  }, [data?.id, data?.nodes?.length, nodes.length, data?.edges?.length, reactFlowInstance]);

  useEffect(() => {
    if (!TEMPLATE_AUTO_CONNECT_ENABLED) {
      templateAutoConnectDoneRef.current = true;
      if (hasInitialData && data?.id && nodes.length > 0 && !templateAutoSaveDoneRef.current) {
        templateAutoSaveDoneRef.current = true;
        handleSave()
          .then(success => {
            if (!success) {
              templateAutoSaveDoneRef.current = false;
            }
          })
          .catch(() => {
            templateAutoSaveDoneRef.current = false;
          });
      }
      return;
    }

    if (!nodes || nodes.length === 0) {
      return;
    }

    if (templateAutoConnectDoneRef.current) {
      return;
    }

    const prevEdgesSnapshot = previousEdgesRef.current || [];
    prevEdgesSnapshot.forEach(edge => {
      if (edge?.id && (edge.id.startsWith('template-') || edge.id.startsWith('auto-')) && !edges.some(current => current.id === edge.id)) {
        removedAutoEdgesRef.current.add(edge.id);
      }
    });

    const nodeTypes = new Set(nodes.map(node => node.type));

    let templateType: 'recursive' | 'translator' | 'rag' | 'chatbot' = 'chatbot';
    if (nodeTypes.has('condition') && nodeTypes.has('union')) {
      templateType = 'recursive';
    } else if (nodeTypes.has('agent__categorizer')) {
      templateType = 'translator';
    } else if (nodeTypes.has('retriever__knowledge')) {
      templateType = 'rag';
    }

    const edgeKeySet = new Set(
      edges.map(edge => `${edge.source}:${edge.sourceHandle || ''}:${edge.target}:${edge.targetHandle || ''}:${edge.data?.edge_type || edge.type || 'none'}`)
    );
    const edgesToAdd: CustomEdge[] = [];

    const defaultStyle = { stroke: EDGE_COLORS.default, strokeWidth: 2.5 };
    const defaultMarkerEnd = { type: 'arrowclosed', width: EDGE_MARKER_SIZE.width, height: EDGE_MARKER_SIZE.height, color: EDGE_COLORS.default };

    const getNodesByType = (type: string) => nodes.filter(node => node.type === type).sort((a, b) => a.position.y - b.position.y);

    const getFirstByType = (type: string) => getNodesByType(type)[0];

    const addEdge = ({
      sourceNode,
      targetNode,
      edgeType = 'none',
      sourceHandle,
      targetHandle,
      label,
    }: {
      sourceNode?: CustomNode;
      targetNode?: CustomNode;
      edgeType?: 'case' | 'none';
      sourceHandle?: string;
      targetHandle?: string;
      label?: string;
    }) => {
      if (!sourceNode || !targetNode) return;
      if (sourceNode.id === targetNode.id) return;
      const resolvedSourceHandle = sourceHandle ?? resolveDefaultSourceHandle(sourceNode);
      const resolvedTargetHandle = targetHandle ?? resolveDefaultTargetHandle(targetNode);
      const key = `${sourceNode.id}:${resolvedSourceHandle || ''}:${targetNode.id}:${resolvedTargetHandle || ''}:${edgeType}`;
      if (edgeKeySet.has(key)) return;

      let styleByType = defaultStyle;
      let markerByType = defaultMarkerEnd;
      let appliedLabel = label;

      if (edgeType === 'case') {
        const appearance = getCaseEdgeAppearance(resolvedSourceHandle, label ?? null);
        styleByType = appearance.style;
        markerByType = appearance.marker as any;
        if (appearance.label) {
          appliedLabel = appearance.label;
        }
      }

      const edgeId = `template-${sourceNode.id}-${resolvedSourceHandle || 'default'}-${targetNode.id}-${resolvedTargetHandle || 'default'}-${edgeType}`;

      if (removedAutoEdgesRef.current.has(edgeId)) {
        return;
      }

      const newEdge: CustomEdge = {
        id: edgeId,
        source: sourceNode.id,
        target: targetNode.id,
        type: edgeType,
        style: styleByType,
        markerEnd: markerByType,
        data: {
          send_from: sourceNode.id,
          send_to: targetNode.id,
          edge_type: edgeType,
          label: appliedLabel,
        },
      } as CustomEdge;

      if (resolvedSourceHandle) {
        newEdge.sourceHandle = resolvedSourceHandle;
      }

      if (resolvedTargetHandle) {
        newEdge.targetHandle = resolvedTargetHandle;
      }

      edgeKeySet.add(key);
      edgesToAdd.push(newEdge);
    };

    if (templateType === 'chatbot') {
      const inputNode = getFirstByType(NodeType.Input.name);
      const generators = getNodesByType(NodeType.AgentGenerator.name).sort((a, b) => a.position.x - b.position.x);
      const outputNode = getFirstByType('output__chat');

      if (inputNode && generators.length > 0) {
        addEdge({ sourceNode: inputNode, targetNode: generators[0] });
      }

      for (let i = 0; i < generators.length - 1; i += 1) {
        addEdge({ sourceNode: generators[i], targetNode: generators[i + 1] });
      }

      if (generators.length > 0 && outputNode) {
        addEdge({ sourceNode: generators[generators.length - 1], targetNode: outputNode });
      }
    } else if (templateType === 'rag') {
      const inputNode = getFirstByType(NodeType.Input.name);
      const rewriterNode = getFirstByType('retriever__rewriter_hyde');
      const retrieverNode = getFirstByType('retriever__knowledge');
      const generatorNode = getNodesByType(NodeType.AgentGenerator.name).sort((a, b) => a.position.x - b.position.x)[0];
      const outputNode = getFirstByType('output__chat');

      if (inputNode && rewriterNode) {
        addEdge({ sourceNode: inputNode, targetNode: rewriterNode });
      }

      if (rewriterNode && retrieverNode) {
        addEdge({ sourceNode: rewriterNode, targetNode: retrieverNode });
      } else if (inputNode && retrieverNode) {
        addEdge({ sourceNode: inputNode, targetNode: retrieverNode });
      }

      if (retrieverNode && generatorNode) {
        addEdge({ sourceNode: retrieverNode, targetNode: generatorNode });
      } else if (rewriterNode && generatorNode) {
        addEdge({ sourceNode: rewriterNode, targetNode: generatorNode });
      }

      if (generatorNode && outputNode) {
        addEdge({ sourceNode: generatorNode, targetNode: outputNode });
      }
    } else if (templateType === 'translator') {
      const inputNode = getFirstByType(NodeType.Input.name);
      const categorizerNode = getFirstByType('agent__categorizer');
      const generatorNodes = getNodesByType(NodeType.AgentGenerator.name).sort((a, b) => a.position.y - b.position.y);
      const outputNode = getFirstByType('output__chat');

      if (inputNode && categorizerNode) {
        addEdge({ sourceNode: inputNode, targetNode: categorizerNode });
      }

      if (categorizerNode && generatorNodes.length > 0) {
        const categories = ((categorizerNode.data as any)?.categories as { id: string; category?: string }[]) || [];

        generatorNodes.forEach((generator, generatorIndex) => {
          const categoryIndex = generatorIndex;
          const category = categories[categoryIndex];
          const handleId = `handle-category-${categoryIndex}`;
          const categoryLabel = category?.category || (categoryIndex === 0 ? 'any_to_kor' : 'kor_to_any') || `Category ${categoryIndex + 1}`;

          addEdge({
            sourceNode: categorizerNode,
            targetNode: generator,
            edgeType: 'case',
            sourceHandle: handleId,
            label: categoryLabel,
          });
        });
      }

      if (outputNode) {
        generatorNodes.forEach(generator => {
          addEdge({ sourceNode: generator, targetNode: outputNode });
        });
      }
    } else if (templateType === 'recursive') {
      const inputNode = getFirstByType(NodeType.Input.name);
      const conditionNode = getFirstByType('condition');
      const generatorNodes = getNodesByType(NodeType.AgentGenerator.name).sort((a, b) => a.position.y - b.position.y);
      const unionNode = getFirstByType('union');
      const reviewerNode = getFirstByType('agent__reviewer');
      const outputNode = getFirstByType('output__chat');

      if (inputNode && conditionNode) {
        addEdge({ sourceNode: inputNode, targetNode: conditionNode, edgeType: 'case' });
      }

      generatorNodes.forEach((generator, index) => {
        if (!conditionNode) return;
        const conditionData = (conditionNode.data as any) || {};
        const conditions = (conditionData.conditions as { id: string; category?: string }[]) || [];
        const conditionId = conditions[index]?.id ?? (index === 0 ? 'condition-1' : 'condition-else');
        const handleId = conditionId === 'condition-else' ? 'handle-condition-else' : `handle-${conditionId}`;
        const label = conditions[index]?.category || (index === 0 ? 'IF' : 'ELSE');

        addEdge({
          sourceNode: conditionNode,
          targetNode: generator,
          edgeType: 'case',
          sourceHandle: handleId,
          label,
        });
      });

      if (unionNode) {
        generatorNodes.forEach(generator => {
          addEdge({ sourceNode: generator, targetNode: unionNode });
        });

        if (reviewerNode) {
          addEdge({ sourceNode: unionNode, targetNode: reviewerNode });
        }

        if (reviewerNode && outputNode) {
          addEdge({
            sourceNode: reviewerNode,
            targetNode: outputNode,
            edgeType: 'case',
            sourceHandle: 'reviewer_pass',
            label: 'PASS',
          });
        }
        if (reviewerNode && conditionNode) {
          addEdge({
            sourceNode: reviewerNode,
            targetNode: conditionNode,
            edgeType: 'case',
            sourceHandle: 'reviewer_fail',
            targetHandle: 'condition_left',
            label: 'FAIL',
          });
        }
      } else {
        if (reviewerNode) {
          generatorNodes.forEach(generator => {
            addEdge({ sourceNode: generator, targetNode: reviewerNode });
          });
        }

        if (reviewerNode && outputNode) {
          addEdge({
            sourceNode: reviewerNode,
            targetNode: outputNode,
            edgeType: 'case',
            sourceHandle: 'reviewer_pass',
            label: 'PASS',
          });
        }
        if (reviewerNode && conditionNode) {
          addEdge({
            sourceNode: reviewerNode,
            targetNode: conditionNode,
            edgeType: 'case',
            sourceHandle: 'reviewer_fail',
            targetHandle: 'condition_left',
            label: 'FAIL',
          });
        }
      }
    }
    if (!data?.nodes || data.nodes.length === 0) return;

    if (nodes.length === 0) return;

    const triggerAutoSave = () => {
      if (hasInitialData && data?.id && !templateAutoSaveDoneRef.current) {
        templateAutoSaveDoneRef.current = true;
        handleSave()
          .then(success => {
            if (!success) {
              templateAutoSaveDoneRef.current = false;
            }
          })
          .catch(() => {
            templateAutoSaveDoneRef.current = false;
          });
      }
    };

    if (edgesToAdd.length > 0) {
      templateAutoConnectDoneRef.current = true;
      setEdges(prevEdges => {
        const newEdges = edgesToAdd.map(edge => convertEdgeWithAppearance(edge, nodes as any));
        const existingEdgeIds = new Set(prevEdges.map(e => e.id));
        const uniqueNewEdges = newEdges.filter(e => !existingEdgeIds.has(e.id));

        if (uniqueNewEdges.length === 0) {
          return prevEdges;
        }

        return [...prevEdges, ...uniqueNewEdges];
      });
      triggerAutoSave();
    } else if (templateAutoConnectDoneRef.current === false && nodes.length > 0 && hasInitialData && data?.id && !templateAutoSaveDoneRef.current) {
      templateAutoConnectDoneRef.current = true;
      triggerAutoSave();
    }
  }, [data?.nodes?.length, nodes.length]);

  useEffect(() => {
    if (!data?.nodes || data.nodes.length === 0) {
      return;
    }

    if (nodes.length === 0) {
      return;
    }

    setEdges(prevEdges => {
      if (!prevEdges || prevEdges.length <= 1) {
        previousEdgesRef.current = prevEdges;
        return prevEdges;
      }

      const grouped = new Map<string, CustomEdge[]>();
      prevEdges.forEach(edge => {
        const handleKey = edge.source_handle || edge.target_handle || edge.data?.category?.id || edge.condition_label || '';
        const key = `${edge.source}::${edge.target}::${handleKey}`;
        if (!grouped.has(key)) {
          grouped.set(key, []);
        }
        grouped.get(key)!.push(edge);
      });

      let changed = false;
      const deduped: CustomEdge[] = [];

      grouped.forEach(edgesGroup => {
        if (edgesGroup.length === 1) {
          deduped.push(edgesGroup[0]);
          return;
        }

        const preferred = edgesGroup.find(edge => !String(edge.id).startsWith('auto-') && !String(edge.id).startsWith('template-')) || edgesGroup[0];
        deduped.push(preferred);

        edgesGroup.forEach(edge => {
          if (edge !== preferred) {
            changed = true;
            if (edge?.id && String(edge.id).startsWith('auto-')) {
              removedAutoEdgesRef.current.add(String(edge.id));
            }
          }
        });
      });

      previousEdgesRef.current = deduped;
      return changed ? deduped : prevEdges;
    });
  }, [data?.nodes?.length]);

  useEffect(() => {
    if (!data?.nodes || data.nodes.length === 0) {
      return;
    }
    if (nodes.length === 0) {
      return;
    }

    const prevEdges = previousEdgesRef.current;
    if (prevEdges.length > 0) {
      const prevMap = new Map(prevEdges.map(edge => [edge.id, edge]));
      edges.forEach(edge => {
        if (edge.id) {
          prevMap.delete(edge.id);
        }
      });

      prevMap.forEach(edge => {
        if (edge?.id?.startsWith('auto-')) {
          removedAutoEdgesRef.current.add(edge.id);
        }

        const sourceNode = nodes.find(n => n.id === edge.source);
        if (sourceNode && sourceNode.type === 'condition' && edge.sourceHandle) {
          const edgeKey = `${edge.source}-${edge.target}-${edge.sourceHandle}`;
          deletedConditionEdgesRef.current.add(edgeKey);
        }
      });
    }

    previousEdgesRef.current = edges;
  }, [data?.nodes?.length]);

  useEffect(() => {
    if (!data?.nodes || data.nodes.length === 0 || nodes.length === 0) {
      return;
    }

    const categorizerNodesForEdgeUpdate = nodes.filter(node => node.type === 'agent__categorizer');
    const nodesWithCategoriesForEdgeUpdate = categorizerNodesForEdgeUpdate.filter(node => {
      const categories = (node.data?.categories || []) as Array<{ category: string; id: string }>;
      return categories.length > 0;
    });

    if (nodesWithCategoriesForEdgeUpdate.length === 0) {
      return;
    }

    const edgesToUpdate = edges.filter(edge => {
      if (!edge.sourceHandle || typeof edge.sourceHandle !== 'string') {
        return false;
      }

      if (edge.sourceHandle.startsWith('handle-') && !edge.sourceHandle.startsWith('handle-category-')) {
        const sourceNode = nodes.find(n => n.id === edge.source);
        if (sourceNode && sourceNode.type === 'agent__categorizer') {
          return true;
        }
      }
      return false;
    });

    if (edgesToUpdate.length > 0) {
      let hasChanges = false;
      const processedEdgeIds = new Set<string>();
      setEdges(prevEdges => {
        const updatedEdges = prevEdges.map(edge => {
          if (processedEdgeIds.has(edge.id)) {
            return edge;
          }
          if (!edge.sourceHandle || typeof edge.sourceHandle !== 'string') {
            return edge;
          }
          if (edge.sourceHandle.startsWith('handle-category-')) {
            return edge;
          }

          if (edge.sourceHandle.startsWith('handle-') && !edge.sourceHandle.startsWith('handle-category-')) {
            const sourceNode = nodesWithCategoriesForEdgeUpdate.find(n => n.id === edge.source);
            if (sourceNode && sourceNode.type === 'agent__categorizer') {
              const categories = (sourceNode.data?.categories || []) as Array<{ category: string; id: string }>;

              if (categories.length > 0) {
                let categoryName = '';
                if (edge.condition_label) {
                  categoryName = edge.condition_label;
                } else {
                  categoryName = edge.sourceHandle.replace('handle-', '');
                }

                const findCategoryIndex = (name: string): number => {
                  if (!name) return categories.length > 0 ? 0 : -1;

                  const lowerName = name.toLowerCase();
                  const matchers = [
                    (cat: { id: string; category: string }) => cat.id === name || cat.category === name,
                    (cat: { id: string; category: string }) =>
                      (cat.id && (cat.id.includes(name) || name.includes(cat.id))) || (cat.category && (cat.category.includes(name) || name.includes(cat.category))),
                    (cat: { id: string; category: string }) => (cat.id && cat.id.toLowerCase() === lowerName) || (cat.category && cat.category.toLowerCase() === lowerName),
                  ];

                  for (const matcher of matchers) {
                    const idx = categories.findIndex(matcher);
                    if (idx !== -1) return idx;
                  }

                  if (name.includes('any_to_kor') && categories[0]?.id?.includes('any_to_kor')) return 0;
                  if (name.includes('kor_to_any') && categories[1]?.id?.includes('kor_to_any')) return 1;

                  return categories.length > 0 ? 0 : -1;
                };

                const categoryIndex = findCategoryIndex(categoryName);

                if (categoryIndex !== -1 && categoryIndex < categories.length) {
                  const newSourceHandle = `handle-category-${categoryIndex}`;
                  hasChanges = true;
                  processedEdgeIds.add(edge.id);
                  return {
                    ...edge,
                    sourceHandle: newSourceHandle,
                    data: {
                      ...edge.data,
                      sourceHandle: newSourceHandle,
                      source_handle: newSourceHandle,
                    },
                  };
                }
              }
            }
          }

          return edge;
        });

        return hasChanges ? updatedEdges : prevEdges;
      });
    }

    if (pendingCategorizerEdgesRef.current.length === 0) {
      return;
    }

    const edgesToProcess: Array<{ edge: any; sourceNodeId: string }> = [];
    const edgesToKeep: Array<{ edge: any; sourceNodeId: string }> = [];

    pendingCategorizerEdgesRef.current.forEach(pending => {
      const sourceNode = nodesWithCategoriesForEdgeUpdate.find(node => node.id === pending.sourceNodeId);
      if (sourceNode) {
        edgesToProcess.push(pending);
      } else {
        edgesToKeep.push(pending);
      }
    });

    if (edgesToProcess.length === 0) {
      return;
    }

    const convertedEdges = edgesToProcess
      .map(({ edge, sourceNodeId }) => {
        const sourceNode = nodesWithCategoriesForEdgeUpdate.find(node => node.id === sourceNodeId);
        if (!sourceNode) {
          return null;
        }
        return convertCategorizerEdge(edge, sourceNode);
      })
      .filter((edge): edge is CustomEdge => edge !== null);

    if (convertedEdges.length > 0) {
      setEdges(prevEdges => {
        const existingEdgeIds = new Set(prevEdges.map(e => e.id));
        const validNewEdges = convertedEdges.filter(e => {
          if (existingEdgeIds.has(e.id)) {
            return false;
          }
          if (!e.source || !e.target) {
            return false;
          }
          const sourceNode = nodes.find(n => n.id === e.source);
          if (sourceNode && (sourceNode.type === 'agent__categorizer' || sourceNode.type === 'condition' || sourceNode.type === 'agent__reviewer')) {
            if (!e.sourceHandle) {
              return false;
            }
          }
          return true;
        });

        if (validNewEdges.length === 0) {
          return prevEdges;
        }

        return [...prevEdges, ...validNewEdges];
      });

      pendingCategorizerEdgesRef.current = edgesToKeep;
    }
  }, [nodes.length, edges.length, data?.nodes?.length]);

  useEffect(() => {
    if (!containerRef.current) return;

    const container = containerRef.current;

    const currentAgentId = data?.id || '';
    const prevAgentId = prevAgentIdRef.current || '';
    const agentIdChanged = currentAgentId && currentAgentId !== prevAgentId;
    const hasEdges = (data?.edges && data.edges.length > 0) || edges.length > 0;

    if (agentIdChanged) {
      setContainerSizeState({ width: 100, height: 100 });
      setReactFlowInstance(null);
      scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 6 });
    }
    if (!hasEdges) {
      scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 5 });

      const currentNodesLength = (nodes || []).length;
      const firstNodeType = (nodes || [])[0]?.type;
      const hasOnlyInputNode = currentNodesLength === 1 && firstNodeType === 'input__basic';

      if (hasOnlyInputNode && reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
        scheduleContainerSizeUpdates(
          () => {
            setContainerSize();
            if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
              (reactFlowInstance as any).updateDimensions();
            }
          },
          { immediate: true, rafCount: 3 }
        );
      }

      if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
        scheduleContainerSizeUpdates(
          () => {
            if (containerRef.current) {
              const container = containerRef.current;
              const width = container.offsetWidth || container.clientWidth || 0;
              const height = container.offsetHeight || container.clientHeight || 0;

              if (width > 0 && height > 0) {
                setContainerSize();
                (reactFlowInstance as any).updateDimensions();
              }
            }
          },
          { immediate: true, rafCount: 3 }
        );
      }
    }

    const cleanup = rafUntil(
      () => {
        const width = container.offsetWidth || container.clientWidth || 0;
        const height = container.offsetHeight || container.clientHeight || 0;
        return width > 0 && height > 0;
      },
      () => setContainerSize(),
      60
    );

    return cleanup;
  }, [data?.id, edges.length, data?.edges, setContainerSize, reactFlowInstance]);

  useEffect(() => {
    const originalError = console.error;
    const errorHandler = (...args: any[]) => {
      const errorMessage = args.join(' ');
      if (errorMessage.includes('React Flow parent container needs a width and a height')) {
        return;
      }
      originalError.apply(console, args);
    };

    console.error = errorHandler;

    return () => {
      console.error = originalError;
    };
  }, []);

  const defaultNodes = useMemo(() => {
    if (!data?.nodes || data.nodes.length === 0) {
      return [];
    }

    const currentNodes = data.nodes;

    return currentNodes.map((node: any, index: number) => {
      const renderSafePosition = {
        x: typeof node.position?.x === 'number' && !isNaN(node.position.x) && isFinite(node.position.x) ? node.position.x : 100 + index * 700,
        y: typeof node.position?.y === 'number' && !isNaN(node.position.y) && isFinite(node.position.y) ? node.position.y : 150,
      };

      return {
        ...node,
        id: String(node.id),
        type: String(node.type || 'default'),
        position: renderSafePosition,
        data: node.data || {},
        width: Number(node.width) || 400,
        height: Number(node.height) || 200,
      };
    });
  }, [data?.id, data?.nodes?.length]);

  const containerStyle = useMemo(() => {
    let minWidth = Math.max(containerSize.width, 100);
    let minHeight = Math.max(containerSize.height, 100);

    if (typeof window !== 'undefined') {
      const windowWidth = window.innerWidth || 100;
      const windowHeight = window.innerHeight - 70 || 100;
      minWidth = Math.max(minWidth, windowWidth);
      minHeight = Math.max(minHeight, windowHeight);
    }

    minWidth = Math.max(minWidth, 100);
    minHeight = Math.max(minHeight, 100);

    return {
      width: `${minWidth}px`,
      height: `${minHeight}px`,
      minWidth: `${minWidth}px`,
      minHeight: `${minHeight}px`,
      zIndex: 1,
      position: 'relative' as const,
      display: 'block' as const,
      overflow: 'hidden' as const,
      boxSizing: 'border-box' as const,
    };
  }, [containerSize.width, containerSize.height]);

  const DEFAULT_EDGE_OPTIONS = {
    type: 'none',
    animated: false,
  };

  const CONNECTION_LINE_STYLE = {
    strokeWidth: 2,
    stroke: '#6B7280',
  };

  return (
    <div ref={containerRef} style={containerStyle}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        nodeTypes={NODE_TYPE}
        edgeTypes={EDGE_TYPE}
        onDragOver={onDragOver}
        onDrop={onDrop}
        onConnect={onConnect}
        panOnScroll={false}
        isValidConnection={isValidConnection}
        connectionMode={ConnectionMode.Loose}
        connectionLineType={ConnectionLineType.Bezier}
        connectionLineStyle={CONNECTION_LINE_STYLE}
        reconnectRadius={20}
        snapToGrid={false}
        snapGrid={[15, 15]}
        defaultEdgeOptions={DEFAULT_EDGE_OPTIONS}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onInit={instance => {
          const currentGraphId = data?.id || 'default';

          if (reactFlowInstance && reactFlowInstance === instance) return;

          const hasEdges = (data?.edges && data.edges.length > 0) || edges.length > 0;

          if (containerRef.current) {
            const viewportWidth = typeof window !== 'undefined' ? window.innerWidth || 100 : 100;
            const viewportHeight = typeof window !== 'undefined' ? window.innerHeight - 70 || 100 : 100;

            setContainerSizeState({
              width: Math.max(viewportWidth, 100),
              height: Math.max(viewportHeight, 100),
            });

            setContainerSize();
          }

          if (!hasEdges) {
            const safeUpdateDimensions = () => {
              if (containerRef.current && instance && (instance as any).updateDimensions) {
                const container = containerRef.current;
                const width = container.offsetWidth || container.clientWidth || 0;
                const height = container.offsetHeight || container.clientHeight || 0;

                if (width === 0 || height === 0) {
                  setContainerSize();
                  return;
                }
                setContainerSize();
                (instance as any).updateDimensions();
              }
            };

            scheduleContainerSizeUpdates(safeUpdateDimensions, { immediate: true, rafCount: 4 });
          }

          const updateDimensions = () => {
            if (containerRef.current) {
              setContainerSize();

              const container = containerRef.current;
              const width = container.offsetWidth || container.clientWidth || 0;
              const height = container.offsetHeight || container.clientHeight || 0;

              if (width === 0 || height === 0) {
                if (containerRef.current) {
                  setContainerSize();
                  const retryContainer = containerRef.current;
                  const retryWidth = retryContainer.offsetWidth || retryContainer.clientWidth || 0;
                  const retryHeight = retryContainer.offsetHeight || retryContainer.clientHeight || 0;

                  if (retryWidth > 0 && retryHeight > 0 && instance && (instance as any).updateDimensions) {
                    setContainerSize();
                    (instance as any).updateDimensions();
                  }
                }
                return;
              }
            }

            setContainerSize();

            if (instance && (instance as any).updateDimensions) {
              try {
                if (containerRef.current) {
                  const container = containerRef.current;
                  const width = container.offsetWidth || container.clientWidth || 0;
                  const height = container.offsetHeight || container.clientHeight || 0;

                  if (width > 0 && height > 0) {
                    (instance as any).updateDimensions();
                    setContainerSize();
                  }
                }
              } catch (error) {
                setContainerSize();
              }
            } else {
              setContainerSize();
            }
          };
          scheduleContainerSizeUpdates(updateDimensions, { immediate: true, rafCount: 8 });

          if (!data.edges || data.edges.length === 0) {
            scheduleContainerSizeUpdates(
              () => {
                setContainerSize();
                updateDimensions();
              },
              { immediate: true, rafCount: 4 }
            );
          }

          setReactFlowInstance(instance);

          const checkAndSetSize = () => {
            if (containerRef.current && instance) {
              const container = containerRef.current;
              const width = container.offsetWidth || container.clientWidth || 0;
              const height = container.offsetHeight || container.clientHeight || 0;

              if (width === 0 || height === 0) {
                setContainerSize();
                if (containerRef.current && instance) {
                  const retryContainer = containerRef.current;
                  const retryWidth = retryContainer.offsetWidth || retryContainer.clientWidth || 0;
                  const retryHeight = retryContainer.offsetHeight || retryContainer.clientHeight || 0;

                  if (retryWidth > 0 && retryHeight > 0 && (instance as any).updateDimensions) {
                    setContainerSize();
                    (instance as any).updateDimensions();
                  }
                }
              } else if (instance && (instance as any).updateDimensions) {
                setContainerSize();
                (instance as any).updateDimensions();
              }
            }
          };

          scheduleContainerSizeUpdates(checkAndSetSize, { immediate: true, rafCount: 4 });

          if (instance) {
            if (instance && (instance as any).updateNodeInternals && typeof (instance as any).updateNodeInternals === 'function') {
              nodes.forEach(node => {
                if (node && node.id) {
                  (instance as any).updateNodeInternals(node.id);
                }
              });
            }

            if (edges.length > 0) {
              instance.setEdges(edges);
            }
          }

          if (instance && edges.length > 0) {
            instance.fitView({ duration: 0, padding: 0.1 });
            const currentEdges = instance.getEdges();
            if (currentEdges.length !== edges.length) {
              const currentViewport = instance.getViewport();
              instance.setViewport({
                x: currentViewport.x + 0.1,
                y: currentViewport.y + 0.1,
                zoom: currentViewport.zoom,
              });
              instance.setViewport(currentViewport);
            }

            const domEdges = document.querySelectorAll('.react-flow__edge');

            if (domEdges.length === 0 && edges.length > 0) {
              const forceRenderSequence = async () => {
                instance.setViewport({ x: 0, y: 0, zoom: 1 });
                await waitForFrames(2);

                const reactFlowPane = document.querySelector('.react-flow__pane');
                if (reactFlowPane) {
                  const pane = reactFlowPane as HTMLElement;
                  pane.style.transform = 'translateZ(0)';
                  pane.style.willChange = 'transform';
                  await waitForFrames(2);
                  pane.style.transform = '';
                  pane.style.willChange = '';
                }

                const finalDomEdges = document.querySelectorAll('.react-flow__edge');

                if (finalDomEdges.length === 0) {
                  const event = new CustomEvent('forceReactFlowRemount', {
                    detail: { graphId: currentGraphId },
                  });
                  window.dispatchEvent(event);
                }
              };

              forceRenderSequence();
            }
          }
          if (defaultNodes.length > 0) {
            instance.fitView({
              padding: 0.2,
              duration: 300,
            });
          }
        }}
        onWheel={handleWheel}
        onNodeDragStart={handleNodeDragStart}
        onNodeDrag={handleNodeDrag}
        onNodeDragStop={handleNodeDragStop}
        onMoveStart={handleMoveStart}
        onMove={handleMove}
        onMoveEnd={handleMoveEnd}
        defaultViewport={{ x: 0, y: 0, zoom: 1 }}
        autoPanOnConnect={false}
        autoPanOnNodeDrag={false}
        preventScrolling={false}
        zoomOnScroll={true}
        zoomOnPinch={true}
        zoomOnDoubleClick={true}
        onViewportChange={handleViewportChange}
        nodesDraggable={!readOnly}
        nodesConnectable={!readOnly}
        elementsSelectable={!readOnly}
        nodesFocusable={!readOnly}
        edgesFocusable={!readOnly}
        deleteKeyCode={readOnly ? null : 'Delete'}
        multiSelectionKeyCode={readOnly ? null : 'Meta'}
        selectionKeyCode={readOnly ? null : 'Meta'}
        panOnDrag={true}
        attributionPosition='top-right'
        minZoom={0.05}
        maxZoom={2.0}
        zoomActivationKeyCode={null}
        proOptions={{ hideAttribution: true }}
        selectNodesOnDrag={false}
        translateExtent={useMemo(
          () => [
            [-5000, -5000],
            [10000, 10000],
          ],
          []
        )}
      >
        <Controls position={'bottom-right'} />
        <Background bgColor='#f1f4fd' variant={BackgroundVariant.Dots} />
        <div
          style={{ zIndex: 1000, pointerEvents: 'auto', display: 'flex', flexDirection: 'row', gap: '10px', justifyContent: 'space-between' }}
          onMouseDown={e => e.stopPropagation()}
          onClick={e => e.stopPropagation()}
        >
          <GraphHeader name={data.name} onClickDesc={handleDescription} readOnly={readOnly} />
          <GraphController
            onSaveClick={handleSave}
            onChatClick={handleChat}
            onDeployClick={handleDeploy}
            onLayoutClick={handleLayout}
            readOnly={readOnly}
            unsavedChanges={unsavedChanges}
          />
          <ChatTest isChatVisible={isChatVisible && !!data?.id} setIsChatVisible={() => setIsChatVisible(prev => !prev)} agentId={data?.id || ''} />
        </div>
      </ReactFlow>

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

      <DeployAgentStep3ResAllocPopupPage
        isOpen={deployStep === 3}
        stepperItems={deployStepperItems}
        builderName={data.name}
        onClose={() => {
          setDeployStep(0);
          resetDeployData();
        }}
        onPreviousStep={() => setDeployStep(2)}
        onDeploySuccess={() => {
          setDeployStep(0);
          navigate('/deploy/agentDeploy');
        }}
        currentStep={2}
      />
    </div>
  );
};

export default Graph;
