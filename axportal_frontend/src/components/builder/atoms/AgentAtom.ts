import { type Agent, type CustomEdge, type CustomNode, type KeyTableData } from '@/components/builder/types/Agents';
import { atom } from 'jotai';

export const agentAtom = atom<Agent>();

export const nodesAtom = atom<CustomNode[]>([]);
export const edgesAtom = atom<CustomEdge[]>([]);

export const keyTableAtom = atom<KeyTableData[]>([]);
