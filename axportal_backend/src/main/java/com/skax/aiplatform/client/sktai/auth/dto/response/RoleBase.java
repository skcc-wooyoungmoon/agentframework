package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 역할 기본 정보 응답 DTO
 * 
 * <p>SKTAI API에서 반환되는 역할의 기본 정보를 담는 데이터 구조입니다.
 * 역할 식별자, 이름, 설명으로 구성되며, 권한 관리의 핵심 요소입니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>id</strong>: 역할 고유 식별자</li>
 *   <li><strong>name</strong>: 역할명</li>
 *   <li><strong>description</strong>: 역할 설명 (선택사항)</li>
 * </ul>
 * 
 * <h3>사용 컨텍스트:</h3>
 * <ul>
 *   <li>프로젝트 역할 목록 조회 시</li>
 *   <li>사용자 역할 매핑 정보</li>
 *   <li>권한 검증 시 역할 참조</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "id": "role-456def",
 *   "name": "admin",
 *   "description": "관리자 역할"
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ProjectRoles 프로젝트-역할 매핑 정보
 * @see ProjectRoleMappingsRead 역할 매핑 목록 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 역할 기본 정보",
    example = """
        {
          "id": "role-456def",
          "name": "admin",
          "description": "관리자 역할"
        }
        """
)
public class RoleBase {
    
    /**
     * 역할 고유 식별자
     * 
     * <p>시스템에서 역할을 고유하게 식별하는 ID입니다.
     * 권한 검증 및 역할 할당 시 사용됩니다.</p>
     * 
     * @implNote 시스템에서 자동 생성되며, 변경할 수 없는 불변 값입니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "역할 고유 식별자",
        example = "role-456def",
        required = true
    )
    private String id;
    
    /**
     * 역할명
     * 
     * <p>역할의 이름으로, 프로젝트 내에서 고유합니다.
     * 일반적으로 admin, member, viewer 등의 표준 역할명을 사용합니다.</p>
     * 
     * @apiNote 역할명은 사용자 인터페이스에서 표시되고 API에서 참조됩니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "역할명 (프로젝트 내 고유)",
        example = "admin",
        required = true
    )
    private String name;
    
    /**
     * 역할 설명
     * 
     * <p>역할의 목적과 권한 범위를 설명하는 텍스트입니다.
     * 관리자가 역할의 용도를 이해할 수 있도록 도움을 줍니다.</p>
     * 
     * @apiNote 선택사항이며, null일 수 있습니다.
     */
    @JsonProperty("description")
    @Schema(
        description = "역할 설명 (목적과 권한 범위)",
        example = "관리자 역할",
        nullable = true
    )
    private String description;
}
