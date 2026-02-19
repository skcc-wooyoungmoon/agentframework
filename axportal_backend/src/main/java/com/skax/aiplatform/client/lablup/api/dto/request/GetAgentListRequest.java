package com.skax.aiplatform.client.lablup.api.dto.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup 에이전트 목록 조회 요청 DTO
 * 
 * <p>Backend.AI 노드별 자원 할당량을 조회하기 위한 GraphQL 쿼리 요청 구조입니다.
 * agent_list 쿼리를 통해 개별 노드(에이전트)의 자원 상태를 확인할 수 있습니다.</p>
 * 
 * <h3>GraphQL 쿼리 예시:</h3>
 * <pre>
 * query(
 *     $limit: Int!, $offset: Int!, $filter: String, $order: String,
 *     $status: String, $scaling_group: String
 * ) {
 *     agent_list(
 *         limit: $limit, offset: $offset, filter: $filter, order: $order,
 *         status: $status, scaling_group: $scaling_group
 *     ) {
 *         items {
 *             id addr status scaling_group schedulable
 *             available_slots occupied_slots
 *         }
 *         total_count
 *     }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Lablup 에이전트 목록 조회 요청 정보 (GraphQL)",
    example = """
        {
          "query": "query($limit: Int!, $offset: Int!, $filter: String, $order: String, $status: String, $scaling_group: String) { agent_list(limit: $limit, offset: $offset, filter: $filter, order: $order, status: $status, scaling_group: $scaling_group) { items { id addr status scaling_group schedulable available_slots occupied_slots } total_count } }",
          "variables": {
            "limit": 50,
            "offset": 0,
            "filter": "schedulable == true",
            "order": "id",
            "status": "ALIVE",
            "scaling_group": "default"
          }
        }
        """
)
public class GetAgentListRequest {
    
    /**
     * GraphQL 쿼리 문자열
     * 
     * <p>agent_list 쿼리를 포함하는 GraphQL 쿼리문입니다.
     * 에이전트의 상세 정보와 자원 슬롯 정보(available_slots, occupied_slots)를 조회합니다.</p>
     * 
     * @apiNote 쿼리에는 available_slots와 occupied_slots 필드가 포함되어야 자원 현황을 확인할 수 있습니다.
     */
    @NotBlank(message = "GraphQL 쿼리는 필수입니다")
    @Schema(
        description = "에이전트 목록 조회를 위한 GraphQL 쿼리", 
        required = true,
        example = "query($limit: Int!, $offset: Int!, $filter: String, $order: String, $status: String, $scaling_group: String) { agent_list(limit: $limit, offset: $offset, filter: $filter, order: $order, status: $status, scaling_group: $scaling_group) { items { id addr status scaling_group schedulable available_slots occupied_slots } total_count } }"
    )
    private String query;
    
    /**
     * GraphQL 쿼리 변수
     * 
     * <p>GraphQL 쿼리에서 사용되는 변수들의 키-값 쌍입니다.
     * 페이징, 필터링, 정렬 등의 조건을 설정할 수 있습니다.</p>
     * 
     * <h3>주요 변수:</h3>
     * <ul>
     *   <li><strong>limit</strong>: 조회할 최대 개수</li>
     *   <li><strong>offset</strong>: 페이징 오프셋</li>
     *   <li><strong>filter</strong>: 필터 조건 (예: "schedulable == true")</li>
     *   <li><strong>order</strong>: 정렬 기준 (예: "id")</li>
     *   <li><strong>status</strong>: 에이전트 상태 (예: "ALIVE")</li>
     *   <li><strong>scaling_group</strong>: 스케일링 그룹 (예: "default")</li>
     * </ul>
     * 
     * @implNote 변수는 쿼리의 파라미터와 정확히 일치해야 합니다.
     */
    @Schema(
        description = "GraphQL 쿼리 변수 (키-값 쌍)",
        example = """
            {
              "limit": 50,
              "offset": 0,
              "filter": "schedulable == true",
              "order": "id",
              "status": "ALIVE",
              "scaling_group": "default"
            }
            """
    )
    private Map<String, Object> variables;
}