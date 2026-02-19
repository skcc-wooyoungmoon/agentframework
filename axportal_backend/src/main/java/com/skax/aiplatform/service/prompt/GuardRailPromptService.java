package com.skax.aiplatform.service.prompt;

import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionResponse;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptMsgsByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptTagsListByIdRes;

/**
 * 가드레일 프롬프트 서비스 인터페이스
 *
 * <p>
 * 가드레일 프롬프트 관리를 위한 비즈니스 로직을 정의합니다.
 * 추론 프롬프트와 완전히 분리되어 독립적으로 동작합니다.
 * </p>
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-11-02
 */
public interface GuardRailPromptService {

    /**
     * 가드레일 프롬프트 목록 조회 (페이징)
     *
     * @param projectId 프로젝트 ID
     * @param page      페이지 번호 (0부터 시작)
     * @param size      페이지 크기
     * @param tag       태그
     * @param search    검색어
     * @param sort      정렬 기준
     * @param filter    필터
     * @return 가드레일 프롬프트 목록 (페이징)
     */
    PageResponse<GuardRailPromptRes> getGuardRailPromptList(String projectId, int page, int size, String tag,
            String search, String sort, String filter);

    /**
     * 가드레일 프롬프트 상세 조회
     *
     * @param promptUuid 가드레일 프롬프트 ID(UUID)
     * @return 가드레일 프롬프트 상세 정보
     */
    GuardRailPromptByIdRes getGuardRailPromptById(String promptUuid);

    /**
     * 가드레일 프롬프트 특정 버전 메시지 조회
     *
     * @param versionUuid 가드레일 프롬프트 버전 ID(UUID)
     * @return 가드레일 프롬프트 특정 버전 메시지 정보
     */
    GuardRailPromptMsgsByIdRes getGuardRailPromptMsgsById(String versionUuid);

    /**
     * 가드레일 프롬프트 특정 버전 태그 조회
     *
     * @param versionUuid 가드레일 프롬프트 버전 ID(UUID)
     * @return 가드레일 프롬프트 특정 버전 태그 정보
     */
    GuardRailPromptTagsListByIdRes getGuardRailPromptTagById(String versionUuid);

    /**
     * 새로운 가드레일 프롬프트 생성
     *
     * @param request 가드레일 프롬프트 생성 요청
     * @return 생성된 가드레일 프롬프트 정보
     */
    GuardRailPromptCreateRes createGuardRailPrompt(GuardRailPromptCreateReq request);

    /**
     * 가드레일 프롬프트 정보 수정
     *
     * @param promptUuid 가드레일 프롬프트 UUID
     * @param request    가드레일 프롬프트 수정 요청
     */
    void updateGuardRailPromptById(String promptUuid, GuardRailPromptUpdateReq request);

    /**
     * 가드레일 프롬프트 삭제
     *
     * @param promptUuid 가드레일 프롬프트 UUID
     */
    void deleteGuardRailPromptById(String promptUuid);

    /**
     * 가드레일 프롬프트 버전 정보 조회
     *
     * @param promptUuid 가드레일 프롬프트 UUID
     * @return 버전 정보
     */
    PromptVersionResponse getVersion(String promptUuid);
}
