import { useApiQuery } from '@/hooks/common/api/useApi';
import type { DatasetCardSearchRequest, DatasetCardSearchResponse } from '@/types/data/dataset.types';

/**
 * Dataset Card 검색
 * 
 * @param params 검색 파라미터
 * @returns Dataset Card 검색 결과
 */
export const useGetDatasetCards = (
  params: DatasetCardSearchRequest = {},
  options?: any
) => {
  const {
    datasetCardType = 'DATASET',
    page = 1,
    size = 10,
    search = '',
  } = params;

  return useApiQuery<DatasetCardSearchResponse>({
    queryKey: ['datasetCards', datasetCardType, page.toString(), size.toString(), search],
    url: '/knowledge/datasets',
    params: {
      dataset_card_type: datasetCardType,
      page: page.toString(),
      size: size.toString(),
      ...(search && { search }),
    },
    staleTime: 0,
    gcTime: 0,
    ...options,
  });
};

