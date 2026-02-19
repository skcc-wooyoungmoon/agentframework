package com.skax.aiplatform.dto.deploy.request;

// import com.skax.aiplatform.dto.common.PageableReq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "API Key 조회 요청")
public class GetApiKeyStaticReq {
    
    // @Schema(description = "사용자ID", example = "user1234", required = false)
    // private String userId;

    @Schema(description = "시작일 (yyyyMMdd)", example = "202501010000", required = true)
    private String startDate;

    @Schema(description = "종료일 (yyyyMMddHHMM)", example = "202501010000", required = true)
    private String endDate;
    
    
}
