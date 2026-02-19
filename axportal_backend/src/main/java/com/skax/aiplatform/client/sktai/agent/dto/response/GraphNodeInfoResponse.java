package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Graph 노드 정보 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 사용 가능한 Graph 노드들의 정보를 나타내는 응답 데이터 구조입니다.
 * Graph 설계 시 참고할 수 있는 노드 타입, 속성, 연결 방법 등의 정보를 제공합니다.</p>
 * 
 * <h3>노드 정보 포함 항목:</h3>
 * <ul>
 *   <li><strong>노드 타입</strong>: 입력, 출력, 처리, 조건 등</li>
 *   <li><strong>노드 속성</strong>: 설정 가능한 파라미터와 옵션</li>
 *   <li><strong>연결 규칙</strong>: 다른 노드와의 연결 제약사항</li>
 *   <li><strong>사용 예시</strong>: 노드 활용 방법과 샘플 설정</li>
 * </ul>
 * 
 * <h3>노드 카테고리:</h3>
 * <ul>
 *   <li><strong>Input/Output</strong>: 데이터 입출력 노드</li>
 *   <li><strong>Processing</strong>: 데이터 처리 및 변환 노드</li>
 *   <li><strong>Decision</strong>: 조건 분기 및 라우팅 노드</li>
 *   <li><strong>Integration</strong>: 외부 시스템 연동 노드</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphNodeInfoResponse nodeInfo = graphClient.getNodeInfo();
 * List&lt;NodeType&gt; availableNodes = nodeInfo.getNodeTypes();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 노드 정보 응답",
    example = """
        {
          "node_types": [
            {
              "type_id": "input_text",
              "name": "Text Input",
              "category": "input",
              "description": "텍스트 입력을 받는 노드",
              "parameters": [
                {
                  "name": "variable_name",
                  "type": "string",
                  "required": true,
                  "description": "입력 변수명"
                }
              ]
            }
          ],
          "categories": ["input", "output", "processing", "decision", "integration"]
        }
        """
)
public class GraphNodeInfoResponse {
    
    /**
     * 노드 타입 목록
     * 
     * <p>사용 가능한 모든 노드 타입들의 상세 정보입니다.</p>
     */
    @JsonProperty("node_types")
    @Schema(
        description = "Graph에서 사용 가능한 노드 타입 목록", 
        example = """
            [
              {
                "type_id": "input_text",
                "name": "Text Input",
                "category": "input",
                "description": "텍스트 입력을 받는 노드"
              }
            ]
            """
    )
    private List<NodeType> nodeTypes;
    
    /**
     * 노드 카테고리 목록
     * 
     * <p>노드들을 분류하는 카테고리 목록입니다.</p>
     */
    @JsonProperty("categories")
    @Schema(
        description = "노드 카테고리 목록", 
        example = "[\"input\", \"output\", \"processing\", \"decision\", \"integration\"]"
    )
    private List<String> categories;
    
    /**
     * 연결 규칙
     * 
     * <p>노드 간 연결 시 적용되는 규칙과 제약사항입니다.</p>
     */
    @JsonProperty("connection_rules")
    @Schema(
        description = "노드 간 연결 규칙", 
        example = """
            {
              "max_input_connections": 5,
              "max_output_connections": 10,
              "forbidden_connections": [
                {"from": "output", "to": "input"}
              ]
            }
            """
    )
    private Object connectionRules;
    
    /**
     * 개별 노드 타입 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 노드 타입 정보")
    public static class NodeType {
        
        /**
         * 노드 타입 ID
         */
        @JsonProperty("type_id")
        @Schema(description = "노드 타입 고유 식별자", example = "input_text")
        private String typeId;
        
        /**
         * 노드 이름
         */
        @JsonProperty("name")
        @Schema(description = "노드 표시명", example = "Text Input")
        private String name;
        
        /**
         * 노드 카테고리
         */
        @JsonProperty("category")
        @Schema(description = "노드 카테고리", example = "input")
        private String category;
        
        /**
         * 노드 설명
         */
        @JsonProperty("description")
        @Schema(description = "노드 기능 설명", example = "텍스트 입력을 받는 노드")
        private String description;
        
        /**
         * 노드 파라미터
         */
        @JsonProperty("parameters")
        @Schema(description = "노드 설정 파라미터 목록")
        private List<NodeParameter> parameters;
        
        /**
         * 입력 포트
         */
        @JsonProperty("input_ports")
        @Schema(description = "노드 입력 포트 목록")
        private List<String> inputPorts;
        
        /**
         * 출력 포트
         */
        @JsonProperty("output_ports")
        @Schema(description = "노드 출력 포트 목록")
        private List<String> outputPorts;
    }
    
    /**
     * 노드 파라미터 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "노드 파라미터 정보")
    public static class NodeParameter {
        
        /**
         * 파라미터 이름
         */
        @JsonProperty("name")
        @Schema(description = "파라미터 이름", example = "variable_name")
        private String name;
        
        /**
         * 파라미터 타입
         */
        @JsonProperty("type")
        @Schema(description = "파라미터 데이터 타입", example = "string")
        private String type;
        
        /**
         * 필수 여부
         */
        @JsonProperty("required")
        @Schema(description = "필수 파라미터 여부", example = "true")
        private Boolean required;
        
        /**
         * 파라미터 설명
         */
        @JsonProperty("description")
        @Schema(description = "파라미터 설명", example = "입력 변수명")
        private String description;
        
        /**
         * 기본값
         */
        @JsonProperty("default_value")
        @Schema(description = "파라미터 기본값", example = "user_input")
        private Object defaultValue;
        
        /**
         * 허용 값 목록
         */
        @JsonProperty("allowed_values")
        @Schema(description = "허용되는 값 목록", example = "[\"text\", \"number\", \"boolean\"]")
        private List<String> allowedValues;
    }
}
