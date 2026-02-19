import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type {
  CreateFewShotRequest,
  CreateFewShotResponse,
  GetFewShotByIdRequest,
  GetFewShotByIdResponse,
  GetFewShotByVerIdRequest,
  GetFewShotLineageRelationsRequest,
  GetFewShotLineageResponse,
  GetFewShotListRequest,
  GetFewShotListResponse,
  GetFewShotTagListResponse,
  GetFewShotTagsByVerIdResponse,
  GetFewShotVerListByIdResponse,
  GetLtstFewShotItemListResponse,
  GetLtstFewShotVerResponse,
  UpdateFewShotRequest,
} from './types';

// 퓨샷 상세 조회
export const useGetFewShotById = ({ uuid }: GetFewShotByIdRequest, options?: ApiQueryOptions<GetFewShotByIdResponse>) => {
  return useApiQuery<GetFewShotByIdResponse>({
    queryKey: ['fewShot', uuid.toString()],
    url: `fewShot/${uuid}`, // baseURL + /fewShot/{uuid} = http://localhost:8080/fewShot/{id}
    ...options,
  });
};

// 퓨샷 목록 조회
export const useGetFewShotList = (params?: GetFewShotListRequest, options?: ApiQueryOptions<PaginatedDataType<GetFewShotListResponse>>) => {
  return useApiQuery<PaginatedDataType<GetFewShotListResponse>>({
    queryKey: ['fewShot-list', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params)],
    url: '/fewShot', // baseURL + /fewShot = http://localhost:8080/fewShot
    params,
    ...options,
    disableCache: true,
    timeout: 60000,
  });
};

// 퓨샷 최신 버전 조회
export const useGetLtstFewShotVerById = ({ uuid }: GetFewShotByIdRequest, options?: ApiQueryOptions<GetLtstFewShotVerResponse>) => {
  return useApiQuery<GetLtstFewShotVerResponse>({
    queryKey: ['fewShot', uuid.toString()],
    url: `fewShot/versions/${uuid}/latest`, // baseURL + /fewShot/versions/{uuid}/latest
    ...options,
  });
};

// 퓨샷 버전 목록 조회
export const useGetFewShotVerListById = ({ uuid }: GetFewShotByIdRequest, options?: ApiQueryOptions<GetFewShotVerListByIdResponse>) => {
  return useApiQuery<GetFewShotVerListByIdResponse>({
    queryKey: ['fewShot', uuid.toString()],
    url: `fewShot/versions/${uuid}`, // baseURL + /fewShot/versions/{uuid}
    ...options,
  });
};

// 퓨샷 아이템 목록 조회
export const useGetFewShotItemListById = ({ verId }: GetFewShotByVerIdRequest, options?: ApiQueryOptions<GetLtstFewShotItemListResponse>) => {
  return useApiQuery<GetLtstFewShotItemListResponse>({
    queryKey: ['fewShot', verId.toString()],
    url: `fewShot/items/${verId}`, // baseURL + /fewShot/items/{verId}
    ...options,
  });
};

// 퓨샷 태그 목록 조회
export const useGetFewShotTagsByVerId = ({ verId }: GetFewShotByVerIdRequest, options?: ApiQueryOptions<GetFewShotTagsByVerIdResponse>) => {
  return useApiQuery<GetFewShotTagsByVerIdResponse>({
    queryKey: ['fewShot', verId.toString()],
    url: `fewShot/tags/${verId}`, // baseURL + /fewShot/tags/{verId}
    ...options,
  });
};

// 퓨샷 태그 목록 조회
export const useGetFewShotTagList = (options?: ApiQueryOptions<GetFewShotTagListResponse>) => {
  return useApiQuery<GetFewShotTagListResponse>({
    queryKey: ['fewShot', DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: `fewShot/list/tags`, // baseURL + /fewShot/{uuid} = http://localhost:8080/fewShot/{id}
    ...options,
  });
};

// 퓨샷 생성
export const useCreateFewShot = (options?: ApiMutationOptions<CreateFewShotResponse, CreateFewShotRequest>) => {
  return useApiMutation<CreateFewShotResponse, CreateFewShotRequest>({
    method: 'POST',
    url: '/fewShot',
    ...options,
    timeout: 60000,
  });
};

// 퓨샷 수정
export const useUpdateFewShot = (options?: ApiMutationOptions<string, UpdateFewShotRequest & { uuid: string }>) => {
  return useApiMutation<string, UpdateFewShotRequest & { uuid: string }>({
    method: 'PUT',
    url: '/fewShot/{uuid}', // URL 템플릿: {uuid}가 request.uuid로 치환됨
    ...options,
  });
};

// 퓨샷 삭제
export const useDeleteFewShotById = (options?: ApiMutationOptions<string, { uuid: string }>) => {
  return useApiMutation<string, { uuid: string }>({
    method: 'DELETE',
    url: '/fewShot/{uuid}', // URL 템플릿: {uuid}가 request.uuid로 치환됨
    ...options,
  });
};

// 퓨샷 연결된 에이전트 목록 조회
export const useGetFewShotLineageRelations = (params: GetFewShotLineageRelationsRequest, options?: ApiQueryOptions<PaginatedDataType<GetFewShotLineageResponse>>) => {
  return useApiQuery<PaginatedDataType<GetFewShotLineageResponse>>({
    queryKey: ['fewShot', 'lineageRelations', params.fewShotUuid, params.page.toString(), params.size.toString()],
    url: `fewShot/${params.fewShotUuid}/lineage`, // baseURL + /fewShot/{fewShotUuid}/lineage
    params,
    ...options,
  });
};
