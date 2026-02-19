package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 기본 정보 DTO
 * 
 * <p>새로운 프로젝트 생성 시 필요한 기본 정보를 담는 데이터 구조입니다.
 * 프로젝트명은 시스템 내에서 고유해야 하며, 사용자 및 리소스 관리의 기본 단위가 됩니다.</p>
 * 
 * <h3>제약사항:</h3>
 * <ul>
 *   <li><strong>고유성</strong>: 프로젝트명은 시스템 전체에서 고유해야 함</li>
 *   <li><strong>명명 규칙</strong>: 영문자, 숫자, 하이픈(-), 언더스코어(_) 사용 가능</li>
 *   <li><strong>길이 제한</strong>: 일반적으로 3-50자 사이</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CreateProject project = CreateProject.builder()
 *     .name("my-ai-project")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see CreateClient 프로젝트 생성 요청의 상위 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 기본 정보",
    example = """
        {
          "name": "my-ai-project"
        }
        """
)
public class CreateProject {
    
    /**
     * 프로젝트명
     * 
     * <p>생성할 프로젝트의 고유한 이름입니다.
     * 이 이름은 시스템 내에서 프로젝트를 식별하는 데 사용되며,
     * API 호출 시 client_id로도 활용됩니다.</p>
     * 
     * @apiNote 프로젝트명은 생성 후 변경이 제한적일 수 있으므로 신중하게 결정해야 합니다.
     * @implNote 이름 규칙: 영문자로 시작, 영문자/숫자/하이픈/언더스코어 조합
     */
    @JsonProperty("name")
    @Schema(
        description = "프로젝트 고유 이름 (영문자로 시작, 영문자/숫자/하이픈/언더스코어 조합)",
        example = "my-ai-project",
        required = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9_-]*$",
        minLength = 3,
        maxLength = 50
    )
    private String name;
}
