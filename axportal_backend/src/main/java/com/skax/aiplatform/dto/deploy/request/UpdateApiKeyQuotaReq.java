package com.skax.aiplatform.dto.deploy.request;

import com.skax.aiplatform.dto.deploy.common.ApiKeyQuota;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "API Key 수정 요청")
public class UpdateApiKeyQuotaReq {
    private ApiKeyQuota quota;
}
