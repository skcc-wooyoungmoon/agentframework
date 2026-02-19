package com.skax.aiplatform.dto.data.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 상세 조회 응답 DTO
 * 
 * <p>
 * Service에서 Controller로 반환하는 데이터셋 상세 정보 DTO입니다.
 * </p>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 상세 조회 응답")

public class DataCtlgDataSetByIdRes {
    /**
     * 데이터셋 ID
     */
    @Schema(description = "데이터셋 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    /**
     * 데이터셋 이름
     */
    @Schema(description = "데이터셋 이름", example = "고객 데이터셋")
    private String name;

    /**
     * 데이터셋 타입
     */
    @Schema(description = "데이터셋 타입", allowableValues = { "unsupervised_finetuning", "supervised_finetuning",
            "model_benchmark", "rag_evaluation" }, example = "unsupervised_finetuning")
    private String type;

    /**
     * 데이터셋 설명
     */
    @Schema(description = "데이터셋 설명", example = "고객 정보 및 구매 이력 데이터")
    private String description;

    /**
     * 데이터셋 태그 목록
     */
    @Schema(description = "데이터셋 태그 목록")
    private List<DataCtlgDatasetTagRes> tags;

    /**
     * 데이터셋 상태
     */
    @Schema(description = "데이터셋 상태", example = "completed")
    private String status;

    /**
     * 프로젝트 ID
     */
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;

    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    /**
     * 파일 경로
     */
    @Schema(description = "파일 경로", example = "/data/datasets/customer_data.csv")
    private String filePath;

    /**
     * 생성일시
     */
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    /**
     * 수정자
     */
    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    /**
     * 데이터소스 ID
     */
    @Schema(description = "데이터소스 ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private String datasourceId;

    /**
     * 소스 파일 이름
     */
    @Schema(description = "소스 파일 이름", example = "테스트_cusotm.zip")
    private String sourceFileName;

    /**
     * 공개 여부
     */
    @Schema(description = "공개 여부", example = "전체공유")
    private String publicStatus;

    /**
     * 최초 project seq
     */

    @Schema(description = "최초 project seq")
    private Integer fstPrjSeq;
    /**
     * 최종 project seq
     */
    @Schema(description = "최종 project seq")
    private Integer lstPrjSeq;

}
