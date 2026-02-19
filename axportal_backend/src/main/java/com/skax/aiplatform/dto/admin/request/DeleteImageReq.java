package com.skax.aiplatform.dto.admin.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IDE 이미지 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "IDE 이미지 삭제 요청")
public class DeleteImageReq {

    @NotEmpty(message = "삭제할 이미지 UUID 목록은 비어 있을 수 없습니다.")
    @Schema(description = "삭제할 이미지 UUID 목록", example = "[\"image-uuid-1\", \"image-uuid-2\"]")
    private List<String> uuids;

}
