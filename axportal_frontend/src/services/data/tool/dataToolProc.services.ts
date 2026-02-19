import type { ApiQueryOptions } from '@/hooks/common/api';
import type { PaginatedDataType } from '@/hooks/common/api/types';
import { useApiQuery } from '@/hooks/common/api/useApi';
import type * as T from './types';

export const useGetProcList = (params?: T.GetToolRequest, options?: ApiQueryOptions<PaginatedDataType<T.GetProcListResponse>>) =>
  useApiQuery<PaginatedDataType<T.GetProcListResponse>>({
    queryKey: ['data-tool-processors', params ? JSON.stringify(params) : ''],
    url: '/dataTool/processors',
    params,
    ...options,
  });

export const useGetProcById = (processorId?: string, options?: ApiQueryOptions<T.GetProcListResponse>) =>
  useApiQuery<T.GetProcListResponse>({
    queryKey: ['data-tool-processor', processorId || ''],
    url: `/dataTool/processors/${processorId}`,
    enabled: !!processorId,
    ...options,
  });
