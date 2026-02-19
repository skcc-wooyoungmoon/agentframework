import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';

import { api } from '@/configs/axios.config';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { ErrorResponse } from '@/hooks/common/api/types';
import { useApiMutation, useApiQuery, type ApiQueryOptions } from '@/hooks/common/api/useApi';
import { useUser } from '@/stores/auth';
import { authUtils } from '@/utils/common';

import { authServices } from './auth.non.services';

import type {
  GetLoginRequest,
  GetLoginResponse,
  GetMeResponse,
  PostLogoutRequest,
  PostLogoutResponse,
  PostMolimateRegisterRequest,
  PostRefreshRequest,
  PostRefreshResponse,
  PostSwingSmsCheckRequest,
  PostSwingSmsRequest,
} from './types';

/**
 * @description 로그인
 */
export const usePostLogin = () => {
  const navigate = useNavigate();
  const { updateUser } = useUser();
  return useApiMutation<GetLoginResponse, GetLoginRequest>({
    method: 'POST',
    url: '/auth/login',
    onSuccess: async data => {
      authUtils.setTokens({
        access_token: data.data.access_token,
        refresh_token: data.data.refresh_token,
        expires_at: data.data.expires_at,
        axAccessToken: data.data.axAccessToken,
      });
      // 기본 그룹(public)으로 exchange
      await authServices.exchangeDefault();
      const user = await authServices.getMe();
      if (user) {
        updateUser(user);
        sessionStorage.setItem('USERNAME', user.userInfo.memberId);
      }
      // replace: true로 히스토리에 남기지 않고 이동하여 중복 리다이렉트 방지
      navigate('/home', { replace: true });
    },
    timeout: 3 * 60 * 1000,
  });
};

/**
 * @description 몰리메이트 로그인
 */
export const usePostMolimateLogin = () => {
  const { updateUser } = useUser();
  return useMutation<{ status: number }, ErrorResponse, PostMolimateRegisterRequest>({
    mutationKey: ['POST', '/auth/login-molimate', DONT_SHOW_LOADING_KEYS.LOGIN_PROCESS],
    mutationFn: async request => {
      const response = await api.post('/auth/login-molimate', request, {
        timeout: (3 * 60 + 5) * 1000, // 타임아웃 설정 (3분 + 5초)
      });
      const body = response.data;

      // 토큰이 포함된 응답(200 등)일 경우 토큰 저장 및 사용자 정보 갱신
      const tokens = body?.data ?? {};
      if (tokens?.access_token && tokens?.refresh_token && tokens?.expires_at) {
        authUtils.setTokens({
          access_token: tokens.access_token,
          refresh_token: tokens.refresh_token,
          expires_at: tokens.expires_at,
          axAccessToken: tokens.axAccessToken,
        });
        // 기본 그룹(public)으로 exchange
        await authServices.exchangeDefault();
        const user = await authServices.getMe();
        if (user) {
          updateUser(user);
          sessionStorage.setItem('USERNAME', user.userInfo.memberId);
        }
      }

      return { status: response.status };
    },
  });
};

/**
 * @description Swing 로그인
 */
export const usePostSwingLogin = () => {
  const { updateUser } = useUser();
  return useMutation<{ status: number }, ErrorResponse, PostMolimateRegisterRequest>({
    mutationKey: ['POST', '/auth/login-swing'],
    mutationFn: async request => {
      const response = await api.post('/auth/login-swing', request, {
        timeout: 3 * 60 * 1000, // 타임아웃 설정 (3분)
      });
      const body = response.data;

      // 토큰이 포함된 응답(200 등)일 경우 토큰 저장 및 사용자 정보 갱신
      const tokens = body?.data ?? {};
      if (tokens?.access_token && tokens?.refresh_token && tokens?.expires_at) {
        authUtils.setTokens({
          access_token: tokens.access_token,
          refresh_token: tokens.refresh_token,
          expires_at: tokens.expires_at,
          axAccessToken: tokens.axAccessToken,
        });
        // 기본 그룹(public)으로 exchange
        await authServices.exchangeDefault();
        const user = await authServices.getMe();
        if (user) {
          updateUser(user);
          sessionStorage.setItem('USERNAME', user.userInfo.memberId);
        }
      }

      return { status: response.status };
    },
  });
};

/**
 * @description Swing 로그인
 */
export const usePostSwingSms = () => {
  return useMutation<{ status: number, data: {
      authEventId: string,
      authRdnVdTm: string,
    } }, ErrorResponse, PostSwingSmsRequest>({
    mutationKey: ['POST', '/auth/swing-sms'],
    mutationFn: async request => {
      const response = await api.post('/auth/swing-sms', request);

      return { status: response.status, data: response.data.data };
    },
  });
};

/**
 * @description Swing 로그인
 */
export const usePostSwingSmsCheck = () => {
  const { updateUser } = useUser();
  return useMutation<{ status: number }, ErrorResponse, PostSwingSmsCheckRequest>({
    mutationKey: ['POST', '/auth/swing-sms-check'],
    mutationFn: async request => {
      const response = await api.post('/auth/swing-sms-check', request);
      const body = response.data;

      // 토큰이 포함된 응답(200 등)일 경우 토큰 저장 및 사용자 정보 갱신
      const tokens = body?.data ?? {};
      if (tokens?.access_token && tokens?.refresh_token && tokens?.expires_at) {
        authUtils.setTokens({
          access_token: tokens.access_token,
          refresh_token: tokens.refresh_token,
          expires_at: tokens.expires_at,
          axAccessToken: tokens.axAccessToken,
        });
        // 기본 그룹(public)으로 exchange
        await authServices.exchangeDefault();
        const user = await authServices.getMe();
        if (user) {
          updateUser(user);
          sessionStorage.setItem('USERNAME', user.userInfo.memberId);
        }
      }

      return { status: response.status };
    },
  });
};

/**
 * @description 로그아웃
 */
export const usePostLogout = () => {
  return useApiMutation<PostLogoutResponse, PostLogoutRequest>({
    method: 'POST',
    url: '/auth/logout',
    onSuccess: () => {
      authUtils.clearTokens();
    },
  });
};

/**
 * @description 토큰 갱신
 */
export const usePostRefresh = () => {
  return useApiMutation<PostRefreshResponse, PostRefreshRequest>({
    method: 'POST',
    url: '/auth/refresh',
    onSuccess: data => {
      authUtils.setTokens({
        access_token: data.data.access_token,
        refresh_token: data.data.refresh_token,
        expires_at: data.data.expires_at,
      });
      return data;
    },
    onError: /* error */ () => {
      // console.error('🎄 토큰 갱신 실패:', error);
      authUtils.clearTokens();
    },
  });
};

/**
 * @description 현재 사용자 정보 조회
 */
export const useGetMe = (options?: ApiQueryOptions<GetMeResponse>) => {
  return useApiQuery<GetMeResponse>({
    url: '/auth/users/me',
    ...options,
  });
};

/**
 * @description 그룹 교환 (프로젝트 선택)
 */
export const usePostExchangeGroup = () => {
  return useMutation<any, ErrorResponse, { prjSeq: string | number }>({
    mutationKey: ['POST', '/auth/users/exchange/group'],
    mutationFn: async ({ prjSeq }) => {
      // 내부 non-hook 서비스를 사용해 통신 일관성 유지
      return await authServices.exchangeGroup(prjSeq);
    },
  });
};
