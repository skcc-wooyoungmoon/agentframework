import type { PaginatedDataType } from '@/hooks/common/api';

// =============================================================================
// Deploy용 API Key 타입
// =============================================================================

/**
 * Deploy - API Key 정보
 */
export type ApiKey = {
  id: string;
  name: string;
  projectName: string;
  type: string;
  permission: string;
  createdAt: string;
  quota: {
    type: string;
    value: number;
  }; // 할당량
  belongsTo: {
    id: string;
    name: string;
    department: string;
  }; // 소유자 정보
  apiKey: string;
  usedCount: number;
  expired: boolean; // 만료일시 기준으로 지났을 경우 true, 아니면 false
};

/**
 * Deploy - API Key 목록 조회 요청
 */
export type GetApiKeyListRequest = {
  // page: number;
  // size: number;
  uuid?: string; // model or agent
  // userId?: string;
  projectId?: string;
  filter?: string;
  search?: string;
};

/**
 * Deploy - API Key 상세 조회 요청
 */
export type GetApiKeyDetailRequest = {
  id: string;
};

/**
 * Deploy - API Key 목록 조회 응답
 */
export type GetApiKeyListResponse = PaginatedDataType<ApiKey>;

/**
 * Deploy - API Key 상세 조회 응답
 */
export type GetApiKeyResponse = ApiKey;

// =============================================================================
// Admin용 API Key 타입
// =============================================================================

/**
 * Admin - API Key 정보
 */
export type AdminApiKeyInfo = ApiKey;

/**
 * Admin - API Key 목록 조회 요청
 */
export type AdminGetApiKeyListRequest = {
  // page: number;
  // size: number;
  userId?: string;
  projectId?: string;
  filter?: string;
  search?: string;
};

/**
 * Admin - API Key 목록 조회 응답
 */
export type AdminGetApiKeyListResponse = PaginatedDataType<AdminApiKeyInfo>;

/**
 * Admin - API Key 상세 조회 요청
 */
export type AdminGetApiKeyDetailRequest = {
  id: string;
};

export type ApiKeyScope = 'model' | 'agent';

/**
 * Admin - API Key 상세 조회 응답
 */
export type AdminGetApiKeyDetailResponse = AdminApiKeyInfo;

/**
 * API Key 생성 요청
 */
export type CreateApiKeyRequest = {
  type: 'USE' | 'ETC';
  name: string;
  scope: ApiKeyScope;
  uuid: string;
};

/**
 * API Key 생성 응답
 */
export type CreateApiKeyResponse = AdminApiKeyInfo;

/**
 * API Key 수정 요청
 */
export type UpdateApiKeyRequest = {
  id: string;
  name?: string;
  description?: string;
  isActive?: boolean;
  expiresAt?: string;
};

/**
 * API Key 수정 응답
 */
export type UpdateApiKeyResponse = AdminApiKeyInfo;

/**
 * API Key 삭제 요청
 */
export type DeleteApiKeyRequest = {
  id: string;
};

/**
 * API Key 삭제 응답
 */
export type DeleteApiKeyResponse = {
  success: boolean;
  message: string;
};

/**
 * API Key 사용차단 요청
 */
export type BlockApiKeyRequest = {
  apiKeyId: string;
};

/**
 * API Key 사용차단 응답
 */
export type BlockApiKeyResponse = {
  success: boolean;
  message: string;
};

/**
 * API Key Quota 수정 요청
 */
export type UpdateQuotaRequest = {
  quota: {
    type: string;
    value: number;
  };
};

/**
 * API Key Quota 수정 응답
 */
export type UpdateQuotaResponse = {
  success: boolean;
  message: string;
};

/**
 * API Key 통계 조회 요청
 */
export type GetApiKeyStaticRequest = {
  id: string;
  startDate?: string;
  endDate?: string;
};

/**
 * 시간별 통계 데이터
 */
export type HourlyStatistic = {
  hour: string;
  count: number;
  avgResponseTime?: number;
  successRate?: number;
  time?: number; // 더미 데이터 호환용
  rate?: number; // 더미 데이터 호환용
};

/**
 * API Key 통계 항목 (배열 아이템)
 */
export type ApiKeyStaticItem = {
  totalCount: number;
  succCount: number;
  failCount: number;
  resMiliSec: number;
  year: string;
  month: string;
  day: string;
  hour: string;
  miniute: string | null;
};

/**
 * API Key 통계 조회 응답
 */
export type GetApiKeyStaticResponse = ApiKeyStaticItem[];
