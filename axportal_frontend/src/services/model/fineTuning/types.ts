// Fine-tuning 관련 타입 정의

// =============================================================================
// 1. 데이터셋 관련 타입
// =============================================================================

export interface DatasetTag {
  name: string;
}

export interface Dataset {
  id: string;
  name: string;
  description: string;
  tags: DatasetTag[];
  type: string;
  createdAt?: string;
  updatedAt?: string;
}

// 데이터셋 API 응답 타입들
export interface DatasetApiResponse {
  content?: Dataset[];
  data?: Dataset[] | { content: Dataset[] };
  items?: Dataset[];
}

// 변환된 데이터셋 타입 (UI에서 사용)
export interface TransformedDataset {
  no: string;
  id: string;
  name: string;
  status: string;
  description: string;
  tags: string[];
  scope: string;
  type: string;
  createdDate: string;
  modifiedDate: string;
}

// =============================================================================
// 2. 리소스 관련 타입
// =============================================================================

// 클러스터 리소스 타입
export interface ClusterResource {
  id: string;
  name: string;
  description: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  node_resource?: NodeResource[];
}

export interface NodeResource {
  id?: string;
  name?: string;
  node_name?: string;
  cpu_quota?: number;
  cpu_used?: number;
  mem_quota?: number;
  mem_used?: number;
  gpu_quota?: number;
  gpu_used?: number;
}

// 태스크 정책 리소스 타입
export interface TaskPolicyResource {
  id?: string;
  name?: string;
  description?: string;
  task_policies?: TaskPolicy[];
  data?: {
    task_policies?: TaskPolicy[];
    data?: {
      task_policies?: TaskPolicy[];
    };
  };
  content?: {
    task_policies?: TaskPolicy[];
  };
}

export interface TaskPolicy {
  id?: string;
  size: string;
  cpu: number;
  memory: number;
  gpu: number;
  task_type: string;
  // 다양한 필드명 지원
  cpu_quota?: number;
  cpu_core?: number;
  mem_quota?: number;
  mem?: number;
  gpu_quota?: number;
  gpu_core?: number;
}

// =============================================================================
// 3. 파인튜닝 관련 타입
// =============================================================================

export interface FineTuningTraining {
  id: string;
  name: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
  description?: string;
  params?: string;
  resource?: {
    cpu_quota?: string;
    mem_quota?: string;
    gpu_quota?: string;
    gpu_type?: string;
    scaling_group?: string;
  };
  baseModelDetail?: {
    provider_name?: string;
    name?: string;
    description?: string;
    type?: string;
    serving_type?: string;
    id: string;
  };
  datasetDetails?: any[];
  datasetIds?: string[];
  metricDetails?: TrainingMetricRead[];
  publicStatus?: string; // 공개범위

  // 임시
  prev_status?: any;
  progress?: any;
  dataset_ids?: any;
  base_model_id?: any;
  envs?: any;
  project_id?: any;
  trainer_id?: any;
  task_id?: any;
  created_at?: any;
  updated_at?: any;
}

export interface TrainingMetricRead {
  /** 트레이닝 스텝 */
  step?: number;

  /** 손실값 */
  loss?: number;

  /** 커스텀 메트릭 (JSON 객체) */
  custom_metric?: Record<string, any>;

  /** 메트릭 고유 식별자 (UUID) */
  id?: string;
}

// =============================================================================
// 3-1. 파인튜닝 이벤트 관련 타입
// =============================================================================

export interface TrainingEventBase {
  /** 이벤트 발생 시간 */
  time?: string;

  /** 이벤트 로그 메시지 */
  log?: string;
}

export interface TrainingEventsRead {
  /** Training 이벤트 목록 (시간 순서로 정렬) */
  data?: TrainingEventBase[];

  /** 마지막 이벤트 식별자 (다음 폴링을 위한 커서) */
  last?: string;
}

export interface GetFineTuningTrainingEventsRequest {
  /** 파인튜닝 모델 ID */
  trainingId: string;

  /** 마지막 이벤트 식별자 (증분 조회용, 선택사항) */
  last?: string;

  /** 특정 시간 이후의 이벤트 조회 (선택사항) */
  after?: string;

  /** 조회할 이벤트 개수 제한 (선택사항) */
  limit?: number;
}

// =============================================================================
// 4. 페이지네이션 관련 타입
// =============================================================================

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    sort: any;
  };
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

// =============================================================================
// 5. API 요청/응답 타입들
// =============================================================================

// Fine-tuning API 요청 타입들
export interface GetFineTuningTrainingsRequest {
  page: number;
  size: number;
  status?: string;
  searchKeyword?: string;
  startDate?: string;
  endDate?: string;
  filter?: string;
  sort?: string;
  search?: string;
  queryKey?: string;
}

export interface GetFineTuningTrainingByIdRequest {
  id: string;
  isDataSet?: boolean;
  isMetric?: boolean;
}

export interface CreateFineTuningTrainingRequest {
  name: string;
  description: string;
  modelId: string;
  datasetIds: string[];
  // 기타 필요한 필드들...
}

export interface CreateFineTuningRequest {
  name: string;
  description?: string;
  status?: string;
  prev_status?: string;
  progress?: Record<string, any>;
  resource?: {
    cpu_quota?: string;
    mem_quota?: string;
    gpu_quota?: string;
    scaling_group?: string;
  };
  dataset_ids?: string[];
  base_model_id?: string;
  params?: string;
  envs?: Record<string, any>;
  project_id?: string;
  trainer_id?: string;
  scalingGroup?: string;
  agentList?: string;
}

export interface UpdateFineTuningRequest {
  id: string;
  name?: string;
  description?: string;
  status?: string;
  prev_status?: string;
  progress?: Record<string, any>;
  resource?: {
    cpu_quota?: string;
    mem_quota?: string;
    gpu_quota?: string;
  };
  dataset_ids?: string[];
  base_model_id?: string;
  params?: string;
  envs?: Record<string, any>;
  project_id?: string;
  trainer_id?: string;
}

export interface DeleteFineTuningTrainingRequest {
  id: string;
}

export interface UpdateFineTuningStatusRequest {
  id: string;
  status: string;
  scalingGroup?: string;
}

// =============================================================================
// 6. 유틸리티 타입들
// =============================================================================

// API 응답 상태 타입
export type ApiStatus = 'idle' | 'loading' | 'success' | 'error';

// 리소스 타입
export type ResourceType = 'cpu' | 'memory' | 'gpu';

// 파인튜닝 상태 타입
export type FineTuningStatus = 'pending' | 'running' | 'completed' | 'failed' | 'cancelled';
