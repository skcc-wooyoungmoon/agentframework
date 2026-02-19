package com.skax.aiplatform.service.prompt;

import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionResponse;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.InfPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.InfPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.response.*;

import java.util.List;

/**
 * 추론프롬프트 서비스 인터페이스
 */
public interface InferPromptService {

    /**
     * 추론프롬프트 목록 조회
     */
    PageResponse<InfPromptRes> getInfPromptList(String projectId, int page, int size, String tag, String search,
                                                String sort, String filter, Boolean release_only);

    /**
     * 추론프롬프트 태그 목록 조회
     */
    InfPromptTagsList getInfPromptTagList(String projectId, String filter);

    /**
     * 추론 프롬프트 상세 조회
     */
    InfPromptByIdRes getInfPromptById(String promptUuid);

    /**
     * 추론 프롬프트 버전 목록 조회
     */
    InfPromptVerListByIdRes getInfPromptVerListById(String promptUuid);

    /**
     * 추론 프롬프트 최신 버전 조회
     */
    InfPromptLatestByIdRes getInfPromptLatestVerById(String promptUuid);

    /**
     * 추론 프롬프트 특정 버전 메시지 조회
     */
    InfPromptMsgsByIdRes getInfPromptMsgsById(String versionUuid);

    /**
     * 추론 프롬프트 특정 버전 변수 조회
     */
    InfPromptVarsByIdRes getInfPromptVarsById(String versionUuid);

    /**
     * 추론 프롬프트 특정 버전 태그 조회
     */
    InfPromptTagsListByIdRes getInfPromptTagById(String versionUuid);

    /**
     * 추론 프롬프트 삭제
     */
    void deleteInfPromptById(String promptUuid);

    /**
     * 추론 프롬프트 생성
     */
    InfPromptCreateRes createInfPrompt(InfPromptCreateReq request);

    /**
     * 추론 프롬프트 수정
     */
    void updateInfPromptById(String promptUuid, InfPromptUpdateReq request);

    /**
     * 추론 프롬프트 내장 템플릿 목록 조회
     */
    InfPromptBuiltinRes getInfPromptBuiltin();

    /**
     * 프롬프트 버전 정보 조회 (담당자 정보 포함)
     */
    PromptVersionResponse getVersion(String promptUuid);

    /**
     * 추론 프롬프트와 연결된 프롬프트 목록 조회
     */
    PageResponse<InfPromptLineageRes> getInfPromptLineageRelations(String promptUuid, Integer page, Integer size);

    /**
     * 추론 프롬프트 Policy 설정
     *
     * @param promptUuid  추론 프롬프트 ID
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @return 설정된 Policy 목록
     */
    List<PolicyRequest> setInferPromptPolicy(String promptUuid, String memberId, String projectName);

}
