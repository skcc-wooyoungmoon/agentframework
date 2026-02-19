export type GetAgentMcpListRequest = {
  page?: number;
  size?: number;
  sort?: string;
  search?: string;
  filter?: string;
};

export type Message = {
  type: string;
  description: string;
};

export type AnnotationsRes = {
  title: string;
  readOnlyHint: string;
  destructiveHint: string;
  idempotentHint: string;
  openWorldHint: string;
  message: Message;
};

export type InputSchemaRes = {
  type: string;
  schema: string;
  properties: object;
  additionalProperties: boolean;
};

// MCP Tool 정보 (기존 GetMcpResponse)
export type GetMcpResponse = {
  name: string;
  title: string;
  description: string;
  inputSchema: InputSchemaRes;
  outputSchema: object;
  annotations: AnnotationsRes;
  meta: object;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
  publicStatus: '전체공유' | '내부공유';
};

// MCP 카탈로그 정보 (새로운 McpCatalogInfo)
export type McpCatalogTag = {
  name: string;
};

export type McpCatalogInfo = {
  id: string;
  name: string;
  displayName?: string;
  description: string;
  type: string;
  serverUrl: string;
  authType: string;
  authConfig: object | null;
  enabled: boolean;
  mcpServingId?: string;
  gatewayEndpoint?: string;
  tags?: McpCatalogTag[];
  transportType: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
  tools?: GetMcpResponse[];
  publicStatus?: '전체공유' | '내부공유';
};

export type GetAgentMcpByIdRequest = {
  mcpId: string;
};

export type GetAgentMcpByIdResponse = McpCatalogInfo;
export type GetAgentMcpListResponse = McpCatalogInfo;

export type UpdateAgentMcpCtlgRequest = {
  mcpId: string;
  name: string;
  displayName?: string | null;
  description: string;
  type: McpType;
  serverUrl: string;
  authType: McpAuthType;
  authConfig: McpAuthConfig;
  tags?: string[];
  transportType: McpTransportType;
};

export type McpType = 'serverless' | 'self-hosting' | 'platform';

export type McpAuthType = 'none' | 'basic' | 'bearer' | 'custom-header';

export type McpTransportType = 'streamable-http' | 'sse';

export type McpBasicAuthConfig = {
  username: string;
  password: string;
};

export type McpBearerAuthConfig = {
  token: string;
};

export type McpCustomHeaderAuthConfig = {
  key: string;
  value: string;
};

export type McpAuthConfig = McpBasicAuthConfig | McpBearerAuthConfig | McpCustomHeaderAuthConfig | null;

export type CreateAgentMcpCtlgRequest = {
  name: string;
  displayName?: string | null;
  description: string;
  type: McpType;
  serverUrl: string;
  authType: McpAuthType;
  authConfig: McpAuthConfig;
  tags?: string[];
  transportType: McpTransportType;
};

export type GetAgentMcpByIdSycnTools = GetMcpResponse[];
export type GetAgentMcpByIdTools = GetMcpResponse[];

export type TestConnectionAgentMcpRequest = {
  serverUrl: string;
  transportType?: McpTransportType;
  authType: McpAuthType;
  authConfig?: McpAuthConfig;
};

export type TestConnectionAgentMcpResponse = {
  isConnected: boolean;
  errorMessage: string;
};

// ------------------------------------------------------------
export type MCPTool = {
  id: string;
  name: string;
  description?: string;
};

export type generateMCPCatalog = {
  id: string;
  name: string;
  description?: string;
  tools: MCPTool[];
  auto_populate_tools: string;
};

export type Tags = {
  name: string;
};

export type MCPCatalog = {
  id: string;
  name: string;
  description: string;
  type: 'Serverless' | 'Self-hosting';
  tags: Tags[];
  enabled: boolean;
  created_at: string;
  updated_at: string;
  actions: string;
  tools: MCPTool[];
  auto_populate_tools?: 'manual' | 'auto';
};
