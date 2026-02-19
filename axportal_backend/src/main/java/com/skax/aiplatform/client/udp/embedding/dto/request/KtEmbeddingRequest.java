package com.skax.aiplatform.client.udp.embedding.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * KT 임베딩 추론 요청 DTO
 * 
 * <p>KT 임베딩 모델을 사용하여 텍스트를 벡터로 변환하기 위한 요청 데이터 구조입니다.</p>
 * 
 * <h3>지원 기능:</h3>
 * <ul>
 *   <li><strong>단일/다중 텍스트</strong>: 하나 또는 여러 텍스트의 임베딩 생성</li>
 *   <li><strong>모델 선택</strong>: 다양한 KT 임베딩 모델 중 선택</li>
 *   <li><strong>정규화 옵션</strong>: 벡터 정규화 여부 설정</li>
 *   <li><strong>배치 처리</strong>: 대량 텍스트의 효율적인 처리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * KtEmbeddingRequest request = KtEmbeddingRequest.builder()
 *     .texts(Arrays.asList("안녕하세요", "반갑습니다"))
 *     .model("kt-embedding-v1")
 *     .normalize(true)
 *     .build();
 * </pre>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "KT 임베딩 추론 요청 정보",
    example = """
        {
          "texts": [
            "안녕하세요, 신한은행입니다.",
            "대출 상담을 원하시나요?"
          ],
          "model": "kt-embedding-v1",
          "normalize": true,
          "maxTokens": 512,
          "batchSize": 10
        }
        """
)
public class KtEmbeddingRequest {

    /**
     * 임베딩할 텍스트 목록
     * 
     * <p>벡터로 변환할 텍스트들의 목록입니다.
     * 단일 텍스트인 경우에도 배열 형태로 전달해야 합니다.</p>
     * 
     * @apiNote 한 번에 처리할 수 있는 텍스트 수는 배치 크기 제한을 따릅니다.
     */
    @Schema(
        description = "임베딩할 텍스트 목록 (1-100개)",
        example = """
            [
              "안녕하세요, 신한은행입니다.",
              "대출 상담을 원하시나요?"
            ]
            """,
        required = true
    )
    private List<String> texts;

    /**
     * 사용할 임베딩 모델
     * 
     * <p>KT에서 제공하는 임베딩 모델의 이름입니다.
     * 모델에 따라 벡터 차원과 성능이 달라집니다.</p>
     * 
     * @implNote 사용 가능한 모델 목록은 KT 공식 문서를 참조하세요.
     */
    @Schema(
        description = "사용할 KT 임베딩 모델 이름",
        example = "kt-embedding-v1",
        required = true,
        allowableValues = {"kt-embedding-v1", "kt-embedding-v2", "kt-embedding-large"}
    )
    private String model;

    /**
     * 벡터 정규화 여부
     * 
     * <p>생성된 임베딩 벡터를 정규화할지 여부입니다.
     * 정규화된 벡터는 코사인 유사도 계산에 유리합니다.</p>
     * 
     * @implNote true인 경우 L2 정규화가 적용됩니다.
     */
    @Schema(
        description = "벡터 정규화 여부 (L2 정규화)",
        example = "true",
        defaultValue = "true"
    )
    @Builder.Default
    private Boolean normalize = true;

    /**
     * 최대 토큰 수
     * 
     * <p>각 텍스트에서 처리할 최대 토큰 수입니다.
     * 이 수를 초과하는 텍스트는 잘림 처리됩니다.</p>
     * 
     * @implNote 모델에 따라 지원하는 최대 토큰 수가 다를 수 있습니다.
     */
    @Schema(
        description = "텍스트당 최대 토큰 수 (1-2048)",
        example = "512",
        defaultValue = "512"
    )
    @Builder.Default
    private Integer maxTokens = 512;

    /**
     * 배치 크기
     * 
     * <p>한 번에 처리할 텍스트의 개수입니다.
     * 큰 배치 크기는 처리 속도를 향상시키지만 메모리 사용량이 증가합니다.</p>
     * 
     * @implNote 시스템 리소스에 따라 적절한 배치 크기를 설정하세요.
     */
    @Schema(
        description = "배치 처리 크기 (1-100)",
        example = "10",
        defaultValue = "10"
    )
    @Builder.Default
    private Integer batchSize = 10;

    /**
     * 임베딩 타입
     * 
     * <p>생성할 임베딩의 타입을 지정합니다.
     * 용도에 따라 다른 타입의 임베딩을 사용할 수 있습니다.</p>
     * 
     * @implNote 일반적으로 "query"와 "document" 타입을 지원합니다.
     */
    @Schema(
        description = "임베딩 타입",
        example = "document",
        allowableValues = {"query", "document", "similarity"},
        defaultValue = "document"
    )
    @Builder.Default
    private String embeddingType = "document";

    /**
     * 요청 ID
     * 
     * <p>요청을 추적하기 위한 선택적 식별자입니다.
     * 로깅이나 디버깅 목적으로 사용할 수 있습니다.</p>
     */
    @Schema(
        description = "요청 추적을 위한 선택적 ID (최대 100자)",
        example = "req_12345"
    )
    private String requestId;

    /**
     * 언어 코드
     * 
     * <p>텍스트의 언어를 명시적으로 지정합니다.
     * 다국어 모델인 경우 성능 향상에 도움이 됩니다.</p>
     */
    @Schema(
        description = "텍스트 언어 코드 (ISO 639-1)",
        example = "ko",
        allowableValues = {"ko", "en", "auto"},
        defaultValue = "ko"
    )
    @Builder.Default
    private String language = "ko";
}