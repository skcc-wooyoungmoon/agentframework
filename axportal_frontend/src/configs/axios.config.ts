import axios, { AxiosError } from 'axios';

import { env } from '@/constants/common/env.constants';
import type { ErrorResponse } from '@/hooks/common/api/types';
import { authServices } from '@/services/auth/auth.non.services';
import { authUtils } from '@/utils/common';

// API 에러를 표준화하는 함수
const normalizeError = (error: AxiosError): ErrorResponse => {
  // console.log('🎄 에러 응답', error);

  // 타임아웃 에러
  if (error.code === 'ECONNABORTED') {
    return {
      success: false,
      message: '요청 시간이 초과되었습니다.',
      error: {
        code: 'ECONNABORTED',
        details: '',
        hscode: 'timeout',
        message: '요청 시간이 초과되었습니다.',
      },
      timestamp: new Date().toISOString(),
      path: error.config?.url || '',
    };
  }

  // 네트워크 에러 (연결 거부, 네트워크 오류 등) << TODO 확인 필요
  // error.response가 없는 경우는 네트워크 레벨 에러
  if (!error.response) {
    const isConnectionRefused = error.code === 'ERR_CONNECTION_REFUSED' || error.message?.includes('ERR_CONNECTION_REFUSED');
    const isNetworkError = error.code === 'ERR_NETWORK' || error.message === 'Network Error';

    return {
      success: false,
      message: isConnectionRefused
        ? '서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.'
        : isNetworkError
          ? '네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.'
          : '네트워크 오류가 발생했습니다.',
      error: {
        code: error.code || 'NETWORK_ERROR',
        details: error.message || '',
        hscode: isConnectionRefused ? 'connection_refused' : 'network_error',
        message: isConnectionRefused
          ? '서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.'
          : isNetworkError
            ? '네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.'
            : '네트워크 오류가 발생했습니다.',
      },
      timestamp: new Date().toISOString(),
      path: error.config?.url || '',
    };
  }

  // 서버 응답이 있는 경우 (4xx, 5xx 등)
  return error.response?.data as ErrorResponse;
};

// API 인스턴스 생성
export const api = axios.create({
  baseURL: env.VITE_API_BASE_URL,
  timeout: env.VITE_API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
api.interceptors.request.use(
  config => {
    // 트랜잭션 요청(POST, PUT, DELETE, GET)일 경우 이벤트 발생
    if (['post', 'put', 'delete', 'get'].includes(config.method?.toLowerCase() || '')) {
      window.dispatchEvent(
        new CustomEvent('api-transaction-start', {
          detail: config, // config 객체 전체를 전달
        })
      );
    }
    const token = authUtils.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // FormData인 경우 Content-Type 헤더 제거 (브라우저가 자동으로 설정)
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }

    // ⭐ 현재 프론트엔드 경로를 헤더로 전송
    config.headers['X-Frontend-Path'] = window.location.pathname;

    return config;
  },
  error => Promise.reject(error)
);

// 응답 인터셉터
api.interceptors.response.use(
  response => {
    // HTTP status가 200이지만 응답 body에 success: false가 있는 경우 에러로 처리
    if (response.data && typeof response.data === 'object' && response.data.success === false) {
      // AxiosError 형식으로 변환하여 error handler로 전달
      const errorResponse: ErrorResponse = response.data as ErrorResponse;
      const error = new Error(errorResponse.message || 'Request failed') as AxiosError<ErrorResponse>;
      (error as any).response = {
        data: errorResponse,
        status: response.status,
        statusText: response.statusText,
        headers: response.headers,
        config: response.config,
      };
      (error as any).isAxiosError = true;
      (error as any).config = response.config;
      return Promise.reject(error);
    }

    // 트랜잭션 성공(POST, PUT, DELETE, GET)일 경우 이벤트 발생
    if (['post', 'put', 'delete', 'get'].includes(response.config.method?.toLowerCase() || '')) {
      window.dispatchEvent(
        new CustomEvent('api-transaction-success', {
          detail: {
            data: response.data,
            config: response.config,
          },
        })
      );
    }

    return response;
  },
  async error => {
    // 로그인 요청에서의 401은 리다이렉트하지 않고 그대로 페이지에서 처리하도록 전달
    if (error.response?.status === 401 && (error.config?.url || '').includes('/auth/login')) {
      const normalizedError = normalizeError(error);
      normalizedError.error.details = '';

      window.dispatchEvent(
        new CustomEvent('api-error', {
          detail: normalizedError.error ? normalizedError.error : error.message,
        })
      );
      return Promise.reject(normalizedError);
    }

    // 401 에러: 토큰 갱신 시도 후 실패 시 로그아웃
    // 403 에러: 권한 부족, 로그인 페이지로 리다이렉트
    if (error.response?.status === 401 && error.response?.data?.error?.hscode === 'AUTHENTICATION_FAILED') {
      try {
        // console.log('🎄 토큰 갱신 시도');
        // refresh API에 token이 필요하다면 별도 axios 인스턴스 생성 필요
        await authServices.refresh();

        // 원래 요청 재시도
        const originalRequest = error.config;
        const newToken = authUtils.getAccessToken();
        originalRequest.headers.Authorization = `Bearer ${newToken}`;

        return api.request(originalRequest);
      } catch {
        // 갱신 실패 시 로그아웃
        // console.log('🎄 토큰 갱신 실패', refreshError);
        authUtils.clearTokens();
        window.location.href = '/login';
      }
      return;
    }
    // 에러 발생 시 에러 이벤트 발생
    const normalizedError = normalizeError(error);
    window.dispatchEvent(
      new CustomEvent('api-error', {
        detail: normalizedError.error ? normalizedError.error : error.message,
      })
    );
    return Promise.reject(normalizedError);
  }
);
