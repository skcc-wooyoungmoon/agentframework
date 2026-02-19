package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 예제 목록 응답 DTO
 * 
 * <p>특정 Few-Shot의 학습 예제 목록을 담는 응답 데이터 구조입니다.
 * 예제는 입력-출력 쌍으로 구성되어 모델의 Few-Shot Learning을 지원합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 예제 목록 응답")
public class FewShotExamplesResponse {
    
    @JsonProperty("few_shot_uuid")
    @Schema(description = "Few-Shot UUID")
    private String fewShotUuid;
    
    @JsonProperty("version")
    @Schema(description = "예제가 속한 버전")
    private String version;
    
    @JsonProperty("examples")
    @Schema(description = "Few-Shot 학습 예제 목록")
    private List<Object> examples;
    
    @JsonProperty("total_count")
    @Schema(description = "전체 예제 수")
    private Integer totalCount;
}
