export interface CreateAgentToolRequest {
  name: string;
  displayName?: string;
  description: string;
  toolType: string;
  projectId?: string;
  method?: string;
  serverUrl?: string;
  code?: string;
  apiParam?: {
    header: Record<string, string>;
    static_params: Record<string, string>;
    dynamic_params: Record<string, string>;
    static_body: Record<string, any>;
    dynamic_body: Record<string, any>;
  };
  inputKeys?: Array<{ key: string; type: string }>;
  tags?: string[];
}

export interface CreateAgentToolResponse {
  agentToolUuid: string;
}
export interface GetAgentToolByIdRequest {
  toolId: string;
}

export interface GetAgentToolByIdResponse {
  id: string;
  name: string;
  displayName?: string;
  description: string;
  toolType: string;
  method?: string;
  serverUrl?: string;
  apiParam?: {
    header: Record<string, string>;
    static_params: Record<string, string>;
    dynamic_params: Record<string, string>;
    static_body: Record<string, any>;
    dynamic_body: Record<string, any>;
  };
  inputKeys?: Array<{ key: string; type: string }>;
  code?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy: string;
  updatedBy: string;
  publicStatus: '전체공유' | '내부공유';
  // tags?: string[];
}

export interface GetAgentToolListRequest {
  page?: number;
  size?: number;
  sort?: string;
  search?: string;
}

export type GetAgentToolListResponse = GetAgentToolByIdResponse;

export interface UpdateAgentToolRequest {
  toolId: string;
  name?: string;
  displayName?: string;
  description?: string;
  toolType?: string;
  method?: string;
  serverUrl?: string;
  code?: string;
  apiParam?: {
    header: Record<string, string>;
    static_params: Record<string, string>;
    dynamic_params: Record<string, string>;
    static_body: Record<string, any>;
    dynamic_body: Record<string, any>;
  };
  inputKeys?: Array<{ key: string; type: string }>;
  tags?: string[];
}

export interface GetGraphAppInfoResponse {
  id: string;
  name: string;
  description: string;
}

export interface AgentTool {
  id: string;
  name: string;
  displayName?: string;
  description: string;
  toolType: string;
  method?: string;
  serverUrl?: string;
  code?: string;
  apiParam?: {
    header: Record<string, string>;
    static_params: Record<string, string>;
    dynamic_params: Record<string, string>;
    static_body: Record<string, any>;
    dynamic_body: Record<string, any>;
  };
  inputKeys?: Array<{ key: string; type: string }>;
  createdAt?: string;
  updatedAt?: string;
  tags?: string[];
}
