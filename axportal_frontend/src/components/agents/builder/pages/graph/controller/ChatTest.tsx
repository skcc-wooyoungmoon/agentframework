import { nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom, builderLogState } from '@/components/agents/builder/atoms/logAtom';
import {
  messagesAtom,
  tracingMessagesAtom,
  progressMessageAtom,
  streamingMessageAtom,
  tracingNodeIdAtom,
  tracingBaseInfoAtom,
} from '@/components/agents/builder/atoms/messagesAtom.ts';
import { Chat } from '@/components/agents/builder/common/chat/Chat.tsx';
// import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { ChatLogSidebar } from '@/components/agents/builder/common/chat/ChatLogSidebar.tsx';
import { useStreamLogs } from '@/components/agents/builder/hooks/useStreamLogs';
import { useStreamingChat } from '@/components/agents/builder/hooks/useStreamingChat';
import { useAtom, useSetAtom } from 'jotai/index';
import { useEffect, useRef, useState } from 'react';

interface ChatTestProps {
  isChatVisible: boolean;
  setIsChatVisible?: () => void;
  agentId: string;
  onChatTest?: (userInput: string) => void;
}

const ChatTest = ({ isChatVisible, setIsChatVisible, agentId }: ChatTestProps) => {
  const [, setMessages] = useAtom(messagesAtom);
  const [, setTracingMessages] = useAtom(tracingMessagesAtom);
  const [, setNodes] = useAtom(nodesAtom);
  const [isLogVisible, setIsLogVisible] = useState(false);
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const [, setHasChatTested] = useAtom(hasChatTestedAtom);
  const setBuilderLogState = useSetAtom(builderLogState);
  const [, setProgressMessage] = useAtom(progressMessageAtom);
  const [, setStreamingMessage] = useAtom(streamingMessageAtom);
  const [, setTracingNodeId] = useAtom(tracingNodeIdAtom);
  const [, setTracingBaseInfo] = useAtom(tracingBaseInfoAtom);

  // 🔥 채팅방을 나갔다 들어올 때 노드 상태 초기화
  const prevIsChatVisibleRef = useRef<boolean>(isChatVisible);
  useEffect(() => {
    // 채팅방이 닫혔다가 다시 열릴 때 노드 상태 초기화
    if (!prevIsChatVisibleRef.current && isChatVisible) {
      // 노드 상태를 확실하게 초기화하기 위해 약간의 지연 후 실행
      setTimeout(() => {
        setNodes(prev => {
          const hasNodesToUpdate = prev.some(node => {
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
            return prev; // 변경이 필요 없으면 이전 상태 반환
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
                  logData: [],
                },
              },
            };
          });
        });
      }, 100); // DOM 업데이트 후 실행
    }
    prevIsChatVisibleRef.current = isChatVisible;
  }, [isChatVisible, setNodes]);

  // 스트림 로그 관리 훅
  const { clearStreamLogs } = useStreamLogs();

  // 핵심 채팅 로직 훅
  const { isChatLoading: streamingChatLoading, handleChatTest: streamingHandleChatTest, resetChatState } = useStreamingChat(agentId);

  // 🚨 채팅 삭제 시 메시지 상태 완전 초기화
  const clearChatMessages = () => {
    resetChatState(); // 🔥 로딩 상태 및 빌더 로그 초기화
    setMessages([]);
    setTracingMessages([]); // 🔥 채팅 테스트 로그 초기화
    setHasChatTested(false); // 🔥 채팅 테스트 상태 초기화
    setBuilderLogState([]); // 🔥 빌더 로그 상태 초기화
    setProgressMessage(''); // 🔥 진행 메시지 초기화
    setStreamingMessage(''); // 🔥 스트리밍 메시지 초기화
    setTracingNodeId([]); // 🔥 app 파일 방식: 빈 배열로 초기화
    setTracingBaseInfo(null); // 🔥 추적 기본 정보 초기화
    clearStreamLogs(); // 🔥 스트림 로그 초기화

    // 모든 노드 상태 초기화 (종, 테두리 색상 제거)
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
              isRunning: false,
              isCompleted: false,
              hasError: false,
              logData: [],
            },
          },
        };
      });
    });
  };

  const handleClose = () => {
    // 🔥 채팅방을 나갈 때 세션 정리 (채팅 히스토리 및 빌더 로그 초기화)
    setIsLogVisible(false);
    
    // 채팅 세션 정리
    resetChatState();
    setMessages([]);
    setTracingMessages([]);
    setHasChatTested(false);
    setBuilderLogState([]);
    setProgressMessage('');
    setStreamingMessage('');
    setTracingNodeId([]);
    setTracingBaseInfo(null);
    clearStreamLogs();
    
    // 노드 상태 초기화
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
              isRunning: false,
              isCompleted: false,
              hasError: false,
              logData: [],
            },
          },
        };
      });
    });

    if (setIsChatVisible) {
      setIsChatVisible();
    }
  };

  // 컴포넌트 언마운트 시 메시지 초기화 제거 (채팅 히스토리 유지)
  // useEffect(() => {
  //   return () => {
  //     clearChatMessages();
  //   };
  // }, []);

  // 기존 handleChatTest 함수를 useStreamingChat 훅으로 대체
  const handleChatTest = async (userInput?: string, isRegenerate?: boolean) => {
    return streamingHandleChatTest(userInput, isRegenerate);
  };

  // 로딩 상태는 훅에서 관리
  const isChatLoading = streamingChatLoading;

  return (
    <>
      <div className={`absolute right-1 top-16 bottom-4 z-50 w-130 ${isChatVisible ? '' : 'hidden'}`} style={{ height: 'calc(100% - 154px)' }}>
        <Chat
          isVisible={isChatVisible}
          onClose={handleClose}
          offset={200}
          title={'Chat Test'}
          onChatTest={handleChatTest}
          onClearChat={clearChatMessages}
          isQueryResponse={false}
          isLoading={isChatLoading}
          isChatLoading={isChatLoading}
          agentId={agentId}
          onOpenLog={() => setIsLogVisible(true)}
        />
      </div>

      <ChatLogSidebar isVisible={isLogVisible} onClose={() => setIsLogVisible(false)} agentId={agentId} hasChatTested={hasChatTested} />
    </>
  );
};

export default ChatTest;
