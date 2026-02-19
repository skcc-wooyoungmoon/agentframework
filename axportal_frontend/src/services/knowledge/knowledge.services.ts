import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';
import { useApiMutation, useApiQuery, type ApiMutationOptions, type ApiQueryOptions } from '@/hooks/common/api/useApi';

// =========================================
// Types
// =========================================

// =========================================
// External Knowledge Types
// =========================================

export interface ExternalRepoResponse {
  id: string;
  name: string;
  description: string;
  embedding_model_id: string;
  vector_db_id: string;
  index_name: string;
  script?: string;
  created_at: string;
  updated_at: string;
  created_by: string;
  updated_by: string;
  is_active: boolean;
  detail?: string;
  vector_db_type: string;
  vector_db_name: string;
  embedding_model_name: string;
  publicStatus: string;
}

export interface ExternalRepoListResponse {
  data: ExternalRepoResponse[];
  payload: {
    pagination: {
      page: number;
      items_per_page: number;
      total: number;
      last_page: number;
      from_: number;
      to: number;
      first_page_url: string;
      last_page_url: string;
      next_page_url?: string;
      prev_page_url?: string;
      links: any[];
    };
  };
  hasNext?: boolean;
}

export interface ExternalRepoFile {
  docPathAnony: string;
  topId: string;
  topIndex: string;
  topSource: { doc_dataset_nm: string; doc_nm: string };
}

export interface ExternalRepoFilesResponse {
  page: {
    content: ExternalRepoFile[];
    pageable: { page: number; size: number; sort: string };
    totalPages: number;
    totalElements: number;
  };
}

export interface ExternalRepoFileChunk {
  index: string;
  id: string;
  score: number;
  source: Record<string, any>; // Object 타입
}

export interface ExternalRepoFileChunksResponse {
  page: {
    content: ExternalRepoFileChunk[];
    pageable: { page: number; size: number; sort: string };
    totalPages: number;
    totalElements: number;
  };
}

export interface GetExternalReposRequest {
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
}

export interface GetExternalReposFilesRequest {
  indexName?: string;
  page?: number;
  countPerPage?: number;
  search?: string;
  uuid?: string;
}

export interface GetExternalReposFileChunkRequest {
  indexName?: string;
  docPathAnony?: string;
  page?: number;
  countPerPage?: number;
}

// =========================================
// Knowledge Types
// =========================================

export interface ChunkingModuleResponse {
  chunkId: string;
  chunkNm: string;
  descCtnt: string;
  delYn: string;
  createdBy: string;
  updatedBy: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
}

export interface EmbeddingModelResponse {
  id: string;
  displayName: string;
  name: string;
  type: string;
  description?: string;
  providerId?: string;
  providerName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface VectorDBResponse {
  id: string;
  name: string;
  type: string;
  projectId?: string;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
  isDeleted?: boolean;
  isDefault?: boolean;
}

export interface ChunkResponse {
  chunk_id: string;
  content: string;
  metadata: Record<string, any>;
  page_number: number;
  chunk_size: number;
  chunk_overlap: number;
  splitter: string;
  created_date: string;
  file_id: string;
  file_name: string;
  is_active: boolean;
}

export interface ChunkListResponse {
  chunks: ChunkResponse[];
  total_count: number;
  page: number;
  size: number;
  total_pages: number;
  has_next: boolean;
  has_previous: boolean;
}

export interface DocumentChunksParams {
  repoId: string;
  documentId: string;
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
}

export interface DocumentDetailParams {
  repoId: string;
  documentId: string;
}

export interface DocumentResponse {
  name: string;
  collection_id: string;
  datasource_file_id: string;
  datasource_file_path: string;
  loader: string;
  processor: string;
  splitter: string;
  chunk_size: number;
  chunk_overlap: number;
  separator: string | null;
  status: string;
  id: string;
  created_at: string;
  updated_at: string;
  created_by: string;
  updated_by: string | null;
  loaded_doc_path: string;
  is_deleted: boolean;
  custom_loader_id: string | null;
  custom_splitter_id: string | null;
  tool_id: string | null;
  processor_ids: string[] | null;
  document_metadata: any | null;
  file_size: number;
  is_active: boolean;
  datasource_updated_at: string;
  chunk_count: number;
  tool_type: string | null;
  tool_name: string | null;
}

export interface PaginationLink {
  url: string | null;
  label: string;
  active: boolean;
  page: number | null;
}

export interface Pagination {
  page: number;
  first_page_url: string;
  from_: number;
  last_page: number;
  links: PaginationLink[];
  next_page_url: string | null;
  items_per_page: number;
  prev_page_url: string | null;
  to: number;
  total: number;
}

export interface DocumentListResponse {
  data: DocumentResponse[];
  payload: {
    pagination: Pagination;
  };
}

export interface DocumentListParams {
  repoId: string;
  page?: number;
  size?: number;
  sort?: string;
  filter?: string;
  search?: string;
}

// =========================================
// External Repo Detail Types
// =========================================

export interface ExternalRepoDetailResponse {
  id: string;
  name: string;
  description?: string;
  embedding_model_name?: string;
  vector_db_name?: string;
  index_name?: string;
  script?: string;
  is_custom_knowledge?: boolean;
  created_at?: string;
  updated_at?: string;

  [key: string]: any;
}

// 청크 병합/분할/삭제 관련 타입
export interface ChunkMergeRequest {
  chunk_ids: string[];
}

export interface ChunkSplitRequest {
  split_position: number;
  new_content_1: string;
  new_content_2: string;
}

export interface ChunkMergeResponse {
  merged_chunk_id: string;
  content: string;
  metadata: Record<string, any>;
}

export interface ChunkSplitResponse {
  chunk_1_id: string;
  chunk_2_id: string;
  content_1: string;
  content_2: string;
  metadata_1: Record<string, any>;
  metadata_2: Record<string, any>;
}

export interface ChunkDeleteResponse {
  deleted_chunk_id: string;
  success: boolean;
}

// =========================================
// API Hooks
// =========================================

/**
 * 청킹 모듈 목록 조회
 *
 * @param options API 옵션
 * @returns 청킹 모듈 목록
 */
export const useGetChunkingModules = (options?: ApiQueryOptions<ChunkingModuleResponse[]>) => {
  return useApiQuery<ChunkingModuleResponse[]>({
    queryKey: ['chunkingModules'],
    url: '/knowledge/chunking-modules',
    ...options,
  });
};

/**
 * 임베딩 모델 목록 조회
 *
 * @param page 페이지 번호
 * @param size 페이지 크기
 * @param options API 옵션
 * @returns 임베딩 모델 목록
 */
export const useGetEmbeddingModels = (page: number = 0, size: number = 100, options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    queryKey: ['embeddingModels', page.toString(), size.toString()],
    url: '/dataCtlg/knowledge/repos/embedding-models',
    params: {
      filter: 'type:embedding',
      page: page.toString(),
      size: size.toString(),
    },
    ...options,
  });
};

/**
 * 벡터DB 목록 조회
 *
 * @param options API 옵션
 * @returns 벡터DB 목록
 */
export const useGetVectorDBs = (options?: ApiQueryOptions<any>) => {
  return useApiQuery<any>({
    queryKey: ['vectorDBs'],
    url: '/dataTool/vectorDb',
    ...options,
  });
};

/**
 * External Knowledge 생성
 *
 * @param options API 옵션
 * @returns External Knowledge 생성 mutation
 */
export type ExternalKnowledgeMutationOptions = ApiMutationOptions<ExternalKnowledgeCreateRes, ExternalKnowledgeCreateReq> & {
  timeout?: number;
};

export const useCreateExternalKnowledge = (options?: ExternalKnowledgeMutationOptions) => {
  return useApiMutation<ExternalKnowledgeCreateRes, ExternalKnowledgeCreateReq>({
    method: 'POST',
    url: '/dataCtlg/knowledge/repos/external',
    timeout: 120000,
    ...options,
  });
};

/**
 * External Knowledge 수정 요청 타입
 */
export interface ExternalKnowledgeUpdateReq {
  name: string;
  description?: string;
  script?: string;
  indexName?: string;
}

/**
 * External Knowledge 수정
 *
 * @param id 지식 ID (knwId 또는 expKnwId)
 * @param options API 옵션
 * @returns External Knowledge 수정 mutation
 */
export const useUpdateExternalKnowledge = (id: string, options?: ApiMutationOptions<any, ExternalKnowledgeUpdateReq>) => {
  return useApiMutation<any, ExternalKnowledgeUpdateReq>({
    method: 'PUT',
    url: `/dataCtlg/knowledge/repos/external/${id}`,
    ...options,
  });
};

/**
 * External Knowledge 삭제 항목 타입
 */
export interface ExternalKnowledgeDeleteItem {
  knwId: string;
  expKnwId: string;
  ragChunkIndexNm: string;
}

/**
 * External Knowledge 삭제 요청 타입
 */
export interface ExternalKnowledgeDeleteRequest {
  items: ExternalKnowledgeDeleteItem[];
}

/**
 * External Knowledge 테스트 요청 타입
 */
export interface ExternalKnowledgeTestReq {
  embeddingModel: string;
  vectorDB: string;
  vectorDbId: string;
  indexName: string;
  script: string;
  query: string;
  retrievalOptions?: string;
}

/**
 * External Knowledge 테스트
 *
 * @param options API 옵션
 * @returns External Knowledge 테스트 mutation
 */
export const useTestExternalKnowledge = (options?: ApiMutationOptions<any, ExternalKnowledgeTestReq>) => {
  return useApiMutation<any, ExternalKnowledgeTestReq>({
    method: 'POST',
    url: '/dataCtlg/knowledge/repos/external/test',
    ...options,
  });
};

/**
 * External Knowledge 상세 조회
 *
 * @param knwId 지식 UUID
 * @param options API 옵션
 * @returns External Knowledge 상세 정보
 */
// useGetExternalKnowledge는 제거됨 - 상세 페이지는 목록에서 state로 데이터를 전달받음

/**
 * External Knowledge 삭제
 *
 * @param options API 옵션
 * @returns External Knowledge 삭제 mutation
 */
export const useDeleteExternalKnowledge = (options?: ApiMutationOptions<void, ExternalKnowledgeDeleteRequest>) => {
  return useApiMutation<void, ExternalKnowledgeDeleteRequest>({
    method: 'POST',
    url: '/dataCtlg/knowledge/repos/external/delete',
    ...options,
  });
};

// 타입 정의
export interface ExternalKnowledgeCreateReq {
  knwId: string;
  knwNm: string;
  description?: string;
  knowledgeType?: string;
  chunkId: string;
  chunkNm?: string;
  chunkSize?: number;
  sentenceOverlap?: number;
  embModelId: string;
  embeddingModel?: string;
  vectorDbId?: string;
  vectorDB?: string;
  ragChunkIndexNm: string;
  syncEnabled?: boolean;
  syncTargets?: string[];
  script?: string;
}

export interface ExternalKnowledgeCreateRes {
  knwId: string;
  knwNm: string;
  expKnwId?: string;
  chunkId: string;
  embModelId: string;
  ragChunkIndexNm: string;
  devSyncYn: string;
  prodSyncYn: string;
  createdBy: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
}

/**
 * External Knowledge 목록 조회
 *
 * @param params 조회 파라미터
 * @param options API 옵션
 * @returns External Knowledge 목록
 */
export const useGetExternalRepos = (params?: GetExternalReposRequest, options?: ApiQueryOptions<ExternalRepoListResponse>) => {
  return useApiQuery<ExternalRepoListResponse>({
    queryKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/dataCtlg/knowledge/repos/external',
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * External Knowledge 지식데이터(MD) 목록 조회
 *
 * @param params 조회 파라미터
 * @param options API 옵션
 * @returns External Knowledge 지식데이터(MD) 목록
 */
export const useGetExternalReposFiles = (params?: GetExternalReposFilesRequest, options?: ApiQueryOptions<ExternalRepoFilesResponse>) => {
  return useApiQuery<ExternalRepoFilesResponse>({
    url: '/dataCtlg/knowledge/repos/files',
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * External Knowledge 지식데이터(MD)의 청크 목록 조회
 *
 * @param params 조회 파라미터
 * @param options API 옵션
 * @returns External Knowledge 지식데이터(MD)의 청크 목록
 */
export const useGetExternalReposFileChunks = (params?: GetExternalReposFileChunkRequest, options?: ApiQueryOptions<ExternalRepoFileChunksResponse>) => {
  return useApiQuery<ExternalRepoFileChunksResponse>({
    url: '/dataCtlg/knowledge/repos/chunks',
    params,
    ...options,
    disableCache: true,
  });
};

/**
 * Document 목록 조회
 *
 * @param params 조회 파라미터
 * @param options API 옵션
 * @returns Document 목록
 */
export const useGetDocuments = (params: DocumentListParams, options?: ApiQueryOptions<DocumentListResponse>) => {
  const { repoId, page = 1, size = 12, sort = 'updated_at,desc', filter, search } = params;

  return useApiQuery<DocumentListResponse>({
    queryKey: ['documents', repoId, page.toString(), size.toString(), sort || 'updated_at,desc', filter || '', search || ''],
    url: `/dataCtlg/knowledge/repos/${repoId}/documents`,
    params: {
      page: page.toString(),
      size: size.toString(),
      sort: sort || 'updated_at,desc',
      ...(filter && { filter }),
      ...(search && { search }),
    },
    ...options,
  });
};

/**
 * External Knowledge Repo 상세 조회
 *
 * @param repoId Repository ID
 * @param options API 옵션
 * @returns Repo 상세 정보
 */
export const useGetExternalRepoDetail = (repoId: string, options?: ApiQueryOptions<ExternalRepoDetailResponse>) => {
  return useApiQuery<ExternalRepoDetailResponse>({
    queryKey: ['externalRepoDetail', repoId],
    url: `/dataCtlg/knowledge/repos/external/${repoId}`,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

/**
 * External Knowledge Repo 상세 조회
 *
 * @param repoId Repository ID
 * @param options API 옵션
 * @returns Repo 상세 정보
 */
export const useGetExternalRepoDetailV2 = (repoId: string, options?: ApiQueryOptions<ExternalRepoDetailResponse>) => {
  return useApiQuery<ExternalRepoDetailResponse>({
    url: `/dataCtlg/knowledge/repos/external/v2/${repoId}`,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

/**
 * External Knowledge 데이터 적재 진행률 조회 응답 타입
 */
export interface ExternalRepoProgressResponse {
  dbLoadProgress: number | null;
  dataPipelineLoadStatus: string;
}

/**
 * External Knowledge 데이터 적재 진행률 조회
 *
 * @param knwId Knowledge ID
 * @param options API 옵션
 * @returns 데이터 적재 진행률 정보
 */
export const useGetExternalRepoProgress = (knwId: string, options?: ApiQueryOptions<ExternalRepoProgressResponse>) => {
  return useApiQuery<ExternalRepoProgressResponse>({
    queryKey: ['externalRepoProgress', knwId],
    url: `/dataCtlg/knowledge/repos/external/progress/${knwId}`,
    ...options,
    disableCache: true, // 항상 최신 데이터 조회
  });
};

/**
 * Document Chunk 목록 조회
 *
 * @param params 조회 파라미터
 * @param options API 옵션
 * @returns Document Chunk 목록
 */
export const useGetDocumentChunks = (params: DocumentChunksParams, options?: ApiQueryOptions<ChunkListResponse>) => {
  const { repoId, documentId, page = 1, size = 10, sort, filter, search } = params;

  return useApiQuery<ChunkListResponse>({
    queryKey: ['document-chunks', repoId, documentId, page.toString(), size.toString(), sort || '', filter || '', search || ''],
    url: `/dataCtlg/knowledge/repos/${repoId}/documents/${documentId}/chunks`,
    params: {
      page: page.toString(),
      size: size.toString(),
      ...(sort && { sort }),
      ...(filter && { filter }),
      ...(search && { search }),
    },
    ...options,
  });
};

/**
 * Document 상세 조회
 *
 * @param params Document 상세 조회 파라미터
 * @param options API 옵션
 * @returns Document 상세 정보
 */
export const useGetDocument = (params: DocumentDetailParams, options?: ApiQueryOptions<DocumentResponse>) => {
  const { repoId, documentId } = params;

  return useApiQuery<DocumentResponse>({
    queryKey: ['document', repoId, documentId],
    url: `/dataCtlg/knowledge/repos/${repoId}/documents/${documentId}`,
    ...options,
  });
};

/**
 * Document Chunks 병합
 *
 * @param repoId Repository ID
 * @param documentId Document ID
 * @param options API 옵션
 * @returns 병합 API 훅
 */
export const useMergeDocumentChunks = (repoId: string, documentId: string, options?: ApiMutationOptions<ChunkMergeResponse, ChunkMergeRequest>) => {
  return useApiMutation<ChunkMergeResponse, ChunkMergeRequest>({
    method: 'PUT',
    url: `/dataCtlg/knowledge/repos/${repoId}/documents/${documentId}/chunks/merge`,
    ...options,
  });
};

/**
 * Document Chunk 분할
 *
 * @param repoId Repository ID
 * @param documentId Document ID
 * @param chunkId Chunk ID
 * @param options API 옵션
 * @returns 분할 API 훅
 */
export const useSplitDocumentChunk = (repoId: string, documentId: string, chunkId: string, options?: ApiMutationOptions<ChunkSplitResponse, ChunkSplitRequest>) => {
  return useApiMutation<ChunkSplitResponse, ChunkSplitRequest>({
    method: 'PUT',
    url: `/dataCtlg/knowledge/repos/${repoId}/documents/${documentId}/chunks/${chunkId}/split`,
    ...options,
  });
};

/**
 * Document Chunk 삭제
 *
 * @param repoId Repository ID
 * @param documentId Document ID
 * @param chunkId Chunk ID
 * @param options API 옵션
 * @returns 삭제 API 훅
 */
export const useDeleteDocumentChunk = (repoId: string, documentId: string, chunkId: string, options?: ApiMutationOptions<ChunkDeleteResponse, void>) => {
  return useApiMutation<ChunkDeleteResponse, void>({
    method: 'DELETE',
    url: `/dataCtlg/knowledge/repos/${repoId}/documents/${documentId}/chunks/${chunkId}`,
    ...options,
  });
};

/**
 * Dataiku 실행 요청
 */
export interface DataikuExecutionRequest {
  [key: string]: any; // JSON 전체를 담는 래퍼
}

/**
 * Dataiku 실행 응답
 */
export interface DataikuExecutionResponse {
  [key: string]: any; // JSON 전체를 담는 래퍼
}

/**
 * Dataiku 실행 (지식 데이터 가져오기)
 *
 * @param options API 옵션
 * @returns Dataiku 실행 API 훅
 */
export const useExecuteDataiku = (options?: ApiMutationOptions<DataikuExecutionResponse, DataikuExecutionRequest>) => {
  return useApiMutation<DataikuExecutionResponse, DataikuExecutionRequest>({
    method: 'POST',
    url: '/dataCtlg/knowledge/repos/dataiku/execute',
    ...options,
  });
};
