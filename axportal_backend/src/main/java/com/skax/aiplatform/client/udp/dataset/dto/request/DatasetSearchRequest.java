package com.skax.aiplatform.client.udp.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 데이터셋 검색 요청 DTO
 * 
 * <p>UDP 데이터셋 카드 검색을 위한 요청 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 검색 요청 정보")
public class DatasetSearchRequest {

    /**
     * 검색어
     */
    @JsonProperty("search_word")
    @Schema(description = "[선택]검색을 위한 키워드", example = "규정")
    private String searchWord;

    /**
     * 데이터셋 카드 ID
     */
    @JsonProperty("dataset_card_id")
    @Schema(description = "[선택]데이터셋카드ID, 유니크한 serial값", example = "12345")
    private String datasetCardId;

    /**
     * 데이터셋 코드
     */
    @JsonProperty("dataset_cd")
    @Schema(description = "[선택]특정 dataset_코드만 필터링", example = "rgl")
    private String datasetCd;

    /**
     * 원천시스템 코드
     */
    @JsonProperty("origin_system_cd")
    @Schema(description = "[선택] 특정 원천시스템 코드만 필터링 - value1,value2,value3 형식으로 구분자(\",\") 사용", example = "sb,basic")
    private List<String> originSystemCd;

    /**
     * 데이터셋 카드 유형
     */
    @JsonProperty("dataset_card_type")
    @Schema(description = "[선택]DATASET/TRAINSET/VALIDSET, 입력값이 없으면 전체 조회", example = "DATASET")
    private String datasetCardType;

    /**
     * 데이터셋 카드 생성일 조회 시작일
     */
    @JsonProperty("dataset_card_create_start")
    @Schema(description = "[선택]YYYYMMDD,생성일/종료일은 PAIR로 입력해야 함", example = "20241001")
    private String datasetCardCreateStart;

    /**
     * 데이터셋 카드 생성일 조회 종료일
     */
    @JsonProperty("dataset_card_create_end")
    @Schema(description = "[선택]YYYYMMDD,생성일/종료일은 PAIR로 입력해야 함", example = "20241031")
    private String datasetCardCreateEnd;

    /**
     * 페이지당 표시수
     */
    @JsonProperty("count_per_page")
    @Schema(description = "[선택]최대 100", example = "20")
    private Long countPerPage;

    /**
     * 페이지
     */
    @JsonProperty("page")
    @Schema(description = "[선택]조회 페이지, 입력값이 없는 경우 1로 설정", example = "1")
    private Long page;
}