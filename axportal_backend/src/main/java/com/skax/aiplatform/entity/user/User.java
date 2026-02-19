package com.skax.aiplatform.entity.user;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.skax.aiplatform.entity.common.BaseEntity2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GPO_USERS_MAS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class User extends BaseEntity2 {

    /**
     * 그룹사 직원 번호(= 사번)
     */
    @Id
    @Column(name = "MEMBER_ID")
    private String memberId;

    /**
     * 회원가입 후 SKT에서 받아오는 사용자 UUID
     */
    @Column(name = "UUID")
    private String uuid;

    /**
     * 직원명
     */
    @Column(name = "JKW_NM")
    private String jkwNm;

    /**
     * 비밀번호
     */
    @Column(name = "USER_PASSWORD")
    private String userPassword;

    /**
     * 부서명
     */
    @Column(name = "DEPT_NM")
    private String deptNm;

    /**
     * 직급명
     */
    @Column(name = "JKGP_NM")
    private String jkgpNm;

    /**
     * 휴대폰번호
     */
    @Column(name = "HP_NO")
    private String hpNo;

    /**
     * 퇴직 직원 여부(0=재직, 1=퇴직)
     */
    @Column(name = "RETR_JKW_YN")
    private Integer retrJkwYn;

    /**
     * 계정 상태(ACTIVE: 활성, DORMANT: 휴면, WITHDRAW: 탈퇴)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DMC_STATUS")
    private DormantStatus dmcStatus;

    /**
     * 최종 접속 일시
     */
    @Column(name = "LST_LOGIN_AT")
    private LocalDateTime lstLoginAt;

    // == 비즈니스 로직 == //

    /**
     * 사용자 계정 활성화
     */
    public void activateStatus() {
        this.dmcStatus = DormantStatus.ACTIVE;
        this.lstLoginAt = LocalDateTime.now();
    }

}
