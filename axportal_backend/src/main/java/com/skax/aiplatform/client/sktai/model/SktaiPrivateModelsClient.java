package com.skax.aiplatform.client.sktai.model;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.model.dto.request.DecryptModelRequest;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelUsageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Private Models API FeignClient
 * 
 * <p>SKTAI Model 시스템의 Private Model 관리를 위한 Feign 클라이언트입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-private-models-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Private Models", description = "SKTAI Private Model 관리 API")
public interface SktaiPrivateModelsClient {

    /**
     * Private Model Usage 상태 조회
     */
    @GetMapping("/api/v1/private-models/{model_id}/usage/{usage_uuid_path}/status")
    @Operation(summary = "Private Model Usage 상태 조회", description = "프라이빗 모델 사용 상태를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object getPrivateModelUsageStatus(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "Usage UUID Path") @PathVariable("usage_uuid_path") String usageUuidPath
    );

    /**
     * Private Model Usage 기록 조회
     */
    @GetMapping("/api/v1/private-models/{model_id}/usage")
    @Operation(summary = "Private Model Usage 기록 조회", description = "특정 모델의 모든 사용 기록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용 기록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object getPrivateModelUsageRecords(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId
    );

    /**
     * Private Model 복호화
     */
    @PostMapping("/api/v1/private-models/{model_id}/decrypt")
    @Operation(summary = "Private Model 복호화", description = "프라이빗 모델을 지정된 경로로 복호화합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "복호화 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "요청 검증 실패")
    })
    Object decryptPrivateModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody DecryptModelRequest request
    );

    /**
     * Model 사용 시작
     */
    @PostMapping("/api/v1/private-models/{model_id}/usage/start")
    @Operation(summary = "Model 사용 시작", description = "프라이빗 모델 사용을 시작합니다 (참조 카운트 증가).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용 시작 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "요청 검증 실패")
    })
    Object startModelUsage(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelUsageRequest request
    );

    /**
     * Model 사용 중지
     */
    @PostMapping("/api/v1/private-models/{model_id}/usage/stop")
    @Operation(summary = "Model 사용 중지", description = "프라이빗 모델 사용을 중지합니다 (참조 카운트 감소 및 필요시 정리).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용 중지 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "요청 검증 실패")
    })
    Object stopModelUsage(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelUsageRequest request
    );

    /**
     * 오래된 Private Model 정리
     */
    @PostMapping("/api/v1/private-models/cleanup-old-private-models")
    @Operation(summary = "오래된 Private Model 정리", description = "사용한지 특정 시간이 지난 복호화 모델에 대한 파일 삭제를 실행합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정리 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    Object cleanupOldUsages();
}
