package com.skax.aiplatform.entity.deploy;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GPO_MIG_MAS")
@IdClass(GpoMigMasId.class)
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GpoMigMas {

    /**
     * 시퀀스 번호 (PK의 일부)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_mig")
    @SequenceGenerator(
            name = "s_seq_no_mig",
            sequenceName = "S_SEQ_NO_MIG",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", insertable = false, updatable = false, precision = 19)
    private Long seqNo;

    /**
     * UUID (PK의 일부)
     */
    @Id
    @Column(name = "UUID", length = 100, nullable = false)
    private String uuid;

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
    private String asstNm;

    /**
     * 프로젝트 시퀀스
     */
    @Column(name = "PRJ_SEQ", nullable = false, precision = 5)
    private Integer prjSeq;

    /**
     * GPO 프로젝트 명
     */
    @Column(name = "GPO_PRJ_NM", length = 150, nullable = false)
    private String gpoPrjNm;

    /**
     * 마이그레이션 파일 경로
     */
    @Column(name = "MIG_FILE_PATH", length = 4000, nullable = false)
    private String migFilePath;

    /**
     * 마이그레이션 파일 명
     */
    @Column(name = "MIG_FILE_NM", length = 4000, nullable = false)
    private String migFileNm;

    /**
     * 프로그램 설명 내용
     */
    @Column(name = "PGM_DESC_CTNT", length = 100)
    private String pgmDescCtnt;

    /**
     * 삭제 여부 (0: 정상, 1: 삭제)
     */
    @Column(name = "DEL_YN", precision = 1)
    private Integer delYn;

    /**
     * 최초 생성일시
     */
    @Column(name = "FST_CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime fstCreatedAt;

    /**
     * 생성자
     */
    @Column(name = "CREATED_BY", length = 50, updatable = false, nullable = false)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (fstCreatedAt == null) {
            fstCreatedAt = now;
        }
        if (delYn == null) {
            delYn = 0; // 0: 정상
        }
        if (createdBy == null) {
            createdBy = "system";
        }
    }
}
