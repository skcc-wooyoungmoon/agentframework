package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WorkFlow 생성 응답 DTO
 *
 * <p>WorkFlow 생성 결과를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 *
 * @author yunyoseob
 * @since 2025-09-18
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "WorkFlow 생성 응답")
public class WorkFlowCreateRes {
    @Schema(description = "생성된 WorkFlow UUID", example = "00000000-0000-0000-0000-000000000000")
    private String workFlowId;
}
