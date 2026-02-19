export type GetFewShotByIdRequest = {
  uuid: string;
};

export type GetFewShotListRequest = {
  projectId: string;
  page: number;
  size: number;
  sort: string;
  filter: string;
  search: string;
  release_only?: boolean;
};

export type AgentGraphRelationType = {
  source_key: string;
  target_key: string;
  action: string;
  source_type: string;
  target_type: string;
};

export type FewShotType = {
  uuid: string;
  name: string;
  dependency: [];
  createdAt: string;
  createdBy: string;
  releaseVersion: number;
  latestVersion: number;
  tags: string[];
  hitRate: number | null;
  connectedAgentCount: number;
  publicStatus: '전체공유' | '내부공유';
  isRelease: boolean;
  agentGraphRelations: AgentGraphRelationType[];
};

export type GetFewShotByIdResponse = FewShotType;
export type GetFewShotListResponse = FewShotType;

export type GetFewShotByVerIdRequest = {
  verId: string;
};

export type FewShotVerType = {
  version: number;
  release: boolean;
  delete_flag: boolean;
  createdBy: string;
  createdAt: string;
  versionId: string;
  uuid: string;
};

export type GetLtstFewShotVerResponse = FewShotVerType;
export type GetFewShotVerListByIdResponse = FewShotVerType[];

export type FewShotItemType = {
  uuid: string;
  itemSequence: number;
  item: string;
  versionId: string;
  itemType: string;
};

export type GetLtstFewShotItemListResponse = FewShotItemType;

export type FewShotTagType = {
  tagUuid: string;
  tag: string;
  fewShotUuid: string;
  versionId: string;
};

export type GetFewShotTagsByVerIdResponse = FewShotTagType[];

// POST/PUT 요청을 위한 타입들
export type FewShotItem = {
  itemQuery: string;
  itemAnswer: string;
};

export type FewShotTag = {
  tag: string;
};

export type CreateFewShotRequest = {
  items: FewShotItem[];
  name: string;
  release: boolean;
  tags: FewShotTag[];
  projectId: string;
};

export type CreateFewShotResponse = {
  fewShotUuid: string;
};

export type UpdateFewShotRequest = {
  items: FewShotItem[];
  newName: string;
  release: boolean;
  tags: FewShotTag[];
};

export type GetFewShotTagListResponse = {
  tags: string[];
};

export type GetFewShotLineageRelationsRequest = {
  fewShotUuid: string;
  page: number;
  size: number;
};

export type GetFewShotLineageRelationsResponse = {
  id: string;
  name: string;
  description: string;
  deployed: boolean;
  createdAt: string;
  updatedAt: string;
};

export type GetFewShotLineageResponse = GetFewShotLineageRelationsResponse[];
