package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * SKTAI 리소스 사용량 응답 DTO
 * 
 * <p>SKTAI 플랫폼의 시스템 리소스 사용량 정보를 담는 응답 데이터 구조입니다.
 * 실시간 사용률, 기간별 통계, 트렌드 분석 등의 정보를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>현재 사용률</strong>: 실시간 CPU, 메모리, 디스크 사용률</li>
 *   <li><strong>기간별 통계</strong>: 평균, 최대, 최소 사용률</li>
 *   <li><strong>트렌드 데이터</strong>: 시간대별 사용 패턴</li>
 *   <li><strong>임계값 상태</strong>: 설정된 임계값 대비 현재 상태</li>
 *   <li><strong>예측 정보</strong>: 리소스 사용량 예측 데이터</li>
 * </ul>
 * 
 * <h3>메트릭 타입:</h3>
 * <ul>
 *   <li><strong>시스템 메트릭</strong>: CPU, 메모리, 디스크, 네트워크</li>
 *   <li><strong>애플리케이션 메트릭</strong>: API 응답시간, 처리량</li>
 *   <li><strong>비즈니스 메트릭</strong>: 사용자 수, 트랜잭션 수</li>
 *   <li><strong>커스텀 메트릭</strong>: 사용자 정의 지표</li>
 * </ul>
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
    description = "SKTAI 리소스 사용량 정보",
    example = """
        {
          "resource_id": "resource-123",
          "resource_type": "compute",
          "timestamp": "2025-08-15T12:30:45Z",
          "current_usage": {
            "cpu_percent": 68.5,
            "memory_percent": 74.2,
            "disk_percent": 45.1,
            "network_in_mbps": 120.5,
            "network_out_mbps": 95.3
          },
          "statistics": {
            "avg_cpu": 65.2,
            "max_cpu": 89.7,
            "min_cpu": 12.3,
            "avg_memory": 71.8,
            "max_memory": 87.4
          },
          "status": "normal",
          "alerts": []
        }
        """
)
public class ResourceUsageResponse {
    
    /**
     * 리소스 고유 식별자
     */
    @JsonProperty("resource_id")
    @Schema(
        description = "리소스 고유 ID",
        example = "resource-123"
    )
    private String resourceId;
    
    /**
     * 리소스 타입
     */
    @JsonProperty("resource_type")
    @Schema(
        description = "리소스 타입",
        example = "compute"
    )
    private String resourceType;
    
    /**
     * 리소스 이름
     */
    @JsonProperty("resource_name")
    @Schema(
        description = "리소스 이름",
        example = "ml-training-cluster-01"
    )
    private String resourceName;
    
    /**
     * 노드 ID
     */
    @JsonProperty("node_id")
    @Schema(
        description = "노드 ID",
        example = "node-456"
    )
    private String nodeId;
    
    /**
     * 측정 시간
     */
    @JsonProperty("timestamp")
    @Schema(
        description = "리소스 사용량 측정 시간",
        example = "2025-08-15T12:30:45Z"
    )
    private LocalDateTime timestamp;
    
    /**
     * 현재 사용량
     */
    @JsonProperty("current_usage")
    @Schema(
        description = "현재 리소스 사용량",
        example = """
            {
              "cpu_percent": 68.5,
              "memory_percent": 74.2,
              "memory_used_gb": 29.6,
              "memory_total_gb": 40.0,
              "disk_percent": 45.1,
              "disk_used_gb": 901.2,
              "disk_total_gb": 2000.0,
              "network_in_mbps": 120.5,
              "network_out_mbps": 95.3,
              "gpu_percent": 85.7,
              "gpu_memory_percent": 91.2
            }
            """
    )
    private Map<String, Object> currentUsage;
    
    /**
     * 기간별 통계
     */
    @JsonProperty("statistics")
    @Schema(
        description = "지정 기간 내 리소스 사용 통계",
        example = """
            {
              "period": "1h",
              "avg_cpu": 65.2,
              "max_cpu": 89.7,
              "min_cpu": 12.3,
              "95th_cpu": 82.1,
              "avg_memory": 71.8,
              "max_memory": 87.4,
              "min_memory": 58.3,
              "total_requests": 15420,
              "avg_response_time": 245
            }
            """
    )
    private Map<String, Object> statistics;
    
    /**
     * 트렌드 데이터
     */
    @JsonProperty("trend_data")
    @Schema(
        description = "시간대별 리소스 사용 트렌드",
        example = """
            [
              {
                "timestamp": "2025-08-15T12:00:00Z",
                "cpu": 62.1,
                "memory": 68.9,
                "requests": 1250
              },
              {
                "timestamp": "2025-08-15T12:15:00Z",
                "cpu": 68.5,
                "memory": 74.2,
                "requests": 1380
              }
            ]
            """
    )
    private List<Map<String, Object>> trendData;
    
    /**
     * 리소스 상태
     */
    @JsonProperty("status")
    @Schema(
        description = "리소스 전반적 상태",
        example = "normal",
        allowableValues = {"normal", "warning", "critical", "unknown"}
    )
    private String status;
    
    /**
     * 임계값 정보
     */
    @JsonProperty("thresholds")
    @Schema(
        description = "설정된 임계값과 현재 상태",
        example = """
            {
              "cpu_warning": 75.0,
              "cpu_critical": 90.0,
              "memory_warning": 80.0,
              "memory_critical": 95.0,
              "disk_warning": 85.0,
              "disk_critical": 95.0
            }
            """
    )
    private Map<String, Object> thresholds;
    
    /**
     * 활성 알림 목록
     */
    @JsonProperty("alerts")
    @Schema(
        description = "현재 활성화된 알림 목록",
        example = """
            [
              {
                "type": "cpu_warning",
                "message": "CPU 사용률이 75%를 초과했습니다",
                "severity": "warning",
                "triggered_at": "2025-08-15T12:25:30Z"
              }
            ]
            """
    )
    private List<Map<String, Object>> alerts;
    
    /**
     * 예측 정보
     */
    @JsonProperty("predictions")
    @Schema(
        description = "리소스 사용량 예측 정보",
        example = """
            {
              "next_hour": {
                "cpu_forecast": 72.3,
                "memory_forecast": 78.1,
                "confidence": 0.85
              },
              "next_day": {
                "peak_time": "14:00",
                "peak_cpu": 91.2,
                "avg_cpu": 68.7
              }
            }
            """
    )
    private Map<String, Object> predictions;
    
    /**
     * 성능 지표
     */
    @JsonProperty("performance_metrics")
    @Schema(
        description = "성능 관련 지표",
        example = """
            {
              "avg_response_time": 245,
              "99th_response_time": 890,
              "requests_per_second": 127.5,
              "error_rate": 0.02,
              "throughput": 1250.7,
              "cache_hit_rate": 0.89
            }
            """
    )
    private Map<String, Object> performanceMetrics;
    
    /**
     * 비용 정보
     */
    @JsonProperty("cost_info")
    @Schema(
        description = "리소스 사용 비용 정보",
        example = """
            {
              "hourly_cost": 12.50,
              "daily_cost": 300.00,
              "monthly_estimate": 9000.00,
              "currency": "USD",
              "billing_period": "hourly"
            }
            """
    )
    private Map<String, Object> costInfo;
    
    /**
     * 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(
        description = "리소스 관련 메타데이터",
        example = """
            {
              "region": "kr-central-1",
              "availability_zone": "kr-central-1a",
              "instance_type": "ml.p3.2xlarge",
              "os": "Ubuntu 20.04",
              "monitoring_agent": "cloudwatch-agent-1.2.3",
              "last_updated": "2025-08-15T12:30:45Z"
            }
            """
    )
    private Map<String, Object> metadata;
}
