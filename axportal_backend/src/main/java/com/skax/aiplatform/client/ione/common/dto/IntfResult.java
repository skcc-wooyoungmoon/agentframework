package com.skax.aiplatform.client.ione.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IONE API 공통 결과 DTO
 * 
 * <p>IONE API Gateway의 기본 응답 구조입니다.
 * Swagger 문서의 IntfResult 스키마를 기반으로 구현되었습니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "IONE API 공통 결과")
public class IntfResult {

    /**
     * 처리 결과 정보
     */
    @JsonProperty("result")
    @Schema(description = "처리 결과 정보")
    private IntfResultBody result;

    /**
     * 성공 여부 (쓰기 전용)
     */
    @JsonProperty("success")
    @Schema(description = "성공 여부", accessMode = Schema.AccessMode.WRITE_ONLY)
    private Boolean success;
}