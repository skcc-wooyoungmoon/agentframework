package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiRagEvaluationLogsClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.LogStatusUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationLogsResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.LogUpdateResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI RAG Evaluation Logs Service
 * 
 * <p>SKTAI Evaluation API의 RAG 평가 로그 관리 비즈니스 로직을 처리하는 서비스입니다.
 * RAG 평가 작업의 로그 조회, 상태 업데이트 등을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>RAG 평가 로그 목록 조회</li>
 *   <li>로그 상태 업데이트</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiRagEvaluationLogsService {
    
    private final SktaiRagEvaluationLogsClient sktaiRagEvaluationLogsClient;
    
    /**
     * RAG 평가 로그 목록 조회
     * 
     * @param evaluationTaskId 평가 작업 ID
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 20)
     * @return RAG 평가 로그 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public RagEvaluationLogsResponse getRagEvaluationLogs(Integer evaluationTaskId, Integer page, Integer size) {
        log.info("SKTAI RAG 평가 로그 목록 조회 요청 - evaluationTaskId: {}, page: {}, size: {}", 
                evaluationTaskId, page, size);
        
        try {
            RagEvaluationLogsResponse response = sktaiRagEvaluationLogsClient.getRagEvaluationLogs(
                    evaluationTaskId, page, size);
            log.info("SKTAI RAG 평가 로그 목록 조회 성공 - evaluationTaskId: {}", evaluationTaskId);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 로그 목록 조회 실패 (BusinessException) - evaluationTaskId: {}, message: {}", evaluationTaskId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 로그 목록 조회 실패 (예상치 못한 오류) - evaluationTaskId: {}", evaluationTaskId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 로그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * RAG 평가 로그 상태 업데이트
     * 
     * @param request 상태 업데이트 요청
     * @return 업데이트 결과
     * @throws BusinessException 외부 API 오류 시
     */
    public LogUpdateResponse updateRagEvaluationLogsStatus(LogStatusUpdateRequest request) {
        log.info("SKTAI RAG 평가 로그 상태 업데이트 요청 - status: {}", request.getStatus());
        
        try {
            LogUpdateResponse response = sktaiRagEvaluationLogsClient.updateRagEvaluationLogsStatus(request);
            log.info("SKTAI RAG 평가 로그 상태 업데이트 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI RAG 평가 로그 상태 업데이트 실패 (BusinessException) - status: {}, message: {}", request.getStatus(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI RAG 평가 로그 상태 업데이트 실패 (예상치 못한 오류) - status: {}", request.getStatus(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "RAG 평가 로그 상태 업데이트에 실패했습니다: " + e.getMessage());
        }
    }
}
