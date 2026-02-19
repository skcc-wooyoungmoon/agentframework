package com.skax.aiplatform.controller.admin;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.ProjectSearchReq;
import com.skax.aiplatform.dto.admin.request.UserAssignableRoleSearchReq;
import com.skax.aiplatform.dto.admin.request.UserRoleUpdateReq;
import com.skax.aiplatform.dto.admin.request.UserSearchReq;
import com.skax.aiplatform.dto.admin.response.RoleRes;
import com.skax.aiplatform.dto.admin.response.UserProjectRes;
import com.skax.aiplatform.dto.admin.response.UserProjectRoleRes;
import com.skax.aiplatform.dto.admin.response.UserRes;
import com.skax.aiplatform.service.admin.UserMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO 포탈 관리자만 접근 가능

/**
 * 사용자 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리 API")
public class UserMgmtController {

    private final UserMgmtService userMgmtService;

    /**
     * 전체 사용자 조회 (페이징)
     *
     * @param searchReq 검색 조건
     * @return 사용자 목록 (페이징)
     */
    @GetMapping
    @Operation(
            summary = "전체 사용자 조회",
            description = "전체 사용자를 페이징 및 검색 조건으로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 유형 (jkwNm, deptNm, jkgpNm)"
                            , example = "jkwNm"),
                    @Parameter(name = "keyword", description = "검색어", example = "홍길동"),
                    @Parameter(name = "retrJkwYn", description = "인사 상태 (1:재직, 0:퇴직)", example = "1"),
                    @Parameter(name = "dmcStatus", description = "계정 상태 (ACTIVE: 활성, DORMANT: 휴면)", example = "ACTIVE")
            }
    )
    @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    public AxResponseEntity<PageResponse<UserRes>> getUsers(UserSearchReq searchReq) {
        log.info(">> 사용자 관리 컨트롤러 - 전체 사용자 조회 요청 << 요청 정보 {}", searchReq);

        Page<UserRes> users = userMgmtService.getUsers(searchReq);

        return AxResponseEntity.okPage(users, "전체 사용자 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 사용자 상세 조회
     *
     * @param memberId 사용자 아이디
     * @return 사용자 정보
     */
    @GetMapping("/{memberId}")
    @Operation(
            summary = "사용자 상세 조회",
            description = "사용자 ID를 통해 사용자 정보를 상세 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "사용자 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public AxResponseEntity<UserRes> getUserById(
            @PathVariable @Parameter(description = "사용자 ID", example = "MEMBER ID") String memberId) {
        log.info("ID로 사용자 상세 조회: {}", memberId);

        UserRes user = userMgmtService.getUserById(memberId);

        return AxResponseEntity.ok(user, "사용자 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 사용자가 참여한 프로젝트 목록 조회 (페이징)
     *
     * @param memberId  사용자 아이디
     * @param searchReq 검색 조건
     * @return 사용자가 참여한 프로젝트 목록 (페이징)
     */
    @GetMapping("/{memberId}/projects")
    @Operation(
            summary = "사용자가 참여한 프로젝트 조회",
            description = "사용자가 참여한 프로젝트를 페이징 및 검색 조건으로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 유형 (프로젝트 이름 : prjNm, 프로젝트 설명 : dtlCtnt)",
                            example = "name"),
                    @Parameter(name = "keyword", description = "검색어", example = "대출 상품 추천"),
                    @Parameter(name = "statusNm", description = "프로젝트 상태 (ONGOING, COMPLETED)", example = "ONGOING")
            }
    )
    @ApiResponse(responseCode = "200", description = "사용자가 참여한 프로젝트 조회 성공")
    public AxResponseEntity<PageResponse<UserProjectRes>> getUserProjects(
            @PathVariable @Parameter(description = "사용자 ID", example = "MEMBER ID") String memberId,
            ProjectSearchReq searchReq) {
        log.info(">> 사용자 관리 컨트롤러 - 사용자가 참여한 프로젝트 조회 요청 << 요청 정보: {}", searchReq);

        Page<UserProjectRes> userProjects = userMgmtService.getUserProjects(memberId, searchReq);

        return AxResponseEntity.okPage(userProjects, "사용자가 참여한 프로젝트 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 사용자가 참여한 프로젝트의 상세 정보 및 할당된 역할 상세 조회
     *
     * @param userId    사용자 아이디 (memberId)
     * @param projectId 프로젝트 아이디 (uuid)
     * @return 사용자가 해당 프로젝트에서 가진 상세 정보 및 역할
     */
    @GetMapping("/{userId}/projects/{projectId}")
    @Operation(
            summary = "사용자가 참여한 프로젝트의 상세 정보 및 역할 조회",
            description = "사용자가 참여한 특정 프로젝트의 상세 정보와 해당 사용자의 역할을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "사용자가 참여한 특정 프로젝트내 역할 조회 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "사용자 또는 프로젝트를 찾을 수 없음")
    public AxResponseEntity<UserProjectRoleRes> getUserProjectDetail(
            @PathVariable @Parameter(description = "사용자 ID", example = "user_000001") String userId,
            @PathVariable @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId) {
        log.info("사용자가 참여한 프로젝트의 상세 정보 및 역할 조회. UserId: {}, ProjectId: {}", userId, projectId);

        UserProjectRoleRes userProjectDetail = userMgmtService.getUserProjectDetail(userId, projectId);

        return AxResponseEntity.ok(userProjectDetail, "사용자가 참여한 프로젝트의 상세 정보 및 역할 조회를 성공적으로 조회했습니다.");
    }

    /**
     * 사용자가 참여한 프로젝트의 할당 가능한 역할 목록 조회
     *
     * @param projectId 프로젝트 아이디 (uuid)
     * @param searchReq 검색 조건
     * @return 사용자가 참여한 프로젝트의 할당 가능한 역할 목록 (포털 단위 역할 제외)
     */
    @GetMapping("/projects/{projectId}/role")
    @Operation(
            summary = "사용자가 참여한 프로젝트의 할당 가능한 역할 목록 조회",
            description = "사용자가 참여한 특정 프로젝트에서 할당 가능한 역할 목록을 조회합니다. 포털 단위 역할은 제외됩니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 유형 (name, description)", example = "name"),
                    @Parameter(name = "keyword", description = "검색어", example = "프로젝트 관리자"),
                    @Parameter(name = "statusNm", description = "역할 상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
            }
    )
    @ApiResponse(responseCode = "200", description = "할당 가능한 역할 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    public AxResponseEntity<PageResponse<RoleRes>> getAssignableProjectRoles(
            @PathVariable @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,
            UserAssignableRoleSearchReq searchReq) {
        log.info(" >> 컨트롤러 << 사용자가 참여한 프로젝트의 할당 가능한 역할 목록 조회 시작!" +
                "프로젝트 아이디={} 요청 정보={}", projectId, searchReq);

        Page<RoleRes> assignableRoles = userMgmtService.getAssignableProjectRoles(projectId, searchReq);

        return AxResponseEntity.okPage(assignableRoles, "사용자가 참여한 프로젝트의 할당 가능한 역할을 성공적으로 조회했습니다.");
    }

    /**
     * 사용자가 참여한 프로젝트내 역할 수정
     *
     * @param userId    사용자 아이디 (memberId)
     * @param projectId 프로젝트 아이디 (uuid)
     * @param updateReq 역할 수정 정보
     */
    @PutMapping("/{userId}/projects/{projectId}/role")
    @Operation(
            summary = "사용자 프로젝트 내 역할 수정",
            description = "특정 사용자의 프로젝트 내 역할을 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "사용자 역할 수정 성공")
    @ApiResponse(responseCode = "404", description = "사용자 또는 프로젝트를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public AxResponseEntity<Void> updateUserRole(
            @PathVariable @Parameter(description = "사용자 ID", example = "user_000001") String userId,
            @PathVariable @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,
            @Valid @RequestBody UserRoleUpdateReq updateReq) {
        log.info("사용자 역할 수정 요청. UserId: {}, ProjectId: {}, NewRoleId: {}",
                userId, projectId, updateReq.getUuid());

        userMgmtService.updateUserRole(userId, projectId, updateReq.getUuid());

        return AxResponseEntity.success("사용자의 프로젝트 내 역할이 성공적으로 수정되었습니다.");
    }

    /**
     * 사용자 계정 상태 활성화 (활성화를 위함)
     *
     * @param userId 사용자 아이디 (memberId)
     */
    @PutMapping("/{userId}/active")
    @Operation(
            summary = "사용자 계정 활성화",
            description = "지정된 사용자의 계정 상태를 활성(ACTIVE)으로 변경합니다."
    )
    @ApiResponse(responseCode = "200", description = "사용자 계정 활성화 성공")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public AxResponseEntity<Void> activateUserStatus(
            @PathVariable @Parameter(description = "사용자 ID", example = "user_000001") String userId) {

        log.info("사용자 계정 활성화 요청. MemberId: {}", userId);

        userMgmtService.activateUserStatus(userId);

        return AxResponseEntity.success("사용자 계정이 성공적으로 활성화되었습니다.");
    }


}

