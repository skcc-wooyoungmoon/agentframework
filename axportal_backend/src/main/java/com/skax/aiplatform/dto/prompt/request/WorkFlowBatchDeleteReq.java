package com.skax.aiplatform.dto.prompt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "워크플로우 일괄 삭제 요청")
public class WorkFlowBatchDeleteReq {
    
    @NotEmpty
    @Schema(description = "삭제할 워크플로우 ID 목록", example = "[\"ED93FBF3\", \"ABC12345\", \"DEF67890\"]")
    private List<String> ids;
}

