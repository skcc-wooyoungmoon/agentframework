package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.AuthorizeRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.AuthorizeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SKTAI Evaluation Auth Feign Client
 * 
 * <p>SKTAI Evaluation API의 인증 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-evaluation-auth-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "Evaluation Auth", description = "SKTAI Evaluation Auth API")
public interface SktaiEvaluationAuthClient {

    /**
     * 인증/인가 처리
     * 
     * @param request 인증 요청 데이터
     * @return 인증 결과
     */
    @PostMapping("/api/v1/auth/authorize")
    @Operation(
        summary = "인증/인가 처리",
        description = "사용자 인증 및 권한 확인을 수행합니다."
    )
    AuthorizeResponse authorize(
        @Parameter(description = "인증 요청 데이터", required = true)
        @RequestBody AuthorizeRequest request
    );
}
