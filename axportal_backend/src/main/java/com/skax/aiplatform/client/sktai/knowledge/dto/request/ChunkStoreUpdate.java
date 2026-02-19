package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Knowledge ChunkStore 수정 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 기존 ChunkStore의 설정을 수정하기 위한 요청 데이터 구조입니다.
 * 이름, 연결 정보 등을 변경하여 저장소 설정을 최적화할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>name</strong>: 수정할 ChunkStore 이름</li>
 *   <li><strong>connection_info</strong>: 수정할 저장소 연결 정보</li>
 * </ul>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>이름</strong>: ChunkStore 표시 이름</li>
 *   <li><strong>연결 정보</strong>: 호스트, 포트, 인증 정보 등</li>
 *   <li><strong>저장소 타입</strong>: SystemDB, OpenSearch 등</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>연결 정보 변경 시 기존 연결이 해제됩니다</li>
 *   <li>저장소 타입 변경 시 데이터 마이그레이션이 필요할 수 있습니다</li>
 *   <li>사용 중인 ChunkStore 수정 시 일시적인 서비스 중단이 발생할 수 있습니다</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ChunkStoreUpdate request = ChunkStoreUpdate.builder()
 *     .name("UpdatedChunkStore")
 *     .type("OpenSearch")
 *     .connectionInfo(Map.of(
 *         "host", "new-opensearch.example.com",
 *         "port", "9200",
 *         "user", "newadmin",
 *         "password", "newpassword123"
 *     ))
 *     .updatedBy("user-456")
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
    description = "SKTAI Knowledge ChunkStore 수정 요청 정보",
    example = """
        {
          "name": "ChunkStore_update",
          "type": "OpenSearch",
          "connection_info": {
            "host": "sktai-opensearch.sktai.svc.cluster.local",
            "port": "9200",
            "user": "admin",
            "password": "Sktai+k8s"
          }
        }
        """
)
public class ChunkStoreUpdate {
    
    /**
     * 프로젝트 식별자
     * 
     * <p>ChunkStore가 속할 프로젝트의 식별자입니다.
     * 변경하려면 이 필드를 포함해야 합니다.</p>
     * 
     * @apiNote 프로젝트 이동 시 권한 검증이 수행됩니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "ChunkStore가 속할 프로젝트 ID (선택적)",
        example = "project-456"
    )
    private String projectId;
    
    /**
     * ChunkStore 이름
     * 
     * <p>수정할 ChunkStore의 새로운 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 최대 200자까지 입력 가능합니다.</p>
     * 
     * @implNote 이름 변경은 즉시 반영되며, 참조하는 모든 시스템에 영향을 줄 수 있습니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "수정할 ChunkStore 이름 (최대 200자)",
        example = "ChunkStore_update",
        required = true,
        maxLength = 200
    )
    private String name;
    
    /**
     * ChunkStore 타입
     * 
     * <p>수정할 청크 저장소의 유형입니다.
     * 타입 변경 시 기존 데이터와의 호환성을 확인해야 합니다.</p>
     * 
     * @implNote 타입 변경은 연결 정보 형식도 함께 변경해야 합니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "수정할 ChunkStore 저장소 유형",
        example = "OpenSearch",
        allowableValues = {"SystemDB", "OpenSearch"},
        defaultValue = "OpenSearch"
    )
    private String type;
    
    /**
     * 저장소 연결 정보
     * 
     * <p>수정할 ChunkStore의 새로운 연결 설정 정보입니다.
     * 변경된 연결 정보로 즉시 재연결을 시도합니다.</p>
     * 
     * <h4>OpenSearch 연결 정보:</h4>
     * <ul>
     *   <li><strong>host</strong>: 새로운 OpenSearch 서버 호스트</li>
     *   <li><strong>port</strong>: 새로운 포트 번호</li>
     *   <li><strong>user</strong>: 새로운 인증 사용자명</li>
     *   <li><strong>password</strong>: 새로운 인증 비밀번호</li>
     * </ul>
     * 
     * @implNote 연결 정보 변경 시 기존 연결이 해제되고 새로운 연결로 전환됩니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "수정할 저장소 연결 정보 (타입별로 다른 형식)",
        example = """
            {
              "host": "new-opensearch.example.com",
              "port": "9200",
              "user": "newadmin",
              "password": "newpassword123"
            }
            """,
        required = true
    )
    private Map<String, Object> connectionInfo;
    
    /**
     * ChunkStore ID (내부 사용)
     * 
     * <p>수정 대상 ChunkStore의 식별자입니다.
     * 일반적으로 URL 경로에서 제공되므로 직접 설정할 필요는 없습니다.</p>
     * 
     * @apiNote API 내부적으로 사용되는 필드로, 일반 사용자는 설정하지 않습니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "ChunkStore ID (내부 사용, 설정 불필요)",
        format = "uuid"
    )
    private String id;
    
    /**
     * 수정자 정보
     * 
     * <p>ChunkStore를 수정하는 사용자의 식별자입니다.
     * 지정하지 않으면 현재 인증된 사용자로 자동 설정됩니다.</p>
     * 
     * @apiNote 감사 추적 및 변경 이력 관리를 위해 사용됩니다.
     */
    @JsonProperty("updated_by")
    @Schema(
        description = "수정자 식별자 (선택적, 자동 설정)",
        example = "user-456"
    )
    private String updatedBy;
}
