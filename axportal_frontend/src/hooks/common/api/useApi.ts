import { useMutation, useQueries, useQuery } from '@tanstack/react-query';

import { api } from '@/configs/axios.config';
import type { ErrorResponse, SuccessResponse } from '@/hooks/common/api/types';

import type { UseMutationOptions, UseQueryOptions } from '@tanstack/react-query';

/******************************
 * useApiQuery
 ******************************/
/**
 * @interface API 쿼리 옵션 타입
 * @description React Query의 UseQueryOptions에서 queryFn, queryKey, select를 제외한 옵션들
 */
export type ApiQueryOptions<TResponse> = Omit<UseQueryOptions<SuccessResponse<TResponse>, ErrorResponse, TResponse>, 'queryFn' | 'queryKey' | 'select'>;

/**
 * @interface API 쿼리 설정 인터페이스
 * @description GET 요청을 위한 설정을 정의하며, React Query의 모든 옵션을 지원
 */
interface ApiQueryConfig<TResponse, TRequest> extends Omit<UseQueryOptions<SuccessResponse<TResponse>, ErrorResponse, TRequest>, 'queryFn' | 'queryKey' | 'select'> {
  /** API 엔드포인트 URL */
  url: string;
  /** 추가 쿼리 키 (선택사항) */
  queryKey?: string[];
  /** 쿼리 파라미터 (선택사항) */
  params?: Record<string, any>;
  /** 요청 타임아웃 (밀리초, 선택사항) */
  timeout?: number;
  /** 캐시 비활성화 여부 (true일 경우 staleTime: 0, gcTime: 0으로 설정) */
  disableCache?: boolean;
  /** 쿼리 파라미터 직렬화 함수 (선택사항) */
  paramsSerializer?: (params: any) => string;
}

/**
 * @hook API GET 요청을 위한 커스텀 훅
 * @description React Query의 모든 기능을 지원하며, 자동으로 응답 데이터를 추출
 *
 * @param config - 쿼리 설정 객체
 * @returns React Query의 useQuery 결과
 *
 * @example
 * ```tsx
 * const { data, isLoading, error } = useApiQuery({
 *   url: '/api/users',
 *   params: { page: 1 },
 *   queryKey: ['users', 'page1']
 * });
 * ```
 */
export const useApiQuery = <TResponse, TRequest = {}>(config: ApiQueryConfig<TResponse, TRequest>) => {
  const { queryKey, url, params, timeout, disableCache, paramsSerializer, ...queryOptions } = config;

  // 캐시 설정 처리
  // 1. disableCache가 true이고 사용자가 staleTime/gcTime을 명시적으로 설정하지 않았으면 캐시 비활성화
  // 2. 사용자가 직접 staleTime/gcTime을 설정했으면 그것을 우선 사용 (queryOptions에 이미 있음)
  // 3. 둘 다 없으면 QueryProvider의 기본값 사용
  const cacheConfig: { staleTime?: number; gcTime?: number } = {};

  if (disableCache) {
    // 사용자가 명시적으로 설정하지 않은 경우에만 캐시 비활성화
    if (queryOptions.staleTime === undefined || typeof queryOptions.staleTime !== 'number') {
      cacheConfig.staleTime = 0;
    }
    if (queryOptions.gcTime === undefined || typeof queryOptions.gcTime !== 'number') {
      cacheConfig.gcTime = 0;
    }
  }

  return useQuery({
    queryKey: ['GET', url, ...(queryKey ?? [])],
    queryFn: async () => {
      const response = await api.get(url, {
        params,
        ...(timeout ? { timeout } : {}),
        ...(paramsSerializer ? { paramsSerializer } : {}),
      });
      return response.data;
    },
    select: (data: SuccessResponse<TResponse>) => data.data,
    // cacheConfig를 먼저 설정하고, queryOptions가 나중에 오므로 사용자 설정이 우선순위가 높음
    ...cacheConfig,
    ...queryOptions,
  });
};

/******************************
 * useApiQueries
 ******************************/

/**
 * @interface API 쿼리 배열 설정 인터페이스
 * @description 여러 개의 API 쿼리를 동시에 실행하기 위한 설정을 정의
 */
interface ApiQueriesConfig<TResponse> {
  /** API 엔드포인트 URL */
  url: string;
  /** 추가 쿼리 키 (선택사항) */
  queryKey?: string[];
  /** 쿼리 파라미터 (선택사항) */
  params?: Record<string, any>;
  /** React Query 옵션들 */
  options?: ApiQueryOptions<TResponse>;
}

/**
 * @hook 여러 API GET 요청을 동시에 실행하는 커스텀 훅
 * @description React Query의 useQueries를 기반으로 여러 API 요청을 병렬로 실행
 *
 * @param configs - 쿼리 설정 객체 배열
 * @returns React Query의 useQueries 결과
 *
 * @example
 * ```tsx
 * const queries = useApiQueries([
 *   {
 *     url: '/api/users',
 *     params: { page: 1 },
 *     queryKey: ['users', 'page1']
 *   },
 *   {
 *     url: '/api/posts',
 *     params: { limit: 10 },
 *     queryKey: ['posts', 'recent']
 *   }
 * ]);
 * ```
 */
export const useApiQueries = <TResponse>(configs: ApiQueriesConfig<TResponse>[]) => {
  const queries = configs.map(config => {
    const { queryKey, url, params, options = {} } = config;

    return {
      queryKey: ['GET', url, ...(queryKey ?? [])],
      queryFn: async () => {
        const response = await api.get(url, { params });
        return response.data;
      },
      select: (data: SuccessResponse<TResponse>) => data.data,
      ...options,
    };
  });

  return useQueries({
    queries,
  });
};

/******************************
 * useApiMutation
 ******************************/

/**
 * @interface API 뮤테이션 옵션 타입
 * @description React Query의 UseMutationOptions에서 mutationFn, mutationKey를 제외한 옵션들
 */
export type ApiMutationOptions<TResponse, TRequest> = Omit<UseMutationOptions<SuccessResponse<TResponse>, ErrorResponse, TRequest>, 'mutationFn' | 'mutationKey'>;

/**
 * @interface API 뮤테이션 설정 인터페이스
 * @description POST, PUT, DELETE 요청을 위한 설정을 정의
 */
interface ApiMutationConfig<TResponse, TRequest> extends ApiMutationOptions<TResponse, TRequest> {
  /** HTTP 메소드 */
  method: 'POST' | 'PUT' | 'DELETE';
  /** API 엔드포인트 URL (템플릿 문자열 지원) */
  url: string;
  /** 추가 뮤테이션 키 (선택사항) */
  mutationKey?: string[];
  /** 요청 타임아웃 (밀리초, 선택사항) */
  timeout?: number;
}

/**
 * @hook API 뮤테이션을 위한 커스텀 훅
 * @description POST, PUT, DELETE 요청을 지원하며, URL 템플릿의 동적 치환 기능 제공
 *
 * @param config - 뮤테이션 설정 객체
 * @returns React Query의 useMutation 결과
 *
 * @example
 * ```tsx
 * const mutation = useApiMutation({
 *   method: 'POST',
 *   url: '/api/users',
 *   mutationKey: ['createUser']
 * });
 *
 * // URL 템플릿 사용 예시
 * const updateMutation = useApiMutation({
 *   method: 'PUT',
 *   url: '/api/users/{id}', // request 객체에 id가 있으면 자동으로 치환됨
 *   mutationKey: ['updateUser']
 * });
 * ```
 */

export const useApiMutation = <TResponse, TRequest>(config: ApiMutationConfig<TResponse, TRequest>) => {
  const { method, url, mutationKey, timeout, ...mutationOptions } = config;

  return useMutation<SuccessResponse<TResponse>, ErrorResponse, TRequest>({
    mutationKey: [method, url, ...(mutationKey ?? [])],
    mutationFn: async (request: TRequest) => {
      // URL 템플릿을 실제 값으로 치환
      const finalUrl = url.replace(/\{(\w+)\}/g, (match, key) => {
        return request[key as keyof TRequest]?.toString() || match;
      });

      const isFormData = request instanceof FormData;
      const axiosConfig = {
        ...(isFormData ? {} : {}),
        ...(timeout ? { timeout } : {}),
      };

      const methodMap = {
        POST: () => api.post(finalUrl, request, axiosConfig),
        PUT: () => api.put(finalUrl, request, axiosConfig),
        DELETE: () => api.delete(finalUrl, { data: request, ...axiosConfig }),
      };

      const apiMethod = methodMap[method];
      if (!apiMethod) {
        throw new Error(`Unsupported method: ${method}`);
      }

      const response = await apiMethod();
      return response.data;
    },
    ...mutationOptions,
  });
};
