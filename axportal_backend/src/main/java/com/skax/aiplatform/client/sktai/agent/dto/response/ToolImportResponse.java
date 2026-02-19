package com.skax.aiplatform.client.sktai.agent.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Tool Import 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Tool Import 결과를 담는 응답 데이터 구조입니다.
 * Tool이 존재하면 검증하고, 존재하지 않으면 생성합니다.</p>
 * 
 * <h3>응답 코드:</h3>
 * <ul>
 *   <li><strong>code: 1</strong>: 검증 성공 또는 생성 성공 (detail: "Validated")</li>
 *   <li><strong>code: -108</strong>: Tool body와 tool이 일치하지 않음 (detail: "Entity가 일치하지 않습니다.: Tool body and tool are not the same.")</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-11-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Tool Import 응답")
public class ToolImportResponse {
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드 (1: 성공, -108: 일치하지 않음)", example = "1")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지", example = "Validated")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "Tool Import 결과 정보")
    private ToolsDetail data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
    
    /**
     * Tools Import 결과 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tools Import 결과 정보")
    public static class ToolsDetail {
        
        @JsonProperty("id")
        @Schema(description = "Tool ID", example = "d872360b-1609-4951-b34a-06ca82c54922")
        private String id;
        
        @JsonProperty("name")
        @Schema(description = "Tool 이름", example = "234234")
        private String name;
        
        @JsonProperty("display_name")
        @Schema(description = "Tool 표시 이름", example = "234234")
        private String displayName;
        
        @JsonProperty("description")
        @Schema(description = "Tool 설명", example = "2434234")
        private String description;
        
        @JsonProperty("tool_type")
        @Schema(description = "Tool 타입", example = "custom_api")
        private String toolType;
        
        @JsonProperty("code")
        @Schema(description = "Tool 코드")
        private String code;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
        private String projectId;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private LocalDateTime updatedAt;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자 ID", example = "1ee5f990-1428-4d21-aa64-98956c5993e2")
        private String createdBy;
        
        @JsonProperty("updated_by")
        @Schema(description = "수정자 ID", example = "1ee5f990-1428-4d21-aa64-98956c5993e2")
        private String updatedBy;

        @JsonProperty("method")
        @Schema(description = "메서드 (custom_api 타입에만 존재)", example = "GET")
        private String method;

        @JsonProperty("server_url")
        @Schema(description = "서버 URL (custom_api 타입에만 존재)", example = "234234234")
        private String serverUrl;
        
        @JsonProperty("api_param")
        @Schema(description = "API 파라미터 (custom_api 타입에만 존재)")
        private Object apiParam;
        
        @JsonProperty("input_keys")
        @Schema(description = "입력 키 목록")
        private List<ToolResponse.InputKey> inputKeys;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<String> tags;
    }
}

