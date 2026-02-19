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
 * 데이터셋 수정 응답 DTO
 * 
 * <p>Service에서 Controller로 전달하는 데이터셋 수정 응답 DTO입니다.
 * 
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 수정 응답")
public class DataCtlgDataSetUpdateRes {
    
    /**
     * 데이터셋 ID
     */
    @Schema(description = "데이터셋 ID", example = "e3b4e453-293e-4a4b-867a-cdd36e212180")
    private UUID id;
    
    /**
     * 데이터셋 이름
     */
    @Schema(description = "데이터셋 이름", example = "테스트 5")
    private String name;
    
    /**
     * 데이터셋 타입
     */
    @Schema(description = "데이터셋 타입", 
            // allowableValues = {"unsupervised_finetuning", "supervised_finetuning", "model_benchmark", "rag_evaluation"},
            example = "unsupervised_finetuning")
    private String type;
    
    /**
     * 데이터셋 설명
     */
    @Schema(description = "데이터셋 설명", example = "")
    private String description;
    
    /**
     * 데이터셋 태그 목록
     */
    @Schema(description = "데이터셋 태그 목록")
    private List<DataCtlgDatasetTagRes> tags;
    
    /**
     * 데이터셋 상태
     */
    @Schema(description = "데이터셋 상태", 
            example = "completed")
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
    @Schema(description = "데이터소스 ID", example = "31b12527-439e-4850-8544-395e8873ea2c")
    private UUID datasourceId;
    
    /**
     * 데이터소스 파일 목록
     */
    @Schema(description = "데이터소스 파일 목록", 
            example = "[\"private/default/data/datasource/repo/datasource-31b12527-439e-4850-8544-395e8873ea2c/d7d27c41-dc5_20250901133435223300_b55b2202.xlsx\"]")
    private List<String> datasourceFiles;
    
    /**
     * 파일 경로
     */
    @Schema(description = "파일 경로", example = "data/dataset/upload/e3b4e453-293e-4a4b-867a-cdd36e212180/aa3f7fa5-53b9-49b6-857d-e467c4010f20.json")
    private String filePath;
    
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
     * 생성일시
     */
    @Schema(description = "생성일시", example = "2025-09-01T13:34:39.943912")
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    @Schema(description = "수정일시", example = "2025-09-04T21:37:38.629186")
    private LocalDateTime updatedAt;
    
    /**
     * 프로세서 설정
     */
    @Schema(description = "프로세서 설정")
    private DataCtlgProcParam processor;
    
   
}