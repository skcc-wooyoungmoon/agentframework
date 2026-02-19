import { MODEL_GARDEN_STATUS_TYPE } from '@/constants/model/garden.constants';

export type CreateModelGardenRequest = {
  // 필수 입력 필드들만 포함
  artifact_id?: string;
  revision_id?: string;
  name?: string;
  description?: string;
  size?: string;
  param_size?: number;
  serving_type?: string;
  version?: string;
  provider?: string;
  providerId?: string;
  type?: string;
  license?: string;
  readme?: string;
  tags?: string;
  langauges?: string;
  url?: string;
  identifier?: string;

  statusNm?: keyof typeof MODEL_GARDEN_STATUS_TYPE;
};

export interface ModelGardenInfo {
  id: string;
  artifact_id: string;
  revision_id: string;
  name: string;
  description: string;
  size: string;
  param_size: string;
  serving_type: string;
  version: string;
  provider: string;
  providerId: string;
  type: string;
  license: string;
  readme: string;
  tags: string;
  langauges: string;
  url: string;
  identifier: string;

  statusNm: keyof typeof MODEL_GARDEN_STATUS_TYPE | string;
  doipAt: string;
  doipMn: string;
  chkAt: string;
  chkMn: string;

  created_at: string;
  updated_at: string;
  created_by: string;
  updated_by: string;
  deleted: string;
}

export type GetModelGardenRequest = {
  page: number;
  size: number;
  dplyTyp: 'self-hosting' | 'serverless';
  search?: string;
  status?: string;
  type?: string;
};

export type GetModelGardenResponse = ModelGardenInfo;

export type UpdateModelGardenRequest = {
  id: string;
  name?: string;
  description?: string;
  type?: string;
  provider?: string;
  providerId?: string;
  param_size?: string;
  url?: string;
  identifier?: string;

  tags?: string;
  langauges?: string;

  statusNm?: keyof typeof MODEL_GARDEN_STATUS_TYPE;
};

export type GetModelGardenAvailableRequest = {
  search?: string;
};

export type GetModelGardenAvailableResponse = {
  artifacts: ModelGardenArtifactInfo[];
};

export type ModelGardenArtifactInfo = {
  id: string;
  name: string;
  type: string;
  description: string;
  registryId: string;
  sourceRegistryId: string;
  registryType: string;
  sourceRegistryType: string;
  scannedAt: string;
  updatedAt: string;
  readonly: boolean;
  revision_id: string;
  version: string;
  size: number;
  status: string;
  revisionCreatedAt: string;
  revisionUpdatedAt: string;
};

/**
 * 백신검사 결과 조회 응답 데이터
 */
export type GetVaccineCheckResultResponse = {
  modelName: string;
  license: string;
  fistChkDtl: string;
  secndChkDtl: string;
  vanbBrSmry: string;
  checkBy: string;
  checkAt: string;
  checkStatus: string;
};
