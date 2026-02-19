package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "SKTAI 모델 태그 목록 응답")
public class ModelTagsResponse {

    @JsonProperty("tags")
    @Schema(description = "모델 태그 목록")
    private List<ModelTag> tags;

    @Data
    @Schema(description = "모델 태그 정보")
    public static class ModelTag {
        
        @JsonProperty("id")
        @Schema(description = "태그 ID", example = "korean")
        private String id;

        @JsonProperty("name")
        @Schema(description = "태그 이름", example = "Korean")
        private String name;

        @JsonProperty("color")
        @Schema(description = "태그 색상", example = "#007bff")
        private String color;

        @JsonProperty("description")
        @Schema(description = "태그 설명")
        private String description;
    }
}
