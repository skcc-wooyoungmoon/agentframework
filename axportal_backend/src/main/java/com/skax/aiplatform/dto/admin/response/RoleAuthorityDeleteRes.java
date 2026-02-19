package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 역할 권한 삭제 결과 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 역할 권한 삭제 결과 DTO")
public class RoleAuthorityDeleteRes {

    @Schema(description = "삭제 성공 개수", example = "3")
    private int successCount;

    @Schema(description = "삭제 실패 개수", example = "1")
    private int failureCount;

    public static RoleAuthorityDeleteRes of(int successCount, int failureCount) {
        return RoleAuthorityDeleteRes.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

}
