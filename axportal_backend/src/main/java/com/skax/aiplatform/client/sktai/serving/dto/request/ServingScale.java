package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Serving 스케일링 요청 DTO
 * 
 * <p>SKTAI Serving 시스템에서 모델 서빙의 레플리카 수를 조정하기 위한 요청 데이터 구조입니다.
 * 트래픽 증가나 감소에 따라 서빙 인스턴스의 수를 수동으로 조정할 때 사용합니다.</p>
 * 
 * <h3>스케일링 유형:</h3>
 * <ul>
 *   <li><strong>Scale Out</strong>: 레플리카 수를 증가시켜 처리 용량 향상</li>
 *   <li><strong>Scale In</strong>: 레플리카 수를 감소시켜 리소스 절약</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ServingResponse 서빙 스케일링 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Serving 스케일링 요청 정보",
    example = """
        {
          "replicas": 5
        }
        """
)
public class ServingScale {
    
    /**
     * 목표 레플리카 수
     * 
     * <p>서빙 인스턴스의 목표 레플리카 수입니다.
     * 현재 실행 중인 레플리카 수를 이 값으로 조정합니다.</p>
     * 
     * @implNote 0으로 설정하면 서빙이 완전히 중지됩니다.
     * @apiNote 오토스케일링이 활성화된 경우 min_replicas와 max_replicas 범위 내에서만 설정 가능합니다.
     */
    @JsonProperty("replicas")
    @Schema(
        description = "목표 레플리카 수 (0: 서빙 중지, 1+: 활성 레플리카 수)", 
        example = "5",
        required = true,
        minimum = "0"
    )
    private Integer replicas;
}
