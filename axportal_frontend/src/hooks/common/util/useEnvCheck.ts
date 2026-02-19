import { RUN_MODE_TYPES } from '@/constants/common/env.constants';

import { env } from '@/constants/common/env.constants';

export const useEnvCheck = () => {
  const isProd = env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD;
  const isDev = env.VITE_RUN_MODE === RUN_MODE_TYPES.DEV;
  const isLocal = env.VITE_RUN_MODE === RUN_MODE_TYPES.LOCAL;
  const isELocal = env.VITE_RUN_MODE === RUN_MODE_TYPES.E_LOCAL;
  const isEDev = env.VITE_RUN_MODE === RUN_MODE_TYPES.E_DEV;
  return { isProd, isDev, isLocal, isELocal, isEDev };
};
