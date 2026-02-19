package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.EvaluationTaskCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.TaskCreateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Evaluation Tasks Feign Client
 * 
 * <p>SKTAI Evaluation API의 Evaluation Tasks 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-evaluation-tasks-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiEvaluationTasksClient {

    @PostMapping("/api/v1/evaluation-tasks")
    TaskCreateResponse createEvaluationTask(@RequestBody EvaluationTaskCreateRequest request);
}
