import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api/useApi';
import type { GetAgentLogListRequest, GetAgentLogListResponse } from './types';

export const useGetAgentLogList = (params?: GetAgentLogListRequest, options?: ApiQueryOptions<PaginatedDataType<GetAgentLogListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAgentLogListResponse>>({
    queryKey: ['agentLogList', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params || {})],
    url: 'agentLog/history/agentList',
    params: params,
    ...options,
  });
};
