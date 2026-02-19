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
public class KubeCreateDeploymentRes {
    private String namespace;
    private String deployName;
    private Map<String,String> labels;
    private String image;
    private Integer desiredReplicas;
    private Integer availableReplicas;
    private boolean ready;
}
