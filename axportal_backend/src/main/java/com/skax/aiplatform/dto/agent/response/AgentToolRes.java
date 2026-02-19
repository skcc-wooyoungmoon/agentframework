package com.skax.aiplatform.dto.agent.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent Tools 응답 DTO
 * 
 * <p>Agent Tools 정보를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Tools 응답")
public class AgentToolRes {

    @Schema(description = "Agent Tool ID", example = "40293e28-8ed4-4738-885a-c7982c5edd75")
    private String id;
    
    @Schema(description = "Agent Tool 이름", example = "tavily_search_test")
    private String name;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Agent Tool 표시 이름", example = "🌎 Wiki 검색 도구")
    private String displayName;
    
    @Schema(description = "Agent Tool 설명", example = "웹검색 tool. 최신, 실시간 데이터 또는 웹에서 정확한 데이터 검색 필요시 사용.")
    private String description;
    
    @Schema(description = "Agent Tool 타입", example = "custom_code")
    private String toolType;
    
    @Schema(description = "Agent Tool 코드")
    private String code;
    
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;
    
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @Schema(description = "수정 시간")
    private String updatedAt;
    
    @Schema(description = "생성자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
    private String createdBy;

    @Schema(description = "수정자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
    private String updatedBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "서버 URL", example = "https://api.example")
    private String serverUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "메서드", example = "GET")
    private String method;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "API 파라미터", example = "{\"header\":{\"auth_key\":\"key123\"},\"static_params\":{\"action\":\"query\",\"format\":\"json\",\"list\":\"search\"},\"dynamic_params\":{\"query\":\"str\"}}")
    private Object apiParam;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "입력 키 목록")
    private List<InputKey> inputKeys;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "태그 목록")
    private List<String> tags;

    @Schema(description = "공개범위")
    private String publicStatus;
    
    /**
     * 입력 키 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "입력 키 정보")
    public static class InputKey {
        
        @Schema(description = "키 이름", example = "query")
        private String key;
        
        @Schema(description = "주석")
        private String comment;
        
        @Schema(description = "필수 여부", example = "true")
        private Boolean required;
        
        @Schema(description = "데이터 타입", example = "str")
        private String type;
        
        @Schema(description = "기본값")
        private Object defaultValue;
    }

}
