package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 키 목록 조회 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 API 키 목록을 조회할 때 반환되는 응답 구조입니다.
 * 페이지네이션된 API 키 목록과 메타데이터를 포함합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>data</strong>: API 키 목록</li>
 *   <li><strong>payload</strong>: 페이지네이션 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ApiKeyRead response = apiKeyService.getApiKeys(1, 10, null, null, null);
 * List&lt;ApiKey&gt; apiKeys = response.getData();
 * Pagination pagination = response.getPayload().getPagination();
 * int totalCount = pagination.getTotal();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see ApiKey API 키 정보
 * @see PayloadApiKey 페이로드 정보
 * @see Pagination 페이지네이션 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API 키 목록 조회 응답",
    example = """
        {
          "data": [
            {
              "api_key": "sk-92a846d7f3014bb817bd57487fa00eec",
              "serving_id": ["srv-123e4567-e89b-12d3-a456-426614174000"],
              "created_at": "2024-10-02T10:30:00Z",
              "started_at": "2024-10-03",
              "expires_at": "2025-10-02",
              "tag": ["production"],
              "allowed_host": ["127.0.0.1"],
              "is_master": false,
              "project_id": "proj-123e4567-e89b-12d3-a456-426614174000",
              "is_active": true,
              "gateway_type": "model",
              "api_key_id": "key-123e4567-e89b-12d3-a456-426614174000"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "first_page_url": "https://api.example.com/apikeys?page=1",
              "from_": 1,
              "last_page": 3,
              "links": [],
              "next_page_url": "https://api.example.com/apikeys?page=2",
              "items_per_page": 10,
              "prev_page_url": null,
              "to": 10,
              "total": 25
            }
          }
        }
        """
)
public class ApiKeyRead {
    
    /**
     * API 키 목록
     * 
     * <p>조회된 API 키들의 목록입니다.
     * 페이지네이션 설정에 따라 일부 데이터만 포함될 수 있습니다.</p>
     */
    @JsonProperty("data")
    @Schema(
        description = "API 키 목록 (페이지네이션된 결과)",
        required = true
    )
    private List<ApiKey> data;
    
    /**
     * 페이로드 정보
     * 
     * <p>페이지네이션 메타데이터를 포함하는 페이로드 정보입니다.</p>
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이로드 정보 (페이지네이션 메타데이터 포함)",
        required = true
    )
    private PayloadApiKey payload;
}