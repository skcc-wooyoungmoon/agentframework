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
public class KubeGetDeploymentRes {
    private String name;
    private Map<String,String> labels;
    private String image;
    private Integer desiredReplicas;     // spec.replicas
    private Integer availableReplicas;   // status.availableReplicas
    private Integer updatedReplicas;     // status.updatedReplicas  ← 추가
    private boolean anyPodReady;         // 라벨 매칭 Pod Ready 존재 여부
    private String requestsCpu;
    private String requestsMem;
    private String limitsCpu;
    private String limitsMem;
}
