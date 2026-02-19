package com.skax.aiplatform.dto.kube.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KubeCreateServiceRes {
    private String namespace;
    private String svcName;
    private Map<String,String> labels;
    private Integer port;        // service port
    private Integer targetPort;  // container port
    private Integer nodePort;
    private String clusterIP;
    private String svcType;
    private String nodeExternalIP;
}
