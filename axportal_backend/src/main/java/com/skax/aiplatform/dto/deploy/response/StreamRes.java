package com.skax.aiplatform.dto.deploy.response;

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
    description = "Agent Gateway 스트리밍 응답",
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
    
    /**
     * 최종 청크 여부
     * 
     * <p>현재 청크가 응답의 마지막 부분인지를 나타냅니다.
     * true인 경우 스트리밍이 완료된 것입니다.</p>
     */
    @Schema(
        description = "최종 청크 여부", 
        example = "false"
    )
    private Boolean isFinal;
    
    /**
     * 스트리밍 메타데이터
     * 
     * <p>스트리밍 과정의 메타데이터입니다.
     * 청크 인덱스, 처리 시간 등의 정보를 포함할 수 있습니다.</p>
     */
    @Schema(
        description = "스트리밍 메타데이터",
        example = "{\"chunk_index\": 1, \"total_chunks\": null, \"processing_time\": 0.1}"
    )
    private Object metadata;
}
