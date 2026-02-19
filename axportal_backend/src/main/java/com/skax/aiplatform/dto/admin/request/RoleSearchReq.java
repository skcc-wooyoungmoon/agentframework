package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 역할 검색 요청 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-09-02
 */
@Getter
@ToString
@Setter
@NoArgsConstructor
public class RoleSearchReq extends PageReq {

    @Schema(description = "역할 유형", example = "DEFAULT", allowableValues = {"DEFAULT", "CUSTOM"})
    private String roleType;

    @Schema(description = "검색 유형", example = "name", allowableValues = {"name", "description"})
    private String filterType;

    @Schema(description = "검색어", example = "역할명")
    private String keyword;

}
