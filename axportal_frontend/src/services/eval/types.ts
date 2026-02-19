/**
 * Evaluation 관련 타입 정의
 */

/**
 * Task 정보 타입
 */
export interface TaskInfo {
  /** Task 고유 식별자 */
  id: string;
  /** 화면 표시용 ID */
  displayId: string;
  /** Task 이름 */
  name: string;
  /** Task 설명 */
  description: string;
  /** 생성 일시 */
  createdAt: string;
  /** 리다이렉트 URL */
  redirectUrl: string;
  /** public 여부 */
  isPublic: boolean;
}

/**
 * Task 목록 조회 응답 타입
 */
export interface TaskListResponse {
  /** 전체 데이터 개수 */
  totalDataCount: number;
  /** 전체 페이지 수 */
  totalPageCount: number;
  /** Task 목록 */
  tasks: TaskInfo[];
}

/**
 * Task 목록 조회 요청 타입
 */
export interface TaskListRequest {
  /** 그룹 */
  group: string;
  /** Task 카테고리 */
  category: string;
  /** 페이지 번호 (1부터 시작) */
  page?: number;
  /** 페이지당 항목 수 */
  pageSize?: number;
  /** 검색어 */
  search?: string;
}
