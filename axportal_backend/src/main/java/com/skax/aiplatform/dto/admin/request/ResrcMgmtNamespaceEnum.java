package com.skax.aiplatform.dto.admin.request;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * 솔루션별 네임스페이스 및 Pod 패턴 관리 Enum
 * 
 * @author SonMunWoo
 * @since 2025-10-25
 * @version 1.0
 */
@Getter
public enum ResrcMgmtNamespaceEnum {

    // 포탈 자원
    AGENT("에이전트",
            new String[] { "ns-24ba585a-02fc-43d8-b9f1-f7ca9e020fe5" },
            "^svc.*"),
    MODEL("모델",
            new String[] { "aiplatform" },
            "model-backend.*"),

    // 솔루션 자원 (dev, prod 분기)
    API_GATEWAY("API G/W",
            new String[] { "gap-apigtw-dev", "gap-apigtw-prd" }),
    DATUMO("Datumo",
            new String[] { "gap-datumo-dev", "gap-datumo-prd" }),
    PORTAL("포탈",
            new String[] { "gap-portal-dev", "gap-portal-prd" }),

    // 솔루션 자원 AxPlatform (네임스페이스별 개별 항목)
    AXPLATFORM_INFRA("ADXP",
            new String[] { "gap-sktai-infra-dev", "gap-sktai-infra-prd" }),
    // AXPLATFORM_CICD("ADXP",
    //         new String[] { "gap-sktai-cicd" }), // CICD는 공통
    AXPLATFORM_AIRFLOW("ADXP",
            new String[] { "gap-sktai-airflow-dev", "gap-sktai-airflow-prd" }),
    AXPLATFORM_CERT_MANAGER("ADXP",
            new String[] { "gap-sktai-cert-manager-dev", "gap-sktai-cert-manager-prd" }),
    AXPLATFORM_MONITORING("ADXP",
            new String[] { "gap-sktai-monitoring-dev", "gap-sktai-monitoring-prd" }),
    // AXPLATFORM_REGISTRY("ADXP",
    //         new String[] { "gap-sktai-registry" }), // Registry는 공통
    KSERVE("ADXP",
            new String[] { "kserve" }), // KServe는 공통
    KNATIVE_SERVING("ADXP",
            new String[] { "knative-serving" }), // Knative Serving은 공통
    KNATIVE_OPERATOR("ADXP",
            new String[] { "knative-operator" }), // Knative Operator는 공통
    ISTIO_SYSTEM("ADXP",
            new String[] { "istio-system" }), // Istio System은 공통
    AXPLATFORM_AIPLATFORM("ADXP",
            new String[] { "gap-sktai-aiplatform-dev", "gap-sktai-aiplatform-prd" });

    /**
     * 솔루션 표시명
     */
    private final String displayName;

    /**
     * Kubernetes 네임스페이스 목록
     */
    private final String[] namespaces;

    /**
     * Pod 이름 패턴 (정규식) - 포탈 자원(AGENT, MODEL)에만 사용
     */
    private final String podPattern;

    /**
     * 생성자 (Pod 패턴 포함 - 포탈 자원용)
     * 
     * @param displayName 솔루션 표시명
     * @param namespaces  Kubernetes 네임스페이스 배열
     * @param podPattern  Pod 이름 패턴 (정규식)
     */
    ResrcMgmtNamespaceEnum(String displayName, String[] namespaces, String podPattern) {
        this.displayName = displayName;
        this.namespaces = namespaces;
        this.podPattern = podPattern;
    }

    /**
     * 생성자 (Pod 패턴 없음 - 솔루션 자원용)
     * 
     * @param displayName 솔루션 표시명
     * @param namespaces  Kubernetes 네임스페이스 배열
     */
    ResrcMgmtNamespaceEnum(String displayName, String[] namespaces) {
        this.displayName = displayName;
        this.namespaces = namespaces;
        this.podPattern = null;
    }

    /**
     * 첫 번째 네임스페이스 반환 (단일 네임스페이스 사용 시)
     * 기본값은 dev 네임스페이스를 반환합니다.
     * 
     * @return 첫 번째 네임스페이스 (기본값: dev)
     */
    public String getNamespace() {
        return namespaces.length > 0 ? namespaces[0] : "";
    }

    /**
     * 프로파일 기반 네임스페이스 반환
     * 
     * @param profile 프로파일 (prod이면 prod 네임스페이스, 그 외는 dev 네임스페이스)
     * @return 프로파일에 해당하는 네임스페이스, 없으면 첫 번째 네임스페이스 반환
     */
    public String getNamespace(String profile) {
        if (profile == null || profile.isEmpty()) {
            return getNamespace(); // 기본값 반환 (dev)
        }

        // prod 프로파일인 경우
        if ("prod".equals(profile)) {
            // 두 번째 네임스페이스가 있으면 반환 (prod)
            if (namespaces.length >= 2) {
                return namespaces[1];
            }
            // prod 네임스페이스가 없으면 첫 번째 반환 (공통 네임스페이스)
            return getNamespace();
        }

        // prod가 아닌 모든 프로파일(local, elocal, dev, edev, staging 등)은 첫 번째 네임스페이스 반환 (dev)
        return getNamespace();
    }

    /**
     * 모든 네임스페이스 목록 반환
     * 
     * @return 네임스페이스 리스트
     */
    public List<String> getNamespaceList() {
        return Arrays.asList(namespaces);
    }

    /**
     * 프로파일 기반 네임스페이스 목록 반환
     * 
     * @param profile 프로파일 (prod이면 prod 네임스페이스, 그 외는 dev 네임스페이스)
     * @return 프로파일에 해당하는 네임스페이스 리스트
     */
    public List<String> getNamespaceList(String profile) {
        if (profile == null || profile.isEmpty()) {
            return List.of(getNamespace()); // 기본값 반환 (dev)
        }

        // prod 프로파일인 경우
        if ("prod".equals(profile)) {
            // 두 번째 네임스페이스가 있으면 반환 (prod)
            if (namespaces.length >= 2) {
                return List.of(namespaces[1]);
            }
            // prod 네임스페이스가 없으면 첫 번째 반환 (공통 네임스페이스)
            return List.of(getNamespace());
        }

        // prod가 아닌 모든 프로파일(local, elocal, dev, edev, staging 등)은 첫 번째 네임스페이스 반환 (dev)
        return List.of(getNamespace());
    }

    /**
     * 표시명으로 ResrcMgmtNamespaceEnum 찾기
     * 
     * @param displayName 솔루션 표시명
     * @return ResrcMgmtNamespaceEnum 또는 null
     */
    public static ResrcMgmtNamespaceEnum findByDisplayName(String displayName) {
        for (ResrcMgmtNamespaceEnum solution : values()) {
            if (solution.displayName.equals(displayName)) {
                return solution;
            }
        }
        return null;
    }

    /**
     * 네임스페이스로 ResrcMgmtNamespaceEnum 찾기
     * 
     * @param namespace Kubernetes 네임스페이스
     * @return ResrcMgmtNamespaceEnum 또는 null
     */
    public static ResrcMgmtNamespaceEnum findByNamespace(String namespace) {
        for (ResrcMgmtNamespaceEnum solution : values()) {
            for (String ns : solution.namespaces) {
                if (ns.equals(namespace)) {
                    return solution;
                }
            }
        }
        return null;
    }

    /**
     * CPU Requests 쿼리를 생성
     * 
     * @return 포맷된 쿼리 문자열
     */
    public String getCpuRequestsQuery() {
        return String.format(ResrcMgmtQueryEnum.SOLUTION_CPU_REQUESTS.getQuery(), getNamespace());
    }

    /**
     * CPU Limits 쿼리를 생성
     * 
     * @return 포맷된 쿼리 문자열
     */
    public String getCpuLimitsQuery() {
        return String.format(ResrcMgmtQueryEnum.SOLUTION_CPU_LIMITS.getQuery(), getNamespace());
    }

    /**
     * CPU Usage 쿼리를 생성
     * 
     * @return 포맷된 쿼리 문자열
     */
    public String getCpuUsageQuery() {
        return String.format(ResrcMgmtQueryEnum.SOLUTION_CPU_USAGE_WITH_CONTAINER.getQuery(), getNamespace());
    }

    /**
     * Memory Requests 쿼리를 생성
     * 
     * @return 포맷된 쿼리 문자열
     */
    public String getMemoryRequestsQuery() {
        return String.format(ResrcMgmtQueryEnum.SOLUTION_MEMORY_REQUESTS.getQuery(), getNamespace());
    }

    /**
     * Memory Limits 쿼리를 생성
     * 
     * @return 포맷된 쿼리 문자열
     */
    public String getMemoryLimitsQuery() {
        return String.format(ResrcMgmtQueryEnum.SOLUTION_MEMORY_LIMITS.getQuery(), getNamespace());
    }

    /**
     * Memory Usage 쿼리를 생성
     * 
     * @return 포맷된 쿼리 문자열
     */
    public String getMemoryUsageQuery() {
        return String.format(ResrcMgmtQueryEnum.SOLUTION_MEMORY_USAGE_WITH_CONTAINER.getQuery(), getNamespace());
    }
}
