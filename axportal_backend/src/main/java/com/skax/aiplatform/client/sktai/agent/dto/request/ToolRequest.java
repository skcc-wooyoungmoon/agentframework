package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Tool 생성/수정 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Tool을 생성하거나 수정할 때 사용하는 요청 데이터 구조입니다.
 * custom_code와 custom_api 두 가지 타입의 Tool을 지원합니다.</p>
 * 
 * <h3>Tool 타입별 특징:</h3>
 * <ul>
 *   <li><strong>custom_code</strong>: 직접 작성한 코드로 실행되는 Tool</li>
 *   <li><strong>custom_api</strong>: 외부 API 호출을 통해 실행되는 Tool</li>
 * </ul>
 * 
 * <h3>Custom API Tool 파라미터 구조:</h3>
 * <pre>
 * {
 *   "header": {
 *     "auth_key": "your_api_key"
 *   },
 *   "static_params": {
 *     "action": "query",
 *     "format": "json",
 *     "list": "search"
 *   },
 *   "dynamic_params": {
 *     "query": "str"
 *   }
 * }
 * </pre>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // Custom API Tool 생성
 * ToolRequest request = ToolRequest.builder()
 *     .name("WikiSearch")
 *     .description("Wikipedia 검색 도구")
 *     .toolType("custom_api")
 *     .serverUrl("https://ko.wikipedia.org/w/api.php")
 *     .method("GET")
 *     .apiParam("{\"header\":{\"auth_key\":\"key123\"},\"static_params\":{\"action\":\"query\",\"format\":\"json\",\"list\":\"search\"},\"dynamic_params\":{\"query\":\"str\"}}")
 *     .build();
 * 
 * // Custom Code Tool 생성
 * ToolRequest codeRequest = ToolRequest.builder()
 *     .name("Calculator")
 *     .description("간단한 계산기")
 *     .toolType("custom_code")
 *     .code("def calculate(expression): return eval(expression)")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Tool 생성/수정 요청 정보",
    example = """
        {
          "name": "WikiSearch",
          "description": "Wikipedia 검색 도구",
          "tool_type": "custom_api",
          "server_url": "https://ko.wikipedia.org/w/api.php",
          "method": "GET",
          "api_param": "{\\"header\\":{\\"auth_key\\":\\"key123\\"},\\"static_params\\":{\\"action\\":\\"query\\",\\"format\\":\\"json\\",\\"list\\":\\"search\\"},\\"dynamic_params\\":{\\"query\\":\\"str\\"}}"
        }
        """
)
public class ToolRequest {
    
    /**
     * Tool 이름
     * 
     * <p>Tool의 고유한 이름입니다. 중복된 이름은 설정할 수 없습니다.
     * Tool의 이름과 Tool에 사용된 함수명은 같아야 합니다.</p>
     * 
     * @apiNote 영문자, 숫자, 언더스코어만 사용 가능하며, 영문자로 시작해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Tool의 고유한 이름 (영문자, 숫자, 언더스코어만 허용)",
        example = "WikiSearch",
        required = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9_]*$",
        minLength = 1,
        maxLength = 100
    )
    private String name;
    
    @JsonProperty("display_name")
    @Schema(
        description = "Tool의 표시 이름",
        example = "Wikipedia 검색기",
        maxLength = 100
    )
    private String displayName;
    
    /**
     * Tool 설명
     * 
     * <p>Tool에 대한 상세한 설명으로, StructuredTool의 Description으로 자동 설정됩니다.
     * Agent가 Tool을 선택할 때 참고하는 중요한 정보입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Tool에 대한 상세한 설명 (Agent가 Tool 선택 시 참고)",
        example = "Wikipedia에서 정보를 검색하는 도구",
        required = true,
        maxLength = 500
    )
    private String description;
    
    @JsonProperty("project_id")
    @Schema(
        description = "프로젝트 ID",
        example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
    )
    private String projectId;
    
    /**
     * Tool 타입
     * 
     * <p>Tool의 종류를 설정합니다.
     * API 호출을 위한 custom_api 타입과 직접 코드를 작성하여 사용하는 custom_code 타입이 있습니다.</p>
     */
    @JsonProperty("tool_type")
    @Schema(
        description = "Tool 타입 (custom_api: API 호출, custom_code: 직접 코드 작성)",
        example = "custom_api",
        required = true,
        allowableValues = {"custom_api", "custom_code"}
    )
    private String toolType;
    
    /**
     * Tool 실행 코드
     * 
     * <p>Tool을 실행하는데 사용되는 코드입니다.
     * custom_code 타입의 경우 직접 작성하며, custom_api 타입의 경우 필수값만 입력하면 자동으로 생성됩니다.</p>
     */
    @JsonProperty("code")
    @Schema(
        description = "Tool 실행 코드 (custom_code 타입: 직접 작성, custom_api 타입: 자동 생성)",
        example = "def search_wiki(query): # API 호출 코드"
    )
    private String code;
    
    /**
     * 서버 URL
     * 
     * <p>custom_api 타입의 Tool에서 호출에 사용되는 서버 주소입니다.
     * HTTP/HTTPS 프로토콜을 지원합니다.</p>
     */
    @JsonProperty("server_url")
    @Schema(
        description = "API 호출 대상 서버 URL (custom_api 타입에서 사용)",
        example = "https://ko.wikipedia.org/w/api.php",
        format = "uri"
    )
    private String serverUrl;
    
    /**
     * HTTP 메서드
     * 
     * <p>custom_api 타입의 Tool에서 호출에 사용되는 HTTP 메서드입니다.
     * 현재는 POST와 GET을 지원합니다.</p>
     */
    @JsonProperty("method")
    @Schema(
        description = "HTTP 메서드 (custom_api 타입에서 사용)",
        example = "GET",
        allowableValues = {"GET", "POST"}
    )
    private String method;
    
    /**
     * API 파라미터
     * 
     * <p>custom_api 타입의 Tool에서 호출에 사용되는 파라미터입니다.
     * header, static_params, dynamic_params, static_body, dynamic_body의 다섯 가지 영역으로 이루어져 있습니다.
     * header 영역은 실제 호출 시 header로 사용되며 나머지는 parameter로 사용됩니다.</p>
     * 
     * @implNote 객체 형태로 전달되며, 다음과 같은 구조를 가집니다:
     * <ul>
     *   <li>header: HTTP 헤더 정보</li>
     *   <li>static_params: 고정 쿼리 파라미터</li>
     *   <li>dynamic_params: 동적 쿼리 파라미터</li>
     *   <li>static_body: 고정 요청 본문</li>
     *   <li>dynamic_body: 동적 요청 본문</li>
     * </ul>
     */
    @JsonProperty("api_param")
    @Schema(
        description = "API 호출 파라미터 (객체 형태, header/static_params/dynamic_params/static_body/dynamic_body 포함)",
        example = """
            {
              "header": {
                "Content-Type": "application/json",
                "Authorization": "Bearer token"
              },
              "static_params": {
                "action": "query",
                "format": "json"
              },
              "dynamic_params": {
                "query": "str"
              },
              "static_body": {},
              "dynamic_body": {}
            }
            """
    )
    private Object apiParam;

    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<String> tags;
}
