package com.skax.aiplatform.client.udp.elasticsearch.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elasticsearch Index 생성 응답 DTO
 * 
 * <p>
 * Elasticsearch Index 생성 결과를 담는 응답 데이터입니다.
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
@Schema(description = "Elasticsearch Index 생성 응답")
public class IndexCreateResponse {

    /**
     * 생성 성공 여부
     */
    @JsonProperty("acknowledged")
    @Schema(description = "생성 성공 여부", example = "true")
    private Boolean acknowledged;

    /**
     * 샤드 생성 성공 여부
     */
    @JsonProperty("shards_acknowledged")
    @Schema(description = "샤드 생성 성공 여부", example = "true")
    private Boolean shardsAcknowledged;

    /**
     * Index 이름
     */
    @JsonProperty("index")
    @Schema(description = "Index 이름", example = "gaf_default_rag_test")
    private String index;
}

