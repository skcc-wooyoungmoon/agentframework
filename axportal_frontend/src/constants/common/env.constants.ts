// 환경변수 접근을 위한 타입 안전한 래퍼
export const env = {
  VITE_APP_NAME: import.meta.env.VITE_APP_NAME || '생성형 AI 플랫폼',
  VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL || '',
  VITE_API_TIMEOUT: parseInt(import.meta.env.VITE_API_TIMEOUT) || 30000,
  VITE_RUN_MODE: import.meta.env.VITE_RUN_MODE || '',
  VITE_DATUMO_URL: import.meta.env.VITE_DATUMO_URL || '',
  VITE_GATEWAY_URL: import.meta.env.VITE_GATEWAY_URL || '',
  VITE_PHOENIX_BASE_URL: import.meta.env.VITE_PHOENIX_BASE_URL || '',
  VITE_API_KEY: import.meta.env.VITE_API_KEY || '',
  VITE_NO_PRESSURE_MODE: import.meta.env.VITE_NO_PRESSURE_MODE === 'true' || import.meta.env.VITE_NO_PRESSURE_MODE === true,
} as const;

export const RUN_MODE_TYPES = {
  PROD: 'PROD',
  DEV: 'DEV',
  LOCAL: 'LOCAL',
  E_LOCAL: 'E-LOCAL',
  E_DEV: 'E-DEV',
} as const;
