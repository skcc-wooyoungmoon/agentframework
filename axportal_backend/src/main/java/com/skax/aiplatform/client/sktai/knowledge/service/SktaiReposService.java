package com.skax.aiplatform.client.sktai.knowledge.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.knowledge.SktaiReposClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.CollectAndUpdateRepo;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.DocumentUpdateRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoEdit;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtUpdateRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoUpdateRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ChunkListResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.IndexingRepoResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.MultiResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoCreateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoExtInfo;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoImportResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoListResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoRetrievalInfo;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoWithCollection;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Knowledge Repository 관리 서비스
 * 
 * <p>
 * SKTAI Knowledge API의 Repository 관리 기능을 제공하는 비즈니스 서비스입니다.
 * Repository 생성부터 문서 관리, 인덱싱, 외부 연동까지 전체 생명주기를 관리합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Repository 관리</strong>: 생성, 조회, 수정, 삭제 및 설정 관리</li>
 * <li><strong>Document 관리</strong>: 문서 목록 조회, 상세 조회, 메타데이터 관리</li>
 * <li><strong>Indexing 관리</strong>: 인덱싱 실행, 중지, 진행 상황 모니터링</li>
 * <li><strong>External Repository</strong>: 외부 지식 저장소 연동 및 동기화</li>
 * <li><strong>검색 최적화</strong>: Repository별 검색 설정 및 성능 튜닝</li>
 * </ul>
 * 
 * <h3>오류 처리:</h3>
 * <ul>
 * <li><strong>연결 오류</strong>: SKTAI API 서버 연결 실패 시 BusinessException 발생</li>
 * <li><strong>인증 오류</strong>: API 키 또는 권한 부족 시 적절한 오류 메시지 제공</li>
 * <li><strong>데이터 오류</strong>: 요청 데이터 검증 실패 시 상세 오류 정보 제공</li>
 * <li><strong>비즈니스 오류</strong>: Repository 상태 충돌 등 비즈니스 로직 오류 처리</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiReposService {

    private final SktaiReposClient sktaiReposClient;

    // ========================================
    // Repository 기본 관리
    // ========================================

    /**
     * Repository 목록 조회
     * 
     * <p>
     * 프로젝트 내의 모든 Knowledge Repository 목록을 조회합니다.
     * 필터링, 정렬, 페이징 옵션을 지원하여 효율적인 Repository 관리를 제공합니다.
     * </p>
     * 
     * @param isActive 활성화 상태 필터 (선택적)
     * @param page     페이지 번호
     * @param size     페이지 크기
     * @param sort     정렬 조건
     * @param filter   필터 조건
     * @param search   검색어
     * @return Repository 목록
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public RepoListResponse getRepositories(Boolean isActive, Integer page, Integer size, String sort, String filter,
            String search) {
        try {
            log.info("SKTAI Knowledge Repository 목록 조회 시작 - isActive: {}, page: {}, size: {}",
                    isActive, page, size);

            RepoListResponse repositories = sktaiReposClient.getRepos(isActive, page, size, sort, filter, search);

            log.info("SKTAI Knowledge Repository 목록 조회 완료 - 조회된 Repository 수: {}",
                    repositories.getData() != null ? repositories.getData().size() : 0);
            return repositories;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 목록 조회 실패 (예상치 못한 오류) - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository 생성
     * 
     * <p>
     * 새로운 Knowledge Repository를 생성합니다.
     * DataSource, 임베딩 모델, 벡터 DB 등 모든 필수 설정을 포함하여 검색 가능한 지식 저장소를 구축합니다.
     * </p>
     * 
     * @param request Repository 생성 요청 정보
     * @return 생성된 Repository 정보
     * @throws BusinessException Repository 생성 실패 시
     */
    public RepoCreateResponse createRepository(RepoCreate request) {
        try {
            log.info("SKTAI Knowledge Repository 생성 시작 - name: {}, datasourceId: {}, embeddingModel: {}",
                    request.getName(), request.getDatasourceId(), request.getEmbeddingModelName());

            RepoCreateResponse response = sktaiReposClient.createRepo(request);

            log.info("SKTAI Knowledge Repository 생성 완료 - repoId: {}, name: {}, status: {}",
                    response.getRepoId(), response.getName(), response.getStatus());
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 생성 실패 (BusinessException) - name: {}, message: {}", request.getName(),
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 생성 실패 (예상치 못한 오류) - name: {}, error: {}", request.getName(),
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository 상세 조회
     * 
     * <p>
     * 특정 Repository의 상세 정보를 조회합니다.
     * 기본 정보, 설정, 통계, 상태 등 Repository의 모든 측면을 포함합니다.
     * </p>
     * 
     * @param repoId Repository ID
     * @return Repository 상세 정보
     * @throws BusinessException Repository 조회 실패 시
     */
    public RepoWithCollection getRepository(String repoId) {
        try {
            log.info("SKTAI Knowledge Repository 상세 조회 시작 - repoId: {}", repoId);

            RepoWithCollection repository = sktaiReposClient.getRepo(repoId);

            log.info("SKTAI Knowledge Repository 상세 조회 완료 - repoId: {}, name: {}",
                    repository.getRepository().getRepoId(), repository.getRepository().getName());
            return repository;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 상세 조회 실패 (BusinessException) - repoId: {}, message: {}", repoId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 상세 조회 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId, e.getMessage(),
                    e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository 설정 업데이트
     * 
     * <p>
     * Repository의 기본 설정을 업데이트합니다.
     * 이름, 설명, 로더 설정, 청크 설정 등을 변경할 수 있습니다.
     * </p>
     * 
     * @param repoId  Repository ID
     * @param request 업데이트 요청 정보
     * @return 업데이트된 Repository 정보
     * @throws BusinessException Repository 업데이트 실패 시
     */
    public RepoResponse updateRepositorySettings(String repoId, RepoEdit request) {
        try {
            log.info("SKTAI Knowledge Repository 설정 업데이트 시작 - repoId: {}, name: {}", repoId, request.getName());

            RepoResponse response = sktaiReposClient.updateRepoSettings(repoId, request);

            log.info("SKTAI Knowledge Repository 설정 업데이트 완료 - repoId: {}, name: {}",
                    response.getRepoId(), response.getName());
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 설정 업데이트 실패 (BusinessException) - repoId: {}, message: {}", repoId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 설정 업데이트 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 설정 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository 데이터 소스 업데이트
     * 
     * <p>
     * Repository와 연결된 DataSource를 변경합니다.
     * DataSource 변경 후 새로운 파일들을 인덱싱할 수 있습니다.
     * </p>
     * 
     * @param repoId  Repository ID
     * @param request 데이터 소스 업데이트 요청
     * @return 업데이트된 Repository 정보
     * @throws BusinessException DataSource 업데이트 실패 시
     */
    public RepoResponse updateRepositoryDataSource(String repoId, RepoUpdateRequest request) {
        try {
            log.info("SKTAI Knowledge Repository DataSource 업데이트 시작 - repoId: {}, datasourceId: {}",
                    repoId, request.getDatasourceId());

            RepoResponse response = sktaiReposClient.updateRepoDataSource(repoId, request);

            log.info("SKTAI Knowledge Repository DataSource 업데이트 완료 - repoId: {}", repoId);
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository DataSource 업데이트 실패 (BusinessException) - repoId: {}, message: {}",
                    repoId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository DataSource 업데이트 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository DataSource 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository 삭제
     * 
     * <p>
     * Repository와 관련된 모든 데이터를 삭제합니다.
     * Document, 벡터 인덱스, 메타데이터 등이 모두 제거됩니다.
     * </p>
     * 
     * @param repoId Repository ID
     * @throws BusinessException Repository 삭제 실패 시
     */
    public void deleteRepository(UUID repoId) {
        try {
            log.info("SKTAI Knowledge Repository 삭제 시작 - repoId: {}", repoId);

            sktaiReposClient.deleteRepo(repoId);

            log.info("SKTAI Knowledge Repository 삭제 완료 - repoId: {}", repoId);

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 삭제 실패 (BusinessException) - repoId: {}, message: {}", repoId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 삭제 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId, e.getMessage(),
                    e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // 검색 정보 관리
    // ========================================

    /**
     * Repository 검색 정보 조회
     * 
     * <p>
     * Repository의 검색 관련 상세 정보를 조회합니다.
     * 검색 설정, 성능 지표, 사용 통계 등을 포함합니다.
     * </p>
     * 
     * @param repoId     Repository ID
     * @param projectId  프로젝트 ID
     * @param isExternal 외부 Repository 여부
     * @return Repository 검색 정보
     * @throws BusinessException 검색 정보 조회 실패 시
     */
    public RepoRetrievalInfo getRepositoryRetrievalInfo(String repoId, String projectId, Boolean isExternal) {
        try {
            log.info("SKTAI Knowledge Repository 검색 정보 조회 시작 - repoId: {}, projectId: {}, isExternal: {}",
                    repoId, projectId, isExternal);

            RepoRetrievalInfo retrievalInfo = sktaiReposClient.getRepoRetrievalInfo(repoId, projectId, isExternal);

            log.info("SKTAI Knowledge Repository 검색 정보 조회 완료 - repoId: {}", repoId);
            return retrievalInfo;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 검색 정보 조회 실패 (BusinessException) - repoId: {}, message: {}", repoId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 검색 정보 조회 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 검색 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // 인덱싱 관리
    // ========================================

    /**
     * Repository 인덱싱 시작
     * 
     * <p>
     * Repository의 모든 Document에 대해 인덱싱 작업을 시작합니다.
     * 문서 처리, 청크 분할, 임베딩 생성, 벡터 인덱싱의 전체 과정을 수행합니다.
     * </p>
     * 
     * @param repoId     Repository ID
     * @param targetStep 인덱싱 대상 단계
     * @return 인덱싱 작업 정보
     * @throws BusinessException 인덱싱 시작 실패 시
     */
    public IndexingRepoResponse startRepositoryIndexing(String repoId, String targetStep) {
        try {
            log.info("SKTAI Knowledge Repository 인덱싱 시작 - repoId: {}, targetStep: {}", repoId, targetStep);

            IndexingRepoResponse response = sktaiReposClient.startIndexing(repoId, targetStep);

            log.info("SKTAI Knowledge Repository 인덱싱 시작 완료 - repoId: {}, jobId: {}",
                    repoId, response.getJobInfo().getJobId());
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 인덱싱 시작 실패 (BusinessException) - repoId: {}, message: {}", repoId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 인덱싱 시작 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 인덱싱 시작에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository 인덱싱 중지
     * 
     * <p>
     * 진행 중인 인덱싱 작업을 중지합니다.
     * 현재 처리 중인 Document는 완료 후 중지됩니다.
     * </p>
     * 
     * @param repoId Repository ID
     * @return 인덱싱 중지 결과
     * @throws BusinessException 인덱싱 중지 실패 시
     */
    public IndexingRepoResponse stopRepositoryIndexing(String repoId) {
        try {
            log.info("SKTAI Knowledge Repository 인덱싱 중지 시작 - repoId: {}", repoId);

            IndexingRepoResponse response = sktaiReposClient.stopIndexing(repoId);

            log.info("SKTAI Knowledge Repository 인덱싱 중지 완료 - repoId: {}", repoId);
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository 인덱싱 중지 실패 (BusinessException) - repoId: {}, message: {}", repoId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository 인덱싱 중지 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository 인덱싱 중지에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository S3 수집 및 업데이트
     * 
     * <p>
     * S3에서 파일을 수집하고 Repository를 업데이트합니다.
     * 새로운 파일 추가, 변경된 파일 업데이트, 삭제된 파일 제거를 자동으로 처리합니다.
     * </p>
     * 
     * @param repoId  Repository ID
     * @param request S3 수집 및 업데이트 요청
     * @return 수집 및 인덱싱 결과
     * @throws BusinessException S3 수집 실패 시
     */
    public IndexingRepoResponse collectAndUpdateRepository(String repoId, CollectAndUpdateRepo request) {
        try {
            log.info("SKTAI Knowledge Repository S3 수집 및 업데이트 시작 - repoId: {}, bucket: {}, prefix: {}",
                    repoId, request.getS3BucketName(), request.getS3Prefix());

            IndexingRepoResponse response = sktaiReposClient.collectAndUpdate(repoId, request);

            log.info("SKTAI Knowledge Repository S3 수집 및 업데이트 시작 완료 - repoId: {}, jobId: {}",
                    repoId, response.getJobInfo().getJobId());
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository S3 수집 및 업데이트 실패 (BusinessException) - repoId: {}, message: {}",
                    repoId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository S3 수집 및 업데이트 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository S3 수집 및 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // Document 관리
    // ========================================

    /**
     * Repository Document 목록 조회
     * 
     * <p>
     * Repository에 포함된 Document 목록을 조회합니다.
     * 필터링, 정렬, 페이징을 지원하여 효율적인 Document 관리를 제공합니다.
     * </p>
     * 
     * @param repoId Repository ID
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return Document 목록
     * @throws BusinessException Document 목록 조회 실패 시
     */
    public MultiResponse getRepositoryDocuments(String repoId, Integer page, Integer size, String sort, String filter,
            String search) {
        try {
            log.info("SKTAI Knowledge Repository Document 목록 조회 시작 - repoId: {}, page: {}, size: {}",
                    repoId, page, size);

            MultiResponse documents = sktaiReposClient.getDocuments(repoId, page, size, sort, filter, search);

            log.info("SKTAI Knowledge Repository Document 목록 조회 완료 - repoId: {}, 조회된 Document 수: {}",
                    repoId, documents.getData() != null ? documents.getData().size() : 0);
            return documents;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository Document 목록 조회 실패 (BusinessException) - repoId: {}, message: {}",
                    repoId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository Document 목록 조회 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository Document 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository Documents 목록 조회 (간단한 메서드명)
     * 
     * @param repoId Repository ID
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return Document 목록
     * @throws BusinessException Document 목록 조회 실패 시
     */
    public Object getDocuments(String repoId, Integer page, Integer size, String sort, String filter, String search) {
        return getRepositoryDocuments(repoId, page, size, sort, filter, search);
    }

    /**
     * Repository Document Chunks 목록 조회 (간단한 메서드명)
     * 
     * @param repoId     Repository ID
     * @param documentId Document ID
     * @param page       페이지 번호
     * @param size       페이지 크기
     * @param sort       정렬 조건
     * @param filter     필터 조건
     * @param search     검색어
     * @return Document Chunks 목록
     * @throws BusinessException Document Chunks 목록 조회 실패 시
     */
    public Object getDocumentChunks(String repoId, String documentId, Integer page, Integer size, String sort,
            String filter, String search) {
        return getRepositoryDocumentChunks(repoId, documentId, page, size, sort, filter, search);
    }

    /**
     * Repository Document 상세 조회
     * 
     * <p>
     * 특정 Document의 상세 정보를 조회합니다.
     * Document 메타데이터, 처리 상태, 청크 정보 등을 포함합니다.
     * </p>
     * 
     * @param repoId     Repository ID
     * @param documentId Document ID
     * @return Document 상세 정보
     * @throws BusinessException Document 조회 실패 시
     */
    public Object getRepositoryDocument(String repoId, String documentId) {
        try {
            log.info("SKTAI Knowledge Repository Document 상세 조회 시작 - repoId: {}, documentId: {}", repoId, documentId);

            Object document = sktaiReposClient.getDocument(repoId, documentId);

            log.info("SKTAI Knowledge Repository Document 상세 조회 완료 - repoId: {}, documentId: {}", repoId, documentId);
            return document;

        } catch (BusinessException e) {
            log.error(
                    "SKTAI Knowledge Repository Document 상세 조회 실패 (BusinessException) - repoId: {}, documentId: {}, message: {}",
                    repoId, documentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error(
                    "SKTAI Knowledge Repository Document 상세 조회 실패 (예상치 못한 오류) - repoId: {}, documentId: {}, error: {}",
                    repoId, documentId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository Document 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository Document 일괄 업데이트
     * 
     * <p>
     * 여러 Document의 메타데이터나 설정을 일괄적으로 업데이트합니다.
     * 활성화 상태, 메타데이터, 처리 설정 등을 변경할 수 있습니다.
     * </p>
     * 
     * @param repoId  Repository ID
     * @param request Document 업데이트 요청
     * @return 업데이트 결과
     * @throws BusinessException Document 업데이트 실패 시
     */
    public Object updateRepositoryDocuments(String repoId, DocumentUpdateRequest request) {
        try {
            log.info("SKTAI Knowledge Repository Document 일괄 업데이트 시작 - repoId: {}, 대상 Document 수: {}",
                    repoId, request.getDocumentIds() != null ? request.getDocumentIds().size() : "전체");

            Object result = sktaiReposClient.updateDocuments(repoId, request);

            log.info("SKTAI Knowledge Repository Document 일괄 업데이트 완료 - repoId: {}", repoId);
            return result;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Repository Document 일괄 업데이트 실패 (BusinessException) - repoId: {}, message: {}",
                    repoId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Repository Document 일괄 업데이트 실패 (예상치 못한 오류) - repoId: {}, error: {}", repoId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository Document 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // External Repository 관리
    // ========================================

    /**
     * External Repository 목록 조회
     * 
     * <p>
     * 등록된 모든 External Repository 목록을 조회합니다.
     * 연동 상태, 동기화 정보 등을 포함합니다.
     * </p>
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return External Repository 목록
     * @throws BusinessException External Repository 목록 조회 실패 시
     */
    public MultiResponse getExternalRepositories(Integer page, Integer size, String sort, String filter,
            String search) {
        try {
            log.info("SKTAI Knowledge External Repository 목록 조회 시작 - page: {}, size: {}", page, size);

            MultiResponse externalRepos = sktaiReposClient.getExternalRepos(page, size, sort, filter, search);

            log.info("SKTAI Knowledge External Repository 목록 조회 완료 - 조회된 External Repository 수: {}",
                    externalRepos.getData() != null ? externalRepos.getData().size() : 0);
            return externalRepos;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge External Repository 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge External Repository 목록 조회 실패 (예상치 못한 오류) - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge External Repository 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * External Repository 생성
     * 
     * <p>
     * 새로운 External Repository를 등록합니다.
     * 외부 시스템과의 연동 설정, 인증 정보, 동기화 옵션 등을 구성합니다.
     * </p>
     * 
     * @param request External Repository 생성 요청
     * @return 생성된 External Repository 정보
     * @throws BusinessException External Repository 생성 실패 시
     */
    public RepoResponse createExternalRepository(RepoExtCreateRequest request) {
        try {
            log.info("SKTAI Knowledge External Repository 생성 시작 - name: {}, type: {}, endpoint: {}",
                    request.getName(), request.getType(), request.getEndpoint());

            RepoResponse response = sktaiReposClient.createExternalRepo(request);

            log.info("SKTAI Knowledge External Repository 생성 완료 - name: {}", request.getName());
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge External Repository 생성 실패 (BusinessException) - name: {}, message: {}",
                    request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge External Repository 생성 실패 (예상치 못한 오류) - name: {}, error: {}", request.getName(),
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge External Repository 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * External Repository 조회
     * 
     * <p>
     * 특정 External Repository의 상세 정보를 조회합니다.
     * 연결 설정, 동기화 상태, 성능 지표 등을 포함합니다.
     * </p>
     * 
     * @param repoExtId External Repository ID
     * @return External Repository 상세 정보
     * @throws BusinessException External Repository 조회 실패 시
     */
    public RepoExtInfo getExternalRepository(String repoExtId) {
        try {
            log.info("SKTAI Knowledge External Repository 조회 시작 - repoExtId: {}", repoExtId);

            RepoExtInfo response = sktaiReposClient.getExternalRepo(repoExtId);

            log.info("SKTAI Knowledge External Repository 조회 완료 - repoExtId: {}, name: {}",
                    repoExtId, response.getBasicInfo().getName());
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge External Repository 조회 실패 (BusinessException) - repoExtId: {}, message: {}",
                    repoExtId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge External Repository 조회 실패 (예상치 못한 오류) - repoExtId: {}, error: {}", repoExtId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge External Repository 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * External Repository 업데이트
     * 
     * <p>
     * External Repository의 설정을 업데이트합니다.
     * 연결 정보, 인증 설정, 동기화 옵션 등을 부분적으로 수정할 수 있습니다.
     * </p>
     * 
     * @param repoExtId External Repository ID
     * @param request   업데이트 요청 정보
     * @return 업데이트된 External Repository 정보
     * @throws BusinessException External Repository 업데이트 실패 시
     */
    public RepoResponse updateExternalRepository(String repoExtId, RepoExtUpdateRequest request) {
        try {
            log.info("SKTAI Knowledge External Repository 업데이트 시작 - repoExtId: {}, name: {}",
                    repoExtId, request.getName());

            RepoResponse response = sktaiReposClient.updateExternalRepo(repoExtId, request);

            log.info("SKTAI Knowledge External Repository 업데이트 완료 - repoExtId: {}", repoExtId);
            return response;

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge External Repository 업데이트 실패 (BusinessException) - repoExtId: {}, message: {}",
                    repoExtId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge External Repository 업데이트 실패 (예상치 못한 오류) - repoExtId: {}, error: {}", repoExtId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge External Repository 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * External Repository 삭제
     * 
     * <p>
     * External Repository를 삭제합니다.
     * 연동 설정과 동기화된 메타데이터가 제거됩니다.
     * </p>
     * 
     * @param repoExtId External Repository ID
     * @throws BusinessException External Repository 삭제 실패 시
     */
    public void deleteExternalRepository(String repoExtId) {
        try {
            log.info("SKTAI Knowledge External Repository 삭제 시작 - repoExtId: {}", repoExtId);

            sktaiReposClient.deleteExternalRepo(repoExtId);

            log.info("SKTAI Knowledge External Repository 삭제 완료 - repoExtId: {}", repoExtId);

        } catch (BusinessException e) {
            log.error("SKTAI Knowledge External Repository 삭제 실패 (BusinessException) - repoExtId: {}, message: {}",
                    repoExtId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge External Repository 삭제 실패 (예상치 못한 오류) - repoExtId: {}, error: {}", repoExtId,
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge External Repository 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Repository Document Chunks 목록 조회
     * 
     * <p>
     * 특정 Document의 Chunks 목록을 조회합니다.
     * 페이징, 정렬, 필터링, 검색 기능을 지원합니다.
     * </p>
     * 
     * @param repoId     Repository ID
     * @param documentId Document ID
     * @param page       페이지 번호
     * @param size       페이지 크기
     * @param sort       정렬 조건
     * @param filter     필터 조건
     * @param search     검색어
     * @return Document Chunks 목록
     * @throws BusinessException Document Chunks 목록 조회 실패 시
     */
    public ChunkListResponse getRepositoryDocumentChunks(String repoId, String documentId, Integer page, Integer size,
            String sort, String filter, String search) {
        try {
            log.info(
                    "SKTAI Knowledge Repository Document Chunks 목록 조회 시작 - repoId: {}, documentId: {}, page: {}, size: {}",
                    repoId, documentId, page, size);

            ChunkListResponse chunks = sktaiReposClient.getDocumentChunks(repoId, documentId, page, size, sort, filter,
                    search);

            log.info(
                    "SKTAI Knowledge Repository Document Chunks 목록 조회 완료 - repoId: {}, documentId: {}, 조회된 Chunk 수: {}",
                    repoId, documentId, chunks.getChunks() != null ? chunks.getChunks().size() : 0);
            return chunks;

        } catch (BusinessException e) {
            log.error(
                    "SKTAI Knowledge Repository Document Chunks 목록 조회 실패 (BusinessException) - repoId: {}, documentId: {}, message: {}",
                    repoId, documentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error(
                    "SKTAI Knowledge Repository Document Chunks 목록 조회 실패 (예상치 못한 오류) - repoId: {}, documentId: {}, error: {}",
                    repoId, documentId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "SKTAI Knowledge Repository Document Chunks 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // External Repository Import
    // ========================================

    /**
     * External Knowledge Repository Import
     * 
     * <p>
     * 외부에서 생성된 VectorDB Index를 조회하기 위한 External Knowledge Repository를 Import합니다.
     * 기존 External Repository의 설정과 데이터를 기반으로 새로운 Internal Repository를 생성합니다.
     * </p>
     * 
     * @param request External Repository Import 요청 정보
     * @return Import된 Repository ID
     * @throws BusinessException External Repository Import 실패 시
     */
    public RepoImportResponse importExternalRepository(RepoExtImportRequest request) {
        try {
            log.info("SKTAI External Knowledge Repository Import 시작 - id: {}, name: {}, vectorDbId: {}",
                    request.getId(), request.getName(), request.getVectorDbId());

            RepoImportResponse response = sktaiReposClient.importExternalRepo(request);

            log.info("SKTAI External Knowledge Repository Import 완료 - repoId: {}",
                    response.getRepoId());
            return response;

        } catch (feign.FeignException.NotFound e) {
            log.error("SKTAI External Repository를 찾을 수 없음 - id: {}, error: {}", request.getId(), e.getMessage());
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "External Repository를 찾을 수 없습니다: " + request.getId());
        } catch (feign.FeignException.Forbidden e) {
            log.error("SKTAI External Repository Import 권한 부족 - id: {}, error: {}", request.getId(), e.getMessage());
            throw new BusinessException(ErrorCode.FORBIDDEN,
                    "External Repository Import 권한이 없습니다");
        } catch (feign.FeignException.UnprocessableEntity e) {
            log.error("SKTAI External Repository Import 요청 데이터 검증 실패 - id: {}, error: {}", request.getId(),
                    e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR,
                    "External Repository Import 요청 데이터가 올바르지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("SKTAI External Knowledge Repository Import 실패 - id: {}, error: {}", request.getId(),
                    e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SKTAI External Knowledge Repository Import에 실패했습니다: " + e.getMessage());
        }
    }
}
