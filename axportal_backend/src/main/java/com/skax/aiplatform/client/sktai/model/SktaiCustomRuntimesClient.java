package com.skax.aiplatform.client.sktai.model;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelCustomRuntimeCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelCustomRuntimeUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelCustomRuntimeRead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Custom Runtimes API FeignClient
 * 
 * <p>SKTAI Model 시스템의 Custom Runtime 관리를 위한 Feign 클라이언트입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-custom-runtimes-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Custom Runtimes", description = "SKTAI Custom Runtime 관리 API")
public interface SktaiCustomRuntimesClient {

    /**
     * Custom Runtime 생성
     */
    @PostMapping("/api/v1/custom-runtimes")
    @Operation(summary = "Custom Runtime 생성", description = "새로운 커스텀 런타임 구성을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Custom Runtime 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelCustomRuntimeRead createCustomRuntime(@RequestBody ModelCustomRuntimeCreate request);

    /**
     * Model별 Custom Runtime 조회
     */
    @GetMapping("/api/v1/custom-runtimes/model/{model_id}")
    @Operation(summary = "Model별 Custom Runtime 조회", description = "모델 ID로 커스텀 런타임 구성을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Custom Runtime 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Custom Runtime을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelCustomRuntimeRead getCustomRuntimeByModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId
    );

    /**
     * Model별 Custom Runtime 수정
     */
    @PutMapping("/api/v1/custom-runtimes/model/{model_id}")
    @Operation(summary = "Model별 Custom Runtime 수정", description = "모델 ID로 기존 커스텀 런타임 구성을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Custom Runtime 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Custom Runtime을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelCustomRuntimeRead updateCustomRuntimeByModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelCustomRuntimeUpdate request
    );

    /**
     * Model별 Custom Runtime 삭제
     */
    @DeleteMapping("/api/v1/custom-runtimes/model/{model_id}")
    @Operation(summary = "Model별 Custom Runtime 삭제", description = "모델 ID로 커스텀 런타임 구성을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Custom Runtime 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Custom Runtime을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    void deleteCustomRuntimeByModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId
    );

    /**
     * Custom Code 파일 업로드
     */
    @PostMapping(value = "/api/v1/custom-runtimes/code/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Custom Code 파일 업로드", description = "커스텀 코드 파일(zip, tar 형식)을 업로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파일 검증 실패")
    })
    Object uploadCustomCodeFile(
            @Parameter(description = "커스텀 코드 파일") @RequestPart("file") MultipartFile file
    );
}
