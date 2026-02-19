import type { SafetyFilter } from '@/services/deploy/safetyFilter';

export type GetModelDeployListRequest = {
  page: number;
  size: number;
  sort?: string;
  filter?: string;
  search?: string;
  queryKey?: string;
  deployModelNames?: string[];
};

export type DeleteModelDeployRequest = {
  servingId: string;
  servingType: string;
};

export type CreateModelDeployRequest = {
  name: string;
  description?: string;
  modelId: string;
  versionId?: string;
  isCustom?: boolean;
  servingParams?: any;
  cpuRequest?: number;
  cpuLimit?: number;
  gpuRequest?: number;
  gpuLimit?: number;
  memRequest?: number;
  memLimit?: number;
  minReplicas?: number;
  maxReplicas?: number;
  autoscalingClass?: string;
  autoscalingMetric?: string;
  target?: number;
  gpuType?: string;
  safetyFilterInput?: boolean;
  safetyFilterOutput?: boolean;
  safetyFilterInputGroups?: string[];
  safetyFilterOutputGroups?: string[];
  dataMaskingInput?: boolean;
  dataMaskingOutput?: boolean;
  servingMode?: string;
  servingOperator?: string;
  endpoint?: string;
  externalEndpoint?: string;
  runtime?: string;
  runtimeImage?: string;
  inferenceParam?: string;
  quantization?: Record<string, any>;
};

export type GetModelDeployResponse = {
  servingId: string;
  name: string;
  description: string;
  kserveYaml: string;
  isvcName: string;
  projectId: string;
  namespace: string;
  status: string;
  modelId: string;
  versionId: string;
  servingParams: any;
  errorMessage: string;
  cpuRequest: number;
  cpuLimit: number;
  gpuRequest: number;
  gpuLimit: number;
  memRequest: number;
  memLimit: number;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
  isDeleted: boolean;
  safetyFilterInput: boolean;
  safetyFilterOutput: boolean;
  safetyFilterInputGroups: string[];
  safetyFilterOutputGroups: string[];
  dataMaskingInput: boolean;
  dataMaskingOutput: boolean;
  minReplicas: number;
  maxReplicas: number;
  autoscalingClass: string;
  autoscalingMetric: string;
  target: number;
  modelName: string;
  displayName: string;
  modelDescription: string;
  type: string;
  servingType: 'self_hosting' | 'serverless';
  isPrivate: boolean;
  isValid: boolean;
  inferenceParam: string;
  quantization: Record<string, any>;
  providerName: string;
  modelVersion: string;
  path: string;
  versionPath: string;
  fineTuningId: string;
  versionIsValid: boolean;
  versionIsDeleted: boolean;
  gpuType: string;
  isCustom: boolean;
  servingMode: string;
  servingOperator: string;
  endpoint: string;
  externalEndpoint: string;
  runtime: string;
  runtimeImage: string;
  production?: boolean;
  publicStatus?: string;
  guardrailApplied?: string;
  envs?: Record<string, any>;
};

export type PutModelDeployRequest = {
  description: string;
  servingParams: string;
  servingId: string;
  isCustom: boolean;
  cpuRequest: number;
  cpuLimit: number;
  gpuRequest: number;
  gpuLimit: number;
  memRequest: number;
  memLimit: number;
  gpuType: string;
  safetyFilterInput: boolean;
  safetyFilterOutput: boolean;
  safetyFilterInputGroups?: string[];
  safetyFilterOutputGroups?: string[];
  dataMaskingInput: boolean;
  dataMaskingOutput: boolean;
  minReplicas: number;
  maxReplicas: number;
  autoscalingClass: string;
  autoscalingMetric: string;
  target: number;
  servingMode?: string;
  servingOperator?: string;
  endpoint?: string;
  externalEndpoint?: string;
  runtime?: string;
  runtimeImage?: string;
  inferenceParam?: string;
  quantization?: Record<string, any>;
  envs?: Record<string, any>;
};

// ============================================================================
// Resource Management Types
// ============================================================================

export type GetTaskResourceRequest = {
  taskType: string;
  projectId?: string;
};

export type NodeResourceInfo = {
  nodeName: string;
  nodeLabel: string[];
  cpuQuota: number;
  memQuota: number;
  gpuQuota: number;
  cpuUsed: number;
  memUsed: number;
  gpuUsed: number;
  cpuUsable: number;
  memUsable: number;
  gpuUsable: number;
};

export type NamespaceResourceInfo = {
  cpuQuota: number;
  memQuota: number;
  gpuQuota: number;
  cpuUsed: number;
  memUsed: number;
  gpuUsed: number;
  cpuUsable: number;
  memUsable: number;
  gpuUsable: number;
};

export type ResourceSpecInfo = {
  cpuQuota: number;
  memQuota: number;
  gpuQuota: number;
};

export type TaskPolicyInfo = {
  small: ResourceSpecInfo;
  medium: ResourceSpecInfo;
  large: ResourceSpecInfo;
  max: ResourceSpecInfo;
};

export type TaskQuotaInfo = {
  quota: number;
  used: number;
};

export type GetTaskResourceResponse = {
  nodeResource: NodeResourceInfo[];
  namespaceResource: NamespaceResourceInfo;
  taskPolicy: TaskPolicyInfo;
  taskQuota: TaskQuotaInfo;
};

// ============================================================================
// Deploy Model Step Types
// ============================================================================

export type InfoInputDataType = {
  name: string;
  description: string;
  selectedFrame: string;
  selectedFrameVer: string;
  advancedChecked: boolean;
  advancedValue: any;
  envs?: Record<string, any>;
  inputFilter: SafetyFilter[];
  outputFilter: SafetyFilter[];
};

export type CreateBackendAiModelDeployRequest = {
  name: string;
  description?: string;
  modelId: string;
  versionId?: string;
  runtime: 'vllm' | 'sglang';
  runtimeImage: string;
  servingMode: 'SINGLE_NODE' | 'MULTI_NODE';
  servingParams?: any;
  cpuRequest: number;
  gpuRequest: number;
  memRequest: number;
  minReplicas: number;
  safetyFilterInput: boolean;
  safetyFilterOutput: boolean;
  dataMaskingInput: boolean;
  dataMaskingOutput: boolean;
  safetyFilterInputGroups?: string[];
  safetyFilterOutputGroups?: string[];
  resourceGroup?: string;
  policy?: any[];
  envs?: Record<string, any>;
};

export type CreateBackendAiModelDeployResponse = {
  servingId: string;
  name: string;
  description: string;
  isvcName: string;
  projectId: string;
  status: string;
  modelId: string;
  versionId: string;
  runtime: string;
  runtimeImage: string;
  servingMode: string;
  servingOperator: string;
  servingParams: any;
  cpuRequest: number;
  gpuRequest: number;
  memRequest: number;
  minReplicas: number;
  safetyFilterInput: boolean;
  safetyFilterOutput: boolean;
  dataMaskingInput: boolean;
  dataMaskingOutput: boolean;
  safetyFilterInputGroups: string[];
  safetyFilterOutputGroups: string[];
  policy: any[];
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
  isDeleted: boolean;
};

// ============================================================================
// System Log Types
// ============================================================================

export type GetSessionLogResponse = {
  result: {
    logs: string;
  };
};

// ============================================================================
// Endpoint Info Types
// ============================================================================

export type GetEndpointResponse = {
  endpoint?: {
    endpointId: string;
    name: string;
    replicas: number;
    status: string;
    imageObject?: {
      registry: string;
      project: string;
      baseImageName: string;
      tag: string;
      name: string;
    };
    modelDefinitionPath?: string;
    url?: string;
    openToPublic?: boolean;
    createdUser?: string;
    createdAt?: string;
    runtimeVariant?: {
      name: string;
    };
    routings?: Array<{
      routingId: string;
      session: string;
      status: string;
      trafficRatio: number;
    }>;
  };
  errors?: Array<{
    message: string;
    locations?: Array<{
      line: number;
      column: number;
    }>;
    path?: string[];
  }>;
};

// ============================================================================
// Inference Performance Types
// ============================================================================

/**
 * 추론 성능 조회 요청 파라미터
 */
export type GetInferencePerformanceRequest = {
  servingId?: string;
  modelName?: string;
  startDate: string; // ISO 형식
  endDate: string; // ISO 형식
};

/**
 * 시간 시리즈 데이터 포인트
 */
export type TimeSeriesDataPoint = {
  /**
   * 타임스탬프 (Unix timestamp, 초 단위)
   */
  timestamp: number;
  /**
   * 값 (초 단위, 필요시 ms로 변환)
   */
  value: number;
};

/**
 * Time To First Token 시계열 데이터
 */
export type TimeToFirstTokenDistribution = {
  /**
   * 시간 시리즈 데이터 리스트
   * x축: timestamp (시간)
   * y축: value (ms 단위)
   */
  timeSeries: TimeSeriesDataPoint[];
};

/**
 * Time Per Output Token 시계열 데이터
 */
export type TimePerOutputTokenDistribution = {
  /**
   * 시간 시리즈 데이터 리스트
   * x축: timestamp (시간)
   * y축: value (ms 단위)
   */
  timeSeries: TimeSeriesDataPoint[];
};

/**
 * End-to-End Request Latency 시계열 데이터
 */
export type EndToEndLatencyDistribution = {
  /**
   * 시간 시리즈 데이터 리스트
   * x축: timestamp (시간)
   * y축: value (초 단위)
   */
  timeSeries: TimeSeriesDataPoint[];
};

/**
 * 추론 성능 조회 응답
 */
export type GetInferencePerformanceResponse = {
  servingId: string;
  timeToFirstToken: TimeToFirstTokenDistribution;
  timePerOutputToken: TimePerOutputTokenDistribution;
  endToEndLatency: EndToEndLatencyDistribution;
};

// ============================================================================
// Model Deploy Resource Info Types
// ============================================================================

/**
 * 모델 배포 자원 현황 조회 응답
 * servingId를 통해 모델 배포의 자원 현황(CPU, Memory, GPU)을 조회합니다.
 */
export type GetModelDeployResourceInfoResponse = {
  sessionId: string;
  modelName: string;
  servingId: string;
  status: string;
  replicas: string;
  projectId: string;
  projectName: string;
  // CPU 자원 (Core 단위)
  cpuUsage: number | null;
  cpuUtilization: number | null;
  cpuRequest: number | null;
  cpuLimit: number | null;
  // Memory 자원 (GiB 단위)
  memoryUsage: number | null;
  memoryUtilization: number | null;
  memoryRequest: number | null;
  memoryLimit: number | null;
  // GPU 자원 (MiB 단위)
  gpuUsage: number | null;
  gpuUtilization: number | null;
  gpuRequest: number | null;
  gpuLimit: number | null;
};

// ============================================================================
// Docker Image URL Types
// ============================================================================

/**
 * 도커 이미지 URL 조회 요청 파라미터
 */
export type GetDockerImgUrlRequest = {
  sysUV: 'vLLM' | 'SGLang' ;
};

/**
 * 도커 이미지 URL 조회 응답
 */
export type GetDockerImgUrlResponse = {
  seqNo: number;
  sysUV: string;
  imgUrl: string;
  delYn: number | null;
  fstCreatedAt: string | null;
  createdBy: string | null;
  lstUpdatedAt: string | null;
  updatedBy: string | null;
};
