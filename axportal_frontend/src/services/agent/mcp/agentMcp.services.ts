import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type {
  CreateAgentMcpCtlgRequest,
  GetAgentMcpByIdRequest,
  GetAgentMcpByIdResponse,
  GetAgentMcpByIdSycnTools,
  GetAgentMcpByIdTools,
  GetAgentMcpListRequest,
  GetAgentMcpListResponse,
  TestConnectionAgentMcpRequest,
  TestConnectionAgentMcpResponse,
  UpdateAgentMcpCtlgRequest,
} from './types';

// 에이전트 MCP 카탈로그 목록 조회
export const useGetAgentMcpList = (params?: GetAgentMcpListRequest, options?: ApiQueryOptions<PaginatedDataType<GetAgentMcpListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAgentMcpListResponse>>({
    queryKey: ['agent-mcp-list', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify({page: params?.page, size: params?.size})],
    url: '/agentMcp/ctlg', // baseURL + /agentMcp = http://localhost:8080/agentMcp
    params,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

// 에이전트  MCP 카탈로그 상세 조회
export const useGetAgentMcpById = ({ mcpId }: GetAgentMcpByIdRequest, options?: ApiQueryOptions<GetAgentMcpByIdResponse>) => {
  return useApiQuery<GetAgentMcpByIdResponse>({
    queryKey: ['agentMcp', mcpId.toString()],
    url: `agentMcp/ctlg/${mcpId}`, // baseURL + /agentMcp/{mcpId} = http://localhost:8080/agentMcp/{id}
    ...options,
  });
};

// 에이전트 MCP 카탈로그 삭제
export const useDeleteAgentMcpCtlgById = (options?: ApiMutationOptions<string, { mcpId: string }>) => {
  return useApiMutation<string, { mcpId: string }>({
    method: 'DELETE',
    url: 'agentMcp/ctlg/{mcpId}', // URL 템플릿: {mcpId}가 request.mcpId로 치환됨
    ...options,
  });
};

// 에이전트 MCP 카탈로그 수정
export const useUpdateAgentMcpCtlg = (options?: ApiMutationOptions<string, UpdateAgentMcpCtlgRequest & { mcpId: string }>) => {
  return useApiMutation<string, UpdateAgentMcpCtlgRequest & { mcpId: string }>({
    method: 'PUT',
    url: `/agentMcp/ctlg/{mcpId}`,
    ...options,
  });
};

// 에이전트 MCP 카탈로그 등록
// 실제 등록 확인
export const useCreateAgentMcpCtlg = (options?: ApiMutationOptions<{ id: string }, CreateAgentMcpCtlgRequest>) => {
  return useApiMutation<{ id: string }, CreateAgentMcpCtlgRequest>({
    method: 'POST',
    url: `/agentMcp/ctlg`,
    ...options,
    timeout: 60000,
  });
};

// 에이전트 MCP 카탈로그 test-connection
export const useTestConnectionAgentMcp = (options?: ApiMutationOptions<TestConnectionAgentMcpResponse, TestConnectionAgentMcpRequest>) => {
  return useApiMutation<TestConnectionAgentMcpResponse, TestConnectionAgentMcpRequest>({
    method: 'POST',
    url: '/agentMcp/test-connection',
    ...options,
  });
};

// 에이전트 MCP 카탈로그 sycn-tools
export const useGetAgentMcpByIdSycnTools = ({ mcpId }: GetAgentMcpByIdRequest, options?: ApiQueryOptions<GetAgentMcpByIdSycnTools>) => {
  return useApiQuery<GetAgentMcpByIdSycnTools>({
    queryKey: ['agent-mcp-sync-tools', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(mcpId || {})],
    url: `agentMcp/ctlg/${mcpId}/sync-tools`, // baseURL + /agentMcp/{mcpId} = http://localhost:8080/agentMcp/{id}
    ...options,
  });
};
// 에이전트 MCP 카탈로그 tool 목록 조회
export const useGetAgentMcpByIdTools = ({ mcpId }: GetAgentMcpByIdRequest, options?: ApiQueryOptions<GetAgentMcpByIdTools>) => {
  return useApiQuery<GetAgentMcpByIdTools>({
    queryKey: ['agentMcp', 'tools', mcpId.toString()],
    url: `agentMcp/ctlg/${mcpId}/tools`, // baseURL + /agentMcp/{mcpId} = http://localhost:8080/agentMcp/{id}
    ...options,
  });
};

// 에이전트 MCP 카탈로그 ping
// export const usePingAgentMcp = (options?: ApiMutationOptions<string, { mcpId: string }>) => {
//   return useApiMutation<string, { mcpId: string }>({
//     method: 'POST',
//     url: '/agentMcp/ctlg/{mcpId}/ping',
//     ...options,
//   });
// };

// 에이전트 MCP 카탈로그 activate
// export const useActAgentMcpCtlg = (options?: ApiMutationOptions<string, { mcpId: string }>) => {
//   return useApiMutation<string, { mcpId: string }>({
//     method: 'POST',
//     url: '/AgentMcpCtlg/ctlg/{mcpId}/activate',
//     ...options,
//   });
// };

// 에이전트 MCP 카탈로그 deactivate
// export const useDeactAgentMcpCtlg = (options?: ApiMutationOptions<string, { mcpId: string }>) => {
//   return useApiMutation<string, { mcpId: string }>({
//     method: 'POST',
//     url: '/AgentMcpCtlg/ctlg/{mcpId}/deactivate',
//     ...options,
//   });
// };
