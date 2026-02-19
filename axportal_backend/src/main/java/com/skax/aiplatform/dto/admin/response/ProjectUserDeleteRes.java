package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 구성원 삭제 결과")
public class ProjectUserDeleteRes {

    @Schema(description = "삭제 성공 건수", example = "3")
    private int successCount;

    @Schema(description = "삭제 실패 건수", example = "1")
    private int failureCount;

    public static ProjectUserDeleteRes of(int successCount, int failureCount) {
        return ProjectUserDeleteRes.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

}
