import { useApiMutation } from '@/hooks/common/api/useApi';

/**
 * MD 패키지 검색 요청 타입
 */
export type MdPackageSearchRequest = {
  page?: number;
  countPerPage?: number;
  originSystemCd?: string;
  searchWord?: string;
  datasetCardType?: string;
};

/**
 * MD 패키지 정보 타입
 */
export type MdPackageInfo = {
  datasetCardId: string;
  datasetCardName: string;
  datasetCd: string;
  datasetName: string;
  originSystemCd: string;
  originSystemName: string;
  datasetCardType: string;
  datasetSummary: string;
  preview: string;
  metadata: string;
  downloadPath: string;
};

/**
 * MD 패키지 검색 응답 타입
 */
export type MdPackageSearchResponse = {
  totalCount: number;
  page: number;
  countPerPage: number;
  items: MdPackageInfo[];
};

/**
 * MD 패키지 목록 조회 (POST)
 */
export const useSearchMdPackages = () => {
  return useApiMutation<MdPackageSearchResponse, MdPackageSearchRequest>({
    url: '/dataCtlg/md-package/search',
    method: 'POST',
  });
};

