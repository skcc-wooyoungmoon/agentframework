package com.skax.aiplatform.client.deepsecurity.service;

import com.skax.aiplatform.client.deepsecurity.DeepSecurityClient;
import com.skax.aiplatform.client.deepsecurity.dto.request.ScanRequest;
import com.skax.aiplatform.client.deepsecurity.dto.response.ScanResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * DeepSecurity Feign Service 구현체
 * 
 * <p>DeepSecurity API를 통한 모델 등록 및 보안 검증을 위한 Feign Service 구현체입니다.
 * Feign Client 호출을 캡슐화하고 비즈니스 로직을 제공합니다.</p>
 * 
 * @author system
 * @since 2025-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSecurityService {

    private final DeepSecurityClient deepSecurityClient;

    /**
     * DeepSecurity 모델 등록 요청
     * 
     * @param request DeepSecurity 요청
     * @return 등록 결과
     */
    public ScanResponse requestDeepSecurity(ScanRequest request) {
        log.info("DeepSecurity Feign Service - 모델 등록 요청: UID={}, Filename={}", 
                request.getUid(), request.getFilename());
        
        try {
            // 파라미터 검증 (request는 이미 사용되고 있으므로 null이 아님)
            if (request.getUid() == null || request.getUid().trim().isEmpty()) {
                log.error(">>> DeepSecurity 모델 등록 실패 - UID가 null 또는 빈 문자열입니다.");
                throw new IllegalArgumentException("UID는 필수입니다.");
            }
            
            if (request.getFilename() == null || request.getFilename().trim().isEmpty()) {
                log.error(">>> DeepSecurity 모델 등록 실패 - Filename이 null 또는 빈 문자열입니다.");
                throw new IllegalArgumentException("Filename은 필수입니다.");
            }
            
            ScanResponse response = deepSecurityClient.requestDeepSecurity(request);
            
            if (response == null) {
                log.error(">>> DeepSecurity 모델 등록 실패 - 응답이 null입니다: UID={}, Filename={}", 
                        request.getUid(), request.getFilename());
                throw new NullPointerException("DeepSecurity 응답을 받을 수 없습니다.");
            }
            
            log.info("DeepSecurity Feign Service - 모델 등록 성공: Model ID={}, Status={}", 
                    response.getModelId(), response.getStatus());
            
            return response;
            
        } catch (IllegalArgumentException e) {
            log.error(">>> DeepSecurity 모델 등록 실패 - 잘못된 파라미터: UID={}, Filename={}, error={}", 
                    request != null ? request.getUid() : "null", 
                    request != null ? request.getFilename() : "null", 
                    e.getMessage(), e);
            throw new RuntimeException("DeepSecurity 모델 등록 실패: 잘못된 파라미터입니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> DeepSecurity 모델 등록 실패 - 필수 데이터 null: UID={}, Filename={}, error={}", 
                    request != null ? request.getUid() : "null", 
                    request != null ? request.getFilename() : "null", 
                    e.getMessage(), e);
            throw new RuntimeException("DeepSecurity 모델 등록 실패: 필수 데이터를 찾을 수 없습니다.", e);
        } catch (feign.FeignException.FeignClientException e) {
            log.error(">>> DeepSecurity 모델 등록 실패 - Feign 클라이언트 오류 (4xx): UID={}, Filename={}, status={}, error={}", 
                    request != null ? request.getUid() : "null", 
                    request != null ? request.getFilename() : "null", 
                    e.status(), e.getMessage(), e);
            throw new RuntimeException("DeepSecurity 모델 등록 실패: 클라이언트 요청 오류입니다.", e);
        } catch (feign.FeignException.FeignServerException e) {
            log.error(">>> DeepSecurity 모델 등록 실패 - Feign 서버 오류 (5xx): UID={}, Filename={}, status={}, error={}", 
                    request != null ? request.getUid() : "null", 
                    request != null ? request.getFilename() : "null", 
                    e.status(), e.getMessage(), e);
            throw new RuntimeException("DeepSecurity 모델 등록 실패: 서버 오류가 발생했습니다.", e);
        } catch (feign.FeignException e) {
            log.error(">>> DeepSecurity 모델 등록 실패 - Feign 통신 오류: UID={}, Filename={}, error={}", 
                    request != null ? request.getUid() : "null", 
                    request != null ? request.getFilename() : "null", 
                    e.getMessage(), e);
            throw new RuntimeException("DeepSecurity 모델 등록 실패: 외부 API 통신 오류입니다.", e);
        } catch (Exception e) {
            log.error("=== DeepSecurity 모델 등록 실패 ===");
            log.error("UID: {}, Filename: {}", 
                    request != null ? request.getUid() : "null", 
                    request != null ? request.getFilename() : "null");
            log.error("예외 타입: {}", e.getClass().getName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("전체 스택 트레이스:", e);
            throw new RuntimeException("DeepSecurity 모델 등록 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

}
