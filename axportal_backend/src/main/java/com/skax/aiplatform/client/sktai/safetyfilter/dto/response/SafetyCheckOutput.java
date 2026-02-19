package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Safety Filter 안전성 검사 응답 DTO
 * 
 * <p>SKTAI Safety Filter 시스템에서 특정 발화의 유해성 판단 결과를 담는 응답 데이터 구조입니다.
 * 안전 여부, 리다이렉션 메시지, 실행 시간, 감지된 키워드 등의 정보를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>is_safe</strong>: 안전 여부 판단 결과</li>
 *   <li><strong>redirection_message</strong>: 리다이렉션 메시지</li>
 *   <li><strong>execution_time</strong>: 검사 실행 시간</li>
 *   <li><strong>stopword</strong>: 감지된 키워드 목록</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>실시간 텍스트 안전성 검증</li>
 *   <li>콘텐츠 필터링 시스템 결과 처리</li>
 *   <li>채팅 모니터링 시스템 응답 처리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see CheckSafeOrNot 안전성 검사 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Safety Filter 안전성 검사 응답 정보",
    example = """
        {
          "is_safe": false,
          "redirection_message": "부적절한 내용이 감지되었습니다.",
          "execution_time": 0.125,
          "stopword": ["inappropriate", "content"]
        }
        """
)
public class SafetyCheckOutput {
    
    /**
     * 안전 여부 판단 결과
     * 
     * <p>입력된 발화가 안전한지 여부를 나타내는 불리언 값입니다.
     * true인 경우 안전, false인 경우 위험으로 판단됩니다.</p>
     * 
     * @apiNote 필수 필드이며, Safety Filter 규칙에 따라 결정됩니다.
     */
    @JsonProperty("is_safe")
    @Schema(
        description = "안전 여부 판단 결과 (true: 안전, false: 위험)", 
        example = "false",
        required = true
    )
    private Boolean isSafe;
    
    /**
     * 리다이렉션 메시지
     * 
     * <p>안전하지 않은 내용이 감지되었을 때 사용자에게 표시할 메시지입니다.
     * 왜 차단되었는지에 대한 설명이나 대안을 제시할 수 있습니다.</p>
     * 
     * @apiNote 필수 필드이며, 안전한 경우에도 적절한 메시지가 제공됩니다.
     */
    @JsonProperty("redirection_message")
    @Schema(
        description = "사용자에게 표시할 리다이렉션 메시지", 
        example = "부적절한 내용이 감지되었습니다.",
        required = true
    )
    private String redirectionMessage;
    
    /**
     * 검사 실행 시간
     * 
     * <p>Safety Filter 검사를 실행하는 데 소요된 시간(초)입니다.
     * 성능 모니터링 및 최적화에 활용됩니다.</p>
     * 
     * @apiNote 필수 필드이며, 소수점 단위로 표현됩니다.
     */
    @JsonProperty("execution_time")
    @Schema(
        description = "검사 실행 시간 (초 단위)", 
        example = "0.125",
        required = true
    )
    private Double executionTime;
    
    /**
     * 감지된 키워드 목록
     * 
     * <p>입력 텍스트에서 감지된 위험 키워드들의 목록입니다.
     * 안전한 경우 null이거나 빈 배열일 수 있습니다.</p>
     * 
     * @apiNote 필수 필드이지만 null 값을 가질 수 있습니다.
     */
    @JsonProperty("stopword")
    @Schema(
        description = "감지된 위험 키워드 목록 (안전한 경우 null 또는 빈 배열)", 
        example = "[\"inappropriate\", \"content\"]",
        required = true
    )
    private List<Object> stopword;
}
