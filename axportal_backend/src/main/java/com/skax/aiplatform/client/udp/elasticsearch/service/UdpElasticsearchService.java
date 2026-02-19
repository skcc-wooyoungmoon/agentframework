package com.skax.aiplatform.client.udp.elasticsearch.service;

import java.util.Map;

import com.skax.aiplatform.client.udp.elasticsearch.dto.request.SearchRequest;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexCreateResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexListResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeChunksReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeFilesReq;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeChunksRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeFilesRes;

/**
 * UDP Elasticsearch 서비스 인터페이스
 * 
 * <p>
 * UDP Elasticsearch API를 호출하여 Index 관리 기능을 제공합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
public interface UdpElasticsearchService {

    /**
     * Index 목록 조회
     * 
     * @return Index 목록
     */
    IndexListResponse listIndices();

    /**
     * Index 존재 여부 확인
     * 
     * @param indexName Index 이름
     * @return Index 존재 여부
     */
    Boolean indexExists(String indexName);

    /**
     * Index 생성 (상세 설정)
     * 
     * @param indexName Index 이름
     * @param mappings Mappings 설정
     * @param settings Settings 설정
     * @return Index 생성 결과
     */
    IndexCreateResponse createIndexWithSettings(
        String indexName, 
        Map<String, Object> mappings, 
        Map<String, Object> settings
    );

    /**
     * Index 삭제
     * 
     * @param indexName Index 이름
     */
    void deleteIndex(String indexName);

    /**
     * Elasticsearch 데이터 검색
     *
     * @param request 검색 요청 정보
     * @return 검색 결과
     */
    SearchResponse searchData(SearchRequest request);

    /**
     * 데이터 삽입
     * 
     * @param indexName Index 이름
     * @param document 데이터 내용
     * @return 삽입 결과
     */
    IndexResponse insertData(String indexName, Object document);

    /**
     * 지식용 Elasticsearch 인덱스 생성
     * 
     * @param indexName Index 이름
     * @param dimension Dense vector 차원 수 (기본값: 2048)
     * @return Index 생성 결과
     */
    IndexCreateResponse createIndexForKnowledge(String indexName, Integer dimension);

    /**
     * 파일 목록 조회
     *
     * @param request 파일 목록 조회 요청 정보
     * @return 페이지 정보와 bucket 목록을 포함한 응답
     */
    ExternalKnowledgeFilesRes searchFilesAggregated(ExternalKnowledgeFilesReq request);

    /**
     * 파일 별 청크 조회 (페이지네이션)
     *
     * @param request 파일 별 청크 조회 요청 정보
     * @return 표준 페이지네이션 응답 (ES hit 맵 원본 그대로, 단 _source.chunk_embedding 제거)
     */
    ExternalKnowledgeChunksRes searchChunksByFile(ExternalKnowledgeChunksReq request);
}

