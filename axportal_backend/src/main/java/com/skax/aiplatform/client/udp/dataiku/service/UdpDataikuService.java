package com.skax.aiplatform.client.udp.dataiku.service;

import com.skax.aiplatform.client.udp.dataiku.UdpDataikuClient;
import com.skax.aiplatform.client.udp.dataiku.config.UdpDataikuProperties;
import com.skax.aiplatform.client.udp.dataiku.dto.request.DataikuExecutionRequest;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuExecutionResponse;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuStatusResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

// no collection utilities needed

/**
 * UDP Dataiku API 서비스
 *
 * <p>UDP Dataiku 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * Dataiku 시나리오 실행 및 상태 조회 관련 API에 대한 서비스 메서드를 제공합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UdpDataikuService {

    private final UdpDataikuClient udpDataikuClient;
    private final UdpDataikuProperties dataikuProperties;
    private final ObjectMapper objectMapper;

    @Value("${udp.api.auth.authrization-bearer-token:}")
    private String udpAuthorizationBearerToken;

    @Value("${udp.api.auth.dataiku-run-key:}")
    private String dataikuRunKey;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int MIN_DELAY_SECONDS = 3;
    private static final int MAX_DELAY_SECONDS = 10;

    /**
     * 공통 예외 처리 메서드
     *
     * <p>외부 API 호출 시 발생하는 예외를 일관된 방식으로 처리합니다.</p>
     *
     * @param operation 작업 설명 (예: "Dataiku 실행", "Dataiku 시나리오 상태 조회" 등)
     * @param e         발생한 예외
     * @return 변환된 비즈니스 예외 (항상 BusinessException)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            // ErrorDecoder에서 변환된 BusinessException (HTTP 응답이 있는 경우: 400, 401, 403, 404, 422, 500 등)
            log.error("❌ UDP Dataiku {} 중 BusinessException 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            // HTTP 응답이 없는 경우 (연결 실패, 타임아웃 등) 또는 ErrorDecoder를 거치지 않은 FeignException
            // FeignException의 상세 정보(status, content, request)를 활용할 수 있음
            FeignException feignEx = (FeignException) e;
            log.error("❌ UDP Dataiku {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}",
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    String.format("UDP Dataiku API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(), feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            // 기타 런타임 예외
            log.error("❌ UDP Dataiku {} 중 런타임 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "UDP Dataiku API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ UDP Dataiku {} 중 예상치 못한 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "UDP Dataiku API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public synchronized DataikuExecutionResponse executeDataiku(DataikuExecutionRequest request) {
        try {
            String bearerToken = udpAuthorizationBearerToken;
            String authorizationHeader = (bearerToken == null || bearerToken.isBlank()) ? null : "Bearer " + bearerToken;
            String apiKey = dataikuRunKey;
            String environment = dataikuProperties.getEnvironment();
            String projectKey = dataikuProperties.getProjectKey();
            String scenarioId = dataikuProperties.getScenarioId();

            log.info(">>> [Dataiku 실행] - env: {}, projectKey: {}, scenarioId: {}", environment, projectKey, scenarioId);
            log.info(">>> [Dataiku 실행] 요청 파라미터\n{}", request);

            DataikuExecutionResponse response = udpDataikuClient.executeScenario(
                    authorizationHeader,
                    apiKey,
                    environment,
                    projectKey,
                    scenarioId,
                    request == null ? new DataikuExecutionRequest(java.util.Collections.emptyMap()) : request
            );

            if (response != null && response.getBody() != null) {
                Object oCancelled = response.getBody().get("cancelled");
                boolean cancelled = (oCancelled instanceof Boolean) ? (boolean) oCancelled : true;

                if (cancelled) {
                    response = retryExecuteDataiku(
                            authorizationHeader,
                            apiKey,
                            environment,
                            projectKey,
                            scenarioId,
                            request
                    );
                }
            }

            log.info(">>> [Dataiku 실행] 실행 성공 - 결과\n{}", response);
            return response;
        } catch (BusinessException e) {
            throw handleException("Dataiku 실행", e);
        } catch (FeignException e) {
            throw handleException("Dataiku 실행", e);
        } catch (RuntimeException e) {
            throw handleException("Dataiku 실행", e);
        } catch (Exception e) {
            throw handleException("Dataiku 실행", e);
        }
    }

    /**
     * Dataiku 실행 재시도 로직
     *
     * <p>cancelled가 true인 경우 최대 3회 재시도합니다.
     * 각 재시도 전에 3~10초 랜덤 대기 시간을 가집니다.</p>
     *
     * @param authorizationHeader 인증 헤더
     * @param apiKey API 키
     * @param environment 환경
     * @param projectKey 프로젝트 키
     * @param scenarioId 시나리오 ID
     * @param request 실행 요청
     * @return Dataiku 실행 응답
     * @throws BusinessException 3회 재시도 후에도 cancelled가 true인 경우
     */
    private DataikuExecutionResponse retryExecuteDataiku(
            String authorizationHeader,
            String apiKey,
            String environment,
            String projectKey,
            String scenarioId,
            DataikuExecutionRequest request) {
        
        DataikuExecutionResponse lastResponse = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                // 3~10초 랜덤 대기 (SecureRandom 사용)
                int delaySeconds = MIN_DELAY_SECONDS + secureRandom.nextInt(MAX_DELAY_SECONDS - MIN_DELAY_SECONDS + 1);
                log.info(">>> [Dataiku 재시도] {}회차 시도 - {}초 대기 후 재시도", attempt, delaySeconds);
                
                // 비동기 지연 처리 (Thread.sleep 대신 CompletableFuture 사용)
                CompletableFuture.runAsync(() -> {}, 
                        CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS))
                        .join();
                
                // 재시도 실행
                lastResponse = udpDataikuClient.executeScenario(
                        authorizationHeader,
                        apiKey,
                        environment,
                        projectKey,
                        scenarioId,
                        request == null ? new DataikuExecutionRequest(java.util.Collections.emptyMap()) : request
                );
                
                // cancelled 확인
                if (lastResponse != null && lastResponse.getBody() != null) {
                    Object oCancelled = lastResponse.getBody().get("cancelled");
                    boolean cancelled = (oCancelled instanceof Boolean) ? (boolean) oCancelled : true;
                    
                    if (!cancelled) {
                        log.info(">>> [Dataiku 재시도] {}회차 시도 성공 - cancelled: false", attempt);
                        return lastResponse;
                    } else {
                        log.warn(">>> [Dataiku 재시도] {}회차 시도 실패 - cancelled: true", attempt);
                    }
                }
                
            } catch (Exception e) {
                log.warn(">>> [Dataiku 재시도] {}회차 시도 중 예외 발생: {}", attempt, e.getMessage(), e);
                // 예외 발생 시에도 다음 재시도 계속 진행
            }
        }
        
        // 3회 재시도 후에도 cancelled가 true인 경우 예외 발생
        log.error(">>> [Dataiku 재시도] 최대 재시도 횟수({}회) 초과 - 모든 시도에서 cancelled가 true였습니다", MAX_RETRY_ATTEMPTS);
        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                String.format("[Dataiku] 초기적재 시나리오 실행에 실패했습니다. 관리자에게 문의해주세요."));
    }

    public DataikuStatusResponse getScenarioStatus(String authorization, String runId) {
        try {
            log.info("🟠 UDP Dataiku 시나리오 상태 조회 요청 - runId: {}", runId);
            DataikuStatusResponse response = udpDataikuClient.getScenarioStatus(authorization, runId);
            log.info("🟠 UDP Dataiku 시나리오 상태 조회 성공 - runId: {}, status: {}, progress: {}%",
                    runId, response.getStatus(), response.getProgress());
            return response;
        } catch (BusinessException e) {
            throw handleException("Dataiku 시나리오 상태 조회", e);
        } catch (FeignException e) {
            throw handleException("Dataiku 시나리오 상태 조회", e);
        } catch (RuntimeException e) {
            throw handleException("Dataiku 시나리오 상태 조회", e);
        } catch (Exception e) {
            throw handleException("Dataiku 시나리오 상태 조회", e);
        }
    }

    /**
     * Dataiku 시나리오 실행 (시나리오 ID 지정)
     *
     * @param scenarioId 실행할 시나리오 ID
     * @param request    Dataiku 실행 요청
     * @return Dataiku 실행 응답
     */
    public synchronized DataikuExecutionResponse executeDataikuWithScenario(String scenarioId, DataikuExecutionRequest request) {
        try {
            String bearerToken = udpAuthorizationBearerToken;
            String authorizationHeader = (bearerToken == null || bearerToken.isBlank()) ? null : "Bearer " + bearerToken;
            String apiKey = dataikuRunKey;
            String environment = dataikuProperties.getEnvironment();
            String projectKey = dataikuProperties.getProjectKey();

            log.info(">>> [Dataiku 실행] - env: {}, projectKey: {}, scenarioId: {}", environment, projectKey, scenarioId);
            log.info(">>> [Dataiku 실행] 요청 파라미터\n{}",
                    objectMapper.valueToTree(request != null ? request.getBody() : java.util.Collections.emptyMap())
                            .toPrettyString());

            DataikuExecutionResponse response = udpDataikuClient.executeScenario(
                    authorizationHeader,
                    apiKey,
                    environment,
                    projectKey,
                    scenarioId,
                    request == null ? new DataikuExecutionRequest(java.util.Collections.emptyMap()) : request
            );

            if (response != null && response.getBody() != null) {
                Object oCancelled = response.getBody().get("cancelled");
                boolean cancelled = (oCancelled instanceof Boolean) ? (boolean) oCancelled : true;

                if (cancelled) {
                    response = retryExecuteDataiku(
                            authorizationHeader,
                            apiKey,
                            environment,
                            projectKey,
                            scenarioId,
                            request
                    );
                }
            }

            log.info(">>> [Dataiku 실행] 실행 성공 - 결과\n{}", response);
            return response;
        } catch (BusinessException e) {
            throw handleException("Dataiku 실행", e);
        } catch (FeignException e) {
            throw handleException("Dataiku 실행", e);
        } catch (RuntimeException e) {
            throw handleException("Dataiku 실행", e);
        } catch (Exception e) {
            throw handleException("Dataiku 실행", e);
        }
    }
}