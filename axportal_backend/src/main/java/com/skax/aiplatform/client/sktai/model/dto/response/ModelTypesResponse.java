package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "SKTAI 모델 타입 목록 응답")
public class ModelTypesResponse {

    @JsonProperty("types")
    @Schema(description = "모델 타입 목록")
    private List<ModelType> types;

    @Data
    @Schema(description = "모델 타입 정보")
    public static class ModelType {
        
        @JsonProperty("id")
        @Schema(description = "타입 ID", example = "text-generation")
        private String id;

        @JsonProperty("name")
        @Schema(description = "타입 이름", example = "Text Generation")
        private String name;

        @JsonProperty("description")
        @Schema(description = "타입 설명")
        private String description;

        @JsonProperty("category")
        @Schema(description = "카테고리", example = "NLP")
        private String category;
    }
}
