import { api } from '@/configs/axios.config';
import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import type { ErrorResponse, SuccessResponse } from '@/hooks/common/api/types';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';
import { useMutation } from '@tanstack/react-query';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type {
  CreateAgentAppRequest,
  GetAgentAppByIdRequest,
  GetAgentAppByIdResponse,
  GetAgentAppDeployListByIdResponse,
  GetAgentAppDeployListRequest,
  GetAgentAppListRequest,
  GetAgentAppListResponse,
  GetAgentDeployByIdRequest,
  GetAgentDeployByIdResponse,
  GetAgentDeployInfoResponse,
  GetAgentServingRequest,
  GetAgentServingResponse,
  GetAgentSysLogRequest,
  GetAppApiKeyResponse,
  GetClusterResourcesRequest,
  GetClusterResourcesResponse,
  GetStreamAgentDeployRequest,
  UpdateAgentAppRequest,
} from './types';

// 에이전트 배포(앱) 상세 조회
export const useGetAgentAppById = ({ appId }: GetAgentAppByIdRequest, options?: ApiQueryOptions<GetAgentAppByIdResponse>) => {
  return useApiQuery<GetAgentAppByIdResponse>({
    queryKey: ['agentDeploy', appId.toString()],
    url: `agentDeploy/app/${appId}`, // baseURL + /agentDeploy/{appId}
    ...options,
  });
};

// Agent Apps 목록 조회
export const useGetAgentAppList = (params?: GetAgentAppListRequest, options?: ApiQueryOptions<PaginatedDataType<GetAgentAppListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAgentAppListResponse>>({
    queryKey: ['agent-app-list', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params || {})],
    url: '/agentDeploy/app',
    params: params,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

// Agent 배포 상세 조회
export const useGetAgentDeployById = ({ deployId }: GetAgentDeployByIdRequest, options?: ApiQueryOptions<GetAgentDeployByIdResponse>) => {
  return useApiQuery<GetAgentDeployByIdResponse>({
    queryKey: ['agentDeploy', deployId.toString()],
    url: `agentDeploy/app/deploy/${deployId}`, // baseURL + /agentDeploy/app/deploy/{deployId}
    ...options,
  });
};

// Agent 배포 목록 조회 (앱별)
export const useGetAgentAppDeployListById = (params: GetAgentAppDeployListRequest, options?: ApiQueryOptions<PaginatedDataType<GetAgentAppDeployListByIdResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAgentAppDeployListByIdResponse>>({
    queryKey: ['agentDeploy', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params || {})],
    url: `agentDeploy/app/${params.appId}/deploy`, // baseURL + /agentDeploy/app/{appId}/deploy
    params: params,
    ...options,
  });
};

// Agent 서빙 상세 조회
export const useGetAgentServing = ({ servingId }: GetAgentServingRequest, options?: ApiQueryOptions<GetAgentServingResponse>) => {
  return useApiQuery<GetAgentServingResponse>({
    queryKey: ['agentDeploy', servingId.toString()],
    url: `agentDeploy/app/serving/${servingId}`, // baseURL + /agentDeploy/serving/{agentServingId}
    ...options,
  });
};

// Agent 앱 삭제
export const useDeleteAgentApp = (options?: ApiMutationOptions<string, { appId: string }>) => {
  return useApiMutation<string, { appId: string }>({
    method: 'DELETE',
    url: '/agentDeploy/app/{appId}', // URL 템플릿: {appId}가 request.appId로 치환됨
    ...options,
  });
};

// Agent 앱 수정
export const useUpdateAgentApp = (options?: ApiMutationOptions<string, UpdateAgentAppRequest & { appId: string }>) => {
  return useApiMutation<string, UpdateAgentAppRequest & { appId: string }>({
    method: 'PUT',
    url: `/agentDeploy/app/{appId}`,
    ...options,
  });
};

// Agent 배포 버전 수정
export const useUpdateAgentAppDeploy = (options?: ApiMutationOptions<string, { deployId: string }>) => {
  return useApiMutation<string, { deployId: string }>({
    method: 'PUT',
    url: '/agentDeploy/app/deploy/{deployId}', // URL 템플릿: {deployId}가 request.deployId로 치환됨
    ...options,
  });
};

// Agent 배포 버전 삭제
export const useDeleteAgentAppDeploy = (options?: ApiMutationOptions<string, { deployId: string }>) => {
  return useApiMutation<string, { deployId: string }>({
    method: 'DELETE',
    url: '/agentDeploy/app/deploy/{deployId}', // URL 템플릿: {deployId}가 request.deployId로 치환됨
    ...options,
  });
};

// Agent 배포 버전 중지
export const useStopAgentDeploy = (options?: ApiMutationOptions<string, { deployId: string }>) => {
  return useApiMutation<string, { deployId: string }>({
    method: 'POST',
    url: '/agentDeploy/app/deploy/{deployId}/stop', // URL 템플릿: {deployId}가 request.deployId로 치환됨
    ...options,
  });
};

// Agent 배포 버전 재시작
export const useRestartAgentDeploy = (options?: ApiMutationOptions<string, { deployId: string }>) => {
  return useApiMutation<string, { deployId: string }>({
    method: 'POST',
    url: '/agentDeploy/app/deploy/{deployId}/restart', // URL 템플릿: {deployId}가 request.deployId로 치환됨
    ...options,
  });
};

// 에이전트 배포 에이전트 Key 조회
export const useGetAgentAppApiKeyListById = ({ appId }: GetAgentAppByIdRequest, options?: ApiQueryOptions<GetAppApiKeyResponse>) => {
  return useApiQuery<GetAppApiKeyResponse>({
    queryKey: ['agentDeploy', appId.toString()],
    url: `agentDeploy/app/${appId}/apiKeys`, // baseURL + /agentDeploy/{appId} = http://localhost:8080/agent-deploy/{appId}
    ...options,
  });
};

// 에이전트 배포 에이전트 Key 재발급
export const useRegenerateAgentAppApiKey = ({ appId }: { appId: string }, options?: ApiMutationOptions<string, { appId: string }>) => {
  return useApiMutation<string, { appId: string }>({
    method: 'POST',
    url: `/agentDeploy/app/${appId}/apiKeys`,
    ...options,
  });
};

// 에이전트 배포
export const useCreateAgentApp = (options?: ApiMutationOptions<string, CreateAgentAppRequest>) => {
  return useApiMutation<string, CreateAgentAppRequest>({
    method: 'POST',
    url: 'agentDeploy/app',
    ...options,
  });
};

// 에이전트 배포 리소스 조회
export const useGetClusterResources = (params?: GetClusterResourcesRequest, options?: ApiQueryOptions<GetClusterResourcesResponse>) => {
  return useApiQuery<GetClusterResourcesResponse>({
    queryKey: ['agentDeploy'],
    url: `agentDeploy/app/cluster/resources`, // baseURL + /agentDeploy/{appId} = http://localhost:8080/agent-deploy/{appId}
    params,
    ...options,
    timeout: 60000,
  });
};

// 에이전트 배포 스트리밍 추론 (SSE 처리)
export const useStreamAgentDeploy = (options?: any) => {
  return useMutation<SuccessResponse<string>, ErrorResponse, GetStreamAgentDeployRequest & { deployId: string }>({
    mutationKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    mutationFn: async request => {
      const { deployId, routerPath, authorization, StreamReq } = request;

      // 기존 axios 인스턴스 사용 (CORS 처리됨)
      const response = await api.post(`/agentDeploy/app/${deployId}/stream`, StreamReq, {
        params: {
          routerPath,
          authorization,
        },
        headers: {
          Accept: 'text/event-stream',
        },
        responseType: 'text', // 텍스트 응답으로 처리
        timeout: 240000, // 4분으로 증가 (스트리밍 요청용)
      });

      return response.data;
    },
    ...options,
  });
};

// 에이전트 ES 시스템 로그 조회 (긴 응답을 위한 별도 설정)
// 기존 useApiMutation 사용 시 응답이 잘림
export const useGetAgentSysLog = (options?: ApiMutationOptions<string, { index: string; body: GetAgentSysLogRequest }>) => {
  return useMutation<SuccessResponse<string>, ErrorResponse, { index: string; body: GetAgentSysLogRequest }>({
    mutationKey: ['POST', '/agentDeploy/elastic/{index}/_search'],
    mutationFn: async (request: { index: string; body: GetAgentSysLogRequest }) => {
      const finalUrl = `/agentDeploy/elastic/${request.index}/_search`;

      // 긴 로그 응답을 위한 별도 axios 설정
      const response = await api.post(finalUrl, request.body, {
        timeout: 60000,
        maxContentLength: 50 * 1024 * 1024, // 50MB
        maxBodyLength: 50 * 1024 * 1024, // 50MB
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.data;
    },
    ...options,
  });
};

// 에이전트 배포 정보 조회
export const useGetAgentDeployInfo = (agentId: string, options?: ApiQueryOptions<GetAgentDeployInfoResponse>) => {
  return useApiQuery<GetAgentDeployInfoResponse>({
    queryKey: ['agentDeploy', agentId],
    url: `agentDeploy/app/${agentId}/info`,
    ...options,
  });
};
