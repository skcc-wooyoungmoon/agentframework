package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 역할 생성 요청 DTO
 * 
 * <p>특정 프로젝트에 새로운 역할을 생성하기 위한 요청 데이터 구조입니다.
 * 생성된 역할은 해당 프로젝트 내에서만 유효하며, 사용자에게 할당할 수 있습니다.</p>
 * 
 * <h3>역할 특징:</h3>
 * <ul>
 *   <li><strong>프로젝트 범위</strong>: 특정 프로젝트 내에서만 유효</li>
 *   <li><strong>권한 관리</strong>: 세부적인 권한 설정 가능</li>
 *   <li><strong>사용자 할당</strong>: 여러 사용자에게 동일 역할 부여 가능</li>
 * </ul>
 * 
 * <h3>역할명 규칙:</h3>
 * <ul>
 *   <li>프로젝트 내에서 고유해야 함</li>
 *   <li>영문자, 숫자, 하이픈(-), 언더스코어(_) 사용 가능</li>
 *   <li>일반적으로 admin, member, viewer 등의 표준 역할명 사용 권장</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CreateProjectRole role = CreateProjectRole.builder()
 *     .name("data-scientist")
 *     .description("데이터 과학자 역할 - 모델 학습 및 분석 권한")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ProjectRoles 역할 할당 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 역할 생성 요청 정보",
    example = """
        {
          "name": "data-scientist",
          "description": "데이터 과학자 역할 - 모델 학습 및 분석 권한"
        }
        """
)
public class CreateProjectRole {
    
    /**
     * 역할명
     * 
     * <p>생성할 역할의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 사용자 할당 시 참조됩니다.</p>
     * 
     * @implNote 역할명은 생성 후 변경이 제한적이므로 신중하게 결정해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "역할 고유 이름 (프로젝트 내 중복 불가)",
        example = "data-scientist",
        required = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9_-]*$",
        minLength = 2,
        maxLength = 50
    )
    private String name;
    
    /**
     * 역할 설명
     * 
     * <p>역할의 목적과 권한 범위를 설명하는 텍스트입니다.
     * 다른 관리자들이 역할의 용도를 이해할 수 있도록 명확하게 작성합니다.</p>
     * 
     * @apiNote 설명은 선택사항이지만, 명확한 역할 관리를 위해 작성을 권장합니다.
     */
    @JsonProperty("description")
    @Schema(
        description = "역할 설명 (목적과 권한 범위)",
        example = "데이터 과학자 역할 - 모델 학습 및 분석 권한",
        nullable = true,
        maxLength = 500
    )
    private String description;
}
