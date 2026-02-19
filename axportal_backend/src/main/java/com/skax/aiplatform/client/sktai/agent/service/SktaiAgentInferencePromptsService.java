package com.skax.aiplatform.client.sktai.agent.service;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentInferencePromptsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.*;
import com.skax.aiplatform.client.sktai.agent.dto.response.*;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Agent Inference Prompts API 서비스
 *
 * <p>SKTAI Agent의 Inference Prompts 관련 모든 API를 제공하는 서비스 레이어입니다.
 * Prompt 생성, 조회, 수정, 삭제부터 버전 관리, 댓글, 태그, 변수 테스트까지
 * 포괄적인 Prompt 관리 기능을 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>기본 CRUD</strong>: Prompt 생성, 조회, 수정, 삭제</li>
 *   <li><strong>버전 관리</strong>: Prompt 버전 목록 및 최신 버전 조회</li>
 *   <li><strong>메시지/변수</strong>: Prompt 메시지 및 변수 관리</li>
 *   <li><strong>태그 관리</strong>: 태그 CRUD, 검색, 목록 조회</li>
 *   <li><strong>댓글 시스템</strong>: 댓글 생성, 수정, 삭제, 목록 조회</li>
 *   <li><strong>고급 기능</strong>: 복사, 하드 삭제, 템플릿, 변수 테스트</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktaiAgentInferencePromptsClient SKTAI API 클라이언트
 * @since 2025-08-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentInferencePromptsService {

    private final SktaiAgentInferencePromptsClient sktaiAgentInferencePromptsClient;

    /**
     * Inference Prompt 목록 조회
     *
     * @param projectId    프로젝트 ID
     * @param page         페이지 번호
     * @param size         페이지 크기
     * @param sort         정렬 기준
     * @param filter       필터링 조건
     * @param search       검색어
     * @param release_only 릴리즈 전용 여부 (기본값: false)
     * @return Inference Prompt 목록
     */
    public PromptsResponse getInferencePrompts(String projectId, Integer page, Integer size, String sort, String filter, String search, Boolean release_only) {
        try {
            log.debug("Inference Prompts 목록 조회 요청 - projectId: {}, page: {}, size: {}", projectId, page, size);
            PromptsResponse response = sktaiAgentInferencePromptsClient.getInferencePrompts(projectId, page, size, sort, filter, search, release_only);
            log.debug("Inference Prompts 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompts 목록 조회 실패 (BusinessException) - projectId: {}, message: {}", projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompts 목록 조회 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompts 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 성능 측정용 Inference Prompt 목록 조회
     *
     * @param projectId    프로젝트 ID
     * @param ignoreOption 무시 옵션
     * @param page         페이지 번호
     * @param size         페이지 크기
     * @param sort         정렬 기준
     * @param filter       필터링 조건
     * @param search       검색어
     * @return Inference Prompt 목록
     */
    public PromptsResponse getInferencePromptsPerf(String projectId, Integer ignoreOption, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("성능 측정용 Inference Prompts 목록 조회 요청 - projectId: {}, page: {}, size: {}", projectId, page, size);
            PromptsResponse response = sktaiAgentInferencePromptsClient.getInferencePromptsPerf(projectId, ignoreOption, page, size, sort, filter, search);
            log.debug("성능 측정용 Inference Prompts 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("성능 측정용 Inference Prompts 목록 조회 실패 (BusinessException) - projectId: {}, message: {}", projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("성능 측정용 Inference Prompts 목록 조회 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "성능 측정용 Inference Prompts 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 생성
     *
     * @param request Prompt 생성 요청
     * @return 생성된 Prompt 정보
     */
    public PromptCreateResponse createInferencePrompt(PromptCreateRequest request) {
        try {
            log.debug("Inference Prompt 생성 요청");
            PromptCreateResponse response = sktaiAgentInferencePromptsClient.createInferencePrompt(request);
            log.debug("Inference Prompt 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 상세 조회
     *
     * @param promptUuid Prompt UUID
     * @return Prompt 상세 정보
     */
    public PromptResponse getInferencePrompt(String promptUuid) {
        try {
            log.debug("Inference Prompt 상세 조회 요청 - promptUuid: {}", promptUuid);
            PromptResponse response = sktaiAgentInferencePromptsClient.getInferencePrompt(promptUuid);
            log.debug("Inference Prompt 상세 조회 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 상세 조회 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 상세 조회 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 수정
     *
     * @param promptUuid Prompt UUID
     * @param request    Prompt 수정 요청
     * @return 수정된 Prompt 정보
     */
    public PromptUpdateOrDeleteResponse updateInferencePrompt(String promptUuid, PromptUpdateRequest request) {
        try {
            log.debug("Inference Prompt 수정 요청 - promptUuid: {}", promptUuid);
            PromptUpdateOrDeleteResponse response = sktaiAgentInferencePromptsClient.updateInferencePrompt(promptUuid, request);
            log.debug("Inference Prompt 수정 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 수정 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 수정 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 삭제
     *
     * @param promptUuid Prompt UUID
     */
    public void deleteInferencePrompt(String promptUuid) {
        try {
            log.debug("Inference Prompt 삭제 요청 - promptUuid: {}", promptUuid);
            sktaiAgentInferencePromptsClient.deleteInferencePrompt(promptUuid);
            log.debug("Inference Prompt 삭제 성공 - promptUuid: {}", promptUuid);
        } catch (BusinessException e) {
            log.error("Inference Prompt 삭제 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 삭제 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 최신 버전 조회
     *
     * @param promptUuid Prompt UUID
     * @return Prompt 최신 버전 정보
     */
    public PromptVersionResponse getLatestInferencePromptVersion(String promptUuid) {
        try {
            log.debug("Inference Prompt 최신 버전 조회 요청 - promptUuid: {}", promptUuid);
            PromptVersionResponse response = sktaiAgentInferencePromptsClient.getLatestInferencePromptVersion(promptUuid);
            log.debug("Inference Prompt 최신 버전 조회 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 최신 버전 조회 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 최신 버전 조회 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 최신 버전 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 버전 목록 조회
     *
     * @param promptUuid Prompt UUID
     * @return Prompt 버전 목록
     */
    public PromptVersionsResponse getInferencePromptVersions(String promptUuid) {
        try {
            log.debug("Inference Prompt 버전 목록 조회 요청 - promptUuid: {}", promptUuid);
            PromptVersionsResponse response = sktaiAgentInferencePromptsClient.getInferencePromptVersions(promptUuid);
            log.debug("Inference Prompt 버전 목록 조회 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 버전 목록 조회 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 버전 목록 조회 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 버전 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 메시지 목록 조회
     *
     * @param versionId 버전 ID
     * @return Prompt 메시지 목록
     */
    public PromptMessagesResponse getInferencePromptMessages(String versionId) {
        try {
            log.debug("Inference Prompt 메시지 목록 조회 요청 - versionId: {}", versionId);
            PromptMessagesResponse response = sktaiAgentInferencePromptsClient.getInferencePromptMessages(versionId);
            log.debug("Inference Prompt 메시지 목록 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 메시지 목록 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 메시지 목록 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 메시지 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 변수 목록 조회
     *
     * @param versionId 버전 ID
     * @return Prompt 변수 목록
     */
    public PromptVariablesResponse getInferencePromptVariables(String versionId) {
        try {
            log.debug("Inference Prompt 변수 목록 조회 요청 - versionId: {}", versionId);
            PromptVariablesResponse response = sktaiAgentInferencePromptsClient.getInferencePromptVariables(versionId);
            log.debug("Inference Prompt 변수 목록 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 변수 목록 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 변수 목록 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 변수 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 태그 조회 (버전별)
     *
     * @param versionId 버전 ID
     * @return Prompt 태그 정보
     */
    public PromptTagsResponse getInferencePromptTagsByVersion(String versionId) {
        try {
            log.debug("Inference Prompt 태그 조회 요청 - versionId: {}", versionId);
            PromptTagsResponse response = sktaiAgentInferencePromptsClient.getInferencePromptTagsByVersion(versionId);
            log.debug("Inference Prompt 태그 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 태그 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 태그 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 태그 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 태그 목록 조회
     *
     * @return 태그 목록
     */
    public PromptTagListResponse getInferencePromptTagsList() {
        try {
            log.debug("Inference Prompt 태그 목록 조회 요청");
            PromptTagListResponse response = sktaiAgentInferencePromptsClient.getInferencePromptTagsList();
            log.debug("Inference Prompt 태그 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 태그 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 태그 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 태그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 태그로 Inference Prompt 검색
     *
     * @param filters 검색 필터 (태그)
     * @return 검색된 Prompt 목록
     */
    public PromptFilterByTagsResponse searchInferencePromptsByTags(String filters) {
        try {
            log.debug("태그로 Inference Prompt 검색 요청 - filters: {}", filters);
            PromptFilterByTagsResponse response = sktaiAgentInferencePromptsClient.searchInferencePromptsByTags(filters);
            log.debug("태그로 Inference Prompt 검색 성공 - filters: {}", filters);
            return response;
        } catch (BusinessException e) {
            log.error("태그로 Inference Prompt 검색 실패 (BusinessException) - filters: {}, message: {}", filters, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("태그로 Inference Prompt 검색 실패 (예상치 못한 오류) - filters: {}", filters, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "태그로 Inference Prompt 검색에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 복사
     *
     * @param promptUuid 복사할 Prompt UUID
     * @param request    복사 요청
     * @return 복사된 Prompt 정보
     */
    public PromptCreateResponse copyInferencePrompt(String promptUuid, PromptCopyRequest request) {
        try {
            log.debug("Inference Prompt 복사 요청 - promptUuid: {}", promptUuid);
            PromptCreateResponse response = sktaiAgentInferencePromptsClient.copyInferencePrompt(promptUuid, request);
            log.debug("Inference Prompt 복사 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 복사 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 복사 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 복사에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 댓글 목록 조회
     *
     * @param versionId 버전 ID
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @return 댓글 목록
     */
    public PromptCommentsResponse getInferencePromptComments(String versionId, Integer page, Integer size) {
        try {
            log.debug("Inference Prompt 댓글 목록 조회 요청 - versionId: {}, page: {}, size: {}", versionId, page, size);
            PromptCommentsResponse response = sktaiAgentInferencePromptsClient.getInferencePromptComments(versionId, page, size);
            log.debug("Inference Prompt 댓글 목록 조회 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 댓글 목록 조회 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 댓글 목록 조회 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 댓글 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 댓글 생성
     *
     * @param versionId 버전 ID
     * @param request   댓글 생성 요청
     * @return 생성된 댓글 정보
     */
    public PromptCommentResponse createInferencePromptComment(String versionId, PromptCommentCreateRequest request) {
        try {
            log.debug("Inference Prompt 댓글 생성 요청 - versionId: {}", versionId);
            PromptCommentResponse response = sktaiAgentInferencePromptsClient.createInferencePromptComment(versionId, request);
            log.debug("Inference Prompt 댓글 생성 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 댓글 생성 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 댓글 생성 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 댓글 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 댓글 수정
     *
     * @param commentUuid 댓글 UUID
     * @param request     댓글 수정 요청
     * @return 수정된 댓글 정보
     */
    public PromptCommentResponse updateInferencePromptComment(String commentUuid, PromptCommentUpdateRequest request) {
        try {
            log.debug("Inference Prompt 댓글 수정 요청 - commentUuid: {}", commentUuid);
            PromptCommentResponse response = sktaiAgentInferencePromptsClient.updateInferencePromptComment(commentUuid, request);
            log.debug("Inference Prompt 댓글 수정 성공 - commentUuid: {}", commentUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 댓글 수정 실패 (BusinessException) - commentUuid: {}, message: {}", commentUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 댓글 수정 실패 (예상치 못한 오류) - commentUuid: {}", commentUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 댓글 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 댓글 삭제
     *
     * @param commentUuid 댓글 UUID
     * @return 삭제 결과
     */
    public CommonResponse deleteInferencePromptComment(String commentUuid) {
        try {
            log.debug("Inference Prompt 댓글 삭제 요청 - commentUuid: {}", commentUuid);
            CommonResponse response = sktaiAgentInferencePromptsClient.deleteInferencePromptComment(commentUuid);
            log.debug("Inference Prompt 댓글 삭제 성공 - commentUuid: {}", commentUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 댓글 삭제 실패 (BusinessException) - commentUuid: {}, message: {}", commentUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 댓글 삭제 실패 (예상치 못한 오류) - commentUuid: {}", commentUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 댓글 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 버전 삭제
     *
     * @param versionId 버전 ID
     * @return 삭제 결과
     */
    public CommonResponse deleteInferencePromptVersion(String versionId) {
        try {
            log.debug("Inference Prompt 버전 삭제 요청 - versionId: {}", versionId);
            CommonResponse response = sktaiAgentInferencePromptsClient.deleteInferencePromptVersion(versionId);
            log.debug("Inference Prompt 버전 삭제 성공 - versionId: {}", versionId);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 버전 삭제 실패 (BusinessException) - versionId: {}, message: {}", versionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 버전 삭제 실패 (예상치 못한 오류) - versionId: {}", versionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 버전 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 하드 삭제
     */
    public void hardDeleteInferencePrompts() {
        try {
            log.debug("Inference Prompt 하드 삭제 요청");
            sktaiAgentInferencePromptsClient.hardDeleteInferencePrompts();
            log.debug("Inference Prompt 하드 삭제 성공");
        } catch (BusinessException e) {
            log.error("Inference Prompt 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 하드 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 하드 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 내장 템플릿 목록 조회
     *
     * @return 내장 템플릿 목록
     */
    public BuiltinPromptsResponse getBuiltinInferencePromptTemplates() {
        try {
            log.debug("Inference Prompt 내장 템플릿 목록 조회 요청");
            BuiltinPromptsResponse response = sktaiAgentInferencePromptsClient.getBuiltinInferencePromptTemplates();
            log.debug("Inference Prompt 내장 템플릿 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 내장 템플릿 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 내장 템플릿 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 내장 템플릿 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 변수 테스트
     *
     * @param promptUuid Prompt UUID
     * @param request    변수 테스트 요청
     * @return 테스트 결과
     */
    public CommonResponse testInferencePromptVariables(String promptUuid, TestPromptVariablesRequest request) {
        try {
            log.debug("Inference Prompt 변수 테스트 요청 - promptUuid: {}", promptUuid);
            CommonResponse response = sktaiAgentInferencePromptsClient.testInferencePromptVariables(promptUuid, request);
            log.debug("Inference Prompt 변수 테스트 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 변수 테스트 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 변수 테스트 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 변수 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt API 통합 조회
     *
     * @param promptUuid Prompt UUID
     * @return API 통합 정보
     */
    public PromptIntegrationResponse getInferencePromptIntegrationApi(String promptUuid) {
        try {
            log.debug("Inference Prompt API 통합 조회 요청 - promptUuid: {}", promptUuid);
            PromptIntegrationResponse response = sktaiAgentInferencePromptsClient.getInferencePromptIntegrationApi(promptUuid);
            log.debug("Inference Prompt API 통합 조회 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt API 통합 조회 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt API 통합 조회 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt API 통합 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt API 변수 조회
     *
     * @param promptUuid Prompt UUID
     * @return API 변수 정보
     */
    public PromptVariablesResponse getInferencePromptVariablesApi(String promptUuid) {
        try {
            log.debug("Inference Prompt API 변수 조회 요청 - promptUuid: {}", promptUuid);
            PromptVariablesResponse response = sktaiAgentInferencePromptsClient.getInferencePromptVariablesApi(promptUuid);
            log.debug("Inference Prompt API 변수 조회 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt API 변수 조회 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt API 변수 조회 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt API 변수 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 통합 테스트
     *
     * @param promptUuid Prompt UUID
     * @param request    테스트 요청
     * @return 테스트 결과
     */
    public CommonResponse testInferencePromptIntegration(String promptUuid, PromptTestRequest request) {
        try {
            log.debug("Inference Prompt 통합 테스트 요청 - promptUuid: {}", promptUuid);
            CommonResponse response = sktaiAgentInferencePromptsClient.testInferencePromptIntegration(promptUuid, request);
            log.debug("Inference Prompt 통합 테스트 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 통합 테스트 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 통합 테스트 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 통합 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt 태그 검색 테스트
     *
     * @param request 태그 검색 테스트 요청
     * @return 태그 검색 결과
     */
    public PromptFilterByTagsResponse testInferencePromptSearchByTags(TagSearchTestRequest request) {
        try {
            log.debug("Inference Prompt 태그 검색 테스트 요청");
            PromptFilterByTagsResponse response = sktaiAgentInferencePromptsClient.testInferencePromptSearchByTags(request);
            log.debug("Inference Prompt 태그 검색 테스트 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt 태그 검색 테스트 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Inference Prompt 태그 검색 테스트 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt 태그 검색 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt Export 조회
     *
     * <p>Import를 위한 통합 데이터를 조회합니다.
     * messages, variables, tags를 모두 포함합니다.</p>
     *
     * @param promptUuid Prompt UUID
     * @return Export 데이터 응답
     */
    public PromptExportResponse getPromptExport(String promptUuid) {
        try {
            log.debug("Inference Prompt Export 조회 요청 - promptUuid: {}", promptUuid);
            PromptExportResponse response = sktaiAgentInferencePromptsClient.getPromptExport(promptUuid);
            log.debug("Inference Prompt Export 조회 성공 - promptUuid: {}", promptUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt Export 조회 실패 (BusinessException) - promptUuid: {}, message: {}", promptUuid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Inference Prompt Export 조회 실패 (예상치 못한 오류) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt Export 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Inference Prompt Import (JSON)
     *
     * <p>JSON 문자열을 받아서 Inference Prompt를 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     *
     * @param promptUuid Prompt UUID
     * @param json       JSON 문자열
     * @return 생성된 Prompt 정보
     */
    public PromptCreateResponse importPrompt(String promptUuid, String json) {
        try {
            log.info("Inference Prompt Import 요청 - promptUuid: {}, jsonLength: {}", promptUuid, json != null ? json.length() : 0);

            // promptUuid null 체크
            if (promptUuid == null || promptUuid.trim().isEmpty()) {
                throw new IllegalArgumentException("promptUuid는 필수입니다.");
            }

            // JSON 문자열을 Map<String, Object>로 변환 (parsedJson 형태로 변환)
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> jsonData = objectMapper.readValue(json, java.util.Map.class);

            // release 필드 확인
            Object releaseValue = jsonData.get("release");
            log.info("Inference Prompt Import 호출 - promptUuid: {}, jsonData type: {}, release: {}",
                    promptUuid, jsonData != null ? jsonData.getClass().getSimpleName() : "null", releaseValue);
            log.debug("Inference Prompt Import JSON 데이터: {}", json);

            PromptCreateResponse response = sktaiAgentInferencePromptsClient.importPrompt(promptUuid, jsonData);
            log.info("Inference Prompt Import 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Inference Prompt Import 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Inference Prompt Import 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Inference Prompt Import에 실패했습니다: " + e.getMessage());
        }
    }
}
