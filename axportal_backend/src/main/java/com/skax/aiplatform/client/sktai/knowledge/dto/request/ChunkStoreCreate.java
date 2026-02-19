package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Knowledge ChunkStore 생성 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 새로운 ChunkStore를 생성하기 위한 요청 데이터 구조입니다.
 * 프로젝트별로 청크 데이터 저장소를 생성하여 Knowledge 시스템의 성능을 최적화할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>name</strong>: ChunkStore 이름</li>
 *   <li><strong>connection_info</strong>: 저장소 연결 정보</li>
 * </ul>
 * 
 * <h3>지원하는 저장소 타입:</h3>
 * <ul>
 *   <li><strong>SystemDB</strong>: 시스템 내장 데이터베이스 (기본값)</li>
 *   <li><strong>OpenSearch</strong>: 외부 OpenSearch 클러스터</li>
 * </ul>
 * 
 * <h3>연결 정보 예시:</h3>
 * <pre>
 * // OpenSearch 연결 정보
 * {
 *   "host": "sktai-opensearch.sktai.svc.cluster.local",
 *   "port": "9200",
 *   "user": "admin",
 *   "password": "Sktai+k8s"
 * }
 * </pre>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ChunkStoreCreate request = ChunkStoreCreate.builder()
 *     .name("MyChunkStore")
 *     .type("OpenSearch")
 *     .connectionInfo(Map.of(
 *         "host", "opensearch.example.com",
 *         "port", "9200",
 *         "user", "admin",
 *         "password", "password123"
 *     ))
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
    description = "SKTAI Knowledge ChunkStore 생성 요청 정보",
    example = """
        {
          "name": "ChunkStore_add",
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
public class ChunkStoreCreate {
    
    /**
     * 프로젝트 식별자
     * 
     * <p>ChunkStore가 속할 프로젝트의 식별자입니다.
     * 지정하지 않으면 현재 사용자의 기본 프로젝트에 생성됩니다.</p>
     * 
     * @apiNote 프로젝트별로 ChunkStore를 격리하여 관리할 수 있습니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "ChunkStore가 속할 프로젝트 ID (선택적)",
        example = "project-123"
    )
    private String projectId;
    
    /**
     * ChunkStore 이름
     * 
     * <p>ChunkStore의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 최대 200자까지 입력 가능합니다.</p>
     * 
     * @implNote 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "ChunkStore 고유 이름 (최대 200자)",
        example = "ChunkStore_add",
        required = true,
        maxLength = 200
    )
    private String name;
    
    /**
     * ChunkStore 타입
     * 
     * <p>사용할 청크 저장소의 유형을 지정합니다.
     * 각 타입에 따라 연결 정보 형식이 달라집니다.</p>
     * 
     * @implNote 기본값은 OpenSearch입니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "ChunkStore 저장소 유형",
        example = "OpenSearch",
        allowableValues = {"SystemDB", "OpenSearch"},
        defaultValue = "OpenSearch"
    )
    private String type;
    
    /**
     * 저장소 연결 정보
     * 
     * <p>ChunkStore 타입에 따른 연결 설정 정보입니다.
     * 각 저장소별로 다른 형식의 연결 정보가 필요합니다.</p>
     * 
     * <h4>OpenSearch 연결 정보:</h4>
     * <ul>
     *   <li><strong>host</strong>: OpenSearch 서버 호스트</li>
     *   <li><strong>port</strong>: 포트 번호 (일반적으로 9200)</li>
     *   <li><strong>user</strong>: 인증 사용자명</li>
     *   <li><strong>password</strong>: 인증 비밀번호</li>
     * </ul>
     * 
     * @implNote 연결 정보는 저장소 타입에 따라 동적으로 검증됩니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "저장소 연결 정보 (타입별로 다른 형식)",
        example = """
            {
              "host": "sktai-opensearch.sktai.svc.cluster.local",
              "port": "9200",
              "user": "admin",
              "password": "Sktai+k8s"
            }
            """,
        required = true
    )
    private Map<String, Object> connectionInfo;
    
    /**
     * 생성자 정보
     * 
     * <p>ChunkStore를 생성하는 사용자의 식별자입니다.
     * 지정하지 않으면 현재 인증된 사용자로 자동 설정됩니다.</p>
     * 
     * @apiNote 감사 추적 및 권한 관리를 위해 사용됩니다.
     */
    @JsonProperty("created_by")
    @Schema(
        description = "생성자 식별자 (선택적, 자동 설정)",
        example = "user-123"
    )
    private String createdBy;
}
