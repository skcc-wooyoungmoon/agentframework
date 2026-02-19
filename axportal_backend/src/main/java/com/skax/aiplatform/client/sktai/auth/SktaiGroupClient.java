package com.skax.aiplatform.client.sktai.auth;

import com.skax.aiplatform.client.sktai.auth.dto.request.GroupMemberAddRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.GroupPermissionRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.GroupUpdateRequest;
import com.skax.aiplatform.client.sktai.auth.dto.response.GroupMembersResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.GroupPermissionsResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.GroupResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.GroupsResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI 그룹 관리 Feign Client
 * 
 * <p>
 * SKTAI Auth API의 그룹 관리 엔드포인트를 호출하는 Feign Client입니다.
 * 그룹 CRUD, 멤버 관리, 권한 관리 등의 완전한 그룹 관리 기능을 제공합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>그룹 CRUD</strong>: 그룹 생성, 조회, 수정, 삭제</li>
 * <li><strong>그룹 목록</strong>: 페이징된 그룹 목록 조회</li>
 * <li><strong>멤버 관리</strong>: 그룹 멤버 추가, 조회, 제거</li>
 * <li><strong>권한 관리</strong>: 그룹 권한 조회, 설정</li>
 * <li><strong>검색 및 필터링</strong>: 그룹명, 설명 기반 검색</li>
 * </ul>
 * 
 * <h3>그룹 유형:</h3>
 * <ul>
 * <li><strong>Department</strong>: 부서/조직 그룹</li>
 * <li><strong>Project</strong>: 프로젝트 기반 그룹</li>
 * <li><strong>Role</strong>: 역할 기반 그룹</li>
 * <li><strong>Custom</strong>: 사용자 정의 그룹</li>
 * </ul>
 * 
 * <h3>인증 방식:</h3>
 * <ul>
 * <li><strong>OAuth2 Bearer Token</strong>: Authorization 헤더에 Bearer 토큰 포함</li>
 * <li><strong>자동 인터셉터</strong>: SktaiClientConfig를 통한 토큰 자동 추가</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 2.0
 * @see SktaiClientConfig Feign Client 설정
 */
@FeignClient(name = "sktai-group-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
@Tag(name = "SKTAI 그룹 관리", description = "SKTAI Auth API 그룹 관리 엔드포인트")
public interface SktaiGroupClient {

    /**
     * 그룹 목록 조회
     * 
     * <p>
     * 시스템의 그룹 목록을 페이징하여 조회합니다.
     * 검색어, 필터, 정렬 옵션을 통해 원하는 그룹을 찾을 수 있습니다.
     * </p>
     * 
     * @param page   페이지 번호 (1부터 시작, 기본값: 1)
     * @param size   페이지당 항목 수 (기본값: 10, 최대값: 100)
     * @param sort   정렬 조건 (예: "name", "created_at desc")
     * @param filter 필터 조건 (department, project, role, custom, active, inactive 등)
     * @param search 검색어 (그룹명, 설명, 태그에서 검색)
     * @return 페이징된 그룹 목록과 메타데이터
     */
    @GetMapping("/api/v1/groups")
    @Operation(summary = "그룹 목록 조회", description = "시스템의 그룹 목록을 페이징하여 조회합니다.", tags = { "그룹 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공", content = @Content(schema = @Schema(implementation = GroupsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "422", description = "요청 파라미터 오류")
    })
    GroupsResponse getGroups(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지당 항목 수", example = "10") Integer size,
            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건", example = "name") String sort,
            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건", example = "department") String filter,
            @RequestParam(value = "search", required = false) @Parameter(description = "검색어", example = "development") String search);

    /**
     * 그룹 생성
     * 
     * <p>새로운 그룹을 생성합니다.</p>
     * 
     * @param request 그룹 생성 요청 데이터
     * @return 생성된 그룹 정보
     */
    @PostMapping("/api/v1/groups?group_name={group_name}")
    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다.", tags = { "그룹 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "그룹 생성 성공", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "409", description = "그룹명 중복")
    })
    GroupResponse createGroup(
            @PathVariable("group_name") String groupName);

    /**
     * 그룹 상세 조회
     * 
     * <p>특정 그룹의 상세 정보를 조회합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @return 그룹 상세 정보
     */
    @GetMapping("/api/v1/groups/{groupId}")
    @Operation(summary = "그룹 상세 조회", description = "특정 그룹의 상세 정보를 조회합니다.", tags = { "그룹 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 조회 성공", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    GroupResponse getGroup(@PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId);

    /**
     * 그룹 수정
     * 
     * <p>기존 그룹의 정보를 수정합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @param request 그룹 수정 요청 데이터
     * @return 수정된 그룹 정보
     */
    @PutMapping("/api/v1/groups/{groupId}")
    @Operation(summary = "그룹 수정", description = "기존 그룹의 정보를 수정합니다.", tags = { "그룹 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 수정 성공", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "그룹명 중복")
    })
    GroupResponse updateGroup(
            @PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId,
            @RequestBody GroupUpdateRequest request);

    /**
     * 그룹 삭제
     * 
     * <p>기존 그룹을 삭제합니다.</p>
     * 
     * @param groupId 그룹 ID
     */
    @DeleteMapping("/api/v1/groups/{groupId}")
    @Operation(summary = "그룹 삭제", description = "기존 그룹을 삭제합니다.", tags = { "그룹 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "그룹 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "그룹에 멤버가 있어 삭제할 수 없음")
    })
    void deleteGroup(@PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId);

    /**
     * 그룹 멤버 목록 조회
     * 
     * <p>특정 그룹의 멤버 목록을 페이징하여 조회합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @param page    페이지 번호 (1부터 시작, 기본값: 1)
     * @param size    페이지당 항목 수 (기본값: 10, 최대값: 100)
     * @param role    역할 필터 (member, admin, moderator)
     * @return 페이징된 그룹 멤버 목록
     */
    @GetMapping("/api/v1/groups/{groupId}/members")
    @Operation(summary = "그룹 멤버 목록 조회", description = "특정 그룹의 멤버 목록을 페이징하여 조회합니다.", tags = { "그룹 멤버 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 멤버 목록 조회 성공", content = @Content(schema = @Schema(implementation = GroupMembersResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    GroupMembersResponse getGroupMembers(
            @PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId,
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지당 항목 수", example = "10") Integer size,
            @RequestParam(value = "role", required = false) @Parameter(description = "역할 필터", example = "member") String role);

    /**
     * 그룹 멤버 추가
     * 
     * <p>그룹에 새로운 멤버를 추가합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @param request 멤버 추가 요청 데이터
     * @return 추가된 멤버 정보
     */
    @PostMapping("/api/v1/groups/{groupId}/members")
    @Operation(summary = "그룹 멤버 추가", description = "그룹에 새로운 멤버를 추가합니다.", tags = { "그룹 멤버 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "멤버 추가 성공", content = @Content(schema = @Schema(implementation = GroupMembersResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 그룹 멤버임")
    })
    GroupMembersResponse addGroupMembers(
            @PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId,
            @RequestBody GroupMemberAddRequest request);

    /**
     * 그룹 멤버 제거
     * 
     * <p>그룹에서 멤버를 제거합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @param userId  사용자 ID
     */
    @DeleteMapping("/api/v1/groups/{groupId}/members/{userId}")
    @Operation(summary = "그룹 멤버 제거", description = "그룹에서 멤버를 제거합니다.", tags = { "그룹 멤버 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "멤버 제거 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹 또는 멤버를 찾을 수 없음")
    })
    void removeGroupMember(
            @PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId,
            @PathVariable("userId") @Parameter(description = "사용자 ID", example = "user-456") String userId);

    /**
     * 그룹 권한 조회
     * 
     * <p>특정 그룹의 권한 정보를 조회합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @return 그룹 권한 정보
     */
    @GetMapping("/api/v1/groups/{groupId}/permissions")
    @Operation(summary = "그룹 권한 조회", description = "특정 그룹의 권한 정보를 조회합니다.", tags = { "그룹 권한 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 권한 조회 성공", content = @Content(schema = @Schema(implementation = GroupPermissionsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    GroupPermissionsResponse getGroupPermissions(
            @PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId);

    /**
     * 그룹 권한 설정
     * 
     * <p>그룹의 권한을 설정합니다.</p>
     * 
     * @param groupId 그룹 ID
     * @param request 권한 설정 요청 데이터
     * @return 설정된 권한 정보
     */
    @PutMapping("/api/v1/groups/{groupId}/permissions")
    @Operation(summary = "그룹 권한 설정", description = "그룹의 권한을 설정합니다.", tags = { "그룹 권한 관리" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 권한 설정 성공", content = @Content(schema = @Schema(implementation = GroupPermissionsResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    GroupPermissionsResponse setGroupPermissions(
            @PathVariable("groupId") @Parameter(description = "그룹 ID", example = "group-123") String groupId,
            @RequestBody GroupPermissionRequest request);
}
