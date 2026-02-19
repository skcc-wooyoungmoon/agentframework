package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Tool 생성 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 새로운 Data Ingestion Tool을 생성하기 위한 요청 데이터 구조입니다.
 * 문서 처리 및 데이터 수집을 위한 다양한 도구를 등록하여 Knowledge Repository의 데이터 품질을 향상시킬 수 있습니다.</p>
 * 
 * <h3>지원하는 Tool 유형:</h3>
 * <ul>
 *   <li><strong>AzureDocumentIntelligence</strong>: Azure AI Document Intelligence 서비스 (기본값)</li>
 *   <li><strong>NaverOCR</strong>: 네이버 클로바 OCR 서비스</li>
 *   <li><strong>Docling</strong>: IBM Docling 문서 처리 엔진</li>
 *   <li><strong>SynapsoftDA</strong>: Synapsoft 문서 분석 도구</li>
 *   <li><strong>SKTDocumentInsight</strong>: SKT 문서 인사이트 서비스</li>
 * </ul>
 * 
 * <h3>연결 정보 형식:</h3>
 * <ul>
 *   <li><strong>API Key 방식</strong>: {"api_key": "your-api-key", "endpoint": "service-url"}</li>
 *   <li><strong>OAuth 방식</strong>: {"client_id": "id", "client_secret": "secret", "auth_url": "url"}</li>
 *   <li><strong>기본 인증</strong>: {"username": "user", "password": "pass", "host": "server"}</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ToolCreate toolRequest = ToolCreate.builder()
 *     .projectId("project-123")
 *     .name("Azure OCR Tool")
 *     .toolType("AzureDocumentIntelligence")
 *     .connectionInfo(Map.of(
 *         "api_key", "azure-key-123",
 *         "endpoint", "https://your-service.cognitiveservices.azure.com/"
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.response.ToolResponse Tool 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Tool 생성 요청 정보",
    example = """
        {
          "project_id": "project-123",
          "name": "Azure OCR Tool",
          "tool_type": "AzureDocumentIntelligence",
          "connection_info": {
            "api_key": "azure-key-123",
            "endpoint": "https://your-service.cognitiveservices.azure.com/"
          }
        }
        """
)
public class ToolCreate {

    /**
     * 프로젝트 식별자
     * 
     * <p>Tool이 속할 프로젝트의 고유 식별자입니다.
     * 프로젝트는 Tool의 접근 권한과 관리 범위를 결정하며, 동일 프로젝트 내에서만 Tool을 사용할 수 있습니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 쓰기 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Tool이 속할 프로젝트 ID",
        example = "project-123",
        required = true,
        minLength = 5,
        maxLength = 50
    )
    private String projectId;

    /**
     * Tool 이름
     * 
     * <p>Data Ingestion Tool의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, Tool의 목적이나 연결된 서비스를 나타내는 명확한 이름을 사용합니다.</p>
     * 
     * @implNote 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Tool 고유 이름 (프로젝트 내 유일해야 함)",
        example = "Azure OCR Tool",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String name;

    /**
     * Tool 유형
     * 
     * <p>사용할 데이터 수집 도구의 유형을 지정합니다.
     * 각 유형은 서로 다른 연결 방식과 설정을 가지며, 지원하는 문서 포맷도 다릅니다.</p>
     * 
     * <h4>지원 유형별 특징:</h4>
     * <ul>
     *   <li><strong>AzureDocumentIntelligence</strong>: Azure 클라우드 기반, 고정밀 OCR 및 문서 분석</li>
     *   <li><strong>NaverOCR</strong>: 한국어 특화 OCR, 네이버 클라우드 서비스</li>
     *   <li><strong>Docling</strong>: 오픈소스 문서 처리, 다양한 포맷 지원</li>
     *   <li><strong>SynapsoftDA</strong>: 기업용 문서 분석, 고급 레이아웃 인식</li>
     *   <li><strong>SKTDocumentInsight</strong>: SKT AI 서비스, 통합 문서 처리</li>
     * </ul>
     * 
     * @implNote 기본값은 "AzureDocumentIntelligence"이며, 가장 안정적인 성능을 제공합니다.
     */
    @JsonProperty("tool_type")
    @Schema(
        description = "Tool 유형 (데이터 수집 도구 종류)",
        example = "AzureDocumentIntelligence",
        defaultValue = "AzureDocumentIntelligence",
        allowableValues = {
            "AzureDocumentIntelligence",
            "NaverOCR", 
            "Docling",
            "SynapsoftDA",
            "SKTDocumentInsight"
        }
    )
    private String toolType;

    /**
     * 연결 정보
     * 
     * <p>외부 서비스와의 연결에 필요한 인증 정보와 설정을 포함하는 JSON 객체입니다.
     * Tool 유형에 따라 필요한 필드가 다르며, 민감한 정보(API 키, 비밀번호 등)는 암호화되어 저장됩니다.</p>
     * 
     * <h4>Tool 유형별 필수 필드:</h4>
     * <ul>
     *   <li><strong>AzureDocumentIntelligence</strong>: api_key, endpoint</li>
     *   <li><strong>NaverOCR</strong>: client_id, client_secret</li>
     *   <li><strong>Docling</strong>: host, port (선택)</li>
     *   <li><strong>SynapsoftDA</strong>: username, password, host</li>
     *   <li><strong>SKTDocumentInsight</strong>: api_key, service_url</li>
     * </ul>
     * 
     * <h4>보안 고려사항:</h4>
     * <ul>
     *   <li>민감한 정보는 시스템에서 자동으로 암호화됩니다</li>
     *   <li>연결 테스트 후 유효성이 검증된 정보만 저장됩니다</li>
     *   <li>정기적인 연결 상태 확인을 통해 유효성을 모니터링합니다</li>
     * </ul>
     * 
     * @apiNote 연결 정보는 Tool 생성 시 즉시 검증되며, 연결 실패 시 생성이 거부됩니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "외부 서비스 연결 정보 (Tool 유형에 따라 필드가 다름)",
        example = """
            {
              "api_key": "azure-key-123",
              "endpoint": "https://your-service.cognitiveservices.azure.com/"
            }
            """,
        required = true
    )
    private Object connectionInfo;
}
