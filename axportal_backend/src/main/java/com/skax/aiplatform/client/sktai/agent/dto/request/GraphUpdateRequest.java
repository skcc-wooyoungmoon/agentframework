package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Graph 수정 요청 DTO
 * 
 * <p>기존 Agent 그래프의 정보를 수정하기 위한 요청 데이터 구조입니다.
 * 그래프 구조, 노드, 엣지, 설정 등을 업데이트할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 정보:</h3>
 * <ul>
 *   <li><strong>메타데이터</strong>: 이름, 설명, 카테고리</li>
 *   <li><strong>구조</strong>: 노드 및 엣지 구성</li>
 *   <li><strong>설정</strong>: 실행 옵션과 동작 제어</li>
 *   <li><strong>상태</strong>: 활성화/비활성화</li>
 * </ul>
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
    description = "SKTAI Agent Graph 수정 요청 정보",
    example = """
        {
          "name": "Enhanced Customer Query Processing",
          "description": "개선된 고객 문의 처리 워크플로우",
          "status": "active",
          "nodes": [
            {
              "id": "start",
              "type": "input",
              "config": {"label": "사용자 입력", "validation": true}
            }
          ]
        }
        """
)
public class GraphUpdateRequest {
    
    /**
     * 수정할 그래프 이름
     */
    @JsonProperty("name")
    @Schema(
        description = "수정할 그래프 이름",
        example = "Enhanced Customer Query Processing",
        minLength = 3,
        maxLength = 100
    )
    private String name;
    
    /**
     * 수정할 그래프 설명
     */
    @JsonProperty("description")
    @Schema(
        description = "수정할 그래프 설명",
        example = "개선된 고객 문의 처리 워크플로우",
        maxLength = 1000
    )
    private String description;
    
    /**
     * 수정할 그래프 타입
     */
    @JsonProperty("type")
    @Schema(
        description = "수정할 그래프 타입",
        example = "workflow",
        allowableValues = {"workflow", "decision_tree", "pipeline", "state_machine"}
    )
    private String type;
    
    /**
     * 수정할 그래프 카테고리
     */
    @JsonProperty("category")
    @Schema(
        description = "수정할 그래프 카테고리",
        example = "customer_service"
    )
    private String category;
    
    /**
     * 수정할 그래프 상태
     */
    @JsonProperty("status")
    @Schema(
        description = "그래프 상태",
        example = "active",
        allowableValues = {"active", "inactive", "draft", "archived"}
    )
    private String status;
    
    /**
     * 수정할 그래프 노드 목록
     */
    @JsonProperty("nodes")
    @Schema(
        description = "수정할 그래프 노드 목록",
        example = """
            [
              {
                "id": "start",
                "type": "input",
                "config": {"label": "사용자 입력", "validation": true}
              }
            ]
            """
    )
    private List<Object> nodes;
    
    /**
     * 수정할 그래프 엣지 목록
     */
    @JsonProperty("edges")
    @Schema(
        description = "수정할 그래프 엣지 목록",
        example = """
            [
              {"from": "start", "to": "process", "condition": "validated"}
            ]
            """
    )
    private List<Object> edges;
    
    /**
     * 수정할 그래프 설정
     */
    @JsonProperty("config")
    @Schema(
        description = "수정할 그래프 실행 설정 정보",
        example = """
            {
              "timeout": 600,
              "retry_count": 5,
              "parallel_execution": true
            }
            """
    )
    private Object config;
}
