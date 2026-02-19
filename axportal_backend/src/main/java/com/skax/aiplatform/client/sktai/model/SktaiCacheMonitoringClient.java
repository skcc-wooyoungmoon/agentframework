package com.skax.aiplatform.client.sktai.model;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Cache Monitoring API FeignClient
 * 
 * <p>SKTAI Model 시스템의 Cache 모니터링을 위한 Feign 클라이언트입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-cache-monitoring-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Cache Monitoring", description = "SKTAI Cache 모니터링 API")
public interface SktaiCacheMonitoringClient {

    /**
     * Cache 통계 조회
     */
    @GetMapping("/api/v1/models/cache/stats")
    @Operation(summary = "Cache 통계 조회", description = "캐시 통계 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache 통계 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object getCacheStats(
            @Parameter(description = "키 접두사") @RequestParam(value = "prefix", required = false) String prefix
    );

    /**
     * Cache 통계 초기화
     */
    @PostMapping("/api/v1/models/cache/stats/reset")
    @Operation(summary = "Cache 통계 초기화", description = "캐시 통계를 초기화합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache 통계 초기화 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object resetCacheStats(
            @Parameter(description = "키 접두사") @RequestParam(value = "prefix", required = false) String prefix
    );

    /**
     * Cache 키 목록 조회
     */
    @GetMapping("/api/v1/models/cache/keys")
    @Operation(summary = "Cache 키 목록 조회", description = "모든 캐시 키를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache 키 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object getCacheKeys(
            @Parameter(description = "패턴") @RequestParam(value = "pattern", defaultValue = "*") String pattern,
            @Parameter(description = "최대 개수") @RequestParam(value = "max_count", defaultValue = "100") Integer maxCount
    );

    /**
     * 패턴으로 Cache 키 삭제
     */
    @DeleteMapping("/api/v1/models/cache/keys/pattern/{pattern}")
    @Operation(summary = "패턴으로 Cache 키 삭제", description = "특정 패턴과 일치하는 캐시 키를 안전하게 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache 키 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object deleteCacheKeysByPattern(
            @Parameter(description = "삭제할 키 패턴") @PathVariable("pattern") String pattern,
            @Parameter(description = "카운트") @RequestParam(value = "count", defaultValue = "100") Integer count
    );

    /**
     * Cache 전체 플러시
     */
    @DeleteMapping("/api/v1/models/cache/flush")
    @Operation(summary = "Cache 전체 플러시", description = "모든 캐시를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache 플러시 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object flushCache(
            @Parameter(description = "확인 플래그") @RequestParam(value = "confirm", defaultValue = "false") Boolean confirm
    );

    /**
     * Redis 정보 조회
     */
    @GetMapping("/api/v1/models/cache/info")
    @Operation(summary = "Redis 정보 조회", description = "Redis 서버 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redis 정보 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    Object getRedisInfo(
            @Parameter(description = "상세 정보 여부") @RequestParam(value = "detail", defaultValue = "false") Boolean detail
    );
}
