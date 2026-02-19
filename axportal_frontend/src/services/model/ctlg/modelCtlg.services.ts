import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import { useApiMutation, useApiQuery, type ApiMutationOptions, type ApiQueryOptions, type PaginatedDataType } from '@/hooks/common/api';
import type { CreateModelCtlgRequest, DeleteModelCtlgBulkRequest, ModelCtlgListRequest, ModelCtlgType, ModelProviderType } from './types';

/**
 * 모델 카드 목록 조회
 * @param request
 * @param options
 * @returns
 */
export const useGetModelCtlgList = (request: ModelCtlgListRequest, options?: ApiQueryOptions<PaginatedDataType<ModelCtlgType>>) => {
  return useApiQuery<PaginatedDataType<ModelCtlgType>, ModelCtlgListRequest>({
    queryKey: ['model-ctlg', DONT_SHOW_LOADING_KEYS.GRID_DATA, request?.queryKey || ''], // queryKey 사용 안함 -> disableCache 사용
    url: '/modelCtlg',
    params: { ...request, sort: 'created_at,desc' },
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

/**
 * 모델 카드 상세 조회
 * @param id
 * @returns
 */
export const useGetModelCtlgById = (id: string) => {
  return useApiQuery<ModelCtlgType, string>({
    queryKey: ['model-ctlg-detail', id],
    url: `/modelCtlg/${id}`,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

/**
 * 모델 카드 삭제
 * @returns
 */
export const useDeleteModelCtlgBulk = () => {
  return useApiMutation<void, DeleteModelCtlgBulkRequest>({
    method: 'DELETE',
    url: `/modelCtlg`,
  });
};

export const useUpdateModelCtlg = (config?: ApiMutationOptions<void, ModelCtlgType>) => {
  return useApiMutation<void, ModelCtlgType>({
    method: 'PUT',
    url: `/modelCtlg/{id}`,
    ...config,
  });
};

/**
 * 모델 카탈로그 생성
 * @param options
 * @returns
 */
export const useCreateModelCtlg = (options?: ApiMutationOptions<ModelCtlgType, CreateModelCtlgRequest>) => {
  return useApiMutation<ModelCtlgType, CreateModelCtlgRequest>({
    method: 'POST',
    url: '/modelCtlg',
    ...options,
  });
};

/**
 * 모델 유형 조회
 * @returns
 */
export const useGetModelTypes = (options?: ApiQueryOptions<{ types: string[] }>) => {
  return useApiQuery<{ types: string[] }>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/modelCtlg/types',
    ...options,
  });
};

/**
 * 모델 공급사 조회
 * @returns
 */
export const useGetModelProviders = () => {
  return useApiQuery<PaginatedDataType<ModelProviderType>>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/modelCtlg/providers',
  });
};

/**
 * 모델 태그 조회
 * @returns
 */
export const useGetModelTags = (options?: ApiQueryOptions<{ tags: string[] }>) => {
  return useApiQuery<{ tags: string[] }>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/modelCtlg/tags',
    ...options,
  });
};
