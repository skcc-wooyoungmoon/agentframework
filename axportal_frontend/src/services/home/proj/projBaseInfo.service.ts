import { type ApiMutationOptions, type ApiQueryOptions, useApiQuery } from '@/hooks/common/api';
import { useApiMutation } from '@/hooks/common/api/useApi';

import type { CreateProjBaseInfoRequest, GetProjUserList, JoinProjInfo, ProjBaseInfo, ProjInfo, ProjJoinDetail, ProjUserInfo, PutProjInfo } from './types';
/**
 * @description FineTuning 생성
 */
export const useCreateProjBaseInfo = (options?: ApiMutationOptions<ProjBaseInfo, CreateProjBaseInfoRequest>) => {
  return useApiMutation<ProjBaseInfo, CreateProjBaseInfoRequest>({
    method: 'POST',
    url: '/home/project',
    ...options,
  });
};

export const usePutJoinProjInfo = (options?: ApiMutationOptions<JoinProjInfo, PutProjInfo>) => {
  return useApiMutation<JoinProjInfo, PutProjInfo>({
    method: 'POST',
    url: '/home/project/join',
    ...options,
  });
};

export const usePutQuitProjInfo = (options?: ApiMutationOptions<string, PutProjInfo>) => {
  return useApiMutation<string, PutProjInfo>({
    method: 'POST',
    url: '/home/project/quit',
    ...options,
  });
};

export const useDeleteProj = (options?: ApiMutationOptions<any, { prjSeq: number }>) => {
  return useApiMutation<any, { prjSeq: number }>({
    method: 'DELETE',
    url: '/home/project/{prjSeq}',
    ...options,
  });
};

export const useGetJoinPrivateProjList = (options?: ApiQueryOptions<ProjInfo>) => {
  const username = sessionStorage.getItem('USERNAME') || '';
  const mergedOptions: ApiQueryOptions<ProjInfo> = {
    staleTime: 0 as any,
    refetchOnMount: true as any,
    ...options,
  };
  return useApiQuery<ProjInfo>({
    url: '/home/project/join-private-proj-list',
    params: {
      username,
      ...(options as any)?.params,
    },
    ...mergedOptions,
    disableCache: true,
  });
};

export const useGetNotJoinPrivateProjList = (params?: GetProjUserList, options?: ApiQueryOptions<ProjInfo>) => {
  const mergedOptions: ApiQueryOptions<ProjInfo> = {
    staleTime: 0 as any,
    refetchOnMount: true as any,
    ...options,
  };
  return useApiQuery<ProjInfo>({
    url: '/home/project/not-join-proj-list',
    params,
    ...mergedOptions,
  });
};

export const useGetNotJoinPrivateProjDetail = (projectId: string, options?: ApiQueryOptions<ProjJoinDetail>) => {
  const mergedOptions: ApiQueryOptions<ProjJoinDetail> = {
    staleTime: 0 as any,
    refetchOnMount: true as any,
    ...options,
  };
  return useApiQuery<ProjJoinDetail>({
    url: '/home/project/not-join-proj-detail',
    params: {
      projectId,
      ...(options as any)?.params,
    },
    ...mergedOptions,
  });
};

export const useGetProjUserGetMe = (memberId: string, options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    url: `/home/project/${memberId}/getme`,
    ...options,
  });
};

export const useGetProjUserList = (params?: GetProjUserList, options?: ApiQueryOptions<ProjUserInfo>) => {
  const mergedOptions: ApiQueryOptions<ProjUserInfo> = {
    staleTime: 0 as any,
    refetchOnMount: true as any,
    ...options,
  };
  return useApiQuery<ProjUserInfo>({
    url: '/home/project/join-user-list',
    params,
    ...mergedOptions,
  });
};

export const useGetProjectList = (options?: ApiQueryOptions<ProjInfo[]>) => {
  const username = sessionStorage.getItem('USERNAME') || '';
  return useApiQuery<ProjInfo[]>({
    url: '/home/project/join-proj-list',
    params: {
      username: username,
    },
    ...options,
  });
};
