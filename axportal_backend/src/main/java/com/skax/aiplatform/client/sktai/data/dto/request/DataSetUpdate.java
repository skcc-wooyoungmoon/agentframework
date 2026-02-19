package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 업데이트 요청 DTO
 * 
 * <p>OpenAPI 스펙에 따르면 description과 project_id만 필수 필드입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 업데이트 요청 정보")
public class DataSetUpdate {
    
    @JsonProperty("description")
    @Schema(description = "데이터셋 설명", required = true)
    private String description;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", required = true)
    private String projectId;
    
    @JsonProperty("policy")
    @Schema(description = "정책 설정")
    private Object policy;
}
