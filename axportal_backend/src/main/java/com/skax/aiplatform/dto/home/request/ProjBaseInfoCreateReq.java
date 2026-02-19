package com.skax.aiplatform.dto.home.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjBaseInfoCreateReq {
    private String username;
    private String name;
    private String description;
    private String projectId;
    private long prjSeq;
    private String is_sensitive;
    private String sensitive_reason;
    private Object[] member_ids;
    private String is_portal_admin;
}
