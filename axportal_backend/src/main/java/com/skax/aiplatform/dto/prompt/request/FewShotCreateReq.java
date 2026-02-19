package com.skax.aiplatform.dto.prompt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Few-Shot 생성 요청 DTO
 * 
 * <p>새로운 Few-Shot 예제를 생성할 때 사용되는 요청 데이터입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-11
 * @version 1.0.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Few-Shot 생성 요청")
public class FewShotCreateReq {

    @NotBlank(message = "Few-Shot 이름은 필수 입력 항목입니다.")
    @Size(min = 1, max = 100, message = "Few-Shot 이름은 1자 이상 100자 이하로 입력해주세요.")
    @Schema(description = "Few-Shot 이름", example = "고객 상담 Few-Shot", required = true)
    private String name;

    @Size(max = 200, message = "프로젝트 ID는 200자 이하로 입력해주세요.")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @Builder.Default
    @Schema(description = "릴리즈 여부", example = "false")
    private Boolean release = false;

    @NotEmpty(message = "Few-Shot 항목은 최소 하나 이상 필요합니다.")
    @Valid
    @Schema(description = "Few-Shot 항목들")
    private List<FewShotItem> items;

    @NotEmpty(message = "태그는 최소 하나 이상 필요합니다.")
    @Valid
    @Schema(description = "태그들")
    private List<FewShotTag> tags;

    /**
     * Few-Shot 항목
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 항목")
    public static class FewShotItem {
        
        @NotBlank(message = "질문은 필수 입력 항목입니다.")
        @Size(min = 1, message = "질문은 1자 이상 입력해주세요.")
        @Schema(description = "질문", example = "안녕하세요. 보험 상담 받고 싶습니다.", required = true)
        private String itemQuery;

        @NotBlank(message = "답변은 필수 입력 항목입니다.")
        @Size(min = 1,  message = "답변은 1자 이상 입력해주세요.")
        @Schema(description = "답변", example = "안녕하세요! 보험 상담을 도와드리겠습니다. 어떤 종류의 보험에 관심이 있으신가요?", required = true)
        private String itemAnswer;
    }

    /**
     * Few-Shot 태그
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 태그")
    public static class FewShotTag {
        
        @NotBlank(message = "태그는 필수 입력 항목입니다.")
        @Size(min = 1, max = 50, message = "태그는 1자 이상 50자 이하로 입력해주세요.")
        @Schema(description = "태그", example = "고객상담", required = true)
        private String tag;
    }
} 