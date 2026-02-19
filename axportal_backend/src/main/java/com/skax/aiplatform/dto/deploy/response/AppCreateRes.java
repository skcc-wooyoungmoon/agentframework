package com.skax.aiplatform.dto.deploy.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent App 생성 응답 DTO
 * 
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCreateRes {
    
    /**
     * Agent Serving ID
     */
    private String agentServingId;
    
    /**
     * 배포 이름
     */
    private String deploymentName;
    
    /**
     * InferenceService 이름
     */
    private String isvcName;
    
    /**
     * 설명
     */
    private String description;
    
    /**
     * KServe YAML 설정
     */
    private String kserveYaml;
    
    /**
     * 프로젝트 ID
     */
    private String projectId;
    
    /**
     * 네임스페이스
     */
    private String namespace;
    
    /**
     * 앱 ID
     */
    private String appId;
    
    /**
     * 앱 버전
     */
    private Integer appVersion;
    
    /**
     * 상태
     */
    private String status;
    
    /**
     * CPU 요청량
     */
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     */
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     */
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     */
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     */
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     */
    private Integer memLimit;
    
    /**
     * 생성자
     */
    private String createdBy;
    
    /**
     * 수정자
     */
    private String updatedBy;
    
    /**
     * 삭제 여부
     */
    private Boolean isDeleted;
    
    /**
     * 생성 시간
     */
    private String createdAt;
    
    /**
     * 수정 시간
     */
    private String updatedAt;
    
    /**
     * 입력 안전 필터
     */
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터
     */
    private Boolean safetyFilterOutput;
    
    /**
     * 모델 목록
     */
    private List<String> modelList;
    
    /**
     * 엔드포인트
     */
    private String endpoint;
    
    /**
     * Agent 파라미터
     */
    private Map<String, Object> agentParams;
    
    /**
     * 서빙 타입
     */
    private String servingType;
    
    /**
     * Agent 앱 이미지
     */
    private String agentAppImage;
    
    /**
     * Agent 앱 이미지 레지스트리
     */
    private String agentAppImageRegistry;
    
    /**
     * 배포 ID
     */
    private String deploymentId;
    
    /**
     * 에러 메시지
     */
    private String errorMessage;
    
    /**
     * GPU 타입
     */
    private String gpuType;
    
    /**
     * 입력 안전 필터 그룹
     */
    private List<String> safetyFilterInputGroups;
    
    /**
     * 출력 안전 필터 그룹
     */
    private List<String> safetyFilterOutputGroups;
    
    /**
     * 입력 데이터 마스킹
     */
    private Boolean dataMaskingInput;
    
    /**
     * 출력 데이터 마스킹
     */
    private Boolean dataMaskingOutput;
    
    /**
     * 최소 복제본 수
     */
    private Integer minReplicas;
    
    /**
     * 최대 복제본 수
     */
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     */
    private String autoscalingClass;
    
    /**
     * 오토스케일링 메트릭
     */
    private String autoscalingMetric;
    
    /**
     * 타겟 값
     */
    private Integer target;
    
    /**
     * 외부 엔드포인트
     */
    private String externalEndpoint;
    
    /**
     * 앱 설정 파일 경로
     */
    private String appConfigFilePath;
    
    /**
     * 공유 백엔드 ID
     */
    private String sharedBackendId;
}
