import { useApiMutation, useApiQuery, type ApiMutationOptions, type ApiQueryOptions } from '@/hooks/common/api';

import type { ProjectInfoResponseResponse, UpdateProjectToPublicRequest } from './types';

export const useUpdateProjectToPublic = (options?: ApiMutationOptions<void, UpdateProjectToPublicRequest>) => {
  return useApiMutation<void, UpdateProjectToPublicRequest>({
    method: 'PUT',
    url: `/common/project-info/public`,
    ...options,
    timeout: 120_000,
  });
};

export const useGetProjectInfo = (params: { assetUuid: string }, options?: ApiQueryOptions<ProjectInfoResponseResponse>) => {
  return useApiQuery<ProjectInfoResponseResponse>({
    queryKey: ['project-info', params.assetUuid],
    url: `/common/project-info/asset/${params.assetUuid}`,
    ...options,
    disableCache: true,
  });
};
