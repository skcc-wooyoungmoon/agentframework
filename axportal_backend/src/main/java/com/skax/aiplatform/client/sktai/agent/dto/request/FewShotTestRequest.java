package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 테스트 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot의 통합 테스트를 수행하기 위한 요청 데이터 구조입니다.
 * Few-Shot이 실제 시나리오에서 어떻게 동작하는지 검증하고 성능을 측정할 때 사용됩니다.</p>
 * 
 * <h3>Few-Shot 테스트 특징:</h3>
 * <ul>
 *   <li><strong>통합 테스트</strong>: Few-Shot과 모델의 전체 파이프라인 테스트</li>
 *   <li><strong>성능 측정</strong>: 응답 시간, 정확도, 품질 지표 측정</li>
 *   <li><strong>시나리오 검증</strong>: 다양한 입력에 대한 Few-Shot 동작 확인</li>
 *   <li><strong>예제 검증</strong>: Few-Shot 예제의 실제 효과 측정</li>
 * </ul>
 * 
 * <h3>테스트 유형:</h3>
 * <ul>
 *   <li><strong>단일 테스트</strong>: 특정 입력에 대한 Few-Shot 결과 확인</li>
 *   <li><strong>배치 테스트</strong>: 여러 입력에 대한 일괄 테스트</li>
 *   <li><strong>성능 테스트</strong>: 응답 시간 및 처리량 측정</li>
 *   <li><strong>품질 테스트</strong>: 출력 품질 및 일관성 검증</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotTestRequest request = FewShotTestRequest.builder()
 *     .input("고객 문의: 환불 정책에 대해 설명해주세요")
 *     .testType("quality")
 *     .iterations(10)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see FewShotTestResponse 테스트 결과 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 테스트 요청 정보",
    example = """
        {
          "input": "고객 문의: 환불 정책에 대해 설명해주세요",
          "test_type": "quality",
          "iterations": 10
        }
        """
)
public class FewShotTestRequest {
    
    /**
     * 테스트 입력 데이터
     * 
     * <p>Few-Shot 테스트에 사용할 입력 텍스트입니다.
     * 실제 사용 시나리오를 반영한 현실적인 입력을 제공해야 합니다.</p>
     * 
     * @implNote Few-Shot 예제와 유사한 형태의 입력을 제공하면 더 정확한 테스트 결과를 얻을 수 있습니다.
     * @apiNote 빈 입력은 허용되지 않으며, 의미 있는 테스트 데이터를 제공해야 합니다.
     */
    @JsonProperty("input")
    @Schema(
        description = "Few-Shot 테스트에 사용할 입력 텍스트", 
        example = "고객 문의: 환불 정책에 대해 설명해주세요",
        required = true,
        minLength = 1,
        maxLength = 5000
    )
    private String input;
    
    /**
     * 테스트 유형
     * 
     * <p>수행할 테스트의 종류를 지정합니다.
     * 각 유형에 따라 다른 평가 기준과 결과가 제공됩니다.</p>
     * 
     * @implNote 기본값은 "quality"이며, 지원되는 테스트 유형은 API 문서를 참조하세요.
     */
    @JsonProperty("test_type")
    @Schema(
        description = "테스트 유형 (quality, performance, consistency 등)", 
        example = "quality",
        allowableValues = {"quality", "performance", "consistency", "accuracy"}
    )
    private String testType;
    
    /**
     * 테스트 반복 횟수
     * 
     * <p>동일한 입력에 대해 테스트를 반복할 횟수입니다.
     * 일관성 테스트나 성능 측정 시 유용합니다.</p>
     * 
     * @implNote 기본값은 1이며, 최대 100회까지 설정 가능합니다.
     */
    @JsonProperty("iterations")
    @Schema(
        description = "테스트 반복 횟수 (1-100)", 
        example = "10",
        minimum = "1",
        maximum = "100"
    )
    private Integer iterations;
    
    /**
     * 세부 옵션
     * 
     * <p>테스트 수행 시 추가 옵션을 JSON 형태로 제공할 수 있습니다.
     * 테스트 유형에 따라 다른 옵션이 지원됩니다.</p>
     * 
     * @implNote 옵션은 선택사항이며, 제공하지 않으면 기본 설정으로 테스트가 수행됩니다.
     */
    @JsonProperty("options")
    @Schema(
        description = "테스트 세부 옵션 (JSON 형태)", 
        example = "{\"temperature\": 0.7, \"max_tokens\": 500}",
        maxLength = 1000
    )
    private String options;
}
