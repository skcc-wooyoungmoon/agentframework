package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 다중 응답 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 목록 조회 요청에 대한 표준 응답 데이터 구조입니다.
 * 페이징된 데이터 목록과 페이징 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>data</strong>: 조회된 데이터 목록</li>
 *   <li><strong>payload</strong>: 페이징 정보</li>
 * </ul>
 * 
 * <h3>활용 방법:</h3>
 * <ul>
 *   <li>Vector DB 목록 조회</li>
 *   <li>Chunk Store 목록 조회</li>
 *   <li>Tool 목록 조회</li>
 *   <li>Custom Script 목록 조회</li>
 *   <li>Repository 목록 조회</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * MultiResponse response = vectorDbClient.getVectorDbs(1, 10, null, null, null);
 * List&lt;Object&gt; vectorDbs = response.getData();
 * Payload pageInfo = response.getPayload();
 * 
 * // 페이징 정보 확인
 * Integer totalItems = pageInfo.getPagination().getTotal();
 * Integer currentPage = pageInfo.getPagination().getPage();
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
    description = "SKTAI 다중 응답 정보 (페이징된 목록 데이터)",
    example = """
        {
          "data": [
            {
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "name": "My Vector DB",
              "type": "Milvus"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 100,
              "items_per_page": 10,
              "last_page": 10
            }
          }
        }
        """
)
public class MultiResponse {
    
    /**
     * 조회된 데이터 목록
     * 
     * <p>요청된 조건에 맞는 데이터 항목들의 목록입니다.
     * 데이터가 없는 경우 빈 배열이 반환됩니다.</p>
     * 
     * @implNote 실제 데이터 타입은 요청하는 API에 따라 달라집니다.
     * @apiNote Vector DB 목록의 경우 VectorDB 객체들의 배열이 반환됩니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "조회된 데이터 목록 (페이징 적용)",
        example = """
            [
              {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "name": "My Vector DB",
                "type": "Milvus",
                "created_at": "2025-08-15T10:30:00Z"
              }
            ]
            """
    )
    private List<Object> data;
    
    /**
     * 페이징 정보
     * 
     * <p>조회 결과의 페이징 관련 정보를 포함합니다.
     * 현재 페이지, 전체 항목 수, 페이지 크기 등의 정보가 제공됩니다.</p>
     * 
     * @apiNote null인 경우 페이징이 적용되지 않은 전체 목록을 의미합니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이징 정보",
        example = """
            {
              "pagination": {
                "page": 1,
                "total": 100,
                "items_per_page": 10,
                "last_page": 10,
                "from_": 1,
                "to": 10,
                "first_page_url": "/api/v1/knowledge/vectordbs?page=1",
                "next_page_url": "/api/v1/knowledge/vectordbs?page=2",
                "prev_page_url": null
              }
            }
            """
    )
    private Payload payload;
}
