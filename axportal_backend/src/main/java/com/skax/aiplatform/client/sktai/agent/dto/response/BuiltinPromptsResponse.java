package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 빌트인 템플릿 응답 DTO
 * 
 * <p>사전 정의된 빌트인 프롬프트 템플릿 목록을 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Inference Prompt 빌트인 템플릿 응답")
public class BuiltinPromptsResponse {
    
    @JsonProperty("data")
    @Schema(description = "빌트인 프롬프트 목록")
    private List<BuiltinPrompt> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Object payload;
    
    /**
     * 빌트인 프롬프트 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "빌트인 프롬프트 정보")
    public static class BuiltinPrompt {
        
        @JsonProperty("name")
        @Schema(description = "프롬프트 이름")
        private String name;
        
        @JsonProperty("uuid")
        @Schema(description = "프롬프트 UUID")
        private String uuid;
        
        @JsonProperty("messages")
        @Schema(description = "프롬프트 메시지 목록")
        private List<Message> messages;
        
        @JsonProperty("variables")
        @Schema(description = "프롬프트 변수 목록")
        private List<Variable> variables;
    }
    
    /**
     * 메시지 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "메시지 정보")
    public static class Message {
        
        @JsonProperty("mtype")
        @Schema(description = "메시지 타입")
        private int mtype;
        
        @JsonProperty("message")
        @Schema(description = "메시지 내용")
        private String message;
    }
    
    /**
     * 변수 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "변수 정보")
    public static class Variable {
        
        @JsonProperty("variable")
        @Schema(description = "변수명")
        private String variable;
        
        @JsonProperty("validation_flag")
        @Schema(description = "검증 플래그")
        private boolean validationFlag;
        
        @JsonProperty("validation")
        @Schema(description = "검증 규칙")
        private String validation;
        
        @JsonProperty("token_limit_flag")
        @Schema(description = "토큰 제한 플래그")
        private boolean tokenLimitFlag;
        
        @JsonProperty("token_limit")
        @Schema(description = "토큰 제한")
        private int tokenLimit;
    }
}
