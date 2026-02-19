package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKT AI Platform Graph 템플릿 목록 API 응답 DTO
 * 
 * <p>실제 SKT AI Platform API 응답 구조에 맞는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-09-05
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKT AI Platform Graph 템플릿 목록 API 응답")
public class GraphTemplatesApiResponse {
    
    /**
     * 타임스탬프
     */
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;
    
    /**
     * 응답 코드
     */
    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;
    
    /**
     * 응답 상세 메시지
     */
    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;
    
    /**
     * 추적 ID
     */
    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;
    
    /**
     * 템플릿 데이터 배열
     */
    @JsonProperty("data")
    @Schema(description = "템플릿 데이터 배열")
    private List<GraphTemplateInfo> data;
    
    /**
     * 페이로드
     */
    @JsonProperty("payload")
    @Schema(description = "페이로드")
    private Object payload;
    
    /**
     * 개별 템플릿 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 템플릿 정보")
    public static class GraphTemplateInfo {
        
        /**
         * 아이콘
         */
        @JsonProperty("icon")
        @Schema(description = "템플릿 아이콘", example = "messages")
        private String icon;
        
        /**
         * 템플릿 ID
         */
        @JsonProperty("template_id")
        @Schema(description = "템플릿 고유 식별자", example = "0fd29a8e-7c25-4099-b35d-01c84e28a66d")
        private String templateId;
        
        /**
         * 템플릿 이름
         */
        @JsonProperty("template_name")
        @Schema(description = "템플릿 이름", example = "Chatbot")
        private String templateName;
        
        /**
         * 템플릿 설명
         */
        @JsonProperty("template_description")
        @Schema(description = "템플릿 설명", example = "간단한 AI 챗봇")
        private String templateDescription;
    }
}
