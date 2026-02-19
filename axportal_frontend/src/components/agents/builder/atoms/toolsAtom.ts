import { type Tool } from '@/components/agents/builder/types/Tools.ts';
import { atom } from 'jotai/index';

export const tempSelectListAtom = atom<Tool[]>([]);
export const selectedAtom = atom<Record<string, Tool>>({});
export const selectedListAtom = atom<Record<string, Tool[]>>({});