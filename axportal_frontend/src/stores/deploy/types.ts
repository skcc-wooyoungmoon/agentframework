export type ServingType = 'shared' | 'standalone';
export type TargetType = 'agent_graph' | 'external_graph';

export interface DeployData {
  cpuLimit: number;
  cpuRequest: number;
  description: string;
  gpuLimit: number;
  gpuRequest: number;
  maxReplicas: number;
  memLimit: number;
  memRequest: number;
  minReplicas: number;
  name: string;
  servingType: ServingType;
  targetId: string;
  targetType: TargetType;
  versionDescription: string;
  workersPerCore: number;
  safetyFilterOptions?: SafetyFilterOptions;
}

export interface SafetyFilterOptions {
  safety_filter_input?: boolean;
  safety_filter_output?: boolean;
  safety_filter_input_groups?: string[];
  safety_filter_output_groups?: string[];
}

export type MigDeployCategory = 'SAFETY_FILTER' | 'SERVING_MODEL' | 'KNOWLEDGE' | 'VECTOR_DB' | 'AGENT_APP' | 'GUARDRAILS' | 'PROJECT';
export type MigDeployCategoryName = '세이프티 필터' | '모델' | '지식' | '벡터 DB' | '에이전트' | '가드레일' | '프로젝트';

// 카테고리와 카테고리명 매핑
export const MIG_DEPLOY_CATEGORY_MAP: Record<MigDeployCategory, MigDeployCategoryName> = {
  SAFETY_FILTER: '세이프티 필터',
  SERVING_MODEL: '모델',
  KNOWLEDGE: '지식',
  VECTOR_DB: '벡터 DB',
  AGENT_APP: '에이전트',
  GUARDRAILS: '가드레일',
  PROJECT: '프로젝트',
};

export interface FieldData {
  id: string;
  fieldName: string;
  devValue: string;
  prodValue: string;
}

export interface MigDeployData {
  prjSeq: string;
  prjNm: string;
  uuidList: string[];
  category: MigDeployCategory;
  categoryName: string;
  name: string;
  resourceFieldsState?: Record<string, FieldData[]>; // Step3에서 입력한 prod 값 저장
}
