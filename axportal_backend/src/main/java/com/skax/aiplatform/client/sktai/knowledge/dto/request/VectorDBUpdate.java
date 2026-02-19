package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Vector DB 수정 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 기존 Vector DB 정보를 수정하기 위한 요청 데이터 구조입니다.
 * Vector DB의 이름, 연결 정보, 기본 설정 등을 업데이트할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 필드:</h3>
 * <ul>
 *   <li><strong>name</strong>: Vector DB 이름</li>
 *   <li><strong>type</strong>: Vector DB 종류</li>
 *   <li><strong>connection_info</strong>: 접속 정보</li>
 *   <li><strong>is_default</strong>: 기본 Vector DB 여부</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>Vector DB 타입 변경 시 기존 데이터와의 호환성을 확인해야 합니다.</li>
 *   <li>연결 정보 변경 시 Knowledge Repository의 동작에 영향을 줄 수 있습니다.</li>
 *   <li>기본 Vector DB 설정 변경 시 다른 Vector DB의 기본 설정이 해제될 수 있습니다.</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * VectorDBUpdate request = VectorDBUpdate.builder()
 *     .name("Updated Vector DB")
 *     .connectionInfo(Map.of(
 *         "host", "new-host",
 *         "port", "19530"
 *     ))
 *     .isDefault(true)
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
    description = "SKTAI Vector DB 수정 요청 정보",
    example = """
        {
          "name": "Milvus_update",
          "type": "Milvus",
          "connection_info": {
            "host": "sktai-milvus.sktai.svc.cluster.local",
            "port": "19530",
            "user": "",
            "password": "",
            "secure": "False",
            "db_name": "default"
          },
          "is_default": false
        }
        """
)
public class VectorDBUpdate {
    
    // /**
    //  * 프로젝트 식별자
    //  * 
    //  * <p>Vector DB가 속할 프로젝트의 고유 식별자입니다.
    //  * 프로젝트 변경 시 접근 권한이 재설정됩니다.</p>
    //  */
    // @JsonProperty("project_id")
    // @Schema(
    //     description = "Vector DB가 속할 프로젝트 ID",
    //     example = "project-123"
    // )
    // private String projectId;
    
    // /**
    //  * Vector DB 이름
    //  * 
    //  * <p>Vector DB의 새로운 이름입니다.
    //  * 최대 200자까지 입력 가능하며, 프로젝트 내에서 중복될 수 없습니다.</p>
    //  */
    // @JsonProperty("name")
    // @Schema(
    //     description = "Vector DB 이름 (최대 200자)",
    //     example = "Milvus_update",
    //     required = true,
    //     maxLength = 200
    // )
    // private String name;
    
    // /**
    //  * Vector DB 종류
    //  * 
    //  * <p>Vector DB 타입을 변경할 수 있습니다.
    //  * 타입 변경 시 기존 데이터와의 호환성을 확인해야 합니다.</p>
    //  */
    // @JsonProperty("type")
    // @Schema(
    //     description = "Vector DB 종류",
    //     example = "Milvus",
    //     required = true,
    //     allowableValues = {"Milvus", "AzureAISearch", "AzureAISearchShared", "OpenSearch"}
    // )
    // private String type;
    
    // /**
    //  * 연결 정보
    //  * 
    //  * <p>Vector DB의 새로운 연결 정보입니다.
    //  * 변경 시 기존 연결이 끊어지고 새 연결 정보로 접속을 시도합니다.</p>
    //  * 
    //  * @implNote 연결 정보 변경 후에는 연결 테스트를 권장합니다.
    //  */
    // @JsonProperty("connection_info")
    // @Schema(
    //     description = "Vector DB 연결 정보 (Vector DB 종류에 따라 필수 필드가 다름)",
    //     example = """
    //         {
    //           "host": "sktai-milvus.sktai.svc.cluster.local",
    //           "port": "19530",
    //           "user": "",
    //           "password": "",
    //           "secure": "False",
    //           "db_name": "default"
    //         }
    //         """,
    //     required = true
    // )
    // private Map<String, Object> connectionInfo;
    
    // /**
    //  * 기본 Vector DB 여부
    //  * 
    //  * <p>이 Vector DB를 프로젝트의 기본 Vector DB로 설정할지 여부입니다.
    //  * true로 설정 시 다른 Vector DB의 기본 설정이 자동으로 해제됩니다.</p>
    //  */
    // @JsonProperty("is_default")
    // @Schema(
    //     description = "기본 Vector DB로 설정 여부",
    //     example = "false"
    // )
    // private Boolean isDefault;
    
    // /**
    //  * Vector DB 식별자
    //  * 
    //  * <p>수정할 Vector DB의 고유 식별자입니다.
    //  * 일반적으로 경로 파라미터로 전달되므로 선택적 필드입니다.</p>
    //  */
    // @JsonProperty("id")
    // @Schema(
    //     description = "Vector DB 고유 식별자",
    //     example = "550e8400-e29b-41d4-a716-446655440000"
    // )
    // private String id;
    
    // /**
    //  * 수정자
    //  * 
    //  * <p>Vector DB를 수정한 사용자의 식별자입니다.
    //  * 수정 이력 추적에 사용됩니다.</p>
    //  */
    // @JsonProperty("updated_by")
    // @Schema(
    //     description = "Vector DB 수정자 식별자",
    //     example = "user123"
    // )
    // private String updatedBy;
    @JsonProperty("name")
    @Schema(
        description = "Vector DB 이름 (최대 200자)",
        example = "Milvus_add",
        required = true,
        maxLength = 200
    )
    private String name;

    @JsonProperty("type")
    @Schema(
        description = "Vector DB 종류",
        example = "Milvus",
        required = true,
        allowableValues = {"Milvus", "AzureAISearch", "AzureAISearchShared", "OpenSearch", "ElasticSearch"}
    )
    private String type;

    @JsonProperty("is_default")
    @Schema(
        description = "기본 Vector DB로 설정 여부",
        example = "False"
    )
    private String isDefault;

    @JsonProperty("connection_info")
    @Schema(description = "데이터 수집 도구 연결 정보", required = true)
    private ConnectionInfo connectionInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "연결 정보")
    public static class ConnectionInfo {
        @JsonProperty("key")
        private String key;
        
        @JsonProperty("endpoint")
        private String endpoint;

        @JsonProperty("host")
        private String host;
        
        @JsonProperty("port")
        private String port;
        
        @JsonProperty("user")
        private String user;
        
        @JsonProperty("password")
        private String password;
        
        @JsonProperty("secure")
        private String secure;
        
        @JsonProperty("db_name")
        private String dbName;
        
        @JsonProperty("api_key")
        private String apiKey;
    }
}
