import { useEffect, useRef, useCallback } from 'react';
import type { CustomNode, CustomEdge } from '@/components/agents/builder/types/Agents';
import {
    transformConditionSourceHandle,
    transformReviewerSourceHandle,
    transformCategorizerSourceHandle,
    getDefaultTargetHandle,
    getDefaultSourceHandle,
    createFinalEdge,
    createNodeIdMaps,
    findNode,
} from '@/components/agents/builder/pages/graph/utils/edgeTransformUtils';
import { scheduleContainerSizeUpdates } from '@/components/agents/builder/pages/graph/utils/containerSizeManager';

interface UseGraphDataLoaderProps {
    data: any;
    setNodes: (nodes: CustomNode[] | ((prev: CustomNode[]) => CustomNode[])) => void;
    setEdges: (edges: CustomEdge[] | ((prev: CustomEdge[]) => CustomEdge[])) => void;
    setSelectedPromptIdRepo: (fn: (prev: Record<string, string | null>) => Record<string, string | null>) => void;
    setSelectedFewShotIdRepo: (fn: (prev: Record<string, string | null>) => Record<string, string | null>) => void;
    setSelectedToolsRepo: (fn: (prev: Record<string, any[]>) => Record<string, any[]>) => void;
    setSelectedLLMRepo: (fn: (prev: Record<string, any>) => Record<string, any>) => void;
    setSelectedKnowledgeIdRepo: (fn: (prev: Record<string, string>) => Record<string, string>) => void;
    setSelectedKnowledgeRepoKind: (fn: (prev: Record<string, string>) => Record<string, string>) => void;
    setSelectedKnowledgeRetrieverId: (fn: (prev: Record<string, string>) => Record<string, string>) => void;
    setSelectedKnowledgeNameRepo: (fn: (prev: Record<string, string>) => Record<string, string>) => void;
    setContainerSize: () => void;
    reactFlowInstance: any;
    containerRef: React.RefObject<HTMLDivElement | null>;
}

interface UseGraphDataLoaderReturn {
    prevAgentIdRef: React.MutableRefObject<string>;
    prevDataNodesRef: React.MutableRefObject<string>;
    prevDataEdgesRef: React.MutableRefObject<string>;
    nodesInitRef: React.MutableRefObject<boolean>;
    edgesInitRef: React.MutableRefObject<boolean>;
    userDeletedEdgesRef: React.MutableRefObject<Set<string>>;
    pendingCategorizerEdgesRef: React.MutableRefObject<Array<{ edge: any; sourceNodeId: string }>>;
}

const normalizeNodes = (dataNodes: any[], isNewTemplate: boolean): any[] => {
    return (dataNodes || []).map((node: any) => {
        const nodeId = String(node.id);
        const nodeData = { ...node.data };

        if (isNewTemplate && (node.type === 'agent__generator' || node.type === 'Generator')) {
            nodeData.serving_name = '';
            nodeData.serving_model = '';
        }

        return {
            ...node,
            id: nodeId,
            data: {
                ...nodeData,
                innerData: {
                    ...(node.data?.innerData || {}),
                    isRun: false,
                    isDone: false,
                    isError: false,
                    isRunning: false,
                    isCompleted: false,
                    hasError: false,
                    logData: node.data?.innerData?.logData || [],
                },
            },
        };
    });
};

const restoreAtomData = (
    normalizedNodes: any[],
    setSelectedPromptIdRepo: (fn: (prev: Record<string, string | null>) => Record<string, string | null>) => void,
    setSelectedFewShotIdRepo: (fn: (prev: Record<string, string | null>) => Record<string, string | null>) => void,
    setSelectedToolsRepo: (fn: (prev: Record<string, any[]>) => Record<string, any[]>) => void,
    setSelectedLLMRepo: (fn: (prev: Record<string, any>) => Record<string, any>) => void,
    setSelectedKnowledgeIdRepo: (fn: (prev: Record<string, string>) => Record<string, string>) => void,
    setSelectedKnowledgeRepoKind: (fn: (prev: Record<string, string>) => Record<string, string>) => void,
    setSelectedKnowledgeRetrieverId: (fn: (prev: Record<string, string>) => Record<string, string>) => void,
    setSelectedKnowledgeNameRepo: (fn: (prev: Record<string, string>) => Record<string, string>) => void
): void => {
    normalizedNodes.forEach((node: any) => {
        const nodeId = String(node.id);
        const nodeData = node.data || {};

        if (nodeData.prompt_id && nodeData.prompt_id.trim() !== '') {
            setSelectedPromptIdRepo((prev) => {
                if (prev[nodeId] !== nodeData.prompt_id) {
                    return { ...prev, [nodeId]: nodeData.prompt_id };
                }
                return prev;
            });
        } else {
            setSelectedPromptIdRepo((prev) => {
                if (prev[nodeId] !== null && prev[nodeId] !== undefined) {
                    return { ...prev, [nodeId]: null };
                }
                return prev;
            });
        }

        if (nodeData.fewshot_id && nodeData.fewshot_id.trim() !== '') {
            setSelectedFewShotIdRepo((prev) => {
                if (prev[nodeId] !== nodeData.fewshot_id) {
                    return { ...prev, [nodeId]: nodeData.fewshot_id };
                }
                return prev;
            });
        } else {
            setSelectedFewShotIdRepo((prev) => {
                if (prev[nodeId] !== null && prev[nodeId] !== undefined) {
                    return { ...prev, [nodeId]: null };
                }
                return prev;
            });
        }

        if (nodeData.tools && Array.isArray(nodeData.tools) && nodeData.tools.length > 0) {
            setSelectedToolsRepo((prev) => {
                if (JSON.stringify(prev[nodeId]) !== JSON.stringify(nodeData.tools)) {
                    return { ...prev, [nodeId]: nodeData.tools };
                }
                return prev;
            });
        } else {
            setSelectedToolsRepo((prev) => {
                if (prev[nodeId] !== null && prev[nodeId] !== undefined) {
                    return { ...prev, [nodeId]: null as any };
                }
                return prev;
            });
        }

        let servingName = '';
        let servingModel = '';

        if (nodeData.serving_name && nodeData.serving_name.trim() !== '') {
            servingName = nodeData.serving_name;
        }
        if (nodeData.serving_model && nodeData.serving_model.trim() !== '') {
            servingModel = nodeData.serving_model;
        }

        const nodeType = node.type || '';

        if ((nodeType === 'retriever__rewriter_hyde' || nodeType === 'retriever__rewriter_multiquery') && !servingName) {
            const llmConfig = nodeData?.query_rewriter?.llm_chain?.llm_config;
            if (llmConfig) {
                servingName = servingName || llmConfig.serving_name || '';
                servingModel = servingModel || llmConfig.serving_model || '';
            }
        }

        if ((nodeType === 'retriever__doc_compressor' || nodeType === 'retriever__doc_filter') && !servingName) {
            const llmConfig = nodeData?.context_refiner?.llm_chain?.llm_config;
            if (llmConfig) {
                servingName = servingName || llmConfig.serving_name || '';
                servingModel = servingModel || llmConfig.serving_model || '';
            }
        }

        if (nodeType === 'retriever__doc_reranker' && !servingName) {
            const modelInfo = nodeData?.context_refiner?.rerank_cnf?.model_info;
            if (modelInfo) {
                servingName = servingName || modelInfo.serving_name || '';
                servingModel = servingModel || modelInfo.serving_model || '';
            }
        }

        if (servingName && servingName.trim() !== '' && servingModel && servingModel.trim() !== '') {
            setSelectedLLMRepo((prev) => {
                const currentLLM = prev[nodeId];
                if (!currentLLM || currentLLM.servingName !== servingName || currentLLM.servingModel !== servingModel) {
                    return {
                        ...prev,
                        [nodeId]: { servingName, servingModel },
                    };
                }
                return prev;
            });
        } else {
            setSelectedLLMRepo((prev) => {
                if (prev[nodeId] !== null && prev[nodeId] !== undefined) {
                    const updated = { ...prev };
                    delete updated[nodeId];
                    return updated;
                }
                return prev;
            });
        }

        // 🔥 Knowledge atom 처리 (Retriever 노드용)
        if (nodeType === 'retriever__knowledge') {
            const knowledgeRetriever = nodeData.knowledge_retriever || {};
            const repoId = knowledgeRetriever.repo_id || '';
            const repoKind = knowledgeRetriever.repo_kind || '';
            const retrieverId = nodeData.retriever_id || repoId || '';

            if (repoId && repoId.trim() !== '') {
                setSelectedKnowledgeIdRepo((prev) => {
                    if (prev[nodeId] !== repoId) {
                        return { ...prev, [nodeId]: repoId };
                    }
                    return prev;
                });
                if (repoKind) {
                    setSelectedKnowledgeRepoKind((prev) => {
                        if (prev[nodeId] !== repoKind) {
                            return { ...prev, [nodeId]: repoKind };
                        }
                        return prev;
                    });
                }
                if (retrieverId) {
                    setSelectedKnowledgeRetrieverId((prev) => {
                        if (prev[nodeId] !== retrieverId) {
                            return { ...prev, [nodeId]: retrieverId };
                        }
                        return prev;
                    });
                }
            } else {
                // 🔥 노드 데이터에 지식 정보가 없으면 atom에서 제거
                setSelectedKnowledgeIdRepo((prev) => {
                    if (prev[nodeId]) {
                        const updated = { ...prev };
                        delete updated[nodeId];
                        return updated;
                    }
                    return prev;
                });
                setSelectedKnowledgeRepoKind((prev) => {
                    if (prev[nodeId]) {
                        const updated = { ...prev };
                        delete updated[nodeId];
                        return updated;
                    }
                    return prev;
                });
                setSelectedKnowledgeRetrieverId((prev) => {
                    if (prev[nodeId]) {
                        const updated = { ...prev };
                        delete updated[nodeId];
                        return updated;
                    }
                    return prev;
                });
                setSelectedKnowledgeNameRepo((prev) => {
                    if (prev[nodeId]) {
                        const updated = { ...prev };
                        delete updated[nodeId];
                        return updated;
                    }
                    return prev;
                });
            }
        }
    });
};

const convertEdges = (
    dataEdges: any[],
    normalizedNodes: any[],
    userDeletedEdges: Set<string>
): CustomEdge[] => {
    const { nodeIdMap, nodeBaseIdMap } = createNodeIdMaps(normalizedNodes);

    const uniqueEdgesMap = new Map<string, any>();
    const uniqueEdgeKeyMap = new Map<string, any>();

    (dataEdges || []).forEach((edge: any) => {
        const edgeId = String(edge.id);

        if (userDeletedEdges.has(edgeId)) {
            return;
        }

        const sourceId = String(edge.source);
        const targetId = String(edge.target);
        const sourceHandle = edge.sourceHandle || edge.source_handle || 'none';
        const edgeKey = `${sourceId}-${targetId}-${sourceHandle}`;

        if (uniqueEdgesMap.has(edgeId) || uniqueEdgeKeyMap.has(edgeKey)) {
            return;
        }

        uniqueEdgesMap.set(edgeId, edge);
        uniqueEdgeKeyMap.set(edgeKey, edge);
    });

    const uniqueEdges = Array.from(uniqueEdgesMap.values());
    const convertedEdges = uniqueEdges
        .filter((edge: any) => {
            const sourceId = String(edge.source);
            const targetId = String(edge.target);

            const sourceNode = findNode(sourceId, nodeIdMap, nodeBaseIdMap);
            const targetNode = findNode(targetId, nodeIdMap, nodeBaseIdMap);

            if (!sourceNode || !targetNode) {
                return false;
            }

            edge.source = sourceNode.id;
            edge.target = targetNode.id;

            return true;
        })
        .map((edge: any) => {
            try {
                const sourceId = String(edge.source);
                const targetId = String(edge.target);

                const sourceNode = findNode(sourceId, nodeIdMap, nodeBaseIdMap);
                const targetNode = findNode(targetId, nodeIdMap, nodeBaseIdMap);

                if (!sourceNode || !targetNode) {
                    return null;
                }

                let sourceHandle = edge.sourceHandle || edge.source_handle || null;
                let targetHandle = edge.targetHandle || edge.target_handle || null;
                let conditionLabel = edge.condition_label || (edge.data as any)?.condition_label || undefined;

                if (sourceNode.type === 'condition') {
                    const result = transformConditionSourceHandle(edge, sourceNode);
                    sourceHandle = result.sourceHandle;
                    conditionLabel = result.conditionLabel;
                } else if (sourceNode.type === 'agent__reviewer') {
                    sourceHandle = transformReviewerSourceHandle(edge);
                } else if (sourceNode.type === 'agent__categorizer') {
                    sourceHandle = transformCategorizerSourceHandle(edge, sourceNode);
                } else if (!sourceHandle) {
                    sourceHandle = getDefaultSourceHandle(sourceNode);
                }

                if (sourceNode.type === 'input__basic') {
                    if (!sourceHandle || sourceHandle === 'null' || sourceHandle === 'undefined' || sourceHandle === '') {
                        sourceHandle = 'input_right';
                    }
                }

                if (!targetHandle || targetHandle === 'null' || targetHandle === 'undefined' || targetHandle === '') {
                    targetHandle = getDefaultTargetHandle(targetNode, sourceNode);
                }

                return createFinalEdge(edge, sourceHandle, targetHandle, conditionLabel, sourceNode, targetNode);
            } catch (error) {
                console.warn('엣지 변환 중 오류:', error);
                return null;
            }
        }).filter((edge): edge is CustomEdge => edge !== null);

    const edgeIdMap = new Map<string, CustomEdge>();
    const edgeKeyMap = new Map<string, number>();

    convertedEdges.forEach((edge: any) => {
        const edgeId = String(edge.id);
        if (userDeletedEdges.has(edgeId) || edgeIdMap.has(edgeId)) {
            return;
        }
        const targetNode = nodeIdMap.get(String(edge.target));
        const isMergerTarget = targetNode && (targetNode.type === 'merger' || targetNode.type === 'union');

        if (!isMergerTarget) {
            const sourceHandleForKey = edge.sourceHandle || 'none';
            const edgeKey = `${edge.source}-${edge.target}-${sourceHandleForKey}`;
            const existingCount = edgeKeyMap.get(edgeKey) || 0;

            if (existingCount > 0) {
                return;
            }
            edgeKeyMap.set(edgeKey, 1);
        }

        edgeIdMap.set(edgeId, edge);
    });

    return Array.from(edgeIdMap.values());
};

export const useGraphDataLoader = ({
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
}: UseGraphDataLoaderProps): UseGraphDataLoaderReturn => {
    const prevAgentIdRef = useRef<string>('');
    const prevDataNodesRef = useRef<string>('');
    const prevDataEdgesRef = useRef<string>('');
    const nodesInitRef = useRef<boolean>(true);
    const edgesInitRef = useRef<boolean>(true);
    const userDeletedEdgesRef = useRef<Set<string>>(new Set());
    const pendingCategorizerEdgesRef = useRef<Array<{ edge: any; sourceNodeId: string }>>([]);

    const updateContainerSize = useCallback(() => {
        scheduleContainerSizeUpdates(setContainerSize);
        if (reactFlowInstance && (reactFlowInstance as any).updateDimensions) {
            if (containerRef.current) {
                const container = containerRef.current;
                const width = container.offsetWidth || container.clientWidth || 0;
                const height = container.offsetHeight || container.clientHeight || 0;
                if (width > 0 && height > 0) {
                    (reactFlowInstance as any).updateDimensions();
                }
            }
        }
    }, [setContainerSize, reactFlowInstance, containerRef]);

    // 데이터 로딩 Effect
    useEffect(() => {
        if (!data || !data.nodes) {
            return;
        }

        const currentAgentId = data?.id || '';
        const prevAgentId = prevAgentIdRef.current || '';
        const currentNodesKey = JSON.stringify(data.nodes.map((n: any) => ({ id: n.id, type: n.type })));
        const agentIdChanged = currentAgentId && currentAgentId !== prevAgentId;

        if (agentIdChanged) {
            prevDataNodesRef.current = '';
            setNodes([]);
        }

        if (!agentIdChanged && prevDataNodesRef.current === currentNodesKey) {
            return;
        }

        prevDataNodesRef.current = currentNodesKey;
        prevAgentIdRef.current = currentAgentId;

        const isNewTemplate = !currentAgentId || currentAgentId === '';
        const normalizedNodes = normalizeNodes(data.nodes, isNewTemplate);
        setNodes(normalizedNodes);
        restoreAtomData(
            normalizedNodes,
            setSelectedPromptIdRepo,
            setSelectedFewShotIdRepo,
            setSelectedToolsRepo,
            setSelectedLLMRepo,
            setSelectedKnowledgeIdRepo,
            setSelectedKnowledgeRepoKind,
            setSelectedKnowledgeRetrieverId,
            setSelectedKnowledgeNameRepo
        );

        pendingCategorizerEdgesRef.current = [];

        if (!data.edges || data.edges.length === 0) {
            setEdges([]);
            prevDataEdgesRef.current = '';
            userDeletedEdgesRef.current.clear();
            updateContainerSize();
            return;
        }

        const checkNodeIdMap = new Map<string, any>();
        normalizedNodes.forEach((node: any) => {
            checkNodeIdMap.set(String(node.id), node);
        });

        const allEdgesMatch = data.edges.every((edge: any) => {
            const sourceId = String(edge.source);
            const targetId = String(edge.target);
            return checkNodeIdMap.has(sourceId) && checkNodeIdMap.has(targetId);
        });

        if (!allEdgesMatch) {
            setEdges([]);
            prevDataEdgesRef.current = '';
            userDeletedEdgesRef.current.clear();
            updateContainerSize();
            return;
        }

        const convertedEdges = convertEdges(data.edges, normalizedNodes, userDeletedEdgesRef.current);

        if (convertedEdges.length === 0 && agentIdChanged) {
            setEdges([]);
            prevDataEdgesRef.current = '';
            updateContainerSize();
        } else if (convertedEdges.length > 0) {
            setEdges(convertedEdges);
            prevDataEdgesRef.current = JSON.stringify(data.edges.map((e: any) => e.id));
            edgesInitRef.current = false;
            updateContainerSize();
        }
    }, [
        data?.id,
        data?.nodes?.length,
        data?.edges?.length,
        setNodes,
        setEdges,
        setSelectedPromptIdRepo,
        setSelectedFewShotIdRepo,
        setSelectedToolsRepo,
        setSelectedLLMRepo,
        updateContainerSize,
    ]);

    return {
        prevAgentIdRef,
        prevDataNodesRef,
        prevDataEdgesRef,
        nodesInitRef,
        edgesInitRef,
        userDeletedEdgesRef,
        pendingCategorizerEdgesRef,
    };
};
