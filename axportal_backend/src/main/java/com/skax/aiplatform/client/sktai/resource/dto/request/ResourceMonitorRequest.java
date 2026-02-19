package com.skax.aiplatform.client.sktai.resource.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI 리소스 모니터링 요청 DTO
 * 
 * <p>SKTAI 플랫폼의 시스템 리소스 모니터링을 요청하기 위한 데이터 구조입니다.
 * CPU, 메모리, 디스크, 네트워크 등의 리소스 사용률과 성능 지표를 모니터링할 수 있습니다.</p>
 * 
 * <h3>모니터링 대상:</h3>
 * <ul>
 *   <li><strong>시스템 리소스</strong>: CPU, 메모리, 디스크 사용률</li>
 *   <li><strong>네트워크</strong>: 대역폭, 연결 수, 지연시간</li>
 *   <li><strong>애플리케이션</strong>: 프로세스별 리소스 사용량</li>
 *   <li><strong>데이터베이스</strong>: 연결풀, 쿼리 성능</li>
 *   <li><strong>API 서비스</strong>: 응답시간, 처리량, 오류율</li>
 * </ul>
 * 
 * <h3>모니터링 유형:</h3>
 * <ul>
 *   <li><strong>실시간 모니터링</strong>: 현재 상태 조회</li>
 *   <li><strong>기간별 모니터링</strong>: 지정 기간 내 리소스 사용 패턴</li>
 *   <li><strong>임계값 모니터링</strong>: 설정된 임계값 초과 여부</li>
 *   <li><strong>트렌드 분석</strong>: 리소스 사용 트렌드 및 예측</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ResourceMonitorRequest request = ResourceMonitorRequest.builder()
 *     .resourceTypes(Arrays.asList("cpu", "memory", "disk"))
 *     .interval("5m")
 *     .startTime(LocalDateTime.now().minusHours(1))
 *     .endTime(LocalDateTime.now())
 *     .aggregation("avg")
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
    description = "SKTAI 리소스 모니터링 요청",
    example = """
        {
          "resource_types": ["cpu", "memory", "disk", "network"],
          "interval": "5m",
          "start_time": "2025-08-15T11:00:00Z",
          "end_time": "2025-08-15T12:00:00Z",
          "aggregation": "avg",
          "granularity": "minute",
          "filters": {
            "node_id": "node-123",
            "service": "model-gateway"
          }
        }
        """
)
public class ResourceMonitorRequest {
    
    /**
     * 모니터링할 리소스 타입 목록
     */
    @JsonProperty("resource_types")
    @Schema(
        description = "모니터링할 리소스 타입 목록",
        example = "[\"cpu\", \"memory\", \"disk\", \"network\"]",
        allowableValues = {
            "cpu", "memory", "disk", "network", 
            "api", "database", "cache", "queue"
        }
    )
    private List<String> resourceTypes;
    
    /**
     * 모니터링 간격
     */
    @JsonProperty("interval")
    @Schema(
        description = "데이터 수집 간격 (1s, 1m, 5m, 15m, 1h, 1d)",
        example = "5m"
    )
    private String interval;
    
    /**
     * 모니터링 시작 시간
     */
    @JsonProperty("start_time")
    @Schema(
        description = "모니터링 시작 시간 (생략 시 현재 시간)",
        example = "2025-08-15T11:00:00Z"
    )
    private LocalDateTime startTime;
    
    /**
     * 모니터링 종료 시간
     */
    @JsonProperty("end_time")
    @Schema(
        description = "모니터링 종료 시간 (생략 시 현재 시간)",
        example = "2025-08-15T12:00:00Z"
    )
    private LocalDateTime endTime;
    
    /**
     * 데이터 집계 방식
     */
    @JsonProperty("aggregation")
    @Schema(
        description = "데이터 집계 방식",
        example = "avg",
        allowableValues = {"avg", "max", "min", "sum", "count", "95th", "99th"}
    )
    private String aggregation;
    
    /**
     * 데이터 세분화 수준
     */
    @JsonProperty("granularity")
    @Schema(
        description = "데이터 세분화 수준",
        example = "minute",
        allowableValues = {"second", "minute", "hour", "day"}
    )
    private String granularity;
    
    /**
     * 노드 ID 목록
     */
    @JsonProperty("node_ids")
    @Schema(
        description = "특정 노드들만 모니터링 (생략 시 전체)",
        example = "[\"node-123\", \"node-456\"]"
    )
    private List<String> nodeIds;
    
    /**
     * 서비스 필터
     */
    @JsonProperty("services")
    @Schema(
        description = "특정 서비스들만 모니터링",
        example = "[\"model-gateway\", \"agent-gateway\"]"
    )
    private List<String> services;
    
    /**
     * 임계값 설정
     */
    @JsonProperty("thresholds")
    @Schema(
        description = "알림을 위한 임계값 설정",
        example = """
            {
              "cpu_usage": 80.0,
              "memory_usage": 85.0,
              "disk_usage": 90.0,
              "response_time": 1000
            }
            """
    )
    private Object thresholds;
    
    /**
     * 추가 필터링 조건
     */
    @JsonProperty("filters")
    @Schema(
        description = "추가 필터링 조건",
        example = """
            {
              "region": "kr-central-1",
              "environment": "production",
              "tier": "web"
            }
            """
    )
    private Object filters;
    
    /**
     * 실시간 모니터링 여부
     */
    @JsonProperty("real_time")
    @Schema(
        description = "실시간 모니터링 활성화 여부",
        example = "false"
    )
    private Boolean realTime;
    
    /**
     * 알림 설정
     */
    @JsonProperty("alerts_enabled")
    @Schema(
        description = "임계값 초과 시 알림 활성화 여부",
        example = "true"
    )
    private Boolean alertsEnabled;
}
