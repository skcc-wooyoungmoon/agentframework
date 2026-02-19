package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiRagEvaluationResultsClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResultsListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResultResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResultsSummaryResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI RAG Evaluation Results Service
 * 
 * <p>SKTAI Evaluation API의 RAG 평가 결과 관리 비즈니스 로직을 처리하는 서비스입니다.
 * RAG 평가 결과 조회, 생성, 수정, 삭제 등의 기능을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>RAG 평가 결과 목록 조회 (페이징, 정렬, 필터링, 검색)</li>
 *   <li>개별 RAG 평가 결과 상세 조회</li>
 *   <li>RAG 평가 결과 생성</li>
 *   <li>RAG 평가 결과 삭제</li>
 *   <li>RAG 평가 결과 요약 통계</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiRagEvaluationResultsService {
    
    private final SktaiRagEvaluationResultsClient sktaiRagEvaluationResultsClient;
    
    /**
     * RAG 평가 결과 목록 조회
     */
    public RagEvaluationResultsListResponse getRagEvaluationResults(
            Integer page, Integer size, String sort, String filter, String search) {
        log.info("SKTAI RAG 평가 결과 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                page, size, sort, filter, search);
        
        try {
            RagEvaluationResultsListResponse response = sktaiRagEvaluationResultsClient.getRagEvaluationResults(
                    page, size, sort, filter, search);
            log.info("SKTAI RAG 평가 결과 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 결과 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 결과 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 결과 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * RAG 평가 결과 생성
     */
    public RagEvaluationResultResponse createRagEvaluationResult(Object request) {
        log.info("SKTAI RAG 평가 결과 생성 요청");
        
        try {
            RagEvaluationResultResponse response = sktaiRagEvaluationResultsClient.createRagEvaluationResult(request);
            log.info("SKTAI RAG 평가 결과 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 결과 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 결과 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 결과 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * RAG 평가 결과 요약 통계 조회
     */
    public RagEvaluationResultsSummaryResponse getRagEvaluationResultsSummary(String filter) {
        log.info("SKTAI RAG 평가 결과 요약 조회 요청 - filter: {}", filter);
        
        try {
            RagEvaluationResultsSummaryResponse response = sktaiRagEvaluationResultsClient.getRagEvaluationResultsSummary(filter);
            log.info("SKTAI RAG 평가 결과 요약 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 결과 요약 조회 실패 (BusinessException) - filter: {}, message: {}", filter, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 결과 요약 조회 실패 (예상치 못한 오류) - filter: {}", filter, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 결과 요약 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 특정 RAG 평가 결과 상세 조회
     */
    public RagEvaluationResultResponse getRagEvaluationResult(Integer id) {
        log.info("SKTAI RAG 평가 결과 상세 조회 요청 - id: {}", id);
        
        try {
            RagEvaluationResultResponse response = sktaiRagEvaluationResultsClient.getRagEvaluationResult(id);
            log.info("SKTAI RAG 평가 결과 상세 조회 성공 - id: {}", id);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 결과 상세 조회 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 결과 상세 조회 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 결과 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * RAG 평가 결과 삭제
     */
    public void deleteRagEvaluationResult(Integer id) {
        log.info("SKTAI RAG 평가 결과 삭제 요청 - id: {}", id);
        
        try {
            sktaiRagEvaluationResultsClient.deleteRagEvaluationResult(id);
            log.info("SKTAI RAG 평가 결과 삭제 성공 - id: {}", id);
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 결과 삭제 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 결과 삭제 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 결과 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
