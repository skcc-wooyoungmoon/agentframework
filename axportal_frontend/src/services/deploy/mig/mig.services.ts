import { api } from '@/configs/axios.config';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type { ErrorResponse, SuccessResponse } from '@/hooks/common/api/types';
import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api/useApi';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';
import { useMutation } from '@tanstack/react-query';
import type {
  AssetValidationRequest,
  CopyFolderRequest,
  // GetMigLineageRequest,
  // GetMigLineageResponse,
  GetMigMasRequest,
  GetMigMasResponse,
  GetMigMasWithMapRequest,
  GetMigMasWithMapResponse,
  GetMigResourceEndpointsRequest,
  GetMigResourceEndpointsResponse,
} from './types';

// /**
//  * Mig Lineage 조회
//  */
// export const useGetMigLineage = ({ uuid }: GetMigLineageRequest, options?: { enabled?: boolean }) => {
//   return useApiQuery<GetMigLineageResponse, GetMigLineageRequest>({
//     url: `/common/mig/lineage/${uuid}`,
//     ...options,
//   });
// };

// Asset Validation
export const useAssetValidation = (options?: ApiMutationOptions<boolean, AssetValidationRequest & { uuid: string }>) => {
  return useApiMutation<boolean, AssetValidationRequest & { uuid: string }>({
    method: 'POST',
    url: '/common/mig/asset-validation/{uuid}',
    ...options,
  });
};

export const useCopyFolder = (options?: ApiMutationOptions<boolean, CopyFolderRequest>) => {
  return useMutation<SuccessResponse<boolean>, ErrorResponse, CopyFolderRequest>({
    mutationKey: ['POST', '/common/mig/copy-folder/{project_id}/{type}/{id}'],
    mutationFn: async (request: CopyFolderRequest) => {
      // URL 템플릿을 실제 값으로 치환
      const finalUrl = `/common/mig/copy-folder/${request.project_id}/${request.type}/${request.id}`;

      // URL 파라미터를 body에서 제거한 새 객체 생성
      const { project_id, type, id, ...requestBody } = request;

      const response = await api.post(finalUrl, requestBody);
      return response.data;
    },
    ...options,
  });
};

/**
 * 리소스별 endpoint 리스트 조회
 * Map<String, List<Map<String, Object>>> 형식으로 반환
 * 예: { vectorDb: [{ endpoint: "url1", ... }, { endpoint: "url2", ... }], embeddingModel: [{ endpoint: "url1", ... }] }
 */
export const useGetMigResourceEndpoints = ({ project_id, type, uuid }: GetMigResourceEndpointsRequest, options?: ApiQueryOptions<GetMigResourceEndpointsResponse>) => {
  return useApiQuery<GetMigResourceEndpointsResponse>({
    queryKey: ['mig-resource-endpoints', project_id, type, uuid],
    url: `/common/mig/resource-endpoints/${project_id}/${type}/${uuid}`,
    ...options,
  });
};

/**
 * 운영 이행 관리 조회
 */
export const useGetMigMas = (request: GetMigMasRequest, options?: ApiQueryOptions<GetMigMasResponse>) => {
  // undefined 값을 제거한 params 객체 생성
  const params = Object.entries(request).reduce(
    (acc, [key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        acc[key] = value;
      }
      return acc;
    },
    {} as Record<string, any>
  );

  return useApiQuery<GetMigMasResponse, GetMigMasRequest>({
    queryKey: ['mig-mas', JSON.stringify(request), DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/common/mig/mig-mas',
    params,
    ...options,
  });
};

/**
 * 운영 이행 관리 조회 (Map 포함, 리스트 형태로 반환)
 */
export const useGetMigMasWithMap = (request: GetMigMasWithMapRequest, options?: ApiQueryOptions<GetMigMasWithMapResponse>) => {
  // undefined 값을 제거한 params 객체 생성
  const params = Object.entries(request).reduce(
    (acc, [key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        acc[key] = value;
      }
      return acc;
    },
    {} as Record<string, any>
  );

  return useApiQuery<GetMigMasWithMapResponse, GetMigMasWithMapRequest>({
    queryKey: ['mig-mas-with-map', JSON.stringify(request), DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/common/mig/mig-mas-with-map',
    params,
    ...options,
  });
};
