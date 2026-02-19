package com.skax.aiplatform.client.sktai.agentgateway.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agentgateway.SktaiAgentGatewayClient;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.BatchRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.InvokeRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamLogRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.AppsResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.BatchResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.InputSchemaResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.InvokeResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.OutputSchemaResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.StreamLogResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.StreamResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Agent Gateway Service
 * 
 * <p>SKTAI Agent Gateway API 호출을 위한 비즈니스 로직 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 로직을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Agent Invocation</strong>: 에이전트 실행 및 응답 처리</li>
 *   <li><strong>Batch Processing</strong>: 일괄 처리 요청 및 결과 관리</li>
 *   <li><strong>Stream Processing</strong>: 실시간 스트리밍 처리</li>
 *   <li><strong>App Management</strong>: 사용 가능한 앱 정보 조회</li>
 *   <li><strong>Schema Validation</strong>: 입력/출력 스키마 검증</li>
 *   <li><strong>Error Handling</strong>: 외부 API 오류 처리</li>
 *   <li><strong>Monitoring</strong>: 요청/응답 로깅 및 모니터링</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>대화형 AI 서비스</strong>: 실시간 채팅 및 응답 처리</li>
 *   <li><strong>일괄 처리</strong>: 대량 데이터 처리 및 분석</li>
 *   <li><strong>실시간 스트리밍</strong>: 실시간 로그 및 이벤트 처리</li>
 *   <li><strong>스키마 검증</strong>: API 호출 전 데이터 유효성 검사</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiAgentGatewayService {
    
    private final SktaiAgentGatewayClient agentGatewayClient;
    
    // ==================== Agent Gateway Operations ====================
    
    /**
     * 에이전트 실행 (Invoke)
     * 
     * <p>지정된 에이전트를 실행하여 사용자 메시지에 대한 응답을 생성합니다.
     * 동기적 처리 방식으로 즉시 결과를 반환합니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>실시간 채팅 서비스</li>
     *   <li>질의응답 시스템</li>
     *   <li>대화형 AI 어시스턴트</li>
     * </ul>
     * 
     * @param agentId 실행할 에이전트 ID
     * @param request 에이전트 실행 요청 정보
     * @param routerPath 라우터 경로 (선택사항)
     * @return 에이전트 실행 결과
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public InvokeResponse invokeAgent(String agentId, InvokeRequest request, String routerPath) {
        log.info("에이전트 실행 요청 - agentId: {}, routerPath: {}", agentId, routerPath);
        
        try {
            // 요청 유효성 검사
            validateInvokeRequest(request);
            
            InvokeResponse response = agentGatewayClient.invoke(agentId, routerPath, request);
            log.info("에이전트 실행 성공 - agentId: {}", agentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 실행 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 실행 실패 (예상치 못한 오류) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 실행에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 에이전트 실행 (라우터 경로 없음)
     * 
     * @param agentId 실행할 에이전트 ID
     * @param request 에이전트 실행 요청 정보
     * @return 에이전트 실행 결과
     */
    public InvokeResponse invokeAgent(String agentId, InvokeRequest request) {
        return invokeAgent(agentId, request, "");
    }
    
    /**
     * 에이전트 일괄 실행 (Batch)
     * 
     * <p>여러 요청을 한 번에 처리하는 일괄 처리 방식입니다.
     * 대량 데이터 처리나 배치 작업에 적합합니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>대량 문서 분석</li>
     *   <li>배치 데이터 처리</li>
     *   <li>정기 보고서 생성</li>
     * </ul>
     * 
     * @param agentId 실행할 에이전트 ID
     * @param request 일괄 처리 요청 정보
     * @param routerPath 라우터 경로 (선택사항)
     * @return 일괄 처리 결과
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public BatchResponse batchProcess(String agentId, BatchRequest request, String routerPath) {
        log.info("에이전트 일괄 처리 요청 - agentId: {}, routerPath: {}", agentId, routerPath);
        
        try {
            // 요청 유효성 검사
            validateBatchRequest(request);
            
            BatchResponse response = agentGatewayClient.batch(agentId, routerPath, request);
            log.info("에이전트 일괄 처리 성공 - agentId: {}", agentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 일괄 처리 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 일괄 처리 실패 (예상치 못한 오류) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 일괄 처리에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 에이전트 일괄 실행 (라우터 경로 없음)
     * 
     * @param agentId 실행할 에이전트 ID
     * @param request 일괄 처리 요청 정보
     * @return 일괄 처리 결과
     */
    public BatchResponse batchProcess(String agentId, BatchRequest request) {
        return batchProcess(agentId, request, "");
    }
    
    /**
     * 에이전트 스트리밍 실행 (Stream)
     * 
     * <p>실시간 스트리밍 방식으로 에이전트를 실행합니다.
     * 응답을 즉시 스트림으로 받을 수 있어 실시간 상호작용에 적합합니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>실시간 채팅</li>
     *   <li>라이브 번역</li>
     *   <li>실시간 코드 생성</li>
     * </ul>
     * 
     * @param agentId 실행할 에이전트 ID
     * @param request 스트리밍 요청 정보
     * @param routerPath 라우터 경로 (선택사항)
     * @return 스트리밍 응답
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public StreamResponse streamAgent(String authorization, String agentId, StreamRequest request, String routerPath) {
        
        try {
            // 요청 유효성 검사
            validateStreamRequest(request);
            
            // routerPath가 빈 문자열이면 null로 변환
            String finalRouterPath = (routerPath != null && routerPath.trim().isEmpty()) ? null : routerPath;
            
            // 현재 사용자 memberId 가져오기 (aip-user 헤더용)
            String aipUser = getCurrentMemberId();

            String rawResponse = agentGatewayClient.streamRaw(authorization, aipUser, agentId, finalRouterPath, request);
            
            // SSE 응답을 StreamResponse로 파싱
            StreamResponse response = parseSSEResponse(rawResponse);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 스트리밍 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 스트리밍 실패 (예상치 못한 오류) - agentId: {}, error: {}", agentId, e.getMessage(), e);
            // 보안: 시스템 내부 정보가 포함된 예외 메시지를 사용자에게 노출하지 않음
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 스트리밍 처리 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 에이전트 스트리밍 실행 (라우터 경로 없음)
     * 
     * @param agentId 실행할 에이전트 ID
     * @param request 스트리밍 요청 정보
     * @return 스트리밍 응답
     */
    public StreamResponse streamAgent(String authorization,String agentId, StreamRequest request) {
        return streamAgent(authorization, agentId, request, "");
    }
    
    /**
     * 스트림 로그 조회 (Stream Log)
     * 
     * <p>실행 중인 스트림의 로그 정보를 조회합니다.
     * 스트리밍 작업의 진행 상황이나 오류 정보를 확인할 수 있습니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>스트리밍 작업 모니터링</li>
     *   <li>오류 진단 및 디버깅</li>
     *   <li>작업 진행률 추적</li>
     * </ul>
     * 
     * @param agentId 에이전트 ID
     * @param request 스트림 로그 요청 정보
     * @param routerPath 라우터 경로 (선택사항)
     * @return 스트림 로그 응답
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public StreamLogResponse getStreamLog(String agentId, StreamLogRequest request, String routerPath) {
        log.info("스트림 로그 조회 요청 - agentId: {}, routerPath: {}", agentId, routerPath);
        
        try {
            StreamLogResponse response = agentGatewayClient.streamLog(agentId, routerPath, request);
            log.info("스트림 로그 조회 성공 - agentId: {}", agentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("스트림 로그 조회 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("스트림 로그 조회 실패 (예상치 못한 오류) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "스트림 로그 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 스트림 로그 조회 (라우터 경로 없음)
     * 
     * @param agentId 에이전트 ID
     * @param request 스트림 로그 요청 정보
     * @return 스트림 로그 응답
     */
    public StreamLogResponse getStreamLog(String agentId, StreamLogRequest request) {
        return getStreamLog(agentId, request, "");
    }
    
    // ==================== App Information ====================
    
    /**
     * 사용 가능한 앱 목록 조회
     * 
     * <p>SKTAI Agent Gateway에서 사용할 수 있는 앱들의 목록을 조회합니다.
     * 각 앱의 기본 정보와 사용 가능 여부를 확인할 수 있습니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>사용 가능한 에이전트 조회</li>
     *   <li>앱 카탈로그 구성</li>
     *   <li>서비스 설정 및 구성</li>
     * </ul>
     * 
     * @return 앱 목록 응답
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public AppsResponse getApps() {
        log.info("앱 목록 조회 요청");
        
        try {
            AppsResponse response = agentGatewayClient.getApps();
            log.info("앱 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("앱 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("앱 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "앱 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Schema Information ====================
    
    /**
     * 입력 스키마 조회
     * 
     * <p>지정된 에이전트의 입력 데이터 스키마를 조회합니다.
     * API 호출 전 입력 데이터의 유효성을 검증할 수 있습니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>API 호출 전 데이터 검증</li>
     *   <li>폼 생성 및 검증</li>
     *   <li>클라이언트 사이드 유효성 검사</li>
     * </ul>
     * 
     * @param agentId 에이전트 ID
     * @param routerPath 라우터 경로 (선택사항)
     * @return 입력 스키마 응답
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public InputSchemaResponse getInputSchema(String agentId, String routerPath) {
        log.info("입력 스키마 조회 요청 - agentId: {}, routerPath: {}", agentId, routerPath);
        
        try {
            InputSchemaResponse response = agentGatewayClient.getInputSchema(agentId, routerPath);
            log.info("입력 스키마 조회 성공 - agentId: {}", agentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("입력 스키마 조회 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("입력 스키마 조회 실패 (예상치 못한 오류) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "입력 스키마 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 입력 스키마 조회 (라우터 경로 없음)
     * 
     * @param agentId 에이전트 ID
     * @return 입력 스키마 응답
     */
    public InputSchemaResponse getInputSchema(String agentId) {
        return getInputSchema(agentId, "");
    }
    
    /**
     * 출력 스키마 조회
     * 
     * <p>지정된 에이전트의 출력 데이터 스키마를 조회합니다.
     * 응답 데이터의 구조를 미리 파악하여 후처리를 준비할 수 있습니다.</p>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li>응답 데이터 파싱 준비</li>
     *   <li>UI 컴포넌트 구성</li>
     *   <li>데이터 변환 로직 구현</li>
     * </ul>
     * 
     * @param agentId 에이전트 ID
     * @param routerPath 라우터 경로 (선택사항)
     * @return 출력 스키마 응답
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public OutputSchemaResponse getOutputSchema(String agentId, String routerPath) {
        log.info("출력 스키마 조회 요청 - agentId: {}, routerPath: {}", agentId, routerPath);
        
        try {
            OutputSchemaResponse response = agentGatewayClient.getOutputSchema(agentId, routerPath);
            log.info("출력 스키마 조회 성공 - agentId: {}", agentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("출력 스키마 조회 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("출력 스키마 조회 실패 (예상치 못한 오류) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "출력 스키마 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 출력 스키마 조회 (라우터 경로 없음)
     * 
     * @param agentId 에이전트 ID
     * @return 출력 스키마 응답
     */
    public OutputSchemaResponse getOutputSchema(String agentId) {
        return getOutputSchema(agentId, "");
    }
    
    // ==================== Validation Methods ====================
    
    /**
     * Invoke 요청 유효성 검사
     * 
     * @param request 검사할 요청
     * @throws BusinessException 유효성 검사 실패 시
     */
    private void validateInvokeRequest(InvokeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청 정보가 없습니다.");
        }
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "메시지가 없습니다.");
        }
    }
    
    /**
     * Batch 요청 유효성 검사
     * 
     * @param request 검사할 요청
     * @throws BusinessException 유효성 검사 실패 시
     */
    private void validateBatchRequest(BatchRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "일괄 처리 요청 정보가 없습니다.");
        }
        // 추가 일괄 처리 관련 유효성 검사 로직
    }
    
    /**
     * Stream 요청 유효성 검사
     * 
     * @param request 검사할 요청
     * @throws BusinessException 유효성 검사 실패 시
     */
    private void validateStreamRequest(StreamRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "스트리밍 요청 정보가 없습니다.");
        }
        // 추가 스트리밍 관련 유효성 검사 로직
    }

    /**
     * 현재 사용자 memberId 조회
     * 
     * @return 현재 사용자 memberId (인증되지 않은 경우 null 반환)
     */
    private String getCurrentMemberId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                String memberId = authentication.getName();
                log.debug("현재 사용자 memberId 조회 성공: {}", memberId);
                return memberId;
            }
        } catch (SecurityException e) {
            log.warn("현재 사용자 정보를 가져올 수 없습니다 (SecurityException): {}", e.getMessage());
        } catch (Exception e) {
            log.warn("현재 사용자 정보를 가져올 수 없습니다: {}", e.getMessage());
        }
        log.debug("현재 사용자 정보 없음 - aip-user 헤더를 null로 설정");
        return "";
    }

    /**
     * SSE 응답을 StreamResponse로 파싱
     */
    private StreamResponse parseSSEResponse(String rawResponse) {
        try {
            
            // SSE 형식 파싱
            if (rawResponse != null && rawResponse.contains("event:")) {
                // 모든 data 라인을 찾아서 마지막 유효한 데이터를 사용
                String[] lines = rawResponse.split("\n");
                String lastValidData = null;
                
                for (String line : lines) {
                    if (line.startsWith("data: ")) {
                        String dataContent = line.substring(6); // "data: " 제거
                        
                        // 빈 데이터나 null이 아닌 경우만 유효한 데이터로 간주
                        if (!dataContent.equals("null") && !dataContent.trim().isEmpty()) {
                            lastValidData = dataContent;
                        }
                    }
                }
                
                if (lastValidData != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(lastValidData, StreamResponse.class);
                }
            }
            
            // 일반 JSON 응답인 경우
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(rawResponse, StreamResponse.class);
        } catch (NullPointerException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "응답 파싱에 실패했습니다: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "응답 파싱에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException re) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "응답 파싱에 실패했습니다: " + re.getMessage());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "응답 파싱에 실패했습니다: " + e.getMessage());
        }
    }

    public String streamAgentRaw(String authorization, String agentId, StreamRequest request, String routerPath) {
        
        try {
            // 요청 유효성 검사
            validateStreamRequest(request);
            
            // routerPath가 빈 문자열이면 null로 변환
            String finalRouterPath = (routerPath != null && routerPath.trim().isEmpty()) ? null : routerPath;
            
            // 현재 사용자 memberId 가져오기 (aip-user 헤더용)
            String aipUser = getCurrentMemberId();
            
            String rawResponse = agentGatewayClient.streamRaw(authorization, aipUser, agentId, finalRouterPath, request);
            
            return rawResponse;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 스트리밍 Raw 실패 (BusinessException) - agentId: {}, message: {}", 
                    agentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 스트리밍 Raw 실패 (예상치 못한 오류) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 스트리밍에 실패했습니다: " + e.getMessage());
        }
    }
}
