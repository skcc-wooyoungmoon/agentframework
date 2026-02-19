import { atom } from 'jotai';

export interface Log {
  time?: string;
  log: string;
  nodeName?: string;
  nodeType?: string;
  type?: string;
  turn?: number;
}

export const logState = atom<Log[]>([]);
export const builderLogState = atom<Log[]>([]);
export const hasChatTestedAtom = atom<boolean>(false);
