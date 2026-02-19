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
@Schema(description = "워크플로우 삭제 응답")
public class WorkFlowDeleteRes {
    
    @Schema(description = "삭제 요청 총 개수", example = "5")
    private int totalCount;
    
    @Schema(description = "삭제 성공 개수", example = "4")
    private int successCount;
    
    @Schema(description = "삭제 실패 개수", example = "1")
    private int failCount;
}

