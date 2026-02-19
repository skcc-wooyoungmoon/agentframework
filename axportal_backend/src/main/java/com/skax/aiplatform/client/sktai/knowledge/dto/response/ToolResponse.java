package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Tool 응답 DTO
 * 
 * <p>SKTAI Knowledge API로부터 받은 Data Ingestion Tool 정보를 담는 응답 데이터 구조입니다.
 * Tool의 등록, 조회, 수정 작업 결과로 반환되며, Tool의 현재 상태와 설정 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 이름, Tool 유형, 프로젝트 정보</li>
 *   <li><strong>연결 상태</strong>: 외부 서비스 연결 정보 및 상태</li>
 *   <li><strong>메타데이터</strong>: 생성/수정 시간, 생성자 정보</li>
 * </ul>
 * 
 * <h3>Tool 상태 정보:</h3>
 * <ul>
 *   <li><strong>ACTIVE</strong>: 정상 작동 중, 문서 처리 가능</li>
 *   <li><strong>INACTIVE</strong>: 비활성화됨, 연결 문제 또는 설정 오류</li>
 *   <li><strong>CONNECTING</strong>: 연결 설정 중</li>
 *   <li><strong>ERROR</strong>: 오류 상태, 연결 실패</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ToolResponse tool = sktaiToolsClient.createTool(createRequest);
 * String toolId = tool.getId();
 * String status = tool.getStatus();
 * 
 * if ("ACTIVE".equals(status)) {
 *     // Tool 사용 가능
 *     processDocuments(toolId);
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolCreate Tool 생성 요청
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolUpdate Tool 수정 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Tool 응답 정보",
    example = """
        {
          "id": "tool-uuid-123",
          "name": "Azure OCR Tool",
          "tool_type": "AzureDocumentIntelligence",
          "project_id": "project-456",
          "status": "ACTIVE",
          "connection_info": {
            "endpoint": "https://your-service.cognitiveservices.azure.com/",
            "api_key_masked": "azure-key-***"
          },
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T11:15:00Z",
          "created_by": "user@example.com"
        }
        """
)
public class ToolResponse {

    /**
     * Tool 고유 식별자
     * 
     * <p>시스템에서 생성된 Tool의 고유 식별자입니다.
     * UUID 형태로 제공되며, 모든 Tool 관련 작업에서 참조 키로 사용됩니다.</p>
     * 
     * @apiNote 이 ID는 Tool 생성 시 자동으로 생성되며 변경할 수 없습니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "Tool 고유 식별자 (UUID)",
        example = "tool-uuid-123",
        format = "uuid"
    )
    private String id;

    /**
     * Tool 이름
     * 
     * <p>사용자가 지정한 Tool의 이름입니다.
     * 프로젝트 내에서 고유하며, Tool의 목적이나 연결된 서비스를 나타냅니다.</p>
     * 
     * @implNote 이름은 Tool 수정을 통해 변경할 수 있습니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Tool 이름",
        example = "Azure OCR Tool"
    )
    private String name;

    /**
     * Tool 유형
     * 
     * <p>사용 중인 데이터 수집 도구의 유형입니다.
     * 각 유형은 서로 다른 문서 처리 능력과 연결 방식을 가집니다.</p>
     * 
     * @apiNote Tool 생성 후에는 유형을 변경할 수 없으며, 변경이 필요한 경우 새로운 Tool을 생성해야 합니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "Tool 유형",
        example = "AzureDocumentIntelligence",
        allowableValues = {
            "AzureDocumentIntelligence",
            "NaverOCR",
            "Docling", 
            "SynapsoftDA",
            "SKTDocumentInsight"
        }
    )
    private String type;

    /**
     * 프로젝트 식별자
     * 
     * <p>Tool이 속한 프로젝트의 고유 식별자입니다.
     * 프로젝트는 Tool의 접근 권한과 관리 범위를 결정합니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Tool이 속한 프로젝트 ID",
        example = "project-456"
    )
    private String projectId;


    /**
     * 연결 정보 (마스킹됨)
     * 
     * <p>외부 서비스와의 연결에 사용되는 설정 정보입니다.
     * 보안상 민감한 정보(API 키, 비밀번호 등)는 마스킹되어 제공됩니다.</p>
     * 
     * <h4>마스킹 규칙:</h4>
     * <ul>
     *   <li><strong>API 키</strong>: 앞 4자리 + "***" (예: "abcd***")</li>
     *   <li><strong>비밀번호</strong>: "***" 으로 완전 마스킹</li>
     *   <li><strong>엔드포인트</strong>: 전체 URL 표시 (민감하지 않음)</li>
     * </ul>
     * 
     * @apiNote 실제 연결 정보는 보안상 노출되지 않으며, 연결 상태만 확인할 수 있습니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "연결 정보 (민감한 정보는 마스킹됨)",
        example = """
            {
              "endpoint": "https://your-service.cognitiveservices.azure.com/",
              "api_key_masked": "azure-key-***"
            }
            """
    )
    private ConnectionInfo connectionInfo;

    /**
     * 생성 시간
     * 
     * <p>Tool이 처음 생성된 시간입니다.
     * ISO 8601 형식의 UTC 시간으로 제공됩니다.</p>
     * 
     * @apiNote 시간은 서버 시간 기준이며, 시간대 변환이 필요한 경우 클라이언트에서 처리해야 합니다.
     */
    @JsonProperty("created_at")
    @Schema(
        description = "Tool 생성 시간 (ISO 8601 UTC)",
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private String createdAt;

    /**
     * 수정 시간
     * 
     * <p>Tool이 마지막으로 수정된 시간입니다.
     * 이름, 연결 정보, 설정 등이 변경될 때마다 업데이트됩니다.</p>
     * 
     * @implNote 연결 상태 변경이나 자동 헬스 체크 결과로도 업데이트될 수 있습니다.
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "Tool 마지막 수정 시간 (ISO 8601 UTC)",
        example = "2025-08-15T11:15:00Z",
        format = "date-time"
    )
    private String updatedAt;

    /**
     * 생성자 정보
     * 
     * <p>Tool을 생성한 사용자의 식별 정보입니다.
     * 일반적으로 이메일 주소나 사용자 ID가 사용됩니다.</p>
     * 
     * @apiNote 생성자 정보는 Tool 관리 및 권한 추적을 위해 기록됩니다.
     */
    @JsonProperty("created_by")
    @Schema(
        description = "Tool 생성자 정보",
        example = "user@example.com"
    )
    private String createdBy;

    
    /**
     * 수정자 정보
     * 
     * <p>Tool을 수정한 사용자의 식별 정보입니다.
     * 일반적으로 이메일 주소나 사용자 ID가 사용됩니다.</p>
     * 
     * @apiNote 수정자 정보는 Tool 관리 및 권한 추적을 위해 기록됩니다.
     */
    @JsonProperty("updated_by")
    @Schema(
        description = "Tool 수정자 정보",
        example = "user@example.com"
    )
    private String updatedBy;

    /**
     * 삭제 여부
     * 
     * <p>Tool이 삭제된 여부입니다.
     * true로 설정되면 Tool이 삭제된 것입니다.</p>
     * 
     * @apiNote 삭제 여부는 Tool 관리 및 권한 추적을 위해 기록됩니다.
     */
    @JsonProperty("is_deleted")
    @Schema(
        description = "Tool 삭제 여부",
        example = "true",
        format = "boolean"
    )
    private Boolean isDeleted;


    
    /**
     * Tools 연결 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "연결 정보")
    public static class ConnectionInfo {
        String endpoint;
        String key;
    }
}
