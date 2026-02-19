package com.skax.aiplatform.dto.deploy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 세이프티 필터 삭제 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 삭제 응답")
public class SafetyFilterDeleteRes {

    @Schema(description = "전체 건수", example = "3")
    private int totalCount;

    @Schema(description = "삭제된 건수", example = "3")
    private int deletedCount;

    public static SafetyFilterDeleteRes of(int totalCount, int successCount) {
        return SafetyFilterDeleteRes.builder()
                .totalCount(totalCount)
                .deletedCount(successCount)
                .build();
    }

}

