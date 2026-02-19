package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "프로젝트 역할 삭제 요청 DTO")
public class RoleDeleteReq {

    @NotEmpty(message = "삭제할 역할 ID 목록은 비어 있을 수 없습니다.")
    @Schema(description = "삭제할 역할 ID(UUID) 목록", example = "[role-uuid-1, role-uuid-2]")
    private List<String> roleUuids;

}
