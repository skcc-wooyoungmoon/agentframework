package com.skax.aiplatform.client.sktai.agent.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Tools 상세 정보 응답 DTO
 * 
 * <p>특정 Tools의 상세 정보를 담는 응답 데이터 구조입니다.</p>
 *
 * @author gyuHeeHwang
 * @since 2025-08-25
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Tools 상세 정보 응답")
public class ToolResponse {
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
    @Schema(description = "Tools 상세 정보")
    private ToolsDetail data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
    
    /**
     * Tools 상세 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tools 상세 정보")
    public static class ToolsDetail {
        
        @JsonProperty("id")
        @Schema(description = "Tool ID", example = "40293e28-8ed4-4738-885a-c7982c5edd75")
        private String id;
        
        @JsonProperty("name")
        @Schema(description = "Tool 이름", example = "tavily_search_test")
        private String name;
        
        @JsonProperty("display_name")
        @Schema(description = "Tool 표시 이름", example = "황규희 도구")
        private String displayName;
        
        @JsonProperty("description")
        @Schema(description = "Tool 설명", example = "웹검색 tool. 최신, 실시간 데이터 또는 웹에서 정확한 데이터 검색 필요시 사용.")
        private String description;
        
        @JsonProperty("tool_type")
        @Schema(description = "Tool 타입", example = "custom_code")
        private String toolType;
        
        @JsonProperty("code")
        @Schema(description = "Tool 코드")
        private String code;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
        private String projectId;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
        private String createdBy;
        
        @JsonProperty("updated_by")
        @Schema(description = "수정자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
        private String updatedBy;

        @JsonProperty("method")
        @Schema(description = "메서드 (custom_api 타입에만 존재)", example = "GET")
        private String method;

        @JsonProperty("server_url")
        @Schema(description = "서버 URL (custom_api 타입에만 존재)", example = "https://api.example")
        private String serverUrl;
        
        @JsonProperty("api_param")
        @Schema(description = "API 파라미터 (custom_api 타입에만 존재)", example = "{\"header\":{\"auth_key\":\"key123\"},\"static_params\":{\"action\":\"query\",\"format\":\"json\",\"list\":\"search\"},\"dynamic_params\":{\"query\":\"str\"}}")
        private Object apiParam;
        
        @JsonProperty("input_keys")
        @Schema(description = "입력 키 목록")
        private List<InputKey> inputKeys;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<Tag> tags;
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
        @Schema(description = "태그 이름", example = "websearch")
        private String name;
    }
    
    /**
     * 입력 키 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "입력 키 정보")
    public static class InputKey {
        
        @JsonProperty("key")
        @Schema(description = "키 이름", example = "query")
        private String key;
        
        @JsonProperty("comment")
        @Schema(description = "주석")
        private String comment;
        
        @JsonProperty("required")
        @Schema(description = "필수 여부", example = "true")
        private Boolean required;
        
        @JsonProperty("type")
        @Schema(description = "데이터 타입", example = "str")
        private String type;
        
        @JsonProperty("default_value")
        @Schema(description = "기본값")
        private String defaultValue;
    }
}
