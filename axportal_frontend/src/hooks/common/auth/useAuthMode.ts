import { NO_SHOW_RUN_MODE } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { useMemo } from 'react';

export const useAuthMode = () => {
  /**
   * @description 숨김 모드 처리 여부
   */
  const isVisibleMode = useMemo(() => !NO_SHOW_RUN_MODE.includes(env.VITE_RUN_MODE), [env.VITE_RUN_MODE]);
  return { isVisibleMode };
};
