package com.skax.aiplatform.service.kube.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.config.KubeClientConfig;
import com.skax.aiplatform.dto.admin.response.DwAccountRes;
import com.skax.aiplatform.dto.kube.request.*;
import com.skax.aiplatform.dto.kube.response.*;
import com.skax.aiplatform.service.kube.KubeService;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.KubernetesClientTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KubeServiceImpl implements KubeService {

    private final KubernetesClient kubeClient;
    private final KubeClientConfig kubeConfig;
    private final com.skax.aiplatform.config.VerticaProperties verticaProperties;
    private final TokenInfo tokenInfo;

    // Vertica 설정이 있을 때만 주입되는 선택적 의존성
    /* 25.11.27 미사용 서비스
    @Autowired(required = false)
    private DwAccountService dwAccountService;
     */

    @Override
    public KubeCreateDeploymentRes createDeployment(KubeCreateDeploymentReq req) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.createDeployment ]");
        log.info("KubeCreateDeploymentReq            : {}", req.toString());
        log.info("-----------------------------------------------------------------------------------------");
        // Deployment 생성 명명 규칙: deploy-<user>-<ide>-py<312|313>-<uuid8>
        try {
            String ns = requireProjectNamespace(null);
            Map<String, String> labels = req.getLabels();
            String ide = req.getIdeType().toLowerCase();
            String deployName = req.getDeployName();
            Map<String, Integer> portMap = (kubeConfig.getPorts() == null) ? Map.of() : kubeConfig.getPorts();
            String user = labels.get("user");
            // String py = labels.get("py");
            String inst = labels.get("inst");
            // 공통 프리픽스
            String basePath = "/%s/%s/%s".formatted(ide, user, inst);

            int containerPort = portMap.getOrDefault(ide, 0);
            if (containerPort == 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 IDE: " + ide);
            }
            int replicas = (kubeConfig.getReplicas() == null) ? 1 : kubeConfig.getReplicas();

            log.info(
                    "[Kubernetes] deploy params ns={}, deployName={}, labels={}, portMap={}, chosenPort={}, replicas={}",
                    ns, req.getDeployName(), req.getLabels(), portMap, containerPort, replicas);

            Quantity rqCpu = new Quantity(req.getCpuDefault().toString());
            Quantity rqMem = new Quantity(req.getMemDefault() + "Gi");
            Quantity lmCpu = new Quantity((req.getCpuMax() != null ? req.getCpuMax() : req.getCpuDefault()).toString());
            Quantity lmMem = new Quantity((req.getMemMax() != null ? req.getMemMax() : req.getMemDefault()) + "Gi");

            log.info("[Kubernetes] resources requests(cpu={},mem={}), limits(cpu={},mem={})", rqCpu, rqMem, lmCpu,
                    lmMem);

            var rr = new ResourceRequirementsBuilder()
                    .addToRequests(Map.of("cpu", rqCpu, "memory", rqMem))
                    .addToLimits(Map.of("cpu", lmCpu, "memory", lmMem))
                    .build();

            var cb = new ContainerBuilder()
                    .withName(ide)
                    .withImage(req.getImageTag()).withImagePullPolicy("IfNotPresent")
                    .addNewPort().withContainerPort(containerPort).endPort()
                    .withResources(rr);

            // 임시사용 -> dw user 계정으로 변경예정
            String currentUserId = tokenInfo.getUserName();
            String dwAccountId = req.getDwAccountId();

            // memberId가 null이면 username을 사용 (Fallback)
            if (currentUserId == null || currentUserId.isBlank()) {
                currentUserId = tokenInfo.getUserName();
                log.warn("[WARN] memberId is null, using username as fallback: {}", currentUserId);
            }

            // IDE별 런타임 설정
            switch (ide) {
                case "jupyter" -> {
                    // Dockerfile이 JUPYTER_BASE_URL을 읽어 --ServerApp.base_url 로 넘김
                    // test용 userID 추가
                    cb.addNewEnv().withName("JUPYTER_BASE_URL").withValue(basePath).endEnv();

                    log.info("[Kubernetes] Jupyter deployment with JUPYTER_BASE_URL={}", basePath);
                    // 토큰 off 필요 시:
                    // cb.addNewEnv().withName("JUPYTER_TOKEN").withValue("").endEnv();
                }
                case "vscode" -> {
                    // VSCode: nginx entrypoint가 BASE_PATH 환경변수를 읽어 동적 경로 처리
                    // nginx.conf.template에서 ${BASE_PATH}를 치환하여 동적 location 블록 생성
                    // test용 userID 추가
                    cb.addNewEnv().withName("BASE_PATH").withValue(basePath).endEnv();
                    log.info("[Kubernetes] VSCode deployment with BASE_PATH={}", basePath);
                }
                default -> {
                    /* noop */
                }
            }

            cb.addNewEnv().withName("USER_ID").withValue(currentUserId).endEnv();

            if (dwAccountId != null && !dwAccountId.isEmpty()) {
                cb.addNewEnv().withName("DW_ACCOUNT_ID").withValue(dwAccountId).endEnv();
                log.info("[Kubernetes] deployment with DW_ACCOUNT_ID = {}", dwAccountId);
            }

            // Portal URL 가져오기 (2025.12.05 properties에서 관리하도록 변경)
            var envConf = kubeConfig.getEnvConf();
            if (envConf != null) {
                envConf.forEach(m -> cb.addNewEnv()
                        .withName(m.getName())
                        .withValue(m.getValue())
                        .endEnv());
            }
            // Affinity 설정 (nodeAffinity)
            var affinityConf = kubeConfig.getAffinity();
            AffinityBuilder affinityBuilder = null;
            if (affinityConf != null && affinityConf.getNodetype() != null && affinityConf.getNodeValue() != null) {
                affinityBuilder = new AffinityBuilder()
                        .withNewNodeAffinity()
                        .withNewRequiredDuringSchedulingIgnoredDuringExecution()
                        .addNewNodeSelectorTerm()
                        .addNewMatchExpression()
                        .withKey(affinityConf.getNodetype())
                        .withOperator("In")
                        .withValues(affinityConf.getNodeValue())
                        .endMatchExpression()
                        .endNodeSelectorTerm()
                        .endRequiredDuringSchedulingIgnoredDuringExecution()
                        .endNodeAffinity();
            }

            // docker spec 추가 hostAliases
            var hostAliases = Optional.ofNullable(kubeConfig.getHostAliases())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(h -> new HostAliasBuilder()
                            .withIp(h.getIp())
                            .withHostnames(h.getHostnames())
                            .build())
                    .toList();

            // NodeSelector 설정
            Map<String, String> nodeSelector = null;
            var nodeSelectorConf = kubeConfig.getNodeSelector();
            if (nodeSelectorConf != null && nodeSelectorConf.getLabels() != null
                    && !nodeSelectorConf.getLabels().isEmpty()) {
                if (nodeSelectorConf.getLabels().get("role") != null
                        && !nodeSelectorConf.getLabels().get("role").isEmpty()) {
                    nodeSelector = nodeSelectorConf.getLabels();
                    log.info("[Kubernetes] nodeSelector applied: {}", nodeSelector);
                }
            }

            // Tolerations 설정
            List<Toleration> tolerations = null;
            var tolerationsConf = kubeConfig.getTolerations();
            if (tolerationsConf != null && !tolerationsConf.isEmpty()) {
                tolerations = tolerationsConf.stream()
                        .filter(t -> t.getKey() != null && !t.getKey().isEmpty())
                        .map(t -> new TolerationBuilder()
                                .withKey(t.getKey())
                                .withOperator(t.getOperator())
                                .withValue(t.getValue())
                                .withEffect(t.getEffect())
                                .build())
                        .toList();
                log.info("[Kubernetes] tolerations applied: {}", tolerations);
            }

            // pvc 생성 및 mount 설정
            String pvcStorageClass = kubeConfig.getPersistentVolumeClaim().getStorageClass(); //설정값으로 관리 StorageClass
            List<String> pvcAccessMode = Collections.singletonList(kubeConfig.getPersistentVolumeClaim().getAccessMode()); //설정값으로 관리 AccessMode
            String baseMountPath = kubeConfig.getPersistentVolumeClaim().getMountPath(); //설정값으로 관리 Mount경로
            String pvcStorageSize = kubeConfig.getPersistentVolumeClaim().getStorageSize(); //설정값으로 관리 StorageSize

            log.info("pvc configurations pvcStorageClass :{}, pvcAccessMode :{}, pvcStorageSize :{}, baseMountPath : {}", pvcStorageClass, pvcAccessMode, pvcStorageSize, baseMountPath);

            //storageClass 검색 후 없으면 실패 storage class 있어야함 >> 방어로직 제거
            //boolean scExist = kubeClient.storage().v1().storageClasses().list().getItems().stream().anyMatch(sc -> pvcStorageClass.equals(sc.getMetadata().getName()));
            //if(!scExist) {
            //    throw new RuntimeException("StorageClass " + pvcStorageClass + " not found.");
            //}

            List<Volume> volumesToAdd = new ArrayList<>();
            List<Long> prjSeqList = req.getPrjSeq();

            if (prjSeqList != null && !prjSeqList.isEmpty()) {
                // 중복 제거
                List<Long> distinctPrjSeqs = prjSeqList.stream().distinct().toList();

                for (Long prjSeq : distinctPrjSeqs) {
                    String normalizedPrjSeq = prjSeq < 0 ? "n" + Math.abs(prjSeq) : "p" + prjSeq; //프로젝트 번호 normalize
                    String pvcName = "pvc-project-" + normalizedPrjSeq; //pvc name을 프로젝트 따라가도록

                    PersistentVolumeClaim pvc = new PersistentVolumeClaimBuilder()
                            .withNewMetadata()
                            .withNamespace(ns)
                            .withName(pvcName)
                            .endMetadata()
                            .withNewSpec()
                            .withAccessModes(pvcAccessMode)
                            .withNewResources()
                            .addToRequests("storage", new Quantity(pvcStorageSize))
                            .endResources()
                            .withStorageClassName(pvcStorageClass)
                            .endSpec()
                            .build();

                    var pvClient = kubeClient.persistentVolumeClaims().inNamespace(ns);

                    if (pvClient.resource(pvc).get() == null) {
                        pvClient.resource(pvc).create();
                        log.info("Created PVC: {}", pvcName);
                    } else {
                        log.info("PVC {} already exists. Using existing claim.", pvcName);
                    }

                    // containerBuilder 에 pvc Mount
                    // Path: /workdir/123
                    String specificMountPath = String.format("%s/%d", baseMountPath.replaceAll("/$", ""), prjSeq);

                    cb.addNewVolumeMount()
                            .withName(pvcName)
                            .withMountPath(specificMountPath.replaceAll("-", "_")) // -999 일경우 예외처리
                            .endVolumeMount();

                    // Volume 목록에 추가
                    volumesToAdd.add(new VolumeBuilder()
                            .withName(pvcName)
                            .withNewPersistentVolumeClaim()
                            .withClaimName(pvcName)
                            .endPersistentVolumeClaim()
                            .build());
                }
            } else {
                log.warn("Project Sequence list is empty. No PVCs will be mounted.");
            }

            var podSecurityContextConfig = kubeConfig.getSecurityContext();

            PodSecurityContextBuilder podSecurityContextBuilder = new PodSecurityContextBuilder()
                    .withRunAsUser(podSecurityContextConfig.getRunAsUser())
                    .withRunAsGroup(podSecurityContextConfig.getRunAsGroup())
                    .withFsGroup(podSecurityContextConfig.getFsGroup());

            var dep = new DeploymentBuilder()
                    .withNewMetadata().withName(deployName).withNamespace(ns).withLabels(labels).endMetadata()
                    .withNewSpec()
                    .withSelector(new LabelSelectorBuilder().withMatchLabels(labels).build())
                    .withReplicas(replicas)
                    .withNewTemplate()
                    .withNewMetadata().withLabels(labels).endMetadata()
                    .withNewSpec().withHostAliases(hostAliases)
                    .withSecurityContext(podSecurityContextBuilder.build())
                    .withContainers(cb.build())
                    .withAffinity(affinityBuilder != null ? affinityBuilder.build() : null)
                    .withNodeSelector(nodeSelector)
                    .withTolerations(tolerations)
                    // Volume 추가
                    .withVolumes(volumesToAdd)
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();

            Deployment deployment = kubeClient.apps().deployments().inNamespace(ns).resource(dep).serverSideApply();
            Integer desired = (deployment.getSpec() != null) ? deployment.getSpec().getReplicas() : null;
            Integer available = (deployment.getStatus() != null) ? deployment.getStatus().getAvailableReplicas() : null;

            log.debug("[Kubernetes] deploy applied: name={}, desired={}, available={}, uid={}",
                    deployment.getMetadata().getName(), desired, available,
                    deployment.getMetadata() != null ? deployment.getMetadata().getUid() : null);


            return KubeCreateDeploymentRes.builder()
                    .namespace(ns)
                    .deployName(deployment.getMetadata().getName())
                    .labels(deployment.getMetadata().getLabels())
                    .image(req.getImageTag())
                    .desiredReplicas(desired != null ? desired : 1)
                    .availableReplicas(available != null ? available : 0)
                    .ready(available != null && available.equals(desired))
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Kubernetes] createDeployment failed: ide={}, deployName={}, cause={}",
                    req.getIdeType(), req.getDeployName(), e.toString());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Kubernetes Deployment 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public KubeCreateServiceRes createService(KubeCreateServiceReq req) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.createService ]");
        log.info("KubeCreateServiceReq            : {}", req.toString());
        log.info("-----------------------------------------------------------------------------------------");
        // Service 생성 명명 규칙: svc-<user>-<ide>-py<312|313>-<uuid8>
        KubeCreateServiceRes result = null;
        try {
            String ns = requireProjectNamespace(null);
            String svcName = req.getSvcName();
            Map<String, String> labels = req.getLabels();
            String ide = req.getIdeType().toLowerCase();

            Map<String, Integer> portMap = (kubeConfig.getPorts() == null) ? Map.of() : kubeConfig.getPorts();
            int containerPort = portMap.getOrDefault(ide, 0);
            if (containerPort <= 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 IDE: " + ide);
            }

            var sc = kubeConfig.getService();
            if (sc == null || sc.getType() == null || sc.getPortName() == null) {
                throw new IllegalStateException("kube.service.type, kube.service.portName 설정은 필수입니다.");
            }

            // VSCode: nginx가 80번 포트에서 listen, code-server는 8080
            // Jupyter: 직접 8888 포트에서 listen
            int servicePort = containerPort;
            int targetPort = "vscode".equals(ide) ? 80 : containerPort;

            log.info(
                    "[Kubernetes] service params ns={}, labels={}, type={}, portName={}, nodePort={}, servicePort={}, targetPort={}",
                    ns, req.getLabels(), sc.getType(),
                    sc.getPortName(),
                    sc.getNodePort(),
                    servicePort,
                    targetPort);

            var portBuilder = new ServicePortBuilder()
                    .withName(sc.getPortName())
                    .withPort(servicePort)
                    .withNewTargetPort(targetPort);

            if ("NodePort".equalsIgnoreCase(sc.getType())
                    && sc.getNodePort() != null && sc.getNodePort() > 0) {
                portBuilder.withNodePort(sc.getNodePort());
            }

            var svc = new ServiceBuilder()
                    .withNewMetadata().withName(svcName).withNamespace(ns).withLabels(labels).endMetadata()
                    .withNewSpec()
                    .withSelector(labels)
                    .withType(sc.getType())
                    .withPorts(portBuilder.build())
                    .endSpec()
                    .build();

            svc = kubeClient.services().inNamespace(ns).resource(svc).serverSideApply();
            var sp = svc.getSpec().getPorts().get(0);

            log.debug(
                    "[Kubernetes] service applied: name={}, type={}, port={}, targetPort={}, nodePort={}, clusterIP={}",
                    svc.getMetadata().getName(), svc.getSpec().getType(),
                    sp.getPort(), sp.getTargetPort() != null ? sp.getTargetPort().getIntVal() : null,
                    sp.getNodePort(), svc.getSpec().getClusterIP());

            Integer target = (sp.getTargetPort() != null) ? sp.getTargetPort().getIntVal() : null;
            String type = svc.getSpec().getType();

            KubeCreateServiceRes.KubeCreateServiceResBuilder builder = KubeCreateServiceRes.builder()
                    .namespace(ns)
                    .svcName(svc.getMetadata().getName())
                    .labels(svc.getMetadata().getLabels())
                    .port(sp.getPort())
                    .targetPort(target != null ? target : containerPort)
                    .svcType(type);

            if ("NodePort".equalsIgnoreCase(type)) {
                builder.nodePort(sp.getNodePort());
            }
            if ("ClusterIP".equalsIgnoreCase(type)) {
                builder.clusterIP(svc.getSpec().getClusterIP());
            }

            result = builder.build();
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Kubernetes] createService failed: svcName={}, cause={}", req.getSvcName(), e.toString());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Kubernetes Service 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public KubeCreateIngressRes createIngress(KubeCreateIngressReq req) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.createIngress ]");
        log.info("KubeCreateIngressReq            : {}", req.toString());
        log.info("-----------------------------------------------------------------------------------------");
        // Ingress 생성 명명 규칙: ing-<user>-<ide>-py<312|313>-<uuid8>
        try {
            String ns = requireProjectNamespace(null);

            Objects.requireNonNull(req.getIngName(), "ingressName required");
            Objects.requireNonNull(req.getSvcName(), "svcName required");
            Objects.requireNonNull(req.getSvcPort(), "svcPort required");
            Objects.requireNonNull(req.getPath(), "path required");

            var ic = kubeConfig.getIngress();
            var idec = kubeConfig.getIde();
            var ingName = req.getIngName();
            String scheme = (idec != null && idec.getUrlScheme() != null) ? idec.getUrlScheme() : "http";
            int expireDays = (idec != null && idec.getExpireDays() != null) ? idec.getExpireDays() : 7;
            String pathType = (ic != null && ic.getPathType() != null) ? ic.getPathType() : "Prefix";
            String cls = (ic != null) ? ic.getClassName() : null;
            Map<String, String> labels = req.getLabels();
            String ide = labels != null ? labels.get("ide") : null;
            String usePath = req.getPath();
            String hc = null;

            log.info(
                    "[Kubernetes] ingress params ns={}, className={}, pathType={}, scheme={}, expireDays={}, annos={}, labels={}",
                    ns, cls, pathType, scheme, expireDays, ic != null ? ic.getAnnotations() : null, labels);

            // Ingress 비활성 모드: 생성 스킵, 메타만 반환
            if (ic != null && Boolean.FALSE.equals(ic.getEnabled())) {
                LocalDateTime now = LocalDateTime.now();
                return KubeCreateIngressRes.builder()
                        .namespace(ns)
                        .ingName(req.getIngName())
                        .labels(req.getLabels())
                        .host(null)
                        .path(usePath)
                        .svcName(req.getSvcName())
                        .svcPort(req.getSvcPort())
                        .scheme(idec != null && idec.getUrlScheme() != null ? idec.getUrlScheme() : "http")
                        .createdAt(now).updatedAt(now)
                        .expireAt(now.plusDays(idec != null && idec.getExpireDays() != null ? idec.getExpireDays() : 7))
                        .build();
            }

            // IDE별 healthcheck 및 path 설정
            String ingressPath = usePath;
            if ("vscode".equals(ide)) {
                // VSCode: 전체 경로로 healthcheck
                hc = req.getPath();
                // VSCode: /* suffix 제거 - ALB가 단일 로드밸런서로 통합 관리
                ingressPath = usePath;
            } else {
                // Jupyter: 전체 경로 + /lab
                hc = req.getPath() + "/lab";
            }

            // Ingress Backend 및 Rule 설정
            var backend = new IngressBackendBuilder()
                    .withNewService()
                    .withName(req.getSvcName())
                    .withNewPort().withNumber(req.getSvcPort()).endPort()
                    .endService()
                    .build();

            var rule = new IngressRuleBuilder()
                    .withHost(null) // hostless → ALB DNS를 status에서 가져옴
                    .withNewHttp()
                    .addNewPath()
                    .withPath(ingressPath)
                    .withPathType(pathType)
                    .withBackend(backend)
                    .endPath()
                    .endHttp()
                    .build();

            Map<String, String> annos = new HashMap<>();
            if (ic != null && ic.getAnnotations() != null)
                annos.putAll(ic.getAnnotations());
            annos.put("alb.ingress.kubernetes.io/healthcheck-path", hc);
            annos.put("alb.ingress.kubernetes.io/success-codes", "200-399");

            var meta = new ObjectMetaBuilder()
                    .withName(req.getIngName())
                    .withNamespace(ns)
                    .withLabels(labels)
                    .addToAnnotations(annos)
                    .build();

            var specBuilder = new IngressSpecBuilder()
                    .withRules(rule);

            if (cls != null && !cls.isBlank()) {
                specBuilder.withIngressClassName(cls);
            }

            var ing = new IngressBuilder()
                    .withMetadata(meta)
                    .withSpec(specBuilder.build())
                    .build();
            ing = kubeClient.network().v1().ingresses().inNamespace(ns).resource(ing).serverSideApply();

            // status에서 ALB DNS(hostname) 가져오기
            // wait ALB hostname
            String hostFromStatus = null;
            int failCount = 0;
            for (int i = 0; i < 5; i++) { // 최대 5번 (약 10초 대기)
                try {
                    var got = kubeClient.network().v1().ingresses()
                            .inNamespace(ns)
                            .withName(ingName)
                            .get();
                    hostFromStatus = Optional.ofNullable(got)
                            .map(Ingress::getStatus)
                            .map(IngressStatus::getLoadBalancer)
                            .map(IngressLoadBalancerStatus::getIngress)
                            .filter(list -> !list.isEmpty())
                            .map(list -> list.get(0).getHostname())
                            .orElse(null);
                } catch (KubernetesClientException e) {
                    failCount++;
                    if (failCount == 4 && hostFromStatus == null) {
                        log.warn("[Kubernetes] Failed to retrieve Ingress hostname after {} attempts", failCount);
                    }
                    log.info("[Kubernetes] ALB hostname not ready for ingress={}, ns={}, retry={}/5", ingName, ns,
                            failCount);
                } catch (RuntimeException e) {
                    // unexpected runtime error
                    failCount++;
                    log.error("[Kubernetes] unexpected runtime error: {}", e.getMessage(), e);
                }
                if (hostFromStatus != null && !hostFromStatus.isBlank())
                    break;
                Thread.sleep(2000); // 2초 대기 후 재시도
            }

            if (hostFromStatus == null) {
                log.warn("[Kubernetes] ALB hostname not ready for ingress={}, ns={}", ingName, ns);
            }

            LocalDateTime now = LocalDateTime.now();

            return KubeCreateIngressRes.builder()
                    .namespace(ns)
                    .ingName(req.getIngName())
                    .labels(ing.getMetadata().getLabels())
                    .host(hostFromStatus)
                    .path(usePath)
                    .svcName(req.getSvcName())
                    .svcPort(req.getSvcPort())
                    .scheme(scheme)
                    .createdAt(now)
                    .updatedAt(now)
                    .expireAt(now.plusDays(expireDays))
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Kubernetes] createIngress failed: ingName={}, cause={}", req.getIngName(), e.toString());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Kubernetes Ingress 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public KubeCreateVirtualServiceRes createVirtualService(KubeCreateVirtualServiceReq req) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.createVirtualService ]");
        log.info("KubeCreateVirtualServiceReq: {}", req.toString());
        log.info("-----------------------------------------------------------------------------------------");

        try {
            String ns = requireProjectNamespace(null);

            // 필수 파라미터 검증
            Objects.requireNonNull(req.getVsName(), "vsName required");
            Objects.requireNonNull(req.getGateway(), "gateway required");
            Objects.requireNonNull(req.getHost(), "host required");
            Objects.requireNonNull(req.getPathPrefix(), "pathPrefix required");
            Objects.requireNonNull(req.getSvcName(), "svcName required");
            Objects.requireNonNull(req.getSvcPort(), "svcPort required");

            // Service FQDN 생성
            String svcFqdn = "%s.%s.svc.cluster.local".formatted(req.getSvcName(), ns);

            // VirtualService 스펙 구성
            Map<String, Object> vsSpec = new HashMap<>();
            vsSpec.put("gateways", java.util.List.of(req.getGateway()));
            vsSpec.put("hosts", java.util.List.of(req.getHost()));

            // IDE 타입 확인 (labels에서 "ide" 값 추출)
            String ideType = req.getLabels() != null ? req.getLabels().get("ide") : null;

            Map<String, Object> httpRule = new HashMap<>();
            httpRule.put("match", java.util.List.of(Map.of("uri", Map.of("prefix", req.getPathPrefix()))));

            // VSCode는 rewrite 필요 (code-server는 하위 경로를 지원하지 않음)
            // if ("vscode".equals(ideType)) {
            // // URI rewrite: /vscode/user/py312/xxx/* -> /
            // //httpRule.put("rewrite", Map.of("uri", "/"));
            // }

            httpRule.put("route", java.util.List.of(
                    Map.of("destination", Map.of(
                            "host", svcFqdn,
                            "port", Map.of("number", req.getSvcPort())))));
            vsSpec.put("http", java.util.List.of(httpRule));

            // GenericKubernetesResource 생성
            Map<String, Object> vsResource = new HashMap<>();
            vsResource.put("apiVersion", "networking.istio.io/v1");
            vsResource.put("kind", "VirtualService");

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("name", req.getVsName());
            metadata.put("namespace", ns);
            if (req.getLabels() != null && !req.getLabels().isEmpty()) {
                metadata.put("labels", req.getLabels());
            }
            vsResource.put("metadata", metadata);
            vsResource.put("spec", vsSpec);

            // Apply VirtualService
            ObjectMapper mapper = new ObjectMapper();
            String vsJson = mapper.writeValueAsString(vsResource);
            kubeClient.resource(vsJson).inNamespace(ns).serverSideApply();

            log.info("[Kubernetes] VirtualService created: name={}, gateway={}, host={}, prefix={}, ide={}, rewrite={}",
                    req.getVsName(), req.getGateway(), req.getHost(), req.getPathPrefix(), ideType,
                    "vscode".equals(ideType));

            return KubeCreateVirtualServiceRes.builder()
                    .vsName(req.getVsName())
                    .gateway(req.getGateway())
                    .host(req.getHost())
                    .pathPrefix(req.getPathPrefix())
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Kubernetes] createVirtualService failed: vsName={}, cause={}", req.getVsName(), e.toString());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Kubernetes VirtualService 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public KubeGetDeploymentRes getDeployment(KubeGetDeploymentReq req) {
        try {
            var ns = req.getNamespace();
            var dep = kubeClient.apps().deployments().inNamespace(ns).withName(req.getDeployName()).get();
            if (dep == null && req.getLabels() != null) {
                dep = kubeClient.apps().deployments().inNamespace(ns).withLabels(req.getLabels())
                        .list().getItems().stream().findFirst().orElse(null);
            }
            if (dep == null) {
                return KubeGetDeploymentRes.builder().name(null).build();
            }

            var podSpec = dep.getSpec().getTemplate().getSpec();
            var c = (podSpec.getContainers() != null && !podSpec.getContainers().isEmpty())
                    ? podSpec.getContainers().get(0)
                    : null;
            String image = c != null ? c.getImage() : null;

            String rqCpu = null, rqMem = null, lmCpu = null, lmMem = null;
            if (c != null && c.getResources() != null) {
                var rq = c.getResources().getRequests();
                var lm = c.getResources().getLimits();
                if (rq != null) {
                    if (rq.get("cpu") != null)
                        rqCpu = rq.get("cpu").getAmount();
                    if (rq.get("memory") != null)
                        rqMem = rq.get("memory").getAmount();
                }
                if (lm != null) {
                    if (lm.get("cpu") != null)
                        lmCpu = lm.get("cpu").getAmount();
                    if (lm.get("memory") != null)
                        lmMem = lm.get("memory").getAmount();
                }
            }

            boolean podReady = false;
            if (req.getLabels() != null) {
                podReady = kubeClient.pods().inNamespace(ns).withLabels(req.getLabels()).list().getItems().stream()
                        .anyMatch(p -> "Running".equalsIgnoreCase(p.getStatus().getPhase()) &&
                                p.getStatus().getConditions() != null &&
                                p.getStatus().getConditions().stream()
                                        .anyMatch(cd -> "Ready".equals(cd.getType())
                                                && "True".equalsIgnoreCase(cd.getStatus())));
            }

            Integer desired = dep.getSpec() != null ? dep.getSpec().getReplicas() : null;
            Integer available = dep.getStatus() != null ? dep.getStatus().getAvailableReplicas() : null;
            Integer updated = dep.getStatus() != null ? dep.getStatus().getUpdatedReplicas() : null;

            return KubeGetDeploymentRes.builder()
                    .name(dep.getMetadata().getName())
                    .labels(dep.getMetadata().getLabels())
                    .image(image)
                    .desiredReplicas(desired)
                    .availableReplicas(available)
                    .updatedReplicas(updated)
                    .anyPodReady(podReady)
                    .requestsCpu(rqCpu).requestsMem(rqMem)
                    .limitsCpu(lmCpu).limitsMem(lmMem)
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Kubernetes deployment 상태 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public void deleteResources(KubeDeleteResourcesReq req) {
        String ns = requireProjectNamespace(req.getNamespace());
        Map<String, String> labels = safeLabels(req.getLabels());

        var ings = kubeClient.network().v1().ingresses().inNamespace(ns).withLabels(labels).list().getItems();
        var svcs = kubeClient.services().inNamespace(ns).withLabels(labels).list().getItems();
        var deps = kubeClient.apps().deployments().inNamespace(ns).withLabels(labels).list().getItems();

        // VirtualService 삭제 (Istio CRD)
        try {
            kubeClient.genericKubernetesResources("networking.istio.io/v1", "VirtualService")
                    .inNamespace(ns)
                    .withLabels(labels)
                    .delete();
            log.info("VirtualService deleted by labels: ns={}, labels={}", ns, labels);
        } catch (KubernetesClientTimeoutException e) {
            log.warn("VirtualService deletion timed out: ns={}, labels={}, cause={}", ns, labels, e.getMessage());

        } catch (KubernetesClientException e) {
            log.error("Kubernetes client error during VirtualService deletion: ns={}, labels={}, cause={}",
                    ns, labels, e.getMessage());
        } catch (Exception e) {
            log.warn("VirtualService deletion by labels failed: ns={}, labels={}, cause={}", ns, labels,
                    e.getMessage());
        }

        // Ingress 삭제
        if (!ings.isEmpty())
            kubeClient.network().v1().ingresses().inNamespace(ns).withLabels(labels).delete();

        // Deployment 삭제 (FOREGROUND propagation으로 자동으로 Pod도 삭제됨)
        if (!deps.isEmpty())
            kubeClient.apps().deployments().inNamespace(ns).withLabels(labels)
                    .withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

        // Service 삭제
        if (!svcs.isEmpty())
            kubeClient.services().inNamespace(ns).withLabels(labels).delete();

        // 이름 기반 fallback (규칙형)
        if (req.getVsName() != null) {
            try {
                kubeClient.genericKubernetesResources("networking.istio.io/v1", "VirtualService")
                        .inNamespace(ns)
                        .withName(req.getVsName())
                        .delete();
                log.info("VirtualService deleted by name: ns={}, vsName={}", ns, req.getVsName());
            } catch (KubernetesClientException e) {
                if (e.getCode() == 404) {
                    log.warn("VirtualService deletion by name failed: ns={}, vsName={}", ns, req.getVsName(), e);
                } else {
                    throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "리소스 삭제 실패");
                }
            } catch (Exception e) {
                log.error("Unexpected error during VirtualService deletion: ns={}, vsName={}", ns, req.getVsName(), e);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "리소스 삭제 실패", e);
            }
        }
        if (req.getIngName() != null)
            kubeClient.network().v1().ingresses().inNamespace(ns).withName(req.getIngName()).delete();
        if (req.getDeployName() != null)
            kubeClient.apps().deployments().inNamespace(ns).withName(req.getDeployName())
                    .withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();
        if (req.getSvcName() != null)
            kubeClient.services().inNamespace(ns).withName(req.getSvcName()).delete();

        log.info("K8S delete attempted ns={} labels={} nameFallback=[vs={},ing={},svc={},dep={}]",
                ns, labels, req.getVsName(), req.getIngName(), req.getSvcName(), req.getDeployName());
    }

    @Override
    public void deleteIngress(KubeDeleteIngressReq req) {
        String ns = requireProjectNamespace(req.getNamespace());
        Map<String, String> labels = safeLabels(req.getLabels());

        try {
            kubeClient.network().v1().ingresses()
                    .inNamespace(ns)
                    .withLabels(labels)
                    .delete();
            log.info("Ingress(label) deleted ns={} labels={}", ns, labels);
        } catch (KubernetesClientTimeoutException e) {
            log.warn("Ingress deletion timed out: ns={}, labels={}, cause={}", ns, labels, e.getMessage());
        } catch (KubernetesClientException e) {
            log.error("K8s client error during ingress deletion: ns={}, labels={}, cause={}", ns, labels, e.getMessage());
        } catch (Exception e) {
            log.debug("Ingress(label) deletion failed: ns={}, labels={}, cause={}", ns, labels, e.getMessage());
        }
    }

    @Override
    public DwGetAccountCredentialsRes dwGetAccountCredentials(DwGetAccountCredentialsReq req) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.getUserCredentials ]");
        log.info("KubeGetUserCredentialsReq          : {}", req.toString());
        log.info("-----------------------------------------------------------------------------------------");

        String empNo = (req.getEmpNo() == null || req.getEmpNo().isEmpty())
                ? "default"
                : req.getEmpNo();
        String accountId = req.getAccountId();
        Map<String, String> accountInfoMap;
        String accountRole = "";
        String accountPw = "";
        String sessionLabel = "";
        String dwSecretFile = verticaProperties.getDwSecretJsonPath();

        if (accountId == null) {
            throw new IllegalArgumentException("조회 조건이 없습니다");
        }

        log.info("[DW-SECRET] Secret 파일 Path props : {}", dwSecretFile);
        Path path = Path.of(dwSecretFile);

        // 파일 존재 여부 로그
        if (Files.exists(path)) {
            log.info("[DW-SECRET] Secret 파일 존재: {}", dwSecretFile);
            try {
                String json = Files.readString(path);
                ObjectMapper mapper = new ObjectMapper();
                // JSON -> Map<String, Map<String,String>>
                Map<String, Map<String, String>> secretMap = mapper.readValue(json, new TypeReference<Map<String, Map<String, String>>>() {
                });
                accountInfoMap = secretMap.get(accountId);
                if (accountInfoMap == null || accountInfoMap.isEmpty()) {
                    log.error("[DW-SECRET] Secret JSON key '{}' 없음", accountId);
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다");
                } else {
                    accountRole = accountInfoMap.get("role");
                    accountPw = accountInfoMap.get("pw");
                    log.info("[DW-SECRET] Secret JSON accountRole : {}, accountPw : {}", accountRole, accountPw.charAt(0) + "*".repeat(accountPw.length() - 1));
                }
                sessionLabel = "GAF_" + empNo;
            } catch (IOException e) {
                // 파일 읽기 문제
                log.error("[DW-SECRET] Secret 파일 읽기 오류: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 읽기 오류");

            } catch (RuntimeException e) {
                // NPE 등 치명적인 로직 오류
                log.error("[DW-SECRET] Secret 처리 중 런타임 오류: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "런타임 오류 발생");

            } catch (Exception e) {
                log.error("[DW-SECRET] Secret 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "알수없는 오류 발생");
            }

        } else {
            log.error("[DW-SECRET] Secret 파일 없음: {}", dwSecretFile);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, "파일을 찾을 수 없습니다");
        }


        // 3-6. Vertica 설정: application.yml에서 읽음
        String host = verticaProperties.getHost();
        String port = verticaProperties.getPort();
        String database = verticaProperties.getDatabase();

        log.info("[DW Credentials] accountId={}, host={}, port={}, database={}, sessionLabel={}, accountRole={}",
                accountId, host, port, database, sessionLabel, accountRole);

        return DwGetAccountCredentialsRes.builder()
                .accountId(accountId)
                .password(accountPw)
                .host(host)
                .port(port)
                .database(database)
                .sessionLabel(sessionLabel)
                .accountRole(accountRole)
                .build();
    }

    @Override
    public List<String> getDwAllAccounts() {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.getDwAllAccounts ]");
        log.info("-----------------------------------------------------------------------------------------");

        String dwSecretFile = verticaProperties.getDwSecretJsonPath();
        log.info("[DW-SECRET] Secret 파일 Path props : {}", dwSecretFile);
        Path path = Path.of(dwSecretFile);

        if (!Files.exists(path)) {
            log.error("[DW-SECRET] Secret 파일 없음: {}", dwSecretFile);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, "파일을 찾을 수 없습니다");
        }

        try {
            String json = Files.readString(path);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> secretMap = mapper.readValue(json, new TypeReference<Map<String, Map<String, String>>>() {
            });

            List<String> analysisAccounts = secretMap.entrySet().stream()
                    .filter(entry -> "analysis".equalsIgnoreCase(entry.getValue().get("role")))
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.toList());

            log.info("[DW-SECRET] analysis role 계정 수: {}", analysisAccounts.size());
            return analysisAccounts;

        } catch (IOException e) {
            log.error("[DW-SECRET] Secret 파일 읽기 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 읽기 오류");
        } catch (Exception e) {
            log.error("[DW-SECRET] Secret 처리 중 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "DW 계정 목록 조회 중 오류 발생");
        }
    }

    @Override
    public List<DwAccountRes> getDwAllAccountsForAdmin() {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service KubeServiceImpl.getDwAllAccountsForAdmin ]");
        log.info("-----------------------------------------------------------------------------------------");

        String dwSecretFile = verticaProperties.getDwSecretJsonPath();
        log.info("[DW-SECRET] Secret 파일 Path props : {}", dwSecretFile);
        Path path = Path.of(dwSecretFile);

        if (!Files.exists(path)) {
            log.error("[DW-SECRET] Secret 파일 없음: {}", dwSecretFile);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, "파일을 찾을 수 없습니다");
        }

        try {
            String json = Files.readString(path);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> secretMap = mapper.readValue(json, new TypeReference<Map<String, Map<String, String>>>() {
            });

            List<DwAccountRes> accounts = secretMap.entrySet().stream()
                    .map(entry -> DwAccountRes.builder()
                            .accountId(entry.getKey())
                            .role(entry.getValue().get("role"))
                            .build())
                    .sorted(Comparator.comparing(DwAccountRes::getAccountId))
                    .collect(Collectors.toList());

            log.info("[DW-SECRET] Admin용 전체 계정 수: {}", accounts.size());
            return accounts;

        } catch (IOException e) {
            log.error("[DW-SECRET] Secret 파일 읽기 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 읽기 오류");
        } catch (Exception e) {
            log.error("[DW-SECRET] Secret 처리 중 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "DW 계정 목록 조회 중 오류 발생");
        }
    }

    /*
     * helper 함수 모음
     */

    private String requireProjectNamespace(String candidate) {
        String allowed = kubeConfig.getNamespace();
        String use = (candidate == null || candidate.isBlank()) ? allowed : candidate;

        // 정책: 오직 allowed 네임스페이스만 허용
        if (!allowed.equals(use)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "namespace not allowed: " + use);
        }
        // 실존 확인
        if (allowed == null || allowed.isBlank() ||
                kubeClient.namespaces().withName(allowed).get() == null) {
            throw new IllegalStateException("네임스페이스 접근 불가: " + allowed);
        }
        return allowed;
    }

    private Map<String, String> safeLabels(Map<String, String> labels) {
        if (labels == null || labels.isEmpty())
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "labels required");
        if (!"webide".equals(labels.getOrDefault("app", "")))
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "label app=webide required");
        for (String k : new String[]{"user", "ide"}) {
            if (labels.get(k) == null || labels.get(k).isBlank())
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "label required: " + k);
        }
        return labels;
    }

    // pod Status 확인 메서드
    // Deploy 후 상태 확인용
    private String getPodStatus(Map<String, String> podLabels) {
        /*
         * readiness 적용보다 우선적으로 Pod 상태 확인 필요
         * Pod 접속 확인 단계
         * 1. Node가 배포되어 있어야 함
         * 2. Pod가 생성되어 있어야 함
         * 3. Pod 상태 확인 (Pending, Running, Succeeded, Failed, Unknown)
         * 4. READY 여부 확인 (컨테이너 준비 완료 여부)
         * pod RUNNING 상태 이후 필요 시 readiness 로직 추가
         */

        String podStatus = null;

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            PodList podList = client.pods()
                    .inNamespace(kubeConfig.getNamespace())
                    .withLabels(podLabels)
                    .list();

            if (!podList.getItems().isEmpty()) {
                Pod pod = podList.getItems().get(0);
                podStatus = pod.getStatus().getPhase();
                log.debug("[Kubernetes] Pod Phase : {}", podStatus);

                // Pod 상태별 처리
                switch (podStatus) {
                    case "Pending":
                        log.info("Pod 상태: Pending - Node 배정 대기 또는 자원 부족");
                        break;
                    case "Running":
                        log.info("Pod 상태: Running - 컨테이너 실행 중");
                        break;
                    case "Succeeded":
                        log.info("Pod 상태: Succeeded - 모든 컨테이너 정상 종료 (Job/Batch)");
                        break;
                    case "Failed":
                        log.info("Pod 상태: Failed - 모든 컨테이너 실패");
                        break;
                    case "Unknown":
                        log.info("Pod 상태: Unknown - 상태 확인 불가");
                        break;
                    default:
                        log.info("Pod 상태: {} - 추가 확인 필요", podStatus);
                        break;
                }
            } else {
                log.warn("조회된 Pod가 없습니다.");
            }
        }
        return podStatus;
    }
}
