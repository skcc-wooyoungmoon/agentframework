package com.skax.aiplatform.client.ione.apikey.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open API Key 등록 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "IONE Open API Key 등록 요청",
    example = """
        {
          "scope": "[*]",
          "validForDays": 365,
          "openApiKeyAlias": "MY_API_KEY",
          "partnerId": "PARTNER001",
          "grpId": "GROUP001",
          "replenishIntervalType": "DAILY",
          "allowedCount": 1000
        }
        """
)
public class IntfOpenApiKeyRegistRequest {

    /**
     * API Key 범위 (JSON 문자열 형태)
     */
    @JsonProperty("scope")
    @Schema(
        description = "API Key 범위 (JSON 문자열 형태)",
        example = "[\"*\"]",
        required = true
    )
    private List<String> scope;

    @JsonProperty("expireAt")
    @Schema(
        description = "만료일",
        example = "20250101"
    )
    private String expireAt;

    /**
     * 유효 기간 (일 단위)
     */
    @JsonProperty("validForDays")
    @Schema(
        description = "유효 기간 (일 단위)",
        example = "365",
        required = true,
        minimum = "1",
        maximum = "3650"
    )
    private Integer validForDays;

    /**
     * API Key 별칭
     */
    @JsonProperty("openApiKeyAlias")
    @Schema(
        description = "API Key 별칭",
        example = "MY_API_KEY",
        maxLength = 100
    )
    private String openApiKeyAlias;

    /**
     * 파트너 ID
     */
    @JsonProperty("partnerId")
    @Schema(
        description = "파트너 ID",
        example = "PARTNER001",
        maxLength = 50
    )
    private String partnerId;

    /**
     * 그룹 ID
     */
    @JsonProperty("grpId")
    @Schema(
        description = "그룹 ID",
        example = "GROUP001",
        maxLength = 50
    )
    private String grpId;


    @JsonProperty("rateLimit")
    @Schema(
        description = "Ratelimit 정책",
        example = "{\"replenishIntervalType\": \"DAILY\", \"allowedCount\": 1000}"
    )
    private RateLimit rateLimit;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RateLimit {
        /**
         * 보충 간격 유형
         */
        @JsonProperty("replenishIntervalType")
        @Schema(
            description = "보충 간격 유형",
            example = "DAILY",
            allowableValues = {"DAILY", "WEEKLY", "MONTHLY"}
        )
        private String replenishIntervalType;
    
        /**
         * 허용 횟수
         */
        @JsonProperty("allowedCount")
        @Schema(
            description = "허용 횟수",
            example = "1000",
            minimum = "1"
        )
        private Number allowedCount;
    }

}