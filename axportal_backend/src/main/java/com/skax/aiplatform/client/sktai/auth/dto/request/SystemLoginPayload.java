package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 시스템 로그인 요청 DTO
 * 
 * <p>시스템 간 인증을 위한 로그인 요청 데이터입니다.
 * 사용자 정보를 시스템에 업데이트하고 액세스 토큰을 발급받습니다.</p>
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
    description = "SKTAI 시스템 로그인 요청",
    example = """
        {
          "username": "system_user",
          "roles": ["admin", "user"],
          "groups": ["group1", "group2"]
        }
        """
)
public class SystemLoginPayload {
    
    /**
     * 사용자명
     * 
     * <p>시스템에서 관리하는 사용자의 사용자명입니다.</p>
     */
    @JsonProperty("username")
    @Schema(
        description = "시스템 사용자명", 
        example = "system_user",
        required = true
    )
    private String username;
    
    /**
     * 사용자 역할 목록
     * 
     * <p>사용자에게 할당할 역할 목록입니다. 기본값은 빈 배열입니다.</p>
     */
    @JsonProperty("roles")
    @Schema(
        description = "사용자 역할 목록", 
        example = "[\"admin\", \"user\"]"
    )
    @Builder.Default
    private List<Object> roles = List.of();
    
    /**
     * 사용자 그룹 목록
     * 
     * <p>사용자가 속할 그룹 목록입니다. 기본값은 빈 배열입니다.</p>
     */
    @JsonProperty("groups")
    @Schema(
        description = "사용자 그룹 목록", 
        example = "[\"group1\", \"group2\"]"
    )
    @Builder.Default
    private List<Object> groups = List.of();
}
