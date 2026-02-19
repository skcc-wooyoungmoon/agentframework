import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { CreateAgentToolRequest, CreateAgentToolResponse, GetAgentToolByIdRequest, GetAgentToolByIdResponse, GetAgentToolListRequest, GetAgentToolListResponse, UpdateAgentToolRequest } from './types';

// 에이전트 도구 상세 조회
export const useGetAgentToolById = ({ toolId }: GetAgentToolByIdRequest, options?: ApiQueryOptions<GetAgentToolByIdResponse>) => {
  return useApiQuery<GetAgentToolByIdResponse>({
    queryKey: ['agentTool', toolId.toString()],
    url: `agentTool/${toolId}`, // baseURL + /agentTool/{toolId} = http://localhost:8080/agentTool/{id}
    ...options,
  });
};

// 에이전트 도구 목록 조회
export const useGetAgentToolList = (params?: GetAgentToolListRequest, options?: ApiQueryOptions<PaginatedDataType<GetAgentToolListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAgentToolListResponse>>({
    queryKey: ['agent-tool-list', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify({page: params?.page, size: params?.size})],
    url: '/agentTool', // baseURL + /agentTool = http://localhost:8080/agentTool
    params,
    ...options,
    disableCache: true,
  });
};

// 에이전트 도구 생성
export const useCreateAgentTool = (options?: ApiMutationOptions<CreateAgentToolResponse, CreateAgentToolRequest>) => {
  return useApiMutation<CreateAgentToolResponse, CreateAgentToolRequest>({
    method: 'POST',
    url: '/agentTool',
    ...options,
    timeout: 60000,
  });
};

// 에이전트 도구 수정
export const useUpdateAgentToolById = (options?: ApiMutationOptions<string, UpdateAgentToolRequest & { toolId: string }>) => {
  return useApiMutation<string, UpdateAgentToolRequest & { toolId: string }>({
    method: 'PUT',
    url: '/agentTool/{toolId}', // URL 템플릿: {toolId}가 request.toolId로 치환됨
    ...options,
  });
};

// 에이전트 도구 삭제
export const useDeleteAgentToolById = (options?: ApiMutationOptions<string, { toolId: string }>) => {
  return useApiMutation<string, { toolId: string }>({
    method: 'DELETE',
    url: '/agentTool/{toolId}', // URL 템플릿: {toolId}가 request.toolId로 치환됨
    ...options,
  });
};
