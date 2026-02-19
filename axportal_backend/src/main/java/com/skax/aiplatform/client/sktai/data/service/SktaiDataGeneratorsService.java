package com.skax.aiplatform.client.sktai.data.service;

import com.skax.aiplatform.client.sktai.data.SktaiDataGeneratorsClient;
import com.skax.aiplatform.client.sktai.data.dto.request.GeneratorCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.GeneratorUpdate;
import com.skax.aiplatform.client.sktai.data.dto.response.Generator;
import com.skax.aiplatform.client.sktai.data.dto.response.GeneratorList;
import com.skax.aiplatform.client.sktai.data.dto.response.GeneratorDetail;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiDataGeneratorsService {
    
    private final SktaiDataGeneratorsClient generatorsClient;
    
    public GeneratorList getGenerators(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Retrieving generators list - page: {}, size: {}", page, size);
        
        try {
            GeneratorList result = generatorsClient.getGenerators(page, size, sort, filter, search);
            log.info("Successfully retrieved {} generators", result.getData().size());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve generators list (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve generators list (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성기 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public Generator createGenerator(GeneratorCreate request) {
        log.debug("Creating new generator: {}", request.getName());
        
        try {
            Generator result = generatorsClient.createGenerator(request);
            log.info("Successfully created generator with ID: {}", result.getId());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to create generator (BusinessException) - name: {}, message: {}", request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Failed to create generator (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성기 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    public GeneratorDetail getGenerator(String generatorId) {
        log.debug("Retrieving generator details for ID: {}", generatorId);
        
        try {
            GeneratorDetail result = generatorsClient.getGenerator(generatorId);
            log.info("Successfully retrieved generator details: {}", result.getName());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve generator details (BusinessException) - ID: {}, message: {}", generatorId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Failed to retrieve generator details (예상치 못한 오류) - ID: {}", generatorId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성기 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public Generator updateGenerator(String generatorId, GeneratorUpdate request) {
        log.debug("Updating generator ID: {}", generatorId);
        
        try {
            Generator result = generatorsClient.updateGenerator(generatorId, request);
            log.info("Successfully updated generator ID: {}", generatorId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update generator (BusinessException) - ID: {}, message: {}", generatorId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Failed to update generator (예상치 못한 오류) - ID: {}", generatorId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성기 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    public void deleteGenerator(String generatorId) {
        log.debug("Deleting generator ID: {}", generatorId);
        
        try {
            generatorsClient.deleteGenerator(generatorId);
            log.info("Successfully deleted generator ID: {}", generatorId);
        } catch (BusinessException e) {
            log.error("Failed to delete generator (BusinessException) - ID: {}, message: {}", generatorId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Failed to delete generator (예상치 못한 오류) - ID: {}", generatorId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "생성기 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
