package com.skax.aiplatform.client.sktai.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 데이터셋 정보 DTO (Official API Spec Compliant)
 * 
 * <p>
 * SKTAI Data API에서 제공하는 데이터셋 정보를 나타내는 응답 DTO입니다.
 * 공식 OpenAPI 스펙(https://aip-stg.sktai.io/api/v1/data/openapi.json)에 따라 구현되었습니다.
 * </p>
 * 
 * <h3>주요 필드:</h3>
 * <ul>
 * <li><strong>id</strong>: 데이터셋 고유 식별자 (UUID)</li>
 * <li><strong>name</strong>: 데이터셋 이름 (필수)</li>
 * <li><strong>type</strong>: 데이터셋 타입 (DatasetTypeEnum)</li>
 * <li><strong>status</strong>: 데이터셋 상태 (필수)</li>
 * <li><strong>project_id</strong>: 프로젝트 ID</li>
 * <li><strong>datasource_id</strong>: 데이터소스 ID (필수)</li>
 * <li><strong>datasource_files</strong>: 데이터소스 파일 목록 (필수)</li>
 * <li><strong>processor</strong>: 프로세서 정보 (필수)</li>
 * <li><strong>file_path</strong>: 파일 경로 (필수)</li>
 * </ul>
 * 
 * <h3>OpenAPI 스펙 준수사항:</h3>
 * <ul>
 * <li>모든 필드명은 snake_case로 매핑</li>
 * <li>필수 필드: name, status, created_by, updated_by, datasource_id,
 * datasource_files, processor, file_path</li>
 * <li>nullable 필드: description, tags</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 * @see <a href="https://aip-stg.sktai.io/api/v1/data/openapi.json">SKTAI Data
 *      API OpenAPI Spec</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 정보")
public class Dataset {

    @JsonProperty("id")
    @Schema(description = "데이터셋 ID")
    private UUID id;

    @JsonProperty("name")
    @Schema(description = "데이터셋 이름")
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터셋 타입")
    private String type;

    @JsonProperty("description")
    @Schema(description = "데이터셋 설명")
    private String description;

    @JsonProperty("tags")
    @Schema(description = "데이터셋 태그 목록")
    private List<DatasetTag> tags;

    @JsonProperty("status")
    @Schema(description = "데이터셋 상태", required = true)
    private String status;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", defaultValue = "")
    private String projectId;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", defaultValue = "false")
    private Boolean isDeleted;

    @JsonProperty("datasource_id")
    @Schema(description = "데이터소스 ID", required = true, format = "uuid")
    private UUID datasourceId;

    @JsonProperty("datasource_files")
    @Schema(description = "데이터소스 파일 목록", required = true)
    private List<String> datasourceFiles;

    @JsonProperty("processor")
    @Schema(description = "프로세서 정보", required = true)
    private Object processor;

    @JsonProperty("file_path")
    @Schema(description = "파일 경로", required = true)
    private String filePath;

    @JsonProperty("created_at")
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자", required = true)
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자", required = true)
    private String updatedBy;

    @JsonProperty("policy")
    @Schema(description = "정책 설정")
    private Object policy;
}
