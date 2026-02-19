package com.skax.aiplatform.dto.agent.response;


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
    description = "SKTAI Agent Gateway 스트리밍 응답",
    example = """
        {
          "chunk": "안녕하세요! 저는",
          "is_final": false,
          "metadata": {
            "chunk_index": 1,
            "total_chunks": null,
            "processing_time": 0.1
          }
        }
        """
)
public class StreamRes {
    
    @Schema(
        description = "스트리밍 응답의 일부분", 
        example = "안녕하세요! 저는"
    )
    private Object chunk;

    @Schema(
        description = "최종 청크 여부", 
        example = "false"
    )
    private Boolean isFinal;

    @Schema(
        description = "스트리밍 메타데이터",
        example = "{\"chunk_index\": 1, \"total_chunks\": null, \"processing_time\": 0.1}"
    )
    private Object metadata;
}
