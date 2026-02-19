package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * SKTAI Agent Few-Shot 테스트 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot 테스트 수행 결과를 나타내는 응답 데이터 구조입니다.
 * Few-Shot의 성능, 품질, 일관성 등을 측정한 결과와 상세한 분석 정보를 포함합니다.</p>
 * 
 * <h3>테스트 결과 포함 항목:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 테스트 ID, Few-Shot UUID, 입력 데이터</li>
 *   <li><strong>결과 데이터</strong>: 모델 출력, 성능 지표</li>
 *   <li><strong>분석 정보</strong>: 품질 점수, 지연 시간, 토큰 사용량</li>
 *   <li><strong>메타데이터</strong>: 테스트 설정, 실행 환경</li>
 * </ul>
 * 
 * <h3>성능 지표:</h3>
 * <ul>
 *   <li><strong>품질 점수</strong>: Few-Shot 출력의 품질 평가 (0-100)</li>
 *   <li><strong>응답 시간</strong>: 처리 시간 (밀리초)</li>
 *   <li><strong>토큰 사용량</strong>: 입력/출력 토큰 수</li>
 *   <li><strong>일관성 점수</strong>: 반복 테스트 시 결과 일관성</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotTestResponse result = fewShotClient.testFewShot(fewShotUuid, request);
 * Double qualityScore = result.getQualityScore();
 * Integer responseTime = result.getResponseTimeMs();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see FewShotTestRequest 테스트 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 테스트 결과 응답 정보",
    example = """
        {
          "test_id": "test-123e4567-e89b-12d3-a456-426614174000",
          "few_shot_uuid": "fs-456e7890-e12b-34d5-a678-426614174111",
          "input": "고객 문의: 환불 정책에 대해 설명해주세요",
          "output": "안녕하세요. 환불 정책은 다음과 같습니다...",
          "quality_score": 85.5,
          "response_time_ms": 1250,
          "token_usage": {
            "input_tokens": 25,
            "output_tokens": 150,
            "total_tokens": 175
          },
          "test_type": "quality",
          "status": "completed",
          "created_at": "2025-08-22T10:30:00Z"
        }
        """
)
public class FewShotTestResponse {
    
    /**
     * 테스트 고유 식별자
     * 
     * <p>수행된 테스트의 고유한 UUID입니다.
     * 테스트 이력 추적 및 결과 참조 시 사용됩니다.</p>
     */
    @JsonProperty("test_id")
    @Schema(
        description = "테스트 고유 식별자 (UUID)", 
        example = "test-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String testId;
    
    /**
     * Few-Shot 고유 식별자
     * 
     * <p>테스트된 Few-Shot의 UUID입니다.</p>
     */
    @JsonProperty("few_shot_uuid")
    @Schema(
        description = "테스트된 Few-Shot UUID", 
        example = "fs-456e7890-e12b-34d5-a678-426614174111",
        format = "uuid"
    )
    private String fewShotUuid;
    
    /**
     * 테스트 입력 데이터
     * 
     * <p>테스트에 사용된 원본 입력 텍스트입니다.</p>
     */
    @JsonProperty("input")
    @Schema(
        description = "테스트에 사용된 입력 데이터", 
        example = "고객 문의: 환불 정책에 대해 설명해주세요"
    )
    private String input;
    
    /**
     * 모델 출력 결과
     * 
     * <p>Few-Shot을 적용한 모델의 실제 출력 텍스트입니다.</p>
     */
    @JsonProperty("output")
    @Schema(
        description = "Few-Shot 적용 후 모델 출력 결과", 
        example = "안녕하세요. 환불 정책은 다음과 같습니다..."
    )
    private String output;
    
    /**
     * 품질 점수
     * 
     * <p>Few-Shot 출력의 품질을 0-100 점수로 평가한 결과입니다.
     * 정확성, 관련성, 완성도 등을 종합적으로 평가합니다.</p>
     */
    @JsonProperty("quality_score")
    @Schema(
        description = "출력 품질 점수 (0-100)", 
        example = "85.5",
        minimum = "0",
        maximum = "100"
    )
    private Double qualityScore;
    
    /**
     * 응답 시간
     * 
     * <p>Few-Shot 처리에 소요된 시간을 밀리초 단위로 나타냅니다.</p>
     */
    @JsonProperty("response_time_ms")
    @Schema(
        description = "응답 시간 (밀리초)", 
        example = "1250",
        minimum = "0"
    )
    private Integer responseTimeMs;
    
    /**
     * 토큰 사용량 정보
     * 
     * <p>입력 토큰, 출력 토큰, 총 토큰 사용량을 포함하는 정보입니다.</p>
     */
    @JsonProperty("token_usage")
    @Schema(
        description = "토큰 사용량 정보", 
        example = """
            {
              "input_tokens": 25,
              "output_tokens": 150,
              "total_tokens": 175
            }
            """
    )
    private Map<String, Integer> tokenUsage;
    
    /**
     * 테스트 유형
     * 
     * <p>수행된 테스트의 종류입니다.</p>
     */
    @JsonProperty("test_type")
    @Schema(
        description = "수행된 테스트 유형", 
        example = "quality",
        allowableValues = {"quality", "performance", "consistency", "accuracy"}
    )
    private String testType;
    
    /**
     * 테스트 상태
     * 
     * <p>테스트의 현재 상태를 나타냅니다.</p>
     */
    @JsonProperty("status")
    @Schema(
        description = "테스트 상태", 
        example = "completed",
        allowableValues = {"running", "completed", "failed", "cancelled"}
    )
    private String status;
    
    /**
     * 추가 메트릭
     * 
     * <p>테스트 유형에 따른 추가적인 성능 지표나 분석 결과입니다.</p>
     */
    @JsonProperty("metrics")
    @Schema(
        description = "추가 성능 지표 및 분석 결과", 
        example = """
            {
              "consistency_score": 92.0,
              "relevance_score": 88.5,
              "coherence_score": 90.2
            }
            """
    )
    private Map<String, Object> metrics;
    
    /**
     * 테스트 실행 일시
     * 
     * <p>테스트가 실행된 날짜와 시간입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "테스트 실행 일시 (ISO 8601)", 
        example = "2025-08-22T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 오류 정보
     * 
     * <p>테스트 실행 중 발생한 오류나 경고 메시지입니다.</p>
     */
    @JsonProperty("error_message")
    @Schema(
        description = "테스트 실행 중 발생한 오류 메시지", 
        example = "일부 지표 계산에서 경고가 발생했습니다."
    )
    private String errorMessage;
}
