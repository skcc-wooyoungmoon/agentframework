export {}; // 이 파일을 모듈로 만듦

declare global {
  interface Window {
    APP_CONFIG?: {
      VITE_APP_NAME: string;
      VITE_API_BASE_URL: string;
      VITE_API_TIMEOUT: number;
      VITE_RUN_MODE: string;
      VITE_DATUMO_URL: string;
      VITE_GATEWAY_URL: string;
    };
  }
}
