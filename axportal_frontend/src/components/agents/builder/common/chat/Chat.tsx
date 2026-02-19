import React, { useEffect, useRef, useState } from 'react';

import { useAtom, useSetAtom } from 'jotai';

import { nodesAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { authServices } from '@/services/auth/auth.non.services';
import { authUtils } from '@/utils/common';
import {
  messagesAtom,
  progressMessageAtom,
  regenerateAtom,
  streamingMessageAtom,
  tracingMessagesAtom,
  tracingNodeIdAtom,
} from '@/components/agents/builder/atoms/messagesAtom.ts';
import { builderLogState } from '@/components/agents/builder/atoms/logAtom.ts';
import { DefaultTooltip } from '@/components/agents/builder/common/tooltip/Tooltip.tsx';
import { ChatType } from '@/components/agents/builder/types/Agents';

import { MessageIn } from './MessageIn.tsx';
import { MessageOut } from './MessageOut.tsx';

const getHeight = (element: HTMLElement | null): number => {
  if (!element) return 0;
  const rect = element.getBoundingClientRect();
  return rect.height;
};

const parseInlineMarkdown = (text: string): React.ReactNode[] => {
  if (!text) return [];

  const parts: React.ReactNode[] = [];
  const regex = /(\*\*.*?\*\*|\*.*?\*|`.*?`|\n)/g;
  let lastIndex = 0;
  let match: RegExpExecArray | null;
  let keyIndex = 0;

  while ((match = regex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      parts.push(text.slice(lastIndex, match.index));
    }

    const token = match[0];
    if (token.startsWith('**') && token.endsWith('**')) {
      parts.push(<strong key={keyIndex++}>{token.slice(2, -2)}</strong>);
    } else if (token.startsWith('*') && token.endsWith('*')) {
      parts.push(<em key={keyIndex++}>{token.slice(1, -1)}</em>);
    } else if (token.startsWith('`') && token.endsWith('`')) {
      parts.push(<code key={keyIndex++}>{token.slice(1, -1)}</code>);
    } else if (token === '\n') {
      parts.push(<br key={keyIndex++} />);
    }

    lastIndex = regex.lastIndex;
  }

  if (lastIndex < text.length) {
    parts.push(text.slice(lastIndex));
  }

  return parts;
};

const ParsedMarkdown = ({ text }: { text: string }): React.ReactElement | null => {
  if (!text || typeof text !== 'string') {
    return null;
  }

  const redactedReasoningRegex = /<think>([\s\S]*?)<\/redacted_reasoning>/gi;
  if (redactedReasoningRegex.test(text)) {
    redactedReasoningRegex.lastIndex = 0;
    const matches = Array.from(text.matchAll(redactedReasoningRegex));

    if (matches.length > 0) {
      const reasoningContent = matches.map(match => match[1]).join('\n\n');
      const answerContent = text.replace(redactedReasoningRegex, '').trim();

      return (
        <>
          <div style={{
            backgroundColor: '#f9fafb',
            border: '1px solid #e5e7eb',
            borderRadius: 8,
            padding: 12,
            margin: '12px 0',
          }}>
            <div style={{
              fontSize: '.875rem',
              fontWeight: 600,
              color: '#6B7280',
              marginTop: 8,
              fontFamily: "Consolas, 'Monaco', 'Courier New', monospace",
            }}>{'🔍 <redacted_reasoning>'}</div>
            <div style={{
              backgroundColor: 'ffffff',
              border: '1px solid #d1d5db',
              borderRadius: 4,
              padding: 12,
              color: '#374151',
              fontSize: '.875rem',
              lineHeight: 1.6,
              whiteSpace: 'pre-wrap',
              wordWrap: 'break-word',
            }}>
              {parseInlineMarkdown(reasoningContent)}
            </div>
            <div style={{
              fontSize: '.875rem',
              fontWeight: 600,
              color: '#6B7280',
              marginTop: 8,
              fontFamily: "'Consolas', 'Monaco', 'Courier New', monospace",
            }}>{'</redacted_reasoning>'}</div>
          </div>
          {answerContent && (
            <>
              <div style={{
                height: 1,
                backgroundColor: '#E5E7EB',
                margin: '16px 0',
              }} />
              <div style={{ marginTop: 12 }}>
                {parseInlineMarkdown(answerContent)}
              </div>
            </>
          )}
        </>
      );
    }
  }

  return <>{parseInlineMarkdown(text)}</>;
};

const CHAT_MESSAGES_HEIGHT = 360;

const useViewport = () => {
  const [viewportHeight, setViewportHeight] = useState(window.innerHeight);

  useEffect(() => {
    const handleResize = () => {
      setViewportHeight(window.innerHeight);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return [viewportHeight];
};

const Chat = ({
  isVisible,
  onClose,
  offset,
  title,
  onChatTest,
  onClearChat,
  isQueryResponse,
  isLoading = false,
  isChatLoading = false,
  agentId: _agentId,
  onOpenLog,
}: {
  isVisible: boolean;
  onClose: () => void;
  offset: number;
  title: string;
  onChatTest: (userInput: string, isRegenerate?: boolean) => void;
  onClearChat?: () => void;
  isQueryResponse: boolean;
  isLoading?: boolean;
  isChatLoading?: boolean;
  agentId: string;
  onOpenLog?: () => void;
}) => {
  const [regenerateQuery, setRegenerateQuery] = useAtom(regenerateAtom);
  const headerRef = useRef<HTMLDivElement>(null);
  const messagesRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const messageUpdateRef = useRef(false);
  const footerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null); // Add ref for textarea
  const [scrollableHeight, setScrollableHeight] = useState<number>(CHAT_MESSAGES_HEIGHT);
  const [input, setInput] = useState('');
  const [isComposing, setIsComposing] = useState(false);
  const [viewportHeight] = useViewport();
  const [_tracingMessages, _setTracingMessages] = useAtom(tracingMessagesAtom);
  const [, setNodes] = useAtom(nodesAtom);
  const [, setTracingNodeId] = useAtom(tracingNodeIdAtom);
  const [progressMessage, setProgressMessage] = useAtom(progressMessageAtom);
  const [streamingMessage, setStreamingMessage] = useAtom(streamingMessageAtom);
  const setBuilderLogState = useSetAtom(builderLogState);
  const [pendingRegenerateIndex, setPendingRegenerateIndex] = useState<number | null>(null);

  useEffect(() => {
    if (isLoading) {
      setProgressMessage('처리중...');
    } else {
      setProgressMessage('대기중');
    }
  }, [isLoading]);

  const [messages, setMessages] = useAtom(messagesAtom);

  const calculateScrollableHeight = () => {
    const containerHeight = containerRef.current?.clientHeight ?? viewportHeight - offset;
    let availableHeight = containerHeight;

    if (headerRef.current) availableHeight -= getHeight(headerRef.current);
    if (footerRef.current) availableHeight -= getHeight(footerRef.current);

    setScrollableHeight(Math.max(CHAT_MESSAGES_HEIGHT, availableHeight));
  };

  useEffect(() => {
    calculateScrollableHeight();
  }, [viewportHeight, isQueryResponse, isVisible, messages.length, isLoading]);

  useEffect(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
    if (regenerateQuery.trigger && regenerateQuery.query) {
      const answerIndex = regenerateQuery.answerIndex ?? null;
      if (answerIndex !== null) {
        setMessages(prev => {
          if (answerIndex < 0 || answerIndex >= prev.length) {
            return prev;
          }
          const target = prev[answerIndex];
          if (!target || (target.type !== ChatType.AI && target.role !== 'assistant')) {
            return prev;
          }
          return prev.filter((_, idx) => idx !== answerIndex);
        });
        setPendingRegenerateIndex(answerIndex);
        handleRegenerateSend(regenerateQuery.query);
      } else {
        handleRegenerateSend(regenerateQuery.query);
      }
    }
  }, [regenerateQuery]);

  useEffect(() => {
    if (pendingRegenerateIndex !== null) {
      const targetMessage = messages[pendingRegenerateIndex];
      if (targetMessage && (targetMessage.type === ChatType.AI || targetMessage.role === 'assistant')) {
        setPendingRegenerateIndex(null);
      }
    }
  }, [messages, pendingRegenerateIndex]);

  const handleRegenerateSend = (inputQuery: string) => {
    if (inputQuery.trim().length <= 0) {
      return;
    }

    setInput('');
    setTracingNodeId([]);
    setRegenerateQuery(prev => ({
      ...prev,
      trigger: false,
    }));

    if (onChatTest) {
      onChatTest(inputQuery, true);
    }

    if (messagesRef.current) {
      messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
    }
  };

  const messagesLengthRef = useRef<number>(0);

  useEffect(() => {
    if (messages.length === messagesLengthRef.current) {
      return;
    }
    messagesLengthRef.current = messages.length;

    if (messageUpdateRef.current) {
      messageUpdateRef.current = false;
    }

    if (isQueryResponse) {
      updateChat();
    }
  }, [messages.length, isQueryResponse]);

  const handleFormInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setInput(e.target.value);
  };

  const handleSend = async () => {
    const userInput = input.trim();

    if (userInput.length <= 0) {
      return;
    }

    if (isChatLoading) return;

    try {
      await authServices.refresh();
    } catch {
      authUtils.clearTokens();
      window.location.href = '/login';
      return;
    }

    setTracingNodeId([]);
    setInput('');
    setRegenerateQuery({ trigger: false, query: '', history: undefined });

    onChatTest(userInput);

    if (messagesRef.current) {
      messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
    }
  };

  const updateChat = () => {
    if (messagesRef.current) {
      messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
    }
  };

  const buildHeader = () => {
    const handleOpenChatLog = () => {
      if (onOpenLog) {
        onOpenLog();
      }
    };

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
                _setTracingMessages([]);
                setBuilderLogState([]);
                setProgressMessage('');
                setStreamingMessage('');
                setTracingNodeId([]);
                setNodes(prev =>
                  prev.map(node => ({
                    ...node,
                    data: {
                      ...node.data,
                      innerData: {
                        ...node.data.innerData,
                        isRun: false,
                        logData: [],
                      },
                    },
                  }))
                );
                onClearChat?.();
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
          if (message.type === ChatType.HUMAN && !message.regen) {
            return <MessageOut key={index} text={message.content} time={message.time || ''} />;
          } else if (message.type === ChatType.AI) {
            return (
              <MessageIn key={index} text={message.content} time={message.time || ''} elapsedTime={message.elapsedTime} index={index} isRegenerating={Boolean(message.regen)} />
            );
          }
          return null;
        })}
        {isLoading && streamingMessage === '' && (
          <div className='flex flex-col items-start gap-1 px-3'>
            <div className='flex items-end gap-3'>
              <div className='flex flex-col gap-1'>
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
          <div className='flex items-end gap-3.5 px-5'>
            <div className='flex flex-col gap-1.5 max-w-[80%]'>
              <div className='card flex flex-col gap-2.5 rounded-bl-none bg-gray-100 p-3 text-2sm font-medium text-gray-700 shadow-none opacity-100 py-3 px-4 rounded-tl-3xl rounded-tr-3xl rounded-br-3xl rounded-bl-sm bg-[#F3F6FB] font-normal text-sm leading-5 tracking-[-0.01%] align-middle'>
                <div
                  className='markdown-content break-words'
                  style={{
                    lineHeight: '1.6',
                    maxWidth: '100%',
                    wordWrap: 'break-word',
                    overflowWrap: 'break-word',
                    whiteSpace: 'pre-wrap',
                  }}
                >
                  <ParsedMarkdown text={streamingMessage} />
                </div>
              </div>
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
          <div className='flex items-center gap-2'>
            <textarea
              ref={inputRef}
              className='flex-1 !h-[40px] px-[10px] py-[8px] rounded-lg border-[1px] !border-[#DCE2ED] resize-none scrollbar-thin'
              onChange={handleFormInput}
              onCompositionStart={() => setIsComposing(true)}
              onCompositionEnd={() => {
                setIsComposing(false);
              }}
              onKeyDown={(e: React.KeyboardEvent<HTMLTextAreaElement>) => {
                if (e.key === 'Enter' && !e.shiftKey && !isComposing) {
                  e.preventDefault();
                  handleSend();
                }
              }}
              onInput={(e: React.FormEvent<HTMLTextAreaElement>) => {
                const target = e.target as HTMLTextAreaElement;
                target.style.height = 'auto';
                target.style.height = Math.min(target.scrollHeight, 120) + 'px';
              }}
              placeholder={'메시지를 입력 해주세요.'}
              value={input}
              rows={1}
            />
            <DefaultTooltip title={'전송'} placement={'top'}>
              <span className='w-[40px] he-[40px] flex-shrink-0 relative top-[3px]' style={{ display: 'inline-block' }}>
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
      className='chat-container card flex h-full flex-col shadow'
      style={{
        backgroundColor: 'white !important',
        background: 'white !important',
        height: '100%',
      }}
    >
      <div ref={headerRef} className='chat-header' style={{ backgroundColor: 'white' }}>
        {buildHeader()}
      </div>
      <div
        ref={messagesRef}
        className='chat-messages-container scrollable-y-auto flex-1'
        style={{
          backgroundColor: 'white',
          overflowY: 'auto',
          maxHeight: `${scrollableHeight}px`,
          minHeight: `${scrollableHeight}px`,
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
