package com.skax.aiplatform.service.data.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.udp.elasticsearch.UdpElasticsearchClient;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.dto.data.response.SourceSystemInfo;
import com.skax.aiplatform.service.data.DataCtlgSourceSystemService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 원천 시스템 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataCtlgSourceSystemServiceImpl implements DataCtlgSourceSystemService {

    private final UdpElasticsearchClient udpElasticsearchClient;
    private final ObjectMapper objectMapper;

    /**
     * 오류 발생 시 기본 옵션만 반환
     *
     * @return 기본 원천 시스템 목록 (전체만 포함)
     */
    private List<SourceSystemInfo> createDefaultSourceSystems() {
        log.warn("오류로 인해 기본 옵션만 반환합니다.");
        return Arrays.asList(
                SourceSystemInfo.builder()
                        .value("ALL")
                        .label("전체")
                        .description("전체 시스템")
                        .build());
    }

    @Override
    public List<SourceSystemInfo> getSourceSystems() {
        log.info("🚀 [Service] 원천 시스템 목록 조회 요청 - UDP Elasticsearch에서 조회");

        try {
            // Elasticsearch 검색 쿼리 - 전체 데이터 조회
            Map<String, Object> searchQuery = new HashMap<>();
            searchQuery.put("size", 1000); // 최대 1000개 조회
            searchQuery.put("_source", Arrays.asList("datasetcard_refer_cd", "datasetcard_refer_nm"));

            Map<String, Object> query = new HashMap<>();
            query.put("match_all", new HashMap<>());
            searchQuery.put("query", query);

            log.info("📤 [Service] Elasticsearch 검색 쿼리: {}", objectMapper.writeValueAsString(searchQuery));

            // UDP Elasticsearch에서 데이터 조회
            log.info("🔌 [Service] UDP Elasticsearch 호출 시작 - 인덱스: udp_srch_datasetcard");
            SearchResponse response = udpElasticsearchClient.searchData("udp_srch_datasetcard", searchQuery);
            log.info("📥 [Service] UDP Elasticsearch 응답 수신 완료");

            log.debug("Elasticsearch 응답 - 총 {} 건", response.getTotalHits());

            // 중복 제거를 위한 Map (referCd -> referNm)
            Map<String, String> uniqueSources = new LinkedHashMap<>();

            // 응답에서 원천 시스템 목록 추출 및 중복 제거
            if (response.getHits() != null && !response.getHits().isEmpty()) {
                for (Map<String, Object> hit : response.getHits()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> source = (Map<String, Object>) hit.get("_source");
                    if (source != null) {
                        String referCd = (String) source.get("datasetcard_refer_cd");
                        String referNm = (String) source.get("datasetcard_refer_nm");

                        if (referCd != null && referNm != null && !uniqueSources.containsKey(referCd)) {
                            uniqueSources.put(referCd, referNm);
                            log.debug("원천 시스템 발견: {} - {}", referCd, referNm);
                        }
                    }
                }
            }

            // 결과 리스트 생성
            List<SourceSystemInfo> sourceSystems = new ArrayList<>();

            // "전체" 옵션 추가
            sourceSystems.add(SourceSystemInfo.builder()
                    .value("ALL")
                    .label("전체")
                    .description("전체 시스템")
                    .build());

            // unique한 원천 시스템 추가
            for (Map.Entry<String, String> entry : uniqueSources.entrySet()) {
                sourceSystems.add(SourceSystemInfo.builder()
                        .value(entry.getKey())
                        .label(entry.getValue())
                        .description(entry.getValue() + " 시스템")
                        .build());

                log.info("✅ 원천 시스템 추가: {} - {}", entry.getKey(), entry.getValue());
            }

            log.info("✅ [Service] 원천 시스템 목록 조회 완료: {} 개 (전체 포함)", sourceSystems.size());
            log.info("📋 [Service] 최종 결과: {}", sourceSystems);
            return sourceSystems;

        } catch (BusinessException e) {
            log.error("❌ [Service] 원천 시스템 목록 조회 중 BusinessException 발생 - 오류: {}", e.getMessage(), e);
            return createDefaultSourceSystems();
        } catch (FeignException e) {
            log.error("❌ [Service] 원천 시스템 목록 조회 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}", 
                    e.status(), e.getMessage(), e.contentUTF8(), e);
            return createDefaultSourceSystems();
        } catch (RuntimeException e) {
            log.error("❌ [Service] 원천 시스템 목록 조회 중 RuntimeException 발생 - 오류: {}", e.getMessage(), e);
            return createDefaultSourceSystems();
        } catch (Exception e) {
            log.error("❌ [Service] 원천 시스템 목록 조회 중 예상치 못한 오류 발생 - 오류: {}", e.getMessage(), e);
            return createDefaultSourceSystems();
        }
    }
}
