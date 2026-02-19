package com.skax.aiplatform.dto.admin.request;

/**
 * 자원 관리 쿼리 타입 Enum
 * Prometheus 쿼리 파라미터를 관리
 * 
 * @author SonMunWoo
 * @since 2025-09-27
 * @version 1.0
 */
public enum ResrcMgmtQueryEnum {

        // ===========================================
        // 포탈 자원 현황
        // ===========================================

        /**
         * 포탈 자원 현황 - 에이전트 CPU 사용량
         */
        PORTAL_AGENT_CPU_USAGE(
                        "sum(node_namespace_pod_container:container_cpu_usage_seconds_total:sum_irate{cluster=\"\", namespace=\"%s\", pod=~\"%s\", container!=\"\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 CPU 요청량
         */
        PORTAL_AGENT_CPU_REQUESTS(
                        "sum(cluster:namespace:pod_cpu:active:kube_pod_container_resource_requests{cluster=\"\", namespace=\"%s\", pod=~\"%s\", container!=\"\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 Memory 사용량
         */
        PORTAL_AGENT_MEMORY_USAGE(
                        "sum(container_memory_working_set_bytes{job=\"kubelet\", metrics_path=\"/metrics/cadvisor\", cluster=\"\", namespace=\"%s\", pod=~\"%s\", container!=\"\", image!=\"\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 Memory 요청량
         */
        PORTAL_AGENT_MEMORY_REQUESTS(
                        "sum(cluster:namespace:pod_memory:active:kube_pod_container_resource_requests{cluster=\"\", namespace=\"%s\", pod=~\"%s\"})"),

        /**
         * 포탈 자원 현황 - 모델 CPU 사용량 (session별)
         * Backend.AI cpu_util 값은 millicore 단위이므로 1000으로 나누어 core 단위로 변환
         * %s: session_id
         */
        PORTAL_MODEL_CPU_USAGE(
                        "sum(rate(backendai_container_utilization{container_metric_name=\"cpu_util\", value_type=\"current\", session_id=\"%s\"}[5m])) / 100"),

        /**
         * 포탈 자원 현황 - 모델 CPU 요청량 (session별)
         * %s: session_id
         */
        PORTAL_MODEL_CPU_REQUESTS(
                        "sum(backendai_container_utilization{container_metric_name=\"cpu_util\", value_type=\"capacity\", session_id=\"%s\"})"),

        /**
         * 포탈 자원 현황 - 모델 Memory 사용량 (session별)
         * %s: session_id
         */
        PORTAL_MODEL_MEMORY_USAGE(
                        "sum(backendai_container_utilization{container_metric_name=\"mem\", value_type=\"current\", session_id=\"%s\"}) / 1024 / 1024 / 1024"),

        /**
         * 포탈 자원 현황 - 모델 Memory 요청량 (session별)
         * %s: session_id
         */
        PORTAL_MODEL_MEMORY_REQUESTS(
                        "sum(backendai_container_utilization{container_metric_name=\"mem\", value_type=\"capacity\", session_id=\"%s\"}) / 1024 / 1024 / 1024"),

        /**
         * 포탈 자원 현황 - 모델 GPU 사용량 (session별)
         * %s: session_id
         */
        PORTAL_MODEL_GPU_USAGE(
                        "avg(backendai_container_utilization{session_id=\"%s\", container_metric_name=\"cuda_mem\"})"),

        /**
         * 포탈 자원 현황 - 모델 GPU 코어 사용률 (session별)
         * %s: session_id
         */
        PORTAL_MODEL_GPU_UTILIZATION(
                        "avg(backendai_container_utilization{session_id=\"%s\", container_metric_name=\"cuda_util\", value_type=\"current\"})"),

        // PORTAL_MODEL_GPU_REQUESTS (미사용)
        // PORTAL_MODEL_GPU_REQUESTS("sum(cluster:namespace:pod_gpu:active:kube_pod_container_resource_requests{cluster=\"\",
        // namespace=\"aiplatform\", pod=\"model-backend-cbcd7c568-mfz5h\",
        // container!=\"\"}) by (container)"),

        // 파드별 자원 현황
        /**
         * 포탈 자원 현황 - 에이전트 파드별 CPU 사용량
         */
        PORTAL_AGENT_POD_CPU_USAGE(
                        "sum by(pod) (node_namespace_pod_container:container_cpu_usage_seconds_total:sum_irate{cluster=\"\", namespace=\"%s\", pod=~\"%s\", container!=\"\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 파드별 CPU 요청량
         */
        PORTAL_AGENT_POD_CPU_REQUESTS(
                        "sum by(pod) (kube_pod_container_resource_requests{namespace=\"%s\", pod=~\"%s\", resource=\"cpu\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 파드별 CPU 제한량
         */
        PORTAL_AGENT_POD_CPU_LIMITS(
                        "sum by(pod) (kube_pod_container_resource_limits{namespace=\"%s\", pod=~\"%s\", resource=\"cpu\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 파드별 Memory 사용량
         */
        PORTAL_AGENT_POD_MEMORY_USAGE(
                        "sum by(pod) (container_memory_working_set_bytes{job=\"kubelet\", metrics_path=\"/metrics/cadvisor\", cluster=\"\", namespace=\"%s\", pod=~\"%s\", container!=\"\", image!=\"\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 파드별 Memory 요청량
         */
        PORTAL_AGENT_POD_MEMORY_REQUESTS(
                        "sum by(pod) (kube_pod_container_resource_requests{namespace=\"%s\", pod=~\"%s\", resource=\"memory\"})"),

        /**
         * 포탈 자원 현황 - 에이전트 파드별 Memory 제한량
         */
        PORTAL_AGENT_POD_MEMORY_LIMITS(
                        "sum by(pod) (kube_pod_container_resource_limits{namespace=\"%s\", pod=~\"%s\", resource=\"memory\"})"),

        // ===========================================
        // 공통 솔루션 자원 현황 (네임스페이스, Pod 패턴 파라미터)
        // ===========================================

        /**
         * 솔루션 CPU 제한량 (네임스페이스만 사용)
         * 파라미터: namespace
         */
        SOLUTION_CPU_LIMITS(
                        "sum(kube_pod_container_resource_limits{namespace=\"%s\", resource=\"cpu\", unit=\"core\"})"),

        /**
         * 솔루션 CPU 사용량 (Container 필터 사용)
         * 파라미터: namespace
         */
        SOLUTION_CPU_USAGE_WITH_CONTAINER(
                        "sum(irate(container_cpu_usage_seconds_total{namespace=\"%s\", container!=\"\", container!=\"POD\"}[1m]))"),

        /**
         * 솔루션 Memory 할당량 (네임스페이스만 사용)
         * 파라미터: namespace
         */
        SOLUTION_MEMORY_REQUESTS("sum(kube_pod_container_resource_requests{namespace=\"%s\", resource=\"memory\"})"),

        /**
         * 솔루션 Memory 제한량
         * 파라미터: namespace
         */
        SOLUTION_MEMORY_LIMITS("sum(kube_pod_container_resource_limits{namespace=\"%s\", resource=\"memory\"})"),

        /**
         * 솔루션 Memory 사용량 (Container 필터 사용)
         * 파라미터: namespace
         */
        SOLUTION_MEMORY_USAGE_WITH_CONTAINER(
                        "sum(container_memory_working_set_bytes{namespace=\"%s\", container!=\"\", container!=\"POD\"})"),

        /**
         * 솔루션 CPU 할당량 (네임스페이스만 사용)
         * 파라미터: namespace
         */
        SOLUTION_CPU_REQUESTS(
                        "sum(kube_pod_container_resource_requests{namespace=\"%s\", resource=\"cpu\", unit=\"core\"})"),

        // ===========================================
        // GPU 노드별 자원 현황
        // ===========================================

        /**
         * GPU 노드별 - 노드별 CPU 총 코어 수 (1 core = 100%) (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_TOTAL_CPU(
                        "count by (display_name)(backendai_device_utilization{device_metric_name=\"cpu_util\", value_type=\"capacity\"})"),

        /**
         * GPU 노드별 - 노드별 CPU 사용 코어 수 (1 core = 100%) (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_CPU_USAGE(
                        "sum by (display_name)(rate(backendai_device_utilization{device_metric_name=\"cpu_util\", value_type=\"current\"}[5m])) / avg by (display_name)(backendai_device_utilization{device_metric_name=\"cpu_util\", value_type=\"capacity\"})"),

        /**
         * GPU 노드별 - 노드별 Memory 총 용량 (GB) (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_TOTAL_MEMORY(
                        "sum by (display_name) (backendai_device_utilization{device_metric_name=\"mem\", value_type=\"capacity\"})/ 1024 / 1024 / 1024"),

        /**
         * GPU 노드별 - 노드별 Memory 사용량 (GB) (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_MEMORY_USAGE(
                        "sum by (display_name) (backendai_device_utilization{device_metric_name=\"mem\", value_type=\"current\"}) / 1024 / 1024 / 1024"),

        /**
         * GPU 노드별 - 노드별 Memory 사용률 (0-100%) (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_MEMORY_UTIL(
                        "100 * sum by (display_name) (backendai_device_utilization{device_metric_name=\"mem\", value_type=\"current\"}) / sum by (display_name) (backendai_device_utilization{device_metric_name=\"mem\", value_type=\"capacity\"})"),

        /**
         * GPU 노드별 - 노드의 GPU 개수 (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_TOTAL_GPU(
                        "count by (display_name) (backendai_device_utilization{device_metric_name=\"cuda_util\", value_type=\"current\"})"),

        /**
         * GPU 노드별 - 노드의 GPU 사용량 (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_GPU_USAGE(
                        "avg by (display_name) (backendai_device_utilization{device_metric_name=\"cuda_util\", value_type=\"current\"}) / 100"),

        /**
         * GPU 노드별 - 노드별 GPU 평균 사용률 (0-100%) (display_name 기준)
         */
        GPU_NODES_BY_INSTANCE_GPU_UTIL(
                        "avg by (display_name) (backendai_device_utilization{device_metric_name=\"cuda_util\", value_type=\"current\"})"),

        // ===========================================
        // ===========================================

        // ===========================================
        // GPU 노드 상세 조회 (일부 미사용 - 주석 처리)
        // ===========================================

        // GPU 노드 상세 - 배포 워크로드 가져오기
        GPU_NODE_WORKLOAD_LIST("count(backendai_container_utilization{display_name=\"%s\"}) by (user_id, session_id)"),

        // GPU 노드 상세 - CPU 요청량 대비 사용률 (할당량 대비)
        GPU_NODE_CPU_USAGE_VS_REQUESTS(
                        "100 * sum(rate(backendai_container_utilization{container_metric_name=\"cpu_util\",display_name=\"%s\", session_id=\"%s\", value_type=\"current\"}[%s])) / sum(backendai_container_utilization{container_metric_name=\"cpu_util\", display_name=\"%s\", session_id=\"%s\", value_type=\"capacity\"})"),

        // GPU 노드 상세 - 메모리 요청량 대비 사용률 (할당량 대비)
        GPU_NODE_MEMORY_USAGE_VS_REQUESTS(
                        "100 * sum(backendai_container_utilization{container_metric_name=\"mem\",display_name=\"%s\", session_id=\"%s\", value_type=\"current\"}) / sum(backendai_container_utilization{display_name=\"%s\", session_id=\"%s\", container_metric_name=\"mem\", value_type=\"capacity\"})"),

        // GPU 노드 상세 - GPU 요청량 대비 사용률 (할당량 대비)
        GPU_NODE_GPU_USAGE_VS_REQUESTS(
                        "avg(backendai_container_utilization{display_name=\"%s\", session_id=\"%s\", container_metric_name=\"cuda_util\", value_type=\"current\"})"),

        // GPU 노드 상세 - CPU 사용량 그래프 (배포 워크로드별)
        GPU_NODE_WORKLOAD_CPU_USAGE_GRAPH(
                        "sum by (session_id)(rate(backendai_container_utilization{container_metric_name=\"cpu_util\", value_type=\"current\", display_name=\"%s\"}[1m])) / 100"),

        // GPU 노드 상세 - 메모리 사용량 그래프 (배포 워크로드별)
        GPU_NODE_WORKLOAD_MEMORY_USAGE_GRAPH(
                        "sum by (session_id)(backendai_container_utilization{container_metric_name=\"mem\", value_type=\"current\", display_name=\"%s\"}) / 1024 / 1024 / 1024"),

        // GPU 노드 상세 - GPU 사용량 그래프 (배포 워크로드별)
        GPU_NODE_WORKLOAD_GPU_USAGE_GRAPH(
                        "avg by (session_id) (backendai_container_utilization{container_metric_name=\"cuda_util\",value_type=\"current\", display_name=\"%s\"}) / 100"),

        // ===========================================
        // GPU 노드 상세 - 세션별 Quota 그리드
        // ===========================================

        // 세션별 CPU 할당량 (capacity)
        GPU_NODE_SESSION_CPU_CAPACITY(
                        "sum by (user_id, session_id) (backendai_container_utilization{container_metric_name=\"cpu_util\", value_type=\"capacity\", display_name=\"%s\"}) / 100"),

        // 세션별 CPU 실제 사용량 (current)
        GPU_NODE_SESSION_CPU_USAGE(
                        "sum by (user_id, session_id) (rate(backendai_container_utilization{container_metric_name=\"cpu_util\", value_type=\"current\", display_name=\"%s\"}[%s])) / 100"),

        // 세션별 Memory 할당량 (capacity) - GB
        GPU_NODE_SESSION_MEMORY_CAPACITY(
                        "sum by (user_id, session_id) (backendai_container_utilization{container_metric_name=\"mem\", value_type=\"capacity\", display_name=\"%s\"}) / 1024 / 1024 / 1024"),

        // 세션별 Memory 실제 사용량 (current) - GB
        GPU_NODE_SESSION_MEMORY_USAGE(
                        "sum by (user_id, session_id) (backendai_container_utilization{container_metric_name=\"mem\", value_type=\"current\", display_name=\"%s\"}) / 1024 / 1024 / 1024"),

        // 세션별 GPU 할당량 (capacity) - cuda_mem capacity (GB)
        GPU_NODE_SESSION_GPU_CAPACITY(
                        "avg by (user_id, session_id) (backendai_container_utilization{container_metric_name=\"cuda_util\", value_type=\"capacity\", display_name=\"%s\"}) / 100"),

        // 세션별 GPU 실제 사용량 (current) - cuda_mem current (GB)
        GPU_NODE_SESSION_GPU_MEMORY_USAGE(
                        "avg by (user_id, session_id) (backendai_container_utilization{container_metric_name=\"cuda_util\", value_type=\"current\", display_name=\"%s\"}) / 100"),

        // 세션별 GPU 사용률 (current) - cuda_util (0-100%)
        GPU_NODE_SESSION_GPU_UTILIZATION(
                        "avg by (user_id, session_id) (backendai_container_utilization{container_metric_name=\"cuda_util\", value_type=\"current\", display_name=\"%s\"})"),

        // ========================================
        // Solution Detail 쿼리
        // ========================================

        /**
         * 솔루션 상세 - Pod 개수
         */
        SOLUTION_DETAIL_POD_COUNT("count(kube_pod_status_phase{namespace=\"%s\", phase=\"Running\"})"),

        /**
         * 솔루션 상세 - Pod 이름
         */
        SOLUTION_DETAIL_POD_NAME("kube_pod_status_phase{namespace=\"%s\", phase=\"Running\"}"),

        /**
         * 솔루션 상세 - CPU 사용량 그래프 (시계열, Pod별 그룹화)
         * 네임스페이스별 Pod의 CPU 사용량 추이를 시계열로 반환 (query_range)
         * Pod별로 그룹화되어 각 Pod의 시간대별 CPU 사용량을 확인 가능
         */
        SOLUTION_DETAIL_CPU_USAGE_GRAPH(
                        "sum(node_namespace_pod_container:container_cpu_usage_seconds_total:sum_irate{namespace=\"%s\", cluster=\"\"}) by (pod)"),
                        //"sum by (pod) (irate(container_cpu_usage_seconds_total{namespace=\"%s\", container!=\"\", container!=\"POD\"}[5m]))"

        /**
         * 솔루션 상세 - 메모리 사용량 그래프 (시계열, Pod별 그룹화)
         * 네임스페이스별 Pod의 메모리 사용량 추이를 시계열로 반환 (query_range)
         * Pod별로 그룹화되어 각 Pod의 시간대별 메모리 사용량을 확인 가능
         * POD 컨테이너 제외하고 실제 컨테이너의 메모리 사용량만 측정
         */
        SOLUTION_DETAIL_MEMORY_USAGE_GRAPH(
                "sum(container_memory_working_set_bytes{job=\"kubelet\", metrics_path=\"/metrics/cadvisor\", cluster=\"\", namespace=\"%s\", container!=\"\", image!=\"\"}) by (pod) / 1024 / 1024 / 1024"),
                //sum by (pod) (container_memory_working_set_bytes{namespace=\"%s\", container!=\"\", container!=\"POD\"}) / 1024 / 1024 / 1024

        /**
         * 솔루션 상세 - Pod별 CPU 요청량 (그리드)
         * 각 Pod의 CPU 요청량 (Requests)
         */
        SOLUTION_DETAIL_POD_CPU_REQUESTS(
                        "sum(kube_pod_container_resource_requests{resource=\"cpu\", namespace=\"%s\"}) by (pod)"),

        /**
         * 솔루션 상세 - Pod별 CPU 할당량 (그리드)
         * 각 Pod의 CPU 할당량 (Limits)
         */
        SOLUTION_DETAIL_POD_CPU_LIMITS(
                        "sum(kube_pod_container_resource_limits{resource=\"cpu\", namespace=\"%s\"}) by (pod)"),

        /**
         * 솔루션 상세 - Pod별 CPU 실제 사용량 (그리드)
         * 각 Pod의 CPU 실제 사용량
         * POD 컨테이너 제외하고 실제 컨테이너의 CPU 사용량만 측정
         * irate() 사용으로 순간 증가율 측정 (그래프와 동일)
         */
        SOLUTION_DETAIL_POD_CPU_USAGE(
                        "sum by (pod) (irate(container_cpu_usage_seconds_total{namespace=\"%s\", container!=\"\", container!=\"POD\"}[5m]))"),



        /**
         * 솔루션 상세 - Pod별 메모리 요청량 (그리드)
         * 각 Pod의 메모리 요청량 (Requests)
         */
        SOLUTION_DETAIL_POD_MEMORY_REQUESTS(
                        "sum(kube_pod_container_resource_requests{resource=\"memory\", namespace=\"%s\"}) by (pod)"),

        /**
         * 솔루션 상세 - Pod별 메모리 할당량 (그리드)
         * 각 Pod의 메모리 할당량 (Limits)
         */
        SOLUTION_DETAIL_POD_MEMORY_LIMITS(
                        "sum(kube_pod_container_resource_limits{resource=\"memory\", namespace=\"%s\"}) by (pod)"),

        /**
         * 솔루션 상세 - Pod별 메모리 실제 사용량 (그리드)
         * 각 Pod의 메모리 실제 사용량
         * POD 컨테이너 제외하고 실제 컨테이너의 메모리 사용량만 측정
         */
        SOLUTION_DETAIL_POD_MEMORY_USAGE(
                        "sum by (pod) (container_memory_working_set_bytes{namespace=\"%s\", container!=\"\", container!=\"POD\"})"),

        /**
         * 솔루션 상세 - Pod별 CPU 요청량 대비 사용률
         * 파라미터: namespace, podName, namespace, podName
         */
        // SOLUTION_DETAIL_POD_CPU_REQUEST_USAGE_RATE(
        //                 "sum(irate(container_cpu_usage_seconds_total{namespace=\"%s\", pod=\"%s\", container!=\"\", container!=\"POD\"}[5m])) / "
        //                                 +
        //                                 "sum(kube_pod_container_resource_requests{resource=\"cpu\", namespace=\"%s\", pod=\"%s\"}) * 100"),

        /**
         * 솔루션 상세 - Pod별 CPU 상한량 대비 사용률
         * 파라미터: namespace, podName, namespace, podName
         */
        // SOLUTION_DETAIL_POD_CPU_LIMIT_USAGE_RATE(
        //                 "sum(irate(container_cpu_usage_seconds_total{namespace=\"%s\", pod=\"%s\", container!=\"\", container!=\"POD\"}[5m])) / "
        //                                 +
        //                                 "sum(kube_pod_container_resource_limits{resource=\"cpu\", namespace=\"%s\", pod=\"%s\"}) * 100"),

        /**
         * 솔루션 상세 - Pod별 메모리 요청량 대비 사용률
         * 파라미터: namespace, podName, namespace, podName
         */
        // SOLUTION_DETAIL_POD_MEMORY_REQUEST_USAGE_RATE(
        //                 "sum(container_memory_working_set_bytes{namespace=\"%s\", pod=\"%s\", container!=\"\", container!=\"POD\"}) / "
        //                                 +
        //                                 "sum(kube_pod_container_resource_requests{resource=\"memory\", namespace=\"%s\", pod=\"%s\"}) * 100"),

        /**
         * 솔루션 상세 - Pod별 메모리 상한량 대비 사용률
         * 파라미터: namespace, podName, namespace, podName
         */
        // SOLUTION_DETAIL_POD_MEMORY_LIMIT_USAGE_RATE(
        //                 "sum(container_memory_working_set_bytes{namespace=\"%s\", pod=\"%s\", container!=\"\", container!=\"POD\"}) / "
        //                                 +
        //                                 "sum(kube_pod_container_resource_limits{resource=\"memory\", namespace=\"%s\", pod=\"%s\"}) * 100"),

        // ===========================================
        // 솔루션 상세 - 평균 사용률 (avg_over_time)
        // ===========================================

        /**
         * 솔루션 상세 - Pod별 CPU 요청량 대비 평균 사용률
         * 파라미터: namespace, podName, duration, namespace, podName
         */
        SOLUTION_DETAIL_POD_CPU_REQUEST_AVG_RATE(
                        "sum(irate(container_cpu_usage_seconds_total{namespace=\"%s\", pod=\"%s\", container!=\"\" }[%s])) / sum(kube_pod_container_resource_requests{resource=\"cpu\", namespace=\"%s\", pod=\"%s\"}) * 100"),


        /**
         * 솔루션 상세 - Pod별 CPU 상한량 대비 평균 사용률
         * 파라미터: namespace, podName, duration, namespace, podName
         */
        SOLUTION_DETAIL_POD_CPU_LIMIT_AVG_RATE(
                        "sum(irate(container_cpu_usage_seconds_total{namespace=\"%s\", pod=\"%s\", container!=\"\" }[%s])) / sum(kube_pod_container_resource_limits{resource=\"cpu\", namespace=\"%s\", pod=\"%s\"}) * 100"),

        /**
         * 솔루션 상세 - 네임스페이스 전체 메모리 요청량 대비 평균 사용률
         * 파라미터: namespace, namespace, duration
         */
        SOLUTION_DETAIL_NS_MEMORY_REQUEST_AVG_RATE(
                        "avg(avg_over_time((sum(container_memory_working_set_bytes{namespace=\"%s\", container!=\"\", container!=\"POD\"}) / "
                                        +
                                        "sum(kube_pod_container_resource_requests{resource=\"memory\", namespace=\"%s\"}) * 100)[%s:]))"),

        /**
         * 솔루션 상세 - Pod별 메모리 요청량 대비 평균 사용률
         * 파라미터: namespace, podName, namespace, podName, duration
         */
        SOLUTION_DETAIL_POD_MEMORY_REQUEST_AVG_RATE(
                        "avg(avg_over_time((sum(container_memory_working_set_bytes{namespace=\"%s\", pod=\"%s\", container!=\"\", container!=\"POD\"}) / "
                                        +
                                        "sum(kube_pod_container_resource_requests{resource=\"memory\", namespace=\"%s\", pod=\"%s\"}) * 100)[%s:]))"),

        /**
         * 솔루션 상세 - 네임스페이스 전체 메모리 상한량 대비 평균 사용률
         * 파라미터: namespace, namespace, duration
         */
        SOLUTION_DETAIL_NS_MEMORY_LIMIT_AVG_RATE(
                        "avg(avg_over_time((sum(container_memory_working_set_bytes{namespace=\"%s\", container!=\"\", container!=\"POD\"}) / "
                                        +
                                        "sum(kube_pod_container_resource_limits{resource=\"memory\", namespace=\"%s\"}) * 100)[%s:]))"),

        /**
         * 솔루션 상세 - Pod별 메모리 상한량 대비 평균 사용률
         * 파라미터: namespace, podName, namespace, podName, duration
         */
        SOLUTION_DETAIL_POD_MEMORY_LIMIT_AVG_RATE(
                        "avg(avg_over_time((sum(container_memory_working_set_bytes{namespace=\"%s\", pod=\"%s\", container!=\"\", container!=\"POD\"}) / "
                                        +
                                        "sum(kube_pod_container_resource_limits{resource=\"memory\", namespace=\"%s\", pod=\"%s\"}) * 100)[%s:]))");

        private final String query;

        ResrcMgmtQueryEnum(String query) {
                this.query = query;
        }

        public String getQuery() {
                return query;
        }

}