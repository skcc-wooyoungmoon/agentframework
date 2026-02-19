package com.skax.aiplatform.client.sktai.data.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.data.SktaiDataDatasourcesClient;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasourceCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasourceUpdate;
import com.skax.aiplatform.client.sktai.data.dto.response.Datasource;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceFileList;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceList;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSourceCreateResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiDataDatasourcesService {

    private final SktaiDataDatasourcesClient datasourcesClient;

    public DatasourceList getDatasources(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Retrieving datasources list - page: {}, size: {}", page, size);

        try {
            DatasourceList result = datasourcesClient.getDatasources(page, size, sort, filter, search);
            log.info("Successfully retrieved {} datasources", result.getData().size());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve datasources list (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve datasources list (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public DataSourceCreateResponse createDatasource(DatasourceCreate request) {
        log.debug("Creating new datasource: {}", request.getName());

        try {
            DataSourceCreateResponse result = datasourcesClient.createDatasource(request);
            log.info("Successfully created datasource with ID: {}", result.getId());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to create datasource (BusinessException) - name: {}, message: {}", request.getName(),
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create datasource (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 생성에 실패했습니다: " + e.getMessage());
        }
    }

    public DatasourceDetail getDatasource(UUID datasourceId) {
        log.debug("Retrieving datasource details for ID: {}", datasourceId);

        try {
            DatasourceDetail result = datasourcesClient.getDatasource(datasourceId);
            log.info("Successfully retrieved datasource details: {}", result.getName());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to retrieve datasource details (BusinessException) - id: {}, message: {}", datasourceId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Failed to retrieve datasource details (예상치 못한 오류) - id: {}", datasourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public Datasource updateDatasource(UUID datasourceId, DatasourceUpdate request) {
        log.debug("Updating datasource ID: {}", datasourceId);

        try {
            Datasource result = datasourcesClient.updateDatasource(datasourceId, request);
            log.info("Successfully updated datasource ID: {}", datasourceId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update datasource (BusinessException) - id: {}, message: {}", datasourceId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update datasource (예상치 못한 오류) - id: {}", datasourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 수정에 실패했습니다: " + e.getMessage());
        }
    }

    public void deleteDatasource(UUID datasourceId) {
        log.debug("Deleting datasource ID: {}", datasourceId);

        try {
            datasourcesClient.deleteDatasource(datasourceId);
            log.info("Successfully deleted datasource ID: {}", datasourceId);
        } catch (BusinessException e) {
            log.error("Failed to delete datasource (BusinessException) - id: {}, message: {}", datasourceId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete datasource (예상치 못한 오류) - id: {}", datasourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 업로드
     * 
     * @param files 업로드할 파일들
     * @return 업로드 결과
     */
    public Map<String, Object> uploadFiles(List<MultipartFile> files) {
        log.debug("Uploading {} files", files.size());

        try {
            Map<String, Object> result = datasourcesClient.uploadFiles(files);
            log.info("Successfully uploaded {} files", files.size());
            return result;
        } catch (BusinessException e) {
            log.error("Failed to upload files (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload files (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 다운로드
     * 
     * @param datasourceFileId 데이터소스 파일 ID
     * @return 파일 리소스
     */
    public ResponseEntity<Resource> downloadFile(String datasourceFileId) {
        log.debug("Downloading file: {}", datasourceFileId);

        try {
            ResponseEntity<Resource> result = datasourcesClient.downloadFile(datasourceFileId);
            log.info("Successfully downloaded file: {}", datasourceFileId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to download file (BusinessException) - id: {}, message: {}", datasourceFileId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to download file (예상치 못한 오류) - id: {}", datasourceFileId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파일 다운로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API Key로 파일 다운로드
     * 
     * @param datasourceFileId 데이터소스 파일 ID
     * @return 파일 리소스
     */
    public ResponseEntity<Resource> downloadFileByApikey(String datasourceFileId) {
        log.debug("Downloading file by API key: {}", datasourceFileId);

        try {
            ResponseEntity<Resource> result = datasourcesClient.downloadFileByApikey(datasourceFileId);
            log.info("Successfully downloaded file by API key: {}", datasourceFileId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to download file by API key (BusinessException) - id: {}, message: {}", datasourceFileId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to download file by API key (예상치 못한 오류) - id: {}", datasourceFileId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 파일 다운로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터소스 파일 목록 조회
     * 
     * <p>지정된 데이터소스에 속한 파일들의 목록을 페이징하여 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원하며, 대량의 파일을 효율적으로 관리할 수 있습니다.</p>
     * 
     * <h3>지원 기능:</h3>
     * <ul>
     *   <li><strong>페이징</strong>: 대량의 파일을 효율적으로 처리</li>
     *   <li><strong>검색</strong>: 파일명 기반 텍스트 검색</li>
     *   <li><strong>필터링</strong>: 파일 상태 및 타입별 필터</li>
     *   <li><strong>정렬</strong>: 다양한 기준으로 정렬</li>
     * </ul>
     * 
     * @param datasourceId 데이터소스 ID (UUID 형식의 문자열)
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지 크기 (1~100, 기본값: 20)
     * @param sort 정렬 기준 (필드명:방향, 예: "created_at:desc")
     * @param filter 필터 조건 (필드명:값, 예: "is_deleted:false")
     * @param search 검색어 (파일명 부분 일치 검색)
     * @return 페이징된 파일 목록과 메타데이터
     * @throws BusinessException 데이터소스를 찾을 수 없거나 API 호출 실패 시
     */
    public DatasourceFileList listDatasourceFiles(String datasourceId, Integer page, Integer size) {
        log.info("데이터소스 파일 목록 조회 요청 시작 - datasourceId: {}, page: {}, size: {}", 
                 datasourceId, page, size);
        
        try {
            // 입력값 검증
            validateDatasourceId(datasourceId);
            validatePagingParameters(page, size);
            
            log.info("필수 파라미터만 사용하여 SKT AI API 호출");
            
            DatasourceFileList result = datasourcesClient.listDatasourceFiles(
                datasourceId, page, size);
            
            if (result == null) {
                log.warn("SKT AI API에서 null 응답 반환 - datasourceId: {}", datasourceId);
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "SKT AI API에서 응답을 받지 못했습니다.");
            }
            
            log.info("데이터소스 파일 목록 조회 성공 - datasourceId: {}, totalCount: {}, currentPage: {}, dataSize: {}", 
                     datasourceId, 
                     result.getPayload() != null && result.getPayload().getPagination() != null 
                         ? result.getPayload().getPagination().getTotal() : 0,
                     result.getPayload() != null && result.getPayload().getPagination() != null 
                         ? result.getPayload().getPagination().getPage() : 0,
                     result.getData() != null ? result.getData().size() : 0);
            
            return result;
        } catch (BusinessException e) {
            log.error("데이터소스 파일 목록 조회 비즈니스 예외 - datasourceId: {}, errorCode: {}, message: {}", 
                     datasourceId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("데이터소스 파일 목록 조회 실패 - datasourceId: {}, errorType: {}, message: {}", 
                     datasourceId, e.getClass().getSimpleName(), e.getMessage(), e);
            
            // 더 구체적인 에러 처리
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("404") || errorMessage.contains("Not Found")) {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "데이터소스를 찾을 수 없습니다: " + datasourceId);
                } else if (errorMessage.contains("400") || errorMessage.contains("Bad Request")) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, 
                        "잘못된 요청 파라미터입니다: " + errorMessage);
                } else if (errorMessage.contains("500") || errorMessage.contains("Internal Server Error")) {
                    throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        "SKT AI 서버 내부 오류가 발생했습니다: " + errorMessage);
                } else if (errorMessage.contains("timeout") || errorMessage.contains("Timeout")) {
                    throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        "SKT AI 서버 응답 시간 초과: " + errorMessage);
                }
            }
            
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                "데이터소스 파일 목록 조회에 실패했습니다: " + errorMessage);
        }
    }
    
    /**
     * 데이터소스 ID 유효성 검증
     * 
     * @param datasourceId 검증할 데이터소스 ID
     * @throws BusinessException UUID 형식이 아닌 경우
     */
    private void validateDatasourceId(String datasourceId) {
        if (datasourceId == null || datasourceId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, 
                "데이터소스 ID는 필수입니다.");
        }
        
        try {
            UUID.fromString(datasourceId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, 
                "데이터소스 ID는 올바른 UUID 형식이어야 합니다: " + datasourceId);
        }
    }
    
    /**
     * 페이징 파라미터 유효성 검증
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @throws BusinessException 유효하지 않은 페이징 파라미터인 경우
     */
    private void validatePagingParameters(Integer page, Integer size) {
        if (page != null && page < 1) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, 
                "페이지 번호는 1 이상이어야 합니다: " + page);
        }
        
        if (size != null && (size < 1 || size > 100)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, 
                "페이지 크기는 1~100 사이의 값이어야 합니다: " + size);
        }
    }

    /**
     * 모든 데이터소스 완전 삭제
     * 
     * @return 삭제 결과
     */
    public Map<String, Object> hardDeleteAllDatasources() {
        log.debug("Hard deleting all datasources");

        try {
            Map<String, Object> result = datasourcesClient.hardDeleteAllDatasources();
            log.info("Successfully hard deleted all datasources");
            return result;
        } catch (BusinessException e) {
            log.error("Failed to hard delete all datasources (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to hard delete all datasources (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모든 데이터소스 완전 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터소스 완전 삭제
     * 
     * @param datasourceId 데이터소스 ID
     */
    public void hardDeleteDatasource(String datasourceId) {
        log.debug("Hard deleting datasource: {}", datasourceId);

        try {
            datasourcesClient.hardDeleteDatasource(datasourceId);
            log.info("Successfully hard deleted datasource: {}", datasourceId);
        } catch (BusinessException e) {
            log.error("Failed to hard delete datasource (BusinessException) - id: {}, message: {}", datasourceId,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to hard delete datasource (예상치 못한 오류) - id: {}", datasourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 완전 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 메타데이터 수정
     * 
     * @param datasourceId 데이터소스 ID
     * @param fileId       파일 ID
     * @param metadata     수정할 메타데이터
     * @return 수정된 메타데이터
     */
    public Map<String, Object> updateFileMetadata(String datasourceId, String fileId, Map<String, Object> metadata) {
        log.debug("Updating file metadata for datasource: {}, file: {}", datasourceId, fileId);

        try {
            Map<String, Object> result = datasourcesClient.updateFileMetadata(datasourceId, fileId, metadata);
            log.info("Successfully updated file metadata for datasource: {}, file: {}", datasourceId, fileId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to update file metadata (BusinessException) - datasourceId: {}, fileId: {}, message: {}",
                    datasourceId, fileId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update file metadata (예상치 못한 오류) - datasourceId: {}, fileId: {}", datasourceId, fileId,
                    e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파일 메타데이터 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터소스 작업을 Taskmanager로 수정
     * 
     * @param datasourceId 데이터소스 ID
     * @param taskUpdate   작업 수정 요청
     * @return 수정된 작업 정보
     */
    public Map<String, Object> updateDatasourceTaskByTaskmanager(String datasourceId, Map<String, Object> taskUpdate) {
        log.debug("Updating datasource task by taskmanager for datasource: {}", datasourceId);

        try {
            Map<String, Object> result = datasourcesClient.updateDatasourceTaskByTaskmanager(datasourceId, taskUpdate);
            log.info("Successfully updated datasource task by taskmanager for datasource: {}", datasourceId);
            return result;
        } catch (BusinessException e) {
            log.error(
                    "Failed to update datasource task by taskmanager (BusinessException) - datasourceId: {}, message: {}",
                    datasourceId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update datasource task by taskmanager (예상치 못한 오류) - datasourceId: {}", datasourceId,
                    e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 작업 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터소스 작업 생성
     * 
     * @param datasourceId 데이터소스 ID
     * @param taskRequest  작업 생성 요청
     * @return 생성된 작업 정보
     */
    public Map<String, Object> createDatasourceTask(String datasourceId, Map<String, Object> taskRequest) {
        log.debug("Creating datasource task for datasource: {}", datasourceId);

        try {
            Map<String, Object> result = datasourcesClient.createDatasourceTask(datasourceId, taskRequest);
            log.info("Successfully created datasource task for datasource: {}", datasourceId);
            return result;
        } catch (BusinessException e) {
            log.error("Failed to create datasource task (BusinessException) - datasourceId: {}, message: {}",
                    datasourceId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create datasource task (예상치 못한 오류) - datasourceId: {}", datasourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터소스 작업 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
}
