import type { ApiQueryOptions } from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api/useApi';

import type {
  GetPortalResourcesRequest,
  GetPortalResourcesResponse,
  GetGpuNodeResourcesRequest,
  GetGpuNodeResourcesResponse,
  GetSolutionResourcesRequest,
  GetSolutionResourcesResponse,
  GetAgentPodsRequest,
  GetAgentPodsResponse,
  GetModelPodsRequest,
  GetModelPodsResponse,
  GetSolutionDetailRequest,
  GetSolutionDetailResponse,
  GetGpuNodeDetailRequest,
  GetGpuNodeDetailResponse,
} from './resrcMgmt.types';

/**
 * 포탈 자원 현황 조회
 */
export const useGetPortalResources = (
  params?: GetPortalResourcesRequest,
  options?: ApiQueryOptions<GetPortalResourcesResponse>
) => {
  return useApiQuery<GetPortalResourcesResponse>({
    queryKey: ['admin-resrc-mgmt-portal', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/portal',
    params,
    ...options,
  });
};

/**
 * GPU 노드별 자원 현황 조회
 */
export const useGetGpuNodeResources = (
  params?: GetGpuNodeResourcesRequest,
  options?: ApiQueryOptions<GetGpuNodeResourcesResponse>
) => {
  return useApiQuery<GetGpuNodeResourcesResponse>({
    queryKey: ['admin-resrc-mgmt-gpu-node', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/gpu-node',
    params,
    ...options,
  });
};

/**
 * GPU 노드 상세 조회
 * 
 * @param params - nodeName, fromDate, toDate
 */
export const useGetGpuNodeDetail = (
  params: GetGpuNodeDetailRequest,
  options?: ApiQueryOptions<GetGpuNodeDetailResponse>
) => {
  return useApiQuery<GetGpuNodeDetailResponse>({
    queryKey: ['admin-resrc-mgmt-gpu-node-detail', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/gpu-node/detail',
    params,
    ...options,
  });
};

/**
 * 솔루션 자원 현황 조회
 */
export const useGetSolutionResources = (
  params?: GetSolutionResourcesRequest,
  options?: ApiQueryOptions<GetSolutionResourcesResponse>
) => {
  return useApiQuery<GetSolutionResourcesResponse>({
    queryKey: ['admin-resrc-mgmt-solution', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/solution',
    params,
    ...options,
  });
};

/**
 * 에이전트 Pods 조회
 */
export const useGetAgentPods = (
  params?: GetAgentPodsRequest,
  options?: ApiQueryOptions<GetAgentPodsResponse>
) => {
  return useApiQuery<GetAgentPodsResponse>({
    queryKey: ['admin-resrc-mgmt-portal-agent-pods', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/portal/agent-pods',
    params,
    ...options,
  });
};

/**
 * 모델 Pods 조회
 */
export const useGetModelPods = (
  params?: GetModelPodsRequest,
  options?: ApiQueryOptions<GetModelPodsResponse>
) => {
  return useApiQuery<GetModelPodsResponse>({
    queryKey: ['admin-resrc-mgmt-portal-model-pods', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/portal/model-pods',
    params,
    ...options,
  });
};

/**
 * 솔루션 기본 정보 조회 (자동 실행)
 * 
 * @param params - fromDate, toDate, namespace, podName(선택적)
 */
export const useGetSolutionInfo = (
  params: GetSolutionDetailRequest,
  options?: ApiQueryOptions<GetSolutionDetailResponse>
) => {
  return useApiQuery<GetSolutionDetailResponse>({
    queryKey: ['admin-resrc-mgmt-solution-info', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/solution/info',
    params,
    ...options,
  });
};

/**
 * 솔루션 상세 조회 (조회 버튼 클릭 시)
 * 
 * @param params - fromDate, toDate, namespace, podName(선택적)
 */
export const useGetSolutionDetail = (
  params: GetSolutionDetailRequest,
  options?: ApiQueryOptions<GetSolutionDetailResponse>
) => {
  return useApiQuery<GetSolutionDetailResponse>({
    queryKey: ['admin-resrc-mgmt-solution-detail', JSON.stringify(params)],
    url: '/admin/resrc-mgmt/solution/detail',
    params,
    ...options,
  });
};

/**
 * 공통 프로젝트 목록 조회
 * @param options react-query 옵션
 * @returns 프로젝트 목록 (prjSeq, uuid, prjNm)
 */
export const useGetCommonProjects = (
  options?: ApiQueryOptions<{ prjSeq: number; uuid: string; prjNm: string }[]>
) => {
  return useApiQuery<{ prjSeq: number; uuid: string; prjNm: string }[]>({
    queryKey: ['common-projects'],
    url: '/admin/user-usage-mgmt/common/projects',
    ...options,
  });
};