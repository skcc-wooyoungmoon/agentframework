export type knowledgeTest = {
  query_text: string;
  repo_id: string;
  collection_id?: string | null;
};

export type QueryMessage = {
  content: string;
  type: string;
};

export const ChatType = {
  HUMAN: 'human',
  AI: 'ai',
};

export type Metadata = {
  id: string;
  source: string;
  page: number;
  offset: number;
  total_pages: number;
  format: string;
  title: string;
  author: string;
  subject: string;
  keywords: string;
  creator: string;
  producer: string;
  creationDate: string;
  modDate: string;
  trapped: string;
  file_id: string;
  file_name: string;
  doc_type: string;
  timestamp: number;
  doc_id: string;
  chunk_id: string;
  chunk_sequence: number;
};

export type Result = {
  content: string;
  metadata: Metadata;
  score: number;
};

export type QueryResponse = {
  timestamp: number;
  code: number;
  detail: string;
  traceId: string | null;
  data: Array<Result> | null;
};

export type chatType = {
  HUMAN: 'human';
  AI: 'ai';
};

export type MessageFormat = {
  id: string;
  content: Array<Result> | null;
  time: string;
  // @ts-ignore
  type: chatType.AI | chatType.HUMAN;
  regen?: boolean;
};
