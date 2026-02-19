import { type RetrievalOptions } from '@/components/builder/types/Agents';
import { atom } from 'jotai';

export interface InputRetrieverData {
  nodeId: string;
  inputIndex: number;
  inputData: {
    repoId: string;
    retrievalOptions: RetrievalOptions;
  };
}

export const inputRetrieverAtom = atom<Record<number, InputRetrieverData>>({});
