package com.skax.aiplatform.client.lablup.api.dto.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup 엔드포인트 조회 요청 DTO
 * 
 * <p>
 * Backend.AI 엔드포인트 정보를 조회하기 위한 GraphQL 쿼리 요청 구조입니다.
 * endpoint 쿼리를 통해 특정 엔드포인트의 상세 정보와 라우팅 정보를 확인할 수 있습니다.
 * </p>
 * 
 * <h3>GraphQL 쿼리 예시:</h3>
 * 
 * <pre>
 * query($endpoint_id: UUID!) {
 *     endpoint(endpoint_id: $endpoint_id) {
 *         endpoint_id
 *         name
 *         replicas
 *         status
 *         image_object {registry project base_image_name tag name}
 *         model_definition_path
 *         url
 *         open_to_public
 *         created_user
 *         created_at
 *         runtime_variant {name}
 *         routings {routing_id session status traffic_ratio}
 *     }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-01-27
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lablup 엔드포인트 조회 요청 정보 (GraphQL)", example = """
        {
          "query": "query($endpoint_id: UUID!) { endpoint(endpoint_id: $endpoint_id) { endpoint_id name replicas status image_object {registry project base_image_name tag name} model_definition_path url open_to_public created_user created_at runtime_variant {name} routings {routing_id session status traffic_ratio} } }",
          "variables": {
            "endpoint_id": "a83e9259-d642-4158-ac4d-06ffc5095017"
          }
        }
        """)
public class GetEndpointRequest {

    /**
     * GraphQL 쿼리 문자열
     * 
     * <p>
     * endpoint 쿼리를 포함하는 GraphQL 쿼리문입니다.
     * 엔드포인트의 상세 정보와 라우팅 정보를 조회합니다.
     * </p>
     * 
     * @apiNote 쿼리에는 endpoint_id, name, replicas, status, image_object, url,
     *          routings 등의 필드가 포함됩니다.
     */
    @NotBlank(message = "GraphQL 쿼리는 필수입니다")
    @Schema(description = "엔드포인트 조회를 위한 GraphQL 쿼리", required = true, example = "query($endpoint_id: UUID!) { endpoint(endpoint_id: $endpoint_id) { endpoint_id name replicas status image_object {registry project base_image_name tag name} model_definition_path url open_to_public created_user created_at runtime_variant {name} routings {routing_id session status traffic_ratio} } }")
    private String query;

    /**
     * GraphQL 쿼리 변수
     * 
     * <p>
     * GraphQL 쿼리에서 사용되는 변수들의 키-값 쌍입니다.
     * endpoint_id를 필수로 포함해야 합니다.
     * </p>
     * 
     * <h3>주요 변수:</h3>
     * <ul>
     * <li><strong>endpoint_id</strong>: 조회할 엔드포인트 ID (UUID) (필수)</li>
     * </ul>
     * 
     * @implNote 변수는 쿼리의 파라미터와 정확히 일치해야 합니다.
     */
    @Schema(description = "GraphQL 쿼리 변수 (키-값 쌍)", example = """
            {
              "endpoint_id": "a83e9259-d642-4158-ac4d-06ffc5095017"
            }
            """)
    private Map<String, Object> variables;
}
