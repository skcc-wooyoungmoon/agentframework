package com.skax.aiplatform.client.sktai.serving.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SKTAI Serving 상태 열거형
 * 
 * <p>SKTAI Serving 시스템에서 사용하는 서빙 인스턴스의 상태를 나타냅니다.
 * 서빙 생성부터 삭제까지의 전체 라이프사이클 상태를 포함합니다.</p>
 * 
 * <h3>상태 설명:</h3>
 * <ul>
 *   <li><strong>Ready</strong>: 서빙이 준비된 상태 (정상 작동)</li>
 *   <li><strong>IngressReady</strong>: Ingress 설정 완료</li>
 *   <li><strong>PredictorReady</strong>: Predictor 준비 완료</li>
 *   <li><strong>RoutesReady</strong>: 라우팅 설정 완료</li>
 *   <li><strong>PredictorFailed</strong>: Predictor 실패</li>
 *   <li><strong>Scaling</strong>: 스케일링 진행 중</li>
 *   <li><strong>Updating</strong>: 업데이트 진행 중</li>
 *   <li><strong>Terminated</strong>: 종료됨</li>
 *   <li><strong>Error</strong>: 오류 상태</li>
 *   <li><strong>Unknown</strong>: 알 수 없는 상태</li>
 *   <li><strong>Deploying</strong>: 배포 진행 중</li>
 *   <li><strong>Available</strong>: 사용 가능</li>
 *   <li><strong>Progressing</strong>: 진행 중</li>
 *   <li><strong>Failed</strong>: 실패</li>
 *   <li><strong>Deleting</strong>: 삭제 진행 중</li>
 *   <li><strong>Stopped</strong>: 중지됨</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse
 * @see com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse
 */
@Schema(
    description = "SKTAI Serving 상태 열거형", 
    allowableValues = {
        "Ready", "IngressReady", "PredictorReady", "RoutesReady", "PredictorFailed",
        "Scaling", "Updating", "Terminated", "Error", "Unknown", "Deploying",
        "Available", "Progressing", "Failed", "Deleting", "Stopped"
    }
)
public enum ServingStatus {
    
    /**
     * 서빙이 준비된 상태 (정상 작동)
     */
    @Schema(description = "서빙이 준비된 상태 (정상 작동)")
    Ready,
    
    /**
     * Ingress 설정 완료
     */
    @Schema(description = "Ingress 설정 완료")
    IngressReady,
    
    /**
     * Predictor 준비 완료
     */
    @Schema(description = "Predictor 준비 완료")
    PredictorReady,
    
    /**
     * 라우팅 설정 완료
     */
    @Schema(description = "라우팅 설정 완료")
    RoutesReady,
    
    /**
     * Predictor 실패
     */
    @Schema(description = "Predictor 실패")
    PredictorFailed,
    
    /**
     * 스케일링 진행 중
     */
    @Schema(description = "스케일링 진행 중")
    Scaling,
    
    /**
     * 업데이트 진행 중
     */
    @Schema(description = "업데이트 진행 중")
    Updating,
    
    /**
     * 종료됨
     */
    @Schema(description = "종료됨")
    Terminated,
    
    /**
     * 오류 상태
     */
    @Schema(description = "오류 상태")
    Error,
    
    /**
     * 알 수 없는 상태
     */
    @Schema(description = "알 수 없는 상태")
    Unknown,
    
    /**
     * 배포 진행 중
     */
    @Schema(description = "배포 진행 중")
    Deploying,
    
    /**
     * 사용 가능
     */
    @Schema(description = "사용 가능")
    Available,
    
    /**
     * 진행 중
     */
    @Schema(description = "진행 중")
    Progressing,
    
    /**
     * 실패
     */
    @Schema(description = "실패")
    Failed,
    
    /**
     * 삭제 진행 중
     */
    @Schema(description = "삭제 진행 중")
    Deleting,
    
    /**
     * 중지됨
     */
    @Schema(description = "중지됨")
    Stopped
}