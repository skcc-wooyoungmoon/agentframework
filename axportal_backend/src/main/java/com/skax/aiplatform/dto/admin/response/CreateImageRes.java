package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * IDE 이미지 생성 응답 DTO
 */
@Builder
@Getter
@Schema(title = "이미지 생성 응답", description = "IDE 이미지 생성 결과")
public class CreateImageRes {

    @Schema(description = "생성된 이미지 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    public static CreateImageRes of(String uuid) {
        return CreateImageRes.builder()
                .uuid(uuid)
                .build();
    }

}
