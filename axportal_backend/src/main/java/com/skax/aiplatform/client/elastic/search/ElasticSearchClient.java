package com.skax.aiplatform.client.elastic.search;

import com.skax.aiplatform.client.elastic.config.ElasticFeignConfig;
import com.skax.aiplatform.client.elastic.search.dto.request.IndexRequest;
import com.skax.aiplatform.client.elastic.search.dto.request.SearchRequest;
import com.skax.aiplatform.client.elastic.search.dto.response.IndexResponse;
import com.skax.aiplatform.client.elastic.search.dto.response.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Elasticsearch 검색 및 인덱싱 클라이언트
 * 
 * <p>Elasticsearch API와 통신하여 문서 검색 및 인덱싱 기능을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@FeignClient(
    name = "elastic-search-client",
    url = "${elastic.search.base-url}",
    configuration = ElasticFeignConfig.class
)
@Tag(name = "Elasticsearch API", description = "Elasticsearch 검색 및 인덱싱 API")
public interface ElasticSearchClient {

    /**
     * 인덱스 전체 검색 (GET) - 2 파라미터
     * 
     * @param index 인덱스명
     * @param query 검색 쿼리
     * @return 검색 결과
     */
    @GetMapping("/{index}/_search")
    @Operation(
        summary = "인덱스 전체 검색",
        description = "지정된 인덱스에서 쿼리를 이용하여 문서를 검색합니다."
    )
    SearchResponse searchIndex(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "검색 쿼리", example = "title:검색어")
        @RequestParam("q") String query
    );

    /**
     * 문서 검색 (GET) - 3 파라미터
     * 
     * @param index 인덱스명
     * @param query 검색 쿼리
     * @param size 검색 결과 수
     * @return 검색 결과
     */
    @GetMapping("/{index}/_search")
    @Operation(
        summary = "문서 검색 (크기 제한)",
        description = "지정된 인덱스에서 쿼리를 이용하여 제한된 수의 문서를 검색합니다."
    )
    SearchResponse searchDocuments(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "검색 쿼리", example = "title:검색어")
        @RequestParam("q") String query,
        
        @Parameter(description = "검색 결과 수", example = "10")
        @RequestParam("size") Integer size
    );

    /**
     * 고급 문서 검색 (GET) - 4 파라미터
     * 
     * @param index 인덱스명
     * @param query 검색 쿼리
     * @param size 검색 결과 수
     * @param fromIndex 시작 위치
     * @return 검색 결과
     */
    @GetMapping("/{index}/_search")
    @Operation(
        summary = "고급 문서 검색 (페이징)",
        description = "지정된 인덱스에서 쿼리를 이용하여 페이징된 문서를 검색합니다."
    )
    SearchResponse searchAdvanced(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "검색 쿼리", example = "title:검색어")
        @RequestParam("q") String query,
        
        @Parameter(description = "검색 결과 수", example = "10")
        @RequestParam("size") Integer size,
        
        @Parameter(description = "시작 위치", example = "0")
        @RequestParam("from") Integer fromIndex
    );

    /**
     * DSL 쿼리 검색 (POST) - 2 파라미터
     * 
     * @param index 인덱스명
     * @param searchRequest 검색 요청
     * @return 검색 결과
     */
    @PostMapping("/{index}/_search")
    @Operation(
        summary = "DSL 쿼리 검색",
        description = "JSON DSL 쿼리를 이용하여 문서를 검색합니다."
    )
    SearchResponse searchWithDsl(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "검색 요청 정보")
        @RequestBody SearchRequest searchRequest
    );


    /**
     * 다중 인덱스 검색 (POST) - 3 파라미터
     * 
     * @param index 인덱스명 (콤마로 구분)
     * @param searchRequest 검색 요청
     * @param allowNoIndices 인덱스가 없을 때 허용 여부
     * @return 검색 결과
     */
    @PostMapping("/{index}/_search")
    @Operation(
        summary = "다중 인덱스 검색",
        description = "여러 인덱스에서 JSON DSL 쿼리를 이용하여 문서를 검색합니다."
    )
    SearchResponse searchMultiIndex(
        @Parameter(description = "인덱스명 (콤마로 구분)", example = "index1,index2")
        @PathVariable("index") String index,
        
        @Parameter(description = "검색 요청 정보")
        @RequestBody SearchRequest searchRequest,
        
        @Parameter(description = "인덱스가 없을 때 허용 여부", example = "true")
        @RequestParam("allow_no_indices") Boolean allowNoIndices
    );

    /**
     * 문서 인덱싱 (POST) - 2 파라미터
     * 
     * @param index 인덱스명
     * @param indexRequest 인덱싱 요청
     * @return 인덱싱 결과
     */
    @PostMapping("/{index}/_doc")
    @Operation(
        summary = "문서 인덱싱",
        description = "지정된 인덱스에 문서를 추가합니다."
    )
    IndexResponse indexDocument(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "인덱싱 요청 정보")
        @RequestBody IndexRequest indexRequest
    );

    /**
     * 문서 인덱싱 with ID (POST) - 3 파라미터
     * 
     * @param index 인덱스명
     * @param id 문서 ID
     * @param indexRequest 인덱싱 요청
     * @return 인덱싱 결과
     */
    @PostMapping("/{index}/_doc/{id}")
    @Operation(
        summary = "문서 인덱싱 (ID 지정)",
        description = "지정된 인덱스에 ID를 지정하여 문서를 추가합니다."
    )
    IndexResponse indexDocumentWithId(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "문서 ID", example = "doc_001")
        @PathVariable("id") String id,
        
        @Parameter(description = "인덱싱 요청 정보")
        @RequestBody IndexRequest indexRequest
    );

    /**
     * 문서 업데이트 (POST) - 4 파라미터
     * 
     * @param index 인덱스명
     * @param id 문서 ID
     * @param indexRequest 업데이트 요청
     * @param refresh 리프레시 여부
     * @return 업데이트 결과
     */
    @PostMapping("/{index}/_update/{id}")
    @Operation(
        summary = "문서 업데이트",
        description = "지정된 인덱스의 문서를 업데이트합니다."
    )
    IndexResponse updateDocument(
        @Parameter(description = "인덱스명", example = "documents")
        @PathVariable("index") String index,
        
        @Parameter(description = "문서 ID", example = "doc_001")
        @PathVariable("id") String id,
        
        @Parameter(description = "업데이트 요청 정보")
        @RequestBody IndexRequest indexRequest,
        
        @Parameter(description = "리프레시 여부", example = "true")
        @RequestParam("refresh") Boolean refresh
    );
}