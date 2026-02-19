import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import type {
  GetAssignableRolesRequest,
  GetAssignableRolesResponse,
  GetUserByIdRequest,
  GetUserByIdResponse,
  GetUserProjectDetailRequest,
  GetUserProjectDetailResponse,
  GetUserProjectsRequest,
  GetUserProjectsResponse,
  GetUsersRequest,
  GetUsersResponse,
  UpdateUserProjectRoleRequest,
} from './userMgmt.types';

/**
 * 사용자 목록 조회 (페이지네이션) - V2
 */
export const useGetUsers = (params?: GetUsersRequest, options?: ApiQueryOptions<PaginatedDataType<GetUsersResponse>>) => {
  return useApiQuery<PaginatedDataType<GetUsersResponse>>({
    queryKey: ['admin-users-list', JSON.stringify(params)],
    url: '/admin/users',
    params,
    disableCache: true,
    ...options,
  });
};

/**
 * 사용자 상세 조회 - V2
 */
export const useGetUserById = ({ userId }: GetUserByIdRequest, options?: ApiQueryOptions<GetUserByIdResponse>) => {
  return useApiQuery<GetUserByIdResponse>({
    queryKey: ['admin-users', userId],
    url: `/admin/users/${userId}`,
    disableCache: true,
    ...options,
  });
};

/**
 * 사용자가 참여한 프로젝트 조회 - V2
 */
export const useGetUserProjects = ({ userId }: GetUserByIdRequest, params: GetUserProjectsRequest, options?: ApiQueryOptions<PaginatedDataType<GetUserProjectsResponse>>) => {
  return useApiQuery<PaginatedDataType<GetUserProjectsResponse>>({
    queryKey: ['admin-user-projects', userId, JSON.stringify(params)],
    url: `/admin/users/${userId}/projects`,
    params,
    ...options,
  });
};

/**
 * 사용자가 참여한 프로젝트의 상세 정보 및 역할 조회 - V2
 */
export const useGetUserProjectDetail = ({ userId, projectId }: GetUserProjectDetailRequest, options?: ApiQueryOptions<GetUserProjectDetailResponse>) => {
  return useApiQuery<GetUserProjectDetailResponse>({
    queryKey: ['admin-user-project-detail', userId, projectId],
    url: `/admin/users/${userId}/projects/${projectId}`,
    ...options,
  });
};

/**
 * 사용자가 참여한 프로젝트의 할당 가능한 역할 목록 조회 - V2
 */
export const useGetAssignableProjectRoles = (projectId: string, params?: GetAssignableRolesRequest, options?: ApiQueryOptions<PaginatedDataType<GetAssignableRolesResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAssignableRolesResponse>>({
    queryKey: ['admin-assignable-roles', projectId, JSON.stringify(params)],
    url: `/admin/users/projects/${projectId}/role`,
    params,
    staleTime: 0,
    ...options,
  });
};
/**
 * 사용자가 참여한 프로젝트내 역할 수정 - V2
 */
export const useUpdateUserProjectRole = (userId: string, projectId: string, options?: ApiMutationOptions<string, UpdateUserProjectRoleRequest>) => {
  return useApiMutation<string, UpdateUserProjectRoleRequest>({
    method: 'PUT',
    url: `/admin/users/${userId}/projects/${projectId}/role`,
    ...options,
  });
};

/**
 * 사용자 계정 상태 ACTIVE 로 변경 - V2
 */
export const useUpdateUserStatus = (userId: string, options?: ApiMutationOptions<void, void>) => {
  return useApiMutation<void, void>({
    method: 'PUT',
    url: `/admin/users/${userId}/active`,
    ...options,
  });
};
