import { nodesAtom } from '@/components/builder/atoms/AgentAtom';
import {
  addMessageAtom,
  messagesAtom,
  progressMessageAtom,
  regenerateTargetIndexAtom,
  replaceMessageAtom,
  resetAllHumanRegenAtom,
  streamingMessageAtom,
} from '@/components/builder/atoms/messagesAtom.ts';
import { Chat } from '@/components/builder/common/chat/Chat.tsx';
import StreamingDisplayComponent from '@/components/builder/common/modal/log/StreamingDisplayComponent.tsx';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { ChatType } from '@/components/builder/types/Agents';
import type { QueryMessage } from '@/components/builder/types/Agents.ts';
import type { MessageFormat } from '@/components/builder/types/messageFormat.ts';
import { streamAgentGraph } from '@/services/agent/builder2/agentBuilder.services';
import { useAtom } from 'jotai/index';
import React, { useRef, useState } from 'react';

interface PanelProps {
  id: string;
  title: string;
  type: 'log-viewer';
  onClose: (_id: string) => void;
  streamingDisplayRef?: React.RefObject<any>;
}

interface PanelData {
  id: string;
  type: 'log-viewer';
  title: string;
  width: number;
  isVisible: boolean; // 패널 표시 여부
}

const Panel: React.FC<PanelProps> = ({ id, type, onClose, streamingDisplayRef }) => {
  const renderContent = () => {
    switch (type) {
      case 'log-viewer':
        return (
          <div className='card flex h-full flex-col bg-white shadow'>
            <div className='flex-shrink-0'>
              <div className={`transform transition-opacity ease-out opacity-100`}>
                <div className='flex items-center justify-between gap-2 px-3 py-1.5 text-sm font-semibold text-gray-900'>
                  <div className='flex items-center gap-2'>
                    <span>Log Viewer</span>
                  </div>
                  <div className='flex items-center gap-2'>
                    <button
                      onClick={() => onClose(id)}
                      className='shrink-0 w-[24px] h-[24px] p-0 bg-transparent border-0 cursor-pointer hover:opacity-70 transition-opacity'
                      title='Close Log Viewer'
                    >
                      <img alt='ico-system-32-AppBar-close' className='w-[24px] h-[24px]' src='/assets/images/system/ico-system-32-AppBar-close.svg' />
                    </button>
                  </div>
                </div>
                <div className='border-b border-b-gray-200'></div>
              </div>
            </div>
            <div className='card-body flex-1 overflow-hidden p-0'>
              {/* 패널 내부도 같은 ref 사용 - 패널이 열려 있을 때 표시용 */}
              <StreamingDisplayComponent ref={streamingDisplayRef} showTimestamp={true} showTokenInfo={true} maxContentLength={80} className='h-full' />
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return <div className='h-full w-96'>{renderContent()}</div>;
};

interface ChatTestProps {
  isChatVisible: boolean;
  setIsChatVisible?: () => void;
  agentId: string;
  // agentData?: Agent;
}

const ChatTest = ({ isChatVisible, setIsChatVisible, agentId }: ChatTestProps) => {
  const [messages] = useAtom(messagesAtom);
  const [, addMessage] = useAtom(addMessageAtom);
  const [, replaceMessage] = useAtom(replaceMessageAtom);
  const [regenerateTargetIndex, setRegenerateTargetIndex] = useAtom(regenerateTargetIndexAtom);
  const [, resetAllHumanRegen] = useAtom(resetAllHumanRegenAtom);
  const { agent } = useGraphActions();
  const queryResponseRef = useRef(false);
  const [isChatLoading, setIsChatLoading] = useState<boolean>(false);
  const [, setNodes] = useAtom(nodesAtom);
  const [, setProgressMessage] = useAtom(progressMessageAtom);
  const [, setStreamingMessage] = useAtom(streamingMessageAtom);

  // 동적 패널 관리 상태
  const [panels, setPanels] = useState<PanelData[]>([]);

  // StreamingDisplay 관련 refs
  const streamingDisplayRef = useRef<any>(null);
  // 🔥 직접 ref를 사용하여 항상 최신 API를 가져옴
  const getStreamingAPI = () => streamingDisplayRef.current?.streamingAPI || null;

  const lineBufferRef = useRef('');

  // 채팅 초기화 시 패널 데이터도 초기화
  const handleClearChat = () => {
    // 모든 패널의 StreamingDisplayComponent 초기화
    streamingDisplayRef.current?.streamingAPI?.reset();
  };

  // 패널 추가 함수 - 중복 방지 로직 추가
  const addPanel = (type: 'log-viewer') => {
    // 이미 해당 타입의 패널이 열려 있는지 확인 (isVisible이 true인 것만)
    const existingVisiblePanel = panels.find(panel => panel.type === type && panel.isVisible !== false);

    if (existingVisiblePanel) {
      // 이미 열려 있으면 닫기
      removePanel(existingVisiblePanel.id);
      return;
    }

    // 숨겨진 패널이 있는지 확인
    const hiddenPanel = panels.find(panel => panel.type === type && panel.isVisible === false);

    if (hiddenPanel) {
      // 숨겨진 패널이 있으면 다시 보이기 (데이터 유지)
      setPanels(prev => prev.map(panel => (panel.id === hiddenPanel.id ? { ...panel, isVisible: true } : panel)));
      return;
    }

    // 없으면 새로 추가
    const id = Date.now().toString();
    let title = '';

    switch (type) {
      case 'log-viewer':
        title = 'Log Viewer';
        break;
    }

    const newPanel: PanelData = { id, type, title, width: 384, isVisible: true };
    setPanels(prev => [...prev, newPanel]);
  };

  // 패널 제거 함수 - 실제로는 숨기기만 함 (언마운트하지 않음)
  const removePanel = (id: string) => {
    setPanels(prev => prev.map(panel => (panel.id === id ? { ...panel, isVisible: false } : panel)));
  };

  // tracingMessages는 Graph.tsx에서 사용되므로 유지
  // eslint-disable-next-line @typescript-eslint/no-unused-vars

  // const previousChunkRef = useRef<string>('');

  // 스트림 데이터 처리 함수 - 조건에 맞는 데이터만 저장 및 메시지 처리
  const handleData = (chunk: string, messageTemp: string[]) => {
    // 이전 chunk와 동일하면 처리하지 않음 (중복 렌더링 방지)
    // if (chunk === previousChunkRef.current) {
    //   return;
    // }
    // previousChunkRef.current = chunk;

    // Log Viewer에 스트림 데이터 전달
    const streamingAPI = getStreamingAPI();

    if (streamingAPI) {
      streamingAPI.addRawData(chunk);
    }

    const combined = lineBufferRef.current + chunk;
    const lines = combined.split(/\r?\n/);

    lineBufferRef.current = lines.pop() || '';

    for (const line of lines) {
      let eventType = '';
      let dataStr = '';
      // 저장 조건 확인
      let shouldSave = false;
      let logData = '';

      if (line.startsWith('event:')) {
        eventType = line.replace('event:', '').trim();
      } else if (line.startsWith('data:')) {
        dataStr = line.replace('data:', '');
      }

      // data 파싱 시도
      let jsonValue: any = {};
      try {
        if (dataStr) {
          jsonValue = JSON.parse(dataStr);
        }
      } catch {
        // JSON 파싱 실패 시 원본 문자열 사용
        jsonValue = dataStr;
      }

      const nodeName = jsonValue?.node_name || '';

      // 1. type이 metadata이고 data가 run_id로 시작
      if (jsonValue?.run_id) {
        shouldSave = true;
        setProgressMessage('처리중...');
      }
      // 2. type이 data이고 data가 updates로 시작
      else if (jsonValue?.updates) {
        eventType = 'on_node_start';
        shouldSave = true;
        setProgressMessage('업데이트 처리중...');
      }
      // 3. type이 data이고 data가 progress로 시작
      else if (jsonValue?.progress) {
        eventType = 'on_node_start';
        shouldSave = true;
        setProgressMessage(jsonValue.progress);
      }
      // 4. type이 data이고 data가 error로 시작
      else if (jsonValue?.error) {
        eventType = 'on_node_error';
        shouldSave = true;
        setProgressMessage('에러');
      }
      // 5. type이 error이고 data가 status_code로 시작
      else if (jsonValue?.status_code) {
        eventType = 'on_node_error';
        shouldSave = true;
        setProgressMessage('에러');
      }
      // 6. type이 message이고 data가 "[DONE]"
      else if (jsonValue?.message === '[DONE]') {
        shouldSave = true;
      }
      // 7. data가 llm로 시작
      else if (jsonValue?.llm) {
        setProgressMessage('LLM 처리중...');
      }
      // 8. data가 tool로 시작
      else if (jsonValue?.tool) {
        shouldSave = true;
        setProgressMessage(`도구 처리중...(${jsonValue.tool?.content?.substring(0, 15)}...)`);
      }
      // 9. data가 final_result로 시작
      else if (jsonValue?.final_result) {
        setProgressMessage('답변 생성중...');
        if (typeof jsonValue.final_result === 'string') {
          messageTemp.push(jsonValue.final_result);
        } else {
          messageTemp.push(JSON.stringify(jsonValue.final_result));
        }
        const currentContent = messageTemp.join('');

        setStreamingMessage(currentContent);

        // regenerate 모드인 경우도 streamingMessage로 표시됨 (완료 후 새 메시지 추가)
      }

      if (shouldSave) {
        logData = dataStr.trim();
      }

      // 조건에 맞는 데이터만 저장
      if (shouldSave) {
        // setTracingMessages({ callback: eventType as string, nodeId: nodeName as string, log: logData as string });
        setNodes(prev => {
          return prev.map(node => {
            if (node?.data?.name !== nodeName) {
              // return node;
              return {
                ...node,
                data: {
                  ...node.data,
                  innerData: {
                    ...node.data.innerData,
                    isDone: node.data.innerData?.isRun ? node.data.innerData?.isRun : false,
                  },
                },
              };
            }

            return {
              ...node,
              data: {
                ...node.data,
                innerData: {
                  ...node.data.innerData,
                  isRun: eventType === 'on_node_start',
                  isDone: false,
                  isError: eventType === 'on_node_error',
                  logData: [...(node.data.innerData?.logData ?? []), logData],
                },
              },
            };
          });
        });
      }
    }
  };

  // useEffect(() => {
  //   // 채팅창이 닫힐 때 queryResponse 상태 리셋
  //   if (!isChatVisible) {
  //     queryResponseRef.current = false;
  //   }
  // }, [isChatVisible]);

  const handleClose = () => {
    setNodes(prev => {
      return prev.map(node => {
        return {
          ...node,
          data: {
            ...node.data,
            innerData: {
              ...node.data.innerData,
              isRun: false,
              isDone: false,
              isError: false,
            },
          },
        };
      });
    });

    if (setIsChatVisible) {
      setIsChatVisible();
    }
  };

  const handleChatTest = async () => {
    if (!agent) return;
    // 메시지 배열 구성
    const tempMessage: QueryMessage[] = [];

    // 재생성 모드 확인 (regenerateTargetIndex가 0 이상이면 재생성 모드)
    if (regenerateTargetIndex >= 0) {
      // 재생성 모드: regenerateTargetIndex에서 역방향으로 human 메시지 찾기

      for (let i = regenerateTargetIndex; i >= 0; i--) {
        if (messages[i].type === ChatType.HUMAN) {
          tempMessage.push({
            content: messages[i].content,
            type: messages[i].type,
          });
          break;
        }
      }
    } else {
      // 일반 모드: 모든 메시지 포함
      messages.forEach((message, index) => {
        const msg = {
          content: message.content,
          type: message.type,
        };
        tempMessage.push(msg);

        // regenerate 모드인 경우: index - 1부터 역순으로 탐색해서 human 메시지 찾아서 포함
        if (message.regen === true) {
          tempMessage.length = 0;
          // index - 1부터 역순으로 탐색해서 human 메시지 찾기
          for (let i = index - 1; i >= 0; i--) {
            if (messages[i].type === ChatType.HUMAN) {
              tempMessage.push({
                content: messages[i].content,
                type: messages[i].type,
              });

              break;
            }
          }
        }
      });
    }

    const request = {
      graph_id: agent?.id || '',
      input_data: {
        messages: tempMessage,
        // additional_kwargs: {},
      },
    };

    try {
      setIsChatLoading(true);
      setProgressMessage('처리중...');
      lineBufferRef.current = '';
      const startTime = Date.now();
      const messageTemp: string[] = [];

      // Log Viewer 스트리밍 시작
      const streamingAPI = getStreamingAPI();
      if (streamingAPI) {
        streamingAPI.startStreaming(startTime);
      }

      // onChunk 콜백으로 스트리밍 데이터 처리
      await streamAgentGraph(request, (chunk: string) => {
        handleData(chunk, messageTemp);
      });

      if (lineBufferRef.current.trim()) { 
        handleData(lineBufferRef.current + '\n', messageTemp);
        lineBufferRef.current = '';
      }

      // 스트리밍 완료 후 최종 메시지 처리
      const endTime = Date.now();
      const elapsedTime = endTime - startTime;
      const currentTime = new Date();
      const finalContent = messageTemp.join('');

      if (regenerateTargetIndex >= 0) {
        // regenerate 모드: 기존 AI 메시지를 새 메시지로 교체 (같은 인덱스 유지)

        const newMessage: MessageFormat = {
          id: `${Date.now()}`,
          time: currentTime.toLocaleString(),
          content: finalContent,
          type: ChatType.AI,
          regen: false, // 재생성 완료 후에는 regen: false
          elapsedTime: elapsedTime,
        };

        // 기존 인덱스 위치에 새 메시지로 교체
        replaceMessage({ messageIndex: regenerateTargetIndex, newMessage });
        // human의 regen 플래그 리셋
        resetAllHumanRegen();
        setRegenerateTargetIndex(-1);
      } else {
        // 새로운 메시지 추가
        const newMessage: MessageFormat = {
          id: `${Date.now()}`,
          time: currentTime.toLocaleString(),
          content: finalContent,
          type: ChatType.AI,
          regen: false,
          elapsedTime: elapsedTime,
        };
        addMessage(newMessage);
      }

      setStreamingMessage('');
      setProgressMessage('대기중');
      queryResponseRef.current = true;

      // Log Viewer 스트리밍 완료
      const streamingAPIComplete = getStreamingAPI();
      if (streamingAPIComplete) {
        streamingAPIComplete.complete();
      }
    } catch (error) {
      setProgressMessage('에러 발생');

      // Log Viewer에 에러 표시
      const streamingAPIError = getStreamingAPI();
      if (streamingAPIError) {
        const errorMessage = error instanceof Error ? error.message : '채팅 테스트 중 오류가 발생했습니다.';
        streamingAPIError.addError(errorMessage);
      }

      const errorMessage = error instanceof Error ? error.message : '채팅 테스트 중 오류가 발생했습니다.';

      const errorResponse: MessageFormat = {
        id: `${Date.now()}`,
        time: new Date().toLocaleString(),
        content: `오류: ${errorMessage}`,
        type: ChatType.AI,
        regen: false,
      };

      if (regenerateTargetIndex >= 0) {
        // regenerate 모드: 기존 AI 메시지를 에러 메시지로 교체
        replaceMessage({ messageIndex: regenerateTargetIndex, newMessage: errorResponse });
        resetAllHumanRegen();
        setRegenerateTargetIndex(-1);
      } else {
        // 일반 모드: 새 에러 메시지 추가
        addMessage(errorResponse);
      }
    } finally {
      setIsChatLoading(false);
    }
  };

  return (
    <div className={`absolute right-1 top-16 z-50 h-[91%] ${isChatVisible ? '' : 'hidden'}`}>
      <div className='flex h-full'>
        {/* 동적 패널들 - 왼쪽부터 순서대로 쌓임 */}
        {/* 모든 패널을 렌더링하되, isVisible이 false인 것은 숨김 (언마운트하지 않음) */}
        {panels.map(panel => (
          <div key={panel.id} className={`mr-2 h-full w-96 ${panel.isVisible === false ? 'hidden' : ''}`}>
            <Panel id={panel.id} title={panel.title} type={panel.type} onClose={removePanel} streamingDisplayRef={streamingDisplayRef} />
          </div>
        ))}

        {/* Chat Component */}
        <div className='w-130'>
          <Chat
            isVisible={isChatVisible}
            onClose={handleClose}
            offset={262}
            title={'Chat Test'}
            onChatTest={handleChatTest}
            isQueryResponse={queryResponseRef.current}
            isLoading={isChatLoading}
            agentId={agentId}
            onAddPanel={addPanel}
            panels={panels}
            onClearChat={handleClearChat}
          />
        </div>
      </div>
    </div>
  );
};

export default ChatTest;
