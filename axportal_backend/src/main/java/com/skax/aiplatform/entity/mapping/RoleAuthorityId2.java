package com.skax.aiplatform.entity.mapping;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class RoleAuthorityId2 implements Serializable {

    /**
     * 역할 시퀀스
     */
    private Long roleSeq;

    /**
     * 권한 아이디
     */
    private String authorityId;

    public RoleAuthorityId2(Long roleSeq, String authorityId) {
        this.roleSeq = roleSeq;
        this.authorityId = authorityId;
    }

}
