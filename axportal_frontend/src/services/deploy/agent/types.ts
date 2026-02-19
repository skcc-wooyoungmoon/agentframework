import { type DeployData } from '@/stores/deploy/types';

export type GetAgentAppByIdRequest = {
  appId: String;
};

export type GetAgentAppDeployListRequest = GetAgentAppByIdRequest & {
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
};

export type GetAgentDeployByIdRequest = { deployId: String };

export type GetAgentAppListRequest = {
  targetType: String;
  page: Number;
  size: Number;
  sort: String;
  filter: String;
  search: String;
};

export type Deployment = {
  description: string;
  servingType: string;
  imageTag: string;
  inputKeys: InputKey[];
  outputType: string;
  deployedDt: string;
  createdBy: string;
  servingId: string;
  targetId: string;
  id: string;
  targetType: string;
  version: number;
  status: string;
  outputKeys: OutputKey[];
  deploymentConfigPath: string;
};

export type GetAgentAppResponse = {
  id: string;
  name: string;
  builderName: string;
  description: string;
  targetId: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
  deployments: Deployment[];
  deploymentVersion: number;
  deploymentStatus: string;
  servingType: string;
  inputKeys: InputKey[];
  outputKeys: OutputKey[];
  outputType: string;
  isMigration: boolean;
  publicStatus: '전체공유' | '내부공유';
  payload: any;
};

export type GetAgentAppByIdResponse = GetAgentAppResponse;
export type GetAgentAppListResponse = GetAgentAppResponse[];

export type GetAgnetAppDeployResponse = {
  appId: string;
  description: string;
  imageTag: string;
  inputKeys: InputKey[];
  outputType: string;
  deployedDt: string;
  createdBy: string;
  servingId: string;
  targetId: string;
  id: string;
  targetType: string;
  version: number;
  status: string;
  outputKeys: OutputKey[];
  deploymentConfigPath: string;
  deleteFlag: boolean;
  endpoint: string;
  isMigration: boolean;
  servingType: string;
};

export type GetAgentDeployByIdResponse = GetAgnetAppDeployResponse;
export type GetAgentAppDeployListByIdResponse = GetAgnetAppDeployResponse[];

export type InputKey = {
  name: string;
  required: boolean;
  keytableId: string;
  description?: string;
  fixedValue?: string;
};

export type OutputKey = {
  name: string;
  keytableId: string;
};

export type UpdateAgentAppRequest = {
  name: string;
  description?: string;
};

// export type AppApiKeyInfo = {
//   apiKey: string;
//   keyName: string;
//   description: string;
//   createdAt: string;
//   expiresAt: string;
//   isActive: boolean;
// };

export type GetAppApiKeyResponse = {
  apiKeys: string[];
};

export const ChatType = {
  HUMAN: 'human',
  AI: 'ai',
};

export type MessageFormat = {
  id: string;
  content: string;
  time: string;
  // @ts-ignore
  type: ChatType.AI | ChatType.HUMAN;
  regen?: boolean;
  elapsedTime?: number; // milliseconds
  regenerations?: Array<{
    content: string;
    time: string;
    elapsedTime?: number;
  }>;
};

export type GetStreamAgentDeployRequest = {
  routerPath?: string;
  authorization: string;
  StreamReq?: StreamRequest;
};

export type StreamRequest = {
  config: object;
  input: object;
  kwargs: object;
};

export type GetStreamAgentDeployResponse = string; // 스트리밍 응답은 string으로 받음

export type GetClusterResourcesRequest = {
  nodeType?: string;
};

export type NodeResource = {
  node_name: string;
  node_label: string[];
  cpu_quota: number;
  mem_quota: number;
  gpu_quota: number;
  cpu_used: number;
  mem_used: number;
  gpu_used: number;
  cpu_usable: number;
  mem_usable: number;
  gpu_usable: number;
};

export type NamespaceResource = {
  cpu_quota: number;
  cpu_used: number;
  cpu_usable: number;
  mem_quota: number;
  mem_used: number;
  mem_usable: number;
  gpu_quota: number;
  gpu_used: number;
  gpu_usable: number;
};

export type TaskPolicy = {
  small: string | null;
  medium: string | null;
  large: string | null;
  max: string | null;
};

export type TaskQuota = {
  quota: number;
  used: number;
};

export type GetClusterResourcesResponse = {
  node_resource: NodeResource[];
  namespace_resource: NamespaceResource;
  task_policy: TaskPolicy;
  task_quota: TaskQuota;
};

export type CreateAgentAppRequest = DeployData;

export type GetAgentServingRequest = {
  servingId: string;
};

export type GetAgentServingResponse = {
  agentServingId: string;
  agentServingName: string;
  agentId: string;
  description: string;
  status: string;
  endpoint: string;
  chatEndpointUrl: string;
  isCustom: string;
  agentServingParams: string;
  cpuRequest: number;
  cpuLimit: number;
  gpuRequest: number;
  gpuLimit: number;
  memRequest: number;
  memLimit: number;
  gpuType: string;
  currentReplicas: string;
  minReplicas: string;
  maxReplicas: string;
  autoscalingClass: string;
  autoscalingMetric: string;
  target: string;
  safetyFilterInput: false;
  safetyFilterOutput: false;
  dataMaskingInput: string;
  dataMaskingOutput: string;
  activeSessions: string;
  createdAt: string;
  updatedAt: string;
  isvcName: string;
  errorMessage: string;
  createdBy: string;
  updatedBy: string;
  agentAppImageRegistry: string;
  appConfigFilePath: string;
  kserveYaml: string;
  isDeleted: false;
  servingType: string;
  projectId: string;
  modelList: string[];
  sharedBackendId: string;
  namespace: string;
  appId: string;
  appVersion: number;
  deploymentName: string;
  agentParams: string;
  agentAppImage: string;
  isMigration: boolean;
};

export type GetAgentSysLogRequest = {
  source?: string[];
  size?: number;
  sort?: Array<Record<string, object>>;
  query?: object;
  from?: number;
};

export type GetAgentSysLogResponse = {
  log: string;
};

export type GetAgentDeployInfoRequest = {
  agentId: string;
};

export type GetAgentDeployInfoResponse = {
  agentId: string;
  namespace: string;
  isvcName: string;
  status: string;
  deployId: string;
  deployDt: string;
};
