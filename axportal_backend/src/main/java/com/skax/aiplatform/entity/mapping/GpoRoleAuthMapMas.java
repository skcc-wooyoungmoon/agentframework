package com.skax.aiplatform.entity.mapping;

import com.skax.aiplatform.entity.auth.GpoAuthorityMas;
import com.skax.aiplatform.entity.common.BaseEntity2;
import com.skax.aiplatform.entity.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GPO_ROLEAUTH_MAP_MAS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GpoRoleAuthMapMas extends BaseEntity2 {

    /**
     * 복합 기본키 (역할 아이디 + 권한 아이디)
     */
    @EmbeddedId
    private RoleAuthorityId2 id;

    /**
     * 역할내 권한 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_NM")
    private RoleAuthorityStatus statusNm;

    // == 연관관계 필드 == //

    @ManyToOne
    @MapsId("roleSeq")
    @JoinColumn(name = "role_seq")
    private Role role;

    @ManyToOne
    @MapsId("authorityId")
    @JoinColumn(name = "authority_id")
    private GpoAuthorityMas authority;

    /**
     * 역할 - 권한 생성
     */
    public static GpoRoleAuthMapMas create(Role role, GpoAuthorityMas authority) {
        GpoRoleAuthMapMas mapping = new GpoRoleAuthMapMas();

        mapping.id = new RoleAuthorityId2(role.getRoleSeq(), authority.getAuthorityId());
        mapping.role = role;
        mapping.authority = authority;
        mapping.statusNm = RoleAuthorityStatus.ACTIVE;

        return mapping;
    }

    public void activate() {
        this.statusNm = RoleAuthorityStatus.ACTIVE;
    }

    public void inActivate() {
        this.statusNm = RoleAuthorityStatus.INACTIVE;
    }

}
