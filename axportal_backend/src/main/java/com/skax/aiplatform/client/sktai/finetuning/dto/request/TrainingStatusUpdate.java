package com.skax.aiplatform.client.sktai.finetuning.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Fine-tuning Training 상태 수정 전용 DTO
 * 
 * <p>Training의 상태만 빠르게 변경하기 위한 전용 DTO입니다.
 * 전체 수정 API와 분리하여 명확한 목적을 가집니다.</p>
 * 
 * <h3>사용 케이스:</h3>
 * <ul>
 *   <li>Training 시작 (initialized → starting)</li>
 *   <li>Training 중지 (training → stopping → stopped)</li>
 *   <li>Training 재시작 (stopped → starting)</li>
 *   <li>에러 상태 처리 (any → error)</li>
 * </ul>
 *
 * @author sonmunwoo
 * @since 2025-10-21
 * @version 1.0
 * @see TrainingUpdate Training 전체 수정 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 상태 변경 요청 (Status만)",
    example = """
        {
          "status": "stopped"
        }
        """
)
public class TrainingStatusUpdate {
    
    /**
     * Training 상태
     * 
     * <p>변경할 Training 상태값입니다.
     * 상태 전이 규칙을 준수해야 합니다.</p>
     * 
     * <h4>허용 상태:</h4>
     * <ul>
     *   <li>initialized: 초기화됨</li>
     *   <li>starting: 시작 중</li>
     *   <li>stopping: 중지 중</li>
     *   <li>stopped: 중지됨</li>
     *   <li>resource-allocating: 리소스 할당 중</li>
     *   <li>resource-allocated: 리소스 할당 완료</li>
     *   <li>training: 훈련 중</li>
     *   <li>trained: 훈련 완료</li>
     *   <li>error: 오류 발생</li>
     * </ul>
     */
    @JsonProperty("status")
    @Schema(
        description = "변경할 Training 상태", 
        example = "stopped",
        allowableValues = {
            "initialized", "starting", "stopping", "stopped", 
            "resource-allocating", "resource-allocated", "training", "trained", "error"
        },
        maxLength = 64,
        required = true
    )
    private String status;


    @JsonProperty("policy")
    @Schema(description = "정책 목록", example = "[{\"scopes\": [\"GET\", \"POST\", \"PUT\", \"DELETE\"], \"policies\": [{\"type\": \"regex\", \"logic\": \"POSITIVE\", \"target_claim\": \"current_group\", \"pattern\": \"^/D2$\"}], \"logic\": \"POSITIVE\", \"decision_strategy\": \"AFFIRMATIVE\", \"cascade\": false}]")
    private List<PolicyRequest> policy;
}

