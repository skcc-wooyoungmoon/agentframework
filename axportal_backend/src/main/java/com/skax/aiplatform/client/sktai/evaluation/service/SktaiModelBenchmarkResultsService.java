package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiModelBenchmarkResultsClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkResultCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkResultUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ResultsBatchUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResultResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResultsListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResultsSummaryResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Model Benchmark Results Service
 * 
 * <p>SKTAI Evaluation API의 모델 벤치마크 결과 관리 비즈니스 로직을 처리하는 서비스입니다.
 * 벤치마크 결과 조회, 생성, 수정, 삭제 등의 기능을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>모델 벤치마크 결과 목록 조회 (페이징, 정렬, 필터링, 검색)</li>
 *   <li>개별 벤치마크 결과 상세 조회</li>
 *   <li>벤치마크 결과 생성</li>
 *   <li>벤치마크 결과 일괄 업데이트</li>
 *   <li>벤치마크 결과 삭제</li>
 *   <li>벤치마크 결과 요약 통계</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiModelBenchmarkResultsService {
    
    private final SktaiModelBenchmarkResultsClient sktaiModelBenchmarkResultsClient;
    
    /**
     * 모델 벤치마크 결과 목록 조회
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 조건 (선택사항)
     * @param filter 필터 조건 (선택사항)
     * @param search 검색어 (선택사항)
     * @return 모델 벤치마크 결과 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public ModelBenchmarkResultsListResponse getModelBenchmarkResults(
            Integer page, Integer size, String sort, String filter, String search) {
        log.info("SKTAI 모델 벤치마크 결과 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                page, size, sort, filter, search);
        
        try {
            ModelBenchmarkResultsListResponse response = sktaiModelBenchmarkResultsClient.getModelBenchmarkResults(
                    page, size, sort, filter, search);
            log.info("SKTAI 모델 벤치마크 결과 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 결과 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 결과 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 결과 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 벤치마크 결과 생성
     * 
     * @param request 벤치마크 결과 생성 요청
     * @return 생성된 벤치마크 결과
     * @throws BusinessException 외부 API 오류 시
     */
    public ModelBenchmarkResultResponse createModelBenchmarkResult(ModelBenchmarkResultCreateRequest request) {
        log.info("SKTAI 모델 벤치마크 결과 생성 요청");
        
        try {
            ModelBenchmarkResultResponse response = sktaiModelBenchmarkResultsClient.createModelBenchmarkResult(request);
            log.info("SKTAI 모델 벤치마크 결과 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 결과 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 결과 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 결과 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 벤치마크 결과 요약 통계 조회
     * 
     * @param filter 필터 조건 (선택사항)
     * @return 벤치마크 결과 요약 통계
     * @throws BusinessException 외부 API 오류 시
     */
    public ModelBenchmarkResultsSummaryResponse getModelBenchmarkResultsSummary(String filter) {
        log.info("SKTAI 모델 벤치마크 결과 요약 조회 요청 - filter: {}", filter);
        
        try {
            ModelBenchmarkResultsSummaryResponse response = sktaiModelBenchmarkResultsClient.getModelBenchmarkResultsSummary(filter);
            log.info("SKTAI 모델 벤치마크 결과 요약 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 결과 요약 조회 실패 (BusinessException) - filter: {}, message: {}", filter, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 결과 요약 조회 실패 (예상치 못한 오류) - filter: {}", filter, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 결과 요약 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 특정 모델 벤치마크 결과 상세 조회
     * 
     * @param id 벤치마크 결과 ID
     * @return 벤치마크 결과 상세 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public ModelBenchmarkResultResponse getModelBenchmarkResult(Integer id) {
        log.info("SKTAI 모델 벤치마크 결과 상세 조회 요청 - id: {}", id);
        
        try {
            ModelBenchmarkResultResponse response = sktaiModelBenchmarkResultsClient.getModelBenchmarkResult(id);
            log.info("SKTAI 모델 벤치마크 결과 상세 조회 성공 - id: {}", id);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 결과 상세 조회 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 결과 상세 조회 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 결과 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 벤치마크 결과 삭제
     * 
     * @param id 벤치마크 결과 ID
     * @throws BusinessException 외부 API 오류 시
     */
    public void deleteModelBenchmarkResult(Integer id) {
        log.info("SKTAI 모델 벤치마크 결과 삭제 요청 - id: {}", id);
        
        try {
            sktaiModelBenchmarkResultsClient.deleteModelBenchmarkResult(id);
            log.info("SKTAI 모델 벤치마크 결과 삭제 성공 - id: {}", id);
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 결과 삭제 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 결과 삭제 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 결과 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 벤치마크 결과 업데이트
     * 
     * @param request 업데이트 요청
     * @return 업데이트 요청 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public ResultsBatchUpdateRequest updateModelBenchmarkResults(ModelBenchmarkResultUpdateRequest request) {
        log.info("SKTAI 모델 벤치마크 결과 업데이트 요청");
        
        try {
            ResultsBatchUpdateRequest response = sktaiModelBenchmarkResultsClient.updateModelBenchmarkResults(request);
            log.info("SKTAI 모델 벤치마크 결과 업데이트 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 결과 업데이트 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 결과 업데이트 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 결과 업데이트에 실패했습니다: " + e.getMessage());
        }
    }
}
