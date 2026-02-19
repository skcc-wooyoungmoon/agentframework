package com.skax.aiplatform.client.sktai.resource.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI 리소스 스케일링 요청 DTO
 * 
 * <p>기존에 할당된 리소스의 용량을 늘리거나 줄이기 위한 스케일링 요청 데이터 구조입니다.
 * 수직 스케일링(Scale Up/Down)과 수평 스케일링(Scale Out/In)을 지원합니다.</p>
 * 
 * <h3>스케일링 유형:</h3>
 * <ul>
 *   <li><strong>수직 스케일링 (Scale Up/Down)</strong>: 단일 인스턴스의 성능 향상/축소</li>
 *   <li><strong>수평 스케일링 (Scale Out/In)</strong>: 인스턴스 수 증가/감소</li>
 *   <li><strong>하이브리드 스케일링</strong>: 수직/수평 스케일링 조합</li>
 *   <li><strong>예측적 스케일링</strong>: 예상 부하 기반 사전 스케일링</li>
 * </ul>
 * 
 * <h3>스케일링 트리거:</h3>
 * <ul>
 *   <li><strong>수동 스케일링</strong>: 사용자 직접 요청</li>
 *   <li><strong>메트릭 기반</strong>: CPU, 메모리 등 임계값 기반</li>
 *   <li><strong>스케줄 기반</strong>: 시간/날짜 기반 정기 스케일링</li>
 *   <li><strong>예측 기반</strong>: AI/ML 기반 부하 예측</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ResourceScalingRequest request = ResourceScalingRequest.builder()
 *     .resourceId("resource-123")
 *     .scalingAction("scale_out")
 *     .targetCapacity(5)
 *     .trigger("cpu_threshold")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 리소스 스케일링 요청",
    example = """
        {
          "resource_id": "resource-123",
          "scaling_action": "scale_out",
          "scaling_type": "horizontal",
          "target_capacity": 5,
          "trigger": "cpu_threshold",
          "trigger_conditions": {
            "cpu_usage": 80,
            "duration": "5m"
          },
          "rollback_enabled": true,
          "max_surge": 2
        }
        """
)
public class ResourceScalingRequest {
    
    /**
     * 스케일링 대상 리소스 ID
     */
    @JsonProperty("resource_id")
    @Schema(
        description = "스케일링할 리소스 ID",
        example = "resource-123",
        required = true
    )
    private String resourceId;
    
    /**
     * 스케일링 액션
     */
    @JsonProperty("scaling_action")
    @Schema(
        description = "수행할 스케일링 액션",
        example = "scale_out",
        allowableValues = {"scale_up", "scale_down", "scale_out", "scale_in"}
    )
    private String scalingAction;
    
    /**
     * 스케일링 타입
     */
    @JsonProperty("scaling_type")
    @Schema(
        description = "스케일링 타입",
        example = "horizontal",
        allowableValues = {"vertical", "horizontal", "hybrid"}
    )
    private String scalingType;
    
    /**
     * 목표 용량
     */
    @JsonProperty("target_capacity")
    @Schema(
        description = "스케일링 후 목표 용량 (인스턴스 수 또는 리소스 크기)",
        example = "5",
        minimum = "0"
    )
    private Integer targetCapacity;
    
    /**
     * 스케일링 트리거
     */
    @JsonProperty("trigger")
    @Schema(
        description = "스케일링 트리거 유형",
        example = "cpu_threshold",
        allowableValues = {
            "manual", "cpu_threshold", "memory_threshold", 
            "request_count", "response_time", "schedule", "predictive"
        }
    )
    private String trigger;
    
    /**
     * 트리거 조건
     */
    @JsonProperty("trigger_conditions")
    @Schema(
        description = "스케일링 트리거 조건",
        example = """
            {
              "cpu_usage": 80,
              "memory_usage": 85,
              "duration": "5m",
              "evaluation_periods": 2
            }
            """
    )
    private Map<String, Object> triggerConditions;
    
    /**
     * 최대 동시 변경 인스턴스 수
     */
    @JsonProperty("max_surge")
    @Schema(
        description = "동시에 변경할 수 있는 최대 인스턴스 수",
        example = "2"
    )
    private Integer maxSurge;
    
    /**
     * 최대 사용 불가 인스턴스 수
     */
    @JsonProperty("max_unavailable")
    @Schema(
        description = "스케일링 중 사용 불가능한 최대 인스턴스 수",
        example = "1"
    )
    private Integer maxUnavailable;
    
    /**
     * 스케일링 속도
     */
    @JsonProperty("scaling_speed")
    @Schema(
        description = "스케일링 진행 속도",
        example = "normal",
        allowableValues = {"slow", "normal", "fast", "immediate"}
    )
    private String scalingSpeed;
    
    /**
     * 롤백 활성화 여부
     */
    @JsonProperty("rollback_enabled")
    @Schema(
        description = "스케일링 실패 시 롤백 여부",
        example = "true"
    )
    private Boolean rollbackEnabled;
    
    /**
     * 롤백 조건
     */
    @JsonProperty("rollback_conditions")
    @Schema(
        description = "자동 롤백 조건",
        example = """
            {
              "health_check_failures": 3,
              "error_rate_threshold": 10,
              "timeout": "10m"
            }
            """
    )
    private Map<String, Object> rollbackConditions;
    
    /**
     * 쿨다운 기간
     */
    @JsonProperty("cooldown_period")
    @Schema(
        description = "다음 스케일링까지 대기 시간",
        example = "300s"
    )
    private String cooldownPeriod;
    
    /**
     * 웜업 시간
     */
    @JsonProperty("warmup_time")
    @Schema(
        description = "새 인스턴스가 완전히 활성화되기까지 소요 시간",
        example = "60s"
    )
    private String warmupTime;
    
    /**
     * 헬스체크 설정
     */
    @JsonProperty("health_check")
    @Schema(
        description = "스케일링된 인스턴스의 헬스체크 설정",
        example = """
            {
              "enabled": true,
              "endpoint": "/health",
              "interval": "30s",
              "timeout": "5s",
              "healthy_threshold": 2,
              "unhealthy_threshold": 3
            }
            """
    )
    private Map<String, Object> healthCheck;
    
    /**
     * 스케일링 정책
     */
    @JsonProperty("scaling_policy")
    @Schema(
        description = "스케일링 세부 정책",
        example = """
            {
              "min_capacity": 1,
              "max_capacity": 10,
              "desired_capacity": 3,
              "scale_out_increment": 1,
              "scale_in_decrement": 1,
              "target_tracking": {
                "metric": "cpu_utilization",
                "target_value": 70
              }
            }
            """
    )
    private Map<String, Object> scalingPolicy;
    
    /**
     * 알림 설정
     */
    @JsonProperty("notifications")
    @Schema(
        description = "스케일링 이벤트 알림 설정",
        example = """
            {
              "email": ["ops@company.com"],
              "slack": "#devops",
              "events": ["scaling_start", "scaling_complete", "scaling_failed"]
            }
            """
    )
    private Map<String, Object> notifications;
    
    /**
     * 강제 스케일링 여부
     */
    @JsonProperty("force")
    @Schema(
        description = "안전성 검사 무시하고 강제 스케일링 여부",
        example = "false"
    )
    private Boolean force;
}
