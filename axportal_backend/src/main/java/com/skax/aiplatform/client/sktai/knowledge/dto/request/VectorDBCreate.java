package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Vector DB 생성 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 새로운 Vector DB를 생성하기 위한 요청 데이터 구조입니다.
 * Vector DB 종류와 접속 정보를 포함하여 Knowledge에서 사용할 수 있는 벡터 데이터베이스를 등록합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>name</strong>: Vector DB 이름</li>
 *   <li><strong>type</strong>: Vector DB 종류</li>
 *   <li><strong>connection_info</strong>: 접속 정보</li>
 * </ul>
 * 
 * <h3>지원하는 Vector DB 종류:</h3>
 * <ul>
 *   <li><strong>Milvus</strong>: 오픈소스 벡터 데이터베이스</li>
 *   <li><strong>AzureAISearch</strong>: Azure AI Search 서비스</li>
 *   <li><strong>AzureAISearchShared</strong>: 공유 Azure AI Search</li>
 *   <li><strong>OpenSearch</strong>: OpenSearch 벡터 검색</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * VectorDBCreate request = VectorDBCreate.builder()
 *     .name("My Vector DB")
 *     .type("Milvus")
 *     .connectionInfo(Map.of(
 *         "host", "localhost",
 *         "port", "19530",
 *         "user", "root",
 *         "password", "password"
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
    description = "SKTAI Vector DB 생성 요청 정보"
)
public class VectorDBCreate {
    
    // /**
    //  * 프로젝트 식별자
    //  * 
    //  * <p>Vector DB가 속할 프로젝트의 고유 식별자입니다.
    //  * 프로젝트 기반으로 접근 권한이 관리됩니다.</p>
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
    //  * <p>Vector DB의 고유한 이름입니다.
    //  * 최대 200자까지 입력 가능하며, 프로젝트 내에서 중복될 수 없습니다.</p>
    //  */
    // @JsonProperty("name")
    // @Schema(
    //     description = "Vector DB 이름 (최대 200자)",
    //     example = "Milvus_add",
    //     required = true,
    //     maxLength = 200
    // )
    // private String name;
    
    // /**
    //  * Vector DB 종류
    //  * 
    //  * <p>지원하는 Vector DB 타입을 지정합니다.
    //  * Milvus, AzureAISearch, AzureAISearchShared, OpenSearch 중 선택할 수 있습니다.</p>
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
    //  * <p>Vector DB에 접속하기 위한 연결 정보입니다.
    //  * Vector DB 종류에 따라 필요한 연결 정보가 다릅니다.</p>
    //  * 
    //  * <h4>Milvus 연결 정보:</h4>
    //  * <ul>
    //  *   <li>host: 서버 호스트</li>
    //  *   <li>port: 포트 번호</li>
    //  *   <li>user: 사용자명</li>
    //  *   <li>password: 비밀번호</li>
    //  *   <li>secure: 보안 연결 여부</li>
    //  *   <li>db_name: 데이터베이스 이름</li>
    //  * </ul>
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
    //  * 기본 Vector DB는 새 Knowledge Repository 생성 시 자동으로 선택됩니다.</p>
    //  */
    // @JsonProperty("is_default")
    // @Schema(
    //     description = "기본 Vector DB로 설정 여부",
    //     example = "false"
    // )
    // private Boolean isDefault;
    
    // /**
    //  * 생성자
    //  * 
    //  * <p>Vector DB를 생성한 사용자의 식별자입니다.
    //  * 생성 후 수정 권한과 관련이 있습니다.</p>
    //  */
    // @JsonProperty("created_by")
    // @Schema(
    //     description = "Vector DB 생성자 식별자",
    //     example = "user123"
    // )
    // private String createdBy;
    
    // /**
    //  * 정책 설정
    //  * 
    //  * <p>Vector DB 접근 권한을 설정하는 정책입니다.
    //  * 사용자, 그룹, 역할 기반으로 접근 권한을 제어할 수 있습니다.</p>
    //  */
    // @JsonProperty("policy")
    // @Schema(description = "Vector DB 접근 권한 정책")
    // private PolicyPayload policy;
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
