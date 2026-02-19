package com.skax.aiplatform.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Configuration
@Validated
@ConfigurationProperties(prefix = "kube")
public class KubeClientConfig {

    private String masterUrl;              // edev: https://kubernetes.default.svc
    private String namespace;              // 예: sktai-web-ide-ns
    private Integer connectTimeoutMs;      // 5000
    private Integer readTimeoutMs;         // 10000
    private String userAgent;              // axportal-backend/1.0
    private Boolean trustCerts;            // false
    private String auth;                   // serviceAccount | kubeconfig
    private String token;                  // SA token string (direct)
    private String tokenFile;              // /var/run/secrets/.../token (fallback)
    private String caCertFile;             // /var/run/secrets/.../ca.crt
    private String kubeconfig;             // (옵션) 로컬 테스트용
    private Integer replicas;
    private Map<String, Integer> ports;
    private List<HostAliasConfig> hostAliases; //pod spec hostAliases 추가
    private List<envConf> envConf;        //환경변수 추가
    private pvcConfig persistentVolumeClaim; //pvc 추가
    private PodSecurityContextConfig securityContext;

    @Valid
    private ServiceConf service;
    @Valid
    private IngressConf ingress;
    @Valid
    private VirtualServiceConf virtualService;
    @Valid
    private IdeConf ide;
    @Valid
    private AffinityConf affinity;
    @Valid
    private NodeSelectorConf nodeSelector;
    @Valid
    private List<TolerationConf> tolerations;

    @Getter
    @Setter
    public static class ServiceConf {
        @NotBlank
        private String type;      // ClusterIP | NodePort | LoadBalancer
        @NotBlank
        private String portName;  // 예: "http"
        private Integer nodePort;
    }

    @Getter
    @Setter
    public static class IngressConf {
        private Boolean enabled;
        private String className;    // 선택(검증 제거)
        private String pathType = "Prefix";
        private Map<String, String> annotations;
    }

    @Getter
    @Setter
    public static class VirtualServiceConf {
        private Boolean enabled;     // VirtualService 활성화 여부
        private String gateway;      // Istio Gateway (예: istio-system/aip-istio-gateway)
        private String host;         // Host (예: adxp.mobigen.com)
    }

    @Getter
    @Setter
    public static class IdeConf {
        private Integer expireDays;
        private String urlScheme = "http";
        private String publicBaseUrl;   // ← 추가
    }

    @Getter
    @Setter
    public static class AffinityConf {
        private String nodetype;        // node affinity key
        private String nodeValue;       // node affinity value
    }

    @Getter
    @Setter
    public static class HostAliasConfig {
        private String ip;
        private List<String> hostnames;
    }

    @Getter
    @Setter
    public static class NodeSelectorConf {
        private Map<String, String> labels;
    }

    @Getter
    @Setter
    public static class TolerationConf {
        private String key;
        private String operator;
        private String value;
        private String effect;
    }

    @Getter
    @Setter
    public static class envConf {
        private String name;
        private String value;
    }

    @Getter
    @Setter
    public static class pvcConfig {
        private String storageClass;
        private String accessMode;
        private String mountPath;
        private String storageSize;
    }

    @Getter
    @Setter
    public static class PodSecurityContextConfig {
        private Long runAsUser;
        private Long runAsGroup;
        private Long fsGroup;
    }

    @PostConstruct
    public void logEffective() {
        log.info("[K8S Config] masterUrl={}, namespace={}, auth={}, replicas={}, ports={}",
                masterUrl, namespace, auth, replicas, ports);
        if (service != null) {
            log.info("[K8S Config] service type={}, portName={}",
                    service.getType(), service.getPortName());
        }
        if (ingress != null && ide != null) {
            log.info("[K8S Config] ingress enabled={}, className={}, pathType={}, annosKeys={}, ide urlScheme={}, publicBaseUrl={}, expireDays={}",
                    ingress.getEnabled(), ingress.getClassName(), ingress.getPathType(),
                    ingress.getAnnotations() != null ? ingress.getAnnotations().keySet() : null,
                    ide.getUrlScheme(), ide.getPublicBaseUrl(), ide.getExpireDays());
        }
    }

    @Bean(destroyMethod = "close")
    public KubernetesClient kubernetesClient() {
        Config base;
        try {
            if ("kubeconfig".equalsIgnoreCase(auth) && kubeconfig != null) {
                String kc = Files.readString(Path.of(kubeconfig));
                base = Config.fromKubeconfig(kc);
            } else {
                base = Config.autoConfigure(null);
            }
        } catch (java.nio.file.NoSuchFileException e) {
            log.error(">>> K8S 설정 파일을 찾을 수 없음 - kubeconfig: {}, error: {}", kubeconfig, e.getMessage(), e);
            throw new IllegalStateException("K8S 설정 파일이 존재하지 않습니다: " + kubeconfig, e);
        } catch (java.nio.file.AccessDeniedException e) {
            log.error(">>> K8S 설정 파일 접근 권한 없음 - kubeconfig: {}, error: {}", kubeconfig, e.getMessage(), e);
            throw new IllegalStateException("K8S 설정 파일 접근 권한이 없습니다: " + kubeconfig, e);
        } catch (java.io.IOException e) {
            log.error(">>> K8S 설정 파일 읽기 실패 - kubeconfig: {}, error: {}", kubeconfig, e.getMessage(), e);
            throw new IllegalStateException("K8S 설정 파일 읽기 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(">>> K8S 설정 파일 경로 오류 - kubeconfig: {}, error: {}", kubeconfig, e.getMessage(), e);
            throw new IllegalStateException("K8S 설정 파일 경로가 올바르지 않습니다: " + kubeconfig, e);
        } catch (NullPointerException e) {
            log.error(">>> K8S 설정 초기화 실패 - 필수 설정값 누락, error: {}", e.getMessage(), e);
            throw new IllegalStateException("K8S 기본 설정 초기화 실패: 필수 설정값이 누락되었습니다.", e);
        } catch (Exception e) {
            log.error(">>> K8S 기본 설정 초기화 실패 - auth: {}, kubeconfig: {}, error: {}", 
                    auth, kubeconfig, e.getMessage(), e);
            throw new IllegalStateException("K8S 기본 설정 초기화 실패: " + e.getMessage(), e);
        }

        ConfigBuilder cb = new ConfigBuilder(base);
        
        try {
            if (masterUrl != null) cb.withMasterUrl(masterUrl);
            if (namespace != null) cb.withNamespace(namespace);
            if (connectTimeoutMs != null) cb.withConnectionTimeout(connectTimeoutMs);
            if (readTimeoutMs != null) cb.withRequestTimeout(readTimeoutMs);
            if (userAgent != null) cb.withUserAgent(userAgent);
            if (trustCerts != null) cb.withTrustCerts(trustCerts);
        } catch (IllegalArgumentException e) {
            log.error(">>> K8S 설정 빌더 구성 실패 - 잘못된 파라미터, masterUrl: {}, namespace: {}, error: {}", 
                    masterUrl, namespace, e.getMessage(), e);
            throw new IllegalStateException("K8S 설정 빌더 구성 실패: 잘못된 파라미터입니다.", e);
        } catch (Exception e) {
            log.error(">>> K8S 설정 빌더 구성 실패 - masterUrl: {}, namespace: {}, error: {}", 
                    masterUrl, namespace, e.getMessage(), e);
            throw new IllegalStateException("K8S 설정 빌더 구성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

        if ("serviceAccount".equalsIgnoreCase(auth)) {
            try {
                String tokenValue;
                if (token != null && !token.isEmpty()) {
                    // Use direct token string if provided
                    tokenValue = token.trim();
                    log.debug(">>> K8S serviceAccount 인증 - 직접 제공된 token 사용");
                } else if (tokenFile != null) {
                    // Fall back to reading from file
                    tokenValue = Files.readString(Path.of(tokenFile)).trim();
                    log.debug(">>> K8S serviceAccount 인증 - tokenFile에서 읽기: {}", tokenFile);
                } else {
                    log.error(">>> K8S serviceAccount 인증 실패 - token 또는 tokenFile이 필요합니다");
                    throw new IllegalStateException("K8S serviceAccount 인증 시 token 또는 tokenFile이 필요합니다");
                }
                
                if (tokenValue == null || tokenValue.isEmpty()) {
                    log.error(">>> K8S serviceAccount 토큰이 비어있음 - tokenFile: {}", tokenFile);
                    throw new IllegalStateException("K8S serviceAccount 토큰이 비어있습니다.");
                }
                
                cb.withOauthToken(tokenValue);
                if (caCertFile != null) {
                    cb.withCaCertFile(caCertFile);
                    log.debug(">>> K8S CA 인증서 설정 - caCertFile: {}", caCertFile);
                }
                
            } catch (java.nio.file.NoSuchFileException e) {
                log.error(">>> K8S serviceAccount 토큰 파일을 찾을 수 없음 - tokenFile: {}, error: {}", 
                        tokenFile, e.getMessage(), e);
                throw new IllegalStateException("K8S serviceAccount 토큰 파일이 존재하지 않습니다: " + tokenFile, e);
            } catch (java.nio.file.AccessDeniedException e) {
                log.error(">>> K8S serviceAccount 토큰 파일 접근 권한 없음 - tokenFile: {}, error: {}", 
                        tokenFile, e.getMessage(), e);
                throw new IllegalStateException("K8S serviceAccount 토큰 파일 접근 권한이 없습니다: " + tokenFile, e);
            } catch (java.io.IOException e) {
                log.error(">>> K8S serviceAccount 토큰 파일 읽기 실패 - tokenFile: {}, error: {}", 
                        tokenFile, e.getMessage(), e);
                throw new IllegalStateException("K8S serviceAccount 토큰 파일 읽기 중 오류가 발생했습니다: " + e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                log.error(">>> K8S serviceAccount 토큰 설정 실패 - 잘못된 파라미터, tokenFile: {}, error: {}", 
                        tokenFile, e.getMessage(), e);
                throw new IllegalStateException("K8S serviceAccount 토큰 설정 실패: 잘못된 파라미터입니다.", e);
            } catch (IllegalStateException e) {
                // 이미 로그된 IllegalStateException은 그대로 전파
                throw e;
            } catch (Exception e) {
                log.error(">>> K8S serviceAccount 토큰 설정 실패 - tokenFile: {}, error: {}", 
                        tokenFile, e.getMessage(), e);
                throw new IllegalStateException("K8S serviceAccount 토큰 설정 중 오류가 발생했습니다: " + e.getMessage(), e);
            }
        }
        
        try {
            return new KubernetesClientBuilder().withConfig(cb.build()).build();
        } catch (IllegalArgumentException e) {
            log.error(">>> K8S 클라이언트 생성 실패 - 잘못된 설정, masterUrl: {}, namespace: {}, error: {}", 
                    masterUrl, namespace, e.getMessage(), e);
            throw new IllegalStateException("K8S 클라이언트 생성 실패: 설정값이 올바르지 않습니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> K8S 클라이언트 생성 실패 - 필수 설정 누락, error: {}", e.getMessage(), e);
            throw new IllegalStateException("K8S 클라이언트 생성 실패: 필수 설정이 누락되었습니다.", e);
        } catch (Exception e) {
            log.error(">>> K8S 클라이언트 생성 실패 - masterUrl: {}, namespace: {}, error: {}", 
                    masterUrl, namespace, e.getMessage(), e);
            throw new IllegalStateException("K8S 클라이언트 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
