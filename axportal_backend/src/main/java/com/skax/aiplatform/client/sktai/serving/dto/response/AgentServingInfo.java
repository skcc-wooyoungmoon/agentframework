package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 에이전트 서빙 정보 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 에이전트 서빙 정보를 조회할 때 반환되는 상세 정보입니다.
 * 에이전트 서빙의 기본 정보, 설정, 상태 등을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 앱 ID, 배포 이름, 설명</li>
 *   <li><strong>서빙 설정</strong>: 네임스페이스, KServe YAML, 프로젝트 ID</li>
 *   <li><strong>리소스 정보</strong>: CPU, GPU, 메모리 할당</li>
 *   <li><strong>스케일링 정보</strong>: 최소/최대 복제본, 오토스케일링 설정</li>
 *   <li><strong>보안 설정</strong>: 안전 필터</li>
 *   <li><strong>에이전트 설정</strong>: 모델 목록, 에이전트 파라미터, 앱 이미지</li>
 *   <li><strong>메타데이터</strong>: 생성/수정 시간, 생성자, 상태</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see AgentServingResponse 에이전트 서빙 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 에이전트 서빙 정보",
    example = """
        {
          "agent_serving_id": "agent-srv-123e4567-e89b-12d3-a456-426614174000",
          "app_id": "app-123e4567-e89b-12d3-a456-426614174000",
          "deployment_name": "chatbot-agent",
          "isvc_name": "chatbot-agent-isvc",
          "description": "고객 지원 챗봇 에이전트",
          "kserve_yaml": "apiVersion: serving.kserve.io/v1beta1...",
          "project_id": "proj-123e4567-e89b-12d3-a456-426614174000",
          "namespace": "agent-serving",
          "app_version": 1,
          "status": "Ready",
          "error_message": null,
          "gpu_type": "T4",
          "cpu_request": 2,
          "cpu_limit": 4,
          "gpu_request": 1,
          "gpu_limit": 1,
          "mem_request": 4096,
          "mem_limit": 8192,
          "created_by": "admin@example.com",
          "updated_by": "admin@example.com",
          "is_deleted": false,
          "safety_filter_input": true,
          "safety_filter_output": true,
          "min_replicas": 1,
          "max_replicas": 5,
          "autoscaling_class": "hpa",
          "autoscaling_metric": "cpu",
          "target": 70,
          "model_list": ["gpt-3.5-turbo", "claude-3"],
          "created_at": "2025-10-16T10:30:00Z",
          "updated_at": "2025-10-16T11:30:00Z",
          "endpoint": "https://agent-api.example.com/chat",
          "agent_params": "{\\"temperature\\": 0.7, \\"max_tokens\\": 1000}",
          "agent_app_image": "agent-chat:v1.0",
          "agent_app_image_registry": "docker.io",
          "app_config_file_path": "/config/agent.yaml",
          "serving_type": "private",
          "shared_backend_id": null
        }
        """
)
public class AgentServingInfo {
    
    /**
     * 에이전트 서빙 ID
     */
    @JsonProperty("agent_serving_id")
    @Schema(description = "에이전트 서빙 고유 식별자", example = "agent-srv-123e4567-e89b-12d3-a456-426614174000", format = "uuid", required = true)
    private String agentServingId;
    
    /**
     * 앱 ID
     */
    @JsonProperty("app_id")
    @Schema(description = "연관된 앱 ID", example = "app-123e4567-e89b-12d3-a456-426614174000", format = "uuid")
    private String appId;
    
    /**
     * 배포 이름
     */
    @JsonProperty("deployment_name")
    @Schema(description = "배포 이름", example = "chatbot-agent", defaultValue = "default_deployment", required = true)
    private String deploymentName;
    
    /**
     * InferenceService 이름
     */
    @JsonProperty("isvc_name")
    @Schema(description = "KServe InferenceService 이름", example = "chatbot-agent-isvc", defaultValue = "default_isvc_name", required = true)
    private String isvcName;
    
    /**
     * 서빙 설명
     */
    @JsonProperty("description")
    @Schema(description = "에이전트 서빙 설명", example = "고객 지원 챗봇 에이전트")
    private String description;
    
    /**
     * KServe YAML 설정
     */
    @JsonProperty("kserve_yaml")
    @Schema(description = "KServe 배포 YAML 설정")
    private String kserveYaml;
    
    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "proj-123e4567-e89b-12d3-a456-426614174000", defaultValue = "default_project_id", required = true)
    private String projectId;
    
    /**
     * 네임스페이스
     */
    @JsonProperty("namespace")
    @Schema(description = "Kubernetes 네임스페이스", example = "agent-serving")
    private String namespace;
    
    /**
     * 앱 버전
     */
    @JsonProperty("app_version")
    @Schema(description = "앱 버전", example = "1", defaultValue = "1")
    private Integer appVersion;
    
    /**
     * 서빙 상태
     */
    @JsonProperty("status")
    @Schema(description = "서빙 상태", example = "Ready", defaultValue = "Deploying", required = true)
    private String status;
    
    /**
     * 에러 메시지
     */
    @JsonProperty("error_message")
    @Schema(description = "에러 메시지 (오류 발생 시)")
    private String errorMessage;
    
    /**
     * GPU 타입
     */
    @JsonProperty("gpu_type")
    @Schema(description = "GPU 타입", example = "T4")
    private String gpuType;
    
    /**
     * CPU 요청량
     */
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량", example = "2", defaultValue = "0")
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     */
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량", example = "4", defaultValue = "0")
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     */
    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청량", example = "1", defaultValue = "0")
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     */
    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한량", example = "1", defaultValue = "0")
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     */
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량 (MB)", example = "4096", defaultValue = "0")
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     */
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량 (MB)", example = "8192", defaultValue = "0")
    private Integer memLimit;
    
    /**
     * 생성자
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "admin@example.com", required = true)
    private String createdBy;
    
    /**
     * 수정자
     */
    @JsonProperty("updated_by")
    @Schema(description = "수정자", example = "admin@example.com")
    private String updatedBy;
    
    /**
     * 삭제 여부
     */
    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", example = "false", defaultValue = "false", required = true)
    private Boolean isDeleted;
    
    /**
     * 입력 안전 필터
     */
    @JsonProperty("safety_filter_input")
    @Schema(description = "입력 안전 필터 활성화 여부", example = "true", defaultValue = "false", required = true)
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터
     */
    @JsonProperty("safety_filter_output")
    @Schema(description = "출력 안전 필터 활성화 여부", example = "true", defaultValue = "false", required = true)
    private Boolean safetyFilterOutput;
    
    /**
     * 최소 복제본 수
     */
    @JsonProperty("min_replicas")
    @Schema(description = "최소 복제본 수", example = "1")
    private Integer minReplicas;
    
    /**
     * 최대 복제본 수
     */
    @JsonProperty("max_replicas")
    @Schema(description = "최대 복제본 수", example = "5")
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     */
    @JsonProperty("autoscaling_class")
    @Schema(description = "오토스케일링 클래스", example = "hpa")
    private String autoscalingClass;
    
    /**
     * 오토스케일링 메트릭
     */
    @JsonProperty("autoscaling_metric")
    @Schema(description = "오토스케일링 메트릭", example = "cpu")
    private String autoscalingMetric;
    
    /**
     * 스케일링 목표값
     */
    @JsonProperty("target")
    @Schema(description = "스케일링 목표값", example = "70")
    private Integer target;
    
    /**
     * 모델 목록
     */
    @JsonProperty("model_list")
    @Schema(description = "에이전트에서 사용하는 모델 목록", example = "[\"gpt-3.5-turbo\", \"claude-3\"]", required = true)
    private List<String> modelList;
    
    /**
     * 생성 시간
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시간", example = "2025-10-16T10:30:00Z", format = "date-time", required = true)
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간", example = "2025-10-16T11:30:00Z", format = "date-time")
    private LocalDateTime updatedAt;
    
    /**
     * 엔드포인트
     */
    @JsonProperty("endpoint")
    @Schema(description = "서빙 엔드포인트 URL", example = "https://agent-api.example.com/chat")
    private String endpoint;
    
    /**
     * 에이전트 파라미터
     */
    @JsonProperty("agent_params")
    @Schema(description = "에이전트 파라미터 (JSON 문자열)", example = "{\"temperature\": 0.7, \"max_tokens\": 1000}")
    private String agentParams;
    
    /**
     * 에이전트 앱 이미지
     */
    @JsonProperty("agent_app_image")
    @Schema(description = "에이전트 앱 Docker 이미지", example = "agent-chat:v1.0")
    private String agentAppImage;
    
    /**
     * 에이전트 앱 이미지 레지스트리
     */
    @JsonProperty("agent_app_image_registry")
    @Schema(description = "에이전트 앱 이미지 레지스트리", example = "docker.io")
    private String agentAppImageRegistry;
    
    /**
     * 앱 설정 파일 경로
     */
    @JsonProperty("app_config_file_path")
    @Schema(description = "앱 설정 파일 경로", example = "/config/agent.yaml")
    private String appConfigFilePath;
    
    /**
     * 서빙 타입
     */
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입 (private, shared)", example = "private", required = true)
    private String servingType;
    
    /**
     * 공유 백엔드 ID
     */
    @JsonProperty("shared_backend_id")
    @Schema(description = "공유 백엔드 ID", format = "uuid")
    private String sharedBackendId;
}