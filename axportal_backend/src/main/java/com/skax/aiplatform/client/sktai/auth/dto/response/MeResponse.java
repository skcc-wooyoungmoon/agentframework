package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 현재 사용자 정보 응답 DTO
 * 
 * <p>SKTAI Auth API의 "/api/v1/users/me" 엔드포인트 응답을 위한 DTO입니다.
 * 현재 로그인한 사용자의 상세 정보를 담고 있으며, 외부 API 스펙에 정확히 맞춰 설계되었습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 사용자명, 이메일</li>
 *   <li><strong>개인 정보</strong>: 이름(first_name), 성(last_name)</li>
 *   <li><strong>권한 정보</strong>: 할당된 역할 목록</li>
 *   <li><strong>프로젝트 정보</strong>: 현재 선택된 프로젝트</li>
 *   <li><strong>그룹 정보</strong>: 소속 그룹 목록</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>프로필 화면</strong>: 사용자 대시보드 정보 표시</li>
 *   <li><strong>인증 확인</strong>: 로그인 상태 및 권한 검증</li>
 *   <li><strong>프로젝트 컨텍스트</strong>: 현재 작업 중인 프로젝트 확인</li>
 *   <li><strong>권한 기반 UI</strong>: 역할/그룹 기반 화면 제어</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 1.0
 * @see UserBase 일반 사용자 정보 DTO
 * @see ProjectPayload 프로젝트 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 현재 사용자 정보 응답 (외부 API 스펙 준수)",
    example = """
        {
              "id": "887bf8b6-24f8-467b-a9a0-5099bf8666f8",
              "username": "sgo1033618",
              "email": "",
              "first_name": "",
              "last_name": "",
              "roles": [
                {
                  "id": "a8209cf9-0a76-4ef5-9e43-017ba3200c40",
                  "name": "admin",
                  "composite": false,
                  "clientRole": true,
                  "containerId": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
                }
              ],
              "project": {
                "id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
                "name": "default"
              },
              "current_group": "/public",
              "groups": ["/public"]
            }
        """
)
public class MeResponse {
    
    /**
     * 사용자 고유 식별자
     * 
     * <p>SKTAI 시스템에서 사용자를 고유하게 식별하는 UUID입니다.
     * 모든 사용자 관련 작업에서 기본 키로 사용됩니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "사용자 고유 식별자 (UUID)", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 사용자명
     * 
     * <p>시스템에서 사용자를 식별하는 고유한 사용자명입니다.
     * 로그인 시 사용되며, 변경 불가능한 경우가 많습니다.</p>
     */
    @JsonProperty("username")
    @Schema(
        description = "시스템 사용자명 (로그인 ID)", 
        example = "john.doe",
        minLength = 3,
        maxLength = 50
    )
    private String username;
    
    /**
     * 이메일 주소
     * 
     * <p>사용자의 이메일 주소입니다.
     * 시스템 알림, 비밀번호 재설정 등에 사용됩니다.</p>
     */
    @JsonProperty("email")
    @Schema(
        description = "사용자 이메일 주소", 
        example = "john.doe@company.com",
        format = "email"
    )
    private String email;
    
    /**
     * 사용자 이름 (First Name)
     * 
     * <p>사용자의 이름(성을 제외한 이름) 부분입니다.
     * 개인 식별 및 개인화에 사용됩니다.</p>
     */
    @JsonProperty("first_name")
    @Schema(
        description = "사용자 이름 (First Name)", 
        example = "John",
        maxLength = 50
    )
    private String firstName;
    
    /**
     * 사용자 성 (Last Name)
     * 
     * <p>사용자의 성(이름을 제외한 성) 부분입니다.
     * 개인 식별 및 개인화에 사용됩니다.</p>
     */
    @JsonProperty("last_name")
    @Schema(
        description = "사용자 성 (Last Name)", 
        example = "Doe",
        maxLength = 50
    )
    private String lastName;
    
    /**
     * 할당된 역할 목록
     * 
     * <p>사용자에게 할당된 시스템 역할 목록입니다.
     * 권한 기반 접근 제어에 사용됩니다.
     * API 스펙상 Object 배열로 정의되어 있습니다.</p>
     */
    @JsonProperty("roles")
    @Schema(
            description = "할당된 역할 목록",
            example = """
                    [
                      {
                        "id": "a8209cf9-0a76-4ef5-9e43-017ba3200c40",
                        "name": "admin",
                        "composite": false,
                        "clientRole": true,
                        "containerId": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
                      }
                    ]
            """
    )
    private List<Object> roles;
    
    /**
     * 현재 프로젝트 정보
     * 
     * <p>사용자가 현재 작업 중인 프로젝트 정보입니다.
     * ProjectPayload 타입으로 정의되어 있습니다.</p>
     */
    @JsonProperty("project")
    @Schema(
        description = "현재 선택된 프로젝트 정보",
        example = """
            {
                  "id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
                  "name": "default"
                }
            """
    )
    private ProjectPayload project;
    
    /**
     * 소속 그룹 목록
     * 
     * <p>사용자가 소속된 그룹 목록입니다.
     * 조직 구조나 프로젝트 단위 권한 관리에 사용됩니다.
     * API 스펙상 Object 배열로 정의되어 있습니다.</p>
     */
    @JsonProperty("groups")
    @Schema(
            description = "소속 그룹 목록",
            example = """
                    ["/public"]
                    """
    )
    private List<String> groups;


    /**
     * 현재 그룹
     *
     * <p>사용자의 현재 활성화된 그룹입니다.
     * 현재 컨텍스트에서 작업 중인 그룹을 나타냅니다.</p>
     */
    @JsonProperty("current_group")
    @Schema(
            description = "현재 활성화된 그룹",
            example = "/public"
    )
    private String currentGroup;
}
