import { type MessageFormat } from '@/components/agents/builder/types/messageFormat';
import { type Message } from '@/components/agents/builder/atoms/messagesAtom';

/**
 * MessageFormat을 Message로 변환하는 헬퍼 함수
 * @param messageFormat 변환할 MessageFormat 객체
 * @returns 변환된 Message 객체
 */
export const convertMessageFormatToMessage = (messageFormat: MessageFormat): Message => {
  return {
    id: messageFormat.id,
    content: messageFormat.content,
    role: messageFormat.type === 'human' ? 'user' : 'assistant',
    timestamp: messageFormat.time || new Date().toISOString(),
    type: messageFormat.type,
    regen: messageFormat.regen,
    time: messageFormat.time,
    elapsedTime: messageFormat.elapsedTime,
  };
};
