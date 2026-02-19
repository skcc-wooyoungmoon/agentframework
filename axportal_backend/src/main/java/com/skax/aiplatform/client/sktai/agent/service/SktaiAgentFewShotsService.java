package com.skax.aiplatform.client.sktai.agent.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentFewShotsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCommentCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCommentUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotTestRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCommentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotDependencyResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotExamplesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotInternalApiResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotItemsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagListResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTestResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotsResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Agent Few-Shots API 서비스
 * 
 * @author gyuHeeHwang
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentFewShotsService {
    
    private final SktaiAgentFewShotsClient sktaiAgentFewShotsClient;
    
    public FewShotsResponse getFewShots(String projectId, Integer page, Integer size, String sort, String filter, String search, Boolean release_only) {
        try {
            log.debug("Few-Shots 목록 조회 요청 - projectId: {}, page: {}, size: {}", projectId, page, size);
            FewShotsResponse response = sktaiAgentFewShotsClient.getFewShots(projectId, page, size, sort, filter, search, release_only);
            log.debug("Few-Shots 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shots 목록 조회 실패 (BusinessException) - projectId: {}, message: {}", 
                    projectId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shots 목록 조회 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shots 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public FewShotCreateResponse createFewShot(FewShotCreateRequest request) {
        try {
            log.debug("Few-Shot 생성 요청 - name: {}", request.getName());
            FewShotCreateResponse response = sktaiAgentFewShotsClient.createFewShot(request);
            log.debug("Few-Shot 생성 성공 - fewShotUuid: {}", response.getData().getFewShotUuid());
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 생성 실패 (BusinessException) - name: {}, message: {}", 
                    request.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 생성 실패 (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 생성에 실패했습니다: " + e.getMessage());
        }
    }

    public FewShotUpdateResponse updateFewShot(String fewShotUuid, FewShotUpdateRequest request) {
        try {
            log.debug("Few-Shot 수정 요청 - fewShotUuid: {}", fewShotUuid);
            FewShotUpdateResponse response = sktaiAgentFewShotsClient.updateFewShot(fewShotUuid, request);
            log.debug("Few-Shot 수정 성공 - fewShotUuid: {}", response.getData().toString());
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 수정 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 수정 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 수정에 실패했습니다: " + e.getMessage());
        }
    }

    public void deleteFewShot(String fewShotUuid) {
        try {
            log.debug("Few-Shot 삭제 요청 - fewShotUuid: {}", fewShotUuid);
            sktaiAgentFewShotsClient.deleteFewShot(fewShotUuid);
            log.debug("Few-Shot 삭제 성공 - fewShotUuid: {}", fewShotUuid);
        } catch (BusinessException e) {
            log.error("Few-Shot 삭제 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 삭제 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    public FewShotResponse getFewShot(String fewShotUuid) {
        try {
            log.debug("Few-Shot 상세 조회 요청 - fewShotUuid: {}", fewShotUuid);
            FewShotResponse response = sktaiAgentFewShotsClient.getFewShot(fewShotUuid);
            log.debug("Few-Shot 상세 조회 성공 - fewShotUuid: {}", fewShotUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 상세 조회 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 상세 조회 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public FewShotVersionResponse getLatestFewShotVersion(String fewShotUuid) {
        try {
            log.debug("Few-Shot 최신 버전 조회 요청 - fewShotUuid: {}", fewShotUuid);
            FewShotVersionResponse response = sktaiAgentFewShotsClient.getLatestFewShotVersion(fewShotUuid);
            log.debug("Few-Shot 최신 버전 조회 성공 - fewShotUuid: {}", fewShotUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 최신 버전 조회 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 최신 버전 조회 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 최신 버전 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public FewShotVersionsResponse getFewShotVersions(String fewShotUuid) {
        try {
            log.debug("Few-Shot 버전 목록 조회 요청 - fewShotUuid: {}", fewShotUuid);
            FewShotVersionsResponse response = sktaiAgentFewShotsClient.getFewShotVersions(fewShotUuid);
            log.debug("Few-Shot 버전 목록 조회 성공 - fewShotUuid: {}", fewShotUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 버전 목록 조회 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 버전 목록 조회 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 버전 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public FewShotExamplesResponse getFewShotExamples(String versionId) {
        try {
            log.debug("Few-Shot 예제 조회 요청 - versionId: {}", versionId);
            FewShotExamplesResponse response = sktaiAgentFewShotsClient.getFewShotExamples(versionId);
            log.debug("Few-Shot 예제 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 예제 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 예제 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 예제 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public FewShotItemsResponse getFewShotItems(String versionId, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("Few-Shots 아이템 목록 조회 요청 - versionId: {}, page: {}, size: {}", versionId, page, size);
            FewShotItemsResponse response = sktaiAgentFewShotsClient.getFewShotsItems(versionId, page, size, sort, filter, search);
            log.debug("Few-Shots 아이템 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shots 아이템 목록 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shots 아이템 목록 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shots 아이템 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public FewShotTagsResponse getFewShotTagsByVersion(String versionId) {
        try {
            log.debug("Few-Shot 태그 목록 조회 요청 - versionId: {}", versionId);
            FewShotTagsResponse response = sktaiAgentFewShotsClient.getFewShotTagsByVersion(versionId);
            log.debug("Few-Shot 태그 목록 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 태그 목록 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 태그 목록 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 태그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    // ===== 추가 메서드들 =====
    
    /**
     * Few-Shot 하드 삭제
     */
    public void hardDeleteFewShots() {
        try {
            log.debug("Few-Shot 하드 삭제 요청");
            sktaiAgentFewShotsClient.hardDeleteFewShots();
            log.debug("Few-Shot 하드 삭제 성공");
        } catch (BusinessException e) {
            log.error("Few-Shot 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 하드 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 하드 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot Excel 템플릿 다운로드
     */
    public Object exportFewShotTemplate() {
        try {
            log.debug("Few-Shot Excel 템플릿 다운로드 요청");
            Object response = sktaiAgentFewShotsClient.exportFewShotTemplate();
            log.debug("Few-Shot Excel 템플릿 다운로드 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot Excel 템플릿 다운로드 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot Excel 템플릿 다운로드 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot Excel 템플릿 다운로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 템플릿 기반 데이터 가져오기
     */
    public Object importFewShotData(String versionId, MultipartFile file) {
        try {
            log.debug("Few-Shot 데이터 가져오기 요청 - versionId: {}, filename: {}", versionId, file.getOriginalFilename());
            Object response = sktaiAgentFewShotsClient.importFewShotData(versionId, file);
            log.debug("Few-Shot 데이터 가져오기 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 데이터 가져오기 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 데이터 가져오기 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 데이터 가져오기에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 의존성 조회
     */
    public FewShotDependencyResponse getFewShotDependency(String versionId) {
        try {
            log.debug("Few-Shot 의존성 조회 요청 - versionId: {}", versionId);
            FewShotDependencyResponse response = sktaiAgentFewShotsClient.getFewShotDependency(versionId);
            log.debug("Few-Shot 의존성 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 의존성 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 의존성 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 의존성 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 댓글 목록 조회
     */
    public Object getFewShotComments(String versionId, Integer page, Integer size) {
        try {
            log.debug("Few-Shot 댓글 목록 조회 요청 - versionId: {}, page: {}, size: {}", versionId, page, size);
            Object response = sktaiAgentFewShotsClient.getFewShotComments(versionId, page, size);
            log.debug("Few-Shot 댓글 목록 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 댓글 목록 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 댓글 목록 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 댓글 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 댓글 생성
     */
    public FewShotCommentResponse createFewShotComment(String versionId, FewShotCommentCreateRequest request) {
        try {
            log.debug("Few-Shot 댓글 생성 요청 - versionId: {}", versionId);
            FewShotCommentResponse response = sktaiAgentFewShotsClient.createFewShotComment(versionId, request);
            log.debug("Few-Shot 댓글 생성 성공 - versionId: {}, commentUuid: {}", versionId, response.getCommentUuid());
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 댓글 생성 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 댓글 생성 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 댓글 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 댓글 수정
     */
    public FewShotCommentResponse updateFewShotComment(String commentUuid, FewShotCommentUpdateRequest request) {
        try {
            log.debug("Few-Shot 댓글 수정 요청 - commentUuid: {}", commentUuid);
            FewShotCommentResponse response = sktaiAgentFewShotsClient.updateFewShotComment(commentUuid, request);
            log.debug("Few-Shot 댓글 수정 성공 - commentUuid: {}", commentUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 댓글 수정 실패 (BusinessException) - commentUuid: {}, message: {}", commentUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 댓글 수정 실패 (예상치 못한 오류) - commentUuid: {}", commentUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 댓글 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 댓글 삭제
     */
    public void deleteFewShotComment(String commentUuid) {
        try {
            log.debug("Few-Shot 댓글 삭제 요청 - commentUuid: {}", commentUuid);
            sktaiAgentFewShotsClient.deleteFewShotComment(commentUuid);
            log.debug("Few-Shot 댓글 삭제 성공 - commentUuid: {}", commentUuid);
        } catch (BusinessException e) {
            log.error("Few-Shot 댓글 삭제 실패 (BusinessException) - commentUuid: {}, message: {}", commentUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 댓글 삭제 실패 (예상치 못한 오류) - commentUuid: {}", commentUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 댓글 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot Internal API 정보 조회
     */
    public FewShotInternalApiResponse getFewShotInternalApi(String fewShotUuid) {
        try {
            log.debug("Few-Shot Internal API 정보 조회 요청 - fewShotUuid: {}", fewShotUuid);
            FewShotInternalApiResponse response = sktaiAgentFewShotsClient.getFewShotInternalApi(fewShotUuid);
            log.debug("Few-Shot Internal API 정보 조회 성공 - fewShotUuid: {}", fewShotUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot Internal API 정보 조회 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot Internal API 정보 조회 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot Internal API 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot 테스트 및 통합 실행
     */
    public FewShotTestResponse testFewShotIntegration(String fewShotUuid, FewShotTestRequest request) {
        try {
            log.debug("Few-Shot 테스트 요청 - fewShotUuid: {}, testType: {}", fewShotUuid, request.getTestType());
            FewShotTestResponse response = sktaiAgentFewShotsClient.testFewShotIntegration(fewShotUuid, request);
            log.debug("Few-Shot 테스트 성공 - fewShotUuid: {}, testId: {}", fewShotUuid, response.getTestId());
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 테스트 실패 (BusinessException) - fewShotUuid: {}, message: {}", fewShotUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 테스트 실패 (예상치 못한 오류) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    public FewShotTagListResponse getFewShotTagsList() {
        try {
            log.debug("Few-Shot 태그 목록 조회 요청");
            FewShotTagListResponse response = sktaiAgentFewShotsClient.getFewShotTagsList();
            log.debug("Few-Shot 태그 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot 태그 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot 태그 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot 태그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Few-Shot Import (JSON)
     * 
     * <p>JSON 문자열을 받아서 Few-Shot을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param fewShotUuid Few-Shot ID
     * @param json JSON 문자열
     * @return 생성된 Few-Shot 정보
     */
    public FewShotCreateResponse importFewShot(String fewShotUuid, String json) {
        try {
            log.info("Few-Shot Import 요청 - fewShotUuid: {}, jsonLength: {}", fewShotUuid, json != null ? json.length() : 0);
            
            // fewShotUuid null 체크
            if (fewShotUuid == null || fewShotUuid.trim().isEmpty()) {
                throw new IllegalArgumentException("fewShotUuid는 필수입니다.");
            }
            
            // JSON 문자열을 Map<String, Object>로 변환 (parsedJson 형태로 변환)
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> jsonData = objectMapper.readValue(json, java.util.Map.class);
            
            log.info("Few-Shot Import 호출 - fewShotUuid: {}, jsonData type: {}", fewShotUuid, jsonData != null ? jsonData.getClass().getSimpleName() : "null");
            FewShotCreateResponse response = sktaiAgentFewShotsClient.importFewShot(fewShotUuid, jsonData);
            log.info("Few-Shot Import 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Few-Shot Import 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Few-Shot Import 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot Import에 실패했습니다: " + e.getMessage());
        }
    }
}
