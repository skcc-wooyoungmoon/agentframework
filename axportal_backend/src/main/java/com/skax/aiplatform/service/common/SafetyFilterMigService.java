package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupKeywordsUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.GroupStopwordsBatchImportResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupAggregate;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupsStopWordRes;
import com.skax.aiplatform.client.sktai.safetyfilter.service.SktaiSafetyFilterGroupStopWordsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.service.admin.AdminAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SafetyFilter 마이그레이션 서비스
 *
 * <p>SafetyFilter 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SafetyFilterMigService {

    private final SktaiSafetyFilterGroupStopWordsService sktaiSafetyFilterGroupStopWordsService;
    private final ObjectMapper objectMapper;

    private final AdminAuthService adminAuthService;

    /**
     * Export 형태에서 Import 형태(= JSON 문자열) 로 변환
     */
    public String exportToImportFormat(String safetyFilterGroupIds) {
        // safetyFilterIds는 uuid1|uuid2|uuid3 형태로 전달됨
        log.info("SafetyFilter Export → Import 형식 변환 시작 - safetyFilterGroupIds: {}", safetyFilterGroupIds);

        String filter = "group_id:" + safetyFilterGroupIds;

        // 여러 그룹 ID에 해당하는 SafetyFilter 그룹 상세 조회 (group_id 필터 사용)
        SktSafetyFilterGroupsStopWordRes sktSafetyFilterGroupsRes =
                sktaiSafetyFilterGroupStopWordsService.getSafetyFilterGroupsStopWords(
                        1,
                        -1,
                        null,
                        filter,
                        null,
                        null,
                        false
                );

        List<SktSafetyFilterGroupAggregate> safetyFilterGroups = Optional.ofNullable(sktSafetyFilterGroupsRes.getData())
                                                                         .orElse(List.of());

        if (CollectionUtils.isEmpty(safetyFilterGroups)) {
            log.error("조회된 SafetyFilter 그룹이 없습니다. safetyFilterGroupIds: {}", safetyFilterGroupIds);
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "조회된 SafetyFilter 그룹이 없습니다.");
        }

        // Import에 필요한 데이터 필드 추출
        List<Map<String, Object>> importData = convertToImportFormat(safetyFilterGroups);

        // JSON 문자열로 변환
        try {
            String json = objectMapper.writeValueAsString(importData);
            log.info("SafetyFilter Export 완료 - {} 건", importData.size());

            return json;
        } catch (JsonProcessingException e) {
            log.error("SafetyFilter Export JSON 변환 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "JSON 변환 중 오류가 발생했습니다.");
        }
    }

    // ====== Private Methods ======

    /**
     * Json 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     *
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId  프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     */
    public boolean importFromJsonString(String importJson, Long projectId) {
        return importFromJsonString(importJson, projectId, false);
    }

    /**
     * Json 문자열로부터 Import 또는 Update 수행
     *
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId  프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist    기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Safety Filter JSON 문자열에서 {} 시작 - isExist: {}", isExist ? "Update" : "Import", isExist);

            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행 (각 그룹별로 updateSafetyFilterGroupKeywords 호출)
                return updateFromJsonString(importJson, projectId);
            }

            // 기존 데이터가 없으면 Import 수행
            GroupStopwordsBatchImportResponse response =
                    sktaiSafetyFilterGroupStopWordsService.importGroupStopwordsBatch(importJson);

            boolean success = response != null && "success".equals(response.getStatus());

            // projectId가 있을 경우만, 권한 설정
            if (success && projectId != null) {
                log.info("Safety Filter JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                response.getSuccessfulGroupIds().forEach(
                        groupId -> adminAuthService.setResourcePolicyByProjectSequence("/safety-filters/groups/" + groupId, projectId)
                );
                log.info("Safety Filter JSON 문자열에서 Import - 권한 설정 완료");
            }

            log.info("Safety Filter JSON 문자열에서 Import 완료");

            return success;
        } catch (BusinessException e) {
            log.error("Safety Filter API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * JSON 문자열로부터 Update 수행 (기존 SafetyFilter 그룹들이 존재할 때 사용)
     *
     * @param importJson Import 형식의 JSON 문자열 (배열 형태)
     * @param projectId  프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String importJson, Long projectId) {
        // TODO 권두현 선임님 코드 확인 해주세요!
        try {
            log.info("Safety Filter JSON 문자열에서 Update 시작 - jsonLength: {}", importJson != null ? importJson.length()
                    : 0);

            // JSON 배열 파싱
            List<Map<String, Object>> groups = objectMapper.readValue(importJson,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
            });

            if (groups == null || groups.isEmpty()) {
                log.warn("Safety Filter Update - 그룹 데이터가 없습니다");
                return false;
            }

            int successCount = 0;
            int failCount = 0;
            List<String> successGroupIds = new ArrayList<>();

            // 각 그룹별로 updateSafetyFilterGroupKeywords 호출
            for (Map<String, Object> group : groups) {
                String groupId = (String) group.get("group_id");
                @SuppressWarnings("unchecked") List<String> stopwords = (List<String>) group.get("stopwords");

                if (groupId == null || groupId.isEmpty()) {
                    log.warn("Safety Filter Update - group_id가 없는 항목 건너뜀");
                    failCount++;
                    continue;
                }

                try {
                    // Update Request 생성
                    SktSafetyFilterGroupKeywordsUpdateReq updateRequest =
                            SktSafetyFilterGroupKeywordsUpdateReq.from(stopwords != null ? stopwords :
                                    new ArrayList<>());

                    // Update API 호출
                    sktaiSafetyFilterGroupStopWordsService.updateSafetyFilterGroupKeywords(groupId, updateRequest);

                    successCount++;
                    successGroupIds.add(groupId);
                    log.debug("Safety Filter Update 성공 - groupId: {}", groupId);

                } catch (Exception e) {
                    log.error("Safety Filter Update 실패 - groupId: {}, error: {}", groupId, e.getMessage());
                    failCount++;
                }
            }

            // projectId가 있을 경우만, 권한 설정
            if (projectId != null && !successGroupIds.isEmpty()) {
                log.info("Safety Filter JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                successGroupIds.forEach(groupId -> adminAuthService.setResourcePolicyByProjectSequence("/safety" +
                        "-filters/groups/" + groupId, projectId));
                log.info("Safety Filter JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Safety Filter Update 완료 - 성공: {}, 실패: {}", successCount, failCount);

            return failCount == 0;

        } catch (Exception e) {
            log.error("Safety Filter JSON 문자열에서 Update 실패 - error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     *
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     *
     * @param safetyFilterGroupIds SafetyFilter 그룹 ID들 (|로 구분, 예: "uuid1|uuid2|uuid3")
     * @param saveToFile           파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String safetyFilterGroupIds, boolean saveToFile) {
        try {
            log.info("SafetyFilter Export → JSON 파일 저장 시작 - safetyFilterGroupIds: {}, saveToFile: {}",
                    safetyFilterGroupIds, saveToFile);

            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(safetyFilterGroupIds);

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

            // 파일명 생성: SAFETYFILTER_{groupIds}_{timestamp}.json
            // groupIds에 |가 포함되어 있으므로 파일명에 사용할 수 있도록 처리
            String safeGroupIds = safetyFilterGroupIds.replace("|", "_");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("SAFETYFILTER_%s_%s.json", safeGroupIds, timestamp);
            Path filePath = exportDir.resolve(fileName);

            // JSON 파일 저장 (UTF-8 인코딩)
            try (FileWriter writer = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
                writer.write(importJson);
                writer.flush();
            }

            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("SafetyFilter Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);

            return absolutePath;

        } catch (IOException e) {
            log.error("SafetyFilter Export → JSON 파일 저장 실패 (IOException) - safetyFilterGroupIds: {}, error: {}",
                    safetyFilterGroupIds, e.getMessage(), e);
            throw new RuntimeException("SafetyFilter Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("SafetyFilter Export → JSON 파일 저장 실패 - safetyFilterGroupIds: {}, error: {}",
                    safetyFilterGroupIds, e.getMessage(), e);
            throw new RuntimeException("SafetyFilter Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("SafetyFilter Export → JSON 파일 저장 실패 - safetyFilterGroupIds: {}, error: {}",
                    safetyFilterGroupIds, e.getMessage(), e);
            throw new RuntimeException("SafetyFilter Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * SafetyFilter 그룹 리스트를 Import 형식의 Map 리스트로 변환
     * <p>Import에 필요한 필드(group_id, group_name, stopwords)만 추출합니다.</p>
     *
     * @param safetyFilterGroups SafetyFilter 그룹 DTO 리스트
     * @return Import 형식의 Map 리스트 (group_id, group_name, stopwords만 포함)
     */
    private List<Map<String, Object>> convertToImportFormat(List<SktSafetyFilterGroupAggregate> safetyFilterGroups) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (var group : safetyFilterGroups) {
            Map<String, Object> filtered = new LinkedHashMap<>();

            filtered.put("group_id", group.getGroupId());
            filtered.put("group_name", group.getGroupName());

            // stopwords 변환
            List<String> stopwordsList = new ArrayList<>();
            var stopWords = group.getStopWords();

            if (!CollectionUtils.isEmpty(stopWords)) {
                for (var stopWord : stopWords) {
                    stopwordsList.add(stopWord.getStopWord());
                }
            }

            filtered.put("stopwords", stopwordsList);
            result.add(filtered);
        }

        return result;
    }

    public boolean checkIfExists(String safetyFilterGroupId) {
        String filter = "group_id:" + safetyFilterGroupId;
        try {
            sktaiSafetyFilterGroupStopWordsService.getSafetyFilterGroupsStopWords(1, -1, null, filter, null, null,
                    false);
            return true;
        } catch (Exception e) {
            log.debug("SafetyFilter 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", safetyFilterGroupId, e.getMessage());
            return false;
        }
    }

}
