import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import type { GetLoginResponse } from '@/services/auth/types';

/**
 * @description [토큰 공통] 세션 스토리지에 토큰 저장 (expires_at 지원)
 */
const setTokens = ({ access_token = '', refresh_token = '', expires_at = '', axAccessToken = '' }: Partial<GetLoginResponse>): void => {
  if (!access_token || !refresh_token) {
    // console.error('🎄 토큰 저장 실패: access_token 또는 refresh_token이 없습니다.');
    return;
  }
  sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, access_token);
  sessionStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refresh_token);
  sessionStorage.setItem(STORAGE_KEYS.AX_ACCESS_TOKEN, axAccessToken);

  // expires_at이 전달되면 함께 저장
  if (expires_at) {
    sessionStorage.setItem(STORAGE_KEYS.EXPIRES_AT, expires_at);
  }
};

/**
 * @description [토큰 공통] 세션 스토리지에서 ACCESS_TOKEN 조회
 */
const getAccessToken = (): string | null => {
  return sessionStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
};

const getAxAccessToken = (): string | null => {
  return sessionStorage.getItem(STORAGE_KEYS.AX_ACCESS_TOKEN);
};

/**
 * @description [토큰 공통] 세션 스토리지에서 REFRESH_TOKEN 조회
 */
const getRefreshToken = (): string | null => {
  return sessionStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
};

/**
 * @description [토큰 공통] 세션 스토리지에서 만료 시각 조회
 */
const getAccessTokenExpiresAt = (): string | null => {
  return sessionStorage.getItem(STORAGE_KEYS.EXPIRES_AT);
};
/**
 * @description [토큰 공통] 세션 스토리지에서 토큰 삭제
 */
const clearTokens = (): void => {
  sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
  sessionStorage.removeItem(STORAGE_KEYS.AX_ACCESS_TOKEN);
  sessionStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
  sessionStorage.removeItem(STORAGE_KEYS.EXPIRES_AT);
  // 로그아웃 시 최근 메뉴도 초기화
  sessionStorage.removeItem(STORAGE_KEYS.RECENT_MENU_ITEMS);
  // 로그아웃 이벤트 발생 (다른 컴포넌트에서 감지 가능)
  window.dispatchEvent(new CustomEvent('logout'));
  // console.log('🎄 토큰 삭제 성공');
};

/**
 * @description 안전한 날짜 파싱 (유효하지 않으면 NaN)
 * - 마이크로초(>3자리) 절삭 → 밀리초 3자리로 정규화
 * - 타임존이 없는 ISO 문자열도 우선 로컬 시간으로 파싱 시도
 * - 여전히 실패하면 UTC(Z) 부여하여 한 번 더 파싱 시도
 * - 숫자 형태(초/밀리초) 문자열도 지원
 */
const parseIsoTime = (isoString: string | null): number => {
  if (!isoString) return NaN;

  const raw = String(isoString).trim();

  // 1) 숫자만으로 구성된 타임스탬프 지원 (초/밀리초 추정)
  if (/^\d+$/.test(raw)) {
    const num = Number(raw);
    if (!Number.isFinite(num)) return NaN;
    // 10자리 이하이면 초 단위로 간주
    if (raw.length <= 10) return num * 1000;
    return num;
  }

  // 2) 원본 파싱 먼저 시도
  let ms = new Date(raw).getTime();
  if (Number.isFinite(ms)) return ms;

  // 3) ISO 형태 정규화: 마이크로초(>3자리) → 3자리로 절삭
  //    예: 2025-08-09T17:39:59.098222 -> 2025-08-09T17:39:59.098
  const isoMatch = raw.match(/^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2})(\.(\d+))?([Zz]|[+-]\d{2}:\d{2})?$/);
  if (isoMatch) {
    const base = isoMatch[1];
    const frac = isoMatch[3] ?? ''; // 소수부 숫자만
    const tz = isoMatch[4] ?? '';

    const normalizedFrac = frac ? '.' + frac.slice(0, 3).padEnd(3, '0') : '';

    const candidate = `${base}${normalizedFrac}${tz}`;
    ms = new Date(candidate).getTime();
    if (Number.isFinite(ms)) return ms;

    // 타임존이 없을 경우 마지막 시도로 UTC 부여
    if (!tz) {
      const candidateUtc = `${base}${normalizedFrac}Z`;
      ms = new Date(candidateUtc).getTime();
      if (Number.isFinite(ms)) return ms;
    }
  }

  return NaN;
};

/**
 * @description [토큰 공통] 액세스 토큰 만료 여부 (expires_at 기준)
 */
const isAccessTokenExpired = (): boolean => {
  const expiresAtMs = parseIsoTime(getAccessTokenExpiresAt());
  if (!Number.isFinite(expiresAtMs)) return true;
  return Date.now() >= (expiresAtMs as number);
};

/**
 * @description [토큰 공통] 액세스 토큰 만료 임박 여부 (기본 5분)
 */
const isAccessTokenExpiringSoon = (thresholdMinutes: number = 5): boolean => {
  const expiresAtMs = parseIsoTime(getAccessTokenExpiresAt());
  if (!Number.isFinite(expiresAtMs)) return true;
  const thresholdMs = thresholdMinutes * 60 * 1000;
  return (expiresAtMs as number) - Date.now() <= thresholdMs;
};

/**
 * @description [토큰 공통] 액세스 토큰 만료까지 남은 시간(초)
 */
const getAccessTokenTimeUntilExpiry = (): number => {
  const expiresAtMs = parseIsoTime(getAccessTokenExpiresAt());
  if (!Number.isFinite(expiresAtMs)) return 0;
  return Math.max(0, Math.floor(((expiresAtMs as number) - Date.now()) / 1000));
};

/**
 * @description [토큰 공통] 액세스 토큰 존재 여부 확인
 */
const hasAccessToken = (): boolean => {
  return !!getAccessToken();
};

/**
 * @description [토큰 공통] 리프레시 토큰 존재 여부 확인
 */
const hasRefreshToken = (): boolean => {
  return !!getRefreshToken();
};

/**
 * @description [토큰 공통] 인증 상태 확인 (토큰 존재 기준)
 */
const isAuthenticated = (): boolean => {
  return hasAccessToken() && hasRefreshToken();
};

/**
 * @description [JWT 공통] JWT 토큰을 파싱하여 헤더, 페이로드, 서명을 분리
 */
const parseJwtToken = (token: string): { header: any; payload: any; signature: string } | null => {
  if (!token) return null;

  const parts = token.split('.');
  if (parts.length !== 3) {
    // console.error('🎄 JWT 토큰 형식이 올바르지 않습니다.');
    return null;
  }

  try {
    const [headerPart, payloadPart, signature] = parts;

    // Base64URL 디코딩
    const header = JSON.parse(atob(headerPart.replace(/-/g, '+').replace(/_/g, '/')));
    const payload = JSON.parse(atob(payloadPart.replace(/-/g, '+').replace(/_/g, '/')));

    return {
      header,
      payload,
      signature,
    };
  } catch (error) {
    // console.error('🎄 JWT 토큰 파싱 실패:', error);
    return null;
  }
};

/**
 * @description [JWT 공통] JWT 토큰의 페이로드 정보 조회
 */
const getJwtPayload = (token: string): any | null => {
  const parsed = parseJwtToken(token);
  return parsed?.payload || null;
};

/**
 * @description [JWT 공통] JWT 토큰의 헤더 정보 조회
 */
const getJwtHeader = (token: string): any | null => {
  const parsed = parseJwtToken(token);
  return parsed?.header || null;
};

/**
 * @description [JWT 공통] JWT 토큰의 만료 시간 조회 (payload의 exp 필드)
 */
const getJwtExpirationTime = (token: string): number | null => {
  const payload = getJwtPayload(token);
  if (!payload || !payload.exp) return null;

  // JWT의 exp는 초 단위이므로 밀리초로 변환
  return payload.exp * 1000;
};

/**
 * @description [JWT 공통] JWT 토큰이 만료되었는지 확인
 */
const isJwtTokenExpired = (token: string): boolean => {
  const expTime = getJwtExpirationTime(token);
  if (!expTime) return true;

  return Date.now() >= expTime;
};

/**
 * @description [JWT 공통] JWT 토큰의 사용자 정보 조회 (payload에서 sub, user_id 등)
 */
const getJwtUserInfo = (token: string): { sub?: string; user_id?: string; email?: string; [key: string]: any } | null => {
  const payload = getJwtPayload(token);
  if (!payload) return null;

  return {
    sub: payload.sub,
    user_id: payload.user_id || payload.userId,
    email: payload.email,
    name: payload.name,
    ...payload,
  };
};

// 필요시 객체로도 내보내기
export default {
  setTokens,
  getAccessToken,
  getAxAccessToken,
  getRefreshToken,
  getAccessTokenExpiresAt,
  clearTokens,
  hasAccessToken,
  hasRefreshToken,
  isAuthenticated,
  // expires_at 기반 유틸
  isAccessTokenExpired,
  isAccessTokenExpiringSoon,
  getAccessTokenTimeUntilExpiry,
  // JWT 파싱 유틸
  parseJwtToken,
  getJwtPayload,
  getJwtHeader,
  getJwtExpirationTime,
  isJwtTokenExpired,
  getJwtUserInfo,
};
