package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메타데이터 필드
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메타데이터 필드")
public class DataCtlgMetadataField {

    /**
     * 필드 이름
     */
    @Schema(description = "필드 이름", example = "m_id")
    private String name;

    /**
     * 필드 타입
     */
    @Schema(description = "필드 타입", example = "SearchFieldDataType.String")
    private String type;

    /**
     * 검색 가능 여부
     */
    @Schema(description = "검색 가능 여부", example = "false")
    private Boolean searchable;

    /**
     * 필터 가능 여부
     */
    @Schema(description = "필터 가능 여부", example = "true")
    private Boolean filterable;

    /**
     * 정렬 가능 여부
     */
    @Schema(description = "정렬 가능 여부", example = "false")
    private Boolean sortable;

    /**
     * 패싯 가능 여부
     */
    @Schema(description = "패싯 가능 여부", example = "true")
    private Boolean facetable;

    /**
     * 검색 결과에 포함 여부
     */
    @Schema(description = "검색 결과에 포함 여부", example = "true")
    private Boolean retrievable;

}
