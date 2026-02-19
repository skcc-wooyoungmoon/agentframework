import type { PaginatedDataType } from '@/hooks/common/api';

/**
 * @description 감시자 정보 타입
 */
export type AuditorInfo = {
  /** 직원명 */
  jkwNm: string;
  /** 부서명 */
  deptNm: string;
};

/**
 * @description 불용어 정보
 */
export type StopWordData = {
  /** 불용어 ID */
  id: string;
  /** 불용어 */
  stopWord: string;
};

/**
 * @description 세이프티 필터 (금지어) 정보
 */
export type SafetyFilter = {
  /** 필터 그룹 ID */
  filterGroupId: string;
  /** 필터 그룹명 */
  filterGroupName: string;
  /** 불용어 목록 */
  stopWords: StopWordData[];
  /** 공개 에셋 여부 */
  isPublicAsset: boolean;
  /** 생성일시 */
  createdAt: string;
  /** 최종 수정일시 */
  updatedAt: string;
  /** 생성자 */
  createdBy?: AuditorInfo;
  /** 수정자 */
  updatedBy?: AuditorInfo;
};

/**
 * @description 세이프티 필터 목록 조회 요청
 */
export type GetSafetyFilterListRequest = {
  /** 페이지 번호 (0부터 시작) */
  page?: number;
  /** 페이지 크기 */
  size: number;
  /** 정렬 기준 (예: 'createdDate,desc') */
  sort?: string;
  /** 검색어 (분류, 금지어 검색) */
  search?: string;
  /** 필터 조건 */
  filter?: string;
};

/**
 * @description 세이프티 필터 목록 조회 응답
 */
export type GetSafetyFilterListResponse = PaginatedDataType<SafetyFilter>;

/**
 * @description 세이프티 필터 상세 조회 응답
 */
export type SafetyFilterDetail = {
  /** 필터 아이디*/
  filterGroupId: string;
  /** 필터 그룹명 */
  filterGroupName: string;
  /** 불용어 목록 */
  stopWords: StopWordData[];
  /** 프로젝트명 */
  projectName: string;
  /** 공개 에셋 여부 */
  isPublicAsset: boolean;
  /** 생성일시 */
  createdAt: string;
  /** 최종 수정일시 */
  updatedAt: string;
  /** 생성자 */
  createdBy?: AuditorInfo;
  /** 수정자 */
  updatedBy?: AuditorInfo;
  /** 권한 수정자 정보 */
  publicAssetUpdatedBy?: AuditorInfo;
};

/**
 * @description 세이프티 필터 상세 조회 응답
 */
export type GetSafetyFilterByIdResponse = SafetyFilterDetail;

/**
 * @description 세이프티 필터 생성 요청
 */
export type CreateSafetyFilterRequest = {
  /** 필터 그룹명 */
  filterGroupName: string;
  /** 금지어 목록 */
  stopWords: string[];
};

/**
 * @description 세이프티 필터 생성 응답
 */
export type CreateSafetyFilterResponse = {
  /** 생성된 세이프티 필터 ID */
  filterGroupId: string;
  /** 분류 */
  filterGroupName: string;
  /** 금지어 목록 */
  stopWords: string[];
};

/**
 * @description 세이프티 필터 수정 요청
 */
export type UpdateSafetyFilterRequest = {
  /** 필터 그룹명 */
  filterGroupName: string;
  /** 금지어 목록 */
  stopWords: string[];
};

/**
 * @description 세이프티 필터 수정 응답
 */
export type UpdateSafetyFilterResponse = {
  /** 수정된 세이프티 필터 ID */
  filterGroupId: string;
  /** 분류 */
  filterGroupName: string;
  /** 금지어 목록 */
  stopWords: string[];
};

/**
 * @description 세이프티 필터 삭제 요청 (단일/복수 통합)
 */
export type DeleteSafetyFilterRequest = {
  /** 삭제할 세이프티 필터 ID 목록 (단일인 경우 1개, 복수인 경우 여러 개) */
  filterGroupIds: string[];
};

/**
 * @description 세이프티 필터 삭제 응답
 */
export type DeleteSafetyFilterResponse = {
  /** 성공 메시지 */
  message: string;
  /** 삭제된 건수 */
  deletedCount?: number;
};
