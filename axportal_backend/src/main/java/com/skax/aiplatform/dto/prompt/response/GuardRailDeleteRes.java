package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가드레일 삭제 응답 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 삭제 응답")
public class GuardRailDeleteRes {

    @Schema(description = "전체 건수", example = "3")
    private int totalCount;

    @Schema(description = "성공 건수", example = "3")
    private int successCount;

    public static GuardRailDeleteRes of(int totalCount, int successCount) {
        return GuardRailDeleteRes.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .build();
    }

}
