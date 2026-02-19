package com.skax.aiplatform.client.elastic.search.service;

import com.skax.aiplatform.client.elastic.search.ElasticSearchClient;
import com.skax.aiplatform.client.elastic.search.dto.request.IndexRequest;
import com.skax.aiplatform.client.elastic.search.dto.request.SearchRequest;
import com.skax.aiplatform.client.elastic.search.dto.response.IndexResponse;
import com.skax.aiplatform.client.elastic.search.dto.response.SearchResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Elasticsearch 검색 및 인덱싱 서비스
 * 
 * <p>Elasticsearch 클라이언트를 래핑하여 비즈니스 로직과 에러 처리를 담당합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticSearchService {

    private final ElasticSearchClient elasticSearchClient;

    /**
     * 인덱스 전체 검색
     * 
     * @param index 인덱스명
     * @param query 검색 쿼리
     * @return 검색 결과
     */
    public SearchResponse searchIndex(String index, String query) {
        log.info("Elasticsearch 인덱스 검색 시작 - index: {}, query: {}", index, query);
        
        try {
            SearchResponse response = elasticSearchClient.searchIndex(index, query);
            Long totalHits = extractTotalHits(response);
            log.info("Elasticsearch 인덱스 검색 성공 - index: {}, hits: {}", 
                    index, totalHits);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 인덱스 검색 실패 (BusinessException) - index: {}, query: {}, message: {}", 
                    index, query, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 인덱스 검색 실패 (예상치 못한 오류) - index: {}, query: {}", 
                    index, query, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 검색에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 검색 (크기 제한)
     * 
     * @param index 인덱스명
     * @param query 검색 쿼리
     * @param size 검색 결과 수
     * @return 검색 결과
     */
    public SearchResponse searchDocuments(String index, String query, Integer size) {
        log.info("Elasticsearch 문서 검색 시작 - index: {}, query: {}, size: {}", index, query, size);
        
        try {
            SearchResponse response = elasticSearchClient.searchDocuments(index, query, size);
            Long totalHits = extractTotalHits(response);
            log.info("Elasticsearch 문서 검색 성공 - index: {}, hits: {}", 
                    index, totalHits);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 문서 검색 실패 (BusinessException) - index: {}, query: {}, size: {}, message: {}", 
                    index, query, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 문서 검색 실패 (예상치 못한 오류) - index: {}, query: {}, size: {}", 
                    index, query, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 문서 검색에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 고급 문서 검색 (페이징)
     * 
     * @param index 인덱스명
     * @param query 검색 쿼리
     * @param size 검색 결과 수
     * @param fromIndex 시작 위치
     * @return 검색 결과
     */
    public SearchResponse searchAdvanced(String index, String query, Integer size, Integer fromIndex) {
        log.info("Elasticsearch 고급 검색 시작 - index: {}, query: {}, size: {}, from: {}", 
                index, query, size, fromIndex);
        
        try {
            SearchResponse response = elasticSearchClient.searchAdvanced(index, query, size, fromIndex);
            Long totalHits = extractTotalHits(response);
            log.info("Elasticsearch 고급 검색 성공 - index: {}, hits: {}", 
                    index, totalHits);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 고급 검색 실패 (BusinessException) - index: {}, query: {}, size: {}, from: {}, message: {}", 
                    index, query, size, fromIndex, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 고급 검색 실패 (예상치 못한 오류) - index: {}, query: {}, size: {}, from: {}", 
                    index, query, size, fromIndex, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 고급 검색에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * DSL 쿼리 검색
     * 
     * @param index 인덱스명
     * @param searchRequest 검색 요청
     * @return 검색 결과
     */
    public SearchResponse searchWithDsl(String index, SearchRequest searchRequest) {
        log.info("Elasticsearch DSL 검색 시작 - index: {}", index);
        log.debug("SearchRequest 내용: {}", searchRequest);
        
        try {
            SearchResponse response = elasticSearchClient.searchWithDsl(index, searchRequest);
            Long totalHits = extractTotalHits(response);
            log.info("Elasticsearch DSL 검색 성공 - index: {}, hits: {}", 
                    index, totalHits);
            return response;
        } catch (FeignException e) {
            // 404 에러는 우회 (빈 응답 반환)
            if (e.status() == 404) {
                log.debug("Elasticsearch DSL 검색 - 인덱스/문서를 찾을 수 없음 - index: {}, 우회 처리", index);
                return SearchResponse.builder()
                        .took(0)
                        .timedOut(false)
                        .hits(SearchResponse.HitsContainer.builder()
                                .total(SearchResponse.TotalHits.builder()
                                        .value(0L)
                                        .relation("eq")
                                        .build())
                                .maxScore(0.0)
                                .hits(java.util.Collections.emptyList())
                                .build())
                        .build();
            }
            // 404가 아닌 FeignException은 BusinessException으로 변환
            log.error("Elasticsearch DSL 검색 실패 (FeignException) - index: {}, status: {}, message: {}", 
                    index, e.status(), e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch DSL 검색에 실패했습니다: " + e.getMessage());
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch DSL 검색 실패 (BusinessException) - index: {}, message: {}", 
                    index, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch DSL 검색 실패 (예상치 못한 오류) - index: {}", 
                    index, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch DSL 검색에 실패했습니다: " + e.getMessage());
        }
    }


    /**
     * 다중 인덱스 검색
     * 
     * @param index 인덱스명 (콤마로 구분)
     * @param searchRequest 검색 요청
     * @param allowNoIndices 인덱스가 없을 때 허용 여부
     * @return 검색 결과
     */
    public SearchResponse searchMultiIndex(String index, SearchRequest searchRequest, Boolean allowNoIndices) {
        log.info("Elasticsearch 다중 인덱스 검색 시작 - index: {}, allowNoIndices: {}", index, allowNoIndices);
        
        try {
            SearchResponse response = elasticSearchClient.searchMultiIndex(index, searchRequest, allowNoIndices);
            Long totalHits = extractTotalHits(response);
            log.info("Elasticsearch 다중 인덱스 검색 성공 - index: {}, hits: {}", 
                    index, totalHits);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 다중 인덱스 검색 실패 (BusinessException) - index: {}, allowNoIndices: {}, message: {}", 
                    index, allowNoIndices, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 다중 인덱스 검색 실패 (예상치 못한 오류) - index: {}, allowNoIndices: {}", 
                    index, allowNoIndices, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 다중 인덱스 검색에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 인덱싱
     * 
     * @param index 인덱스명
     * @param indexRequest 인덱싱 요청
     * @return 인덱싱 결과
     */
    public IndexResponse indexDocument(String index, IndexRequest indexRequest) {
        log.info("Elasticsearch 문서 인덱싱 시작 - index: {}", index);
        
        try {
            IndexResponse response = elasticSearchClient.indexDocument(index, indexRequest);
            String docId = Optional.ofNullable(response.getId()).orElse("unknown");
            String result = Optional.ofNullable(response.getResult()).orElse("unknown");
            log.info("Elasticsearch 문서 인덱싱 성공 - index: {}, id: {}, result: {}", 
                    index, docId, result);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 문서 인덱싱 실패 (BusinessException) - index: {}, message: {}", 
                    index, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 문서 인덱싱 실패 (예상치 못한 오류) - index: {}", 
                    index, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 문서 인덱싱에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 인덱싱 (ID 지정)
     * 
     * @param index 인덱스명
     * @param id 문서 ID
     * @param indexRequest 인덱싱 요청
     * @return 인덱싱 결과
     */
    public IndexResponse indexDocumentWithId(String index, String id, IndexRequest indexRequest) {
        log.info("Elasticsearch 문서 인덱싱 시작 (ID 지정) - index: {}, id: {}", index, id);
        
        try {
            IndexResponse response = elasticSearchClient.indexDocumentWithId(index, id, indexRequest);
            String result = Optional.ofNullable(response.getResult()).orElse("unknown");
            log.info("Elasticsearch 문서 인덱싱 성공 (ID 지정) - index: {}, id: {}, result: {}", 
                    index, id, result);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 문서 인덱싱 실패 (BusinessException, ID 지정) - index: {}, id: {}, message: {}", 
                    index, id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 문서 인덱싱 실패 (예상치 못한 오류, ID 지정) - index: {}, id: {}", 
                    index, id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 문서 인덱싱에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 업데이트
     * 
     * @param index 인덱스명
     * @param id 문서 ID
     * @param indexRequest 업데이트 요청
     * @param refresh 리프레시 여부
     * @return 업데이트 결과
     */
    public IndexResponse updateDocument(String index, String id, IndexRequest indexRequest, Boolean refresh) {
        log.info("Elasticsearch 문서 업데이트 시작 - index: {}, id: {}, refresh: {}", index, id, refresh);
        
        try {
            IndexResponse response = elasticSearchClient.updateDocument(index, id, indexRequest, refresh);
            String result = Optional.ofNullable(response.getResult()).orElse("unknown");
            log.info("Elasticsearch 문서 업데이트 성공 - index: {}, id: {}, result: {}", 
                    index, id, result);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 ElasticErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Elasticsearch 문서 업데이트 실패 (BusinessException) - index: {}, id: {}, refresh: {}, message: {}", 
                    index, id, refresh, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Elasticsearch 문서 업데이트 실패 (예상치 못한 오류) - index: {}, id: {}, refresh: {}", 
                    index, id, refresh, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Elasticsearch 문서 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SearchResponse에서 총 히트 수를 안전하게 추출
     * 
     * <p>널 포인터 역참조를 방지하기 위해 Optional 체이닝을 사용합니다.</p>
     * 
     * @param response 검색 응답
     * @return 총 히트 수 (널인 경우 0 반환)
     */
    private Long extractTotalHits(SearchResponse response) {
        return Optional.ofNullable(response)
                .map(SearchResponse::getHits)
                .map(hits -> hits.getTotal())
                .map(total -> total.getValue())
                .orElse(0L);
    }
}