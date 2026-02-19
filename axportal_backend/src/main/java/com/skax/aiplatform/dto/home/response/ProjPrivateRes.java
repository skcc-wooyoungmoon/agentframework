package com.skax.aiplatform.dto.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjPrivateRes {
    private String prjSeq;
    private String memberId;
    private String roleSeq;
    private String prjNm;
    private String dtlCtnt;
    private int memberCount;
    private String createrInfo;
    private String fstCreatedAt;
}
