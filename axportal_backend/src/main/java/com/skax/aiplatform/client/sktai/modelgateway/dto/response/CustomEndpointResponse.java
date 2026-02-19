package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 커스텀 엔드포인트 응답 DTO
 * 
 * <p>사용자 정의 API 엔드포인트의 응답을 담는 범용 구조입니다.
 * 다양한 커스텀 모델과 기능에 대한 유연한 응답 처리를 지원합니다.</p>
 * 
 * <h3>특징:</h3>
 * <ul>
 *   <li><strong>유연한 구조</strong>: 다양한 응답 형태 지원</li>
 *   <li><strong>표준화된 메타데이터</strong>: 공통 처리 정보 포함</li>
 *   <li><strong>확장 가능</strong>: 새로운 모델 유형에 대한 확장성</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 커스텀 엔드포인트 응답 정보",
    example = """
        {
          "success": true,
          "data": {
            "result": "커스텀 처리 결과",
            "value": 42
          },
          "model_info": {
            "model_name": "custom-model-v1",
            "version": "1.2.3"
          },
          "processing": {
            "time_ms": 250,
            "tokens": 30
          },
          "metadata": {
            "endpoint": "/custom/analyze",
            "request_id": "custom-123"
          }
        }
        """
)
public class CustomEndpointResponse {
    
    /**
     * 처리 성공 여부
     */
    @JsonProperty("success")
    @Schema(description = "처리 성공 여부", example = "true")
    private Boolean success;
    
    /**
     * 커스텀 응답 데이터
     */
    @JsonProperty("data")
    @Schema(description = "커스텀 응답 데이터 (모델별 가변 구조)")
    private Object data;
    
    /**
     * 에러 정보 (실패 시)
     */
    @JsonProperty("error")
    @Schema(description = "에러 정보 (실패 시)")
    private ErrorInfo error;
    
    /**
     * 모델 정보
     */
    @JsonProperty("model_info")
    @Schema(description = "사용된 모델 정보")
    private ModelInfo modelInfo;
    
    /**
     * 처리 정보
     */
    @JsonProperty("processing")
    @Schema(description = "처리 성능 정보")
    private ProcessingInfo processing;
    
    /**
     * 추가 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터")
    private Object metadata;
    
    /**
     * 에러 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "에러 정보")
    public static class ErrorInfo {
        
        /**
         * 에러 코드
         */
        @JsonProperty("code")
        @Schema(description = "에러 코드", example = "CUSTOM_ERROR_001")
        private String code;
        
        /**
         * 에러 메시지
         */
        @JsonProperty("message")
        @Schema(description = "에러 메시지", example = "커스텀 처리 중 오류 발생")
        private String message;
        
        /**
         * 에러 상세 정보
         */
        @JsonProperty("details")
        @Schema(description = "에러 상세 정보")
        private Object details;
    }
    
    /**
     * 모델 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "모델 정보")
    public static class ModelInfo {
        
        /**
         * 모델명
         */
        @JsonProperty("model_name")
        @Schema(description = "모델명", example = "custom-model-v1")
        private String modelName;
        
        /**
         * 모델 버전
         */
        @JsonProperty("version")
        @Schema(description = "모델 버전", example = "1.2.3")
        private String version;
        
        /**
         * 모델 타입
         */
        @JsonProperty("type")
        @Schema(description = "모델 타입", example = "text-analysis")
        private String type;
    }
    
    /**
     * 처리 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "처리 성능 정보")
    public static class ProcessingInfo {
        
        /**
         * 처리 시간 (밀리초)
         */
        @JsonProperty("time_ms")
        @Schema(description = "처리 시간 (밀리초)", example = "250")
        private Long timeMs;
        
        /**
         * 사용된 토큰 수
         */
        @JsonProperty("tokens")
        @Schema(description = "사용된 토큰 수", example = "30")
        private Integer tokens;
        
        /**
         * 메모리 사용량 (MB)
         */
        @JsonProperty("memory_mb")
        @Schema(description = "메모리 사용량 (MB)", example = "128")
        private Double memoryMb;
    }
}
