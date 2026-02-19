import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants.ts';
import type {
  CreateGuardRailPromptRequest,
  CreateGuardRailPromptResponse,
  CreateGuardRailRequest,
  CreateGuardRailResponse,
  DeleteGuardRailPromptRequest,
  DeleteGuardRailRequest,
  DeleteGuardRailResponse,
  GetGuardRailByIdResponse,
  GetGuardRailListRequest,
  GetGuardRailListResponse,
  GetGuardRailPromptByIdRequest,
  GetGuardRailPromptByIdResponse,
  GetGuardRailPromptListRequest,
  GetGuardRailPromptListResponse,
  UpdateGuardRailPromptRequest,
  UpdateGuardRailPromptResponse,
  UpdateGuardRailRequest,
  UpdateGuardRailResponse,
} from './types';

// 가드레일 프롬프트 목록 조회
export const useGetGuardRailPromptList = (params?: GetGuardRailPromptListRequest, options?: ApiQueryOptions<PaginatedDataType<GetGuardRailPromptListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetGuardRailPromptListResponse>>({
    queryKey: ['guardRail', 'prompts', DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/guardrails/prompts',
    params,
    ...options,
    disableCache: true,
  });
};

// 가드레일 프롬프트 상세 조회
export const useGetGuardRailPromptById = ({ id }: GetGuardRailPromptByIdRequest, options?: ApiQueryOptions<GetGuardRailPromptByIdResponse>) => {
  return useApiQuery<GetGuardRailPromptByIdResponse>({
    queryKey: ['guardRail', 'prompts', id],
    url: `/guardrails/prompts/${id}`,
    ...options,
  });
};

// 가드레일 프롬프트 생성
export const useCreateGuardRailPrompt = (options?: ApiMutationOptions<CreateGuardRailPromptResponse, CreateGuardRailPromptRequest>) => {
  return useApiMutation<CreateGuardRailPromptResponse, CreateGuardRailPromptRequest>({
    mutationKey: ['guardRail', 'prompts', 'create'],
    url: '/guardrails/prompts',
    method: 'POST',
    ...options,
  });
};

// 가드레일 프롬프트 수정
export const useUpdateGuardRailPrompt = (options?: ApiMutationOptions<UpdateGuardRailPromptResponse, UpdateGuardRailPromptRequest>) => {
  return useApiMutation<UpdateGuardRailPromptResponse, UpdateGuardRailPromptRequest>({
    mutationKey: ['guardRail', 'prompts', 'update'],
    url: '/guardrails/prompts/{id}',
    method: 'PUT',
    ...options,
  });
};

// 가드레일 프롬프트 삭제
export const useDeleteGuardRailPrompt = (options?: ApiMutationOptions<void, DeleteGuardRailPromptRequest>) => {
  return useApiMutation<void, DeleteGuardRailPromptRequest>({
    mutationKey: ['guardRail', 'prompts', 'delete'],
    url: '/guardrails/prompts/{id}',
    method: 'DELETE',
    ...options,
  });
};

// 가드레일 프롬프트 태그 목록 조회
export const useGetGuardRailPromptTags = (options?: ApiQueryOptions<string[]>) => {
  return useApiQuery<string[]>({
    queryKey: ['guardRail', 'prompts', 'tags'],
    url: '/guardrails/prompts/tags',
    ...options,
  });
};

// == 가드레일 == //

// 가드레일 목록 조회
export const useGetGuardRailList = (params?: GetGuardRailListRequest, options?: ApiQueryOptions<PaginatedDataType<GetGuardRailListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetGuardRailListResponse>>({
    queryKey: ['guardRail', 'list', JSON.stringify(params), DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/guardrails',
    params,
    ...options,
    disableCache: true,
  });
};

// 가드레일 상세 조회
export const useGetGuardRailById = (id: string, options?: ApiQueryOptions<GetGuardRailByIdResponse>) => {
  return useApiQuery<GetGuardRailByIdResponse>({
    queryKey: ['guardRail', id],
    url: `/guardrails/${id}`,
    ...options,
  });
};

// 가드레일 생성
export const useCreateGuardRail = (options?: ApiMutationOptions<CreateGuardRailResponse, CreateGuardRailRequest>) => {
  return useApiMutation<CreateGuardRailResponse, CreateGuardRailRequest>({
    mutationKey: ['guardRail', 'create'],
    url: '/guardrails',
    method: 'POST',
    ...options,
  });
};

// 가드레일 수정
export const useUpdateGuardRail = (options?: ApiMutationOptions<UpdateGuardRailResponse, UpdateGuardRailRequest>) => {
  return useApiMutation<UpdateGuardRailResponse, UpdateGuardRailRequest>({
    mutationKey: ['guardRail', 'update'],
    url: '/guardrails/{id}',
    method: 'PUT',
    ...options,
  });
};

/**
 * 가드레일 삭제 (단일/복수 통합)
 *
 * @param options React Query Mutation 옵션
 * @example
 * // 단일 삭제
 * deleteGuardRail({ guardrailIds: ['id1'] })
 *
 * // 복수 삭제
 * deleteGuardRail({ guardrailIds: ['id1', 'id2', 'id3'] })
 */
export const useDeleteGuardRail = (options?: ApiMutationOptions<DeleteGuardRailResponse, DeleteGuardRailRequest>) => {
  return useApiMutation<DeleteGuardRailResponse, DeleteGuardRailRequest>({
    mutationKey: ['guardRail', 'delete'],
    url: '/guardrails',
    method: 'DELETE',
    ...options,
  });
};
