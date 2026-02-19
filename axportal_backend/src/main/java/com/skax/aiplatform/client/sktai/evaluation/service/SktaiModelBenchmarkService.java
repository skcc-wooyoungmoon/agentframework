package com.skax.aiplatform.client.sktai.evaluation.service;

import com.skax.aiplatform.client.sktai.evaluation.SktaiModelBenchmarkClient;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkTaskFilesDeleteRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkTaskFilesListResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Model Benchmark Service
 * 
 * <p>SKTAI Evaluation API의 Model Benchmark 관련 기능을 제공하는 서비스 클래스입니다.
 * Feign Client를 래핑하여 추가적인 로직 처리와 예외 처리를 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Model Benchmark 관리</strong>: 생성, 조회, 목록 조회</li>
 *   <li><strong>작업 파일 관리</strong>: 업로드, 조회, 삭제</li>
 *   <li><strong>오류 처리</strong>: API 호출 시 발생하는 예외 처리</li>
 *   <li><strong>로깅</strong>: 주요 작업에 대한 로그 기록</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiModelBenchmarkService {

    private final SktaiModelBenchmarkClient modelBenchmarkClient;

    /**
     * Model Benchmark 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return Model Benchmark 목록
     */
    public ModelBenchmarkListResponse getModelBenchmarks(Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.info("Model Benchmark 목록 조회 시작 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                    page, size, sort, filter, search);
            
            ModelBenchmarkListResponse response = modelBenchmarkClient.getModelBenchmarks(page, size, sort, filter, search);
            
            log.info("Model Benchmark 목록 조회 완료 - 총 {}개 항목", 
                    response.getData() != null ? response.getData().size() : 0);
            
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Model Benchmark 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", 
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model Benchmark 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Benchmark 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Benchmark 생성
     * 
     * @param request 생성 요청 데이터
     * @return 생성된 Model Benchmark 정보
     */
    public ModelBenchmarkResponse createModelBenchmark(ModelBenchmarkCreateRequest request) {
        try {
            log.info("Model Benchmark 생성 시작 - name: {}, tasks: {}", 
                    request.getName(), request.getTasks());
            
            ModelBenchmarkResponse response = modelBenchmarkClient.createModelBenchmark(request);
            
            log.info("Model Benchmark 생성 완료 - id: {}, name: {}", 
                    response.getId(), response.getName());
            
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Model Benchmark 생성 실패 (BusinessException) - name: {}, message: {}", 
                    request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model Benchmark 생성 실패 (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Benchmark 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Benchmark 상세 조회
     * 
     * @param id Model Benchmark ID
     * @return Model Benchmark 상세 정보
     */
    public ModelBenchmarkResponse getModelBenchmark(Integer id) {
        try {
            log.info("Model Benchmark 상세 조회 시작 - id: {}", id);
            
            ModelBenchmarkResponse response = modelBenchmarkClient.getModelBenchmark(id);
            
            log.info("Model Benchmark 상세 조회 완료 - id: {}, name: {}", 
                    response.getId(), response.getName());
            
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Model Benchmark 상세 조회 실패 (BusinessException) - id: {}, message: {}", 
                    id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model Benchmark 상세 조회 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Benchmark 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Benchmark 작업 파일 업로드
     * 
     * @param id Model Benchmark ID
     * @param files 업로드할 파일들
     * @return 업로드 결과
     */
    public Object uploadModelBenchmarkTaskFiles(Integer id, MultipartFile[] files) {
        try {
            log.info("Model Benchmark 작업 파일 업로드 시작 - id: {}, 파일 수: {}", 
                    id, files != null ? files.length : 0);
            
            Object response = modelBenchmarkClient.uploadModelBenchmarkTaskFiles(id, files);
            
            log.info("Model Benchmark 작업 파일 업로드 완료 - id: {}", id);
            
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Model Benchmark 작업 파일 업로드 실패 (BusinessException) - id: {}, message: {}", 
                    id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model Benchmark 작업 파일 업로드 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "작업 파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Benchmark 작업 파일 목록 조회
     * 
     * @param id Model Benchmark ID
     * @return 작업 파일 목록
     */
    public ModelBenchmarkTaskFilesListResponse getModelBenchmarkTaskFiles(Integer id) {
        try {
            log.info("Model Benchmark 작업 파일 목록 조회 시작 - id: {}", id);
            
            ModelBenchmarkTaskFilesListResponse response = modelBenchmarkClient.getModelBenchmarkTaskFiles(id);
            
            log.info("Model Benchmark 작업 파일 목록 조회 완료 - id: {}, 파일 수: {}", 
                    id, response.getData() != null ? response.getData().size() : 0);
            
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Model Benchmark 작업 파일 목록 조회 실패 (BusinessException) - id: {}, message: {}", 
                    id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model Benchmark 작업 파일 목록 조회 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "작업 파일 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Benchmark 작업 파일 삭제
     * 
     * @param id Model Benchmark ID
     * @param request 삭제할 파일 목록
     * @return 삭제 결과
     */
    public Object deleteModelBenchmarkTaskFile(Integer id, ModelBenchmarkTaskFilesDeleteRequest request) {
        try {
            log.info("Model Benchmark 작업 파일 삭제 시작 - id: {}, 삭제할 파일 수: {}", 
                    id, request.getFiles() != null ? request.getFiles().size() : 0);
            
            Object response = modelBenchmarkClient.deleteModelBenchmarkTaskFile(id, request);
            
            log.info("Model Benchmark 작업 파일 삭제 완료 - id: {}", id);
            
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Model Benchmark 작업 파일 삭제 실패 (BusinessException) - id: {}, message: {}", 
                    id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Model Benchmark 작업 파일 삭제 실패 (예상치 못한 오류) - id: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "작업 파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
