import type {
  ApiQueryOptions,
  PaginatedDataType,
} from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api/useApi';
import { api } from '@/configs/axios.config';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { 
  UserActivity,
  UserActivityStats,
  GetUserActivityListParams
} from '@/stores/admin/userUsageMgmt/types';

/**
 * 사용자 이용 현황 목록을 조회합니다.
 * @param params 조회 조건
 * @param options react-query 옵션
 * @returns 사용자 이용 현황 목록과 페이지네이션 정보
 */
export const useGetUseUsageMgmtList = (
  params?: GetUserActivityListParams,
  options?: ApiQueryOptions<PaginatedDataType<UserActivity>>
) => {
  return useApiQuery<PaginatedDataType<UserActivity>>({
    queryKey: ['user-activity-list', DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/admin/user-usage-mgmt',
    params,
    ...options,
  });
};


/**
 * 사용자 이용 현황 통계를 조회합니다.
 * @param params 조회 조건 (조회기간, 년월, 프로젝트명)
 * @param options react-query 옵션
 * @returns 사용자 이용 현황 통계
 */
export const useGetUserActivityStats = ( 
  params?: { 
    searchType?: string;    // 조회기간 (month, week, day)
    selectedDate?: string;  // 선택된 날짜 (YYYY-MM 또는 YYYY-MM-DD)
    projectType?: string;   // 프로젝트명
    startDate?: string; 
    endDate?: string; 
  },
  options?: ApiQueryOptions<UserActivityStats>
) => {
  return useApiQuery<UserActivityStats>({
    queryKey: ['user-usage-mgmt-stats', JSON.stringify(params)],
    url: 'admin/user-usage-mgmt/stats',
    params,
    disableCache: true,
    refetchOnMount: 'always',
    staleTime: 0,
    gcTime: 0,
    ...options,
  });
};


/**
 * 공통 프로젝트 목록을 조회합니다.
 * @param options react-query 옵션
 * @returns 프로젝트 목록 (prjSeq, uuid, prjNm)
 */
export const useGetCommonProjects = (
  options?: ApiQueryOptions<{ prjSeq: number; uuid: string; prjNm: string }[]>
) => {
  return useApiQuery<{ prjSeq: number; uuid: string; prjNm: string }[]>({
    queryKey: ['common-projects'],
    url: '/admin/user-usage-mgmt/common/projects',
    disableCache: true,
    refetchOnMount: 'always',
    staleTime: 0,
    gcTime: 0,
    ...options,
  });
};


/**
 * 사용자 이용 현황 데이터를 Excel로 내보냅니다.
 * @param selectedRows 선택된 행 전체 데이터 (필수)
 * @param headers 그리드 헤더 정보 (필수)
 * @returns Excel 파일 다운로드 함수
 */
export const exportUserUsageMgmts = async (
  selectedRows: any[],
  headers: any[]
) => {
  // 선택된 행이 없으면 에러 발생
  if (!selectedRows || selectedRows.length === 0) {
    throw new Error('다운로드할 항목을 선택해주세요.');
  }
  
  // 헤더 정보가 없으면 에러 발생
  if (!headers || headers.length === 0) {
    throw new Error('헤더 정보가 없습니다.');
  }
  
  // 선택된 ID 목록 추출
  const selectedIds = selectedRows.map(row => row.id);
  
  // 데이터를 Map<String, Object> 형태로 변환
  const data = selectedRows.map((row) => {
    const rowData: { [key: string]: any } = {};
    headers.forEach(header => {
      if (header.field === 'no') {
        // NO 필드는 원본 데이터의 no 값을 그대로 사용
        rowData[header.field] = row.no;
      } else {
        rowData[header.field] = row[header.field];
      }
    });
    return rowData;
  });
  
  const requestData = {
    selectedIds: selectedIds,
    headers: headers,
    data: data
  };
  
  // POST 방식으로 백엔드 요구 구조에 맞게 전송
  return api.post('/admin/user-usage-mgmt/export/data', requestData, {
    responseType: 'blob'  // 필수!
  });
};
