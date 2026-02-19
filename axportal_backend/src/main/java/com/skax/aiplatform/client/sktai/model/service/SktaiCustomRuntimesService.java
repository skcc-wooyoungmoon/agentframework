package com.skax.aiplatform.client.sktai.model.service;

import com.skax.aiplatform.client.sktai.model.SktaiCustomRuntimesClient;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelCustomRuntimeCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelCustomRuntimeUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelCustomRuntimeRead;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Custom Runtimes 서비스
 * 
 * <p>SKTAI Custom Runtime API와의 통신을 담당하는 서비스 계층입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Custom Runtime 관리</strong>: 생성, 조회, 수정, 삭제</li>
 *   <li><strong>파일 업로드</strong>: 커스텀 코드 파일 업로드</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiCustomRuntimesService {

    private final SktaiCustomRuntimesClient sktaiCustomRuntimesClient;

    /**
     * Custom Runtime 생성
     * 
     * @param request Custom Runtime 생성 요청
     * @return 생성된 Custom Runtime 정보
     */
    public ModelCustomRuntimeRead createCustomRuntime(ModelCustomRuntimeCreate request) {
        log.debug("Custom Runtime 생성 요청 - modelId: {}", request.getModelId());
        
        try {
            ModelCustomRuntimeRead response = sktaiCustomRuntimesClient.createCustomRuntime(request);
            log.debug("Custom Runtime 생성 성공 - id: {}", response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("Custom Runtime 생성 실패 - modelId: {}", request.getModelId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Custom Runtime 생성 실패 - modelId: {}", request.getModelId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Custom Runtime 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model별 Custom Runtime 조회
     * 
     * @param modelId 모델 ID
     * @return Custom Runtime 정보
     */
    public ModelCustomRuntimeRead getCustomRuntimeByModel(String modelId) {
        log.debug("Custom Runtime 조회 요청 - modelId: {}", modelId);
        
        try {
            ModelCustomRuntimeRead response = sktaiCustomRuntimesClient.getCustomRuntimeByModel(modelId);
            log.debug("Custom Runtime 조회 성공 - id: {}", response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("Custom Runtime 조회 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("Custom Runtime 조회 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Custom Runtime 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model별 Custom Runtime 수정
     * 
     * @param modelId 모델 ID
     * @param request Custom Runtime 수정 요청
     * @return 수정된 Custom Runtime 정보
     */
    public ModelCustomRuntimeRead updateCustomRuntimeByModel(String modelId, ModelCustomRuntimeUpdate request) {
        log.debug("Custom Runtime 수정 요청 - modelId: {}", modelId);
        
        try {
            ModelCustomRuntimeRead response = sktaiCustomRuntimesClient.updateCustomRuntimeByModel(modelId, request);
            log.debug("Custom Runtime 수정 성공 - id: {}", response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("Custom Runtime 수정 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("Custom Runtime 수정 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Custom Runtime 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model별 Custom Runtime 삭제
     * 
     * @param modelId 모델 ID
     */
    public void deleteCustomRuntimeByModel(String modelId) {
        log.debug("Custom Runtime 삭제 요청 - modelId: {}", modelId);
        
        try {
            sktaiCustomRuntimesClient.deleteCustomRuntimeByModel(modelId);
            log.debug("Custom Runtime 삭제 성공 - modelId: {}", modelId);
        } catch (BusinessException e) {
            log.error("Custom Runtime 삭제 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("Custom Runtime 삭제 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Custom Runtime 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Custom Code 파일 업로드
     * 
     * @param file 업로드할 커스텀 코드 파일
     * @return 업로드 결과
     */
    public Object uploadCustomCodeFile(MultipartFile file) {
        log.debug("Custom Code 파일 업로드 요청 - fileName: {}", file.getOriginalFilename());
        
        try {
            Object response = sktaiCustomRuntimesClient.uploadCustomCodeFile(file);
            log.debug("Custom Code 파일 업로드 성공 - fileName: {}", file.getOriginalFilename());
            return response;
        } catch (BusinessException e) {
            log.error("Custom Code 파일 업로드 실패 - fileName: {}", file.getOriginalFilename(), e);
            throw e;
        } catch (Exception e) {
            log.error("Custom Code 파일 업로드 실패 - fileName: {}", file.getOriginalFilename(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Custom Code 파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }
}
