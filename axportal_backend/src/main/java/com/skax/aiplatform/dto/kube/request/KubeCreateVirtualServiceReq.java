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
public class KubeCreateVirtualServiceReq {
    private String vsName;
    private String gateway;
    private String host;
    private String pathPrefix;
    private String svcName;
    private Integer svcPort;
    private Map<String, String> labels;
}
