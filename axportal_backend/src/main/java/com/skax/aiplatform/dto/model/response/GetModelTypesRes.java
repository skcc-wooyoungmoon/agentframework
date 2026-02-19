package com.skax.aiplatform.dto.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 타입 목록 응답")
public class GetModelTypesRes {
      
    @Schema(description = "모델 타입 목록")
    private List<String> types;

    // @Schema(description = "타입 코드", example = "language")
    // private String code;
    
    // @Schema(description = "타입 이름", example = "언어 모델")
    // private String name;
    
    // @Schema(description = "타입 설명", example = "자연어 처리 및 생성이 가능한 언어 모델")
    // private String description;
    
    // @Schema(description = "사용 가능 여부", example = "true")
    // private Boolean isAvailable;
} 