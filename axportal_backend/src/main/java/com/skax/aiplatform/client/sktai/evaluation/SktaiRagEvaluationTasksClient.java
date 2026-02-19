package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.RagEvaluationTaskCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.TaskCreateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI RAG Evaluation Tasks Feign Client
 * 
 * <p>SKTAI Evaluation API의 RAG Evaluation Tasks 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-rag-evaluation-tasks-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiRagEvaluationTasksClient {

    @PostMapping("/api/v1/rag-evaluation-tasks")
    TaskCreateResponse createRagEvaluationTask(@RequestBody RagEvaluationTaskCreateRequest request);
}
