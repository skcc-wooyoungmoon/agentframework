package com.skax.aiplatform.controller.elastic;

import com.skax.aiplatform.client.elastic.search.dto.request.IndexRequest;
import com.skax.aiplatform.client.elastic.search.dto.request.SearchRequest;
import com.skax.aiplatform.client.elastic.search.dto.response.IndexResponse;
import com.skax.aiplatform.client.elastic.search.dto.response.SearchResponse;
import com.skax.aiplatform.client.elastic.search.service.ElasticSearchService;
import com.skax.aiplatform.common.response.AxResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Elasticsearch 테스트 컨트롤러
 * 
 * <p>Elasticsearch 클라이언트의 기능을 테스트하기 위한 컨트롤러입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@RestController
@RequestMapping("/v1/elastic")
@Tag(name = "Elasticsearch Test", description = "Elasticsearch 테스트 API")
@RequiredArgsConstructor
public class ElasticTestController {

    private final ElasticSearchService elasticSearchService;

    /**
     * 인덱스 전체 검색 (GET) - 2 파라미터
     */
    @GetMapping("/search/{index}")
    @Operation(summary = "인덱스 전체 검색", description = "지정된 인덱스에서 쿼리를 이용하여 문서를 검색합니다.")
    public AxResponseEntity<SearchResponse> searchIndex(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "검색 쿼리", example = "title:검색어")
            @RequestParam String query
    ) {
        SearchResponse response = elasticSearchService.searchIndex(index, query);
        return AxResponseEntity.ok(response, "인덱스 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * 문서 검색 (GET) - 3 파라미터
     */
    @GetMapping("/search/{index}/documents")
    @Operation(summary = "문서 검색 (크기 제한)", description = "지정된 인덱스에서 쿼리를 이용하여 제한된 수의 문서를 검색합니다.")
    public AxResponseEntity<SearchResponse> searchDocuments(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "검색 쿼리", example = "title:검색어")
            @RequestParam String query,
            
            @Parameter(description = "검색 결과 수", example = "10")
            @RequestParam Integer size
    ) {
        SearchResponse response = elasticSearchService.searchDocuments(index, query, size);
        return AxResponseEntity.ok(response, "문서 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * 고급 문서 검색 (GET) - 4 파라미터
     */
    @GetMapping("/search/{index}/advanced")
    @Operation(summary = "고급 문서 검색 (페이징)", description = "지정된 인덱스에서 쿼리를 이용하여 페이징된 문서를 검색합니다.")
    public AxResponseEntity<SearchResponse> searchAdvanced(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "검색 쿼리", example = "title:검색어")
            @RequestParam String query,
            
            @Parameter(description = "검색 결과 수", example = "10")
            @RequestParam Integer size,
            
            @Parameter(description = "시작 위치", example = "0")
            @RequestParam Integer fromIndex
    ) {
        SearchResponse response = elasticSearchService.searchAdvanced(index, query, size, fromIndex);
        return AxResponseEntity.ok(response, "고급 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * DSL 쿼리 검색 (POST) - 2 파라미터
     */
    @PostMapping("/search/{index}/dsl")
    @Operation(summary = "DSL 쿼리 검색", description = "JSON DSL 쿼리를 이용하여 문서를 검색합니다.")
    public AxResponseEntity<SearchResponse> searchWithDsl(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "검색 요청 정보")
            @Valid @RequestBody SearchRequest searchRequest
    ) {
        SearchResponse response = elasticSearchService.searchWithDsl(index, searchRequest);
        return AxResponseEntity.ok(response, "DSL 쿼리 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * 다중 인덱스 검색 (POST) - 3 파라미터
     */
    @PostMapping("/search/{index}/multi")
    @Operation(summary = "다중 인덱스 검색", description = "여러 인덱스에서 JSON DSL 쿼리를 이용하여 문서를 검색합니다.")
    public AxResponseEntity<SearchResponse> searchMultiIndex(
            @Parameter(description = "인덱스명 (콤마로 구분)", example = "index1,index2")
            @PathVariable String index,
            
            @Parameter(description = "검색 요청 정보")
            @Valid @RequestBody SearchRequest searchRequest,
            
            @Parameter(description = "인덱스가 없을 때 허용 여부", example = "true")
            @RequestParam Boolean allowNoIndices
    ) {
        SearchResponse response = elasticSearchService.searchMultiIndex(index, searchRequest, allowNoIndices);
        return AxResponseEntity.ok(response, "다중 인덱스 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * 문서 인덱싱 (POST) - 2 파라미터
     */
    @PostMapping("/index/{index}")
    @Operation(summary = "문서 인덱싱", description = "지정된 인덱스에 문서를 추가합니다.")
    public AxResponseEntity<IndexResponse> indexDocument(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "인덱싱 요청 정보")
            @Valid @RequestBody IndexRequest indexRequest
    ) {
        IndexResponse response = elasticSearchService.indexDocument(index, indexRequest);
        return AxResponseEntity.created(response, "문서 인덱싱이 성공적으로 완료되었습니다.");
    }

    /**
     * 문서 인덱싱 with ID (POST) - 3 파라미터
     */
    @PostMapping("/index/{index}/{id}")
    @Operation(summary = "문서 인덱싱 (ID 지정)", description = "지정된 인덱스에 ID를 지정하여 문서를 추가합니다.")
    public AxResponseEntity<IndexResponse> indexDocumentWithId(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "문서 ID", example = "doc_001")
            @PathVariable String id,
            
            @Parameter(description = "인덱싱 요청 정보")
            @Valid @RequestBody IndexRequest indexRequest
    ) {
        IndexResponse response = elasticSearchService.indexDocumentWithId(index, id, indexRequest);
        return AxResponseEntity.created(response, "문서 인덱싱(ID 지정)이 성공적으로 완료되었습니다.");
    }

    /**
     * 문서 업데이트 (POST) - 4 파라미터
     */
    @PostMapping("/update/{index}/{id}")
    @Operation(summary = "문서 업데이트", description = "지정된 인덱스의 문서를 업데이트합니다.")
    public AxResponseEntity<IndexResponse> updateDocument(
            @Parameter(description = "인덱스명", example = "documents")
            @PathVariable String index,
            
            @Parameter(description = "문서 ID", example = "doc_001")
            @PathVariable String id,
            
            @Parameter(description = "업데이트 요청 정보")
            @Valid @RequestBody IndexRequest indexRequest,
            
            @Parameter(description = "리프레시 여부", example = "true")
            @RequestParam Boolean refresh
    ) {
        IndexResponse response = elasticSearchService.updateDocument(index, id, indexRequest, refresh);
        return AxResponseEntity.updated(response, "문서 업데이트가 성공적으로 완료되었습니다.");
    }
}