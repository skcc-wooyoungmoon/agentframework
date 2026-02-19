package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotItemsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentFewShotsService;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Few-Shot 마이그레이션 서비스
 * 
 * <p>Few-Shot 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FewShotMigService {
    
    private final SktaiAgentFewShotsService sktaiAgentFewShotsService;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>Few-Shot을 조회하고 Import 형식으로 변환합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String fewShotUuid) {
        try {
            log.info("Few-Shot Export → Import 형식 변환 시작 - fewShotUuid: {}", fewShotUuid);
            
            // 1. Few-Shot 데이터 수집
            Map<String, Object> data = collectFewShotData(fewShotUuid);
            if (data == null) {
                throw new RuntimeException("Few-Shot 데이터를 수집할 수 없습니다: " + fewShotUuid);
            }
            
            // 2. Import 형식으로 변환
            String importJson = convertFewShotToImportFormat(data);
            
            log.info("Few-Shot Export → Import 형식 변환 완료 - fewShotUuid: {}, jsonLength: {}", 
                    fewShotUuid, importJson.length());
            
            return importJson;
            
        } catch (FeignException e) {
            log.error("Few-Shot API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Few-Shot Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Few-Shot Export → Import 형식 변환 실패 - fewShotUuid: {}, error: {}", 
                    fewShotUuid, e.getMessage(), e);
            throw new RuntimeException("Few-Shot Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String fewShotUuid) {
        try {
            log.info("Few-Shot Export → Import 거래 시작 - fewShotUuid: {}", fewShotUuid);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(fewShotUuid);
            
            // 2. Import 거래 호출
            FewShotCreateResponse response = sktaiAgentFewShotsService.importFewShot(fewShotUuid, importJson);
            
            if (response == null) {
                log.error("Few-Shot Import 응답이 null입니다 - fewShotUuid: {}", fewShotUuid);
                return false;
            }
            
            Integer responseCode = response.getCode();
            String responseDetail = response.getDetail();
            
            log.info("Few-Shot Import 응답 - fewShotUuid: {}, code: {}, detail: {}", 
                    fewShotUuid, responseCode, responseDetail);
            
            // 성공 코드: 1 (Validated)
            boolean success = responseCode != null && responseCode.equals(1);
            
            if (success && response.getData() != null) {
                log.info("Few-Shot Import 성공 - fewShotUuid: {}, 생성된 UUID: {}", 
                        fewShotUuid, response.getData().getFewShotUuid());
            } else if (!success) {
                log.warn("Few-Shot Import 실패 - fewShotUuid: {}, code: {}, detail: {}", 
                        fewShotUuid, responseCode, responseDetail);
            }
            
            log.info("Few-Shot Export → Import 거래 완료 - fewShotUuid: {}, success: {}", fewShotUuid, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Few-Shot API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Few-Shot Export → Import 거래 실패 - fewShotUuid: {}, error: {}", 
                    fewShotUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String fewShotUuid, String importJson, Long projectId) {
        return importFromJsonString(fewShotUuid, importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String fewShotUuid, String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Few-Shot JSON 문자열에서 {} 시작 - fewShotUuid: {}, jsonLength: {}, isExist: {}", 
                    isExist ? "Update" : "Import", fewShotUuid, importJson != null ? importJson.length() : 0, isExist);
            
            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(fewShotUuid, importJson, projectId);
            }
            
            // 기존 데이터가 없으면 Import 수행
            FewShotCreateResponse response = sktaiAgentFewShotsService.importFewShot(fewShotUuid, importJson);
            
            if (response == null) {
                log.error("Few-Shot Import 응답이 null입니다 - fewShotUuid: {}", fewShotUuid);
                return false;
            }
            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Few-Shot JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
               
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/" + fewShotUuid, projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/versions/" + fewShotUuid, projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/versions/" + fewShotUuid + "/latest", projectId);

                // Few-Shot 버전별 리소스 URL 추가
                FewShotVersionsResponse versionsResponse = sktaiAgentFewShotsService.getFewShotVersions(fewShotUuid);
                if (versionsResponse != null && versionsResponse.getData() != null) {
                    for (FewShotVersionsResponse.FewShotVersionDetail versionDetail : versionsResponse.getData()) {
                        if (versionDetail == null || versionDetail.getVersionId() == null) {
                            continue;
                        }
                        adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/items/" + versionDetail.getVersionId(), projectId);
                        adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/tags/" + versionDetail.getVersionId(), projectId);
                    }
                }
                log.info("Few-Shot JSON 문자열에서 Import - 권한 설정 완료");
            }
            
            Integer responseCode = response.getCode();
            String responseDetail = response.getDetail();
            
            log.info("Few-Shot Import 응답 - fewShotUuid: {}, code: {}, detail: {}", 
                    fewShotUuid, responseCode, responseDetail);
            
            // 성공 코드: 1 (Validated)
            boolean success = responseCode != null && responseCode.equals(1);
            
            if (!success) {
                log.warn("Few-Shot Import 실패 - fewShotUuid: {}, code: {}, detail: {}", 
                        fewShotUuid, responseCode, responseDetail);
            }
            
            log.info("Few-Shot JSON 문자열에서 Import 완료 - fewShotUuid: {}, success: {}", fewShotUuid, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Few-Shot API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Few-Shot JSON 문자열에서 Import 실패 - fewShotUuid: {}, error: {}", 
                    fewShotUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 Few-Shot이 존재할 때 사용)
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String fewShotUuid, String importJson, Long projectId) {
        try {
            log.info("Few-Shot JSON 문자열에서 Update 시작 - fewShotUuid: {}, jsonLength: {}", fewShotUuid,
                    importJson != null ? importJson.length() : 0);

            // Export JSON의 "name" 필드를 Update API가 기대하는 "new_name"으로 변환
            String convertedJson = importJson;
            try {
                com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(importJson);
                if (jsonNode.has("name") && !jsonNode.has("new_name")) {
                    com.fasterxml.jackson.databind.node.ObjectNode objectNode = (com.fasterxml.jackson.databind.node.ObjectNode) jsonNode;
                    objectNode.put("new_name", jsonNode.get("name").asText());
                    objectNode.remove("name");
                    convertedJson = objectMapper.writeValueAsString(objectNode);
                    log.debug("Few-Shot JSON 변환 완료 - name → new_name");
                }
            } catch (Exception e) {
                log.warn("Few-Shot JSON 변환 중 오류 (원본 사용) - error: {}", e.getMessage());
            }

            // JSON 문자열을 FewShotUpdateRequest로 변환
            FewShotUpdateRequest updateRequest = objectMapper.readValue(convertedJson, FewShotUpdateRequest.class);
            
            // updateFewShot 호출 (PUT)
            FewShotUpdateResponse response = sktaiAgentFewShotsService.updateFewShot(fewShotUuid, updateRequest);

            if (response == null) {
                log.error("Few-Shot Update 응답이 null입니다 - fewShotUuid: {}", fewShotUuid);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Few-Shot JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/" + fewShotUuid, projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/versions/" + fewShotUuid, projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/few-shots/versions/" + fewShotUuid + "/latest", projectId);
                log.info("Few-Shot JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Few-Shot Update 성공 - fewShotUuid: {}", fewShotUuid);
            return true;

        } catch (FeignException e) {
            log.error("Few-Shot API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Few-Shot JSON 문자열에서 Update 실패 - fewShotUuid: {}, error: {}", 
                    fewShotUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String fewShotUuid, boolean saveToFile) {
        try {
            log.info("Few-Shot Export → JSON 파일 저장 시작 - fewShotUuid: {}, saveToFile: {}", 
                    fewShotUuid, saveToFile);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(fewShotUuid);
            
            // 2. JSON 파일로 저장 (조건 처리)
            if (!saveToFile) {
                log.info("파일 저장 옵션이 false이므로 파일 저장을 건너뜁니다.");
                return null;
            }
            
            // 저장 디렉토리 생성
            String baseDir = "data/exports";
            Path exportDir = Paths.get(baseDir);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }
            
            // 파일명 생성: FEW_SHOT_{uuid}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("FEW_SHOT_%s_%s.json", fewShotUuid, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Few-Shot Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("Few-Shot Export → JSON 파일 저장 실패 (IOException) - fewShotUuid: {}, error: {}", 
                    fewShotUuid, e.getMessage(), e);
            throw new RuntimeException("Few-Shot Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Few-Shot Export → JSON 파일 저장 실패 - fewShotUuid: {}, error: {}", 
                    fewShotUuid, e.getMessage(), e);
            throw new RuntimeException("Few-Shot Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * FEW_SHOT 데이터 수집
     * 
     * <p>1. GET /few-shots/{few_shot_uuid} 조회하여 release_version 확인
     * 2. GET /few-shots/versions/{few_shot_uuid} 조회하여 release: true인 version_id 찾기
     * 3. GET /few-shots/items/{version_id} 조회하여 모든 items 수집 (페이징 처리)
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 수집된 FEW_SHOT 데이터 (Map 형태)
     */
    private Map<String, Object> collectFewShotData(String fewShotUuid) {
        try {
            log.info("FEW_SHOT 데이터 수집 시작 - fewShotUuid: {}", fewShotUuid);
            
            // 1. Few-Shot 상세 조회
            log.debug("[1단계] Few-Shot 상세 조회 - fewShotUuid: {}", fewShotUuid);
            FewShotResponse fewShotResponse = sktaiAgentFewShotsService.getFewShot(fewShotUuid);
            
            if (fewShotResponse == null || fewShotResponse.getData() == null) {
                log.error("Few-Shot 상세 조회 실패 - fewShotUuid: {}", fewShotUuid);
                return null;
            }
            
            FewShotResponse.FewShotDetail detail = fewShotResponse.getData();
            String name = detail.getName();
            List<String> tags = detail.getTags();
            Integer releaseVersion = detail.getReleaseVersion();
            
            log.info("[1단계] Few-Shot 상세 조회 완료 - name: {}, releaseVersion: {}", name, releaseVersion);
            
            // 2. 버전 목록 조회하여 release: true인 version_id 찾기
            log.debug("[2단계] Few-Shot 버전 목록 조회 - fewShotUuid: {}", fewShotUuid);
            FewShotVersionsResponse versionsResponse = sktaiAgentFewShotsService.getFewShotVersions(fewShotUuid);
            
            if (versionsResponse == null || versionsResponse.getData() == null || versionsResponse.getData().isEmpty()) {
                log.error("Few-Shot 버전 목록 조회 실패 - fewShotUuid: {}", fewShotUuid);
                return null;
            }
            
            // release: true인 version_id 찾기
            String releaseVersionId = null;
            for (FewShotVersionsResponse.FewShotVersionDetail versionDetail : versionsResponse.getData()) {
                if (Boolean.TRUE.equals(versionDetail.getRelease())) {
                    releaseVersionId = versionDetail.getVersionId();
                    log.info("[2단계] Release 버전 찾음 - versionId: {}, version: {}", 
                            releaseVersionId, versionDetail.getVersion());
                    break;
                }
            }
            
            if (releaseVersionId == null) {
                log.error("Release 버전을 찾을 수 없음 - fewShotUuid: {}", fewShotUuid);
                return null;
            }
            
            // 3. Items 수집 (페이징 처리)
            log.debug("[3단계] Few-Shot Items 수집 시작 - versionId: {}", releaseVersionId);
            List<FewShotItemsResponse.FewShotItemSummary> allItems = new ArrayList<>();
            int page = 1;
            int size = 10;
            boolean hasNextPage = true;
            
            while (hasNextPage) {
                log.debug("[3단계] Items 조회 - page: {}, size: {}", page, size);
                FewShotItemsResponse itemsResponse = sktaiAgentFewShotsService.getFewShotItems(
                        releaseVersionId, page, size, null, null, null);
                
                if (itemsResponse == null || itemsResponse.getData() == null) {
                    log.warn("Items 조회 실패 - versionId: {}, page: {}", releaseVersionId, page);
                    break;
                }
                
                allItems.addAll(itemsResponse.getData());
                log.debug("[3단계] Items 수집 - 현재 총 개수: {}", allItems.size());
                
                // 다음 페이지 확인
                if (itemsResponse.getPayload() != null && 
                    itemsResponse.getPayload().getPagination() != null) {
                    String nextPageUrl = itemsResponse.getPayload().getPagination().getNextPageUrl();
                    hasNextPage = (nextPageUrl != null && !nextPageUrl.isEmpty());
                    page++;
                } else {
                    hasNextPage = false;
                }
            }
            
            log.info("[3단계] Items 수집 완료 - 총 개수: {}", allItems.size());
            
            // 4. 수집된 데이터를 Map으로 구성
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("name", name);
            result.put("tags", tags);
            result.put("items", allItems);
            result.put("project_id", null); // project_id는 나중에 설정 가능
            
            log.info("FEW_SHOT 데이터 수집 완료 - fewShotUuid: {}, items 개수: {}", fewShotUuid, allItems.size());
            
            return result;
            
        } catch (FeignException e) {
            log.error("FEW_SHOT 데이터 수집 실패 (FeignException) - fewShotUuid: {}, error: {}", fewShotUuid, e.getMessage(), e);
            return null;
        } catch (RuntimeException e) {
            log.error("FEW_SHOT 데이터 수집 실패 - fewShotUuid: {}, error: {}", fewShotUuid, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * FEW_SHOT 데이터를 Import 형식으로 변환
     * 
     * <p>변환 규칙:
     * 1. items를 item_sequence로 그룹화
     * 2. item_type이 "Q"면 item_query, "A"면 item_answer로 변환
     * 3. name, tags, project_id 포함
     * 
     * @param data FEW_SHOT 데이터 (Map 형태, collectFewShotData의 결과)
     * @return Import 형식의 JSON 문자열
     */
    @SuppressWarnings("unchecked")
    private String convertFewShotToImportFormat(Object data) {
        try {
            log.info("FEW_SHOT Import 형식으로 변환 시작");
            
            if (!(data instanceof Map)) {
                throw new IllegalArgumentException("FEW_SHOT 데이터는 Map 형태여야 합니다.");
            }
            
            Map<String, Object> dataMap = (Map<String, Object>) data;
            
            // 1. 기본 정보 추출
            String name = (String) dataMap.get("name");
            List<String> tags = (List<String>) dataMap.get("tags");
            Object projectId = dataMap.get("project_id");
            List<FewShotItemsResponse.FewShotItemSummary> items = 
                    (List<FewShotItemsResponse.FewShotItemSummary>) dataMap.get("items");
            
            if (items == null || items.isEmpty()) {
                log.warn("FEW_SHOT items가 비어있습니다.");
                items = new ArrayList<>();
            }
            
            // 2. items를 item_sequence로 그룹화
            Map<Integer, Map<String, String>> groupedItems = new LinkedHashMap<>();
            
            for (FewShotItemsResponse.FewShotItemSummary item : items) {
                Integer sequence = item.getItemSequence();
                String itemType = item.getItemType();
                String itemContent = item.getItem();
                
                if (sequence == null) {
                    log.warn("item_sequence가 null인 item 발견 - itemType: {}", itemType);
                    continue;
                }
                
                groupedItems.computeIfAbsent(sequence, k -> new LinkedHashMap<>());
                Map<String, String> sequenceMap = groupedItems.get(sequence);
                
                if ("Q".equals(itemType)) {
                    sequenceMap.put("item_query", itemContent);
                } else if ("A".equals(itemType)) {
                    sequenceMap.put("item_answer", itemContent);
                } else {
                    log.warn("알 수 없는 item_type: {} - sequence: {}", itemType, sequence);
                }
            }
            
            // 3. 그룹화된 items를 sequence 순서대로 리스트로 변환
            List<Map<String, String>> itemsList = new ArrayList<>();
            // sequence 순서대로 정렬
            List<Integer> sortedSequences = new ArrayList<>(groupedItems.keySet());
            Collections.sort(sortedSequences);
            
            for (Integer sequence : sortedSequences) {
                Map<String, String> itemMap = groupedItems.get(sequence);
                // item_query와 item_answer가 모두 있어야 유효한 item
                if (itemMap.containsKey("item_query") && itemMap.containsKey("item_answer")) {
                    itemsList.add(itemMap);
                } else {
                    log.warn("item_query 또는 item_answer가 없는 sequence: {} - itemMap: {}", 
                            sequence, itemMap);
                }
            }
            
            log.info("그룹화 완료 - 총 {}개의 유효한 items", itemsList.size());
            
            // 4. Import 형식 구성 (필드 순서: items, name, project_id, release, tags)
            Map<String, Object> result = new LinkedHashMap<>();
            
            // items를 먼저 추가
            result.put("items", itemsList);
            
            // name 추가
            result.put("name", name);
            
            // project_id 추가 (null이면 제외)
            if (projectId != null) {
                result.put("project_id", projectId);
            }
            
            // release 추가 (true로 설정)
            result.put("release", true);
            
            // tags 변환: List<String> -> List<Map<String, String>>
            if (tags != null && !tags.isEmpty()) {
                List<Map<String, String>> tagsList = new ArrayList<>();
                for (String tag : tags) {
                    Map<String, String> tagMap = new LinkedHashMap<>();
                    tagMap.put("tag", tag);
                    tagsList.add(tagMap);
                }
                result.put("tags", tagsList);
            } else {
                result.put("tags", new ArrayList<>());
            }
            
            // 5. JSON으로 변환
            String json = objectMapper.writeValueAsString(result);
            
            log.info("FEW_SHOT Import 형식으로 변환 완료 - jsonLength: {}", json.length());
            log.info("=== FEW_SHOT Import JSON 생성 완료 ===");
            log.info("name: {}", name);
            log.info("tags 개수: {}", tags != null ? tags.size() : 0);
            log.info("items 개수: {}", itemsList.size());
            log.info("project_id: {}", projectId);
            log.info("생성된 JSON 전체 내용:");
            log.info("{}", json);
            log.info("=== FEW_SHOT Import JSON 끝 ===");
            
            return json;
            
        } catch (JsonProcessingException e) {
            log.error("FEW_SHOT JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("FEW_SHOT Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("FEW_SHOT Import 형식 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("FEW_SHOT Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * FewShot 존재 여부 확인
     * 
     * @param fewShotId FewShot ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String fewShotId) {
        try {
            sktaiAgentFewShotsService.getFewShot(fewShotId);
            return true;
        } catch (Exception e) {
            log.debug("FewShot 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", fewShotId, e.getMessage());
            return false;
        }
    }
}

