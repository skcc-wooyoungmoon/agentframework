package com.skax.aiplatform.dto.data.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 생성 요청")
public class DataCtlgDataSourceCreateReq {

    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private UUID projectId;

    @Schema(description = "데이터소스 이름", example = "11_datasource_73ffc86a", required = true)
    private String name;

    @Schema(description = "데이터소스 타입", example = "file")
    private String type;

    @Schema(description = "생성자", example = "")
    private String createdBy;

    @Schema(description = "수정자", example = "")
    private String updatedBy;

    @Schema(description = "데이터소스 설명", example = "")
    private String description;

    @Schema(description = "삭제 여부", example = "false")
    @Builder.Default
    private Boolean isDeleted = false;

    @Schema(description = "S3 설정")
    private DataCtlgDataSourceS3Config s3Config;

    @Schema(description = "임시 파일 목록")
    private List<TempFileDto> tempFiles;

    @Schema(description = "정책 목록")
    private List<Object> policy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "임시 파일 정보")
    public static class TempFileDto {

        @Schema(description = "파일 이름", example = "테스트_unsupervised.xlsx", required = true)
        private String fileName;

        @Schema(description = "임시 파일 경로", example = "/cephfs/data/datasource/temp/9afa8bda-b60_20251019171533591813.xlsx", required = true)
        private String tempFilePath;

        @Schema(description = "파일 메타데이터")
        private Object fileMetadata;

        @Schema(description = "지식 설정")
        private Object knowledgeConfig;
    }
}
