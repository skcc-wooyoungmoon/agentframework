/**
 * 감시자 정보 타입
 */
export type AuditorInfo = {
  /** 직원명 */
  jkwNm: string;
  /** 부서명 */
  deptNm: string;
};

export type GetGuardRailListRequest = {
  project_id?: string;
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
};

// === 가드레일 요청/응답 타입 ===

export type GuardRailTag = {
  tag: string;
};

export type GuardRailLlm = {
  servingId: string;
  servingName: string;
};

// 가드레일 목록 조회 응답 타입
export type GetGuardRailListResponse = {
  uuid: string;
  name: string;
  description: string;
  isPublicAsset: boolean;
  createdAt: string;
  createdBy: string;
  updatedAt: string;
  updatedBy: string;
};

// 가드레일 상세 조회 응답 타입
export type GetGuardRailByIdResponse = {
  uuid: string;
  name: string;
  description: string;
  promptId: string;
  llms: GuardRailLlm[];
  /** 공개 에셋 여부 */
  isPublicAsset: boolean;
  /** 프로젝트명 */
  projectName: string;
  createdAt: string;
  createdBy: AuditorInfo;
  updatedAt: string;
  updatedBy: AuditorInfo;
  /** 권한 수정자 정보 */
  publicAssetUpdatedBy?: AuditorInfo;
};

export type CreateGuardRailRequest = {
  projectId: string;
  name: string;
  description: string;
  promptId: string;
  llms: GuardRailLlm[];
  tags?: GuardRailTag[];
};

export type UpdateGuardRailRequest = {
  id: string;
  name: string;
  description?: string;
  projectId: string;
  promptId: string;
  llms?: Array<{
    servingName: string;
  }>;
  tags?: Array<{
    tag: string;
  }>;
};

export type DeleteGuardRailRequest = {
  guardrailIds: string[];
};

export type DeleteGuardRailResponse = {
  totalCount: number;
  successCount: number;
};

export type CreateGuardRailResponse = {
  guardrailsId: string;
};

export type UpdateGuardRailResponse = {
  timestamp: string;
  code: number;
  detail: string;
  traceId: string;
  data: {
    result: boolean;
  };
};

// === GuardRailPrompt 전용 타입 ===

export type GuardRailPromptTag = {
  tag: string;
};

// 가드레일 프롬프트 목록 조회 요청 타입
export type GetGuardRailPromptListRequest = {
  project_id?: string;
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
  tag?: string;
};

// 가드레일 프롬프트 목록 조회 응답 타입
export type GetGuardRailPromptListResponse = {
  uuid: string;
  name: string;
  desc: string | null;
  message: string | null;
  project_id: string | null;
  ptype: string;
  release_version: string | null;
  latest_version: string;
  delete_flag: string | null;
  tags: Array<{ uuid: string; tag_uuid: string; tag: string; version_id: string }>;
  tags_raw: string | null;
  createdAt: string;
  updatedAt: string | null;
};

// 가드레일 프롬프트 단건 조회 요청 타입
export type GetGuardRailPromptByIdRequest = {
  id: string;
};

// 가드레일 프롬프트 상세 조회 응답 타입
export type GetGuardRailPromptByIdResponse = {
  uuid: string;
  name: string;
  message: string;
  description?: string;
  tags: GuardRailPromptTag[];
  created_by_name: string;
  created_by_depts: string;
  updated_by_name: string;
  updated_by_depts: string;
  updatedAt: string;
  updatedBy: string;
  createdAt: string;
  createdBy: string;
};

// 가드레일 프롬프트 생성 요청 타입
export type CreateGuardRailPromptRequest = {
  projectId: string;
  name: string;
  message: string;
  tags?: string[];
};

// 가드레일 프롬프트 생성 응답 타입
export type CreateGuardRailPromptResponse = {
  promptUuid: string;
};

// 가드레일 프롬프트 수정 요청 타입
export type UpdateGuardRailPromptRequest = {
  id: string;
  name: string;
  message: string;
  tags?: string[];
};

// 가드레일 프롬프트 수정 응답 타입
export type UpdateGuardRailPromptResponse = {
  timestamp: string;
  code: number;
  detail: string;
  traceId: string;
  data: {
    result: boolean;
  };
};

// 가드레일 프롬프트 삭제 요청 타입
export type DeleteGuardRailPromptRequest = {
  id: string;
};
