package com.skax.aiplatform.dto.deploy.request;

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
    description = "Agent Gateway 스트리밍 추론 요청 정보",
    example = """
        {
          "config": {},
          "input": {
            "additional_kwargs": {
              "additional1": "value"
            },
            "messages": [
              {
                "content": "hello",
                "type": "human"
              }
            ]
          },
          "kwargs": {}
        }
        """
)
public class StreamReq {
    @Schema(
        description = "스트리밍 추론 설정 정보",
        example = "{}"
    )
    private Object config;
    
    @Schema(
        description = "에이전트에게 전달할 입력 데이터 (messages와 additional_kwargs 포함)", 
        example = "{\"additional_kwargs\": {\"additional1\": \"value\"}, \"messages\": [{\"content\": \"hello\", \"type\": \"human\"}]}"
    )
    private Object input;
    
    @Schema(
        description = "추가 매개변수",
        example = "{}"
    )
    private Object kwargs;
}
