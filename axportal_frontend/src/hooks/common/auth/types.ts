/**
 * @description 자동 갱신 옵션
 */
export type UseAutoRefreshOptions = {
  checkInterval?: number; // 체크 간격 (ms, 기본값: 30초)
  refreshBeforeExpiry?: number; // 만료 전 갱신 시간 (분, 기본값: 5분)
  enabled?: boolean; // 자동 갱신 활성화 여부
};
