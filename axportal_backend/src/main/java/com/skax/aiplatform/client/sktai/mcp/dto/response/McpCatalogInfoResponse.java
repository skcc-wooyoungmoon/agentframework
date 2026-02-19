package com.skax.aiplatform.client.sktai.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI MCP Catalog 목록 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Catalog 목록 응답")
public class McpCatalogInfoResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "카탈로그 목록")
    private List<McpCatalogInfo> data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "카탈로그 정보")
    public static class McpCatalogInfo {
        @JsonProperty("id")
        @Schema(description = "카탈로그 ID")
        private String id;

        @JsonProperty("name")
        @Schema(description = "카탈로그 이름")
        private String name;

        @JsonProperty("display_name")
        @Schema(description = "표시 이름")
        private String displayName;

        @JsonProperty("description")
        @Schema(description = "설명")
        private String description;

        @JsonProperty("type")
        @Schema(description = "타입", example = "serverless")
        private String type;

        @JsonProperty("server_url")
        @Schema(description = "서버 URL")
        private String serverUrl;

        @JsonProperty("auth_type")
        @Schema(description = "인증 타입")
        private String authType;

        @JsonProperty("auth_config")
        @Schema(description = "인증 설정")
        private Object authConfig;

        @JsonProperty("enabled")
        @Schema(description = "사용 여부")
        private Boolean enabled;

        @JsonProperty("mcp_serving_id")
        @Schema(description = "MCP 서빙 ID")
        private String mcpServingId;

        @JsonProperty("gateway_endpoint")
        @Schema(description = "게이트웨이 엔드포인트")
        private String gatewayEndpoint;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<Tag> tags;

        @JsonProperty("transport_type")
        @Schema(description = "전송 타입", example = "streamable-http")
        private String transportType;

        @JsonProperty("created_at")
        @Schema(description = "생성일시")
        private String createdAt;

        @JsonProperty("updated_at")
        @Schema(description = "수정일시")
        private String updatedAt;

        @JsonProperty("created_by")
        @Schema(description = "생성자 ID")
        private String createdBy;

        @JsonProperty("updated_by")
        @Schema(description = "수정자 ID")
        private String updatedBy;

        @JsonProperty("tools")
        @Schema(description = "도구 목록")
        private List<Tool> tools;
    }

    /**
     * 태그 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "태그 정보")
    public static class Tag {
        @JsonProperty("name")
        @Schema(description = "태그 이름")
        private String name;
    }

     /**
     * Tool 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tool 정보")
    public static class Tool {
        
        @JsonProperty("name")
        @Schema(description = "도구 이름", example = "echo")
        private String name;
        
        @JsonProperty("title")
        @Schema(description = "도구 제목")
        private String title;
        
        @JsonProperty("description")
        @Schema(description = "도구 설명", example = "Echoes back the message provided")
        private String description;
        
        @JsonProperty("inputSchema")
        @Schema(description = "입력 스키마")
        private InputSchema inputSchema;
        
        @JsonProperty("outputSchema")
        @Schema(description = "출력 스키마")
        private Object outputSchema;
        
        @JsonProperty("annotations")
        @Schema(description = "어노테이션")
        private Annotations annotations;
        
        @JsonProperty("_meta")
        @Schema(description = "메타데이터")
        private Object meta;
        
        /**
         * 입력 스키마
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "입력 스키마")
        public static class InputSchema {
            
            @JsonProperty("type")
            @Schema(description = "스키마 타입", example = "object")
            private String type;
        }
        
        /**
         * 어노테이션
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "어노테이션")
        public static class Annotations {
            
            @JsonProperty("title")
            @Schema(description = "제목")
            private String title;
            
            @JsonProperty("readOnlyHint")
            @Schema(description = "읽기 전용 힌트")
            private String readOnlyHint;
            
            @JsonProperty("destructiveHint")
            @Schema(description = "파괴적 힌트")
            private String destructiveHint;
            
            @JsonProperty("idempotentHint")
            @Schema(description = "멱등성 힌트")
            private String idempotentHint;
            
            @JsonProperty("openWorldHint")
            @Schema(description = "오픈 월드 힌트")
            private String openWorldHint;
            
            @JsonProperty("message")
            @Schema(description = "메시지")
            private Message message;
            
            /**
             * 메시지
             */
            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            @Schema(description = "메시지")
            public static class Message {
                
                @JsonProperty("type")
                @Schema(description = "타입", example = "string")
                private String type;
                
                @JsonProperty("description")
                @Schema(description = "설명", example = "The message to echo back")
                private String description;
            }
        }
    }

} 