package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 역할 삭제 결과 DTO")
public class ProjectRoleDeleteRes {

    @Schema(description = "삭제 성공 개수")
    private int successCount;

    @Schema(description = "삭제 실패 개수")
    private int failureCount;

    @Schema(description = "실패한 역할에 대한 상세 메시지")
    private String errorMessage;

    public static ProjectRoleDeleteRes of(int successCount, int failureCount, String errorMessage) {
        return new ProjectRoleDeleteRes(successCount, failureCount, errorMessage);
    }

}
