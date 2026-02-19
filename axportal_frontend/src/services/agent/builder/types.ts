import type { Agent } from '@/components/agents/builder/types/Agents';
import type { AgentGraph } from '@/components/agents/builder/types/Agents.ts';

export interface GetAgentByIdRequest {
  agentId: string;
}

export interface GetAgentLineagesResponse {
  data: Lineage[];
}

export type Lineage = {
  sourceKey: string;
  targetKey: string;
  action: 'USED' | 'CREATED';
  depth: number;
  sourceType: LineageTargetType;
  targetType: LineageTargetType;
};

export type LineageTargetType =
  | 'SERVING_AGENT'
  | 'AGENT_GRAPH'
  | 'AGENT_APP'
  | 'AGENT_DEPLOYMENT'
  | 'SUB_AGENT'
  | 'SERVING_MODEL'
  | 'MODEL'
  | 'MODEL_VERSION'
  | 'KNOWLEDGE'
  | 'PROMPT'
  | 'TOOL'
  | 'MCP'
  | 'FEW_SHOT'
  | 'INGESTION_TOOL'
  | 'DATASOURCE'
  | 'DATASOURCE_FILE'
  | 'VECTOR_DB'
  | 'CUSTOM_SCRIPT'
  | 'TRAINING'
  | 'DATASET';

export interface AgentBuilderSearchReq {
  project_id?: string;
  page?: number;
  size?: number;
  sort?: string;
  search?: string;
  agentId?: string;
}

export interface AgentBuilderRes {
  id: string;
  project_id: string;
  name: string;
  description: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  type: string;
  category: string;
  createdBy: string;
  updatedBy: string;
  nodeCount: number;
  edgeCount: number;
  nodes?: any[];
  edges?: any[];
  // 공개범위 정보
  scope?: string;
  visibility?: string;
  // 사용모델 정보
  usedModel?: string;
  usedModelName?: string;
  // 사용지식 정보
  usedKnowledge?: string[];
  usedTools?: string[];
}

export interface AgentBuilderDetailReq {
  agent_id: string;
}

export interface AgentBuilderDetailRes extends AgentBuilderRes {
  // 상세 정보에 필요한 추가 필드들
}

export interface CreateAgentBuilderReq {
  name: string;
  description: string;
  type: string;
  category: string;
}

export interface CreateAgentFromTemplateReq {
  name: string;
  description: string;
  template_id?: string | null; // 🔥 new_template일 때는 전달하지 않음
}

export type UpdateAgentBuilderReq = AgentGraph;

export interface DeleteAgentBuilderReq {
  graphUuid: string;
}

export interface BulkDeleteAgentBuilderReq {
  agentIds: string[];
}

export interface GetGraphAppInfoResponse {
  id: string;
  name: string;
  description: string;
}

export interface Template {
  id: string;
  name: string;
  description: string;
  type: 'template' | 'example';
  category?: string;
  icon?: string;
  template_id?: string;
  template_name?: string;
  template_description?: string;
  version?: string;
  tags?: string[];
  created_at?: string;
  usage_count?: number;
}

export interface TemplatesResponse {
  data?: {
    templates?: Template[];
    examples?: Template[];
  };
  templates?: Template[];
  examples?: Template[];
  total?: number;
  page?: number;
  size?: number;
}

export interface TemplateStructure {
  nodes: any[];
  edges: any[];
}

export type AgentCreateResponse = {
  timestamp: number;
  code: number;
  message: string;
  traceId: string;
  data: Agent;
  payload: string;
  status: number;
};
