package com.skax.aiplatform.entity.project;

import com.skax.aiplatform.entity.common.BaseEntity2;
import com.skax.aiplatform.entity.common.enums.YNStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "GPO_PROJECTS_MAS")
@Getter
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
public class Project extends BaseEntity2 {

    /**
     * 프로젝트 시퀀스
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_prj_seq")
    @SequenceGenerator(
            name = "s_prj_seq",
            sequenceName = "S_PRJ_SEQ",
            allocationSize = 1  // 데이터베이스 시퀀스와 일치시킴
    )
    @Column(name = "PRJ_SEQ")
    private Long prjSeq;

    /**
     * 프로젝트 생성 후 승인이되면 SKT에서 받아오는 프로젝트 ID
     */
    @Column(name = "UUID")
    private String uuid;

    /**
     * 프로젝트명
     */
    @Column(name = "GPO_PRJ_NM")
    private String prjNm;

    /**
     * 프로젝트 설명
     */
    @Column(name = "DTL_CTNT")
    private String dtlCtnt;

    /**
     * 프로젝트 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_NM")
    private ProjectStatus statusNm;

    /**
     * 개인정보 포함 여부
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "SSTV_INF_INCL_YN")
    private YNStatus sstvInfInclYn;

    /**
     * 개인정보 포함 사유
     */
    @Column(name = "SSTV_INF_INCL_DTL_CTNT")
    private String sstvInfInclDesc;

    /**
     * 프로젝트 정보 수정
     */
    public void update(String prjNm, String dtlCtnt, YNStatus sstvInfInclYn, String sstvInfInclDesc) {
        if (StringUtils.hasText(prjNm)) {
            this.prjNm = prjNm.trim();
        }

        if (dtlCtnt != null) {
            this.dtlCtnt = dtlCtnt.trim();
        }

        this.sstvInfInclYn = sstvInfInclYn;

        if (this.sstvInfInclYn == YNStatus.Y) {
            this.sstvInfInclDesc = sstvInfInclDesc.trim();
        } else {
            this.sstvInfInclDesc = null;
        }
    }

}
