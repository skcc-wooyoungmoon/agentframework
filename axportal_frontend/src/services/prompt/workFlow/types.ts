// 워크플로우 레지스트리 테이블 구조에 맞는 타입 정의
export type WorkFlowRegistryItem = {
  workflowId: string; // uuid
  workflowName: string; // varchar(50)
  versionNo: number; // int4
  xmlText: string; // text
  description?: string | null; // text (nullable)
  isActive: string; // char(1) - 'Y' or 'N'
  createdAt: string; // timestamptz
  createdBy?: string | null; // varchar(50) (nullable)
  updatedAt?: string | null; // timestamptz (nullable)
  updatedBy?: string | null; // varchar(50) (nullable)
  tag?: string | null; // varchar(100) (nullable)
  projectSeq?: string | null; // 프로젝트 SEQ (nullable)
  projectScope?: string | null; // varchar(30) (nullable)
};

// 기존 응답 타입 (하위 호환성을 위해 유지)
export type GetWorkFlowResponse = {
  workflowId: string;
  workflowName: string;
  versionNo: number;
  xmlText: string;
  description: string | null;
  isActive: string; // char(1) - 'Y' or 'N'
  createdAt: string;
  createdBy: string;
  updatedAt: string | null;
  updatedBy: string;
  tags: string[];
  tagsRaw: string;
  projectSeq: string;
  projectScope: string;
};

export type GetWorkFlowLatestVersionResponse = {
  workflowId: string; // 워크플로우 UUID
  workflowName: string; // 워크플로우 이름
  versionNo: number; // 버전 번호
  xmlText: string; // 워크플로우 XML
  description?: string; // 워크플로우 설명 (nullable)
  isActive: string; // char(1) - 'Y' or 'N'
  createdAt: string; // 생성일시 (Instant)
  createdBy?: string; // 생성자 (nullable)
  updatedAt?: string; // 최종 수정일시 (Instant, nullable)
  updatedBy?: string; // 최종 수정자 (nullable)
  tags: string[]; // 태그 목록
  tagsRaw?: string; // 원본 태그 문자열 (nullable)
  projectSeq?: string; // 프로젝트 SEQ (nullable)
  projectScope?: string; // 프로젝트 공개 범위 (nullable)
};

export type WorkFlowVersionType = {
  versionNo: number;
  createdAt: string;
  updatedAt: string;
};

export type GetWorkFlowVersionDataResponse = {
  workFlowId: string;
  totalVersions: number;
  versions: WorkFlowVersionType[];
};

export type WorkFlowMessageType = {
  messageId?: string;
  mtype: number;
  message: string;
  order?: number;
};

export type GetWorkFlowMsgsByIdResponse = {
  versionUuid: string;
  messages: WorkFlowMessageType[];
};

export type WorkFlowVariableType = {
  variableId?: string;
  variable: string;
  validation: string;
  validationFlag: boolean;
  tokenLimitFlag: boolean;
  tokenLimit: number;
};

export type GetWorkFlowVarsByIdResponse = {
  versionUuid: string;
  variables: WorkFlowVariableType[];
};

export type WorkFlowTagType = {
  tagId?: string;
  tag: string;
};

export type GetWorkFlowTagsRequest = object;

export type GetWorkFlowTagsResponse = string[];

// 페이지네이션 정보 타입 - 백엔드 PageableInfo 구조와 매칭
// @deprecated PaginatedDataType을 사용하세요. 하위 호환성을 위해 유지됩니다.
export type PageableInfo = {
  page: number;
  size: number;
  sort: string;
};

// 페이지네이션 응답 타입 - 백엔드 PageResponse 구조와 매칭
// @deprecated PaginatedDataType을 사용하세요. 구조가 동일하므로 PaginatedDataType으로 통일되었습니다.
export type PageResponse<T> = {
  content: T[];
  pageable: PageableInfo;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};

export type GetWorkFlowsRequest = {
  project_id?: string;
  page?: number;
  size?: number;
  sort?: string;
  search?: string;
  tag?: string;
};
export type GetWorkFlowByIdRequest = { workFlowId: string };
export type GetWorkFlowVerByIdRequest = { workFlowId: string; versionNo: number };
export type DeleteWorkFlowRequest = { workFlowId: string };
export type DeleteWorkFlowsRequest = { ids: string[] }; // ID 배열
export type DeleteWorkFlowsResponse = {
  totalCount: number;
  successCount: number;
  failCount: number;
};
export type GetWorkFlowByVersionIdRequest = { versionUuid: string };

// 워크플로우 생성 요청 타입 - 백엔드 WorkFlowCreateReq DTO에 맞춤
export type CreateWorkFlowRegistryRequest = {
  workflowName: string; // @NotBlank @Size(max = 50) - 필수
  xmlText?: string; // 선택 (직접 입력 시 사용)
  description?: string; // 선택
  isActive?: string; // char(1) - 'Y' or 'N', 기본값 'Y'
  tag?: string; // @Size(max = 100) - 선택 (태그들을 쉼표로 연결)
  projectScope?: string; // @Size(max = 30) - 선택
  createdBy?: string; // @Size(max = 50) - 선택
};

// multipart/form-data 요청을 위한 타입
export type CreateWorkFlowRegistryFormData = {
  form: CreateWorkFlowRegistryRequest;
  xmlFile?: File; // 파일 업로드 시 사용
};

// 워크플로우 생성 응답 타입 - 백엔드 WorkFlowCreateRes DTO에 맞춤
export type CreateWorkFlowRegistryResponse = {
  workFlowId: string; // 생성된 워크플로우 ID
};

export type WorkFlowItemType = {
  desc: string;
  messages: WorkFlowMessageType[];
  name: string;
  projectId: string;
  release: boolean;
  tags: WorkFlowTagType[];
  variables: WorkFlowVariableType[];
  templateUuid: string | null;
};

// 기존 생성 요청 타입 (하위 호환성을 위해 유지)
export type CreateWorkFlowRequest = WorkFlowItemType;

// 워크플로우 수정 요청 타입 - 백엔드 WorkFlowUpdateReq DTO에 맞춤
export type UpdateWorkFlowRequest = {
  workFlowId: string;
  workflowName: string; // @NotBlank @Size(max = 50) - 필수
  versionNo: number; // @NotNull - 필수
  xmlText?: string; // 선택 (직접 입력 시 사용)
  description?: string; // 선택
  isActive?: string; // char(1) - 'Y' or 'N', 기본값 'Y'
  tag?: string; // @Size(max = 100) - 선택 (태그들을 쉼표로 연결)
  projectScope?: string; // @Size(max = 30) - 선택
  updatedBy?: string; // @Size(max = 50) - 선택 (수정자 정보)
};

// 워크플로우 수정 FormData 타입 - multipart/form-data용
export type UpdateWorkFlowFormData = {
  form: UpdateWorkFlowRequest;
  xmlFile?: File;
};

// 워크플로우 공개설정 요청 타입
export type SetWorkFlowPublicRequest = { workFlowId: string };
