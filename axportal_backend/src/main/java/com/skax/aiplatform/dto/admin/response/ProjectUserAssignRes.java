package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 구성원 역할 할당 결과 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 구성원 역할 할당 결과")
public class ProjectUserAssignRes {

    @Schema(description = "할당 성공 건수", example = "3")
    private int successCount;

    @Schema(description = "할당 실패 건수", example = "1")
    private int failureCount;


    public static ProjectUserAssignRes of(int successCount, int failureCount) {
        return new ProjectUserAssignRes(successCount, failureCount);
    }

}
