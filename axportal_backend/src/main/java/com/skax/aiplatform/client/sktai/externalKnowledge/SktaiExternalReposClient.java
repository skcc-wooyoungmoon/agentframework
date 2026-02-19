package com.skax.aiplatform.client.sktai.externalKnowledge;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse;

import com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoListResponse;

/**
 * ADXP External Knowledge Repository 클라이언트
 * 
 * <p>ADXP API의 External Knowledge Repository 관련 엔드포인트를 호출하는 Feign Client입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 1.0
 */
@FeignClient(
    name = "sktaiExternalReposClient",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiExternalReposClient {

    /**
     * External Knowledge Repository 목록 조회
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준 (기본값: created_at,desc)
     * @param filter 필터 조건
     * @param search 검색 키워드 (이름 기준)
     * @return External Knowledge Repository 목록
     */
    @GetMapping("/api/v1/knowledge/repos/external")
    ExternalRepoListResponse getExternalRepos(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "sort", defaultValue = "created_at,desc") String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * External Knowledge Repository 생성 (multipart/form-data)
     * 
     * @param name External Repository 이름
     * @param description External Repository 설명
     * @param embeddingModelName 임베딩 모델 이름
     * @param vectorDbId Vector DB ID
     * @param indexName 인덱스 이름
     * @param scriptFile Retrieval Script 파일
     * @return 생성된 External Repository 정보
     */
    @PostMapping(value = "/api/v1/knowledge/repos/external", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    RepoResponse createExternalRepo(
        @RequestPart("name") String name,
        @RequestPart("description") String description,
        @RequestPart("embedding_model_name") String embeddingModelName,
        @RequestPart("vector_db_id") String vectorDbId,
        @RequestPart("index_name") String indexName,
        @RequestPart("script_file") MultipartFile scriptFile
    );

    /**
     * External Knowledge Repository 상세 조회
     * 
     * @param repoId External Repository ID
     * @return External Repository 상세 정보 (script 포함, Object로 반환)
     */
    @GetMapping("/api/v1/knowledge/repos/external/{repo_id}")
    Object getExternalRepo(@PathVariable("repo_id") String repoId);

    /**
     * External Knowledge Repository 테스트
     * 
     * @param embeddingModelName 임베딩 모델 서빙명
     * @param vectorDbId Vector DB ID
     * @param indexName 인덱스명
     * @param scriptFile Retrieval Script 파일
     * @param query 테스트 질의
     * @param retrievalOptions Retrieval 옵션(JSON 문자열)
     * @return 테스트 결과
     */
    @PostMapping(value = "/api/v1/knowledge/repos/external/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> testExternalRepo(
        @RequestPart("embedding_model_name") String embeddingModelName,
        @RequestPart("vector_db_id") String vectorDbId,
        @RequestPart("index_name") String indexName,
        @RequestPart("script_file") MultipartFile scriptFile,
        @RequestPart("query") String query,
        @RequestPart(value = "retrieval_options", required = false) String retrievalOptions
    );

    /**
     * External Knowledge Repository 수정 (multipart/form-data)
     * 
     * @param repoId External Repository ID
     * @param name Repository 이름
     * @param description Repository 설명
     * @param embeddingModelName 임베딩 모델명 (빈값 가능)
     * @param indexName 인덱스명 (빈값 가능)
     * @param scriptFile Script 파일 (MultipartFile, 선택)
     * @return 수정된 Repository 정보
     */
    @PutMapping(value = "/api/v1/knowledge/repos/external/{repo_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Object updateExternalRepo(
        @PathVariable("repo_id") String repoId,
        @RequestPart(value = "name", required = false) String name,
        @RequestPart(value = "description", required = false) String description,
        @RequestPart(value = "embedding_model_name", required = false) String embeddingModelName,
        @RequestPart(value = "index_name", required = false) String indexName,
        @RequestPart(value = "script_file", required = false) MultipartFile scriptFile
    );

    /**
     * External Knowledge Repository 삭제
     * 
     * @param repoId External Repository ID
     */
    @DeleteMapping("/api/v1/knowledge/repos/external/{repo_id}")
    void deleteExternalRepo(@PathVariable("repo_id") String repoId);
}



