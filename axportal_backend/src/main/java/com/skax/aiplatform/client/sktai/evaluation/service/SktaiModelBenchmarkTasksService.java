package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiModelBenchmarkTasksClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkTaskCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkTaskResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Model Benchmark Tasks Service
 * 
 * <p>SKTAI Evaluation API의 모델 벤치마크 작업 관리 비즈니스 로직을 처리하는 서비스입니다.
 * 모델 벤치마크 작업 생성 및 관리 기능을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>모델 벤치마크 작업 생성</li>
 *   <li>벤치마크 작업 실행 관리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiModelBenchmarkTasksService {
    
    private final SktaiModelBenchmarkTasksClient sktaiModelBenchmarkTasksClient;
    
    /**
     * 모델 벤치마크 작업 생성
     * 
     * @param request 벤치마크 작업 생성 요청
     * @return 생성된 벤치마크 작업 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public ModelBenchmarkTaskResponse createModelBenchmarkTask(ModelBenchmarkTaskCreateRequest request) {
        log.info("SKTAI 모델 벤치마크 작업 생성 요청");
        
        try {
            ModelBenchmarkTaskResponse response = sktaiModelBenchmarkTasksClient.createModelBenchmarkTask(request);
            log.info("SKTAI 모델 벤치마크 작업 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 모델 벤치마크 작업 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 모델 벤치마크 작업 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 벤치마크 작업 생성에 실패했습니다: " + e.getMessage());
        }
    }
}
