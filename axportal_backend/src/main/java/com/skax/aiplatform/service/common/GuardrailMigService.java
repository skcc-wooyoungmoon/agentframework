package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailUpdateReq;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailCreateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailDetailRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGuardRailsService;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Guardrail 마이그레이션 서비스
 *
 * <p>Guardrail 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuardrailMigService {

    private final AdminAuthService adminAuthService;

    private final SktaiAgentGuardRailsService sktaiAgentGuardRailsService;
    private final ObjectMapper objectMapper;

    /**
     * 1. Export 형태를 만드는 것
     *
     * <p>Guardrail을 조회하고 Import 형식으로 변환합니다.</p>
     *
     * @param guardrailId Guardrail ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String guardrailId) {
        try {
            log.info("Guardrail Export → Import 형식 변환 시작 - guardrailId: {}", guardrailId);

            // Guardrail 조회
            SktGuardRailDetailRes guardrailResponse = sktaiAgentGuardRailsService.getGuardRailById(guardrailId);
            if (guardrailResponse == null || guardrailResponse.getData() == null) {
                throw new RuntimeException("Guardrail을 찾을 수 없습니다: " + guardrailId);
            }

            // Import 형식으로 변환
            String importJson = convertGuardrailToImportFormat(guardrailResponse.getData());

            log.info("Guardrail Export → Import 형식 변환 완료 - guardrailId: {}, jsonLength: {}", guardrailId,
                    importJson.length());

            return importJson;

        } catch (FeignException e) {
            log.error("Guardrail API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Guardrail Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Guardrail Export → Import 형식 변환 실패 - guardrailId: {}, error: {}", guardrailId, e.getMessage(),
                    e);
            throw new RuntimeException("Guardrail Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 2. Export 형태를 Import 거래 날리는 것
     *
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     *
     * @param guardrailId Guardrail ID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String guardrailId) {
        try {
            log.info("Guardrail Export → Import 거래 시작 - guardrailId: {}", guardrailId);

            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(guardrailId);

            // 2. Import 거래 호출
            SktGuardRailCreateRes response = sktaiAgentGuardRailsService.importGuardRail(guardrailId, importJson);

            boolean success = response != null && response.getData() != null;

            log.info("Guardrail Export → Import 거래 완료 - guardrailId: {}, success: {}", guardrailId, success);

            return success;

        } catch (FeignException e) {
            log.error("Guardrail API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Guardrail Export → Import 거래 실패 - guardrailId: {}, error: {}", guardrailId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     *
     * @param guardrailId Guardrail ID
     * @param importJson  Import 형식의 JSON 문자열
     * @param projectId   프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String guardrailId, String importJson, Long projectId) {
        return importFromJsonString(guardrailId, importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     *
     * @param guardrailId Guardrail ID
     * @param importJson  Import 형식의 JSON 문자열
     * @param projectId   프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String guardrailId, String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Guardrail JSON 문자열에서 {} 시작 - guardrailId: {}, isExist: {}", 
                    isExist ? "Update" : "Import", guardrailId, isExist);

            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(guardrailId, importJson, projectId);
            }
            
            // 기존 데이터가 없으면 Import 수행
            SktGuardRailCreateRes response = sktaiAgentGuardRailsService.importGuardRail(guardrailId, importJson);

            boolean success = response != null && response.getData() != null;

            // projectId가 있을 경우만, 권한 설정
            if (success && projectId != null) {
                log.info("Guardrail JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                // Guardrail 권한 설정 로직 추가
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/guardrails/" + response.getData().getGuardrailsId(), projectId);
                log.info("Guardrail JSON 문자열에서 Import - 권한 설정 완료");
            }

            log.info("Guardrail JSON 문자열에서 Import 완료 - guardrailId: {}, success: {}", guardrailId, success);

            return success;

        } catch (FeignException e) {
            log.error("Guardrail API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Guardrail JSON 문자열에서 Import 실패 - guardrailId: {}, error: {}", guardrailId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 Guardrail이 존재할 때 사용)
     * 
     * @param guardrailId Guardrail ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String guardrailId, String importJson, Long projectId) {
        try {
            log.info("Guardrail JSON 문자열에서 Update 시작 - guardrailId: {}, jsonLength: {}", guardrailId,
                    importJson != null ? importJson.length() : 0);

            // JSON 문자열을 SktGuardRailUpdateReq로 변환
            SktGuardRailUpdateReq updateRequest = objectMapper.readValue(importJson, SktGuardRailUpdateReq.class);
            
            // updateGuardRail 호출 (PUT)
            SktGuardRailUpdateRes response = sktaiAgentGuardRailsService.updateGuardRail(guardrailId, updateRequest);

            if (response == null) {
                log.error("Guardrail Update 응답이 null입니다 - guardrailId: {}", guardrailId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Guardrail JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/guardrails/" + guardrailId, projectId);
                log.info("Guardrail JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Guardrail Update 성공 - guardrailId: {}", guardrailId);
            return true;

        } catch (FeignException e) {
            log.error("Guardrail API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Guardrail JSON 문자열에서 Update 실패 - guardrailId: {}, error: {}", 
                    guardrailId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     *
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     *
     * @param guardrailId Guardrail ID
     * @param saveToFile  파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String guardrailId, boolean saveToFile) {
        try {
            log.info("Guardrail Export → JSON 파일 저장 시작 - guardrailId: {}, saveToFile: {}", guardrailId, saveToFile);

            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(guardrailId);

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

            // 파일명 생성: GUARDRAIL_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("GUARDRAIL_%s_%s.json", guardrailId, timestamp);
            Path filePath = exportDir.resolve(fileName);

            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }

            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Guardrail Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);

            return absolutePath;

        } catch (IOException e) {
            log.error("Guardrail Export → JSON 파일 저장 실패 (IOException) - guardrailId: {}, error: {}", guardrailId,
                    e.getMessage(), e);
            throw new RuntimeException("Guardrail Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Guardrail Export → JSON 파일 저장 실패 - guardrailId: {}, error: {}", guardrailId, e.getMessage(), e);
            throw new RuntimeException("Guardrail Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Guardrail 데이터를 Import 형식으로 변환
     */
    @SuppressWarnings("unchecked")
    private String convertGuardrailToImportFormat(SktGuardRailDetailRes.GuardRailDetailData guardrailData) {
        try {
            if (guardrailData == null) {
                throw new IllegalArgumentException("Guardrail 데이터가 null입니다.");
            }

            // Guardrail 데이터를 Map으로 변환
            Map<String, Object> map = objectMapper.convertValue(guardrailData, Map.class);

            // 메타데이터 필드 제거
            if (map.containsKey("uuid")) {
                map.remove("uuid");
            }
            if (map.containsKey("created_by")) {
                map.remove("created_by");
            }
            if (map.containsKey("created_at")) {
                map.remove("created_at");
            }
            if (map.containsKey("updated_by")) {
                map.remove("updated_by");
            }
            if (map.containsKey("updated_at")) {
                map.remove("updated_at");
            }

            // null 필드 제거 (재귀적으로 처리)
            removeNullFields(map);

            // JSON으로 변환
            String json = objectMapper.writeValueAsString(map);

            return json;

        } catch (JsonProcessingException e) {
            log.error("Guardrail JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Guardrail Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Guardrail Import 형식 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Guardrail Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Map에서 null 값을 가진 필드를 재귀적으로 제거
     */
    @SuppressWarnings("unchecked")
    private void removeNullFields(Map<String, Object> map) {
        if (map == null) {
            return;
        }

        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            if (value == null) {
                keysToRemove.add(entry.getKey());
            } else if (value instanceof Map) {
                removeNullFields((Map<String, Object>) value);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    if (item instanceof Map) {
                        removeNullFields((Map<String, Object>) item);
                    }
                }
            }
        }

        for (String key : keysToRemove) {
            map.remove(key);
        }
    }
    
    /**
     * Guardrail 존재 여부 확인
     * 
     * @param guardrailId Guardrail ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String guardrailId) {
        try {
            sktaiAgentGuardRailsService.getGuardRailById(guardrailId);
            return true;
        } catch (Exception e) {
            log.debug("Guardrail 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", guardrailId, e.getMessage());
            return false;
        }
    }

}

