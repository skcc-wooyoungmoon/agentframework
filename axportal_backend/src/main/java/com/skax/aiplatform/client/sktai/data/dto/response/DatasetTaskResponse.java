package com.skax.aiplatform.client.sktai.data.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋의 Task 정보를 담는 응답 DTO입니다.
 * 
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 Task 응답")
public class DatasetTaskResponse {

    @JsonProperty("dataset_id")
    @Schema(description = "데이터셋 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID datasetId;

    @JsonProperty("status")
    @Schema(description = "Task 상태", example = "failed")
    private String status;

    @JsonProperty("error_message")
    @Schema(description = "에러메시지")
    private String errorMessage;

    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

}