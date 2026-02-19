/***************************************
 * 데이터 도구 - 조회 Request
 ***************************************/
export type GetToolRequest = {
  page: number;
  size?: number;
  sort?: string;
  search?: string;
  filter?: string;
  customScriptType?: string;
};

/***************************************
 * 데이터 도구 - 프로세서
 ***************************************/
export type GetProcListResponse = {
  id: string;
  name: string | null;
  description: string | null;
  type: string | null; // default | rule ...
  dataType: string | null; // all | dataframe ...
  rulePattern: string | null;
  ruleValue: string | null;
  code: string | null;
  defaultKey: string | null;
  projectId: string | null;
  createdBy: string | null;
  updatedBy: string | null;
  createdAt: string | null;
  updatedAt: string | null;
};

export type GetProcDetailRequest = {
  processorId: string;
};

export type GetProcDetailResponse = {
  id: string;
  name: string | null;
  description: string | null;
  type: string | null;
  dataType: string | null;
  rulePattern: string | null;
  ruleValue: string | null;
  code: string | null;
  defaultKey: string | null;
  projectId: string | null;
  createdAt: string | null;
  updatedAt: string | null;
  createdBy: string | null;
  updatedBy: string | null;
};

/***************************************
 * 데이터 도구 - 벡터 DB
 ***************************************/
export type GetVectorDBListResponse = {
  name: string;
  type: string;
  id: string;
  projectId: string;
  createdAt: string;
  createdBy: string;
  updatedAt: string;
  updatedBy: string;
  isDeleted: boolean;
  isDefault: string;
};

export type GetVectorDBResponse = {
  projectId: string;
  name: string;
  type: string;
  isDefault: boolean;
  createdAt: string;
  createdBy: string;
  updatedAt: string;
  updatedBy: string;
  connectionInfo: ConnectionInfo;
};

export type ConnectionInfo = {
  // 엘라스틱서치 필드
  apiKey?: string;
  endpoint?: string;
  // 밀버스 필드
  host?: string;
  port?: string;
  user?: string;
  password?: string;
  secure?: string;
  dbName?: string;
  key?: string;
};

export type VectorDBRequestType = {
  name: string;
  type: string;
  connectionInfo: ConnectionInfo;
  isDefault: string;
};

export type CreateVectorDBRequest = VectorDBRequestType;
export type UpdateVectorDBRequest = VectorDBRequestType;

export type GetConnectionArgs = {
  type: string;
  displayName: string;
  connectionInfoArgs: object;
  supportedFileExtensions: string[];
  enable: boolean;
}[];


export type CreateVectorDBResponse = {
  vectorDbId: string;
};
