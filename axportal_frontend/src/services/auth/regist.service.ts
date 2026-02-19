import type { ApiMutationOptions } from '@/hooks/common/api';
import { useApiMutation  } from '@/hooks/common/api/useApi';
import type { PostRegisterRequest, PostRegisterResponse } from './types';

export const usePostRegistrInfo = (
  options?: ApiMutationOptions<PostRegisterResponse, PostRegisterRequest>
) => {
  return useApiMutation<PostRegisterResponse, PostRegisterRequest>({
    method: 'POST',
    url: '/auth/register',
    ...options,
  });
};