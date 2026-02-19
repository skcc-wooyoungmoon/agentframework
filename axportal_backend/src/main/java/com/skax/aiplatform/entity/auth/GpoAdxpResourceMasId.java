package com.skax.aiplatform.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Composite key for GpoAdxpResourceMas: (authority_id, scope, resource_url)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class GpoAdxpResourceMasId implements Serializable {

    /**
     * 권한 아이디
     */
    @Column(name = "authority_id", length = 50, nullable = false)
    private String authorityId;

    /**
     * 범위 (GET|POST|PUT|DELETE)
     */
    @Column(name = "scop_v_desc_ctnt", length = 10, nullable = false)
    private String scope;

    /**
     * 리소스 URL
     */
    @Column(name = "call_url", length = 200, nullable = false)
    private String resourceUrl;

    public GpoAdxpResourceMasId(String authorityId, String scope, String resourceUrl) {
        this.authorityId = authorityId;
        this.scope = scope;
        this.resourceUrl = resourceUrl;
    }
}
