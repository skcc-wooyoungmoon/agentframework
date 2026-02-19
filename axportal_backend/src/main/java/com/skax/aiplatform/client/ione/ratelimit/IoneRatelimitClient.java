package com.skax.aiplatform.client.ione.ratelimit;

import com.skax.aiplatform.client.ione.config.IoneFeignConfig;
import com.skax.aiplatform.client.ione.ratelimit.dto.request.IntfApiKeyPolicyConfigRequest;
import com.skax.aiplatform.client.ione.ratelimit.dto.request.IntfApiKeyPolicyReplenishRequest;
import com.skax.aiplatform.client.ione.ratelimit.dto.request.IntfRateLimitPolicyRequest;
import com.skax.aiplatform.client.ione.ratelimit.dto.response.IntfRatelimitPolicyVo;
import com.skax.aiplatform.client.ione.ratelimit.dto.response.IntfRatelimitUpdateResult;
import com.skax.aiplatform.client.ione.ratelimit.dto.response.PaginatedPolicyResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * iONE Ratelimit 클라이언트
 * 
 * <p>Ratelimit 관련 설정 관리 API와의 통신을 담당하는 Feign Client입니다.
 * 5개의 주요 API를 제공합니다:</p>
 * 
 * <ul>
 *   <li>정책 목록 조회</li>
 *   <li>정책 Pagination 조회</li>
 *   <li>정책 추가/수정/삭제</li>
 *   <li>API KEY 정책 설정</li>
 *   <li>API KEY 정책 limit 충전</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@FeignClient(
    name = "ione-ratelimit-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneRatelimitClient {

    /**
     * [API-LMT-001] Ratelimit정책 목록 조회
     * 
     * <p>설정된 Ratelimit정책 목록을 전체 조회합니다.</p>
     * 
     * @return Ratelimit정책 목록
     */
    @GetMapping("/admin/intf/v1/system/ratelimit/policy/list.idt")
    @Operation(summary = "[API-LMT-001] Ratelimit정책 목록 조회", description = "설정된 Ratelimit정책 목록을 조회합니다")
    List<IntfRatelimitPolicyVo> selectPolicyList();

    /**
     * [API-LMT-002] Ratelimit정책 목록 Pagination 조회
     * 
     * <p>정책 목록을 페이지 단위로 조회합니다. 검색 조건을 통해 특정 정책을 찾을 수 있습니다.</p>
     * 
     * @param pageNum 페이지 번호 (1부터 시작)
     * @param pageSize 페이지당 항목 수
     * @param policyId 검색할 정책 명 (선택사항)
     * @return 페이지네이션된 정책 결과
     */
    @GetMapping("/admin/intf/v1/system/ratelimit/page/policy.idt")
    @Operation(summary = "[API-LMT-002] Ratelimit정책 목록 Pagination 조회", description = "정책 목록을 Paginate해서 조회합니다")
    PaginatedPolicyResult getPolicyWithPagination(
            @Parameter(description = "페이지 번호") @RequestParam("pageNum") Integer pageNum,
            @Parameter(description = "페이지 크기") @RequestParam("pageSize") Integer pageSize,
            @Parameter(description = "검색할 정책 명") @RequestParam(value = "policyId", required = false) String policyId
    );

    /**
     * [API-LMT-003] Ratelimit정책 추가/수정/삭제
     * 
     * <p>Ratelimit정책의 CRUD 작업을 수행합니다. 
     * 요청 타입에 따라 추가/수정/삭제가 결정됩니다.</p>
     * 
     * @param request 정책 업데이트 요청
     * @return Ratelimit 업데이트 결과
     */
    @PutMapping("/admin/intf/v1/system/ratelimit/policy.idt")
    @Operation(summary = "[API-LMT-003] Ratelimit정책 추가/수정/삭제", description = "Ratelimit정책을 추가/수정/삭제 합니다")
    IntfRatelimitUpdateResult updatePolicy(@RequestBody IntfRateLimitPolicyRequest request);

    /**
     * [API-LMT-004] API KEY 정책 추가/수정/삭제
     * 
     * <p>특정 API KEY에 대한 Ratelimit 정책을 설정합니다.
     * API KEY별로 개별적인 제한 정책을 적용할 수 있습니다.</p>
     * 
     * @param request API KEY 정책 설정 요청
     * @return Ratelimit 업데이트 결과
     */
    @PostMapping("/admin/intf/v1/system/ratelimit/apikey/config.idt")
    @Operation(summary = "[API-LMT-004] API KEY 정책 추가/수정/삭제", description = "API KEY에 대한 정책을 추가/수정/삭제합니다")
    IntfRatelimitUpdateResult configApiKeyPolicy(@RequestBody IntfApiKeyPolicyConfigRequest request);

    /**
     * [API-LMT-005] API KEY 정책 limit 충전
     * 
     * <p>API KEY에 설정된 사용 한도를 충전합니다.
     * 사용량이 초과된 API KEY의 제한을 해제하거나 추가 사용량을 부여할 때 사용합니다.</p>
     * 
     * @param request API KEY 정책 충전 요청
     * @return Ratelimit 업데이트 결과
     */
    @PostMapping("/admin/intf/v1/system/ratelimit/apikey/replenish.idt")
    @Operation(summary = "[API-LMT-005] API KEY 정책 limit 충전", description = "API KEY 정책의 limit을 충전합니다")
    IntfRatelimitUpdateResult replenishApiKeyPolicy(@RequestBody IntfApiKeyPolicyReplenishRequest request);
}
