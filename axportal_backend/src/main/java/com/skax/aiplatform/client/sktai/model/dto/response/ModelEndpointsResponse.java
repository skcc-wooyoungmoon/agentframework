package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Endpoints 응답 DTO
 *
 * @author kimyeri
 * @since 2025-08-30
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model Endpoints 응답 정보")
public class ModelEndpointsResponse {
    
    @JsonProperty("data")
    @Schema(description = "Endpoints 목록")
    private List<ModelEndpoint> data;
    

    private Payload payload;
    
    // Inner class for individual endpoint data
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKTAI Model Endpoint 정보")
    public static class ModelEndpoint {

        @JsonProperty("url")
        @Schema(description = "Endpoint URL")
        private String url;

        @JsonProperty("identifier")
        @Schema(description = "Endpoint Identifier")
        private String identifier;

        @JsonProperty("key")
        @Schema(description = "Endpoint Key")
        private String key;

        @JsonProperty("description")
        @Schema(description = "Endpoint Description")
        private String description;

        @JsonProperty("id")
        @Schema(description = "Endpoint ID")
        private String id;

        @JsonProperty("created_at")
        @Schema(description = "Endpoint Created At")
        private String created_at;

        @JsonProperty("updated_at")
        @Schema(description = "Endpoint Updated At")
        private String updated_at;
        
    }
}
