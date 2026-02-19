import { atom } from 'jotai';
import type { UserActivity, UserActivityStats } from './types';

// 사용자 이용 현황 목록 데이터
export const userActivityListAtom = atom<UserActivity[]>([]);

// 사용자 이용 현황 통계 데이터
export const userActivityStatsAtom = atom<UserActivityStats | null>(null);

// 선택된 사용자 이용 현황 상세 데이터
export const selectedUserActivityAtom = atom<UserActivity | null>(null);

// 로딩 상태
export const userActivityLoadingAtom = atom<boolean>(false);

// 에러 상태
export const userActivityErrorAtom = atom<string | null>(null);

// 검색 관련 atoms
export const userActivitySearchTextAtom = atom<string>('');
export const userActivitySearchFilterAtom = atom<string>('사용자명');
export const userActivityCurrentPageAtom = atom<number>(1);
export const userActivityPageSizeAtom = atom<number>(10);

// 사용자 이용 현황 선택된 행 저장 atom
export const userUsageSelectedRowsAtom = atom<Record<string, any>>({});

// 사용자 이용 현황 히스토리 페이지네이션 상태
export const userUsageHistPaginationAtom = atom<{ page: number; pageSize: number }>({
  page: 1,
  pageSize: 12,
});

// 사용자 이용 현황 히스토리 필터 상태
export type UserUsageHistFilter = {
  dateType: string;
  projectName: string;
  result: string;
  searchType: string;
  searchValue: string;
  fromDate: string;
  toDate: string;
};

export const userUsageHistFilterAtom = atom<UserUsageHistFilter | null>(null);

// 날짜 필터 atoms
export const userActivityStartDateAtom = atom<string>('');
export const userActivityEndDateAtom = atom<string>('');

// 활동 타입 필터 atom
export const userActivityTypeFilterAtom = atom<string>('전체');

// 정렬 관련 atoms
export const userActivitySortColumnAtom = atom<string>('created_at');
export const userActivitySortDirectionAtom = atom<'asc' | 'desc'>('desc');

// 페이지네이션 정보
export const userActivityTotalItemsAtom = atom<number>(0);
export const userActivityTotalPagesAtom = atom<number>(0);

// 필터 및 검색 조건을 종합한 쿼리 atom
export const userActivityQueryAtom = atom((get) => {
  const searchText = get(userActivitySearchTextAtom);
  const searchFilter = get(userActivitySearchFilterAtom);
  const currentPage = get(userActivityCurrentPageAtom);
  const pageSize = get(userActivityPageSizeAtom);
  const startDate = get(userActivityStartDateAtom);
  const endDate = get(userActivityEndDateAtom);
  const activityType = get(userActivityTypeFilterAtom);
  const sortColumn = get(userActivitySortColumnAtom);
  const sortDirection = get(userActivitySortDirectionAtom);

  return {
    search: searchText,
    searchFilter,
    page: currentPage - 1, // API에서 0-based indexing 사용
    size: pageSize,
    startDate,
    endDate,
    activityType: activityType === '전체' ? '' : activityType,
    sort: `${sortColumn},${sortDirection}`,
  };
});

// 모든 사용자 이용 현황 상태 초기화 함수
export const resetUserActivityDataAtom = atom(
  null,
  (_, set) => {
    // 데이터 초기화
    set(userActivityListAtom, []);
    set(userActivityStatsAtom, null);
    set(selectedUserActivityAtom, null);
    
    // 상태 초기화
    set(userActivityLoadingAtom, false);
    set(userActivityErrorAtom, null);
    
    // 검색 및 필터 초기화
    set(userActivitySearchTextAtom, '');
    set(userActivitySearchFilterAtom, '사용자명');
    set(userActivityCurrentPageAtom, 1);
    set(userActivityPageSizeAtom, 10);
    set(userActivityStartDateAtom, '');
    set(userActivityEndDateAtom, '');
    set(userActivityTypeFilterAtom, '전체');
    
    // 정렬 초기화
    set(userActivitySortColumnAtom, 'created_at');
    set(userActivitySortDirectionAtom, 'desc');
    
    // 페이지네이션 초기화
    set(userActivityTotalItemsAtom, 0);
    set(userActivityTotalPagesAtom, 0);
  }
);

// 상수들은 types.ts로 이동됨
// 필요시 types.ts에서 import하여 사용
