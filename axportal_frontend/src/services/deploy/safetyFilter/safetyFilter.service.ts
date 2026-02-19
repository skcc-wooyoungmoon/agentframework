import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants.ts';
import type {
  CreateSafetyFilterRequest,
  CreateSafetyFilterResponse,
  DeleteSafetyFilterRequest,
  DeleteSafetyFilterResponse,
  GetSafetyFilterByIdResponse,
  GetSafetyFilterListRequest,
  GetSafetyFilterListResponse,
  UpdateSafetyFilterRequest,
  UpdateSafetyFilterResponse,
} from './types';

/**
 * 세이프티 필터 목록 조회
 *
 * @param params 조회 파라미터 (페이지, 사이즈, 검색어 등)
 * @param options React Query 옵션
 */
export const useGetSafetyFilterList = (params?: GetSafetyFilterListRequest, options?: ApiQueryOptions<GetSafetyFilterListResponse>) => {
  return useApiQuery<GetSafetyFilterListResponse>({
    queryKey: [
      DONT_SHOW_LOADING_KEYS.GRID_DATA,
      'safety-filter-list',
      String(params?.page ?? 1),
      String(params?.size ?? 12),
      params?.search ?? '',
      params?.sort ?? '',
    ],
    url: '/safety-filter',
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * 세이프티 필터 상세 조회
 *
 * @param filterId 세이프티 필터 ID
 * @param options React Query 옵션
 */
export const useGetSafetyFilterDetail = (filterId: string, options?: ApiQueryOptions<GetSafetyFilterByIdResponse>) => {
  return useApiQuery<GetSafetyFilterByIdResponse>({
    queryKey: ['safety-filter-detail', filterId],
    url: `/safety-filter/${filterId}`,
    ...options,
  });
};

/**
 * 세이프티 필터 생성
 *
 * @param options React Query Mutation 옵션
 */
export const useCreateSafetyFilter = (options?: ApiMutationOptions<CreateSafetyFilterResponse, CreateSafetyFilterRequest>) => {
  return useApiMutation<CreateSafetyFilterResponse, CreateSafetyFilterRequest>({
    method: 'POST',
    url: '/safety-filter',
    ...options,
  });
};

/**
 * 세이프티 필터 수정
 *
 * @param filterId 세이프티 필터 ID
 * @param options React Query Mutation 옵션
 */
export const useUpdateSafetyFilter = (filterId: string, options?: ApiMutationOptions<UpdateSafetyFilterResponse, UpdateSafetyFilterRequest>) => {
  return useApiMutation<UpdateSafetyFilterResponse, UpdateSafetyFilterRequest>({
    method: 'PUT',
    url: `/safety-filter/${filterId}`,
    ...options,
  });
};

/**
 * 세이프티 필터 삭제 (단일/복수 통합)
 *
 * @param options React Query Mutation 옵션
 * @example
 * // 단일 삭제
 * deleteSafetyFilter({ filterGroupIds: ['id1'] })
 *
 * // 복수 삭제
 * deleteSafetyFilter({ filterGroupIds: ['id1', 'id2', 'id3'] })
 */
export const useDeleteSafetyFilter = (options?: ApiMutationOptions<DeleteSafetyFilterResponse, DeleteSafetyFilterRequest>) => {
  return useApiMutation<DeleteSafetyFilterResponse, DeleteSafetyFilterRequest>({
    method: 'DELETE',
    url: '/safety-filter',
    ...options,
  });
};
