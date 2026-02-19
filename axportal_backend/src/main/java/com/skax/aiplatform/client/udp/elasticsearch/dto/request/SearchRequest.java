package com.skax.aiplatform.client.udp.elasticsearch.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 범용적인 Elasticsearch 데이터 검색 요청 DTO
 * 
 * <p>
 * 더 범용적인 Elasticsearch 검색을 위해 인덱스명과 쿼리 바디만 받는 구조로 단순화되었습니다.
 * 실제 쿼리 조합은 서비스 계층에서 수행하며, 이 DTO는 전송 역할만 담당합니다.
 * </p>
 * 
 * @author 장지원
 * @since 2025-10-18
 * @version 1.0
 */
@Data
@Builder
@Schema(description = "범용적인 Elasticsearch 데이터 검색 요청")
public class SearchRequest {

    /**
     * 검색할 인덱스 이름
     */
    @Schema(description = "검색할 인덱스 이름", example = "gaf_datasets", required = true)
    private String indexName;

    /**
     * Elasticsearch 검색 쿼리 바디
     * 서비스 계층에서 조합된 완전한 쿼리를 받습니다.
     */
    @Schema(description = "Elasticsearch 검색 쿼리 바디 (JSON 형태의 Object)", 
            example = "{query:{\"match_all\":{}},\"size\":20,\"from\":0}")
    private Object queryBody;
}