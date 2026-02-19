import { atom } from 'jotai';

export interface Log {
  time?: string;
  log: string;
}

export const logState = atom<Log[]>([]);
