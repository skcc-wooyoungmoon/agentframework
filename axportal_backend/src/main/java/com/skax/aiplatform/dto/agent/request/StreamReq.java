package com.skax.aiplatform.dto.agent.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Gateway 스트리밍 추론 요청 정보",
    example = """
        {
          "input": "긴 텍스트를 생성해주세요",
          "config": {
            "stream": true,
            "temperature": 0.7,
            "max_tokens": 2000
          },
          "kwargs": {}
        }
        """
)
public class StreamReq {

    @JsonProperty("input")
    @Schema(
        description = "에이전트에게 전달할 입력 데이터", 
        example = "긴 텍스트를 생성해주세요"
    )
    private Object input;
    
    @JsonProperty("config")
    @Schema(
        description = "스트리밍 추론 설정 정보",
        example = "{\"stream\": true, \"temperature\": 0.7, \"max_tokens\": 2000}"
    )
    private Object config;

    @JsonProperty("kwargs")
    @Schema(
        description = "추가 매개변수",
        example = "{}"
    )
    private Object kwargs;
}
