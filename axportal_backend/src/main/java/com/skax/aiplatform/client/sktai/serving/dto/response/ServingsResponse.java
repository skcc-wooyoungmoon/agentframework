package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 서빙 모델 목록 조회 응답
 * 
 * <p>배포된 AI 모델들의 목록과 상세 정보를 포함합니다.
 * 각 모델의 상태, 성능 지표, 접근 정보 등을 제공합니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "서빙 모델 목록 조회 응답")
public class ServingsResponse {

    @JsonProperty("data")
    @Schema(description = "서빙 모델 목록")
    private List<ServingResponse> data;

    @JsonProperty("total_count")
    @Schema(description = "전체 서빙 모델 수")
    private Integer totalCount;

    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;

}
