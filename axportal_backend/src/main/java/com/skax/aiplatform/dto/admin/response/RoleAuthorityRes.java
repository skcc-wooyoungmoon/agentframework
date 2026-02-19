package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.entity.auth.GpoAuthorityMas;
import com.skax.aiplatform.entity.mapping.GpoRoleAuthMapMas;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트 역할 권한 응답 DTO
 */
@Getter
@Builder
public class RoleAuthorityRes {

    /**
     * 권한 아이디
     */
    private final String authorityId;

    /**
     * 상위 권한 아이디
     */
    private final String hrnkAuthorityId;

    /**
     * 권한명
     */
    private final String authorityNm;

    /**
     * 하위 메뉴명 (2 Depth)
     */
    private final String twoDepthMenu;

    /**
     * 상세 권한 설명
     */
    private final String detailContent;

    public static RoleAuthorityRes of(GpoRoleAuthMapMas mapping) {
        GpoAuthorityMas authority = mapping.getAuthority();

        return RoleAuthorityRes.builder()
                .authorityId(authority.getAuthorityId())
                .hrnkAuthorityId(authority.getHrnkAuthorityId())
                .authorityNm(authority.getAuthorityNm())
                .twoDepthMenu(authority.getTwoDphMenu())
                .detailContent(authority.getDtlCtnt())
                .build();
    }

}
