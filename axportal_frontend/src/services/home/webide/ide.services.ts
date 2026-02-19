import { type ApiQueryOptions, useApiMutation, useApiQuery } from '@/hooks/common/api';
import type { PaginatedDataType } from '@/hooks/common/api/types';

import type {
  AccessTokenRes,
  CheckIdeCreateAvailableRequest,
  CreateIdeRequest,
  DwAccountRes,
  DwAllAccountsRes,
  GetDwAccountRequest,
  GetIdeRequest,
  GetIdeStatusRequest,
  GetPythonVerRequest,
  IdeCreateRes,
  IdeDeleteReq,
  IdeExtendReq,
  IdeImageRes,
  IdeListRes,
  IdeStatusRes,
  PythonVerRes
} from './types';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants.ts';

/**
 * 사용중인 IDE 상태 조회
 * @param params userName (실제로는 사용자 ID 값), ideType
 * @param options React Query 옵션 (enabled 옵션으로 자동 fetch 여부 제어 가능)
 */
export const useGetIde = (params: GetIdeRequest, options?: ApiQueryOptions<IdeListRes>) =>
  useApiQuery<IdeListRes>({
    queryKey: ['ide', params.userName, params.ideType],
    url: `/home/ide/${params.userName}/${params.ideType}`,
    staleTime: 0, // 항상 stale로 간주하여 새로고침
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    ...options,
  });

/**
 * IDE 파이썬 버전 목록 조회
 * @param params ideType
 * @param options React Query 옵션
 */
export const useGetPythonVer = (params: GetPythonVerRequest, options?: ApiQueryOptions<PythonVerRes>) =>
  useApiQuery<PythonVerRes>({
    queryKey: ['ide', 'python-version', params.ideType as 'python' | 'jupyter'],
    url: `/home/ide/python-version/${params.ideType}`,
    ...options,
  });

/**
 * IDE 생성
 * @param options React Query mutation 옵션
 */
export const useCreateIde = (options?: { onSuccess?: () => void; onError?: () => void }) =>
  useApiMutation<IdeCreateRes, CreateIdeRequest>({
    url: '/home/ide',
    method: 'POST',
    ...options,
  });

/**
 * IDE 삭제
 * @param options React Query mutation 옵션
 */
export const useDeleteIde = (options?: { onSuccess?: () => void; onError?: () => void }) =>
  useApiMutation<void, IdeDeleteReq>({
    url: '/home/ide/{ideId}',
    method: 'DELETE',
    ...options,
  });

/**
 * DW 계정 목록 조회
 * @param params userId
 * @param options React Query 옵션
 */
export const useGetDwAccount = (params: GetDwAccountRequest, options?: ApiQueryOptions<DwAccountRes>) =>
  useApiQuery<DwAccountRes>({
    queryKey: ['ide', 'dw-account', params.userId],
    url: `/home/ide/dw-account/${params.userId}`,
    ...options,
  });

/**
 * 전체 DW 계정 목록 조회
 * @param options React Query 옵션
 */
export const useDwAllAccounts = (options?: ApiQueryOptions<DwAllAccountsRes>) =>
  useApiQuery<DwAllAccountsRes>({
    queryKey: ['ide', 'dw-all-accounts'],
    url: '/home/ide/dw-all-accounts',
    ...options,
  });

/**
 * DW 계정 목록 조회
 * @param 없음
 * @param options React Query 옵션
 */
export const useGetAccessToken = (options?: ApiQueryOptions<AccessTokenRes>) =>
  useApiQuery<AccessTokenRes>({
    queryKey: ['auth', 'access-token'], // ← 고정된 키
    url: '/auth/sktai/access-token',
    enabled: false,
    ...options,
  });

/**
 * IDE 이미지 목록 조회
 * @param options React Query 옵션
 */
export const useGetIdeImages = (options?: ApiQueryOptions<IdeImageRes>) =>
  useApiQuery<IdeImageRes>({
    url: '/home/ide/images',
    ...options,
  });

/**
 * IDE 생성 가능 여부 확인
 * @param params ideType
 * @param options React Query 옵션
 */
export const useCheckIdeCreateAvailable = (params: CheckIdeCreateAvailableRequest, options?: ApiQueryOptions<boolean>) =>
  useApiQuery<boolean>({
    url: '/home/ide/create-available',
    params: { ideType: params.ideType },
    ...options,
  });

/**
 * 사용자 IDE 목록 조회 (페이지네이션 지원)
 * @param params memberId (사용자 ID), keyword (검색어), page, size
 * @param options React Query 옵션
 */
export const useGetIdeStatus = (params: GetIdeStatusRequest, options?: ApiQueryOptions<PaginatedDataType<IdeStatusRes>>) =>
  useApiQuery<PaginatedDataType<IdeStatusRes>>({
    queryKey: ['ide', 'status', JSON.stringify(params), DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: `/home/ide/status/${params.memberId}`,
    params: {
      keyword: params.keyword,
      page: params.page,
      size: params.size,
    },
    ...options,
    disableCache: true,
  });

/**
 * IDE 사용 기간 연장
 * @param options React Query mutation 옵션
 */
export const useExtendIdeExpiration = (options?: { onSuccess?: () => void; onError?: () => void }) =>
  useApiMutation<void, IdeExtendReq>({
    url: '/home/ide/status/{statusUuid}/extend',
    method: 'PUT',
    ...options,
  });
