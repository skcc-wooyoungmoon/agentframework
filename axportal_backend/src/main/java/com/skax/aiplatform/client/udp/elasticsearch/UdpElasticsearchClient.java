package com.skax.aiplatform.client.udp.elasticsearch;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.udp.elasticsearch.config.UdpElasticsearchConfig;
import com.skax.aiplatform.client.udp.elasticsearch.dto.request.IndexCreateRequest;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexCreateResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexExistsResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexListResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * UDP Elasticsearch API 클라이언트
 * 
 * <p>
 * UDP 환경의 Elasticsearch와 연동하여 Index 관리 기능을 제공합니다.
 * </p>
 * 
 * <h3>연결 정보:</h3>
 * <ul>
 *   <li><strong>URL</strong>: https://elasticsearch.didim365.app:9200</li>
 *   <li><strong>인증</strong>: Basic Auth (elastic / w1FrGPO3Fc1i71mqf931UJ30)</li>
 *   <li><strong>SSL</strong>: 개발환경에서 검증 우회</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>Index 목록 조회 (_cat/indices)</li>
 *   <li>Index 존재 여부 확인 (HEAD /{index})</li>
 *   <li>Index 생성 (PUT /{index})</li>
 *   <li>Index 삭제 (DELETE /{index})</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@FeignClient(
    name = "udp-elasticsearch-client",
    url = "${udp.elasticsearch.base-url}",
    configuration = UdpElasticsearchConfig.class
)
@Tag(name = "UDP Elasticsearch API", description = "UDP Elasticsearch Index 관리 API")
public interface UdpElasticsearchClient {

    /**
     * Index 목록 조회
     * 
     * <p>Elasticsearch의 모든 Index 목록을 조회합니다.</p>
     * 
     * @param format 응답 포맷 (json)
     * @return Index 목록
     */
    @GetMapping("/_cat/indices")
    @Operation(
        summary = "Index 목록 조회",
        description = "Elasticsearch의 모든 Index 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Index 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    IndexListResponse listIndices(
        @Parameter(description = "응답 포맷", example = "json")
        @RequestParam(value = "format", defaultValue = "json") String format
    );

    /**
     * Index 존재 여부 확인
     * 
     * <p>지정된 Index가 존재하는지 확인합니다.</p>
     * 
     * @param indexName Index 이름
     * @return Index 존재 여부
     */
    @GetMapping("/{indexName}")
    @Operation(
        summary = "Index 존재 여부 확인",
        description = "지정된 Index가 존재하는지 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Index 존재"),
        @ApiResponse(responseCode = "404", description = "Index 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    IndexExistsResponse indexExists(
        @Parameter(description = "Index 이름", example = "gaf_default_rag_test")
        @PathVariable("indexName") String indexName
    );

    /**
     * Index 생성
     * 
     * <p>새로운 Elasticsearch Index를 생성합니다.</p>
     * 
     * @param indexName Index 이름
     * @param request Index 생성 요청 (mappings, settings)
     * @return Index 생성 결과
     */
    @PutMapping("/{indexName}")
    @Operation(
        summary = "Index 생성",
        description = "새로운 Elasticsearch Index를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Index 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    IndexCreateResponse createIndex(
        @Parameter(description = "Index 이름", example = "gaf_default_rag_test")
        @PathVariable("indexName") String indexName,
        
        @Parameter(description = "Index 생성 요청")
        @RequestBody IndexCreateRequest request,

        @Parameter(description = "활성 샤드 대기 옵션", example = "all")
        @RequestParam(value = "wait_for_active_shards", required = false) String waitForActiveShards
    );

    /**
     * Index 삭제
     * 
     * <p>지정된 Elasticsearch Index를 삭제합니다.</p>
     * 
     * @param indexName Index 이름
     */
    @DeleteMapping("/{indexName}")
    @Operation(
        summary = "Index 삭제",
        description = "지정된 Elasticsearch Index를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Index 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "Index 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    void deleteIndex(
        @Parameter(description = "Index 이름", example = "gaf_default_rag_test")
        @PathVariable("indexName") String indexName
    );

    /**
     * Elasticsearch 데이터 검색
     *
     * <p>지정된 Index에서 데이터를 검색합니다.</p>
     *
     * @param indexName Index 이름
     * @param searchBody 검색 쿼리
     * @return 검색 결과
     */
    @PostMapping("/{indexName}/_search")
    @Operation(
            summary = "데이터 검색",
            description = "지정된 Index에서 조건에 맞는 데이터를 검색합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색 쿼리"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "Index 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    SearchResponse searchData(
            @Parameter(description = "Index 이름", example = "gaf_datasets")
            @PathVariable("indexName") String indexName,

            @Parameter(description = "검색 쿼리")
            @RequestBody Object searchBody
    );

    /**
     * 데이터 삽입
     * 
     * <p>지정된 Index에 데이터를 삽입합니다.</p>
     * 
     * @param indexName Index 이름
     * @param document 데이터 내용
     * @return 삽입 결과
     */
    @PostMapping("/{indexName}/_doc")
    @Operation(
        summary = "데이터 삽입",
        description = "지정된 Index에 데이터를 삽입합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "데이터 삽입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Index 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    IndexResponse insertData(
        @Parameter(description = "Index 이름", example = "gaf_datasets")
        @PathVariable("indexName") String indexName,
        
        @Parameter(description = "데이터 내용")
        @RequestBody Object document
    );

    /**
     * 지식용 Elasticsearch 인덱스 생성
     * 
     * @param indexName Index 이름
     * @return Index 생성 결과
     */
    @PutMapping("/{indexName}/knowledge")
    @Operation(
        summary = "지식용 인덱스 생성",
        description = "지식용 Elasticsearch Index를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인덱스 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    IndexCreateResponse createIndexForKnowledge(
        @Parameter(description = "Index 이름", example = "knowledge_index")
        @PathVariable("indexName") String indexName
    );

}

