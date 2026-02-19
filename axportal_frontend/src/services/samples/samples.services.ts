import type {
  ApiMutationOptions,
  ApiQueryOptions,
  PaginatedDataType,
} from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import type {
  GetSampleByIdRequest,
  GetSampleByIdResponse,
  GetSamplesRequest,
  GetSamplesResponse,
  PostSampleRequest,
  PutSampleRequest,
} from './types';

export const useGetSamplesById = (
  { id }: GetSampleByIdRequest,
  options?: ApiQueryOptions<GetSampleByIdResponse>
) => {
  return useApiQuery<GetSampleByIdResponse>({
    queryKey: ['samples', id.toString()],
    url: `samples/${id}`, // baseURL + /samples/{id} = http://localhost:8080/samples/{id}
    ...options,
  });
};

export const useGetSamples = (
  params?: GetSamplesRequest,
  options?: ApiQueryOptions<PaginatedDataType<GetSamplesResponse>>
) => {
  return useApiQuery<PaginatedDataType<GetSamplesResponse>>({
    queryKey: ['samples-list'],
    url: '/samples', // baseURL + /samples = http://localhost:8080/samples
    params,
    ...options,
  });
};

export const useDeleteSample = (
  options?: ApiMutationOptions<string, { id: number }>
) => {
  return useApiMutation<string, { id: number }>({
    method: 'DELETE',
    url: `/samples/{id}`,
    ...options,
  });
};

export const usePostSample = () => {
  return useApiMutation<string, PostSampleRequest>({
    method: 'POST',
    url: '/samples',
  });
};

export const usePutSample = (
  options?: ApiMutationOptions<string, PutSampleRequest>
) => {
  return useApiMutation<string, PutSampleRequest>({
    method: 'PUT',
    url: '/samples/{id}',
    ...options,
  });
};
