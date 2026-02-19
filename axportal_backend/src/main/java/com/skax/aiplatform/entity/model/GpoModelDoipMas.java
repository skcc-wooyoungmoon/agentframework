package com.skax.aiplatform.entity.model;

import java.time.LocalDateTime;

import com.skax.aiplatform.entity.common.AuditableEntity;
import com.skax.aiplatform.enums.ModelGardenStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "GPO_MODEL_DOIP_MAS")
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GpoModelDoipMas extends AuditableEntity {

    /**
     * 시퀀스 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_model_doip")
    @SequenceGenerator(
            name = "s_seq_no_model_doip",
            sequenceName = "S_SEQ_NO_MODEL_DOIP",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", insertable = false, updatable = false)
    private Long seqNo;

    /**
     * 모델 정보 (OneToOne 관계 - 역방향)
     */
    @OneToOne(mappedBy = "doipInfo", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GpoUseGnynModelMas gnynModel;

    /**
     * 아티팩트 ID
     */
    @Column(name = "ARTIFACT_ID")
    private String artifactId;

    /**
     * 리비전 ID
     */
    @Column(name = "REVISION_ID")
    private String revisionId;

    /**
     * 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_NM")
    @Builder.Default
    private ModelGardenStatus statusNm = ModelGardenStatus.PENDING;

    /**
     * 반입 요청 일시
     */
    @Column(name = "DOIP_AT")
    private LocalDateTime doipAt;

    /**
     * 반입 요청 담당자
     */
    @Column(name = "DOIP_MN")
    private String doipMn;

    /**
     * 반입 체크 일시
     */
    @Column(name = "CHK_AT")
    private LocalDateTime chkAt;

    /**
     * 체크 담당자
     */
    @Column(name = "CHK_MN")
    private String chkMn;

    /**
     * 첫 체크 상세 내용
     */
    @Column(name = "FIST_CHK_DTL_CTNT", length = 4000)
    private String fistChkDtlCtnt;

    /**
     * 두 번째 체크 상세 내용
     */
    @Column(name = "SECD_CHK_DTL_CTNT", length = 4000)
    private String secdChkDtlCtnt;

    /**
     * 취약점 점검 상세 내용
     */
    @Column(name = "VANB_BR_DTL_CTNT", length = 4000)
    private String vanbBrDtlCtnt;

    /**
     * 취약점 점검 요약 내용
     */
    @Column(name = "VANB_BR_SMRY_CTNT", length = 4000)
    private String vanbBrSmryCtnt;

    /**
     * 파일 구분 개수
     */
    @Column(name = "FILE_DIV_CNT")
    private Integer fileDivCnt;

    /**
     * 파일 점검 완료 수
     */
    @Column(name = "FILE_CHK_CPLT_CNT")
    private Integer fileChkCpltCnt;

}
