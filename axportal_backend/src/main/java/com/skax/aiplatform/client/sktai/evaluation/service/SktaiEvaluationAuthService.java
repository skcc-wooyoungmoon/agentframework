package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiEvaluationAuthClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.AuthorizeRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.AuthorizeResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Evaluation Auth Service
 * 
 * <p>SKTAI Evaluation API의 인증 관련 비즈니스 로직을 처리하는 서비스입니다.
 * OAuth2 기반 인증과 토큰 관리를 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>OAuth2 토큰 발급 및 갱신</li>
 *   <li>인증 요청 처리 및 응답 변환</li>
 *   <li>외부 API 호출 예외 처리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiEvaluationAuthService {
    
    private final SktaiEvaluationAuthClient sktaiEvaluationAuthClient;
    
    /**
     * 사용자 권한 인증
     * 
     * <p>제공된 사용자 정보와 리소스 접근 권한을 확인하여
     * SKTAI Evaluation API 접근 권한을 검증합니다.</p>
     * 
     * @param request 인증 요청 정보 (사용자 ID, 리소스, 액션)
     * @return 인증 결과 정보
     * @throws BusinessException 인증 실패 또는 외부 API 오류 시
     */
    public AuthorizeResponse authorize(AuthorizeRequest request) {
        log.info("SKTAI Evaluation 권한 인증 요청 - userId: {}, resource: {}, action: {}", 
                request.getUserId(), request.getResource(), request.getAction());
        
        try {
            AuthorizeResponse response = sktaiEvaluationAuthClient.authorize(request);
            log.info("SKTAI Evaluation 권한 인증 완료 - userId: {}, authorized: {}", 
                    request.getUserId(), response.getAuthorized());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 권한 인증 실패 (BusinessException) - userId: {}, resource: {}, message: {}", 
                    request.getUserId(), request.getResource(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 권한 인증 실패 (예상치 못한 오류) - userId: {}, resource: {}, error: {}", 
                    request.getUserId(), request.getResource(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "SKTAI Evaluation 권한 인증에 실패했습니다: " + e.getMessage());
        }
    }
}
