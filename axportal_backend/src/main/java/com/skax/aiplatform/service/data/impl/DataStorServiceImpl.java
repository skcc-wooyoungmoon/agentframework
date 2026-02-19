package com.skax.aiplatform.service.data.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skax.aiplatform.client.udp.dataset.dto.request.DatasetSearchRequest;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetSearchResponse;
import com.skax.aiplatform.client.udp.dataset.service.UdpDatasetService;
import com.skax.aiplatform.client.udp.document.dto.request.DocumentSearchRequest;
import com.skax.aiplatform.client.udp.document.dto.response.DocumentSearchResponse;
import com.skax.aiplatform.client.udp.document.service.UdpDocumentService;
import com.skax.aiplatform.client.udp.elasticsearch.dto.request.SearchRequest;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.client.udp.elasticsearch.service.UdpElasticsearchService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import feign.FeignException;
import com.skax.aiplatform.dto.data.request.DataStorDatasetSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorDocumentSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorTrainEvalSearchReq;
import com.skax.aiplatform.dto.data.response.DataStorDatasetRes;
import com.skax.aiplatform.dto.data.response.DataStorDocumentRes;
import com.skax.aiplatform.dto.data.response.DataStorTrainEvalRes;
import com.skax.aiplatform.client.udp.dataset.dto.response.UdpEsDatasetAggregationResponse;
import com.skax.aiplatform.mapper.data.DataStorMapper;
import com.skax.aiplatform.service.data.DataStorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 데이터 저장소 서비스 구현체
 *
 * @author 장지원
 * @version 1.0.0
 * @since 2025-10-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataStorServiceImpl implements DataStorService {

    private final UdpDatasetService udpDatasetService;
    private final UdpDocumentService udpDocumentService;
    private final UdpElasticsearchService udpElasticsearchService;
    private final DataStorMapper dataStorMapper;

    // JSON 로깅을 위한 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // es 조회 인덱스 이름 (학습/평가 데이터)
    private final String esIndexName = "gaf_datasets";

    /**
     * 공통 예외 처리 메서드
     *
     * @param operation 작업 설명
     * @param e 발생한 예외
     * @return RuntimeException (BusinessException으로 변환)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            log.error("❌ DataStor {} 중 BusinessException 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            FeignException feignEx = (FeignException) e;
            log.error("❌ DataStor {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}",
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    String.format("%s 중 오류가 발생했습니다: HTTP %d - %s", operation, feignEx.status(), feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            log.error("❌ DataStor {} 중 런타임 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    String.format("%s 중 오류가 발생했습니다: %s", operation, e.getMessage()));
        } else {
            log.error("❌ DataStor {} 중 예상치 못한 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    String.format("%s 중 오류가 발생했습니다: %s", operation, e.getMessage()));
        }
    }

    @Override
    public PageResponse<DataStorDatasetRes> getDatasets(DataStorDatasetSearchReq request) {
        log.info("데이터셋 검색 서비스 호출 - searchWord: {}, originSystemCd: {}, page: {}, countPerPage: {}",
                request.getSearchWord(), request.getOriginSystemCd(), request.getPage(), request.getCountPerPage());

        try {
            // 프론트 REQ -> UDP REQ
            DatasetSearchRequest udpRequest = dataStorMapper.toUdpRequest(request);
            // 데이터셋 카드유형을 지식데이터셋으로 한정 (학습, 평가데이터 제외)
            udpRequest.setDatasetCardType("DATS");
            log.info(">>>MD Package : 프론트 REQ -> UDP REQ 결과 :\n{}", toJsonString(udpRequest));

            // UDP 데이터셋 서비스 호출 (API Key는 내부에서 설정값 사용)
            DatasetSearchResponse udpResponse = udpDatasetService.searchDataset(udpRequest);
            log.info(">>>MD Package : UDP 서비스 호출 결과 RES :\n{}", toJsonString(udpResponse));

            // UDP 서비스 호출 결과 RES -> 프론트로 가는 RES (페이징)
            PageResponse<DataStorDatasetRes> pageResponse = dataStorMapper.toPageResponse(udpResponse, request);
            log.info(">>>MD Package : UDP 서비스 호출 결과 RES -> 프론트로 가는 RES (페이징) :\n{}", toJsonString(pageResponse));

            log.info(
                    ">>>MD Package: UDP 데이터셋 검색 완료 - totalElements: {}, totalPages: {}, currentPage: {}, returnedItems: {}",
                    pageResponse.getTotalElements(), pageResponse.getTotalPages(),
                    pageResponse.getPageable().getPage() + 1, pageResponse.getContent().size());

            return pageResponse;

        } catch (BusinessException e) {
            throw handleException("데이터셋 검색", e);
        } catch (FeignException e) {
            throw handleException("데이터셋 검색", e);
        } catch (RuntimeException e) {
            throw handleException("데이터셋 검색", e);
        } catch (Exception e) {
            throw handleException("데이터셋 검색", e);
        }
    }

    @Override
    public PageResponse<DataStorDocumentRes> getDatasetsDocuments(DataStorDocumentSearchReq request) {
        log.info("문서 검색 서비스 호출 - datasetCd: {}, searchWord: {}, page: {}, countPerPage: {}",
                request.getDatasetCd(), request.getSearchWord(), request.getPage(), request.getCountPerPage());

        try {
            // 프론트 REQ -> UDP Document REQ
            DocumentSearchRequest udpDocRequest = dataStorMapper.toUdpDocumentRequest(request);
            log.info(">>>Document : 프론트 REQ -> UDP Document REQ DTO 변환 결과 REQ:\n{}", toJsonString(udpDocRequest));

            // UDP 문서 검색 서비스 호출
            DocumentSearchResponse udpDocResponse = udpDocumentService.searchDocuments(udpDocRequest);
            log.info(">>>Document : UDP Document 서비스 호출 결과 RES :\n{}", toJsonString(udpDocResponse));

            // UDP Document 서비스 호출 결과 RES -> 프론트로 가는 RES (페이징)
            PageResponse<DataStorDocumentRes> pageResponse = dataStorMapper.toDocumentPageResponse(udpDocResponse,
                    request);
            log.info(">>>Document : UDP Document 서비스 호출 결과 RES -> 프론트로 가는 RES (페이징) :\n{}", toJsonString(pageResponse));

            log.info(
                    ">>>Document: UDP 문서 검색 완료 - totalElements: {}, totalPages: {}, currentPage: {}, returnedItems: {}",
                    pageResponse.getTotalElements(), pageResponse.getTotalPages(),
                    pageResponse.getPageable().getPage() + 1, pageResponse.getContent().size());

            return pageResponse;

        } catch (BusinessException e) {
            throw handleException("문서 검색", e);
        } catch (FeignException e) {
            throw handleException("문서 검색", e);
        } catch (RuntimeException e) {
            throw handleException("문서 검색", e);
        } catch (Exception e) {
            throw handleException("문서 검색", e);
        }
    }

    @Override
    public PageResponse<DataStorTrainEvalRes> getTrainEvalData(DataStorTrainEvalSearchReq request) {
        log.info(">>> 학습/평가 데이터셋 검색 서비스 호출 - cat01: {}, cat02: {}, title: {}, page: {}, countPerPage: {}",
                request.getCat01(), request.getCat02(), request.getTitle(), request.getPage(),
                request.getCountPerPage());

        try {
            // Elasticsearch 쿼리 바디 생성
            Map<String, Object> queryBody = buildElasticsearchQueryBody(request);
            log.info(">>>train/eval : ES 쿼리 바디 생성:\n{}", toJsonString(queryBody));

            // SearchRequest로 변환
            SearchRequest elasticRequest = dataStorMapper.convertToElasticsearchRequest(esIndexName, queryBody);
            log.info(">>>train/eval : 프론트 -> UDP Elasticsearch REQ:\n{}", toJsonString(elasticRequest));
            log.info(">>>Elasticsearch 검색 요청 변환 완료 - indexName: {}",
                    elasticRequest.getIndexName());

            // UdpElasticsearchService를 통한 검색 수행
            SearchResponse elasticResponse = udpElasticsearchService.searchData(elasticRequest);
            log.info(">>>train/eval : UDP Elasticsearch 조회 서비스 호출 결과 RES:\n{}", toJsonString(elasticResponse));
            
            List<DataStorTrainEvalRes> trainDataList = null;

            if (elasticResponse != null && elasticResponse.getHits() != null) {
                log.info(">>>Elasticsearch 검색 완료 - totalHits: {}, returnedHits: {}",
                        elasticResponse.getTotalHits(), elasticResponse.getHits().size());
                // Elasticsearch 결과를 DTO로 변환
                trainDataList = dataStorMapper.toDataStorTrainEvalResList(elasticResponse.getHits());
                log.info(">>>train/eval : UDP Elasticsearch RES -> List<> 로 변환:\n{}", toJsonString(trainDataList));
            } else {
                log.warn(">>>Elasticsearch 검색 응답이 null이거나 hits가 null - elasticResponse: {}",
                        elasticResponse != null ? "exists" : "null");
            }

            if(trainDataList != null && request != null) {
                // TITLE 기반 후처리 필터링
                int beforeCount = trainDataList.size();
                trainDataList = applyTitleFiltering(trainDataList, request);
                int afterCount = trainDataList.size();
                log.info(">>> train/eval : TITLE 필터링 결과 (Before: {}건 → After: {}건)", beforeCount, afterCount);
            }

            // PageResponse로 변환
            PageResponse<DataStorTrainEvalRes> pageResponse = dataStorMapper
                    .toTrainEvalDataPageResponse(elasticResponse, trainDataList, request);
            log.info(">>>train/eval : 프론트로 가는 RES (페이징):\n{}", toJsonString(pageResponse));

            log.info(">>>데이터셋 검색 완료 - totalElements: {}, totalPages: {}, currentPage: {}, returnedItems: {}",
                    pageResponse.getTotalElements(), pageResponse.getTotalPages(),
                    request.getPage(), pageResponse.getContent().size());

            return pageResponse;

        } catch (BusinessException e) {
            throw handleException("학습/평가 데이터셋 검색", e);
        } catch (FeignException e) {
            throw handleException("학습/평가 데이터셋 검색", e);
        } catch (RuntimeException e) {
            throw handleException("학습/평가 데이터셋 검색", e);
        } catch (Exception e) {
            throw handleException("학습/평가 데이터셋 검색", e);
        }
    }

    /**
     * Elasticsearch 쿼리 바디 생성
     *
     * @param request 검색 요청
     * @return Elasticsearch 쿼리 바디 Map
     */
    private Map<String, Object> buildElasticsearchQueryBody(DataStorTrainEvalSearchReq request) {
        Map<String, Object> queryBody = new HashMap<>();

        // size 설정
        int size = request.getCountPerPage() != null ? request.getCountPerPage().intValue() : 20;
        queryBody.put("size", size);

        // from 설정 (페이징을 위한 offset)
        int page = request.getPage() != null ? request.getPage().intValue() : 1;
        int from = (page - 1) * size;
        queryBody.put("from", from);


        // 생성일시 기준 내림차순으로 정렬(최신순)
        java.util.List<Object> sort = new java.util.ArrayList<>();
        sort.add(java.util.Collections.singletonMap("FST_CREATED_AT",
                java.util.Collections.singletonMap("order", "desc")));
        queryBody.put("sort", sort);


        // track_total_hits를 true로 설정하여 전체 개수를 정확히 추적
        queryBody.put("track_total_hits", true);

        // 쿼리 설정
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> bool = new HashMap<>();
        List<Map<String, Object>> must = new ArrayList<>();

        // CAT01 필터 (cat01 → DATASET_CAT01)
        if (request.getCat01() != null && !request.getCat01().trim().isEmpty()) {
            Map<String, Object> cat01Match = new HashMap<>();
            Map<String, Object> cat01Value = new HashMap<>();
            cat01Value.put("DATASET_CAT01", request.getCat01());
            cat01Match.put("match", cat01Value);
            must.add(cat01Match);
        }

        // CAT02 필터 (cat02 → DATASET_CAT02)
        if (request.getCat02() != null && !request.getCat02().trim().isEmpty()) {
            Map<String, Object> cat02Match = new HashMap<>();
            Map<String, Object> cat02Value = new HashMap<>();
            cat02Value.put("DATASET_CAT02", request.getCat02());
            cat02Match.put("match", cat02Value);
            must.add(cat02Match);
        }

        // 최소한 하나의 조건이 있어야 함
        if (must.isEmpty()) {
            // 조건이 없으면 match_all 사용
            query.put("match_all", new HashMap<>());
        } else {
            // 조건이 있으면 bool query 사용
            bool.put("must", must);
            query.put("bool", bool);
        }

        queryBody.put("query", query);

        return queryBody;
    }

    /**
     * TITLE 기반 후처리 필터링
     *
     * @param trainEvalDataList 원본 데이터 목록
     * @param request           검색 요청 (title 필터 포함)
     * @return 필터링된 데이터 목록
     */
    private List<DataStorTrainEvalRes> applyTitleFiltering(List<DataStorTrainEvalRes> trainEvalDataList,
            DataStorTrainEvalSearchReq request) {
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            String titleFilter = request.getTitle().trim().toLowerCase();

            return trainEvalDataList.stream()
                    .filter(data -> {
                        String title = data.getTitle();
                        if (title == null) {
                            return false;
                        }
                        // 대소문자 구분 없이 부분 일치 검색
                        return title.toLowerCase().contains(titleFilter);
                    })
                    .collect(java.util.stream.Collectors.toList());
        }

        return trainEvalDataList;
    }

    /**
     * 객체를 JSON 문자열로 변환하는 헬퍼 메서드 (로깅을 위함)
     */
    private String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            log.warn("JSON 변환 실패 (RuntimeException) - 오류: {}", e.getMessage());
            return "JSON 변환 실패: " + obj.toString();
        } catch (Exception e) {
            log.warn("JSON 변환 실패 (Exception) - 오류: {}", e.getMessage());
            return "JSON 변환 실패: " + obj.toString();
        }
    }

    @Override
    public UdpEsDatasetAggregationResponse getOriginSystems() {
        return udpDatasetService.searchUdpEsDatasetAggregation();
    }
}