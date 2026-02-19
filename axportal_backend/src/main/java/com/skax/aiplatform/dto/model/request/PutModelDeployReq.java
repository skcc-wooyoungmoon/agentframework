package com.skax.aiplatform.dto.model.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PutModelDeployReq {
    
    /**
     * 서빙 설명
     * 
     * <p>서빙의 목적과 용도를 설명하는 텍스트입니다.
     * 기존 설명을 새로운 내용으로 업데이트할 때 사용합니다.</p>
     */
    @Schema(
        description = "서빙 설명 (목적과 용도)", 
        example = "업데이트된 GPT-4 모델을 활용한 텍스트 생성 서빙 인스턴스",
        maxLength = 500
    )
    private String description;
    
    /**
     * 커스텀 서빙 여부
     * 
     * <p>커스텀 서빙 설정을 사용할지 여부입니다.
     * 기존 설정을 변경할 때 사용합니다.</p>
     */
    @Schema(
        description = "커스텀 서빙 설정 사용 여부", 
        example = "false"
    )
    private Boolean isCustom;
    
    /**
     * 서빙 파라미터
     * 
     * <p>모델 서빙에 사용할 상세 파라미터 설정입니다.
     * 양자화, GPU 메모리 사용률, 텐서 병렬화 등의 옵션을 수정할 수 있습니다.</p>
     */
    @Schema(
        description = "서빙 파라미터 설정 (양자화, GPU 설정 등)"
    )
    private Object servingParams;
    
    /**
     * CPU 요청량
     * 
     * <p>서빙 인스턴스가 요청할 CPU 리소스 양입니다.
     * 단위는 CPU 코어 수입니다.</p>
     */
    @Schema(
        description = "CPU 요청량 (코어 수)", 
        example = "4",
        minimum = "0"
    )
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     * 
     * <p>서빙 인스턴스가 사용할 수 있는 최대 CPU 리소스 양입니다.</p>
     */
    @Schema(
        description = "CPU 제한량 (코어 수)", 
        example = "8",
        minimum = "0"
    )
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     * 
     * <p>서빙 인스턴스가 요청할 GPU 리소스 양입니다.</p>
     */
    @Schema(
        description = "GPU 요청량", 
        example = "2",
        minimum = "0"
    )
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     * 
     * <p>서빙 인스턴스가 사용할 수 있는 최대 GPU 리소스 양입니다.</p>
     */
    @Schema(
        description = "GPU 제한량", 
        example = "2",
        minimum = "0"
    )
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     * 
     * <p>서빙 인스턴스가 요청할 메모리 리소스 양입니다.
     * 단위는 MB입니다.</p>
     */
    @Schema(
        description = "메모리 요청량 (MB)", 
        example = "16384",
        minimum = "0"
    )
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     * 
     * <p>서빙 인스턴스가 사용할 수 있는 최대 메모리 리소스 양입니다.</p>
     */
    @Schema(
        description = "메모리 제한량 (MB)", 
        example = "32768",
        minimum = "0"
    )
    private Integer memLimit;
    
    /**
     * GPU 타입
     * 
     * <p>사용할 GPU의 타입을 지정합니다.</p>
     */
    @Schema(
        description = "GPU 타입", 
        example = "nvidia-tesla-v100"
    )
    private String gpuType;
    
    /**
     * 입력 안전 필터 적용 여부
     * 
     * <p>입력 데이터에 대한 안전 필터링을 적용할지 여부입니다.</p>
     */
    @Schema(
        description = "입력 안전 필터 적용 여부", 
        example = "true"
    )
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터 적용 여부
     * 
     * <p>출력 데이터에 대한 안전 필터링을 적용할지 여부입니다.</p>
     */
    @Schema(
        description = "출력 안전 필터 적용 여부", 
        example = "true"
    )
    private Boolean safetyFilterOutput;
    
    /**
     * 입력 데이터 마스킹 적용 여부
     * 
     * <p>입력 데이터에 대한 개인정보 마스킹을 적용할지 여부입니다.</p>
     */
    @Schema(
        description = "입력 데이터 마스킹 적용 여부", 
        example = "false"
    )
    private Boolean dataMaskingInput;
    
    /**
     * 출력 데이터 마스킹 적용 여부
     * 
     * <p>출력 데이터에 대한 개인정보 마스킹을 적용할지 여부입니다.</p>
     */
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
    @Schema(
        description = "최소 레플리카 수 (오토스케일링)", 
        example = "2",
        minimum = "0"
    )
    private Integer minReplicas;
    
    /**
     * 최대 레플리카 수
     * 
     * <p>오토스케일링 시 생성할 수 있는 최대 인스턴스 수입니다.</p>
     */
    @Schema(
        description = "최대 레플리카 수 (오토스케일링)", 
        example = "10",
        minimum = "1"
    )
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     * 
     * <p>사용할 오토스케일링 정책의 클래스입니다.</p>
     */
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
    @Schema(
        description = "스케일링 타겟 값", 
        example = "20",
        minimum = "1"
    )
    private Integer target;

    /**
     * 환경 변수
     * 
     * <p>서빙에 설정할 환경 변수 맵입니다.</p>
     */
    @Schema(
        description = "서빙에 설정할 환경 변수 맵",
        example = "{}"
    )
    private Map<String, Object> envs;
}
