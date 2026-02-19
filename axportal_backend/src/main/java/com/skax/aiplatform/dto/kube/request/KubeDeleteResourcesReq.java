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
public class KubeDeleteResourcesReq {
    private String namespace;                 // null 이면 kubeConfig.namespace 사용
    private String deployName;
    private String svcName;
    private String ingName;
    private String vsName;                    // VirtualService name
    private Map<String, String> labels;       // app,user,ide,py,inst
}
