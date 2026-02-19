package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추론 성능 조회 요청 DTO
 * 
 * <p>
 * 모델 배포의 추론 성능(Time To First Token, Time Per Output Token)을 조회하기 위한 요청 데이터 구조입니다.
 * 사용자가 설정한 시작/종료일자를 기준으로 프로메테우스에서 데이터를 조회합니다.
 * </p>
 * 
 * @author AXPortal Team
 * @since 2025-01-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "추론 성능 조회 요청 정보")
public class GetInferencePerformanceReq {

    /**
     * 서빙 ID
     */
    @Schema(description = "서빙 ID", example = "test-model-name", requiredMode = RequiredMode.NOT_REQUIRED)
    private String servingId;

    /**
     * 모델 이름
     */
    @Schema(description = "모델 이름", example = "modelName", requiredMode = RequiredMode.NOT_REQUIRED)
    private String modelName;

    /**
     * 조회 시작일시
     */
    @NotBlank(message = "조회 시작일시는 필수입니다.")
    @Schema(description = "조회 시작일시 (ISO 형식, 타임존 포함 가능)", example = "2025-01-27T00:00:00 또는 2025-01-27T00:00:00+09:00", requiredMode = RequiredMode.REQUIRED)
    private String startDate;

    /**
     * 조회 종료일시
     */
    @NotBlank(message = "조회 종료일시는 필수입니다.")
    @Schema(description = "조회 종료일시 (ISO 형식, 타임존 포함 가능)", example = "2025-01-27T23:59:59 또는 2025-01-27T23:59:59+09:00", requiredMode = RequiredMode.REQUIRED)
    private String endDate;
}

