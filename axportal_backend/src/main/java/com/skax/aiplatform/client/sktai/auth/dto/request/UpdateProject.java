package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 수정 정보 DTO
 * 
 * <p>기존 프로젝트의 기본 정보를 수정하기 위한 데이터 구조입니다.
 * 프로젝트명과 같은 기본 속성을 변경할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 속성:</h3>
 * <ul>
 *   <li><strong>name</strong>: 프로젝트 표시명</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>프로젝트명 변경 시 기존 참조 및 권한 설정에 영향을 줄 수 있음</li>
 *   <li>null 값인 필드는 업데이트되지 않음 (부분 업데이트 지원)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * UpdateProject project = UpdateProject.builder()
 *     .name("새로운 프로젝트명")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see UpdateClient 프로젝트 수정 요청의 상위 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 수정 기본 정보",
    example = """
        {
          "name": "새로운 프로젝트명"
        }
        """
)
public class UpdateProject {
    
    /**
     * 프로젝트명
     * 
     * <p>변경할 프로젝트의 새로운 이름입니다.
     * null로 설정하면 기존 이름이 유지됩니다.</p>
     * 
     * @apiNote 프로젝트명 변경은 기존 API 호출 및 권한 설정에 영향을 줄 수 있습니다.
     * @implNote 이름 규칙: 영문자로 시작, 영문자/숫자/하이픈/언더스코어 조합
     */
    @JsonProperty("name")
    @Schema(
        description = "새로운 프로젝트명 (null이면 기존 이름 유지)",
        example = "updated-project-name",
        nullable = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9_-]*$",
        minLength = 3,
        maxLength = 50
    )
    private String name;
}
