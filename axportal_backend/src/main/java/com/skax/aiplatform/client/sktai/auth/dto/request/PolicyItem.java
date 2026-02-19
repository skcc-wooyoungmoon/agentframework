package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 정책 항목 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-10
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "정책 항목")
public class PolicyItem {
    
    @JsonProperty("type")
    @Schema(description = "정책 타입 (role, regex 등)", example = "role")
    private String type;
    
    @JsonProperty("logic")
    @Schema(description = "논리 연산자", example = "POSITIVE")
    private String logic;
    
    @JsonProperty("names")
    @Schema(description = "역할 이름 목록 (type이 role인 경우)", example = "[\"ai_tool_admin\", \"finetuning_user\"]")
    private List<String> names;
    
    @JsonProperty("target_claim")
    @Schema(description = "대상 클레임 (type이 regex인 경우)", example = "current_group")
    private String targetClaim;
    
    @JsonProperty("pattern")
    @Schema(description = "정규표현식 패턴 (type이 regex인 경우)", example = "/public")
    private String pattern;
}
