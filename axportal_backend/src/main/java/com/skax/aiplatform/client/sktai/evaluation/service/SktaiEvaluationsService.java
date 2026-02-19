package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiEvaluationsClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.EvaluationCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationsListResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Evaluations Service
 * 
 * <p>SKTAI Evaluation API의 일반 평가 관리 비즈니스 로직을 처리하는 서비스입니다.
 * 평가 생성, 조회, 목록 관리 등의 기능을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>평가 목록 조회 (페이징, 정렬, 필터링, 검색)</li>
 *   <li>개별 평가 상세 조회</li>
 *   <li>평가 생성</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiEvaluationsService {
    
    private final SktaiEvaluationsClient sktaiEvaluationsClient;
    
    /**
     * 평가 목록 조회
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 조건 (선택사항)
     * @param filter 필터 조건 (선택사항)
     * @param search 검색어 (선택사항)
     * @return 평가 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationsListResponse getEvaluations(
            Integer page, Integer size, String sort, String filter, String search) {
        log.info("SKTAI 평가 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                page, size, sort, filter, search);
        
        try {
            EvaluationsListResponse response = sktaiEvaluationsClient.getEvaluations(
                    page, size, sort, filter, search);
            log.info("SKTAI 평가 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 평가 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 평가 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 평가 생성
     * 
     * @param request 평가 생성 요청
     * @return 생성된 평가 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResponse createEvaluation(EvaluationCreateRequest request) {
        log.info("SKTAI 평가 생성 요청 - name: {}", request.getName());
        
        try {
            EvaluationResponse response = sktaiEvaluationsClient.createEvaluation(request);
            log.info("SKTAI 평가 생성 성공 - name: {}", request.getName());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 평가 생성 실패 (BusinessException) - name: {}, message: {}", request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 평가 생성 실패 (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 특정 평가 상세 조회
     * 
     * @param id 평가 ID
     * @return 평가 상세 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public EvaluationResponse getEvaluation(Integer id) {
        log.info("SKTAI 평가 상세 조회 요청 - id: {}", id);
        
        try {
            EvaluationResponse response = sktaiEvaluationsClient.getEvaluation(id);
            log.info("SKTAI 평가 상세 조회 성공 - id: {}", id);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 평가 상세 조회 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 평가 상세 조회 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "평가 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
