package com.skax.aiplatform.dto.home.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeCreateReq {
    private List<Long> prjSeq;
    private String userId;
    private String imgUuid;
    private String ideType;
    private Boolean dwAccountUsed;
    private String dwAccount;
    private double cpu;
    private double memory;
}
