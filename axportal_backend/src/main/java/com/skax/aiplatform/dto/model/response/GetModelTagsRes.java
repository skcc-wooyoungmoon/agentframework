package com.skax.aiplatform.dto.model.response;

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
@Schema(description = "모델 태그 응답")
public class GetModelTagsRes {
    
    @Schema(description = "모델 태그 목록")
    private List<String> tags;


    // @Schema(description = "태그 ID", example = "korean")
    // private String id;

    // @Schema(description = "태그 이름", example = "Korean")
    // private String name;
    
    // @Schema(description = "태그 색상", example = "#007bff")
    // private String color;
    
    // @Schema(description = "태그 설명")
    // private String description;
} 