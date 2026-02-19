package com.skax.aiplatform.client.sktai.model;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.model.dto.response.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SKTAI Health API FeignClient
 * 
 * <p>SKTAI Model 시스템의 Health Check를 위한 Feign 클라이언트입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-health-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Health", description = "SKTAI Health Check API")
public interface SktaiHealthClient {

    /**
     * Liveness Check
     */
    @GetMapping("/api/v1/models/health/live")
    @Operation(summary = "Liveness Check", description = "k8s livenessProbe를 위한 체크입니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "서비스가 정상적으로 동작 중"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    HealthResponse livenessCheck();

    /**
     * Readiness Check
     */
    @GetMapping("/api/v1/models/health/ready")
    @Operation(summary = "Readiness Check", description = "k8s readinessProbe를 위한 체크입니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "서비스가 요청을 받을 준비가 됨"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    HealthResponse readinessCheck();
}
