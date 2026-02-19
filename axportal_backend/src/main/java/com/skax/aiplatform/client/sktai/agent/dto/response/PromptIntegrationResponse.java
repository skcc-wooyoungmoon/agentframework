package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent 프롬프트 Internal API 응답 DTO
 * 
 * <p>SKTAI Agent 시스템의 내부 API에서 프롬프트 통합 정보를 제공하는 응답 데이터 구조입니다.
 * 다른 시스템이나 서비스에서 프롬프트를 통합하여 사용할 때 필요한 정보를 포함합니다.</p>
 * 
 * <h3>내부 API 특징:</h3>
 * <ul>
 *   <li><strong>시스템 간 통합</strong>: 다른 서비스에서 프롬프트 사용을 위한 표준 인터페이스</li>
 *   <li><strong>메타데이터 제공</strong>: 프롬프트 구조, 변수, 제약사항 등의 정보</li>
 *   <li><strong>버전 관리</strong>: 프롬프트 버전별 호환성 정보</li>
 *   <li><strong>실행 가이드</strong>: 프롬프트 실행에 필요한 파라미터 가이드</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>외부 시스템에서 프롬프트 통합 시 메타데이터 조회</li>
 *   <li>프롬프트 실행 전 필수 정보 확인</li>
 *   <li>자동화된 프롬프트 배포 및 통합</li>
 *   <li>프롬프트 호환성 검증</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent 프롬프트 Internal API 응답 정보",
    example = """
        {
          "prompt_uuid": "prompt-12345",
          "integration_metadata": {
            "version": "1.0",
            "required_variables": ["user_name", "topic"],
            "optional_variables": ["language"]
          }
        }
        """
)
public class PromptIntegrationResponse {
    
    /**
     * 프롬프트 UUID
     * 
     * <p>통합할 프롬프트의 고유 식별자입니다.</p>
     */
    @JsonProperty("prompt_uuid")
    @Schema(
        description = "프롬프트 고유 식별자", 
        example = "prompt-12345"
    )
    private String promptUuid;
    
    /**
     * 통합 메타데이터
     * 
     * <p>프롬프트 통합에 필요한 메타데이터 정보입니다.
     * 버전, 변수, 제약사항 등의 정보를 포함합니다.</p>
     * 
     * @implNote 동적인 구조를 가지며, 프롬프트 타입에 따라 다른 정보를 포함할 수 있습니다.
     * @apiNote JSON 객체 형태로 제공되며, 클라이언트에서 적절히 파싱해야 합니다.
     */
    @JsonProperty("integration_metadata")
    @Schema(
        description = "프롬프트 통합 메타데이터",
        example = """
            {
              "version": "1.0",
              "required_variables": ["user_name", "topic"],
              "optional_variables": ["language"],
              "constraints": {
                "max_tokens": 4096,
                "temperature_range": [0.0, 1.0]
              }
            }
            """
    )
    private Object integrationMetadata;
}
