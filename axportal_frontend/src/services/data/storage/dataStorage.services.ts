import type { ApiQueryOptions } from '@/hooks/common/api';
import type { PaginatedDataType } from '@/hooks/common/api/types';
import { useApiQuery } from '@/hooks/common/api/useApi';
import type * as T from './types';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';

export const useGetMDPackageList = (params?: T.GetMDPackageListRequest, options?: ApiQueryOptions<PaginatedDataType<T.MDPackageListItem>>) =>
  useApiQuery<PaginatedDataType<T.MDPackageListItem>>({
    queryKey: ['data-storage-mdpackage', DONT_SHOW_LOADING_KEYS.GRID_DATA, params ? JSON.stringify(params) : ''],
    url: '/data-stor/dataset',
    params,
    ...options,
    disableCache: true,
  });

export const useGetMDPackageDetail = (params?: T.GetMDPackageDetailRequest, options?: ApiQueryOptions<PaginatedDataType<T.MDPackageDetailItem>>) =>
  useApiQuery<PaginatedDataType<T.MDPackageDetailItem>>({
    queryKey: ['data-storage-mdpackage-detail', params ? JSON.stringify(params) : ''],
    url: '/data-stor/dataset/documents',
    params,
    ...options,
  });

export const useGetTrainingDataList = (params?: T.GetTrainingDataListRequest, options?: ApiQueryOptions<PaginatedDataType<T.TrainingDataListItem>>) =>
  useApiQuery<PaginatedDataType<T.TrainingDataListItem>>({
    queryKey: ['data-storage-training', params ? JSON.stringify(params) : ''],
    url: '/data-stor/dataset/train-eval',
    params,
    ...options,
  });

export const useGetEvaluationDataList = (params?: T.GetEvaluationDataListRequest, options?: ApiQueryOptions<PaginatedDataType<T.EvaluationDataListItem>>) =>
  useApiQuery<PaginatedDataType<T.EvaluationDataListItem>>({
    queryKey: ['data-storage-evaluation', params ? JSON.stringify(params) : ''],
    url: '/data-stor/dataset/train-eval',
    params,
    ...options,
  });

// 원천 시스템 목록 조회
export const useGetOriginSystems = (options?: ApiQueryOptions<T.OriginSystemsResponse>) =>
  useApiQuery<T.OriginSystemsResponse>({
    queryKey: ['data-storage-origin-systems'],
    url: '/data-stor/dataset/origin-systems',
    ...options,
  });
