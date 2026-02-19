package com.skax.aiplatform.client.lablup.api.dto.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup 스케일링 그룹 조회 요청 DTO
 * 
 * <p>Backend.AI 리소스 그룹별 자원 할당량을 조회하기 위한 GraphQL 쿼리 요청 구조입니다.
 * scaling_groups 쿼리를 통해 리소스 그룹의 상태별 자원 현황을 확인할 수 있습니다.</p>
 * 
 * <h3>GraphQL 쿼리 예시:</h3>
 * <pre>
 * query($is_active: Boolean) {
 *     scaling_groups(is_active: $is_active) {
 *         name
 *         description
 *         is_active
 *         created_at
 *         driver
 *         driver_opts
 *         scheduler
 *         scheduler_opts
 *         use_host_network
 *         wsproxy_addr
 *         wsproxy_api_token
 *         agent_total_resource_slots_by_status
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
    description = "Lablup 스케일링 그룹 조회 요청 정보 (GraphQL)",
    example = """
        {
          "query": "query($is_active: Boolean) { scaling_groups(is_active: $is_active) { name description is_active created_at driver driver_opts scheduler scheduler_opts use_host_network wsproxy_addr wsproxy_api_token agent_total_resource_slots_by_status } }",
          "variables": {
            "is_active": true
          }
        }
        """
)
public class GetScalingGroupsRequest {
    
    /**
     * GraphQL 쿼리 문자열
     * 
     * <p>scaling_groups 쿼리를 포함하는 GraphQL 쿼리문입니다.
     * 리소스 그룹의 상세 정보와 상태별 자원 슬롯 정보를 조회합니다.</p>
     * 
     * @apiNote 쿼리에는 agent_total_resource_slots_by_status 필드가 포함되어야 상태별 자원 현황을 확인할 수 있습니다.
     */
    @NotBlank(message = "GraphQL 쿼리는 필수입니다")
    @Schema(
        description = "스케일링 그룹 조회를 위한 GraphQL 쿼리", 
        required = true,
        example = "query($is_active: Boolean) { scaling_groups(is_active: $is_active) { name description is_active created_at driver driver_opts scheduler scheduler_opts use_host_network wsproxy_addr wsproxy_api_token agent_total_resource_slots_by_status } }"
    )
    private String query;
    
    /**
     * GraphQL 쿼리 변수
     * 
     * <p>GraphQL 쿼리에서 사용되는 변수들의 키-값 쌍입니다.
     * 주로 is_active 변수를 통해 활성화된 스케일링 그룹만 조회하거나 전체를 조회할 수 있습니다.</p>
     * 
     * @implNote 변수는 쿼리의 파라미터와 정확히 일치해야 합니다.
     */
    @Schema(
        description = "GraphQL 쿼리 변수 (키-값 쌍)",
        example = """
            {
              "is_active": true
            }
            """
    )
    private Map<String, Object> variables;
}