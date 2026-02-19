package com.skax.aiplatform.client.sktai.knowledge;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RetrievalAdvancedRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RetrievalRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.TestRetrievalAdvancedRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.TestRetrievalRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RetrievalResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Knowledge 쿼리 검색 API 클라이언트
 * 
 * <p>SKTAI Knowledge API의 문서 검색 관련 기능을 제공하는 Feign Client입니다.
 * 사용자 질의에 대해 유사도 기반 문서 검색을 수행할 수 있습니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>기본 검색</strong>: 사용자 질의 기반 문서 검색</li>
 *   <li><strong>고급 검색</strong>: 상세 옵션을 적용한 전문가용 검색</li>
 *   <li><strong>테스트 검색</strong>: 검색 기능 테스트 및 검증</li>
 *   <li><strong>고급 테스트 검색</strong>: 고급 옵션 테스트</li>
 * </ul>
 * 
 * <h3>지원하는 검색 모드:</h3>
 * <ul>
 *   <li><strong>Dense</strong>: 벡터 기반 유사도 검색</li>
 *   <li><strong>Sparse</strong>: 키워드 기반 검색</li>
 *   <li><strong>Hybrid</strong>: Dense + Sparse 결합 검색</li>
 *   <li><strong>Semantic</strong>: 의미 기반 검색 (Azure AI Search 전용)</li>
 * </ul>
 * 
 * <h3>검색 결과:</h3>
 * <ul>
 *   <li>검색된 문서의 내용과 메타데이터</li>
 *   <li>사용자 질의와 문서의 연관 점수</li>
 *   <li>페이징 및 정렬 기능</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Tag(name = "SKTAI Knowledge Queries", description = "SKTAI Knowledge 쿼리 검색 API")
@FeignClient(
    name = "sktai-knowledge-queries-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiQueriesClient {

    /**
     * 사용자 질의 관련 유사도 높은 문서 검색
     * 
     * <p>주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다.
     * 기본적인 벡터 기반 유사도 검색을 수행합니다.</p>
     * 
     * @param request 검색 요청 정보 (질의문, Repository ID 포함)
     * @return 검색된 문서 목록과 유사도 점수
     * 
     * @apiNote Knowledge Repository에 인덱싱된 문서를 대상으로 검색합니다.
     */
    @Operation(
        summary = "사용자 질의 관련 유사도 높은 문서 검색",
        description = "주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검색된 문서의 내용과 메타데이터, 사용자 질의와 문서의 연관 점수를 리턴합니다.",
            content = @Content(schema = @Schema(implementation = RetrievalResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/queries")
    RetrievalResponse queries(@RequestBody RetrievalRequest request);

    /**
     * 사용자 질의 관련 유사도 높은 문서 검색 (전문가용)
     * 
     * <p>주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다.
     * 고급 검색 옵션을 사용하여 더 정교한 검색이 가능합니다.</p>
     * 
     * @param request 고급 검색 요청 정보 (검색 옵션, 필터, 정렬 등 포함)
     * @return 검색된 문서 목록과 유사도 점수
     * 
     * @apiNote 검색 모드, 필터, 스코어링 등 상세 옵션을 지원합니다.
     */
    @Operation(
        summary = "사용자 질의 관련 유사도 높은 문서 검색 (전문가용)",
        description = "주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다. (전문가용)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검색된 문서의 내용과 메타데이터, 사용자 질의와 문서의 연관 점수를 리턴합니다.",
            content = @Content(schema = @Schema(implementation = RetrievalResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/queries/advanced")
    RetrievalResponse queriesAdvanced(@RequestBody RetrievalAdvancedRequest request);

    /**
     * 사용자 질의 관련 유사도 높은 문서 검색 테스트
     * 
     * <p>주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다.
     * 테스트 목적으로 사용되며, 기본 검색 기능의 동작을 확인할 수 있습니다.</p>
     * 
     * @param request 테스트 검색 요청 정보
     * @return 검색된 문서 목록과 유사도 점수
     * 
     * @apiNote 개발 및 디버깅 목적으로 사용되는 테스트 엔드포인트입니다.
     */
    @Operation(
        summary = "사용자 질의 관련 유사도 높은 문서 검색 테스트",
        description = "주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다. (테스트용)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검색된 문서의 내용과 메타데이터, 사용자 질의와 문서의 연관 점수를 리턴합니다.",
            content = @Content(schema = @Schema(implementation = RetrievalResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/queries/test")
    RetrievalResponse testQueries(@RequestBody TestRetrievalRequest request);

    /**
     * 사용자 질의 관련 유사도 높은 문서 검색 테스트 (전문가용)
     * 
     * <p>주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다.
     * 고급 검색 옵션을 포함한 테스트 기능을 제공합니다.</p>
     * 
     * @param request 고급 테스트 검색 요청 정보
     * @return 검색된 문서 목록과 유사도 점수
     * 
     * @apiNote 전문가용 검색 기능의 테스트 및 검증에 사용됩니다.
     */
    @Operation(
        summary = "사용자 질의 관련 유사도 높은 문서 검색 테스트 (전문가용)",
        description = "주어진 사용자 질의에 대해 문서 검색 후, 관련 결과를 반환합니다. (고급 테스트용)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검색된 문서의 내용과 메타데이터, 사용자 질의와 문서의 연관 점수를 리턴합니다.",
            content = @Content(schema = @Schema(implementation = RetrievalResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/queries/test/advanced")
    RetrievalResponse testQueriesAdvanced(@RequestBody TestRetrievalAdvancedRequest request);
}
