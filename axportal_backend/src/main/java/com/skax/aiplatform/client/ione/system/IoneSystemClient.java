package com.skax.aiplatform.client.ione.system;

import com.skax.aiplatform.client.ione.system.dto.request.ApiListSearchData;
import com.skax.aiplatform.client.ione.system.dto.response.ApiInfoResult;
import com.skax.aiplatform.client.ione.system.dto.response.ApiListResultWithPagination;
import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.config.IoneFeignConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * iONE System API 클라이언트
 * 
 * <p>iONE 시스템 관리 API와의 통신을 담당하는 Feign Client입니다.
 * API 목록 조회 및 상세 정보 조회 기능을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@FeignClient(
    name = "ione-system-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneSystemClient {

    /**
     * API 목록 조회
     * 
     * @param request API 목록 검색 조건
     * @return API 목록 결과
     */
    @PostMapping("/admin/intf/v1/system/api/list.idt")
    @Operation(summary = "API 목록 조회", description = "API의 목록을 조회합니다.")
    ApiListResultWithPagination getApiList(@RequestBody ApiListSearchData request);

    /**
     * API 정보 조회
     * 
     * @param apiId 조회할 API ID
     * @return API 상세 정보
     */
    @GetMapping("/admin/intf/v1/system/api/{apiId}/get.idt")
    @Operation(summary = "API 정보 조회", description = "API ID로 해당 API에 대한 상세 정보를 조회합니다.")
    InfResponseBody<ApiInfoResult> getApiInfo(@Parameter(description = "조회할 API ID") @PathVariable("apiId") String apiId);
}