package com.skax.aiplatform.dto.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추론 성능 조회 응답 DTO
 * 
 * <p>
 * 모델 배포의 추론 성능 데이터를 담는 응답 구조입니다.
 * Time To First Token (TTFT)와 Time Per Output Token의 구간별 호출 수 분포를 포함합니다.
 * </p>
 * 
 * @author AXPortal Team
 * @since 2025-01-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "추론 성능 조회 응답 정보")
public class GetInferencePerformanceRes {

    /**
     * 서빙 ID
     */
    @Schema(description = "서빙 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String servingId;

    /**
     * Time To First Token 시계열 데이터
     */
    @Schema(description = "Time To First Token 시계열 데이터 (Average 값)")
    private TimeToFirstTokenTimeSeries timeToFirstToken;

    /**
     * Time Per Output Token 시계열 데이터
     */
    @Schema(description = "Time Per Output Token 시계열 데이터 (Mean 값)")
    private TimePerOutputTokenTimeSeries timePerOutputToken;

    /**
     * End-to-End Request Latency 시계열 데이터
     */
    @Schema(description = "End-to-End Request Latency 시계열 데이터 (Average 값)")
    private EndToEndLatencyTimeSeries endToEndLatency;

    /**
     * Time To First Token 시계열 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Time To First Token 시계열 데이터 (Average 값)")
    public static class TimeToFirstTokenTimeSeries {
        
        /**
         * 시계열 데이터 리스트 (timestamp와 value)
         * x축: 추론 시각 (timestamp)
         * y축: 소요 시간 (value, 초 단위)
         */
        @Schema(description = "시계열 데이터 리스트", 
                example = "[{\"timestamp\":1768410000,\"value\":0.5},{\"timestamp\":1768413600,\"value\":0.6}]")
        private List<TimeSeriesData> timeSeries;
    }

    /**
     * Time Per Output Token 시계열 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Time Per Output Token 시계열 데이터 (Mean 값)")
    public static class TimePerOutputTokenTimeSeries {
        
        /**
         * 시계열 데이터 리스트 (timestamp와 value)
         * x축: 추론 시각 (timestamp)
         * y축: 소요 시간 (value, 초 단위)
         */
        @Schema(description = "시계열 데이터 리스트", 
                example = "[{\"timestamp\":1768410000,\"value\":0.008},{\"timestamp\":1768413600,\"value\":0.009}]")
        private List<TimeSeriesData> timeSeries;
    }

    /**
     * End-to-End Request Latency 시계열 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "End-to-End Request Latency 시계열 데이터 (Average 값)")
    public static class EndToEndLatencyTimeSeries {
        
        /**
         * 시계열 데이터 리스트 (timestamp와 value)
         * x축: 추론 시각 (timestamp)
         * y축: 소요 시간 (value, 초 단위)
         */
        @Schema(description = "시계열 데이터 리스트", 
                example = "[{\"timestamp\":1768410000,\"value\":3.5},{\"timestamp\":1768413600,\"value\":4.2}]")
        private List<TimeSeriesData> timeSeries;
    }

    /**
     * 시계열 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "시계열 데이터 (timestamp와 value)")
    public static class TimeSeriesData {
        
        /**
         * Unix Timestamp (초 단위)
         */
        @Schema(description = "Unix Timestamp (초 단위)", example = "1768410000")
        private Long timestamp;
        
        /**
         * 값 (초 단위)
         */
        @Schema(description = "값 (초 단위)", example = "0.5")
        private Double value;
    }
}

