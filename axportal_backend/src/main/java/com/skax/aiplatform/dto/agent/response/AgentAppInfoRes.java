package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 에이전트 빌더 앱 정보 응답 DTO
 * 
 * <p>에이전트 빌더 앱 정보를 담는 응답 DTO입니다.</p>
 * 
 * @author 
 * @since 2025-09-16
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "에이전트 빌더 응답")
public class AgentAppInfoRes {
    @Schema(description = "App ID", example = "40293e28-8ed4-4738-885a-c7982c5edd75")
    private String id;

    @Schema(description = "앱 이름", example = "SK 에이전트")
    private String name;

    @Schema(description = "description", example = "SK 에이전트 입니다.")
    private String description;
}