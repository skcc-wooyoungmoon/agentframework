package com.skax.aiplatform.entity.knowledge;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GPO 지식 정보 원장 엔티티
 * 
 * <p>
 * GPO 지식 정보를 관리하는 마스터 테이블입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Entity
@Table(name = "gpo_kwlg_info_mas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpoKwlgInfoMas {

    /**
     * 지식ID (Primary Key)
     */
    @Id
    @Column(name = "kwlg_id", length = 50)
    private String kwlgId;

    /**
     * 지식명
     */
    @Column(name = "kwlg_nm", length = 100)
    private String kwlgNm;

    /**
     * 외부지식ID
     */
    @Column(name = "ex_kwlg_id", length = 50)
    private String exKwlgId;

    /**
     * CHUNK ID
     */
    @Column(name = "chunk_id", length = 50)
    private String chunkId;

    /**
     * 파라미터 내용
     * 청킹 파라미터 내용을 JSON 형식으로 저장
     * ex) {"chunk_size": 300, "sentence_overlap": 0}
     */
    @Column(name = "prmt_ctnt", length = 4000)
    private String prmtCtnt;

    /**
     * 모델ID
     */
    @Column(name = "model_id", length = 50)
    private String modelId;

    /**
     * DATASET ID
     */
    @Column(name = "data_set_long_id", length = 4000)
    private String dataSetId;

    /**
     * DATASET명
     */
    @Column(name = "data_set_nm", length = 100)
    private String dataSetNm;

    /**
     * 인덱스명
     */
    @Column(name = "idx_nm", length = 100)
    private String idxNm;

    /**
     * CONSUMER 그룹명
     */
    @Column(name = "consumer_grp_nm", length = 100)
    private String consumerGrpNm;

    /**
     * 파일 LOAD 진행율
     */
    @Column(name = "file_load_jinhg_rt", precision = 10)
    private BigDecimal fileLoadJinhgRt;

    /**
     * CHUNK 진행율
     */
    @Column(name = "chunk_jinhg_rt", precision = 10)
    private BigDecimal chunkJinhgRt;

    /**
     * DB LOAD 진행율
     */
    @Column(name = "db_load_jinhg_rt", precision = 10)
    private BigDecimal dbLoadJinhgRt;

    /**
     * 개발 동기화 여부 (0: 미동기화, 1: 동기화)
     */
    @Column(name = "dvlp_synch_yn", precision = 1)
    private BigDecimal dvlpSynchYn;

    /**
     * 운영 동기화 여부 (0: 미동기화, 1: 동기화)
     */
    @Column(name = "unyung_synch_yn", precision = 1)
    private BigDecimal unyungSynchYn;

    /**
     * KAFKA CONNECTOR STATUS
     */
    @Column(name = "kafka_cntr_status", length = 100)
    private String kafkaCntrStatus;

    /**
     * DATA PIPELINE 실행 ID
     */
    @Column(name = "data_pipeline_exe_id", length = 50)
    private String dataPipelineExeId;

    /**
     * DATA PIPELINE LOAD STATUS
     */
    @Column(name = "data_pipeline_load_status", length = 100)
    private String dataPipelineLoadStatus;

    /**
     * DATA PIPELINE 동기화 STATUS
     */
    @Column(name = "data_pipeline_synch_status", length = 200)
    private String dataPipelineSynchStatus;

    /**
     * 인덱스 생성 시작 AT
     */
    @Column(name = "idx_mk_stt_at")
    private LocalDateTime idxMkSttAt;

    /**
     * 인덱스 생성 종료 AT
     */
    @Column(name = "idx_mk_end_at")
    private LocalDateTime idxMkEndAt;

    /**
     * 최초 생성일시
     */
    @Column(name = "fst_created_at")
    private LocalDateTime fstCreatedAt;

    /**
     * 생성자
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 최종 수정일시
     */
    @Column(name = "lst_updated_at")
    private LocalDateTime lstUpdatedAt;

    /**
     * 수정자
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (fstCreatedAt == null) {
            fstCreatedAt = now;
        }
        lstUpdatedAt = now;
        if (createdBy == null) {
            createdBy = "system";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lstUpdatedAt = LocalDateTime.now();
    }
}
