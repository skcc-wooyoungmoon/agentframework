package com.skax.aiplatform.dto.prompt.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "워크플로우 기본 정보")
public class WorkFlowRes {
    @Schema(description = "워크플로우 식별자 (논리 단위)", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private UUID workflowId;

    @Schema(description = "워크플로우 이름 (논리 단위)", example = "Customer Support Assistant")
    private String workflowName;

    @Schema(description = "워크플로우 버전", example = "1")
    private Integer versionNo;

    @Schema(description = "워크플로우 XML", example = "<xml/>")
    private String xmlText;

    @Schema(description = "워크플로우 설명", example = "고객 상담용 Assistant 워크플로우")
    private String description;

    @Schema(description = "사용 여부", example = "true")
    private char isActive; // 또는 primitive boolean

    @Schema(description = "생성 일시", example = "2025-08-15T10:30:00Z")
    private Instant createdAt;

    @Schema(description = "생성자", example = "kimsh")
    private String createdBy;

    @Schema(description = "최종 수정 일시", example = "2025-08-15T11:45:00Z")
    private Instant updatedAt;

    @Schema(description = "최종 수정자", example = "leejw")
    private String updatedBy;

    @Schema(description = "태그 목록", example = "[\"customer-support\",\"chatbot\"]")
    @Builder.Default
    private List<String> tags = List.of();

    @Schema(description = "원본 태그 문자열(쉼표 구분)", example = "customer-support,chatbot")
    private String tagsRaw;

    @Schema(description = "프로젝트 SEQ", example = "-999")
    private long projectSeq;

    @Schema(description = "프로젝트 공개 범위", example = "private")
    private String projectScope;
}
