import type { Result } from '@/components/builder/types/knowledge';

export type MessageFormat = {
  id: string;
  content: string;
  // content: string | Record<string, any>;
  time: string;
  // @ts-ignore
  type: ChatType.AI | ChatType.HUMAN;
  regen?: boolean;
  elapsedTime?: number; // milliseconds
  regenerations?: Array<{
    content: string;
    time: string;
    elapsedTime?: number;
  }>;
};

export type KnowledgeMessageFormat = {
  id: string;
  content: Array<Result> | null;
  time: string;
  // @ts-ignore
  type: ChatType.AI | ChatType.HUMAN;
  regen?: boolean;
};
