package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 수정 요청 DTO
 * 
 * <p>기존 Inference Prompt를 수정하기 위한 요청 데이터 구조입니다.
 * 프롬프트의 기본 정보, 메시지, 변수, 태그를 업데이트할 수 있으며, 
 * 수정 시 새로운 버전이 생성됩니다.</p>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 이름, 설명</li>
 *   <li><strong>메시지</strong>: 대화형 프롬프트 메시지 체인</li>
 *   <li><strong>변수</strong>: 동적 값 치환을 위한 변수 정의</li>
 *   <li><strong>태그</strong>: 분류 및 검색을 위한 태그</li>
 * </ul>
 * 
 * <h3>버전 관리:</h3>
 * <ul>
 *   <li>수정 시 자동으로 새로운 버전 생성</li>
 *   <li>기존 버전은 보존되어 이력 관리</li>
 *   <li>최신 버전이 기본 사용 버전으로 설정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PromptUpdateRequest request = PromptUpdateRequest.builder()
 *     .name("Updated Customer Support Assistant")
 *     .description("개선된 고객 문의 응답 AI 어시스턴트 프롬프트")
 *     .messages(Arrays.asList(
 *         PromptMessage.builder()
 *             .mtype(1)
 *             .message("You are an expert customer support assistant with years of experience.")
 *             .build(),
 *         PromptMessage.builder()
 *             .mtype(2)
 *             .message("Customer Issue: {{customer_query}}\nPriority: {{priority_level}}")
 *             .build()
 *     ))
 *     .variables(Arrays.asList(
 *         PromptVariable.builder()
 *             .variable("{{customer_query}}")
 *             .validation("required|min:10|max:1000")
 *             .build(),
 *         PromptVariable.builder()
 *             .variable("{{priority_level}}")
 *             .validation("required|in:low,medium,high,urgent")
 *             .build()
 *     ))
 *     .tags(Arrays.asList(
 *         PromptTag.builder().tag("customer-support").build(),
 *         PromptTag.builder().tag("enhanced").build()
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see PromptCreateRequest 프롬프트 생성 요청 (동일한 구조)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Inference Prompt 수정 요청 정보",
    example = """
        {
          "new_name": "Updated Customer Support Assistant",
          "desc": "개선된 고객 문의 응답 AI 어시스턴트 프롬프트",
          "messages": [
            {
              "mtype": 1,
              "message": "You are an expert customer support assistant with years of experience."
            },
            {
              "mtype": 2,
              "message": "Customer Issue: {{customer_query}}"
            }
          ],
          "variables": [
            {
              "variable": "{{customer_query}}",
              "validation": "required|min:10|max:1000"
            }
          ],
          "tags": [
            {
              "tag": "customer-support"
            },
            {
              "tag": "enhanced"
            }
          ]
        }
        """
)
public class PromptUpdateRequest {
    
    /**
     * 프롬프트 이름
     * 
     * <p>수정할 프롬프트의 새로운 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 프롬프트의 목적을 명확히 나타내야 합니다.</p>
     * 
     * @implNote 이름 변경 시 참조하는 시스템들에 영향을 줄 수 있으므로 주의가 필요합니다.
     */
    @JsonProperty("new_name")
    @Schema(
        description = "수정할 프롬프트 이름 (프로젝트 내 중복 불가)", 
        example = "Updated Customer Support Assistant",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String newName;

    @JsonProperty
    @Schema(
            description = "프롬프트 배포 여부",
            example = "false",
            required = true,
            minLength = 3,
            maxLength = 100
    )
    private boolean release;

    /**
     * 프롬프트 설명
     * 
     * <p>수정할 프롬프트의 새로운 설명입니다.
     * 변경된 내용이나 개선사항을 포함하여 명확하게 작성합니다.</p>
     */
    @JsonProperty("desc")
    @Schema(
        description = "수정할 프롬프트 설명 (변경사항 포함)", 
        example = "개선된 고객 문의 응답 AI 어시스턴트 프롬프트",
        maxLength = 1000
    )
    private String description;
    
    /**
     * 프롬프트 메시지 목록
     * 
     * <p>수정할 대화형 프롬프트를 구성하는 새로운 메시지들의 목록입니다.
     * 기존 메시지들을 완전히 대체하며, 순서가 중요합니다.</p>
     * 
     * @implNote 기존 메시지들은 삭제되고 새로운 메시지들로 완전 교체됩니다.
     * @apiNote 최소 1개 이상의 메시지가 필요합니다.
     */
    @JsonProperty("messages")
    @Schema(
        description = "수정할 프롬프트 메시지 목록 (기존 메시지 완전 대체)",
        required = true
    )
    private List<PromptMessage> messages;
    
    /**
     * 프롬프트 변수 목록
     * 
     * <p>수정할 프롬프트에서 사용될 새로운 변수들의 정의 목록입니다.
     * 기존 변수들을 완전히 대체합니다.</p>
     * 
     * @implNote 새로운 변수 추가 시 메시지에서 해당 변수가 사용되는지 확인해야 합니다.
     */
    @JsonProperty("variables")
    @Schema(
        description = "수정할 프롬프트 변수 정의 목록 (기존 변수 완전 대체)"
    )
    private List<PromptVariable> variables;
    
    /**
     * 프롬프트 태그 목록
     * 
     * <p>수정할 프롬프트에 적용될 새로운 태그들의 목록입니다.
     * 기존 태그들을 완전히 대체합니다.</p>
     * 
     * @implNote 태그 변경은 검색 및 분류에 영향을 줄 수 있습니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "수정할 프롬프트 태그 목록 (기존 태그 완전 대체)"
    )
    private List<PromptTag> tags;
    
    /**
     * 프롬프트 타입
     * 
     * <p>프롬프트의 타입을 나타내는 값입니다.
     * 가드레일 프롬프트는 2, 추론 프롬프트는 1로 설정됩니다.</p>
     * 
     * @apiNote 수정 시에도 기존 ptype을 유지해야 합니다.
     */
    @JsonProperty("ptype")
    @Schema(
        description = "프롬프트 타입 (가드레일 프롬프트: 2, 추론 프롬프트: 1)",
        example = "2"
    )
    private Integer ptype;
    
    /**
     * 프롬프트 메시지 구조
     * 
     * <p>개별 프롬프트 메시지의 구조를 정의합니다.
     * PromptCreateRequest의 PromptMessage와 동일한 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 메시지 구조")
    public static class PromptMessage {
        
        /**
         * 메시지 타입
         * 
         * <p>메시지의 역할을 나타내는 타입입니다.</p>
         * <ul>
         *   <li><strong>0: text</strong> - 일반 텍스트</li>
         *   <li><strong>1: system</strong> - 시스템 메시지 (AI 행동 지침)</li>
         *   <li><strong>2: user</strong> - 사용자 메시지</li>
         *   <li><strong>3: assistant</strong> - 어시스턴트 응답 메시지</li>
         * </ul>
         */
        @JsonProperty("mtype")
        @Schema(
            description = "메시지 타입 (0:text, 1:system, 2:user, 3:assistant)", 
            example = "1",
            allowableValues = {"0", "1", "2", "3"},
            required = true
        )
        private Integer mtype;
        
        /**
         * 메시지 내용
         * 
         * <p>수정할 메시지의 새로운 텍스트입니다. 
         * 변수 치환을 위한 {{variable}} 형태의 플레이스홀더를 포함할 수 있습니다.</p>
         */
        @JsonProperty("message")
        @Schema(
            description = "수정할 메시지 내용 (변수 플레이스홀더 {{variable}} 포함 가능)", 
            example = "You are an expert customer support assistant with years of experience.",
            required = true,
            maxLength = 10000
        )
        private String message;
    }
    
    /**
     * 프롬프트 변수 구조
     * 
     * <p>프롬프트에서 사용할 변수의 구조를 정의합니다.
     * PromptCreateRequest의 PromptVariable과 동일한 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 변수 구조")
    public static class PromptVariable {
        
        /**
         * 변수명
         * 
         * <p>메시지에서 치환될 변수의 이름입니다. {{variable_name}} 형식으로 사용됩니다.</p>
         */
        @JsonProperty("variable")
        @Schema(
            description = "변수명 ({{variable_name}} 형식)", 
            example = "{{customer_query}}",
            required = true,
            pattern = "^\\{\\{[a-zA-Z_][a-zA-Z0-9_]*\\}\\}$"
        )
        private String variable;
        
        /**
         * 변수 검증 규칙
         * 
         * <p>변수 값에 대한 검증 규칙을 파이프(|)로 구분하여 정의합니다.</p>
         * <ul>
         *   <li><strong>required</strong> - 필수 값</li>
         *   <li><strong>min:숫자</strong> - 최소 길이</li>
         *   <li><strong>max:숫자</strong> - 최대 길이</li>
         *   <li><strong>regex:패턴</strong> - 정규식 패턴</li>
         *   <li><strong>in:값1,값2</strong> - 허용값 목록</li>
         * </ul>
         */
        @JsonProperty("validation")
        @Schema(
            description = "변수 검증 규칙 (파이프 구분: required|min:10|max:1000|in:low,medium,high)", 
            example = "required|min:10|max:1000"
        )
        private String validation;

        @Builder.Default
        @JsonProperty("validation_flag")
        private Boolean validationFlag = Boolean.FALSE;

        @Builder.Default
        @JsonProperty("token_limit_flag")
        private Boolean tokenLimitFlag = Boolean.FALSE;

        @Builder.Default
        @JsonProperty("token_limit")
        private Integer tokenLimit = 0;
    }
    
    /**
     * 프롬프트 태그 구조
     * 
     * <p>프롬프트 분류를 위한 태그의 구조를 정의합니다.
     * PromptCreateRequest의 PromptTag와 동일한 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 태그 구조")
    public static class PromptTag {
        
        /**
         * 태그명
         * 
         * <p>프롬프트를 분류하기 위한 태그 이름입니다.
         * 영문자, 숫자, 하이픈(-)만 사용 가능하며 대소문자를 구분하지 않습니다.</p>
         */
        @JsonProperty("tag")
        @Schema(
            description = "태그명 (영문자, 숫자, 하이픈만 허용)", 
            example = "customer-support",
            required = true,
            pattern = "^[a-zA-Z0-9-]+$",
            maxLength = 50
        )
        private String tag;
        
        /**
         * 버전 ID (수정 시 필요)
         */
        @JsonProperty("version_id")
        @Schema(
            description = "버전 ID (수정 시 기존 태그와 연결)",
            example = "569e0645-52d7-4f24-93fb-38374bbeedb7"
        )
        private String versionId;
    }
}
