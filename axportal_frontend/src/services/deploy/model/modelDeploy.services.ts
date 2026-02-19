import { type ApiQueryOptions, type PaginatedDataType, useApiMutation, useApiQueries, useApiQuery } from '@/hooks/common/api';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type {
  CreateBackendAiModelDeployRequest,
  CreateBackendAiModelDeployResponse,
  CreateModelDeployRequest,
  DeleteModelDeployRequest,
  GetDockerImgUrlRequest,
  GetDockerImgUrlResponse,
  GetInferencePerformanceRequest,
  GetInferencePerformanceResponse,
  GetModelDeployListRequest,
  GetModelDeployResourceInfoResponse,
  GetModelDeployResponse,
  GetSessionLogResponse,
  GetTaskResourceRequest,
  GetTaskResourceResponse,
  PutModelDeployRequest,
} from './types';

export const useGetModelDeployDetail = (id: string) => {
  return useApiQuery<GetModelDeployResponse>({
    url: `/modelDeploy/${id}`,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

// 여러 개의 모델 배포 상세 정보 조회
export const useGetModelDeployDetailsBulk = (servingIds: string[]) => {
  return useApiQueries<GetModelDeployResponse>(
    servingIds.map(servingId => ({
      url: `/modelDeploy/${servingId}`,
      queryKey: ['modelDeploy', servingId],
      options: {
        enabled: !!servingId,
      },
    }))
  );
};

export const useGetModelDeployList = (request: GetModelDeployListRequest, options?: ApiQueryOptions<PaginatedDataType<GetModelDeployResponse>>) => {
  return useApiQuery<PaginatedDataType<GetModelDeployResponse>>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA, request?.queryKey || ''], // type 사용 안함 -> disableCache 사용
    url: '/modelDeploy',
    params: { ...request, sort: 'created_at,desc' }, // TODO : sort 방식이 어딘지 찾기
    paramsSerializer: serializeQueryParams,
    disableCache: true, // 항상 최신 데이터 조회
    ...options,
  });
};

export const useDeleteModelDeployBulk = () => {
  return useApiMutation<void, DeleteModelDeployRequest[]>({
    url: `/modelDeploy/bulk`,
    method: 'DELETE',
  });
};

export const useChangeModelDeployStatus = () => {
  return useApiMutation<
    void,
    {
      id: string;
      status: 'start' | 'stop';
    }
  >({
    url: `/modelDeploy/{id}/{status}`,
    method: 'POST',
  });
};

export const useChangeBackendAiModelDeployStatus = () => {
  return useApiMutation<
    void,
    {
      id: string;
      status: 'start' | 'stop';
    }
  >({
    url: `/modelDeploy/backend-ai/{id}/{status}`,
    method: 'POST',
  });
};

export const useCreateModelDeploy = () => {
  return useApiMutation<GetModelDeployResponse, CreateModelDeployRequest>({
    url: '/modelDeploy',
    method: 'POST',
  });
};

export const useCreateBackendAiModelDeploy = () => {
  return useApiMutation<CreateBackendAiModelDeployResponse, CreateBackendAiModelDeployRequest>({
    url: '/modelDeploy/backend-ai',
    method: 'POST',
  });
};

export const usePutModelDeploy = () => {
  return useApiMutation<void, PutModelDeployRequest>({
    url: '/modelDeploy/{servingId}',
    method: 'PUT',
  });
};

export const usePutBackendAiModelDeploy = () => {
  return useApiMutation<void, PutModelDeployRequest>({
    url: '/modelDeploy/backend-ai/{servingId}',
    method: 'PUT',
  });
};

// ============================================================================
// Resource Management Services
// ============================================================================

/**
 * 태스크 타입별 리소스 정보 조회
 *
 * <p>특정 태스크 타입에 대한 리소스 정보를 조회합니다.
 * 노드별 리소스 사용량, 네임스페이스 리소스, 태스크 정책, 할당량 정보를 포함합니다.</p>
 *
 * @param request 태스크 리소스 조회 요청 파라미터
 * @returns 태스크 리소스 정보
 */
export const useGetTaskResource = (request: GetTaskResourceRequest) => {
  return useApiQuery<GetTaskResourceResponse>({
    queryKey: ['task-resource', JSON.stringify(request)],
    url: '/resources/task',
    params: {
      task_type: request.taskType,
      project_id: request.projectId,
    },
  });
};

/**
 * 모델 배포 시스템 로그 조회
 *
 * <p>모델 배포에 대한 시스템(컨테이너) 로그를 조회합니다.</p>
 *
 * @param id 모델 배포 세션 ID
 * @returns 시스템 로그 조회 결과
 */
export const useGetModelDeploySystemLog = (id: string) => {
  return useApiQuery<GetSessionLogResponse>({
    queryKey: ['model-deploy-system-log', id],
    url: `/modelDeploy/${id}/system-log`,
    enabled: !!id,
    disableCache: true,
  });
};

/**
 * 모델 배포 엔드포인트 정보 조회
 *
 * <p>모델 배포의 Backend.AI 엔드포인트 상세 정보를 조회합니다.</p>
 *
 * @param id 모델 배포 ID
 * @returns 엔드포인트 정보 조회 결과
 */
// export const useGetModelDeployEndpointInfo = (id: string) => {
//   return useApiQuery<GetEndpointResponse>({
//     queryKey: ['model-deploy-endpoint-info', id],
//     url: `/modelDeploy/endpoint-info/${id}`,
//     enabled: !!id,
//   });
// };

/**
 * 모델 배포 추론 성능 조회
 *
 * <p>모델 배포의 추론 성능 데이터를 조회합니다.
 * Time To First Token (TTFT)와 Time Per Output Token의 구간별 호출 수 분포를 포함합니다.</p>
 *
 * @param request 추론 성능 조회 요청 파라미터
 * @param options API 옵션
 * @returns 추론 성능 조회 결과
 */
export const useGetInferencePerformance = (request: GetInferencePerformanceRequest, options?: ApiQueryOptions<GetInferencePerformanceResponse>) => {
  const { servingId, modelName, startDate, endDate } = request;

  return useApiQuery<GetInferencePerformanceResponse>({
    url: `/modelDeploy/inference-performance`,
    params: {
      servingId,
      modelName,
      startDate,
      endDate,
    },
    ...options,
    disableCache: true,
  });
};

/**
 * 모델 배포 자원 현황 조회
 *
 * <p>servingId를 통해 모델 배포의 자원 현황(CPU, Memory, GPU)을 조회합니다.</p>
 *
 * @param id 모델 배포 서빙 ID
 * @param options API 옵션
 * @returns 모델 배포 자원 현황 조회 결과
 */
export const useGetModelDeployResourceInfo = (id: string, options?: ApiQueryOptions<GetModelDeployResourceInfoResponse>) => {
  return useApiQuery<GetModelDeployResourceInfoResponse>({
    queryKey: ['model-deploy-resource-info', id],
    url: `/modelDeploy/${id}/resource-info`,
    disableCache: true,
    ...options,
  });
};

/**
 * 도커 이미지 URL 조회
 *
 * <p>SYS_U_V 값으로 도커 이미지 URL을 조회합니다. (DEL_YN = 0인 경우만)</p>
 *
 * @param request 도커 이미지 URL 조회 요청 파라미터
 * @param options API 옵션
 * @returns 도커 이미지 URL 목록
 */
export const useGetDockerImgUrl = (request: GetDockerImgUrlRequest, options?: ApiQueryOptions<GetDockerImgUrlResponse[]>) => {
  return useApiQuery<GetDockerImgUrlResponse[]>({
    queryKey: ['docker-img-url', request.sysUV],
    url: '/modelDeploy/docker-img-url',
    params: {
      sysUV: request.sysUV,
    },
    enabled: !!request.sysUV,
    disableCache: true,
    ...options,
  });
};

const serializeQueryParams = (params?: Record<string, unknown>) => {
  const searchParams = new URLSearchParams();

  if (!params) {
    return searchParams.toString();
  }

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null) {
      return;
    }

    if (Array.isArray(value)) {
      value.forEach(item => {
        if (item === undefined || item === null) {
          return;
        }
        searchParams.append(key, String(item));
      });
    } else {
      searchParams.append(key, String(value));
    }
  });

  return searchParams.toString();
};
