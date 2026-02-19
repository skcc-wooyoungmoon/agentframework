import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api';
import type { PaginatedDataType } from '@/hooks/common/api/types';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';
import type * as T from './types';

/**
 * 추론 프롬프트 목록 조회
 */
export const useGetInfPromptList = (params?: T.GetInferencePromptsRequest, options?: ApiQueryOptions<PaginatedDataType<T.GetInfPromptResponse>>) =>
  useApiQuery<PaginatedDataType<any>>({
    queryKey: ['inference-prompts', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params)],
    url: '/inference-prompts',
    params,
    ...options,
    disableCache: true,
  });

/*
Prompt UUID 기반 조회 영역
*/
export const useGetInfPromptById = (params: T.GetInfPromptByPromptIdRequest, options?: ApiQueryOptions<T.GetInfPromptByIdResponse>) =>
  useApiQuery<T.GetInfPromptByIdResponse>({
    queryKey: ['inf-prompts', 'detail', params.promptUuid],
    url: `/inference-prompts/${params.promptUuid ?? ''}`,
    ...options,
    disableCache: true,
  });

/*
Prompt UUID 기반 조회 영역
*/
export const useGetInfPromptVerListById = (params: T.GetInfPromptByPromptIdRequest, options?: ApiQueryOptions<T.GetInfPromptVersionDataResponse>) =>
  useApiQuery<T.GetInfPromptVersionDataResponse>({
    queryKey: ['inf-prompts', 'versions', params.promptUuid],
    url: `/inference-prompts/versions/${params.promptUuid ?? ''}`,
    ...options,
  });

/*
Prompt UUID 기반 조회 영역
*/
export const useGetInfPromptLatestVerById = (params: T.GetInfPromptByPromptIdRequest, options?: ApiQueryOptions<T.GetInfPromptLatestVersionResponse>) =>
  useApiQuery<T.GetInfPromptLatestVersionResponse>({
    queryKey: ['inf-prompts', 'versions', 'latest', params.promptUuid],
    url: `/inference-prompts/versions/${params.promptUuid ?? ''}/latest`,
    ...options,
  });

/*
Version UUID 기반 조회 영역
*/
export const useGetInfPromptMsgsById = (params: T.GetInfPromptByVersionIdRequest, options?: ApiQueryOptions<T.GetInfPromptMsgsByIdResponse>) =>
  useApiQuery<T.GetInfPromptMsgsByIdResponse>({
    queryKey: ['inf-prompts', 'messages', params.versionUuid],
    url: `/inference-prompts/messages/${params.versionUuid ?? ''}`,
    ...options,
  });

/*
Version UUID 기반 조회 영역
*/
export const useGetInfPromptVarsById = (params: T.GetInfPromptByVersionIdRequest, options?: ApiQueryOptions<T.GetInfPromptVarsByIdResponse>) =>
  useApiQuery<T.GetInfPromptVarsByIdResponse>({
    queryKey: ['inf-prompts', 'variables', params.versionUuid],
    url: `/inference-prompts/variables/${params.versionUuid ?? ''}`,
    ...options,
  });

/*
Version UUID 기반 조회 영역
*/
export const useGetInfPromptTagById = (params: T.GetInfPromptByVersionIdRequest, options?: ApiQueryOptions<T.GetInfPromptTagsByIdResponse>) =>
  useApiQuery<T.GetInfPromptTagsByIdResponse>({
    queryKey: ['inf-prompts', 'tags', params.versionUuid],
    url: `/inference-prompts/tags/${params.versionUuid ?? ''}`,
    ...options,
  });

// 추론 프롬프트 삭제
export const useDeleteInfPromptByPromptId = (options?: ApiMutationOptions<string, { promptUuid: string }>) => {
  return useApiMutation<string, { promptUuid: string }>({
    method: 'DELETE',
    url: '/inference-prompts/{promptUuid}', // URL 템플릿: {uuid}가 request.uuid로 치환됨
    ...options,
  });
};

// 추론 프롬프트 생성
export const useCreateInfPrompt = (options?: ApiMutationOptions<T.CreateInferencePromptResponse, T.CreateInferencePromptRequest>) => {
  return useApiMutation<T.CreateInferencePromptResponse, T.CreateInferencePromptRequest>({
    method: 'POST',
    url: '/inference-prompts',
    ...options,
    timeout: 180_000,
  });
};

// 추론 프롬프트 수정
export const useUpdateInfPrompt = (options?: ApiMutationOptions<string, T.UpdateInferencePromptRequest>) => {
  return useApiMutation<string, T.UpdateInferencePromptRequest>({
    method: 'PUT',
    url: '/inference-prompts/{promptUuid}',
    ...options,
  });
};

// 추론 프롬프트 릴리즈
export const useReleaseInfPrompt = (options?: ApiMutationOptions<string, T.UpdateInferencePromptRequest>) => {
  return useApiMutation<string, T.UpdateInferencePromptRequest>({
    method: 'PUT',
    url: '/inference-prompts/{promptUuid}',
    ...options,
  });
};

export const useGetInfPromptBuiltin = (options?: ApiQueryOptions<T.GetInfPromptBuiltinResponse>) => {
  return useApiQuery<T.GetInfPromptBuiltinResponse>({
    url: `/inference-prompts/builtin`,
    ...options,
  });
};

// 추론 프롬프트 태그 목록 조회
export const useGetInfPromptTags = (options?: ApiQueryOptions<T.InfPromptTagsListResponse>) => {
  return useApiQuery<T.InfPromptTagsListResponse>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/inference-prompts/tags',
    ...options,
  });
};

// 추론 프롬프트 연결 프롬프트 조회
export const useGetInfPromptLineageRelations = (params?: T.GetInfPromptLineageRelationsRequest, options?: ApiQueryOptions<PaginatedDataType<T.InfPromptLineageRes>>) =>
  useApiQuery<PaginatedDataType<T.InfPromptLineageRes>>({
    queryKey: ['inference-prompts', 'lineage-relations', params?.promptUuid ?? ''],
    url: `/inference-prompts/${params?.promptUuid ?? ''}/lineage-relations`,
    params: {
      page: params?.page,
      size: params?.size,
    },
    ...options,
  });
