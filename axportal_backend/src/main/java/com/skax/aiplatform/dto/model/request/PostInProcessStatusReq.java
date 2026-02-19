package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostInProcessStatusReq {

    @Deprecated
    @Schema(description = "id", example = "43", required = true)
    private String id;
    
    @Schema(description = "name", example = "test-model-name", required = true)
    private String name;

    @Schema(description = "success", example = "true", required = true)
    private boolean success;

    @Schema(description = "message", example = "성공하였습니다.")
    private String message;

    @Schema(description = "summary", example = "취약점 점검 후 요약본")
    private String summary;

    @Schema(description = "split_count", example = "1")
    private Integer split_count;
}
