import { useQuery } from '@tanstack/react-query';
import { useSearchMdPackages, type MdPackageSearchRequest, type MdPackageSearchResponse } from '@/services/data/mdPackage.services';
import type { SuccessResponse } from '@/hooks/common/api/types';

/**
 * MD 패키지 목록 조회 훅
 * 
 * @param request 검색 요청 파라미터
 * @param enabled 쿼리 활성화 여부
 * @returns MD 패키지 목록 쿼리 결과
 */
export const useMdPackages = (request: MdPackageSearchRequest, enabled: boolean = true) => {
  const { mutateAsync } = useSearchMdPackages();

  return useQuery<SuccessResponse<MdPackageSearchResponse>, Error, MdPackageSearchResponse>({
    queryKey: ['mdPackages', request],
    queryFn: async () => {
      const result = await mutateAsync(request);
      return result;
    },
    select: (data) => {
      // data가 이미 MdPackageSearchResponse 형태라면 그대로 반환
      // data가 SuccessResponse 형태라면 data.data 반환
      if ('data' in data && data.data) {
        return data.data as MdPackageSearchResponse;
      }
      return data as unknown as MdPackageSearchResponse;
    },
    enabled,
    staleTime: 1000 * 60 * 5, // 5분
  });
};

