package com.skax.aiplatform.dto.home.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "IDE 단건 아이템")
public class IdeItemRes {
    private String ideId;
    private String userId;
    private String username;
    private String ide;
    private String status;
    private long prjSeq;
    private BigDecimal cpu;
    private BigDecimal memory;
    private String image;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expireAt;
    private String ingressUrl;
    private String pythonVer;
}
