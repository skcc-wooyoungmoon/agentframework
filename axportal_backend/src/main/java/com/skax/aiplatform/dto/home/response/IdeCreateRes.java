package com.skax.aiplatform.dto.home.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdeCreateRes {
    private String ideId;
    private String userId;
    private List<Long> prjSeq;
    private String ide;          // vscode|jupyter
    private String ingressUrl;   // 접속 URL
    private Instant expireAt;    // 만료일시
    private double cpu;          // 기본 CPU
    private double memory;       // 기본 MEM
    private String image;        // 사용 이미지 태그
}
