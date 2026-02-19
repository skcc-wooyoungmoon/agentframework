package com.skax.aiplatform.client.sktai.safetyfilter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Safety Filter 안전성 검사 요청 DTO
 * 
 * <p>SKTAI Safety Filter 시스템에서 특정 발화의 유해성을 판단하기 위한 요청 데이터 구조입니다.
 * 입력된 텍스트가 안전한지 위험한지를 검사합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>utterance</strong>: 검사할 발화 텍스트</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용자 입력 텍스트의 안전성 검증</li>
 *   <li>콘텐츠 필터링 시스템의 사전 검사</li>
 *   <li>실시간 채팅 모니터링</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SafetyCheckOutput 안전성 검사 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Safety Filter 안전성 검사 요청 정보",
    example = """
        {
          "utterance": "이것은 검사할 텍스트입니다."
        }
        """
)
public class CheckSafeOrNot {
    
    /**
     * 검사할 발화 텍스트
     * 
     * <p>안전성을 검사할 대상 텍스트입니다.
     * 이 텍스트가 등록된 Safety Filter 규칙에 위배되는지 검사합니다.</p>
     * 
     * @apiNote 필수 필드이며, 검사 대상이 되는 모든 텍스트를 포함해야 합니다.
     */
    @JsonProperty("utterance")
    @Schema(
        description = "안전성을 검사할 발화 텍스트", 
        example = "이것은 검사할 텍스트입니다.",
        required = true
    )
    private String utterance;
}
