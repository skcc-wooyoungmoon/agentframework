import { useApiMutation, useApiQuery, type ApiMutationOptions, type ApiQueryOptions } from '@/hooks/common/api';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';

import type {
  AdminGetApiKeyDetailRequest,
  AdminGetApiKeyDetailResponse,
  // Admin 타입
  AdminGetApiKeyListRequest,
  AdminGetApiKeyListResponse,
  BlockApiKeyRequest,
  BlockApiKeyResponse,
  CreateApiKeyRequest,
  CreateApiKeyResponse,
  DeleteApiKeyRequest,
  DeleteApiKeyResponse,
  // Deploy 타입
  GetApiKeyDetailRequest,
  GetApiKeyListRequest,
  GetApiKeyListResponse,
  GetApiKeyResponse,
  GetApiKeyStaticRequest,
  GetApiKeyStaticResponse,
  UpdateQuotaRequest,
  UpdateQuotaResponse,
} from './types';

/**
 * Deploy - API Key 목록 조회
 */
export const useGetApiKeyList = (params: GetApiKeyListRequest) => {
  return useApiQuery<GetApiKeyListResponse, GetApiKeyListRequest>({
    url: '/apiKeys',
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    params,
    disableCache: true,
  });
};

/**
 * Deploy - API Key 상세 조회
 */
export const useGetApiKeyDetail = ({ id }: GetApiKeyDetailRequest) => {
  return useApiQuery<GetApiKeyResponse, GetApiKeyDetailRequest>({
    url: `/apiKeys/${id}`,
  });
};

/**
 * Admin - API Key 목록 조회
 */
export const useGetAdminApiKeyList = (params: AdminGetApiKeyListRequest) => {
  return useApiQuery<AdminGetApiKeyListResponse, AdminGetApiKeyListRequest>({
    url: '/apiKeys/mgmt/admin',
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    params,
    disableCache: true,
  });
};

/**
 * Admin - API Key 상세 조회
 */
export const useGetAdminApiKeyDetail = ({ id }: AdminGetApiKeyDetailRequest, options?: ApiQueryOptions<AdminGetApiKeyDetailResponse>) => {
  return useApiQuery<AdminGetApiKeyDetailResponse>({
    queryKey: ['admin-apikey-detail', id],
    url: `/apiKeys/${id}`,
    ...options,
  });
};

/**
 * API Key 삭제
 */
export const useDeleteApiKey = (id: string) => {
  return useApiMutation<DeleteApiKeyResponse, DeleteApiKeyRequest>({
    url: `/apiKeys/${id}`,
    method: 'DELETE',
  });
};

/**
 * API Key 사용차단
 */
export const useBlockApiKey = (id: string, options?: ApiMutationOptions<BlockApiKeyResponse, BlockApiKeyRequest>) => {
  return useApiMutation<BlockApiKeyResponse, BlockApiKeyRequest>({
    url: `/apiKeys/${id}/expire`,
    method: 'PUT',
    ...options,
  });
};

/**
 * API Key 차단해제
 */
export const useRestoreApiKey = (id: string, options?: ApiMutationOptions<BlockApiKeyResponse, BlockApiKeyRequest>) => {
  return useApiMutation<BlockApiKeyResponse, BlockApiKeyRequest>({
    url: `/apiKeys/${id}/restore`,
    method: 'PUT',
    ...options,
  });
};

/**
 * API Key Quota 수정
 */
export const useUpdateQuota = (id: string, options?: ApiMutationOptions<UpdateQuotaResponse, UpdateQuotaRequest>) => {
  return useApiMutation<UpdateQuotaResponse, UpdateQuotaRequest>({
    url: `/apiKeys/${id}/quota`,
    method: 'PUT',
    ...options,
  });
};

export const useCreateApiKey = () => {
  return useApiMutation<CreateApiKeyResponse, CreateApiKeyRequest>({
    url: '/apiKeys',
    method: 'POST',
  });
};

/**
 * API Key 통계 조회
 */
export const useGetApiKeyStatic = ({ id, startDate, endDate }: GetApiKeyStaticRequest, options?: ApiQueryOptions<GetApiKeyStaticResponse>) => {
  // 임시 하드코딩
  // const hardcodedId = '20251021182514862MAI6JD156QPHKK397692K1160KLHOG227';
  // const hardcodedStartDate = '202509150000';
  // const hardcodedEndDate = '202509152359';
  // id;
  // startDate;
  // endDate;
  return useApiQuery<GetApiKeyStaticResponse>({
    // queryKey: ['apikey-static', id, startDate, endDate],
    url: `/apiKeys/${id}/static`,
    params: {
      startDate: startDate,
      endDate: endDate,
    },
    ...options,
  });
};
