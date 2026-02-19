package com.skax.aiplatform.dto.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRes {
    private String prjSeq;
    private String memberId;
    private String roleSeq;
    private String prjNm;
    private String dtlCtnt;
    private String uuid;
    private int memberCnt;
    private boolean selected;
}
