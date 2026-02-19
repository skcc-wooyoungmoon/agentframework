package com.skax.aiplatform.entity;


import com.skax.aiplatform.common.constant.CommCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authority")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority extends BaseEntity {

    /**
     * 권한 아이디
     */
    @Id
    @Column(name = "authority_id")
    private String id;

    @Column(nullable = false)
    private String name;

    private String menu1;

    private String menu2;

    private String type;

    @Enumerated(EnumType.STRING)
    private CommCode.RoleStatus status;
    /**
     * 권한 설명
     */
    private String description;

    /**
     * 부모 권한
     */
    @ManyToOne
    @JoinColumn(name = "parent_authority_id")
    private Authority parentAuthority;

    /**
     * 하위 권한 목록
     */
    @OneToMany(mappedBy = "parentAuthority")
    private Set<Authority> childAuthorities = new HashSet<>();

     /**
     * 권한 생성 (최상위 권한)
     */
    public static Authority createTopLevelAuthority(String id, String name) {
        Authority authority = new Authority();
        authority.id = id;
        authority.name = name;
        return authority;
    }


}
