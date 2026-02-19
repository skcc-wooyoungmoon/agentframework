package com.skax.aiplatform.client.sktai.model.service;

import com.skax.aiplatform.client.sktai.model.SktaiHealthClient;
import com.skax.aiplatform.client.sktai.model.dto.response.HealthResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Health 서비스
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiHealthService {

    private final SktaiHealthClient sktaiHealthClient;

    /**
     * Liveness Check
     */
    public HealthResponse livenessCheck() {
        log.debug("Liveness Check 요청");
        
        try {
            HealthResponse response = sktaiHealthClient.livenessCheck();
            log.debug("Liveness Check 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Liveness Check 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("Liveness Check 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Liveness Check에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Readiness Check
     */
    public HealthResponse readinessCheck() {
        log.debug("Readiness Check 요청");
        
        try {
            HealthResponse response = sktaiHealthClient.readinessCheck();
            log.debug("Readiness Check 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Readiness Check 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("Readiness Check 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Readiness Check에 실패했습니다: " + e.getMessage());
        }
    }
}
