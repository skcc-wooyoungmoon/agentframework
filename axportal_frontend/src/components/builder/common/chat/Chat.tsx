import React, { useCallback, useEffect, useRef, useState } from 'react';

import { useAtom } from 'jotai';

import { nodesAtom } from '@/components/builder/atoms/AgentAtom.ts';
import {
  addMessageAtom,
  messagesAtom,
  progressMessageAtom,
  regenerateAtom,
  regenerateTargetIndexAtom,
  streamingMessageAtom,
  tracingNodeIdAtom,
} from '@/components/builder/atoms/messagesAtom.ts';
import { DefaultTooltip } from '@/components/builder/common/tooltip/Tooltip.tsx';
import { ChatType } from '@/components/builder/types/Agents';
import type { MessageFormat } from '@/components/builder/types/messageFormat.ts';

import { MessageIn } from './MessageIn.tsx';
import { MessageOut } from './MessageOut.tsx';

// localStorage 관련 유틸리티 함수들
const CHAT_HISTORY_KEY = 'chat_message_history';
const MAX_HISTORY_SIZE = 50;

const loadMessageHistory = (): string[] => {
  try {
    const history = localStorage.getItem(CHAT_HISTORY_KEY);
    return history ? JSON.parse(history) : [];
  } catch (error) {
    console.error('Failed to load message history:', error);
    return [];
  }
};

const saveMessageHistory = (history: string[]) => {
  try {
    const trimmedHistory = history.slice(-MAX_HISTORY_SIZE);
    localStorage.setItem(CHAT_HISTORY_KEY, JSON.stringify(trimmedHistory));
  } catch (error) {
    console.error('Failed to save message history:', error);
  }
};

const addToHistory = (message: string, currentHistory: string[]): string[] => {
  if (!message.trim()) return currentHistory;

  // 중복 제거 (같은 메시지가 이미 있으면 제거 후 최신으로 추가)
  const filteredHistory = currentHistory.filter(item => item !== message.trim());
  const newHistory = [...filteredHistory, message.trim()];

  return newHistory.slice(-MAX_HISTORY_SIZE);
};

const Chat = ({
  isVisible,
  onClose,
  offset: _offset, // 사용하지 않음 (flexbox로 높이 자동 계산)
  title,
  onChatTest,
  isQueryResponse,
  isLoading = false,
  onClearError,
  onAddPanel,
  onClearChat,
  // panels,
}: {
  isVisible: boolean;
  onClose: () => void;
  offset: number;
  title: string;
  onChatTest: () => void;
  isQueryResponse: boolean;
  isLoading?: boolean;
  agentId: string;
  onClearError?: () => void;
  onAddPanel?: (type: 'log-viewer') => void;
  onClearChat?: () => void;
  panels?: Array<{
    id: string;
    type: 'log-viewer';
    title: string;
  }>;
}) => {
  const [regenerateQuery, setRegenerateQuery] = useAtom(regenerateAtom);
  const [regenerateTargetIndex] = useAtom(regenerateTargetIndexAtom);
  const headerRef = useRef<HTMLDivElement>(null);
  const messagesRef = useRef<HTMLDivElement>(null);
  const messageUpdateRef = useRef(false);
  const footerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null); // Add ref for textarea
  const containerRef = useRef<HTMLDivElement>(null);
  const [input, setInput] = useState('');
  // const [isComposing, setIsComposing] = useState(false);
  const [, setIsComposing] = useState(false);
  const [messageHistory, setMessageHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);
  const [, setNodes] = useAtom(nodesAtom);
  const [, setTracingNodeId] = useAtom(tracingNodeIdAtom);
  const [progressMessage, setProgressMessage] = useAtom(progressMessageAtom);
  const [streamingMessage] = useAtom(streamingMessageAtom);
  const isChatLoading = isLoading;

  // 컴포넌트 초기화 시 메시지 히스토리 로드
  useEffect(() => {
    const loadedHistory = loadMessageHistory();
    setMessageHistory(loadedHistory);
  }, []);

  useEffect(() => {
    if (isLoading) {
      setProgressMessage('처리중...');
    } else {
      setProgressMessage('대기중');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading]);

  const [messages, setMessages] = useAtom(messagesAtom);
  const [, addMessage] = useAtom(addMessageAtom);

  const handleRegenerateSend = useCallback(
    (inputQuery: string) => {
      // inputQuery가 문자열이 아니면 문자열로 변환
      const queryString = typeof inputQuery === 'string' ? inputQuery : String(inputQuery || '');
      if (queryString.trim().length <= 0) {
        return;
      }

      // 재생성 시 에러 메시지 초기화
      if (onClearError) {
        onClearError();
      }

      setTracingNodeId([]);

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

      // 재생성 시에는 새 human 메시지를 추가하지 않음
      // MessageIn.tsx에서 setHumanRegen으로 기존 human의 regen을 true로 설정함
      // messages가 변경되었으므로 useEffect에서 onChatTest가 호출됨

      setInput('');
      setRegenerateQuery('');

      setTimeout(() => {
        if (messagesRef.current) {
          messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
        }
      }, 0);
    },
    [setTracingNodeId, setNodes, setInput, setRegenerateQuery, onClearError]
  );

  useEffect(() => {
    // regenerateQuery가 유효한 문자열일 때만 포커스 및 재생성 처리
    const queryString = typeof regenerateQuery === 'string' ? regenerateQuery : String(regenerateQuery || '');
    if (queryString.trim().length > 0) {
      if (inputRef.current) {
        inputRef.current.focus(); // Focus the input (재생성 시에만)
      }
      handleRegenerateSend(regenerateQuery);
      // 재생성 시에는 직접 onChatTest 호출
      if (onChatTest) {
        onChatTest();
      }
    }
  }, [regenerateQuery, handleRegenerateSend, onChatTest]);

  useEffect(() => {
    if (messageUpdateRef.current) {
      messageUpdateRef.current = false;
      if (onChatTest) {
        onChatTest();
      }
    }

    if (isQueryResponse) {
      updateChat();
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [messages]);

  // 간단한 마크다운 파싱
  const parseMarkdown = (text: string) => {
    if (!text || typeof text !== 'string') {
      return '';
    }
    return text
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // **bold**
      .replace(/\*(.*?)\*/g, '<em>$1</em>') // *italic*
      .replace(/`(.*?)`/g, '<code>$1</code>') // `code`
      .replace(/\n/g, '<br />'); // 줄바꿈
  };

  // 현재 시간 가져오기
  const getCurrentTime = () => {
    return new Date().toLocaleTimeString('en-GB', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    });
  };

  const handleFormInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setInput(e.target.value);
    // 사용자가 직접 입력하면 히스토리 인덱스 초기화
    setHistoryIndex(-1);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    const cursorPosition = target.selectionStart;
    const isAtStart = cursorPosition === 0;

    // Enter 키 처리
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      // 로딩 중이거나 입력값이 없으면 전송하지 않음
      if (!isLoading && input.trim().length > 0) {
        handleSend();
      }
      return;
    }

    // 커서가 맨 앞에 있을 때만 화살표 키로 히스토리 탐색
    if (isAtStart) {
      if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (messageHistory?.length > 0) {
          const newIndex = Math.min(historyIndex + 1, messageHistory?.length - 1);
          setHistoryIndex(newIndex);
          const historyMessage = messageHistory[messageHistory.length - 1 - newIndex];
          setInput(historyMessage);

          // 텍스트를 설정한 후 커서를 맨 앞으로 이동 
          setTimeout(() => {
            if (target) {
              target.setSelectionRange(0, 0);
            }
          }, 0);
        }
      } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (historyIndex > 0) {
          const newIndex = historyIndex - 1;
          setHistoryIndex(newIndex);
          const historyMessage = messageHistory[messageHistory.length - 1 - newIndex];
          setInput(historyMessage);

          // 텍스트를 설정한 후 커서를 맨 앞으로 이동
          setTimeout(() => {
            if (target) {
              target.setSelectionRange(0, 0);
            }
          }, 0);
        } else if (historyIndex === 0) {
          setHistoryIndex(-1);
          setInput('');
        }
      }
    }
  };

  const handleSend = () => {
    // 로딩 중이거나 입력값이 없으면 전송하지 않음
    if (isLoading || input.trim().length <= 0) {
      return;
    }

    // 메시지 전송 시 에러 메시지 초기화
    if (onClearError) {
      onClearError();
    }

    // 메시지 히스토리에 추가
    const newHistory = addToHistory(input, messageHistory);

    setMessageHistory(newHistory);
    saveMessageHistory(newHistory);
    setHistoryIndex(-1); // 히스토리 인덱스 초기화

    setTracingNodeId([]);

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

    const time = getCurrentTime();
    const message: MessageFormat = {
      id: `${Date.now()}`, // 고유 ID 추가
      content: input,
      time: time,
      type: ChatType.HUMAN,
      regen: false,
    };

    addMessage(message);
    messageUpdateRef.current = true;
    setInput('');
    setRegenerateQuery('');

    setTimeout(() => {
      if (messagesRef.current) {
        messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
      }
    }, 0);
  };

  const updateChat = () => {
    setTimeout(() => {
      if (messagesRef.current) {
        messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
      }
    }, 0);
  };

  const handleOpenChatLog = () => {
    if (onAddPanel) {
      onAddPanel('log-viewer');
    }
  };

  const buildHeader = () => {
    return (
      <div className={`transform transition-opacity ease-out ${isVisible ? 'translate-y-0 opacity-100' : 'translate-y-5 opacity-0'}`}>
        <div className='flex items-center justify-between gap-2 px-3 py-1.5 text-sm font-semibold text-gray-900'>
          <div className='flex items-center gap-2'>
            <span>{title}</span>
            <button className='btn btn-sm' onClick={handleOpenChatLog} title='채팅 로그 보기'>
              <span>
                <img alt='ico-system-24-outline-gray-log' className='w-[24px] h-[24px]' src='/assets/images/system/ico-system-24-outline-gray-log.svg' />
              </span>
            </button>
          </div>

          <div className='flex items-center gap-2'>
            <button
              className='btn btn-sm'
              onClick={() => {
                setMessages([]);
                setTracingNodeId([]);
                if (onClearError) {
                  onClearError();
                }
                // 패널 데이터도 초기화
                if (onClearChat) {
                  onClearChat();
                }
              }}
              title='Clear Chat'
            >
              <span>채팅 초기화</span>
            </button>
            <button onClick={onClose} className='shrink-0 w-[24px] h-[24px] p-0 bg-transparent border-0 cursor-pointer hover:opacity-70 transition-opacity' title='채팅 닫기'>
              <img
                alt='ico-system-32-AppBar-close'
                className='w-[24px] h-[24px]'
                src='/assets/images/system/ico-system-32-AppBar-close.svg'
                style={{ filter: isChatLoading ? 'brightness(0) saturate(100%) invert(36%) sepia(96%) saturate(2345%) hue-rotate(201deg) brightness(99%) contrast(101%)' : 'none' }}
              />
            </button>
          </div>
        </div>
        <div className='border-b border-b-gray-200'></div>
      </div>
    );
  };

  const buildMessages = () => {
    return (
      <div className='flex flex-col gap-3 py-3'>
        {messages.length === 0 && <div className='mb-4 text-center text-2sm text-gray-700'>{'메시지가 없습니다'}</div>}

        {messages.map((message, index) => {
          if (message.type === ChatType.HUMAN) {
            // human 메시지는 regen 여부와 상관없이 항상 표시
            return <MessageOut key={index} text={message.content} time={message.time || ''} />;
          } else if (message.type === ChatType.AI) {
            // regenerating 중인 메시지는 표시하지 않음
            const shouldHide = regenerateTargetIndex === index;
            if (shouldHide) return;

            // regenerations가 있고 마지막 regeneration에 content가 있으면 그것을 표시
            const hasRegenerations = message.regenerations && message.regenerations.length > 0;
            const lastRegeneration = hasRegenerations && message.regenerations ? message.regenerations[message.regenerations.length - 1] : null;
            const displayText = lastRegeneration && lastRegeneration.content ? lastRegeneration.content : message.content;
            const displayTime = lastRegeneration && lastRegeneration.time ? lastRegeneration.time : message.time || '';
            const displayElapsedTime = lastRegeneration && lastRegeneration.elapsedTime !== undefined ? lastRegeneration.elapsedTime : message.elapsedTime;

            return <MessageIn key={index} text={displayText} time={displayTime} elapsedTime={displayElapsedTime} index={index} isRegenerating={Boolean(message.regen)} />;
          }
          return null;
        })}
        {isLoading && streamingMessage === '' && (
          <div className='flex flex-col items-start gap-1 px-3'>
            <div className='flex items-end gap-3'>
              <div className='flex flex-col gap-1'>
                {/* <div className='card flex max-w-md flex-col items-start gap-2.5 break-words rounded-bl-none bg-gray-100 p-3 text-2sm font-medium text-gray-700 shadow-none'> */}
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
            <span className='break-words text-2xs font-medium text-gray-500'>{progressMessage}</span>
          </div>
        )}
        {isLoading && streamingMessage !== '' && (
          <div className='flex items-end gap-3 px-3'>
            <div className='flex flex-col gap-1.5'>
              <div className='card flex flex-col items-center gap-2 rounded-bl-none bg-gray-100 p-2.5 text-2sm font-medium text-gray-700 shadow-none'>
                <div
                  dangerouslySetInnerHTML={{
                    __html: parseMarkdown(streamingMessage),
                  }}
                  style={{
                    lineHeight: '1.5',
                  }}
                />
              </div>
              <span className='text-2xs font-medium text-gray-500'></span>
            </div>
          </div>
        )}
      </div>
    );
  };

  const buildForm = () => {
    return (
      <div>
        <div className='relative mx-3 mb-3'>
          {/* <div className='flex items-center gap-3 rounded-lg border border-gray-300 bg-white p-3 shadow-sm'> */}
          <div className='flex items-center gap-2'>
            <textarea
              ref={inputRef}
              className='flex-1 !h-[80px] px-[10px] py-[8px] rounded-lg border-[1px] !border-[#DCE2ED] resize-none scrollbar-thin'
              onChange={handleFormInput}
              onCompositionStart={() => setIsComposing(true)}
              onCompositionEnd={() => setIsComposing(false)}
              onKeyDown={handleKeyDown}
              placeholder={'메시지를 입력 해주세요.'}
              value={input}
              rows={2}
            />
            <DefaultTooltip title={'전송'} placement={'top'}>
              <span className='w-[40px] he-[40px] flex-shrink-0' style={{ display: 'inline-block' }}>
                <button className='w-[40px] he-[40px]' onClick={handleSend} disabled={isLoading || input.trim().length === 0}>
                  <img alt='ico-data-message-48' className='w-[40px] h-[40px]' src='/assets/images/data/ico-data-message-48.svg' />
                </button>
              </span>
            </DefaultTooltip>
          </div>
        </div>
      </div>
    );
  };

  if (!isVisible) {
    return null;
  }

  return (
    <div
      ref={containerRef}
      className='chat-container card flex flex-col shadow h-full'
      style={{
        backgroundColor: 'white !important',
        background: 'white !important',
      }}
    >
      {/* WebSocket 연결 제거 - 필요시 별도로 구현 */}
      <div ref={headerRef} className='chat-header' style={{ backgroundColor: 'white' }}>
        {buildHeader()}
      </div>
      <div
        ref={messagesRef}
        className='chat-messages-container scrollable-y-auto flex-1 overflow-y-auto'
        style={{
          backgroundColor: 'white',
        }}
      >
        {buildMessages()}
      </div>
      <div ref={footerRef} className='chat-input-container flex-shrink-0' style={{ backgroundColor: 'white' }}>
        {buildForm()}
      </div>
    </div>
  );
};
export { Chat };
