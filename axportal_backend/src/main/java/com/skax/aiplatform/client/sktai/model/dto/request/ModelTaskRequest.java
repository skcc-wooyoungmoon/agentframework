package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model 작업 요청 DTO
 * 
 * <p>SKTAI Model이 수행할 수 있는 작업을 추가하거나 제거할 때 사용하는 요청 데이터 구조입니다.
 * 모델의 기능과 용도를 정의하는 작업 관리를 지원합니다.</p>
 * 
 * <h3>지원하는 작업 타입:</h3>
 * <ul>
 *   <li><strong>completion</strong>: 텍스트 완성</li>
 *   <li><strong>generation</strong>: 텍스트 생성</li>
 *   <li><strong>classification</strong>: 분류</li>
 *   <li><strong>embedding</strong>: 임베딩</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelTaskRequest request = ModelTaskRequest.builder()
 *     .name("completion")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model 작업 요청 정보",
    example = """
        {
          "name": "completion"
        }
        """
)
public class ModelTaskRequest {
    
    /**
     * 작업 이름
     * 
     * <p>모델이 수행할 수 있는 작업의 이름입니다.
     * 모델의 기능과 용도를 정의하는 중요한 속성입니다.</p>
     * 
     * @implNote 작업 이름은 사전 정의된 값들 중에서 선택해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "작업 이름 (모델이 수행할 수 있는 기능)", 
        example = "completion",
        required = true,
        allowableValues = {"completion", "generation", "classification", "embedding", "chat", "instruction"},
        maxLength = 64
    )
    private String name;
}
