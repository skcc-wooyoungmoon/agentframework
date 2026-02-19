package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Shared Agent Backend 응답 DTO
 *
 * <p>Shared Agent Backend의 상세 정보를 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-09-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Shared Agent Backend 정보",
    example = """
        {
          "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
          "shared_agent_backend_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
          "status": "Ready"
        }
        """
)
public class SharedAgentBackendRead {

    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    /**
     * 네임스페이스
     */
    @JsonProperty("namespace")
    @Schema(description = "네임스페이스")
    private String namespace;

    /**
     * Shared Agent Backend ID
     */
    @JsonProperty("shared_agent_backend_id")
    @Schema(description = "Shared Agent Backend ID", format = "uuid")
    private String sharedAgentBackendId;

    /**
     * Shared Agent Backend 이미지
     */
    @JsonProperty("shared_agent_backend_image")
    @Schema(description = "Shared Agent Backend 이미지")
    private String sharedAgentBackendImage;

    /**
     * Shared Agent Backend 최신 이미지
     */
    @JsonProperty("shared_agent_backend_image_latest")
    @Schema(description = "Shared Agent Backend 최신 이미지")
    private String sharedAgentBackendImageLatest;

    /**
     * ISVC 이름
     */
    @JsonProperty("isvc_name")
    @Schema(description = "ISVC 이름")
    private String isvcName;

    /**
     * 상태
     */
    @JsonProperty("status")
    @Schema(description = "백엔드 상태")
    private String status;

    /**
     * CPU 요청량
     */
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량")
    private Integer cpuRequest;

    /**
     * CPU 제한량
     */
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량")
    private Integer cpuLimit;

    /**
     * 메모리 요청량
     */
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량")
    private Integer memRequest;

    /**
     * 메모리 제한량
     */
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량")
    private Integer memLimit;

    /**
     * 최소 복제본 수
     */
    @JsonProperty("min_replicas")
    @Schema(description = "최소 복제본 수")
    private Integer minReplicas;

    /**
     * 최대 복제본 수
     */
    @JsonProperty("max_replicas")
    @Schema(description = "최대 복제본 수")
    private Integer maxReplicas;

    /**
     * 자동 스케일링 클래스
     */
    @JsonProperty("autoscaling_class")
    @Schema(description = "자동 스케일링 클래스")
    private String autoscalingClass;

    /**
     * 자동 스케일링 메트릭
     */
    @JsonProperty("autoscaling_metric")
    @Schema(description = "자동 스케일링 메트릭")
    private String autoscalingMetric;

    /**
     * 타겟 값
     */
    @JsonProperty("target")
    @Schema(description = "자동 스케일링 타겟 값")
    private Integer target;

    /**
     * 삭제 여부
     */
    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부")
    private Boolean isDeleted;

    /**
     * 생성자
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    /**
     * 수정자
     */
    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;

    /**
     * 생성 시간
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시간", format = "date-time")
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간", format = "date-time")
    private LocalDateTime updatedAt;

    /**
     * 에러 메시지
     */
    @JsonProperty("error_message")
    @Schema(description = "에러 메시지")
    private String errorMessage;
}
