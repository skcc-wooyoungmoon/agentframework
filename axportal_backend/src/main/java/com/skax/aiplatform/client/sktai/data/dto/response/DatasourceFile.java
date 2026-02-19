package com.skax.aiplatform.client.sktai.data.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 데이터소스 파일 정보 DTO
 * 
 * <p>데이터소스에 속한 개별 파일의 상세 정보를 포함하는 DTO입니다.
 * 파일의 메타데이터, 생성/수정 정보, S3 정보 등을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>파일 식별자</strong>: 고유 ID, 데이터소스 ID</li>
 *   <li><strong>파일 정보</strong>: 이름, 경로, 크기</li>
 *   <li><strong>상태 정보</strong>: 삭제 여부</li>
 *   <li><strong>생성/수정 정보</strong>: 생성/수정 시간, 담당자</li>
 *   <li><strong>S3 정보</strong>: ETag, 메타데이터</li>
 *   <li><strong>설정 정보</strong>: 지식 설정</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-23
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 데이터소스 파일 정보",
    example = """
        {
          "id": "ac338846-e725-4930-a722-c4c36ad24dc4",
          "datasource_id": "454c67ff-99f6-4295-8cfb-810be4345467",
          "file_name": "테스트_unsupervised.xlsx",
          "file_path": "private/default/data/datasource/repo/datasource-454c67ff-99f6-4295-8cfb-810be4345467/9afa8bda-b60_20251019171533591813_86605e78.xlsx",
          "file_size": 10891,
          "is_deleted": false,
          "created_at": "2025-10-19T17:15:42.072812",
          "updated_at": "2025-10-19T17:15:42.073069",
          "created_by": "admin",
          "updated_by": "admin",
          "s3_etag": null,
          "file_metadata": null,
          "knowledge_config": null
        }
        """
)
public class DatasourceFile {
    
    /**
     * 파일 고유 식별자
     * 
     * <p>데이터베이스에서 파일을 식별하는 고유 UUID입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "파일 고유 식별자",
        example = "ac338846-e725-4930-a722-c4c36ad24dc4"
    )
    private String id;
    
    /**
     * 데이터소스 식별자
     * 
     * <p>이 파일이 속한 데이터소스의 고유 UUID입니다.</p>
     */
    @JsonProperty("datasource_id")
    @Schema(
        description = "데이터소스 식별자",
        example = "454c67ff-99f6-4295-8cfb-810be4345467"
    )
    private String datasourceId;
    
    /**
     * 파일명
     * 
     * <p>사용자가 업로드한 원본 파일명입니다.</p>
     */
    @JsonProperty("file_name")
    @Schema(
        description = "파일명",
        example = "테스트_unsupervised.xlsx"
    )
    private String fileName;
    
    /**
     * 파일 경로
     * 
     * <p>S3 또는 파일 시스템에서의 실제 파일 경로입니다.</p>
     */
    @JsonProperty("file_path")
    @Schema(
        description = "파일 경로",
        example = "private/default/data/datasource/repo/datasource-454c67ff-99f6-4295-8cfb-810be4345467/9afa8bda-b60_20251019171533591813_86605e78.xlsx"
    )
    private String filePath;
    
    /**
     * 파일 크기 (바이트)
     * 
     * <p>파일의 크기를 바이트 단위로 나타냅니다.</p>
     */
    @JsonProperty("file_size")
    @Schema(
        description = "파일 크기 (바이트)",
        example = "10891",
        minimum = "0"
    )
    private Long fileSize;
    
    /**
     * 삭제 여부
     * 
     * <p>파일이 논리적으로 삭제되었는지 여부를 나타냅니다.</p>
     */
    @JsonProperty("is_deleted")
    @Schema(
        description = "삭제 여부",
        example = "false"
    )
    private Boolean isDeleted;
    
    /**
     * 생성 시간
     * 
     * <p>파일이 업로드된 시간입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "생성 시간",
        example = "2025-10-19T17:15:42.072812"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     * 
     * <p>파일 정보가 마지막으로 수정된 시간입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "수정 시간",
        example = "2025-10-19T17:15:42.073069"
    )
    private LocalDateTime updatedAt;
    
    /**
     * 생성자
     * 
     * <p>파일을 업로드한 사용자의 식별자입니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(
        description = "생성자",
        example = "admin"
    )
    private String createdBy;
    
    /**
     * 수정자
     * 
     * <p>파일 정보를 마지막으로 수정한 사용자의 식별자입니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
        description = "수정자",
        example = "admin"
    )
    private String updatedBy;
    
    /**
     * S3 ETag
     * 
     * <p>S3에 저장된 파일의 ETag 값입니다. 파일 무결성 검증에 사용됩니다.</p>
     */
    @JsonProperty("s3_etag")
    @Schema(
        description = "S3 ETag",
        example = "null"
    )
    private String s3Etag;
    
    /**
     * 파일 메타데이터
     * 
     * <p>파일의 추가 메타데이터 정보입니다.</p>
     */
    @JsonProperty("file_metadata")
    @Schema(
        description = "파일 메타데이터",
        example = "null"
    )
    private Object fileMetadata;
    
    /**
     * 지식 설정
     * 
     * <p>파일과 관련된 지식 설정 정보입니다.</p>
     */
    @JsonProperty("knowledge_config")
    @Schema(
        description = "지식 설정",
        example = "null"
    )
    private Object knowledgeConfig;
}