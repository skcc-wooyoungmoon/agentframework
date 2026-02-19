import { atom } from 'jotai';

export const clearModelEventAtom = atom<{
  nodeId: string;
  nodeType: string;
  modelName: string;
  timestamp: number;
} | null>(null);

export const isClearModelAtom = atom<boolean>(false);
