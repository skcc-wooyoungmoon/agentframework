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
 * 데이터소스 상세 조회 응답 DTO
 * 
 * <p>Service에서 Controller로 반환하는 데이터소스 상세 정보 DTO입니다.</p>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 상세 조회 응답")
public class DataCtlgDataSourceByIdRes {
    
    /**
     * 프로젝트 ID
     */
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;
    
    /**
     * 데이터소스 이름
     */
    @Schema(description = "데이터소스 이름", example = "5_datasource_378e9c53")
    private String name;
    
    /**
     * 데이터소스 타입
     */
    @Schema(description = "데이터소스 타입", example = "file")
    private String type;
    
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
     * 데이터소스 설명
     */
    @Schema(description = "데이터소스 설명", example = "")
    private String description;
    
    /**
     * S3 설정
     */
    @Schema(description = "S3 설정")
    private Object s3Config;
    
    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;
    
    /**
     * 스코프
     */
    @Schema(description = "스코프", example = "public")
    private String scope;
    
    /**
     * 데이터소스 ID
     */
    @Schema(description = "데이터소스 ID", example = "14fba495-2970-46e3-933c-11fd08439e8d")
    private UUID id;
    
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
     * 데이터소스 상태
     */
    @Schema(description = "데이터소스 상태", example = "enabled")
    private String status;
    
    /**
     * 버킷 이름
     */
    @Schema(description = "버킷 이름", example = "data/datasource/repo/datasource-14fba495-2970-46e3-933c-11fd08439e8d")
    private String bucketName;
    
    /**
     * 파일 목록
     */
    @Schema(description = "파일 목록")
    private List<DataCtlgDataSourceFileRes> files;
    
}