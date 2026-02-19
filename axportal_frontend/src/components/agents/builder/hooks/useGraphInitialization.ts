import { useEffect, useRef, useCallback } from 'react';
import type { CustomNode, CustomEdge } from '@/components/agents/builder/types/Agents';
import { scheduleContainerSizeUpdates } from '../pages/graph/utils';

interface UseGraphInitializationProps {
    dataId: string | undefined;
    hasInitialData: boolean | undefined;
    containerRef: React.RefObject<HTMLDivElement | null>;
    setContainerSize: () => void | boolean;
    setContainerSizeState: (size: { width: number; height: number }) => void;
    setNodes: (nodes: CustomNode[]) => void;
    setEdges: (edges: CustomEdge[]) => void;
    setReactFlowInstance: (instance: any) => void;
    setSelectedPromptIdRepo: (repo: Record<string, string | null>) => void;
    setSelectedFewShotIdRepo: (repo: Record<string, string | null>) => void;
    setSelectedToolsRepo: (repo: Record<string, any>) => void;
    setSelectedLLMRepo: (repo: Record<string, any>) => void;
    setMessages: (messages: any[]) => void;
    setTracingMessages: (messages: any[]) => void;
    setHasChatTested: (value: boolean) => void;
    setBuilderLogState: (logs: any[]) => void;
    setProgressMessage: (message: string) => void;
    setStreamingMessage: (message: string) => void;
    setTracingNodeId: (ids: string[]) => void;
    setTracingBaseInfo: (info: any) => void;
    clearStreamLogs: () => void;
    fitViewAppliedRef?: React.MutableRefObject<boolean>;
    nodesInitRef: React.MutableRefObject<boolean>;
    edgesInitRef: React.MutableRefObject<boolean>;
    prevDataNodesRef: React.MutableRefObject<string>;
    prevDataEdgesRef: React.MutableRefObject<string>;
    templateAutoConnectDoneRef: React.MutableRefObject<boolean>;
}

interface UseGraphInitializationReturn {
    currentAgentId: string;
    isAgentChanged: boolean;
    resetAllState: () => void;
}

export const useGraphInitialization = ({
    dataId,
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
    fitViewAppliedRef,
    nodesInitRef,
    edgesInitRef,
    prevDataNodesRef,
    prevDataEdgesRef,
    templateAutoConnectDoneRef,
}: UseGraphInitializationProps): UseGraphInitializationReturn => {
    const prevAgentIdRef = useRef<string>('');
    const prevHasInitialDataRef = useRef<boolean | undefined>(undefined);
    const isAgentChangedRef = useRef<boolean>(false);

    const resetAllState = useCallback(() => {
        setNodes([]);
        setEdges([]);

        setSelectedPromptIdRepo({});
        setSelectedFewShotIdRepo({});
        setSelectedToolsRepo({});
        setSelectedLLMRepo({});

        nodesInitRef.current = true;
        edgesInitRef.current = true;
        prevDataNodesRef.current = '';
        prevDataEdgesRef.current = '';
        templateAutoConnectDoneRef.current = false;

        if (containerRef.current) {
            setContainerSizeState({ width: 100, height: 100 });
            setReactFlowInstance(null);
            scheduleContainerSizeUpdates(setContainerSize, { immediate: true, rafCount: 3 });
        }

        setMessages([]);
        setTracingMessages([]);
        setHasChatTested(false);
        setBuilderLogState([]);
        setProgressMessage('');
        setStreamingMessage('');
        setTracingNodeId([]);
        setTracingBaseInfo(null);
        clearStreamLogs();

        if (fitViewAppliedRef) {
            fitViewAppliedRef.current = false;
        }
    }, [
        setNodes,
        setEdges,
        setSelectedPromptIdRepo,
        setSelectedFewShotIdRepo,
        setSelectedToolsRepo,
        setSelectedLLMRepo,
        containerRef,
        setContainerSizeState,
        setReactFlowInstance,
        setContainerSize,
        setMessages,
        setTracingMessages,
        setHasChatTested,
        setBuilderLogState,
        setProgressMessage,
        setStreamingMessage,
        setTracingNodeId,
        setTracingBaseInfo,
        clearStreamLogs,
        fitViewAppliedRef,
        nodesInitRef,
        edgesInitRef,
        prevDataNodesRef,
        prevDataEdgesRef,
        templateAutoConnectDoneRef,
    ]);

    useEffect(() => {
        const currentAgentId = dataId || '';

        const agentIdChanged = prevAgentIdRef.current !== '' && prevAgentIdRef.current !== currentAgentId && currentAgentId !== '';

        const templateChanged =
            (prevHasInitialDataRef.current !== undefined && prevHasInitialDataRef.current !== hasInitialData) ||
            (hasInitialData && (!currentAgentId || currentAgentId === '') && prevAgentIdRef.current !== '');

        isAgentChangedRef.current = !!(agentIdChanged || templateChanged);

        if (agentIdChanged || templateChanged) {
            resetAllState();
        }

        prevAgentIdRef.current = currentAgentId;
        prevHasInitialDataRef.current = hasInitialData;
    }, [dataId, hasInitialData, resetAllState]);

    return {
        currentAgentId: prevAgentIdRef.current,
        isAgentChanged: isAgentChangedRef.current,
        resetAllState,
    };
};
