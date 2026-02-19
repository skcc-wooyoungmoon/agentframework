package com.skax.aiplatform.dto.kube.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KubeCreateIngressReq {
    private String ingName;
    private Map<String, String> labels;
    private String svcName;
    private Integer svcPort;
    private String path;
}
