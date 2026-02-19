import type { ApiQueryOptions } from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api';
import type { ManagerInfoResponse, ManagerInfoResponseBulkRequest, ManagerInfoResponseRequest } from './types';

export const useGetManagerInfo = (params: ManagerInfoResponseRequest, options?: ApiQueryOptions<ManagerInfoResponse>) => {
  return useApiQuery<ManagerInfoResponse>({
    url: `/common/manager-info`,
    params,
    ...options,
    disableCache: true,
  });
};

export const useGetManagerInfoBulk = (params: ManagerInfoResponseBulkRequest, options?: ApiQueryOptions<ManagerInfoResponse[]>) => {
  return useApiQuery<ManagerInfoResponse[]>({
    url: `/common/manager-info/bulk`,
    queryKey: [JSON.stringify(params)],
    params,
    paramsSerializer: (params: any) => {
      const searchParams = new URLSearchParams();
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (Array.isArray(value)) {
          // 배열을 중복 키 형태로 직렬화 (values=value1&values=value2)
          value.forEach(item => {
            searchParams.append(key, item);
          });
        } else {
          searchParams.append(key, value);
        }
      });
      return searchParams.toString();
    },
    ...options,
    disableCache: true,
  });
};
