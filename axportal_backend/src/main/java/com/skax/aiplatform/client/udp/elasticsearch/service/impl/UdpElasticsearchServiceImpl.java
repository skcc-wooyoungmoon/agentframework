package com.skax.aiplatform.client.udp.elasticsearch.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.udp.elasticsearch.UdpElasticsearchClient;
import com.skax.aiplatform.client.udp.elasticsearch.dto.request.IndexCreateRequest;
import com.skax.aiplatform.client.udp.elasticsearch.dto.request.SearchRequest;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexCreateResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexExistsResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexListResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.client.udp.elasticsearch.service.UdpElasticsearchService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.response.PageableInfo;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeChunksReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeFilesReq;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeChunksRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeFilesRes;

import feign.FeignException;
// removed typed item DTO usages to use generic maps in responses
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UDP Elasticsearch 서비스 구현체
 * 
 * <p>
 * UDP Elasticsearch API를 호출하여 Index 관리 기능을 제공합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UdpElasticsearchServiceImpl implements UdpElasticsearchService {

    private static final String WAIT_FOR_ACTIVE_SHARDS_ALL = "1";

    private final UdpElasticsearchClient udpElasticsearchClient;

    @Value("${udp.elasticsearch.index.number_of_shards}")
    private String number_of_shards;

    @Value("${udp.elasticsearch.index.number_of_replicas}")
    private String number_of_replicas;

    /**
     * 공통 예외 처리 메서드
     * 
     * <p>
     * 외부 API 호출 시 발생하는 예외를 일관된 방식으로 처리합니다.
     * </p>
     * 
     * @param operation 작업 설명 (예: "Index 목록 조회", "Index 생성" 등)
     * @param e         발생한 예외
     * @return 변환된 비즈니스 예외 (항상 BusinessException)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            // ErrorDecoder에서 변환된 BusinessException (HTTP 응답이 있는 경우: 400, 401, 403, 404,
            // 422, 500 등)
            log.error("❌ UDP Elasticsearch {} 중 BusinessException 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            // HTTP 응답이 없는 경우 (연결 실패, 타임아웃 등) 또는 ErrorDecoder를 거치지 않은 FeignException
            // FeignException의 상세 정보(status, content, request)를 활용할 수 있음
            FeignException feignEx = (FeignException) e;
            log.error("❌ UDP Elasticsearch {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}",
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    String.format("UDP Elasticsearch API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(),
                            feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            // 기타 런타임 예외
            log.error("❌ UDP Elasticsearch {} 중 런타임 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "UDP Elasticsearch API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ UDP Elasticsearch {} 중 예상치 못한 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "UDP Elasticsearch API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public IndexListResponse listIndices() {
        log.info("🔍 [UDP Elasticsearch] Index 목록 조회 시작");

        try {
            IndexListResponse response = udpElasticsearchClient.listIndices("json");
            log.info("✅ [UDP Elasticsearch] Index 목록 조회 성공 - 개수: {}",
                    response.getIndices() != null ? response.getIndices().size() : 0);
            return response;
        } catch (BusinessException e) {
            throw handleException("Index 목록 조회", e);
        } catch (FeignException e) {
            throw handleException("Index 목록 조회", e);
        } catch (RuntimeException e) {
            throw handleException("Index 목록 조회", e);
        } catch (Exception e) {
            throw handleException("Index 목록 조회", e);
        }
    }

    @Override
    public Boolean indexExists(String indexName) {
        log.info("🔍 [UDP Elasticsearch] Index 존재 여부 확인 - indexName: {}", indexName);

        try {
            IndexExistsResponse response = udpElasticsearchClient.indexExists(indexName);
            boolean exists = response != null && response.getIndices() != null && !response.getIndices().isEmpty();
            log.info("✅ [UDP Elasticsearch] Index 존재 여부 확인 완료 - indexName: {}, exists: {}",
                    indexName, exists);
            return exists;
        } catch (BusinessException e) {
            // 404는 Index가 없다는 의미
            if (e.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                log.info("ℹ️ [UDP Elasticsearch] Index 없음 - indexName: {}", indexName);
                return false;
            }
            log.error("❌ [UDP Elasticsearch] Index 존재 여부 확인 실패 - indexName: {}", indexName, e);
            throw e;
        } catch (FeignException e) {
            throw handleException("Index 존재 여부 확인", e);
        } catch (RuntimeException e) {
            throw handleException("Index 존재 여부 확인", e);
        } catch (Exception e) {
            throw handleException("Index 존재 여부 확인", e);
        }
    }

    @Override
    public IndexCreateResponse createIndexWithSettings(
            String indexName,
            Map<String, Object> mappings,
            Map<String, Object> settings) {

        log.info("🚀 [UDP Elasticsearch] Index 생성 시작 (상세 설정) - indexName: {}", indexName);

        try {
            IndexCreateRequest request = IndexCreateRequest.builder()
                    .mappings(mappings)
                    .settings(settings)
                    .build();

            // 📋 요청 JSON 로깅 (디버깅용)
            try {
                ObjectMapper mapper = new ObjectMapper();
                String requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
                log.info("📋 [UDP Elasticsearch] Index 생성 요청 JSON:\n{}", requestJson);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.warn("⚠️ Index 생성 요청 JSON 변환 실패 (JsonProcessingException) - 오류: {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("⚠️ Index 생성 요청 JSON 변환 실패 (RuntimeException) - 오류: {}", e.getMessage());
            } catch (Exception e) {
                log.warn("⚠️ Index 생성 요청 JSON 변환 실패 (Exception) - 오류: {}", e.getMessage());
            }

            IndexCreateResponse response = udpElasticsearchClient.createIndex(indexName, request,
                    WAIT_FOR_ACTIVE_SHARDS_ALL);

            log.info("✅ [UDP Elasticsearch] Index 생성 성공 - indexName: {}, acknowledged: {}",
                    indexName, response.getAcknowledged());
            return response;
        } catch (BusinessException e) {
            throw handleException("Index 생성 (상세 설정)", e);
        } catch (FeignException e) {
            throw handleException("Index 생성 (상세 설정)", e);
        } catch (RuntimeException e) {
            throw handleException("Index 생성 (상세 설정)", e);
        } catch (Exception e) {
            throw handleException("Index 생성 (상세 설정)", e);
        }
    }

    @Override
    public void deleteIndex(String indexName) {
        log.info("🗑️ [UDP Elasticsearch] Index 삭제 시작 - indexName: {}", indexName);

        try {
            udpElasticsearchClient.deleteIndex(indexName);
            log.info("✅ [UDP Elasticsearch] Index 삭제 성공 - indexName: {}", indexName);
        } catch (BusinessException e) {
            throw handleException("Index 삭제", e);
        } catch (FeignException e) {
            throw handleException("Index 삭제", e);
        } catch (RuntimeException e) {
            throw handleException("Index 삭제", e);
        } catch (Exception e) {
            throw handleException("Index 삭제", e);
        }
    }

    @Override
    public SearchResponse searchData(SearchRequest request) {
        log.info(">>> [UDP Elasticsearch] 데이터 검색 시작 - indexName: {}",
                request.getIndexName());

        try {
            // 쿼리 바디
            Object queryBody = request.getQueryBody();

            try {
                ObjectMapper pretty = new ObjectMapper();
                String bodyJson = pretty.writerWithDefaultPrettyPrinter().writeValueAsString(queryBody);
                log.info(
                        "\n===== [UDP ES] Request Body (Pretty JSON) =====\n{}\n==============================================",
                        bodyJson);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.debug("[UDP ES] Request Body pretty-print 실패 (JsonProcessingException), toString으로 대체: {}",
                        String.valueOf(queryBody));
            } catch (RuntimeException e) {
                log.debug("[UDP ES] Request Body pretty-print 실패 (RuntimeException), toString으로 대체: {}",
                        String.valueOf(queryBody));
            } catch (Exception e) {
                log.debug("[UDP ES] Request Body pretty-print 실패 (Exception), toString으로 대체: {}",
                        String.valueOf(queryBody));
            }

            // Feign 클라이언트를 통한 검색
            SearchResponse searchResponse = udpElasticsearchClient.searchData(
                    request.getIndexName(),
                    queryBody);

            if (searchResponse != null && searchResponse.getHits() != null) {
                log.info(">>> [UDP Elasticsearch] 데이터 검색 성공 - indexName: {}, totalHits: {}, returnedHits: {}",
                        request.getIndexName(), searchResponse.getTotalHits(), searchResponse.getHits().size());
            } else {
                log.warn(">>> [UDP Elasticsearch] 데이터 검색 응답이 null이거나 hits가 null - indexName: {}, searchResponse: {}",
                        request.getIndexName(), searchResponse != null ? "exists" : "null");
            }

            try {
                ObjectMapper pretty = new ObjectMapper();
                String respJson = pretty.writerWithDefaultPrettyPrinter().writeValueAsString(searchResponse);
                log.info(
                        "\n===== [UDP ES] Full Response (Pretty JSON) =====\n{}\n===============================================",
                        respJson);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.debug("[UDP ES] Response pretty-print 실패 (JsonProcessingException)");
            } catch (RuntimeException e) {
                log.debug("[UDP ES] Response pretty-print 실패 (RuntimeException)");
            } catch (Exception e) {
                log.debug("[UDP ES] Response pretty-print 실패 (Exception)");
            }

            return searchResponse;

        } catch (BusinessException e) {
            throw handleException("데이터 검색", e);
        } catch (FeignException e) {
            throw handleException("데이터 검색", e);
        } catch (RuntimeException e) {
            throw handleException("데이터 검색", e);
        } catch (Exception e) {
            throw handleException("데이터 검색", e);
        }
    }

    @Override
    public IndexResponse insertData(String indexName, Object document) {
        log.info(">>> [UDP Elasticsearch] 데이터 삽입 시작 - indexName: {}", indexName);

        try {
            IndexResponse response = udpElasticsearchClient.insertData(indexName, document);
            log.info(">>> [UDP Elasticsearch] 데이터 삽입 성공 - indexName: {}, id: {}, result: {}",
                    indexName, response.getId(), response.getResult());
            return response;
        } catch (BusinessException e) {
            throw handleException("데이터 삽입", e);
        } catch (FeignException e) {
            throw handleException("데이터 삽입", e);
        } catch (RuntimeException e) {
            throw handleException("데이터 삽입", e);
        } catch (Exception e) {
            throw handleException("데이터 삽입", e);
        }
    }

    @Override
    public IndexCreateResponse createIndexForKnowledge(String indexName, Integer dimension) {
        // dimension이 null이면 기본값 2048 사용
        if (dimension == null) {
            dimension = 2048;
        }
        log.info("[UDP Elasticsearch] 지식용 인덱스 생성 시작 - indexName: {}, dimension: {}", indexName, dimension);

        try {
            // 1. Settings 설정
            Map<String, Object> settings = new HashMap<>();
            Map<String, Object> indexSettings = new HashMap<>();
            indexSettings.put("number_of_shards", number_of_shards);
            indexSettings.put("number_of_replicas", number_of_replicas);
            settings.put("index", indexSettings);

            // 2. Mappings 설정
            Map<String, Object> mappings = new HashMap<>();
            mappings.put("dynamic", false);

            Map<String, Object> properties = new HashMap<>();

            // doc_dataset_cd (keyword)
            Map<String, Object> docDatasetCdField = new HashMap<>();
            docDatasetCdField.put("type", "keyword");
            properties.put("doc_dataset_cd", docDatasetCdField);

            // doc_dataset_nm (keyword)
            Map<String, Object> docDatasetNmField = new HashMap<>();
            docDatasetNmField.put("type", "keyword");
            properties.put("doc_dataset_nm", docDatasetNmField);

            // doc_uuid (keyword)
            Map<String, Object> docUuidField = new HashMap<>();
            docUuidField.put("type", "keyword");
            properties.put("doc_uuid", docUuidField);

            // doc_nm (keyword with text sub-field)
            Map<String, Object> docNmField = new HashMap<>();
            docNmField.put("type", "keyword");
            Map<String, Object> docNmFields = new HashMap<>();
            Map<String, Object> docNmTextField = new HashMap<>();
            docNmTextField.put("type", "text");
            docNmFields.put("text", docNmTextField);
            docNmField.put("fields", docNmFields);
            properties.put("doc_nm", docNmField);

            // doc_refer_cd (keyword)
            Map<String, Object> docReferCdField = new HashMap<>();
            docReferCdField.put("type", "keyword");
            properties.put("doc_refer_cd", docReferCdField);

            // doc_refer_nm (keyword)
            Map<String, Object> docReferNmField = new HashMap<>();
            docReferNmField.put("type", "keyword");
            properties.put("doc_refer_nm", docReferNmField);

            // doc_path_anony (keyword)
            Map<String, Object> docPathAnonyField = new HashMap<>();
            docPathAnonyField.put("type", "keyword");
            properties.put("doc_path_anony", docPathAnonyField);

            // doc_keyword_list (keyword)
            Map<String, Object> docKeywordListField = new HashMap<>();
            docKeywordListField.put("type", "keyword");
            properties.put("doc_keyword_list", docKeywordListField);

            // doc_summary (text)
            Map<String, Object> docSummaryField = new HashMap<>();
            docSummaryField.put("type", "text");
            properties.put("doc_summary", docSummaryField);

            // doc_origin_metadata (object)
            Map<String, Object> docOriginMetadataField = new HashMap<>();
            docOriginMetadataField.put("type", "object");
            properties.put("doc_origin_metadata", docOriginMetadataField);

            // chunk_id (keyword)
            Map<String, Object> chunkIdField = new HashMap<>();
            chunkIdField.put("type", "keyword");
            properties.put("chunk_id", chunkIdField);

            // chunk_seq (keyword with long sub-field)
            Map<String, Object> chunkSeqField = new HashMap<>();
            chunkSeqField.put("type", "keyword");
            Map<String, Object> chunkSeqFields = new HashMap<>();
            Map<String, Object> chunkSeqNumField = new HashMap<>();
            chunkSeqNumField.put("type", "long");
            chunkSeqNumField.put("ignore_malformed", true);
            chunkSeqFields.put("num", chunkSeqNumField);
            chunkSeqField.put("fields", chunkSeqFields);
            properties.put("chunk_seq", chunkSeqField);

            // chunk_conts (text)
            Map<String, Object> chunkContsField = new HashMap<>();
            chunkContsField.put("type", "text");
            properties.put("chunk_conts", chunkContsField);

            // chunk_embedding (dense_vector)
            Map<String, Object> chunkEmbeddingField = new HashMap<>();
            chunkEmbeddingField.put("type", "dense_vector");
            chunkEmbeddingField.put("dims", dimension);
            chunkEmbeddingField.put("index", true);
            chunkEmbeddingField.put("similarity", "cosine");
            properties.put("chunk_embedding", chunkEmbeddingField);

            // doc_attach_uuids (keyword)
            Map<String, Object> docAttachUuidsField = new HashMap<>();
            docAttachUuidsField.put("type", "keyword");
            properties.put("doc_attach_uuids", docAttachUuidsField);

            // doc_attach_yn (keyword)
            Map<String, Object> docAttachYnField = new HashMap<>();
            docAttachYnField.put("type", "keyword");
            properties.put("doc_attach_yn", docAttachYnField);

            // doc_mig_yn (keyword)
            Map<String, Object> docMigYnField = new HashMap<>();
            docMigYnField.put("type", "keyword");
            properties.put("doc_mig_yn", docMigYnField);

            // doc_mig_expire_date (date)
            Map<String, Object> docMigExpireDateField = new HashMap<>();
            docMigExpireDateField.put("type", "date");
            docMigExpireDateField.put("format", "yyyyMMdd");
            properties.put("doc_mig_expire_date", docMigExpireDateField);

            // doc_parent_uuid (keyword)
            Map<String, Object> docParentUuidField = new HashMap<>();
            docParentUuidField.put("type", "keyword");
            properties.put("doc_parent_uuid", docParentUuidField);

            // chunk_created_by (keyword)
            Map<String, Object> chunkCreatedByField = new HashMap<>();
            chunkCreatedByField.put("type", "keyword");
            properties.put("chunk_created_by", chunkCreatedByField);

            // chunk_updated_by (keyword)
            Map<String, Object> chunkUpdatedByField = new HashMap<>();
            chunkUpdatedByField.put("type", "keyword");
            properties.put("chunk_updated_by", chunkUpdatedByField);

            // chunk_fst_created_at (date)
            Map<String, Object> chunkFstCreatedAtField = new HashMap<>();
            chunkFstCreatedAtField.put("type", "date");
            chunkFstCreatedAtField.put("format", "yyyy-MM-dd HH:mm:ss");
            properties.put("chunk_fst_created_at", chunkFstCreatedAtField);

            // chunk_lst_updated_at (date)
            Map<String, Object> chunkLstUpdatedAtField = new HashMap<>();
            chunkLstUpdatedAtField.put("type", "date");
            chunkLstUpdatedAtField.put("format", "yyyy-MM-dd HH:mm:ss");
            properties.put("chunk_lst_updated_at", chunkLstUpdatedAtField);

            // doc_create_day (date)
            Map<String, Object> documentCreateDay = new HashMap<>();
            documentCreateDay.put("type", "date");
            documentCreateDay.put("format", "yyyyMMdd");
            properties.put("doc_create_day", documentCreateDay);

            // doc_mdfcn_day (date)
            Map<String, Object> docuemntModifyedContentDay = new HashMap<>();
            docuemntModifyedContentDay.put("type", "date");
            docuemntModifyedContentDay.put("format", "yyyyMMdd");
            properties.put("doc_mdfcn_day", docuemntModifyedContentDay);

            // 21. doc_id (keyword)
            Map<String, Object> docIdField = new HashMap<>();
            docIdField.put("type", "keyword");
            properties.put("doc_id", docIdField);

            mappings.put("properties", properties);

            // 3. 인덱스 생성
            IndexCreateResponse response = createIndexWithSettings(indexName, mappings, settings);

            log.info("[UDP Elasticsearch] 지식용 인덱스 생성 성공 - indexName: {}, acknowledged: {}",
                    indexName, response.getAcknowledged());
            return response;

        } catch (BusinessException e) {
            throw handleException("지식용 인덱스 생성", e);
        } catch (FeignException e) {
            throw handleException("지식용 인덱스 생성", e);
        } catch (RuntimeException e) {
            throw handleException("지식용 인덱스 생성", e);
        } catch (Exception e) {
            throw handleException("지식용 인덱스 생성", e);
        }
    }

    @Override
    public ExternalKnowledgeFilesRes searchFilesAggregated(ExternalKnowledgeFilesReq request) {
        String indexName = request.getIndexName();
        Integer page = request.getPage();
        Integer countPerPage = request.getCountPerPage();
        String search = request.getSearch();
        String uuid = request.getUuid();

        log.info(
                ">>> [UDP Elasticsearch] doc_path_anony 집계 페이지 조회 - indexName: {}, page: {}, countPerPage: {}, search: {}, uuid: {}",
                indexName, page, countPerPage, search, uuid);

        if (page == null || page < 1)
            page = 1;
        if (countPerPage == null || countPerPage < 1)
            countPerPage = 12;

        try {
            // 1) 전체 고유 개수 (cardinality, 근사값)
            Map<String, Object> countBody = new HashMap<>();
            countBody.put("size", 0); // 실제 문서 말고 개수만
            Map<String, Object> aggsCount = new HashMap<>();
            Map<String, Object> cardinality = new HashMap<>();
            cardinality.put("field", "doc_path_anony"); // 이 필드의 고유한 값 개수
            aggsCount.put("unique_paths_count", Map.of("cardinality", cardinality)); // 중복 제거를 위해 cardinality 사용
            countBody.put("aggs", aggsCount);

            // 검색어 "search(파일명)"가 주어진 경우 doc_nm.name 필드에 대해 검색하는 쿼리 구성
            Map<String, Object> query = new HashMap<>();
            if (search != null && !search.trim().isEmpty()) {
                query = Map.of(
                        "wildcard", Map.of(
                                "doc_nm", Map.of(
                                        "value", "*" + search + "*")));
                // 검색어 uuid일 경우, uuid필드에 대해 검색하는 쿼리 구성
            } else if (uuid != null && !uuid.trim().isEmpty()) {
                query = Map.of(
                        "wildcard", Map.of(
                                "doc_uuid", Map.of(
                                        "value", "*" + uuid + "*")));
            } else {
                query = Map.of("match_all", new HashMap<>());
            }
            countBody.put("query", query);
            // cardinality aggregation에는 from/size 불필요 (size: 0만 있으면 됨)

            log.info(
                    ">>> [UDP Elasticsearch] doc_path_anony 집계 페이지 조회 - indexName: {}, page: {}, countPerPage: {}, query: {}",
                    indexName, page, countPerPage, query);
            log.info(">>> [UDP Elasticsearch] countBody: {}", countBody);

            SearchResponse countResp = udpElasticsearchClient.searchData(indexName, countBody);

            log.info(">>> [UDP Elasticsearch] countResp: {}", countResp);

            Long totalCount = 0L;
            try {
                Object aggObj = countResp.getAggregations().get("unique_paths_count");
                if (aggObj instanceof Map) {
                    Object value = ((Map<?, ?>) aggObj).get("value");
                    if (value instanceof Number)
                        totalCount = ((Number) value).longValue();
                }
            } catch (ClassCastException e) {
                log.warn("[UDP ES] cardinality 총계 파싱 실패 (ClassCastException), 0으로 처리");
            } catch (NullPointerException e) {
                log.warn("[UDP ES] cardinality 총계 파싱 실패 (NullPointerException), 0으로 처리");
            } catch (RuntimeException e) {
                log.warn("[UDP ES] cardinality 총계 파싱 실패 (RuntimeException), 0으로 처리");
            } catch (Exception e) {
                log.warn("[UDP ES] cardinality 총계 파싱 실패 (Exception), 0으로 처리");
            }

            // 2) composite agg로 페이지 찾아가기 (after_key 반복)
            Map<String, Object> afterKey = null;
            List<ExternalKnowledgeFilesRes.Item> pageBuckets = new java.util.ArrayList<>();

            for (int currentPage = 1; currentPage <= page; currentPage++) {
                Map<String, Object> comp = new HashMap<>();
                comp.put("size", countPerPage);
                java.util.List<Map<String, Object>> sources = new java.util.ArrayList<>();
                sources.add(Map.of("doc_path_anony", Map.of("terms", Map.of("field", "doc_path_anony"))));
                comp.put("sources", sources);
                if (afterKey != null)
                    comp.put("after", afterKey);

                // sub-aggregation: top_hits
                Map<String, Object> topHits = new HashMap<>();
                topHits.put("size", 1);
                topHits.put("_source", Boolean.TRUE);

                Map<String, Object> uniqueDocs = new HashMap<>();
                uniqueDocs.put("composite", comp);
                uniqueDocs.put("aggs", java.util.Map.of("top_doc", java.util.Map.of("top_hits", topHits)));

                Map<String, Object> aggs = new HashMap<>();
                aggs.put("unique_docs", uniqueDocs);

                Map<String, Object> body = new HashMap<>();
                body.put("size", 0);
                body.put("query", query); // 첫 번째 호출과 동일한 검색 필터 적용
                body.put("aggs", aggs);

                SearchResponse resp = udpElasticsearchClient.searchData(indexName, body);

                Object uniquePathsObj = resp.getAggregations().get("unique_docs");
                if (!(uniquePathsObj instanceof Map)) {
                    break;
                }
                Map<?, ?> aggsMap = (Map<?, ?>) uniquePathsObj;
                // LBG if (aggsMap == null) break;

                Object bucketsObj = aggsMap.get("buckets");
                java.util.List<?> buckets = (bucketsObj instanceof java.util.List) ? (java.util.List<?>) bucketsObj
                        : java.util.Collections.emptyList();
                Object afterKeyObj = aggsMap.get("after_key");
                if (afterKeyObj instanceof Map) {
                    afterKey = new java.util.HashMap<>();
                    for (Object k : ((Map<?, ?>) afterKeyObj).keySet()) {
                        afterKey.put(String.valueOf(k), ((Map<?, ?>) afterKeyObj).get(k));
                    }
                } else {
                    afterKey = null;
                }

                if (currentPage == page) {
                    if (buckets != null) {
                        for (Object b : buckets) {
                            if (b instanceof Map) {
                                Map<?, ?> bm = (Map<?, ?>) b;
                                String docPath = null;
                                Long docCount = null;
                                String topIndex = null;
                                String topId = null;
                                Double topScore = null;
                                Object topSourceObj = null;
                                try {
                                    Object keyObj = bm.get("key");
                                    if (keyObj instanceof Map) {
                                        Object path = ((Map<?, ?>) keyObj).get("doc_path_anony");
                                        if (path != null)
                                            docPath = String.valueOf(path);
                                    }
                                    Object dc = bm.get("doc_count");
                                    if (dc instanceof Number)
                                        docCount = ((Number) dc).longValue();

                                    Object topDoc = bm.get("top_doc");
                                    if (topDoc instanceof Map) {
                                        Object hitsObj = ((Map<?, ?>) topDoc).get("hits");
                                        if (hitsObj instanceof Map) {
                                            Object innerHitsObj = ((Map<?, ?>) hitsObj).get("hits");
                                            if (innerHitsObj instanceof java.util.List
                                                    && !((java.util.List<?>) innerHitsObj).isEmpty()) {
                                                Object firstHit = ((java.util.List<?>) innerHitsObj).get(0);
                                                if (firstHit instanceof Map) {
                                                    Object idx = ((Map<?, ?>) firstHit).get("_index");
                                                    Object id = ((Map<?, ?>) firstHit).get("_id");
                                                    Object score = ((Map<?, ?>) firstHit).get("_score");
                                                    if (idx != null)
                                                        topIndex = String.valueOf(idx);
                                                    if (id != null)
                                                        topId = String.valueOf(id);
                                                    if (score instanceof Number)
                                                        topScore = ((Number) score).doubleValue();
                                                    try {
                                                        Object sourceObj = ((Map<?, ?>) firstHit).get("_source");
                                                        if (sourceObj instanceof Map) {
                                                            ((Map<?, ?>) sourceObj).remove("chunk_embedding");
                                                            topSourceObj = sourceObj;
                                                        }
                                                    } catch (ClassCastException e) {
                                                        log.debug(
                                                                "[UDP ES] chunk_embedding sanitize 실패 (ClassCastException), 무시합니다.");
                                                    } catch (NullPointerException e) {
                                                        log.debug(
                                                                "[UDP ES] chunk_embedding sanitize 실패 (NullPointerException), 무시합니다.");
                                                    } catch (UnsupportedOperationException e) {
                                                        log.debug(
                                                                "[UDP ES] chunk_embedding sanitize 실패 (UnsupportedOperationException), 무시합니다.");
                                                    } catch (RuntimeException e) {
                                                        log.debug(
                                                                "[UDP ES] chunk_embedding sanitize 실패 (RuntimeException), 무시합니다.");
                                                    } catch (Exception e) {
                                                        log.debug(
                                                                "[UDP ES] chunk_embedding sanitize 실패 (Exception), 무시합니다.");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (ClassCastException e) {
                                    log.debug("[UDP ES] 버킷 처리 중 ClassCastException 발생, 무시합니다.");
                                } catch (NullPointerException e) {
                                    log.debug("[UDP ES] 버킷 처리 중 NullPointerException 발생, 무시합니다.");
                                } catch (RuntimeException e) {
                                    log.debug("[UDP ES] 버킷 처리 중 런타임 오류 발생, 무시합니다.");
                                } catch (Exception e) {
                                    log.debug("[UDP ES] 버킷 처리 중 예상치 못한 오류 발생, 무시합니다.");
                                }

                                ExternalKnowledgeFilesRes.Item item = ExternalKnowledgeFilesRes.Item.builder()
                                        .docPathAnony(docPath)
                                        .docCount(docCount)
                                        .topIndex(topIndex)
                                        .topId(topId)
                                        .topScore(topScore)
                                        .topSource(topSourceObj)
                                        .build();
                                pageBuckets.add(item);
                            }
                        }
                    }
                }

                if (buckets == null || buckets.isEmpty() || afterKey == null)
                    break;
            }

            int totalPages = (int) Math.ceil((totalCount != null ? totalCount : 0L) / (double) countPerPage);
            boolean first = page == 1;
            boolean last = totalPages == 0 ? true : page >= totalPages;
            boolean hasNext = !last;
            boolean hasPrevious = !first && totalPages > 0;

            PageableInfo pageableInfo = PageableInfo.builder()
                    .page(page - 1) // 0-based 내부 표준
                    .size(countPerPage)
                    .sort("")
                    .build();

            PageResponse<ExternalKnowledgeFilesRes.Item> pageRes = PageResponse.<ExternalKnowledgeFilesRes.Item>builder()
                    .content(pageBuckets)
                    .pageable(pageableInfo)
                    .totalElements(totalCount != null ? totalCount : 0L)
                    .totalPages(totalPages)
                    .first(first)
                    .last(last)
                    .hasNext(hasNext)
                    .hasPrevious(hasPrevious)
                    .build();

            return ExternalKnowledgeFilesRes.builder().page(pageRes).build();

        } catch (BusinessException e) {
            throw handleException("집계 페이지 조회", e);
        } catch (FeignException e) {
            throw handleException("집계 페이지 조회", e);
        } catch (RuntimeException e) {
            throw handleException("집계 페이지 조회", e);
        } catch (Exception e) {
            throw handleException("집계 페이지 조회", e);
        }
    }

    @Override
    public ExternalKnowledgeChunksRes searchChunksByFile(ExternalKnowledgeChunksReq request) {
        String indexName = request.getIndexName();
        String docPathAnony = request.getDocPathAnony();
        Integer page = request.getPage();
        Integer countPerPage = request.getCountPerPage();
        log.info(
                ">>> [UDP Elasticsearch] 파일별 청크 조회 (페이징) - indexName: {}, doc_path_anony: {}, page: {}, countPerPage: {}",
                indexName, docPathAnony, page, countPerPage);

        if (page == null || page < 1)
            page = 1;
        if (countPerPage == null || countPerPage < 1)
            countPerPage = 12;

        try {
            int from = (page - 1) * countPerPage;

            Map<String, Object> query = new HashMap<>();
            Map<String, Object> term = new HashMap<>();
            Map<String, Object> termField = new HashMap<>();
            termField.put("value", docPathAnony);
            term.put("doc_path_anony", termField);
            query.put("term", term);

            Map<String, Object> body = new HashMap<>();
            body.put("query", query);

            java.util.List<Object> sort = new java.util.ArrayList<>();
            sort.add(java.util.Collections.singletonMap("chunk_seq.num",
                    java.util.Collections.singletonMap("order", "asc")));
            body.put("sort", sort);

            body.put("from", from);
            body.put("size", countPerPage);

            SearchResponse resp = udpElasticsearchClient.searchData(indexName, body);

            long total = resp.getTotalHits() != null ? resp.getTotalHits() : 0L;
            int totalPages = (int) Math.ceil(total / (double) countPerPage);
            boolean first = page == 1;
            boolean last = totalPages == 0 ? true : page >= totalPages;
            boolean hasNext = !last;
            boolean hasPrevious = !first && totalPages > 0;

            java.util.List<ExternalKnowledgeChunksRes.Item> content = new java.util.ArrayList<>();
            java.util.List<java.util.Map<String, Object>> hits = resp.getHits();
            if (hits != null) {
                for (java.util.Map<String, Object> hit : hits) {
                    String idx = hit.get("_index") != null ? String.valueOf(hit.get("_index")) : null;
                    String id = hit.get("_id") != null ? String.valueOf(hit.get("_id")) : null;
                    Double score = null;
                    Object sc = hit.get("_score");
                    if (sc instanceof Number)
                        score = ((Number) sc).doubleValue();
                    Object source = hit.get("_source");
                    ExternalKnowledgeChunksRes.Item item = ExternalKnowledgeChunksRes.Item.builder()
                            .index(idx)
                            .id(id)
                            .score(score)
                            .source(source)
                            .build();
                    content.add(item);
                }
            }

            PageableInfo pageableInfo = PageableInfo.builder()
                    .page(page - 1)
                    .size(countPerPage)
                    .sort("chunk_seq.num,asc")
                    .build();

            PageResponse<ExternalKnowledgeChunksRes.Item> pageRes = PageResponse.<ExternalKnowledgeChunksRes.Item>builder()
                    .content(content)
                    .pageable(pageableInfo)
                    .totalElements(total)
                    .totalPages(totalPages)
                    .first(first)
                    .last(last)
                    .hasNext(hasNext)
                    .hasPrevious(hasPrevious)
                    .build();

            return ExternalKnowledgeChunksRes.builder().page(pageRes).build();

        } catch (BusinessException e) {
            throw handleException("파일별 청크 조회", e);
        } catch (FeignException e) {
            throw handleException("파일별 청크 조회", e);
        } catch (RuntimeException e) {
            throw handleException("파일별 청크 조회", e);
        } catch (Exception e) {
            throw handleException("파일별 청크 조회", e);
        }
    }
}
