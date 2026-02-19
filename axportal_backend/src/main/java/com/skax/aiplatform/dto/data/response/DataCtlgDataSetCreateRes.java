package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 데이터셋 생성 응답 DTO
 * 
 * <p>
 * Service에서 Controller로 반환하는 데이터셋 생성 결과 DTO입니다.
 * </p>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 생성 응답")
public class DataCtlgDataSetCreateRes {

    /**
     * 데이터셋 ID
     */
    @Schema(description = "데이터셋 ID", example = "7177122d-06fa-4c49-af94-3462f6433402")
    private UUID id;

    /**
     * 데이터셋 이름
     */
    @Schema(description = "데이터셋 이름", example = "AI 학습 데이터셋")
    private String name;

    /**
     * 데이터셋 타입
     */
    @Schema(description = "데이터셋 타입", example = "unsupervised_finetuning")
    private String type;

    /**
     * 데이터셋 설명
     */
    @Schema(description = "데이터셋 설명", example = "AI 모델 학습을 위한 데이터셋")
    private String description;

    /**
     * 데이터셋 태그 목록
     */
    @Schema(description = "데이터셋 태그 목록")
    private List<DataCtlgDatasetTagRes> tags;

    /**
     * 데이터셋 상태
     */
    @Schema(description = "데이터셋 상태", example = "processing")
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
     * 데이터소스 ID
     */
    @Schema(description = "데이터소스 ID", example = "c6f781b3-02cf-447b-a7b3-75d97b6e3f22")
    private UUID datasourceId;

    /**
     * 데이터소스 파일 목록
     */
    @Schema(description = "데이터소스 파일 목록")
    private List<String> datasourceFiles;

    /**
     * 프로세서 정보
     */
    @Schema(description = "프로세서 정보")
    private Object processor;

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

}