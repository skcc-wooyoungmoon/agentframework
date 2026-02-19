import { useToast } from '@/hooks/common/toast/useToast';
import { forwardRef, useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react';
import './StreamingDisplay.css';

const StreamingDisplayComponent = forwardRef<
  any,
  {
    onStatusChange?: any;
    showTimestamp?: boolean;
    showTokenInfo?: boolean;
    maxContentLength?: number;
    className?: string;
  }
>(({ onStatusChange, showTimestamp = true, showTokenInfo = true, maxContentLength = 80, className = '' }, ref) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const updatesListRef = useRef<HTMLDivElement>(null);
  const [status, setStatus] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [tokenInfo, setTokenInfo] = useState('');
  const [showRaw, setShowRaw] = useState(false);
  const [expandedUpdates, setExpandedUpdates] = useState<Record<string, boolean>>({});
  const [copiedStates, setCopiedStates] = useState<Record<string, boolean>>({});

  const { toast } = useToast();

  // 상태 관리
  const startTimeRef = useRef<number | null>(null);
  const [startTime, setStartTime] = useState<number | null>(null);
  const [updates, setUpdates] = useState<any[]>([]);
  const [rawBuffer, setRawBuffer] = useState('');
  const jsonBuffer = useRef(''); // JSON 청크 버퍼링용
  const lineBuffer = useRef(''); // 라인 버퍼링용 (SSE 처리)

  // startTime 상태와 ref 동기화
  useEffect(() => {
    startTimeRef.current = startTime;
  }, [startTime]);

  // 경과 시간 계산
  const getElapsedTime = useCallback(() => {
    const currentStartTime = startTimeRef.current;
    if (!currentStartTime) {
      return '0.00';
    }
    const elapsed = ((Date.now() - currentStartTime) / 1000).toFixed(2);
    return elapsed;
  }, []);

  // 업데이트 추가 함수
  const addUpdate = useCallback(
    (type: string, content: any, elapsedTime: string | null = null) => {
      const timestamp = elapsedTime !== null ? elapsedTime : getElapsedTime();
      const newUpdate = {
        id: Date.now() + Math.random(),
        type,
        content,
        timestamp,
        createdAt: new Date(),
      };

      setUpdates(prev => {
        const newUpdates = [...prev, newUpdate];
        prevLength: (prev.length,
          console.log('🔥 setUpdates 실행됨:', {
            newLength: newUpdates.length,
            lastUpdate: newUpdates[newUpdates.length - 1],
          }));
        return newUpdates;
      });
      return newUpdate;
    },
    [getElapsedTime]
  );

  // 토글 함수들
  const toggleView = useCallback(() => {
    setShowRaw(prev => !prev);
  }, []);

  const toggleContent = useCallback((updateId: string) => {
    setExpandedUpdates(prev => ({
      ...prev,
      [updateId]: !prev[updateId],
    }));
  }, []);

  // 복사 기능 처리
  const handleCopy = useCallback((text: string, updateId: string) => {
    navigator.clipboard.writeText(text);
    setCopiedStates(prev => ({
      ...prev,
      [updateId]: true,
    }));

    toast.success('복사가 완료되었습니다.');

    // 2초 후 원복
    setTimeout(() => {
      setCopiedStates(prev => ({
        ...prev,
        [updateId]: false,
      }));
    }, 2000);
  }, []);

  // JSON 완성 여부 확인 함수
  const isCompleteJSON = useCallback((str: string): boolean => {
    if (!str.trim()) {
      return false;
    }
    try {
      JSON.parse(str);
      return true;
    } catch (error) {
      return false;
    }
  }, []);

  // JSON 데이터 처리 함수 (먼저 정의)
  const processJSONData = useCallback(
    (dataLines: string[]) => {
      // Python boolean/null 값 정규화
      const preprocessedValue = dataLines.map(line => line.replace(/False/gi, 'false').replace(/True/gi, 'true').replace(/None/gi, 'null'));

      for (const jsonLine of preprocessedValue) {
        if (jsonLine.trim()) {
          const currentBuffer = jsonLine;

          // 완성된 JSON인지 확인
          if (isCompleteJSON(currentBuffer)) {
            jsonBuffer.current = '';
            try {
              const jsonValue = JSON.parse(currentBuffer);

              if (jsonValue.progress !== undefined) {
                const elapsedTime = getElapsedTime();
                addUpdate('progress', `${jsonValue.progress}`, elapsedTime);
                setStatus(`[progress] ${jsonValue.progress}`);
              }

              if (jsonValue.llm && jsonValue.llm.content !== undefined) {
                const currentTime = getElapsedTime();
                const nodeName = jsonValue.node_name;

                setUpdates(prevUpdates => {
                  // 가장 최근 업데이트가 같은 node_name의 llm-content-streaming인지 확인
                  const lastUpdate = prevUpdates[prevUpdates.length - 1];
                  const canAppendToLast = lastUpdate && lastUpdate.type === 'llm-content-streaming' && lastUpdate.nodeName === nodeName;

                  if (canAppendToLast) {
                    // 가장 최근 블록에 내용 추가 (시간은 유지)
                    const newUpdates = [...prevUpdates];
                    const newBuffer = newUpdates[newUpdates.length - 1].content + jsonValue.llm.content;
                    newUpdates[newUpdates.length - 1] = {
                      ...newUpdates[newUpdates.length - 1],
                      content: newBuffer,
                      // timestamp는 유지 (첫 번째 chunk의 시간을 유지)
                    };
                    return newUpdates;
                  } else {
                    // 새 업데이트 추가 (새로운 node_name이거나 다른 타입이 중간에 있었음)
                    return [
                      ...prevUpdates,
                      {
                        id: 'llm-content-' + Date.now() + (nodeName ? '-' + nodeName : ''),
                        type: 'llm-content-streaming',
                        content: jsonValue.llm.content,
                        timestamp: currentTime,
                        createdAt: new Date(),
                        nodeName: nodeName,
                      },
                    ];
                  }
                });
              }

              if (jsonValue.final_result !== undefined) {
                const isJsonType = typeof jsonValue.final_result === 'object' && jsonValue.final_result !== null;
                const resultStr = isJsonType ? JSON.stringify(jsonValue.final_result) : String(jsonValue.final_result);

                if (isJsonType) {
                  const needsToggle = resultStr.length > maxContentLength;
                  addUpdate('final-result', {
                    content: resultStr,
                    isJson: true,
                    needsToggle,
                    shortContent: needsToggle ? resultStr.substring(0, maxContentLength) : null,
                  });
                } else {
                  const currentTime = getElapsedTime();

                  setUpdates(prevUpdates => {
                    const existingIndex = prevUpdates.findIndex(u => u.type === 'final-result-streaming');
                    if (existingIndex >= 0) {
                      // 기존 업데이트 수정 (시간은 유지)
                      const newUpdates = [...prevUpdates];
                      const newBuffer = newUpdates[existingIndex].content + jsonValue.final_result;
                      newUpdates[existingIndex] = {
                        ...newUpdates[existingIndex],
                        content: newBuffer,
                        // timestamp는 유지 (첫 번째 chunk의 시간을 유지)
                      };
                      return newUpdates;
                    } else {
                      // 새 업데이트 추가 (첫 번째 chunk이므로 현재 시간 사용)
                      return [
                        ...prevUpdates,
                        {
                          id: 'final-result-' + Date.now(),
                          type: 'final-result-streaming',
                          content: jsonValue.final_result,
                          timestamp: currentTime,
                          createdAt: new Date(),
                        },
                      ];
                    }
                  });
                }
              }

              if (jsonValue.tool_calls !== undefined) {
                let toolCallsInfo = '';
                if (jsonValue.tool_calls.tool_calls && Array.isArray(jsonValue.tool_calls.tool_calls)) {
                  jsonValue.tool_calls.tool_calls.forEach((toolCall: any, index: number) => {
                    if (toolCall.function) {
                      toolCallsInfo += `Tool ${index + 1}: ${toolCall.function.name}(${toolCall.function.arguments})\n`;
                    }
                  });
                } else if (jsonValue.tool_calls.content && jsonValue.tool_calls.additional_kwargs && jsonValue.tool_calls.additional_kwargs.tool_calls) {
                  jsonValue.tool_calls.additional_kwargs.tool_calls.forEach((toolCall: any, index: number) => {
                    if (toolCall.function) {
                      toolCallsInfo += `Tool ${index + 1}: ${toolCall.function.name}(${toolCall.function.arguments})\n`;
                    }
                  });
                }

                const content = toolCallsInfo || JSON.stringify(jsonValue.tool_calls);
                const needsToggle = content.length > maxContentLength * 1.5;

                addUpdate('tool-calls', {
                  content,
                  needsToggle,
                  shortContent: needsToggle ? content.substring(0, maxContentLength) : null,
                });
              }

              if (jsonValue.tool !== undefined) {
                const toolInfo =
                  `Tool: ${jsonValue.tool.name || 'Unknown'}\n` + `Status: ${jsonValue.tool.status || 'N/A'}\n` + `Content: ${jsonValue.tool.content || 'No content'}`;

                const needsToggle = toolInfo.length > maxContentLength;

                addUpdate('tool-result', {
                  content: toolInfo,
                  needsToggle,
                  shortContent: needsToggle ? toolInfo.substring(0, maxContentLength) : null,
                  status: jsonValue.tool.status,
                  toolName: jsonValue.tool.name,
                });
              }

              if (jsonValue.updates !== undefined) {
                const formatUpdatesContent = (data: any) => {
                  if (!data) return 'No updates';

                  try {
                    const additional_kwargs = data.additional_kwargs || data.updates?.additional_kwargs;

                    if (additional_kwargs) {
                      let formattedContent = '';

                      if (data.node_name || data.updates?.node_name) {
                        const nodeName = data.node_name || data.updates.node_name;
                        formattedContent += `🔧 Node: ${nodeName}\n\n`;
                      }

                      const contextKey = Object.keys(additional_kwargs).find(key => key.startsWith('context_'));
                      if (contextKey && additional_kwargs[contextKey]) {
                        formattedContent += '📄 Context Information:\n';
                        const contextText = additional_kwargs[contextKey];

                        const docs = contextText.split('[doc_').slice(1);
                        docs.forEach((doc: any, index: number) => {
                          const docNumber = index + 1;
                          const docContent = doc.split('\n').slice(0, 3).join('\n');
                          formattedContent += `  └ Document ${docNumber}: ${docContent.trim().substring(0, 100)}...\n`;
                        });
                        formattedContent += '\n';
                      }

                      const docsKey = Object.keys(additional_kwargs).find(key => key.startsWith('docs_'));
                      if (docsKey && additional_kwargs[docsKey]) {
                        formattedContent += '📋 Retrieved Documents:\n';
                        const docs = additional_kwargs[docsKey];
                        if (Array.isArray(docs)) {
                          docs.forEach((doc: any, index: number) => {
                            const fileName = doc.metadata?.file_name || 'Unknown file';
                            const page = doc.metadata?.page || 'N/A';
                            const score = doc.score ? ` (score: ${doc.score.toFixed(3)})` : '';
                            formattedContent += `  └ ${index + 1}. ${fileName} (page ${page})${score}\n`;
                            if (doc.content) {
                              const preview = doc.content.substring(0, 80).replace(/\n/g, ' ');
                              formattedContent += `     Preview: ${preview}...\n`;
                            }
                          });
                        }
                        formattedContent += '\n';
                      }

                      if (additional_kwargs.global_ref) {
                        formattedContent += '🔗 Global References:\n';
                        const globalRef = additional_kwargs.global_ref;
                        Object.entries(globalRef).forEach(([key, value]) => {
                          formattedContent += `  └ ${key}: ${value}\n`;
                        });
                      }

                      return formattedContent.trim() || JSON.stringify(data);
                    }

                    return JSON.stringify(data, null, 2);
                  } catch (error) {
                    console.warn('Updates 포맷팅 실패:', error);
                    return JSON.stringify(data);
                  }
                };

                try {
                  const formattedContent = formatUpdatesContent(jsonValue.updates);
                  const needsToggle = formattedContent.length > maxContentLength;
                  const isFormatted = !!(jsonValue.updates.additional_kwargs || jsonValue.updates.updates?.additional_kwargs);
                  addUpdate('updates', {
                    content: formattedContent,
                    rawContent: JSON.stringify(jsonValue.updates),
                    needsToggle,
                    shortContent: needsToggle ? formattedContent.substring(0, maxContentLength) : null,
                    isFormatted: isFormatted,
                  });
                } catch (error) {
                  console.error('❌ Updates 처리 실패:', error, jsonValue.updates);
                  addUpdate('updates', {
                    content: JSON.stringify(jsonValue.updates, null, 2),
                    rawContent: JSON.stringify(jsonValue.updates),
                    needsToggle: true,
                    shortContent: JSON.stringify(jsonValue.updates).substring(0, maxContentLength),
                    isFormatted: false,
                  });
                }
              }
            } catch (parseError) {
              // console.warn('❌ JSON 파싱 실패:', {
              //   error: parseError,
              //   bufferLength: currentBuffer.length,
              //   bufferStart: currentBuffer.substring(0, 200),
              // });
            }
          } else { 
            jsonBuffer.current = currentBuffer;
          }
        }
      }
    },
    [getElapsedTime, addUpdate, setStatus, maxContentLength, isCompleteJSON]
  );

  // SSE 라인 처리 함수 (processJSONData 이후에 정의)
  const processCompleteSSELines = useCallback(
    (lines: string[]) => {
      try {
        const dataLines: string[] = [];
        // let isDataEvent = false;

        for (const line of lines) {
          if (line.startsWith('event: metadata')) {
            const elapsedTime = getElapsedTime();
            addUpdate('progress', 'Processing...', elapsedTime);
            setStatus(`[progress] Processing...`);
            continue;
          }

          if (line.startsWith('event: end')) {
            const elapsedTime = getElapsedTime();
            addUpdate('progress', 'Complete.', elapsedTime);
            setStatus(`[progress] Complete.`);
            continue;
          }

          if (line.startsWith('event: error')) {
            const elapsedTime = getElapsedTime();
            addUpdate('error', 'Processing Error', elapsedTime);
            setStatus(`Call failed [Total time: ${elapsedTime}s]`);
            setIsLoading(false);
            continue;
          }

          if (line.startsWith('event: data')) {
            // isDataEvent = true;
            continue;
          } else if (line.startsWith('event:')) {
            // isDataEvent = false;
            continue;
          } else if (line.startsWith('data:')) {
            // if (isDataEvent) {
              const jsonData = line.slice(5).trim(); // 'data:' 제거
              if (jsonData) {
                dataLines.push(jsonData);
              }
            // }
          } else if (line.startsWith(':')) {
            // ping 또는 comment 라인은 무시
            continue;
          } else if (line.trim() === '') {
            // 빈 라인은 무시
            continue;
          }
        }

        // 데이터 라인들이 있으면 JSON 처리 (에러 발생해도 스트림은 계속 유지)
        if (dataLines.length > 0) {
          try {
            processJSONData(dataLines);
          } catch (error) {
            console.error('❌ processJSONData 에러 (스트림 계속 유지):', error);
            // 에러가 발생해도 스트림은 계속 유지
          }
        }
      } catch (error) {
        console.error('❌ processCompleteSSELines 에러 (스트림 계속 유지):', error);
        // 에러가 발생해도 스트림은 계속 유지
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [getElapsedTime, addUpdate, setStatus, setIsLoading, processJSONData]
  );

  // 공개 API
  const streamingAPI = useMemo(
    () => ({
      startStreaming: (providedStartTime?: number) => {
        const newStartTime = providedStartTime || Date.now();
        setStartTime(newStartTime);
        setUpdates([]);
        setRawBuffer('');
        jsonBuffer.current = '';
        lineBuffer.current = '';
        setStatus('Calling...');
        setIsLoading(true);
        setTokenInfo('');
        setShowRaw(false);
      },

      reset: () => {
        setUpdates([]);
        setRawBuffer('');
        jsonBuffer.current = '';
        lineBuffer.current = '';
        setStatus('');
        setIsLoading(false);
        setTokenInfo('');
        setStartTime(null);
      },

      addProgress: (progress: string) => {
        const elapsedTime = getElapsedTime();
        addUpdate('progress', `${progress}`, elapsedTime);
        setStatus(`[progress] ${progress}`);
      },

      addLLMContent: (content: any, nodeName?: string) => {
        const currentTime = getElapsedTime();

        // LLM content 업데이트 찾기 또는 생성
        setUpdates(prevUpdates => {
          // 가장 최근 업데이트가 같은 node_name의 llm-content-streaming인지 확인
          const lastUpdate = prevUpdates[prevUpdates.length - 1];
          const canAppendToLast = lastUpdate && lastUpdate.type === 'llm-content-streaming' && lastUpdate.nodeName === nodeName;

          if (canAppendToLast) {
            // 가장 최근 블록에 내용 추가 (시간은 유지)
            const newUpdates = [...prevUpdates];
            const newBuffer = newUpdates[newUpdates.length - 1].content + content;
            newUpdates[newUpdates.length - 1] = {
              ...newUpdates[newUpdates.length - 1],
              content: newBuffer,
              // timestamp는 유지 (첫 번째 chunk의 시간을 유지)
            };
            return newUpdates;
          } else {
            // 새 업데이트 추가 (새로운 node_name이거나 다른 타입이 중간에 있었음)
            return [
              ...prevUpdates,
              {
                id: 'llm-content-' + Date.now() + (nodeName ? '-' + nodeName : ''),
                type: 'llm-content-streaming',
                content: content,
                timestamp: currentTime,
                createdAt: new Date(),
                nodeName: nodeName,
              },
            ];
          }
        });
      },

      addFinalResult: (result: any) => {
        const isJsonType = typeof result === 'object' && result !== null;
        const resultStr = isJsonType ? JSON.stringify(result) : String(result);

        if (isJsonType) {
          const needsToggle = resultStr.length > maxContentLength;
          addUpdate('final-result', {
            content: resultStr,
            isJson: true,
            needsToggle,
            shortContent: needsToggle ? resultStr.substring(0, maxContentLength) : null,
          });
        } else {
          const currentTime = getElapsedTime();

          setUpdates(prevUpdates => {
            const existingIndex = prevUpdates.findIndex(u => u.type === 'final-result-streaming');
            if (existingIndex >= 0) {
              // 기존 업데이트 수정 (시간은 유지)
              const newUpdates = [...prevUpdates];
              const newBuffer = newUpdates[existingIndex].content + result;
              newUpdates[existingIndex] = {
                ...newUpdates[existingIndex],
                content: newBuffer,
                // timestamp는 유지 (첫 번째 chunk의 시간을 유지)
              };
              return newUpdates;
            } else {
              // 새 업데이트 추가 (첫 번째 chunk이므로 현재 시간 사용)
              return [
                ...prevUpdates,
                {
                  id: 'final-result-' + Date.now(),
                  type: 'final-result-streaming',
                  content: result,
                  timestamp: currentTime,
                  createdAt: new Date(),
                },
              ];
            }
          });
        }
      },

      addToolCalls: (toolCalls: any) => {
        let toolCallsInfo = '';
        if (toolCalls.tool_calls && Array.isArray(toolCalls.tool_calls)) {
          toolCalls.tool_calls.forEach((toolCall: any, index: number) => {
            if (toolCall.function) {
              toolCallsInfo += `Tool ${index + 1}: ${toolCall.function.name}(${toolCall.function.arguments})\n`;
            }
          });
        } else if (toolCalls.content && toolCalls.additional_kwargs && toolCalls.additional_kwargs.tool_calls) {
          toolCalls.additional_kwargs.tool_calls.forEach((toolCall: any, index: number) => {
            if (toolCall.function) {
              toolCallsInfo += `Tool ${index + 1}: ${toolCall.function.name}(${toolCall.function.arguments})\n`;
            }
          });
        }

        const content = toolCallsInfo || JSON.stringify(toolCalls);
        const needsToggle = content.length > maxContentLength * 1.5;

        addUpdate('tool-calls', {
          content,
          needsToggle,
          shortContent: needsToggle ? content.substring(0, maxContentLength) : null,
        });
      },

      addUpdates: (updatesData: any) => {
        // 구조화된 형태로 Updates 정보 처리
        const formatUpdatesContent = (data: any) => {
          if (!data) return 'No updates';

          try {
            // updates 객체가 있는 경우 그 안의 additional_kwargs를 확인
            const additional_kwargs = data.additional_kwargs || data.updates?.additional_kwargs;

            if (additional_kwargs) {
              let formattedContent = '';

              // node_name 정보 추가
              if (data.node_name || data.updates?.node_name) {
                const nodeName = data.node_name || data.updates.node_name;
                formattedContent += `🔧 Node: ${nodeName}\n\n`;
              }

              // context 정보 처리 - context_로 시작하는 키 찾기
              const contextKey = Object.keys(additional_kwargs).find(key => key.startsWith('context_'));
              if (contextKey && additional_kwargs[contextKey]) {
                formattedContent += '📄 Context Information:\n';
                const contextText = additional_kwargs[contextKey];

                // 문서별로 구분
                const docs = contextText.split('[doc_').slice(1);
                docs.forEach((doc: any, index: number) => {
                  const docNumber = index + 1;
                  const docContent = doc.split('\n').slice(0, 3).join('\n'); // 첫 3줄만
                  formattedContent += `  └ Document ${docNumber}: ${docContent.trim().substring(0, 100)}...\n`;
                });
                formattedContent += '\n';
              }

              // docs 정보 처리 - docs_로 시작하는 키 찾기
              const docsKey = Object.keys(additional_kwargs).find(key => key.startsWith('docs_'));
              if (docsKey && additional_kwargs[docsKey]) {
                formattedContent += '📋 Retrieved Documents:\n';
                const docs = additional_kwargs[docsKey];
                if (Array.isArray(docs)) {
                  docs.forEach((doc: any, index: number) => {
                    const fileName = doc.metadata?.file_name || 'Unknown file';
                    const page = doc.metadata?.page || 'N/A';
                    const score = doc.score ? ` (score: ${doc.score.toFixed(3)})` : '';
                    formattedContent += `  └ ${index + 1}. ${fileName} (page ${page})${score}\n`;
                    if (doc.content) {
                      const preview = doc.content.substring(0, 80).replace(/\n/g, ' ');
                      formattedContent += `     Preview: ${preview}...\n`;
                    }
                  });
                }
                formattedContent += '\n';
              }

              // global_ref 정보 처리
              if (additional_kwargs.global_ref) {
                formattedContent += '🔗 Global References:\n';
                const globalRef = additional_kwargs.global_ref;
                Object.entries(globalRef).forEach(([key, value]) => {
                  formattedContent += `  └ ${key}: ${value}\n`;
                });
              }

              return formattedContent.trim() || JSON.stringify(data);
            }

            // additional_kwargs가 없어도 JSON으로라도 표시
            return JSON.stringify(data, null, 2);
          } catch {
            return JSON.stringify(data);
          }
        };

        const formattedContent = formatUpdatesContent(updatesData);
        const needsToggle = formattedContent.length > maxContentLength;
        const isFormatted = !!(updatesData.additional_kwargs || updatesData.updates?.additional_kwargs);

        addUpdate('updates', {
          content: formattedContent,
          rawContent: JSON.stringify(updatesData), // 원본 JSON도 보관
          needsToggle,
          shortContent: needsToggle ? formattedContent.substring(0, maxContentLength) : null,
          isFormatted: isFormatted,
        });
      },

      addError: (error: any, statusCode: number | null = null) => {
        const elapsedTime = getElapsedTime();
        let errorText = error;
        if (statusCode) {
          errorText = `HTTP 에러: 상태코드 ${statusCode} - ${error}`;
        }

        addUpdate('error', errorText, elapsedTime);
        setStatus(`Call failed [Total time: ${elapsedTime}s]`);
        setIsLoading(false);
      },

      complete: (tokenCount: number | null = null) => {
        const elapsedTime = getElapsedTime();
        addUpdate('complete', `호출이 성공적으로 완료되었습니다.` + (tokenCount ? ` (Tokens: ${tokenCount.toString()})` : ''), elapsedTime);

        let statusText = `Call completed [Total time: ${elapsedTime}s]`;
        if (tokenCount) {
          statusText += ` [Tokens: ${tokenCount.toString()}]`;
          setTokenInfo(`Tokens: ${tokenCount.toString()}`);
        }

        setStatus(statusText);
        setIsLoading(false);
      },

      addRawData: (data: any) => {
        setRawBuffer(prev => prev + data);

        // 라인 단위로 SSE 데이터 처리
        if (data && typeof data === 'string') {
          const fullBuffer = lineBuffer.current + data;
          const lines = fullBuffer.split(/\r?\n|\r/);

          const incompleteLastLine = lines.pop() || '';

          // 완성된 라인들 처리
          processCompleteSSELines(lines);

          lineBuffer.current = incompleteLastLine;
        }
      },

      processStreamData: (line: string) => {
        try {
          if (line.startsWith('data:')) {
            let payload = line.slice(5).trim();

            if (payload === '[DONE]') {
              return 'done';
            }

            if (payload) {
              const obj = JSON.parse(payload);

              if (obj.progress !== undefined) {
                const elapsedTime = getElapsedTime();
                addUpdate('progress', `${obj.progress} (시간: ${elapsedTime}s)`, elapsedTime);
                setStatus(`[progress] ${obj.progress}`);
              }

              if (obj.llm && obj.llm.content !== undefined) {
                const currentTime = getElapsedTime();
                const nodeName = obj.node_name;

                setUpdates(prevUpdates => {
                  const lastUpdate = prevUpdates[prevUpdates.length - 1];
                  const canAppendToLast = lastUpdate && lastUpdate.type === 'llm-content-streaming' && lastUpdate.nodeName === nodeName;

                  if (canAppendToLast) {
                    const newUpdates = [...prevUpdates];
                    const newBuffer = newUpdates[newUpdates.length - 1].content + obj.llm.content;
                    newUpdates[newUpdates.length - 1] = {
                      ...newUpdates[newUpdates.length - 1],
                      content: newBuffer,
                    };
                    return newUpdates;
                  } else {
                    return [
                      ...prevUpdates,
                      {
                        id: 'llm-content-' + Date.now() + (nodeName ? '-' + nodeName : ''),
                        type: 'llm-content-streaming',
                        content: obj.llm.content,
                        timestamp: currentTime,
                        createdAt: new Date(),
                        nodeName: nodeName,
                      },
                    ];
                  }
                });
              }

              if (obj.final_result !== undefined) {
                const isJsonType = typeof obj.final_result === 'object' && obj.final_result !== null;
                const resultStr = isJsonType ? JSON.stringify(obj.final_result) : String(obj.final_result);

                if (isJsonType) {
                  const needsToggle = resultStr.length > maxContentLength;
                  addUpdate('final-result', {
                    content: resultStr,
                    isJson: true,
                    needsToggle,
                    shortContent: needsToggle ? resultStr.substring(0, maxContentLength) : null,
                  });
                } else {
                  const currentTime = getElapsedTime();

                  setUpdates(prevUpdates => {
                    const existingIndex = prevUpdates.findIndex(u => u.type === 'final-result-streaming');
                    if (existingIndex >= 0) {
                      const newUpdates = [...prevUpdates];
                      const newBuffer = newUpdates[existingIndex].content + obj.final_result;
                      newUpdates[existingIndex] = {
                        ...newUpdates[existingIndex],
                        content: newBuffer,
                      };
                      return newUpdates;
                    } else {
                      return [
                        ...prevUpdates,
                        {
                          id: 'final-result-' + Date.now(),
                          type: 'final-result-streaming',
                          content: obj.final_result,
                          timestamp: currentTime,
                          createdAt: new Date(),
                        },
                      ];
                    }
                  });
                }
              }

              if (obj.tool_calls !== undefined) {
                let toolCallsInfo = '';
                if (obj.tool_calls.tool_calls && Array.isArray(obj.tool_calls.tool_calls)) {
                  obj.tool_calls.tool_calls.forEach((toolCall: any, index: number) => {
                    if (toolCall.function) {
                      toolCallsInfo += `Tool ${index + 1}: ${toolCall.function.name}(${toolCall.function.arguments})\n`;
                    }
                  });
                } else if (obj.tool_calls.content && obj.tool_calls.additional_kwargs && obj.tool_calls.additional_kwargs.tool_calls) {
                  obj.tool_calls.additional_kwargs.tool_calls.forEach((toolCall: any, index: number) => {
                    if (toolCall.function) {
                      toolCallsInfo += `Tool ${index + 1}: ${toolCall.function.name}(${toolCall.function.arguments})\n`;
                    }
                  });
                }

                const content = toolCallsInfo || JSON.stringify(obj.tool_calls);
                const needsToggle = content.length > maxContentLength * 1.5;

                addUpdate('tool-calls', {
                  content,
                  needsToggle,
                  shortContent: needsToggle ? content.substring(0, maxContentLength) : null,
                });
              }

              if (obj.updates !== undefined) {
                const formatUpdatesContent = (data: any) => {
                  if (!data) return 'No updates';

                  try {
                    const additional_kwargs = data.additional_kwargs || data.updates?.additional_kwargs;

                    if (additional_kwargs) {
                      let formattedContent = '';

                      if (data.node_name || data.updates?.node_name) {
                        const nodeName = data.node_name || data.updates.node_name;
                        formattedContent += `🔧 Node: ${nodeName}\n\n`;
                      }

                      const contextKey = Object.keys(additional_kwargs).find(key => key.startsWith('context_'));
                      if (contextKey && additional_kwargs[contextKey]) {
                        formattedContent += '📄 Context Information:\n';
                        const contextText = additional_kwargs[contextKey];

                        const docs = contextText.split('[doc_').slice(1);
                        docs.forEach((doc: any, index: number) => {
                          const docNumber = index + 1;
                          const docContent = doc.split('\n').slice(0, 3).join('\n');
                          formattedContent += `  └ Document ${docNumber}: ${docContent.trim().substring(0, 100)}...\n`;
                        });
                        formattedContent += '\n';
                      }

                      const docsKey = Object.keys(additional_kwargs).find(key => key.startsWith('docs_'));
                      if (docsKey && additional_kwargs[docsKey]) {
                        formattedContent += '📋 Retrieved Documents:\n';
                        const docs = additional_kwargs[docsKey];
                        if (Array.isArray(docs)) {
                          docs.forEach((doc: any, index: number) => {
                            const fileName = doc.metadata?.file_name || 'Unknown file';
                            const page = doc.metadata?.page || 'N/A';
                            const score = doc.score ? ` (score: ${doc.score.toFixed(3)})` : '';
                            formattedContent += `  └ ${index + 1}. ${fileName} (page ${page})${score}\n`;
                            if (doc.content) {
                              const preview = doc.content.substring(0, 80).replace(/\n/g, ' ');
                              formattedContent += `     Preview: ${preview}...\n`;
                            }
                          });
                        }
                        formattedContent += '\n';
                      }

                      if (additional_kwargs.global_ref) {
                        formattedContent += '🔗 Global References:\n';
                        const globalRef = additional_kwargs.global_ref;
                        Object.entries(globalRef).forEach(([key, value]) => {
                          formattedContent += `  └ ${key}: ${value}\n`;
                        });
                      }

                      return formattedContent.trim() || JSON.stringify(data);
                    }

                    return JSON.stringify(data, null, 2);
                  } catch {
                    return JSON.stringify(data);
                  }
                };

                const formattedContent = formatUpdatesContent(obj.updates);
                const needsToggle = formattedContent.length > maxContentLength;
                const isFormatted = !!(obj.updates.additional_kwargs || obj.updates.updates?.additional_kwargs);

                addUpdate('updates', {
                  content: formattedContent,
                  rawContent: JSON.stringify(obj.updates),
                  needsToggle,
                  shortContent: needsToggle ? formattedContent.substring(0, maxContentLength) : null,
                  isFormatted: isFormatted,
                });
              }

              if (obj.usage && obj.usage.total_tokens !== undefined) {
                return { tokenInfo: obj.usage.total_tokens };
              } else if (obj.total_tokens !== undefined) {
                return { tokenInfo: obj.total_tokens };
              }
            }
          }
        } catch (e) {
          console.warn('스트림 데이터 파싱 실패:', e);
        }

        return null;
      },
    }),
    [getElapsedTime, addUpdate, maxContentLength, processCompleteSSELines]
  );

  // 외부에서 사용할 수 있도록 ref에 API 설정
  useImperativeHandle(ref, () => {
    return {
      streamingAPI,
    };
  }, [streamingAPI]);

  // 업데이트가 추가될 때마다 스크롤을 맨 아래로
  useEffect(() => {
    if (updatesListRef.current) {
      updatesListRef.current.scrollTop = updatesListRef.current.scrollHeight;
    }
  }, [updates]);

  // 상태 변경 알림
  useEffect(() => {
    if (onStatusChange) {
      onStatusChange({ status, isLoading, tokenInfo });
    }
  }, [status, isLoading, tokenInfo, onStatusChange]);

  // 업데이트 렌더링 함수
  const renderUpdate = (update: any) => {
    const getUpdateClass = (type: string) => {
      const baseClass = 'stream-update';
      let typeClass = `update-${type.replace('-streaming', '')}`;

      // tool-result는 final-result 스타일 사용
      if (type === 'tool-result') {
        typeClass = 'update-final-result';
      }
      // tool-calls는 updates 스타일 사용
      if (type === 'tool-calls') {
        typeClass = 'update-updates';
      }

      return `${baseClass} ${typeClass}`;
    };

    const renderContent = (update: any) => {
      const { type, content } = update;

      if (type === 'llm-content-streaming') {
        const nodeNameDisplay = update.nodeName ? ` [${update.nodeName}]` : '';
        return (
          <>
            <strong>LLM Content (streaming){nodeNameDisplay}:</strong>
            <span className='content-text'>{content}</span>
          </>
        );
      }

      if (type === 'final-result-streaming') {
        return (
          <>
            <strong>Final Result (streaming):</strong>
            <span className='content-text'>{content}</span>
          </>
        );
      }

      if (type === 'final-result' && content.isJson) {
        if (content.needsToggle) {
          const isExpanded = expandedUpdates[update.id] || false;
          const isCopied = copiedStates[update.id] || false;
          return (
            <>
              <strong>Final Result (JSON):</strong>
              <pre className='json-content'>{isExpanded ? content.content : content.shortContent}</pre>
              <div>
                <button onClick={() => toggleContent(update.id)} className='toggle-btn'>
                  {isExpanded ? 'Show Less' : 'Show More'}
                </button>
                <button onClick={() => handleCopy(content.content, update.id)} className='copy-btn'>
                  {isCopied ? 'Copied!' : 'Copy Clipboard'}
                </button>
              </div>
            </>
          );
        } else {
          const isCopied = copiedStates[update.id] || false;
          return (
            <>
              <strong>Final Result (JSON):</strong>
              <pre className='json-content'>{content.content}</pre>
              <button onClick={() => handleCopy(content.content, update.id)} className='copy-btn'>
                {isCopied ? 'Copied!' : 'Copy Clipboard'}
              </button>
            </>
          );
        }
      }

      if ((type === 'tool-calls' || type === 'updates' || type === 'tool-result') && content.needsToggle) {
        const isExpanded = expandedUpdates[update.id] || false;
        let label = 'Updates';
        if (type === 'tool-calls') label = 'Tool Calls';
        if (type === 'tool-result') label = 'Tool Result';

        // Tool Result의 특별 처리
        if (type === 'tool-result') {
          const statusColor = content.status === 'error' ? 'color: red' : 'color: green';
          const isCopied = copiedStates[update.id] || false;
          return (
            <>
              <strong>{label}:</strong>
              <span
                style={{
                  marginLeft: '8px',
                  fontSize: '0.9em',
                  ...{ color: statusColor === 'color: red' ? 'red' : 'green' },
                }}
              >
                [{content.status}]
              </span>
              <pre className='content-text'>{isExpanded ? content.content : content.shortContent}</pre>
              <div>
                <button onClick={() => toggleContent(update.id)} className='toggle-btn'>
                  {isExpanded ? 'Show Less' : 'Show More'}
                </button>
                <button onClick={() => handleCopy(content.content, update.id)} className='copy-btn'>
                  {isCopied ? 'Copied!' : 'Copy Clipboard'}
                </button>
              </div>
            </>
          );
        }

        // Updates의 경우 포맷된 내용인지 확인
        if (type === 'updates' && content.isFormatted) {
          const isCopied = copiedStates[update.id] || false;
          return (
            <>
              <strong>{label} (structured):</strong>
              <pre className='content-text formatted-updates'>{isExpanded ? content.content : content.shortContent}</pre>
              <div>
                <button onClick={() => toggleContent(update.id)} className='toggle-btn'>
                  {isExpanded ? 'Show Less' : 'Show More'}
                </button>
                <button onClick={() => handleCopy(content.rawContent || content.content, update.id)} className='copy-btn'>
                  {isCopied ? 'Copied!' : 'Copy Clipboard'}
                </button>
              </div>
            </>
          );
        } else {
          const isCopied = copiedStates[update.id] || false;
          return (
            <>
              <strong>{label}:</strong>
              <pre className='content-text'>{isExpanded ? content.content : content.shortContent}</pre>
              <div>
                <button onClick={() => toggleContent(update.id)} className='toggle-btn'>
                  {isExpanded ? 'Show Less' : 'Show More'}
                </button>
                <button onClick={() => handleCopy(content.content, update.id)} className='copy-btn'>
                  {isCopied ? 'Copied!' : 'Copy Clipboard'}
                </button>
              </div>
            </>
          );
        }
      }

      // 기본 텍스트 내용
      return (
        <>
          <strong>{type}:</strong>
          <span className='content-text'>{typeof content === 'string' ? content : JSON.stringify(content)}</span>
        </>
      );
    };

    return (
      <div key={update.id} className={getUpdateClass(update.type)}>
        <div className='update-header'>
          {showTimestamp && <span className='timestamp'>[{update.timestamp}s]</span>}
          <div className='update-content'>{renderContent(update)}</div>
        </div>
      </div>
    );
  };

  return (
    <div ref={containerRef} className={`streaming-display ${className}`}>
      <div className='status-bar'>
        {status ? <div className='status-text'>{status}</div> : <div className='status-text'>Ready</div>}
        {showTokenInfo && tokenInfo && <div className='token-info'>{tokenInfo}</div>}
        <button onClick={toggleView} className='toggle-view-btn'>
          {showRaw ? 'Show Structured' : 'Show Raw'}
        </button>
      </div>

      {showRaw ? (
        <div className='raw-output'>
          <pre>{rawBuffer}</pre>
        </div>
      ) : (
        <div ref={updatesListRef} className='updates-list'>
          {updates.map(update => {
            return renderUpdate(update);
          })}
        </div>
      )}
    </div>
  );
});

StreamingDisplayComponent.displayName = 'StreamingDisplayComponent';

export default StreamingDisplayComponent;