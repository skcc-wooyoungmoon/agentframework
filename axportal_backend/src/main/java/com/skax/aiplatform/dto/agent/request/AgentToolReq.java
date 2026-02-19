package com.skax.aiplatform.dto.agent.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent Tools 생성 요청 DTO
 * 
 * <p>Agent Tools 생성을 위해 클라이언트로부터 받는 요청 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Tools 생성 요청")
public class AgentToolReq {

    @NotBlank(message = "Agent Tool 이름은 필수입니다.")
    @Schema(description = "Tool의 고유한 이름 (영문자, 숫자, 언더스코어만 허용)", example = "WikiSearch")
    private String name;
    
    @Schema(description = "Tool의 표시 이름", example = "Wikipedia 검색기")
    private String displayName;
    
    @Schema(description = "Tool에 대한 상세한 설명 (Agent가 Tool 선택 시 참고)", example = "Wikipedia에서 정보를 검색하는 도구")
    private String description;
    
    @Schema(description = "프로젝트 ID")
    private String projectId;
    
    @NotBlank(message = "Agent Tool 타입은 필수입니다.")
    @Schema(description = "Tool 타입 (custom_api: API 호출, custom_code: 직접 코드 작성)", example = "custom_api")
    private String toolType;
    
    @Schema(description = "Tool 실행 코드 (custom_code 타입: 직접 작성, custom_api 타입: 자동 생성)", example = "def search_wiki(query): # API 호출 코드")
    private String code;
    
    @Schema(description = "API 호출 대상 서버 URL (custom_api 타입에서 사용)", example = "https://ko.wikipedia.org/w/api.php")
    private String serverUrl;
    
    @Schema(description = "HTTP 메서드 (custom_api 타입에서 사용)", example = "GET")
    private String method;
    
    @Schema(description = "API 호출 파라미터 (객체 형태, header/static_params/dynamic_params/static_body/dynamic_body 포함)")
    private Object apiParam;

    @Schema(description = "태그 목록")
    private List<String> tags;
}
