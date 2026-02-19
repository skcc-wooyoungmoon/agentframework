export interface SuccessResponse<T = any> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  path: string;
}

/**
 * @description 에러 정보 클래스
 */
export type ErrorInfoType = {
  hscode: string; // http status code
  code: string; // Application 코드
  message: string;
  details: string;
};

/**
 * @description 에러 응답 타입
 */
export interface ErrorResponse {
  success: boolean;
  message: string;
  error: ErrorInfoType;
  timestamp: string;
  path: string;
}

/**
 * @description 백엔드 페이지네이션 데이터 타입
 */
export interface PaginatedDataType<T> {
  /** 데이터 목록 */
  content: T[];
  pageable: {
    page: number;
    size: number;
    sort: string;
  };
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}
