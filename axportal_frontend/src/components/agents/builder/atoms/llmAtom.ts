import { atom } from 'jotai/index';
export const selectedLLMRepoAtom = atom<Record<string, any>>({});
export const isChangeLLMAtom = atom<boolean>(false);