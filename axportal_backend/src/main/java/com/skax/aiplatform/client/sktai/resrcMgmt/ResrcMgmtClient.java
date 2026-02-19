package com.skax.aiplatform.client.sktai.resrcMgmt;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 자원 관리 공통 모듈 Feign Client
 * POST 호출 기반의 공통 자원 관리 API
 * 
 * @author SonMunWoo
 * @since 2025-09-27
 * @version 1.0
 */
@FeignClient(
    name = "resrc-mgmt-client",
    url = "${prometheus.api.base-url}",
    configuration = com.skax.aiplatform.client.sktai.resrcMgmt.config.ResrcMgmtFeignConfig.class
)
@Tag(name = "ResrcMgmtClient", description = "자원 관리 공통 모듈 API")
public interface ResrcMgmtClient {
    
    /**
     * 포탈 자원 현황 조회
     * 
     * @return 포탈 자원 현황 정보
     */
    @GetMapping
    Object getPortalResources();
    
    /**
     * Prometheus 쿼리 실행
     * 
     * @param query Prometheus 쿼리
     * @return 쿼리 결과
     */
    @GetMapping("/query")
    Object executeQuery(@RequestParam("query") String query);
    
    /**
     * Prometheus Range 쿼리 실행 (시계열 데이터) - ISO 날짜 형식
     * 
     * @param query Prometheus 쿼리
     * @param start 시작 날짜 (ISO 형식: 2025-09-27T14:30:00Z)
     * @param end 종료 날짜 (ISO 형식: 2025-09-30T14:30:00Z)
     * @param step 쿼리 간격 (초 단위)
     * @return 쿼리 결과 (시계열 데이터)
     */
    @GetMapping("/query_range")
    Object executeQueryRange(
        @RequestParam("query") String query,
        @RequestParam("start") String start,
        @RequestParam("end") String end,
        @RequestParam("step") String step
    );
}
