package com.skax.aiplatform.client.ione.admin;

import com.skax.aiplatform.client.ione.config.IoneFeignConfig;
import com.skax.aiplatform.client.ione.admin.dto.request.IntfAdminUserCreateRequest;
import com.skax.aiplatform.client.ione.admin.dto.request.IntfAdminUserUpdateRequest;
import com.skax.aiplatform.client.ione.admin.dto.request.IntfAdminUserDeleteRequest;
import com.skax.aiplatform.client.ione.admin.dto.response.IntfAdminUserVo;
import com.skax.aiplatform.client.ione.admin.dto.response.IntfAdminUserDetailVo;
import com.skax.aiplatform.client.ione.admin.dto.response.IntfAdminUserResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

/**
 * iONE 어드민 사용자 관리 클라이언트
 * 
 * <p>어드민 사용자 관리 API와의 통신을 담당하는 Feign Client입니다.
 * 5개의 주요 API를 제공합니다:</p>
 * 
 * <ul>
 *   <li>사용자 목록 조회</li>
 *   <li>사용자 정보 상세 조회</li>
 *   <li>관리자 생성</li>
 *   <li>관리자 수정</li>
 *   <li>관리자 삭제</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@FeignClient(
    name = "ione-admin-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneAdminClient {

    /**
     * [API-ADM-001] 사용자 목록 조회
     * 
     * <p>등록된 어드민 사용자 목록을 조회합니다.</p>
     * 
     * @return 어드민 사용자 목록
     */
    @GetMapping("/admin/intf/v1/system/adm/list.idt")
    @Operation(summary = "[API-ADM-001] 사용자 목록 조회", description = "등록된 어드민 사용자 목록을 조회합니다")
    List<IntfAdminUserVo> getAdminUserList();

    /**
     * [API-ADM-002] 사용자 정보 조회
     * 
     * <p>특정 어드민 사용자의 상세 정보를 조회합니다.</p>
     * 
     * @param id 조회할 사용자 ID
     * @return 어드민 사용자 상세 정보
     */
    @GetMapping("/admin/intf/v1/system/adm/{id}/details.idt")
    @Operation(summary = "[API-ADM-002] 사용자 정보 조회", description = "특정 어드민 사용자의 상세 정보를 조회합니다")
    IntfAdminUserDetailVo getAdminUserDetails(
            @Parameter(description = "조회할 사용자 ID") @PathVariable("id") String id
    );

    /**
     * [API-ADM-003] 관리자 생성
     * 
     * <p>새로운 어드민 사용자를 생성합니다.
     * 사용자 기본 정보와 권한 정보를 설정할 수 있습니다.</p>
     * 
     * @param request 관리자 생성 요청
     * @return 관리자 생성 결과
     */
    @PostMapping("/admin/intf/v1/system/adm/regist.idt")
    @Operation(summary = "[API-ADM-003] 관리자 생성", description = "새로운 어드민 사용자를 생성합니다")
    IntfAdminUserResult createAdminUser(@RequestBody IntfAdminUserCreateRequest request);

    /**
     * [API-ADM-004] 관리자 수정
     * 
     * <p>기존 어드민 사용자의 정보를 수정합니다.
     * 사용자 정보, 권한, 상태 등을 변경할 수 있습니다.</p>
     * 
     * @param request 관리자 수정 요청
     * @return 관리자 수정 결과
     */
    @PutMapping("/admin/intf/v1/system/adm/update.idt")
    @Operation(summary = "[API-ADM-004] 관리자 수정", description = "기존 어드민 사용자의 정보를 수정합니다")
    IntfAdminUserResult updateAdminUser(@RequestBody IntfAdminUserUpdateRequest request);

    /**
     * [API-ADM-005] 관리자 삭제
     * 
     * <p>어드민 사용자를 삭제합니다.
     * 실제 삭제 또는 비활성화 처리가 가능합니다.</p>
     * 
     * @param request 관리자 삭제 요청
     * @return 관리자 삭제 결과
     */
    @PostMapping("/admin/intf/v1/system/adm/delete.idt")
    @Operation(summary = "[API-ADM-005] 관리자 삭제", description = "어드민 사용자를 삭제합니다")
    IntfAdminUserResult deleteAdminUser(@RequestBody IntfAdminUserDeleteRequest request);
}