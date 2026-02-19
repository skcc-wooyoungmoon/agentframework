package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 그룹 수정 요청 DTO
 * 
 * <p>SKTAI Auth API에서 기존 그룹 정보를 수정하기 위한 요청 데이터 구조입니다.
 * 그룹명, 설명, 활성 상태, 태그 등을 수정할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 필드:</h3>
 * <ul>
 *   <li><strong>name</strong>: 그룹 이름 변경</li>
 *   <li><strong>description</strong>: 그룹 설명 변경</li>
 *   <li><strong>is_active</strong>: 활성 상태 변경</li>
 *   <li><strong>tags</strong>: 태그 목록 변경</li>
 *   <li><strong>auto_approve_members</strong>: 자동 승인 설정 변경</li>
 * </ul>
 * 
 * <h3>수정 불가능한 필드:</h3>
 * <ul>
 *   <li><strong>type</strong>: 그룹 유형은 생성 후 변경 불가</li>
 *   <li><strong>parent_group_id</strong>: 상위 그룹은 별도 API로 변경</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GroupUpdateRequest request = GroupUpdateRequest.builder()
 *     .name("Advanced Development Team")
 *     .description("고급 개발팀 그룹")
 *     .isActive(true)
 *     .tags(Arrays.asList("development", "advanced", "senior"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GroupResponse 그룹 수정 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 수정 요청 정보",
    example = """
        {
          "name": "Advanced Development Team",
          "description": "고급 개발팀 그룹",
          "is_active": true,
          "tags": ["development", "advanced", "senior"],
          "auto_approve_members": false
        }
        """
)
public class GroupUpdateRequest {

    /**
     * 그룹 이름
     * 
     * <p>변경할 그룹의 새로운 이름입니다.
     * 시스템 내에서 중복될 수 없으며, 기존 참조 관계를 고려하여 변경해야 합니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "변경할 그룹 이름", 
        example = "Advanced Development Team",
        minLength = 2,
        maxLength = 100
    )
    private String name;

    /**
     * 그룹 설명
     * 
     * <p>변경할 그룹의 새로운 설명입니다.
     * 그룹의 목적과 역할을 명확하게 표현하도록 작성합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "변경할 그룹 설명", 
        example = "고급 개발팀 그룹",
        maxLength = 500
    )
    private String description;

    /**
     * 그룹 태그
     * 
     * <p>변경할 그룹의 새로운 태그 목록입니다.
     * 기존 태그를 완전히 대체하므로 유지하고 싶은 태그도 포함해야 합니다.</p>
     * 
     * @implNote null을 전달하면 태그 목록을 변경하지 않습니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "변경할 태그 목록 (기존 태그 완전 대체)", 
        example = "[\"development\", \"advanced\", \"senior\"]"
    )
    private List<String> tags;

    /**
     * 활성 상태
     * 
     * <p>그룹의 활성화 여부를 변경합니다.
     * 비활성화하면 일반적인 그룹 목록에서 제외됩니다.</p>
     */
    @JsonProperty("is_active")
    @Schema(
        description = "그룹 활성화 상태", 
        example = "true"
    )
    private Boolean isActive;

    /**
     * 멤버 자동 승인
     * 
     * <p>그룹 가입 요청에 대한 자동 승인 설정을 변경합니다.
     * true로 설정하면 가입 요청이 자동으로 승인됩니다.</p>
     */
    @JsonProperty("auto_approve_members")
    @Schema(
        description = "멤버 가입 자동 승인 여부", 
        example = "false"
    )
    private Boolean autoApproveMembers;
}
