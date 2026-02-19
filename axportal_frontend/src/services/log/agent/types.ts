export type GetAgentLogListRequest = {
  fromDate: string;
  toDate: string;
  page: number;
  size: number;
  fields: string;
  errorLogs: boolean;
  additionalHistoryOption: string;
  filter: string;
  search: string;
  sort: string;
};

// 추가 히스토리 정보 타입
export type TracingInfo = {
  nodeId?: string;
  nodeName?: string;
  nodeType?: string;
  startTime?: string;
  endTime?: string;
  duration?: number;
  status?: string;
  input?: any;
  output?: any;
  error?: string;
};

export type ModelInfo = {
  modelId?: string;
  modelName?: string;
  modelType?: string;
  provider?: string;
  tokens?: {
    prompt?: number;
    completion?: number;
    total?: number;
  };
};

export type RetrievalInfo = {
  query?: string;
  documents?: Array<{
    id?: string;
    content?: string;
    score?: number;
    metadata?: any;
  }>;
  retrievalTime?: number;
  documentCount?: number;
};

export type GetAgentLogListResponse = {
  requestTime: string;
  responseTime: string | null;
  elapsedTime: number | null;
  funcType: string;
  servingType: string;
  graphConfigPath: string;
  endpoint: string;
  apiKey: string;
  appId: string;
  agentAppVersion: string;
  agentAppId: string;
  agentAppName: string;
  agentAppServingId: string;
  agentAppServingName: string;
  company: string;
  department: string;
  user: string;
  chatId: string;
  transactionId: string;
  completionTokens: number;
  promptTokens: number;
  totalTokens: number;
  projectId: string;
  inputJson: string;
  outputJson: string;
  errorCode: string;
  errorMessage: string;
  // 추가 히스토리 옵션으로 받는 정보
  tracing?: TracingInfo[];
  model?: ModelInfo;
  retrieval?: RetrievalInfo;
};

// 세션 아이템 타입
export type LogSessionItem = {
  requestTime?: string;
  responseTime?: string;
  elapsedTime?: number;
  apiKey?: string;
  totalTokens?: number;
  promptTokens?: number;
  completionTokens?: number;
  agentAppId?: string;
  agentAppName?: string;
  agentAppServingId?: string;
  agentAppServingName?: string;
  agentAppVersion?: string;
  funcType?: string;
  servingType?: string;
  endpoint?: string;
  graphConfigPath?: string;
  inputJson?: string;
  outputJson?: string;
  projectId?: string;
  transactionId?: string;
  company?: string;
  department?: string;
  user?: string;
  errorCode?: string;
  errorMessage?: string;
};

// 로그 상세 데이터 타입 (그리드용)
export type LogDetailData = {
  no: number;
  requestTime: string;
  responseTime: string;
  elapsedTime: number;
  funcType: string;
  servingType: string;
  endpoint: string;
  apiKey: string;
  agentAppServingName: string;
  company: string;
  department: string;
  user: string;
  chatId: string;
  content: any;
  transactionId: string;
  callKeyName: string;
  completionTokens: number;
  promptTokens: number;
  totalTokens: number;
  inputJson: string;
  outputJson: string;
  status: 'normal' | 'error';
  errorCode?: string;
  errorMessage?: string;
  // RAG 추적 정보
  tracing?: TracingInfo[];
  model?: ModelInfo;
  retrieval?: RetrievalInfo;
};

// 팝업용 로그 데이터 타입
export type LogPopupData = {
  id: number;
  timestamp: string;
  requestTime: string;
  responseTime: string;
  callType: string;
  status: 'normal' | 'error';
  sessionId: string;
  traceId: string;
  content: string;
  callKeyName: string;
  latency?: string;
  elapsedTime?: number;
  safetyFilter?: string;
  inputJson?: string;
  outputJson?: string;
  funcType?: string;
  servingType?: string;
  graphConfigPath?: string;
  endpoint?: string;
  apiKey?: string;
  appId?: string;
  agentAppVersion?: string;
  agentAppId?: string;
  agentAppName?: string;
  agentAppServingId?: string;
  agentAppServingName?: string;
  company?: string;
  department?: string;
  user?: string;
  chatId?: string;
  transactionId?: string;
  completionTokens?: number;
  promptTokens?: number;
  totalTokens?: number;
  projectId?: string;
  errorCode?: string;
  errorMessage?: string;
  sessionKey?: string;
  sessionItems?: LogSessionItem[];
  // RAG 추적 정보
  tracing?: TracingInfo[];
  model?: ModelInfo;
  retrieval?: RetrievalInfo;
};