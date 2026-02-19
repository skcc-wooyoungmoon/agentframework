package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiModelBenchmarkLogsClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.LogStatusUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkLogsResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.LogUpdateResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Model Benchmark Logs Service
 * 
 * <p>SKTAI Evaluation API의 모델 벤치마크 로그 관리 비즈니스 로직을 처리하는 서비스입니다.
 * 모델 벤치마크 작업의 로그 조회, 상태 업데이트 등을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>모델 벤치마크 로그 목록 조회</li>
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
public class SktaiModelBenchmarkLogsService {
    
    private final SktaiModelBenchmarkLogsClient sktaiModelBenchmarkLogsClient;
    
    /**
     * 모델 벤치마크 로그 목록 조회
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 조건 (선택사항)
     * @param filter 필터 조건 (선택사항)
     * @param search 검색어 (선택사항)
     * @return 모델 벤치마크 로그 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public ModelBenchmarkLogsResponse getModelBenchmarkLogs(
            Integer page, Integer size, String sort, String filter, String search) {
        log.info("SKTAI 모델 벤치마크 로그 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                page, size, sort, filter, search);
        
        try {
            ModelBenchmarkLogsResponse response = sktaiModelBenchmarkLogsClient.getModelBenchmarkLogs(
                    page, size, sort, filter, search);
            log.info("SKTAI 모델 벤치마크 로그 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 로그 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 로그 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 로그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 벤치마크 로그 상태 업데이트
     * 
     * @param id 로그 ID
     * @param request 상태 업데이트 요청
     * @return 업데이트 결과
     * @throws BusinessException 외부 API 오류 시
     */
    public LogUpdateResponse updateModelBenchmarkLogStatus(Integer id, LogStatusUpdateRequest request) {
        log.info("SKTAI 모델 벤치마크 로그 상태 업데이트 요청 - id: {}, status: {}", 
                id, request.getStatus());
        
        try {
            LogUpdateResponse response = sktaiModelBenchmarkLogsClient.updateModelBenchmarkLogStatus(id, request);
            log.info("SKTAI 모델 벤치마크 로그 상태 업데이트 성공 - id: {}", id);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 로그 상태 업데이트 실패 (BusinessException) - id: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 로그 상태 업데이트 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 로그 상태 업데이트에 실패했습니다: " + e.getMessage());
        }
    }
}
