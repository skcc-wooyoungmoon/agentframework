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
public class KubeGetDeploymentReq {
    private String namespace;
    private String deployName;
    private Map<String, String> labels;
}
