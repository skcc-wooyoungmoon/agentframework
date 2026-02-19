import type { PaginatedDataType } from '@/hooks/common/api/types';

/**
 * 모델 사용 이력 조회 요청 타입
 */
export type GetModelHistoryListRequest = {
  /** 필드 선택 (콤마 구분) */
  fields?: string;
  /** 오류 로그만 조회 여부 */
  error_logs?: boolean;
  /** 조회 시작 날짜 (YYYY-MM-DD) */
  from_date: string;
  /** 조회 종료 날짜 (YYYY-MM-DD) */
  to_date: string;
  /** 페이지 번호 (1부터 시작) */
  page: number;
  /** 페이지당 항목 수 */
  size: number;
  /** 필터 (key:value,...) */
  filter?: string;
  /** 검색 (key:*value*...) */
  search?: string;
  /** 정렬 (field,order) */
  sort?: string;
};

/**
 * 페이지 네비게이션 링크 타입
 */
export type PaginationLink = {
  /** 링크 URL */
  url: string | null;
  /** 링크 라벨 */
  label: string;
  /** 현재 활성 페이지 여부 */
  active: boolean;
  /** 페이지 번호 */
  page: number | null;
};

/**
 * 페이징 정보 타입 (Laravel 스타일)
 */
export type ModelHistoryPagination = {
  /** 현재 페이지 번호 (1부터 시작) */
  page: number;
  /** 첫 번째 페이지 URL */
  firstPageUrl: string;
  /** 현재 페이지의 첫 번째 항목 번호 (1부터 시작) */
  from: number;
  /** 마지막 페이지 번호 */
  lastPage: number;
  /** 페이지 네비게이션 링크 목록 */
  links: PaginationLink[];
  /** 다음 페이지 URL */
  nextPageUrl: string | null;
  /** 페이지당 항목 수 */
  itemsPerPage: number;
  /** 이전 페이지 URL */
  prevPageUrl: string | null;
  /** 현재 페이지의 마지막 항목 번호 (1부터 시작) */
  to: number;
  /** 전체 항목 수 */
  total: number;

  hasNext: boolean;
};

/**
 * 페이로드 타입 (새로운 API 응답 구조와 일치)
 */
export type Payload = {
  /** 페이징 정보 */
  pagination: ModelHistoryPagination;
};

/**
 * 모델 사용 이력 개별 레코드 타입 (백엔드 ModelHistoryRecordRes와 일치)
 */
export type ModelHistoryRecord = {
  /** 요청 고유 식별자 (transactionId에서 매핑) */
  requestId: string;
  /** 모델 식별자 */
  modelId: string;
  /** 모델 타입 */
  modelType?: string;
  /** 프로젝트 식별자 */
  projectId?: string;
  /** 애플리케이션 식별자 */
  appId?: string;
  /** 사용자 정보 */
  user: string;
  /** 요청 시간 (ISO 8601 형식) */
  requestTime: string;
  /** 응답 시간 (ISO 8601 형식) */
  responseTime?: string;
  /** 경과 시간 (초) */
  elapsedTime?: number;
  /** 엔드포인트 URL */
  endpoint?: string;
  /** 모델 이름 */
  modelName?: string;
  /** 모델 식별자 */
  modelIdentifier?: string;
  /** 모델 서빙 ID */
  modelServingId?: string;
  /** 모델 서빙 이름 */
  modelServingName?: string;
  /** 객체 타입 */
  objectType?: string;
  /** API 키 */
  apiKey?: string;
  /** 모델 키 */
  modelKey?: string;
  /** 입력 JSON */
  inputJson?: string;
  /** 출력 JSON */
  outputJson?: string;
  /** 완성 토큰 수 */
  completionTokens?: number;
  /** 프롬프트 토큰 수 */
  promptTokens?: number;
  /** 총 토큰 수 */
  totalTokens?: number;
  /** 토큰 사용량 (총 토큰 수와 동일) */
  tokenCount?: number;
  /** 트랜잭션 ID */
  transactionId?: string;
  /** 에이전트 앱 서빙 ID */
  agentAppServingId?: string;
  /** 회사 정보 */
  company?: string;
  /** 부서 정보 */
  department?: string;
  /** 채팅 ID */
  chatId?: string;
  /** 요청 상태 (기본값: success) */
  status: string;
  /** 오류 메시지 (실패 시) */
  errorMessage?: string;
  /** 오류 코드 (실패 시) */
  errorCode?: string;
};

/**
 * 모델 사용 이력 조회 응답 타입
 * 백엔드 PageResponse 구조와 일치
 */
export type GetModelHistoryListResponse = PaginatedDataType<ModelHistoryRecord>;

export type LogGridItem = { no?: number } & ModelHistoryRecord;
