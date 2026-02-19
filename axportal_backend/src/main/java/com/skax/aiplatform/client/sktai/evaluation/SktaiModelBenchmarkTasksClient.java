package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkTaskCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkTaskResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Model Benchmark Tasks Feign Client
 * 
 * <p>SKTAI Evaluation API의 Model Benchmark Tasks 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-model-benchmark-tasks-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiModelBenchmarkTasksClient {

    @PostMapping("/api/v1/model-benchmark-tasks")
    ModelBenchmarkTaskResponse createModelBenchmarkTask(@RequestBody ModelBenchmarkTaskCreateRequest request);
}
