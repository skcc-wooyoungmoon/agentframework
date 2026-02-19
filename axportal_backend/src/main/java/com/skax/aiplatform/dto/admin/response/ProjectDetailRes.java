package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.project.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "프로젝트 상세 응답")
public class ProjectDetailRes {

    private final ProjectInfo project;

    @Getter
    @Builder
    public static class ProjectInfo {

        @Schema(description = "프로젝트 시퀀스")
        private final Long prjSeq;

        @Schema(description = "프로젝트 아이디")
        private final String uuid;

        @Schema(description = "프로젝트명")
        private final String prjNm;

        @Schema(description = "프로젝트 설명")
        private final String dtlCtnt;

        @Schema(description = "민감정보 포함 여부")
        private final String sstvInfInclYn;

        @Schema(description = "민감정보 포함 사유")
        private final String sstvInfInclDesc;

        @Schema(description = "최초 생성일시")
        private final String fstCreatedAt;

        @Schema(description = "최종 수정일시")
        private final String lstUpdatedAt;

        @Schema(description = "최초 생성자 정보")
        private final AuditorInfo createdBy;

        @Schema(description = "최종 수정자 정보")
        private final AuditorInfo updatedBy;

    }

    @Getter
    @Builder
    public static class AuditorInfo {

        @Schema(description = "이름", example = "김신한")
        private final String jkwNm;

        @Schema(description = "부서명", example = "AI플랫폼셀")
        private final String deptNm;

    }

    public static ProjectDetailRes of(Project project, GpoUsersMas createdBy, GpoUsersMas updatedBy) {
        AuditorInfo createdByInfo = AuditorInfo.builder()
                .jkwNm(createdBy != null ? createdBy.getJkwNm() : "")
                .deptNm(createdBy != null ? createdBy.getDeptNm() : "")
                .build();

        AuditorInfo updatedByInfo = AuditorInfo.builder()
                .jkwNm(updatedBy != null ? updatedBy.getJkwNm() : "")
                .deptNm(updatedBy != null ? updatedBy.getDeptNm() : "")
                .build();

        ProjectInfo projectInfo = ProjectInfo.builder()
                .prjSeq(project.getPrjSeq())
                .uuid(project.getUuid())
                .prjNm(project.getPrjNm())
                .dtlCtnt(project.getDtlCtnt())
                .sstvInfInclYn(project.getSstvInfInclYn().toString())
                .sstvInfInclDesc(project.getSstvInfInclDesc())
                .fstCreatedAt(DateUtils.toDateTimeString(project.getFstCreatedAt()))
                .lstUpdatedAt(DateUtils.toDateTimeString(project.getLstUpdatedAt()))
                .createdBy(createdByInfo)
                .updatedBy(updatedByInfo)
                .build();

        return ProjectDetailRes.builder()
                .project(projectInfo)
                .build();
    }

}
