package com.skax.aiplatform.client.udp.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UDP 데이터셋 검색 응답 DTO
 * 
 * <p>UDP 시스템의 데이터셋 검색 결과를 담는 응답 데이터 구조입니다.
 * 페이징 정보와 함께 검색된 데이터셋 목록을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "데이터셋 검색 응답 정보")
public class DatasetSearchResponse {

    /**
     * 전체 데이터셋 개수
     */
    @JsonProperty("total_count")
    @Schema(description = "전체 데이터셋 개수", example = "1")
    private Long totalCount;

    /**
     * 현재 조회 페이지
     */
    @JsonProperty("page")
    @Schema(description = "현재 조회 페이지", example = "1")
    private Long page;

    /**
     * 검색 결과 목록
     * 
     * <p>검색 조건에 맞는 데이터셋 목록입니다.</p>
     */
    @JsonProperty("result_lists")
    @Schema(description = "검색된 데이터셋 목록")
    private List<DatasetCardInfo> resultLists;
}