import { edgesAtom, keyTableAtom, nodesAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom';
import { addMessageAtom, messagesAtom, progressMessageAtom, regenerateAtom, streamingMessageAtom, tracingMessagesAtom } from '@/components/agents/builder/atoms/messagesAtom.ts';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { ChatType, type QueryMessage } from '@/components/agents/builder/types/Agents';
import { type MessageFormat } from '@/components/agents/builder/types/messageFormat';
import { convertMessageFormatToMessage } from '@/components/agents/builder/utils/messageUtils';
import { streamAgentGraph } from '@/services/agent/builder/agentBuilder.services';
import { useAtom, useAtomValue, useSetAtom } from 'jotai';
import { useRef, useState } from 'react';
import { useNodeLogs } from './useNodeLogs';
import { useStreamLogs } from './useStreamLogs';

const extractMeaningfulText = (input: any): string | null => {
  const visited = new WeakSet<object>();

  const traverse = (value: any): string | null => {
    if (value === null || value === undefined) {
      return null;
    }

    if (typeof value === 'string') {
      const trimmed = value.trim();
      if (!trimmed) {
        return null;
      }

      const firstChar = trimmed[0];
      const lastChar = trimmed[trimmed.length - 1];
      const looksLikeJson = (firstChar === '{' && lastChar === '}') || (firstChar === '[' && lastChar === ']');

      if (looksLikeJson) {
        try {
          const parsed = JSON.parse(trimmed);
          const extracted = traverse(parsed);
          if (extracted) {
            return extracted;
          }
        } catch (error) {}
      }

      return trimmed;
    }

    if (typeof value === 'number' || typeof value === 'boolean') {
      return String(value);
    }

    if (Array.isArray(value)) {
      for (const item of value) {
        const result = traverse(item);
        if (result) {
          return result;
        }
      }
      return null;
    }

    if (typeof value === 'object') {
      if (visited.has(value as object)) {
        return null;
      }
      visited.add(value as object);

      const candidates = ['content', 'text', 'answer', 'result', 'message', 'output', 'value', 'completion', 'final_result'];

      for (const key of candidates) {
        if (Object.prototype.hasOwnProperty.call(value, key)) {
          const result = traverse((value as Record<string, unknown>)[key]);
          if (result) {
            return result;
          }
        }
      }

      for (const val of Object.values(value as Record<string, unknown>)) {
        const result = traverse(val);
        if (result) {
          return result;
        }
      }
    }

    return null;
  };

  return traverse(input);
};


const applyFormatString = (formatString?: string | null, tokenValues: Record<string, string> = {}, fallbackValue?: string): string => {
  if (!formatString || formatString.trim() === '') {
    return fallbackValue ?? '';
  }

  const tokenRegex = /\{\{\s*([^}]+)\s*\}\}/g;

  const hasTokens = tokenRegex.test(formatString);

  if (!hasTokens) {
    return formatString;
  }

  tokenRegex.lastIndex = 0; 
  let usedFallback = false;
  
  const processedTokens = new Set<string>();

  const formatted = formatString.replace(tokenRegex, (_match, token) => {
    const key = token.trim();
    
    if (processedTokens.has(key)) {
      return '';
    }
    
    const value = tokenValues[key];

    if (typeof value === 'string' && value.length > 0) {
      processedTokens.add(key);
      return value;
    }

    if (!usedFallback && typeof fallbackValue === 'string') {
      usedFallback = true;
      processedTokens.add(key);
      return fallbackValue;
    }

    return '';
  });
  
  const originalLines = formatString.split('\n');
  const cleanedFormatted = formatted
    .split('\n')
    .map((line, index) => {
      const originalLine = originalLines[index] || '';
      const originalLineWithoutTokens = originalLine.replace(/\{\{[^}]+\}\}/g, '').trim();
      
      if (originalLineWithoutTokens === '') {
        if (line.trim() !== '') {
          return line.trim();
        }
        return '';
      }
      
      return line.trim();
    })
    .filter((line, index, array) => {
      if (line === '') {
        if (index === 0 || index === array.length - 1) {
          return false;
        }
        return array[index - 1] !== '';
      }
      return true;
    })
    .join('\n');
  
  if (cleanedFormatted.trim().length === 0 && fallbackValue) {
    return fallbackValue;
  }
  
  if (cleanedFormatted.trim().length === 0 && formatted.trim().length > 0) {
    return formatted.trim();
  }

  const isIncompletePattern = 
    cleanedFormatted.trim() === 'Plan: Execution:' ||
    cleanedFormatted.trim() === 'Plan:\nExecution:' ||
    cleanedFormatted.trim() === 'Plan:\n답변하기\nExecution:' ||
    cleanedFormatted.trim() === 'Plan: 답변하기 Execution:' ||
    cleanedFormatted.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) ||
    cleanedFormatted.match(/^Plan:\s*답변하기\s*Execution:\s*$/m) ||
    (cleanedFormatted.includes('Plan:') && cleanedFormatted.includes('Execution:') && cleanedFormatted.length < 100);

  if (isIncompletePattern) {
    if (fallbackValue && typeof fallbackValue === 'string' && fallbackValue.length > 20) {
      return fallbackValue;
    }
    return '';
  }

  return cleanedFormatted;
};

export const useStreamingChat = (agentId: string) => {
  const { agent } = useGraphActions();
  const { generateBuilderLogs, addStreamLog, clearStreamLogs, streamLogsRef } = useStreamLogs();
  const { updateNodeLogs } = useNodeLogs();

  const [messages, setMessages] = useAtom(messagesAtom);
  const [, addMessage] = useAtom(addMessageAtom);
  const [, setProgressMessage] = useAtom(progressMessageAtom);
  const [streamingMessage, setStreamingMessage] = useAtom(streamingMessageAtom);
  const [, setHasChatTested] = useAtom(hasChatTestedAtom);
  const [currentNodes] = useAtom(nodesAtom);
  const [currentEdges] = useAtom(edgesAtom);
  const setNodes = useSetAtom(nodesAtom);
  const tracingMessages = useAtomValue(tracingMessagesAtom);
  const setTracingMessages = useSetAtom(tracingMessagesAtom);
  const keyTableList = useAtomValue(keyTableAtom);
  const [regenerateState, setRegenerateState] = useAtom(regenerateAtom);
  const [isChatLoading, setIsChatLoading] = useState<boolean>(false);

  const queryResponseRef = useRef(false);
  const lastCallTimeRef = useRef<number>(0);
  const conversationTurnRef = useRef<number>(0);
  const nodeStatusRef = useRef<Record<string, 'running' | 'completed' | 'error'>>({});
  const nodeOrderRef = useRef<string[]>([]);
  const checkIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);
  const pendingNodeUpdatesRef = useRef<Map<string, { isRun: boolean; isRunning: boolean; isCompleted: boolean; isError: boolean }>>(new Map());
  const nodeUpdateTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const pendingTracingUpdatesRef = useRef<
    Array<{
      nodeId: string;
      nodeType: string;
      callback: string;
      progress?: string;
      llm: any;
      tool_calls: any;
      tool_result: any;
      updates: any;
      final_result: any;
      status: string;
      log: any;
      turn: number;
    }>
  >([]);
  const tracingUpdateTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const tracingUpdateCountRef = useRef<number>(0);
  const tracingUpdateResetInterval = useRef<NodeJS.Timeout | null>(null);

  const maxTracingUpdatesPerSecond = 10;
  const maxBatchSize = 50;

  const commitAssistantMessage = (messageFormat: MessageFormat, options?: { targetIndex?: number | null; resetRegenerate?: boolean }) => {
    const converted = convertMessageFormatToMessage(messageFormat);
    const targetIndex = options?.targetIndex ?? null;
    const shouldReset = options?.resetRegenerate ?? true;

    if (targetIndex !== null && targetIndex >= 0) {
      setMessages(prev => {
        const updated = [...prev];
        const insertionIndex = Math.min(targetIndex, updated.length);
        updated.splice(insertionIndex, 0, { ...converted, regen: false });
        return updated;
      });
      if (shouldReset) {
        setRegenerateState({ trigger: false, query: '', answerIndex: undefined, history: undefined });
      }
    } else {
      addMessage(converted);
      if (shouldReset && regenerateState.answerIndex !== undefined) {
        setRegenerateState({ trigger: false, query: '', answerIndex: undefined, history: undefined });
      }
    }
  };

  const resolveNodeStatus = (data: any): 'running' | 'completed' | 'error' | null => {
    if (!data) {
      return null;
    }

    const statusText = typeof data.status === 'string' ? data.status.toLowerCase() : '';
    const eventText = typeof data.event === 'string' ? data.event.toLowerCase() : '';
    const progressText = typeof data.progress === 'string' ? data.progress.toLowerCase() : '';

    const isError = ['error', 'failed', 'failure'].some(keyword => statusText.includes(keyword)) || ['error', 'failed'].includes(eventText);
    if (isError) {
      return 'error';
    }

    const isCompleted =
      ['completed', 'success', 'finished', 'done'].some(keyword => statusText.includes(keyword)) || ['end', 'finish', 'stop'].includes(eventText) || progressText.includes('완료');
    if (isCompleted) {
      return 'completed';
    }

    const isRunning = ['running', 'in_progress', 'processing', 'start', 'started'].some(keyword => statusText.includes(keyword)) || ['start', 'progress'].includes(eventText);
    if (isRunning) {
      return 'running';
    }

    return null;
  };

  const formatAnswer = (text: string): string => {
    if (!text) return text;
    let t = text;
    t = t.replace(/\r\n/g, '\n');
    t = t.replace(/\t/g, ' ');
    t = t.replace(/[ \u00A0]+/g, ' ');
    t = t.replace(/\n{3,}/g, '\n\n');
    t = t.replace(/[ ]+\n/g, '\n').replace(/\n[ ]+/g, '\n');
    t = t.trim();

    const pythonContentMatch = t.match(/content['"]?\s*[:=]\s*['"]([^'"]+)['"]/);
    if (pythonContentMatch && pythonContentMatch[1]) {
      return pythonContentMatch[1].trim();
    }

    if (t.startsWith('{') || t.startsWith('[')) {
      try {
        const normalizedJson = t
          .replace(/([,{]\s*)'([^']+?)'\s*:/g, '$1"$2":')
          .replace(/:\s*'([^']*?)'/g, ':"$1"');

        const parsed = JSON.parse(normalizedJson);
        const extracted = extractMeaningfulText(parsed);
        if (extracted) {
          return extracted.trim();
        }
      } catch (error) {
        if (process.env.NODE_ENV === 'development') {
        }
      }
    }

    return t;
  };

  const handleChatTest = async (userInput?: string, isRegenerate: boolean = false) => {
    if (!agent) {
      return;
    }
    if (isChatLoading) {
      return;
    }

    if (!isRegenerate) {
      const now = Date.now();
      if (lastCallTimeRef.current && now - lastCallTimeRef.current < 1000) {
        return;
      }
      lastCallTimeRef.current = now;
    }

    setIsChatLoading(true);
    setHasChatTested(true);

    const targetAnswerIndex = isRegenerate ? (regenerateState.answerIndex ?? null) : null;

    let currentTurn = conversationTurnRef.current;

    if (tracingUpdateTimeoutRef.current) {
      clearTimeout(tracingUpdateTimeoutRef.current);
      tracingUpdateTimeoutRef.current = null;
    }
    pendingTracingUpdatesRef.current = [];
    setTracingMessages([]);

    clearStreamLogs();

    nodeStatusRef.current = {};
    nodeOrderRef.current = [];
    setNodes(prev =>
      prev.map(node => {
        const innerData = node.data?.innerData ?? {};
        return {
          ...node,
          data: {
            ...node.data,
            innerData: {
              ...innerData,
              logData: [],
              isRun: false,
              isDone: false,
              isRunning: false,
              isCompleted: false,
              isError: false,
              hasError: false,
            },
          },
        };
      })
    );

    setStreamingMessage('');
    setProgressMessage('');

    const startTime = Date.now();

    const directUserInput = userInput || '';
    const currentUserInput = directUserInput || (messages.length > 0 ? messages[messages.length - 1]?.content : '');

    if (!isRegenerate) {
      const lastMessage = messages[messages.length - 1];
      const secondLastMessage = messages[messages.length - 2];
      if (lastMessage && lastMessage.type === ChatType.HUMAN && lastMessage.content === currentUserInput && secondLastMessage && secondLastMessage.type === ChatType.HUMAN) {
        setIsChatLoading(false);
        return;
      }
    } else {
    }

    const nextTurn = currentTurn + 1;
    conversationTurnRef.current = nextTurn;
    currentTurn = nextTurn;

    if (!currentUserInput || currentUserInput.trim() === '') {
      setIsChatLoading(false);
      return;
    }

    if (!isRegenerate) {
      const userMessage: MessageFormat = {
        id: '',
        time: new Date().toLocaleString(),
        content: currentUserInput || '사용자 입력 없음',
        type: ChatType.HUMAN,
        regen: false,
        elapsedTime: 0,
      };

      const convertedUserMessage = convertMessageFormatToMessage(userMessage);
      addMessage(convertedUserMessage);
    }

    if (!currentUserInput) {
      setIsChatLoading(false);
      return;
    }

    const conversationMessages: QueryMessage[] = [
      {
        content: currentUserInput,
        type: 'human',
      },
    ];

    const outputNode = currentNodes.find((node: any) => 
      node && 
      (node.type === 'output__chat' || node.type === 'output__keys') &&
      node.data &&
      typeof node.data === 'object'
    );
    const outputFormatString = (outputNode && outputNode.type === 'output__chat' && outputNode.data)
      ? ((outputNode.data as any)?.format_string || '')
      : '';
    const formatTokenValues: Record<string, string> = {};

    const inputNode = currentNodes.find(node => node.type === 'input__basic');
    if (inputNode && currentUserInput) {
      const inputKeys = Array.isArray((inputNode.data as any)?.input_keys) ? (inputNode.data as any).input_keys : [];
      inputKeys.forEach((inputKey: any) => {
        if (inputKey && inputKey.keytable_id && inputKey.name === 'query') {
          formatTokenValues[inputKey.keytable_id] = currentUserInput;
          formatTokenValues[`${inputKey.name}__global`] = currentUserInput;
        }
      });
    }

    let finalResultContent: string | null = null;
    let outputNodeContent: string | null = null;

    try {
      const currentAgentId = agent.id || agentId;

      if (!currentAgentId || currentAgentId.trim() === '') {
        const errorMessage = '❌ 에이전트 ID가 없습니다. 에이전트를 다시 생성해주세요.';
        const errorChatMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: errorMessage,
          type: ChatType.AI,
          regen: false,
          elapsedTime: 0,
        };
        commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });
        setIsChatLoading(false);
        return;
      }

      if (!currentNodes || currentNodes.length === 0) {
        const errorMessage = '❌ 에이전트에 노드가 없습니다. 노드를 추가한 후 다시 시도해주세요.';
        const errorChatMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: errorMessage,
          type: ChatType.AI,
          regen: false,
          elapsedTime: 0,
        };
        commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });
        setIsChatLoading(false);
        return;
      }

      const hasInputNode = currentNodes.some((node: any) => node.type === 'input__basic');
      const hasOutputNode = currentNodes.some((node: any) => node.type === 'output__chat' || node.type === 'output__keys');

      if (!hasInputNode) {
        const errorMessage = '❌ 입력 노드가 없습니다. 입력 노드를 추가한 후 다시 시도해주세요.';
        const errorChatMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: errorMessage,
          type: ChatType.AI,
          regen: false,
          elapsedTime: 0,
        };
        commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });
        setIsChatLoading(false);
        return;
      }

      if (!hasOutputNode) {
        const errorMessage = '❌ 출력 노드가 없습니다. 출력 노드를 추가한 후 다시 시도해주세요.';
        const errorChatMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: errorMessage,
          type: ChatType.AI,
          regen: false,
          elapsedTime: 0,
        };
        commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });
        setIsChatLoading(false);
        return;
      }

      const finalMessages = conversationMessages.map((msg) => ({
        content: msg.content,
        type: msg.type,
      }));
      
      const apiRequest = {
        graph_id: currentAgentId,
        input_data: {
          messages: finalMessages,
        },
      };
      
      const actualInputNode = currentNodes.find(node => node.type === 'input__basic');
      const inputNodeId: string = actualInputNode?.id || (actualInputNode?.data?.name as string) || 'input__basic_1';
      const inputNodeName: string = (actualInputNode?.data?.name as string) || 'input__basic_1';
      
      setTracingMessages(prev => [
        ...prev,
        {
          nodeId: inputNodeId,
          node_name: inputNodeName,
          nodeType: 'input__basic',
          callback: 'user_input',
          progress: '',
          llm: null,
          tool_calls: null,
          tool_result: null,
          updates: { user_input: currentUserInput },
          final_result: null,
          status: 'input',
          log: { user_input: currentUserInput },
          turn: currentTurn,
        },
      ]);

      let streamBuffer = '';
      let responseReceived = false;
      let messageTemp: string[] = [];
      let accumulatedResponse = '';
      let currentEvent: string | null = null;
      let shouldTerminate = false;
      let actualError: string | null = null;

      const processLine = (line: string) => {
        const trimmedLine = line?.trim();
        if (!trimmedLine) return;

        if (trimmedLine.startsWith('event:')) {
          currentEvent = trimmedLine.slice(6).trim();
          return;
        }

        if (trimmedLine.startsWith('data: ')) {
          const data = trimmedLine.slice(6);
          if (data.trim() === '[DONE]') {
            shouldTerminate = true;
            return;
          }

          try {
            let cleanData = data.trim();
            if (cleanData.startsWith('data: ')) {
              cleanData = cleanData.substring(6);
            }
            if (!cleanData || cleanData === '' || cleanData === '[DONE]') return;

            const parsedData = JSON.parse(cleanData);
            if (!parsedData.event && currentEvent) {
              parsedData.event = currentEvent;
            }

            if (parsedData.final_result !== undefined) {
              const finalValue = parsedData.final_result;
              if (typeof finalValue === 'string') {
                const filteredFinal = finalValue.toString();
                
                if (accumulatedResponse && filteredFinal.length > 0) {
                  if (!accumulatedResponse.includes(filteredFinal) && 
                      !filteredFinal.includes(accumulatedResponse) &&
                      filteredFinal.length >= accumulatedResponse.length * 0.5) {
                    accumulatedResponse = filteredFinal;
                  } else {
                    if (!accumulatedResponse.endsWith(filteredFinal)) {
                      accumulatedResponse += filteredFinal;
                    }
                  }
                } else {
                  accumulatedResponse = filteredFinal;
                }
                
                responseReceived = true;
                               
                if (parsedData.node_name && filteredFinal) {
                  let actualNodeId = parsedData.node_name;
                  const foundNode = currentNodes.find(node => {
                    const nodeName = node.data?.name as string;
                    const nodeId = node.id;
                    return nodeId === parsedData.node_name || 
                           nodeName === parsedData.node_name ||
                           nodeId.includes(parsedData.node_name) ||
                           parsedData.node_name.includes(nodeId) ||
                           (nodeName && (nodeName === parsedData.node_name || nodeName.includes(parsedData.node_name) || parsedData.node_name.includes(nodeName)));
                  });
                  if (foundNode) {
                    actualNodeId = foundNode.id;
                  }
                  
                  const newTracingMessage = {
                    nodeId: actualNodeId,
                    node_name: parsedData.node_name,
                    nodeType: parsedData.node_type || '',
                    callback: parsedData.event || currentEvent || '',
                    final_result: filteredFinal,
                    updates: parsedData.updates,
                    log: parsedData,
                    turn: currentTurn,
                  };
                  
         // 기존 final_result를 업데이트하거나 새로 추가
                  setTracingMessages(prev => {
                    const existingIndex = prev.findIndex(
                      msg => msg.nodeId === newTracingMessage.nodeId && 
                             msg.turn === currentTurn &&
                             msg.final_result !== undefined
                    );
                    
                    if (existingIndex >= 0) {
                      // 기존 항목을 더 긴 값으로 교체
                      const existing = prev[existingIndex];
                                           
                      if (!existing.final_result || filteredFinal.length > (existing.final_result as string).length) {
                        const updated = [...prev];
                        updated[existingIndex] = newTracingMessage;
                        return updated;
                      }
                      return prev;
                    } else {
                      // 새로 추가
                      return [...prev, newTracingMessage];
                    }
                  });
                }
              }
            }

            if (parsedData.progress) {
              setProgressMessage(parsedData.progress);
              if (parsedData.node_name && parsedData.node_name !== 'unknown') {
                let actualNodeId = parsedData.node_name;
                const foundNode = currentNodes.find(node => {
                  const nodeName = node.data?.name as string;
                  const nodeId = node.id;
                  return nodeId === parsedData.node_name || 
                         nodeName === parsedData.node_name ||
                         nodeId.includes(parsedData.node_name) ||
                         parsedData.node_name.includes(nodeId) ||
                         (nodeName && (nodeName === parsedData.node_name || nodeName.includes(parsedData.node_name) || parsedData.node_name.includes(nodeName)));
                });
                if (foundNode) {
                  actualNodeId = foundNode.id;
                }
                
                const newTracingMessage = {
                  nodeId: actualNodeId,
                  node_name: parsedData.node_name,
                  nodeType: parsedData.node_type || '',
                  callback: parsedData.event || currentEvent || '',
                  progress: parsedData.progress,
                  updates: parsedData.updates,
                  log: parsedData,
                  turn: currentTurn,
                };
                const isDuplicate = tracingMessages.some(
                  msg => (msg.nodeId === newTracingMessage.nodeId || msg.node_name === newTracingMessage.node_name) && 
                         msg.progress === newTracingMessage.progress &&
                         msg.turn === currentTurn
                );
                if (!isDuplicate) {
                  setTracingMessages(prev => [...prev, newTracingMessage]);
                }
              }
            }

            if (parsedData.event === 'end' || parsedData.data === '[DONE]') {
              shouldTerminate = true;
            }

            if (parsedData.error || parsedData.exception || parsedData.traceback) {
              actualError = parsedData.error || parsedData.exception || '알 수 없는 오류가 발생했습니다.';
            }
          } catch {
            }
        }
      };

      const onChunk = (chunk: string) => {
        streamBuffer += chunk;

        const lines = streamBuffer.split('\n');
        streamBuffer = lines.pop() || '';

        for (const line of lines) {
          processLine(line);
        }
      };

      let streamResponse;
      try {
        streamResponse = await streamAgentGraph(apiRequest, onChunk);

        if (streamBuffer) {
          processLine(streamBuffer);
        }

        if (accumulatedResponse) {
          setStreamingMessage(accumulatedResponse);
        }

        if (typeof streamResponse !== 'string') {
          throw new Error('Unexpected response type from streamAgentGraph');
        }
      } catch (apiError: any) {
        let errorMessage = '';
        const errorParts: string[] = [];

        if (apiError?.isNetworkError || apiError?.name === 'TypeError') {
          const networkErrorMsg = apiError?.message || '네트워크 오류가 발생했습니다.';
          if (networkErrorMsg.includes('Failed to fetch') || networkErrorMsg.includes('NetworkError') || networkErrorMsg.includes('network') || networkErrorMsg.includes('fetch')) {
            errorMessage = '❌ 네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.';
          } else {
            errorMessage = `❌ ${networkErrorMsg}`;
          }
        } else {
          if (apiError?.status) {
            errorParts.push(`Status: ${apiError.status}`);
          }

          if (apiError?.statusText) {
            errorParts.push(`Status Text: ${apiError.statusText}`);
          }

          const errorDetail = apiError?.response?.data?.error?.message || apiError?.response?.data?.message || apiError?.response?.data?.error || apiError?.message || '';

          if (errorDetail && !errorDetail.includes('HTTP error!')) {
            errorParts.push(`Error: ${typeof errorDetail === 'string' ? errorDetail : JSON.stringify(errorDetail)}`);
          }

          if (apiError?.response?.data?.error?.details) {
            const details = apiError.response.data.error.details;
            errorParts.push(`Details: ${typeof details === 'string' ? details : JSON.stringify(details)}`);
          }

          if (apiError?.isStreamError) {
            if (apiError?.isIncompleteChunkedEncoding) {
              errorParts.push(`스트림 연결 오류: 스트림이 중간에 끊어졌습니다. 서버나 프록시의 타임아웃(약 30초)으로 인해 발생할 수 있습니다.`);
              if (apiError?.originalError?.message) {
                errorParts.push(`원본 오류: ${apiError.originalError.message}`);
              }
            } else {
              const streamErrorMsg = apiError?.message || apiError?.originalError?.message || '스트림 읽기 중 오류가 발생했습니다.';
              errorParts.push(`스트림 오류: ${streamErrorMsg}`);
              if (apiError?.originalError && apiError.originalError?.message && apiError.originalError.message !== streamErrorMsg) {
                errorParts.push(`원본 오류: ${apiError.originalError.message}`);
              }
            }
          }

          if (errorParts.length > 0) {
            errorMessage = `❌ ${errorParts.join('\n\n')}`;
          } else if (apiError?.message && !apiError.message.includes('HTTP error!')) {
            errorMessage = `❌ ${apiError.message}`;
          } else {
            if (responseReceived || accumulatedResponse.length > 0) {
              setIsChatLoading(false);
              queryResponseRef.current = true;
              return;
            }
            errorMessage = `❌ 알 수 없는 오류가 발생했습니다.`;
          }
        }

        if (errorMessage) {
          const errorChatMessage: MessageFormat = {
            id: '',
            time: new Date().toLocaleString(),
            content: errorMessage,
            type: ChatType.AI,
            regen: false,
            elapsedTime: Date.now() - startTime,
          };
          commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });
        }

        setIsChatLoading(false);
        queryResponseRef.current = true;
        return;
      }

      const lines = streamResponse.split('\n');
      currentEvent = null;
      shouldTerminate = false;

      try {
        for (const line of lines) {
          const trimmedLine = line?.trim();

          if (!trimmedLine) {
            continue;
          }

          if (trimmedLine.startsWith('event:')) {
            currentEvent = trimmedLine.slice(6).trim();
            continue;
          }

          if (trimmedLine.startsWith('data: ')) {
            const data = trimmedLine.slice(6);
            if (data.trim() === '[DONE]') {
              shouldTerminate = true;
              break;
            }

            try {
              let cleanData = data.trim();

              if (cleanData.startsWith('data: ')) {
                cleanData = cleanData.substring(6);
              }

              if (!cleanData || cleanData === '' || cleanData === '[DONE]') {
                continue;
              }

              const parsedData = JSON.parse(cleanData);

              if (!parsedData.event && currentEvent) {
                parsedData.event = currentEvent;
              }

              addStreamLog({ ...parsedData, turn: currentTurn });

              const event = parsedData.event || currentEvent || '';
              const isChainEvent =
                event === 'on_chain_start' || event === 'on_chain_end' || event === 'on_chain_error' || event === 'chain_start' || event === 'chain_end' || event === 'chain_error';

              const hasProgress = parsedData.progress && parsedData.progress.trim();
              const hasUpdates = parsedData.updates && Object.keys(parsedData.updates).length > 0;
              const hasNodeName = parsedData.node_name && parsedData.node_name !== 'unknown';
              const hasLlmContent = parsedData.llm?.content || parsedData.log?.llm?.content;

              const shouldAddToTracing =
                isChainEvent ||
                parsedData.tool_calls ||
                parsedData.tool_result ||
                parsedData.updates?.messages ||
                parsedData.final_result ||
                (hasProgress && hasNodeName) ||
                (hasUpdates && hasNodeName) ||
                (hasLlmContent && hasNodeName && !hasUpdates);

              if (shouldAddToTracing) {
                const llmContent = parsedData.llm?.content || parsedData.log?.llm?.content;
                
                let actualNodeId = parsedData.node_name || 'unknown';
                if (parsedData.node_name && parsedData.node_name !== 'unknown') {
                  const foundNode = currentNodes.find(node => {
                    const nodeName = node.data?.name as string;
                    const nodeId = node.id;
                    return nodeId === parsedData.node_name || 
                           nodeName === parsedData.node_name ||
                           nodeId.includes(parsedData.node_name) ||
                           parsedData.node_name.includes(nodeId) ||
                           (nodeName && (nodeName === parsedData.node_name || nodeName.includes(parsedData.node_name) || parsedData.node_name.includes(nodeName)));
                  });
                  if (foundNode) {
                    actualNodeId = foundNode.id;
                  }
                }
                
                const newTracingMessage = {
                  nodeId: actualNodeId,
                  node_name: parsedData.node_name,
                  nodeType: parsedData.node_type || '',
                  callback: event,
                  progress: parsedData.progress,
                  llm: llmContent ? { content: llmContent } : null,
                  tool_calls: parsedData.tool_calls,
                  tool_result: parsedData.tool_result,
                  updates: parsedData.updates,
                  final_result: parsedData.final_result,
                  status: parsedData.status,
                  log: parsedData,
                  turn: currentTurn,
                };

                const isCriticalEvent = isChainEvent || (hasLlmContent && hasNodeName) || (hasUpdates && hasNodeName);
                
                if (isCriticalEvent) {
                  if ((hasLlmContent || hasUpdates) && hasNodeName && actualNodeId !== 'unknown') {
                    setNodes(prev => {
                      const nodeIndex = prev.findIndex(n => n.id === actualNodeId);
                      if (nodeIndex === -1) {
                        return prev;
                      }
                      
                      const node = prev[nodeIndex];
                      const currentInnerData = node.data?.innerData ?? {};
                      
                      const hasFinalResult = parsedData.final_result !== undefined;
                      const isDone = hasFinalResult && !hasLlmContent;
                      
                      if (currentInnerData.isRun === true && 
                          currentInnerData.isDone === isDone && 
                          !currentInnerData.isError) {
                        return prev;
                      }
                      
                      const updatedNodes = [...prev];
                      updatedNodes[nodeIndex] = {
                        ...node,
                        data: {
                          ...node.data,
                          innerData: {
                            ...currentInnerData,
                            isRun: true,
                            isDone: isDone,
                            isError: false,
                            isRunning: !isDone,
                            isCompleted: isDone,
                            hasError: false,
                          },
                        },
                      };
                      
                      return updatedNodes;
                    });
                  }
                  
                  setTracingMessages(prev => {
                    if (hasLlmContent && hasNodeName) {
                      const filtered = prev.filter(
                        existing => 
                          !(existing.nodeId === newTracingMessage.nodeId &&
                            (existing.turn ?? currentTurn) === currentTurn &&
                            (existing.llm?.content || existing.log?.llm?.content))
                      );
                      return [...filtered, newTracingMessage];
                    }
                    
                    if (hasUpdates && hasNodeName) {
                      const filtered = prev.filter(
                        existing => 
                          !(existing.nodeId === newTracingMessage.nodeId &&
                            (existing.turn ?? currentTurn) === currentTurn &&
                            existing.updates &&
                            Object.keys(existing.updates).length > 0)
                      );
                      return [...filtered, newTracingMessage];
                    }
                    
                    const isDuplicate = prev.some(
                      existing => 
                        existing.nodeId === newTracingMessage.nodeId &&
                        (existing.turn ?? currentTurn) === currentTurn &&
                        existing.callback === event
                    );
                    if (isDuplicate) {
                      return prev;
                    }
                    return [...prev, newTracingMessage];
                  });
                } else {
                  const isPendingDuplicate = pendingTracingUpdatesRef.current.some(
                    pending =>
                      pending.nodeId === newTracingMessage.nodeId &&
                      (pending.turn ?? currentTurn) === currentTurn &&
                      pending.callback === event &&
                      (isChainEvent || (pending.updates === parsedData.updates && pending.tool_calls === parsedData.tool_calls))
                  );

                  if (pendingTracingUpdatesRef.current.length >= maxBatchSize) {
                    pendingTracingUpdatesRef.current.shift();
                  }

                  if (!isPendingDuplicate) {
                    pendingTracingUpdatesRef.current.push(newTracingMessage);
                  }

                  if (tracingUpdateTimeoutRef.current) {
                    clearTimeout(tracingUpdateTimeoutRef.current);
                  }
                  tracingUpdateTimeoutRef.current = setTimeout(() => {
                  try {
                    if (tracingUpdateCountRef.current >= maxTracingUpdatesPerSecond) {
                      const updates = pendingTracingUpdatesRef.current.slice(0, Math.floor(maxBatchSize / 2));
                      pendingTracingUpdatesRef.current = pendingTracingUpdatesRef.current.slice(Math.floor(maxBatchSize / 2));

                      if (updates.length === 0) {
                        return;
                      }

                      setTracingMessages(prev => {
                        try {
                          const filteredUpdates = updates.filter(update => {
                            const isDuplicate = prev.some(existing => {
                              if (existing.nodeId !== update.nodeId || (existing.turn ?? currentTurn) !== currentTurn || existing.callback !== update.callback) {
                                return false;
                              }

                              const isChainEventForUpdate =
                                update.callback === 'on_chain_start' ||
                                update.callback === 'on_chain_end' ||
                                update.callback === 'on_chain_error' ||
                                update.callback === 'chain_start' ||
                                update.callback === 'chain_end' ||
                                update.callback === 'chain_error';

                              if (isChainEventForUpdate) {
                                return true;
                              }

                              if (existing.updates === update.updates && existing.tool_calls === update.tool_calls) {
                                return true;
                              }

                              if (!existing.updates && !update.updates && !existing.tool_calls && !update.tool_calls) {
                                return true;
                              }

                              return false;
                            });
                            return !isDuplicate;
                          });

                          if (filteredUpdates.length === 0) {
                            return prev;
                          }

                          const maxMessages = 1000;
                          const newMessages = [...prev, ...filteredUpdates];
                          if (newMessages.length > maxMessages) {
                            return newMessages.slice(-maxMessages);
                          }

                          return newMessages;
                        } catch (error) {
                          console.error('tracingMessages 업데이트 중 에러:', error);
                          return prev;
                        }
                      });

                      tracingUpdateCountRef.current++;
                    } else {
                      const updates = pendingTracingUpdatesRef.current;
                      if (updates.length === 0) {
                        return;
                      }

                      setTracingMessages(prev => {
                        try {
                            const filteredUpdates = updates.filter(update => {
                            const isDuplicate = prev.some(existing => {
                              if (existing.nodeId !== update.nodeId || (existing.turn ?? currentTurn) !== currentTurn || existing.callback !== update.callback) {
                                return false;
                              }

                              const isChainEventForUpdate =
                                update.callback === 'on_chain_start' ||
                                update.callback === 'on_chain_end' ||
                                update.callback === 'on_chain_error' ||
                                update.callback === 'chain_start' ||
                                update.callback === 'chain_end' ||
                                update.callback === 'chain_error';

                              if (isChainEventForUpdate) {
                                return true;
                              }

                              if (existing.updates === update.updates && existing.tool_calls === update.tool_calls) {
                                return true;
                              }

                              if (!existing.updates && !update.updates && !existing.tool_calls && !update.tool_calls) {
                                return true;
                              }

                              return false;
                            });
                            return !isDuplicate;
                          });

                          if (filteredUpdates.length === 0) {
                            return prev;
                          }

                          const maxMessages = 1000;
                          const newMessages = [...prev, ...filteredUpdates];
                          if (newMessages.length > maxMessages) {
                            return newMessages.slice(-maxMessages);
                          }

                          return newMessages;
                        } catch (error) {
                          console.error('tracingMessages 업데이트 중 에러:', error);
                          return prev;
                        }
                      });

                      pendingTracingUpdatesRef.current = [];
                      tracingUpdateCountRef.current++;
                    }

                    if (tracingUpdateResetInterval.current === null) {
                      tracingUpdateResetInterval.current = setInterval(() => {
                        tracingUpdateCountRef.current = 0;
                      }, 1000);
                    }
                  } catch (error) {
                    console.error('tracingMessages 배치 업데이트 중 에러:', error);
                      pendingTracingUpdatesRef.current = [];
                    }
                  }, 10);
                  }
                }

              if (parsedData.progress) {
                setProgressMessage(parsedData.progress);
                
                if (parsedData.node_name && parsedData.node_name !== 'unknown') {
                  let actualNodeId = parsedData.node_name;
                  const foundNode = currentNodes.find(node => {
                    const nodeName = node.data?.name as string;
                    const nodeId = node.id;
                    return nodeId === parsedData.node_name || 
                           nodeName === parsedData.node_name ||
                           nodeId.includes(parsedData.node_name) ||
                           parsedData.node_name.includes(nodeId) ||
                           (nodeName && (nodeName === parsedData.node_name || nodeName.includes(parsedData.node_name) || parsedData.node_name.includes(nodeName)));
                  });
                  if (foundNode) {
                    actualNodeId = foundNode.id;
                  }
                  
                  if (actualNodeId !== 'unknown') {
                    setNodes(prev => {
                      const nodeIndex = prev.findIndex(n => n.id === actualNodeId);
                      if (nodeIndex === -1) {
                        return prev;
                      }
                      
                      const node = prev[nodeIndex];
                      const currentInnerData = node.data?.innerData ?? {};
                      
                      if (currentInnerData.isRun && !currentInnerData.isDone && !currentInnerData.isError) {
                        return prev;
                      }
                      
                      const updatedNodes = [...prev];
                      updatedNodes[nodeIndex] = {
                        ...node,
                        data: {
                          ...node.data,
                          innerData: {
                            ...currentInnerData,
                            isRun: true,
                            isDone: false,
                            isError: false,
                            isRunning: true,
                            isCompleted: false,
                            hasError: false,
                          },
                        },
                      };
                      
                      return updatedNodes;
                    });
                  }
                }
              }

              if (parsedData.node_name && parsedData.node_type) {
                const nodeDisplayName = parsedData.node_type.replace('__', ' ').replace('_', ' ');
                const hasUpdates = parsedData.updates !== undefined && parsedData.updates !== null && Object.keys(parsedData.updates).length > 0;

                const statusHint = resolveNodeStatus(parsedData);

                let matchedNodeId: string | null = null;
                let targetStatus: 'running' | 'completed' | 'error' | null = null;
                let scheduledCompletionId: string | null = null;

                let localMatched: string | null = null;
                currentNodes.forEach(node => {
                  const isMatchById = node.id === parsedData.node_name || node.id === parsedData.node_id;
                  const isMatchByName = node.data?.name === parsedData.node_name;

                  if (isMatchById || isMatchByName) {
                    localMatched = node.id;
                  }
                });

                if (!localMatched) {
                } else {
                  matchedNodeId = localMatched;

                  if (!nodeOrderRef.current.includes(localMatched)) {
                    nodeOrderRef.current.push(localMatched);
                  }

                  const nextStatuses = { ...nodeStatusRef.current };

                  const statusForNode: 'running' | 'completed' | 'error' = (() => {
                    if (statusHint) {
                      return statusHint;
                    }

                    if (parsedData.status === 'error') {
                      return 'error';
                    }

                    if (hasUpdates && (parsedData.final_result !== undefined || parsedData.tool_result !== undefined)) {
                      return 'completed';
                    }

                    if (parsedData.progress && !parsedData.progress.includes('완료')) {
                      return 'running';
                    }

                    return nextStatuses[localMatched] ?? 'running';
                  })();

                  if (statusForNode === 'running') {
                    Object.entries(nextStatuses).forEach(([id, status]) => {
                      if (status === 'running' && id !== localMatched) {
                        nextStatuses[id] = 'completed';
                      }
                    });
                  }

                  const previousStatus = nextStatuses[localMatched];
                  let appliedStatus = statusForNode;

                  if (!previousStatus && statusForNode === 'completed') {
                    appliedStatus = 'running';
                    scheduledCompletionId = localMatched;
                  }

                  nextStatuses[localMatched] = appliedStatus;
                  targetStatus = statusForNode;

                  const currentNode = currentNodes.find(n => n.id === localMatched);
                  const currentInnerData = (currentNode?.data?.innerData ?? {}) as any;
                  const isRunning = appliedStatus === 'running';
                  const isCompleted = appliedStatus === 'completed';
                  const isError = appliedStatus === 'error';
                  const isRun = isRunning || isCompleted || isError;

                  if (
                    currentInnerData.isRunning === isRunning &&
                    currentInnerData.isCompleted === isCompleted &&
                    currentInnerData.isError === isError &&
                    currentInnerData.isRun === isRun
                  ) {
                    nodeStatusRef.current = nextStatuses;
                  } else {
                    nodeStatusRef.current = nextStatuses;
                    pendingNodeUpdatesRef.current.set(localMatched, {
                      isRun,
                      isRunning,
                      isCompleted,
                      isError,
                    });

                    const isCriticalUpdate = parsedData.node_name && (
                      parsedData.event === 'on_chain_start' || 
                      parsedData.event === 'chain_start' ||
                      parsedData.llm?.content ||
                      parsedData.log?.llm?.content
                    );
                    
                    if (isCriticalUpdate) {
                      const updates = pendingNodeUpdatesRef.current;
                      if (updates.size > 0) {
                        setNodes(prev => {
                          let hasChanges = false;
                          const updatedNodes = prev.map(node => {
                            const update = updates.get(node.id);
                            if (!update) {
                              return node;
                            }

                            const innerData = node.data?.innerData ?? {};
                            if (
                              innerData.isRunning === update.isRunning &&
                              innerData.isCompleted === update.isCompleted &&
                              innerData.isError === update.isError &&
                              innerData.isRun === update.isRun
                            ) {
                              return node;
                            }

                            hasChanges = true;
                            return {
                              ...node,
                              data: {
                                ...node.data,
                                innerData: {
                                  ...innerData,
                                  isRun: update.isRun,
                                  isRunning: update.isRunning,
                                  isCompleted: update.isCompleted,
                                  isError: update.isError,
                                  hasError: update.isError,
                                },
                              },
                            };
                          });

                          if (!hasChanges) {
                            return prev;
                          }

                          pendingNodeUpdatesRef.current.clear();
                          return updatedNodes;
                        });
                      }
                    }
                    
                    if (nodeUpdateTimeoutRef.current) {
                      clearTimeout(nodeUpdateTimeoutRef.current);
                    }
                    nodeUpdateTimeoutRef.current = setTimeout(() => {
                      const updates = pendingNodeUpdatesRef.current;
                      if (updates.size === 0) {
                        return;
                      }

                      setNodes(prev => {
                        let hasChanges = false;
                        const updatedNodes = prev.map(node => {
                          const update = updates.get(node.id);
                          if (!update) {
                            return node;
                          }

                          const innerData = node.data?.innerData ?? {};
                          if (
                            innerData.isRunning === update.isRunning &&
                            innerData.isCompleted === update.isCompleted &&
                            innerData.isError === update.isError &&
                            innerData.isRun === update.isRun
                          ) {
                            return node;
                          }

                          hasChanges = true;
                          return {
                            ...node,
                            data: {
                              ...node.data,
                              innerData: {
                                ...innerData,
                                isRun: update.isRun,
                                isRunning: update.isRunning,
                                isCompleted: update.isCompleted,
                                isError: update.isError,
                                hasError: update.isError,
                              },
                            },
                          };
                        });

                        if (!hasChanges) {
                          return prev;
                        }

                        pendingNodeUpdatesRef.current.clear();
                        return updatedNodes;
                      });
                    }, 10);
                  }
                }

                if (matchedNodeId && targetStatus) {
                  if (targetStatus === 'error') {
                    setProgressMessage(`${nodeDisplayName} 오류 발생`);
                  } else if (targetStatus === 'completed') {
                    setProgressMessage(`${nodeDisplayName} 완료`);
                  } else {
                    setProgressMessage(`${nodeDisplayName} 처리 중...`);
                  }
                }

                if (scheduledCompletionId) {
                  const completionNodeId = scheduledCompletionId;
                  window.setTimeout(() => {
                    nodeStatusRef.current = {
                      ...nodeStatusRef.current,
                      [completionNodeId]: 'completed',
                    };
                    pendingNodeUpdatesRef.current.set(completionNodeId, {
                      isRun: true,
                      isRunning: false,
                      isCompleted: true,
                      isError: false,
                    });

                    if (nodeUpdateTimeoutRef.current) {
                      clearTimeout(nodeUpdateTimeoutRef.current);
                    }
                    nodeUpdateTimeoutRef.current = setTimeout(() => {
                      const updates = pendingNodeUpdatesRef.current;
                      if (updates.size === 0) {
                        return;
                      }

                      setNodes(prev => {
                        let hasChanges = false;
                        const updatedNodes = prev.map(node => {
                          const update = updates.get(node.id);
                          if (!update) {
                            return node;
                          }

                          const innerData = node.data?.innerData ?? {};
                          if (
                            innerData.isRunning === update.isRunning &&
                            innerData.isCompleted === update.isCompleted &&
                            innerData.isError === update.isError &&
                            innerData.isRun === update.isRun
                          ) {
                            return node;
                          }

                          hasChanges = true;
                          return {
                            ...node,
                            data: {
                              ...node.data,
                              innerData: {
                                ...innerData,
                                isRun: update.isRun,
                                isRunning: update.isRunning,
                                isCompleted: update.isCompleted,
                                isError: update.isError,
                                hasError: update.isError,
                              },
                            },
                          };
                        });

                        if (!hasChanges) {
                          return prev;
                        }

                        pendingNodeUpdatesRef.current.clear();
                        return updatedNodes;
                      });
                    }, 10);
                  }, 150);
                }
              } else if (parsedData.event === 'end' || resolveNodeStatus(parsedData) === 'completed') {
                const executionOrder = nodeOrderRef.current.length > 0 ? nodeOrderRef.current : Object.keys(nodeStatusRef.current);

                if (executionOrder.length === 0) {
                  // 🔥 모든 노드를 완료 상태로 변경 (디바운싱 적용)
                  currentNodes.forEach(node => {
                    nodeStatusRef.current[node.id] = 'completed';
                    pendingNodeUpdatesRef.current.set(node.id, {
                      isRun: true,
                      isRunning: false,
                      isCompleted: true,
                      isError: false,
                    });
                  });

                  if (nodeUpdateTimeoutRef.current) {
                    clearTimeout(nodeUpdateTimeoutRef.current);
                  }
                  nodeUpdateTimeoutRef.current = setTimeout(() => {
                    const updates = pendingNodeUpdatesRef.current;
                    if (updates.size === 0) {
                      return;
                    }

                    setNodes(prev => {
                      let hasChanges = false;
                      const updatedNodes = prev.map(node => {
                        const update = updates.get(node.id);
                        if (!update) {
                          return node;
                        }

                        const innerData = node.data?.innerData ?? {};
                        if (
                          innerData.isRunning === update.isRunning &&
                          innerData.isCompleted === update.isCompleted &&
                          innerData.isError === update.isError &&
                          innerData.isRun === update.isRun
                        ) {
                          return node;
                        }

                        hasChanges = true;
                        return {
                          ...node,
                          data: {
                            ...node.data,
                            innerData: {
                              ...innerData,
                              isRun: update.isRun,
                              isRunning: update.isRunning,
                              isCompleted: update.isCompleted,
                              isError: update.isError,
                              hasError: update.isError,
                            },
                          },
                        };
                      });

                      if (!hasChanges) {
                        return prev;
                      }

                      pendingNodeUpdatesRef.current.clear();
                      return updatedNodes;
                    });
                  }, 10); // 🔥 10ms 디바운싱 (실시간 반응 향상)
                } else {
                  executionOrder.forEach((nodeId, index) => {
                    window.setTimeout(() => {
                      nodeStatusRef.current[nodeId] = 'completed';
                      // 🔥 디바운싱: pendingNodeUpdatesRef에 추가
                      pendingNodeUpdatesRef.current.set(nodeId, {
                        isRun: true,
                        isRunning: false,
                        isCompleted: true,
                        isError: false,
                      });

                      if (nodeUpdateTimeoutRef.current) {
                        clearTimeout(nodeUpdateTimeoutRef.current);
                      }
                      nodeUpdateTimeoutRef.current = setTimeout(() => {
                        const updates = pendingNodeUpdatesRef.current;
                        if (updates.size === 0) {
                          return;
                        }

                        setNodes(prev => {
                          let hasChanges = false;
                          const updatedNodes = prev.map(node => {
                            const update = updates.get(node.id);
                            if (!update) {
                              return node;
                            }

                            const innerData = node.data?.innerData ?? {};
                            if (
                              innerData.isRunning === update.isRunning &&
                              innerData.isCompleted === update.isCompleted &&
                              innerData.isError === update.isError &&
                              innerData.isRun === update.isRun
                            ) {
                              return node;
                            }

                            hasChanges = true;
                            return {
                              ...node,
                              data: {
                                ...node.data,
                                innerData: {
                                  ...innerData,
                                  isRun: update.isRun,
                                  isRunning: update.isRunning,
                                  isCompleted: update.isCompleted,
                                  isError: update.isError,
                                  hasError: update.isError,
                                },
                              },
                            };
                          });

                          if (!hasChanges) {
                            return prev;
                          }

                          pendingNodeUpdatesRef.current.clear();
                          return updatedNodes;
                        });
                      }, 10);
                    }, index * 160);
                  });
                }
              } else if (parsedData.final_result !== undefined) {
                setProgressMessage('최종 결과 생성 중...');
              } else if (parsedData.updates) {
                setProgressMessage('응답 완성 중...');
              }

              
              if (parsedData.event === 'end' || parsedData.data === '[DONE]') {
                shouldTerminate = true;
              }

              if (parsedData.updates && parsedData.updates.messages && !Array.isArray(parsedData.updates.messages) && parsedData.updates.messages.content) {
                const content = parsedData.updates.messages.content;
                if (content && typeof content === 'string' && content.length > 0) {
                  const filteredContent = content;
                  accumulatedResponse += filteredContent;
                  setStreamingMessage(accumulatedResponse);
                  responseReceived = true;
                }
              }

              if (parsedData.llm && parsedData.llm.content !== undefined) {
                const llmContent = parsedData.llm.content;
                if (typeof llmContent === 'string' && llmContent.length > 0) {
                  const nodeType = parsedData.node_type || '';
                  if (nodeType.includes('generator') || !nodeType.includes('categorizer')) {
                    const filteredContent = llmContent
                    if (!responseReceived || accumulatedResponse.length < filteredContent.length) {
                      accumulatedResponse = filteredContent;
                      setStreamingMessage(filteredContent);
                      responseReceived = true;
                    }
                  }
                }
              }

              if (!responseReceived && parsedData.content && typeof parsedData.content === 'string' && parsedData.content.length > 0) {
                const filteredContent = parsedData.content;
                accumulatedResponse = filteredContent;
                setStreamingMessage(filteredContent);
                responseReceived = true;
              }

              if (parsedData.node_type === 'agent__categorizer' && parsedData.llm && parsedData.llm.selected) {
                const selectedValue = parsedData.llm.selected;
                if (typeof selectedValue === 'string' && selectedValue.length > 0) {
                  formatTokenValues['selected_96b13362'] = selectedValue;
                  formatTokenValues['selected__global'] = selectedValue;
                }
              }

              if (parsedData.node_type === 'condition' || parsedData.node_type === 'agent__condition') {
                const conditionNode = currentNodes.find((node: any) => {
                  if (parsedData.node_name && ((node.data?.name as string) === parsedData.node_name || node.id === parsedData.node_name)) {
                    return true;
                  }
                  if (parsedData.node_id && node.id === parsedData.node_id) {
                    return true;
                  }
                  if (parsedData.node_id) {
                    const parts = parsedData.node_id.split('_');
                    if (parts.length > 0 && node.id && node.id.includes(parts[0])) {
                      return true;
                    }
                  }
                  return false;
                });

                if (conditionNode) {
                  const conditionData = conditionNode.data as any;
                  const outputKeys = Array.isArray(conditionData?.output_keys) ? conditionData.output_keys : [];

                  let nodeIdBase = '';
                  if (conditionNode.id) {
                    const parts = conditionNode.id.split('_');
                    if (parts.length > 0) {
                      nodeIdBase = parts[0];
                    }
                  }

                  if (parsedData.updates?.additional_kwargs) {
                    let conditionLabel = parsedData.updates.additional_kwargs.condition_label || parsedData.updates.additional_kwargs.selected;

                    if (!conditionLabel) {
                      const allAdditionalKeys = Object.keys(parsedData.updates.additional_kwargs);
                      for (const key of allAdditionalKeys) {
                        const value = parsedData.updates.additional_kwargs[key];
                        if (typeof value === 'string' && value.length > 0) {
                          if (value === 'condition-else' || value.includes('condition-else') || key.includes('condition-else')) {
                            conditionLabel = value;
                            break;
                          }
                        }
                      }
                    }

                    if (!conditionLabel && parsedData.updates) {
                      const allKeys = Object.keys(parsedData.updates);
                      for (const key of allKeys) {
                        if (key.includes('condition') || key.includes('handle-')) {
                          const value = (parsedData.updates as any)[key];
                          if (typeof value === 'string' && value.length > 0) {
                            if (value === 'condition-else' || value.includes('condition-else')) {
                              conditionLabel = value;
                              break;
                            }
                            conditionLabel = value;
                            break;
                          }
                        }
                      }
                    }

                    if (!conditionLabel && parsedData.updates?.additional_kwargs) {
                      const additionalKeys = Object.keys(parsedData.updates.additional_kwargs);
                      for (const key of additionalKeys) {
                        if (key.includes('condition') || key.includes('handle-') || key.startsWith('handle-')) {
                          const value = parsedData.updates.additional_kwargs[key];
                          if (typeof value === 'string' && value.length > 0) {
                            conditionLabel = value;
                            break;
                          }
                        }
                      }
                    }
                    
                    if (!conditionLabel && conditionNode) {
                      const conditions = Array.isArray(conditionData?.conditions) ? conditionData.conditions : [];
                      const hasNonElseConditions = conditions.some((cond: any) => cond.id && cond.id !== 'condition-else');
                      
                      const defaultCondition = conditionData?.default_condition || '';
                      const isDefaultElse = defaultCondition && (defaultCondition.includes('condition-else') || defaultCondition === 'condition-else');
                      
                      if (hasNonElseConditions || parsedData.node_type === 'condition' || isDefaultElse) {
                        if (defaultCondition && defaultCondition.includes('condition-else')) {
                          conditionLabel = defaultCondition;
                        } else {
                          conditionLabel = conditionNode.id ? `${conditionNode.id}-condition-else` : 'condition-else';
                        }
                        console.log(`[DEBUG] ELSE 분기 감지: conditionLabel = ${conditionLabel}, nodeId = ${conditionNode.id}, defaultCondition = ${defaultCondition}`);
                      }
                    }

                    if (typeof conditionLabel === 'string' && conditionLabel.length > 0) {
                      outputKeys.forEach((outputKey: any) => {
                        if (outputKey.keytable_id) {
                          formatTokenValues[outputKey.keytable_id] = conditionLabel;
                        }
                        if (outputKey.name === 'selected' && conditionNode.id) {
                          formatTokenValues[`selected__${conditionNode.id}`] = conditionLabel;
                          formatTokenValues[`selected_${conditionNode.id}`] = conditionLabel;
                        }
                        if (outputKey.name === 'selected' && nodeIdBase) {
                          formatTokenValues[`selected__${nodeIdBase}`] = conditionLabel;
                          formatTokenValues[`selected_${nodeIdBase}`] = conditionLabel;
                        }
                        if (outputKey.name) {
                          formatTokenValues[outputKey.name] = conditionLabel;
                        }
                      });
                      formatTokenValues['condition_label'] = conditionLabel;
                      formatTokenValues['selected__global'] = conditionLabel;
                      formatTokenValues['selected_global'] = conditionLabel;
                      if (parsedData.node_name) {
                        formatTokenValues[`${parsedData.node_name}_condition_label`] = conditionLabel;
                      }
                    }
                  }

                  if (!formatTokenValues[`selected__${conditionNode.id}`] && parsedData) {
                    const parsedKeys = Object.keys(parsedData);
                    for (const key of parsedKeys) {
                      if (key.includes('condition') || key.includes('handle-') || key.startsWith('handle-')) {
                        const value = (parsedData as any)[key];
                        if (typeof value === 'string' && value.length > 0) {
                          formatTokenValues[`selected__${conditionNode.id}`] = value;
                          formatTokenValues[`selected_${conditionNode.id}`] = value;
                          if (nodeIdBase) {
                            formatTokenValues[`selected__${nodeIdBase}`] = value;
                            formatTokenValues[`selected_${nodeIdBase}`] = value;
                          }
                          outputKeys.forEach((outputKey: any) => {
                            if (outputKey.keytable_id) {
                              formatTokenValues[outputKey.keytable_id] = value;
                            }
                          });
                          break;
                        }
                      }
                    }
                  }

                  if (parsedData.updates) {
                    if (parsedData.updates.additional_kwargs) {
                      const additionalKeys = Object.keys(parsedData.updates.additional_kwargs);
                      for (const key of additionalKeys) {
                        if (key.startsWith('selected_') && conditionNode.id) {
                          const value = parsedData.updates.additional_kwargs[key];
                          if (typeof value === 'string' && value.length > 0) {
                            formatTokenValues[key] = value;
                            formatTokenValues[`selected__${conditionNode.id}`] = value;
                            formatTokenValues[`selected_${conditionNode.id}`] = value;
                            if (nodeIdBase) {
                              formatTokenValues[`selected__${nodeIdBase}`] = value;
                              formatTokenValues[`selected_${nodeIdBase}`] = value;
                            }
                          }
                        }
                      }
                    }

                    outputKeys.forEach((outputKey: any) => {
                      const keytableId = outputKey.keytable_id;
                      const keyName = outputKey.name;

                      if (keytableId && parsedData.updates.additional_kwargs?.[keytableId]) {
                        const value = parsedData.updates.additional_kwargs[keytableId];
                        if (typeof value === 'string' && value.length > 0) {
                          formatTokenValues[keytableId] = value;
                          if (keyName === 'selected' && conditionNode.id) {
                            formatTokenValues[`selected__${conditionNode.id}`] = value;
                            formatTokenValues[`selected_${conditionNode.id}`] = value;
                          }
                          if (keyName) {
                            formatTokenValues[keyName] = value;
                          }
                        }
                      }

                      if (keyName && parsedData.updates.additional_kwargs?.[keyName]) {
                        const value = parsedData.updates.additional_kwargs[keyName];
                        if (typeof value === 'string' && value.length > 0) {
                          formatTokenValues[keyName] = value;
                          if (keyName === 'selected' && conditionNode.id) {
                            formatTokenValues[`selected__${conditionNode.id}`] = value;
                            formatTokenValues[`selected_${conditionNode.id}`] = value;
                          }
                          if (keytableId) {
                            formatTokenValues[keytableId] = value;
                          }
                        }
                      }
                    });
                  }
                }
              }

              if (parsedData.llm && parsedData.llm.content && typeof parsedData.llm.content === 'string' && parsedData.llm.content.length > 0) {
                if (parsedData.node_type !== 'agent__categorizer' && !parsedData.llm.selected) {
                  const llmContent = parsedData.llm.content.trim();
                  if (llmContent && llmContent.length > 0) {
                    const filteredContent = llmContent;
                    if (!responseReceived || filteredContent.length > accumulatedResponse.length) {
                      accumulatedResponse = filteredContent;
                      setStreamingMessage(filteredContent);
                      finalResultContent = filteredContent;
                      responseReceived = true;
                    }
                  }
                }
              }

              if (parsedData.final_result !== undefined && !outputNodeContent) {
                const extractedFinal = extractMeaningfulText(parsedData.final_result);
                if (extractedFinal) {
                  const filteredFinal = extractedFinal;

                  if (accumulatedResponse.length === 0) {
                    accumulatedResponse = filteredFinal;
                    finalResultContent = filteredFinal;
                  } else if (filteredFinal.startsWith(accumulatedResponse)) {
                    accumulatedResponse = filteredFinal;
                    finalResultContent = filteredFinal;
                  } else if (accumulatedResponse.length < filteredFinal.length && filteredFinal.startsWith(accumulatedResponse)) {
                    accumulatedResponse = filteredFinal;
                    finalResultContent = filteredFinal;
                  } else if (filteredFinal.length <= 3) {
                    if (!accumulatedResponse.endsWith(filteredFinal)) {
                      accumulatedResponse += filteredFinal;
                      finalResultContent = accumulatedResponse;
                    }
                  } else {
                    let overlapFound = false;
                    for (let i = Math.min(accumulatedResponse.length, filteredFinal.length); i > 0; i--) {
                      if (accumulatedResponse.endsWith(filteredFinal.substring(0, i))) {
                        accumulatedResponse = accumulatedResponse + filteredFinal.substring(i);
                        finalResultContent = accumulatedResponse;
                        overlapFound = true;
                        break;
                      }
                    }
                    if (!overlapFound) {
                      if (filteredFinal.length > accumulatedResponse.length) {
                        accumulatedResponse = filteredFinal;
                        finalResultContent = filteredFinal;
                      } else {
                        accumulatedResponse += filteredFinal;
                        finalResultContent = accumulatedResponse;
                      }
                    }
                  }
                  setStreamingMessage(accumulatedResponse);
                  responseReceived = true;
                }
              }

              if (parsedData.updates && parsedData.updates.content && typeof parsedData.updates.content === 'string' && parsedData.updates.content.length > 0) {
                  const filteredContent = parsedData.updates.content;

                if ((parsedData.node_type === 'output__chat' || parsedData.node_type === 'output__keys') && parsedData.node_name && parsedData.node_name !== 'unknown') {
                  let actualNodeId = parsedData.node_name;
                  const foundNode = currentNodes.find(node => {
                    const nodeName = node.data?.name as string;
                    const nodeId = node.id;
                    const nodeType = node.type;
                    return (nodeType === 'output__chat' || nodeType === 'output__keys') &&
                           (nodeId === parsedData.node_name || 
                            nodeName === parsedData.node_name ||
                            nodeId.includes(parsedData.node_name) ||
                            parsedData.node_name.includes(nodeId) ||
                            (nodeName && (nodeName === parsedData.node_name || nodeName.includes(parsedData.node_name) || parsedData.node_name.includes(nodeName))));
                  });
                  if (foundNode) {
                    actualNodeId = foundNode.id;
                  }
                  
                  const newTracingMessage = {
                    nodeId: actualNodeId,
                    node_name: parsedData.node_name,
                    nodeType: parsedData.node_type || '',
                    callback: parsedData.event || currentEvent || 'on_chain_end',
                    progress: parsedData.progress,
                    updates: parsedData.updates,
                    final_result: parsedData.final_result,
                    log: parsedData,
                    turn: currentTurn,
                  };
                  const isDuplicate = tracingMessages.some(
                    msg => (msg.nodeId === newTracingMessage.nodeId || msg.node_name === newTracingMessage.node_name) && 
                           msg.turn === currentTurn &&
                           msg.nodeType === newTracingMessage.nodeType
                  );
                  if (!isDuplicate) {
                    setTracingMessages(prev => [...prev, newTracingMessage]);
                  }
                }

                if (parsedData.node_type !== 'output__chat' && parsedData.node_type !== 'output__keys') {
                  if (filteredContent.length > accumulatedResponse.length) {
                    accumulatedResponse = filteredContent;
                    setStreamingMessage(filteredContent);
                    finalResultContent = filteredContent;
                    responseReceived = true;
                  }
                } else if (parsedData.node_type === 'output__keys') {
                  let jsonContent = '';
                  try {
                    if (parsedData.updates.additional_kwargs && typeof parsedData.updates.additional_kwargs === 'object') {
                      jsonContent = JSON.stringify(parsedData.updates.additional_kwargs);
                    } else {
                      const parsed = JSON.parse(filteredContent);
                      jsonContent = JSON.stringify(parsed);
                    }
                  } catch (e) {
                    jsonContent = filteredContent;
                  }
                  outputNodeContent = jsonContent;
                  finalResultContent = jsonContent;
                } else {
                  outputNodeContent = filteredContent;
                  finalResultContent = filteredContent;
                  if (filteredContent && 
                      !filteredContent.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) &&
                      filteredContent !== '답변하기' && 
                      filteredContent !== '하기') {
                    accumulatedResponse = filteredContent;
                    setStreamingMessage(filteredContent);
                    responseReceived = true;
                  }
                }
              }
              
              if (parsedData.updates && parsedData.updates.messages && Array.isArray(parsedData.updates.messages) && parsedData.updates.messages.length > 0) {
                const lastMessage = parsedData.updates.messages[parsedData.updates.messages.length - 1];
                if (lastMessage && lastMessage.content && typeof lastMessage.content === 'string' && lastMessage.content.length > 0) {
                  const filteredContent = lastMessage.content;
                  if (parsedData.node_type === 'output__chat' && filteredContent && 
                      !filteredContent.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) &&
                      filteredContent !== '답변하기' && filteredContent !== '하기') {
                    outputNodeContent = filteredContent;
                    finalResultContent = filteredContent;
                    accumulatedResponse = filteredContent;
                    setStreamingMessage(filteredContent);
                    responseReceived = true;
                  }
                  else if (parsedData.node_type === 'agent__generator' && filteredContent && filteredContent.length > 20 && 
                      !filteredContent.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) &&
                      filteredContent !== '답변하기' && filteredContent !== '하기' &&
                      !outputNodeContent) {
                    finalResultContent = filteredContent;
                    accumulatedResponse = filteredContent;
                    setStreamingMessage(filteredContent);
                    responseReceived = true;
                  }
                }
              }

              if (parsedData.node_type === 'output__keys' && parsedData.updates && parsedData.updates.additional_kwargs) {
                try {
                  const jsonContent = JSON.stringify(parsedData.updates.additional_kwargs);
                  if (jsonContent && jsonContent.trim() !== '{}') {
                    outputNodeContent = jsonContent;
                    finalResultContent = jsonContent;
                  }
                } catch (e) {
                }
              }

              if (parsedData.updates) {
                if (
                  parsedData.node_type === 'agent__generator' &&
                  parsedData.node_name &&
                  parsedData.updates.messages &&
                  Array.isArray(parsedData.updates.messages) &&
                  parsedData.updates.messages.length > 0
                ) {
                  const lastMessage = parsedData.updates.messages[parsedData.updates.messages.length - 1];
                  if (lastMessage && lastMessage.content && typeof lastMessage.content === 'string' && lastMessage.content.length > 0) {
                    const filteredContent = lastMessage.content;
                    if (filteredContent && filteredContent.length > 20) {
                      if (outputNodeContent && outputNodeContent.trim() !== '' && outputNodeContent.length > 20) {
                      } else {
                        if (parsedData.node_name === 'Executor' || parsedData.node_name?.includes('Executor')) {
                          accumulatedResponse = filteredContent;
                          finalResultContent = filteredContent;
                          setStreamingMessage(filteredContent);
                          responseReceived = true;
                        } else if (!responseReceived || accumulatedResponse.length < filteredContent.length) {
                          accumulatedResponse = filteredContent;
                          finalResultContent = filteredContent;
                          setStreamingMessage(filteredContent);
                          responseReceived = true;
                        }
                      }
                    }

                    const nodeName = parsedData.node_name;
                    const outputKeyName = `${nodeName}_content`;

                    formatTokenValues[outputKeyName] = lastMessage.content;

                    let foundContentKey: string | null = null;
                    if (parsedData.updates.additional_kwargs) {
                      Object.entries(parsedData.updates.additional_kwargs).forEach(([key, value]) => {
                        if (typeof value === 'string' && /^content__[a-f0-9_]+/.test(key) && value.length > 0) {
                          formatTokenValues[key] = value;
                          foundContentKey = key;
                          formatTokenValues[outputKeyName] = value;
                        }
                      });
                    }

                    if (!foundContentKey) {
                      let nodeIdBase = '';
                      if (parsedData.node_id) {
                        const parts = parsedData.node_id.split('_');
                        if (parts.length > 0) {
                          nodeIdBase = parts[0];
                        }
                      }

                      const currentNode = currentNodes.find((node: any) => {
                        if (node.data?.name === nodeName) return true;
                        if (parsedData.node_id && node.id === parsedData.node_id) return true;
                        if (nodeIdBase && node.id && node.id.includes(nodeIdBase)) return true;
                        if (nodeName && node.data?.name && String(node.data.name).includes(nodeName)) return true;
                        return false;
                      });

                      if (currentNode) {
                        const outputKeys = Array.isArray((currentNode.data as any)?.output_keys) ? (currentNode.data as any).output_keys : [];

                        const contentKey = outputKeys.find((key: any) => {
                          const keyName = key?.name || key?.key || '';
                          return keyName === 'content' || keyName.includes('content');
                        });

                        if (contentKey && contentKey.keytable_id) {
                          // keytable_id로 저장 (예: content__aaee65ec)
                          formatTokenValues[contentKey.keytable_id] = lastMessage.content;
                          foundContentKey = contentKey.keytable_id;
                        }
                      }

                      // keyTableList에서도 찾기 (노드 이름 기반)
                      if (!foundContentKey && keyTableList) {
                        // 🔥 재귀 템플릿: 먼저 노드 ID에서 접미사 제거하여 keytable_id 패턴 찾기
                        // 노드 ID가 "aaee65ec_c03e0fc8"이면 "aaee65ec" 부분을 추출하여 "content__aaee65ec" 찾기
                        let nodeIdBase = '';
                        if (parsedData.node_id) {
                          // 노드 ID에서 접미사 제거 (예: "aaee65ec_c03e0fc8" -> "aaee65ec")
                          const parts = parsedData.node_id.split('_');
                          if (parts.length > 0) {
                            nodeIdBase = parts[0];
                          }
                        }

                        // keyTableList에서 content__<nodeIdBase> 패턴 찾기
                        const keyTableEntry = keyTableList.find((entry: any) => {
                          // 1. 직접 keytable_id 패턴 찾기 (content__aaee65ec)
                          if (entry.id && nodeIdBase && entry.id === `content__${nodeIdBase}`) {
                            return true;
                          }
                          // 2. 노드 ID로 찾기
                          if (parsedData.node_id && entry.nodeId === parsedData.node_id) {
                            // content 키를 가진 항목 찾기
                            if (entry.key === 'content' || entry.name?.includes('content')) {
                              return true;
                            }
                          }
                          // 3. 노드 이름이 포함된 id 찾기
                          if (entry.id && entry.id.includes(nodeName) && (entry.key === 'content' || entry.name?.includes('content'))) {
                            return true;
                          }
                          // 4. 또는 노드 이름과 content가 포함된 경우
                          if (entry.name && entry.name.includes('content') && entry.nodeName === nodeName) {
                            return true;
                          }
                          // 5. 노드 ID 기반으로 content__* 패턴 찾기
                          if (entry.id && nodeIdBase && entry.id.includes(`content__${nodeIdBase}`)) {
                            return true;
                          }
                          return false;
                        });

                        if (keyTableEntry && keyTableEntry.id) {
                          formatTokenValues[keyTableEntry.id] = lastMessage.content;
                          foundContentKey = keyTableEntry.id;
                        }
                      }
                    }
                  }
                }

                // 🔥 Union 노드의 출력값을 formatTokenValues에 저장 (Output Chat 노드가 사용할 수 있도록)
                if (parsedData.node_type === 'union' && parsedData.updates?.additional_kwargs) {
                  const additionalKwargs = parsedData.updates.additional_kwargs;

                  // additional_kwargs에서 content__* 패턴 찾기
                  Object.entries(additionalKwargs).forEach(([key, value]) => {
                    if (typeof value === 'string' && /^content__[a-f0-9_]+/.test(key) && value.length > 0) {
                      formatTokenValues[key] = value;
                    }
                  });
                }

                // 🔥 Code 노드의 출력값을 formatTokenValues에 저장 (Output Chat 노드가 사용할 수 있도록)
                if (parsedData.node_type === 'agent__coder' && parsedData.node_name) {
                  const nodeName = parsedData.node_name;

                  // Code 노드의 출력은 다양한 형식으로 올 수 있음
                  let codeOutput = '';

                  // 1. updates.content 확인 (가장 일반적)
                  if (parsedData.updates?.content && typeof parsedData.updates.content === 'string' && parsedData.updates.content.length > 0) {
                    codeOutput = parsedData.updates.content;
                  }
                  // 2. updates.result 확인
                  else if (parsedData.updates?.result && typeof parsedData.updates.result === 'string' && parsedData.updates.result.length > 0) {
                    codeOutput = parsedData.updates.result;
                  }
                  // 3. updates.output 확인
                  else if (parsedData.updates?.output && typeof parsedData.updates.output === 'string' && parsedData.updates.output.length > 0) {
                    codeOutput = parsedData.updates.output;
                  }
                  // 4. updates.final_result 확인
                  else if (parsedData.updates?.final_result !== undefined) {
                    const extractedFinal = extractMeaningfulText(parsedData.updates.final_result);
                    if (extractedFinal && typeof extractedFinal === 'string' && extractedFinal.length > 0) {
                      codeOutput = extractedFinal;
                    }
                  }
                  // 5. additional_kwargs에서 Code 노드의 출력 키 찾기
                  else if (parsedData.updates?.additional_kwargs) {
                    const additionalKwargs = parsedData.updates.additional_kwargs;

                    // 가능한 키 패턴들
                    const possibleKeys = [
                      'result',
                      'output',
                      'content',
                      `${nodeName}_result`,
                      `${nodeName}_output`,
                      `${nodeName}_content`,
                      'output_result',
                      'code_result',
                      'code_output',
                    ];

                    for (const key of possibleKeys) {
                      const value = additionalKwargs[key];
                      if (typeof value === 'string' && value.length > 0) {
                        codeOutput = value;
                        break;
                      }
                    }

                    // 위에서 찾지 못했으면 모든 키를 확인
                    if (!codeOutput) {
                      for (const key of Object.keys(additionalKwargs)) {
                        const value = additionalKwargs[key];
                        if (typeof value === 'string' && value.length > 0 && value.length < 100) {
                          // 짧은 문자열만 확인 (Code 노드 출력은 보통 짧음)
                          codeOutput = value;
                          break;
                        }
                      }
                    }
                  }

                  if (!codeOutput) {
                  }

                  // Code 노드의 출력을 formatTokenValues에 저장
                  if (codeOutput) {
                    // 🔥 Code 노드의 output_keys에서 keytable_id 찾기
                    const codeNode = currentNodes.find(
                      (node: any) => (node.data?.name === nodeName || node.id === nodeName || node.id === parsedData.node_id) && node.type === 'agent__coder'
                    );

                    if (codeNode && codeNode.data?.output_keys && Array.isArray(codeNode.data.output_keys)) {
                      // Code 노드의 output_keys에서 keytable_id 찾기
                      codeNode.data.output_keys.forEach((outputKey: any) => {
                        if (outputKey.keytable_id) {
                          // keytable_id를 키로 사용하여 formatTokenValues에 저장
                          formatTokenValues[outputKey.keytable_id] = codeOutput;
                        }
                      });
                    } else {
                      // 노드를 찾을 수 없으면 노드 이름 기반 키 사용 (fallback)
                      const outputKeyName = `${nodeName}_output`;
                      formatTokenValues[outputKeyName] = codeOutput;
                      formatTokenValues[`${nodeName}_result`] = codeOutput;
                    }

                    // Code 노드의 출력이 최종 응답으로 사용되도록 설정
                    if (!responseReceived || codeOutput.length > accumulatedResponse.length) {
                      accumulatedResponse = codeOutput;
                      setStreamingMessage(codeOutput);
                      finalResultContent = codeOutput;
                      responseReceived = true;
                    }
                  }
                }

                // updates.messages에서 답변 추출 (우선순위 1)
                if (!responseReceived && parsedData.updates.messages && Array.isArray(parsedData.updates.messages) && parsedData.updates.messages.length > 0) {
                  const lastMessage = parsedData.updates.messages[parsedData.updates.messages.length - 1];
                  if (lastMessage && lastMessage.content && typeof lastMessage.content === 'string' && lastMessage.content.length > 0) {
                    messageTemp.length = 0;
                    messageTemp.push(lastMessage.content);
                    accumulatedResponse = lastMessage.content;
                    setStreamingMessage(lastMessage.content);
                    responseReceived = true;
                  }
                }

                // additional_kwargs에서 답변 추출 (우선순위 2 - messages가 없을 때만)
                if (!responseReceived && parsedData.updates.additional_kwargs) {
                  const additionalKwargs = parsedData.updates.additional_kwargs;

                  // 프롬프트 관련 필드 제외 목록 (더 확실하게)
                  const excludePatterns = [
                    'selected',
                    'category',
                    'instruction',
                    'detailed_response',
                    'context_connection',
                    'explicit_context',
                    'conversation_continuity',
                    'context_preservation',
                    'no_duplication',
                    'system_prompt',
                    'context_instruction',
                    'conversation_mode',
                    'context_awareness',
                    'memory_enabled',
                    'project_id',
                    'user_input',
                    'current_user_input',
                    'latest_user_input',
                    'query_',
                    'selected_',
                    'category_',
                    'conversation_history',
                    'chat_messages',
                    'messages',
                    'config',
                  ];

                  // formatTokenValues에 저장할 때도 프롬프트 필드 제외
                  // 단, Categorizer의 선택 결과(selected_96b13362)와 Condition의 condition_label은 저장해야 함
                  Object.entries(additionalKwargs).forEach(([key, value]) => {
                    if (typeof value === 'string') {
                      // Categorizer의 선택 결과는 항상 저장
                      if (key === 'selected_96b13362' || key === 'selected__global') {
                        formatTokenValues[key] = value;
                        return;
                      }

                      // Condition의 condition_label도 항상 저장
                      if (key === 'condition_label' || key.endsWith('_condition_label')) {
                        formatTokenValues[key] = value;
                        return;
                      }

                      // 프롬프트 필드는 formatTokenValues에도 저장하지 않음
                      const isExcluded = excludePatterns.some(pattern => key.toLowerCase().includes(pattern.toLowerCase()));
                      if (!isExcluded) {
                        formatTokenValues[key] = value;
                      }
                    }
                  });

                  // content_* 패턴 찾기 (정확한 패턴만 - 노드 ID가 포함된 형식)
                  const contentKeys = Object.keys(additionalKwargs).filter(key => {
                    // content_로 시작하고 그 뒤에 노드 ID 형식이 있는 키만 (content_0ad58992 같은 형식)
                    if (!/^content_[a-f0-9]+/.test(key)) {
                      return false;
                    }

                    // 제외 패턴 확인
                    const shouldExclude = excludePatterns.some(pattern => key.toLowerCase().includes(pattern.toLowerCase()));

                    return !shouldExclude;
                  });

                  for (const contentKey of contentKeys) {
                    const content = additionalKwargs[contentKey];

                    if (content && typeof content === 'string' && content.length > 0) {
                      // 완전한 답변만 저장 (더 긴 답변으로 업데이트)
                      if (!responseReceived || accumulatedResponse.length < content.length) {
                        messageTemp.length = 0;
                        messageTemp.push(content);
                        accumulatedResponse = content;
                        setStreamingMessage(content);
                        responseReceived = true;
                      }
                    }
                  }
                }

                // updates.final_result는 updates.content가 없을 때만 사용
                if (!responseReceived && parsedData.updates.final_result !== undefined && !parsedData.updates.content) {
                  const extractedFinal = extractMeaningfulText(parsedData.updates.final_result);
                  if (extractedFinal) {
                    // final_result는 문자 단위로 스트리밍될 수 있으므로 누적 처리
                    // extractedFinal이 기존 accumulatedResponse의 접두사이거나,
                    // accumulatedResponse가 extractedFinal의 접두사이면 누적
                    if (accumulatedResponse.length === 0) {
                      // 첫 번째 문자/텍스트
                      accumulatedResponse = extractedFinal;
                      finalResultContent = extractedFinal;
                    } else if (extractedFinal.startsWith(accumulatedResponse)) {
                      // 전체 응답이 온 경우 (기존 내용으로 시작)
                      accumulatedResponse = extractedFinal;
                      finalResultContent = extractedFinal;
                    } else if (accumulatedResponse.length < extractedFinal.length && extractedFinal.startsWith(accumulatedResponse)) {
                      // 기존 내용이 새 내용의 접두사인 경우 (누적)
                      accumulatedResponse = extractedFinal;
                      finalResultContent = extractedFinal;
                    } else if (extractedFinal.length <= 2 && !accumulatedResponse.endsWith(extractedFinal)) {
                      // 문자 단위로 오는 경우 (1-2자) 누적
                      accumulatedResponse += extractedFinal;
                      finalResultContent = accumulatedResponse;
                    } else if (extractedFinal.length > accumulatedResponse.length) {
                      // 새로운 내용이 더 길면 교체 (전체 응답이 온 경우)
                      accumulatedResponse = extractedFinal;
                      finalResultContent = extractedFinal;
                    }
                    setStreamingMessage(accumulatedResponse);
                    responseReceived = true;
                  }
                }
              }

              // 에러 처리 - 실제 에러 메시지 추출
              if (parsedData.status_code && parsedData.status_code >= 400) {
                actualError = parsedData.message || parsedData.error || parsedData.detail || '알 수 없는 오류가 발생했습니다.';
                setProgressMessage(actualError || '에러 발생');
                setStreamingMessage('');
                setIsChatLoading(false);

                const errorChatMessage: MessageFormat = {
                  id: '',
                  time: new Date().toLocaleString(),
                  content: `❌ 에러 발생: ${actualError}\n\n상세 정보: ${JSON.stringify(parsedData, null, 2)}`,
                  type: ChatType.AI,
                  regen: false,
                  elapsedTime: Date.now() - startTime,
                };
                commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });
                break;
              }

              // 에러 필드 확인 (다양한 에러 필드명 지원)
              if (parsedData.error || parsedData.exception || parsedData.traceback) {
                actualError = parsedData.error || parsedData.exception || '알 수 없는 오류가 발생했습니다.';
              }

              // 노드 실행 에러 확인
              if (parsedData.node_type && (parsedData.status === 'error' || parsedData.error)) {
                const nodeError = parsedData.error || parsedData.message || parsedData.exception || '노드 실행 중 오류 발생';
                actualError = `${parsedData.node_name || parsedData.node_type} 노드 오류: ${nodeError}`;
              }
              // currentEvent가 'end'일 때도 shouldTerminate만 설정
              if (currentEvent === 'end') {
                shouldTerminate = true;
              }
            } catch (parseError) {
              // JSON 파싱 실패는 조용히 무시
              continue;
            }
          }

          if (shouldTerminate) {
            break;
          }
        }

        // 응답이 없으면 조용히 종료 (하지만 "처리중" 메시지는 유지)
        // 실제로 노드가 실행 중일 수 있으므로 노드 실행 상태 확인
        
        // 🔥 output__chat 노드가 실행되지 않았지만 format_string과 selected_* 값이 있으면 답변 생성 시도 (hasAnyResponse 체크 전에 실행)
        // 🔥 안전성 체크: formatTokenValues가 존재하고, currentNodes가 배열인지 확인
        if (formatTokenValues && Array.isArray(currentNodes) && currentNodes.length > 0) {
          try {
            // 🔥 AgentApp 노드 복원 시 data 구조가 다를 수 있으므로 안전하게 처리
            const outputNode = currentNodes.find((node: any) => 
              node && 
              node.type === 'output__chat' && 
              node.data && 
              typeof node.data === 'object'
            );
            const outputFormatString = (outputNode && outputNode.type === 'output__chat' && outputNode.data) 
              ? ((outputNode.data as any)?.format_string || '') 
              : '';
            
            if (!responseReceived && !accumulatedResponse && outputFormatString && outputFormatString.includes('selected_')) {
              // formatTokenValues에서 selected_* 값 찾기
              const selectedKeys = Object.keys(formatTokenValues).filter(key => key && key.startsWith('selected_'));
              
              if (selectedKeys.length > 0) {
                const hasValueForFormat = selectedKeys.some(key => {
                  try {
                    const tokenPattern = new RegExp(`\\{\\{\\s*${key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\s*\\}\\}`, 'i');
                    const matches = tokenPattern.test(outputFormatString);
                    const hasValue = formatTokenValues[key];
                    return matches && hasValue;
                  } catch (e) {
                    return false;
                  }
                });
                
                if (hasValueForFormat) {
                  const cleanedFormatTokenValues: Record<string, string> = {};
                  Object.entries(formatTokenValues).forEach(([key, value]) => {
                    try {
                      const excludePatterns = [
                        'instruction',
                        'detailed_response',
                        'context_connection',
                        'explicit_context',
                        'conversation_continuity',
                        'context_preservation',
                        'no_duplication',
                        'system_prompt',
                        'context_instruction',
                      ];
                      const isExcluded = excludePatterns.some(pattern => key.toLowerCase().includes(pattern.toLowerCase()));
                      if (!isExcluded && typeof value === 'string' && value.length > 0) {
                        cleanedFormatTokenValues[key] = value;
                      }
                    } catch (e) {
                    }
                  });
                  
                  const hasTokensInFormatString = outputFormatString && /\{\{\s*([^}]+)\s*\}\}/g.test(outputFormatString);
                  const fallbackForFormatString = !hasTokensInFormatString ? undefined : (finalResultContent || undefined);
                  const formattedContent = applyFormatString(outputFormatString, cleanedFormatTokenValues, fallbackForFormatString);
                  
                  if (formattedContent && formattedContent.trim() !== '' && formattedContent !== outputFormatString) {
                    let finalContent = formattedContent.trim();
                    finalContent = finalContent.replace(/^[a-f0-9]{8}-(condition-\w+)$/, '$1');
                    if (finalContent === formattedContent.trim()) {
                      finalContent = finalContent.replace(/^[a-f0-9]+-(condition-\w+)$/, '$1');
                    }
                    
                    if (outputNode && outputNode.id) {
                      try {
                        nodeStatusRef.current[outputNode.id] = 'completed';
                        
                        const outputNodeName = (outputNode.data as any)?.name || outputNode.id;
                        
                        const additionalKwargs: Record<string, string> = {};
                        Object.keys(formatTokenValues).forEach(key => {
                          if (key && key.startsWith('selected_') && formatTokenValues[key]) {
                            additionalKwargs[key] = formatTokenValues[key];
                          }
                        });
                        
                        if (addStreamLog) {
                          addStreamLog({
                            node_id: outputNode.id,
                            node_name: outputNodeName,
                            node_type: 'output__chat',
                            turn: currentTurn,
                            updates: {
                              content: finalContent,
                              additional_kwargs: additionalKwargs,
                            },
                            timestamp: new Date().toISOString(),
                          });
                        }
                        
                        if (setTracingMessages) {
                          const newTracingMessage = {
                            nodeId: outputNode.id,
                            node_name: outputNodeName,
                            nodeType: 'output__chat',
                            callback: 'on_chain_end',
                            progress: '',
                            updates: {
                              content: finalContent,
                            },
                            final_result: finalContent,
                            log: {
                              node_id: outputNode.id,
                              node_name: outputNodeName,
                              node_type: 'output__chat',
                              updates: {
                                content: finalContent,
                              },
                            },
                            turn: currentTurn,
                          };
                          setTracingMessages(prev => [...prev, newTracingMessage]);
                        }
                      } catch (e) {
                        console.error('[DEBUG] output__chat 노드 로그 추가 중 에러:', e);
                      }
                    }
                    
                    const newMessage: MessageFormat = {
                      id: '',
                      time: new Date().toLocaleString(),
                      content: finalContent,
                      type: ChatType.AI,
                      regen: false,
                      elapsedTime: Date.now() - startTime,
                    };
                    commitAssistantMessage(newMessage, { targetIndex: targetAnswerIndex });
                    setIsChatLoading(false);
                    queryResponseRef.current = true;
                    return;
                  }
                }
              }
            }
          } catch (e) {
          }
        }
        
        const hasAnyResponse = responseReceived || accumulatedResponse.length > 0 || streamingMessage.length > 0 || (finalResultContent && finalResultContent.length > 0);
        
        if (!hasAnyResponse) {
          const hasRunningNodes = Object.values(nodeStatusRef.current).some(status => status === 'running');

          if (shouldTerminate) {
            if (checkIntervalRef.current) {
              clearInterval(checkIntervalRef.current);
              checkIntervalRef.current = null;
            }
            if (timeoutRef.current) {
              clearTimeout(timeoutRef.current);
              timeoutRef.current = null;
            }

            const waitTime = hasRunningNodes ? 3000 : 1000;

            timeoutRef.current = setTimeout(() => {
              if (checkIntervalRef.current) {
                clearInterval(checkIntervalRef.current);
                checkIntervalRef.current = null;
              }

              const nodeStatuses = nodeStatusRef.current;
              const executedNodes = Object.keys(nodeStatuses).filter(nodeId => nodeStatuses[nodeId] === 'completed' || nodeStatuses[nodeId] === 'running');
              const errorNodes = Object.keys(nodeStatuses).filter(nodeId => nodeStatuses[nodeId] === 'error');

              const getNodeName = (nodeId: string): string => {
                const node = currentNodes.find((n: any) => n.id === nodeId);
                if (node && node.data && node.data.name) {
                  return String(node.data.name);
                }
                return nodeId;
              };

              const getNodeType = (nodeId: string): string => {
                const node = currentNodes.find((n: any) => n.id === nodeId);
                return node?.type || '알 수 없음';
              };

              let errorDetails = '';
              let solutionGuide = '';

              if (actualError) {
                errorDetails = `\n\n📋 에러 내용:\n${actualError}`;
              }

              if (executedNodes.length > 0) {
                const executedNodeNames = executedNodes.map(nodeId => {
                  const nodeName = getNodeName(nodeId);
                  const nodeType = getNodeType(nodeId);
                  return `  • ${nodeName} (${nodeType})`;
                }).join('\n');
                errorDetails += `\n\n✅ 실행된 노드:\n${executedNodeNames}`;
              }

              if (errorNodes.length > 0) {
                const errorNodeNames = errorNodes.map(nodeId => {
                  const nodeName = getNodeName(nodeId);
                  const nodeType = getNodeType(nodeId);
                  return `  • ${nodeName} (${nodeType})`;
                }).join('\n');
                errorDetails += `\n\n❌ 에러 발생 노드:\n${errorNodeNames}`;
                solutionGuide = '\n\n💡 해결 방법:\n1. 에러 발생 노드를 확인하고 설정을 점검해주세요.\n2. 노드의 입력값과 연결 상태를 확인해주세요.\n3. 필요시 노드를 삭제하고 다시 추가해보세요.';
              }

              const allNodes = currentNodes.map((n: any) => n.id);
              const notExecutedNodes = allNodes.filter((nodeId: string) => !nodeStatuses[nodeId]);
              
                const notExecutedButValidNodes = notExecutedNodes.filter((nodeId: string) => {
                const node = currentNodes.find((n: any) => n.id === nodeId);
                if (!node) return true;
                
                const incomingEdges = currentEdges.filter((e: any) => e.target === nodeId);
                const isFromCondition = incomingEdges.some((e: any) => {
                  const sourceNode = currentNodes.find((n: any) => n.id === e.source);
                  return sourceNode && (sourceNode.type === 'condition' || sourceNode.type === 'agent__condition');
                });
                
                if (isFromCondition) {
                  for (const edge of incomingEdges) {
                    const sourceNode = currentNodes.find((n: any) => n.id === edge.source);
                    if (sourceNode && (sourceNode.type === 'condition' || sourceNode.type === 'agent__condition')) {
                      const conditionNodeId = edge.source;
                      if (nodeStatuses[conditionNodeId]) {
                        return false;
                      }
                    }
                  }
                }
                
                const isFromCategorizer = incomingEdges.some((e: any) => {
                  const sourceNode = currentNodes.find((n: any) => n.id === e.source);
                  return sourceNode && (sourceNode.type === 'agent__categorizer');
                });
                
                if (isFromCategorizer) {
                  for (const edge of incomingEdges) {
                    const sourceNode = currentNodes.find((n: any) => n.id === edge.source);
                    if (sourceNode && sourceNode.type === 'agent__categorizer') {
                      const categorizerNodeId = edge.source;
                      if (nodeStatuses[categorizerNodeId]) {
                        return false;
                      }
                    }
                  }
                }
                
                return true;
              });
              
              if (notExecutedButValidNodes.length > 0 && executedNodes.length > 0) {
                const notExecutedNodeNames = notExecutedButValidNodes.map(nodeId => {
                  const nodeName = getNodeName(nodeId);
                  const nodeType = getNodeType(nodeId);
                  const node = currentNodes.find((n: any) => n.id === nodeId);
                  
                  const isOutputNode = node && (node.type === 'output__chat' || node.type === 'output__keys');
                  
                  let nodeInfo = `  • ${nodeName} (${nodeType})`;
                  if (isOutputNode) {
                    nodeInfo += ' ⚠️ Output 노드가 실행되지 않았습니다.';
                  }
                  return nodeInfo;
                }).join('\n');
                
                errorDetails += `\n\n⚠️ 실행되지 않은 노드:\n${notExecutedNodeNames}`;
                
                const hasOutputNodeNotExecuted = notExecutedButValidNodes.some(nodeId => {
                  const node = currentNodes.find((n: any) => n.id === nodeId);
                  return node && (node.type === 'output__chat' || node.type === 'output__keys');
                });
                
                if (hasOutputNodeNotExecuted) {
                  solutionGuide = '\n\n💡 해결 방법:\n1. Output Chat 노드의 format_string 설정을 확인해주세요.\n2. format_string에 사용된 변수(예: {{selected_xxx}})가 올바르게 전달되는지 확인해주세요.\n3. Output 노드로 연결된 이전 노드들이 정상적으로 실행되었는지 확인해주세요.\n4. 빌더 로그를 확인하여 상세한 실행 정보를 확인해주세요.';
                } else if (!solutionGuide) {
                  solutionGuide = '\n\n💡 해결 방법:\n1. 실행되지 않은 노드의 연결 상태를 확인해주세요.\n2. 이전 노드들이 정상적으로 실행되었는지 확인해주세요.\n3. 노드의 입력값 설정을 확인해주세요.\n4. 빌더 로그를 확인하여 상세한 실행 정보를 확인해주세요.';
                }
              }

              let errorMessage = '응답을 받지 못했습니다.';
              if (actualError) {
                errorMessage = actualError;
              } else if (notExecutedButValidNodes.length > 0) {
                const hasOutputNode = notExecutedButValidNodes.some(nodeId => {
                  const node = currentNodes.find((n: any) => n.id === nodeId);
                  return node && (node.type === 'output__chat' || node.type === 'output__keys');
                });
                if (hasOutputNode) {
                  errorMessage = 'Output 노드가 실행되지 않아 응답을 생성할 수 없습니다.';
                } else {
                  errorMessage = '일부 노드가 실행되지 않아 응답을 생성할 수 없습니다.';
                }
              } else if (errorNodes.length > 0) {
                errorMessage = '노드 실행 중 오류가 발생했습니다.';
              }

              const fullErrorMessage = `❌ ${errorMessage}${errorDetails}${solutionGuide}`;

              const errorChatMessage: MessageFormat = {
                id: '',
                time: new Date().toLocaleString(),
                content: fullErrorMessage,
                type: ChatType.AI,
                regen: false,
                elapsedTime: Date.now() - startTime,
              };
              commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });

              setProgressMessage(actualError || '처리 완료');
              setIsChatLoading(false);
              queryResponseRef.current = true;
              timeoutRef.current = null;
            }, waitTime);

            return;
          }
        }
      } catch (parseError: any) {
      }

      const candidates = [finalResultContent, messageTemp.join(''), accumulatedResponse, streamingMessage].filter((value): value is string => {
        if (!value || value.length === 0) {
          return false;
        }

        return true;
      });

      const finalRaw = candidates.reduce<string>((best, current) => {
        if (!best) {
          return current;
        }
        return current.length > best.length ? current : best;
      }, '');

      const finalContent = formatAnswer(finalRaw);

      if (finalContent && finalContent.length > 0) {
        responseReceived = true;
      }

      const enhanceTokenValuesWithUnion = () => {
        const unionNodes = currentNodes.filter(node => node.type === 'union');
        if (unionNodes.length === 0) {
          return;
        }

        unionNodes.forEach(unionNode => {
          const unionData = unionNode.data as any;
          const unionFormatString = typeof unionData?.format_string === 'string' ? unionData.format_string : '';
          const unionOutputKeys = Array.isArray(unionData?.output_keys) ? unionData.output_keys : [];
          const unionInputKeys = Array.isArray(unionData?.input_keys) ? unionData.input_keys : [];

          if (!unionFormatString || unionOutputKeys.length === 0) {
            return;
          }

          const unionTokenValues: Record<string, string> = { ...formatTokenValues };

          unionInputKeys.forEach((inputKey: any) => {
            if (!inputKey) return;
            const aliasName = inputKey.name;
            const sourceKeytableId = inputKey.keytable_id;

            let sourceNodeName = '';
            if (aliasName) {
              const match = aliasName.match(/^(.+?)_content$/);
              if (match) {
                sourceNodeName = match[1];
              }
            }

            const possibleKeys = [
              sourceKeytableId,
              aliasName,
              sourceNodeName ? `${sourceNodeName}_content` : null,
              sourceNodeName ? `content_${sourceNodeName.replace(/^agent__/, '')}` : null,
            ].filter(Boolean) as string[];

            let sourceValue = '';
            for (const key of possibleKeys) {
              if (typeof formatTokenValues[key] === 'string' && formatTokenValues[key].length > 0) {
                sourceValue = formatTokenValues[key];
                break;
              }
            }

            if (!sourceValue && sourceNodeName) {
              const nodeBasedKey = `${sourceNodeName}_content`;
              if (typeof formatTokenValues[nodeBasedKey] === 'string' && formatTokenValues[nodeBasedKey].length > 0) {
                sourceValue = formatTokenValues[nodeBasedKey];
              }
            }

            if (!sourceValue) {
              const contentPattern = new RegExp(`^content_[a-f0-9]+$`);
              Object.entries(formatTokenValues).forEach(([key, value]) => {
                if (contentPattern.test(key) && typeof value === 'string' && value.length > 0) {
                  if (!sourceValue) {
                    sourceValue = value;
                  }
                }
              });
            }

            if (sourceValue && sourceValue.length > 0) {
              if (aliasName) {
                unionTokenValues[aliasName] = sourceValue;
              }
              if (sourceKeytableId) {
                unionTokenValues[sourceKeytableId] = sourceValue;
              }
            }
          });

          const unionResult = applyFormatString(unionFormatString, unionTokenValues);
          if (!unionResult || unionResult.trim().length === 0) {
            return;
          }

          unionOutputKeys.forEach((outputKey: any) => {
            if (!outputKey) return;
            if (outputKey.keytable_id) {
              formatTokenValues[outputKey.keytable_id] = unionResult;
            }
            if (outputKey.name) {
              formatTokenValues[outputKey.name] = unionResult;
            }
          });
        });
      };

      enhanceTokenValuesWithUnion();

      const cleanedFormatTokenValues: Record<string, string> = {};
      Object.entries(formatTokenValues).forEach(([key, value]) => {
        const excludePatterns = [
          'instruction',
          'detailed_response',
          'context_connection',
          'explicit_context',
          'conversation_continuity',
          'context_preservation',
          'no_duplication',
          'system_prompt',
          'context_instruction',
        ];
        const isExcluded = excludePatterns.some(pattern => key.toLowerCase().includes(pattern.toLowerCase()));
        if (!isExcluded && typeof value === 'string' && value.length > 0) {
          cleanedFormatTokenValues[key] = value;
        }
      });

      const codeNode = currentNodes.find((node: any) => node.type === 'agent__coder');
      const codeOutputKeys = codeNode?.data?.output_keys;
      const codeOutputKeytableId = Array.isArray(codeOutputKeys) && codeOutputKeys.length > 0 ? (codeOutputKeys[0] as any)?.keytable_id : undefined;
      const isReferencingCodeOutput = codeOutputKeytableId && outputFormatString.includes(`{{${codeOutputKeytableId}}}`);

      const toolNodes = currentNodes.filter((node: any) => node.type === 'tool');
      let isReferencingToolOutput = false;
      for (const toolNode of toolNodes) {
        const toolOutputKeys = toolNode?.data?.output_keys;
        if (Array.isArray(toolOutputKeys) && toolOutputKeys.length > 0) {
          for (const outputKey of toolOutputKeys) {
            const toolOutputKeytableId = (outputKey as any)?.keytable_id;
            if (toolOutputKeytableId && outputFormatString.includes(`{{${toolOutputKeytableId}}}`)) {
              isReferencingToolOutput = true;
              break;
            }
          }
          if (isReferencingToolOutput) break;
        }
      }

      const hasTokensInFormatString = outputFormatString && /\{\{\s*([^}]+)\s*\}\}/g.test(outputFormatString);

      const isOutputKeysNode = outputNode?.type === 'output__keys';

      if (outputNodeContent && 
          outputNodeContent.trim() !== '' && 
          !isOutputKeysNode &&
          !outputNodeContent.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) &&
          outputNodeContent !== '답변하기' && 
          outputNodeContent !== '하기') {
        const newMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: outputNodeContent.trim(),
          type: ChatType.AI,
          regen: false,
          elapsedTime: Date.now() - startTime,
        };
        commitAssistantMessage(newMessage, { targetIndex: targetAnswerIndex });
        setIsChatLoading(false);
        queryResponseRef.current = true;
        return;
      }

      if (!outputNodeContent && outputFormatString && outputFormatString.includes('selected_')) {
        const selectedKeys = Object.keys(formatTokenValues).filter(key => key.startsWith('selected_'));
        if (selectedKeys.length > 0) {
          const hasValueForFormat = selectedKeys.some(key => {
            const tokenPattern = new RegExp(`\\{\\{\\s*${key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\s*\\}\\}`, 'i');
            return tokenPattern.test(outputFormatString) && formatTokenValues[key];
          });
          
          if (hasValueForFormat) {
            const fallbackForFormatString = isReferencingCodeOutput || isReferencingToolOutput || !hasTokensInFormatString ? undefined : finalContent;
            const formattedContent = applyFormatString(outputFormatString, cleanedFormatTokenValues, fallbackForFormatString);
            
            if (formattedContent && formattedContent.trim() !== '' && formattedContent !== outputFormatString) {
              const newMessage: MessageFormat = {
                id: '',
                time: new Date().toLocaleString(),
                content: formattedContent.trim(),
                type: ChatType.AI,
                regen: false,
                elapsedTime: Date.now() - startTime,
              };
              commitAssistantMessage(newMessage, { targetIndex: targetAnswerIndex });
              setIsChatLoading(false);
              queryResponseRef.current = true;
              return;
            }
          }
        }
      }

      const fallbackForFormatString = isReferencingCodeOutput || isReferencingToolOutput || !hasTokensInFormatString ? undefined : finalContent;

      const formattedContent = isOutputKeysNode ? '' : applyFormatString(outputFormatString, cleanedFormatTokenValues, fallbackForFormatString);

      if (isReferencingCodeOutput && !cleanedFormatTokenValues[codeOutputKeytableId]) {
      }

      let contentToUse = '';

      if (isOutputKeysNode) {
        let existingJsonObject: Record<string, any> | null = null;
        if (outputNodeContent && outputNodeContent.trim() !== '') {
          try {
            const parsed = JSON.parse(outputNodeContent);
            if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
              existingJsonObject = parsed;
            }
          } catch (e) {
          }
        }

        const outputKeysSchema = Array.isArray(outputNode?.data?.output_keys) ? outputNode.data.output_keys : [];
        const outputKeysInputKeys = Array.isArray(outputNode?.data?.input_keys) ? outputNode.data.input_keys : [];
        const jsonObject: Record<string, string> = existingJsonObject ? { ...existingJsonObject } : {};

        const keysToProcess: Array<{ keytable_id: string; name: string }> = [];

        outputKeysSchema.forEach((outputKey: any) => {
          if (outputKey && outputKey.keytable_id) {
            keysToProcess.push({
              keytable_id: outputKey.keytable_id,
              name: outputKey.name || outputKey.keytable_id,
            });
          }
        });

        // output_keys 스키마에 키가 없으면 input_keys 사용 (하위 호환성)
        if (keysToProcess.length === 0) {
          outputKeysInputKeys.forEach((inputKey: any) => {
            if (inputKey && inputKey.keytable_id) {
              keysToProcess.push({
                keytable_id: inputKey.keytable_id,
                name: inputKey.name || inputKey.keytable_id,
              });
            }
          });
        }

        // 🔥 수집된 모든 키에 대해 값을 찾아서 JSON 객체에 추가 (기존 JSON 객체를 확장)
        keysToProcess.forEach((keyInfo: { keytable_id: string; name: string }) => {
          const keytableId = keyInfo.keytable_id;
          const keyName = keyInfo.name;
          // 🔥 Phoenix 로그처럼 전체 노드 이름을 포함한 키 이름 사용 (예: "input__basic_1_query", "agent__generator_1_content")
          // "L " 같은 prefix만 제거하고 나머지는 그대로 유지
          let jsonKey = keyName.replace(/^[A-Z]\s+/, '').trim();
          // 🔥 단순화하지 않고 원본 키 이름 유지 (Phoenix 로그 형식)
          // if (jsonKey.includes('_')) {
          //   const parts = jsonKey.split('_');
          //   jsonKey = parts[parts.length - 1];
          // }
          if (jsonObject[jsonKey] && jsonObject[jsonKey].trim() !== '') {
            return; // 이미 값이 있으면 건너뛰기
          }

          // formatTokenValues에서 값 찾기 (여러 패턴으로 시도)
          // 🔥 output__keys 노드에서는 원본 formatTokenValues를 우선 사용 (thinking 포함 전체 내용 표시)
          // 1. keytable_id로 직접 찾기 (원본 우선)
          // 2. 키 이름으로 찾기 (원본 우선)
          // 3. cleanedFormatTokenValues에서도 찾기 (fallback)
          let value = formatTokenValues[keytableId] || formatTokenValues[keyName] || cleanedFormatTokenValues[keytableId] || cleanedFormatTokenValues[keyName];

          // 🔥 keyTableList에서도 찾기 (input 노드의 query 값 등)
          if (!value) {
            const keyTableEntry = keyTableList.find(entry => entry.id === keytableId);
            if (keyTableEntry && keyTableEntry.value && keyTableEntry.value.trim() !== '') {
              value = keyTableEntry.value;
            }
          }

          // 🔥 keyTableList에서 키 이름으로도 찾기 (Input_basic_1_query 같은 형식)
          if (!value) {
            // 키 이름에서 실제 키 이름 추출 (예: "Input_basic_1_query" -> "query")
            const keyNameFromSchema = keyName.split('_').slice(-1)[0];
            const keyTableEntryByName = keyTableList.find(entry => {
              // entry.key가 "query"이고, entry.name이 keyName과 일치하거나 포함하는 경우
              return entry.key === keyNameFromSchema || entry.name === keyName || entry.name.includes(keyNameFromSchema) || keyName.includes(entry.key);
            });
            if (keyTableEntryByName && keyTableEntryByName.value && keyTableEntryByName.value.trim() !== '') {
              value = keyTableEntryByName.value;
            }
          }

          if (!value) {
            const nameWithoutPrefix = keyName.replace(/^[A-Z]\s+/, '').trim();
            value = formatTokenValues[nameWithoutPrefix] || cleanedFormatTokenValues[nameWithoutPrefix];

            if (!value && nameWithoutPrefix.includes('_')) {
              const lastPart = nameWithoutPrefix.split('_').slice(-1)[0];
              value = formatTokenValues[lastPart] || cleanedFormatTokenValues[lastPart];
            }

            if (!value && nameWithoutPrefix.includes('_')) {
              const parts = nameWithoutPrefix.split('_');
              if (parts.length >= 2) {
                const nodeNamePattern = parts.slice(0, -1).join('_');
                const keyPattern = parts[parts.length - 1];
                const nodeNameKeyPattern = `${nodeNamePattern}_${keyPattern}`;
                value = formatTokenValues[nodeNameKeyPattern] || cleanedFormatTokenValues[nodeNameKeyPattern];
              }
            }

            if (!value && outputNodeContent) {
              try {
                const parsed = JSON.parse(outputNodeContent);
                if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
                  if (parsed[jsonKey]) {
                    value = String(parsed[jsonKey]);
                  } else {
                    for (const key in parsed) {
                      if (key.includes(jsonKey) || jsonKey.includes(key)) {
                        value = String(parsed[key]);
                        break;
                      }
                    }
                  }
                }
              } catch (e) {
              }
            }

            if (!value) {
              const candidates = [finalResultContent, accumulatedResponse, streamingMessage].filter(Boolean);
              for (const candidate of candidates) {
                if (!candidate || typeof candidate !== 'string') continue;
                try {
                  const parsed = JSON.parse(candidate);
                  if (parsed && typeof parsed === 'object') {
                    if (parsed[jsonKey]) {
                      value = String(parsed[jsonKey]);
                      break;
                    }
                    for (const key in parsed) {
                      if (key.includes(jsonKey) || jsonKey.includes(key)) {
                        value = String(parsed[key]);
                        break;
                      }
                    }
                    if (value) break;
                  }
                } catch (e) {
                }
              }
            }
          }

          if (value && typeof value === 'string' && value.trim() !== '') {
            jsonObject[jsonKey] = value;
          }
        });

        const hasValidValues = Object.keys(jsonObject).some(key => jsonObject[key] && String(jsonObject[key]).trim() !== '');
        if (hasValidValues) {
          contentToUse = JSON.stringify(jsonObject, null, 2);
        } else {
          contentToUse = outputNodeContent || finalResultContent || accumulatedResponse || streamingMessage || '';

          if (contentToUse) {
            try {
              const parsed = JSON.parse(contentToUse);
              contentToUse = JSON.stringify(parsed, null, 2);
            } catch (e) {
              contentToUse = contentToUse;
            }
          }
        }
      } else if (isReferencingCodeOutput) {
        contentToUse = formattedContent || '';
        if (formattedContent && formattedContent.trim() !== '' && formattedContent.length > 20) {
          outputNodeContent = formattedContent;
          finalResultContent = formattedContent;
        }
      } else {
        const allContentKeys = Object.keys(formatTokenValues).filter(
          key => key.startsWith('content__') && formatTokenValues[key] && typeof formatTokenValues[key] === 'string' && formatTokenValues[key].trim() !== ''
        );

        if (allContentKeys.length > 0) {
          const bestContent = allContentKeys.reduce((best, key) => {
            const current = formatTokenValues[key];
            const bestValue = formatTokenValues[best];
            return current.length > bestValue.length ? key : best;
          }, allContentKeys[0]);

          const bestContentValue = formatTokenValues[bestContent];

          const filteredBestContent = bestContentValue;

          const isFormattedContentEmpty = !formattedContent || formattedContent.trim() === '';
          const isFormattedContentShort = formattedContent && formattedContent.length < 50;
          const isFormattedContentIncomplete =
            formattedContent &&
            (formattedContent.trim() === 'Plan: Execution:' ||
              formattedContent.trim() === 'Plan:\nExecution:' ||
              formattedContent.trim() === 'Plan:\n답변하기\nExecution:' ||
              formattedContent.trim() === 'Plan: 답변하기 Execution:' ||
              formattedContent.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) || // "Plan: Execution:" 또는 "Plan: 답변하기 Execution:" 패턴
              formattedContent.match(/^Plan:\s*답변하기\s*Execution:\s*$/m) || // "Plan: 답변하기 Execution:" 패턴
              (formattedContent.includes('Plan:') && formattedContent.includes('Execution:') && formattedContent.length < 100) || // Plan과 Execution만 있고 내용이 짧은 경우
              formattedContent.match(/Plan:\s*\n?\s*답변하기\s*\n?\s*Execution:\s*$/m) ||
              formattedContent.match(/Plan:\s*\n?\s*Execution:\s*$/m)); // "Plan:\nExecution:" 형식
          const isBestContentLonger = filteredBestContent && filteredBestContent.length > (formattedContent?.length || 0);
          const isBestContentMuchLonger = filteredBestContent && formattedContent && filteredBestContent.length > formattedContent.length * 2; // 2배 이상 긴 경우

          const finalResultFiltered = finalResultContent ? finalResultContent : '';
          const accumulatedFiltered = accumulatedResponse ? accumulatedResponse : '';
          const hasValidFinalResult =
            finalResultFiltered && finalResultFiltered.trim() !== '' && !finalResultFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) && finalResultFiltered.length > 50; // 최소 길이 체크
          const hasValidAccumulated =
            accumulatedFiltered && accumulatedFiltered.trim() !== '' && !accumulatedFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) && accumulatedFiltered.length > 50; // 최소 길이 체크

          if (isFormattedContentEmpty || isFormattedContentIncomplete || isBestContentMuchLonger || (isFormattedContentShort && isBestContentLonger)) {
            if (hasValidFinalResult) {
              contentToUse = finalResultFiltered;
            } else if (hasValidAccumulated) {
              contentToUse = accumulatedFiltered;
            } else if (isFormattedContentIncomplete && accumulatedResponse && accumulatedResponse.trim() !== '') {
              if (accumulatedResponse.includes('Execution:')) {
                const executionMatch = accumulatedResponse.match(/Execution:\s*\n?\s*([\s\S]+)$/);
                if (executionMatch && executionMatch[1] && executionMatch[1].trim().length > 0) {
                  contentToUse = executionMatch[1].trim();
                } else if (accumulatedResponse.length > 50 && !accumulatedResponse.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m)) {
                  contentToUse = accumulatedResponse;
                }
              } else if (accumulatedResponse.length > 50 && !accumulatedResponse.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m)) {
                contentToUse = accumulatedResponse;
              }
            } else if (filteredBestContent && filteredBestContent.trim() !== '' && filteredBestContent.length > 50) {
              contentToUse = filteredBestContent;
            } else {
              let fallbackContent = formattedContent || '';

              if (fallbackContent && fallbackContent.includes('Execution:')) {
                const executionMatch = fallbackContent.match(/Execution:\s*\n?\s*([\s\S]+)$/);
                if (executionMatch && executionMatch[1] && executionMatch[1].trim().length > 0) {
                  fallbackContent = executionMatch[1].trim();
                } else {
                  const altMatch = fallbackContent.split(/Execution:\s*/);
                  if (altMatch.length > 1 && altMatch[altMatch.length - 1].trim().length > 0) {
                    fallbackContent = altMatch[altMatch.length - 1].trim();
                  }
                }
              }

              if (fallbackContent && fallbackContent.includes('Execution:')) {
                const executionMatch = fallbackContent.match(/Execution:\s*\n?\s*([\s\S]+)$/);
                if (executionMatch && executionMatch[1] && executionMatch[1].trim().length > 0) {
                  fallbackContent = executionMatch[1].trim();
                } else {
                  const altMatch = fallbackContent.split(/Execution:\s*/);
                  if (altMatch.length > 1 && altMatch[altMatch.length - 1].trim().length > 0) {
                    fallbackContent = altMatch[altMatch.length - 1].trim();
                  }
                }
              }

              contentToUse = fallbackContent;
            }
          } else {
            let finalContent = formattedContent;

            if (finalContent && finalContent.includes('Execution:')) {
              const executionMatch = finalContent.match(/Execution:\s*\n?\s*([\s\S]+)$/);
              if (executionMatch && executionMatch[1] && executionMatch[1].trim().length > 0) {
                finalContent = executionMatch[1].trim();
              } else {
                const altMatch = finalContent.split(/Execution:\s*/);
                if (altMatch.length > 1 && altMatch[altMatch.length - 1].trim().length > 0) {
                  finalContent = altMatch[altMatch.length - 1].trim();
                }
              }
            }
            
            // // 🔥 여전히 "Plan:" 또는 "Execution:"이 포함되어 있으면 제거
            // if (finalContent && (finalContent.includes('Plan:') || finalContent.includes('Execution:'))) {
            //   // Plan: 이후의 내용 제거하고 Execution: 이후만 추출
            //   if (finalContent.includes('Execution:')) {
            //     const executionPart = finalContent.split('Execution:').pop();
            //     if (executionPart && executionPart.trim().length > 0) {
            //       finalContent = executionPart.trim();
            //     }
            //   }
            // }
            
            // if (!finalContent || finalContent.trim() === '하기' || finalContent.trim() === '답변하기' || finalContent.trim().length < 20) {
            //     // "하기"만 있거나 너무 짧으면 다른 소스에서 찾기
            //     const alternativeContent = finalResultContent || accumulatedResponse || streamingMessage || '';
            //     if (alternativeContent && alternativeContent.trim() !== '' && alternativeContent.length > 20) {
            //       const alternativeFiltered = alternativeContent;
            //       if (
            //         alternativeFiltered &&
            //         !alternativeFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) &&
            //         alternativeFiltered.length > 20 &&
            //         alternativeFiltered !== '하기' &&
            //         alternativeFiltered !== '답변하기'
            //       ) {
            //         finalContent = alternativeFiltered;
            //       } else {
            //         if (alternativeFiltered.includes('Execution:')) {
            //           const altExecutionMatch = alternativeFiltered.match(/Execution:\s*\n?\s*([\s\S]+)$/);
            //           if (altExecutionMatch && altExecutionMatch[1] && altExecutionMatch[1].trim().length > 0) {
            //             finalContent = altExecutionMatch[1].trim();
            //           } else {
            //             const altMatch = alternativeFiltered.split(/Execution:\s*/);
            //             if (altMatch.length > 1 && altMatch[altMatch.length - 1].trim().length > 0) {
            //               finalContent = altMatch[altMatch.length - 1].trim();
            //             }
            //           }
            //         }
            //       }
            //     }

            //     if (finalContent && finalContent.trim().length < 20) {
            //       const contentKeys = Object.keys(cleanedFormatTokenValues).filter(
            //         key => key.startsWith('content__') && cleanedFormatTokenValues[key] && cleanedFormatTokenValues[key].length > 20
            //       );
            //       if (contentKeys.length > 0) {
            //         const longestContent = contentKeys.reduce((longest, key) => {
            //           const current = cleanedFormatTokenValues[key];
            //           return current.length > (cleanedFormatTokenValues[longest]?.length || 0) ? key : longest;
            //         }, contentKeys[0]);
            //         const executorContent = cleanedFormatTokenValues[longestContent];
            //         if (executorContent && executorContent.trim().length > 20) {
            //           finalContent = executorContent;
            //         }
            //       }
            //     }
            //   }

            contentToUse = finalContent;
          }
        } else {
          const finalResultFiltered = finalResultContent ? finalResultContent : '';
          const accumulatedFiltered = accumulatedResponse ? accumulatedResponse : '';
          const hasValidFinalResult =
            finalResultFiltered && finalResultFiltered.trim() !== '' && !finalResultFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) && finalResultFiltered.length > 50; // 최소 길이 체크
          const hasValidAccumulated =
            accumulatedFiltered && accumulatedFiltered.trim() !== '' && !accumulatedFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) && accumulatedFiltered.length > 50; // 최소 길이 체크

          const isFormattedIncomplete =
            formattedContent &&
            (formattedContent.trim() === 'Plan: Execution:' ||
              formattedContent.trim() === 'Plan:\nExecution:' ||
              formattedContent.trim() === 'Plan:\n답변하기\nExecution:' ||
              formattedContent.trim() === 'Plan: 답변하기 Execution:' ||
              formattedContent.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) ||
              (formattedContent.includes('Plan:') && formattedContent.includes('Execution:') && formattedContent.length < 100));

          if (isFormattedIncomplete && (hasValidFinalResult || hasValidAccumulated)) {
            contentToUse = hasValidFinalResult ? finalResultFiltered : accumulatedFiltered;
          } else if (formattedContent && formattedContent.trim() !== '' && !isFormattedIncomplete) {
            contentToUse = formattedContent;
          } else {
            const rawContent = finalResultContent || accumulatedResponse || streamingMessage || '';
            contentToUse = rawContent || '';
          }
        }
      }

      const elapsedTime = Date.now() - startTime;
      const contentForLog = finalContent || contentToUse || accumulatedResponse || streamingMessage || '';

      const executedNodeIds = Object.keys(nodeStatusRef.current).filter(nodeId => nodeStatusRef.current[nodeId] === 'completed' || nodeStatusRef.current[nodeId] === 'running');

      const loggedTracingNodeNames = new Set(tracingMessages.map((trace: any) => trace.node_name || trace.nodeName || trace.node_id || trace.nodeId).filter(Boolean));

      const loggedNodeNames = new Set(streamLogsRef.current.map(log => log.node_name || log.node_id).filter(Boolean));

      executedNodeIds.forEach(nodeId => {
        const node = currentNodes.find(n => n.id === nodeId);
        if (!node) return;

        const nodeName = (node.data?.name as string) || nodeId;
        const nodeType = node.type || '';

        if (!loggedTracingNodeNames.has(nodeName)) {
          setTracingMessages(prev => {
            // 중복 체크
            const isDuplicate = prev.some(
              existing => (existing.node_name || existing.nodeName || existing.node_id || existing.nodeId) === nodeName && (existing.turn ?? currentTurn) === currentTurn
            );

            if (isDuplicate) {
              return prev;
            }

            return [
              ...prev,
              {
                nodeId: nodeName,
                nodeType: nodeType,
                callback: 'chain_end',
                progress: null,
                llm: null,
                tool_calls: null,
                tool_result: null,
                updates: {},
                final_result: null,
                status: nodeStatusRef.current[nodeId] || 'completed',
                log: {
                  node_name: nodeName,
                  node_type: nodeType,
                  node_id: nodeId,
                },
                turn: currentTurn,
              },
            ];
          });
        }

        if (!loggedNodeNames.has(nodeName)) {
          addStreamLog({
            node_name: nodeName,
            node_type: nodeType,
            node_id: nodeId,
            timestamp: new Date().toISOString(),
            request_time: new Date().toISOString(),
            updates: {},
            turn: currentTurn,
          });
        }
      });
    
      generateBuilderLogs(currentUserInput, contentForLog, elapsedTime, currentTurn);

      let retryCount = 0;
      const maxRetries = 5;
      const baseRetryDelay = 500;
      const retryDelay = baseRetryDelay;

      const tryUpdateNodeLogs = () => {
        setTimeout(
          () => {
            updateNodeLogs(currentTurn, retryCount);

            retryCount++;
            if (retryCount < maxRetries) {
              tryUpdateNodeLogs();
            } else {
            }
          },
          retryDelay * (retryCount + 1)
        );
      };

      tryUpdateNodeLogs();

      let finalContentToUse = contentToUse;
      if (contentToUse && contentToUse.trim() !== '') {
        const isContentIncomplete =
          contentToUse.trim() === 'Plan: Execution:' ||
          contentToUse.trim() === 'Plan:\nExecution:' ||
          contentToUse.trim() === 'Plan:\n답변하기\nExecution:' ||
          contentToUse.trim() === 'Plan: 답변하기 Execution:' ||
          contentToUse.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) ||
          (contentToUse.includes('Plan:') && contentToUse.includes('Execution:') && contentToUse.length < 100);

        if (isContentIncomplete) {
          const finalResultFiltered = finalResultContent ? finalResultContent : '';
          const accumulatedFiltered = accumulatedResponse ? accumulatedResponse : '';
          const hasValidFinalResult =
            finalResultFiltered && finalResultFiltered.trim() !== '' && !finalResultFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) && finalResultFiltered.length > 50;
          const hasValidAccumulated =
            accumulatedFiltered && accumulatedFiltered.trim() !== '' && !accumulatedFiltered.match(/^Plan:\s*(답변하기\s*)?Execution:\s*$/m) && accumulatedFiltered.length > 50;

          if (hasValidFinalResult) {
            finalContentToUse = finalResultFiltered;
          } else if (hasValidAccumulated) {
            finalContentToUse = accumulatedFiltered;
          }
        }
      }

      let messageContent = finalContentToUse;
      if (messageContent && messageContent.includes('Execution:')) {
        const executionMatch = messageContent.match(/Execution:\s*\n?\s*([\s\S]+)$/);
        if (executionMatch && executionMatch[1] && executionMatch[1].trim().length > 0) {
          messageContent = executionMatch[1].trim();
        } else {
          const altMatch = messageContent.split(/Execution:\s*/);
          if (altMatch.length > 1 && altMatch[altMatch.length - 1].trim().length > 0) {
            messageContent = altMatch[altMatch.length - 1].trim();
          }
        }
      }
      
      if (messageContent && messageContent.includes('Plan:')) {
        if (messageContent.includes('Execution:')) {
          const executionPart = messageContent.split('Execution:').pop();
          if (executionPart && executionPart.trim().length > 0) {
            messageContent = executionPart.trim();
          }
        } else {
          messageContent = messageContent.replace(/Plan:\s*[\s\S]*/, '').trim();
        }
      }
      
      if (messageContent && messageContent.trim() !== '') {
        const newMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: messageContent,
          type: ChatType.AI,
          regen: false,
          elapsedTime,
        };
        commitAssistantMessage(newMessage, { targetIndex: targetAnswerIndex });

        // interval과 timeout 정리
        if (checkIntervalRef.current) {
          clearInterval(checkIntervalRef.current);
          checkIntervalRef.current = null;
        }
        if (timeoutRef.current) {
          clearTimeout(timeoutRef.current);
          timeoutRef.current = null;
        }

        currentNodes.forEach(node => {
          const innerData = node.data?.innerData ?? {};
          if (innerData.isRunning && !innerData.isCompleted && !innerData.isError && !innerData.hasError) {
            pendingNodeUpdatesRef.current.set(node.id, {
              isRun: true,
              isRunning: false,
              isCompleted: true,
              isError: false,
            });
          }
        });

        if (nodeUpdateTimeoutRef.current) {
          clearTimeout(nodeUpdateTimeoutRef.current);
        }
        nodeUpdateTimeoutRef.current = setTimeout(() => {
          const updates = pendingNodeUpdatesRef.current;
          if (updates.size === 0) {
            return;
          }

          setNodes(prev => {
            let hasChanges = false;
            const updatedNodes = prev.map(node => {
              const update = updates.get(node.id);
              if (!update) {
                return node;
              }

              const innerData = node.data?.innerData ?? {};
              if (
                innerData.isRunning === update.isRunning &&
                innerData.isCompleted === update.isCompleted &&
                innerData.isError === update.isError &&
                innerData.isRun === update.isRun
              ) {
                return node;
              }

              hasChanges = true;
              return {
                ...node,
                data: {
                  ...node.data,
                  innerData: {
                    ...innerData,
                    isRun: update.isRun,
                    isDone: true,
                    isRunning: update.isRunning,
                    isCompleted: update.isCompleted,
                    isError: update.isError,
                    hasError: update.isError,
                  },
                },
              };
            });

            if (!hasChanges) {
              return prev;
            }

            pendingNodeUpdatesRef.current.clear();
            return updatedNodes;
          });
        }, 10);

        setIsChatLoading(false);
        setProgressMessage('답변 완료.');
        queryResponseRef.current = true;
        return;
      }

      const elapsedTimeForError = Date.now() - startTime;

      const nodeStatusesForError = nodeStatusRef.current;
      const executedNodesForError = Object.keys(nodeStatusesForError).filter(nodeId => nodeStatusesForError[nodeId] === 'completed' || nodeStatusesForError[nodeId] === 'running');
      const errorNodesForError = Object.keys(nodeStatusesForError).filter(nodeId => nodeStatusesForError[nodeId] === 'error');

      const getNodeName = (nodeId: string): string => {
        const node = currentNodes.find((n: any) => n.id === nodeId);
        if (node && node.data && node.data.name) {
          return String(node.data.name);
        }
        return nodeId;
      };

      const getNodeType = (nodeId: string): string => {
        const node = currentNodes.find((n: any) => n.id === nodeId);
        return node?.type || '알 수 없음';
      };

      let errorDetails = '';
      let solutionGuide = '';

      if (actualError) {
        errorDetails = `\n\n📋 에러 내용:\n${actualError}`;
      }

      if (executedNodesForError.length > 0) {
        const executedNodeNames = executedNodesForError.map(nodeId => {
          const nodeName = getNodeName(nodeId);
          const nodeType = getNodeType(nodeId);
          return `  • ${nodeName} (${nodeType})`;
        }).join('\n');
        errorDetails += `\n\n✅ 실행된 노드:\n${executedNodeNames}`;
      }

      if (errorNodesForError.length > 0) {
        const errorNodeNames = errorNodesForError.map(nodeId => {
          const nodeName = getNodeName(nodeId);
          const nodeType = getNodeType(nodeId);
          return `  • ${nodeName} (${nodeType})`;
        }).join('\n');
        errorDetails += `\n\n❌ 에러 발생 노드:\n${errorNodeNames}`;
        solutionGuide = '\n\n💡 해결 방법:\n1. 에러 발생 노드를 확인하고 설정을 점검해주세요.\n2. 노드의 입력값과 연결 상태를 확인해주세요.\n3. 필요시 노드를 삭제하고 다시 추가해보세요.';
      }

      const allNodesForError = currentNodes.map((n: any) => n.id);
      const notExecutedNodesForError = allNodesForError.filter((nodeId: string) => !nodeStatusesForError[nodeId]);
      
      const notExecutedButValidNodesForError = notExecutedNodesForError.filter((nodeId: string) => {
        const node = currentNodes.find((n: any) => n.id === nodeId);
        if (!node) return true;
        
        const incomingEdges = currentEdges.filter((e: any) => e.target === nodeId);
        const isFromCondition = incomingEdges.some((e: any) => {
          const sourceNode = currentNodes.find((n: any) => n.id === e.source);
          return sourceNode && (sourceNode.type === 'condition' || sourceNode.type === 'agent__condition');
        });
        
        if (isFromCondition) {
          for (const edge of incomingEdges) {
            const sourceNode = currentNodes.find((n: any) => n.id === edge.source);
            if (sourceNode && (sourceNode.type === 'condition' || sourceNode.type === 'agent__condition')) {
              const conditionNodeId = edge.source;
              if (nodeStatusesForError[conditionNodeId]) {
                return false;
              }
            }
          }
        }
        
        const isFromCategorizer = incomingEdges.some((e: any) => {
          const sourceNode = currentNodes.find((n: any) => n.id === e.source);
          return sourceNode && (sourceNode.type === 'agent__categorizer');
        });
        
        if (isFromCategorizer) {
          for (const edge of incomingEdges) {
            const sourceNode = currentNodes.find((n: any) => n.id === edge.source);
            if (sourceNode && sourceNode.type === 'agent__categorizer') {
              const categorizerNodeId = edge.source;
              if (nodeStatusesForError[categorizerNodeId]) {
                return false;
              }
            }
          }
        }
        
        return true;
      });
      
      if (notExecutedButValidNodesForError.length > 0 && executedNodesForError.length > 0) {
        const notExecutedNodeNames = notExecutedButValidNodesForError.map(nodeId => {
          const nodeName = getNodeName(nodeId);
          const nodeType = getNodeType(nodeId);
          const node = currentNodes.find((n: any) => n.id === nodeId);
          
          // Output 노드인지 확인
          const isOutputNode = node && (node.type === 'output__chat' || node.type === 'output__keys');
          
          let nodeInfo = `  • ${nodeName} (${nodeType})`;
          if (isOutputNode) {
            nodeInfo += ' ⚠️ Output 노드가 실행되지 않았습니다.';
          }
          return nodeInfo;
        }).join('\n');
        
        errorDetails += `\n\n⚠️ 실행되지 않은 노드:\n${notExecutedNodeNames}`;
        
        const hasOutputNodeNotExecuted = notExecutedButValidNodesForError.some(nodeId => {
          const node = currentNodes.find((n: any) => n.id === nodeId);
          return node && (node.type === 'output__chat' || node.type === 'output__keys');
        });
        
        if (hasOutputNodeNotExecuted) {
          solutionGuide = '\n\n💡 해결 방법:\n1. Output Chat 노드의 format_string 설정을 확인해주세요.\n2. format_string에 사용된 변수(예: {{selected_xxx}})가 올바르게 전달되는지 확인해주세요.\n3. Output 노드로 연결된 이전 노드들이 정상적으로 실행되었는지 확인해주세요.\n4. 빌더 로그를 확인하여 상세한 실행 정보를 확인해주세요.';
        } else if (!solutionGuide) {
          solutionGuide = '\n\n💡 해결 방법:\n1. 실행되지 않은 노드의 연결 상태를 확인해주세요.\n2. 이전 노드들이 정상적으로 실행되었는지 확인해주세요.\n3. 노드의 입력값 설정을 확인해주세요.\n4. 빌더 로그를 확인하여 상세한 실행 정보를 확인해주세요.';
        }
      }

        // 기본 에러 메시지
      let errorMessage = '응답을 받지 못했습니다.';
      if (actualError) {
        errorMessage = actualError;
      } else if (notExecutedButValidNodesForError.length > 0) {
        const hasOutputNode = notExecutedButValidNodesForError.some(nodeId => {
          const node = currentNodes.find((n: any) => n.id === nodeId);
          return node && (node.type === 'output__chat' || node.type === 'output__keys');
        });
        if (hasOutputNode) {
          errorMessage = 'Output 노드가 실행되지 않아 응답을 생성할 수 없습니다.';
        } else {
          errorMessage = '일부 노드가 실행되지 않아 응답을 생성할 수 없습니다.';
        }
      } else if (errorNodesForError.length > 0) {
        errorMessage = '노드 실행 중 오류가 발생했습니다.';
      }

      const fullErrorMessage = `❌ ${errorMessage}${errorDetails}${solutionGuide}`;

      const errorChatMessage: MessageFormat = {
        id: '',
        time: new Date().toLocaleString(),
        content: fullErrorMessage,
        type: ChatType.AI,
        regen: false,
        elapsedTime: elapsedTimeForError,
      };
      commitAssistantMessage(errorChatMessage, { targetIndex: targetAnswerIndex });

      const executedNodeIdsForError = Object.keys(nodeStatusesForError).filter(
        nodeId => nodeStatusesForError[nodeId] === 'completed' || nodeStatusesForError[nodeId] === 'running' || nodeStatusesForError[nodeId] === 'error'
      );

      const loggedNodeNamesForError = new Set(streamLogsRef.current.map(log => log.node_name || log.node_id).filter(Boolean));

      executedNodeIdsForError.forEach(nodeId => {
        const node = currentNodes.find(n => n.id === nodeId);
        if (!node) return;

        const nodeName = (node.data?.name as string) || nodeId;
        const nodeType = node.type || '';

        if (!loggedNodeNamesForError.has(nodeName)) {
          addStreamLog({
            node_name: nodeName,
            node_type: nodeType,
            node_id: nodeId,
            timestamp: new Date().toISOString(),
            request_time: new Date().toISOString(),
            updates: {},
            turn: currentTurn,
          });
        }
      });

      generateBuilderLogs(currentUserInput, fullErrorMessage, elapsedTimeForError, currentTurn);

      setTimeout(() => {
        updateNodeLogs(currentTurn);
      }, 500);

      if (checkIntervalRef.current) {
        clearInterval(checkIntervalRef.current);
        checkIntervalRef.current = null;
      }
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current as any);
        timeoutRef.current = null;
      }
      if (tracingUpdateTimeoutRef.current) {
        clearTimeout(tracingUpdateTimeoutRef.current);
        tracingUpdateTimeoutRef.current = null;
      }
      if (pendingTracingUpdatesRef.current.length > 0) {
        setTracingMessages(prev => {
          const filteredUpdates = pendingTracingUpdatesRef.current.filter(update => {
            const isDuplicate = prev.some(existing => existing.nodeId === update.nodeId && (existing.turn ?? currentTurn) === currentTurn && existing.callback === update.callback);
            return !isDuplicate;
          });
          return filteredUpdates.length > 0 ? [...prev, ...filteredUpdates] : prev;
        });
        pendingTracingUpdatesRef.current = [];
      }

      setIsChatLoading(false);
      setProgressMessage(actualError || '처리 완료');
      queryResponseRef.current = true;
    } catch (error: any) {
      if (checkIntervalRef.current) {
        clearInterval(checkIntervalRef.current);
        checkIntervalRef.current = null;
      }
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
        timeoutRef.current = null;
      }
      if (tracingUpdateTimeoutRef.current) {
        clearTimeout(tracingUpdateTimeoutRef.current);
        tracingUpdateTimeoutRef.current = null;
      }
      if (tracingUpdateResetInterval.current) {
        clearInterval(tracingUpdateResetInterval.current);
        tracingUpdateResetInterval.current = null;
      }
      pendingTracingUpdatesRef.current = [];
      tracingUpdateCountRef.current = 0;

      setIsChatLoading(false);
      const fallbackRaw = finalResultContent || (typeof streamingMessage === 'string' ? streamingMessage : '');
      const fallback = formatAnswer(fallbackRaw);

      const hasTokensInFormatString = outputFormatString && /\{\{\s*([^}]+)\s*\}\}/g.test(outputFormatString);
      const fallbackForError = hasTokensInFormatString ? fallback : undefined;

      const formattedFallback = applyFormatString(outputFormatString, formatTokenValues, fallbackForError);

      const contentToUseForError = formattedFallback && formattedFallback.trim() !== '' ? formattedFallback : fallback;

      if (contentToUseForError && contentToUseForError.trim() !== '') {
        const newMessage: MessageFormat = {
          id: '',
          time: new Date().toLocaleString(),
          content: contentToUseForError,
          type: ChatType.AI,
          regen: false,
          elapsedTime: Date.now() - startTime,
        };
        commitAssistantMessage(newMessage, { targetIndex: targetAnswerIndex });
        setProgressMessage('답변 완료.');
      } else {
        setProgressMessage('오류 발생.');
      }
      queryResponseRef.current = true;
    }
  };

  const resetChatState = () => {
    if (checkIntervalRef.current) {
      clearInterval(checkIntervalRef.current);
      checkIntervalRef.current = null;
    }
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }

    setIsChatLoading(false);
    setStreamingMessage('');
    setProgressMessage('');
    conversationTurnRef.current = 0;
    nodeStatusRef.current = {};
    nodeOrderRef.current = [];
  };

  return {
    isChatLoading,
    handleChatTest,
    resetChatState,
  };
};