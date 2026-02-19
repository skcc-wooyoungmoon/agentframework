package com.skax.aiplatform.client.ione.statistics;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.config.IoneFeignConfig;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiGroupStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiKeyGroupStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiKeyRatelimitStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.StatisticTypeRatelimitStatistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * iONE 통계정보 조회 클라이언트
 * 
 * <p>통계정보 조회 API와의 통신을 담당하는 Feign Client입니다.
 * 6개의 주요 통계 API를 제공합니다:</p>
 * 
 * <ul>
 *   <li>API 호출 통계</li>
 *   <li>API ID별 호출 통계</li>
 *   <li>API KEY 호출 통계</li>
 *   <li>API KEY별 호출 통계</li>
 *   <li>API KEY RateLimit 호출 통계</li>
 *   <li>통계 유형별 RateLimit 호출 통계</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@FeignClient(
    name = "ione-statistics-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneStatisticsClient {

    /**
     * [API-STS-001] API 호출 통계
     * 
     * <p>API 호출에 대한 전반적인 통계 정보를 조회합니다.</p>
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param apiId API ID (선택사항)
     * @return API 호출 통계
     */
    @GetMapping("/admin/intf/v1/system/statistic/api.idt")
    @Operation(summary = "[API-STS-001] API 호출 통계", description = "API 호출에 대한 전반적인 통계 정보를 조회합니다")
    InfResponseBody<List<ApiStatistics>> getApiCallStatistics(
            @Parameter(description = "시작일 (YYYY-MM-DD)") @RequestParam("fromDtm") String fromDtm,
            @Parameter(description = "종료일 (YYYY-MM-DD)") @RequestParam("toDtm") String toDtm,
            @Parameter(description = "그룹화 기준 (HR)") @RequestParam("groupType") String groupType,
            @Parameter(description = "API ID (선택사항)") @RequestParam(value = "apiId", required = false) String apiId
    );

    /**
     * [API-STS-002] API ID별 호출 통계
     * 
     * <p>각 API ID별로 그룹화된 호출 통계를 조회합니다.</p>
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param groupBy 그룹화 기준 (hour, day, month)
     * @return API ID별 호출 통계
     */
    @GetMapping("/admin/intf/v1/system/statistic/apiGroup.idt")
    @Operation(summary = "[API-STS-002] API ID별 호출 통계", description = "각 API ID별로 그룹화된 호출 통계를 조회합니다")
    ApiGroupStatistics getApiGroupStatistics(
            @Parameter(description = "시작일 (YYYY-MM-DD)") @RequestParam("startDate") String startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)") @RequestParam("endDate") String endDate,
            @Parameter(description = "그룹화 기준 (hour, day, month)") @RequestParam(value = "groupBy", required = false) String groupBy
    );

    /**
     * [API-STS-003] API KEY 호출 통계
     * 
     * <p>API KEY 사용에 대한 호출 통계를 조회합니다.</p>
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param apiKey API KEY (선택사항)
     * @return API KEY 호출 통계
     */
    @GetMapping("/admin/intf/v1/system/statistic/apiKey.idt")
    @Operation(summary = "[API-STS-003] API KEY 호출 통계", description = "API KEY 사용에 대한 호출 통계를 조회합니다")
    InfResponseBody<List<ApiStatistics>> getApiKeyStatistics(
        @Parameter(description = "시작일 (YYYY-MM-DD)") @RequestParam("fromDtm") String fromDtm,
        @Parameter(description = "종료일 (YYYY-MM-DD)") @RequestParam("toDtm") String toDtm,
        @Parameter(description = "그룹화 기준 (hour, day, month)") @RequestParam(value = "groupType", required = false) String groupType,
        @Parameter(description = "API KEY (선택사항)") @RequestParam(value = "apiKey", required = false) String apiKey
    );

    /**
     * [API-STS-004] API KEY별 호출 통계
     * 
     * <p>각 API KEY별로 그룹화된 호출 통계를 조회합니다.</p>
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param groupBy 그룹화 기준 (hour, day, month)
     * @return API KEY별 호출 통계
     */
    @GetMapping("/admin/intf/v1/system/statistic/apiKeyGroup.idt")
    @Operation(summary = "[API-STS-004] API KEY별 호출 통계", description = "각 API KEY별로 그룹화된 호출 통계를 조회합니다")
    InfResponseBody<ApiKeyGroupStatistics> getApiKeyGroupStatistics(
            @Parameter(description = "시작일 (YYYY-MM-DD)") @RequestParam("fromDtm") String fromDtm,
            @Parameter(description = "종료일 (YYYY-MM-DD)") @RequestParam("toDtm") String toDtm,
            @Parameter(description = "그룹화 기준 (hour, day, month)") @RequestParam(value = "groupType", required = false) String groupType,
            @Parameter(description = "API KEY (선택사항)") @RequestParam(value = "apiKey", required = false) String apiKey
    );

    /**
     * [API-STS-005] API KEY RateLimit 호출 통계
     * 
     * <p>API KEY의 RateLimit 적용 및 호출 통계를 조회합니다.</p>
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param apiKey API KEY (선택사항)
     * @return API KEY RateLimit 호출 통계
     */
    @GetMapping("/admin/intf/v1/system/statistic/ratelimit/apiKey.idt")
    @Operation(summary = "[API-STS-005] API KEY RateLimit 호출 통계", description = "API KEY의 RateLimit 적용 및 호출 통계를 조회합니다")
    InfResponseBody<List<ApiKeyRatelimitStatistics>> getApiKeyRatelimitStatistics(
            @Parameter(description = "시작일 (YYYYMMDDHHMM)") @RequestParam("fromDtm") String fromDtm,
            @Parameter(description = "종료일 (YYYYMMDDHHMM)") @RequestParam("toDtm") String toDtm,
            @Parameter(description = "API KEY") @RequestParam("apiKey") String apiKey
    );

    /**
     * [API-STS-006] 통계 유형별 RateLimit 호출 통계
     * 
     * <p>지정된 통계 유형에 따른 RateLimit 호출 통계를 조회합니다.</p>
     * 
     * @param statisticType 통계 유형 (api, apikey, partner, policy)
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param filter 필터 조건 (선택사항)
     * @return 통계 유형별 RateLimit 호출 통계
     */
    @GetMapping("/admin/intf/v1/system/statistic/{statisticType}/stat.idt")
    @Operation(summary = "[API-STS-006] 통계 유형별 RateLimit 호출 통계", description = "지정된 통계 유형에 따른 RateLimit 호출 통계를 조회합니다")
    StatisticTypeRatelimitStatistics getStatisticTypeRatelimitStatistics(
            @Parameter(description = "통계 유형 (api, apikey, partner, policy)") @PathVariable("statisticType") String statisticType,
            @Parameter(description = "시작일 (YYYY-MM-DD)") @RequestParam("startDate") String startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)") @RequestParam("endDate") String endDate,
            @Parameter(description = "필터 조건 (선택사항)") @RequestParam(value = "filter", required = false) String filter
    );
}