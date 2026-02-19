package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphCreateResponse;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Graph 생성 요청 DTO
 * 
 * <p>새로운 Agent 그래프를 생성하기 위한 요청 데이터 구조입니다.
 * Agent Graph는 AI 워크플로우와 의사결정 트리를 시각적으로 구성하는 단위입니다.</p>
 * 
 * <h3>그래프 구성 요소:</h3>
 * <ul>
 *   <li><strong>노드(Nodes)</strong>: 처리 단계 또는 의사결정 포인트</li>
 *   <li><strong>엣지(Edges)</strong>: 노드 간의 연결과 데이터 흐름</li>
 *   <li><strong>메타데이터</strong>: 그래프 설명 및 실행 설정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphCreateRequest request = GraphCreateRequest.builder()
 *     .name("Customer Query Processing")
 *     .description("고객 문의 처리를 위한 워크플로우")
 *     .type("workflow")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see GraphCreateResponse Graph 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 생성 요청 정보",
    example = """
        {
          "name": "Customer Query Processing",
          "description": "고객 문의 처리를 위한 워크플로우",
          "type": "workflow",
          "nodes": [
            {
              "id": "start",
              "type": "input",
              "config": {"label": "사용자 입력"}
            },
            {
              "id": "process",
              "type": "llm",
              "config": {"model": "gpt-4"}
            }
          ],
          "edges": [
            {"from": "start", "to": "process", "condition": "always"}
          ]
        }
        """
)
public class GraphCreateRequest {
    
    /**
     * 그래프 이름
     * 
     * <p>Agent 그래프의 고유한 이름입니다.
     * 워크플로우의 목적을 명확히 나타내야 합니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "그래프 고유 이름",
        example = "Customer Query Processing",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String name;
    
    /**
     * 그래프 설명
     * 
     * <p>그래프의 목적, 기능, 사용 용도를 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "그래프 설명 (목적과 기능)",
        example = "고객 문의 처리를 위한 워크플로우",
        maxLength = 1000
    )
    private String description;
    
    /**
     * 그래프 타입
     * 
     * <p>그래프의 용도와 실행 방식을 나타내는 타입입니다.</p>
     */
    @JsonProperty("type")
    @Schema(
        description = "그래프 타입",
        example = "workflow",
        allowableValues = {"workflow", "decision_tree", "pipeline", "state_machine"}
    )
    private String type;
    
    /**
     * 그래프 카테고리
     * 
     * <p>그래프의 분류를 나타내는 카테고리입니다.</p>
     */
    @JsonProperty("category")
    @Schema(
        description = "그래프 카테고리",
        example = "customer_service",
        allowableValues = {"customer_service", "data_analysis", "content_generation", "automation", "other"}
    )
    private String category;
    
    /**
     * 그래프 노드 목록
     * 
     * <p>그래프를 구성하는 노드들의 정의입니다.
     * 각 노드는 처리 단계나 의사결정 포인트를 나타냅니다.</p>
     */
    @JsonProperty("nodes")
    @Schema(
        description = "그래프 노드 목록",
        example = """
            [
              {
                "id": "start",
                "type": "input",
                "config": {"label": "사용자 입력"}
              },
              {
                "id": "process",
                "type": "llm",
                "config": {"model": "gpt-4"}
              }
            ]
            """
    )
    private List<Object> nodes;
    
    /**
     * 그래프 엣지 목록
     * 
     * <p>노드 간의 연결과 데이터 흐름을 정의합니다.
     * 조건부 분기와 순차 실행을 제어할 수 있습니다.</p>
     */
    @JsonProperty("edges")
    @Schema(
        description = "그래프 엣지 목록",
        example = """
            [
              {"from": "start", "to": "process", "condition": "always"},
              {"from": "process", "to": "end", "condition": "success"}
            ]
            """
    )
    private List<Object> edges;
    
    /**
     * 그래프 설정
     * 
     * <p>그래프의 실행과 동작을 제어하는 설정 정보입니다.</p>
     */
    @JsonProperty("config")
    @Schema(
        description = "그래프 실행 설정 정보 (JSON 형태)",
        example = """
            {
              "timeout": 300,
              "retry_count": 3,
              "parallel_execution": false
            }
            """
    )
    private Object config;
    
    /**
     * 그래프 구조
     * 
     * <p>전체 그래프의 구조 정보를 포함하는 객체입니다.
     * SKT AI Platform API에서 필수로 요구하는 필드입니다.</p>
     */
    @JsonProperty("graph")
    @Schema(
        description = "그래프 구조 정보 (노드와 엣지를 포함한 전체 구조)",
        example = """
            {
              "nodes": [...],
              "edges": [...]
            }
            """
    )
    private Object graph;

    @JsonProperty("policy")
    @Schema(description = "정책 목록")
    private List<PolicyRequest> policy;

    /**
     * 템플릿 ID
     * 
     * <p>템플릿을 기반으로 그래프를 생성할 때 사용하는 템플릿 ID입니다.</p>
     */
    @JsonProperty("template_id")
    @Schema(
        description = "템플릿 ID (템플릿 기반 그래프 생성 시 사용)",
        example = "template-123"
    )
    private String templateId;
}
