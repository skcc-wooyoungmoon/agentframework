import { useEffect, useRef } from 'react';
import type { CustomNode } from '@/components/agents/builder/types/Agents';

interface TracingMessage {
    nodeId?: string;
    node_name?: string;
    callback?: string;
    event?: string;
    status?: string;
    progress?: boolean;
    llm?: { content?: string };
    log?: { llm?: { content?: string } };
    updates?: Record<string, any>;
}

interface UseGraphTracingProps {
    nodes: CustomNode[];
    tracingMessages: TracingMessage[] | null;
    setNodes: (updater: (prev: CustomNode[]) => CustomNode[]) => void;
    hasData: boolean;
    hasDataNodes: boolean;
}

export const useGraphTracing = ({
    nodes,
    tracingMessages,
    setNodes,
    hasData,
    hasDataNodes,
}: UseGraphTracingProps): void => {
    const prevTracingMessagesLengthRef = useRef<number>(0);
    const nodesInitializedForChatRef = useRef<boolean>(false);
    const nodesInitializedOnMountRef = useRef<boolean>(false);

    useEffect(() => {
        if (!hasData || !hasDataNodes || nodes.length === 0) {
            return;
        }

        const currentLength = tracingMessages?.length || 0;
        const prevLength = prevTracingMessagesLengthRef.current;

        if (prevLength === 0 && currentLength > 0) {
            prevTracingMessagesLengthRef.current = currentLength;
            nodesInitializedForChatRef.current = false;

            setNodes((prev: CustomNode[]) => {
                const hasNodesToUpdate = prev.some((node: CustomNode) => {
                    const innerData = node.data?.innerData ?? {};
                    return (
                        innerData.isRun ||
                        innerData.isDone ||
                        innerData.isError ||
                        innerData.isRunning ||
                        innerData.isCompleted ||
                        innerData.hasError ||
                        (innerData.logData && innerData.logData.length > 0)
                    );
                });

                if (!hasNodesToUpdate) {
                    return prev;
                }

                return prev.map(node => {
                    const currentInnerData = node.data.innerData || {};
                    return {
                        ...node,
                        data: {
                            ...node.data,
                            innerData: {
                                ...currentInnerData,
                                isRun: false,
                                isDone: false,
                                isError: false,
                                isRunning: false,
                                isCompleted: false,
                                hasError: false,
                                logData: [],
                            },
                        },
                    };
                });
            });
        } else if (prevLength > 0 && currentLength === 0) {
            prevTracingMessagesLengthRef.current = currentLength;
            nodesInitializedForChatRef.current = true;

            setNodes((prev: CustomNode[]) => {
                let hasChanges = false;
                const updatedNodes = prev.map((node: CustomNode) => {
                    const currentInnerData = node.data.innerData || {};
                    if (currentInnerData.isRun && !currentInnerData.isDone && !currentInnerData.isError) {
                        hasChanges = true;
                        return {
                            ...node,
                            data: {
                                ...node.data,
                                innerData: {
                                    ...currentInnerData,
                                    isRun: true,
                                    isDone: true,
                                    isError: false,
                                    isRunning: false,
                                    isCompleted: true,
                                    hasError: false,
                                },
                            },
                        };
                    }
                    return node;
                });
                return hasChanges ? updatedNodes : prev;
            });
            return;
        } else if (currentLength > 0) {
            prevTracingMessagesLengthRef.current = currentLength;
        }

        if (tracingMessages && tracingMessages.length > 0 && nodes.length > 0) {
            const nodeStatusMap = new Map<string, { isRun: boolean; isDone: boolean; isError: boolean }>();

            for (let i = tracingMessages.length - 1; i >= 0; i--) {
                const msg = tracingMessages[i];
                const msgNodeId = msg.nodeId || msg.node_name || '';
                const msgCallback = msg.callback || msg.event || '';

                if (!msgNodeId || msgNodeId === 'unknown') continue;

                const matchedNode = nodes.find(node => {
                    const nodeId = node.id;
                    const nodeName = node.data?.name as string;
                    return (
                        nodeId === msgNodeId ||
                        nodeName === msgNodeId ||
                        nodeId.includes(msgNodeId) ||
                        msgNodeId.includes(nodeId) ||
                        (nodeName && (nodeName === msgNodeId || nodeName.includes(msgNodeId) || msgNodeId.includes(nodeName)))
                    );
                });

                if (!matchedNode) continue;

                const nodeId = matchedNode.id;

                if (nodeStatusMap.has(nodeId)) continue;

                if (msgCallback === 'on_chain_error' || msgCallback === 'chain_error' || msg.status === 'error') {
                    nodeStatusMap.set(nodeId, { isRun: true, isDone: false, isError: true });
                    continue;
                }

                if (msgCallback === 'on_chain_end' || msgCallback === 'chain_end') {
                    nodeStatusMap.set(nodeId, { isRun: true, isDone: true, isError: false });
                    continue;
                }

                const hasUpdates = msg.updates && Object.keys(msg.updates).length > 0;
                const isInputNode = matchedNode.type === 'input__basic';
                const isOutputNode =
                    matchedNode.type === 'output__chat' ||
                    matchedNode.type === 'output__keys' ||
                    matchedNode.type === 'output__formatter' ||
                    matchedNode.type === 'output__selector';

                if (msg.progress || msg.llm?.content || msg.log?.llm?.content || msgCallback === 'on_chain_start' || msgCallback === 'chain_start' || hasUpdates) {
                    if (isInputNode && hasUpdates) {
                        nodeStatusMap.set(nodeId, { isRun: true, isDone: false, isError: false });
                    } else if (isOutputNode && hasUpdates && msg.updates?.content) {
                        nodeStatusMap.set(nodeId, { isRun: true, isDone: false, isError: false });
                    } else {
                        nodeStatusMap.set(nodeId, { isRun: true, isDone: false, isError: false });
                    }
                    continue;
                }
            }

            if (nodeStatusMap.size > 0) {
                setNodes((prev: CustomNode[]) => {
                    let hasChanges = false;
                    const updatedNodes = prev.map((node: CustomNode) => {
                        const status = nodeStatusMap.get(node.id);
                        if (!status) {
                            return node;
                        }

                        const currentInnerData = node.data?.innerData ?? {};

                        if (currentInnerData.isRun === status.isRun && currentInnerData.isDone === status.isDone && currentInnerData.isError === status.isError) {
                            return node;
                        }

                        hasChanges = true;
                        return {
                            ...node,
                            data: {
                                ...node.data,
                                innerData: {
                                    ...currentInnerData,
                                    isRun: status.isRun,
                                    isDone: status.isDone,
                                    isError: status.isError,
                                    isRunning: status.isRun && !status.isDone && !status.isError,
                                    isCompleted: status.isDone,
                                    hasError: status.isError,
                                },
                            },
                        };
                    });

                    return hasChanges ? updatedNodes : prev;
                });
            }
        }

        if ((!tracingMessages || tracingMessages.length === 0) && !nodesInitializedOnMountRef.current) {
            setNodes((prev: CustomNode[]) => {
                const hasNodesToUpdate = prev.some((node: CustomNode) => {
                    const innerData = node.data?.innerData ?? {};
                    return innerData.isRun || innerData.isDone || innerData.isError || innerData.isRunning || innerData.isCompleted || innerData.hasError;
                });

                if (!hasNodesToUpdate) {
                    nodesInitializedOnMountRef.current = true;
                    return prev;
                }

                nodesInitializedOnMountRef.current = true;
                return prev.map((node: CustomNode) => {
                    const currentInnerData = node.data.innerData || {};
                    return {
                        ...node,
                        data: {
                            ...node.data,
                            innerData: {
                                ...currentInnerData,
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

            if (!tracingMessages || tracingMessages.length === 0) {
                return;
            }
        }

        if (tracingMessages && tracingMessages.length > 0) {
            nodesInitializedOnMountRef.current = false;
        }
        const wasEmpty = prevLength === 0;
        const isNewChat = tracingMessages && tracingMessages.length > 0 && (wasEmpty || !nodesInitializedForChatRef.current);

        if (isNewChat) {
            nodesInitializedForChatRef.current = true;
            prevTracingMessagesLengthRef.current = tracingMessages.length;

            setNodes((prev: CustomNode[]) => {
                const hasNodesToUpdate = prev.some((node: CustomNode) => {
                    const innerData = node.data?.innerData ?? {};
                    return (
                        innerData.isRun ||
                        innerData.isDone ||
                        innerData.isError ||
                        innerData.isRunning ||
                        innerData.isCompleted ||
                        innerData.hasError ||
                        (innerData.logData && innerData.logData.length > 0)
                    );
                });

                if (!hasNodesToUpdate) {
                    return prev;
                }

                return prev.map((node: CustomNode) => {
                    const currentInnerData = node.data.innerData || {};
                    return {
                        ...node,
                        data: {
                            ...node.data,
                            innerData: {
                                ...currentInnerData,
                                isRun: false,
                                isDone: false,
                                isError: false,
                                isRunning: false,
                                isCompleted: false,
                                hasError: false,
                                logData: [],
                            },
                        },
                    };
                });
            });
        } else if (tracingMessages && tracingMessages.length !== prevTracingMessagesLengthRef.current) {
            prevTracingMessagesLengthRef.current = tracingMessages.length;
        }
    }, [tracingMessages, setNodes, hasData, hasDataNodes, nodes.length]);
};
