package com.skax.aiplatform.dto.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 배포 자원 현황 정보 DTO
 * 
 * <p>
 * 모델 배포(Serving)의 자원 사용 현황을 담는 응답 데이터 구조입니다.
 * CPU, Memory, GPU의 사용량, 사용률, 요청량 정보를 제공합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @since 2025-01-08
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모델 배포 자원 현황 정보")
public class ModelDeployResourceInfo {

    @Schema(description = "세션 ID", example = "c182a9d3-147e-4ee1-a465-c3646d7a1758")
    private String sessionId;

    @Schema(description = "모델명", example = "DeployModelTest1")
    private String modelName;

    @Schema(description = "서빙 ID", example = "a83e9259-d642-4158-ac4d-06ffc5095017")
    private String servingId;

    @Schema(description = "상태", example = "RUNNING")
    private String status;

    @Schema(description = "프로젝트 ID", example = "proj-12345")
    private String projectId;

    @Schema(description = "프로젝트명", example = "AI 추론 프로젝트")
    private String projectName;

    @Schema(description = "레플리카 수", example = "1")
    private Integer replicas;

    // CPU 자원 (Core 단위)
    @Schema(description = "CPU 사용량 (Core)", example = "1.5")
    private Double cpuUsage;

    @Schema(description = "CPU 사용률 (%)", example = "75.0")
    private Double cpuUtilization;

    @Schema(description = "CPU 요청량 (Core)", example = "2.0")
    private Double cpuRequest;

    // Memory 자원 (GiB 단위)
    @Schema(description = "Memory 사용량 (GiB)", example = "3.2")
    private Double memoryUsage;

    @Schema(description = "Memory 사용률 (%)", example = "80.0")
    private Double memoryUtilization;

    @Schema(description = "Memory 요청량 (GiB)", example = "4.0")
    private Double memoryRequest;

    // GPU 자원
    @Schema(description = "GPU 사용량", example = "0.8")
    private Double gpuUsage;

    @Schema(description = "GPU 사용률 (%)", example = "80.0")
    private Double gpuUtilization;

    @Schema(description = "GPU 요청량 (개수)", example = "1.0")
    private Double gpuRequest;
}

