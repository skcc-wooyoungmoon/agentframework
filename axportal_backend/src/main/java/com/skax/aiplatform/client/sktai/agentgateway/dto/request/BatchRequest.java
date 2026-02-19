package com.skax.aiplatform.client.sktai.agentgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Gateway 배치 추론 요청 DTO
 * 
 * <p>SKTAI Agent Gateway에서 여러 입력에 대해 배치로 추론을 수행하기 위한 요청 데이터 구조입니다.
 * 다수의 입력 데이터를 한 번에 처리하여 효율성을 높일 수 있습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>inputs</strong>: 배치로 처리할 입력 데이터 목록</li>
 *   <li><strong>config</strong>: 배치 추론 설정 정보 (선택사항)</li>
 *   <li><strong>kwargs</strong>: 추가 매개변수 (선택사항)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * BatchRequest request = BatchRequest.builder()
 *     .inputs(Arrays.asList("질문1", "질문2", "질문3"))
 *     .config(Map.of("temperature", 0.7))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Gateway 배치 추론 요청 정보",
    example = """
        {
          "inputs": [
            "첫 번째 질문입니다",
            "두 번째 질문입니다",
            "세 번째 질문입니다"
          ],
          "config": {
            "temperature": 0.7,
            "max_tokens": 1000
          },
          "kwargs": {}
        }
        """
)
public class BatchRequest {
    
    /**
     * 입력 데이터 목록
     * 
     * <p>배치로 처리할 입력 데이터들의 목록입니다.
     * 각 입력은 문자열, 객체 등 다양한 형태가 될 수 있습니다.</p>
     */
    @JsonProperty("inputs")
    @Schema(
        description = "배치로 처리할 입력 데이터 목록", 
        example = "[\"첫 번째 질문입니다\", \"두 번째 질문입니다\"]"
    )
    private List<Object> inputs;
    
    /**
     * 배치 추론 설정
     * 
     * <p>배치 추론의 동작을 제어하는 설정 정보입니다.
     * temperature, max_tokens 등의 파라미터를 포함할 수 있습니다.</p>
     */
    @JsonProperty("config")
    @Schema(
        description = "배치 추론 설정 정보",
        example = "{\"temperature\": 0.7, \"max_tokens\": 1000}"
    )
    private Object config;
    
    /**
     * 추가 매개변수
     * 
     * <p>배치 처리에 필요한 추가 매개변수를 전달할 수 있는 필드입니다.</p>
     */
    @JsonProperty("kwargs")
    @Schema(
        description = "추가 매개변수",
        example = "{}"
    )
    private Object kwargs;
}
