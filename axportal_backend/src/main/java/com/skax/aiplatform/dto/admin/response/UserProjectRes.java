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
@Schema(description = "사용자가 참여한 프로젝트 응답")
public class UserProjectRes {

    @Schema(description = "프로젝트 시퀀스")
    private String prjSeq;

    @Schema(description = "프로젝트 생성 후 승인이되면 SKT에서 받아오는 프로젝트 ID")
    private String uuid;

    @Schema(description = "프로젝트명")
    private String prjNm;

    @Schema(description = "프로젝트 설명")
    private String dtlCtnt;

    @Schema(description = "민감정보 포함 여부")
    private String sstvInfInclYn;

    @Schema(description = "민감정보 포함 사유")
    private String sstvInfInclDesc;

    @Schema(description = "최초 생성일시")
    private String fstCreatedAt;

    @Schema(description = "최종 수정일시")
    private String lstUpdatedAt;

    @Schema(description = "최초 생성자")
    private String createdBy;

    @Schema(description = "최종 수정자")
    private String updatedBy;

    public static UserProjectRes from(Project project) {
        return UserProjectRes.builder()
                .prjSeq(project.getPrjSeq().toString())
                .uuid(project.getUuid())
                .prjNm(project.getPrjNm())
                .dtlCtnt(project.getDtlCtnt())
                .sstvInfInclYn(project.getSstvInfInclYn().toString())
                .sstvInfInclDesc(project.getSstvInfInclDesc())
                .fstCreatedAt(DateUtils.toDateTimeString(project.getFstCreatedAt()))
                .lstUpdatedAt(DateUtils.toDateTimeString(project.getLstUpdatedAt()))
                .createdBy(project.getCreatedBy())
                .updatedBy(project.getUpdatedBy())
                .build();
    }

}
