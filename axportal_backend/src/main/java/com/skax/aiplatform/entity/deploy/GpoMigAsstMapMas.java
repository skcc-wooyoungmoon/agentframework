package com.skax.aiplatform.entity.deploy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GPO 마이그레이션 매핑 마스터 엔티티
 */
@Entity
@Table(name = "GPO_MIGASST_MAP_MAS")
@IdClass(GpoMigAsstMapMasId.class)
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GpoMigAsstMapMas {

    /**
     * 시퀀스 번호 (PK의 일부)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_migasst_map")
    @SequenceGenerator(
            name = "s_seq_no_migasst_map",
            sequenceName = "S_SEQ_NO_MIGASST_MAP",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", insertable = false, updatable = false, precision = 19)
    private Long seqNo;

    /**
     * 마이그레이션 시퀀스 번호
     */
    @Column(name = "MIG_SEQ_NO", nullable = false, precision = 19)
    private Long migSeqNo;

    /**
     * 마이그레이션 UUID (PK의 일부)
     */
    @Id
    @Column(name = "MIG_UUID", length = 100, nullable = false)
    private String migUuid;

    /**
     * 어시스트 UUID
     */
    @Column(name = "ASST_UUID", length = 100, nullable = false)
    private String asstUuid;

    /**
     * 어시스트 그룹 (PK의 일부)
     */
    @Id
    @Column(name = "ASST_G", length = 500, nullable = false)
    private String asstG;

    /**
     * 어시스트 명
     */
    @Column(name = "ASST_NM", length = 150, nullable = false)
    private String assetNm;

    /**
     * 마이그레이션 매핑 명
     */
    @Column(name = "MIG_MAP_NM", length = 150, nullable = false)
    private String migMapNm;

    /**
     * 개발 상세 내용
     */
    @Column(name = "DVLP_DTL_CTNT", length = 4000, nullable = false)
    private String dvlpDtlCtnt;

    /**
     * 운영 상세 내용
     */
    @Column(name = "UNYUNG_DTL_CTNT", length = 4000, nullable = false)
    private String unyungDtlCtnt;
}

