package com.skax.aiplatform.dto.agent.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 에이전트 빌더 요청 DTO (수정 및 일괄 삭제)
 * 
 * @author ByounggwanLee
 * @since 2025-08-19
 * @version 1.0.0
 */
@Getter
@Setter
@ToString
@Schema(description = "에이전트 빌더 요청")
public class AgentBuilderUpdateReq {

    @Schema(description = "에이전트 이름", example = "신용대출 상담 에이전트")
    private String name;

    @Schema(description = "에이전트 설명", example = "신용대출 상담을 위한 AI 에이전트입니다.")
    private String description;

    @Schema(description = "에이전트 타입", example = "chat")
    private String type;

    @Schema(description = "에이전트 카테고리", example = "finance")
    private String category;

    @NotEmpty(message = "삭제할 에이전트 ID 목록은 필수입니다.")
    @Schema(description = "삭제할 에이전트 ID 목록 (일괄 삭제용)", example = "[\"uuid1\", \"uuid2\"]")
    private List<String> agentIds;
} 