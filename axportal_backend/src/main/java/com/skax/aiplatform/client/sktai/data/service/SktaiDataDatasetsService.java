package com.skax.aiplatform.client.sktai.data.service;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.data.SktaiDataDatasetsClient;
import com.skax.aiplatform.client.sktai.data.dto.request.DataSetUpdate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetCreate;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetList;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetPreview;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTag;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTaskResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetUpdateResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Data Datasets Service
 * 
 * <p>
 * SKTAI Data API의 데이터셋 관리 기능을 위한 서비스 클래스입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 담당합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li>데이터셋 CRUD 작업의 비즈니스 로직 처리</li>
 * <li>API 호출 결과에 대한 로깅 및 예외 처리</li>
 * <li>데이터셋 파일 업로드 및 미리보기 기능</li>
 * <li>데이터셋 태그 관리 및 검색 기능</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiDataDatasetsService {

    private final SktaiDataDatasetsClient datasetsClient;

    /**
     * 데이터셋 목록 조회
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 데이터셋 목록
     */
    public DataSetList getDatasets(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Retrieving datasets list - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);

        try {
            DataSetList result = datasetsClient.getDatasets(page, size, sort, filter, search);
            log.info("Successfully retrieved {} datasets", result.getData().size());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve datasets list (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve datasets list (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 새로운 데이터셋 생성
     * 
     * @param request 데이터셋 생성 요청
     * @return 생성된 데이터셋 정보
     */
    public DatasetCreateResponse createDataset(DatasetCreate request) {
        log.debug("Creating new dataset: {}", request);

        try {
            DatasetCreateResponse result = datasetsClient.createDataset(request);
            log.info("Successfully created dataset with ID: {}", result.getId());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to create dataset (BusinessException) - name: {},message:{}", request.getName(),
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create dataset (예상치 못한 오류) - name: {}",
                    request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 생성에 실패했습니다: "
                    + e.getMessage());
        }
    }

    /**
     * 데이터셋 상세 조회
     * 
     * @param datasetId 데이터셋 ID
     * @return 데이터셋 상세 정보
     */
    public DataSetDetail getDataset(UUID datasetId) {
        log.debug("Retrieving dataset details for ID: {}", datasetId);

        try {
            DataSetDetail result = datasetsClient.getDatasetById(datasetId);
            log.info("Successfully retrieved dataset details: {}", result.getName());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve dataset details (BusinessException) - ID: {}, message: {}", datasetId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve dataset details (예상치 못한 오류) - ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 수정
     * 
     * @param datasetId 데이터셋 ID
     * @param request   데이터셋 수정 요청
     * @return 수정된 데이터셋 정보
     */
    public DatasetUpdateResponse updateDataset(UUID datasetId, DataSetUpdate request) {
        log.debug("Updating dataset ID: {}", datasetId);

        try {
            DatasetUpdateResponse result = datasetsClient.updateDataset(datasetId, request);
            log.info("Successfully updated dataset ID: {}", datasetId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update dataset (BusinessException) - ID: {}, message: {}", datasetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update dataset (예상치 못한 오류) - ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 삭제
     * 
     * @param datasetId 데이터셋 ID
     */
    public void deleteDataset(UUID datasetId) {
        log.debug("Deleting dataset ID: {}", datasetId);

        try {
            datasetsClient.deleteDataset(datasetId);
            log.info("Successfully deleted dataset ID: {}", datasetId);
        } catch (BusinessException e) {
            log.error("Failed to delete dataset (BusinessException) - ID: {}, message: {}", datasetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete dataset (예상치 못한 오류) - ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 파일 업로드
     * 
     * @param file 업로드할 파일
     * @param name 데이터셋 이름
     * @param type 데이터셋 타입
     * @return 업로드 결과
     */
    public Object uploadFile(MultipartFile file, String name, String type, String status, String description,
            String tags, String projectId, String createdBy, String updatedBy, String payload) {
        log.debug("Uploading file: {}", file.getOriginalFilename());

        try {
            Object result = datasetsClient.uploadFile(file, name, type, status, description, tags, projectId,
                    createdBy, updatedBy, payload);
            log.info("Successfully uploaded file: {}", file.getOriginalFilename());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to upload file (BusinessException) - filename: {}, message: {}",
                    file.getOriginalFilename(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload file (예상치 못한 오류) - filename: {}", file.getOriginalFilename(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 미리보기
     * 
     * @param datasetId 데이터셋 ID
     * @param limit     미리보기 행 수
     * @return 데이터셋 미리보기 정보
     */
    public DataSetPreview previewDataset(UUID datasetId, Integer limit) {
        log.debug("Previewing dataset ID: {} with limit: {}", datasetId, limit);

        try {
            DataSetPreview result = datasetsClient.getDatasetPreviews(datasetId, limit);
            log.info("Successfully generated preview for dataset ID: {}", datasetId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to preview dataset (BusinessException) - ID: {}, message: {}", datasetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to preview dataset (예상치 못한 오류) - ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 미리보기 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 태그 업데이트
     * 
     * @param datasetId 데이터셋 ID
     * @param tags      태그 목록
     * @return 업데이트 결과
     */
    public DatasetUpdateResponse updateTags(UUID datasetId, List<DatasetTag> tags) {
        log.debug("Updating tags for dataset ID: {}", datasetId);

        try {
            DatasetUpdateResponse result = datasetsClient.updateTags(datasetId, tags);
            log.info("Successfully updated tags for dataset ID: {}", datasetId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update tags (BusinessException) - dataset ID: {}, message: {}", datasetId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update tags (예상치 못한 오류) - dataset ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 태그 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모든 삭제된 데이터셋 완전 삭제
     * 
     * @return 삭제 결과
     */
    public Object hardDeleteAllDatasets() {
        log.debug("Hard deleting all datasets");

        try {
            Object result = datasetsClient.hardDeleteAllDatasets();
            log.info("Successfully hard deleted all datasets");
            return result;
        } catch (BusinessException e) {
            log.error("Failed to hard delete all datasets (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to hard delete all datasets (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 완전 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 태그 삭제
     * 
     * @param datasetId 데이터셋 ID
     * @param sktaiTags 삭제할 태그 목록
     * @return 삭제 결과
     */
    public DatasetUpdateResponse deleteTags(UUID datasetId, List<DatasetTag> tags) {
        log.debug("Deleting tags for dataset ID: {}", datasetId);

        try {
            DatasetUpdateResponse result = datasetsClient.deleteTags(datasetId, tags);
            log.info("Successfully deleted tags for dataset ID: {}", datasetId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to delete tags (BusinessException) - dataset ID: {}, message: {}", datasetId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete tags (예상치 못한 오류) - dataset ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터셋 태그 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 소스 아카이브 다운로드
     * 
     * <p>
     * 데이터셋의 원본 파일을 압축한 아카이브(ZIP/TAR)를 다운로드합니다.
     * 지원되는 데이터셋 타입: model_benchmark, rag_evaluation, custom
     * </p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     * <li>업로드된 원본 파일을 ZIP 또는 TAR 형식으로 다운로드</li>
     * <li>대용량 파일 스트리밍 지원</li>
     * <li>Content-Disposition 헤더를 통한 파일명 제공</li>
     * </ul>
     * 
     * <h3>비즈니스 로직:</h3>
     * <ul>
     * <li>데이터셋 ID 유효성 검증 (UUID 형식)</li>
     * <li>지원 타입 확인 (model_benchmark, rag_evaluation, custom)</li>
     * <li>파일 스트림 ResponseEntity 반환</li>
     * <li>상세 로깅 및 예외 처리</li>
     * </ul>
     * 
     * <h3>예외 처리:</h3>
     * <ul>
     * <li><strong>404 Not Found</strong>: 데이터셋을 찾을 수 없는 경우</li>
     * <li><strong>422 Validation Error</strong>: UUID 형식 오류 또는 지원하지 않는 타입</li>
     * <li><strong>500 Internal Error</strong>: 서버 내부 오류 또는 네트워크 문제</li>
     * </ul>
     * 
     * <h3>사용 예시:</h3>
     * 
     * <pre>
     * UUID datasetId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
     * ResponseEntity&lt;Resource&gt; response = service.getDatasetSourceArchive(datasetId);
     * 
     * // 파일명 추출
     * String filename = response.getHeaders().getContentDisposition().getFilename();
     * 
     * // 파일 저장
     * Resource resource = response.getBody();
     * Files.copy(resource.getInputStream(), Paths.get("downloads/" + filename));
     * </pre>
     * 
     * @param datasetId 데이터셋 고유 식별자 (UUID 형식, 필수)
     * @return 파일 스트림을 포함한 ResponseEntity (Content-Type: application/zip 또는
     *         application/x-tar)
     * @throws RuntimeException 데이터셋을 찾을 수 없거나 다운로드에 실패한 경우
     */
    public ResponseEntity<Resource> getDatasetSourceArchive(UUID datasetId) {
        log.debug("Downloading source archive for dataset ID: {}", datasetId);

        try {
            ResponseEntity<Resource> response = datasetsClient.getDatasetSourceArchive(datasetId);

            // 응답 헤더에서 파일명 추출 (있는 경우)
            String filename = "unknown";
            if (response.getHeaders().getContentDisposition() != null &&
                    response.getHeaders().getContentDisposition().getFilename() != null) {
                filename = response.getHeaders().getContentDisposition().getFilename();
            }

            // Content-Type 확인
            String contentType = "unknown";
            if (response.getHeaders() != null) {
                var ct = response.getHeaders().getContentType();
                if (ct != null) {
                    contentType = ct.toString();
                }
            }

            log.info("Successfully downloaded source archive for dataset ID: {} - filename: {}, contentType: {}",
                    datasetId, filename, contentType);

            return response;

        } catch (feign.FeignException.NotFound e) {
            log.error("Dataset not found for ID: {}", datasetId);
            throw new RuntimeException("데이터셋을 찾을 수 없습니다 (ID: " + datasetId + ")", e);

        } catch (feign.FeignException.UnprocessableEntity e) {
            log.error("Validation error for dataset ID: {} - {}", datasetId, e.contentUTF8());
            throw new RuntimeException("데이터셋 소스 아카이브 다운로드 유효성 검증 오류: " + e.contentUTF8(), e);

        } catch (Exception e) {
            log.error("Failed to download source archive for dataset ID: {}", datasetId, e);
            throw new RuntimeException("데이터셋 소스 아카이브 다운로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 데이터셋 Task 조회
     * 
     * <p>
     * 지정된 데이터셋의 Task 정보를 조회합니다.
     * OpenAPI 스펙: GET /api/v1/datasets/{dataset_id}/task
     * </p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     * <li>데이터셋 ID로 Task 정보 조회</li>
     * <li>Task 상태 및 상세 정보 반환</li>
     * <li>상세 로깅 및 예외 처리</li>
     * </ul>
     * 
     * <h3>예외 처리:</h3>
     * <ul>
     * <li><strong>404 Not Found</strong>: 데이터셋을 찾을 수 없는 경우</li>
     * <li><strong>422 Validation Error</strong>: UUID 형식 오류</li>
     * <li><strong>500 Internal Error</strong>: 서버 내부 오류 또는 네트워크 문제</li>
     * </ul>
     * 
     * @param datasetId 데이터셋 고유 식별자 (UUID 형식, 필수)
     * @return 데이터셋 Task 정보
     * @throws BusinessException Task 조회에 실패한 경우
     */
    public DatasetTaskResponse getDatasetTask(UUID datasetId) {
        log.debug("Retrieving task for dataset ID: {}", datasetId);

        try {
            DatasetTaskResponse result = datasetsClient.getDatasetTask(datasetId);
            log.info("Successfully retrieved task for dataset ID: {} - status: {}",
                    datasetId, result.getStatus() != null ? result.getStatus() : "N/A");
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve dataset task (BusinessException) - ID: {}, message: {}",
                    datasetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve dataset task (예상치 못한 오류) - ID: {}", datasetId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "데이터셋 Task 조회에 실패했습니다: " + e.getMessage());
        }
    }

}
