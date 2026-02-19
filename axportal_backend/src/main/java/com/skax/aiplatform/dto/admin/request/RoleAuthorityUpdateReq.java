package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 프로젝트 역할 권한 일괄 수정 요청 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoleAuthorityUpdateReq {

    @NotNull(message = "권한 아이디 목록은 null 일 수 없습니다.")
    @Schema(description = "권한 아이디 목록")
    private List<String> authorityIds = new ArrayList<>();

}
