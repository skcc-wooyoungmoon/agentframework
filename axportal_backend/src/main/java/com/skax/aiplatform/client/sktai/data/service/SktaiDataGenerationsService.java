package com.skax.aiplatform.client.sktai.data.service;

import com.skax.aiplatform.client.sktai.data.SktaiDataGenerationsClient;
import com.skax.aiplatform.client.sktai.data.dto.request.GenerationCreate;
import com.skax.aiplatform.client.sktai.data.dto.response.Generation;
import com.skax.aiplatform.client.sktai.data.dto.response.GenerationList;
import com.skax.aiplatform.client.sktai.data.dto.response.GenerationDetail;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * SKTAI Data Generations Service
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiDataGenerationsService {
    
    private final SktaiDataGenerationsClient generationsClient;
    
    public GenerationList getGenerations(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Retrieving generations list - page: {}, size: {}", page, size);
        
        try {
            GenerationList result = generationsClient.getGenerations(page, size, sort, filter, search);
            log.info("Successfully retrieved {} generations", result.getData().size());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve generations list (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve generations list (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성 작업 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public Generation createGeneration(GenerationCreate request) {
        log.debug("Creating new generation: {}", request.getName());
        
        try {
            Generation result = generationsClient.createGeneration(request);
            log.info("Successfully created generation with ID: {}", result.getId());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to create generation (BusinessException) - name: {}, message: {}", request.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create generation (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성 작업 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    public GenerationDetail getGeneration(UUID generationId) {
        log.debug("Retrieving generation details for ID: {}", generationId);
        
        try {
            GenerationDetail result = generationsClient.getGeneration(generationId);
            log.info("Successfully retrieved generation details: {}", result.getName());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve generation details (BusinessException) - ID: {}, message: {}", generationId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve generation details (예상치 못한 오류) - ID: {}", generationId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성 작업 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public Generation updateGeneration(UUID generationId, GenerationCreate request) {
        log.debug("Updating generation ID: {}", generationId);
        
        try {
            Generation result = generationsClient.updateGeneration(generationId, request);
            log.info("Successfully updated generation ID: {}", generationId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update generation (BusinessException) - ID: {}, message: {}", generationId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update generation (예상치 못한 오류) - ID: {}", generationId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성 작업 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    public void deleteGeneration(UUID generationId) {
        log.debug("Deleting generation ID: {}", generationId);
        
        try {
            generationsClient.deleteGeneration(generationId);
            log.info("Successfully deleted generation ID: {}", generationId);
        } catch (BusinessException e) {
            log.error("Failed to delete generation (BusinessException) - ID: {}, message: {}", generationId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete generation (예상치 못한 오류) - ID: {}", generationId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성 작업 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
