package com.skax.aiplatform.dto.home.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfo {
    private String id;
    private String projectName;
    private String roleId;
    private String createdBy;
    private String uuid;
}
