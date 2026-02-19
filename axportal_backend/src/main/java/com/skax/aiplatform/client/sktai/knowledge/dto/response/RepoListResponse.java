package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Repository 목록 응답 DTO
 * 
 * @author HyeleeLee
 * @since 2025-08-28
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Repository 목록 응답")

public class RepoListResponse {
    @JsonProperty("data")
    @Schema(description = "Repository 목록")
    private List<Repo> data;

    
    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;
}
    

