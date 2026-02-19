package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 상세 정보")
public class DatasourceDetail {
    
    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;
    
    /**
     * 데이터소스 이름
     */
    @JsonProperty("name")
    @Schema(description = "데이터소스 이름", example = "Summary 2nd_datasource_ca48bc60")
    private String name;
    
    /**
     * 데이터소스 타입
     */
    @JsonProperty("type")
    @Schema(description = "데이터소스 타입", example = "file")
    private String type;
    
    /**
     * 생성자
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "admin")
    private String createdBy;
    
    /**
     * 수정자
     */
    @JsonProperty("updated_by")
    @Schema(description = "수정자", example = "admin")
    private String updatedBy;
    
    /**
     * 데이터소스 설명
     */
    @JsonProperty("description")
    @Schema(description = "데이터소스 설명", example = "")
    private String description;
    
    /**
     * S3 설정
     */
    @JsonProperty("s3_config")
    @Schema(description = "S3 설정")
    private Object s3Config;
    
    /**
     * 삭제 여부
     */
    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;
    
    /**
     * 스코프
     */
    @JsonProperty("scope")
    @Schema(description = "스코프", example = "public")
    private String scope;
    
    /**
     * 데이터소스 ID
     */
    @JsonProperty("id")
    @Schema(description = "데이터소스 ID", example = "c6f781b3-02cf-447b-a7b3-75d97b6e3f22")
    private UUID id;
    
    /**
     * 생성일시
     */
    @JsonProperty("created_at")
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
    
    /**
     * 데이터소스 상태
     */
    @JsonProperty("status")
    @Schema(description = "데이터소스 상태", example = "enabled")
    private String status;
    
    /**
     * 버킷 이름
     */
    @JsonProperty("bucket_name")
    @Schema(description = "버킷 이름", example = "data/datasource/repo/datasource-c6f781b3-02cf-447b-a7b3-75d97b6e3f22")
    private String bucketName;
    
    /**
     * 파일 목록
     */
    @JsonProperty("files")
    @Schema(description = "파일 목록")
    private List<DatasourceFile> files;
}
