package com.skax.aiplatform.dto.kube.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KubeCreateIngressRes {
    private String namespace;
    private String ingName;
    private Map<String,String> labels;
    private String host;    // 최종 host
    private String path;    // 최종 path
    private String svcName;
    private Integer svcPort;
    private String scheme;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expireAt;
}
