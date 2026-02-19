package com.skax.aiplatform.controller.sample;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.exception.ActionType;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.sample.request.SampleUserCreateReq;
import com.skax.aiplatform.dto.sample.request.SampleUserUpdateReq;
import com.skax.aiplatform.dto.sample.response.SampleUserRes;
import com.skax.aiplatform.service.sample.SampleUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 샘플 사용자 컨트롤러 (AxResponseEntity 적용)
 * 
 * <p>샘플 사용자 관리 API 엔드포인트를 제공합니다.
 * 사용자 생성, 조회, 수정, 삭제 및 검색 기능을 포함합니다.
 * AxResponseEntity를 통해 통합된 응답 형식을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/samples")
@RequiredArgsConstructor
@Tag(name = "Sample User Management", description = "샘플 사용자 관리 API")
public class SampleUserController {

    private final SampleUserService sampleUserService;

    /**
     * 모든 샘플 사용자 조회 (페이징)
     * 
     * @param pageable 페이징 정보
     * @return 사용자 목록 (페이징)
     */
    @GetMapping
    @Operation(
        summary = "모든 샘플 사용자 조회",
        description = "모든 샘플 사용자를 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    public AxResponseEntity<PageResponse<SampleUserRes>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("모든 샘플 사용자 조회 요청");
        
        Page<SampleUserRes> users = sampleUserService.getAllUsers(pageable);
        
        log.info("샘플 사용자 목록 조회 성공: 총 {}명, 현재 페이지 {}명", 
                users.getTotalElements(), users.getNumberOfElements());
        
        return AxResponseEntity.okPage(users, "사용자 목록을 성공적으로 조회했습니다.");
    }

    /**
     * ID로 샘플 사용자 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "ID로 샘플 사용자 조회",
        description = "사용자 ID를 통해 특정 샘플 사용자 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public AxResponseEntity<SampleUserRes> getUserById(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.info("ID로 샘플 사용자 조회: {}", id);
        
        SampleUserRes user = sampleUserService.getUserById(id);
        
        log.info("샘플 사용자 조회 성공: {}", user.getUsername());
        
        return AxResponseEntity.ok(user, "사용자 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 사용자명으로 샘플 사용자 조회
     * 
     * @param username 사용자명
     * @return 사용자 정보
     */
    @GetMapping("/username/{username}")
    @Operation(
        summary = "사용자명으로 샘플 사용자 조회",
        description = "사용자명을 통해 특정 샘플 사용자 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public AxResponseEntity<SampleUserRes> getUserByUsername(
            @PathVariable @Parameter(description = "사용자명", example = "john_doe") String username) {
        
        log.info("사용자명으로 샘플 사용자 조회: {}", username);
        
        SampleUserRes user = sampleUserService.getUserByUsername(username);
        
        log.info("샘플 사용자 조회 성공: {}", user.getUsername());
        
        return AxResponseEntity.ok(user, "사용자 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 새로운 샘플 사용자 생성
     * 
     * @param createReq 사용자 생성 요청
     * @return 생성된 사용자 정보
     */
    @PostMapping
    @Operation(
        summary = "새로운 샘플 사용자 생성",
        description = "새로운 샘플 사용자를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "중복된 사용자명 또는 이메일")
    })
    public AxResponseEntity<SampleUserRes> createUser(
            @Valid @RequestBody SampleUserCreateReq createReq) {
        
        log.info("새로운 샘플 사용자 생성 요청: {}", createReq.getUsername());
        
        SampleUserRes user = sampleUserService.createUser(createReq);
        
        log.info("샘플 사용자 생성 성공: {}", user.getUsername());
        
        return AxResponseEntity.created(user, "사용자가 성공적으로 생성되었습니다.");
    }

    /**
     * 샘플 사용자 정보 수정
     * 
     * @param id 사용자 ID
     * @param updateReq 사용자 수정 요청
     * @return 수정된 사용자 정보
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "샘플 사용자 정보 수정",
        description = "기존 샘플 사용자의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "중복된 이메일")
    })
    public AxResponseEntity<SampleUserRes> updateUser(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id,
            @Valid @RequestBody SampleUserUpdateReq updateReq) {
        
        log.info("샘플 사용자 정보 수정 요청: ID={}", id);
        
        SampleUserRes user = sampleUserService.updateUser(id, updateReq);
        
        log.info("샘플 사용자 정보 수정 성공: {}", user.getUsername());
        
        return AxResponseEntity.updated(user, "사용자 정보가 성공적으로 수정되었습니다.");
    }

    /**
     * 샘플 사용자 삭제
     * 
     * @param id 사용자 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "샘플 사용자 삭제",
        description = "특정 샘플 사용자를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public AxResponseEntity<Void> deleteUser(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.info("샘플 사용자 삭제 요청: ID={}", id);
        
        sampleUserService.deleteUser(id);
        
        log.info("샘플 사용자 삭제 성공: ID={}", id);
        
        return AxResponseEntity.deleted("사용자가 성공적으로 삭제되었습니다.");
    }

    /**
     * 활성화 상태별 사용자 조회 (페이징)
     * 
     * @param isActive 활성화 여부
     * @param pageable 페이징 정보
     * @return 사용자 목록 (페이징)
     */
    @GetMapping("/status/{isActive}")
    @Operation(
        summary = "활성화 상태별 사용자 조회",
        description = "활성화 상태에 따라 사용자를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    public AxResponseEntity<PageResponse<SampleUserRes>> getUsersByActiveStatus(
            @PathVariable @Parameter(description = "활성화 여부", example = "true") Boolean isActive,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("활성화 상태별 샘플 사용자 조회: isActive={}", isActive);
        
        Page<SampleUserRes> users = sampleUserService.getUsersByActiveStatus(isActive, pageable);
        
        log.info("활성화 상태별 사용자 조회 성공: 총 {}명", users.getTotalElements());
        
        return AxResponseEntity.okPage(users, "활성화 상태별 사용자 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 부서별 사용자 조회 (페이징)
     * 
     * @param department 부서명
     * @param pageable 페이징 정보
     * @return 사용자 목록 (페이징)
     */
    @GetMapping("/department")
    @Operation(
        summary = "부서별 사용자 조회",
        description = "특정 부서에 속한 사용자를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    public AxResponseEntity<PageResponse<SampleUserRes>> getUsersByDepartment(
            @RequestParam @Parameter(description = "부서명", example = "개발팀") String department,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("부서별 샘플 사용자 조회: department={}", department);
        
        Page<SampleUserRes> users = sampleUserService.getUsersByDepartment(department, pageable);
        
        log.info("부서별 사용자 조회 성공: 총 {}명", users.getTotalElements());
        
        return AxResponseEntity.okPage(users, "부서별 사용자 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 키워드로 사용자 검색 (페이징)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 사용자 목록 (페이징)
     */
    @GetMapping("/search")
    @Operation(
        summary = "키워드로 사용자 검색",
        description = "이름, 사용자명, 이메일에서 키워드를 검색하여 사용자를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 검색 성공")
    })
    public AxResponseEntity<PageResponse<SampleUserRes>> searchUsers(
            @RequestParam @Parameter(description = "검색 키워드", example = "홍길동") String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("키워드로 샘플 사용자 검색: keyword={}", keyword);
        
        Page<SampleUserRes> users = sampleUserService.searchUsers(keyword, pageable);
        
        log.info("키워드 검색 성공: 총 {}명", users.getTotalElements());
        
        return AxResponseEntity.okPage(users, "사용자 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * 활성화된 사용자 수 조회
     * 
     * @return 활성화된 사용자 수
     */
    @GetMapping("/count/active")
    @Operation(
        summary = "활성화된 사용자 수 조회",
        description = "현재 활성화된 사용자의 총 수를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 수 조회 성공")
    })
    public AxResponseEntity<Long> getActiveUserCount() {
        
        log.info("활성화된 샘플 사용자 수 조회");
        
        long count = sampleUserService.getActiveUserCount();
        
        log.info("활성화된 사용자 수 조회 성공: {}명", count);
        
        return AxResponseEntity.ok(count, "활성화된 사용자 수를 성공적으로 조회했습니다.");
    }

    // ==================== ActionType 사용 예제 ====================

    /**
     * ActionType 예제 - RETRY를 요구하는 에러 시나리오
     * 
     * @param id 사용자 ID
     * @return 에러 응답 (ActionType.RETRY)
     */
    @PostMapping("/{id}/connect")
    @Operation(
        summary = "외부 서비스 연결 시도",
        description = "사용자의 외부 서비스 연결을 시도합니다. 실패 시 재시도를 요구합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "502", description = "외부 서비스 연결 실패 - 재시도 필요")
    })
    public AxResponseEntity<Void> connectExternalService(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.warn("외부 서비스 연결 실패 - 사용자 ID: {}", id);
        
        // ActionType.RETRY를 사용한 에러 응답 예제
        return AxResponseEntity.failure(
            "외부 서비스 연결에 실패했습니다",
            "BAD_GATEWAY", 
            "E002",
            502,
            "Bad Gateway",
            "외부 API 서버가 일시적으로 응답하지 않습니다. 잠시 후 다시 시도해주세요.",
            ActionType.RETRY,
            org.springframework.http.HttpStatus.BAD_GATEWAY
        );
    }

    /**
     * ActionType 예제 - PREVIOUS를 요구하는 에러 시나리오
     * 
     * @param id 사용자 ID
     * @return 에러 응답 (ActionType.PREVIOUS)
     */
    @PostMapping("/{id}/wizard/next")
    @Operation(
        summary = "설정 마법사 다음 단계",
        description = "설정 마법사의 다음 단계로 진행합니다. 필수 입력값 부족 시 이전 단계로 돌아가도록 안내합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "필수 입력값 부족 - 이전 단계로 이동 필요")
    })
    public AxResponseEntity<Void> wizardNextStep(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.warn("설정 마법사 필수 입력값 부족 - 사용자 ID: {}", id);
        
        // ActionType.PREVIOUS를 사용한 에러 응답 예제
        return AxResponseEntity.failure(
            "필수 입력값이 부족합니다",
            "BAD_REQUEST", 
            "C001",
            400,
            "Bad Request",
            "이메일 주소와 전화번호를 모두 입력해야 다음 단계로 진행할 수 있습니다.",
            ActionType.PREVIOUS,
            org.springframework.http.HttpStatus.BAD_REQUEST
        );
    }

    /**
     * ActionType 예제 - CANCEL을 요구하는 에러 시나리오
     * 
     * @param id 사용자 ID
     * @return 에러 응답 (ActionType.CANCEL)
     */
    @DeleteMapping("/{id}/force")
    @Operation(
        summary = "사용자 강제 삭제",
        description = "사용자를 강제 삭제합니다. 중요 데이터 존재 시 작업 취소를 권장합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "409", description = "중요 데이터 존재 - 작업 취소 권장")
    })
    public AxResponseEntity<Void> forceDeleteUser(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.warn("사용자 강제 삭제 시도 - 중요 데이터 존재, 사용자 ID: {}", id);
        
        // ActionType.CANCEL을 사용한 에러 응답 예제
        return AxResponseEntity.failure(
            "중요 데이터가 존재합니다",
            "CONFLICT", 
            "D001",
            409,
            "Conflict",
            "이 사용자는 아직 처리되지 않은 주문 데이터가 있습니다. 데이터 손실 방지를 위해 작업을 취소하는 것을 권장합니다.",
            ActionType.CANCEL,
            org.springframework.http.HttpStatus.CONFLICT
        );
    }

    // ==================== ActionType Exception 사용 예제 ====================

    /**
     * ActionType.RETRY를 사용한 BusinessException 예제
     * 
     * @param id 사용자 ID
     * @return 에러 응답 (ActionType.RETRY)
     */
    @PostMapping("/{id}/sync")
    @Operation(
        summary = "사용자 데이터 동기화",
        description = "외부 시스템과 사용자 데이터를 동기화합니다. 실패 시 재시도를 권장합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "502", description = "동기화 실패 - 재시도 필요")
    })
    public AxResponseEntity<Void> syncUserData(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.warn("사용자 데이터 동기화 실패 - 사용자 ID: {}", id);
        
        // ActionType.RETRY를 사용한 BusinessException 예제
        throw new com.skax.aiplatform.common.exception.BusinessException(
            com.skax.aiplatform.common.exception.ErrorCode.EXTERNAL_SERVICE_ERROR,
            "사용자 데이터 동기화에 실패했습니다. 외부 시스템이 일시적으로 응답하지 않습니다.",
            ActionType.RETRY
        );
    }

    /**
     * ActionType.PREVIOUS를 사용한 ValidationException 예제
     * 
     * @param id 사용자 ID
     * @return 에러 응답 (ActionType.PREVIOUS)
     */
    @PostMapping("/{id}/profile/complete")
    @Operation(
        summary = "프로필 완성",
        description = "사용자 프로필을 완성합니다. 필수 정보 부족 시 이전 단계로 안내합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "필수 정보 부족 - 이전 단계로 이동 필요")
    })
    public AxResponseEntity<Void> completeProfile(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.warn("프로필 완성 실패 - 필수 정보 부족, 사용자 ID: {}", id);
        
        // ActionType.PREVIOUS를 사용한 ValidationException 예제
        throw new com.skax.aiplatform.common.exception.ValidationException(
            com.skax.aiplatform.common.exception.ErrorCode.INVALID_INPUT_VALUE,
            "필수 정보가 누락되었습니다. 이메일 주소와 전화번호를 입력해주세요.",
            ActionType.PREVIOUS
        );
    }

    /**
     * ActionType.CANCEL을 사용한 BusinessException 예제
     * 
     * @param id 사용자 ID
     * @return 에러 응답 (ActionType.CANCEL)
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(
        summary = "사용자 영구 삭제",
        description = "사용자를 영구적으로 삭제합니다. 중요 데이터 존재 시 작업 취소를 권장합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "409", description = "중요 데이터 존재 - 작업 취소 권장")
    })
    public AxResponseEntity<Void> permanentDeleteUser(
            @PathVariable @Parameter(description = "사용자 ID", example = "1") Long id) {
        
        log.warn("사용자 영구 삭제 시도 - 중요 데이터 존재, 사용자 ID: {}", id);
        
        // ActionType.CANCEL을 사용한 BusinessException 예제
        throw new com.skax.aiplatform.common.exception.BusinessException(
            com.skax.aiplatform.common.exception.ErrorCode.RESOURCE_NOT_FOUND,
            "이 사용자는 처리되지 않은 중요 데이터가 있어 삭제할 수 없습니다.",
            ActionType.CANCEL
        );
    }
}
