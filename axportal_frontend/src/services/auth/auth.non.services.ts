import { api } from '@/configs/axios.config';
import { authUtils } from '@/utils/common';

/**
 * @description 인증 서비스 hook 처리 X
 */
export const authServices = {
  logout: async () => {
    const response = await api.post('/auth/logout', {
      headers: {
        Authorization: `Bearer ${authUtils.getAccessToken()}`,
      },
    });
    authUtils.clearTokens();
    return response.data;
  },
  refresh: async () => {
    const response = await api.post('/auth/refresh', {
      refreshToken: authUtils.getRefreshToken(),
    });
    if (response.status === 200) {
      // 성공시 토큰 갱신
      authUtils.setTokens({
        access_token: response.data.data.access_token,
        refresh_token: response.data.data.refresh_token,
        expires_at: response.data.data.expires_at,
      });
    } else {
      authUtils.clearTokens();
      window.location.href = '/login';
    }
    return response.data;
  },
  getMe: async () => {
    const response = await api.get('/auth/users/me', {
      headers: {
        Authorization: `Bearer ${authUtils.getAccessToken()}`,
      },
    });
    return response.data.data;
  },
  exchangeDefault: async () => {
    const response = await api.post('/auth/users/exchange/default', {
      headers: {
        Authorization: `Bearer ${authUtils.getAccessToken()}`,
      },
    });
    return response.data;
  },
  exchangeGroup: async (prjSeq: string | number) => {
    const response = await api.post('/auth/users/exchange/group', undefined, {
      params: { prjSeq },
      headers: {
        Authorization: `Bearer ${authUtils.getAccessToken()}`,
      },
    });
    return response.data;
  },
};
