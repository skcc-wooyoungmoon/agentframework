package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model 승격 요청 DTO
 * 
 * <p>SKTAI Model의 특정 버전을 기본 모델로 승격시킬 때 사용하는 요청 데이터 구조입니다.
 * 버전을 프로덕션 모델로 변경하는 중요한 작업을 수행합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>display_name</strong>: 승격된 모델의 표시 이름</li>
 *   <li><strong>description</strong>: 승격 사유 및 설명</li>
 * </ul>
 * 
 * <h3>선택 정보:</h3>
 * <ul>
 *   <li><strong>policy</strong>: 접근 정책</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelPromoteRequest request = ModelPromoteRequest.builder()
 *     .displayName("Production Model v2.0")
 *     .description("성능 테스트 완료 후 프로덕션 배포")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model 승격 요청 정보",
    example = """
        {
          "display_name": "Production Model v2.0",
          "description": "성능 테스트 완료 후 프로덕션 배포"
        }
        """
)
public class ModelPromoteRequest {
    
    /**
     * 모델 표시 이름
     * 
     * <p>승격된 모델의 새로운 표시 이름입니다.
     * 사용자에게 표시되는 친화적인 이름으로 설정됩니다.</p>
     * 
     * @apiNote 의미있고 구분 가능한 이름을 사용하는 것이 좋습니다.
     */
    @JsonProperty("display_name")
    @Schema(
        description = "승격된 모델의 표시 이름", 
        example = "Production Model v2.0",
        required = true,
        maxLength = 255
    )
    private String displayName;
    
    /**
     * 승격 설명
     * 
     * <p>버전을 기본 모델로 승격시키는 이유와 변경사항을 설명합니다.
     * 이력 관리와 추적을 위해 중요한 정보입니다.</p>
     * 
     * @implNote 승격 사유, 테스트 결과, 주요 변경사항 등을 포함하는 것이 좋습니다.
     */
    @JsonProperty("description")
    @Schema(
        description = "승격 사유 및 설명", 
        example = "성능 테스트 완료 후 프로덕션 배포",
        required = true,
        maxLength = 1000
    )
    private String description;
    
    /**
     * 접근 정책
     * 
     * <p>승격된 모델의 접근 정책을 설정합니다.
     * 사용자 또는 그룹별 접근 권한을 정의할 수 있습니다.</p>
     * 
     * @implNote 정책이 지정되지 않으면 기본 정책이 적용됩니다.
     */
    @JsonProperty("policy")
    @Schema(
        description = "모델 접근 정책 (선택사항)",
        example = "null"
    )
    private Object policy;
}
