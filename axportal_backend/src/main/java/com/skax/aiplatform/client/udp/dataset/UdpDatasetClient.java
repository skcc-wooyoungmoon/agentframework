package com.skax.aiplatform.client.udp.dataset;

import com.skax.aiplatform.client.udp.config.UdpFeignConfig;
import com.skax.aiplatform.client.udp.dataset.dto.request.DatasetSearchRequest;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * UDP Dataset 검색 API 클라이언트
 * 
 * <p>
 * UDP Portal의 데이터셋 카드 검색 기능을 제공합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>데이터셋 검색</strong>: 검색어를 통한 데이터셋 카드 검색</li>
 * <li><strong>타입별 필터링</strong>: 데이터셋 카드 타입별 필터링</li>
 * <li><strong>메타데이터 제공</strong>: 데이터셋의 상세 메타데이터 정보</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Tag(name = "UDP Dataset API", description = "UDP 데이터셋 검색 API")
@FeignClient(name = "udp-dataset-client", url = "${udp.api.base-url}", configuration = UdpFeignConfig.class)
public interface UdpDatasetClient {

        /**
         * 데이터셋 카드 검색
         * 
         * <p>
         * 검색어와 데이터셋 카드 타입을 기반으로 데이터셋을 검색합니다.
         * </p>
         * 
         * @param apiKey x-cruz-api-key 헤더
         * @return 검색된 데이터셋 목록
         */
        @Operation(summary = "데이터셋 카드 검색", description = "검색어와 타입을 기반으로 데이터셋 카드를 검색합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 검색 성공", content = @Content(schema = @Schema(implementation = DatasetSearchResponse.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        @PostMapping(value = "/portal/udp/api/datasetcard/search/v2", consumes = MediaType.APPLICATION_JSON_VALUE)
        DatasetSearchResponse searchDataset(
                        @Parameter(description = "게이트웨이 API 키", required = true) @RequestHeader("x-cruz-api-key") String apiKey,

                        @Parameter(description = "JSON 요청 본문", required = true) @RequestBody DatasetSearchRequest requestBody);

        /**
         * UDP 엘라스틱서치 데이터셋 집계 조회 (YAML 설정값 사용)
         * 
         * <p>
         * UDP Elasticsearch를 통해 데이터셋의 코드와 이름 목록을 집계하여 조회합니다.
         * </p>
         * <p>
         * Authorization과 API Key는 YAML 설정에서 자동으로 로드됩니다.
         * </p>
         * 
         * @param requestBody 집계 요청 본문
         * @return UDP Elasticsearch 집계 결과
         */
        @Operation(summary = "UDP 엘라스틱서치 데이터셋 집계 조회 (YAML 설정값 사용)", description = "UDP Elasticsearch를 통해 데이터셋의 코드와 이름 목록을 집계하여 조회합니다. Authorization과 API Key는 YAML에서 자동 로드됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "UDP ES 집계 조회 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        @PostMapping(value = "/udp/es/udp_srch_datasetcard/_search", consumes = "application/json", produces = "application/json")
        Map<String, Object> searchUdpEsDatasetAggregation(
                        @Parameter(description = "인증 토큰", required = true) @RequestHeader("Authorization") String authorization,

                        @Parameter(description = "게이트웨이 API 키", required = true) @RequestHeader("x-cruz-api-key") String apiKey,

                        @Parameter(description = "집계 요청 본문", required = true) @RequestBody Map<String, Object> requestBody);
}