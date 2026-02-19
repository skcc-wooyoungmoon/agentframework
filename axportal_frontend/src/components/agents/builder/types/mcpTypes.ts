export interface MCPCatalog {
  id: string;
  name: string;
  description?: string;
  tools: MCPTool[];
  isActive: boolean;
}

export interface MCPTool {
  id: string;
  name: string;
  description: string;
  type: 'serverless' | 'local' | 'remote' | 'custom_code' | 'custom_api';
  isActive: boolean;
  catalogId: string;
}

export interface MCPSelection {
  catalogId: string;
  catalogName: string;
  serverName: string;
  serverUrl: string;
  toolIds: string[];
  // 선택된 MCP에서 제공하는 실제 Tool 메타데이터 목록 (Gateway 응답 그대로 보존)
  tools?: any[];
  // 도구 활성화/비활성화 상태 (도구 ID를 키로 사용)
  toolStates?: Record<string, boolean>;
  // 백엔드 요구 스펙: mcp_id 전달 필요 시 사용
  mcp_id?: string;
}
