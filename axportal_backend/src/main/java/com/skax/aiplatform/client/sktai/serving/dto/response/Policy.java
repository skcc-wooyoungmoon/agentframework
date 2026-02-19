package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 정책 DTO
 * 
 * <p>SKTAI Serving 시스템에서 접근 권한 정책에 대한 정보입니다.
 * 각 정책은 고유한 식별자와 메타데이터를 가지며, 서빙 리소스에 대한 접근 제어를 담당합니다.</p>
 * 
 * <h3>정책 구성요소:</h3>
 * <ul>
 *   <li><strong>ID</strong>: 정책의 고유 식별자</li>
 *   <li><strong>이름</strong>: 정책의 표시 이름</li>
 *   <li><strong>설명</strong>: 정책의 목적과 범위</li>
 *   <li><strong>상태</strong>: 정책의 활성화 여부</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>서빙 서비스 접근 권한 관리</li>
 *   <li>API 키 정책 연결</li>
 *   <li>역할 기반 접근 제어 (RBAC)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * Policy policy = Policy.builder()
 *     .id("policy-123")
 *     .name("Default Access Policy")
 *     .description("기본 접근 권한 정책")
 *     .enabled(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see PolicyPayload 정책 페이로드
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 접근 권한 정책 정보",
    example = """
        {
          "id": "policy-123",
          "name": "Default Access Policy",
          "description": "기본 접근 권한 정책",
          "enabled": true,
          "created_at": "2025-10-16T10:30:00Z",
          "updated_at": "2025-10-16T10:30:00Z"
        }
        """
)
public class Policy {
    
    /**
     * 정책 고유 식별자
     * 
     * <p>각 정책을 구별하는 고유한 식별자입니다.
     * 정책 조회, 수정, 삭제 시 기준이 되는 키값입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "정책 고유 식별자",
        example = "policy-123"
    )
    private String id;
    
    /**
     * 정책 이름
     * 
     * <p>정책의 표시 이름입니다.
     * 관리자와 사용자가 정책을 구별할 수 있도록 하는 의미 있는 이름입니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "정책의 표시 이름",
        example = "Default Access Policy"
    )
    private String name;
    
    /**
     * 정책 설명
     * 
     * <p>정책의 목적과 적용 범위를 설명하는 텍스트입니다.
     * 정책이 어떤 권한을 부여하거나 제한하는지 명시합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "정책의 목적과 적용 범위 설명",
        example = "기본 접근 권한 정책"
    )
    private String description;
    
    /**
     * 정책 활성화 여부
     * 
     * <p>정책이 현재 활성화되어 있는지 여부입니다.
     * false인 경우 정책이 비활성화되어 권한 검사에 사용되지 않습니다.</p>
     */
    @JsonProperty("enabled")
    @Schema(
        description = "정책 활성화 여부",
        example = "true"
    )
    private Boolean enabled;
    
    /**
     * 정책 생성 시간
     * 
     * <p>정책이 처음 생성된 시간입니다.
     * ISO 8601 형식의 UTC 시간으로 표현됩니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "정책 생성 시간 (ISO 8601 UTC)",
        example = "2025-10-16T10:30:00Z"
    )
    private String createdAt;
    
    /**
     * 정책 최종 수정 시간
     * 
     * <p>정책이 마지막으로 수정된 시간입니다.
     * ISO 8601 형식의 UTC 시간으로 표현됩니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "정책 최종 수정 시간 (ISO 8601 UTC)",
        example = "2025-10-16T10:30:00Z"
    )
    private String updatedAt;
}