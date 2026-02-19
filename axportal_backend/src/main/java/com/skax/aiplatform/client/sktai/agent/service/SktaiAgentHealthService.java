package com.skax.aiplatform.client.sktai.agent.service;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentHealthClient;
import com.skax.aiplatform.client.sktai.agent.dto.response.CommonResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Agent Health API 서비스
 * 
 * <p>SKTAI Agent 시스템의 헬스 체크 API를 호출하는 비즈니스 로직을 담당합니다.
 * Feign Client를 래핑하여 편리한 메서드를 제공하고 로깅 및 예외 처리를 수행합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentHealthService {
    
    private final SktaiAgentHealthClient sktaiAgentHealthClient;
    
    /**
     * Agent 시스템 Liveness 체크
     * 
     * @return 응답 메시지
     */
    public CommonResponse checkLiveness() {
        try {
            log.debug("Agent 시스템 Liveness 체크 요청");
            CommonResponse response = sktaiAgentHealthClient.checkLiveness();
            log.debug("Agent 시스템 Liveness 체크 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent 시스템 Liveness 체크 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent 시스템 Liveness 체크 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent Liveness 체크에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent 시스템 Readiness 체크
     * 
     * @return 응답 메시지
     */
    public CommonResponse checkReadiness() {
        try {
            log.debug("Agent 시스템 Readiness 체크 요청");
            CommonResponse response = sktaiAgentHealthClient.checkReadiness();
            log.debug("Agent 시스템 Readiness 체크 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent 시스템 Readiness 체크 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent 시스템 Readiness 체크 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent Readiness 체크에 실패했습니다: " + e.getMessage());
        }
    }
}
