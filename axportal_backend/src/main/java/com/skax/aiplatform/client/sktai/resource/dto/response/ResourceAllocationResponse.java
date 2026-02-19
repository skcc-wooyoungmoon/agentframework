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
 * SKTAI 리소스 할당 응답 DTO
 * 
 * <p>리소스 할당 요청에 대한 결과를 담는 응답 데이터 구조입니다.
 * 할당된 리소스 정보, 상태, 접속 정보 등을 포함합니다.</p>
 * 
 * <h3>할당 상태:</h3>
 * <ul>
 *   <li><strong>pending</strong>: 할당 요청 처리 중</li>
 *   <li><strong>provisioning</strong>: 리소스 프로비저닝 중</li>
 *   <li><strong>active</strong>: 할당 완료 및 사용 가능</li>
 *   <li><strong>failed</strong>: 할당 실패</li>
 *   <li><strong>terminated</strong>: 할당 해제됨</li>
 * </ul>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>할당된 리소스 스펙</strong>: CPU, 메모리, 스토리지 등</li>
 *   <li><strong>접속 정보</strong>: 엔드포인트, 포트, 인증 정보</li>
 *   <li><strong>비용 정보</strong>: 시간당 비용, 예상 총 비용</li>
 *   <li><strong>모니터링 정보</strong>: 사용량 추적 엔드포인트</li>
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
    description = "SKTAI 리소스 할당 결과",
    example = """
        {
          "allocation_id": "alloc-123",
          "resource_id": "resource-456",
          "status": "active",
          "resource_type": "gpu",
          "allocated_at": "2025-08-15T12:30:45Z",
          "expires_at": "2025-08-16T12:30:45Z",
          "specifications": {
            "type": "A100",
            "memory": "40GB",
            "cuda_cores": 6912,
            "count": 2
          },
          "connection_info": {
            "endpoint": "gpu-cluster-01.sktai.io",
            "ssh_port": 22,
            "jupyter_port": 8888
          },
          "cost_info": {
            "hourly_rate": 12.50,
            "estimated_total": 300.00
          }
        }
        """
)
public class ResourceAllocationResponse {
    
    /**
     * 할당 고유 식별자
     */
    @JsonProperty("allocation_id")
    @Schema(
        description = "리소스 할당 고유 ID",
        example = "alloc-123"
    )
    private String allocationId;
    
    /**
     * 리소스 ID
     */
    @JsonProperty("resource_id")
    @Schema(
        description = "할당된 리소스 ID",
        example = "resource-456"
    )
    private String resourceId;
    
    /**
     * 할당 상태
     */
    @JsonProperty("status")
    @Schema(
        description = "리소스 할당 상태",
        example = "active",
        allowableValues = {"pending", "provisioning", "active", "failed", "terminated"}
    )
    private String status;
    
    /**
     * 리소스 타입
     */
    @JsonProperty("resource_type")
    @Schema(
        description = "할당된 리소스 타입",
        example = "gpu"
    )
    private String resourceType;
    
    /**
     * 리소스 이름
     */
    @JsonProperty("resource_name")
    @Schema(
        description = "할당된 리소스 이름",
        example = "GPU-Cluster-Production-01"
    )
    private String resourceName;
    
    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(
        description = "리소스가 할당된 프로젝트 ID",
        example = "project-123"
    )
    private String projectId;
    
    /**
     * 사용자 ID
     */
    @JsonProperty("user_id")
    @Schema(
        description = "리소스를 요청한 사용자 ID",
        example = "user-456"
    )
    private String userId;
    
    /**
     * 할당 시간
     */
    @JsonProperty("allocated_at")
    @Schema(
        description = "리소스 할당 완료 시간",
        example = "2025-08-15T12:30:45Z"
    )
    private LocalDateTime allocatedAt;
    
    /**
     * 만료 시간
     */
    @JsonProperty("expires_at")
    @Schema(
        description = "리소스 할당 만료 시간",
        example = "2025-08-16T12:30:45Z"
    )
    private LocalDateTime expiresAt;
    
    /**
     * 할당된 리소스 사양
     */
    @JsonProperty("specifications")
    @Schema(
        description = "할당된 리소스의 상세 사양",
        example = """
            {
              "type": "A100",
              "memory": "40GB",
              "cuda_cores": 6912,
              "tensor_cores": 432,
              "count": 2,
              "cpu_cores": 16,
              "ram": "128GB",
              "storage": "1TB SSD"
            }
            """
    )
    private Map<String, Object> specifications;
    
    /**
     * 접속 정보
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "리소스 접속을 위한 연결 정보",
        example = """
            {
              "endpoint": "gpu-cluster-01.sktai.io",
              "ssh_port": 22,
              "jupyter_port": 8888,
              "tensorboard_port": 6006,
              "username": "sktai-user",
              "ssh_key": "ssh-rsa AAAAB3...",
              "access_token": "eyJhbGciOiJIUzI1..."
            }
            """
    )
    private Map<String, Object> connectionInfo;
    
    /**
     * 네트워크 정보
     */
    @JsonProperty("network_info")
    @Schema(
        description = "네트워크 구성 정보",
        example = """
            {
              "vpc_id": "vpc-123",
              "subnet_id": "subnet-456",
              "security_groups": ["sg-789"],
              "public_ip": "203.0.113.12",
              "private_ip": "10.0.1.42",
              "bandwidth": "10Gbps"
            }
            """
    )
    private Map<String, Object> networkInfo;
    
    /**
     * 비용 정보
     */
    @JsonProperty("cost_info")
    @Schema(
        description = "리소스 할당 비용 정보",
        example = """
            {
              "hourly_rate": 12.50,
              "daily_rate": 300.00,
              "estimated_total": 300.00,
              "currency": "USD",
              "billing_model": "pay-per-use",
              "cost_breakdown": {
                "compute": 8.00,
                "storage": 2.50,
                "network": 2.00
              }
            }
            """
    )
    private Map<String, Object> costInfo;
    
    /**
     * 모니터링 정보
     */
    @JsonProperty("monitoring_info")
    @Schema(
        description = "리소스 모니터링 관련 정보",
        example = """
            {
              "metrics_endpoint": "https://metrics.sktai.io/resource-456",
              "logs_endpoint": "https://logs.sktai.io/resource-456",
              "dashboard_url": "https://dashboard.sktai.io/resources/456",
              "alerts_webhook": "https://alerts.sktai.io/webhook/123"
            }
            """
    )
    private Map<String, Object> monitoringInfo;
    
    /**
     * 사용 가능한 소프트웨어
     */
    @JsonProperty("software_stack")
    @Schema(
        description = "설치된 소프트웨어 스택 정보",
        example = """
            {
              "os": "Ubuntu 20.04 LTS",
              "python": "3.9.7",
              "cuda": "11.8",
              "pytorch": "2.0.1",
              "tensorflow": "2.13.0",
              "jupyter": "3.4.8",
              "docker": "24.0.5"
            }
            """
    )
    private Map<String, Object> softwareStack;
    
    /**
     * 백업 설정
     */
    @JsonProperty("backup_info")
    @Schema(
        description = "백업 및 복구 설정 정보",
        example = """
            {
              "enabled": true,
              "frequency": "daily",
              "retention_days": 30,
              "backup_location": "s3://sktai-backups/resource-456",
              "last_backup": "2025-08-15T02:00:00Z"
            }
            """
    )
    private Map<String, Object> backupInfo;
    
    /**
     * 할당 이력
     */
    @JsonProperty("allocation_history")
    @Schema(
        description = "할당 과정의 이력 정보",
        example = """
            [
              {
                "timestamp": "2025-08-15T12:28:30Z",
                "status": "pending",
                "message": "할당 요청 접수"
              },
              {
                "timestamp": "2025-08-15T12:29:15Z",
                "status": "provisioning",
                "message": "리소스 프로비저닝 시작"
              },
              {
                "timestamp": "2025-08-15T12:30:45Z",
                "status": "active",
                "message": "할당 완료"
              }
            ]
            """
    )
    private List<Map<String, Object>> allocationHistory;
    
    /**
     * 태그 정보
     */
    @JsonProperty("tags")
    @Schema(
        description = "리소스 관리를 위한 태그",
        example = """
            {
              "environment": "production",
              "team": "ml-ops",
              "cost_center": "ai-research",
              "owner": "john.doe@company.com",
              "auto_shutdown": "true"
            }
            """
    )
    private Map<String, String> tags;
    
    /**
     * 추가 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(
        description = "추가 메타데이터",
        example = """
            {
              "region": "kr-central-1",
              "availability_zone": "kr-central-1a",
              "provider": "aws",
              "cluster_id": "cluster-789",
              "allocation_strategy": "immediate",
              "auto_scaling_enabled": true
            }
            """
    )
    private Map<String, Object> metadata;
}
