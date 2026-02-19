package com.skax.aiplatform.entity.auth;

import com.skax.aiplatform.entity.common.BaseEntity2;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GPO_PORTAL리소스_원장 엔티티
 * 참고 테이블: gpo_adxp_resource_mas
 */
@Entity
@Table(name = "gpo_p_authresrc_map_mas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GpoPortalResourceMas extends BaseEntity2 {

    /**
     * 복합 기본키 (authority_id, scope, resource_url)
     */
    @EmbeddedId
    private GpoPortalResourceMasId id;

    /**
     * 리소스 상세 설명
     */
    @Column(name = "dtl_ctnt", length = 4000)
    private String dtlCtnt;

    // Convenience getters to preserve previous API
    public String getAuthorityId() {
        return id != null ? id.getAuthorityId() : null;
    }

    public String getScope() {
        return id != null ? id.getScope() : null;
    }

    public String getResourceUrl() {
        return id != null ? id.getResourceUrl() : null;
    }
}
