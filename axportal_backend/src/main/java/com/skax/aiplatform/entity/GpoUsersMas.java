package com.skax.aiplatform.entity;

import com.skax.aiplatform.entity.common.BaseEntity2;
import com.skax.aiplatform.entity.user.DormantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gpo_users_mas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Builder
public class GpoUsersMas extends BaseEntity2 {

    /**
     * MEMBER ID
     */
    @Id
    @Column(name = "member_id")
    private String memberId;

    /**
     * UUID
     */
    @Column(name = "uuid")
    private String uuid;

    /**
     * 직원명
     */
    @Column(name = "jkw_nm")
    private String jkwNm;

    /**
     * 사용자 PASSWORD
     */
    @Column(name = "user_password")
    private String userPassword;

    /**
     * 부서명
     */
    @Column(name = "dept_nm")
    private String deptNm;

    /**
     * 직급명
     */
    @Column(name = "jkgp_nm")
    private String jkgpNm;

    /**
     * 휴대폰 번호
     */
    @Column(name = "hp_no")
    private String hpNo;

    /**
     * 퇴직 직원 여부 (1: 재직, 0: 퇴직)
     */
    @Column(name = "retr_jkw_yn")
    private Integer retrJkwYn;

    /**
     * 휴먼계정 상태 (ACTIVE: 활성, DORMANT: 휴면)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "dmc_status")
    private DormantStatus dmcStatus;

    /**
     * 최종 로그인 일시
     */
    @Column(name = "lst_login_at")
    private LocalDateTime lstLoginAt;

}
