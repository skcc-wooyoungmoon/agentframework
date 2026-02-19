package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.skax.aiplatform.dto.data.response.DataCtlgProcParam;

/**
 * 데이터셋 생성 요청 DTO
 * 
 * <p>
 * Controller에서 Service로 전달하는 데이터셋 생성 요청 DTO입니다.
 * </p>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 생성 요청")
public class DataCtlgDataSetCreateReq {

    /**
     * 데이터셋 이름
     */
    @NotBlank(message = "데이터셋 이름은 필수입니다")
    @Schema(description = "데이터셋 이름", example = "AI 학습 데이터셋", required = true)
    private String name;

    /**
     * 데이터셋 타입
     */
    @Schema(description = "데이터셋 타입", allowableValues = { "unsupervised_finetuning", "supervised_finetuning",
            "model_benchmark",
            "rag_evaluation" }, example = "unsupervised_finetuning", defaultValue = "unsupervised_finetuning")
    private String type;

    /**
     * 데이터셋 설명
     */
    @Schema(description = "데이터셋 설명", example = "AI 모델 학습을 위한 데이터셋", defaultValue = "")
    private String description;

    /**
     * 데이터셋 태그 목록
     */
    @Schema(description = "데이터셋 태그 목록")
    private List<DataCtlgDataSetTag> tags;

    /**
     * 데이터셋 상태
     */
    @Schema(description = "데이터셋 상태", example = "processing", defaultValue = "processing")
    private String status;

    /**
     * 프로젝트 ID
     */
    @NotBlank(message = "프로젝트 ID는 필수입니다")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", required = true)
    private String projectId;

    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "false", defaultValue = "false")
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * 데이터소스 ID
     */
    @NotNull(message = "데이터소스 ID는 필수입니다")
    @Schema(description = "데이터소스 ID", example = "c6f781b3-02cf-447b-a7b3-75d97b6e3f22", required = true)
    private UUID datasourceId;

    /**
     * 프로세서 파라미터
     */
    @Schema(description = "프로세서 파라미터")
    private DataCtlgProcParam processor;

    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "admin", maxLength = 255)
    private String createdBy;

    /**
     * 수정자
     */
    @Schema(description = "수정자", example = "admin", maxLength = 255)
    private String updatedBy;

    /**
     * 정책 설정
     */
    @Schema(description = "정책 설정")
    private Object policy;

}