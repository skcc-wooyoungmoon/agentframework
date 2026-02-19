// 리스트의 한 Row(아이템) 타입
export type GetInfPromptResponse = {
  uuid: string;
  name: string;
  createdAt: string;
  releaseVersion: string;
  latestVersion: string;
  ptype: number;
  tags: Array<{ tag: string }>;
};

export type GetInfPromptByIdResponse = {
  uuid: string;
  name: string;
  projectId: string;
  createdAt: string;
  ptype: number;
  deleteFlag: boolean;
  releaseVersion: number;
  tags: string[];
  fstPrjSeq: number;
  lstPrjSeq: number;
};

export type GetInfPromptLatestVersionResponse = {
  promptUuid: string; // 프롬프트 UUID
  versionUuid: string; // 버전 UUID
  version: number; // 버전 (숫자)
  createdAt: string; // 생성일시
  release: boolean; // 릴리즈 여부
  deleteFlag: boolean; // 삭제 여부
  createdBy: string; // 생성자 UUID
};

export type InfPromptVersionType = {
  versionUuid: string;
  version: number;
  createdAt: string;
  release: boolean;
  deleteFlag: boolean;
  createdBy: string;
};

export type GetInfPromptVersionDataResponse = {
  promptUuid: string;
  totalVersions: number;
  versions: InfPromptVersionType[];
};

export type InfPromptMessageType = {
  messageId?: string;
  mtype: number;
  message: string;
  order?: number;
};

export type GetInfPromptMsgsByIdResponse = {
  versionUuid: string;
  messages: InfPromptMessageType[];
};

export type InfPromptVariableType = {
  variableId?: string;
  variable: string;
  validation: string;
  validationFlag: boolean;
  tokenLimitFlag: boolean;
  tokenLimit: number;
};

export type GetInfPromptVarsByIdResponse = {
  versionUuid: string;
  variables: InfPromptVariableType[];
};

export type InfPromptTagType = {
  tagId?: string;
  tag: string;
};

export type GetInfPromptTagsByIdResponse = {
  versionUuid: string;
  tags: InfPromptTagType[];
};

export type GetInferencePromptsRequest = {
  project_id?: string;
  page?: number;
  size?: number;
  sort?: string;
  release_only?: boolean;
  dateFilterType?: string;
  fromDate?: string;
  toDate?: string;
  searchFilterType?: string;
  search?: string;
  filter?: string;
  tag?: string | null;
};
export type GetInfPromptByPromptIdRequest = { promptUuid: string };
export type GetInfPromptByVersionIdRequest = { versionUuid: string };

export type InfPromptItemType = {
  desc: string;
  messages: InfPromptMessageType[];
  name: string;
  projectId: string;
  release: boolean;
  tags: InfPromptTagType[];
  variables: InfPromptVariableType[];
  templateUuid: string | null;
};

export type CreateInferencePromptRequest = InfPromptItemType;
export type CreateInferencePromptResponse = {
  promptUuid: string;
};
export type UpdateInferencePromptRequest = {
  promptUuid: string; // URL 바인딩용
  newName: string;
  desc: string;
  messages: InfPromptMessageType[];
  release: boolean;
  tags: InfPromptTagType[];
  variables: InfPromptVariableType[];
};

export type GetInfPromptBuiltinResponse = {
  data: InfPromptBuiltinType[];
};

export type InfPromptBuiltinType = {
  name: string;
  uuid: string;
  messages: InfPromptMessageType[];
  variables: InfPromptVariableType[];
};

// 추론 프롬프트 태그 목록 응답 타입
export type InfPromptTagsListResponse = {
  tags: string[] | null;
  total: number | null;
};

// 추론 프롬프트 연결 프롬프트 관계
export type InfPromptLineageRes = {
  id: string;
  name: string;
  description: string;
  deployed: boolean;
  createdAt: string;
  updatedAt: string;
};

export type GetInfPromptLineageRelationsRequest = {
  promptUuid: string;
  page?: number;
  size?: number;
};
