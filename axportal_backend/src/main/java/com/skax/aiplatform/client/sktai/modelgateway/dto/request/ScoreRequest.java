package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 스코어링 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 텍스트 간 유사도 스코어링을 위한 요청 데이터 구조입니다.
 * 두 텍스트 간의 의미적 유사도를 측정하여 검색, 추천, 매칭 등에 활용할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>model</strong>: 사용할 스코어링 모델 식별자</li>
 *   <li><strong>text_1</strong>: 첫 번째 비교 텍스트</li>
 *   <li><strong>text_2</strong>: 두 번째 비교 텍스트</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>의미적 유사도 측정</li>
 *   <li>단일/배치 텍스트 비교</li>
 *   <li>다국어 지원</li>
 *   <li>정규화된 스코어 (0~1 범위)</li>
 *   <li>고성능 재랭킹 모델 활용</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ScoreRequest request = ScoreRequest.builder()
 *     .model("BAAI/bge-reranker-v2-m3")
 *     .text1("What is the capital of France?")
 *     .text2("The capital of France is Paris.")
 *     .encodingFormat("float")
 *     .build();
 * </pre>
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
    description = "SKTAI Model Gateway 스코어링 요청 정보",
    example = """
        {
          "model": "BAAI/bge-reranker-v2-m3",
          "text_1": "What is the capital of France?",
          "text_2": "The capital of France is Paris.",
          "encoding_format": "float"
        }
        """
)
public class ScoreRequest {
    
    /**
     * 스코어링 모델 식별자
     * 
     * <p>텍스트 간 유사도를 계산할 모델의 이름입니다.
     * 재랭킹 전용 모델로 높은 정확도를 제공합니다.</p>
     * 
     * @implNote 지원 모델: BAAI/bge-reranker-v2-m3, BAAI/bge-reranker-large 등
     * @apiNote BGE 시리즈는 다국어를 지원하며 뛰어난 성능을 제공합니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 스코어링/재랭킹 모델 식별자",
        example = "BAAI/bge-reranker-v2-m3",
        required = true
    )
    private String model;
    
    /**
     * 첫 번째 비교 텍스트
     * 
     * <p>유사도를 측정할 첫 번째 텍스트입니다.
     * 단일 문자열 또는 문자열 배열을 지원합니다.</p>
     * 
     * @apiNote 일반적으로 쿼리나 질문 텍스트로 사용됩니다.
     * @implNote 배열로 전송 시 각 요소와 text_2의 모든 요소 간 비교가 수행됩니다.
     */
    @JsonProperty("text_1")
    @Schema(
        description = "첫 번째 비교 텍스트 (단일 또는 배열)",
        example = "What is the capital of France?",
        required = true
    )
    private Object text1; // String 또는 List<String>
    
    /**
     * 두 번째 비교 텍스트
     * 
     * <p>유사도를 측정할 두 번째 텍스트입니다.
     * 단일 문자열 또는 문자열 배열을 지원합니다.</p>
     * 
     * @apiNote 일반적으로 문서나 답변 텍스트로 사용됩니다.
     * @implNote 배열로 전송 시 각 요소와 text_1의 모든 요소 간 비교가 수행됩니다.
     */
    @JsonProperty("text_2")
    @Schema(
        description = "두 번째 비교 텍스트 (단일 또는 배열)",
        example = "The capital of France is Paris.",
        required = true
    )
    private Object text2; // String 또는 List<String>
    
    /**
     * 인코딩 포맷
     * 
     * <p>스코어 값의 인코딩 방식을 지정합니다.
     * float는 부동소수점, base64는 압축된 바이너리 형태입니다.</p>
     * 
     * @implNote 기본값: float, 일반적으로 float 사용을 권장합니다.
     * @apiNote float 형태가 직관적이고 해석하기 쉽습니다.
     */
    @JsonProperty("encoding_format")
    @Schema(
        description = "스코어 인코딩 포맷 (float 또는 base64)",
        example = "float",
        allowableValues = {"float", "base64"}
    )
    private String encodingFormat;
    
    /**
     * 정규화 여부
     * 
     * <p>스코어를 0~1 범위로 정규화할지 여부를 결정합니다.
     * true로 설정하면 비교하기 쉬운 정규화된 값을 반환합니다.</p>
     * 
     * @implNote 기본값: true
     * @apiNote 정규화된 스코어는 임계값 설정과 해석이 용이합니다.
     */
    @JsonProperty("normalize")
    @Schema(
        description = "스코어 정규화 여부 (0~1 범위로 조정)",
        example = "true"
    )
    private Boolean normalize;
    
    /**
     * 최대 길이
     * 
     * <p>입력 텍스트의 최대 토큰 길이를 제한합니다.
     * 긴 텍스트는 자동으로 잘려서 처리됩니다.</p>
     * 
     * @implNote 기본값: 512, 최대값은 모델에 따라 다름
     * @apiNote 너무 긴 텍스트는 처리 시간과 정확도에 영향을 줄 수 있습니다.
     */
    @JsonProperty("max_length")
    @Schema(
        description = "입력 텍스트 최대 토큰 길이",
        example = "512",
        minimum = "1"
    )
    private Integer maxLength;
    
    /**
     * 사용자 식별자 (선택적)
     * 
     * <p>요청을 보낸 최종 사용자를 식별하는 고유 식별자입니다.
     * 모니터링, 남용 방지, 사용량 추적 등에 활용됩니다.</p>
     * 
     * @apiNote 개인정보 보호를 위해 해시값이나 익명화된 ID 사용을 권장합니다.
     */
    @JsonProperty("user")
    @Schema(
        description = "최종 사용자 식별자 (모니터링 및 남용 방지용)",
        example = "user-123",
        maxLength = 100
    )
    private String user;
}
