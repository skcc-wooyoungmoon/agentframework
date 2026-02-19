import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery, type ApiQueryOptions } from '@/hooks/common/api/useApi';

import type {
  CreateModelGardenRequest,
  GetModelGardenAvailableRequest,
  GetModelGardenAvailableResponse,
  GetModelGardenRequest,
  GetVaccineCheckResultResponse,
  ModelGardenInfo,
  UpdateModelGardenRequest,
} from './types';

export const useGetModelGardenList = (params: GetModelGardenRequest, options?: ApiQueryOptions<PaginatedDataType<ModelGardenInfo>>, key?: 'DEFAULT' | 'IN_PROCESS') => {
  return useApiQuery<PaginatedDataType<ModelGardenInfo>, GetModelGardenRequest>({
    queryKey: ['model-garden', DONT_SHOW_LOADING_KEYS.GRID_DATA, key ?? 'DEFAULT'],
    url: '/modelGarden',
    params,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

export const useGetModelGardenDetail = (id: string, options?: ApiQueryOptions<ModelGardenInfo>) => {
  return useApiQuery<ModelGardenInfo, string>({
    url: `/modelGarden/${id}`,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

/**
 * 모델 가든 백신검사 결과 조회
 */
export const useGetVaccineCheckResult = (id: string, options?: ApiQueryOptions<GetVaccineCheckResultResponse>) => {
  return useApiQuery<GetVaccineCheckResultResponse, string>({
    url: `/modelGarden/${id}/check-result`,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

export const useCreateModelGarden = () => {
  return useApiMutation<ModelGardenInfo, CreateModelGardenRequest>({
    url: `/modelGarden`,
    method: 'POST',
  });
};

export const useUpdateModelGarden = () => {
  return useApiMutation<ModelGardenInfo, UpdateModelGardenRequest>({
    url: `/modelGarden/{id}`,
    method: 'PUT',
  });
};

export const useDeleteModelGarden = () => {
  return useApiMutation<void, { id: string }>({
    url: `/modelGarden/{id}`,
    method: 'DELETE',
  });
};

export const useGetModelGardenAvailable = (params: GetModelGardenAvailableRequest, options?: ApiQueryOptions<GetModelGardenAvailableResponse>) => {
  return useApiQuery<GetModelGardenAvailableResponse, GetModelGardenAvailableRequest>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA, params.search ?? ''],
    url: '/modelGarden/available',
    params,
    ...options,
    timeout: 60000,
  });
};
