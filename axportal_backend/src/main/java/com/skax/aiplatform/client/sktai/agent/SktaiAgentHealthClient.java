package com.skax.aiplatform.client.sktai.agent;

import com.skax.aiplatform.client.sktai.agent.dto.response.CommonResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SKTAI Agent Health API Client
 * 
 * <p>SKTAI Agent 시스템의 Health Check 기능을 제공하는 Feign Client입니다.
 * 서비스의 라이브니스(Liveness)와 레디니스(Readiness) 상태를 확인할 수 있습니다.</p>
 * 
 * <h3>제공 기능:</h3>
 * <ul>
 *   <li><strong>Liveness Check</strong>: 서비스가 살아있는지 확인</li>
 *   <li><strong>Readiness Check</strong>: 서비스가 요청을 처리할 준비가 되었는지 확인</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CommonResponse liveStatus = healthClient.checkLiveness();
 * CommonResponse readyStatus = healthClient.checkReadiness();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-agent-health-client",
    url = "${sktai.api.base-url}/api/v1/agent",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Agent Health", description = "SKTAI Agent Health Check API")
public interface SktaiAgentHealthClient {
    
    /**
     * 서비스 Liveness 상태 확인
     * 
     * <p>SKTAI Agent 서비스가 살아있는지 확인합니다.
     * 주로 Kubernetes의 Liveness Probe에서 사용됩니다.</p>
     * 
     * @return 서비스 Liveness 상태 응답
     * @apiNote 이 API는 인증이 필요하지 않습니다.
     * @since 1.0
     */
    @GetMapping("/health/live")
    @Operation(
        summary = "서비스 Liveness 확인",
        description = "SKTAI Agent 서비스가 살아있는지 확인합니다. Kubernetes Liveness Probe용 엔드포인트입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "서비스가 정상적으로 동작 중"),
        @ApiResponse(responseCode = "503", description = "서비스가 비정상 상태")
    })
    CommonResponse checkLiveness();
    
    /**
     * 서비스 Readiness 상태 확인
     * 
     * <p>SKTAI Agent 서비스가 요청을 처리할 준비가 되었는지 확인합니다.
     * 주로 Kubernetes의 Readiness Probe에서 사용됩니다.</p>
     * 
     * @return 서비스 Readiness 상태 응답
     * @apiNote 이 API는 인증이 필요하지 않습니다.
     * @since 1.0
     */
    @GetMapping("/health/ready")
    @Operation(
        summary = "서비스 Readiness 확인",
        description = "SKTAI Agent 서비스가 요청을 처리할 준비가 되었는지 확인합니다. Kubernetes Readiness Probe용 엔드포인트입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "서비스가 요청 처리 준비 완료"),
        @ApiResponse(responseCode = "503", description = "서비스가 요청 처리 준비 안됨")
    })
    CommonResponse checkReadiness();
}
