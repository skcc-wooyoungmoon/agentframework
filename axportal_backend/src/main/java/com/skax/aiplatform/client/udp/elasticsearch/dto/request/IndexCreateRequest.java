package com.skax.aiplatform.client.udp.elasticsearch.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Elasticsearch Index 생성 요청 DTO
 * 
 * <p>
 * Elasticsearch Index를 생성할 때 사용하는 요청 데이터입니다.
 * Mappings와 Settings를 포함할 수 있습니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch Index 생성 요청")
public class IndexCreateRequest {

    /**
     * Index Mappings (필드 정의)
     */
    @JsonProperty("mappings")
    @Schema(description = "Index Mappings (필드 정의)")
    private Map<String, Object> mappings;

    /**
     * Index Settings (설정)
     */
    @JsonProperty("settings")
    @Schema(description = "Index Settings (설정)")
    private Map<String, Object> settings;
}

