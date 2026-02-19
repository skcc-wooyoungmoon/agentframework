package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 커스텀 학습 데이터셋 생성 응답 DTO
 * 
 * <p>
 * 커스텀 학습 데이터셋 생성 결과를 반환하는 응답 DTO입니다.
 * </p>
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커스텀 학습 데이터셋 생성 응답")
public class DataCtlgCustomTrainingDataCreateRes {

    @JsonProperty("id")
    @Schema(description = "데이터셋 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @JsonProperty("name")
    @Schema(description = "데이터셋 이름", example = "AI 학습 데이터셋")
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터셋 타입", example = "unsupervised_finetuning")
    private String type;

    @JsonProperty("description")
    @Schema(description = "데이터셋 설명", example = "AI 모델 학습을 위한 데이터셋")
    private String description;

    @JsonProperty("tags")
    @Schema(description = "데이터셋 태그 목록")
    private List<DatasetTag> tags;

    @JsonProperty("status")
    @Schema(description = "데이터셋 상태", example = "processing")
    private String status;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-10-26T09:37:43.071Z")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정일시", example = "2025-10-26T09:37:43.071Z")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    @JsonProperty("datasource_id")
    @Schema(description = "데이터소스 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID datasourceId;

    @JsonProperty("datasource_files")
    @Schema(description = "데이터소스 파일 목록")
    private List<String> datasourceFiles;

    @JsonProperty("processor")
    @Schema(description = "프로세서 정보")
    private Object processor;

    @JsonProperty("file_path")
    @Schema(description = "파일 경로", example = "data/dataset/upload/...")
    private String filePath;
    
    /**
     * 생성 성공 여부
     */
    private Boolean success;
    
    /**
     * 생성 결과 메시지
     */
    private String message;
    
    /**
     * 생성 시간 (밀리초)
     */
    private Long creationTimeMs;
    
    /**
     * 에러 코드 (실패 시)
     */
    private String errorCode;
    
    /**
     * 에러 상세 정보 (실패 시)
     */
    private String errorDetails;
    
    /**
     * SKT AI API 호출 (파일 업로드) 결과
     */
    private StepResult uploadStep;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "데이터셋 태그")
    public static class DatasetTag {
        @JsonProperty("name")
        @Schema(description = "태그 이름", example = "tag1")
        private String name;
    }
}