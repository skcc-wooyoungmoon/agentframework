package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Evaluation API 기본 정책 정보 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 사용되는 기본 정책 구조입니다.
 * 사용자, 그룹, 역할, 토큰 교환 등의 정책 타입을 지원합니다.</p>
 * 
 * <h3>지원하는 정책 타입:</h3>
 * <ul>
 *   <li><strong>user</strong>: 사용자 기반 정책</li>
 *   <li><strong>group</strong>: 그룹 기반 정책</li>
 *   <li><strong>role</strong>: 역할 기반 정책</li>
 *   <li><strong>token-exchange</strong>: 토큰 교환 정책</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * BasePolicy policy = BasePolicy.builder()
 *     .type("user")
 *     .logic("POSITIVE")
 *     .names(Arrays.asList("admin", "user"))
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
    description = "SKTAI Evaluation API 기본 정책 정보",
    example = """
        {
          "type": "user",
          "logic": "POSITIVE",
          "names": ["admin", "user"]
        }
        """
)
public class BasePolicy {
    
    /**
     * 정책 타입
     * 
     * <p>정책이 적용될 대상의 타입을 지정합니다.</p>
     * 
     * @apiNote user, group, role, token-exchange 중 하나의 값을 사용해야 합니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "정책 타입", 
        example = "user",
        required = true,
        allowableValues = {"user", "group", "role", "token-exchange"}
    )
    private String type;
    
    /**
     * 정책 로직
     * 
     * <p>정책 적용 시 사용할 로직을 지정합니다.
     * POSITIVE는 허용, NEGATIVE는 거부를 의미합니다.</p>
     * 
     * @implNote 기본값은 POSITIVE입니다.
     */
    @JsonProperty("logic")
    @Schema(
        description = "정책 로직 (POSITIVE: 허용, NEGATIVE: 거부)", 
        example = "POSITIVE",
        allowableValues = {"POSITIVE", "NEGATIVE"},
        defaultValue = "POSITIVE"
    )
    @Builder.Default
    private String logic = "POSITIVE";
    
    /**
     * 대상 이름 목록
     * 
     * <p>정책이 적용될 대상들의 이름 목록입니다.
     * 타입에 따라 사용자명, 그룹명, 역할명 등이 될 수 있습니다.</p>
     * 
     * @apiNote 최소 1개 이상의 이름이 필요합니다.
     */
    @JsonProperty("names")
    @Schema(
        description = "정책 적용 대상 이름 목록", 
        example = "[\"admin\", \"user\"]",
        required = true
    )
    private List<String> names;
}
