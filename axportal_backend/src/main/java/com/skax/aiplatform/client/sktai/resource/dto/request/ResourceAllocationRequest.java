package com.skax.aiplatform.client.sktai.resource.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI 리소스 할당 요청 DTO
 * 
 * <p>SKTAI 플랫폼에서 새로운 리소스를 할당하거나 기존 리소스를 조정하기 위한 요청 데이터 구조입니다.
 * 컴퓨팅 리소스, 스토리지, 네트워크 등의 할당을 관리합니다.</p>
 * 
 * <h3>할당 가능한 리소스:</h3>
 * <ul>
 *   <li><strong>컴퓨팅</strong>: CPU 코어, GPU, 메모리</li>
 *   <li><strong>스토리지</strong>: 디스크 용량, IOPS</li>
 *   <li><strong>네트워크</strong>: 대역폭, 포트</li>
 *   <li><strong>AI 모델</strong>: 모델 인스턴스, 추론 슬롯</li>
 *   <li><strong>데이터베이스</strong>: 연결풀, 캐시</li>
 * </ul>
 * 
 * <h3>할당 전략:</h3>
 * <ul>
 *   <li><strong>즉시 할당</strong>: 요청 즉시 리소스 할당</li>
 *   <li><strong>예약 할당</strong>: 지정된 시간에 할당</li>
 *   <li><strong>조건부 할당</strong>: 특정 조건 충족 시 할당</li>
 *   <li><strong>자동 스케일링</strong>: 부하에 따른 동적 할당</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ResourceAllocationRequest request = ResourceAllocationRequest.builder()
 *     .resourceType("gpu")
 *     .quantity(2)
 *     .specifications(Map.of("memory", "16GB", "type", "A100"))
 *     .priority("high")
 *     .duration("24h")
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
    description = "SKTAI 리소스 할당 요청",
    example = """
        {
          "resource_type": "gpu",
          "quantity": 2,
          "specifications": {
            "memory": "16GB",
            "type": "A100",
            "cuda_cores": 6912
          },
          "priority": "high",
          "duration": "24h",
          "allocation_strategy": "immediate",
          "project_id": "project-123",
          "tags": {
            "environment": "production",
            "team": "ml-ops"
          }
        }
        """
)
public class ResourceAllocationRequest {
    
    /**
     * 할당할 리소스 타입
     */
    @JsonProperty("resource_type")
    @Schema(
        description = "할당할 리소스 타입",
        example = "gpu",
        allowableValues = {
            "cpu", "gpu", "memory", "storage", "network",
            "model_instance", "database_connection", "cache"
        }
    )
    private String resourceType;
    
    /**
     * 할당할 리소스 수량
     */
    @JsonProperty("quantity")
    @Schema(
        description = "할당할 리소스 수량",
        example = "2",
        minimum = "1"
    )
    private Integer quantity;
    
    /**
     * 리소스 상세 사양
     */
    @JsonProperty("specifications")
    @Schema(
        description = "리소스 상세 사양 및 요구사항",
        example = """
            {
              "memory": "16GB",
              "type": "A100",
              "cuda_cores": 6912,
              "tensor_cores": 432
            }
            """
    )
    private Map<String, Object> specifications;
    
    /**
     * 할당 우선순위
     */
    @JsonProperty("priority")
    @Schema(
        description = "할당 우선순위",
        example = "high",
        allowableValues = {"low", "normal", "high", "critical"}
    )
    private String priority;
    
    /**
     * 할당 지속 시간
     */
    @JsonProperty("duration")
    @Schema(
        description = "리소스 할당 지속 시간 (예: 1h, 24h, 7d, permanent)",
        example = "24h"
    )
    private String duration;
    
    /**
     * 할당 전략
     */
    @JsonProperty("allocation_strategy")
    @Schema(
        description = "리소스 할당 전략",
        example = "immediate",
        allowableValues = {"immediate", "scheduled", "conditional", "auto_scale"}
    )
    private String allocationStrategy;
    
    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(
        description = "리소스를 할당받을 프로젝트 ID",
        example = "project-123"
    )
    private String projectId;
    
    /**
     * 사용자 ID
     */
    @JsonProperty("user_id")
    @Schema(
        description = "리소스 요청 사용자 ID",
        example = "user-456"
    )
    private String userId;
    
    /**
     * 지역/존 선호도
     */
    @JsonProperty("region_preference")
    @Schema(
        description = "선호하는 지역 또는 가용 영역",
        example = "kr-central-1"
    )
    private String regionPreference;
    
    /**
     * 비용 제한
     */
    @JsonProperty("cost_limit")
    @Schema(
        description = "최대 허용 비용 (시간당)",
        example = "100.0"
    )
    private Double costLimit;
    
    /**
     * 자동 스케일링 설정
     */
    @JsonProperty("auto_scaling")
    @Schema(
        description = "자동 스케일링 설정",
        example = """
            {
              "enabled": true,
              "min_instances": 1,
              "max_instances": 10,
              "target_utilization": 70,
              "scale_up_threshold": 80,
              "scale_down_threshold": 30
            }
            """
    )
    private Map<String, Object> autoScaling;
    
    /**
     * 네트워크 요구사항
     */
    @JsonProperty("network_requirements")
    @Schema(
        description = "네트워크 관련 요구사항",
        example = """
            {
              "bandwidth": "10Gbps",
              "latency": "<1ms",
              "vpc_id": "vpc-123",
              "security_groups": ["sg-456"]
            }
            """
    )
    private Map<String, Object> networkRequirements;
    
    /**
     * 백업 및 복구 설정
     */
    @JsonProperty("backup_settings")
    @Schema(
        description = "백업 및 복구 설정",
        example = """
            {
              "enabled": true,
              "frequency": "daily",
              "retention_days": 30,
              "encryption": true
            }
            """
    )
    private Map<String, Object> backupSettings;
    
    /**
     * 리소스 태그
     */
    @JsonProperty("tags")
    @Schema(
        description = "리소스 관리를 위한 태그",
        example = """
            {
              "environment": "production",
              "team": "ml-ops",
              "cost_center": "ai-research",
              "owner": "john.doe@company.com"
            }
            """
    )
    private Map<String, String> tags;
    
    /**
     * 알림 설정
     */
    @JsonProperty("notifications")
    @Schema(
        description = "할당 완료/실패 알림 설정",
        example = """
            {
              "email": ["admin@company.com"],
              "slack": "#ml-ops",
              "webhook": "https://api.company.com/webhooks/resource"
            }
            """
    )
    private Map<String, Object> notifications;
}
