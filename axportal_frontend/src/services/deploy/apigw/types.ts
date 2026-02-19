import type { ApiKeyStaticItem } from '../apikey/types';

export type GetApiEndpointStatisticsRequest = {
  id: string;
  startDate?: string;
  endDate?: string;
};

/**
 * API Key 통계 조회 응답
 */
export type GetApiEndpointStatisticsResponse = ApiKeyStaticItem[];

/**
 * API 엔드포인트 발급 상태 조회 응답
 */
export type GetCheckApiEndpointResponse = {
  status: string;
  message: string;
  infWorkSeq: string;
};
