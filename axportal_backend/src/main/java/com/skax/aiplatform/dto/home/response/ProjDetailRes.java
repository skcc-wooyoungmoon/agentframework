package com.skax.aiplatform.dto.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjDetailRes {
    private Long prjSeq;
    private String fstCreatedAt;
    private String createrInfo;
    private String projMgmteInfo;
}
