package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 파일 업로드 요청 DTO
 * 
 * <p>
 * 데이터셋 파일 업로드를 위한 요청 파라미터를 매핑하는 DTO입니다.
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
@Schema(description = "데이터셋 파일 업로드 요청")
public class DataCtlgDatasetUploadReq {

    @Schema(description = "데이터셋 이름", example = "AI 학습 데이터셋", required = true)
    private String name;

    @Schema(description = "데이터셋 타입", example = "unsupervised_finetuning", 
            allowableValues = {"unsupervised_finetuning", "supervised_finetuning", "model_benchmark", "rag_evaluation", "custom", "dpo_finetuning"}, 
            required = true)
    private String type;

    @Schema(description = "데이터셋 상태", example = "processing")
    private String status;

    @Schema(description = "데이터셋 설명", example = "AI 모델 학습을 위한 데이터셋")
    private String description;

    @Schema(description = "데이터셋 태그", example = "tag1,tag2")
    private String tags;

    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", required = true)
    private String projectId;

    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    @Schema(description = "페이로드", example = "{}")
    private String payload;
}
