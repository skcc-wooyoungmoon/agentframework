package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 생성 요청 DTO
 * 
 * <p>새로운 Agent 애플리케이션을 생성하기 위한 요청 데이터 구조입니다.
 * Agent App은 AI 기반 애플리케이션을 구성하고 배포하기 위한 단위입니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: 애플리케이션의 고유한 이름</li>
 *   <li><strong>description</strong>: 애플리케이션의 목적과 기능 설명</li>
 *   <li><strong>target_id</strong>: 배포 대상 ID</li>
 *   <li><strong>target_type</strong>: 배포 대상 타입</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AppCreateRequest request = AppCreateRequest.builder()
 *     .name("finance_agent_form_service")
 *     .description("agent_form_service_deploy")
 *     .targetId("c89a7451-3d40-4bab-b4ee-6aecd55b4f32")
 *     .targetType("agent_graph")
 *     .servingType("shared")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
public class AppCreateRequest {
 * @see AppCreateRequest App 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "SKTAI Agent App 생성 요청 정보",
    example = """
        {
          "name": "finance_agent_form_service",
          "description": "agent_form_service_deploy",
          "target_id": "c89a7451-3d40-4bab-b4ee-6aecd55b4f32",
          "target_type": "agent_graph",
          "serving_type": "shared",
          "cpu_limit": 2,
          "cpu_request": 1,
          "gpu_limit": 0,
          "gpu_request": 0,
          "mem_limit": 4,
          "mem_request": 2,
          "max_replicas": 1,
          "min_replicas": 1,
          "workers_per_core": 3,
          "version_description": "this is version description"
        }
        """
)
public class AppCreateRequest {
    
    /**
     * 애플리케이션 이름
     */
    @JsonProperty("name")
    @Schema(description = "애플리케이션 고유 이름", example = "finance_agent_form_service", required = true)
    private String name;
    
    /**
     * 애플리케이션 설명
     */
    @JsonProperty("description")
    @Schema(description = "애플리케이션 설명", example = "agent_form_service_deploy")
    private String description;
    
    /**
     * 배포 대상 ID
     */
    @JsonProperty("target_id")
    @Schema(description = "배포 대상 ID", example = "c89a7451-3d40-4bab-b4ee-6aecd55b4f32", required = true)
    private String targetId;
    
    /**
     * 배포 대상 타입
     */
    @JsonProperty("target_type")
    @Schema(description = "배포 대상 타입", example = "agent_graph", required = true)
    private String targetType;
    
    /**
     * 서빙 타입
     */
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입", example = "shared")
    private String servingType;
    
    /**
     * CPU 제한
     */
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한", example = "2")
    private Integer cpuLimit;
    
    /**
     * CPU 요청
     */
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청", example = "1")
    private Integer cpuRequest;
    
    /**
     * GPU 제한
     */
    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한", example = "0")
    private Integer gpuLimit;
    
    /**
     * GPU 요청
     */
    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청", example = "0")
    private Integer gpuRequest;
    
    /**
     * 메모리 제한 (GB)
     */
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한 (GB)", example = "4")
    private Integer memLimit;
    
    /**
     * 메모리 요청 (GB)
     */
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청 (GB)", example = "2")
    private Integer memRequest;
    
    /**
     * 최대 복제본 수
     */
    @JsonProperty("max_replicas")
    @Schema(description = "최대 복제본 수", example = "1")
    private Integer maxReplicas;
    
    /**
     * 최소 복제본 수
     */
    @JsonProperty("min_replicas")
    @Schema(description = "최소 복제본 수", example = "1")
    private Integer minReplicas;
    
    /**
     * 코어당 워커 수
     */
    @JsonProperty("workers_per_core")
    @Schema(description = "코어당 워커 수", example = "3")
    private Integer workersPerCore;
    
    /**
     * 버전 설명
     */
    @JsonProperty("version_description")
    @Schema(description = "버전 설명", example = "this is version description")
    private String versionDescription;

    /**
     * 안전 필터 옵션 (선택사항)
     */
    @JsonProperty("safety_filter_options")
    @Schema(description = "안전 필터 옵션 (선택사항)", required = false)
    private SafetyFilterOptions safetyFilterOptions;

    @JsonProperty("policy")
    @Schema(description = "정책 목록", example = "[{\"scopes\": [\"GET\", \"POST\", \"PUT\", \"DELETE\"], \"policies\": [{\"type\": \"regex\", \"logic\": \"POSITIVE\", \"target_claim\": \"current_group\", \"pattern\": \"^/D2$\"}], \"logic\": \"POSITIVE\", \"decision_strategy\": \"AFFIRMATIVE\", \"cascade\": false}]")
    private List<PolicyRequest> policy;
}
