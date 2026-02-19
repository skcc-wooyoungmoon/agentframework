package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent Tools 생성 응답 DTO
 * 
 * <p>Agent Tools 생성 결과를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Tools 생성 응답")
public class AgentToolCreateRes {

    @Schema(description = "생성된 Agent Tool UUID", example = "00000000-0000-0000-0000-000000000000")
    private String agentToolUuid;
}
