package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.entity.auth.GpoAuthorityMas;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "역할 메뉴 권한 응답")
public class MenuPermitRes {

    @Schema(description = "권한 ID", example = "AUTH001")
    private final String authorityId;

    @Schema(description = "상위 메뉴명", example = "데이터")
    private final String oneDphMenu;

    @Schema(description = "하위 메뉴명", example = "데이터 저장소")
    private final String twoDphMenu;

    @Builder
    private MenuPermitRes(String authorityId, String oneDphMenu, String twoDphMenu) {
        this.authorityId = authorityId;
        this.oneDphMenu = oneDphMenu;
        this.twoDphMenu = twoDphMenu;
    }

    public static MenuPermitRes from(GpoAuthorityMas authority) {
        return MenuPermitRes.builder()
                .authorityId(authority.getAuthorityId())
                .oneDphMenu(authority.getOneDphMenu())
                .twoDphMenu(authority.getTwoDphMenu())
                .build();
    }

}
