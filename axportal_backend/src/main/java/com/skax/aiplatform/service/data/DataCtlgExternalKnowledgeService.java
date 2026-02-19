package com.skax.aiplatform.service.data;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoListResponse;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeCreateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeTestReq;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeUpdateReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeFilesReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeChunksReq;
import com.skax.aiplatform.dto.data.response.DataCtlgExternalKnowledgeCreateRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeTestResult;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeChunksRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeFilesRes;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuExecutionResponse;
import com.skax.aiplatform.client.udp.dataiku.dto.request.DataikuExecutionRequest;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.GetModelServingReq;

import java.util.List;

/**
 * DataCtlg External Knowledge 서비스 인터페이스
 * 
 * <p>External Knowledge Repository 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 2.0
 */
public interface DataCtlgExternalKnowledgeService {
    
    /**
     * External Knowledge Repository 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return External Knowledge Repository 목록
     */
    ExternalRepoListResponse getExternalRepos(Integer page, Integer size, String sort, String filter, String search);

    /**
     * External Knowledge 상세 조회 (DB + ADXP 통합)
     * 
     * @param knwId 지식 UUID (DB PK)
     * @return External Knowledge 상세 정보
     */
    Object getExternalKnowledge(String knwId);

    /**
     * External Knowledge 데이터 적재 현황 조회
     * 
     * @param knwId 지식 UUID (knwId 또는 expKnwId)
     * @return External Knowledge 데이터 적재 현황 (fileLoadProgress 포함)
     */
    Object getExternalKnowledgeProgress(String knwId);

    /**
     * External Knowledge 생성
     * 
     * @param request External Knowledge 생성 요청
     * @return External Knowledge 생성 응답
     */
    DataCtlgExternalKnowledgeCreateRes createExternalKnowledge(DataCtlgExternalKnowledgeCreateReq request);

    /**
     * External Knowledge 테스트
     * 
     * @param request External Knowledge 테스트 요청
     * @return 테스트 결과
     */
    ExternalKnowledgeTestResult testExternalKnowledge(DataCtlgExternalKnowledgeTestReq request);

    /**
     * External Knowledge 수정
     *
     * @param id 지식 ID (knwId 또는 expKnwId)
     * @param request 수정할 정보 (이름, 설명, 스크립트, 인덱스명)
     * @return 수정 결과
     */
    Object updateExternalKnowledge(String id, DataCtlgExternalKnowledgeUpdateReq request);

    /**
     * External Knowledge 삭제 (상세 정보 포함)
     * 
     * @param knwId 지식 UUID (DB PK)
     * @param expKnwId External Knowledge repo id (ADXP)
     * @param ragChunkIndexNm RAG chunk index명 (Elasticsearch)
     */
    void deleteExternalKnowledgeWithInfo(String knwId, String expKnwId, String ragChunkIndexNm);

    /**
     * Dataiku 실행
     */
    DataikuExecutionResponse executeDataiku(DataikuExecutionRequest request);

    /**
     * 파일 목록 조회
     */
    ExternalKnowledgeFilesRes getFiles(ExternalKnowledgeFilesReq request);

    /**
     * 파일별 청크 조회
     */
    ExternalKnowledgeChunksRes getFileChunks(ExternalKnowledgeChunksReq request);


    List<PolicyRequest> setKnowledgePolicy(String knowledgeId, String memberId, String projectName);


    /**
     * Agent 에서 지식 조회
     */
    Object getExternalKnowledgeByExternalKnowledgeId(String exknwId);

    /**
     * 임베딩 모델 목록 조회
     * 
     * @param request 페이지 및 필터 정보
     * @return 임베딩 모델 목록 (type:embedding 필터 적용)
     */
    PageResponse<ServingResponse> getEmbeddingModels(GetModelServingReq request);
}

