import React, { useEffect, useMemo, useRef, useState } from 'react';

import { ReactFlowProvider } from '@xyflow/react';
import { useLocation } from 'react-router-dom';

import { nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { builderLogState, hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom';
import {
  messagesAtom,
  progressMessageAtom,
  streamingMessageAtom,
  tracingBaseInfoAtom,
  tracingMessagesAtom,
  tracingNodeIdAtom,
} from '@/components/agents/builder/atoms/messagesAtom';
import { selectedLLMRepoAtom } from '@/components/agents/builder/atoms/llmAtom';
import { useStreamLogs } from '@/components/agents/builder/hooks/useStreamLogs';
import { AgentBuilderUIProviders } from '@/components/agents/builder/providers/AgentBuilderUIProviders';
import { DnDProvider } from '@/components/agents/builder/utils/DnDContext';
import { useApiQuery } from '@/hooks/common/api/useApi';
import type { AgentBuilderDetailRes } from '@/services/agent/builder/types';
import { useModal } from '@/stores/common/modal/useModal';
import { useAtom, useSetAtom } from 'jotai';
import { useQueryClient } from '@tanstack/react-query';

import Graph from './graph/Graph';
import GraphSidebar from './graph/GraphSidebar';

const GraphErrorBoundary: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [hasError, setHasError] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const handleRetry = () => {
    setHasError(false);
    setError(null);
  };

  const handleGoBack = () => {
    window.history.back();
  };

  if (hasError) {
    return (
      <div style={{ width: '100%', height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column' }}>
        <h2 className='text-xl font-bold text-red-600 mb-4'>캔버스 로드 중 오류가 발생했습니다</h2>
        <p className='text-gray-600 mb-4'>에러: {error?.message}</p>
        <div className='space-x-4'>
          <button onClick={handleRetry} className='px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600'>
            다시 시도
          </button>
          <button onClick={handleGoBack} className='px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600'>
            뒤로 가기
          </button>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};

const SafeGraph = ({ data, hasInitialData, readOnly = false }: { data: any; hasInitialData?: boolean; readOnly?: boolean }) => {
  try {
    return (
      <GraphErrorBoundary>
        <Graph data={data} hasInitialData={hasInitialData} readOnly={readOnly} />
      </GraphErrorBoundary>
    );
  } catch (error) {
    return (
      <div className='graph-pull-back-1' style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column' }}>
        <h2 className='text-2xl font-bold text-blue-600 mb-4'>빌더 캔버스</h2>
        <p className='text-gray-600 mb-4'>에이전트: {data.name}</p>
        <p className='text-gray-600 mb-4'>설명: {data.description}</p>
        <p className='text-gray-600 mb-4'>노드 수: {data.nodes?.length || 0}</p>
        <p className='text-gray-600 mb-4'>엣지 수: {data.edges?.length || 0}</p>
        <div className='mt-4 space-x-4'>
          <button onClick={() => window.history.back()} className='px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600'>
            뒤로 가기
          </button>
          <button onClick={() => window.location.reload()} className='px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600'>
            페이지 새로고침
          </button>
        </div>
      </div>
    );
  }
};

const SafeGraphSidebar = ({ readOnly = false }: { readOnly?: boolean }) => {
  try {
    return <GraphSidebar readOnly={readOnly} />;
  } catch (error) {
    return (
      <div className='graph-pull-back-2' style={{ width: '100%', height: '100%', backgroundColor: 'white', border: '1px solid #ccc', borderRadius: '8px', padding: '16px' }}>
        <h3 className='text-lg font-bold mb-4'>노드 목록</h3>
        <div className='space-y-2'>
          <div className='p-2 bg-blue-100 rounded cursor-pointer'>입력 노드</div>
          <div className='p-2 bg-green-100 rounded cursor-pointer'>출력 노드</div>
          <div className='p-2 bg-yellow-100 rounded cursor-pointer'>생성기 노드</div>
          <div className='p-2 bg-purple-100 rounded cursor-pointer'>도구 노드</div>
        </div>
      </div>
    );
  }
};

const GraphPage = () => {
  const location = useLocation();
  const { openAlert } = useModal();
  const queryClient = useQueryClient();

  const agentId = location.state?.agentId || location.state?.data?.id;
  const initialData = location.state?.data;
  const templateDetail = location.state?.templateDetail;
  const isReadOnly = location.state?.isReadOnly || false;
  const [showReadOnlyAlert, setShowReadOnlyAlert] = useState(true);
  const readOnlyAlertShownRef = useRef(false);
  const hasInitialDataFromState = Boolean(initialData?.nodes?.length);
  const [isTemplateInitialLoad, setIsTemplateInitialLoad] = useState<boolean>(!hasInitialDataFromState && !!templateDetail);

  const [, setMessages] = useAtom(messagesAtom);
  const [, setTracingMessages] = useAtom(tracingMessagesAtom);
  const setNodes = useSetAtom(nodesAtom);
  const [, setHasChatTested] = useAtom(hasChatTestedAtom);
  const setBuilderLogState = useSetAtom(builderLogState);
  const [, setProgressMessage] = useAtom(progressMessageAtom);
  const [, setStreamingMessage] = useAtom(streamingMessageAtom);
  const [, setTracingNodeId] = useAtom(tracingNodeIdAtom);
  const [, setTracingBaseInfo] = useAtom(tracingBaseInfoAtom);
  const [, setSelectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const { clearStreamLogs } = useStreamLogs();

  const isClearingRef = useRef(false);
  const prevPathnameRef = useRef(location.pathname);

  useEffect(() => {
    const currentPath = location.pathname;
    const prevPath = prevPathnameRef.current;

    const isGraphPage = currentPath.includes('/test/secret/graph2');
    const wasGraphPage = prevPath.includes('/test/secret/graph2');

    if (wasGraphPage && !isGraphPage && !isClearingRef.current) {
      isClearingRef.current = true;
      setMessages([]);
      setTracingMessages([]);
      setHasChatTested(false);
      setBuilderLogState([]);
      setProgressMessage('');
      setStreamingMessage('');
      setTracingNodeId([]);
      setTracingBaseInfo(null);
      clearStreamLogs();

      const viewportKeysToRemove: string[] = [];
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith('graph-viewport-')) {
          viewportKeysToRemove.push(key);
        }
      }

      if (viewportKeysToRemove.length > 0) {
        viewportKeysToRemove.forEach(key => {
          localStorage.removeItem(key);
        });
      }

      setNodes(prev => {
        const hasNodesToUpdate = prev.some(node => {
          const innerData = node.data?.innerData ?? {};
          return innerData.isRun || innerData.isDone || innerData.isError || innerData.isRunning || innerData.isCompleted || innerData.hasError;
        });

        if (!hasNodesToUpdate) {
          return prev;
        }

        return prev.map(node => {
          const innerData = node.data?.innerData ?? {};
          return {
            ...node,
            data: {
              ...node.data,
              innerData: {
                ...innerData,
                isRun: false,
                isDone: false,
                isError: false,
                isRunning: false,
                isCompleted: false,
                hasError: false,
              },
            },
          };
        });
      });

      isClearingRef.current = false;
    }

    prevPathnameRef.current = currentPath;
  }, [
    location.pathname,
    setMessages,
    setTracingMessages,
    setNodes,
    setHasChatTested,
    setBuilderLogState,
    setProgressMessage,
    setStreamingMessage,
    setTracingNodeId,
    setTracingBaseInfo,
    clearStreamLogs,
  ]);

  useEffect(() => {
    return () => {
      if (!isClearingRef.current) {
        setMessages([]);
        setTracingMessages([]);
        setHasChatTested(false);
        setBuilderLogState([]);
        setProgressMessage('');
        setStreamingMessage('');
        setTracingNodeId([]);
        setTracingBaseInfo(null);
        clearStreamLogs();

        setNodes(prev => {
          const hasNodesToUpdate = prev.some(node => {
            const innerData = node.data?.innerData ?? {};
            return innerData.isRun || innerData.isDone || innerData.isError || innerData.isRunning || innerData.isCompleted || innerData.hasError;
          });

          if (!hasNodesToUpdate) {
            return prev;
          }

          return prev.map(node => {
            const innerData = node.data?.innerData ?? {};
            return {
              ...node,
              data: {
                ...node.data,
                innerData: {
                  ...innerData,
                  isRun: false,
                  isDone: false,
                  isError: false,
                  isRunning: false,
                  isCompleted: false,
                  hasError: false,
                },
              },
            };
          });
        });
      }
    };
  }, []);

  const [currentGraph, setCurrentGraph] = useState<any>(() => {
    if (initialData && initialData.nodes && initialData.edges) {
      return {
        nodes: initialData.nodes || [],
        edges: initialData.edges || [],
      };
    }

    if (templateDetail && templateDetail.data && templateDetail.data.nodes && templateDetail.data.edges) {
      return {
        nodes: templateDetail.data.nodes || [],
        edges: templateDetail.data.edges || [],
      };
    }

    return { nodes: [], edges: [] };
  });

  useEffect(() => {
    (window as any).__PREVENT_REDIRECT__ = true;

    return () => {
      (window as any).__PREVENT_REDIRECT__ = false;
    };
  }, []);

  const shouldCallAPI = agentId && typeof agentId === 'string' && agentId.trim() !== '';
  const {
    data: agentBuilderResponse,
    isLoading,
    error,
  } = useApiQuery<AgentBuilderDetailRes>({
    queryKey: ['agent-builder', agentId],
    url: `/agent/builder/${agentId}`,
    timeout: 60000,
    enabled: shouldCallAPI,
    staleTime: 0,
    gcTime: 0,
    refetchOnMount: 'always',
    select: (data: any) => data,
  } as any);

  const agentBuilder = (agentBuilderResponse as any)?.data || agentBuilderResponse;

  const initialDataProcessedRef = useRef<string>('');
  const prevAgentIdRef = useRef<string>('');
  const prevTemplateDetailRef = useRef<any>(null);
  const initialDataRef = useRef(initialData);

  useEffect(() => {
    if (initialData && (initialData.nodes?.length || initialData.edges?.length)) {
      initialDataRef.current = initialData;
    }
  }, [initialData?.nodes?.length, initialData?.edges?.length]);

  useEffect(() => {
    if (templateDetail && prevTemplateDetailRef.current !== null && prevTemplateDetailRef.current !== templateDetail) {
      setSelectedLLMRepo({});
    }
    prevTemplateDetailRef.current = templateDetail;
  }, [templateDetail, setSelectedLLMRepo]);

  useEffect(() => {
    if (prevAgentIdRef.current !== agentId && prevAgentIdRef.current !== '') {
      queryClient.removeQueries({ queryKey: ['agent-builder', prevAgentIdRef.current] });
      queryClient.removeQueries({ queryKey: ['agent-builder', agentId] });
      initialDataProcessedRef.current = '';
    }
    if ((!agentId || agentId === '') && prevAgentIdRef.current !== '') {
      setSelectedLLMRepo({});
    }

    if (prevAgentIdRef.current !== agentId) {
      prevAgentIdRef.current = agentId || '';
    }

    if (initialDataProcessedRef.current === agentId) {
      return;
    }

    const currentInitialData = initialDataRef.current;

    if (currentInitialData && currentInitialData.nodes && currentInitialData.edges) {
      setCurrentGraph({
        nodes: currentInitialData.nodes || [],
        edges: currentInitialData.edges || [],
      });
      setIsTemplateInitialLoad(false);
      initialDataProcessedRef.current = agentId || '';
    } else if (templateDetail && templateDetail.data) {
      setCurrentGraph({
        nodes: templateDetail.data.nodes || [],
        edges: templateDetail.data.edges || [],
      });
      setIsTemplateInitialLoad(true);
      initialDataProcessedRef.current = agentId || '';
    } else if (agentId) {
      setCurrentGraph({ nodes: [], edges: [] });
      setIsTemplateInitialLoad(false);
      initialDataProcessedRef.current = agentId || '';
    }
  }, [agentId, templateDetail, queryClient]);

  useEffect(() => {
    if (!shouldCallAPI || !agentBuilder || isLoading || error) {
      return;
    }
    const resolveGraphPayload = (payload: any) => {
      if (!payload) {
        return { nodes: [], edges: [] };
      }

      const basePayload = typeof payload === 'object' && payload !== null && 'data' in payload && typeof payload.data === 'object' ? payload.data : payload;
      const graphPayload = typeof basePayload.graph === 'object' && basePayload.graph !== null ? basePayload.graph : basePayload;

      const nodes = graphPayload?.nodes || basePayload?.nodes || [];
      const edges = graphPayload?.edges || basePayload?.edges || [];

      return { nodes, edges };
    };

    const newGraph = resolveGraphPayload(agentBuilder);
    const hasGraphData = (newGraph.nodes?.length ?? 0) > 0 || (newGraph.edges?.length ?? 0) > 0;

    if (hasGraphData) {
      setCurrentGraph(newGraph);
      if (templateDetail && isTemplateInitialLoad) {
        setIsTemplateInitialLoad(false);
      }
    }
  }, [shouldCallAPI, agentBuilder, isLoading, error, agentId, templateDetail, isTemplateInitialLoad]);

  useEffect(() => {
    if (!isReadOnly || !showReadOnlyAlert || readOnlyAlertShownRef.current) {
      return;
    }

    const message = [
      '현재 진입하신 화면은 조회 전용 모드입니다. 편집이나 수정이 불가능합니다.',
      '조회 모드에서 가능한 기능',
      '• 그래프 확대/축소 및 이동',
      '• 노드와 연결선 구조 확인',
      '• 그래프 분석 및 상태 점검',
    ].join('\n');

    void openAlert({
      title: '안내',
      message,
      confirmText: '확인',
      onConfirm: () => setShowReadOnlyAlert(false),
      onClose: () => setShowReadOnlyAlert(false),
    });
    readOnlyAlertShownRef.current = true;
  }, [isReadOnly, showReadOnlyAlert, openAlert]);

  if (!agentId || typeof agentId !== 'string' || agentId.trim() === '') {
    return (
      <div style={{ width: '100%', height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column' }}>
        <h2 className='text-2xl font-bold text-red-600 mb-4'>Agent ID가 없습니다</h2>
        <p className='text-gray-600 mb-4'>올바른 경로로 접근해주세요.</p>
        <button onClick={() => window.history.back()} className='px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600'>
          뒤로 가기
        </button>
      </div>
    );
  }

  if (shouldCallAPI && error) {
    return (
      <div style={{ width: '100%', height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <div>에러가 발생했습니다: {error.message}</div>
      </div>
    );
  }

  try {
    return (
      <AgentBuilderUIProviders>
        <GraphErrorBoundary>
          <ReactFlowProvider>
            <DnDProvider>
              <div
                className='graph-wrap'
                style={{
                  margin: '-40px -48px',
                  width: 'calc(100% + 96px)',
                  height: 'calc(100vh - 70px)',
                  position: 'relative',
                  overflow: 'hidden',
                }}
              >
                <div
                  className='graph-side-bar'
                  style={{
                    position: 'absolute',
                    top: '77px',
                    left: '50px',
                    width: '250px',
                    height: '808px',
                    zIndex: 50,
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    borderRadius: '12px',
                    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
                    border: '1px solid rgba(0, 0, 0, 0.1)',
                    backdropFilter: 'blur(10px)',
                    transition: 'all 0.3s ease',
                  }}
                >
                  <SafeGraphSidebar readOnly={isReadOnly} />
                </div>

                <SafeGraph
                  key={agentId}
                  data={useMemo(
                    () => ({
                      ...currentGraph,
                      id: agentId,
                      name: agentBuilder?.name || initialData?.name || '이름 없음',
                      description: agentBuilder?.description || initialData?.description || '설명 없음',
                      created_at: agentBuilder?.createdAt || initialData?.created_at || '',
                      updated_at: agentBuilder?.updatedAt || initialData?.updated_at || '',
                      created_by: agentBuilder?.createdBy || initialData?.created_by || '',
                      updated_by: agentBuilder?.updatedBy || initialData?.updated_by || '',
                      project_id: agentId,
                      nodes: currentGraph.nodes || [],
                      edges: currentGraph.edges || [],
                    }),
                    [
                      agentId,
                      currentGraph.nodes?.length,
                      currentGraph.edges?.length,
                      agentBuilder?.name,
                      agentBuilder?.description,
                      agentBuilder?.createdAt,
                      agentBuilder?.updatedAt,
                      agentBuilder?.createdBy,
                      agentBuilder?.updatedBy,
                      initialData?.name,
                      initialData?.description,
                      initialData?.created_at,
                      initialData?.updated_at,
                      initialData?.created_by,
                      initialData?.updated_by,
                    ]
                  )}
                  hasInitialData={isTemplateInitialLoad}
                  readOnly={isReadOnly}
                />
                {/* </div> */}
              </div>
            </DnDProvider>
          </ReactFlowProvider>
        </GraphErrorBoundary>
      </AgentBuilderUIProviders>
    );
  } catch (error) {
    return (
      <div style={{ width: '100%', height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <div>캔버스 로드 중 오류가 발생했습니다</div>
      </div>
    );
  }
};

export default GraphPage;
