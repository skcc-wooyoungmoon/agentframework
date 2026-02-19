package com.skax.aiplatform.client.sktai.data.service;

import com.skax.aiplatform.client.sktai.data.SktaiDataProcessorsClient;
import com.skax.aiplatform.client.sktai.data.dto.request.ProcessorCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.ProcessorUpdate;
import com.skax.aiplatform.client.sktai.data.dto.response.Processor;
import com.skax.aiplatform.client.sktai.data.dto.response.ProcessorList;
import com.skax.aiplatform.client.sktai.data.dto.response.ProcessorDetail;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiDataProcessorsService {
    
    private final SktaiDataProcessorsClient processorsClient;
    
    public ProcessorList getProcessors(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Retrieving processors list - page: {}, size: {}", page, size);
        
        try {
            ProcessorList result = processorsClient.getProcessors(page, size, sort, filter, search);
            log.info("Successfully retrieved {} processors", result.getData().size());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve processors list (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve processors list (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로세서 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public Processor createProcessor(ProcessorCreate request) {
        log.debug("Creating new processor: {}", request.getName());
        
        try {
            Processor result = processorsClient.createProcessor(request);
            log.info("Successfully created processor with ID: {}", result.getId());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to create processor (BusinessException) - name: {}, message: {}", request.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create processor (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로세서 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    public ProcessorDetail getProcessor(UUID processorId) {
        log.debug("Retrieving processor details for ID: {}", processorId);
        
        try {
            ProcessorDetail result = processorsClient.getProcessor(processorId);
            log.info("Successfully retrieved processor details: {}", result.getName());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve processor details (BusinessException) - ID: {}, message: {}", processorId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve processor details (예상치 못한 오류) - ID: {}", processorId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로세서 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public Processor updateProcessor(UUID processorId, ProcessorUpdate request) {
        log.debug("Updating processor ID: {}", processorId);
        
        try {
            Processor result = processorsClient.updateProcessor(processorId, request);
            log.info("Successfully updated processor ID: {}", processorId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update processor (BusinessException) - ID: {}, message: {}", processorId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update processor (예상치 못한 오류) - ID: {}", processorId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로세서 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    public void deleteProcessor(UUID processorId) {
        log.debug("Deleting processor ID: {}", processorId);
        
        try {
            processorsClient.deleteProcessor(processorId);
            log.info("Successfully deleted processor ID: {}", processorId);
        } catch (BusinessException e) {
            log.error("Failed to delete processor (BusinessException) - ID: {}, message: {}", processorId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete processor (예상치 못한 오류) - ID: {}", processorId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로세서 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 데이터 프로세서 실행
     * 
     * @param executeRequest 프로세서 실행 요청 정보
     * @return 실행 결과 정보
     */
    public Object executeProcessor(Object executeRequest) {
        log.debug("Executing processor with request: {}", executeRequest);
        
        try {
            Object result = processorsClient.executeProcessor(executeRequest);
            log.info("Successfully executed processor");
            return result;
        } catch (BusinessException e) {
            log.error("Failed to execute processor (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to execute processor (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로세서 실행에 실패했습니다: " + e.getMessage());
        }
    }
}
