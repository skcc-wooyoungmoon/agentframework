package com.skax.aiplatform.client.sktai.externalKnowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External Knowledge Repository 상세 정보 응답 DTO
 * 
 * <p>
 * ADXP API의 RepoExtInfo 스키마에 대응하는 응답 객체입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "External Knowledge Repository 상세 정보")
public class ExternalRepoInfo {

    @JsonProperty("name")
    @Schema(description = "Repository 이름", example = "external knowledge name_1")
    private String name;

    @JsonProperty("description")
    @Schema(description = "Repository 설명", example = "external knowledge desc")
    private String description;

    @JsonProperty("embedding_model_id")
    @Schema(description = "임베딩 모델 ID (UUID)", example = "5ece4bda-0e9d-4bb7-be29-fa764b383088")
    private String embeddingModelId;

    @JsonProperty("vector_db_id")
    @Schema(description = "Vector DB ID (UUID)", example = "78c05a11-cdbe-43a4-887e-66fc3b6d1d16")
    private String vectorDbId;

    @JsonProperty("index_name")
    @Schema(description = "인덱스 이름", example = "corus-rb086ea76-1451-4d4f-bec3-e8978645134d")
    private String indexName;

    @JsonProperty("script")
    @Schema(description = "검색 스크립트")
    private String script;

    @JsonProperty("id")
    @Schema(description = "Repository ID (UUID)", example = "c6cfcb16-70a8-418c-8815-12c90a7cd37f")
    private String id;

    @JsonProperty("created_at")
    @Schema(description = "생성 일시", example = "2025-10-10T11:45:45.595997Z")
    private String createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 일시", example = "2025-10-10T11:45:45.596012Z")
    private String updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
    private String updatedBy;

    @JsonProperty("is_active")
    @Schema(description = "활성화 상태", example = "false")
    private Boolean isActive;

    @JsonProperty("detail")
    @Schema(description = "상세 정보 또는 에러 메시지", example = "embedding request failed: ...")
    private String detail;

    @JsonProperty("vector_db_type")
    @Schema(description = "Vector DB 타입", example = "Milvus")
    private String vectorDbType;

    @JsonProperty("vector_db_name")
    @Schema(description = "Vector DB 이름", example = "milvus-db")
    private String vectorDbName;

    @JsonProperty("embedding_model_name")
    @Schema(description = "임베딩 모델 이름", example = "GIP/text-embedding-3-large-new")
    private String embeddingModelName;

    // DB 정보 (Frontend 삭제용 - Backend에서 추가)
    @JsonProperty("knw_id")
    @Schema(description = "지식 UUID (DB PK)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String knwId;

    @JsonProperty("rag_chunk_index_nm")
    @Schema(description = "RAG chunk index명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
    private String ragChunkIndexNm;

    @JsonProperty("is_custom_knowledge")
    @Schema(description = "사용자 정의 지식 여부 (DB에 없으면 true)", example = "false")
    private Boolean isCustomKnowledge;

    // 추가 DB 정보
    @JsonProperty("chunk_id")
    @Schema(description = "청킹 ID")
    private String chunkId;

    @JsonProperty("chunk_nm")
    @Schema(description = "청킹명")
    private String chunkNm;

    @JsonProperty("model_id")
    @Schema(description = "모델 ID")
    private String modelId;

    @JsonProperty("data_set_long_id")
    @Schema(description = "데이터셋 ID")
    private String dataSetId;

    @JsonProperty("data_set_nm")
    @Schema(description = "데이터셋명")
    private String dataSetNm;

    @JsonProperty("consumer_grp_nm")
    @Schema(description = "컨슈머 그룹명")
    private String consumerGrpNm;

    @JsonProperty("file_load_progress")
    @Schema(description = "파일 로드 진행률")
    private java.math.BigDecimal fileLoadProgress;

    @JsonProperty("chunk_progress")
    @Schema(description = "청킹 진행률")
    private java.math.BigDecimal chunkProgress;

    @JsonProperty("db_load_progress")
    @Schema(description = "DB 로드 진행률")
    private java.math.BigDecimal dbLoadProgress;

    @JsonProperty("dvlp_sync_yn")
    @Schema(description = "개발 동기화 여부")
    private java.math.BigDecimal dvlpSyncYn;

    @JsonProperty("prod_sync_yn")
    @Schema(description = "운영 동기화 여부")
    private java.math.BigDecimal prodSyncYn;

    @JsonProperty("kafka_connector_status")
    @Schema(description = "Kafka Connector 상태")
    private String kafkaConnectorStatus;

    @JsonProperty("data_pipeline_load_status")
    @Schema(description = "Data Pipeline 로드 상태")
    private String dataPipelineLoadStatus;

    @JsonProperty("data_pipeline_sync_status")
    @Schema(description = "Data Pipeline 동기화 상태")
    private String dataPipelineSyncStatus;

    @JsonProperty("public_status")
    @Schema(description = "공개 여부", example = "전체공유")
    private String publicStatus;

    @JsonProperty("idx_mk_stt_at")
    @Schema(description = "인덱스 생성 시작 시간")
    private java.time.LocalDateTime idxMkSttAt;

    @JsonProperty("idx_mk_end_at")
    @Schema(description = "인덱스 생성 종료 시간")
    private java.time.LocalDateTime idxMkEndAt;

    @JsonProperty("fst_created_at")
    @Schema(description = "최초 생성 일시")
    private java.time.LocalDateTime fstCreatedAt;

    @JsonProperty("lst_updated_at")
    @Schema(description = "최종 수정 일시")
    private java.time.LocalDateTime lstUpdatedAt;

    @JsonProperty("fst_prj_seq")
    @Schema(description = "최초 project seq")
    private Integer fstPrjSeq;

    @JsonProperty("lst_prj_seq")
    @Schema(description = "최종 project seq")
    private Integer lstPrjSeq;

    public void setPublicStatus(String publicStatus) {
        this.publicStatus = publicStatus;
    }
}
