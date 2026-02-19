/**
 * @description 데이터셋 타입
 */
export type DatasetType = {
  id: string;
  name: string;
  status: string;
  description: string;
  tags: string[];
  type: string;
  createdAt: string;
  updatedAt: string;
  projectId: string;
  isDeleted: boolean;
  datasourceId: string;
  datasourceFiles: string[];
  filePath: string;
  processor: string;
  createdBy: string;
  updatedBy: string;
  publicStatus: '전체공유' | '내부공유';
  lstPrjSeq: number;
  fstPrjSeq: number;
};

export type GetDatasetsRequest = {
  page: number;
  size: number;
  sort?: string;
  search?: string;
  filter?: string | { status?: string; type?: string } | undefined;
};

export type GetDatasetsResponse = DatasetType;

export type GetDatasetByIdRequest = {
  datasetId: string;
};

export type GetDatasetByIdResponse = {
  name: string;
  type: string;
  description: string;
  tags: string[];
  datasourceId: string;
  projectId: string;
  isDeleted: boolean;
  filePath: string;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
  status: string;
  sourceFileName: string;
  lstPrjSeq: number;
  fstPrjSeq: number;
};

export type GetDataSourceByIdRequest = {
  dataSourceId: string;
};

export type GetDataSourceByIdResponse = {
  id: string;
  projectId: string;
  name: string;
  type: string;
  description: string;
  s3Config: string;
  isDeleted: boolean;
  scope: string;
  status: string;
  bucketName: string;
  files: string[];
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
};

export type UpdateDatasetRequest = {
  datasetId: string;
  description: string;
  projectId: string;
};

export type UpdateDatasetResponse = {
  id: string;
  name: string;
  type: string;
  description: string;
  tags: { name: string }[];
  status: string;
  isDeleted: boolean;
  projectId: string;
  datasourceId: string;
  datasourceFiles: string[];
  filePath: string;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
  processor: object;
};

export type UpdateDatasetTagsRequest = {
  datasetId: string;
  tags: { name: string }[];
};
export type UpdateDatasetTagsResponse = UpdateDatasetResponse;

export type DeleteDatasetTagsRequest = UpdateDatasetTagsRequest;

export type DeleteDatasetTagsResponse = UpdateDatasetResponse;

/**
 * @  description 지식 타입
 */
export type KnwType = {
  id: string;
  name: string;
  description: string;
  datasourceId: string;
  activeCollectionId: string;
  activeCollection: Record<string, unknown>; // Object 타입
  latestCollection: Record<string, unknown>; // Object 타입
  latestTask: Record<string, unknown>; // Object 타입
  vectorDbType: string;
  vectorDbName: string;
  embeddingModelName: string;
  embeddingModelServingName: string;
  loader: string;
  splitter: string;
  isActive: boolean;
  lstPrjSeq: number;
  fstPrjSeq: number;
};

export type GetKnwResponse = KnwType;
export type GetKnwRequest = {
  page: number;
  size: number;
  sort?: string;
  search?: string;
  filter?: string | { status?: string } | undefined;
};

/***************************************
 * 데이터소스 파일 업로드
 ***************************************/
export type UploadFilesRequest = {
  files: File[];
};

export type UploadFilesResponse = {
  data: UploadFilesResponseItem[];
};

export type UploadFilesResponseItem = {
  fileName: string;
  tempFilePath: string;
  fileMetadata: Record<string, unknown>;
  knowledgeConfig: Record<string, unknown>;
};

/***************************************
 * 데이터소스 파일 목록 조회
 ***************************************/
export type GetDatasourceFilesRequest = {
  datasourceId: string;
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
};

export type DatasourceFileItem = {
  id: string;
  fileName: string;
  fileSize: number;
  filePath: string;
  datasourceId: string;
  fileMetadata: any;
  knowledgeConfig: any;
  s3Etag: string | null;
  isDeleted: boolean;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
};

export type GetDatasourceFilesResponse = {
  content: DatasourceFileItem[];
  pageable: {
    page: number;
    size: number;
    sort: string;
  };
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};

/***************************************
 * 다운로드, S3 업로드 및 ES 저장
 ***************************************/
export type DownloadUploadAndSaveToEsRequest = {
  download: boolean;
  uploadToS3: boolean;
  saveToEs: boolean;
  createdBy: string;
  datasetCat01: string;
  datasetCat02: string;
  datasetCat03: string;
  datasetCat04: string;
  datasetCat05: string;
  descCtnt: string;
  title: string;
  tags: string;
  updatedBy: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
  ozonePath: string;
};

export type DownloadUploadAndSaveToEsResponse = {
  success: boolean;
  message: string;
  data: {
    ozonePath: string;
    fstCreatedAt: string;
    lstUpdatedAt: string;
  };
};

/***************************************
 * S3 훈련 데이터셋 생성
 ***************************************/
export type FileMetadata = {
  content_type: string;
  file_size: number;
  original_name: string;
};

export type KnowledgeConfig = {
  extraction_method: string;
  sheet_names?: string[];
  encoding: string;
  line_break?: string;
};

export type TempFile = {
  file_name: string;
  temp_file_path: string;
  file_metadata: FileMetadata;
  knowledge_config: KnowledgeConfig;
};

export type PolicyItem = {
  logic: 'POSITIVE' | 'NEGATIVE';
  names: string[];
  type: 'user' | 'role' | 'group';
};

export type Policy = {
  cascade: boolean;
  decision_strategy: 'UNANIMOUS' | 'AFFIRMATIVE' | 'CONSENSUS';
  logic: 'POSITIVE' | 'NEGATIVE';
  policies: PolicyItem[];
  scopes: ('GET' | 'POST' | 'PUT' | 'DELETE')[];
};

export type ProcessorData = {
  ids: string[];
  duplicate_subset_columns: string[];
  regular_expression: string[];
} | null;

export type CreateS3TrainingDatasetRequest = {
  source_bucket_name: string | null;
  file_names: string | null;
  name: string;
  type: 's3' | 'file';
  description: string;
  project_id: string | null;
  created_by: string | null;
  updated_by: string | null;
  is_deleted: boolean;
  scope: 'public' | 'private' | null;
  temp_files: TempFile[] | null;
  policy: Policy[] | null;
  dataset_type: string;
  tags: { name: string }[];
  processor: ProcessorData;
};

export type CreateS3TrainingDatasetResponse = {
  success: boolean;
  message: string;
  data: {
    id: string;
    name: string;
    type: string;
    description: string;
    project_id: string;
    created_by: string;
    updated_by: string;
    is_deleted: boolean;
    scope: string;
    created_at: string;
    updated_at: string;
  };
};

/***************************************
 * 데이터셋 파일 업로드
 ***************************************/
export type UploadDatasetFileRequest = {
  name: string; // 필수
  type: string; // 필수 (예: "custom")
  status?: string | null; // 선택 (null 허용)
  description?: string; // 선택
  tags?: string; // 선택 (JSON 문자열: [{"name":"tag1"}, {"name":"tag2"}])
  projectId?: string | null; // 선택 (null 허용)
  createdBy?: string | null; // 선택 (null 허용)
  updatedBy?: string | null; // 선택 (null 허용)
  payload?: string | null; // 선택 (null 허용)
};

export type UploadDatasetFileResponse = {
  success: boolean;
  message: string;
  data: {
    id: string;
    name: string;
    type: string;
    status: string;
    description: string;
    tags: string[];
    projectId: string;
    createdBy: string;
    updatedBy: string;
    payload: string;
    createdAt: string;
    updatedAt: string;
  };
};
