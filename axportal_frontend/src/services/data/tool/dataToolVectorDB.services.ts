import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api';
import type { PaginatedDataType } from '@/hooks/common/api/types';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';
import type * as T from './types';

export const useGetVectorDBList = (params?: T.GetToolRequest, options?: ApiQueryOptions<PaginatedDataType<T.GetVectorDBListResponse>>) =>
  useApiQuery<PaginatedDataType<T.GetVectorDBListResponse>>({
    url: '/dataTool/vectorDb',
    params,
    ...options,
    disableCache: true,
  });

export const useGetVectorDBById = (vectorDbId?: string, options?: ApiQueryOptions<T.GetVectorDBResponse>) =>
  useApiQuery<T.GetVectorDBResponse>({
    queryKey: ['data-tool-vector-db', vectorDbId || ''],
    url: `/dataTool/vectorDb/${vectorDbId}`,
    enabled: !!vectorDbId,
    ...options,
    disableCache: true,
  });

export const useDeleteVectorDB = (options?: ApiMutationOptions<string, { vectorDbId: string }>) => {
  return useApiMutation<string, { vectorDbId: string }>({
    method: 'DELETE',
    url: '/dataTool/vectorDb/{vectorDbId}', // URL 템플릿: {vectorDbId}가 request.uuid로 치환됨
    ...options,
  });
};

export const useCreateVectorDB = (options?: ApiMutationOptions<T.CreateVectorDBResponse, T.CreateVectorDBRequest>) => {
  return useApiMutation<T.CreateVectorDBResponse, T.CreateVectorDBRequest>({
    method: 'POST',
    url: '/dataTool/vectorDb',
    ...options,
  });
};

export const useUpdateVectorDB = (options?: ApiMutationOptions<string, T.UpdateVectorDBRequest & { vectorDbId: string }>) => {
  return useApiMutation<string, T.UpdateVectorDBRequest & { vectorDbId: string }>({
    method: 'PUT',
    url: '/dataTool/vectorDb/{vectorDbId}', // URL 템플릿: {vectorDbId}가 request.vectorDbId로 치환됨
    ...options,
  });
};

// 커스텀 스크립트 연결 정보
export const useGetConnectionArgs = () =>
  useApiQuery<T.GetConnectionArgs>({
    queryKey: ['data-tool-vectorDb'],
    url: `/dataTool/vectorDb/connetionArgs`,
  });
