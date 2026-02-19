import type { ApiQueryOptions } from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api/useApi';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/configs/axios.config';
import type { GetModelHistoryListRequest, GetModelHistoryListResponse } from './types';

/**
 * 모델 사용 이력 조회 API 훅
 *
 * @param params - 조회 파라미터 (시작일, 종료일, 페이지, 필터 등)
 * @param options - API 쿼리 옵션
 * @returns 모델 사용 이력 조회 결과
 */
export const useGetModelHistoryList = (params?: GetModelHistoryListRequest, options?: ApiQueryOptions<GetModelHistoryListResponse>) => {
  return useApiQuery<GetModelHistoryListResponse>({
    queryKey: ['modelHistory'],
    url: 'modelDeployLog/history',
    params: params,
    ...options,
  });
};

/**
 * 모델 사용 이력 CSV 다운로드 API 훅
 *
 * @param params - 다운로드 파라미터 (시작일, 종료일, 필터 등)
 * @returns CSV 다운로드 함수
 */
export const useDownloadModelHistoryCsv = () => {
  return useMutation({
    mutationFn: async (params: any) => {
      const response = await api.get('modelDeployLog/history/download', {
        params,
        responseType: 'blob',
      });
      return response.data;
    },
  });
};
