import { Button } from '@/components/common/auth';
import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UITextArea2 } from '@/components/UI/molecules';
import { UIFilter } from '@/components/UI/organisms';
import type { ErrorResponse, SuccessResponse } from '@/hooks/common/api/types';
import { useStreamAgentDeploy } from '@/services/deploy/agent/agentDeploy.services';
import { useModal } from '@/stores/common/modal';
import { useEffect, useRef, useState } from 'react';

interface DeployAgentChatTestPopupPageProps {
  isOpen: boolean;
  onClose: () => void;
  endPoint?: string;
  targetType?: string;
  dropdownOptions?: Array<{ value: string; label: string }>;
  authorization?: string;
}

interface Message {
  id: string;
  text: string;
  isUser: boolean;
  timestamp: Date;
}

/**
 * 안전한 JSON 파싱 헬퍼 함수
 * 파싱 실패 시 null 반환
 */
const safeJsonParse = (jsonString: string): any => {
  if (!jsonString || typeof jsonString !== 'string') {
    return null;
  }
  try {
    return JSON.parse(jsonString);
  } catch {
    return null;
  }
};

export function DeployAgentChatTestPopupPage({ isOpen, onClose, endPoint, targetType, dropdownOptions = [], authorization = '' }: DeployAgentChatTestPopupPageProps) {
  const { openConfirm } = useModal();
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [progressMessage, setProgressMessage] = useState('');
  const [selectedDeployId, setSelectedDeployId] = useState<string>('');
  const [regeneratingMessageIndex, setRegeneratingMessageIndex] = useState<number | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // dropdownOptions가 변경될 때 첫 번째 옵션을 기본값으로 설정 (초기화는 하지 않음)
  useEffect(() => {
    if (dropdownOptions.length > 0 && !selectedDeployId) {
      setSelectedDeployId(dropdownOptions[0].value);
    }
  }, [dropdownOptions.length, JSON.stringify(dropdownOptions)]);

  const streamMutation = useStreamAgentDeploy({
    onSuccess: (response: SuccessResponse<string>) => {
      // response가 문자열 자체일 수 있음
      const sseData = typeof response === 'string' ? response : response.data;
      let streamingContent = ''; // llm.content와 final_result 스트리밍 누적
      let completeResponse = ''; // updates.messages[0].content 또는 updates.content (완전한 응답)
      let hasContent = false;
      let errorMessages: string[] = [];
      let isEndEvent = false; // event: end 감지 플래그

      if (sseData) {
        // SSE 이벤트 파싱
        const lines = sseData.split('\n');

        for (const line of lines) {
          if (line.startsWith('event: end')) {
            isEndEvent = true;
            break;
          }

          if (line.startsWith('data: ')) {
            const dataContent = line.substring(6).trim();

            if (!dataContent || dataContent === '[DONE]' || dataContent === '{}') {
              continue;
            }

            let parsedData: any = null;

            // targetType에 따라 다른 파싱 로직 적용
            if (targetType === 'agent_graph') {
              // agent_graph: 이전 코드 (단순한 파싱)
              parsedData = safeJsonParse(dataContent);
            } else {
              // external_graph: 현재 코드 (복잡한 파싱)
              // [object Object] 형태인 경우 처리
              if (dataContent.includes('[object Object]')) {
                // [object Object]를 빈 문자열로 치환하여 순수 텍스트만 추출
                const cleanText = dataContent.replace(/\[object Object\]/g, '');

                // 빈 문자열이 아닌 경우에만 처리
                if (cleanText.trim()) {
                  parsedData = { llm: cleanText };
                } else {
                  continue;
                }
              } else if (dataContent.includes("'")) {
                // {'key': 'value'} 형태인 경우 {"key": "value"}로 변환
                const jsonString = dataContent.replace(/'/g, '"');
                parsedData = safeJsonParse(jsonString);
              } else {
                // 일반 JSON 형태
                parsedData = safeJsonParse(dataContent);
              }
            }

            // 파싱 실패 시 다음 반복으로
            if (!parsedData) {
              continue;
            }

            // ===== 현재 로직: final_result만 사용 =====
            if (parsedData.final_result !== undefined) {
              streamingContent += parsedData.final_result;
              hasContent = true;
            }

            // ===== 원래 로직 (주석 보존) =====
            // 우선순위: 완전한 응답 > final_result 스트리밍 > llm.content 스트리밍
            // 1. updates.messages[0].content (가장 완전한 응답)
            // if (parsedData.updates && parsedData.updates.messages && parsedData.updates.messages.length > 0) {
            //   const llmMessage = parsedData.updates.messages[0];
            //   if (llmMessage.content) {
            //     completeResponse = llmMessage.content;
            //     streamingContent = ''; // 완전한 응답이 설정되면 스트리밍 내용 초기화
            //     hasContent = true;
            //   }
            // }
            // 2. updates.content (완전한 응답)
            // else if (parsedData.updates && parsedData.updates.content) {
            //   completeResponse = parsedData.updates.content;
            //   streamingContent = ''; // 완전한 응답이 설정되면 스트리밍 내용 초기화
            //   hasContent = true;
            // }
            // 3. final_result 스트리밍 (누적) - completeResponse가 없을 때만
            // else if (!completeResponse && parsedData.final_result !== undefined) {
            //   streamingContent += parsedData.final_result;
            //   hasContent = true;
            // }
            // 4. llm.content 스트리밍 (누적) - completeResponse가 없을 때만
            // else if (!completeResponse && parsedData.llm && typeof parsedData.llm === 'object' && parsedData.llm.content !== undefined) {
            //   streamingContent += parsedData.llm.content;
            //   hasContent = true;
            // }
            // 5. llm이 문자열로 직접 스트리밍되는 경우 - completeResponse가 없을 때만
            // else if (!completeResponse && parsedData.llm !== undefined && typeof parsedData.llm === 'string') {
            //   streamingContent += parsedData.llm;
            //   hasContent = true;
            // }

            // 진행 상태 메시지 처리
            if (parsedData.progress) {
              setProgressMessage(parsedData.progress);
            }

            // 에러 처리 (공통)
            if (parsedData.error) {
              errorMessages.push(parsedData.error);
            }

            if (parsedData.message && parsedData.status_code && parsedData.status_code >= 400) {
              errorMessages.push(parsedData.message);
            }
          }
        }
      }

      // 최종 응답 구성: 완전한 응답이 있으면 우선 사용, 없으면 스트리밍 누적값 사용
      let finalResponse = '';

      if (completeResponse.trim()) {
        // 완전한 응답이 있으면 그것만 사용 (중복 방지)
        finalResponse = completeResponse.trim();
      } else if (streamingContent.trim()) {
        // 완전한 응답이 없으면 스트리밍 누적값 사용
        finalResponse = streamingContent.trim();
      } else if (errorMessages.length > 0) {
        finalResponse = '❌ 처리 중 오류가 발생했습니다:\n\n' + errorMessages.join('\n\n');
      } else {
        // 내용이 없으면 메시지 추가하지 않음
        if (!hasContent) {
          setIsLoading(false);
          return;
        }
        finalResponse = '응답을 받았지만 내용을 파싱할 수 없습니다.';
      }

      // 메시지 업데이트: 중복 방지 강화
      setMessages(prev => {
        const lastMessage = prev[prev.length - 1];

        // 재생성 중인 경우: 원래 위치에 추가
        if (regeneratingMessageIndex !== null) {
          const botMessage: Message = {
            id: Date.now().toString(),
            text: finalResponse,
            isUser: false,
            timestamp: new Date(),
          };
          const newMessages = [...prev];
          newMessages.splice(regeneratingMessageIndex, 0, botMessage);
          setRegeneratingMessageIndex(null);
          return newMessages;
        }

        // 마지막 메시지가 봇 메시지인 경우 - 무조건 업데이트만 (중복 방지)
        if (lastMessage && !lastMessage.isUser) {
          // 정확히 같은 내용이면 업데이트하지 않음
          if (lastMessage.text === finalResponse) {
            return prev;
          }

          // 무조건 업데이트만 하고 추가하지 않음 (중복 방지)
          return [
            ...prev.slice(0, -1),
            {
              ...lastMessage,
              text: finalResponse,
              timestamp: new Date(),
            },
          ];
        }

        // 새 메시지 추가 (마지막 메시지가 사용자 메시지이거나 봇 메시지가 아닌 경우)
        const botMessage: Message = {
          id: Date.now().toString(),
          text: finalResponse,
          isUser: false,
          timestamp: new Date(),
        };
        return [...prev, botMessage];
      });

      // 응답이 완료되면 무조건 로딩 종료
      // 1. event: end가 감지되었거나 (스트리밍 완료)
      // 2. completeResponse가 있는 경우 (완전한 응답을 받았으므로)
      if (isEndEvent || completeResponse.trim()) {
        setIsLoading(false);
        setProgressMessage('');
      }
    },
    onError: (error: ErrorResponse) => {
      let errorText = '채팅 처리 중 오류가 발생했습니다.';

      // 타임아웃 에러인 경우
      if (error.error?.code === 'ECONNABORTED' || error.error?.hscode === 'timeout') {
        errorText = '요청 시간이 초과되었습니다. 다시 시도해주세요.';
      }
      // 네트워크 에러인 경우
      else if (error.error?.code === 'NETWORK_ERROR') {
        errorText = '네트워크 연결을 확인해주세요.';
      }
      // 서버 에러인 경우
      else if (error.error?.message) {
        errorText = `서버 오류: ${error.error.message}`;
      }

      const errorMessage: Message = {
        id: Date.now().toString(),
        text: errorText,
        isUser: false,
        timestamp: new Date(),
      };

      // 재생성 중인 경우: 원래 위치에 에러 메시지 추가
      if (regeneratingMessageIndex !== null) {
        setMessages(prev => {
          const newMessages = [...prev];
          newMessages.splice(regeneratingMessageIndex, 0, errorMessage);
          return newMessages;
        });
        setRegeneratingMessageIndex(null);
      } else {
        setMessages(prev => [...prev, errorMessage]);
      }
      setIsLoading(false);
    },
  });

  /**
   * 팝업 닫기
   */
  const handleClose = () => {
    handleChatReset();
    onClose();
  };

  /**
   * 메시지 전송
   */
  const handleSendMessage = async () => {
    // endPoint 체크 제거하고 하드코딩된 deployId 사용
    if (!inputMessage.trim() || isLoading) {
      return;
    }

    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputMessage,
      isUser: true,
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    const currentMessage = inputMessage;
    setInputMessage('');
    setIsLoading(true);

    const deployId = selectedDeployId || dropdownOptions[0]?.value || '';
    // endPoint에서 실제 agent_id 추출
    let actualAgentId = deployId;

    // endPoint가 URL 형태인 경우 마지막 부분 추출
    if (endPoint && endPoint.includes('/')) {
      const parts = endPoint.split('/');
      actualAgentId = parts[parts.length - 1];
    }

    try {
      // 실제 스트리밍 API 호출
      streamMutation.mutate({
        deployId: actualAgentId, // endPoint에서 추출한 실제 agent_id 사용
        routerPath: '', // 적절한 라우터 경로
        authorization: authorization, // 부모에서 전달받은 토큰 사용
        StreamReq: {
          config: {},
          input: {
            messages: [
              {
                content: currentMessage,
                type: 'human',
              },
            ],
            additional_kwargs: {},
          },
          kwargs: {},
        },
      });
    } catch (error) {
      setIsLoading(false);
    }
  };

  /**
   * 답변 재생성
   */
  const handleRegenerateAnswer = (messageIndex: number) => {
    if (isLoading) {
      return;
    }

    // 클릭한 봇 메시지 확인
    const targetBotMessage = messages[messageIndex];
    if (!targetBotMessage || targetBotMessage.isUser) {
      return;
    }

    // 클릭한 봇 메시지의 이전 사용자 메시지 찾기
    let targetUserMessage = null;
    // let targetUserIndex = -1;

    // 현재 index부터 역순으로 사용자 메시지 찾기
    for (let i = messageIndex - 1; i >= 0; i--) {
      if (messages[i] && messages[i].isUser) {
        targetUserMessage = messages[i];
        // targetUserIndex = i;
        break;
      }
    }

    if (!targetUserMessage) {
      return;
    }

    // 재생성할 봇 메시지만 제거 (질문은 모두 유지)
    setMessages(prev => {
      const newMessages = [...prev];
      newMessages.splice(messageIndex, 1); // 재생성할 답변만 제거
      return newMessages;
    });

    // 재생성 중인 메시지 인덱스 저장 (제거 후 인덱스는 그대로 유지)
    setRegeneratingMessageIndex(messageIndex);
    setIsLoading(true);

    const deployId = selectedDeployId || dropdownOptions[0]?.value || '';
    let actualAgentId = deployId;

    // endPoint가 URL 형태인 경우 마지막 부분 추출
    if (endPoint && endPoint.includes('/')) {
      const parts = endPoint.split('/');
      actualAgentId = parts[parts.length - 1];
    }

    try {
      // 클릭한 메시지로 다시 스트리밍 API 호출
      streamMutation.mutate({
        deployId: actualAgentId,
        routerPath: '',
        authorization: authorization,
        StreamReq: {
          config: {},
          input: {
            messages: [
              {
                content: targetUserMessage.text,
                type: 'human',
              },
            ],
            additional_kwargs: {},
          },
          kwargs: {},
        },
      });
    } catch (error) {
      // console.error('답변 재생성 실패:', error);
      setIsLoading(false);
    }
  };

  /**
   * 채팅 초기화
   */
  const handleChatReset = () => {
    setMessages([]);
    setInputMessage('');
    setProgressMessage('');
    setRegeneratingMessageIndex(null);
  };

  /**
   * 드롭다운 변경 핸들러
   */
  const handleDropdownChange = (value: string) => {
    // 값이 실제로 변경되었고, 기존 대화가 있는 경우에만 확인 알림 표시
    if (value !== selectedDeployId && messages.length > 0) {
      openConfirm({
        title: '안내',
        message: '버전을 변경하시겠습니까?\n기존 진행 대화가 사라집니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          // 확인 시 버전 변경 및 대화 초기화
          setSelectedDeployId(value);
          handleChatReset();
        },
        onCancel: () => {
          // 취소 시 아무것도 하지 않음 (드롭다운은 원래 값으로 유지됨)
        },
      });
    } else {
      // 대화가 없거나 값이 변경되지 않은 경우 바로 변경
      setSelectedDeployId(value);
    }
  };

  return (
    <UIFilter
      isVisible={isOpen}
      onChatReset={handleChatReset}
      onClose={handleClose}
      showDropdown={true}
      dropdownOptions={dropdownOptions}
      onDropdownChange={handleDropdownChange}
      defaultDropdownValue={dropdownOptions[0]?.value || ''}
    >
      <div className='flex-1 flex flex-col h-full'>
        <div className='flex-1 overflow-y-auto p-6 space-y-6'>
          {messages.map((message, index) => (
            <div key={index}>
              {message.isUser ? (
                <div className='flex flex-col items-end space-y-2 question-group'>
                  <div>
                    <div className='max-w-[320px] bg-blue-700 text-white rounded-xl rounded-br-sm px-4 py-3 question-wrap mb-0'>
                      <UITypography variant='body-2' className='secondary-neutral-f text-sb'>
                        {message.text}
                      </UITypography>
                    </div>
                  </div>
                </div>
              ) : (
                <div className='max-w-[368px] mt-[24px]'>
                  <div className='text-sm text-gray-900 leading-5 px-4 py-3 bg-gray-100 rounded-xl rounded-bl-sm'>
                    <UITypography variant='body-2' className='secondary-neutral-800'>
                      {message.text}
                    </UITypography>
                  </div>
                  <div className='w-full flex justify-between items-center mt-[8px] question-sub-wrap'>
                    <UITypography variant='caption-2' className='secondary-neutral-600'>
                      {message.timestamp.toLocaleString()}
                    </UITypography>
                    <Button
                      type='button'
                      onClick={e => {
                        e.preventDefault();
                        e.stopPropagation();
                        handleRegenerateAnswer(index);
                      }}
                      className='flex gap-1 text-xs text-[#373E4D] font-semibold leading-5 cursor-pointer'
                      leftIcon={{ className: 'ic-system-20-reload', children: '' }}
                      disabled={isLoading}
                    >
                      답변 재생성
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ))}
          {/* 로딩 중일 때 말풍선 표시 */}
          {isLoading && (
            <div className='flex flex-col items-start gap-1.5 mt-[24px]'>
              <div className='flex items-end gap-3.5'>
                <div className='flex flex-col gap-1.5'>
                  <div className='card flex max-w-md flex-col items-center gap-2.5 break-words w-[88px] h-11 rotate-0 opacity-100 p-4 rounded-tl-3xl rounded-tr-3xl rounded-br-3xl rounded-bl-sm bg-[#F3F6FB]'>
                    <div className='flex space-x-1'>
                      <span className='inline-block h-[6px] w-[6px] animate-bounce rounded-full bg-gray-500' style={{ animationDelay: '0s' }}></span>
                      <span className='inline-block h-[6px] w-[6px] animate-bounce rounded-full bg-gray-500' style={{ animationDelay: '0.15s' }}></span>
                      <span className='inline-block h-[6px] w-[6px] animate-bounce rounded-full bg-gray-500' style={{ animationDelay: '0.3s' }}></span>
                    </div>
                  </div>
                  <span className='text-2xs font-medium text-gray-500'></span>
                </div>
              </div>
              {progressMessage && <span className='break-words text-2xs font-medium text-gray-500'>{progressMessage}</span>}
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        <div className='p-6'>
          <div className='flex gap-2'>
            <div className='flex-1  min-w-[344px] h-[48px]'>
              <UITextArea2
                value={inputMessage}
                onChange={e => setInputMessage(e.target.value)}
                onKeyDown={e => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendMessage();
                  }
                }}
                placeholder='메시지를 입력하세요'
                rows={1}
                lineType={'single-line'}
                resizable={false}
              />
            </div>
            <Button
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                handleSendMessage();
              }}
              className='cursor-pointer'
            >
              <UIIcon2 className='ic-system-48-chat' />
            </Button>
          </div>
        </div>
      </div>
    </UIFilter>
  );
}
