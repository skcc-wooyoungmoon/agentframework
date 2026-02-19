package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.entity.auth.GpoAuthorityMas;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "역할 상세 권한 응답")
public class PermitDetailRes {

    @Schema(description = "권한 ID", example = "AUTH001")
    private final String authorityId;

    @Schema(description = "하위 메뉴명", example = "데이터 저장소")
    private final String twoDphMenu;

    @Schema(description = "권한명", example = "데이터 저장소 조회")
    private final String authorityNm;

    @Schema(description = "권한 설명", example = "데이터 저장소를 조회할 수 있습니다.")
    private final String dtlCtnt;

    @Builder
    private PermitDetailRes(String authorityId, String twoDphMenu, String authorityNm, String dtlCtnt) {
        this.authorityId = authorityId;
        this.twoDphMenu = twoDphMenu;
        this.authorityNm = authorityNm;
        this.dtlCtnt = dtlCtnt;
    }

    public static PermitDetailRes from(GpoAuthorityMas authority) {
        return PermitDetailRes.builder()
                .authorityId(authority.getAuthorityId())
                .twoDphMenu(authority.getTwoDphMenu())
                .authorityNm(authority.getAuthorityNm())
                .dtlCtnt(authority.getDtlCtnt())
                .build();
    }

    public static List<PermitDetailRes> of(List<GpoAuthorityMas> authorities) {
        return authorities.stream()
                .map(PermitDetailRes::from)
                .toList();
    }

}
