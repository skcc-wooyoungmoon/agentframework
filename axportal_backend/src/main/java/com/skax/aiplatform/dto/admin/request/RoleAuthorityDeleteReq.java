package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 프로젝트 역할 권한 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 역할 권한 삭제 요청 DTO")
public class RoleAuthorityDeleteReq {

    @NotEmpty(message = "삭제할 권한 ID 목록은 비어 있을 수 없습니다.")
    @Schema(description = "삭제할 권한 ID 목록")
    private List<String> authorityIds;

}
