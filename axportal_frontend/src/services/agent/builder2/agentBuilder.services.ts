import { api } from '@/configs/axios.config';
import { env } from '@/constants/common/env.constants';
import type { PaginatedDataType } from '@/hooks/common/api/types';
import { useApiMutation, useApiQuery, type ApiMutationOptions, type ApiQueryOptions } from '@/hooks/common/api/useApi';
import { authServices } from '@/services/auth/auth.non.services';
import { authUtils } from '@/utils/common';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type {
  AgentBuilderDetailRes,
  AgentBuilderRes,
  AgentBuilderSearchReq,
  CreateAgentBuilderReq,
  CreateAgentFromTemplateReq,
  DeleteAgentBuilderReq,
  GetAgentByIdRequest,
  GetAgentGraphCodeRequest,
  GetAgentLineagesResponse,
  PhoenixProjectResponse,
  UpdateAgentBuilderReq,
} from './types';
//
// 템플릿 목록 조회 훅 (useQuery + service 함수 사용)
export const useGetBuilderTemplates = (options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    queryKey: ['builderTemplates', DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: `/agent/builder/templates`,
    ...options,
  });
};

/**
 * Agent Builder 목록 조회 (페이지네이션)
 */
export const useGetAgentBuilders = (params?: AgentBuilderSearchReq, options?: ApiQueryOptions<PaginatedDataType<AgentBuilderRes>>) => {
  return useApiQuery<PaginatedDataType<AgentBuilderRes>>({
    queryKey: ['agent-builder-list', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify({page: params?.page, size: params?.size})],
    url: '/agent/builder',
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * Agent Builder 상세 조회
 */
export const useGetAgentBuilderById = (id: string, options?: ApiQueryOptions<AgentBuilderDetailRes>) => {
  // id가 유효한 문자열인지 검증
  const isValidId = typeof id === 'string' && id.trim() !== '';

  return useApiQuery<AgentBuilderDetailRes>({
    queryKey: ['agent-builder', id],
    url: `/agent/builder/${id}`,
    timeout: 60000,
    ...options,
    // options.enabled가 있으면 그것과 함께 검증, 없으면 isValidId만 사용
    enabled: options?.enabled !== undefined ? isValidId && options.enabled : isValidId,
  });
};

/**
 * Agent Builder 생성
 */
export const useCreateAgentBuilder = (options?: ApiMutationOptions<AgentBuilderRes, CreateAgentBuilderReq>) => {
  return useApiMutation<AgentBuilderRes, CreateAgentBuilderReq>({
    method: 'POST',
    url: '/agent/builder',
    ...options,
  });
};

/**
 * 템플릿 기반 에이전트 생성
 */
export const useCreateAgentFromTemplate = (options?: ApiMutationOptions<any, CreateAgentFromTemplateReq>) => {
  return useApiMutation<any, CreateAgentFromTemplateReq>({
    method: 'POST',
    url: '/agent/builder/create-from-template',
    ...options,
  });
};

/**
 * Agent Builder 수정 (그래프 전체 저장용)
 */
export const useUpdateAgentBuilder = (
  options?: ApiMutationOptions<AgentBuilderRes, UpdateAgentBuilderReq>
): ReturnType<typeof useApiMutation<AgentBuilderRes, UpdateAgentBuilderReq>> => {
  return useApiMutation<AgentBuilderRes, UpdateAgentBuilderReq>({
    method: 'PUT',
    url: '/agent/builder/graphs/{id}', // request.id를 path 변수로 사용
    ...options,
    timeout: 60000,
  });
};

/**
 * Agent Builder 삭제
 */
export const useDeleteAgentBuilder = (options?: ApiMutationOptions<{}, DeleteAgentBuilderReq>) => {
  return useApiMutation<{}, DeleteAgentBuilderReq>({
    method: 'DELETE',
    url: '/agent/graphs/{graphUuid}',
    ...options,
  });
};

/**
 * 에이전트 배포 정보 조회
 */
export const useGetAgentDeployInfo = (agentId: string, options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    queryKey: ['agent-deploy-info', agentId],
    url: `/agent/builder/graphs/${agentId}/app`,
    ...options,
  });
};

// 에이전트 정보 업데이트
export const useUpdateAgentInfo = (options?: ApiMutationOptions<any, { id: string; name: string; description: string }>) => {
  return useApiMutation<any, { id: string; name: string; description: string }>({
    method: 'PUT',
    url: '/agent/builder/graphs/{id}/info',
    ...options,
    timeout: 60000,
  });
};

/**
 * 템플릿 상세 정보 조회
 */
export const useGetTemplateDetail = (templateId: string, options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    url: `/agent/builder/templates/${templateId}`,
    enabled: !!templateId,
    ...options,
  });
};

/**
 * 에이전트 Lineage 조회
 */
export const useGetAgentLineages = ({ agentId }: GetAgentByIdRequest, options?: ApiQueryOptions<GetAgentLineagesResponse>) => {
  const idString = agentId ? String(agentId) : '';
  const enabled = Boolean(agentId);

  return useApiQuery<GetAgentLineagesResponse>({
    queryKey: ['agentLineages', idString],
    url: `agent/builder/graphs/${agentId}/lineages`,
    enabled: options?.enabled ?? enabled,
    ...options,
  });
};

/**
 * 에이전트 그래프 스트리밍 실행 (실시간 SSE 스트리밍)
 * @param request 스트리밍 요청 데이터
 * @param onChunk 청크 단위로 데이터를 받을 콜백 함수
 * @returns 전체 응답 문자열 (콜백을 사용하지 않을 경우)
 */
export const streamAgentGraph = async (request: { graph_id: string; input_data: any; [key: string]: any }, onChunk?: (chunk: string) => void): Promise<string> => {
  // 🔥 스트리밍 요청 전 토큰 만료 체크 및 갱신 (멀티턴 대화 시 401 방지)
  let token = authUtils.getAccessToken();
  if (token) {
    const isExpired = authUtils.isAccessTokenExpired();
    // 타임아웃이 15분이므로, 10분 전에 갱신하여 안전 마진 확보
    const isExpiringSoon = authUtils.isAccessTokenExpiringSoon(10);

    if (isExpired || isExpiringSoon) {
      try {
        const refreshToken = authUtils.getRefreshToken();
        if (refreshToken) {
          await authServices.refresh();
          token = authUtils.getAccessToken(); // 갱신된 토큰 가져오기
        } else {
          authUtils.clearTokens();
          window.location.href = '/login';
          throw new Error('Refresh token not found');
        }
      } catch (refreshError) {
        authUtils.clearTokens();
        window.location.href = '/login';
        throw refreshError;
      }
    }
  }

  // fetch API를 사용하여 실시간 스트리밍 지원
  const baseUrl = env.VITE_API_BASE_URL.endsWith('/') ? env.VITE_API_BASE_URL : `${env.VITE_API_BASE_URL}/`;
  let response: Response;

  try {
    response = await fetch(`${baseUrl}agent/builder/graphs/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
        Authorization: token ? `Bearer ${token}` : '',
        'X-Frontend-Path': window.location.pathname,
      },
      body: JSON.stringify(request),
    });
  } catch (networkError: any) {
    // 네트워크 에러 (연결 실패, 타임아웃 등)
    const error = new Error(networkError?.message || '네트워크 오류가 발생했습니다.') as any;
    error.isNetworkError = true;
    error.originalError = networkError;
    throw error;
  }

  if (!response.ok) {
    // HTTP 에러 응답 본문 읽기 시도
    let errorBody = '';
    try {
      const errorText = await response.text();
      errorBody = errorText;
      // JSON 파싱 시도
      try {
        const errorJson = JSON.parse(errorText);
        const error = new Error(errorJson?.error?.message || errorJson?.message || `HTTP error! status: ${response.status}`) as any;
        error.status = response.status;
        error.statusText = response.statusText;
        error.response = {
          status: response.status,
          statusText: response.statusText,
          data: errorJson,
        };
        throw error;
      } catch {
        // JSON 파싱 실패 시 텍스트 그대로 사용
      }
    } catch {
      // 응답 본문 읽기 실패
    }

    const error = new Error(errorBody || `HTTP error! status: ${response.status}`) as any;
    error.status = response.status;
    error.statusText = response.statusText;
    error.response = {
      status: response.status,
      statusText: response.statusText,
      data: errorBody ? { message: errorBody } : undefined,
    };
    throw error;
  }

  const reader = response.body?.getReader();
  const decoder = new TextDecoder();

  if (!reader) {
    throw new Error('Response body is not readable');
  }

  let fullResponse = '';
  let hasError = false;
  let errorMessage = '';

  // 실시간으로 청크 단위로 데이터 읽기
  try {
    while (true) {
      let readResult: ReadableStreamReadResult<Uint8Array>;

      try {
        readResult = await reader.read();
      } catch (readError: any) {
        // 🔥 ERR_INCOMPLETE_CHUNKED_ENCODING 또는 네트워크 오류 처리
        const isIncompleteChunked =
          readError?.message?.includes('ERR_INCOMPLETE_CHUNKED_ENCODING') || readError?.message?.includes('incomplete') || readError?.message?.includes('chunked');

        const isNetworkError =
          readError?.message?.toLowerCase().includes('network') ||
          readError?.message?.toLowerCase().includes('fetch') ||
          readError?.message?.toLowerCase().includes('connection') ||
          readError?.name === 'NetworkError' ||
          readError?.name === 'TypeError';

        // 이미 수집된 데이터가 있으면 반환 (부분 성공)
        if (fullResponse.trim().length > 0) {
          try {
            reader.releaseLock();
          } catch {
            // 무시
          }

          // 에러 메시지가 있으면 포함
          if (errorMessage) {
            const error = new Error(errorMessage) as any;
            error.isStreamError = true;
            error.isPartialResponse = true;
            error.partialResponse = fullResponse;
            throw error;
          }

          return fullResponse;
        }

        // reader 해제 시도
        try {
          reader.releaseLock();
        } catch {
          // 무시
        }

        // 🔥 에러 메시지 표시하지 않고 조용히 처리
        if (isIncompleteChunked || isNetworkError) {
          // 수집된 데이터가 있으면 반환, 없으면 빈 문자열 반환
          return fullResponse || '';
        }

        // 기타 에러도 조용히 처리
        return fullResponse || '';
      }

      const { done, value } = readResult;

      // 🔥 done이 true이면 스트림이 완전히 종료된 것
      if (done) {
        // 마지막 버퍼에 남은 데이터 디코딩
        try {
          const finalChunk = decoder.decode(new Uint8Array(), { stream: false });
          if (finalChunk) {
            fullResponse += finalChunk;
            if (onChunk) {
              onChunk(finalChunk);
            }
          }
        } catch {
          // 디코딩 실패는 무시
        }
        break;
      }

      const chunk = decoder.decode(value, { stream: true });
      fullResponse += chunk;

      // 🔥 스트림에서 에러 이벤트 감지 (error: {...} 형식)
      if (chunk.includes('"error"') || chunk.includes('"status_code"')) {
        try {
          // JSON 형식의 에러 메시지 추출 시도
          const errorMatch = chunk.match(/"message"\s*:\s*"([^"]+)"/);
          if (errorMatch && errorMatch[1]) {
            errorMessage = errorMatch[1];
            hasError = true;
          }

          // status_code 추출
          const statusMatch = chunk.match(/"status_code"\s*:\s*(\d+)/);
          if (statusMatch && statusMatch[1] && parseInt(statusMatch[1]) >= 400) {
            hasError = true;
          }
        } catch {
          // JSON 파싱 실패는 무시
        }
      }

      // 콜백이 있으면 청크 단위로 전달
      if (onChunk) {
        onChunk(chunk);
      }

      // 🔥 [DONE] 신호를 감지하면 스트림을 명시적으로 취소 (ERR_INCOMPLETE_CHUNKED_ENCODING 방지)
      if (chunk.includes('[DONE]')) {
        // 스트림 취소 - 서버가 연결을 닫기 전에 클라이언트에서 먼저 종료
        try {
          await reader.cancel();
        } catch {
          // 취소 실패는 무시
        }
        break;
      }
    }

    // 🔥 에러가 감지되었지만 스트림이 정상 종료된 경우
    if (hasError && errorMessage) {
      const error = new Error(errorMessage) as any;
      error.isStreamError = true;
      error.responseData = fullResponse;
      throw error;
    }
  } catch (streamError: any) {
    // 🔥 스트림 에러 중 AbortError는 정상적인 취소이므로 무시
    if (streamError?.name === 'AbortError' || streamError?.message?.includes('cancel')) {
      // 정상적인 스트림 취소
      return fullResponse;
    }

    // 🔥 이미 처리된 네트워크 오류는 그대로 전파
    if (streamError?.isNetworkError || streamError?.isIncompleteChunked) {
      throw streamError;
    }

    // 🔥 스트림 에러 발생 시 reader 해제
    try {
      reader.releaseLock();
    } catch {
      // 이미 해제된 경우 무시
    }

    // 스트림 읽기 중 에러
    const error = new Error(streamError?.message || '스트림 읽기 중 오류가 발생했습니다.') as any;
    error.isStreamError = true;
    error.originalError = streamError;

    // 부분 응답이 있으면 포함
    if (fullResponse) {
      error.partialResponse = fullResponse;
    }

    throw error;
  } finally {
    // 🔥 정상 종료 시에도 reader 해제 (메모리 누수 방지)
    try {
      reader.releaseLock();
    } catch {
      // 이미 해제된 경우 무시
    }
  }

  return fullResponse;
};

export const useGetAgentGraphCode = ({ graphId, credentialType = 'token' }: GetAgentGraphCodeRequest, options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    url: `/agent/builder/graphs/${graphId}/export/code`,
    params: {
      credential_type: credentialType,
    },
    ...options,
  });
};

export const getPhoenixProjectId = async (graphId: string): Promise<PhoenixProjectResponse | null> => {
  try {
    const baseUrl = (api.defaults.baseURL || '').replace(/\/$/, '');
    const url = `${baseUrl}/agent/builder/phoenix/project?type=graph&id=${encodeURIComponent(graphId)}`;
    const token = authUtils.getAccessToken();

    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      // credentials 제거하여 CORS 문제 해결
    });

    if (!response.ok) {
      // console.warn('⚠️ Phoenix 프로젝트 ID 조회 실패:', response.status, response.statusText);
      return null;
    }

    const result = await response.json();
    const projectId = result?.data?.projectId;
    const enableAuth = result?.data?.enableAuth ?? result?.enableAuth ?? false;
    const phoenixUrl = result?.data?.phoenixUrl ?? result?.phoenixUrl;

    return {
      projectId: typeof projectId === 'string' && projectId.trim().length > 0 ? projectId.trim() : null,
      enableAuth,
      phoenixUrl,
    };
  } catch (error) {
    // console.warn('⚠️ Phoenix 프로젝트 ID 조회 중 예외 발생:', error);
    return null;
  }
};
