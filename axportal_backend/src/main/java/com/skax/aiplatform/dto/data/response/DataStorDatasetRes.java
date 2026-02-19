package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터 저장소 데이터셋 응답 DTO
 * 
 * <p>데이터셋 검색 결과를 담는 응답 DTO입니다.
 * UDP 데이터셋 검색 API 응답을 프론트엔드용 DTO로 변환합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "데이터 저장소 데이터셋 응답")
public class DataStorDatasetRes {

    @Schema(description = "데이터셋카드ID", example = "19")
    private String datasetCardId;
    
    @Schema(description = "데이터셋카드명", example = "규정")
    private String datasetCardName;
    
    @Schema(description = "데이터셋코드", example = "rgl")
    private String datasetCd;
    
    @Schema(description = "데이터셋명", example = "규정")
    private String datasetName;
    
    @Schema(description = "원천시스템코드", example = "sb")
    private String originSystemCd;
    
    @Schema(description = "원천시스템명", example = "S-Basic")
    private String originSystemName;
    
    @Schema(description = "데이터셋카드유형", example = "DATASET")
    private String datasetCardType;
    
    @Schema(description = "데이터셋 설명", example = "신한은행 자금이체약정서의 2025년 최신 개정판으로 업무에 참고하세요.")
    private String datasetCardSummary;
    
    @Schema(description = "미리보기", example = "데이터셋 미리보기")
    private String preview;
    
    @Schema(description = "메타데이터", example = "메타1, 메타2,메타3")
    private String metadata;
    
    @Schema(description = "다운로드 경로 (데이터셋이 학습데이터일 경우 제공)", example = "")
    private String downloadPath;
}