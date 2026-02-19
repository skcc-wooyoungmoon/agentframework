package com.skax.aiplatform.dto.prompt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가드레일 삭제 요청 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 삭제 요청")
public class GuardRailDeleteReq {

    @Schema(description = "삭제할 ID 목록", example = "[\"08dbdf23-ebe0-4156-953c-8b3ca62f7881\", \"18dbdf23-ebe0-4156-953c-8b3ca62f7882\"]")
    @NotEmpty(message = "최소 하나의 아이디가 필요합니다.")
    private List<String> guardrailIds;

}
