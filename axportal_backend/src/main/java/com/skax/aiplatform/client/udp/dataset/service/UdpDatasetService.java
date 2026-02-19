package com.skax.aiplatform.client.udp.dataset.service;

import com.skax.aiplatform.client.udp.dataset.UdpDatasetClient;
import com.skax.aiplatform.client.udp.dataset.dto.request.DatasetSearchRequest;
import com.skax.aiplatform.client.udp.dataset.dto.response.UdpEsDatasetAggregationResponse;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetSearchResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * UDP Dataset API 서비스
 * 
 * <p>
 * UDP Dataset 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * 데이터셋 검색 관련 API에 대한 서비스 메서드를 제공합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UdpDatasetService {

    private final UdpDatasetClient udpDatasetClient;

    @Value("${udp.api.auth.authorization-api-key}")
    private String authorizationApiKey;

    @Value("${udp.api.auth.dataset-search-key:}")
    private String datasetSearchKey;

    @Value("${udp.api.auth.dataset-2d-search-key:}")
    private String dataset2dSearchKey;


    /**
     * 빈 데이터셋 집계 응답 생성
     * 
     * <p>오류 발생 시 빈 응답 객체를 생성하여 반환합니다.</p>
     * 
     * @return 빈 데이터셋 집계 응답
     */
    private UdpEsDatasetAggregationResponse createEmptyAggregationResponse() {
        UdpEsDatasetAggregationResponse errorResponse = new UdpEsDatasetAggregationResponse();
        errorResponse.setDatasetReferList(new ArrayList<>());
        log.info(">>> 오류 발생으로 빈 응답 객체 생성");
        return errorResponse;
    }

    /**
     * 공통 예외 처리 메서드
     * 
     * <p>외부 API 호출 시 발생하는 예외를 일관된 방식으로 처리합니다.</p>
     * 
     * @param operation 작업 설명 (예: "데이터셋 검색", "데이터셋 집계 조회" 등)
     * @param e 발생한 예외
     * @return 변환된 비즈니스 예외 (항상 BusinessException)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            // ErrorDecoder에서 변환된 BusinessException (HTTP 응답이 있는 경우: 400, 401, 403, 404, 422, 500 등)
            log.error("❌ UDP Dataset {} 중 BusinessException 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            // HTTP 응답이 없는 경우 (연결 실패, 타임아웃 등) 또는 ErrorDecoder를 거치지 않은 FeignException
            // FeignException의 상세 정보(status, content, request)를 활용할 수 있음
            FeignException feignEx = (FeignException) e;
            log.error("❌ UDP Dataset {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}", 
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    String.format("UDP Dataset API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(), feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            // 기타 런타임 예외
            log.error("❌ UDP Dataset {} 중 런타임 오류 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "UDP Dataset API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ UDP Dataset {} 중 예상치 못한 오류 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "UDP Dataset API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 카드 검색
     * 
     * <p>
     * 검색어와 데이터셋 카드 타입을 기반으로 데이터셋을 검색합니다.
     * </p>
     * 
     * @param request 데이터셋 검색 요청 정보
     * @return 검색된 데이터셋 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public DatasetSearchResponse searchDataset(DatasetSearchRequest request) {
        try {
            // YAML 설정에서 x-cruz-api-key 가져오기
            String apiKey = datasetSearchKey;

            log.info(">>> UDP 데이터셋 검색 요청 - search_word: {}, origin_system_cd: {}, countPerPage: {}, page: {}",
                    request.getSearchWord(), request.getOriginSystemCd(), request.getCountPerPage(), request.getPage());

            log.info(">>> UDP API 전송 JSON 요청: {}", request);

            DatasetSearchResponse response = udpDatasetClient.searchDataset(
                    apiKey,
                    request);

            // 원본 응답 로깅
            log.info(">>> UDP 데이터셋 검색 원본 응답 - totalCount: {}, page: {}, resultLists size: {}",
                    response.getTotalCount(), response.getPage(),
                    response.getResultLists() != null ? response.getResultLists().size() : 0);

            if (response.getResultLists() != null && !response.getResultLists().isEmpty()) {
                log.info("🟠 UDP 데이터셋 첫 번째 결과 - datasetCardId: {}, datasetCardName: {}",
                        response.getResultLists().get(0).getDatasetCardId(),
                        response.getResultLists().get(0).getDatasetCardName());
            }

            log.info(">>> UDP 데이터셋 검색 성공 - searchWord: {}, 결과 건수: {}",
                    request.getSearchWord(),
                    response.getResultLists() != null ? response.getResultLists().size() : 0);

            return response;
        } catch (BusinessException e) {
            throw handleException("UDP 데이터셋 검색", e);
        } catch (FeignException e) {
            throw handleException("UDP 데이터셋 검색", e);
        } catch (RuntimeException e) {
            throw handleException("UDP 데이터셋 검색", e);
        } catch (Exception e) {
            throw handleException("UDP 데이터셋 검색", e);
        }
    }

    /**
     * UDP 엘라스틱서치 데이터셋 집계 조회
     * 
     * <p>
     * UDP Elasticsearch를 통해 데이터셋의 코드와 이름 목록을 집계하여 조회합니다.
     * </p>
     * <p>
     * FEIGN 클라이언트에서 YAML 설정값을 직접 사용합니다.
     * </p>
     * 
     * @return 추출된 데이터셋 참조 정보 목록
     */
    public UdpEsDatasetAggregationResponse searchUdpEsDatasetAggregation() {
        try {
            log.info(">>> UDP ES 데이터셋 집계 조회 시작");

            // aggregation 쿼리 구성
            Map<String, Object> requestBody = createAggregationRequestBody();

            // Authorization 헤더에 "ApiKey " 접두사 추가
            String authorizationHeader = "ApiKey " + authorizationApiKey;

            Map<String, Object> response = udpDatasetClient.searchUdpEsDatasetAggregation(
                    authorizationHeader, dataset2dSearchKey, requestBody);

            if (response != null) {
                log.info(">>> UDP ES 데이터셋 집계 조회 성공 - 응답 크기: {}", response.size());

                // 응답 구조 로깅
                if (response.containsKey("aggregations")) {
                    log.info(">>> UDP ES 응답에 aggregations 필드 존재");
                } else {
                    log.warn(">>> UDP ES 응답에 aggregations 필드가 없습니다.");
                }
            } else {
                log.warn(">>> UDP ES 데이터셋 집계 조회 결과가 null입니다.");
            }

            // 응답에서 datasetcard_refer_nm, datasetcard_refer_cd 추출
            UdpEsDatasetAggregationResponse aggregationResponse = new UdpEsDatasetAggregationResponse();
            
            if(response != null){
                aggregationResponse = extractDatasetReferInfo(response);
            }

            if (aggregationResponse != null && aggregationResponse.getDatasetReferList() != null) {
                log.info(">>> UDP ES 데이터셋 참조 정보 추출 완료 - 총 {}개 시스템",
                        aggregationResponse.getDatasetReferList().size());

                // 추출된 각 시스템 정보 로깅
                if (!aggregationResponse.getDatasetReferList().isEmpty()) {
                    log.info(">>> 추출된 원천 시스템 목록:");
                    aggregationResponse.getDatasetReferList().forEach(system -> {
                        log.info("시스템 정보 - 참조코드: {}, 참조명: {}",
                                system.getDatasetcardReferCd(),
                                system.getDatasetcardReferNm());
                    });
                } else {
                    log.warn(">>> 추출된 원천 시스템이 없습니다.");
                }
            } else {
                log.warn(">>> 데이터셋 참조 정보 추출 결과가 null이거나 빈 목록입니다.");
            }

            return aggregationResponse;
        } catch (BusinessException e) {
            log.error(">>> UDP ES 데이터셋 집계 조회 실패 - BusinessException: {}", e.getMessage(), e);
            return createEmptyAggregationResponse();
        } catch (FeignException e) {
            log.error(">>> UDP ES 데이터셋 집계 조회 실패 - FeignException: 상태코드: {}, 오류: {}", 
                    e.status(), e.getMessage(), e);
            return createEmptyAggregationResponse();
        } catch (RuntimeException e) {
            log.error(">>> UDP ES 데이터셋 집계 조회 실패 - 런타임 오류: {}", e.getMessage(), e);
            return createEmptyAggregationResponse();
        } catch (Exception e) {
            log.error(">>> UDP ES 데이터셋 집계 조회 실패 - 오류 유형: {}, 메시지: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            return createEmptyAggregationResponse();
        }
    }

    /**
     * UDP Elasticsearch 응답에서 데이터셋 참조 정보 추출
     * 
     * @param esResponse UDP Elasticsearch 응답
     * @return 추출된 데이터셋 참조 정보
     */
    @SuppressWarnings("unchecked")
    private UdpEsDatasetAggregationResponse extractDatasetReferInfo(Map<String, Object> esResponse) {
        log.info(">>> UDP ES 응답에서 데이터셋 참조 정보 추출 시작");

        UdpEsDatasetAggregationResponse response = new UdpEsDatasetAggregationResponse();
        List<UdpEsDatasetAggregationResponse.DatasetReferInfo> referInfoList = new ArrayList<>();

        try {
            // aggregations -> origin_system_list -> buckets 에서 key 배열 추출
            Map<String, Object> aggregations = (Map<String, Object>) esResponse.get("aggregations");
            if (aggregations != null) {
                log.info(">>> UDP ES 응답에서 aggregations 필드 발견");

                Map<String, Object> originSystemList = (Map<String, Object>) aggregations.get("origin_system_list");
                if (originSystemList != null) {
                    log.info(">>> UDP ES 응답에서 origin_system_list 필드 발견");

                    List<Map<String, Object>> buckets = (List<Map<String, Object>>) originSystemList.get("buckets");
                    if (buckets != null && !buckets.isEmpty()) {
                        log.info(">>> UDP ES 응답에서 buckets 발견 - 총 {}개 버킷", buckets.size());

                        for (Map<String, Object> bucket : buckets) {
                            if (bucket == null) {
                                continue;
                            }

                            try {
                                Object keyObj = bucket.get("key");
                                if (!(keyObj instanceof List)) {
                                    continue;
                                }

                                List<String> key = (List<String>) keyObj;
                                
                                // key가 2개 이상이고, null이 아닌지 확인
                                if (key == null || key.size() < 2) {
                                    continue;
                                }

                                String referNm = key.get(0);
                                String referCd = key.get(1);

                                // 빈 값 체크
                                if (referNm == null || referNm.trim().isEmpty() || 
                                    referCd == null || referCd.trim().isEmpty()) {
                                    continue;
                                }

                                // XC 필터링
                                if ("XC".equals(referCd)) {
                                    log.info(">>> 로컬(수기업로드)-XC 정보 제외 - 참조명: {}, 참조코드: {}", referNm, referCd);
                                    continue;
                                }

                                UdpEsDatasetAggregationResponse.DatasetReferInfo referInfo = 
                                    new UdpEsDatasetAggregationResponse.DatasetReferInfo();
                                referInfo.setDatasetcardReferNm(referNm);
                                referInfo.setDatasetcardReferCd(referCd);
                                referInfoList.add(referInfo);

                                log.info("  📋 시스템 정보 추출 완료 - 참조명: {}, 참조코드: {}", referNm, referCd);
                            } catch (ClassCastException e) {
                                // keyObj를 List로 캐스팅할 때 발생
                                log.warn("  ⚠️ 버킷 처리 중 ClassCastException 발생 - key: {}, 오류: {}", 
                                        bucket.get("key"), e.getMessage());
                                continue;
                            } catch (IndexOutOfBoundsException e) {
                                // key.get(0) 또는 key.get(1)에서 발생
                                log.warn("  ⚠️ 버킷 처리 중 IndexOutOfBoundsException 발생 - 오류: {}", e.getMessage());
                                continue;
                            } catch (NullPointerException e) {
                                // null 값 처리 중 발생
                                log.warn("  ⚠️ 버킷 처리 중 NullPointerException 발생 - 오류: {}", e.getMessage());
                                continue;
                            } catch (RuntimeException e) {
                                // 기타 런타임 예외
                                log.warn("  ⚠️ 버킷 처리 중 런타임 오류 발생 - 오류: {}", e.getMessage());
                                continue;
                            } catch (Exception e) {
                                // 예상치 못한 예외 (checked exception 등)
                                log.warn("  ⚠️ 버킷 처리 중 예상치 못한 오류 발생 - 오류: {}", e.getMessage());
                                continue;
                            }
                        }
                    } else {
                        log.warn(">>> UDP ES 응답에서 buckets가 null입니다.");
                    }
                } else {
                    log.warn(">>> UDP ES 응답에서 origin_system_list가 null입니다.");
                }
            } else {
                log.warn(">>> UDP ES 응답에서 aggregations가 null입니다.");
            }

            response.setDatasetReferList(referInfoList);
            log.info(">>> UDP ES 데이터셋 참조 정보 추출 완료 - 총 {}개 시스템", referInfoList.size());

        } catch (ClassCastException e) {
            // Map이나 List로 캐스팅할 때 발생
            log.error(">>> UDP ES 데이터셋 참조 정보 추출 중 ClassCastException 발생 - 오류: {}", e.getMessage(), e);
            response.setDatasetReferList(new ArrayList<>());
        } catch (NullPointerException e) {
            // null 값 처리 중 발생
            log.error(">>> UDP ES 데이터셋 참조 정보 추출 중 NullPointerException 발생 - 오류: {}", e.getMessage(), e);
            response.setDatasetReferList(new ArrayList<>());
        } catch (RuntimeException e) {
            // 기타 런타임 예외
            log.error(">>> UDP ES 데이터셋 참조 정보 추출 중 런타임 오류 발생 - 오류: {}", e.getMessage(), e);
            response.setDatasetReferList(new ArrayList<>());
        } catch (Exception e) {
            // 예상치 못한 예외 (checked exception 등)
            log.error(">>> UDP ES 데이터셋 참조 정보 추출 중 예상치 못한 오류 발생 - 오류 유형: {}, 메시지: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            response.setDatasetReferList(new ArrayList<>());
        }

        return response;
    }

    /**
     * Aggregation 요청 본문 생성
     * 
     * @return aggregation 쿼리가 포함된 요청 본문
     */
    private Map<String, Object> createAggregationRequestBody() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("size", 0);

        // query 구성 추가
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> bool = new HashMap<>();
        List<Map<String, Object>> filter = new ArrayList<>();
        Map<String, Object> filter_term = new HashMap<>();
        Map<String, Object> filter_term_map = new HashMap<>();

        filter_term_map.put("datasetcard_type_cd", "DATS");
        filter_term.put("term", filter_term_map);
        filter.add(filter_term);
        bool.put("filter", filter);
        query.put("bool", bool);

        requestBody.put("query", query);

        // aggregations 구성
        Map<String, Object> aggregations = new HashMap<>();
        Map<String, Object> originSystemList = new HashMap<>();
        Map<String, Object> multiTerms = new HashMap<>();

        // terms 배열 구성
        List<Map<String, Object>> terms = new ArrayList<>();
        Map<String, Object> term1 = new HashMap<>();
        term1.put("field", "datasetcard_refer_nm");
        terms.add(term1);

        Map<String, Object> term2 = new HashMap<>();
        term2.put("field", "datasetcard_refer_cd");
        terms.add(term2);

        multiTerms.put("terms", terms);
        multiTerms.put("size", 20);

        originSystemList.put("multi_terms", multiTerms);
        aggregations.put("origin_system_list", originSystemList);

        requestBody.put("aggs", aggregations);

        log.info(">>>>>>>>>>>>>>>requestBody", requestBody);
        return requestBody;
    }

    /**
     * Form Body 생성 메서드 (리팩토링된 버전)
     * 
     * @param request 데이터셋 검색 요청
     * @return URL 인코딩된 Form Body 문자열
     */
    private String buildFormBody(DatasetSearchRequest request) {
        try {
            Map<String, Object> params = new LinkedHashMap<>();

            // 문자열 파라미터들
            addStringParam(params, "search_word", request.getSearchWord());
            addStringParam(params, "dataset_card_id", request.getDatasetCardId());
            addStringParam(params, "dataset_cd", request.getDatasetCd());
            addStringParam(params, "dataset_card_type", request.getDatasetCardType());
            addStringParam(params, "dataset_card_create_start", request.getDatasetCardCreateStart());
            addStringParam(params, "dataset_card_create_end", request.getDatasetCardCreateEnd());

            // 숫자 파라미터들
            addNumberParam(params, "count_per_page", request.getCountPerPage());
            addNumberParam(params, "page", request.getPage());

            //  리스프 파라미터
            addListParam(params, "origin_system_cd", request.getOriginSystemCd());

            String formBody = buildFormBodyFromMap(params);
            log.info(">>> Form Body 생성 완료 - 길이: {}, 내용: {}", formBody.length(), formBody);

            return formBody;

        } catch (NullPointerException e) {
            // null 값 처리 중 발생 가능
            log.error(">>> Form Body 생성 중 NullPointerException 발생 - 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Form Body 생성에 실패했습니다: 필수 파라미터가 null입니다.");
        } catch (RuntimeException e) {
            // 기타 런타임 예외 (ClassCastException 등)
            log.error(">>> Form Body 생성 중 런타임 오류 발생 - 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Form Body 생성에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            // 예상치 못한 예외 (checked exception 등)
            log.error(">>> Form Body 생성 중 예상치 못한 오류 발생 - 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Form Body 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문자열 파라미터 추가
     */
    private void addStringParam(Map<String, Object> params, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            params.put(key, value);
        }
    }

    /**
     * 숫자 파라미터 추가
     */
    private void addNumberParam(Map<String, Object> params, String key, Number value) {
        if (value != null) {
            params.put(key, value);
        }
    }

    /**
     * 리스트 파라미터 추가
     */
    private void addListParam(Map<String, Object> params, String key, List value) {
        if (value != null) {
            params.put(key, value);
        }
    }

    /**
     * Map에서 Form Body 문자열 생성
     */
    private String buildFormBodyFromMap(Map<String, Object> params) {
        return params.entrySet().stream()
                .map(entry -> {
                    try {
                        String value = entry.getValue() instanceof String
                                ? URLEncoder.encode((String) entry.getValue(), StandardCharsets.UTF_8)
                                : entry.getValue().toString();
                        return entry.getKey() + "=" + value;
                    } catch (IllegalArgumentException e) {
                        // URLEncoder.encode()에서 발생 가능 (하지만 StandardCharsets.UTF_8 사용 시 실제로는 발생하지 않음)
                        log.warn("파라미터 인코딩 실패 (IllegalArgumentException) - key: {}, value: {}, 오류: {}", 
                                entry.getKey(), entry.getValue(), e.getMessage());
                        return entry.getKey() + "=" + entry.getValue();
                    } catch (NullPointerException e) {
                        // entry.getValue()가 null인 경우
                        log.warn("파라미터 값이 null (NullPointerException) - key: {}", entry.getKey());
                        return entry.getKey() + "=";
                    } catch (RuntimeException e) {
                        // 기타 런타임 예외 (ClassCastException 등)
                        log.warn("파라미터 처리 실패 (RuntimeException) - key: {}, value: {}, 오류: {}", 
                                entry.getKey(), entry.getValue(), e.getMessage());
                        return entry.getKey() + "=" + entry.getValue();
                    } catch (Exception e) {
                        // 예상치 못한 예외 (checked exception 등)
                        log.warn("파라미터 처리 실패 (Exception) - key: {}, value: {}, 오류: {}", 
                                entry.getKey(), entry.getValue(), e.getMessage());
                        return entry.getKey() + "=" + entry.getValue();
                    }
                })
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }
}