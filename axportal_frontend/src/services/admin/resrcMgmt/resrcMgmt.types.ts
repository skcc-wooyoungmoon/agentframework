// 리소스 관리 API 타입 정의

/**
 * 리소스 타입
 */
export const ResourceType = {
  CPU: 'CPU',
  MEMORY: 'MEMORY',
  GPU: 'GPU',
} as const;

export type ResourceType = (typeof ResourceType)[keyof typeof ResourceType];

/**
 * 배포 타입
 */
export const DeploymentType = {
  AGENT: 'AGENT',
  MODEL: 'MODEL',
} as const;

export type DeploymentType = (typeof DeploymentType)[keyof typeof DeploymentType];

/**
 * 노드 상태
 */
export const NodeStatus = {
  READY: 'READY',
  NOT_READY: 'NOT_READY',
  UNKNOWN: 'UNKNOWN',
} as const;

export type NodeStatus = (typeof NodeStatus)[keyof typeof NodeStatus];

// ================================
// 요청 타입
// ================================

/**
 * 포탈 자원 현황 조회 요청 타입
 *
 * @param searchType - 검색 타입 (userName, BankNum, userId) (선택적)
 * @param searchValue - 검색어 (선택적)
 */
export type GetPortalResourcesRequest = {
  searchType?: string;
  searchValue?: string;
};

/**
 * GPU 노드별 자원 현황 조회 요청 타입
 *
 * @param dateType - 조회 기간 타입 (선택적)
 * @param startDate - 조회 시작일 (선택적)
 * @param endDate - 조회 종료일 (선택적)
 * @param nodeStatus - 노드 상태 (READY, NOT_READY, UNKNOWN) (선택적)
 */
export type GetGpuNodeResourcesRequest = {
  dateType?: string;
  startDate?: string;
  endDate?: string;
  nodeStatus?: string;
};

/**
 * 솔루션 자원 현황 조회 요청 타입
 *
 * @param dateType - 조회 기간 타입 (선택적)
 * @param startDate - 조회 시작일 (선택적)
 * @param endDate - 조회 종료일 (선택적)
 * @param solutionType - 솔루션 타입 (선택적)
 */
export type GetSolutionResourcesRequest = {
  dateType?: string;
  startDate?: string;
  endDate?: string;
  solutionType?: string;
};

/**
 * 에이전트 Pods 조회 요청 타입
 *
 * @param namespace - 네임스페이스 (선택적)
 * @param status - Pod 상태 (선택적)
 * @param agentType - 에이전트 타입 (선택적)
 */
export type GetAgentPodsRequest = {
  namespace?: string;
  status?: string;
  agentType?: string;
};

/**
 * 모델 Pods 조회 요청 타입
 *
 * @param namespace - 네임스페이스 (선택적)
 * @param status - Pod 상태 (선택적)
 * @param modelType - 모델 타입 (선택적)
 */
export type GetModelPodsRequest = {
  namespace?: string;
  status?: string;
  modelType?: string;
};

/**
 * 솔루션 상세 조회 요청 타입
 *
 * @param fromDate - 조회 시작일
 * @param toDate - 조회 종료일
 * @param nameSpace - 네임스페이스
 * @param podName - Pod명 (선택적)
 */
export type GetSolutionDetailRequest = {
  fromDate: string;
  toDate: string;
  nameSpace: string;
  podName?: string;
};

/**
 * GPU 노드 상세 조회 요청 타입
 *
 * @param nodeName - 노드명
 * @param fromDate - 조회 시작일
 * @param toDate - 조회 종료일
 */
export type GetGpuNodeDetailRequest = {
  nodeName: string;
  fromDate: string;
  toDate: string;
  workloadName?: string; // 워크로드명 (선택적)
};

// ================================
// 응답 타입
// ================================

/**
 * 리소스 사용량 정보
 *
 * @param total - 전체 할당량
 * @param used - 사용량
 * @param available - 사용 가능량
 * @param usagePercentage - 사용률 (%)
 */
export type ResourceUsage = {
  total: number;
  used: number;
  available: number;
  usagePercentage: number;
};

/**
 * Model Session 타입
 */
export type ModelSession = {
  modelName: string;
  projectId: string;
  servingId: string;
  sessionId: string;
  status: string;
  cpu_limit: number;
  cpu_request: number;
  cpu_usage: number;
  cpu_utilization: number;
  gpu_limit: number;
  gpu_request: number;
  gpu_usage: number;
  gpu_utilization: number;
  memory_limit: number;
  memory_request: number;
  memory_usage: number;
  memory_utilization: number | null;
  [key: string]: any;
};

/**
 * 포탈 자원 현황 타입
 *
 * @param components - 컴포넌트별 자원 현황 (Agent, Model)
 * @param ideResources - IDE 리소스 목록
 */
export type PortalResourceType = {
  components: {
    Agent: {
      memory_usage: number;
      memory_request: number;
      cpu_usage: number;
      cpu_request: number;
      memory_limit?: number;
      cpu_limit?: number;
    };
    Model: {
      memory_usage: number;
      memory_request: number;
      cpu_usage: number;
      cpu_request: number;
      gpu_usage: number;
      gpu_request: number;
      memory_limit: number;
      cpu_limit: number;
      gpu_limit: number;
      sessions: ModelSession[];
    };
  };
  ideResources?: Array<{
    ideStatusId?: number;
    userId?: string;
    username?: string;
    imageType?: 'VSCODE' | 'JUPYTER';
    imageName?: string;
    dwAccountId?: string;
    cpu?: number;
    memory?: number;
    expireAt?: string;
  }>;
};

/**
 * GPU 노드 자원 현황 타입
 *
 * @param data - 응답 데이터
 * @param data.data - 실제 노드 데이터
 * @param data.data.nodes - GPU 노드 목록 (노드명을 키로 하는 객체)
 * @param message - 응답 메시지
 * @param path - API 경로
 * @param success - 성공 여부
 * @param timestamp - 응답 시간
 */
export type GpuNodeResourceType = {
  nodes: {
    [nodeName: string]: {
      display_name?: string;
      service_group?: string;
      cpu_limit: number;
      cpu_request: number;
      cpu_usage: number;
      gpu_limit: number;
      gpu_request: number;
      gpu_usage: number;
      memory_limit: number;
      memory_request: number;
      memory_usage: number;
    };
  };
};

/**
 * 솔루션 자원 현황 타입
 *
 * @param solutionList - 솔루션 목록 (배열)
 * @param message - 응답 메시지
 */
export type SolutionResourceType = {
  solutionList: Array<{
    id: string;
    name: string;
    namespaces: string[];
    cpu_limit: number;
    cpu_request: number;
    cpu_usage: number;
    memory_limit: number;
    memory_request: number;
    memory_usage: number;
  }>;
  message?: string;
};

// 포탈 자원 현황 조회 응답 타입
export type GetPortalResourcesResponse = PortalResourceType;

// GPU 노드별 자원 현황 조회 응답 타입
export type GetGpuNodeResourcesResponse = GpuNodeResourceType;

// 솔루션 자원 현황 조회 응답 타입
export type GetSolutionResourcesResponse = SolutionResourceType;

/**
 * 에이전트 Pod 정보 타입
 *
 * @param podName - Pod 이름
 * @param namespace - 네임스페이스
 * @param status - Pod 상태
 * @param agentType - 에이전트 타입
 * @param cpuUsage - CPU 사용량
 * @param memoryUsage - 메모리 사용량
 * @param gpuUsage - GPU 사용량 (선택적)
 * @param createdAt - 생성 시간
 * @param lastUpdated - 마지막 업데이트 시간
 */
export type AgentPodType = {
  podName: string;
  namespace: string;
  status: string;
  agentType: string;
  cpuUsage: ResourceUsage;
  memoryUsage: ResourceUsage;
  gpuUsage?: ResourceUsage;
  createdAt: string;
  lastUpdated: string;
};

/**
 * 에이전트 Pods 조회 응답 타입
 *
 * @param pods - 에이전트 Pod 목록 (Pod 이름을 키로 하는 객체)
 */
export type GetAgentPodsResponse = {
  pods: {
    [podName: string]: {
      cpu_request: number;
      cpu_usage: string;
      memory_request: number;
      memory_usage: string;
    };
  };
};

/**
 * 모델 Pods 조회 응답 타입
 *
 * @param sessions - 모델 세션 목록
 */
export type GetModelPodsResponse = {
  sessions: ModelSession[];
};

/**
 * 솔루션 상세 조회 응답 타입
 *
 * @param solutionName - 솔루션명
 * @param namespace - 네임스페이스
 * @param podCount - Pod 개수
 * @param podNames - Pod 이름 목록
 * @param pods - Pod 목록
 * @param cpuUsage - CPU 사용량 정보
 * @param memoryUsage - 메모리 사용량 정보
 * @param status - 상태
 */
export type GetSolutionDetailResponse = {
  solutionName: string;
  namespace: string;
  podCount: number;
  podNames: Array<{ pod: string }>;
  pods: {
    [podName: string]: {
      cpu_request: number;
      cpu_usage: string;
      memory_request: number;
      memory_usage: string;
    };
  };
  cpuUsage: ResourceUsage;
  memoryUsage: ResourceUsage;
  status: string;
  usageRates?: {
    cpuRequestUsageRate: number;
    cpuLimitUsageRate: number;
    memoryRequestUsageRate: number;
    memoryLimitUsageRate: number;
  };
  cpuGraph?: {
    [podName: string]: Array<[number, string]>;
  };
  memoryGraph?: {
    [podName: string]: Array<[number, string]>;
  };
  podCpuGrid?: Array<{
    cpuLimitUsageRate: number;
    cpuLimits: number;
    cpuRequestUsageRate: number;
    cpuRequests: number;
    cpuUsage: number;
    podName: string;
  }>;
  podMemoryGrid?: Array<{
    memoryLimitUsageRate: number;
    memoryLimits: number;
    memoryRequestUsageRate: number;
    memoryRequests: number;
    memoryUsage: number;
    podName: string;
  }>;

  fromDate?: string;
  toDate?: string;
};

/**
 * GPU 노드 상세 조회 응답 타입
 *
 * @param nodeName - 노드명
 * @param displayName - 표시 이름
 * @param serviceGroup - 서비스 그룹
 * @param status - 상태
 * @param workload_count - 배포 워크로드 수
 * @param usageRates - 사용률 정보
 * @param cpuGraph - CPU 그래프 데이터
 * @param memoryGraph - 메모리 그래프 데이터
 * @param gpuGraph - GPU 그래프 데이터
 * @param workloadCpuGrid - 워크로드별 CPU 그리드 데이터
 * @param workloadMemoryGrid - 워크로드별 메모리 그리드 데이터
 * @param workloadGpuGrid - 워크로드별 GPU 그리드 데이터
 */
export type GetGpuNodeDetailResponse = {
  nodeName: string;
  displayName?: string;
  serviceGroup?: string;
  status: string;
  workload_count?: number;
  workloads?: string[];
  usageRates?: {
    cpu_allocation_usage_rate: number;
    cpu_limit_usage_rate: number;
    cpu_usage_vs_limits: number;
    cpu_usage_vs_requests: number;
    gpu_allocation_usage_rate: number;
    gpu_limit_usage_rate: number;
    gpu_usage_vs_limits: number;
    gpu_usage_vs_requests: number;
    memory_allocation_usage_rate: number;
    memory_limit_usage_rate: number;
    memory_usage_vs_limits: number;
    memory_usage_vs_requests: number;
  };
  workloadCpuGraph?: {
    [workloadName: string]: Array<[number, string]>;
  };
  workloadMemoryGraph?: {
    [workloadName: string]: Array<[number, string]>;
  };
  workloadGpuGraph?: {
    [workloadName: string]: Array<[number, string]>;
  };
  workloadCpuQuota?: Array<{
    workloadName: string;
    cpuLimit: number;
    cpuRequest: number;
    cpuUsage: number;
    requestUsageRate: number;
    limitUsageRate: number;
  }>;
  workloadMemoryQuota?: Array<{
    workloadName: string;
    memoryLimit: number;
    memoryRequest: number;
    memoryUsage: number;
    requestUsageRate: number;
    limitUsageRate: number;
  }>;
  workloadGpuQuota?: Array<{
    workloadName: string;
    gpuLimit: number;
    gpuRequest: number;
    gpuUsage: number;
    requestUsageRate: number;
    limitUsageRate: number;
  }>;
  sessionCpuQuotaGrid?: Array<{
    user_id: string;
    session_id: string;
    allocation: number;
    request: number;
    usage: number;
    request_usage_rate: number;
    allocation_usage_rate: number;
  }>;
  sessionMemoryQuotaGrid?: Array<{
    user_id: string;
    session_id: string;
    allocation: number;
    request: number;
    usage: number;
    request_usage_rate: number;
    allocation_usage_rate: number;
  }>;
  sessionGpuQuotaGrid?: Array<{
    user_id: string;
    session_id: string;
    allocation: number;
    request: number;
    usage: number;
    request_usage_rate: number;
    allocation_usage_rate: number;
  }>;
  fromDate?: string;
  toDate?: string;
};
