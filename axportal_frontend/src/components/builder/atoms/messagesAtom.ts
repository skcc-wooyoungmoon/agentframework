import type { MessageFormat } from '@/components/builder/types/messageFormat.ts';
import { atom } from 'jotai';

export const messagesAtom = atom<MessageFormat[]>([]);
export const regenerateAtom = atom<string>('');
export const regenerateTargetIndexAtom = atom<number>(-1);

export const addMessageAtom = atom(
  get => get(messagesAtom),
  (get, set, newMessage: MessageFormat) => {
    const messages = get(messagesAtom);
    set(messagesAtom, [...messages, newMessage]);
  }
);

export const addRegenerationAtom = atom(
  get => get(messagesAtom),
  (
    get,
    set,
    payload: {
      messageIndex: number;
      regeneration: { content: string; time: string; elapsedTime?: number };
    }
  ) => {
    const messages = get(messagesAtom);
    const updatedMessages = messages.map((message, index) => {
      if (index === payload.messageIndex && message.type === 'ai') {
        return {
          ...message,
          regenerations: [...(message?.regenerations || []), payload.regeneration],
          regen: true, // regen: true 설정
        };
      }
      return message;
    });
    set(messagesAtom, updatedMessages);
  }
);

export const updateLastRegenerationAtom = atom(
  get => get(messagesAtom),
  (
    get,
    set,
    payload: {
      messageIndex: number;
      content: string;
      time?: string;
      elapsedTime?: number;
    }
  ) => {
    const messages = get(messagesAtom);
    const updatedMessages = messages.map((message, index) => {
      if (index === payload.messageIndex && message.type === 'ai' && message?.regenerations?.length) {
        const regenerations = [...(message?.regenerations || [])];
        const lastIndex = regenerations.length - 1;
        regenerations[lastIndex] = {
          ...regenerations[lastIndex],
          content: payload.content,
          ...(payload.time && { time: payload.time }),
          ...(payload.elapsedTime && { elapsedTime: payload.elapsedTime }),
        };
        return {
          ...message,
          regenerations,
        };
      }
      return message;
    });
    set(messagesAtom, updatedMessages);
  }
);

// 메시지 삭제 atom (인덱스로 삭제)
export const removeMessageAtom = atom(
  get => get(messagesAtom),
  (get, set, messageIndex: number) => {
    const messages = get(messagesAtom);
    const updatedMessages = messages.filter((_, index) => index !== messageIndex);
    set(messagesAtom, updatedMessages);
  }
);

// 메시지 교체 atom (인덱스 위치에 새 메시지로 교체)
export const replaceMessageAtom = atom(
  get => get(messagesAtom),
  (get, set, payload: { messageIndex: number; newMessage: MessageFormat }) => {
    const messages = get(messagesAtom);
    const updatedMessages = messages.map((message, index) => {
      if (index === payload.messageIndex) {
        return payload.newMessage;
      }
      return message;
    });
    set(messagesAtom, updatedMessages);
  }
);

// human 메시지의 regen을 true로 설정하는 atom
export const setHumanRegenAtom = atom(
  get => get(messagesAtom),
  (get, set, humanIndex: number) => {
    const messages = get(messagesAtom);
    const updatedMessages = messages.map((message, index) => {
      if (index === humanIndex && message.type === 'human') {
        return {
          ...message,
          regen: true,
        };
      }
      return message;
    });
    set(messagesAtom, updatedMessages);
  }
);

// 모든 human 메시지의 regen을 false로 리셋하는 atom
export const resetAllHumanRegenAtom = atom(
  get => get(messagesAtom),
  (get, set) => {
    const messages = get(messagesAtom);
    const updatedMessages = messages.map(message => {
      if (message.type === 'human' && message.regen === true) {
        return {
          ...message,
          regen: false,
        };
      }
      return message;
    });
    set(messagesAtom, updatedMessages);
  }
);

export const tracingMessagesAtom = atom({
  callback: '',
  nodeId: '',
  log: '',
});

export const tracingNodeIdAtom = atom<string[]>([]);
export const progressMessageAtom = atom<string>('');
export const streamingMessageAtom = atom<string>('');

export const tracingBaseInfoAtom = atom({
  graphId: '',
  projectId: '',
});
