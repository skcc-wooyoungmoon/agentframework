package com.skax.aiplatform.service.kube;

import com.skax.aiplatform.dto.kube.request.*;
import com.skax.aiplatform.dto.kube.response.*;
import com.skax.aiplatform.dto.admin.response.DwAccountRes;


import java.util.List;

public interface KubeService {
    KubeCreateDeploymentRes createDeployment(KubeCreateDeploymentReq kubeCreateDeploymentReq);
    KubeCreateServiceRes createService(KubeCreateServiceReq kubeCreateServiceReq);
    KubeCreateIngressRes createIngress(KubeCreateIngressReq kubeCreateIngressReq);
    KubeCreateVirtualServiceRes createVirtualService(KubeCreateVirtualServiceReq req);
    KubeGetDeploymentRes getDeployment(KubeGetDeploymentReq req);
    void deleteResources(KubeDeleteResourcesReq req);
    void deleteIngress(KubeDeleteIngressReq req);
    DwGetAccountCredentialsRes dwGetAccountCredentials(DwGetAccountCredentialsReq req);
    List<String> getDwAllAccounts();
    List<DwAccountRes> getDwAllAccountsForAdmin();
}
