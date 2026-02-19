package com.skax.aiplatform.dto.prompt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "워크플로우 생성 요청")
public class WorkFlowCreateReq {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "워크플로우 이름", example = "Customer Support Assistant", maxLength = 50)
    private String workflowName;

    @Schema(description = "워크플로우 XML", example = "<xml/>")
    private String xmlText;

    @Schema(description = "설명", example = "고객 상담 시나리오를 정의한 워크플로우")
    private String description;

    @Builder.Default
    @Schema(description = "사용 여부", example = "Y", defaultValue = "Y")
    private char isActive = 'Y';

    @Size(max = 100)
    @Schema(description = "태그(쉼표 구분 문자열)", example = "chatbot,customer-support", maxLength = 100)
    private String tag;

    @NotNull
    @Schema(description = "프로젝트 SEQ", example = "-999")
    private long projectSeq;

    @Size(max = 30)
    @Schema(description = "프로젝트 공개 범위", example = "private", maxLength = 30)
    private String projectScope;

    @Size(max = 50)
    @Schema(description = "생성자", example = "kimsh", maxLength = 50)
    private String createdBy;
}
