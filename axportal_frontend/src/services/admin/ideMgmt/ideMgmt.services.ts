import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import type {
  CreateImageRequest,
  CreateImageResponse,
  DeleteImageRequest,
  DeleteImageResponse,
  GetDwAccountListResponse,
  GetImageDetailResponse,
  GetImageListRequest,
  GetImageListResponse,
  GetImageResourceResponse,
  UpdateImageRequest,
  UpdateImageResourceRequest,
  UpdateImageResourceResponse,
  UpdateImageResponse,
} from './ideMgmt.types';

/**
 * IDE 이미지 목록 조회
 */
export const useGetImageList = (params?: GetImageListRequest, options?: ApiQueryOptions<PaginatedDataType<GetImageListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetImageListResponse>>({
    queryKey: ['admin-ide-image-list', JSON.stringify(params)],
    url: '/admin/ide',
    params,
    disableCache: true,
    ...options,
  });
};

/**
 * IDE 이미지 상세 조회
 */
export const useGetImageDetail = (imageId: string, options?: ApiQueryOptions<GetImageDetailResponse>) => {
  return useApiQuery<GetImageDetailResponse>({
    queryKey: ['admin-ide-image-detail', imageId],
    url: `/admin/ide/${imageId}`,
    disableCache: true,
    ...options,
  });
};

/**
 * IDE 이미지 생성
 */
export const useCreateImage = (options?: ApiMutationOptions<CreateImageResponse, CreateImageRequest>) => {
  return useApiMutation<CreateImageResponse, CreateImageRequest>({
    method: 'POST',
    url: '/admin/ide',
    ...options,
  });
};

/**
 * IDE 이미지 수정
 */
export const useUpdateImage = (imageId: string, options?: ApiMutationOptions<UpdateImageResponse, UpdateImageRequest>) => {
  return useApiMutation<UpdateImageResponse, UpdateImageRequest>({
    method: 'PUT',
    url: `/admin/ide/${imageId}`,
    ...options,
  });
};

/**
 * IDE 이미지 삭제
 */
export const useDeleteImage = (options?: ApiMutationOptions<DeleteImageResponse, DeleteImageRequest>) => {
  return useApiMutation<DeleteImageResponse, DeleteImageRequest>({
    method: 'DELETE',
    url: '/admin/ide',
    ...options,
  });
};

// == Dw Account == //

/**
 * DW 계정 목록 조회
 */
export const useGetDwAccountList = (options?: ApiQueryOptions<GetDwAccountListResponse>) => {
  return useApiQuery<GetDwAccountListResponse>({
    queryKey: ['admin-ide-dw-accounts'],
    url: '/admin/ide/dw-accounts',
    disableCache: true,
    ...options,
  });
};

// == Resource == //

/**
 * IDE 리소스 환경 설정 조회
 */
export const useGetImageResource = (options?: ApiQueryOptions<GetImageResourceResponse>) => {
  return useApiQuery<GetImageResourceResponse>({
    queryKey: ['admin-ide-resource'],
    url: '/admin/ide/resource',
    disableCache: true,
    ...options,
  });
};

/**
 * IDE 이미지 리소스 환경 설정 (배열로 처리)
 */
export const useUpdateImageResource = (options?: ApiMutationOptions<UpdateImageResourceResponse, UpdateImageResourceRequest[]>) => {
  return useApiMutation<UpdateImageResourceResponse, UpdateImageResourceRequest[]>({
    method: 'PUT',
    url: '/admin/ide/resource',
    ...options,
  });
};
