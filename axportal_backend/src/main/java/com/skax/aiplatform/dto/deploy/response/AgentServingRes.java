package com.skax.aiplatform.dto.deploy.response;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Serving 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 에이전트 서빙 관련 작업의 응답 데이터를 담는 구조입니다.
 * 에이전트 서빙 생성, 수정, 조회 등의 작업 결과를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 에이전트 서빙 ID, 이름, 설명, 상태</li>
 *   <li><strong>에이전트 정보</strong>: 에이전트 ID, 서빙 파라미터</li>
 *   <li><strong>리소스 정보</strong>: CPU, GPU, 메모리 할당량</li>
 *   <li><strong>스케일링 정보</strong>: 레플리카 수, 오토스케일링 설정</li>
 *   <li><strong>보안 정보</strong>: 안전 필터, 데이터 마스킹 설정</li>
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
    description = "SKTAI Agent Serving 응답 정보",
    example = """
        {
          "agent_serving_id": "agt-srv-123e4567-e89b-12d3-a456-426614174000",
          "agent_serving_name": "conversational-ai-service",
          "agent_id": "agent-gpt4-conversation-v1",
          "description": "고객 상담용 대화형 AI 에이전트",
          "status": "running",
          "endpoint_url": "https://api.sktai.io/agent/conversational-ai-service",
          "chat_endpoint_url": "wss://api.sktai.io/agent/conversational-ai-service/chat",
          "cpu_request": 2,
          "cpu_limit": 4,
          "gpu_request": 1,
          "gpu_limit": 1,
          "mem_request": 8192,
          "mem_limit": 16384,
          "current_replicas": 2,
          "min_replicas": 1,
          "max_replicas": 5,
          "safety_filter_input": true,
          "safety_filter_output": true,
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T10:30:00Z"
        }
        """
)
public class AgentServingRes {
    
    /**
     * 에이전트 서빙 식별자
     * 
     * <p>에이전트 서빙 인스턴스의 고유 식별자입니다.</p>
     */
    @Schema(description = "에이전트 서빙 고유 식별자", example = "agt-srv-123e4567-e89b-12d3-a456-426614174000")
    private String agentServingId;
    
    /**
     * 에이전트 서빙 이름
     * 
     * <p>에이전트 서빙 인스턴스의 이름입니다.</p>
     */
    @Schema(description = "에이전트 서빙 인스턴스 이름", example = "conversational-ai-service")
    private String agentServingName;
    
    /**
     * 에이전트 식별자
     * 
     * <p>서빙에 사용되는 에이전트의 식별자입니다.</p>
     */
    @Schema(description = "사용 에이전트 ID", example = "agent-gpt4-conversation-v1")
    private String agentId;
    
    /**
     * 에이전트 서빙 설명
     * 
     * <p>에이전트 서빙의 목적과 용도를 설명하는 텍스트입니다.</p>
     */
    @Schema(description = "에이전트 서빙 설명", example = "고객 상담용 대화형 AI 에이전트")
    private String description;
    
    /**
     * 에이전트 서빙 상태
     * 
     * <p>에이전트 서빙 인스턴스의 현재 상태입니다.</p>
     */
    @Schema(description = "에이전트 서빙 상태 (pending, running, stopped, error)", example = "running")
    private String status;
    
    /**
     * 에이전트 엔드포인트 URL
     * 
     * <p>에이전트 서빙에 접근할 수 있는 HTTP 엔드포인트 URL입니다.</p>
     */
    @Schema(description = "에이전트 서빙 엔드포인트 URL", example = "https://aip-stg.sktai.io/api/v1/agent_gateway/873218da-a611-47e5-86f9-645764ffeb15")
    private String endpoint;
    
    /**
     * 채팅 엔드포인트 URL
     * 
     * <p>실시간 채팅을 위한 WebSocket 엔드포인트 URL입니다.</p>
     */
    @Schema(description = "실시간 채팅 WebSocket 엔드포인트 URL", example = "wss://api.sktai.io/agent/conversational-ai-service/chat")
    private String chatEndpointUrl;
    
    /**
     * 커스텀 에이전트 서빙 여부
     * 
     * <p>커스텀 에이전트 서빙 설정 사용 여부입니다.</p>
     */
    @Schema(description = "커스텀 에이전트 서빙 설정 사용 여부", example = "false")
    private Boolean isCustom;
    
    /**
     * 에이전트 서빙 파라미터
     * 
     * <p>에이전트 서빙에 적용된 상세 파라미터 설정입니다.</p>
     */
    @Schema(description = "에이전트 서빙 파라미터 설정")
    private Object agentServingParams;
    
    /**
     * CPU 요청량
     * 
     * <p>에이전트 서빙 인스턴스가 요청한 CPU 리소스 양입니다.</p>
     */
    @Schema(description = "CPU 요청량 (코어 수)", example = "2")
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     * 
     * <p>에이전트 서빙 인스턴스의 최대 CPU 리소스 양입니다.</p>
     */
    @Schema(description = "CPU 제한량 (코어 수)", example = "4")
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     * 
     * <p>에이전트 서빙 인스턴스가 요청한 GPU 리소스 양입니다.</p>
     */
    @Schema(description = "GPU 요청량", example = "1")
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     * 
     * <p>에이전트 서빙 인스턴스의 최대 GPU 리소스 양입니다.</p>
     */
    @Schema(description = "GPU 제한량", example = "1")
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     * 
     * <p>에이전트 서빙 인스턴스가 요청한 메모리 리소스 양입니다.</p>
     */
    @Schema(description = "메모리 요청량 (MB)", example = "8192")
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     * 
     * <p>에이전트 서빙 인스턴스의 최대 메모리 리소스 양입니다.</p>
     */
    @Schema(description = "메모리 제한량 (MB)", example = "16384")
    private Integer memLimit;
    
    /**
     * GPU 타입
     * 
     * <p>사용 중인 GPU의 타입입니다.</p>
     */
    @Schema(description = "GPU 타입", example = "nvidia-tesla-v100")
    private String gpuType;
    
    /**
     * 현재 레플리카 수
     * 
     * <p>현재 실행 중인 에이전트 서빙 인스턴스의 수입니다.</p>
     */
    @Schema(description = "현재 실행 중인 레플리카 수", example = "2")
    private Integer currentReplicas;
    
    /**
     * 최소 레플리카 수
     * 
     * <p>오토스케일링 시 유지하는 최소 인스턴스 수입니다.</p>
     */
    @Schema(description = "최소 레플리카 수", example = "1")
    private Integer minReplicas;
    
    /**
     * 최대 레플리카 수
     * 
     * <p>오토스케일링 시 생성할 수 있는 최대 인스턴스 수입니다.</p>
     */
    @Schema(description = "최대 레플리카 수", example = "5")
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     * 
     * <p>사용 중인 오토스케일링 정책의 클래스입니다.</p>
     */
    @Schema(description = "오토스케일링 클래스", example = "kpa.autoscaling.knative.dev")
    private String autoscalingClass;
    
    /**
     * 오토스케일링 메트릭
     * 
     * <p>오토스케일링의 기준이 되는 메트릭입니다.</p>
     */
    @Schema(description = "오토스케일링 메트릭", example = "concurrency")
    private String autoscalingMetric;
    
    /**
     * 스케일링 타겟
     * 
     * <p>오토스케일링의 목표 값입니다.</p>
     */
    @Schema(description = "스케일링 타겟 값", example = "10")
    private Integer target;
    
    /**
     * 입력 안전 필터 적용 여부
     * 
     * <p>사용자 입력에 대한 안전 필터링 적용 여부입니다.</p>
     */
    @Schema(description = "입력 안전 필터 적용 여부", example = "true")
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터 적용 여부
     * 
     * <p>에이전트 응답에 대한 안전 필터링 적용 여부입니다.</p>
     */
    @Schema(description = "출력 안전 필터 적용 여부", example = "true")
    private Boolean safetyFilterOutput;
    
    /**
     * 입력 데이터 마스킹 적용 여부
     * 
     * <p>사용자 입력에 대한 개인정보 마스킹 적용 여부입니다.</p>
     */
    @Schema(description = "입력 데이터 마스킹 적용 여부", example = "false")
    private Boolean dataMaskingInput;
    
    /**
     * 출력 데이터 마스킹 적용 여부
     * 
     * <p>에이전트 응답에 대한 개인정보 마스킹 적용 여부입니다.</p>
     */
    @Schema(description = "출력 데이터 마스킹 적용 여부", example = "false")
    private Boolean dataMaskingOutput;
    
    /**
     * 활성 세션 수
     * 
     * <p>현재 활성화된 채팅 세션의 수입니다.</p>
     */
    @Schema(description = "현재 활성 채팅 세션 수", example = "25")
    private Integer activeSessions;
    
    /**
     * 생성 시간
     * 
     * <p>에이전트 서빙이 생성된 시간입니다.</p>
     */
    @Schema(description = "생성 시간", example = "2025-08-15T10:30:00Z")
    private String createdAt;
    
    /**
     * 수정 시간
     * 
     * <p>에이전트 서빙이 마지막으로 수정된 시간입니다.</p>
     */
    @Schema(description = "수정 시간", example = "2025-08-15T10:30:00Z")
    private String updatedAt;

    /**
     * Inference Service 이름
     */
    @Schema(description = "Inference Service 이름", example = "shared-backend-db262b63-e8b2-44f1-9639-1451d216411a")
    private String isvcName;
    
    /**
     * 에러 메시지
     */
    @Schema(description = "에러 메시지", example = "null")
    private String errorMessage;
    
    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "admin")
    private String createdBy;
    
    /**
     * 수정자
     */
    @Schema(description = "수정자", example = "null")
    private String updatedBy;
    
    /**
     * 에이전트 앱 이미지 레지스트리
     */
    @Schema(description = "에이전트 앱 이미지 레지스트리", example = "null")
    private String agentAppImageRegistry;
    
    /**
     * 앱 설정 파일 경로
     */
    @Schema(description = "앱 설정 파일 경로", example = "/mnt/agent/agentapp/app/24ba585a-02fc-43d8-b9f1-f7ca9e020fe5/904db155-6727-4d89-b6d0-fcf4fed3ce0e/app_graph_1.json")
    private String appConfigFilePath;
    
    /**
     * KServe YAML 설정
     */
    @Schema(description = "KServe YAML 설정", example = "null")
    private String kserveYaml;
    
    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;
    
    /**
     * 서빙 타입
     */
    @Schema(description = "서빙 타입", example = "shared")
    private String servingType;
    
    /**
     * 프로젝트 ID
     */
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;
    
    /**
     * 모델 목록
     */
    @Schema(description = "모델 목록", example = "[\"GIP/gpt-4o-mini\", \"gip/text-embedding-3-large\"]")
    private List<String> modelList;
    
    /**
     * 공유 백엔드 ID
     */
    @Schema(description = "공유 백엔드 ID", example = "db262b63-e8b2-44f1-9639-1451d216411a")
    private String sharedBackendId;
    
    /**
     * 네임스페이스
     */
    @Schema(description = "네임스페이스", example = "ns-24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String namespace;
    
    /**
     * 앱 ID
     */
    @Schema(description = "앱 ID", example = "904db155-6727-4d89-b6d0-fcf4fed3ce0e")
    private String appId;
    
    /**
     * 앱 버전
     */
    @Schema(description = "앱 버전", example = "1")
    private Integer appVersion;
    
    /**
     * 배포 이름
     */
    @Schema(description = "배포 이름", example = "904db155-6727-4d89-b6d0-fcf4fed3ce0e-1")
    private String deploymentName;
    
    /**
     * 에이전트 파라미터
     */
    @Schema(description = "에이전트 파라미터 (JSON 문자열)", example = "{\"APP__PORT\": 18080}")
    private Map<String, Object> agentParams;
    
    /**
     * 에이전트 앱 이미지
     */
    @Schema(description = "에이전트 앱 이미지", example = "aip-stg-harbor.sktai.io/sktai/agent/shared_app:v1.1.2")
    private String agentAppImage;

    @Schema(description = "에이전트 운영 배포 여부", example = "true")
    private Boolean isMigration;
}
