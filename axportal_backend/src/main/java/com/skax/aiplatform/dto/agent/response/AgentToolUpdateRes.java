package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent Tools 수정 응답 DTO
 * 
 * <p>Agent Tools 수정 결과를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Tools 수정 응답")
public class AgentToolUpdateRes {

    @Schema(description = "Agent Tool ID", example = "cf71d95d-29c9-4131-ade4-4b88938a2a88")
    private String id;
    
    @Schema(description = "Agent Tool 이름", example = "testtesttest")
    private String name;
    
    @Schema(description = "Agent Tool 표시 이름", example = "🌎 Wiki 검색 도구")
    private String displayName;
    
    @Schema(description = "Agent Tool 설명", example = "testtesttest")
    private String description;
    
    @Schema(description = "Agent Tool 타입", example = "custom_api")
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
    
    @Schema(description = "입력 키 목록")
    private List<Object> inputKeys;
    
    @Schema(description = "서버 URL", example = "http://123.com/v1/getError")
    private String serverUrl;
    
    @Schema(description = "HTTP 메서드", example = "GET")
    private String method;
    
    @Schema(description = "API 파라미터", example = "{\"header\":{},\"static_params\":{},\"dynamic_params\":{}}")
    private Object apiParam;

    @Schema(description = "태그 목록")
    private List<String> tags;
}
