import { api } from '@/configs/axios.config';
import { authUtils } from '@/utils/common';
import { authServices } from '@/services/auth/auth.non.services';
import type { PaginatedDataType } from '@/hooks/common/api/types';
import { useApiMutation, useApiQuery, type ApiMutationOptions, type ApiQueryOptions } from '@/hooks/common/api/useApi';
import { useMutation } from '@tanstack/react-query';
import type { ErrorResponse, SuccessResponse } from '@/hooks/common/api/types';
import { env } from '@/constants/common/env.constants';

import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import type {
  AgentBuilderDetailRes,
  AgentBuilderRes,
  AgentBuilderSearchReq,
  CreateAgentBuilderReq,
  CreateAgentFromTemplateReq,
  DeleteAgentBuilderReq,
  GetAgentByIdRequest,
  GetAgentLineagesResponse,
  Template,
  TemplatesResponse,
  TemplateStructure,
  UpdateAgentBuilderReq,
} from './types';

/**
 * 템플릿 API 호출 훅
 */
export const useAgentTemplates = () => {
  return useApiQuery<TemplatesResponse>({
    url: '/agent/builder/templates',
  });
};

/**
 * Agent Builder 목록 조회 (페이지네이션)
 */
export const useGetAgentBuilders = (params?: AgentBuilderSearchReq, options?: ApiQueryOptions<PaginatedDataType<AgentBuilderRes>>) => {
  return useApiQuery<PaginatedDataType<AgentBuilderRes>>({
    queryKey: ['agent-builder-list', DONT_SHOW_LOADING_KEYS.GRID_DATA, JSON.stringify(params || {})],
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
export const useUpdateAgentBuilder = (options?: ApiMutationOptions<AgentBuilderRes, UpdateAgentBuilderReq>) => {
  return useApiMutation<AgentBuilderRes, UpdateAgentBuilderReq>({
    method: 'PUT',
    url: '/agent/builder/graphs/{agentId}', // putAgent와 동일한 엔드포인트 사용
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
 * Agent Builder 일괄 삭제 (개별 삭제 API를 여러 번 호출)
 */
// export const useBulkDeleteAgentBuilder = (options?: ApiMutationOptions<{}, BulkDeleteAgentBuilderReq>) => {
//   return useApiMutation<{}, BulkDeleteAgentBuilderReq>({
//     method: 'DELETE',
//     url: '/agent/builder/bulk',
//     ...options,
//   });
// };

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
export const useUpdateAgentInfo = (options?: ApiMutationOptions<any, { graphUuid: string; name: string; description: string }>) => {
  return useApiMutation<any, { graphUuid: string; name: string; description: string }>({
    method: 'PUT',
    url: '/agent/builder/graphs/{graphUuid}/info',
    ...options,
    timeout: 60000,
  });
};

// 템플릿 목록 조회 (plain service)
export const getBuilderTemplates = async (): Promise<any> => {
  const response = await api.get('/agent/builder/templates');
  return response as any;
};

// 템플릿 목록 조회 훅 (useQuery + service 함수 사용)
export const useGetBuilderTemplates = (options?: any) => {
  return useApiQuery({
    queryKey: ['builderTemplates'],
    url: `/agent/builder/templates`,
    ...(options ?? {}),
  });
};

export class TemplateService {
  /**
   * 템플릿 목록을 가져오는 메서드
   * @returns 템플릿 목록
   */
  static async getTemplates(): Promise<Template[]> {
    try {
      const response: any = await getBuilderTemplates();

      // 응답 구조 분석
      const hasData = response && response.data;
      const hasDataData = hasData && response.data.data;
      const hasTemplates = hasDataData && response.data.data.templates;
      const isTemplatesArray = hasTemplates && Array.isArray(response.data.data.templates);

      if (isTemplatesArray) {
        const templates = response.data.data.templates;

        // 템플릿 데이터 검증 및 매핑
        const validTemplates = templates.filter((template: any) => {
          const hasId = template.template_id;
          const hasName = template.name;
          const isValid = hasId && hasName;
          return isValid;
        });

        // 템플릿 데이터를 프론트엔드 형식으로 매핑
        const mappedTemplates = validTemplates.map((template: any) => {
          const mappedTemplate = {
            icon: this.getTemplateIcon(template.name),
            template_id: template.template_id,
            template_name: template.name,
            template_description: template.description,
            category: template.category,
            version: template.version,
            tags: template.tags,
            created_at: template.created_at,
            usage_count: template.usage_count,
          };

          return mappedTemplate;
        });

        return mappedTemplates;
      } else {
        return [];
      }
    } catch (error) {
      return [];
    }
  }

  /**
   * 템플릿 이름에 따른 아이콘 반환
   * @param templateName 템플릿 이름
   * @returns 아이콘 경로
   */
  static getTemplateIcon(templateName: string): string {
    const iconMap: Record<string, string> = {
      Chatbot: '/assets/images/templates/chatbot.svg',
      RAG: '/assets/images/templates/rag.svg',
      Translator: '/assets/images/templates/translator.svg',
      'Plan And Execute': '/assets/images/templates/plan-execute.svg',
      'Simple RAG': '/assets/images/templates/simple-rag.svg',
    };

    return iconMap[templateName] || '/assets/images/templates/default.svg';
  }

  /**
   * 특정 템플릿의 상세 정보 가져오기
   * @param templateId 템플릿 ID
   * @returns 템플릿 상세 정보
   */
  static async getTemplateDetail(templateId: string): Promise<any> {
    try {
      const response = await api.get(`/agent/builder/templates/${templateId}`);

      if (response?.data?.success && response?.data?.data) {
        return response.data.data;
      } else {
        return null;
      }
    } catch (error) {
      return null;
    }
  }

  /**
   * 템플릿별 기본 노드 구조 생성 (API에서 받은 실제 데이터 사용)
   * @param templateData 템플릿 데이터
   * @returns 템플릿 구조
   */
  static getTemplateStructure(templateData: any): TemplateStructure {
    if (!templateData) {
      return { nodes: [], edges: [] };
    }

    // API에서 받은 실제 nodes와 edges 데이터 사용
    const nodes = templateData.nodes || [];
    const edges = templateData.edges || [];

    return {
      nodes: nodes,
      edges: edges,
    };
  }
}

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
 * Lineage 삭제 (모델 연결 끊기)
 */
export const useDeleteLineage = (options?: ApiMutationOptions<string, { sourceKey: string }>) => {
  return useApiMutation<string, { sourceKey: string }>({
    method: 'DELETE',
    url: '/lineage/{sourceKey}',
    ...options,
  });
};

/**
 * 에이전트 그래프 스트리밍 실행 (SSE 처리) - useStreamAgentDeploy와 동일한 구조
 * @param options mutation 옵션
 * @returns useMutation 훅
 */
export const useStreamAgentGraph = (options?: any) => {
  return useMutation<SuccessResponse<string>, ErrorResponse, { graph_id: string; input_data: any; [key: string]: any }>({
    mutationKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    mutationFn: async request => {
      // 기존 axios 인스턴스 사용 (CORS 처리됨)
      const response = await api.post(`/agent/builder/graphs/stream`, request, {
        headers: {
          Accept: 'text/event-stream',
        },
        responseType: 'text', // 텍스트 응답으로 처리
        timeout: 240000, // 4분으로 증가 (스트리밍 요청용)
      });

      return response.data;
    },
    ...options,
  });
};

/**
 * 에이전트 그래프 스트리밍 실행 (레거시 함수 - 실시간 SSE 스트리밍)
 * useStreamingChat.ts에서 사용 중이므로 유지
 * @param request 스트리밍 요청 데이터
 * @param onChunk 청크 단위로 데이터를 받을 콜백 함수
 * @returns 전체 응답 문자열 (콜백을 사용하지 않을 경우)
 */
export const streamAgentGraph = async (request: { graph_id: string; input_data: any; [key: string]: any }, onChunk?: (chunk: string) => void): Promise<string> => {
  // 🔥 스트리밍 요청 전 토큰 만료 체크 및 갱신 (멀티턴 대화 시 401 방지)
  let token = authUtils.getAccessToken();
  if (token) {
    const isExpired = authUtils.isAccessTokenExpired();
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

  // 타임아웃 설정 (4분 = 240초) - useStreamAgentDeploy와 동일하게
  const abortController = new AbortController();
  const timeoutId = setTimeout(() => {
    abortController.abort();
  }, 240000); // 4분 = 240000ms
  
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
      signal: abortController.signal,
    });

    clearTimeout(timeoutId);
  } catch (networkError: any) {
    // 타임아웃 타이머 정리
    clearTimeout(timeoutId);
    
    // 타임아웃 에러 처리
    if (networkError?.name === 'AbortError') {
      const error = new Error('요청 시간이 초과되었습니다. (4분)') as any;
      error.isNetworkError = true;
      error.isTimeout = true;
      error.originalError = networkError;
      throw error;
    }
    
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
  let lastDataTime = Date.now(); // 마지막 데이터 수신 시간 추적

  // 실시간으로 청크 단위로 데이터 읽기
  try {
    while (true) {
      let readResult: ReadableStreamReadResult<Uint8Array>;
      
      try {
        // 🔥 프록시 타임아웃 방지: read()에 타임아웃 추가 (20초로 짧게 설정하여 프록시 타임아웃 전에 재시도)
        const readPromise = reader.read();
        let timeoutId: NodeJS.Timeout | null = null;
        const timeoutPromise = new Promise<ReadableStreamReadResult<Uint8Array>>((_, reject) => {
          timeoutId = setTimeout(() => {
            reject(new Error('Read timeout'));
          }, 20000); // 20초 타임아웃 (프록시 30초 타임아웃보다 짧게)
        });

        try {
          readResult = await Promise.race([readPromise, timeoutPromise]);
          if (timeoutId) clearTimeout(timeoutId);
        } catch (raceError) {
          if (timeoutId) clearTimeout(timeoutId);
          throw raceError;
        }
      } catch (readError: any) {
        // 타임아웃 에러인 경우 - keep-alive 신호가 올 수 있으므로 재시도
        if (readError?.message === 'Read timeout') {
          const timeSinceLastData = Date.now() - lastDataTime;
          
          // 4분 이상 데이터가 없으면 실제 타임아웃으로 간주
          if (timeSinceLastData > 240000) {
            const error = new Error('요청 시간이 초과되었습니다. (4분간 데이터 수신 없음)') as any;
            error.isNetworkError = true;
            error.isTimeout = true;
            throw error;
          }
          
          // keep-alive 대기 중이므로 계속 진행 (재시도)
          continue;
        }
        
        // 🔥 ERR_INCOMPLETE_CHUNKED_ENCODING 처리 - 프록시 타임아웃으로 인한 연결 끊김
        const isIncompleteChunked = 
          readError?.message?.includes('ERR_INCOMPLETE_CHUNKED_ENCODING') ||
          readError?.message?.includes('incomplete') ||
          readError?.message?.includes('chunked');
        
        const isNetworkError = 
          readError?.message?.toLowerCase().includes('network') ||
          readError?.message?.toLowerCase().includes('fetch') ||
          readError?.message?.toLowerCase().includes('connection') ||
          readError?.name === 'NetworkError' ||
          readError?.name === 'TypeError';

        // 🔥 프록시 타임아웃으로 인한 ERR_INCOMPLETE_CHUNKED_ENCODING인 경우
        if (isIncompleteChunked) {
          const timeSinceLastData = Date.now() - lastDataTime;
          
          // 4분 이내이고 데이터가 있으면 재시도 (프록시가 연결을 끊었지만 백엔드는 살아있을 수 있음)
          if (timeSinceLastData < 240000 && fullResponse.trim().length > 0) {
            // 짧은 대기 후 재시도 (백엔드 keep-alive 대기)
            await new Promise(resolve => setTimeout(resolve, 1000));
            continue;
          }
          
          // 이미 수집된 데이터가 있으면 반환 (부분 성공)
          if (fullResponse.trim().length > 0) {
            try {
              reader.releaseLock();
            } catch {
              // 무시
            }
            
            if (errorMessage) {
              const error = new Error(errorMessage) as any;
              error.isStreamError = true;
              error.isPartialResponse = true;
              error.partialResponse = fullResponse;
              throw error;
            }
            
            return fullResponse;
          }
          
          // 데이터가 없으면 빈 문자열 반환
          return fullResponse || '';
        }

        // 네트워크 에러 처리
        if (isNetworkError) {
          // 이미 수집된 데이터가 있으면 반환 (부분 성공)
          if (fullResponse.trim().length > 0) {
            try {
              reader.releaseLock();
            } catch {
              // 무시
            }
            
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
          
          return fullResponse || '';
        }

        // 기타 에러 처리
        if (fullResponse.trim().length > 0) {
          try {
            reader.releaseLock();
          } catch {
            // 무시
          }
          return fullResponse;
        }

        // reader 해제 시도
        try {
          reader.releaseLock();
        } catch {
          // 무시
        }

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

      // 🔥 데이터 수신 시간 업데이트
      lastDataTime = Date.now();

      const chunk = decoder.decode(value, { stream: true });
      
      // 🔥 keep-alive 신호 무시 (백엔드에서 15초마다 보내는 ": keep-alive\n")
      if (chunk.trim() === ': keep-alive' || chunk.trim() === 'keep-alive' || chunk.includes(': keep-alive')) {
        continue; // keep-alive 신호는 무시하고 계속 읽기
      }
      
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

export interface PhoenixProjectResponse {
  projectId?: string | null;
  enableAuth?: boolean;
  phoenixUrl?: string;
  // 참고: Phoenix는 쿠키 기반 인증을 사용하므로 URL 파라미터로 API Key를 전달하지 않습니다.
}

/**
 * Agent Graph Export (Python 코드 조회)
 */
export const exportAgentGraphCode = async (graphId: string, credentialType: string = 'token'): Promise<string | null> => {
  try {
    const response = await api.get<{ data: { data: string } }>(`/agent/builder/graphs/${graphId}/export/code`, {
      params: {
        credential_type: credentialType,
      },
    });
    // 응답 구조: { data: { data: "python code..." } }
    const codeData = response.data?.data;
    if (typeof codeData === 'string') {
      return codeData;
    }
    if (codeData && typeof codeData === 'object' && 'data' in codeData) {
      return (codeData as any).data || null;
    }
    return null;
  } catch (error) {
    return null;
  }
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
    return null;
  }
};
