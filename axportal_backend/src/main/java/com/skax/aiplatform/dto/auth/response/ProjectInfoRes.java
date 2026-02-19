package com.skax.aiplatform.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoRes {
    private String prjNm;
    private String prjSeq;
    private String prjUuid;
    private String prjDesc;

    private String prjRoleNm;
    private String prjRoleSeq;
    private boolean active;

    private String adxpGroupNm;
    private String adxpGroupPath;
}