package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

/**
 * SKTAI Agent Few-Shot 태그 전체 목록 응답 DTO
 * 
 * <p>시스템에서 사용 가능한 모든 Few-Shot 태그의 목록을 담는 응답 데이터 구조입니다.
 * 새로운 Few-Shot 생성 시 태그 선택의 참고 자료로 활용됩니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 시스템 전체 태그 목록 응답")
public class FewShotTagListResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;
    
    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    // API는 리스트를 data 키로 반환함
    @JsonProperty("data")
    @Schema(description = "시스템에서 사용 가능한 모든 Few-Shot 태그 목록")
    private List<String> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
}
