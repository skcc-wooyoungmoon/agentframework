package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Inference Prompt 생성 요청 DTO
 *
 * <p>SKTAI Agent 시스템에서 새로운 Inference Prompt를 생성하기 위한 요청 데이터 구조입니다.
 * 프롬프트 메시지, 변수, 태그를 포함하여 AI Agent가 추론에 사용할 프롬프트를 정의합니다.</p>
 *
 * <h3>구성 요소:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 이름, 설명, 프로젝트 연결</li>
 *   <li><strong>메시지</strong>: 대화형 프롬프트 메시지 체인</li>
 *   <li><strong>변수</strong>: 동적 값 치환을 위한 변수 정의</li>
 *   <li><strong>태그</strong>: 분류 및 검색을 위한 태그</li>
 * </ul>
 *
 * <h3>메시지 타입:</h3>
 * <ul>
 *   <li><strong>0: text</strong> - 일반 텍스트</li>
 *   <li><strong>1: system</strong> - 시스템 메시지</li>
 *   <li><strong>2: user</strong> - 사용자 메시지</li>
 *   <li><strong>3: assistant</strong> - 어시스턴트 메시지</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * PromptCreateRequest request = PromptCreateRequest.builder()
 *     .name("Customer Support Assistant")
 *     .description("고객 문의 응답을 위한 AI 어시스턴트 프롬프트")
 *     .projectId("project-123")
 *     .messages(Arrays.asList(
 *         PromptMessage.builder()
 *             .mtype(1)
 *             .message("You are a helpful customer support assistant.")
 *             .build(),
 *         PromptMessage.builder()
 *             .mtype(2)
 *             .message("Customer: {{customer_query}}")
 *             .build(),
 *         PromptMessage.builder()
 *             .mtype(3)
 *             .message("I understand your concern. Let me help you with {{customer_query}}.")
 *             .build()
 *     ))
 *     .variables(Arrays.asList(
 *         PromptVariable.builder()
 *             .variable("{{customer_query}}")
 *             .validation("required|min:10|max:500")
 *             .build()
 *     ))
 *     .tags(Arrays.asList(
 *         PromptTag.builder().tag("customer-support").build(),
 *         PromptTag.builder().tag("chatbot").build()
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see PromptMessage 프롬프트 메시지 구조
 * @see PromptVariable 프롬프트 변수 구조
 * @see PromptTag 프롬프트 태그 구조
 * @since 2025-08-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI Agent Inference Prompt 생성 요청 정보",
        example = """
                {
                  "name": "Customer Support Assistant",
                  "description": "고객 문의 응답을 위한 AI 어시스턴트 프롬프트",
                  "project_id": "d89a7451-3d40-4bab-b4ee-6aecd55b4f32",
                  "messages": [
                    {
                      "mtype": 1,
                      "message": "You are a helpful customer support assistant."
                    },
                    {
                      "mtype": 2,
                      "message": "Customer: {{customer_query}}"
                    }
                  ],
                  "variables": [
                    {
                      "variable": "{{customer_query}}",
                      "validation": "required|min:10|max:500"
                    }
                  ],
                  "tags": [
                    {
                      "tag": "customer-support"
                    }
                  ]
                }
                """
)
public class PromptCreateRequest {

    boolean release;

    /**
     * 프롬프트 이름
     *
     * <p>프롬프트를 식별하는 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 프롬프트의 목적을 명확히 나타내야 합니다.</p>
     *
     * @implNote 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
            description = "프롬프트 고유 이름 (프로젝트 내 중복 불가)",
            example = "Customer Support Assistant",
            required = true,
            minLength = 3,
            maxLength = 100
    )
    private String name;

    /**
     * 프롬프트 설명
     *
     * <p>프롬프트의 목적과 사용 방법을 설명하는 텍스트입니다.
     * 다른 사용자들이 프롬프트의 용도를 이해할 수 있도록 명확하게 작성합니다.</p>
     */
    @JsonProperty("desc")
    @Schema(
            description = "프롬프트 설명 (목적과 사용 방법)",
            example = "고객 문의 응답을 위한 AI 어시스턴트 프롬프트",
            maxLength = 1000
    )
    private String description;

    /**
     * 프로젝트 식별자
     *
     * <p>프롬프트가 속할 프로젝트의 고유 식별자입니다.
     * 프로젝트는 프롬프트의 접근 권한과 관리 범위를 결정합니다.</p>
     *
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
            description = "프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)",
            example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32",
            defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32"
    )
    private String projectId;

    /**
     * 프롬프트 메시지 목록
     *
     * <p>대화형 프롬프트를 구성하는 메시지들의 순서대로 배열된 목록입니다.
     * 각 메시지는 타입(system/user/assistant)과 내용을 가집니다.</p>
     *
     * @implNote 메시지는 순서가 중요하며, 대화의 흐름을 나타냅니다.
     * @apiNote 최소 1개 이상의 메시지가 필요합니다.
     */
    @JsonProperty("messages")
    @Schema(
            description = "프롬프트 메시지 목록 (순서 중요)",
            required = true
    )
    private List<PromptMessage> messages;

    /**
     * 프롬프트 변수 목록
     *
     * <p>프롬프트 메시지에서 동적으로 치환될 변수들의 정의 목록입니다.
     * 각 변수는 이름과 검증 규칙을 가집니다.</p>
     *
     * @implNote 변수명은 {{variable_name}} 형식으로 메시지에서 사용됩니다.
     * @apiNote 변수가 없는 경우 빈 배열을 전달할 수 있습니다.
     */
    @JsonProperty("variables")
    @Schema(
            description = "프롬프트 변수 정의 목록"
    )
    private List<PromptVariable> variables;

    /**
     * 프롬프트 태그 목록
     *
     * <p>프롬프트 분류 및 검색을 위한 태그들의 목록입니다.
     * 태그를 통해 관련 프롬프트들을 그룹화하고 효율적으로 검색할 수 있습니다.</p>
     *
     * @implNote 태그는 대소문자를 구분하지 않으며, 공백은 하이픈(-)으로 대체됩니다.
     * @apiNote 태그가 없는 경우 빈 배열을 전달할 수 있습니다.
     */
    @JsonProperty("tags")
    @Schema(
            description = "프롬프트 분류 태그 목록"
    )
    private List<PromptTag> tags;

    /**
     * 프롬프트 타입
     *
     * <p>프롬프트의 타입을 나타내는 값입니다.
     * 가드레일 프롬프트는 2로 설정됩니다.</p>
     *
     * @apiNote 가드레일 프롬프트 생성 시 2로 설정됩니다.
     */
    @JsonProperty("ptype")
    @Schema(
            description = "프롬프트 타입 (가드레일 프롬프트: 2)",
            example = "2"
    )
    private Integer ptype;

    /**
     * 프롬프트 메시지 구조
     *
     * <p>개별 프롬프트 메시지의 구조를 정의합니다.</p>
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
         * <p>실제 메시지 텍스트입니다. 변수 치환을 위한 {{variable}} 형태의 플레이스홀더를 포함할 수 있습니다.</p>
         */
        @JsonProperty("message")
        @Schema(
                description = "메시지 내용 (변수 플레이스홀더 {{variable}} 포함 가능)",
                example = "You are a helpful customer support assistant.",
                required = true,
                maxLength = 10000
        )
        private String message;

    }

    /**
     * 프롬프트 변수 구조
     *
     * <p>프롬프트에서 사용할 변수의 구조를 정의합니다.</p>
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
         * </ul>
         */
        @JsonProperty("validation")
        @Schema(
                description = "변수 검증 규칙 (파이프 구분: required|min:10|max:500)",
                example = "required|min:10|max:500"
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
     * <p>프롬프트 분류를 위한 태그의 구조를 정의합니다.</p>
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
         * 버전 ID (선택 사항, 생성 시에는 불필요)
         */
        @JsonProperty("version_id")
        @Schema(
                description = "버전 ID (선택 사항)",
                example = "569e0645-52d7-4f24-93fb-38374bbeedb7"
        )
        private String versionId;

    }

}
