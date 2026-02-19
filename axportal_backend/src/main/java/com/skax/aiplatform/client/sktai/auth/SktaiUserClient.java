package com.skax.aiplatform.client.sktai.auth;

import com.skax.aiplatform.client.sktai.auth.dto.request.*;
import com.skax.aiplatform.client.sktai.auth.dto.response.AvailableGroupsResponseDto;
import com.skax.aiplatform.client.sktai.auth.dto.response.AvailableRolesResponseDto;
import com.skax.aiplatform.client.sktai.auth.dto.response.RoleAvailablePageResponseDto;
import com.skax.aiplatform.client.sktai.auth.dto.response.MeResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.UserBase;
import com.skax.aiplatform.client.sktai.auth.dto.response.UserRepresentation;
import com.skax.aiplatform.client.sktai.auth.dto.response.UsersRead;
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

import java.util.List;

/**
 * SKTAI 사용자 관리 Feign Client
 * 
 * <p>SKTAI Auth API의 사용자 관리 엔드포인트를 호출하는 Feign Client입니다.
 * 사용자 CRUD, 검색, 필터링, 프로필 관리 등의 완전한 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>사용자 CRUD</strong>: 생성, 조회, 수정, 삭제</li>
 *   <li><strong>현재 사용자 정보</strong>: /users/me 엔드포인트</li>
 *   <li><strong>사용자 검색</strong>: 이름, 이메일 기반 검색</li>
 *   <li><strong>권한 관리</strong>: 역할, 그룹 할당</li>
 *   <li><strong>계정 관리</strong>: 활성화/비활성화</li>
 * </ul>
 * 
 * <h3>인증 방식:</h3>
 * <ul>
 *   <li><strong>OAuth2 Bearer Token</strong>: Authorization 헤더에 Bearer 토큰 포함</li>
 *   <li><strong>자동 인터셉터</strong>: SktaiClientConfig를 통한 토큰 자동 추가</li>
 * </ul>
 * 
 * <h3>권한 체계:</h3>
 * <ul>
 *   <li><strong>일반 사용자</strong>: 자신의 프로필 조회/수정, 타사용자 목록 조회</li>
 *   <li><strong>관리자</strong>: 모든 사용자 관리, 권한 할당</li>
 *   <li><strong>슈퍼유저</strong>: 시스템 전체 관리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 1.0
 * @see MeResponse 현재 사용자 정보 DTO
 * @see UserBase 사용자 정보 DTO
 * @see UsersRead 사용자 목록 DTO
 * @see RegisterUserPayload 사용자 등록 요청 DTO
 * @see UpdateUserPayload 사용자 수정 요청 DTO
 * @see UserRepresentation 사용자 등록 응답 DTO
 */
@FeignClient(
    name = "sktai-user-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI 사용자 관리", description = "SKTAI Auth API 사용자 관리 엔드포인트")
public interface SktaiUserClient {
    
    // ==================== 현재 사용자 관리 ====================
    
    /**
     * 현재 로그인한 사용자 정보 조회
     * 
     * <p>JWT 토큰을 기반으로 현재 로그인한 사용자의 상세 정보를 조회합니다.
     * 사용자 프로필 화면, 권한 확인, 개인화 설정 등에서 활용됩니다.</p>
     * 
     * <h3>반환 정보:</h3>
     * <ul>
     *   <li><strong>기본 정보</strong>: ID, 사용자명, 이메일, 전체명</li>
     *   <li><strong>상태 정보</strong>: 활성화 여부, 슈퍼유저 권한</li>
     *   <li><strong>활동 정보</strong>: 마지막 로그인, 계정 생성일</li>
     *   <li><strong>권한 정보</strong>: 할당된 역할, 소속 그룹</li>
     * </ul>
     * 
     * <h3>보안 특징:</h3>
     * <ul>
     *   <li>JWT 토큰 기반 인증</li>
     *   <li>본인 정보만 조회 가능</li>
     *   <li>민감 정보 마스킹 처리</li>
     * </ul>
     * 
     * @return 현재 로그인한 사용자의 상세 정보
     * 
     * @throws FeignException.Unauthorized JWT 토큰이 유효하지 않거나 만료된 경우
     * @throws FeignException.Forbidden 토큰은 유효하지만 사용자 계정이 비활성화된 경우
     * 
     * @implNote 토큰에서 추출한 사용자 ID로 조회하므로 별도 파라미터가 필요하지 않습니다.
     * @apiNote 프론트엔드에서 사용자 인증 상태 확인 시 주로 사용됩니다.
     */
    @GetMapping("/api/v1/users/me")
    @Operation(
        summary = "현재 사용자 정보 조회",
        description = """
            JWT 토큰을 기반으로 현재 로그인한 사용자의 상세 정보를 조회합니다.
            
            **특징:**
            - 토큰 기반 자동 사용자 식별
            - 본인 정보만 조회 가능
            - 실시간 권한 정보 포함
            
            **활용 사례:**
            - 사용자 프로필 화면 데이터
            - 권한 기반 UI 제어
            - 개인화 설정 적용
            """,
        tags = {"현재 사용자"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "현재 사용자 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = MeResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - JWT 토큰이 유효하지 않거나 만료됨"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "계정 비활성화 - 사용자 계정이 비활성화된 상태"
        )
    })
    MeResponse getCurrentUser();
    
    // ==================== 사용자 목록 및 검색 ====================
    
    /**
     * 사용자 목록 조회
     * 
     * <p>시스템의 사용자 목록을 페이징하여 조회합니다.
     * 검색어, 필터, 정렬 옵션을 통해 원하는 사용자를 찾을 수 있습니다.</p>
     * 
     * <h3>검색 기능:</h3>
     * <ul>
     *   <li><strong>사용자명 검색</strong>: username 필드에서 부분 일치 검색</li>
     *   <li><strong>이메일 검색</strong>: email 필드에서 부분 일치 검색</li>
     *   <li><strong>전체명 검색</strong>: full_name 필드에서 부분 일치 검색</li>
     * </ul>
     * 
     * <h3>필터링 옵션:</h3>
     * <ul>
     *   <li><strong>active</strong>: 활성 사용자만 조회</li>
     *   <li><strong>inactive</strong>: 비활성 사용자만 조회</li>
     *   <li><strong>admin</strong>: 관리자 권한 사용자만 조회</li>
     * </ul>
     * 
     * <h3>정렬 옵션:</h3>
     * <ul>
     *   <li><strong>username</strong>: 사용자명 오름차순</li>
     *   <li><strong>username desc</strong>: 사용자명 내림차순</li>
     *   <li><strong>created_at</strong>: 생성일 오름차순</li>
     *   <li><strong>created_at desc</strong>: 생성일 내림차순</li>
     *   <li><strong>last_login desc</strong>: 최근 로그인 내림차순</li>
     * </ul>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10, 최대값: 100)
     * @param sort 정렬 조건 (예: "username", "created_at desc")
     * @param filter 필터 조건 (active, inactive, admin 등)
     * @param search 검색어 (사용자명, 이메일, 전체명에서 검색)
     * @return 페이징된 사용자 목록과 메타데이터
     * 
     * @throws FeignException.Unauthorized 토큰이 유효하지 않은 경우
     * @throws FeignException.Forbidden 사용자 관리 권한이 없는 경우
     * @throws FeignException.UnprocessableEntity 요청 파라미터가 잘못된 경우
     * 
     * @implNote 민감한 개인정보는 마스킹되어 반환됩니다.
     * @apiNote 대용량 사용자 데이터를 효율적으로 처리하기 위해 페이징이 필수입니다.
     */
    @GetMapping("/api/v1/users")
    @Operation(
        summary = "사용자 목록 조회",
        description = """
            시스템의 사용자 목록을 페이징하여 조회합니다.
            
            **검색 기능:**
            - 사용자명, 이메일, 전체명에서 부분 일치 검색
            - 대소문자 구분 없이 검색
            
            **필터링 옵션:**
            - active: 활성 사용자만 조회
            - inactive: 비활성 사용자만 조회
            - admin: 관리자 권한 사용자만 조회
            
            **정렬 옵션:**
            - username, created_at, last_login 등
            - desc 키워드로 내림차순 정렬 가능
            """,
        tags = {"사용자 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "사용자 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = UsersRead.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - 토큰이 유효하지 않음"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 사용자 관리 권한이 필요함"
        ),
        @ApiResponse(
            responseCode = "422",
            description = "요청 파라미터 오류 - 잘못된 페이지 번호, 크기 또는 정렬 조건"
        )
    })
    UsersRead getUsers(
        @RequestParam(value = "page", defaultValue = "1")
        @Parameter(
            description = "페이지 번호 (1부터 시작)",
            example = "1",
            schema = @Schema(minimum = "1", maximum = "999999")
        )
        Integer page,
        
        @RequestParam(value = "size", defaultValue = "10")
        @Parameter(
            description = "페이지당 항목 수",
            example = "10",
            schema = @Schema(minimum = "1", maximum = "100")
        )
        Integer size,
        
        @RequestParam(value = "sort", required = false)
        @Parameter(
            description = "정렬 조건",
            example = "username",
            schema = @Schema(
                allowableValues = {
                    "username", "username desc",
                    "email", "email desc",
                    "created_at", "created_at desc",
                    "last_login", "last_login desc",
                    "full_name", "full_name desc"
                }
            )
        )
        String sort,
        
        @RequestParam(value = "filter", required = false)
        @Parameter(
            description = "필터 조건",
            example = "active",
            schema = @Schema(
                allowableValues = {"active", "inactive", "admin", "user"}
            )
        )
        String filter,
        
        @RequestParam(value = "search", required = false)
        @Parameter(
            description = "검색어 (사용자명, 이메일, 전체명에서 검색)",
            example = "john"
        )
        String search
    );
    
    // ==================== 사용자 CRUD 관리 ====================
    
    /**
     * 특정 사용자 상세 정보 조회
     * 
     * <p>사용자 ID를 사용하여 특정 사용자의 상세 정보를 조회합니다.
     * 관리자가 사용자 관리 시 또는 팀원 정보 확인 시 사용됩니다.</p>
     * 
     * @param userId 조회할 사용자의 고유 식별자
     * @return 사용자 상세 정보
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 조회 권한 없음
     * @throws FeignException.NotFound 사용자를 찾을 수 없음
     */
    @GetMapping("/api/v1/users/{userId}")
    @Operation(
        summary = "특정 사용자 상세 조회",
        description = "사용자 ID로 특정 사용자의 상세 정보를 조회합니다.",
        tags = {"사용자 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "사용자 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = UserBase.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "조회 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    UserBase getUser(
        @PathVariable("userId")
        @Parameter(description = "사용자 ID", example = "user-123", required = true)
        String userId
    );
    
    /**
     * 새로운 사용자 등록
     * 
     * <p>새로운 사용자 계정을 등록합니다.
     * 관리자 권한이 필요하며, 팀에 새로운 멤버를 추가할 때 사용됩니다.</p>
     * 
     * @param request 사용자 등록 요청 정보
     * @return 등록된 사용자 정보
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 사용자 등록 권한 없음
     * @throws FeignException.UnprocessableEntity 중복 사용자명/이메일 또는 유효성 검증 실패
     */
    @PostMapping("/api/v1/users/register")
    @Operation(
        summary = "새로운 사용자 등록",
        description = """
            새로운 사용자 계정을 등록합니다.
            
            **필수 정보:**
            - username: 고유한 사용자명
            - email: 고유한 이메일 주소
            - password: 강력한 초기 비밀번호
            - first_name: 사용자 이름
            - last_name: 사용자 성
            
            **권한:** 관리자 이상 필요
            """,
        tags = {"사용자 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "사용자 등록 성공",
            content = @Content(schema = @Schema(implementation = UserRepresentation.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "사용자 등록 권한 없음"),
        @ApiResponse(responseCode = "422", description = "중복 사용자명/이메일 또는 유효성 검증 실패")
    })
    UserRepresentation registerUser(
        @RequestBody
        @Parameter(description = "사용자 등록 요청", required = true)
        RegisterUserPayload request
    );
    
    /**
     * SSO 사용자 등록
     * 
     * <p>SSO(Single Sign-On) 인증을 통해 외부 시스템에서 인증된 사용자를 등록합니다.
     * SAML, OAuth2 등의 외부 인증 시스템과 연동하여 사용자 계정을 생성할 때 사용됩니다.</p>
     * 
     * <h3>SSO 등록 특징:</h3>
     * <ul>
     *   <li><strong>외부 인증 기반</strong>: 이미 외부에서 인증된 사용자 정보 활용</li>
     *   <li><strong>비밀번호 불필요</strong>: SSO 인증을 사용하므로 별도 비밀번호 설정 불필요</li>
     *   <li><strong>자동 권한 매핑</strong>: 외부 시스템의 권한 정보를 기반으로 역할 자동 할당</li>
     * </ul>
     * 
     * @param request SSO 사용자 등록 요청 정보
     * @return 등록된 SSO 사용자 정보
     * 
     * @throws FeignException.Unauthorized SSO 인증 실패 또는 토큰 만료
     * @throws FeignException.Forbidden SSO 사용자 등록 권한 없음
     * @throws FeignException.UnprocessableEntity 중복 사용자명/이메일 또는 유효성 검증 실패
     * @throws FeignException.BadGateway SSO 시스템 연동 오류
     */
    @PostMapping("/api/v1/users/sso-register")
    @Operation(
        summary = "SSO 사용자 등록",
        description = """
            SSO(Single Sign-On) 인증을 통해 외부 시스템에서 인증된 사용자를 등록합니다.
            
            **SSO 등록 과정:**
            1. 외부 SSO 시스템에서 사용자 인증 완료
            2. 인증된 사용자 정보를 바탕으로 내부 계정 생성
            3. 권한 매핑 규칙에 따라 기본 역할 할당
            
            **필수 정보:**
            - username: SSO 시스템에서 제공되는 고유 사용자명
            - email: SSO 시스템에서 제공되는 이메일 주소
            - first_name: 사용자 이름
            - last_name: 사용자 성
            
            **권한:** SSO 시스템 관리자 또는 자동화 시스템 권한 필요
            """,
        tags = {"사용자 관리", "SSO"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "SSO 사용자 등록 성공",
            content = @Content(schema = @Schema(implementation = UserRepresentation.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "SSO 인증 실패 또는 토큰 만료"),
        @ApiResponse(responseCode = "403", description = "SSO 사용자 등록 권한 없음"),
        @ApiResponse(responseCode = "422", description = "중복 사용자명/이메일 또는 유효성 검증 실패"),
        @ApiResponse(responseCode = "502", description = "SSO 시스템 연동 오류")
    })
    UserRepresentation registerSsoUser(
        @RequestBody
        @Parameter(description = "SSO 사용자 등록 요청", required = true)
        SsoUserRegisterRequestDto request
    );
    
    /**
     * 사용자 정보 수정
     * 
     * <p>기존 사용자의 정보를 수정합니다.
     * 사용자는 자신의 프로필을, 관리자는 모든 사용자 정보를 수정할 수 있습니다.</p>
     * 
     * @param userId 수정할 사용자의 고유 식별자
     * @param request 사용자 수정 요청 정보
     * @return 수정된 사용자 정보
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 수정 권한 없음
     * @throws FeignException.NotFound 사용자를 찾을 수 없음
     * @throws FeignException.UnprocessableEntity 중복 이메일 또는 유효성 검증 실패
     */
    @PutMapping("/api/v1/users/{userId}")
    @Operation(
        summary = "사용자 정보 수정",
        description = """
            사용자 정보를 수정합니다.
            
            **권한 구분:**
            - 일반 사용자: 자신의 email, full_name만 수정 가능
            - 관리자: 모든 사용자의 모든 정보 수정 가능
            
            **수정 불가능:**
            - username (사용자명은 변경 불가)
            - password (별도 API 사용)
            """,
        tags = {"사용자 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "사용자 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = UserBase.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "중복 이메일 또는 유효성 검증 실패")
    })
    UserBase updateUser(
        @PathVariable("userId")
        @Parameter(description = "사용자 ID", example = "user-123", required = true)
        String userId,
        
        @RequestBody
        @Parameter(description = "사용자 수정 요청", required = true)
        UpdateUserPayload request
    );
    
    /**
     * 사용자 계정 삭제
     * 
     * <p>사용자 계정을 삭제합니다.
     * 관리자 권한이 필요하며, 실제로는 비활성화 처리됩니다.</p>
     * 
     * @param userId 삭제할 사용자의 고유 식별자
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 삭제 권한 없음
     * @throws FeignException.NotFound 사용자를 찾을 수 없음
     * @throws FeignException.Conflict 삭제할 수 없는 사용자 (슈퍼유저 등)
     * 
     * @implNote 실제 데이터 삭제가 아닌 soft delete (비활성화) 처리됩니다.
     * @apiNote 삭제된 사용자의 데이터는 보존되며, 필요시 복구 가능합니다.
     */
    @DeleteMapping("/api/v1/users/{userId}")
    @Operation(
        summary = "사용자 계정 삭제",
        description = """
            사용자 계정을 삭제합니다.
            
            **특징:**
            - Soft Delete: 실제 삭제가 아닌 비활성화 처리
            - 데이터 보존: 사용자가 생성한 콘텐츠는 유지
            - 복구 가능: 관리자가 필요시 계정 복구 가능
            
            **권한:** 관리자 이상 필요
            **제한:** 슈퍼유저 계정은 삭제 불가
            """,
        tags = {"사용자 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "삭제할 수 없는 사용자")
    })
    void deleteUser(
        @PathVariable("userId")
        @Parameter(description = "사용자 ID", example = "user-123", required = true)
        String userId
    );

    // ==================== 추가 사용자 관리 API ====================

    /**
     * 사용자 역할 매핑 조회
     * 
     * <p>특정 사용자에게 할당된 역할 목록을 조회합니다.</p>
     * 
     * @param userId 사용자 ID
     * @return 사용자 역할 매핑 목록
     */
    @GetMapping("/api/v1/users/{userId}/role-mappings")
    @Operation(
        summary = "사용자 역할 매핑 조회",
        description = "특정 사용자에게 할당된 역할 목록을 조회합니다.",
        tags = {"사용자 관리", "역할 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "역할 매핑 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "조회 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    Object getUserRoleMappings(@PathVariable String userId);

    /**
     * 사용자에게 역할 할당
     * 
     * <p>특정 사용자에게 새로운 역할을 할당합니다.</p>
     * 
     * @param userId 사용자 ID
     * @param request 역할 할당 요청
     * @return 할당 결과
     */
    @PostMapping("/api/v1/users/{userId}/role-mappings")
    @Operation(
        summary = "사용자 역할 할당",
        description = "특정 사용자에게 새로운 역할을 할당합니다.",
        tags = {"사용자 관리", "역할 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "역할 할당 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "역할 할당 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 역할을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 할당된 역할")
    })
    Object assignUserRole(
        @PathVariable String userId,
        @RequestBody Object request
    );

    /**
     * 사용자 역할 제거
     * 
     * <p>특정 사용자에게서 역할을 제거합니다.</p>
     * 
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    @DeleteMapping("/api/v1/users/{userId}/role-mappings/{roleId}")
    @Operation(
        summary = "사용자 역할 제거",
        description = "특정 사용자에게서 역할을 제거합니다.",
        tags = {"사용자 관리", "역할 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "역할 제거 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "역할 제거 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자, 역할 또는 매핑을 찾을 수 없음")
    })
    void removeUserRole(
        @PathVariable String userId,
        @PathVariable String roleId
    );

    /**
     * 사용자 그룹 매핑 조회
     * 
     * <p>특정 사용자가 속한 그룹 목록을 조회합니다.</p>
     * 
     * @param userId 사용자 ID
     * @return 사용자 그룹 매핑 목록
     */
    @GetMapping("/api/v1/users/{userId}/group-mappings")
    @Operation(
        summary = "사용자 그룹 매핑 조회",
        description = "특정 사용자가 속한 그룹 목록을 조회합니다.",
        tags = {"사용자 관리", "그룹 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그룹 매핑 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "조회 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    Object getUserGroupMappings(@PathVariable String userId);

    /**
     * 사용자를 그룹에 추가
     * 
     * <p>특정 사용자를 새로운 그룹에 추가합니다.</p>
     * 
     * @param userId 사용자 ID
     * @param groupId 그룹 ID
     * @return 추가 결과
     */
    @PutMapping("/api/v1/users/{userId}/group-mappings")
    @Operation(
        summary = "사용자 그룹 추가",
        description = "특정 사용자를 새로운 그룹에 추가합니다.",
        tags = {"사용자 관리", "그룹 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그룹 추가 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "그룹 추가 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 그룹을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 그룹에 속함")
    })
    Object addUserToGroup(
        @PathVariable String userId,
        @RequestParam("group_id")
        @Parameter(description = "그룹 ID", required = true)
        String groupId
    );

    /**
     * 사용자를 그룹에서 제거
     * 
     * <p>특정 사용자를 그룹에서 제거합니다.</p>
     * 
     * @param userId 사용자 ID
     * @param groupId 그룹 ID
     */
    @DeleteMapping("/api/v1/users/{userId}/group-mappings")
    @Operation(
        summary = "사용자 그룹 제거",
        description = "특정 사용자를 그룹에서 제거합니다.",
        tags = {"사용자 관리", "그룹 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "그룹 제거 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "그룹 제거 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자, 그룹 또는 매핑을 찾을 수 없음")
    })
    void removeUserFromGroup(
        @PathVariable String userId,
        @RequestParam("group_id")
        @Parameter(description = "그룹 ID", example = "ad3920c4-b9fd-40ae-b15a-3a798abad328", required = true)
        String groupId
    );

    /**
     * 사용자 비밀번호 변경
     * 
     * <p>특정 사용자의 비밀번호를 변경합니다.</p>
     * 
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청
     */
    @PutMapping("/api/v1/users/{userId}/reset-password")
    @Operation(
        summary = "사용자 비밀번호 변경",
        description = "특정 사용자의 비밀번호를 변경합니다.",
        tags = {"사용자 관리", "보안"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "비밀번호 변경 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "비밀번호 유효성 검증 실패")
    })
    void resetUserPassword(
        @PathVariable String userId,
        @RequestBody Object request
    );
    
    /**
     * 사용자 할당 가능한 역할 목록 조회
     * 
     * <p>특정 사용자에게 할당 가능한 역할(Role) 목록을 조회합니다.
     * 현재 사용자에게 할당되지 않은 역할들 중에서 권한 정책에 따라 할당 가능한 역할들을 반환합니다.</p>
     * 
     * <h3>조회 조건:</h3>
     * <ul>
     *   <li><strong>미할당 역할</strong>: 현재 사용자에게 할당되지 않은 역할</li>
     *   <li><strong>권한 정책</strong>: 요청자의 권한으로 할당 가능한 역할</li>
     *   <li><strong>활성 역할</strong>: 현재 활성화되어 있는 역할</li>
     * </ul>
     * 
     * @param userId 할당 가능한 역할을 조회할 사용자 ID
     * @return 할당 가능한 역할 목록
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 역할 조회 권한 없음
     * @throws FeignException.NotFound 사용자를 찾을 수 없음
     */
    @GetMapping("/api/v1/users/{userId}/available-roles")
    @Operation(
        summary = "사용자 할당 가능한 역할 목록 조회",
        description = """
            특정 사용자에게 할당 가능한 역할 목록을 조회합니다.
            
            **조회 결과:**
            - 현재 사용자에게 할당되지 않은 역할들
            - 요청자의 권한으로 할당 가능한 역할들
            - 현재 활성화되어 있는 역할들
            
            **용도:**
            - 사용자 관리 화면에서 역할 할당 드롭다운 구성
            - 역할 권한 관리 시 선택 가능한 역할 목록 제공
            
            **권한:** 사용자 관리 권한 필요
            """,
        tags = {"사용자 관리", "역할 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "할당 가능한 역할 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = AvailableRolesResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "역할 조회 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    AvailableRolesResponseDto getAvailableRoles(
        @PathVariable 
        @Parameter(description = "할당 가능한 역할을 조회할 사용자 ID", required = true)
        String userId
    );
    
    /**
     * 사용자 역할 매핑 일괄 수정
     * 
     * <p>특정 사용자의 역할 매핑을 일괄적으로 수정합니다.
     * 기존의 모든 역할 할당을 제거하고 새로운 역할 목록으로 대체합니다.</p>
     * 
     * <h3>일괄 수정 특징:</h3>
     * <ul>
     *   <li><strong>전체 교체</strong>: 기존 역할을 모두 제거하고 새 역할로 교체</li>
     *   <li><strong>원자적 처리</strong>: 모든 변경사항이 성공하거나 모두 실패</li>
     *   <li><strong>권한 검증</strong>: 할당하려는 모든 역할에 대한 권한 검증</li>
     * </ul>
     * 
     * @param userId 역할을 수정할 사용자 ID
     * @param request 새로운 역할 매핑 정보
     * @return 수정된 역할 매핑 정보
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 역할 수정 권한 없음
     * @throws FeignException.NotFound 사용자 또는 역할을 찾을 수 없음
     * @throws FeignException.UnprocessableEntity 역할 할당 규칙 위반
     */
    @PutMapping("/api/v1/users/{userId}/role-mappings")
    @Operation(
        summary = "사용자 역할 매핑 일괄 수정",
        description = """
            특정 사용자의 역할 매핑을 일괄적으로 수정합니다.
            
            요청 본문은 다음과 같은 항목의 배열(List)입니다:
            [
              {
                "project": { "id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", "name": "default" },
                "role": { "id": "a8209cf9-0a76-4ef5-9e43-017ba3200c40", "name": "admin", "description": null }
              }
            ]
            
            **일괄 수정 과정:**
            1. 기존 모든 역할 할당 제거
            2. 새로운 역할 목록 검증
            3. 새로운 역할 일괄 할당
            
            **주의사항:**
            - 기존 역할이 모두 제거되므로 신중하게 사용
            - 시스템 필수 역할이 있는 경우 반드시 포함
            - 원자적 처리로 중간 실패 시 모든 변경사항 롤백
            
            **권한:** 역할 관리 권한 필요
            """,
        tags = {"사용자 관리", "역할 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "역할 매핑 일괄 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "역할 수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 역할을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "역할 할당 규칙 위반")
    })
    void updateUserRoleMappings(
        @PathVariable 
        @Parameter(description = "역할을 수정할 사용자 ID", required = true)
        String userId,
        @RequestBody 
        @Parameter(description = "새로운 역할 매핑 정보 (배열)", required = true)
        List<UserRoleMappingUpdateItemDto> request
    );
    
    /**
     * 사용자 할당 가능한 그룹 목록 조회
     * 
     * <p>특정 사용자에게 할당 가능한 그룹(Group) 목록을 조회합니다.
     * 현재 사용자에게 할당되지 않은 그룹들 중에서 권한 정책에 따라 할당 가능한 그룹들을 반환합니다.</p>
     * 
     * <h3>조회 조건:</h3>
     * <ul>
     *   <li><strong>미할당 그룹</strong>: 현재 사용자에게 할당되지 않은 그룹</li>
     *   <li><strong>권한 정책</strong>: 요청자의 권한으로 할당 가능한 그룹</li>
     *   <li><strong>활성 그룹</strong>: 현재 활성화되어 있는 그룹</li>
     * </ul>
     * 
     * @param userId 할당 가능한 그룹을 조회할 사용자 ID
     * @return 할당 가능한 그룹 목록
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 그룹 조회 권한 없음
     * @throws FeignException.NotFound 사용자를 찾을 수 없음
     */
    @GetMapping("/api/v1/users/{userId}/available-groups")
    @Operation(
        summary = "사용자 할당 가능한 그룹 목록 조회",
        description = """
            특정 사용자에게 할당 가능한 그룹 목록을 조회합니다.
            
            **조회 결과:**
            - 현재 사용자에게 할당되지 않은 그룹들
            - 요청자의 권한으로 할당 가능한 그룹들
            - 현재 활성화되어 있는 그룹들
            
            **용도:**
            - 사용자 관리 화면에서 그룹 할당 드롭다운 구성
            - 팀 또는 부서 기반 권한 관리
            - 그룹 권한 관리 시 선택 가능한 그룹 목록 제공
            
            **권한:** 사용자 관리 권한 필요
            """,
        tags = {"사용자 관리", "그룹 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "할당 가능한 그룹 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = AvailableGroupsResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "그룹 조회 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    AvailableGroupsResponseDto getAvailableGroups(
        @PathVariable 
        @Parameter(description = "할당 가능한 그룹을 조회할 사용자 ID", required = true)
        String userId
    );
    
    /**
     * 사용자 그룹 매핑 일괄 수정
     * 
     * <p>특정 사용자의 그룹 매핑을 일괄적으로 수정합니다.
     * 기존의 모든 그룹 할당을 제거하고 새로운 그룹 목록으로 대체합니다.</p>
     * 
     * <h3>일괄 수정 특징:</h3>
     * <ul>
     *   <li><strong>전체 교체</strong>: 기존 그룹을 모두 제거하고 새 그룹으로 교체</li>
     *   <li><strong>원자적 처리</strong>: 모든 변경사항이 성공하거나 모두 실패</li>
     *   <li><strong>권한 검증</strong>: 할당하려는 모든 그룹에 대한 권한 검증</li>
     * </ul>
     * 
     * @param userId 그룹을 수정할 사용자 ID
     * @param request 새로운 그룹 매핑 정보
     * @return 수정된 그룹 매핑 정보
     * 
     * @throws FeignException.Unauthorized 인증 실패
     * @throws FeignException.Forbidden 그룹 수정 권한 없음
     * @throws FeignException.NotFound 사용자 또는 그룹을 찾을 수 없음
     * @throws FeignException.UnprocessableEntity 그룹 할당 규칙 위반
     */
    @PutMapping("/api/v1/users/{userId}/group-mappings")
    @Operation(
        summary = "사용자 그룹 매핑 일괄 수정",
        description = """
            특정 사용자의 그룹 매핑을 일괄적으로 수정합니다.
            
            **일괄 수정 과정:**
            1. 기존 모든 그룹 할당 제거
            2. 새로운 그룹 목록 검증
            3. 새로운 그룹 일괄 할당
            
            **주의사항:**
            - 기존 그룹이 모두 제거되므로 신중하게 사용
            - 시스템 필수 그룹이 있는 경우 반드시 포함
            - 원자적 처리로 중간 실패 시 모든 변경사항 롤백
            
            **권한:** 그룹 관리 권한 필요
            """,
        tags = {"사용자 관리", "그룹 관리"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그룹 매핑 일괄 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "그룹 수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 그룹을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "그룹 할당 규칙 위반")
    })
    Object updateUserGroupMappings(
        @PathVariable 
        @Parameter(description = "그룹을 수정할 사용자 ID", required = true)
        String userId,
        @RequestBody 
        @Parameter(description = "새로운 그룹 매핑 정보", required = true)
        Object request
    );
    
    /**
     * 사용자 비밀번호 변경
     * 
     * <p>사용자가 본인의 비밀번호를 변경합니다.
     * 비밀번호 리셋과 달리 현재 비밀번호 확인이 필요한 보안이 강화된 변경 작업입니다.</p>
     * 
     * <h3>비밀번호 변경 vs 리셋 차이점:</h3>
     * <ul>
     *   <li><strong>변경(Change)</strong>: 현재 비밀번호 확인 필요, 사용자 본인 인증</li>
     *   <li><strong>리셋(Reset)</strong>: 관리자 권한으로 강제 변경, 현재 비밀번호 불필요</li>
     * </ul>
     * 
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param request 비밀번호 변경 요청 정보
     * 
     * @throws FeignException.Unauthorized 인증 실패 또는 현재 비밀번호 불일치
     * @throws FeignException.Forbidden 비밀번호 변경 권한 없음
     * @throws FeignException.NotFound 사용자를 찾을 수 없음
     * @throws FeignException.UnprocessableEntity 새 비밀번호 정책 위반
     */
    @PutMapping("/api/v1/users/{userId}/change-password")
    @Operation(
        summary = "사용자 비밀번호 변경",
        description = """
            사용자가 본인의 비밀번호를 변경합니다.
            
            **변경 과정:**
            1. 현재 비밀번호 확인
            2. 새 비밀번호 정책 검증
            3. 비밀번호 해시화 및 저장
            4. 변경 이력 기록
            
            **보안 요구사항:**
            - 현재 비밀번호 확인 필수
            - 새 비밀번호는 시스템 정책 준수
            - 이전 비밀번호와 다른 값
            
            **권한:** 본인 또는 관리자 권한 필요
            """,
        tags = {"사용자 관리", "보안"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패 또는 현재 비밀번호 불일치"),
        @ApiResponse(responseCode = "403", description = "비밀번호 변경 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "새 비밀번호 정책 위반")
    })
    void changeUserPassword(
        @PathVariable 
        @Parameter(description = "비밀번호를 변경할 사용자 ID", required = true)
        String userId,
        @RequestBody 
        @Parameter(description = "비밀번호 변경 요청", required = true)
        PasswordChangeRequestDto request
    );

    /**
     * 사용자 할당 가능한 역할 목록 조회 (role-available)
     *
     * <p>특정 사용자에게 할당 가능한 역할(Role) 목록을 조회합니다. adxp.mobigen.com 문서의
     * GET /api/v1/users/{user_id}/role-available 엔드포인트를 호출합니다.</p>
     *
     * @param userId 할당 가능한 역할을 조회할 사용자 ID
     * @return 할당 가능한 역할 목록 응답
     */
    @GetMapping("/api/v1/users/{userId}/role-available")
    @Operation(
        summary = "사용자 할당 가능한 역할 목록 조회 (role-available)",
        description = "특정 사용자에게 할당 가능한 역할 목록을 조회합니다.",
        tags = {"사용자 관리", "역할 관리"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "할당 가능한 역할 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = RoleAvailablePageResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "역할 조회 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    RoleAvailablePageResponseDto getUserAvailableRoles(
        @PathVariable 
        @Parameter(description = "할당 가능한 역할을 조회할 사용자 ID", required = true)
        String userId,
        @RequestParam(value = "page", defaultValue = "1")
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        Integer page,
        @RequestParam(value = "size", defaultValue = "10")
        @Parameter(description = "페이지당 항목 수", example = "10")
        Integer size
    );
}
