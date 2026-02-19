import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import type {
  CreateProjectRoleRequest,
  CreateProjectRoleResponse,
  DeleteProjectRolesRequest,
  DeleteProjectRolesResponse,
  GetAvailableUsersRequest as GetAvailableUsersRequest,
  GetAvailableUsersResponse as GetAvailableUsersResponse,
  GetMenuPermitsRequest,
  GetPermitDetailsRequest,
  GetProjectByIdResponse,
  GetProjectRoleAuthoritiesRequest,
  GetProjectRoleAuthoritiesResponse,
  GetProjectRoleByIdRequest,
  GetProjectRoleByIdResponse,
  GetProjectRolesRequest,
  GetProjectRolesResponse,
  GetProjectRoleUsersRequest,
  GetProjectRoleUsersResponse,
  GetProjectsRequest,
  GetProjectsResponse,
  GetProjectUsersRequest,
  GetProjectUsersResponse,
  MenuPermitRes,
  PermitDetailRes,
  ProjectUserAssignRequest as ProjectUserAssignRequest,
  ProjectUserAssignResponse as ProjectUserAssignResponse,
  ProjectUserDeleteRequest,
  ProjectUserDeleteResponse,
  ProjectUserRoleChangeRequest as ProjectUserRoleChangeRequest,
  ProjectUserRoleChangeResponse as ProjectUserRoleChangeResponse,
  UpdateProjectRequest,
  UpdateProjectResponse,
  UpdateProjectRoleAuthoritiesRequest,
  UpdateProjectRoleRequest,
  UpdateProjectRoleResponse,
} from './projMgmt.types';

// ================================
// 1. 프로젝트
// ================================

/**
 * 프로젝트 전체 목록 조회
 */
export const useGetProjects = (params?: GetProjectsRequest, options?: ApiQueryOptions<PaginatedDataType<GetProjectsResponse>>) => {
  return useApiQuery<PaginatedDataType<GetProjectsResponse>>({
    queryKey: ['admin-projects-list', JSON.stringify(params)],
    url: '/admin/projects',
    params,
    staleTime: 0,
    refetchOnMount: 'always',
    ...options,
  });
};

/**
 * 프로젝트 상세 조회
 */
export const useGetProjectById = (projectId: string, options?: ApiQueryOptions<GetProjectByIdResponse>) => {
  return useApiQuery<GetProjectByIdResponse>({
    queryKey: ['admin-project-detail', projectId],
    url: `/admin/projects/${projectId}`,
    ...options,
  });
};

/**
 * 프로젝트 수정
 */
export const useUpdateProject = (projectId: string, options?: ApiMutationOptions<UpdateProjectResponse, UpdateProjectRequest>) => {
  return useApiMutation<UpdateProjectResponse, UpdateProjectRequest>({
    method: 'PUT',
    url: `/admin/projects/${projectId}`,
    ...options,
  });
};

/**
 * 프로젝트 종료
 */
export const useDeleteProject = (projectId: string, options?: ApiMutationOptions<void, void>) => {
  return useApiMutation<void, void>({
    method: 'DELETE',
    url: `/admin/projects/${projectId}`,
    ...options,
  });
};

// ================================
// 2. 프로젝트 - 역할
// ================================

/**
 * 프로젝트내 역할 목록 조회
 */
export const useGetProjectRoles = (projectId: string, params?: GetProjectRolesRequest, options?: ApiQueryOptions<PaginatedDataType<GetProjectRolesResponse>>) => {
  return useApiQuery<PaginatedDataType<GetProjectRolesResponse>>({
    queryKey: ['admin-project-roles', projectId, JSON.stringify(params)],
    url: `/admin/projects/${projectId}/roles`,
    params,
    ...options,
  });
};

/**
 * 프로젝트 역할 생성
 */
export const useCreateProjectRole = (projectId: string, options?: ApiMutationOptions<CreateProjectRoleResponse, CreateProjectRoleRequest>) => {
  return useApiMutation<CreateProjectRoleResponse, CreateProjectRoleRequest>({
    method: 'POST',
    url: `/admin/projects/${projectId}/roles`,
    ...options,
  });
};

/**
 * 프로젝트 역할 삭제
 */
export const useDeleteProjectRoles = (projectId: string, options?: ApiMutationOptions<DeleteProjectRolesResponse, DeleteProjectRolesRequest>) => {
  return useApiMutation<DeleteProjectRolesResponse, DeleteProjectRolesRequest>({
    method: 'DELETE',
    url: `/admin/projects/${projectId}/roles`,
    ...options,
  });
};

/**
 * 프로젝트 역할 상세 조회
 */
export const useGetProjectRoleById = ({ projectId, roleId }: GetProjectRoleByIdRequest, options?: ApiQueryOptions<GetProjectRoleByIdResponse>) => {
  return useApiQuery<GetProjectRoleByIdResponse>({
    queryKey: ['admin-project-role-detail', projectId, roleId],
    url: `/admin/projects/${projectId}/roles/${roleId}`,
    ...options,
  });
};

/**
 * 프로젝트 역할 수정
 */
export const useUpdateProjectRole = (projectId: string, roleId: string, options?: ApiMutationOptions<UpdateProjectRoleResponse, UpdateProjectRoleRequest>) => {
  return useApiMutation<UpdateProjectRoleResponse, UpdateProjectRoleRequest>({
    method: 'PUT',
    url: `/admin/projects/${projectId}/roles/${roleId}`,
    ...options,
  });
};

// ================================
// 2-1. 프로젝트 - 역할 - 권한
// ================================

/**
 * 프로젝트 역할 권한 목록 조회
 */
export const useGetProjectRoleAuthorities = (
  projectId: string,
  roleId: string,
  params?: GetProjectRoleAuthoritiesRequest,
  options?: ApiQueryOptions<PaginatedDataType<GetProjectRoleAuthoritiesResponse>>
) => {
  return useApiQuery<PaginatedDataType<GetProjectRoleAuthoritiesResponse>>({
    queryKey: ['admin-project-role-authorities', projectId, roleId, JSON.stringify(params)],
    url: `/admin/projects/${projectId}/roles/${roleId}/authorities`,
    params,
    disableCache: true,
    ...options,
  });
};

/**
 * 프로젝트 역할 권한 수정
 */
export const useUpdateProjectRoleAuthorities = (projectId: string, roleId: string, options?: ApiMutationOptions<void, UpdateProjectRoleAuthoritiesRequest>) => {
  return useApiMutation<void, UpdateProjectRoleAuthoritiesRequest>({
    method: 'PUT',
    url: `/admin/projects/${projectId}/roles/${roleId}/authorities`,
    ...options,
  });
};

// ================================
// 2-2. 프로젝트 - 역할 - 구성원
// ================================

/**
 * 프로젝트 역할에 배정된 구성원 목록 조회
 */
export const useGetProjectRoleUsers = (
  projectId: string,
  roleId: string,
  params?: GetProjectRoleUsersRequest,
  options?: ApiQueryOptions<PaginatedDataType<GetProjectRoleUsersResponse>>
) => {
  return useApiQuery<PaginatedDataType<GetProjectRoleUsersResponse>>({
    queryKey: ['admin-project-role-users', projectId, roleId, JSON.stringify(params)],
    url: `/admin/projects/${projectId}/roles/${roleId}/users`,
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * 프로젝트 구성원 역할 변경
 */
export const useUpdateProjectUserRoles = (projectId: string, options?: ApiMutationOptions<ProjectUserRoleChangeResponse, ProjectUserRoleChangeRequest>) => {
  return useApiMutation<ProjectUserRoleChangeResponse, ProjectUserRoleChangeRequest>({
    method: 'PUT',
    url: `/admin/projects/${projectId}/user-role-mappings`,
    ...options,
  });
};

// ================================
// 3. 프로젝트 - 구성원
// ================================

/**
 * 프로젝트 구성원 목록 조회
 */
export const useGetProjectUsers = (projectId: string, params?: GetProjectUsersRequest, options?: ApiQueryOptions<PaginatedDataType<GetProjectUsersResponse>>) => {
  return useApiQuery<PaginatedDataType<GetProjectUsersResponse>>({
    queryKey: ['admin-project-users', projectId, JSON.stringify(params)],
    url: `/admin/projects/${projectId}/users`,
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * 프로젝트 구성원 삭제
 */
export const useDeleteProjectUsers = (projectId: string, options?: ApiMutationOptions<ProjectUserDeleteResponse, ProjectUserDeleteRequest>) => {
  return useApiMutation<ProjectUserDeleteResponse, ProjectUserDeleteRequest>({
    method: 'DELETE',
    url: `/admin/projects/${projectId}/users`,
    ...options,
  });
};

/**
 * 구성원 초대하기 - 사용자 목록 (프로젝트 내에 참여하지 않은 사용자)
 */
export const useGetAvailableUsersForProject = (projectId: string, params?: GetAvailableUsersRequest, options?: ApiQueryOptions<PaginatedDataType<GetAvailableUsersResponse>>) => {
  return useApiQuery<PaginatedDataType<GetAvailableUsersResponse>>({
    queryKey: ['admin-available-users-for-project', projectId, JSON.stringify(params)],
    url: `/admin/projects/${projectId}/user-available`,
    params,
    ...options,
  });
};

/**
 * 프로젝트 구성원 역할 할당
 */
export const useAssignProjectUsers = (projectId: string, options?: ApiMutationOptions<ProjectUserAssignResponse, ProjectUserAssignRequest>) => {
  return useApiMutation<ProjectUserAssignResponse, ProjectUserAssignRequest>({
    method: 'POST',
    url: `/admin/projects/${projectId}/user-role-mappings`,
    ...options,
  });
};

// ================================
// 4. 권한
// ================================

/**
 * 메뉴 진입 설정을 위한 권한(메뉴) 목록 조회
 */
export const useGetMenuPermits = (params?: GetMenuPermitsRequest, options?: ApiQueryOptions<PaginatedDataType<MenuPermitRes>>) => {
  return useApiQuery<PaginatedDataType<MenuPermitRes>>({
    queryKey: ['admin-menu-permits', JSON.stringify(params)],
    url: '/admin/permit/menus',
    params,
    ...options,
  });
};

/**
 * 상세 권한 목록 조회
 */
export const useGetPermitDetails = (params?: GetPermitDetailsRequest, options?: ApiQueryOptions<PaginatedDataType<PermitDetailRes>>) => {
  return useApiQuery<PaginatedDataType<PermitDetailRes>>({
    queryKey: ['admin-permit-details', JSON.stringify(params)],
    url: '/admin/permit/details',
    params,
    ...options,
  });
};
