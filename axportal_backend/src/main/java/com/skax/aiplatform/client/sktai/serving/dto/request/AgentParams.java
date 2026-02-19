package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent 파라미터 DTO
 * 
 * <p>에이전트 서빙에 사용되는 상세 설정 파라미터입니다.
 * 대화 관리, 응답 옵션, 세션 설정 등 에이전트별 특화 설정을 포함합니다.</p>
 * 
 * <h3>주요 설정 영역:</h3>
 * <ul>
 *   <li><strong>대화 설정</strong>: 대화 턴 제한, 컨텍스트 관리</li>
 *   <li><strong>응답 옵션</strong>: 응답 스타일, 길이 제한</li>
 *   <li><strong>세션 관리</strong>: 세션 타임아웃, 상태 관리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AgentParams params = AgentParams.builder()
 *     .maxTurns(10)
 *     .sessionTimeout(1800)
 *     .enableMemory(true)
 *     .responseMaxLength(2000)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent 파라미터 설정",
    example = """
        {
          "max_turns": 10,
          "session_timeout": 1800,
          "enable_memory": true,
          "response_max_length": 2000,
          "temperature": 0.7,
          "top_p": 0.9,
          "enable_streaming": true,
          "enable_function_calling": true
        }
        """
)
public class AgentParams {
    
    /**
     * 최대 대화 턴 수
     * 
     * <p>단일 세션에서 허용할 최대 대화 턴(turn) 수입니다.
     * 무한 대화 방지 및 리소스 관리를 위해 설정합니다.</p>
     * 
     * @implNote 기본값은 10턴이며, 필요에 따라 조정 가능합니다.
     */
    @JsonProperty("max_turns")
    @Schema(
        description = "단일 세션 최대 대화 턴 수", 
        example = "10",
        minimum = "1",
        maximum = "100"
    )
    private Integer maxTurns;
    
    /**
     * 세션 타임아웃 시간
     * 
     * <p>세션 비활성화 후 만료까지의 시간입니다.
     * 단위는 초(seconds)입니다.</p>
     * 
     * @apiNote 메모리 절약을 위해 적절한 타임아웃 설정이 중요합니다.
     */
    @JsonProperty("session_timeout")
    @Schema(
        description = "세션 타임아웃 시간 (초)", 
        example = "1800",
        minimum = "300",
        maximum = "7200"
    )
    private Integer sessionTimeout;
    
    /**
     * 메모리 기능 활성화 여부
     * 
     * <p>대화 히스토리를 기억하여 컨텍스트를 유지할지 여부입니다.
     * 활성화 시 더 일관된 대화가 가능하지만 메모리 사용량이 증가합니다.</p>
     */
    @JsonProperty("enable_memory")
    @Schema(
        description = "대화 히스토리 메모리 기능 활성화 여부", 
        example = "true"
    )
    private Boolean enableMemory;
    
    /**
     * 응답 최대 길이
     * 
     * <p>에이전트가 생성할 수 있는 응답의 최대 문자 수입니다.
     * 너무 긴 응답 방지 및 응답 시간 최적화에 사용됩니다.</p>
     */
    @JsonProperty("response_max_length")
    @Schema(
        description = "응답 최대 문자 수", 
        example = "2000",
        minimum = "100",
        maximum = "10000"
    )
    private Integer responseMaxLength;
    
    /**
     * Temperature 설정
     * 
     * <p>응답 생성 시 창의성과 일관성의 균형을 조절하는 파라미터입니다.
     * 낮을수록 더 일관되고 예측 가능한 응답을 생성합니다.</p>
     * 
     * @implNote 0.0~1.0 범위에서 설정하며, 0.7이 일반적인 기본값입니다.
     */
    @JsonProperty("temperature")
    @Schema(
        description = "응답 생성 Temperature (창의성 조절)", 
        example = "0.7",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double temperature;
    
    /**
     * Top-p 설정
     * 
     * <p>응답 생성 시 고려할 토큰의 누적 확률 임계값입니다.
     * 응답의 다양성을 조절하는 데 사용됩니다.</p>
     */
    @JsonProperty("top_p")
    @Schema(
        description = "Top-p 누적 확률 임계값 (다양성 조절)", 
        example = "0.9",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double topP;
    
    /**
     * 스트리밍 응답 활성화 여부
     * 
     * <p>응답을 실시간으로 스트리밍할지 여부입니다.
     * 활성화 시 사용자가 응답 생성 과정을 실시간으로 볼 수 있습니다.</p>
     */
    @JsonProperty("enable_streaming")
    @Schema(
        description = "실시간 스트리밍 응답 활성화 여부", 
        example = "true"
    )
    private Boolean enableStreaming;
    
    /**
     * 함수 호출 기능 활성화 여부
     * 
     * <p>에이전트가 외부 함수를 호출할 수 있는 기능의 활성화 여부입니다.
     * 활성화 시 더 다양한 작업을 수행할 수 있지만 보안 고려가 필요합니다.</p>
     */
    @JsonProperty("enable_function_calling")
    @Schema(
        description = "외부 함수 호출 기능 활성화 여부", 
        example = "true"
    )
    private Boolean enableFunctionCalling;
    
    /**
     * 시스템 프롬프트
     * 
     * <p>에이전트의 역할과 행동 방식을 정의하는 시스템 프롬프트입니다.
     * 에이전트의 페르소나와 응답 스타일을 결정합니다.</p>
     */
    @JsonProperty("system_prompt")
    @Schema(
        description = "에이전트 시스템 프롬프트 (역할 및 행동 정의)", 
        example = "당신은 친절하고 도움이 되는 AI 어시스턴트입니다.",
        maxLength = 5000
    )
    private String systemPrompt;
    
    /**
     * 컨텍스트 윈도우 크기
     * 
     * <p>에이전트가 고려할 수 있는 컨텍스트의 최대 토큰 수입니다.
     * 메모리 사용량과 처리 속도에 영향을 줍니다.</p>
     */
    @JsonProperty("context_window_size")
    @Schema(
        description = "컨텍스트 윈도우 크기 (토큰 수)", 
        example = "4096",
        minimum = "512",
        maximum = "32768"
    )
    private Integer contextWindowSize;
}
