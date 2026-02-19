import { useMutation } from '@tanstack/react-query';

import { api } from '@/configs/axios.config';
import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type * as T from './types';

export const useGetWorkFlowTags = (params: T.GetWorkFlowTagsRequest, options?: ApiQueryOptions<T.GetWorkFlowTagsResponse>) =>
  useApiQuery<T.GetWorkFlowTagsResponse>({
    queryKey: ['workflows', 'tags'],
    url: '/workflow/tags',
    params,
    ...options,
  });

export const useGetWorkFlowList = (params: T.GetWorkFlowsRequest, options?: ApiQueryOptions<PaginatedDataType<T.GetWorkFlowResponse>>) =>
  useApiQuery<PaginatedDataType<T.GetWorkFlowResponse>>({
    queryKey: ['workflows', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params || {})],
    url: '/workflow',
    params,
    ...options,
    refetchOnMount: 'always',
  });

/*
WorkFlow ID 기반 조회 영역
*/
export const useGetWorkFlowVerListById = (params: T.GetWorkFlowByIdRequest, options?: ApiQueryOptions<T.GetWorkFlowVersionDataResponse>) =>
  useApiQuery<T.GetWorkFlowVersionDataResponse>({
    queryKey: ['workflows', 'versions', params.workFlowId],
    url: `/workflow/versions/${params.workFlowId ?? ''}`,
    ...options,
  });

/*
Workflow ID 기반 조회 영역
*/
export const useGetWorkFlowLatestVerById = (params: T.GetWorkFlowByIdRequest, options?: ApiQueryOptions<T.GetWorkFlowLatestVersionResponse>) =>
  useApiQuery<T.GetWorkFlowLatestVersionResponse>({
    queryKey: ['workflows', 'versions', 'latest', params.workFlowId],
    url: `/workflow/versions/${params.workFlowId ?? ''}/latest`,
    ...options,
  });

/*
워크플로우 특정 버전 조회
*/
export const useGetWorkFlowVerById = (params: T.GetWorkFlowVerByIdRequest, options?: ApiQueryOptions<T.GetWorkFlowLatestVersionResponse>) =>
  useApiQuery<T.GetWorkFlowLatestVersionResponse>({
    queryKey: ['workflows', 'versions', 'specific', params.workFlowId, String(params.versionNo)],
    url: `/workflow/versions/${params.workFlowId ?? ''}/${String(params.versionNo ?? 0)}`,
    ...options,
  });

// 워크플로우 삭제 (새로운 API)
export const useDeleteWorkFlowByWorkFlowId = (options?: ApiMutationOptions<string, T.DeleteWorkFlowRequest>) => {
  return useApiMutation<string, T.DeleteWorkFlowRequest>({
    method: 'DELETE',
    url: '/workflow/{workFlowId}', // URL 템플릿: {workFlowId}가 request.workFlowId로 치환됨
    ...options,
  });
};

// 워크플로우 삭제 (기존 API 호환성 - deprecated, 추후 제거 예정)
export const useDeleteWorkFlowByWorkflowId = (options?: ApiMutationOptions<string, { workFlowId: string }>) => {
  return useApiMutation<string, { workFlowId: string }>({
    method: 'DELETE',
    url: '/workflow/{workFlowId}',
    ...options,
  });
};

// 워크플로우 일괄 삭제
export const useDeleteWorkFlowsByIds = (options?: ApiMutationOptions<T.DeleteWorkFlowsResponse, T.DeleteWorkFlowsRequest>) => {
  return useApiMutation<T.DeleteWorkFlowsResponse, T.DeleteWorkFlowsRequest>({
    method: 'POST',
    url: '/workflow/batch/delete',
    ...options,
  });
};

// 워크플로우 생성
export const useCreateWorkFlow = (options?: ApiMutationOptions<string, T.CreateWorkFlowRequest>) => {
  return useApiMutation<string, T.CreateWorkFlowRequest>({
    method: 'POST',
    url: '/workflow',
    ...options,
  });
};

// 워크플로우 레지스트리 생성 (multipart/form-data)
export const useCreateWorkFlowRegistry = (options?: Parameters<typeof useMutation<T.CreateWorkFlowRegistryResponse, unknown, T.CreateWorkFlowRegistryFormData>>[0]) => {
  return useMutation<T.CreateWorkFlowRegistryResponse, unknown, T.CreateWorkFlowRegistryFormData>({
    mutationFn: async (data: T.CreateWorkFlowRegistryFormData) => {
      const formData = new FormData();

      // form 데이터를 JSON 문자열로 변환하여 추가
      formData.append(
        'form',
        new Blob([JSON.stringify(data.form)], {
          type: 'application/json',
        })
      );

      // 파일이 있는 경우 추가
      if (data.xmlFile) {
        formData.append('xmlFile', data.xmlFile);
      }

      const response = await api.post('/workflow', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      // AxResponseEntity 구조에 맞게 데이터 추출
      return response.data.data;
    },
    ...options,
  });
};

// 워크플로우 수정 (multipart/form-data)
export const useUpdateWorkFlow = (options?: Parameters<typeof useMutation<string, unknown, T.UpdateWorkFlowFormData>>[0]) => {
  return useMutation<string, unknown, T.UpdateWorkFlowFormData>({
    mutationFn: async (data: T.UpdateWorkFlowFormData) => {
      const formData = new FormData();

      // form 데이터를 JSON 문자열로 변환하여 추가
      formData.append(
        'form',
        new Blob([JSON.stringify(data.form)], {
          type: 'application/json',
        })
      );

      // 파일이 있는 경우 추가
      if (data.xmlFile) {
        formData.append('xmlFile', data.xmlFile);
      }

      const response = await api.put(`/workflow/${data.form.workFlowId}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      // AxResponseEntity 구조에 맞게 데이터 추출
      return response.data.data;
    },
    ...options,
  });
};

// 워크플로우 공개설정
export const useSetWorkFlowPublic = (options?: ApiMutationOptions<string, T.SetWorkFlowPublicRequest>) => {
  return useApiMutation<string, T.SetWorkFlowPublicRequest>({
    method: 'POST',
    url: '/workflow/policy/{workFlowId}/public',
    ...options,
  });
};
