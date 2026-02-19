package com.skax.aiplatform.service.prompt;

import java.util.List;

import com.skax.aiplatform.client.sktai.agent.dto.response.GuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.GuardRailCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailDeleteReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailUpdateReq;
import com.skax.aiplatform.dto.prompt.response.GuardRailCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailDeleteRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailDetailRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailRes;

/**
 * 가드레일 관리 서비스 인터페이스
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-10-16
 */
public interface GuardRailService {

    /**
     * 가드레일 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @param filter    검색 조건
     * @param search    검색어
     * @param sort      정렬 조건
     * @return 가드레일 목록 (페이징)
     */
    PageResponse<GuardRailRes> getGuardRailList(
            String projectId,
            Integer page,
            Integer size,
            String filter,
            String search,
            String sort
    );

    /**
     * 가드레일 상세 조회
     *
     * @param id 가드레일 ID
     * @return 가드레일 상세 정보
     */
    GuardRailDetailRes getGuardRailById(String id);

    /**
     * 가드레일 생성
     *
     * @param request 가드레일 생성 요청
     * @return 생성된 가드레일 ID
     */
    GuardRailCreateRes createGuardRail(GuardRailCreateReq request);

    /**
     * 가드레일 수정
     *
     * @param id      가드레일 ID
     * @param request 가드레일 수정 요청
     * @return 수정된 가드레일 정보
     */
    GuardRailUpdateRes updateGuardRail(String id, GuardRailUpdateReq request);

    /**
     * 가드레일 복수 삭제
     *
     * @param request 가드레일 삭제 요청
     * @return 삭제 결과 (전체 건수, 성공 건수)
     */
    GuardRailDeleteRes deleteGuardRailBulk(GuardRailDeleteReq request);

    /**
     * 가드레일 Policy 설정
     *
     * @param guardrailId 가드레일 ID
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    List<PolicyRequest> setGuardRailPolicy(String guardrailId, String memberId, String projectName);

}

