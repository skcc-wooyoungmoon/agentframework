package com.skax.aiplatform.service.home.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.config.KubeClientConfig;
import com.skax.aiplatform.dto.home.request.IdeCreateReq;
import com.skax.aiplatform.dto.home.request.IdeDeleteReq;
import com.skax.aiplatform.dto.home.request.IdeExtendReq;
import com.skax.aiplatform.dto.home.request.SearchIdeStatusReq;
import com.skax.aiplatform.dto.home.response.IdeCreateRes;
import com.skax.aiplatform.dto.home.response.IdeImage;
import com.skax.aiplatform.dto.home.response.IdeImageRes;
import com.skax.aiplatform.dto.home.response.IdeStatusRes;
import com.skax.aiplatform.dto.kube.request.KubeCreateDeploymentReq;
import com.skax.aiplatform.dto.kube.request.KubeCreateIngressReq;
import com.skax.aiplatform.dto.kube.request.KubeCreateServiceReq;
import com.skax.aiplatform.dto.kube.request.KubeCreateVirtualServiceReq;
import com.skax.aiplatform.dto.kube.request.KubeDeleteIngressReq;
import com.skax.aiplatform.dto.kube.request.KubeDeleteResourcesReq;
import com.skax.aiplatform.dto.kube.response.KubeCreateDeploymentRes;
import com.skax.aiplatform.dto.kube.response.KubeCreateIngressRes;
import com.skax.aiplatform.dto.kube.response.KubeCreateServiceRes;
import com.skax.aiplatform.dto.kube.response.KubeCreateVirtualServiceRes;
import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;
import com.skax.aiplatform.entity.ide.GpoIdeImageMas;
import com.skax.aiplatform.entity.ide.GpoIdeResourceMas;
import com.skax.aiplatform.entity.ide.GpoIdeStatusMas;
import com.skax.aiplatform.entity.ide.ImageType;
import com.skax.aiplatform.entity.user.User;
import com.skax.aiplatform.repository.home.GpoIdeImageMasRepository;
import com.skax.aiplatform.repository.home.GpoIdeResourceMasRepository;
import com.skax.aiplatform.repository.home.GpoIdeStatusMasRepository;
import com.skax.aiplatform.repository.home.GpoUsersRepository;
import com.skax.aiplatform.service.home.IDEService;
import com.skax.aiplatform.service.kube.KubeService;
import com.skax.aiplatform.service.vertica.DwAccountService;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("IDEService")
public class IDEServiceImpl implements IDEService {

    private final GpoIdeImageMasRepository gpoIdeImageMasRepository;
    private final GpoIdeStatusMasRepository gpoIdeStatusMasRepository;
    private final GpoIdeResourceMasRepository gpoIdeResourceMasRepository;
    private final GpoUsersRepository gpoUsersRepository;
    private final KubeService kubeService;
    private final KubeClientConfig kubeConfig;

    @Autowired(required = false)
    private DwAccountService dwAccountService;

    public IDEServiceImpl(GpoIdeImageMasRepository gpoIdeImageMasRepository,
            GpoIdeStatusMasRepository gpoIdeStatusMasRepository,
            GpoIdeResourceMasRepository gpoIdeResourceMasRepository, GpoUsersRepository gpoUsersRepository,
            KubeService kubeService,
            KubeClientConfig kubeConfig) {
        this.gpoIdeImageMasRepository = gpoIdeImageMasRepository;
        this.gpoIdeStatusMasRepository = gpoIdeStatusMasRepository;
        this.gpoIdeResourceMasRepository = gpoIdeResourceMasRepository;
        this.gpoUsersRepository = gpoUsersRepository;
        this.kubeService = kubeService;
        this.kubeConfig = kubeConfig;
    }

    @Override
    public IdeImageRes getIdeImage() {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.getIdeImage ]");
        log.info("-----------------------------------------------------------------------------------------");

        List<GpoIdeImageMas> images = gpoIdeImageMasRepository.findAll();

        return IdeImageRes.builder()
                .images(images.stream().map(this::toIdeImage).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<DwAccountListRes> getDWAccount(String user_id) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.getDWAccount ]");
        log.info("user_id            : {}", user_id);
        log.info("-----------------------------------------------------------------------------------------");

        if (dwAccountService != null) {
            return dwAccountService.getDwAccountList(user_id);
        } else {
            log.warn("[WARN] DwAccountService is not available (profile: dev/local only)");
            return List.of(); // Return empty list when service is not available
        }
    }

    @Override
    public boolean isIdeCreateAvailable(String ideType, String userId) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.isIdeCreateAvailable ]");
        log.info("ideType            : {}", ideType);
        log.info("userId             : {}", userId);
        log.info("-----------------------------------------------------------------------------------------");

        ImageType type;
        try {
            type = ImageType.valueOf(ideType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 IDE 타입입니다: " + ideType);
        }

        GpoIdeResourceMas resource = gpoIdeResourceMasRepository.findById(type)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "IDE 리소스 정책을 찾을 수 없습니다."));

        long currentCount = gpoIdeStatusMasRepository.countByMemberIdAndImgG(userId, type);

        log.info("IDE Availability Check: Type={}, User={}, Limit={}, Current={}", type, userId,
                resource.getLimitCnt(), currentCount);

        return currentCount < resource.getLimitCnt();
    }

    @Override
    @Transactional
    public IdeCreateRes createIde(IdeCreateReq request) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.createIde ]");
        log.info("request            : {}", request.toString());
        log.info("-----------------------------------------------------------------------------------------");
        /*
         * Label 규칙: app=webide, user=<user>, ide=<vscode|jupyter>, py=<312|313>,
         * inst=<uuid8>
         * - user: 사용자 식별자
         * - ide: ide 식별자 (예시: VSCode 혹은 Jupyter)
         * - py: Python 버전 식별자 (예시: 3.12 혹은 3.13)
         * - inst는 특정 사용자가 동일 webide, python version으로 여러 개의 IDE 환경을 구성하여 사용하고 싶을 때,
         * 구분하기 위한 구별자
         *
         * Deployment: deploy-<ide>-<user>-py<312|313>-<uuid8>
         * Service: svc-<ide>-<user>-py<312|313>-<uuid8>
         * Ingress: ing-<ide>-<user>-py<312|313>-<uuid8>
         *
         * URL Path: <dns or ip>:<port>/<ide>/<user>/py<312|313>/<uuid8>
         * Label Example:
         * app=webide
         * user=shinhan_demo
         * ide=vscode
         * py=312
         * inst=9f8a6c12
         */
        try {
            // (임시 주석 / 25.11.05 박종태)
            /*
             * // 필수 파라미터 선검증
             * requireNonBlank(String.valueOf(request.getPrjSeq()), "prjSeq");
             * requireNonBlank(request.getUserId(), "userId");
             * requireNonBlank(request.getIdeType(), "ideType");
             * requireNonBlank(request.getPythonVer(), "pythonVer");
             *
             * // 유저가 소속된 프로젝트에 할당된 CPU/Memory 확인
             * List<UserIdeResourcePolicy> policies =
             * userIdeResourcePolicyRepository.findEffectiveOne(
             * request.getPrjSeq(), request.getUserId(), request.getIdeType());
             *
             * if (policies.isEmpty()) {
             * throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "리소스 정책이 없습니다.");
             * }
             *
             * UserIdeResourcePolicy policy = policies.get(0);
             * double cpuDefault = policy.getCpuUseDfltV().doubleValue();
             * double memDefault = policy.getMemUseDfltV().doubleValue();
             * Double cpuMax = policy.getCpuUseMaxV() == null ? null :
             * policy.getCpuUseMaxV().doubleValue();
             * Double memMax = policy.getMemUseMaxV() == null ? null :
             * policy.getMemUseMaxV().doubleValue();
             */

            // 유저가 선택한 IDE와 파이썬 버전 기반으로 도커 이미지 태그 조회
            GpoIdeImageMas gpoIdeImageMas = gpoIdeImageMasRepository.findById(request.getImgUuid())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "IDE 이미지 카탈로그에서 매칭되는 도커 이미지가 없습니다."));
            String imageUrl = gpoIdeImageMas.getImgUrl();

            // Kubernetes Deployment, Service, Ingress 생성 시, 사용할 변수 세팅
            String inst = UUID.randomUUID().toString().substring(0, 8);
            String ide = request.getIdeType().toLowerCase();
            String user = safe(request.getUserId());
            String dwSecretName = null;

            // Kubernetes 라벨 설정 및 Deployment, Service, Ingress 명명 규칙
            Map<String, String> labels = Map.of("app", "webide", "user", user, "ide", ide, "inst", inst);
            String deployName = "deploy-%s-%s-%s".formatted(ide, user, inst);
            String svcName = "svc-%s-%s-%s".formatted(ide, user, inst);
            String ingName = "ing-%s-%s-%s".formatted(ide, user, inst);
            String path = "/%s/%s/%s".formatted(ide, user, inst);

            // TODO: dwSecretName이 있는 경우
            /*
             * dwAccountUsed: Boolean => True이고, dwAccount: String => 빈 문자열이 아닌 경우, DW계정 정보를
             * 통해 DW 연동 후, Kubernetes Secret을 통해 임시 디렉토리에 저장하는 로직 추가
             * dwSecretName이 있는 경우
             * - Kubernetes Secret 존재 검사/생성
             * - Pod에 envFrom/volumeMount로 주입
             * - IDE POD 내 특정 디렉토리 경로로 마운트
             */
            if (Boolean.TRUE.equals(request.getDwAccountUsed())) {
                if (request.getDwAccount() == null || request.getDwAccount().isBlank()) {
                    throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "dwAccount는 필수입니다.");
                }
                /*
                 * dwSecretName 처리...
                 * request.getDwAccount() 를 통해 DW 접속 키 조회 후 변수 할당
                 * dwSecretName; // 시크릿명으로 사용
                 */
            }

            // kubernetes 리소스 생성
            KubeCreateDeploymentReq.KubeCreateDeploymentReqBuilder kubeCreateDeploymentReqBuilder =
                    KubeCreateDeploymentReq
                            .builder()
                            .prjSeq(request.getPrjSeq())
                            .imageTag(imageUrl)
                            .ideType(ide)
                            // .cpuDefault(cpuDefault)
                            // .memDefault(memDefault)
                            // .cpuMax(cpuMax)
                            // .memMax(memMax)
                            .cpuDefault(request.getCpu())
                            .memDefault(request.getMemory())
                            .cpuMax(request.getCpu())
                            .memMax(request.getMemory())
                            .deployName(deployName)
                            .labels(labels)
                            .path(path);
            if (Boolean.TRUE.equals(request.getDwAccountUsed())) {
                kubeCreateDeploymentReqBuilder.dwAccountId(request.getDwAccount());
            }
            if (dwSecretName != null && !dwSecretName.isBlank()) {
                kubeCreateDeploymentReqBuilder.dwSecretName(dwSecretName);
            }

            KubeCreateDeploymentRes depRes = kubeService.createDeployment(kubeCreateDeploymentReqBuilder.build());
            validateDeployment(depRes);
            KubeCreateServiceRes svcRes = kubeService.createService(KubeCreateServiceReq.builder()
                    .svcName(svcName)
                    .ideType(ide)
                    .labels(labels)
                    .build());
            validateService(svcRes);
            KubeCreateIngressReq ingressReq = KubeCreateIngressReq.builder()
                    .ingName(ingName)
                    .labels(labels)
                    .svcName(svcName)
                    .path(path)
                    .svcPort(svcRes.getPort())
                    .build();
            KubeCreateIngressRes ingRes = kubeService.createIngress(ingressReq);
            validateIngress(ingRes);

            // VirtualService 생성 (조건부)
            var vsConf = kubeConfig.getVirtualService();
            boolean vsCreated = false;
            String vsHost = null;
            String vsPathPrefix = null;

            log.info("[DEBUG] VirtualService config check: vsConf={}, enabled={}",
                    vsConf != null ? "not null" : "null",
                    vsConf != null ? vsConf.getEnabled() : "N/A");

            if (vsConf != null && Boolean.TRUE.equals(vsConf.getEnabled())) {
                log.info("[DEBUG] VirtualService creation started - gateway={}, host={}",
                        vsConf.getGateway(), vsConf.getHost());
                try {
                    String vsName = "vs-%s-%s-%s".formatted(ide, user, inst);
                    String gateway = vsConf.getGateway();
                    String host = vsConf.getHost();

                    log.info(
                            "[DEBUG] VirtualService parameters: vsName={}, gateway={}, host={}, pathPrefix={}, " +
                                    "svcName={}, svcPort={}",
                            vsName, gateway, host, path, svcName, svcRes.getPort());

                    if (gateway != null && !gateway.isBlank() && host != null && !host.isBlank()) {
                        log.info("[DEBUG] Calling kubeService.createVirtualService()...");
                        KubeCreateVirtualServiceRes vsRes = kubeService.createVirtualService(
                                KubeCreateVirtualServiceReq.builder()
                                        .vsName(vsName)
                                        .gateway(gateway)
                                        .host(host)
                                        .pathPrefix(path)
                                        .svcName(svcName)
                                        .svcPort(svcRes.getPort())
                                        .labels(labels)
                                        .build());
                        log.info("[IDE] VirtualService created: vsName={}, gateway={}, host={}, pathPrefix={}",
                                vsRes.getVsName(), vsRes.getGateway(), vsRes.getHost(), vsRes.getPathPrefix());

                        // VirtualService 생성 성공 - URL 정보 저장
                        vsCreated = true;
                        vsHost = vsRes.getHost();
                        vsPathPrefix = vsRes.getPathPrefix();
                    } else {
                        log.warn(
                                "[IDE] VirtualService enabled but gateway or host not configured, skipping " +
                                        "VirtualService creation (gateway={}, host={})",
                                gateway, host);
                    }
                } catch (KubernetesClientException e) {
                    // VirtualService Ingress 삭제
                    kubeService.deleteIngress(
                            KubeDeleteIngressReq.builder()
                                    .namespace(kubeConfig.getNamespace())
                                    .labels(labels)
                                    .build());
                    throw e;
                } catch (IllegalArgumentException e) {
                    log.error("[IDE] VirtualService creation failed, Rollback Ingress : {}", e.getMessage(), e);
                    throw e;
                }
            } else {
                log.info("[DEBUG] VirtualService creation skipped - vsConf is null or not enabled");
            }

            // svrUrlNm에 저장할 URL 결정
            String svrUrl;

            if (vsCreated && vsHost != null && vsPathPrefix != null) {
                // VirtualService가 활성화되고 성공적으로 생성된 경우: host + pathPrefix 조합
                // scheme은 application.yml의 kube.ide.urlScheme 설정값 사용
                String scheme = Optional.ofNullable(kubeConfig.getIde())
                        .map(KubeClientConfig.IdeConf::getUrlScheme)
                        .filter(s -> !s.isBlank())
                        .orElse("http")
                        .toLowerCase(Locale.ROOT);
                String normPathPrefix = vsPathPrefix.startsWith("/") ? vsPathPrefix : "/" + vsPathPrefix;
                svrUrl = "%s://%s%s".formatted(scheme, vsHost, normPathPrefix);
                log.info("VirtualService enabled - using VirtualService URL: {}", svrUrl);
            } else {
                // VirtualService 비활성화 또는 생성 실패: 기존 Ingress URL 사용
                String scheme = Optional.ofNullable(ingRes.getScheme()).orElse("http").toLowerCase(Locale.ROOT);
                String hostFromIngress = Optional.ofNullable(ingRes.getHost()).filter(h -> !h.isBlank()).orElse(null);
                String publicBaseUrl = Optional.ofNullable(kubeConfig.getIde())
                        .map(KubeClientConfig.IdeConf::getPublicBaseUrl)
                        .filter(s -> !s.isBlank())
                        .orElse(null);

                String normPath = path.startsWith("/") ? path : "/" + path;

                if (hostFromIngress != null) {
                    // ALB가 제공하는 DNS 사용
                    svrUrl = "%s://%s%s".formatted(scheme, hostFromIngress, normPath);
                } else if (publicBaseUrl != null) {
                    // fallback: 수동 설정한 publicBaseUrl 사용
                    String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                            : publicBaseUrl;
                    svrUrl = base + normPath;
                } else {
                    svrUrl = normPath; // 최소 정보
                }
                log.info("VirtualService disabled - using Ingress URL: {}", svrUrl);
            }

            // VSCode의 경우 URL 끝에 trailing slash 추가
            if ("vscode".equals(ide) && !svrUrl.endsWith("/")) {
                svrUrl = svrUrl + "/";
                log.info("Added trailing slash for VSCode URL: {}", svrUrl);
            }

            // IDE 자동만료: 7일
            LocalDateTime expireAt = LocalDateTime.now()
                    .plusDays(7)
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(0);

            // 사용자 이름 처리
            Optional<User> userInfo = gpoUsersRepository.findByMemberId(request.getUserId());

            GpoIdeStatusMas row = GpoIdeStatusMas.builder()
                    .uuid(UUID.randomUUID().toString())
                    .memberId(request.getUserId())
                    .imgUuid(gpoIdeImageMas.getUuid())
                    .svrUrlNm(svrUrl)
                    .dwAccountId(request.getDwAccount())
                    .cpuUseHaldngV(BigDecimal.valueOf(request.getCpu()))
                    .memUseHaldngV(BigDecimal.valueOf(request.getMemory()))
                    .expAt(expireAt)
                    .build();
            gpoIdeStatusMasRepository.save(row);

            // 응답 생성 후 반환
            IdeCreateRes res = IdeCreateRes.builder()
                    .ideId(row.getUuid())
                    .userId(request.getUserId())
                    .prjSeq(request.getPrjSeq())
                    .ide(ide)
                    .ingressUrl(svrUrl)
                    .expireAt(expireAt != null ? expireAt.atZone(ZoneId.systemDefault()).toInstant() : null)
                    .cpu(request.getCpu())
                    .memory(request.getMemory())
                    .image(imageUrl)
                    .build();
            log.info("[IDE] createIde done :{}", res);

            return res;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("IDE 생성 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "IDE 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public void deleteIde(IdeDeleteReq req) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.deleteIde ]");
        log.info("ideId            : {}", req.getIdeId());
        log.info("-----------------------------------------------------------------------------------------");
        try {
            GpoIdeStatusMas gpoIdeStatusMas = gpoIdeStatusMasRepository.findById(req.getIdeId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "IDE not found: " + req.getIdeId()));
            Optional<GpoIdeImageMas> imageOpt = gpoIdeImageMasRepository.findById(gpoIdeStatusMas.getImgUuid());

            String userSafe = safe(gpoIdeStatusMas.getMemberId());
            String ideKey =
                    imageOpt.map(gpoIdeImageMas -> gpoIdeImageMas.getImgG().name()).orElse("").toLowerCase(Locale.ROOT);
            String inst = Optional.ofNullable(parseInst(gpoIdeStatusMas.getSvrUrlNm())).orElse("");

            // inst가 비어있을 경우 레이블에서 inst를 제외하고 검색
            // (URL 파싱 실패 시 fallback)
            Map<String, String> labels;
            if (inst.isEmpty()) {
                // inst 없이 검색 (URL에서 inst 추출 실패한 경우)
                labels = Map.of(
                        "app", "webide",
                        "user", userSafe,
                        "ide", ideKey);
                log.info("inst not found in URL, using labels without inst: {}", labels);
            } else {
                // inst 포함 검색 (정상적인 경우)
                labels = Map.of(
                        "app", "webide",
                        "user", userSafe,
                        "ide", ideKey,
                        "inst", inst);
            }

            kubeService.deleteResources(
                    KubeDeleteResourcesReq.builder()
                            .namespace(kubeConfig.getNamespace())
                            .labels(labels)
                            .deployName(inst.isEmpty() ? null
                                    : "deploy-%s-%s-%s".formatted(ideKey, userSafe, inst))
                            .svcName(inst.isEmpty() ? null : "svc-%s-%s-%s".formatted(ideKey, userSafe, inst))
                            .ingName(inst.isEmpty() ? null : "ing-%s-%s-%s".formatted(ideKey, userSafe, inst))
                            .build());

            // DB 삭제는 성공/실패와 분리하거나, 성공 후 수행
            gpoIdeStatusMasRepository.deleteById(req.getIdeId());
            log.info("IDE deleted: ideId={} labels={}", req.getIdeId(), labels);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("IDE 삭제 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "IDE 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteIdeBatch() {
        LocalDateTime now = LocalDateTime.now();
        List<GpoIdeStatusMas> targets = gpoIdeStatusMasRepository.findExpired(now);
        List<String> batchFailedList = new ArrayList<>();
        log.info("[BATCH] deleteIdeBatch targets={}", targets.size());

        for (GpoIdeStatusMas u : targets) {
            try {
                IdeDeleteReq req = IdeDeleteReq.builder()
                        .ideId(u.getUuid())
                        .build();
                // 기존 단건 삭제 로직 재사용
                deleteIde(req);
            } catch (KubernetesClientException e) {
                log.error("[BATCH] K8s API error during deletion ideId={}", u.getUuid(), e);
                batchFailedList.add(u.getUuid());
            } catch (IllegalArgumentException e) {
                log.error("[BATCH] invalid parameter ideId={}", u.getUuid(), e);
                batchFailedList.add(u.getUuid());
            } catch (Exception e) {
                log.error("[BATCH] delete failed ideId={}", u.getUuid(), e);
                // 계속 진행
                batchFailedList.add(u.getUuid());
            }

        }
        if (!batchFailedList.isEmpty()) {
            log.warn("[BATCH] delete completed with {} failures. failedIdeIds={}",
                    batchFailedList.size(), batchFailedList);
        }
    }

    @Override
    public Page<IdeStatusRes> getIdeStatus(String memberId, SearchIdeStatusReq request) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.getIdeStatus ]");
        log.info("memberId            : {}", memberId);
        log.info("keyword             : {}", request.getKeyword());
        log.info("-----------------------------------------------------------------------------------------");

        String keyword = request.getKeyword() != null
                ? request.getKeyword().toLowerCase()
                : null;

        return gpoIdeStatusMasRepository.findIdeStatusBySearch(
                memberId,
                keyword,
                request.toPageable()
        );
    }

    @Override
    @Transactional
    public void extendIdeExpiration(String statusUuid, IdeExtendReq request) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service IDEServiceImpl.extendIdeExpiration ]");
        log.info("statusUuid          : {}", statusUuid);
        log.info("extendDays          : {}", request.getExtendDays());
        log.info("-----------------------------------------------------------------------------------------");

        GpoIdeStatusMas ideStatus = gpoIdeStatusMasRepository.findById(statusUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "IDE not found: " + statusUuid));

        ideStatus.extendExpiration(request.getExtendDays());
    }

    /*
     * ====================== Helper 함수 모음 ======================
     */
    private IdeImage toIdeImage(GpoIdeImageMas image) {
        return IdeImage.builder()
                .id(image.getUuid())
                .name(image.getImgNm())
                .desc(image.getDtlCtnt())
                .type(image.getImgG() != null ? image.getImgG().name() : null)
                .build();
    }

    private String safe(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9-]", "-");
    }

    private String pyShort(String v) {
        return v.replace(".", "");
    }

    private void requireNonBlank(String v, String name) {
        if (v == null || v.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, name + " required");
        }
    }

    private void validateDeployment(KubeCreateDeploymentRes r) {
        if (r == null || r.getDeployName() == null) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Deployment 생성 실패");
        }
        Integer desired = r.getDesiredReplicas();
        if (desired == null || desired < 1) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Deployment replicas 비정상");
        }
        // 즉시 Ready 보장은 어려움. 생성 성공만 확인. Ready 보장 필요시 별도 대기/폴링 로직 추가.
    }

    private void validateService(KubeCreateServiceRes r) {
        if (r == null || r.getSvcName() == null) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Service 생성 실패");
        }
        if (r.getPort() == null || r.getPort() <= 0) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Service 포트 비정상");
        }
        if (r.getTargetPort() == null || r.getTargetPort() <= 0) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Service 타겟포트 비정상");
        }
        if (r.getSvcType() == null) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Service 타입 누락");
        }
    }

    private void validateIngress(KubeCreateIngressRes r) {
        if (r == null || r.getIngName() == null) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Ingress 생성 실패");
        }
        if (r.getPath() == null || !r.getPath().startsWith("/")) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Ingress path 비정상");
        }
        // host는 null일 수 있음(ALB/DNS 미할당). URL 생성은 별도 분기 처리.
    }

    private String parseInst(String ingressUrl) {
        if (ingressUrl == null || ingressUrl.isBlank()) {
            return null;
        }
        var m = Pattern
                .compile(".*/([a-f0-9]{8})/?$", Pattern.CASE_INSENSITIVE)
                .matcher(ingressUrl);
        return m.find() ? m.group(1) : null;
    }

}
