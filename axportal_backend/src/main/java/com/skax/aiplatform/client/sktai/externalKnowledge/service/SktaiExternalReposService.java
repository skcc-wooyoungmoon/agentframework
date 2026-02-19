package com.skax.aiplatform.client.sktai.externalKnowledge.service;

import com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoListResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtTestRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoImportResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeTestResult;

/**
 * ADXP External Knowledge Repository 서비스
 * 
 * <p>ADXP API의 External Knowledge Repository 관련 비즈니스 로직을 처리하는 서비스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 1.0
 */
public interface SktaiExternalReposService {

    /**
     * External Knowledge Repository 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return External Knowledge Repository 목록
     */
    ExternalRepoListResponse getExternalRepos(Integer page, Integer size, String sort, String filter, String search);

    /**
     * External Knowledge Repository 생성
     * 
     * @param request External Repository 생성 요청
     * @return 생성된 External Repository 정보
     */
    RepoResponse createExternalRepo(RepoExtCreateRequest request);

    /**
     * External Knowledge Repository 상세 조회
     * 
     * @param repoId External Repository ID
     * @return External Repository 상세 정보 (script 포함)
     */
    Object getExternalRepo(String repoId);

    /**
     * External Knowledge Repository 테스트
     * 
     * @param request 테스트 요청 정보
     * @return 테스트 결과
     */
    ExternalKnowledgeTestResult testExternalRepo(RepoExtTestRequest request);

    /**
     * External Knowledge Repository 수정
     * 
     * @param repoId External Repository ID
     * @param name Repository 이름
     * @param description Repository 설명
     * @param script Script 내용
     * @param indexName 인덱스명
     * @return 수정된 Repository 정보
     */
    Object updateExternalRepo(String repoId, String name, String description, String script, String indexName);

    /**
     * External Knowledge Repository 삭제
     * 
     * @param repoId External Repository ID
     */
    void deleteExternalRepo(String repoId);

    /**
     * External Knowledge Repository Import
     * 
     * <p>외부에서 생성된 VectorDB Index를 조회하기 위한 External Knowledge Repository를 Import합니다.
     * 기존 External Repository의 설정과 데이터를 기반으로 새로운 Internal Repository를 생성합니다.</p>
     * 
     * @param request External Repository Import 요청 정보
     * @return Import된 Repository ID
     */
    RepoImportResponse importExternalRepo(RepoExtImportRequest request);
}



