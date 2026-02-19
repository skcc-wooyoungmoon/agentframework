package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiEvaluationResultsClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ResultsBatchUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResultsListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResultResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResultsSummaryResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Evaluation Results Service
 * 
 * <p>SKTAI Evaluation API의 평가 결과 관리 비즈니스 로직을 처리하는 서비스입니다.
 * 평가 결과 조회, 생성, 수정, 삭제 등의 기능을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>평가 결과 목록 조회 (페이징, 정렬, 필터링, 검색)</li>
 *   <li>개별 평가 결과 상세 조회</li>
 *   <li>평가 결과 생성</li>
 *   <li>평가 결과 일괄 업데이트</li>
 *   <li>평가 결과 삭제</li>
 *   <li>평가 결과 요약 통계</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiEvaluationResultsService {
    
    private final SktaiEvaluationResultsClient sktaiEvaluationResultsClient;
    
    /**
     * 평가 결과 목록 조회
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 조건 (선택사항)
     * @param filter 필터 조건 (선택사항)
     * @param search 검색어 (선택사항)
     * @return 평가 결과 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResultsListResponse getEvaluationResults(
            Integer page, Integer size, String sort, String filter, String search) {
        log.info("SKTAI Evaluation 결과 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                page, size, sort, filter, search);
        
        try {
            EvaluationResultsListResponse response = sktaiEvaluationResultsClient.getEvaluationResults(
                    page, size, sort, filter, search);
            log.info("SKTAI Evaluation 결과 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 결과 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 결과 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 결과 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 평가 결과 생성
     * 
     * @param request 평가 결과 생성 요청
     * @return 생성된 평가 결과
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResultResponse createEvaluationResult(Object request) {
        log.info("SKTAI Evaluation 결과 생성 요청");
        
        try {
            EvaluationResultResponse response = sktaiEvaluationResultsClient.createEvaluationResult(request);
            log.info("SKTAI Evaluation 결과 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 결과 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 결과 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 결과 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 평가 결과 요약 통계 조회
     * 
     * @param filter 필터 조건 (선택사항)
     * @return 평가 결과 요약 통계
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResultsSummaryResponse getEvaluationResultsSummary(String filter) {
        log.info("SKTAI Evaluation 결과 요약 조회 요청 - filter: {}", filter);
        
        try {
            EvaluationResultsSummaryResponse response = sktaiEvaluationResultsClient.getEvaluationResultsSummary(filter);
            log.info("SKTAI Evaluation 결과 요약 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 결과 요약 조회 실패 (BusinessException) - filter: {}, message: {}", filter, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 결과 요약 조회 실패 (예상치 못한 오류) - filter: {}", filter, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 결과 요약 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 특정 평가 결과 상세 조회
     * 
     * @param id 평가 결과 ID
     * @return 평가 결과 상세 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResultResponse getEvaluationResult(Integer id) {
        log.info("SKTAI Evaluation 결과 상세 조회 요청 - id: {}", id);
        
        try {
            EvaluationResultResponse response = sktaiEvaluationResultsClient.getEvaluationResult(id);
            log.info("SKTAI Evaluation 결과 상세 조회 성공 - id: {}", id);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 결과 상세 조회 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 결과 상세 조회 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 결과 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 평가 결과 삭제
     * 
     * @param id 평가 결과 ID
     * @throws BusinessException 외부 API 오류 시
     */
    public void deleteEvaluationResult(Integer id) {
        log.info("SKTAI Evaluation 결과 삭제 요청 - id: {}", id);
        
        try {
            sktaiEvaluationResultsClient.deleteEvaluationResult(id);
            log.info("SKTAI Evaluation 결과 삭제 성공 - id: {}", id);
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 결과 삭제 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 결과 삭제 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 결과 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 평가 결과 일괄 업데이트
     * 
     * @param request 일괄 업데이트 요청
     * @return 업데이트된 평가 결과 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResultsListResponse updateEvaluationResults(ResultsBatchUpdateRequest request) {
        log.info("SKTAI Evaluation 결과 일괄 업데이트 요청");
        
        try {
            EvaluationResultsListResponse response = sktaiEvaluationResultsClient.updateEvaluationResults(request);
            log.info("SKTAI Evaluation 결과 일괄 업데이트 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Evaluation 결과 일괄 업데이트 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Evaluation 결과 일괄 업데이트 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 결과 일괄 업데이트에 실패했습니다: " + e.getMessage());
        }
    }
}
