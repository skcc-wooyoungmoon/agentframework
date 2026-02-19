package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Knowledge ChunkStore 응답 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 ChunkStore 관련 작업의 응답 데이터 구조입니다.
 * 생성, 조회, 수정 작업의 결과로 ChunkStore 정보를 반환할 때 사용됩니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>id</strong>: ChunkStore 고유 식별자</li>
 *   <li><strong>name</strong>: ChunkStore 이름</li>
 *   <li><strong>connection_info</strong>: 저장소 연결 정보</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>ChunkStore 생성 후 생성된 정보 확인</li>
 *   <li>ChunkStore 상세 조회 결과 표시</li>
 *   <li>ChunkStore 수정 후 변경된 정보 확인</li>
 *   <li>ChunkStore 목록에서 개별 항목 정보</li>
 * </ul>
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
    description = "SKTAI Knowledge ChunkStore 응답 정보",
    example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "name": "MyChunkStore",
          "connection_info": {
            "host": "sktai-opensearch.sktai.svc.cluster.local",
            "port": "9200",
            "user": "admin",
            "password": "****"
          }
        }
        """
)
public class ChunkStoreResponse {
    
    /**
     * ChunkStore 고유 식별자
     * 
     * <p>시스템에서 생성된 ChunkStore의 고유 ID입니다.
     * UUID 형식으로 제공되며, 다른 API 호출 시 참조용으로 사용됩니다.</p>
     * 
     * @implNote 이 ID는 ChunkStore의 수명 동안 변경되지 않습니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "ChunkStore 고유 식별자 (UUID 형식)",
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid",
        required = true
    )
    private String id;
    
    /**
     * ChunkStore 이름
     * 
     * <p>사용자가 지정한 ChunkStore의 이름입니다.
     * 프로젝트 내에서 ChunkStore를 식별하는 데 사용됩니다.</p>
     * 
     * @implNote 이름은 프로젝트 내에서 고유해야 하며, 변경 가능합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "ChunkStore 이름",
        example = "MyChunkStore",
        required = true
    )
    private String name;
    
    /**
     * 저장소 연결 정보
     * 
     * <p>ChunkStore의 실제 저장소 연결 설정 정보입니다.
     * 저장소 타입에 따라 다른 형식의 연결 정보가 포함됩니다.</p>
     * 
     * <h4>OpenSearch 연결 정보:</h4>
     * <ul>
     *   <li><strong>host</strong>: OpenSearch 서버 호스트</li>
     *   <li><strong>port</strong>: 포트 번호</li>
     *   <li><strong>user</strong>: 인증 사용자명</li>
     *   <li><strong>password</strong>: 인증 비밀번호 (마스킹됨)</li>
     * </ul>
     * 
     * @implNote 보안상 민감한 정보(비밀번호 등)는 마스킹되어 반환될 수 있습니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "저장소 연결 정보 (타입별로 다른 형식, 민감 정보는 마스킹됨)",
        example = """
            {
              "host": "sktai-opensearch.sktai.svc.cluster.local",
              "port": "9200",
              "user": "admin",
              "password": "****"
            }
            """,
        required = true
    )
    private Map<String, Object> connectionInfo;
}
