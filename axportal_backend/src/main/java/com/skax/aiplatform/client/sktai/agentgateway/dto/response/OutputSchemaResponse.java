package com.skax.aiplatform.client.sktai.agentgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Gateway 출력 스키마 응답 DTO
 * 
 * <p>SKTAI Agent Gateway에서 특정 에이전트의 출력 스키마 정보를 담는 응답 데이터 구조입니다.
 * 에이전트가 생성하는 출력 데이터의 구조와 타입 정보를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>schema</strong>: JSON Schema 형태의 출력 구조 정의</li>
 *   <li><strong>examples</strong>: 출력 예시 데이터</li>
 *   <li><strong>metadata</strong>: 스키마 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>에이전트 응답 데이터 파싱 및 검증</li>
 *   <li>클라이언트 응답 처리 로직 구현</li>
 *   <li>API 문서화 및 타입 정의</li>
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
    description = "SKTAI Agent Gateway 출력 스키마 응답",
    example = """
        {
          "schema": {
            "type": "object",
            "properties": {
              "response": {
                "type": "string",
                "description": "에이전트 응답 메시지"
              },
              "confidence": {
                "type": "number",
                "description": "응답 신뢰도"
              }
            },
            "required": ["response"]
          },
          "examples": [
            {
              "response": "안녕하세요! 무엇을 도와드릴까요?",
              "confidence": 0.95
            }
          ],
          "metadata": {
            "version": "1.0",
            "last_updated": "2025-08-22T10:00:00Z"
          }
        }
        """
)
public class OutputSchemaResponse {
    
    /**
     * 출력 스키마
     * 
     * <p>JSON Schema 형태로 정의된 에이전트의 출력 데이터 구조입니다.
     * 응답 필드, 데이터 타입, 제약 조건 등을 포함합니다.</p>
     */
    @JsonProperty("schema")
    @Schema(
        description = "JSON Schema 형태의 출력 구조 정의"
    )
    private Object schema;
    
    /**
     * 출력 예시
     * 
     * <p>스키마에 맞는 출력 데이터의 예시들입니다.
     * 개발자가 응답 처리 로직을 구현하는데 도움을 제공합니다.</p>
     */
    @JsonProperty("examples")
    @Schema(
        description = "출력 예시 데이터 목록"
    )
    private Object examples;
    
    /**
     * 스키마 메타데이터
     * 
     * <p>스키마의 버전, 마지막 업데이트 시간 등의 메타데이터입니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(
        description = "스키마 메타데이터",
        example = "{\"version\": \"1.0\", \"last_updated\": \"2025-08-22T10:00:00Z\"}"
    )
    private Object metadata;
}
