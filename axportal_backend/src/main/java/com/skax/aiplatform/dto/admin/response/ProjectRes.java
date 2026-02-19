package com.skax.aiplatform.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 프로젝트 응답 DTO
 * 
 * @author sonmunwoo
 * @since 2025-10-21
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 응답 정보")
public class ProjectRes {

    /**
     * 프로젝트 시퀀스
     */
    @Schema(description = "프로젝트 시퀀스", example = "1")
    private Long prjSeq;

    /**
     * 프로젝트 UUID
     */
    @Schema(description = "프로젝트 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuid;

    /**
     * 프로젝트명
     */
    @Schema(description = "프로젝트명", example = "AI 플랫폼 프로젝트")
    private String prjNm;

    /**
     * Entity를 DTO로 변환
     * 
     * @param project 프로젝트 Entity
     * @return 프로젝트 응답 DTO
     */
    public static ProjectRes from(com.skax.aiplatform.entity.project.Project project) {
        if (project == null) {
            return null;
        }
        
        return ProjectRes.builder()
                .prjSeq(project.getPrjSeq())
                .uuid(project.getUuid())
                .prjNm(project.getPrjNm())
                .build();
    }
}

