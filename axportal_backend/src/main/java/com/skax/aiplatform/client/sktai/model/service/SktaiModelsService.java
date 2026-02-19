package com.skax.aiplatform.client.sktai.model.service;

import com.skax.aiplatform.client.sktai.model.SktaiModelsClient;
import com.skax.aiplatform.client.sktai.model.dto.request.*;
import com.skax.aiplatform.client.sktai.model.dto.response.*;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Models API 서비스
 * 
 * <p>SKTAI Model API와의 통신을 담당하는 서비스 계층입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>모델 관리</strong>: CRUD 작업</li>
 *   <li><strong>버전 관리</strong>: 모델 버전 생성, 조회, 수정, 삭제</li>
 *   <li><strong>엔드포인트 관리</strong>: API 엔드포인트 관리</li>
 *   <li><strong>메타데이터 관리</strong>: 태그, 작업, 언어 관리</li>
 *   <li><strong>파일 관리</strong>: 모델 파일 업로드</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiModelsService {
    
    private final SktaiModelsClient sktaiModelsClient;

    
    // === 기본 모델 관리 ===
    
    /**
     * 모델 등록
     * 
     * @param request 모델 생성 요청
     * @return 생성된 모델 정보
     */
    public ModelRead registerModel(ModelCreate request) {
        try {
            log.debug("모델 등록 요청 - name: {}", request.getName());
            ModelRead response = sktaiModelsClient.registerModel(request);
            log.debug("모델 등록 성공 - modelId: {}", response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("모델 등록 실패 - name: {}", request.getName(), e);
            throw e;
        } catch (Exception e) {
            log.error("모델 등록 실패 - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 등록에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @param ids 모델 ID 목록
     * @return 페이징된 모델 목록
     */
    public ModelsRead readModels(Integer page, Integer size, String sort, String filter, String search, String ids) {
        try {
            log.debug("모델 목록 조회 요청 - page: {}, size: {}", page, size);
            ModelsRead response = sktaiModelsClient.readModels(page, size, sort, filter, search, ids);
            log.debug("모델 목록 조회 성공 - count: {}", response.getData().size());
            return response;
        } catch (BusinessException e) {
            log.error("모델 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 타입 조회
     * 
     * @return 모델 타입 목록
     */
    public List<String> readModelTypes() {
        try {
            log.debug("모델 타입 조회 요청");
            List<String> response = sktaiModelsClient.readModelTypes();
            log.debug("모델 타입 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("모델 타입 조회 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("모델 타입 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 타입 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 태그 조회
     * 
     * @return 모델 태그 목록
     */
    public List<String> readModelTags() {
        try {
            log.debug("모델 태그 조회 요청");
            List<String> response = sktaiModelsClient.readModelTags();
            log.debug("모델 태그 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("모델 태그 조회 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("모델 태그 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 태그 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 상세 조회
     * 
     * @param modelId 모델 ID
     * @return 모델 상세 정보
     */
    public ModelRead readModel(String modelId) {
        try {
            log.debug("모델 상세 조회 요청 - modelId: {}", modelId);
            ModelRead response = sktaiModelsClient.readModel(modelId);
            log.debug("모델 상세 조회 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 상세 조회 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 상세 조회 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 수정
     * 
     * @param modelId 모델 ID
     * @param request 모델 수정 요청
     * @return 수정된 모델 정보
     */
    public ModelRead editModel(String modelId, ModelUpdate request) {
        try {
            log.debug("모델 수정 요청 - modelId: {}", modelId);
            ModelRead response = sktaiModelsClient.editModel(modelId, request);
            log.debug("모델 수정 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 수정 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 수정 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 삭제
     * 
     * @param modelId 모델 ID
     */
    public void removeModel(String modelId) {
        try {
            log.debug("모델 삭제 요청 - modelId: {}", modelId);
            sktaiModelsClient.removeModel(modelId);
            log.debug("모델 삭제 성공 - modelId: {}", modelId);
        } catch (BusinessException e) {
            log.error("모델 삭제 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 삭제 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 복구
     * 
     * @param modelId 모델 ID
     */
    public void recoverModel(String modelId) {
        try {
            log.debug("모델 복구 요청 - modelId: {}", modelId);
            sktaiModelsClient.recoverModel(modelId);
            log.debug("모델 복구 성공 - modelId: {}", modelId);
        } catch (BusinessException e) {
            log.error("모델 복구 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 복구 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 복구에 실패했습니다: " + e.getMessage());
        }
    }
    
    // === 모델 메타데이터 관리 ===
    
    /**
     * 모델에 태그 추가
     * 
     * @param modelId 모델 ID
     * @param tags 추가할 태그 목록
     * @return 업데이트된 모델 정보
     */
    public ModelRead addTagsToModel(String modelId, ModelTagRequest[] tags) {
        try {
            log.debug("모델 태그 추가 요청 - modelId: {}, tagCount: {}", modelId, tags.length);
            ModelRead response = sktaiModelsClient.addTagsToModel(modelId, tags);
            log.debug("모델 태그 추가 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 태그 추가 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 태그 추가 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 태그 추가에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델에서 태그 제거
     * 
     * @param modelId 모델 ID
     * @param tags 제거할 태그 목록
     * @return 업데이트된 모델 정보
     */
    public ModelRead removeTagsFromModel(String modelId, ModelTagRequest[] tags) {
        try {
            log.debug("모델 태그 제거 요청 - modelId: {}, tagCount: {}", modelId, tags.length);
            ModelRead response = sktaiModelsClient.removeTagsFromModel(modelId, tags);
            log.debug("모델 태그 제거 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 태그 제거 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 태그 제거 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 태그 제거에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델에 작업 추가
     * 
     * @param modelId 모델 ID
     * @param tasks 추가할 작업 목록
     * @return 업데이트된 모델 정보
     */
    public ModelRead addTasksToModel(String modelId, ModelTaskRequest[] tasks) {
        try {
            log.debug("모델 작업 추가 요청 - modelId: {}, taskCount: {}", modelId, tasks.length);
            ModelRead response = sktaiModelsClient.addTasksToModel(modelId, tasks);
            log.debug("모델 작업 추가 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 작업 추가 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 작업 추가 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 작업 추가에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델에서 작업 제거
     * 
     * @param modelId 모델 ID
     * @param tasks 제거할 작업 목록
     * @return 업데이트된 모델 정보
     */
    public ModelRead removeTasksFromModel(String modelId, ModelTaskRequest[] tasks) {
        try {
            log.debug("모델 작업 제거 요청 - modelId: {}, taskCount: {}", modelId, tasks.length);
            ModelRead response = sktaiModelsClient.removeTasksFromModel(modelId, tasks);
            log.debug("모델 작업 제거 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 작업 제거 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 작업 제거 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 작업 제거에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델에 언어 추가
     * 
     * @param modelId 모델 ID
     * @param languages 추가할 언어 목록
     * @return 업데이트된 모델 정보
     */
    public ModelRead addLanguagesToModel(String modelId, ModelLanguageRequest[] languages) {
        try {
            log.debug("모델 언어 추가 요청 - modelId: {}, languageCount: {}", modelId, languages.length);
            ModelRead response = sktaiModelsClient.addLanguagesToModel(modelId, languages);
            log.debug("모델 언어 추가 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 언어 추가 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 언어 추가 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 언어 추가에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델에서 언어 제거
     * 
     * @param modelId 모델 ID
     * @param languages 제거할 언어 목록
     * @return 업데이트된 모델 정보
     */
    public ModelRead removeLanguagesFromModel(String modelId, ModelLanguageRequest[] languages) {
        try {
            log.debug("모델 언어 제거 요청 - modelId: {}, languageCount: {}", modelId, languages.length);
            ModelRead response = sktaiModelsClient.removeLanguagesFromModel(modelId, languages);
            log.debug("모델 언어 제거 성공 - modelId: {}", modelId);
            return response;
         } catch (BusinessException e) {
            log.error("모델 언어 제거 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 언어 제거 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 언어 제거에 실패했습니다: " + e.getMessage());
        }
    }
    
    // === 파일 관리 ===
    
    /**
     * 모델 파일 업로드
     * 
     * @param file 업로드할 모델 파일
     * @return 업로드 결과
     */
    public Object uploadModelFile(MultipartFile file) {
        try {
            log.debug("모델 파일 업로드 요청 - fileName: {}", file.getOriginalFilename());
            Object response = sktaiModelsClient.uploadModelFile(file);
            log.debug("모델 파일 업로드 성공 - fileName: {}", file.getOriginalFilename());
            return response;
        } catch (BusinessException e) {
            log.error("모델 파일 업로드 실패 - fileName: {}", file.getOriginalFilename(), e);
            throw e;
        } catch (Exception e) {
            log.error("모델 파일 업로드 실패 - fileName: {}", file.getOriginalFilename(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    // === 유틸리티 ===
    
    /**
     * 모델 하드 삭제
     * 
     * @return 삭제 결과
     */
    public Object hardRemoveModel() {
        try {
            log.debug("모델 하드 삭제 요청");
            Object response = sktaiModelsClient.hardRemoveModel();
            log.debug("모델 하드 삭제 성공");
            return response;
        } catch (BusinessException e) {
            log.error("모델 하드 삭제 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("모델 하드 삭제 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 하드 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 Lookup
     * 
     * @param request Lookup 요청
     * @return 조회 결과
     */
    public ModelLookupResponse lookupModels(ModelLookupRequest request) {
        try {
            log.debug("모델 Lookup 요청 - itemCount: {}", request.getItems().size());
            ModelLookupResponse response = sktaiModelsClient.lookupModels(request);
            log.debug("모델 Lookup 성공 - resultCount: {}", response.getResults().size());
            return response;
        } catch (BusinessException e) {
            log.error("모델 Lookup 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("모델 Lookup 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 Lookup에 실패했습니다: " + e.getMessage());
        }
    }
    
    // === 모델 버전 관리 ===
    
    /**
     * 모델 버전 등록
     * 
     * @param modelId 모델 ID
     * @param request 버전 생성 요청
     * @return 생성된 버전 정보
     */
    public ModelVersionRead registerModelVersion(String modelId, ModelVersionCreate request) {
        try {
            log.debug("모델 버전 등록 요청 - modelId: {}", modelId);
            ModelVersionRead response = sktaiModelsClient.registerModelVersion(modelId, request);
            log.debug("모델 버전 등록 성공 - modelId: {}, versionId: {}", modelId, response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("모델 버전 등록 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 버전 등록 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 버전 등록에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 버전 목록 조회
     * 
     * @param modelId 모델 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @param ids 버전 ID 목록
     * @return 페이징된 버전 목록
     */
    public ModelVersionsRead readModelVersions(String modelId, Integer page, Integer size, String sort, String filter, String search, String ids) {
        try {
            log.debug("모델 버전 목록 조회 요청 - modelId: {}, page: {}, size: {}", modelId, page, size);
            ModelVersionsRead response = sktaiModelsClient.readModelVersions(modelId, page, size, sort, filter, search, ids);
            log.debug("모델 버전 목록 조회 성공 - modelId: {}, count: {}", modelId, response.getData().size());
            return response;
        } catch (BusinessException e) {
            log.error("모델 버전 목록 조회 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 버전 목록 조회 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 버전 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 버전 조회 (버전 ID만으로)
     * 
     * @param versionId 버전 ID
     * @return 버전 정보
     */
    public ModelVersionRead readVersion(String versionId) {
        try {
            log.debug("버전 조회 요청 - versionId: {}", versionId);
            ModelVersionRead response = sktaiModelsClient.readVersion(versionId);
            log.debug("버전 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("버전 조회 실패 - versionId: {}", versionId, e);
            throw e;
        } catch (Exception e) {
            log.error("버전 조회 실패 - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "버전 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 버전 조회 (모델 ID + 버전 ID)
     * 
     * @param modelId 모델 ID
     * @param versionId 버전 ID
     * @return 모델 버전 정보
     */
    public ModelVersionRead readModelVersion(String modelId, String versionId) {
        try {
            log.debug("모델 버전 조회 요청 - modelId: {}, versionId: {}", modelId, versionId);
            ModelVersionRead response = sktaiModelsClient.readModelVersion(modelId, versionId);
            log.debug("모델 버전 조회 성공 - modelId: {}, versionId: {}", modelId, versionId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 버전 조회 실패 - modelId: {}, versionId: {}", modelId, versionId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 버전 조회 실패 - modelId: {}, versionId: {}", modelId, versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 버전 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 버전 삭제
     * 
     * @param modelId 모델 ID
     * @param versionId 버전 ID
     */
    public void removeModelVersion(String modelId, String versionId) {
        try {
            log.debug("모델 버전 삭제 요청 - modelId: {}, versionId: {}", modelId, versionId);
            sktaiModelsClient.removeModelVersion(modelId, versionId);
            log.debug("모델 버전 삭제 성공 - modelId: {}, versionId: {}", modelId, versionId);
        } catch (BusinessException e) {
            log.error("모델 버전 삭제 실패 - modelId: {}, versionId: {}", modelId, versionId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 버전 삭제 실패 - modelId: {}, versionId: {}", modelId, versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 버전 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 버전 수정
     * 
     * @param modelId 모델 ID
     * @param versionId 버전 ID
     * @param request 버전 수정 요청
     * @return 수정된 버전 정보
     */
    public ModelVersionRead editModelVersion(String modelId, String versionId, ModelVersionUpdate request) {
        try {
            log.debug("모델 버전 수정 요청 - modelId: {}, versionId: {}", modelId, versionId);
            ModelVersionRead response = sktaiModelsClient.editModelVersion(modelId, versionId, request);
            log.debug("모델 버전 수정 성공 - modelId: {}, versionId: {}", modelId, versionId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 버전 수정 실패 - modelId: {}, versionId: {}", modelId, versionId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 버전 수정 실패 - modelId: {}, versionId: {}", modelId, versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 버전 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 버전을 기본 모델로 승격
     * 
     * @param versionId 버전 ID
     * @param request 승격 요청
     * @return 승격된 모델 정보
     */
    public ModelRead promoteVersionToBaseModel(String versionId, ModelPromoteRequest request) {
        try {
            log.debug("버전 승격 요청 - versionId: {}", versionId);
            ModelRead response = sktaiModelsClient.promoteVersionToBaseModel(versionId, request);
            log.debug("버전 승격 성공 - versionId: {}, modelId: {}", versionId, response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("버전 승격 실패 - versionId: {}", versionId, e);
            throw e;
        } catch (Exception e) {
            log.error("버전 승격 실패 - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "버전 승격에 실패했습니다: " + e.getMessage());
        }
    }
    
    // === 모델 엔드포인트 관리 ===
    
    /**
     * 모델 엔드포인트 등록
     * 
     * @param modelId 모델 ID
     * @param request 엔드포인트 생성 요청
     * @return 생성된 엔드포인트 정보
     */
    public ModelEndpointRead registerModelEndpoint(String modelId, ModelEndpointCreate request) {
        try {
            log.debug("모델 엔드포인트 등록 요청 - modelId: {}", modelId);
            ModelEndpointRead response = sktaiModelsClient.registerModelEndpoint(modelId, request);
            log.debug("모델 엔드포인트 등록 성공 - modelId: {}, endpointId: {}", modelId, response.getId());
            return response;
        } catch (BusinessException e) {
            log.error("모델 엔드포인트 등록 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 엔드포인트 등록 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 엔드포인트 등록에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 엔드포인트 목록 조회
     * 
     * @param modelId 모델 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 엔드포인트 목록
     */
    public ModelEndpointsRead readModelEndpoints(String modelId, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("모델 엔드포인트 목록 조회 요청 - modelId: {}, page: {}, size: {}", modelId, page, size);
            ModelEndpointsRead response = sktaiModelsClient.readModelEndpoints(modelId, page, size, sort, filter, search);
            log.debug("모델 엔드포인트 목록 조회 성공 - modelId: {}, count: {}", modelId, response.getData().size());
            return response;
        } catch (BusinessException e) {
            log.error("모델 엔드포인트 목록 조회 실패 - modelId: {}", modelId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 엔드포인트 목록 조회 실패 - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 엔드포인트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 엔드포인트 조회
     * 
     * @param modelId 모델 ID
     * @param endpointId 엔드포인트 ID
     * @return 엔드포인트 정보
     */
    public ModelEndpointRead readModelEndpoint(String modelId, String endpointId) {
        try {
            log.debug("모델 엔드포인트 조회 요청 - modelId: {}, endpointId: {}", modelId, endpointId);
            ModelEndpointRead response = sktaiModelsClient.readModelEndpoint(modelId, endpointId);
            log.debug("모델 엔드포인트 조회 성공 - modelId: {}, endpointId: {}", modelId, endpointId);
            return response;
        } catch (BusinessException e) {
            log.error("모델 엔드포인트 조회 실패 - modelId: {}, endpointId: {}", modelId, endpointId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 엔드포인트 조회 실패 - modelId: {}, endpointId: {}", modelId, endpointId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 엔드포인트 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 엔드포인트 삭제
     * 
     * @param modelId 모델 ID
     * @param endpointId 엔드포인트 ID
     */
    public void removeModelEndpoint(String modelId, String endpointId) {
        try {
            log.debug("모델 엔드포인트 삭제 요청 - modelId: {}, endpointId: {}", modelId, endpointId);
            sktaiModelsClient.removeModelEndpoint(modelId, endpointId);
            log.debug("모델 엔드포인트 삭제 성공 - modelId: {}, endpointId: {}", modelId, endpointId);
        } catch (BusinessException e) {
            log.error("모델 엔드포인트 삭제 실패 - modelId: {}, endpointId: {}", modelId, endpointId, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 엔드포인트 삭제 실패 - modelId: {}, endpointId: {}", modelId, endpointId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 엔드포인트 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Model Export
     * 
     * <p>지정된 모델의 Export용 데이터를 조회합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param modelId Model ID
     * @return Export용 Model 데이터
     */
    public ModelExportResponse exportModel(String modelId) {
        try {
            log.info("Model Export 요청 - modelId: {}", modelId);
            ModelExportResponse response = sktaiModelsClient.exportModel(modelId);
            log.info("Model Export 성공 - modelId: {}", modelId);
            return response;
        } catch (BusinessException e) {
            log.error("Model Export 실패 (BusinessException) - modelId: {}, message: {}", modelId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Model Export 실패 (예상치 못한 오류) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Export에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Model Import
     * 
     * <p>JSON 데이터를 받아서 Model을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param request Model Import 요청 데이터
     * @return 생성된 Model 정보
     */
    public ModelImportResponse importModel(ModelImportRequest request) {
        try {
            log.info("Model Import 요청");
            ModelImportResponse response = sktaiModelsClient.importModel(request);
            log.info("Model Import 성공 - modelId: {}, status: {}", response.getId(), response.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("Model Import 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Model Import 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Import에 실패했습니다: " + e.getMessage());
        }
    }
}
