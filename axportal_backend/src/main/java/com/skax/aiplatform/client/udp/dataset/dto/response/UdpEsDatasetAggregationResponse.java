package com.skax.aiplatform.client.udp.dataset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UDP 엘라스틱서치 데이터셋 집계 응답 DTO
 * 
 * <p>UDP Elasticsearch를 통한 데이터셋 집계 조회 결과를 담는 응답 객체입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Schema(description = "UDP 엘라스틱서치 데이터셋 집계 응답")
public class UdpEsDatasetAggregationResponse {
    
    @Schema(description = "데이터셋 참조 정보 목록")
    private List<DatasetReferInfo> datasetReferList;
    
    /**
     * 데이터셋 참조 정보
     */
    @Data
    @NoArgsConstructor
    @Schema(description = "데이터셋 참조 정보")
    public static class DatasetReferInfo {
        
        @Schema(description = "데이터셋 카드 참조명", example = "S-Basic")
        private String datasetcardReferNm;
        
        @Schema(description = "데이터셋 카드 참조코드", example = "sb")
        private String datasetcardReferCd;
    }
}