package com.skax.aiplatform.client.ione.apikey;

import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyDeleteRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRegistRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRenewRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRescheduleRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyUpdateRequest;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyListResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyRegistResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyRenewResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyUpdateResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyVo;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfPubApiResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfRateLimitStatisticsVo;
import com.skax.aiplatform.client.ione.common.dto.InfRequestBody;
import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.config.IoneFeignConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * iONE Open API Key 클라이언트
 * 
 * Gateway 등록된 API 호출시 사용될 API Key 관리 API와의 통신을 담당하는 Feign Client입니다.
 * API Key 생성, 조회, 수정, 삭제, 갱신, 재발급 등의 기능을 제공합니다.
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@FeignClient(
    name = "ione-apikey-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneApiKeyClient {

    /**
     * [API-KEY-001] Open API Key 목록 조회
     */
    @GetMapping("/admin/intf/v1/system/auth/apikey/list.idt")
    @Operation(summary = "[API-KEY-001] Open API Key 목록 조회", description = "발급된 Open API Key 전체 목록을 페이징 처리하여 조회합니다")
    IntfOpenApiKeyListResult selectApiKeyList(
            @Parameter(description = "페이지 번호") @RequestParam("pageNum") Integer pageNum,
            @Parameter(description = "페이지 크기") @RequestParam("pageSize") Integer pageSize,
            @Parameter(description = "파트너ID") @RequestParam(value = "partnerId", required = false) String partnerId,
            @Parameter(description = "그룹ID") @RequestParam(value = "grpId", required = false) String grpId,
            @Parameter(description = "scope") @RequestParam(value = "scope", required = false) String scope,
            @Parameter(description = "orderBy") @RequestParam(value = "orderBy", required = false) String orderBy
    );

    /**
     * [API-KEY-002] Open API Key 단건 조회
     */
    @GetMapping("/admin/intf/v1/system/auth/apikey/{openApiKey}")
    @Operation(summary = "[API-KEY-002] Open API Key 단건 조회", description = "Open API Key로 해당 Key에 대한 상세 정보를 조회합니다")
    InfResponseBody<IntfOpenApiKeyVo> selectApiKey(@Parameter(description = "발급된 Open API Key") @PathVariable("openApiKey") String openApiKey);

    /**
     * [API-KEY-003] Open API Key 신규 발급
     */
    @PostMapping("/admin/intf/v1/system/auth/apikey.idt")
    @Operation(summary = "[API-KEY-003] Open API Key 신규 발급", description = "Open API Key를 새로 발급합니다")
    InfResponseBody<IntfOpenApiKeyRegistResult> issueApiKey(@RequestBody InfRequestBody<IntfOpenApiKeyRegistRequest> request);

    /**
     * [API-KEY-004] Open API Key 수정
     */
    @PatchMapping("/admin/intf/v1/system/auth/apikey.idt")
    @Operation(summary = "[API-KEY-004] Open API Key 수정", description = "Open API Key 상세 정보를 수정합니다")
    IntfOpenApiKeyUpdateResult updateApiKey(@RequestBody InfRequestBody<IntfOpenApiKeyUpdateRequest> request);

    /**
     * [API-KEY-005] Open API Key 삭제
     */
    @PostMapping("/admin/intf/v1/system/auth/apikey/delete.idt")
    @Operation(summary = "[API-KEY-005] Open API Key 삭제", description = "Open API Key를 삭제합니다")
    InfResponseBody<Void> deleteApiKey(@RequestBody InfRequestBody<IntfOpenApiKeyDeleteRequest> request);

    /**
     * [API-KEY-006] Open API Key 갱신
     */
    @PatchMapping("/admin/intf/v1/system/auth/apikey/renew.idt")
    @Operation(summary = "[API-KEY-006] Open API Key 갱신", description = "Open API Key 유효 기간을 갱신합니다")
    IntfOpenApiKeyRenewResult renewApiKey(@RequestBody IntfOpenApiKeyRenewRequest request);

    /**
     * [API-KEY-007] Open API Key 재발급
     */
    @PostMapping("/admin/intf/v1/system/auth/apikey/regen.idt")
    @Operation(summary = "[API-KEY-007] Open API Key 재발급", description = "Open API Key를 재발급합니다")
    InfResponseBody<IntfOpenApiKeyRegistResult> regenerateApiKey(@RequestBody InfRequestBody<IntfOpenApiKeyRegistRequest> request);

    /**
     * [API-KEY-008] Open API Key 유효기간 재설정
     */
    @PatchMapping("/admin/intf/v1/system/auth/apikey/reschedule.idt")
    @Operation(summary = "[API-KEY-008] Open API Key 유효기간 재설정", description = "Open API Key의 유효기간을 재설정합니다")
    InfResponseBody<Void> rescheduleApiKey(@RequestBody InfRequestBody<IntfOpenApiKeyRescheduleRequest> request);

    /**
     * [API-KEY-009] Open API Key scope추가
     */
    @PatchMapping("/admin/intf/v1/system/auth/apikey/addScope.idt")
    @Operation(summary = "[API-KEY-009] Open API Key scope추가", description = "Open API Key에 새로운 scope를 추가합니다")
    IntfOpenApiKeyUpdateResult addScopeToApiKey(@RequestBody IntfOpenApiKeyUpdateRequest request);

    /**
     * [API-KEY-010] (ione portal solution) 포탈용 API 목록
     */
    @GetMapping("/admin/intf/v1/system/auth/portal/api.idt")
    @Operation(summary = "[API-KEY-010] 포탈용 API 목록", description = "포탈용 API 상세 정보와 전문 정보를 반환합니다")
    IntfPubApiResult getPortalApis();

    /**
     * [API-KEY-011] (ione portal solution) 파트너 및 그룹 API 요청 통계 조회
     */
    @GetMapping("/admin/intf/v1/system/auth/statistic.idt")
    @Operation(summary = "[API-KEY-011] 파트너 및 그룹 API 요청 통계 조회", description = "파트너 및 그룹의 API 요청 통계를 조회합니다")
    List<IntfRateLimitStatisticsVo> getStatistics(
            @Parameter(description = "조회 연도") @RequestParam("year") Integer year,
            @Parameter(description = "통계 조회 유형") @RequestParam("statisticType") String statisticType,
            @Parameter(description = "조회 월") @RequestParam(value = "month", required = false) Integer month,
            @Parameter(description = "조회 일") @RequestParam(value = "day", required = false) Integer day,
            @Parameter(description = "조회할 파트너/그룹 ID") @RequestParam(value = "statisticKey", required = false) String statisticKey
    );
}