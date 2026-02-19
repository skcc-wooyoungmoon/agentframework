package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 사용 이력 페이징 링크 DTO
 * 
 * <p>
 * SKTAI History API의 모델 사용 이력 조회 응답에서 사용되는 페이징 링크 정보입니다.
 * Laravel 스타일의 페이징 링크 구조를 따릅니다.
 * </p>
 * 
 * <h3>구조 특징:</h3>
 * <ul>
 * <li><strong>Laravel 호환</strong>: Laravel의 pagination links 구조와 동일</li>
 * <li><strong>활성 상태</strong>: 현재 페이지 표시를 위한 active 플래그</li>
 * <li><strong>라벨 정보</strong>: HTML 엔티티를 포함한 링크 라벨</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 페이징 링크 정보", example = """
        {
          "url": "/history/model/list?from_date=2025-09-01&to_date=2025-09-30&page=1&size=10",
          "label": "1",
          "active": true,
          "page": 1
        }
        """)
public class ModelHistoryLink {

    /**
     * 링크 URL
     */
    @JsonProperty("url")
    @Schema(description = "링크 URL", example = "/history/model/list?from_date=2025-09-01&to_date=2025-09-30&page=1&size=10")
    private String url;

    /**
     * 링크 라벨 (HTML 엔티티 포함 가능)
     */
    @JsonProperty("label")
    @Schema(description = "링크 라벨", example = "1")
    private String label;

    /**
     * 현재 활성 페이지 여부
     */
    @JsonProperty("active")
    @Schema(description = "현재 활성 페이지 여부", example = "true")
    private Boolean active;

    /**
     * 페이지 번호
     */
    @JsonProperty("page")
    @Schema(description = "페이지 번호", example = "1")
    private Integer page;
}

