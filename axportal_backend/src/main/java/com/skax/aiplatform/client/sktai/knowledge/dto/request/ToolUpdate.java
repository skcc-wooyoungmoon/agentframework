package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Tool 수정 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 기존 Data Ingestion Tool의 정보를 수정하기 위한 요청 데이터 구조입니다.
 * Tool의 이름, 연결 정보, 설정 등을 변경하여 문서 처리 성능을 최적화하거나 새로운 서비스로 이전할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>이름 변경</strong>: Tool의 식별 이름 수정</li>
 *   <li><strong>연결 정보 업데이트</strong>: API 키, 엔드포인트, 인증 정보 변경</li>
 *   <li><strong>Tool 유형 변경</strong>: 다른 문서 처리 서비스로 전환</li>
 * </ul>
 * 
 * <h3>변경 시 주의사항:</h3>
 * <ul>
 *   <li><strong>연결 검증</strong>: 새로운 연결 정보는 즉시 검증됩니다</li>
 *   <li><strong>기존 작업 영향</strong>: 진행 중인 문서 처리에 영향을 줄 수 있습니다</li>
 *   <li><strong>권한 확인</strong>: Tool 수정 권한이 필요합니다</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ToolUpdate updateRequest = ToolUpdate.builder()
 *     .name("Updated Azure OCR Tool")
 *     .connectionInfo(Map.of(
 *         "api_key", "new-azure-key-456",
 *         "endpoint", "https://new-service.cognitiveservices.azure.com/"
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolCreate Tool 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Tool 수정 요청 정보",
    example = """
        {
          "name": "Updated Azure OCR Tool",
          "connection_info": {
            "api_key": "new-azure-key-456",
            "endpoint": "https://new-service.cognitiveservices.azure.com/"
          }
        }
        """
)
public class ToolUpdate {

    /**
     * Tool 이름 (선택적)
     * 
     * <p>Data Ingestion Tool의 새로운 이름입니다.
     * 프로젝트 내에서 고유해야 하며, 다른 Tool과 중복될 수 없습니다.</p>
     * 
     * <h4>이름 변경 고려사항:</h4>
     * <ul>
     *   <li>참조하는 Repository나 설정이 있는지 확인 필요</li>
     *   <li>팀원들과의 협의 후 변경 권장</li>
     *   <li>변경 이력이 로그에 기록됩니다</li>
     * </ul>
     * 
     * @implNote null 값인 경우 기존 이름이 유지됩니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Tool 새 이름 (선택적, null인 경우 기존값 유지)",
        example = "Updated Azure OCR Tool",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    /**
     * 연결 정보 (선택적)
     * 
     * <p>외부 서비스와의 연결에 필요한 새로운 인증 정보와 설정을 포함하는 JSON 객체입니다.
     * 연결 정보 변경 시 기존 연결이 해제되고 새로운 연결이 설정됩니다.</p>
     * 
     * <h4>연결 정보 변경 프로세스:</h4>
     * <ol>
     *   <li><strong>새 연결 검증</strong>: 제공된 정보로 외부 서비스 연결 테스트</li>
     *   <li><strong>기존 연결 해제</strong>: 이전 연결 정보 비활성화</li>
     *   <li><strong>새 연결 활성화</strong>: 검증된 새 연결 정보 적용</li>
     *   <li><strong>상태 업데이트</strong>: Tool 상태를 새 연결로 업데이트</li>
     * </ol>
     * 
     * <h4>Tool 유형별 연결 정보 형식:</h4>
     * <ul>
     *   <li><strong>AzureDocumentIntelligence</strong>: {"api_key": "key", "endpoint": "url"}</li>
     *   <li><strong>NaverOCR</strong>: {"client_id": "id", "client_secret": "secret"}</li>
     *   <li><strong>Docling</strong>: {"host": "server", "port": 8080} (포트 선택적)</li>
     *   <li><strong>SynapsoftDA</strong>: {"username": "user", "password": "pass", "host": "server"}</li>
     *   <li><strong>SKTDocumentInsight</strong>: {"api_key": "key", "service_url": "url"}</li>
     * </ul>
     * 
     * <h4>보안 및 검증:</h4>
     * <ul>
     *   <li>새로운 인증 정보는 실시간으로 검증됩니다</li>
     *   <li>연결 실패 시 기존 설정이 유지됩니다</li>
     *   <li>민감한 정보는 자동으로 암호화되어 저장됩니다</li>
     *   <li>변경 사항은 감사 로그에 기록됩니다</li>
     * </ul>
     * 
     * @apiNote null 값인 경우 기존 연결 정보가 유지되며, 부분 업데이트는 지원되지 않습니다.
     * @implNote 연결 정보 변경 시 진행 중인 문서 처리 작업에 영향을 줄 수 있으므로 주의가 필요합니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "새로운 외부 서비스 연결 정보 (선택적, null인 경우 기존값 유지)",
        example = """
            {
              "api_key": "new-azure-key-456",
              "endpoint": "https://new-service.cognitiveservices.azure.com/"
            }
            """
    )
    private Object connectionInfo;
}
