package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 구성원 초대용 사용자 검색 요청 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-09-05
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InviteUserSearchReq extends PageReq {

    @Schema(description = "검색 조건", example = "jkwNm",
            allowableValues = {"jkwNm", "deptNm"})
    private String filterType;

    @Schema(description = "검색어", example = "홍길동")
    private String keyword;

}
