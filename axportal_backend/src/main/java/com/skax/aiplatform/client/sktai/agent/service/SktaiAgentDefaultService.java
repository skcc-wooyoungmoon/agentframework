package com.skax.aiplatform.client.sktai.agent.service;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentDefaultClient;
import com.skax.aiplatform.client.sktai.agent.dto.response.DefaultStatusResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.DefaultInfoResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.TestTracingResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ProfileFilesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ProfileFileDeleteResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Agent Default API 서비스
 * 
 * <p>SKTAI Agent 시스템의 기본 정보, 상태 조회, 테스트 추적, 프로파일 관리 등의
 * 비즈니스 로직을 처리하는 서비스 클래스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentDefaultService {
    
    private final SktaiAgentDefaultClient sktaiAgentDefaultClient;
    
    /**
     * 시스템 상태 조회
     * 
     * @return 시스템 상태 정보
     */
    public DefaultStatusResponse getSystemStatus() {
        try {
            log.debug("시스템 상태 조회 요청");
            DefaultStatusResponse response = sktaiAgentDefaultClient.getSystemStatus();
            log.debug("시스템 상태 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("시스템 상태 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("시스템 상태 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "시스템 상태 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 시스템 정보 조회
     * 
     * @return 시스템 정보
     */
    public DefaultInfoResponse getSystemInfo() {
        try {
            log.debug("시스템 정보 조회 요청");
            DefaultInfoResponse response = sktaiAgentDefaultClient.getSystemInfo();
            log.debug("시스템 정보 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("시스템 정보 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("시스템 정보 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "시스템 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 테스트 추적 조회
     * 
     * @param clientId 클라이언트 식별자
     * @param appId 애플리케이션 식별자
     * @return 테스트 추적 정보
     */
    public TestTracingResponse getTestTracing(String clientId, String appId) {
        try {
            log.debug("테스트 추적 조회 요청 - clientId: {}, appId: {}", clientId, appId);
            TestTracingResponse response = sktaiAgentDefaultClient.getTestTracing(clientId, appId);
            log.debug("테스트 추적 조회 성공 - clientId: {}, appId: {}", clientId, appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("테스트 추적 조회 실패 (BusinessException) - clientId: {}, appId: {}, message: {}", 
                    clientId, appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("테스트 추적 조회 실패 - clientId: {}, appId: {}", clientId, appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "테스트 추적 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로파일 파일 목록 조회
     * 
     * @return 프로파일 파일 목록
     */
    public ProfileFilesResponse getProfileFiles() {
        try {
            log.debug("프로파일 파일 목록 조회 요청");
            ProfileFilesResponse response = sktaiAgentDefaultClient.getProfileFiles();
            log.debug("프로파일 파일 목록 조회 성공 - 파일 수: {}", 
                response.getTotalCount() != null ? response.getTotalCount() : 0);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("프로파일 파일 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("프로파일 파일 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로파일 파일 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로파일 파일 삭제
     * 
     * @param filename 삭제할 파일명
     * @return 삭제 결과 정보
     */
    public ProfileFileDeleteResponse deleteProfileFile(String filename) {
        try {
            log.debug("프로파일 파일 삭제 요청 - filename: {}", filename);
            ProfileFileDeleteResponse response = sktaiAgentDefaultClient.deleteProfileFile(filename);
            log.debug("프로파일 파일 삭제 완료 - filename: {}, 성공 여부: {}", 
                filename, response.getSuccess());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("프로파일 파일 삭제 실패 (BusinessException) - filename: {}, message: {}", 
                    filename, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("프로파일 파일 삭제 실패 - filename: {}", filename, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로파일 파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
