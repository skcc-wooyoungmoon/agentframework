package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Serving 생성 요청 DTO
 * 
 * <p>SKTAI Serving 시스템에서 새로운 에이전트 서빙을 생성하기 위한 요청 데이터 구조입니다.
 * 에이전트 모델을 배포하여 대화형 AI 서비스를 제공할 때 사용합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>agent_id</strong>: 배포할 에이전트 모델 ID</li>
 *   <li><strong>agent_serving_name</strong>: 에이전트 서빙 인스턴스 이름</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>대화형 AI 에이전트 배포</li>
 *   <li>리소스 할당 및 스케일링 설정</li>
 *   <li>보안 필터 및 데이터 마스킹 적용</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see AgentServingResponse 에이전트 서빙 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Serving 생성 요청 정보",
    example = """
        {
          "agent_id": "agent-gpt4-conversation-v1",
          "agent_serving_name": "conversational-ai-service",
          "description": "고객 상담용 대화형 AI 에이전트",
          "cpu_request": 2,
          "cpu_limit": 4,
          "gpu_request": 1,
          "gpu_limit": 1,
          "mem_request": 8192,
          "mem_limit": 16384,
          "min_replicas": 1,
          "max_replicas": 5,
          "safety_filter_input": true,
          "safety_filter_output": true
        }
        """
)
public class AgentServingCreate {
    
    /**
     * 에이전트 식별자
     * 
     * <p>배포할 에이전트 모델의 고유 식별자입니다.
     * 사전에 등록된 에이전트 모델이어야 합니다.</p>
     * 
     * @apiNote 에이전트는 대화형 AI 모델로, 일반 모델과 다른 인터페이스를 제공합니다.
     */
    @JsonProperty("agent_id")
    @Schema(
        description = "배포할 에이전트 모델 ID", 
        example = "agent-gpt4-conversation-v1",
        required = true,
        minLength = 5,
        maxLength = 100
    )
    private String agentId;
    
    /**
     * 에이전트 서빙 이름
     * 
     * <p>에이전트 서빙 인스턴스의 고유한 이름입니다.
     * 서빙 엔드포인트의 식별자로 사용됩니다.</p>
     * 
     * @implNote 이름은 URL 경로에 사용되므로 영문자, 숫자, 하이픈만 허용됩니다.
     */
    @JsonProperty("agent_serving_name")
    @Schema(
        description = "에이전트 서빙 인스턴스 이름 (영문자, 숫자, 하이픈만 허용)", 
        example = "conversational-ai-service",
        required = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9-]*$",
        minLength = 3,
        maxLength = 100
    )
    private String agentServingName;
    
    /**
     * 에이전트 서빙 설명
     * 
     * <p>에이전트 서빙의 목적과 용도를 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "에이전트 서빙 설명 (목적과 용도)", 
        example = "고객 상담용 대화형 AI 에이전트",
        maxLength = 500
    )
    private String description;
    
    /**
     * 커스텀 에이전트 서빙 여부
     * 
     * <p>커스텀 에이전트 서빙 설정을 사용할지 여부입니다.</p>
     */
    @JsonProperty("is_custom")
    @Schema(
        description = "커스텀 에이전트 서빙 설정 사용 여부", 
        example = "false"
    )
    private Boolean isCustom;
    
    /**
     * 에이전트 서빙 파라미터
     * 
     * <p>에이전트 서빙에 사용할 상세 파라미터 설정입니다.
     * 대화 설정, 응답 옵션, 세션 관리 등을 포함합니다.</p>
     */
    @JsonProperty("agent_serving_params")
    @Schema(
        description = "에이전트 서빙 파라미터 설정 (대화 설정, 응답 옵션 등)"
    )
    private AgentParams agentServingParams;
    
    /**
     * CPU 요청량
     * 
     * <p>에이전트 서빙 인스턴스가 요청할 CPU 리소스 양입니다.
     * 단위는 CPU 코어 수입니다.</p>
     */
    @JsonProperty("cpu_request")
    @Schema(
        description = "CPU 요청량 (코어 수)", 
        example = "2",
        minimum = "0"
    )
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     * 
     * <p>에이전트 서빙 인스턴스가 사용할 수 있는 최대 CPU 리소스 양입니다.</p>
     */
    @JsonProperty("cpu_limit")
    @Schema(
        description = "CPU 제한량 (코어 수)", 
        example = "4",
        minimum = "0"
    )
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     * 
     * <p>에이전트 서빙 인스턴스가 요청할 GPU 리소스 양입니다.</p>
     */
    @JsonProperty("gpu_request")
    @Schema(
        description = "GPU 요청량", 
        example = "1",
        minimum = "0"
    )
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     * 
     * <p>에이전트 서빙 인스턴스가 사용할 수 있는 최대 GPU 리소스 양입니다.</p>
     */
    @JsonProperty("gpu_limit")
    @Schema(
        description = "GPU 제한량", 
        example = "1",
        minimum = "0"
    )
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     * 
     * <p>에이전트 서빙 인스턴스가 요청할 메모리 리소스 양입니다.
     * 단위는 MB입니다.</p>
     */
    @JsonProperty("mem_request")
    @Schema(
        description = "메모리 요청량 (MB)", 
        example = "8192",
        minimum = "0"
    )
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     * 
     * <p>에이전트 서빙 인스턴스가 사용할 수 있는 최대 메모리 리소스 양입니다.</p>
     */
    @JsonProperty("mem_limit")
    @Schema(
        description = "메모리 제한량 (MB)", 
        example = "16384",
        minimum = "0"
    )
    private Integer memLimit;
    
    /**
     * GPU 타입
     * 
     * <p>사용할 GPU의 타입을 지정합니다.</p>
     */
    @JsonProperty("gpu_type")
    @Schema(
        description = "GPU 타입", 
        example = "nvidia-tesla-v100"
    )
    private String gpuType;
    
    /**
     * 입력 안전 필터 적용 여부
     * 
     * <p>사용자 입력에 대한 안전 필터링을 적용할지 여부입니다.
     * 에이전트는 사용자와 직접 상호작용하므로 안전 필터가 중요합니다.</p>
     */
    @JsonProperty("safety_filter_input")
    @Schema(
        description = "입력 안전 필터 적용 여부", 
        example = "true"
    )
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터 적용 여부
     * 
     * <p>에이전트 응답에 대한 안전 필터링을 적용할지 여부입니다.</p>
     */
    @JsonProperty("safety_filter_output")
    @Schema(
        description = "출력 안전 필터 적용 여부", 
        example = "true"
    )
    private Boolean safetyFilterOutput;
    
    /**
     * 입력 데이터 마스킹 적용 여부
     * 
     * <p>사용자 입력에 대한 개인정보 마스킹을 적용할지 여부입니다.</p>
     */
    @JsonProperty("data_masking_input")
    @Schema(
        description = "입력 데이터 마스킹 적용 여부", 
        example = "false"
    )
    private Boolean dataMaskingInput;
    
    /**
     * 출력 데이터 마스킹 적용 여부
     * 
     * <p>에이전트 응답에 대한 개인정보 마스킹을 적용할지 여부입니다.</p>
     */
    @JsonProperty("data_masking_output")
    @Schema(
        description = "출력 데이터 마스킹 적용 여부", 
        example = "false"
    )
    private Boolean dataMaskingOutput;
    
    /**
     * 최소 레플리카 수
     * 
     * <p>오토스케일링 시 유지할 최소 인스턴스 수입니다.</p>
     */
    @JsonProperty("min_replicas")
    @Schema(
        description = "최소 레플리카 수 (오토스케일링)", 
        example = "1",
        minimum = "0"
    )
    private Integer minReplicas;
    
    /**
     * 최대 레플리카 수
     * 
     * <p>오토스케일링 시 생성할 수 있는 최대 인스턴스 수입니다.</p>
     */
    @JsonProperty("max_replicas")
    @Schema(
        description = "최대 레플리카 수 (오토스케일링)", 
        example = "5",
        minimum = "1"
    )
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     * 
     * <p>사용할 오토스케일링 정책의 클래스입니다.</p>
     */
    @JsonProperty("autoscaling_class")
    @Schema(
        description = "오토스케일링 클래스", 
        example = "kpa.autoscaling.knative.dev"
    )
    private String autoscalingClass;
    
    /**
     * 오토스케일링 메트릭
     * 
     * <p>오토스케일링의 기준이 되는 메트릭입니다.</p>
     */
    @JsonProperty("autoscaling_metric")
    @Schema(
        description = "오토스케일링 메트릭", 
        example = "concurrency"
    )
    private String autoscalingMetric;
    
    /**
     * 스케일링 타겟
     * 
     * <p>오토스케일링의 목표 값입니다.</p>
     */
    @JsonProperty("target")
    @Schema(
        description = "스케일링 타겟 값", 
        example = "10",
        minimum = "1"
    )
    private Integer target;
}
