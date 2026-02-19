package com.skax.aiplatform.dto.model.response;

import java.time.LocalDateTime;
import java.util.List;

import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Backend.AI 연동 기반 모델 배포 생성 응답 DTO
 * 
 * <p>
 * Backend.AI 시스템과 연동하여 모델 배포를 생성한 후 반환되는 응답 데이터 구조입니다.
 * 생성된 배포의 상세 정보와 설정을 포함합니다.
 * </p>
 * 
 * <h3>주요 정보:</h3>
 * <ul>
 * <li><strong>servingId</strong>: 생성된 서빙의 고유 ID</li>
 * <li><strong>isvcName</strong>: 생성 시에는 ""로 반환되며 추후 URL 정보가 삽입됨</li>
 * <li><strong>status</strong>: 서빙 상태 (Deploying으로 반환)</li>
 * <li><strong>servingOperator</strong>: BACKEND_AI로 일괄 반환</li>
 * <li><strong>isDeleted</strong>: 삭제 여부 (FALSE)</li>
 * </ul>
 *
 * @author AXPortal Team
 * @since 2025-01-27
 * @version 1.0
 * @see CreateBackendAiModelDeployReq Backend.AI 모델 배포 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Backend.AI 연동 기반 모델 배포 생성 응답", example = """
        {
          "servingId": "740bb210-7052-404b-99f5-756f3700061",
          "name": "backend-ai-serving-test",
          "description": "backend-ai-serving-test",
          "isvcName": "",
          "projectId": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
          "status": "Deploying",
          "modelId": "27f2c388-fc77-4029-825f-840bab231625",
          "versionId": "660e8400-e29b-41d4-a716-446655440001",
          "runtime": "vllm",
          "runtimeImage": "bai-repo:7080/bai/vllm:0.10.1-cuda12.8-ubuntu24.04",
          "servingMode": "SINGLE_NODE",
          "servingOperator": "BACKEND_AI",
          "servingParams": {},
          "cpuRequest": 2,
          "gpuRequest": 1,
          "memRequest": 8,
          "minReplicas": 1,
          "safetyFilterInput": true,
          "safetyFilterOutput": true,
          "dataMaskingInput": false,
          "dataMaskingOutput": false,
          "safetyFilterInputList": [],
          "safetyFilterOutputList": [],
          "createdAt": "2025-01-15T10:30:00",
          "updatedAt": "2025-01-15T10:30:00",
          "createdBy": "user123",
          "updatedBy": "user123",
          "isDeleted": false
        }
        """)
public class CreateBackendAiModelDeployRes {

    /**
     * 서빙 ID
     * 
     * <p>
     * 생성된 서빙의 고유 식별자입니다.
     * </p>
     */
    @Schema(description = "서빙 ID", example = "740bb210-7052-404b-99f5-756f3700061", format = "uuid")
    private String servingId;

    /**
     * 서빙 이름
     * 
     * <p>
     * 생성된 서빙의 이름입니다.
     * </p>
     */
    @Schema(description = "서빙 이름", example = "backend-ai-serving-test")
    private String name;

    /**
     * 서빙 설명
     * 
     * <p>
     * 생성된 서빙의 설명입니다.
     * </p>
     */
    @Schema(description = "서빙 설명", example = "backend-ai-serving-test")
    private String description;

    /**
     * ISVC 이름
     * 
     * <p>
     * 생성 시에는 ""로 반환되며 추후 URL 정보가 삽입됩니다.
     * </p>
     */
    @Schema(description = "생성 시에는 \"\"로 반환되며 추후 URL 정보가 삽입됨", example = "")
    private String isvcName;

    /**
     * 프로젝트 ID
     * 
     * <p>
     * A.X Platform 프로젝트 ID입니다.
     * </p>
     */
    @Schema(description = "A.X Platform 프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", format = "uuid")
    private String projectId;

    /**
     * 서빙 상태
     * 
     * <p>
     * 서빙의 현재 상태입니다. Deploying으로 반환됩니다.
     * </p>
     */
    @Schema(description = "서빙 상태", example = "Deploying")
    private String status;

    /**
     * 모델 ID
     * 
     * <p>
     * 서빙된 모델의 ID입니다.
     * </p>
     */
    @Schema(description = "서빙된 모델의 ID", example = "27f2c388-fc77-4029-825f-840bab231625", format = "uuid")
    private String modelId;

    /**
     * 모델 버전 ID
     * 
     * <p>
     * 서빙된 모델 버전의 ID입니다.
     * </p>
     */
    @Schema(description = "서빙된 모델 버전의 ID", example = "660e8400-e29b-41d4-a716-446655440001", format = "uuid")
    private String versionId;

    /**
     * 런타임
     * 
     * <p>
     * 사용된 런타임입니다.
     * </p>
     */
    @Schema(description = "사용된 런타임", example = "vllm")
    private String runtime;

    /**
     * 런타임 이미지
     * 
     * <p>
     * 사용된 런타임 이미지 주소입니다.
     * </p>
     */
    @Schema(description = "사용된 런타임 이미지 주소", example = "bai-repo:7080/bai/vllm:0.10.1-cuda12.8-ubuntu24.04")
    private String runtimeImage;

    /**
     * 서빙 모드
     * 
     * <p>
     * 설정된 서빙 모드입니다.
     * </p>
     */
    @Schema(description = "설정된 서빙 모드", example = "SINGLE_NODE")
    private String servingMode;

    /**
     * 서빙 오퍼레이터
     * 
     * <p>
     * Serving 시 사용되는 오퍼레이터입니다. BACKEND_AI로 일괄 반환됩니다.
     * </p>
     */
    @Schema(description = "Serving 시 사용되는 오퍼레이터", example = "BACKEND_AI")
    private String servingOperator;

    /**
     * 서빙 파라미터
     * 
     * <p>
     * 적용된 서빙 파라미터입니다.
     * </p>
     */
    @Schema(description = "적용된 서빙 파라미터")
    private Object servingParams;

    /**
     * CPU 요청량
     * 
     * <p>
     * 설정된 CPU 코어 수입니다.
     * </p>
     */
    @Schema(description = "설정된 CPU 코어 수", example = "2")
    private Integer cpuRequest;

    /**
     * GPU 요청량
     * 
     * <p>
     * 설정된 GPU 개수입니다.
     * </p>
     */
    @Schema(description = "설정된 GPU 개수", example = "1")
    private Integer gpuRequest;

    /**
     * 메모리 요청량
     * 
     * <p>
     * 설정된 메모리 크기 (Gi)입니다.
     * </p>
     */
    @Schema(description = "설정된 메모리 크기 (Gi)", example = "8")
    private Integer memRequest;

    /**
     * 최소 레플리카 수
     * 
     * <p>
     * 설정된 레플리카 개수입니다.
     * </p>
     */
    @Schema(description = "설정된 레플리카 개수", example = "1")
    private Integer minReplicas;

    /**
     * Safety Filter Input 적용 여부
     * 
     * <p>
     * 입력 데이터에 Safety Filter 적용 여부입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Input 적용 여부", example = "true")
    private Boolean safetyFilterInput;

    /**
     * Safety Filter Output 적용 여부
     * 
     * <p>
     * 출력 데이터에 Safety Filter 적용 여부입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Output 적용 여부", example = "true")
    private Boolean safetyFilterOutput;

    /**
     * Data Masking Input 적용 여부
     * 
     * <p>
     * 입력 데이터에 Data Masking 적용 여부입니다.
     * </p>
     */
    @Schema(description = "Data Masking Input 적용 여부", example = "false")
    private Boolean dataMaskingInput;

    /**
     * Data Masking Output 적용 여부
     * 
     * <p>
     * 출력 데이터에 Data Masking 적용 여부입니다.
     * </p>
     */
    @Schema(description = "Data Masking Output 적용 여부", example = "false")
    private Boolean dataMaskingOutput;

    /**
     * Safety Filter Input 그룹 목록
     * 
     * <p>
     * 입력 데이터에 적용된 Safety Filter 그룹 목록입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Input 그룹 목록")
    private List<String> safetyFilterInputGroups;

    /**
     * Safety Filter Output 그룹 목록
     * 
     * <p>
     * 출력 데이터에 적용된 Safety Filter 그룹 목록입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Output 그룹 목록")
    private List<String> safetyFilterOutputGroups;

    /**
     * 정책 목록
     * 
     * <p>
     * 서빙에 적용된 정책 목록입니다.
     * </p>
     */
    @Schema(description = "서빙에 적용된 정책 목록")
    private List<PolicyPayload> policy;

    /**
     * 생성일시
     * 
     * <p>
     * 서빙이 생성된 날짜와 시간입니다.
     * </p>
     */
    @Schema(description = "서빙 생성일시", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    /**
     * 수정일시
     * 
     * <p>
     * 서빙이 마지막으로 수정된 날짜와 시간입니다.
     * </p>
     */
    @Schema(description = "서빙 수정일시", example = "2025-01-15T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * 생성자
     * 
     * <p>
     * 서빙을 생성한 사용자입니다.
     * </p>
     */
    @Schema(description = "서빙 생성자", example = "user123")
    private String createdBy;

    /**
     * 수정자
     * 
     * <p>
     * 서빙을 마지막으로 수정한 사용자입니다.
     * </p>
     */
    @Schema(description = "서빙 수정자", example = "user123")
    private String updatedBy;

    /**
     * 삭제 여부
     * 
     * <p>
     * 서빙이 삭제되었는지 여부입니다. FALSE로 반환됩니다.
     * </p>
     */
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;
}
