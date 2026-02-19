package com.skax.aiplatform.dto.deploy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent App API 키 목록 응답 정보")
public class AppApiKeysRes {

    @Schema(description = "API 키 목록")
    private List<String> apiKeys;
    
}
