package com.skax.aiplatform.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gpo_grpco_jkw_mas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GpoGroupcoJkwMas {

    /**
     * 사번 (멤버 ID)
     */
    @Id
    @Column(name = "member_id", length = 50)
    private String memberId;

    /**
     * 부서명
     */
    @Column(name = "dept_nm", length = 100)
    private String deptNm;

    /**
     * 부서번호
     */
    @Column(name = "dept_no", length = 4)
    private String deptNo;

    /**
     * 그룹사 코드
     */
    @Column(name = "grpco_c", length = 4)
    private String grpcoC;

    /**
     * 그룹사명
     */
    @Column(name = "grpco_nm", length = 100)
    private String grpcoNm;

    /**
     * 휴대폰번호
     */
    @Column(name = "hp_no", length = 30)
    private String hpNo;

    /**
     * 직급명
     */
    @Column(name = "jkgp_nm", length = 100)
    private String jkgpNm;

    /**
     * 직원명
     */
    @Column(name = "jkw_nm", length = 100)
    private String jkwNm;

    /**
     * 직위 코드
     */
    @Column(name = "jkwi_c", length = 2)
    private String jkwiC;

    /**
     * 직위명
     */
    @Column(name = "jkwi_nm", length = 60)
    private String jkwiNm;

    /**
     * 퇴직 직원 여부 (1: 퇴직, 0: 재직)
     */
    @Column(name = "retr_jkw_yn")
    private Integer retrJkwYn;

    /**
     * 최초 생성일시
     */
    @Column(name = "fst_created_at")
    private LocalDateTime fstCreatedAt;

    /**
     * 최종 수정일시
     */
    @Column(name = "lst_updated_at")
    private LocalDateTime lstUpdatedAt;
}
