package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway Embeddings 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 텍스트 임베딩 벡터 생성을 위한 요청 데이터 구조입니다.
 * 텍스트를 고차원 벡터로 변환하여 유사도 검색, 클러스터링, 분류 등에 활용할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>input</strong>: 임베딩할 텍스트 (문자열 또는 문자열 배열)</li>
 *   <li><strong>model</strong>: 사용할 임베딩 모델 식별자</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>단일/다중 텍스트 임베딩 처리</li>
 *   <li>다양한 임베딩 모델 지원 (OpenAI, BGE 등)</li>
 *   <li>인코딩 포맷 설정 (float, base64)</li>
 *   <li>배치 처리를 통한 효율성 극대화</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * EmbeddingsRequest request = EmbeddingsRequest.builder()
 *     .model("text-embedding-ada-002")
 *     .input(Arrays.asList(
 *         "OpenAI is an AI research company.",
 *         "New Jeans is a Korean girl group."
 *     ))
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
    description = "SKTAI Model Gateway Embeddings 요청 정보",
    example = """
        {
          "model": "text-embedding-ada-002",
          "input": [
            "OpenAI is an AI research company.",
            "New Jeans is a Korean girl group."
          ],
          "encoding_format": "float"
        }
        """
)
public class EmbeddingsRequest {
    
    /**
     * 임베딩할 입력 텍스트
     * 
     * <p>단일 문자열 또는 문자열 배열을 지원합니다.
     * 여러 텍스트를 배치로 처리할 경우 효율성이 향상됩니다.</p>
     * 
     * @apiNote 배열로 전송 시 각 요소가 개별 임베딩 벡터로 변환됩니다.
     * @implNote 텍스트 길이는 모델별 최대 토큰 수 제한을 따릅니다.
     */
    @JsonProperty("input")
    @Schema(
        description = "임베딩할 텍스트 (문자열 또는 문자열 배열)",
        required = true,
        example = """
            [
              "OpenAI is an AI research company.",
              "New Jeans is a Korean girl group."
            ]
            """
    )
    private Object input; // String 또는 List<String> 또는 List<List<Integer>>
    
    /**
     * 임베딩 모델 식별자
     * 
     * <p>사용할 임베딩 모델의 이름입니다.
     * 각 모델마다 벡터 차원과 성능 특성이 다릅니다.</p>
     * 
     * @implNote 지원 모델: text-embedding-ada-002, text-embedding-3-small, text-embedding-3-large 등
     * @apiNote 모델별로 최대 입력 토큰 수와 출력 차원이 상이합니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 임베딩 모델 식별자",
        example = "text-embedding-ada-002",
        required = true
    )
    private String model;
    
    /**
     * 인코딩 포맷
     * 
     * <p>임베딩 벡터의 인코딩 방식을 지정합니다.
     * float는 부동소수점, base64는 압축된 바이너리 형태입니다.</p>
     * 
     * @implNote 기본값: float, 네트워크 효율성을 위해 base64 사용 가능
     * @apiNote float 형태가 일반적으로 더 직관적이고 사용하기 쉽습니다.
     */
    @JsonProperty("encoding_format")
    @Schema(
        description = "임베딩 벡터 인코딩 포맷 (float 또는 base64)",
        example = "float",
        allowableValues = {"float", "base64"}
    )
    private String encodingFormat;
    
    /**
     * 차원 수 (선택적)
     * 
     * <p>일부 모델에서 출력 임베딩의 차원을 조정할 수 있습니다.
     * 더 작은 차원으로 설정하면 저장 공간과 계산 비용을 절약할 수 있습니다.</p>
     * 
     * @implNote 모델의 기본 차원보다 작은 값만 설정 가능합니다.
     * @apiNote 차원 축소 시 일부 정보 손실이 발생할 수 있습니다.
     */
    @JsonProperty("dimensions")
    @Schema(
        description = "출력 임베딩 차원 수 (모델별 최대값 이하)",
        example = "1536",
        minimum = "1"
    )
    private Integer dimensions;
    
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
