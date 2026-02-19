package com.skax.aiplatform.dto.kube.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KubeCreateVirtualServiceRes {
    private String vsName;
    private String gateway;
    private String host;
    private String pathPrefix;
}
