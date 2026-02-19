package com.skax.aiplatform.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SktaiAccessTokenRes {

    @Schema(description = "SKTAI Access Token")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "SKTAI Refresh Token")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "활성화 프로젝트 정보")
    @JsonProperty("project_info")
    private ProjectInfoRes projectInfo;
}
