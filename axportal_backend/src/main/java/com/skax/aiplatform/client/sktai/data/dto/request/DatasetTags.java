package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 태그 DTO
 * 
 * <p>
 * OpenAPI 스펙에 따르면 name 필드만 가집니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 태그 정보")
public class DatasetTags {

    @JsonProperty("name")
    @Schema(description = "태그 이름", required = true)
    private String name;
}
