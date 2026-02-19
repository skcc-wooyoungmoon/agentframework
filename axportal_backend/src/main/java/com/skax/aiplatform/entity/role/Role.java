package com.skax.aiplatform.entity.role;

import com.skax.aiplatform.entity.common.BaseEntity2;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "GPO_ROLES_MAS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Role extends BaseEntity2 {

    /**
     * 역할 시퀀스
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_role_seq")
    @SequenceGenerator(
            name = "s_role_seq",
            sequenceName = "S_ROLE_SEQ",
            allocationSize = 1
    )
    @Column(name = "ROLE_SEQ")
    private Long roleSeq;

    /**
     * 프로젝트 시퀀스
     */
    @Column(name = "PRJ_SEQ")
    private Long prjSeq;

    /**
     * 역할 생성 후 SKT에서 받아오는 역할 ID
     */
    @Column(name = "UUID")
    private String uuid;

    /**
     * 역할 범위(Portal 또는 Project)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "RIGHT_SCOP_CTNT")
    private RoleScope rightScopCtnt;

    /**
     * 역할명
     */
    @Column(name = "GPO_ROLE_NM")
    private String roleNm;

    /**
     * 역할 설명
     */
    @Column(name = "DTL_CTNT")
    private String dtlCtnt;

    /**
     * 역할 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_NM")
    private RoleStatus statusNm;

    /**
     * 역할 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_TYPE")
    private RoleType roleType;

    // == 비즈니스 메소드 == //

    // 역할 생성 로직
    public static Role create(Long prjSeq, String uuid, String roleNm, String dtlCtnt) {
        return Role.builder()
                .prjSeq(prjSeq)
                .uuid(uuid)
                .rightScopCtnt(RoleScope.PROJECT)
                .roleNm(roleNm)
                .dtlCtnt(dtlCtnt)
                .statusNm(RoleStatus.ACTIVE)
                .roleType(RoleType.CUSTOM)
                .build();
    }

    // 역할 수정 로직
    public void update(String roleNm, String dtlCtnt) {
        this.roleNm = roleNm;
        this.dtlCtnt = dtlCtnt;
    }

    // 역할 활성화
    public void activate() {
        this.statusNm = RoleStatus.ACTIVE;
    }

    // 역할 비활성화
    public void inActivate() {
        this.statusNm = RoleStatus.INACTIVE;
    }

}
