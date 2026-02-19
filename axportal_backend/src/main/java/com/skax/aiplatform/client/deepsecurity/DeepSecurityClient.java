package com.skax.aiplatform.client.deepsecurity;

import com.skax.aiplatform.client.deepsecurity.config.DeepSecurityFeignConfig;
import com.skax.aiplatform.client.deepsecurity.dto.request.ScanRequest;
import com.skax.aiplatform.client.deepsecurity.dto.response.ScanResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * DeepSecurity API FeignClient
 * 
 * <p>DeepSecurity 시스템의 Model 관리를 위한 Feign 클라이언트입니다.
 * AI 모델의 보안 검증 및 등록을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>모델 등록</strong>: 새로운 AI 모델을 DeepSecurity 시스템에 등록</li>
 *   <li><strong>보안 검증</strong>: 모델의 보안성을 검증하고 승인</li>
 * </ul>
 *
 * @author system
 * @since 2025-01-15
 * @version 1.0
 */
@FeignClient(
    name = "deepsecurity-client",
    url = "${deepsecurity.api.base-url}",
    configuration = DeepSecurityFeignConfig.class
)
@Tag(name = "DeepSecurity", description = "DeepSecurity Model 관리 API")
public interface DeepSecurityClient {

    /**
     * DeepSecurity Model 등록
     * 
     * <p>새로운 AI 모델을 DeepSecurity 시스템에 등록하고 보안 검증을 수행합니다.</p>
     * 
     * @param request DeepSecurity Model 등록 요청 정보
     * @return 등록 결과 및 모델 정보
     */
    @PostMapping("/api/v1/models/deepsecurity")
    @Operation(summary = "DeepSecurity Model 등록", description = "새로운 AI 모델을 DeepSecurity 시스템에 등록하고 보안 검증을 수행합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model 등록 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ScanResponse requestDeepSecurity(
        @Parameter(description = "DeepSecurity 요청", required = true)
        @RequestBody ScanRequest request
    );
}
