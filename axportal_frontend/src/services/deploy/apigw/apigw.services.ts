import { useApiMutation, useApiQuery, type ApiQueryOptions } from '@/hooks/common/api';
import type { GetApiEndpointStatisticsRequest, GetApiEndpointStatisticsResponse, GetCheckApiEndpointResponse } from './types';

export const useGetApiEndpointStatistics = ({ id, startDate, endDate }: GetApiEndpointStatisticsRequest, options?: ApiQueryOptions<GetApiEndpointStatisticsResponse>) => {
  return useApiQuery<GetApiEndpointStatisticsResponse>({
    url: `/api-gw/statistics/${id}`,
    params: { startDate, endDate },
    ...options,
  });
};

export const useCheckApiEndpoint = (type: string, id: string) => {
  return useApiQuery<GetCheckApiEndpointResponse>({
    url: `/api-gw/endpoint/${type}-${id}/check`,
  });
};

export const usePostRetryApiEndpoint = (apiId: string) => {
  return useApiMutation({
    url: `/api-gw/enpoint/${apiId}/retry`,
    method: 'POST',
  });
};
