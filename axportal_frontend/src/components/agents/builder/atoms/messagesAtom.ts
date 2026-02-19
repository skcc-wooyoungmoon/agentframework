import { atom } from 'jotai';

export interface Message {
  id: string;
  content: string;
  role: 'user' | 'assistant' | 'system';
  timestamp: string;
  metadata?: any;
  type?: string;
  regen?: boolean;
  time?: string;
  elapsedTime?: number;
}

export const messagesAtom = atom<Message[]>([]);

export const addMessageAtom = atom(
  null,
  (get, set, message: Message) => {
    const current = get(messagesAtom);
    set(messagesAtom, [...current, message]);
  }
);

export const progressMessageAtom = atom<string>('');
export const regenerateAtom = atom<{ trigger: boolean; query: string; answerIndex?: number; history?: Message[] }>({
  trigger: false,
  query: '',
  answerIndex: undefined,
  history: undefined,
});

export const isRegeneratingAtom = atom<boolean>(false);
export const streamingMessageAtom = atom<string>('');
export interface TracingMessage {
  nodeId?: string;
  callback?: string;
  log?: any;
  turn?: number;
  [key: string]: any;
}

export const tracingMessagesAtom = atom<TracingMessage[]>([]);
export const tracingNodeIdAtom = atom<string[]>([]);
export const tracingBaseInfoAtom = atom<any>(null);
