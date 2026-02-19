package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SafetyFilter 작업 처리 결과 응답 DTO
 * 
 * <p>SafetyFilter의 단순 작업 처리 결과를 나타내는 응답입니다.
 * 주로 삭제 작업이나 기타 처리 시간만 필요한 작업에서 사용됩니다.</p>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>불용어 삭제</strong>: 개별 불용어 삭제 작업</li>
 *   <li><strong>그룹 삭제</strong>: 그룹 삭제 작업</li>
 *   <li><strong>기타 처리</strong>: 단순 성공/실패만 확인하는 작업</li>
 * </ul>
 * 
 * <h3>특징:</h3>
 * <ul>
 *   <li>최소한의 정보만 포함</li>
 *   <li>처리 시간 측정 제공</li>
 *   <li>성능 모니터링에 활용</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI SafetyFilter 작업 처리 결과 (처리 시간 포함)",
    example = """
        {
          "response_time_ms": 89.3
        }
        """
)
public class OperationResponse {
    
    /**
     * 응답 처리 시간
     * 
     * <p>작업의 총 처리 시간을 밀리초 단위로 나타냅니다.
     * 성능 모니터링 및 시스템 최적화에 활용됩니다.</p>
     * 
     * @implNote 모든 작업에 대해 처리 시간이 측정되어 제공됩니다.
     */
    @JsonProperty("response_time_ms")
    @Schema(
        description = "작업 처리 시간 (밀리초 단위)",
        example = "89.3"
    )
    private Double responseTimeMs;
}