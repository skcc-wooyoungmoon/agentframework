package com.skax.aiplatform.dto.prompt.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "추론프롬프트 상세 정보")
public class InfPromptByIdRes {

    @JsonProperty("uuid")
    @Schema(description = "프롬프트 UUID", example = "4ff2dab7-bffe-414d-88a5-1826b9fea8df")
    private String uuid;

    @JsonProperty("name")
    @Schema(description = "프롬프트 이름", example = "Insurance Consultation Prompt")
    private String name;

    @JsonProperty("desc")
    @Schema(description = "프롬프트 설명", example = "고객 문의 응답용 AI 어시스턴트")
    private String description;

    @JsonProperty("message")
    @Schema(description = "첫 번째 메시지 내용", example = "You are a helpful AI assistant...")
    private String message;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @JsonProperty("created_at")
    @Schema(description = "생성 일시", example = "2025-08-26T04:46:14.648658Z")
    private String createdAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자 사용자 ID", example = "3398a9ff-2617-4dd0-9413-8b785aea9d33")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자 사용자 ID", example = "3398a9ff-2617-4dd0-9413-8b785aea9d33")
    private String updatedBy;

    @JsonProperty("updated_at")
    @Schema(description = "수정 일시", example = "2025-10-23T04:15:27.912907Z")
    private String updatedAt;

    @JsonProperty("created_by_name")
    @Schema(description = "생성자 한글명", example = "김철수")
    private String createdByName;

    @JsonProperty("created_by_depts")
    @Schema(description = "생성자 부서명", example = "IT개발팀")
    private String createdByDepts;

    @JsonProperty("created_by_pos")
    @Schema(description = "생성자 직급명", example = "팀장")
    private String createdByPos;

    @JsonProperty("updated_by_name")
    @Schema(description = "수정자 한글명", example = "이영희")
    private String updatedByName;

    @JsonProperty("updated_by_depts")
    @Schema(description = "수정자 부서명", example = "IT개발팀")
    private String updatedByDepts;

    @JsonProperty("updated_by_pos")
    @Schema(description = "수정자 직급명", example = "대리")
    private String updatedByPos;

    @JsonProperty("ptype")
    @Schema(description = "프롬프트 타입(ptype)", example = "0")
    private Integer ptype;

    @JsonProperty("delete_flag")
    @Schema(description = "삭제 여부(delete_flag)", example = "false")
    private Boolean deleteFlag;

    @JsonProperty("release_version")
    @Schema(description = "릴리즈 버전(release_version)", example = "2")
    private Integer releaseVersion;

    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    @Builder.Default
    private List<InfPromptRes.TagInfo> tags = List.of();

    @JsonProperty("fstPrjSeq")
    @Schema(description = "최초 프로젝트 시퀀스")
    private Integer fstPrjSeq;

    @JsonProperty("lstPrjSeq")
    @Schema(description = "최종 프로젝트 시퀀스")
    private Integer lstPrjSeq;
}