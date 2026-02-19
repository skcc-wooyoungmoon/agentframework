package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 페이지네이션 링크 정보 DTO
 * 
 * <p>페이지네이션에서 사용되는 개별 링크 정보를 나타냅니다.
 * 이전/다음 페이지, 첫 페이지/마지막 페이지 등의 링크 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>url</strong>: 페이지 URL</li>
 *   <li><strong>label</strong>: 페이지 레이블 (예: "이전", "다음", "1", "2")</li>
 *   <li><strong>active</strong>: 현재 활성 페이지 여부</li>
 *   <li><strong>page</strong>: 페이지 번호</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see Pagination 페이지네이션 메인 DTO
 */
@Deprecated // 공통 PaginationLink(com.skax.aiplatform.client.sktai.common.dto.PaginationLink) 사용 권장
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API 페이지네이션 링크 정보 (deprecated: 공통 PaginationLink 사용 권장)",
    example = """
        {
          "url": "https://api.example.com/servings?page=2",
          "label": "2",
          "active": true,
          "page": 2
        }
        """
)
public class PaginationLinks {
    
    /**
     * 페이지 URL
     * 
     * <p>해당 페이지로 이동할 수 있는 전체 URL입니다.
     * null인 경우 해당 페이지가 존재하지 않음을 의미합니다.</p>
     */
    @JsonProperty("url")
    @Schema(
        description = "페이지 URL (null인 경우 해당 페이지 없음)",
        example = "https://api.example.com/servings?page=2",
        maxLength = 255
    )
    private String url;
    
    /**
     * 페이지 레이블
     * 
     * <p>페이지네이션 UI에서 표시되는 레이블입니다.
     * 페이지 번호 또는 "이전", "다음" 등의 텍스트가 될 수 있습니다.</p>
     */
    @JsonProperty("label")
    @Schema(
        description = "페이지 레이블 (페이지 번호 또는 이전/다음 등의 텍스트)",
        example = "2",
        required = true
    )
    private String label;
    
    /**
     * 현재 활성 페이지 여부
     * 
     * <p>현재 사용자가 보고 있는 페이지인지를 나타냅니다.</p>
     */
    @JsonProperty("active")
    @Schema(
        description = "현재 활성 페이지 여부",
        example = "true",
        required = true
    )
    private Boolean active;
    
    /**
     * 페이지 번호
     * 
     * <p>실제 페이지 번호입니다. null인 경우 페이지 번호가 없는 링크임을 의미합니다.</p>
     */
    @JsonProperty("page")
    @Schema(
        description = "페이지 번호 (null인 경우 페이지 번호 없는 링크)",
        example = "2"
    )
    private Integer page;
}