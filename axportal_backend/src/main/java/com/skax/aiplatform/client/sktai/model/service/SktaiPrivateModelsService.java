package com.skax.aiplatform.client.sktai.model.service;

import com.skax.aiplatform.client.sktai.model.SktaiPrivateModelsClient;
import com.skax.aiplatform.client.sktai.model.dto.request.DecryptModelRequest;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelUsageRequest;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Private Models 서비스
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiPrivateModelsService {

    private final SktaiPrivateModelsClient sktaiPrivateModelsClient;

    /**
     * Private Model Usage 상태 조회
     */
    public Object getPrivateModelUsageStatus(String modelId, String usageUuidPath) {
        log.debug("Private Model Usage 상태 조회 요청 - modelId: {}, usageUuidPath: {}", modelId, usageUuidPath);
        
        try {
            Object response = sktaiPrivateModelsClient.getPrivateModelUsageStatus(modelId, usageUuidPath);
            log.debug("Private Model Usage 상태 조회 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("Private Model Usage 상태 조회 실패 (BusinessException) - modelId: {}, message: {}", modelId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Private Model Usage 상태 조회 실패 (예상치 못한 오류) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Private Model Usage 상태 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Private Model Usage 기록 조회
     */
    public Object getPrivateModelUsageRecords(String modelId) {
        log.debug("Private Model Usage 기록 조회 요청 - modelId: {}", modelId);
        
        try {
            Object response = sktaiPrivateModelsClient.getPrivateModelUsageRecords(modelId);
            log.debug("Private Model Usage 기록 조회 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("Private Model Usage 기록 조회 실패 (BusinessException) - modelId: {}, message: {}", modelId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Private Model Usage 기록 조회 실패 (예상치 못한 오류) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Private Model Usage 기록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Private Model 복호화
     */
    public Object decryptPrivateModel(String modelId, DecryptModelRequest request) {
        log.debug("Private Model 복호화 요청 - modelId: {}, targetUuid: {}", modelId, request.getTargetUuid());
        
        try {
            Object response = sktaiPrivateModelsClient.decryptPrivateModel(modelId, request);
            log.debug("Private Model 복호화 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("Private Model 복호화 실패 (BusinessException) - modelId: {}, message: {}", modelId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Private Model 복호화 실패 (예상치 못한 오류) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Private Model 복호화에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model 사용 시작
     */
    public Object startModelUsage(String modelId, ModelUsageRequest request) {
        log.debug("Model 사용 시작 요청 - modelId: {}, usageUuidPath: {}", modelId, request.getUsageUuidPath());
        
        try {
            Object response = sktaiPrivateModelsClient.startModelUsage(modelId, request);
            log.debug("Model 사용 시작 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("Model 사용 시작 실패 (BusinessException) - modelId: {}, message: {}", modelId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model 사용 시작 실패 (예상치 못한 오류) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model 사용 시작에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model 사용 중지
     */
    public Object stopModelUsage(String modelId, ModelUsageRequest request) {
        log.debug("Model 사용 중지 요청 - modelId: {}, usageUuidPath: {}", modelId, request.getUsageUuidPath());
        
        try {
            Object response = sktaiPrivateModelsClient.stopModelUsage(modelId, request);
            log.debug("Model 사용 중지 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("Model 사용 중지 실패 (BusinessException) - modelId: {}, message: {}", modelId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model 사용 중지 실패 (예상치 못한 오류) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model 사용 중지에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 오래된 Private Model 정리
     */
    public Object cleanupOldUsages() {
        log.debug("오래된 Private Model 정리 요청");
        
        try {
            Object response = sktaiPrivateModelsClient.cleanupOldUsages();
            log.debug("오래된 Private Model 정리 성공");
            return response;
        } catch (BusinessException e) {
            log.error("오래된 Private Model 정리 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("오래된 Private Model 정리 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "오래된 Private Model 정리에 실패했습니다: " + e.getMessage());
        }
    }
}
