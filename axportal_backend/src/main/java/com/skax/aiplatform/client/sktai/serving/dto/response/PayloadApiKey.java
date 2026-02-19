package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 키 페이로드 DTO
 * 
 * <p>API 키 목록 조회 응답에서 사용되는 페이로드 정보입니다.
 * 페이지네이션 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>pagination</strong>: 페이지네이션 정보</li>
 * </ul>
 *
 * @deprecated 공통 Payload(com.skax.aiplatform.client.sktai.common.dto.Payload) 사용 권장
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see ApiKeyRead API 키 목록 응답
 * @see Pagination 페이지네이션 정보
 */
@Deprecated // 공통 Payload(com.skax.aiplatform.client.sktai.common.dto.Payload) 사용 권장
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API 키 페이로드 정보",
    example = """
        {
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
        """
)
public class PayloadApiKey {
    
    /**
     * 페이지네이션 정보
     * 
     * <p>API 키 목록의 페이지네이션 메타데이터입니다.</p>
     */
    @JsonProperty("pagination")
    @Schema(
        description = "페이지네이션 정보",
        required = true
    )
    private Pagination pagination; // 공통 Pagination 사용
}