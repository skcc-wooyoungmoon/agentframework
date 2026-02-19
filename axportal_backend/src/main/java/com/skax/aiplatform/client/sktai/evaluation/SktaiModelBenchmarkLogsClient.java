package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.LogStatusUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkLogsResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.LogUpdateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Model Benchmark Logs Feign Client
 * 
 * <p>SKTAI Evaluation API의 Model Benchmark Logs 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-model-benchmark-logs-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiModelBenchmarkLogsClient {

    @GetMapping("/api/v1/model-benchmark-logs")
    ModelBenchmarkLogsResponse getModelBenchmarkLogs(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    @PutMapping("/api/v1/model-benchmark-logs/{id}")
    LogUpdateResponse updateModelBenchmarkLogStatus(@PathVariable("id") Integer id, @RequestBody LogStatusUpdateRequest request);
}
