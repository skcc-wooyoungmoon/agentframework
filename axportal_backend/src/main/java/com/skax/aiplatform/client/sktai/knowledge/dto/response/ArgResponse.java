package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Tool Type 정의 응답 DTO
 * 
 * <p>각 Tool 타입의 스키마 정의를 담는 응답 데이터 구조입니다.
 * Tool 생성 시 필요한 connection_info_args의 구조와 지원하는 파일 확장자 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 타입, 표시명, 활성화 상태</li>
 *   <li><strong>연결 정보 스키마</strong>: connection_info_args의 동적 구조 (Object로 처리)</li>
 *   <li><strong>지원 파일 확장자</strong>: 해당 Tool이 처리할 수 있는 파일 형식</li>
 * </ul>
 * 
 * <h3>Tool 타입 예시:</h3>
 * <ul>
 *   <li><strong>default</strong>: 기본 로더, connection_info_args가 null</li>
 *   <li><strong>AzureDocumentIntelligence</strong>: Azure AI 서비스, endpoint, key 필요</li>
 *   <li><strong>SynapsoftDA</strong>: Synapsoft DocuAnalyzer, 다양한 옵션 지원</li>
 *   <li><strong>SKTDocumentInsight</strong>: SKT Document Insight, deployment_name 등 필요</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Tool Type 정의 정보",
    example = """
        {
          "type": "AzureDocumentIntelligence",
          "display_name": "Azure Document Intelligence",
          "connection_info_args": {
            "endpoint": {
              "type": "string",
              "required": true
            },
            "key": {
              "type": "string", 
              "required": true
            }
          },
          "supported_file_extensions": [".pdf", ".jpg", ".jpeg", ".png"],
          "enable": true
        }
        """
)
public class ArgResponse {

    /**
     * Tool 타입
     * 
     * <p>Tool의 고유 타입 식별자입니다.
     * 이 값은 Tool 생성 시 type 필드에 사용됩니다.</p>
     */
    @JsonProperty("type")
    @Schema(
        description = "Tool 타입 식별자",
        example = "AzureDocumentIntelligence",
        allowableValues = {
            "default",
            "AzureDocumentIntelligence", 
            "NaverOCR",
            "SynapsoftDA",
            "Docling",
            "SKTDocumentInsight"
        }
    )
    private String type;

    /**
     * 표시명
     * 
     * <p>사용자에게 보여지는 Tool의 친숙한 이름입니다.
     * UI에서 드롭다운이나 선택 목록에 표시됩니다.</p>
     */
    @JsonProperty("display_name")
    @Schema(
        description = "Tool 표시명",
        example = "Azure Document Intelligence"
    )
    private String displayName;

    /**
     * 연결 정보 스키마
     * 
     * <p>해당 Tool 타입이 필요로 하는 연결 정보의 구조를 정의합니다.
     * null인 경우 연결 정보가 필요하지 않음을 의미합니다.</p>
     * 
     * <p>각 Tool 타입마다 다른 구조를 가지므로 Object로 처리하여 동적으로 파싱합니다.</p>
     * 
     * <h4>예시 구조:</h4>
     * <ul>
     *   <li><strong>AzureDocumentIntelligence</strong>: {"endpoint": {"type": "string", "required": true}, "key": {"type": "string", "required": true}}</li>
     *   <li><strong>SynapsoftDA</strong>: {"endpoint": {...}, "key": {...}, "timeout_request": {...}, "poll_max_attempts": {...}, "poll_interval": {...}}</li>
     *   <li><strong>SKTDocumentInsight</strong>: {"deployment_name": {...}, "prompt": {...}, "max_tokens": {...}, "dpi": {...}, "timeout": {...}, "max_retries": {...}, "force_ocr": {...}}</li>
     * </ul>
     */
    @JsonProperty("connection_info_args")
    @Schema(
        description = "연결 정보 스키마 정의 (null 가능, 동적 구조)",
        example = """
            {
              "endpoint": {
                "type": "string",
                "required": true
              },
              "key": {
                "type": "string",
                "required": true
              }
            }
            """
    )
    private Object connectionInfoArgs;

    /**
     * 지원 파일 확장자 목록
     * 
     * <p>해당 Tool이 처리할 수 있는 파일 확장자들의 목록입니다.
     * 점(.)으로 시작하는 확장자 형식으로 제공됩니다.</p>
     */
    @JsonProperty("supported_file_extensions")
    @Schema(
        description = "지원하는 파일 확장자 목록",
        example = "[\".pdf\", \".jpg\", \".jpeg\", \".png\", \".bmp\", \".xlsx\", \".docx\", \".pptx\", \".html\"]"
    )
    private List<String> supportedFileExtensions;

    /**
     * 활성화 상태
     * 
     * <p>해당 Tool 타입이 현재 사용 가능한지 여부를 나타냅니다.
     * false인 경우 Tool 생성 시 선택할 수 없습니다.</p>
     */
    @JsonProperty("enable")
    @Schema(
        description = "Tool 타입 활성화 상태",
        example = "true"
    )
    private boolean enable;
}
