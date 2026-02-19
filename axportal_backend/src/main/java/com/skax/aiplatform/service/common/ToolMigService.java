package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.ToolRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolImportResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentToolsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.converter.common.MigDataConverter;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Tool 마이그레이션 서비스
 * 
 * <p>Tool 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolMigService {
    
    private final SktaiAgentToolsService sktaiAgentToolsService;
    private final MigDataConverter migDataConverter;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>Tool을 조회하고 Import 형식으로 변환합니다.</p>
     * 
     * @param toolId Tool ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String toolId) {
        try {
            log.info("Tool Export → Import 형식 변환 시작 - toolId: {}", toolId);
            
            // Tool 조회
            ToolResponse tool = sktaiAgentToolsService.getToolById(toolId);
            if (tool == null || tool.getData() == null) {
                throw new RuntimeException("Tool을 찾을 수 없습니다: " + toolId);
            }
            
            // Import 형식으로 변환
            String importJson = migDataConverter.convertToolToImportFormat(tool);
            
            log.info("Tool Export → Import 형식 변환 완료 - toolId: {}, jsonLength: {}", toolId, importJson.length());
            
            return importJson;
            
        } catch (FeignException e) {
            log.error("Tool API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Tool Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (BusinessException e) {
            log.error("Tool Export → Import 형식 변환 실패 (BusinessException) - toolId: {}, error: {}", toolId, e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Tool Export → Import 형식 변환 실패 (RuntimeException) - toolId: {}, error: {}", toolId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Tool Export → Import 형식 변환 실패 - toolId: {}, error: {}", toolId, e.getMessage(), e);
            throw new RuntimeException("Tool Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param toolId Tool ID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String toolId) {
        try {
            log.info("Tool Export → Import 거래 시작 - toolId: {}", toolId);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(toolId);
            
            // 2. Import 거래 호출
            ToolImportResponse response = sktaiAgentToolsService.importTool(toolId, importJson);
            
            // 성공 코드: 1 (검증 성공 또는 생성 성공)
            boolean success = response != null && response.getCode() != null && response.getCode().equals(1);
            
            if (!success && response != null) {
                log.warn("Tool Export → Import 거래 실패 - toolId: {}, code: {}, detail: {}", 
                        toolId, response.getCode(), response.getDetail());
            }
            
            log.info("Tool Export → Import 거래 완료 - toolId: {}, success: {}", toolId, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Tool API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Tool Export → Import 거래 실패 - toolId: {}, error: {}", toolId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     * 
     * @param toolId Tool ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String toolId, String importJson, Long projectId) {
        return importFromJsonString(toolId, importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     * 
     * @param toolId Tool ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String toolId, String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Tool JSON 문자열에서 {} 시작 - toolId: {}, jsonLength: {}, isExist: {}", 
                    isExist ? "Update" : "Import", toolId, importJson != null ? importJson.length() : 0, isExist);
            
            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(toolId, importJson, projectId);
            }
            
            // 기존 데이터가 없으면 Import 수행
            ToolImportResponse response = sktaiAgentToolsService.importTool(toolId, importJson);
            
            if (response == null) {
                log.error("Tool Import 응답이 null입니다 - toolId: {}", toolId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Tool JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/tools/" + response.getData().getId(), projectId);
                log.info("Tool JSON 문자열에서 Import - 권한 설정 완료");
            }
            
            Integer responseCode = response.getCode();
            String responseDetail = response.getDetail();
            
            log.info("Tool Import 응답 - toolId: {}, code: {}, detail: {}", toolId, responseCode, responseDetail);
            
            // 성공 코드: 1 (검증 성공 또는 생성 성공)
            boolean success = responseCode != null && responseCode.equals(1);
            
            if (!success) {
                log.warn("Tool Import 실패 - toolId: {}, code: {}, detail: {}", toolId, responseCode, responseDetail);
            }
            
            log.info("Tool JSON 문자열에서 Import 완료 - toolId: {}, success: {}", toolId, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Tool API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Tool JSON 문자열에서 Import 실패 (RuntimeException) - toolId: {}, error: {}", toolId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Tool JSON 문자열에서 Import 실패 - toolId: {}, error: {}", toolId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 Tool이 존재할 때 사용)
     * 
     * @param toolId Tool ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    // Default Project ID (기본값: default project)
    private static final String DEFAULT_PROJECT_ID = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5";
    
    public boolean updateFromJsonString(String toolId, String importJson, Long projectId) {
        try {
            log.info("Tool JSON 문자열에서 Update 시작 - toolId: {}, jsonLength: {}", toolId,
                    importJson != null ? importJson.length() : 0);

            // JSON 문자열을 ToolRequest로 변환
            ToolRequest updateRequest = objectMapper.readValue(importJson, ToolRequest.class);
            
            // Export JSON에는 project_id가 없으므로 Update 시 default project_id 설정
            if (updateRequest.getProjectId() == null || updateRequest.getProjectId().isEmpty()) {
                updateRequest.setProjectId(DEFAULT_PROJECT_ID);
                log.debug("Tool Update - project_id 필드 설정: {}", DEFAULT_PROJECT_ID);
            }
            
            // updateTool 호출 (PUT)
            ToolUpdateResponse response = sktaiAgentToolsService.updateTool(toolId, updateRequest);

            if (response == null) {
                log.error("Tool Update 응답이 null입니다 - toolId: {}", toolId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Tool JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/tools/" + toolId, projectId);
                log.info("Tool JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Tool Update 성공 - toolId: {}", toolId);
            return true;

        } catch (FeignException e) {
            log.error("Tool API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Tool JSON 문자열에서 Update 실패 - toolId: {}, error: {}", 
                    toolId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param toolId Tool ID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String toolId, boolean saveToFile) {
        try {
            log.info("Tool Export → JSON 파일 저장 시작 - toolId: {}, saveToFile: {}", toolId, saveToFile);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(toolId);
            
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
            
            // 파일명 생성: TOOL_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("TOOL_%s_%s.json", toolId, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Tool Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("Tool Export → JSON 파일 저장 실패 (IOException) - toolId: {}, error: {}", toolId, e.getMessage(), e);
            throw new RuntimeException("Tool Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Tool Export → JSON 파일 저장 실패 - toolId: {}, error: {}", toolId, e.getMessage(), e);
            throw new RuntimeException("Tool Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * TOOL 타입 필드 추출
     * 
     * <p>server_url과 api_param 필드를 추출합니다.</p>
     * <p>api_param은 복잡한 구조로, body와 params가 여러 개일 수 있으며, params는 중복 키일 수 있습니다.</p>
     * 
     * @param jsonNode JSON 노드
     * @param id 파일 ID
     * @param fields 추출할 필드 목록 (server_url, api_param)
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return 추출된 필드 Map
     */
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields, Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            for (String field : fields) {
                if ("server_url".equals(field)) {
                    // server_url은 최상위 레벨 필드
                    String fileValue = null;
                    if (jsonNode.has("server_url") && !jsonNode.get("server_url").isNull()) {
                        fileValue = jsonNode.get("server_url").asText();
                    }
                    
                    String dbValue = getValueFromDb.apply(field);
                    
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("dev", fileValue != null ? fileValue : "");
                    fieldMap.put("prod", dbValue != null ? dbValue : "");
                    result.put(field, fieldMap);
                    
                } else if ("api_param".equals(field)) {
                    // api_param은 복잡한 구조 (header, static_params, dynamic_params, static_body, dynamic_body, body, params 등)
                    // 각 필드를 dev/prod로 분리하여 추출
                    Map<String, Object> apiParamMap = new HashMap<>();
                    
                    // 파일에서 api_param 읽기
                    Map<String, Object> fileApiParamMap = new HashMap<>();
                    if (jsonNode.has("api_param") && !jsonNode.get("api_param").isNull()) {
                        JsonNode apiParamNode = jsonNode.get("api_param");
                        
                        log.debug("api_param 추출 시작 - id: {}, apiParamNode: {}", id, apiParamNode);
                        
                        try {
                            // api_param 전체를 Map으로 변환
                            Object apiParamValue = objectMapper.treeToValue(apiParamNode, Object.class);
                            
                            if (apiParamValue instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> apiParamValueMap = (Map<String, Object>) apiParamValue;
                                fileApiParamMap = apiParamValueMap;
                                log.debug("api_param Map 변환 완료 - id: {}, keys: {}", id, apiParamValueMap.keySet());
                            }
                        } catch (Exception e) {
                            log.warn("api_param 변환 실패 - id: {}, error: {}", id, e.getMessage(), e);
                        }
                    }
                    
                    // DB에서 api_param 조회
                    Map<String, Object> dbApiParamMap = new HashMap<>();
                    String dbValue = getValueFromDb.apply(field);
                    if (dbValue != null && !dbValue.isEmpty()) {
                        try {
                            JsonNode dbJsonNode = objectMapper.readTree(dbValue);
                            if (dbJsonNode.isObject()) {
                                Object dbApiParamValue = objectMapper.treeToValue(dbJsonNode, Object.class);
                                if (dbApiParamValue instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> dbMap = (Map<String, Object>) dbApiParamValue;
                                    dbApiParamMap = dbMap;
                                    log.debug("DB api_param 파싱 완료 - id: {}, keys: {}", id, dbMap.keySet());
                                }
                            }
                        } catch (Exception e) {
                            log.debug("DB api_param JSON 파싱 실패 - id: {}, error: {}", id, e.getMessage());
                        }
                    }
                    
                    // api_param의 모든 가능한 필드 목록
                    Set<String> allKeys = new HashSet<>();
                    allKeys.addAll(fileApiParamMap.keySet());
                    allKeys.addAll(dbApiParamMap.keySet());
                    
                    // 각 필드별로 dev/prod 추출
                    for (String key : allKeys) {
                        Object fileValue = fileApiParamMap.get(key);
                        Object dbValueObj = dbApiParamMap.get(key);
                        
                        Map<String, Object> fieldMap = new HashMap<>();
                        fieldMap.put("dev", fileValue != null ? fileValue : new HashMap<>());
                        fieldMap.put("prod", dbValueObj != null ? dbValueObj : new HashMap<>());
                        
                        apiParamMap.put(key, fieldMap);
                        log.debug("api_param 필드 추출 - id: {}, key: {}, hasDev: {}, hasProd: {}", 
                                id, key, fileValue != null, dbValueObj != null);
                    }
                    
                    result.put(field, apiParamMap);
                }
            }
        } catch (RuntimeException e) {
            log.error("Tool 필드 추출 실패 (RuntimeException) - id: {}, error: {}", id, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Tool 필드 추출 실패 - id: {}, error: {}", id, e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * Tool 존재 여부 확인
     * 
     * @param toolId Tool ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String toolId) {
        try {
            sktaiAgentToolsService.getToolById(toolId);
            return true;
        } catch (Exception e) {
            log.debug("Tool 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", toolId, e.getMessage());
            return false;
        }
    }
}

