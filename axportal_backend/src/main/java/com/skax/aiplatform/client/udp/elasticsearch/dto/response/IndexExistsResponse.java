package com.skax.aiplatform.client.udp.elasticsearch.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Elasticsearch Index 존재 여부 응답 DTO
 * 
 * <p>
 * Elasticsearch Index 존재 여부 확인 결과를 담는 응답 데이터입니다.
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
@Schema(description = "Elasticsearch Index 존재 여부 응답")
public class IndexExistsResponse {

    /**
     * Index별 상세 정보
     * Key: Index 이름
     * Value: Index 상세 정보 (aliases, mappings, settings)
     */
    @Schema(description = "Index별 상세 정보")
    private Map<String, Map<String, Object>> indices;
}

