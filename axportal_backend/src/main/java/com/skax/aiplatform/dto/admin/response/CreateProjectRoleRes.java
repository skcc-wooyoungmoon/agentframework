package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트 역할 생성 응답 DTO
 */
@Builder
@Getter
@Schema(title = "프로젝트 역할 생성 응답", description = "프로젝트 역할 생성 결과")
public class CreateProjectRoleRes {

    @Schema(description = "생성된 역할 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    public static CreateProjectRoleRes of(String uuid) {
        return CreateProjectRoleRes.builder()
                .uuid(uuid)
                .build();
    }

}
