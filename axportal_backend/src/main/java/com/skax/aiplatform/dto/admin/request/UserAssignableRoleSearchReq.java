package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 사용자 할당 가능한 역할 검색 요청 DTO
 *
 * @author 장지원
 * @version 2.0.0
 * @since 2025-10-03
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserAssignableRoleSearchReq extends PageReq {

    @Schema(description = "검색 유형", example = "name", allowableValues = {"name", "description"})
    private String filterType;

    @Schema(description = "검색어", example = "프로젝트 관리자")
    private String keyword;

}
