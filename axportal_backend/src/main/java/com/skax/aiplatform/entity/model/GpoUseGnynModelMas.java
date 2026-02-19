package com.skax.aiplatform.entity.model;

import java.math.BigDecimal;

import com.skax.aiplatform.entity.common.BaseEntity2;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "GPO_USE_GNYN_MODEL_MAS")
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GpoUseGnynModelMas extends BaseEntity2 {

    /**
     * 시퀀스 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_gnyn_model")
    @SequenceGenerator(
            name = "s_seq_no_gnyn_model",
            sequenceName = "S_SEQ_NO_GNYN_MODEL",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", insertable = false, updatable = false)
    private Long seqNo; 

    /**
     * 모델 이름
        -변경
     */

    @Column(name = "MODEL_DTL_NM", unique = true)
    private String modelNm;
   
    /**
     * description
     */
    @Column(name = "DTL_CTNT")   
    private String dtlCtn;

    /**
     * 파일 크기
     */
    @Column(name = "FILE_SIZE_CNT")
    private BigDecimal fileSizeCnt;

    /**
     * 파라미터 개수
     * - 변경
     */
    @Column(name = "PRMT_CTNT")
    private Long prmtCnt;

    /**
     * 배포 유형
     */
    @Column(name = "DPLY_TYP")
    private String dplyTyp;

    /**
     * 버전
     */
    @Column(name = "DPLY_VER")
    private String dplyVer;

    
    /**
     * 공급사 ID
     */
    @Column(name = "SUPJ_CO_ID")
    private String supjCoId;

    /**
     * provider
     */
    @Column(name = "SUPJ_CO_NM")
    private String supjCoNm;

    /**
     * 모델 유형
     */
    @Column(name = "MODEL_TYP")
    private String modelTyp;

    /**
     * license  // TODO 해당 컬럼 임시로 uuid로 이용
     * -변경
     */
    @Column(name = "CERT_DTL_NM")
    private String certNm;

    /**
     * 태그 
     * - 변경
     */
    @Column(name = "TAG_DTL_CTNT")
    private String tagCtnt;

    /**
     * 언어
     * - 변경
     */
    @Column(name = "PGM_DTL_CTNT")
    private String pgmDescCtnt;

    /**
     * URL
     */
    @Column(name = "CALL_URL")
    private String callUrl;

    /**
     * identifier
     * - 변경
     */
    @Column(name = "MODEL_UUID")
    private String uuid;


    /**
     * 모델 번호
     * // size로 활용
     */
    @Column(name = "MODEL_NO")
    private String modelNo;

    /**
     * 삭제 여부
     */
    @Column(name = "DEL_YN")
    @Builder.Default
    private Integer delYn = 0;


    @Column(name = "DOIP_SEQ_NO", insertable = false, updatable = false)
    private Long doipSeqNo;

    /**
     * 도입 작업 정보 (OneToOne 관계)
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "DOIP_SEQ_NO", referencedColumnName = "SEQ_NO")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GpoModelDoipMas doipInfo;
}
