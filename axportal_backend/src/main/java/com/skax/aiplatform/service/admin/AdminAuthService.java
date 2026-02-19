package com.skax.aiplatform.service.admin;

import java.util.List;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.MeResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.RoleAvailablePageResponseDto;
import com.skax.aiplatform.common.exception.BusinessException;

/**
 * 관리자 권한 관리 서비스 인터페이스
 *
 * <p>Admin 계정의 하드코딩된 인증 정보를 사용하여 사용자 권한을 관리합니다.
 * 기존 AuthService, SktaiAuthService를 활용하되 AdminContext를 통해
 * Admin 모드로 동작합니다.</p>
 *
 * @author Jongtae Park
 * @version 1.0.0
 * @since 2025-10-08
 */
public interface AdminAuthService {

    /**
     * Admin 토큰 확보 및 갱신
     *
     * @return Admin 계정의 유효한 토큰 존재 여부
     */
    boolean ensureAdminToken();

    /**
     * 현재(Admin 컨텍스트) 사용자 정보 조회
     *
     * <p>AdminContext와 Admin 토큰을 사용하여 SKTAI의 /users/me를 호출합니다.</p>
     *
     * @return 현재 사용자 정보(MeResponse)
     */
    MeResponse getCurrentUser();

    /**
     * 사용자 삭제 (Admin 권한 필요)
     *
     * @param userId 사용자 ID
     */
    void deleteUser(String userId);

    /**
     * 사용자 초기 권한 지정 (Admin 권한 필요)
     *
     * @param userId  사용자 ID
     * @param groupNm 그룹 이름
     */
    void assignUserToGroup(String userId, String groupNm);


    /**
     * ADXP ID를 사용하여 사용자를 그룹에 할당 (Admin 권한 필요)
     *
     * @param adxpUserId  ADXP 사용자 ID
     * @param adxpGroupId ADXP 그룹 ID
     */
    void assignUserToGroupWithAdxpId(String adxpUserId, String adxpGroupId);

    /**
     * 사용자 권한 해제 (Admin 권한 필요)
     *
     * @param userId  사용자 ID
     * @param groupNm 그룹 이름
     */
    void unassignUserFromGroup(String userId, String groupNm);

    /**
     * 사용자에게 할당 가능한 역할 목록 조회 (Admin 권한 필요)
     *
     * @param userId 사용자 ID
     * @return 할당 가능한 역할 목록 (페이징)
     */
    RoleAvailablePageResponseDto getUserRoleAvailable(String userId);

    /**
     * 사용자의 역할 매핑 업데이트 (Admin 권한 필요)
     *
     * @param userId         사용자 ID
     * @param availableItems 할당 가능한 역할 목록
     */
    void updateUserRoleMappingsFromAvailable(String userId, List<RoleAvailablePageResponseDto.Item> availableItems);

    /**
     * 그룹 생성 (Admin 권한 필요)
     *
     * @param groupName 그룹 이름
     * @return 생성된 그룹 ID
     */
    String createGroup(String groupName);

    /**
     * 그룹 삭제 (Admin 권한 필요)
     *
     * @param groupName 그룹 이름
     */
    void deleteGroup(String groupName);

    /**
     * 그룹명 키워드로 그룹 이름 목록 조회 (Admin 권한 필요)
     *
     * @param keyword 검색 키워드 (예: "P123_")
     * @return 키워드가 포함된 그룹 이름 목록
     */
    List<String> findGroupNamesByKeyword(String keyword);

    /**
     * 그룹명 키워드로 조회되는 모든 그룹 삭제 (Admin 권한 필요)
     *
     * @param keyword 검색 키워드 (예: "P123_")
     */
    void deleteGroupsByKeyword(String keyword);

    /**
     * 리소스에 대한 권한 정책 업데이트 (Admin 권한 필요)
     *
     * @param resourceUrl    리소스 URL
     * @param policyRequests 정책 요청 목록
     * @return 업데이트된 정책 목록
     */
    List<PolicyRequest> updateResourcePolicy(String resourceUrl, List<PolicyRequest> policyRequests);

    /**
     * 현재 활성 프로젝트 그룹 기준으로 리소스 정책을 설정 (Admin 권한 필요)
     *
     * @param resourceUrl 리소스 URL
     */
    void setResourcePolicyByCurrentGroup(String resourceUrl);

    /**
     * 현재 활성 프로젝트 그룹 기준으로 리소스 정책을 설정 (Admin 권한 필요)
     *
     * @param resourceUrl 리소스 URL
     */
    void setResourcePolicyByCurrentGroupWithPut(String resourceUrl);

    /**
     * 특정 프로젝트 시퀀스로 리소스 정책을 설정 (Admin 권한 필요)
     *
     * @param resourceUrl 리소스 URL
     * @param projectSeq  프로젝트 시퀀스
     */
    void setResourcePolicyByProjectSequence(String resourceUrl, long projectSeq);

    /**
     * 프라이빗 정책을 공개 정책으로 변환하여 리소스에 설정 (Admin 권한 필요)
     *
     * @param resourceUrl 리소스 URL
     */
    void setResourcePublicPolicyFromPrivate(String resourceUrl);

    /**
     * 현재 활성 프로젝트 그룹 기준으로 정책 요청 목록 조회
     *
     * @return 정책 요청 목록
     */
    List<PolicyRequest> getPolicyRequestsByCurrentGroup();

    /**
     * 특정 프로젝트 시퀀스 기준으로 정책 요청 목록 조회
     *
     * @param projectSeq 프로젝트 시퀀스
     * @return 정책 요청 목록
     */
    List<PolicyRequest> getPolicyRequestsByCurrentProjectSequence(long projectSeq);

    /**
     * 특정 사용자 ID 기준으로 정책 요청 목록 조회
     *
     * <p>사용자 ID를 받아 해당 사용자의 활성 프로젝트를 조회한 후, 
     * 해당 프로젝트 시퀀스에 맞는 정책 요청 목록을 반환합니다.</p>
     *
     * @param userId 사용자 ID
     * @return 정책 요청 목록
     * @throws BusinessException 사용자 정보 또는 활성 프로젝트 조회 실패 시
     */
    List<PolicyRequest> getPolicyRequestsByUserId(String userId);

    /**
     * 사용자 ID와 프로젝트명 기준으로 정책 요청 목록 조회
     *
     * <p>프로젝트명(GPO_PRJ_NM)으로 프로젝트를 조회한 후, 
     * 해당 프로젝트 시퀀스에 맞는 정책 요청 목록을 반환합니다.</p>
     *
     * @param memberId 사용자 ID (검증용, 선택적)
     * @param projectName 프로젝트명 (GPO_PRJ_NM)
     * @return 정책 요청 목록
     * @throws BusinessException 프로젝트 조회 실패 시
     */
    List<PolicyRequest> getPolicyRequestsByMemberIdAndProjectName(String memberId, String projectName);

    /**
     * 사용자 ID와 프로젝트명으로 리소스 정책 설정 (Admin 권한 필요)
     *
     * @param resourceUrl 리소스 URL
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @throws BusinessException 프로젝트 조회 실패 시
     */
    void setResourcePolicyByMemberIdAndProjectName(String resourceUrl, String memberId, String projectName);

    /**
     * 프로젝트명을 기준으로 프로젝트 정보를 조회한다.
     * SKTAI의 프로젝트 목록 조회 API를 사용해 검색 후, 이름이 파라미터와 일치하는 항목을 반환한다.
     *
     * @param projectName 프로젝트명
     * @return 일치하는 프로젝트 정보(ClientRead), 없으면 null
     */
    ClientRead getProjectByName(String projectName);
}
