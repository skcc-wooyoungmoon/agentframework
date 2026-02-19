package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiRagEvaluationTasksClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.RagEvaluationTaskCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.TaskCreateResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI RAG Evaluation Tasks Service
 * 
 * <p>SKTAI Evaluation API의 RAG 평가 작업 관리 비즈니스 로직을 처리하는 서비스입니다.
 * RAG 평가 작업 생성 및 관리 기능을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>RAG 평가 작업 생성</li>
 *   <li>RAG 평가 작업 실행 관리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiRagEvaluationTasksService {
    
    private final SktaiRagEvaluationTasksClient sktaiRagEvaluationTasksClient;
    
    /**
     * RAG 평가 작업 생성
     * 
     * @param request RAG 평가 작업 생성 요청
     * @return 생성된 RAG 평가 작업 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TaskCreateResponse createRagEvaluationTask(RagEvaluationTaskCreateRequest request) {
        log.info("SKTAI RAG 평가 작업 생성 요청");
        
        try {
            TaskCreateResponse response = sktaiRagEvaluationTasksClient.createRagEvaluationTask(request);
            log.info("SKTAI RAG 평가 작업 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 작업 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 작업 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 작업 생성에 실패했습니다: " + e.getMessage());
        }
    }
}
