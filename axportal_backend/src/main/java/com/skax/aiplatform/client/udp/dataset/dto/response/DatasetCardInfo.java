package com.skax.aiplatform.client.udp.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 카드 정보 DTO
 * 
 * <p>개별 데이터셋 카드의 상세 정보를 담는 데이터 구조입니다.</p>
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
@Schema(description = "데이터셋 카드 정보")
public class DatasetCardInfo {

    /**
     * 데이터셋 카드 ID
     */
    @JsonProperty("dataset_card_id")
    @Schema(description = "데이터셋 카드 ID", example = "19")
    private String datasetCardId;

    /**
     * 데이터셋 카드 이름
     */
    @JsonProperty("dataset_card_name")
    @Schema(description = "데이터셋 카드 이름", example = "규정")
    private String datasetCardName;

    /**
     * 데이터셋 코드
     */
    @JsonProperty("dataset_cd")
    @Schema(description = "데이터셋 코드", example = "rgl")
    private String datasetCd;

    /**
     * 데이터셋 이름
     */
    @JsonProperty("dataset_name")
    @Schema(description = "데이터셋 이름", example = "규정")
    private String datasetName;

    /**
     * 원본 시스템 코드
     */
    @JsonProperty("origin_system_cd")
    @Schema(description = "원본 시스템 코드", example = "sb")
    private String originSystemCd;

    /**
     * 원본 시스템 이름
     */
    @JsonProperty("origin_system_name")
    @Schema(description = "원본 시스템 이름", example = "S-Basic")
    private String originSystemName;

    /**
     * 데이터셋 카드 타입
     */
    @JsonProperty("dataset_card_type")
    @Schema(description = "데이터셋 카드 타입", example = "DATASET")
    private String datasetCardType;

    /**
     * 데이터셋 요약
     */
    @JsonProperty("dataset_card_summary")
    @Schema(description = "데이터셋 요약", example = "신한은행 자금이체약정서의 2025년 최신 개정판으로 업무에 참고하세요.")
    private String datasetCardSummary;

    /**
     * 미리보기
     */
    @JsonProperty("preview")
    @Schema(description = "데이터셋 미리보기", example = "데이터셋 미리보기")
    private String preview;

    /**
     * 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(description = "메타데이터", example = "메타1, 메타2, 메타3")
    private String metadata;

    /**
     * 다운로드 경로
     */
    @JsonProperty("download_path")
    @Schema(description = "다운로드 경로")
    private String downloadPath;
}