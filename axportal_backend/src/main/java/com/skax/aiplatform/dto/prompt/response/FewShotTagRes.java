package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Few-Shot 태그 정보 응답")
public class FewShotTagRes {
    @Schema(description = "태그 UUID")
    private String tagUuid;
        
    @Schema(description = "태그 이름")
    private String tag;
        
    @Schema(description = "Few-Shot UUID")
    private String fewShotUuid;
        
    @Schema(description = "버전 ID")
    private String versionId;
}
