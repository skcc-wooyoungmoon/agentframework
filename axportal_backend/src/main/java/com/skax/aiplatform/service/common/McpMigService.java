package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogUpdateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogImportResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogInfo;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogResponse;
import com.skax.aiplatform.client.sktai.mcp.service.SktaiMcpService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP 마이그레이션 서비스
 * 
 * <p>MCP 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpMigService {
    
    private final SktaiMcpService sktaiMcpService;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>MCP 카탈로그를 조회하고 Import 형식으로 변환합니다.</p>
     * 
     * @param mcpId MCP 카탈로그 ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String mcpId) {
        try {
            log.info("MCP Export → Import 형식 변환 시작 - mcpId: {}", mcpId);
            
            // MCP 카탈로그 조회
            McpCatalogResponse catalogResponse = sktaiMcpService.getCatalogById(mcpId);
            if (catalogResponse == null || catalogResponse.getData() == null) {
                throw new RuntimeException("MCP 카탈로그를 찾을 수 없습니다: " + mcpId);
            }
            
            // Import 형식으로 변환
            String importJson = convertMcpCatalogToImportFormat(catalogResponse.getData());
            
            log.info("MCP Export → Import 형식 변환 완료 - mcpId: {}, jsonLength: {}", mcpId, importJson.length());
            
            return importJson;
            
        } catch (BusinessException e) {
            log.error("MCP Export → Import 형식 변환 실패 (BusinessException) - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw e;
        } catch (FeignException e) {
            log.error("MCP API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("MCP Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("MCP Export → Import 형식 변환 실패 (RuntimeException) - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("MCP Export → Import 형식 변환 실패 - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw new RuntimeException("MCP Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * Export 형태를 만드는 것 (카탈로그 데이터 직접 전달)
     * 
     * <p>이미 조회한 MCP 카탈로그 데이터를 Import 형식으로 변환합니다.</p>
     * 
     * @param catalogData MCP 카탈로그 데이터
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(McpCatalogInfo catalogData) {
        try {
            log.info("MCP Export → Import 형식 변환 시작 (카탈로그 데이터 직접 전달)");
            
            if (catalogData == null) {
                throw new IllegalArgumentException("MCP 카탈로그 데이터가 null입니다.");
            }
            
            // Import 형식으로 변환
            String importJson = convertMcpCatalogToImportFormat(catalogData);
            
            log.info("MCP Export → Import 형식 변환 완료 - jsonLength: {}", importJson.length());
            
            return importJson;
            
        } catch (RuntimeException e) {
            log.error("MCP Export → Import 형식 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("MCP Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param mcpId MCP 카탈로그 ID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String mcpId) {
        try {
            log.info("MCP Export → Import 거래 시작 - mcpId: {}", mcpId);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(mcpId);
            
            // 2. Import 거래 호출
            McpCatalogImportResponse response = sktaiMcpService.importCatalog(mcpId, importJson);
            
            // 성공 코드: 1 (검증 성공 또는 생성 성공)
            boolean success = response != null && response.getCode() != null && response.getCode().equals(1);
            
            if (!success && response != null) {
                log.warn("MCP Export → Import 거래 실패 - mcpId: {}, code: {}, detail: {}", 
                        mcpId, response.getCode(), response.getDetail());
            }
            
            log.info("MCP Export → Import 거래 완료 - mcpId: {}, success: {}", mcpId, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("MCP API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("MCP Export → Import 거래 실패 - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     * 
     * @param mcpId MCP 카탈로그 ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String mcpId, String importJson, Long projectId) {
        return importFromJsonString(mcpId, importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     * 
     * @param mcpId MCP 카탈로그 ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String mcpId, String importJson, Long projectId, boolean isExist) {
        try {
            log.info("MCP JSON 문자열에서 {} 시작 - mcpId: {}, jsonLength: {}, isExist: {}", 
                    isExist ? "Update" : "Import", mcpId, importJson != null ? importJson.length() : 0, isExist);
            
            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(mcpId, importJson, projectId);
            }
            
            // 기존 데이터가 없으면 Import 수행
            McpCatalogImportResponse response = sktaiMcpService.importCatalog(mcpId, importJson);
            
            if (response == null) {
                log.error("MCP Import 응답이 null입니다 - mcpId: {}", mcpId);
                return false;
            }
            
            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("MCP JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId, projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/tools", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/sync-tools", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/ping", projectId);
                log.info("MCP JSON 문자열에서 Import - 권한 설정 완료");

                // 생성 후 활성화 (기본 policy 사용)
                List<PolicyRequest> defaultPolicyRequests = adminAuthService.getPolicyRequestsByCurrentProjectSequence(projectId);
                if (defaultPolicyRequests != null && !defaultPolicyRequests.isEmpty()) {
                    log.info("MCP JSON 문자열에서 Import - 활성화 시작 - policyRequests: {}", defaultPolicyRequests);
                    adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/activate", projectId);  
                    sktaiMcpService.activateCatalog(mcpId, defaultPolicyRequests);
                    log.info("MCP JSON 문자열에서 Import - 활성화 응답: 활성화 성공");
                } else {
                    log.warn("MCP 카탈로그 활성화 - policy가 없어 활성화를 건너뜁니다: mcpId={}", mcpId);
                }
            }
            
            Integer responseCode = response.getCode();
            String responseDetail = response.getDetail();
            
            log.info("MCP Import 응답 - mcpId: {}, code: {}, detail: {}", mcpId, responseCode, responseDetail);
            
            // 성공 코드: 1 (검증 성공 또는 생성 성공)
            boolean success = responseCode != null && responseCode.equals(1);
            
            if (!success) {
                log.warn("MCP Import 실패 - mcpId: {}, code: {}, detail: {}", mcpId, responseCode, responseDetail);
            }
            
            log.info("MCP JSON 문자열에서 Import 완료 - mcpId: {}, success: {}", mcpId, success);
            
            return success;
            
        } catch (BusinessException e) {
            log.error("MCP Import 실패 (BusinessException) - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw e;
        } catch (FeignException e) {
            log.error("MCP API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("MCP JSON 문자열에서 Import 실패 (RuntimeException) - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("MCP JSON 문자열에서 Import 실패 - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 MCP 카탈로그가 존재할 때 사용)
     * 
     * @param mcpId MCP 카탈로그 ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String mcpId, String importJson, Long projectId) {
        try {
            log.info("MCP JSON 문자열에서 Update 시작 - mcpId: {}, jsonLength: {}", mcpId,
                    importJson != null ? importJson.length() : 0);

            // JSON 문자열을 McpCatalogUpdateRequest로 변환
            McpCatalogUpdateRequest updateRequest = objectMapper.readValue(importJson, McpCatalogUpdateRequest.class);
            
            // Export JSON에는 enabled가 없으므로 Update 시 enabled: true 설정
            if (updateRequest.getEnabled() == null) {
                updateRequest.setEnabled(true);
                log.debug("MCP Update - enabled 필드 설정: true");
            }
            
            // updateCatalog 호출 (PUT)
            McpCatalogResponse response = sktaiMcpService.updateCatalog(mcpId, updateRequest);

            if (response == null) {
                log.error("MCP Update 응답이 null입니다 - mcpId: {}", mcpId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("MCP JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId, projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/tools", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/sync-tools", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/mcp/catalogs/" + mcpId + "/ping", projectId);
                log.info("MCP JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("MCP Update 성공 - mcpId: {}", mcpId);
            return true;

        } catch (FeignException e) {
            log.error("MCP API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("MCP JSON 문자열에서 Update 실패 - mcpId: {}, error: {}", 
                    mcpId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param mcpId MCP 카탈로그 ID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String mcpId, boolean saveToFile) {
        try {
            log.info("MCP Export → JSON 파일 저장 시작 - mcpId: {}, saveToFile: {}", mcpId, saveToFile);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(mcpId);
            
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
            
            // 파일명 생성: MCP_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("MCP_%s_%s.json", mcpId, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("MCP Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("MCP Export → JSON 파일 저장 실패 (IOException) - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw new RuntimeException("MCP Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("MCP Export → JSON 파일 저장 실패 - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
            throw new RuntimeException("MCP Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * MCP 카탈로그 데이터를 Import 형식으로 변환
     */
    @SuppressWarnings("unchecked")
    private String convertMcpCatalogToImportFormat(McpCatalogInfo catalogData) {
        try {
            if (catalogData == null) {
                throw new IllegalArgumentException("MCP 카탈로그 데이터가 null입니다.");
            }
            
            // MCP 카탈로그 데이터를 Map으로 변환
            Map<String, Object> map = objectMapper.convertValue(catalogData, Map.class);
            
            // 메타데이터 필드 제거
            if (map.containsKey("id")) {
                map.remove("id");
            }
            if (map.containsKey("created_at")) {
                map.remove("created_at");
            }
            if (map.containsKey("updated_at")) {
                map.remove("updated_at");
            }
            if (map.containsKey("created_by")) {
                map.remove("created_by");
            }
            if (map.containsKey("updated_by")) {
                map.remove("updated_by");
            }
            if (map.containsKey("enabled")) {
                map.remove("enabled");
            }
            if (map.containsKey("tools")) {
                map.remove("tools");
            }
            if (map.containsKey("mcp_serving_id")) {
                map.remove("mcp_serving_id");
            }
            if (map.containsKey("mcpId")) {
                map.remove("mcpId");
            }
            if (map.containsKey("mcp_id")) {
                map.remove("mcp_id");
            }
            
            // null 필드 제거 (재귀적으로 처리)
            removeNullFields(map);
            
            // JSON으로 변환
            String json = objectMapper.writeValueAsString(map);
            
            return json;
            
        } catch (JsonProcessingException e) {
            log.error("MCP 카탈로그 JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("MCP 카탈로그 Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("MCP 카탈로그 Import 형식 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("MCP 카탈로그 Import 형식 변환 실패: " + e.getMessage(), e);
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
     * MCP 타입 필드 추출
     * 
     * <p>server_url, auth_type, auth_config 필드를 dev/prod 구조로 추출합니다.</p>
     * 
     * @param jsonNode JSON 노드
     * @param id 파일 ID
     * @param fields 추출할 필드 목록 (server_url, auth_type, auth_config)
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return 추출된 필드 Map
     */
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields, Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            for (String field : fields) {
                if ("authConfig".equals(field) || "auth_config".equals(field)) {
                    // auth_config는 복잡한 구조 (Object) - api_param처럼 내부 필드들을 dev/prod로 분리
                    Map<String, Object> authConfigMap = new HashMap<>();
                    
                    // 파일에서 auth_config 읽기
                    Map<String, Object> fileAuthConfigMap = new HashMap<>();
                    JsonNode authConfigNode = null;
                    if (jsonNode.has("auth_config") && !jsonNode.get("auth_config").isNull()) {
                        authConfigNode = jsonNode.get("auth_config");
                    } else if (jsonNode.has("authConfig") && !jsonNode.get("authConfig").isNull()) {
                        authConfigNode = jsonNode.get("authConfig");
                    }
                    
                    if (authConfigNode != null && authConfigNode.isObject()) {
                        try {
                            Object authConfigValue = objectMapper.treeToValue(authConfigNode, Object.class);
                            if (authConfigValue instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> authConfigValueMap = (Map<String, Object>) authConfigValue;
                                fileAuthConfigMap = authConfigValueMap;
                                log.debug("auth_config Map 변환 완료 - id: {}, keys: {}", id, authConfigValueMap.keySet());
                            }
                        } catch (Exception e) {
                            log.warn("auth_config 변환 실패 - id: {}, error: {}", id, e.getMessage(), e);
                        }
                    }
                    
                    // DB에서 auth_config 조회
                    Map<String, Object> dbAuthConfigMap = new HashMap<>();
                    String dbValue = getValueFromDb.apply("auth_config");
                    if (dbValue != null && !dbValue.isEmpty()) {
                        try {
                            JsonNode dbJsonNode = objectMapper.readTree(dbValue);
                            if (dbJsonNode.isObject()) {
                                Object dbAuthConfigValue = objectMapper.treeToValue(dbJsonNode, Object.class);
                                if (dbAuthConfigValue instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> dbMap = (Map<String, Object>) dbAuthConfigValue;
                                    dbAuthConfigMap = dbMap;
                                    log.debug("DB auth_config 파싱 완료 - id: {}, keys: {}", id, dbMap.keySet());
                                }
                            }
                        } catch (Exception e) {
                            log.debug("DB auth_config JSON 파싱 실패 - id: {}, error: {}", id, e.getMessage());
                        }
                    }
                    
                    // auth_config의 모든 가능한 필드 목록
                    Set<String> allKeys = new HashSet<>();
                    allKeys.addAll(fileAuthConfigMap.keySet());
                    allKeys.addAll(dbAuthConfigMap.keySet());
                    
                    // 각 필드별로 dev/prod 추출
                    for (String key : allKeys) {
                        Object fileValue = fileAuthConfigMap.get(key);
                        Object dbValueObj = dbAuthConfigMap.get(key);
                        
                        Map<String, Object> fieldMap = new HashMap<>();
                        fieldMap.put("dev", fileValue != null ? fileValue : "");
                        fieldMap.put("prod", dbValueObj != null ? dbValueObj : "");
                        
                        authConfigMap.put(key, fieldMap);
                        log.debug("auth_config 필드 추출 - id: {}, key: {}, hasDev: {}, hasProd: {}", 
                                id, key, fileValue != null, dbValueObj != null);
                    }
                    
                    result.put("auth_config", authConfigMap);
                    
                } else {
                    // server_url은 문자열 필드 (auth_type은 변경될 일이 없어서 추출 대상에서 제외됨)
                    String fieldName = field;
                    if (!jsonNode.has(field)) {
                        if (field.equals("server_url") && jsonNode.has("serverUrl")) {
                            fieldName = "serverUrl";
                        }
                        // auth_type은 추출 대상에서 제외되었지만, 혹시 모를 경우를 대비해 로직은 유지
                        // else if (field.equals("auth_type") && jsonNode.has("authType")) {
                        //     fieldName = "authType";
                        // }
                    }
                    
                    String fileValue = null;
                    if (jsonNode.has(fieldName) && !jsonNode.get(fieldName).isNull()) {
                        fileValue = jsonNode.get(fieldName).asText();
                    }
                    
                    String dbValue = getValueFromDb.apply(field);
                    
                    // dev/prod 구조로 저장
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("dev", fileValue != null ? fileValue : "");
                    fieldMap.put("prod", dbValue != null ? dbValue : "");
                    
                    result.put(field, fieldMap);
                }
            }
        } catch (RuntimeException e) {
            log.error("MCP 필드 추출 실패 (RuntimeException) - id: {}, error: {}", id, e.getMessage(), e);
        } catch (Exception e) {
            log.error("MCP 필드 추출 실패 - id: {}, error: {}", id, e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * MCP 존재 여부 확인
     * 
     * @param mcpId MCP ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String mcpId) {
        try {
            sktaiMcpService.getCatalogById(mcpId);
            return true;
        } catch (Exception e) {
            log.debug("MCP 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", mcpId, e.getMessage());
            return false;
        }
    }
}
