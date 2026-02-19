package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Serving 스케일링 요청 DTO
 * 
 * <p>SKTAI Serving 시스템에서 에이전트 서빙의 레플리카 수를 조정하기 위한 요청 데이터 구조입니다.
 * 대화형 서비스의 트래픽 변화에 따라 에이전트 서빙 인스턴스의 수를 수동으로 조정할 때 사용합니다.</p>
 * 
 * <h3>에이전트 서빙 스케일링 특징:</h3>
 * <ul>
 *   <li><strong>세션 유지</strong>: 기존 대화 세션이 유지되는 방식으로 스케일링</li>
 *   <li><strong>로드 밸런싱</strong>: 신규 대화는 새로운 인스턴스로 분산</li>
 *   <li><strong>점진적 스케일링</strong>: 급격한 변화보다는 점진적 조정 권장</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see AgentServingResponse 에이전트 서빙 스케일링 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Serving 스케일링 요청 정보",
    example = """
        {
          "replicas": 3
        }
        """
)
public class AgentServingScale {
    
    /**
     * 목표 레플리카 수
     * 
     * <p>에이전트 서빙 인스턴스의 목표 레플리카 수입니다.
     * 현재 실행 중인 레플리카 수를 이 값으로 조정합니다.</p>
     * 
     * @implNote 에이전트 서빙은 세션 상태를 가지므로 0으로 설정 시 모든 대화 세션이 종료됩니다.
     * @apiNote 오토스케일링이 활성화된 경우 min_replicas와 max_replicas 범위 내에서만 설정 가능합니다.
     */
    @JsonProperty("replicas")
    @Schema(
        description = "목표 레플리카 수 (0: 서빙 중지 및 모든 세션 종료, 1+: 활성 레플리카 수)", 
        example = "3",
        required = true,
        minimum = "0"
    )
    private Integer replicas;
}
