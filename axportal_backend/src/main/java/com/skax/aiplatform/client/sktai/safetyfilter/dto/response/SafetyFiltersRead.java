package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Safety Filter 목록 조회 응답 DTO
 * 
 * <p>SKTAI Safety Filter 시스템에서 안전 필터 목록을 조회한 결과를 담는 응답 데이터 구조입니다.
 * 페이지네이션 정보와 함께 Safety Filter 목록을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>data</strong>: Safety Filter 목록 데이터</li>
 *   <li><strong>payload</strong>: 페이지네이션 정보</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Safety Filter 관리 화면의 목록 표시</li>
 *   <li>페이지네이션된 Safety Filter 브라우징</li>
 *   <li>검색 및 필터링된 Safety Filter 조회</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SafetyFilterRead Safety Filter 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Safety Filter 목록 조회 응답 정보",
    example = """
        {
          "data": [
            {
              "stopword": "inappropriate_content",
              "label": "unsafe_user_defined",
              "category": "",
              "except_sources": "",
              "valid_tags": "ALL",
              "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
              "id": "123e4567-e89b-12d3-a456-426614174000",
              "created_at": "2025-08-15T10:30:00Z",
              "updated_at": "2025-08-15T10:30:00Z"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "first_page_url": "http://example.com?page=1",
              "from_": 1,
              "last_page": 1,
              "links": [],
              "items_per_page": 10,
              "to": 1,
              "total": 1
            }
          }
        }
        """
)
public class SafetyFiltersRead {
    
    /**
     * Safety Filter 목록 데이터
     * 
     * <p>조회된 Safety Filter들의 상세 정보 배열입니다.
     * 각 항목은 SafetyFilterRead와 동일한 구조를 가집니다.</p>
     * 
     * @apiNote 빈 배열일 수 있으며, 페이지네이션에 따라 제한된 수의 항목만 포함됩니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "Safety Filter 목록 데이터",
        example = """
            [
              {
                "stopword": "inappropriate_content",
                "label": "unsafe_user_defined",
                "id": "123e4567-e89b-12d3-a456-426614174000"
              }
            ]
            """
    )
    private List<SafetyFilterRead> data;
    
    /**
     * 페이지네이션 정보
     * 
     * <p>목록 조회 결과의 페이지네이션 관련 메타데이터입니다.
     * 현재 페이지, 전체 페이지 수, 항목 수 등의 정보를 포함합니다.</p>
     * 
     * @apiNote null일 수 있으며, 페이지네이션이 적용되지 않은 경우 생략될 수 있습니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이지네이션 정보",
        example = """
            {
              "pagination": {
                "page": 1,
                "total": 1
              }
            }
            """
    )
    private Payload payload;
}
