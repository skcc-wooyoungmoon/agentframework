package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 수정 요청 DTO
 * 
 * <p>기존 Agent 애플리케이션의 정보를 수정하기 위한 요청 데이터 구조입니다.
 * 애플리케이션의 기본 정보, 설정, 상태 등을 업데이트할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: 애플리케이션 이름 (고유성 체크)</li>
 *   <li><strong>description</strong>: 애플리케이션 설명</li>
 *   <li><strong>category</strong>: 애플리케이션 카테고리</li>
 *   <li><strong>config</strong>: 애플리케이션 설정</li>
 *   <li><strong>status</strong>: 애플리케이션 상태</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AppUpdateRequest request = AppUpdateRequest.builder()
 *     .name("Enhanced Customer Support Bot")
 *     .description("업데이트된 고객 지원 AI 챗봇")
 *     .status("active")
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
    description = "SKTAI Agent App 수정 요청 정보",
    example = """
        {
          "name": "Enhanced Customer Support Bot",
          "description": "업데이트된 고객 지원 AI 챗봇",
        }
        """
)
public class AppUpdateRequest {
    
    /**
     * 수정할 애플리케이션 이름
     * 
     * <p>애플리케이션의 새로운 이름입니다.
     * 프로젝트 내에서 중복되지 않아야 합니다.</p>
     * 
     * @apiNote 이름 변경 시 기존 참조들이 영향받을 수 있으므로 신중하게 결정해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "수정할 애플리케이션 이름",
        example = "Enhanced Customer Support Bot",
        minLength = 3,
        maxLength = 100
    )
    private String name;
    
    /**
     * 수정할 애플리케이션 설명
     * 
     * <p>애플리케이션의 업데이트된 설명입니다.
     * 변경된 기능이나 용도를 반영하여 작성합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "수정할 애플리케이션 설명",
        example = "업데이트된 고객 지원 AI 챗봇",
        maxLength = 1000
    )
    private String description;
}
