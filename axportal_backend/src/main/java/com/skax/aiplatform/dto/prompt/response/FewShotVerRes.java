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
@Schema(description = "Agent Few-Shot 버전 정보 응답")
public class FewShotVerRes {
    @Schema(description = "버전")
    private Integer version;

    @Schema(description = "릴리즈 여부")
    private Boolean release;

    @Schema(description = "삭제 여부")
    private Boolean deleteFlag;
    
    @Schema(description = "생성자 ID")
    private String createdBy;
    
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @Schema(description = "버전 ID")
    private String versionId;
    
    @Schema(description = "Few-Shot UUID")
    private String uuid;

}

