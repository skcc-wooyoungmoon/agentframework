package com.skax.aiplatform.client.sktai.resrcMgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * GPU 자원 관리 Feign Client
 * GPU 전용 Prometheus API 호출
 * 
 * @author SonMunWoo
 * @since 2025-10-29
 * @version 1.0
 */
@FeignClient(
    name = "resrc-mgmt-gpu-client",
    url = "${prometheus.api.gpu-base-url}",
    configuration = com.skax.aiplatform.client.sktai.resrcMgmt.config.ResrcMgmtFeignConfig.class
)
@Tag(name = "ResrcMgmtGpuClient", description = "GPU 자원 관리 API")
public interface ResrcMgmtGpuClient {
    
    /**
     * GPU Prometheus 쿼리 실행
     * 
     * @param query Prometheus 쿼리
     * @return 쿼리 결과
     */
    @GetMapping("/query")
    Object executeQuery(@RequestParam("query") String query);
    
    /**
     * GPU Prometheus Range 쿼리 실행 (시계열 데이터)
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

