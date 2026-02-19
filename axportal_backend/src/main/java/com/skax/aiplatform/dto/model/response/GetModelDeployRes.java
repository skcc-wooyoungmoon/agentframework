package com.skax.aiplatform.dto.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetModelDeployRes {

    @Schema(description = "서빙 ID")
    private String servingId;

    @Schema(description = "서빙 이름")
    private String name;

    @Schema(description = "서빙 설명")
    private String description;

    @Schema(description = "KServe YAML")
    private String kserveYaml;

    @Schema(description = "InferenceService 이름")
    private String isvcName;

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "네임스페이스")
    private String namespace;

    @Schema(description = "서빙 상태")
    private String status;

    @Schema(description = "모델 ID")
    private String modelId;

    @Schema(description = "버전 ID")
    private String versionId;

    @Schema(description = "서빙 파라미터")
    private String servingParams;

    @Schema(description = "오류 메시지")
    private String errorMessage;

    @Schema(description = "CPU 요청량")
    private Number cpuRequest;

    @Schema(description = "CPU 제한량")
    private Number cpuLimit;

    @Schema(description = "GPU 요청량")
    private Number gpuRequest;

    @Schema(description = "GPU 제한량")
    private Number gpuLimit;

    @Schema(description = "메모리 요청량")
    private Number memRequest;

    @Schema(description = "메모리 제한량")
    private Number memLimit;

    @Schema(description = "생성자")
    private String createdBy;

    @Schema(description = "수정자")
    private String updatedBy;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 시간")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제 여부")
    private Boolean isDeleted;

    @Schema(description = "입력 안전 필터")
    private Boolean safetyFilterInput;

    @Schema(description = "출력 안전 필터")
    private Boolean safetyFilterOutput;

    @Schema(description = "입력 안전 필터 그룹")
    private List<String> safetyFilterInputGroups;

    @Schema(description = "입력 안전 필터 그룹 이름")
    private List<String> safetyFilterInputGroupNameList;

    @Schema(description = "출력 안전 필터 그룹")
    private List<String> safetyFilterOutputGroups;

    @Schema(description = "출력 안전 필터 그룹 이름")
    private List<String> safetyFilterOutputGroupNameList;

    @Schema(description = "입력 데이터 마스킹")
    private Boolean dataMaskingInput;

    @Schema(description = "출력 데이터 마스킹")
    private Boolean dataMaskingOutput;

    @Schema(description = "최소 복제본 수")
    private Integer minReplicas;

    @Schema(description = "최대 복제본 수")
    private Integer maxReplicas;

    @Schema(description = "오토스케일링 클래스")
    private String autoscalingClass;

    @Schema(description = "오토스케일링 메트릭")
    private String autoscalingMetric;

    @Schema(description = "타겟 값")
    private Integer target;

    @Schema(description = "모델 이름")
    private String modelName;

    @Schema(description = "표시 이름")
    private String displayName;

    @Schema(description = "모델 설명")
    private String modelDescription;

    @Schema(description = "모델 타입")
    private String type;

    @Schema(description = "서빙 타입")
    private String servingType;

    @Schema(description = "비공개 여부")
    private Boolean isPrivate;

    @Schema(description = "유효 여부")
    private Boolean isValid;

    @Schema(description = "추론 파라미터")
    private Map<String, Object> inferenceParam;

    @Schema(description = "양자화 정보")
    private Map<String, Object> quantization;

    @Schema(description = "제공자 이름")
    private String providerName;

    @Schema(description = "모델 버전")
    private String modelVersion;

    @Schema(description = "경로")
    private String path;

    @Schema(description = "버전 경로")
    private String versionPath;

    @Schema(description = "파인튜닝 ID")
    private String fineTuningId;

    @Schema(description = "버전 유효 여부")
    private Boolean versionIsValid;

    @Schema(description = "버전 삭제 여부")
    private Boolean versionIsDeleted;

    @Schema(description = "GPU 타입")
    private String gpuType;

    @Schema(description = "커스텀 여부")
    private Boolean isCustom;

    @Schema(description = "서빙 모드")
    private String servingMode;

    @Schema(description = "서빙 오퍼레이터")
    private String servingOperator;

    @Schema(description = "엔드포인트")
    private String endpoint;

    @Schema(description = "외부 엔드포인트")
    private String externalEndpoint;

    @Schema(description = "런타임")
    private String runtime;

    @Schema(description = "런타임 이미지")
    private String runtimeImage;

    @Schema(description = "환경 변수")
    private Map<String, Object> envs;

    @Schema(description = "공개범위")
    private String publicStatus;

    @Schema(description = "가드레일 적용 여부(적용중/미적용/적용 불가)")
    private String guardrailApplied;

    @Schema(description = "마이그레이션 여부")
    private Boolean production;

    @Schema(description = "리소스그룹")
    private String resourceGroup;
}
