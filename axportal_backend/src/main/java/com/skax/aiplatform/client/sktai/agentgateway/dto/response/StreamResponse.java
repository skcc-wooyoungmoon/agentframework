package com.skax.aiplatform.client.sktai.agentgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Agent Gateway 스트리밍 응답 DTO
 * 
 * <p>SKTAI Agent Gateway에서 스트리밍 추론의 실시간 응답을 담는 데이터 구조입니다.
 * 응답이 청크 단위로 전송되며, 각 청크는 부분적인 결과를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>chunk</strong>: 스트리밍 응답의 일부분</li>
 *   <li><strong>is_final</strong>: 최종 청크 여부</li>
 *   <li><strong>metadata</strong>: 스트리밍 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>실시간 텍스트 생성 응답 처리</li>
 *   <li>점진적 UI 업데이트</li>
 *   <li>사용자 경험 향상을 위한 즉시 피드백</li>
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
@JsonIgnoreProperties(ignoreUnknown = true)  // 알 수 없는 필드 무시
@Schema(description = "SKTAI Agent Gateway 스트리밍 응답")
public class StreamResponse {
    
    @JsonProperty("run_id")
    @Schema(description = "실행 ID")
    private String runId;
    
    @JsonProperty("chunk")
    @Schema(description = "스트리밍 청크 데이터")
    private String chunk;
    
    @JsonProperty("is_final")
    @Schema(description = "최종 응답 여부")
    private Boolean isFinal;
    
    @JsonProperty("metadata")
    @Schema(description = "메타데이터")
    private Map<String, Object> metadata;
    
    @JsonProperty("updates")
    @Schema(description = "업데이트 정보")
    private Map<String, Object> updates;
    
    @JsonProperty("node_name")
    @Schema(description = "노드 이름")
    private String nodeName;
    
    @JsonProperty("progress")
    @Schema(description = "진행 상황")
    private String progress;
    
    @JsonProperty("llm")
    @Schema(description = "LLM 응답")
    private Map<String, Object> llm;
    
    @JsonProperty("content")
    @Schema(description = "콘텐츠")
    private String content;
    
    @JsonProperty("messages")
    @Schema(description = "메시지 목록")
    private Object messages;
}
