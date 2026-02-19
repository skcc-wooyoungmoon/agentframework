package com.skax.aiplatform.dto.kube.request;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KubeCreateDeploymentReq {
    private List<Long> prjSeq;
    private String imageTag;
    private String ideType;
    private Double cpuDefault;
    private Double memDefault;
    private Double cpuMax;
    private Double memMax;
    private String path;
    private String dwAccountId;

    // 추가: 상위 레이어 계산값
    private String deployName;
    private Map<String, String> labels;

    @ToString.Exclude
    private String dwSecretName; // K8S Secret

    @ToString.Exclude
    private String dwSecretMountPath;

    @ToString.Exclude
    private String pvcName;

    @ToString.Exclude
    private String pvcMountPath;
}
