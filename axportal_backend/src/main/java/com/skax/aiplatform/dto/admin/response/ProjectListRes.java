package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.project.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@Schema(description = "프로젝트 응답")
public class ProjectListRes {

    @Schema(description = "프로젝트 시퀀스")
    private Long prjSeq;

    @Schema(description = "프로젝트 아이디")
    private String uuid;

    @Schema(description = "프로젝트명")
    private String prjNm;

    @Schema(description = "프로젝트 설명")
    private String dtlCtnt;

    @Schema(description = "최초 생성일시")
    private String fstCreatedAt;

    @Schema(description = "최종 수정일시")
    private String lstUpdatedAt;

    public static ProjectListRes of(Project project) {
        return ProjectListRes.builder()
                .prjSeq(project.getPrjSeq())
                .uuid(project.getUuid())
                .prjNm(project.getPrjNm())
                .dtlCtnt(project.getDtlCtnt())
                .fstCreatedAt(DateUtils.toDateTimeString(project.getFstCreatedAt()))
                .lstUpdatedAt(DateUtils.toDateTimeString(project.getLstUpdatedAt()))
                .build();
    }

}
