import { type Agent, type CustomEdge, type CustomNode, type KeyTableData, type KnowledgeRetriever } from '@/components/agents/builder/types/Agents';
import { atom } from 'jotai';

export const agentAtom = atom<Agent>();

export const nodesAtom = atom<CustomNode[]>([]);
export const edgesAtom = atom<CustomEdge[]>([]);

export const keyTableAtom = atom<KeyTableData[]>([]);
export const selectedPromptIdRepoAtom = atom<Record<string, string | null>>({});
export const selectedPromptNameRepoAtom = atom<Record<string, string | null>>({});
export const selectedPromptDataRepoAtom = atom<Record<string, any>>({});
export const isChangePromptAtom = atom<boolean>(false);

export const selectedFewShotIdRepoAtom = atom<Record<string, string | null>>({});
export const selectedFewShotNameRepoAtom = atom<Record<string, string | null>>({});
export const selectedFewShotDataRepoAtom = atom<Record<string, any>>({});
export const isChangeFewShotAtom = atom<boolean>(false);

export const isChangeToolAtom = atom<boolean>(false);
export const isChangeToolsAtom = atom<boolean>(false);

export const selectedKnowledgeIdRepoAtom = atom<Record<string, string>>({});
export const selectedKnowledgeServingNameAtom = atom<Record<string, string>>({});
export const selectedKnowledgeNameRepoAtom = atom<Record<string, string>>({});
export const selectedKnowledgeRepoKindAtom = atom<Record<string, string>>({});
export const selectedKnowledgeRetrieverIdAtom = atom<Record<string, string>>({});
type KnowledgeDetail = Partial<KnowledgeRetriever> & { __fetched?: boolean };

export const selectedKnowledgeDetailAtom = atom<Record<string, KnowledgeDetail>>({});
export const isChangeKnowledgeAtom = atom<boolean>(false);

// 20250913추가
export const selectedAgentAppIdRepoAtom = atom<Record<string, string | null>>({});
export const selectedAgentAppNameRepoAtom = atom<Record<string, string>>({});
export const selectedAgentAppVersionRepoAtom = atom<Record<string, number | undefined>>({});
export const tempSelectedAgentAppAtom = atom<any | null>(null);
export const isChangeAgentAppAtom = atom<boolean>(false);
