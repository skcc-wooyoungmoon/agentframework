package com.skax.aiplatform.service.knowledge;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.udp.dataiku.UdpDataikuClient;
import com.skax.aiplatform.client.udp.dataiku.config.UdpDataikuProperties;
import com.skax.aiplatform.client.udp.elasticsearch.UdpElasticsearchClient;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.entity.knowledge.GpoKwlgInfoMas;
import com.skax.aiplatform.repository.knowledge.GpoKwlgInfoMasRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Data Pipeline 모니터링 서비스
 *
 * <p>Elasticsearch에서 Data Pipeline 실행 상태를 조회하고 DB를 업데이트하는 서비스입니다.</p>
 *
 * @author younglot
 * @since 2025-11-03
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipelineMonitorService {

    private static final String STATUS_INDEX_NAME = "gaf_syslog_rag-index-status";
    private static final String NORMAL = "정상";
    private static final String ERROR = "에러";

    private final GpoKwlgInfoMasRepository gpoKwlgInfoMasRepository;
    private final UdpElasticsearchClient udpElasticsearchClient;
    private final UdpDataikuClient udpDataikuClient;
    private final UdpDataikuProperties udpDataikuProperties;

    @Value("${udp.api.auth.authrization-bearer-token:}")
    private String authorizationBearerToken;

    @Value("${udp.api.auth.dataiku-continuous-key:}")
    private String apiKey;

    @Value("${spring.profiles.active:elocal}")
    private String activeProfile;

    //private final String STOPPED = "STOPPED";
    private final String STARTED = "STARTED";

    /**
     * 실행 중인 Data Pipeline 상태 모니터링 및 업데이트
     *
     * <p>data_pipeline_load_status = 'running'인 항목들을 조회하여
     * 각각의 실행 상태를 확인하고 DB를 업데이트합니다.</p>
     */
    @Transactional
    public void monitorAndUpdatePipelineStatus() {
//        log.info("[Data Pipeline Monitor] 모니터링 시작");

        // 1. DB에서 실행 중인 파이프라인 목록 조회 (status = 'running')
        List<GpoKwlgInfoMas> runningPipelines =
                gpoKwlgInfoMasRepository.findRunningPipelines();

//        log.info("[Data Pipeline Monitor] 실행 중인 파이프라인 개수: {}", runningPipelines.size());

        // 2. 각 파이프라인의 상태 확인 및 업데이트
        int successCount = 0;
        int failCount = 0;

        for (GpoKwlgInfoMas pipeline : runningPipelines) {
            try {
                updatePipelineStatus(pipeline);
                successCount++;

            } catch (BusinessException e) {
                log.error("[Data Pipeline Monitor] 파이프라인 상태 업데이트 실패 (BusinessException) - " +
                                "실행ID: {}, 에러: {}",
                        pipeline.getDataPipelineExeId(), e.getMessage(), e);
                failCount++;
            } catch (FeignException e) {
                log.error("[Data Pipeline Monitor] 파이프라인 상태 업데이트 실패 (FeignException) - " +
                                "실행ID: {}, 상태코드: {}, 에러: {}",
                        pipeline.getDataPipelineExeId(), e.status(), e.getMessage(), e);
                failCount++;
            } catch (RuntimeException e) {
                log.error("[Data Pipeline Monitor] 파이프라인 상태 업데이트 실패 (RuntimeException) - " +
                                "실행ID: {}, 에러: {}",
                        pipeline.getDataPipelineExeId(), e.getMessage(), e);
                failCount++;
            } catch (Exception e) {
                log.error("[Data Pipeline Monitor] 파이프라인 상태 업데이트 실패 (Exception) - " +
                                "실행ID: {}, 에러: {}",
                        pipeline.getDataPipelineExeId(), e.getMessage(), e);
                failCount++;
            }
        }

        log.debug("[Data Pipeline Monitor] 모니터링 완료 - 성공: {}, 실패: {}",
                successCount, failCount);
    }

    /**
     * 개별 파이프라인 상태 업데이트
     *
     * <p>Elasticsearch에서 index_name으로 상태 정보를 조회하고 DB를 업데이트합니다.</p>
     *
     * @param pipeline 업데이트할 파이프라인 정보
     */
    private void updatePipelineStatus(GpoKwlgInfoMas pipeline) {
        String indexName = pipeline.getIdxNm();

        if (indexName == null || indexName.isEmpty()) {
//            log.warn("[Data Pipeline Monitor] Skip - 인덱스명 없음, 지식ID: {}", pipeline.getKwlgId());
            return;
        }

        try {
            // 1. Elasticsearch에서 index_name으로 조회
            List<Map<String, Object>> statusDocuments = queryStatusFromElasticsearch(indexName);

            if (statusDocuments == null || statusDocuments.isEmpty()) {
                return;
            }

            // 2. 조회된 문서들에서 status와 rate 추출
            List<String> statuses = new ArrayList<>();
            List<String> rates = new ArrayList<>();

            for (Map<String, Object> doc : statusDocuments) {
                Object sourceObj = doc.get("_source");
                if (sourceObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> source = (Map<String, Object>) sourceObj;

                    Object statusObj = source.get("status");
                    if (statusObj != null) {
                        statuses.add(String.valueOf(statusObj));
                    }

                    Object rateObj = source.get("rate");
                    if (rateObj != null) {
                        rates.add(String.valueOf(rateObj));
                    }
                }
            }

            // 3. 상태 판단 및 DB 업데이트
            String finalStatus = determineStatusFromList(statuses);
            BigDecimal finalProgress = determineProgress(statuses, rates, pipeline.getDbLoadJinhgRt());

            pipeline.setDataPipelineLoadStatus(finalStatus);
            // error 상태가 아닐 때만 진행률 업데이트
            if (finalProgress != null) {
                pipeline.setDbLoadJinhgRt(finalProgress);
            }

            // 완료 상태일 때 인덱스 생성 종료 시간 업데이트
            if ("complete".equalsIgnoreCase(finalStatus)) {
                pipeline.setIdxMkEndAt(java.time.LocalDateTime.now());
            }

            gpoKwlgInfoMasRepository.save(pipeline);

//            log.info("[Data Pipeline Monitor] 업데이트 완료 - 인덱스명: {}, 상태: {}, 진행률: {}%",
//                    indexName, finalStatus, finalProgress != null ? finalProgress : pipeline.getDbLoadJinhgRt());

        } catch (BusinessException e) {
            log.warn("[Data Pipeline Monitor] Skip - Elasticsearch 조회 실패 (BusinessException), 인덱스명: {}, 에러: {}",
                    indexName, e.getMessage());
            // 에러 발생 시 skip하고 다음 항목 계속 처리
        } catch (FeignException e) {
            log.warn("[Data Pipeline Monitor] Skip - Elasticsearch 조회 실패 (FeignException), 인덱스명: {}, 상태코드: {}, 에러: {}",
                    indexName, e.status(), e.getMessage());
            // 에러 발생 시 skip하고 다음 항목 계속 처리
        } catch (RuntimeException e) {
            log.warn("[Data Pipeline Monitor] Skip - Elasticsearch 조회 실패 (RuntimeException), 인덱스명: {}, 에러: {}",
                    indexName, e.getMessage());
            // 에러 발생 시 skip하고 다음 항목 계속 처리
        } catch (Exception e) {
            log.warn("[Data Pipeline Monitor] Skip - Elasticsearch 조회 실패 (Exception), 인덱스명: {}, 에러: {}",
                    indexName, e.getMessage());
            // 에러 발생 시 skip하고 다음 항목 계속 처리
        }
    }

    /**
     * Elasticsearch에서 index_name으로 상태 정보 조회
     *
     * @param indexName 조회할 인덱스명
     * @return 상태 문서 목록
     */
    private List<Map<String, Object>> queryStatusFromElasticsearch(String indexName) {
        try {
            // Elasticsearch 쿼리 구성
            Map<String, Object> query = new HashMap<>();
            Map<String, Object> term = new HashMap<>();
            Map<String, Object> termField = new HashMap<>();
            termField.put("value", indexName);
            term.put("index_name", termField);
            query.put("term", term);

            Map<String, Object> body = new HashMap<>();
            body.put("query", query);
            body.put("size", 1000); // 최대 1000개 조회

//            log.debug("[Data Pipeline Monitor] Elasticsearch 조회 - 인덱스: {}, index_name: {}",
//                    STATUS_INDEX_NAME, indexName);

            // Elasticsearch 조회
            SearchResponse response = udpElasticsearchClient.searchData(STATUS_INDEX_NAME, body);

            if (response == null || response.getHits() == null) {
//                log.warn("[Data Pipeline Monitor] Elasticsearch 응답이 비어있음");
                return new ArrayList<>();
            }

            return response.getHits();

        } catch (BusinessException e) {
            log.error("[Data Pipeline Monitor] Elasticsearch 조회 실패 (BusinessException) - 인덱스명: {}, 에러: {}",
                    indexName, e.getMessage(), e);
            throw e;
        } catch (FeignException e) {
            log.error("[Data Pipeline Monitor] Elasticsearch 조회 실패 (FeignException) - 인덱스명: {}, 상태코드: {}, 에러: {}",
                    indexName, e.status(), e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("[Data Pipeline Monitor] Elasticsearch 조회 실패 (RuntimeException) - 인덱스명: {}, 에러: {}",
                    indexName, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[Data Pipeline Monitor] Elasticsearch 조회 실패 (Exception) - 인덱스명: {}, 에러: {}",
                    indexName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 상태 목록을 기반으로 최종 상태 판단
     *
     * <p>우선순위: error > complete > running</p>
     *
     * @param statuses 상태 목록
     * @return 최종 상태 (error, complete, running)
     */
    private String determineStatusFromList(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return "running";
        }

        // error가 하나라도 있으면 error
        for (String status : statuses) {
            if ("error".equalsIgnoreCase(status)) {
//                log.debug("[Data Pipeline Monitor] error 상태 발견");
                return "error";
            }
        }

        // complete가 하나라도 있으면 complete
        for (String status : statuses) {
            if ("complete".equalsIgnoreCase(status)) {
//                log.debug("[Data Pipeline Monitor] complete 상태 발견");
                return "complete";
            }
        }

        // 그 외는 running
//        log.debug("[Data Pipeline Monitor] running 상태로 판단");
        return "running";
    }

    /**
     * 상태와 rate 목록을 기반으로 최종 진행률 판단
     *
     * <p>로직:</p>
     * <ul>
     *   <li>error가 하나라도 있으면: null 반환 (진행률 업데이트 안 함)</li>
     *   <li>complete가 하나라도 있으면: 100% 반환</li>
     *   <li>running만 있으면: 가장 높은 rate 반환</li>
     * </ul>
     *
     * @param statuses 상태 목록
     * @param rates rate 목록
     * @param currentProgress 현재 진행률 (error일 때 유지하기 위해)
     * @return 최종 진행률 (null이면 업데이트 안 함)
     */
    private BigDecimal determineProgress(List<String> statuses, List<String> rates, BigDecimal currentProgress) {
        // error가 하나라도 있으면 진행률 업데이트 안 함 (null 반환)
        boolean hasError = statuses != null && statuses.stream()
                .anyMatch(s -> "error".equalsIgnoreCase(s));
        if (hasError) {
//            log.debug("[Data Pipeline Monitor] error 상태 - 진행률 업데이트 안 함");
            return null; // null 반환 시 진행률 업데이트 안 함
        }

        // complete가 하나라도 있으면 100% 반환
        boolean hasComplete = statuses != null && statuses.stream()
                .anyMatch(s -> "complete".equalsIgnoreCase(s));
        if (hasComplete) {
//            log.debug("[Data Pipeline Monitor] complete 상태 - 진행률 100%");
            return new BigDecimal(100);
        }

        // running만 있으면 가장 높은 rate 반환
//        log.debug("[Data Pipeline Monitor] running 상태 - 가장 높은 rate 사용");
        return getMaxRate(rates);
    }

    /**
     * rate 목록에서 가장 높은 값을 반환
     *
     * @param rates rate 목록 (문자열)
     * @return 최대 rate (BigDecimal), 없으면 0
     */
    private BigDecimal getMaxRate(List<String> rates) {
        if (rates == null || rates.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal maxRate = BigDecimal.ZERO;
        for (String rateStr : rates) {
            try {
                BigDecimal rate = new BigDecimal(rateStr);
                if (rate.compareTo(maxRate) > 0) {
                    maxRate = rate;
                }
            } catch (NumberFormatException e) {
                log.warn("[Data Pipeline Monitor] rate 파싱 실패 (NumberFormatException): {}", rateStr);
            } catch (RuntimeException e) {
                log.warn("[Data Pipeline Monitor] rate 파싱 실패 (RuntimeException): {}, 에러: {}", rateStr, e.getMessage());
            } catch (Exception e) {
                log.warn("[Data Pipeline Monitor] rate 파싱 실패 (Exception): {}, 에러: {}", rateStr, e.getMessage());
            }
        }

        return maxRate;
    }

    /**
     * Dataiku Continuous Activities 모니터링 및 상태 업데이트
     *
     * <p>Dataiku 프로젝트의 continuous-activities를 조회하여 상태를 확인합니다.</p>
     */
    public void monitorDataikuContinuousActivities() {
        try {
//            log.info("[Dataiku Continuous Activities Monitor] 모니터링 시작 - 프로젝트: {}, 환경: {}", projectId, activeProfile);

            // 1. 환경에 따른 동기화 대상 조회
            List<GpoKwlgInfoMas> syncTargets = getSyncTargets();
            
            if (syncTargets == null || syncTargets.isEmpty()) {
//                log.info("[Dataiku Continuous Activities Monitor] 동기화 대상이 없습니다 - 환경: {}", activeProfile);
                return;
            }

//            log.info("[Dataiku Continuous Activities Monitor] 동기화 대상 개수: {} - 환경: {}", syncTargets.size(), activeProfile);

            // 2. kwlg_id와 idx_nm 리스트 추출
            List<SyncTargetInfo> targetInfoList = extractSyncTargetInfo(syncTargets);
//            log.debug("[Dataiku Continuous Activities Monitor] 추출된 대상 정보 - kwlg_id 개수: {}, idx_nm 개수: {}",
//                    targetInfoList.size(), targetInfoList.size());

            // 3. Dataiku Continuous Activities 조회
            String environment = udpDataikuProperties.getEnvironment();
            String projectKey = udpDataikuProperties.getProjectKey();
            String authorizationHeader = (authorizationBearerToken == null || authorizationBearerToken.isBlank()) 
                ? null : "Bearer " + authorizationBearerToken;
            
            Object dataikuStreamingRecipeStatusObject = udpDataikuClient.getContinuousActivities(
                authorizationHeader,
                apiKey,
                environment,
                projectKey
            );

            if (dataikuStreamingRecipeStatusObject != null) {
//                log.info("[Dataiku Continuous Activities Monitor] Dataiku API 조회 성공 - 프로젝트: {}", projectId);

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> dataikuStreamingRecipeStatusList = (List<Map<String, Object>>) dataikuStreamingRecipeStatusObject;

                //  2개의 List<Map<String, Object> 를 조인하기위해 1개의 List 를 해시인덱싱 방식으로 변환후 사용 (시간복잡도 : 2중포문 O(N*M) but 해시인덱싱후비교 O(N+M))
                Map<String, Map<String, Object>> index = new HashMap<>();
                for (Map<String, Object> row : dataikuStreamingRecipeStatusList) {
                    Object keyValue = row.get("recipeId");
                    if (keyValue != null) {
                        index.put(keyValue.toString(), row);
                    }
                }

                for (GpoKwlgInfoMas row : syncTargets) {
                    String syncRecipeName = "sync_recipe_" + row.getIdxNm();
                    if (index.containsKey(syncRecipeName)) {
                        try {
                            //  상태값 확인
                            Map<String, Object> dataikuStreamingRecipeStatus = index.get(syncRecipeName);
                            Object desiredState = dataikuStreamingRecipeStatus.get("desiredState");

                            if (Objects.isNull(desiredState)) {
                                //  에러로 업데이트
                                updateSyncStatus(row, ERROR, "desiredState가 null");
                                continue;
                            }

                            String desiredStateStr = desiredState.toString();
                            
                            if (STARTED.equals(desiredStateStr)) {
                                //  정상으로 업데이트
                                updateSyncStatus(row, NORMAL, "STARTED 상태로 변경");
                            } else {
                                //  에러로 업데이트
                                updateSyncStatus(row, ERROR, "desiredState: " + desiredStateStr);
                            }

                        } catch (Exception e) {
                            //  에러로 업데이트
                            updateSyncStatus(row, ERROR, "예외 발생: " + e.getMessage());
                        }
                    } else {
                        // index에 없는 경우 에러로 업데이트
                        updateSyncStatus(row, ERROR, "syncRecipeName을 찾을 수 없음: " + syncRecipeName);
                    }
                }


            } else {
                log.warn("[Dataiku Continuous Activities Monitor] 응답이 null입니다 - 프로젝트: {}", udpDataikuProperties.getProjectKey());
            }

        } catch (BusinessException e) {
            log.error("[Dataiku Continuous Activities Monitor] 조회 실패 (BusinessException) - 프로젝트: {}, 에러: {}",
                    udpDataikuProperties.getProjectKey(), e.getMessage(), e);
        } catch (FeignException e) {
            log.error("[Dataiku Continuous Activities Monitor] 조회 실패 (FeignException) - 프로젝트: {}, 상태코드: {}, 에러: {}",
                    udpDataikuProperties.getProjectKey(), e.status(), e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("[Dataiku Continuous Activities Monitor] 조회 실패 (RuntimeException) - 프로젝트: {}, 에러: {}",
                    udpDataikuProperties.getProjectKey(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("[Dataiku Continuous Activities Monitor] 조회 실패 (Exception) - 프로젝트: {}, 에러: {}",
                    udpDataikuProperties.getProjectKey(), e.getMessage(), e);
        }
    }

    /**
     * 환경에 따른 동기화 대상 조회
     * 
     * <p>운영환경(prod)일 때는 unyung_synch_yn > 0인 항목,
     * 개발환경일 때는 dvlp_synch_yn > 0인 항목을 조회합니다.</p>
     * 
     * @return 동기화 대상 목록
     */
    private List<GpoKwlgInfoMas> getSyncTargets() {
        if ("prod".equals(activeProfile)) {
            // 운영환경: unyung_synch_yn > 0
            return gpoKwlgInfoMasRepository.findProdSyncTargets();
        } else {
            // 개발환경: dvlp_synch_yn > 0
            return gpoKwlgInfoMasRepository.findDevSyncTargets();
        }
    }

    /**
     * 동기화 대상 정보 추출 (kwlg_id, idx_nm)
     * 
     * @param syncTargets 동기화 대상 목록
     * @return kwlg_id와 idx_nm 리스트
     */
    private List<SyncTargetInfo> extractSyncTargetInfo(List<GpoKwlgInfoMas> syncTargets) {
        List<SyncTargetInfo> result = new ArrayList<>();
        for (GpoKwlgInfoMas target : syncTargets) {
            if (target.getKwlgId() != null && target.getIdxNm() != null) {
                result.add(new SyncTargetInfo(target.getKwlgId(), target.getIdxNm()));
            }
        }
        return result;
    }

    /**
     * 동기화 상태 업데이트 공통 함수
     * 
     * <p>data_pipeline_synch_status 컬럼을 업데이트하고 DB에 저장합니다.</p>
     * <p>각 레코드마다 개별 트랜잭션으로 처리됩니다.</p>
     * 
     * @param pipeline 업데이트할 파이프라인 정보
     * @param status 업데이트할 상태 (정상 또는 에러)
     * @param reason 업데이트 사유 (로깅용)
     */
    @Transactional
    private void updateSyncStatus(GpoKwlgInfoMas pipeline, String status, String reason) {
        String lastStatus = pipeline.getDataPipelineSynchStatus();
        
        // 상태가 동일하면 업데이트하지 않음
        if (status.equals(lastStatus)) {
            return;
        }

        pipeline.setDataPipelineSynchStatus(status);
        gpoKwlgInfoMasRepository.save(pipeline);

        if (NORMAL.equals(status)) {
            log.info("[Dataiku Continuous Activities Monitor] 상태 업데이트 - kwlg_id: {}, idx_nm: {}, 상태: 정상, 사유: {}", 
                    pipeline.getKwlgId(), pipeline.getIdxNm(), reason);
        } else {
            log.warn("[Dataiku Continuous Activities Monitor] 상태 업데이트 - kwlg_id: {}, idx_nm: {}, 상태: 에러, 사유: {}", 
                    pipeline.getKwlgId(), pipeline.getIdxNm(), reason);
        }
    }

    /**
     * 동기화 대상 정보 DTO
     */
    private static class SyncTargetInfo {
        private final String kwlgId;
        private final String idxNm;

        public SyncTargetInfo(String kwlgId, String idxNm) {
            this.kwlgId = kwlgId;
            this.idxNm = idxNm;
        }

        public String getKwlgId() {
            return kwlgId;
        }

        public String getIdxNm() {
            return idxNm;
        }
    }

}

