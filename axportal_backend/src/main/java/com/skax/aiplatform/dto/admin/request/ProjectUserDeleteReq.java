package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 프로젝트 구성원 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 구성원 삭제 요청")
public class ProjectUserDeleteReq {

    @NotEmpty(message = "삭제할 사용자 UUID 목록은 비어 있을 수 없습니다.")
    @Schema(description = "삭제할 사용자 UUID 목록", example = "[\"user-uuid-1\", \"user-uuid-2\"]")
    private List<String> userUuids;

}
