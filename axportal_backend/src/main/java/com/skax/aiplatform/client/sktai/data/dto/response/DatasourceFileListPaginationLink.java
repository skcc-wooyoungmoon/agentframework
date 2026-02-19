package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 데이터소스 파일 목록 페이징 링크 정보 DTO
 * 
 * <p>데이터소스 파일 목록 페이징 네비게이션을 위한 링크 정보를 포함하는 DTO입니다.
 * 이전/다음 페이지, 특정 페이지로의 링크 정보를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>링크 URL</strong>: 해당 페이지로 이동하는 URL</li>
 *   <li><strong>라벨</strong>: 사용자에게 표시되는 링크 텍스트</li>
 *   <li><strong>활성 상태</strong>: 현재 페이지 여부</li>
 *   <li><strong>페이지 번호</strong>: 해당 링크의 페이지 번호</li>
 * </ul>
 *
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Deprecated // 공통 PaginationLink(com.skax.aiplatform.client.sktai.common.dto.PaginationLink) 사용 권장
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 데이터소스 파일 목록 페이징 링크 정보 (deprecated: 공통 PaginationLink 사용 권장)",
    example = """
        {
          "url": "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10",
          "label": "1",
          "active": true,
          "page": 1
        }
        """
)
public class DatasourceFileListPaginationLink {
    
    /**
     * 링크 URL
     * 
     * <p>해당 페이지로 이동하는 URL입니다. null인 경우 해당 링크가 비활성화됨을 의미합니다.</p>
     */
    @JsonProperty("url")
    @Schema(
        description = "링크 URL",
        example = "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10"
    )
    private String url;
    
    /**
     * 링크 라벨
     * 
     * <p>사용자에게 표시되는 링크 텍스트입니다.</p>
     */
    @JsonProperty("label")
    @Schema(
        description = "링크 라벨",
        example = "1"
    )
    private String label;
    
    /**
     * 활성 상태
     * 
     * <p>현재 페이지인지 여부를 나타냅니다.</p>
     */
    @JsonProperty("active")
    @Schema(
        description = "활성 상태 (현재 페이지 여부)",
        example = "true"
    )
    private Boolean active;
    
    /**
     * 페이지 번호
     * 
     * <p>해당 링크가 가리키는 페이지 번호입니다. null인 경우 해당 링크가 비활성화됨을 의미합니다.</p>
     */
    @JsonProperty("page")
    @Schema(
        description = "페이지 번호",
        example = "1"
    )
    private Integer page;
}