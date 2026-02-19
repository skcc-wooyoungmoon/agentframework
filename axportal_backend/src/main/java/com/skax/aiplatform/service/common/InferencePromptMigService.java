package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptExportResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentInferencePromptsService;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inference Prompt 마이그레이션 서비스
 * 
 * <p>Inference Prompt 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InferencePromptMigService {
    
    private final SktaiAgentInferencePromptsService sktaiAgentInferencePromptsService;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>Prompt Export API를 호출하고 Import 형식으로 변환합니다.</p>
     * 
     * @param promptUuid Prompt UUID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String promptUuid) {
        try {
            log.info("Inference Prompt Export → Import 형식 변환 시작 - promptUuid: {}", promptUuid);
            
            // Export API 호출
            PromptExportResponse exportResponse = sktaiAgentInferencePromptsService.getPromptExport(promptUuid);
            if (exportResponse == null || exportResponse.getData() == null) {
                throw new RuntimeException("Prompt Export 데이터를 찾을 수 없습니다: " + promptUuid);
            }
            
            // Import 형식으로 변환
            String importJson = convertPromptToImportFormatFromExport(exportResponse.getData());
            
            log.info("Inference Prompt Export → Import 형식 변환 완료 - promptUuid: {}, jsonLength: {}", promptUuid, importJson.length());
            
            return importJson;
            
        } catch (FeignException e) {
            log.error("Inference Prompt API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Inference Prompt Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Inference Prompt Export → Import 형식 변환 실패 - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            throw new RuntimeException("Inference Prompt Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param promptUuid Prompt UUID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String promptUuid) {
        try {
            log.info("Inference Prompt Export → Import 거래 시작 - promptUuid: {}", promptUuid);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(promptUuid);
            
            // 2. Import 거래 호출
            PromptCreateResponse response = sktaiAgentInferencePromptsService.importPrompt(promptUuid, importJson);
            
            boolean success = response != null && response.getData() != null;
            
            log.info("Inference Prompt Export → Import 거래 완료 - promptUuid: {}, success: {}", promptUuid, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Inference Prompt API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Inference Prompt Export → Import 거래 실패 - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     * 
     * @param promptUuid Prompt UUID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String promptUuid, String importJson, Long projectId) {
        return importFromJsonString(promptUuid, importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     * 
     * @param promptUuid Prompt UUID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String promptUuid, String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Inference Prompt JSON 문자열에서 {} 시작 - promptUuid: {}, isExist: {}", 
                    isExist ? "Update" : "Import", promptUuid, isExist);
            
            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(promptUuid, importJson, projectId);
            }
            
            // 기존 데이터가 없으면 Import 수행
            PromptCreateResponse response = sktaiAgentInferencePromptsService.importPrompt(promptUuid, importJson);
            
            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Inference Prompt JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/" + promptUuid, projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/prompt/" + promptUuid, projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/versions/" + promptUuid, projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/versions/" + promptUuid + "/latest", projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/lineages/" + promptUuid + "/upstream", projectId);
                //
                // // Inference Prompt 버전별 리소스 URL 추가
                // PromptVersionsResponse versionsResponse = sktaiAgentInferencePromptsService.getInferencePromptVersions(promptUuid);
                // if (versionsResponse != null && versionsResponse.getData() != null) {
                //     for (PromptVersionsResponse.VersionData versionDetail : versionsResponse.getData()) {
                //         if (versionDetail == null || versionDetail.getVersionId() == null) {
                //             continue;
                //         }
                //         adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/variables/" + versionDetail.getVersionId(), projectId);
                //         adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/messages/" + versionDetail.getVersionId(), projectId);
                //         adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/tags/" + versionDetail.getVersionId(), projectId);
                //     }
                // }
                log.info("Inference Prompt JSON 문자열에서 Import - 권한 설정 완료");
            }

            boolean success = response != null && response.getData() != null;
            
            log.info("Inference Prompt JSON 문자열에서 Import 완료 - promptUuid: {}, success: {}", promptUuid, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Inference Prompt API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Inference Prompt JSON 문자열에서 Import 실패 - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 Prompt가 존재할 때 사용)
     * 
     * @param promptUuid Prompt UUID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String promptUuid, String importJson, Long projectId) {
        try {
            log.info("Inference Prompt JSON 문자열에서 Update 시작 - promptUuid: {}, jsonLength: {}", promptUuid,
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
                    log.debug("Inference Prompt JSON 변환 완료 - name → new_name");
                }
            } catch (Exception e) {
                log.warn("Inference Prompt JSON 변환 중 오류 (원본 사용) - error: {}", e.getMessage());
            }

            // JSON 문자열을 PromptUpdateRequest로 변환
            PromptUpdateRequest updateRequest = objectMapper.readValue(convertedJson, PromptUpdateRequest.class);
            
            // updateInferencePrompt 호출 (PUT)
            PromptUpdateOrDeleteResponse response = sktaiAgentInferencePromptsService.updateInferencePrompt(promptUuid, updateRequest);

            if (response == null) {
                log.error("Inference Prompt Update 응답이 null입니다 - promptUuid: {}", promptUuid);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Inference Prompt JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/" + promptUuid, projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/prompt/" + promptUuid, projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/versions/" + promptUuid, projectId);
                // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/versions/" + promptUuid + "/latest", projectId);
                log.info("Inference Prompt JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Inference Prompt Update 성공 - promptUuid: {}", promptUuid);
            return true;

        } catch (FeignException e) {
            log.error("Inference Prompt API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Inference Prompt JSON 문자열에서 Update 실패 - promptUuid: {}, error: {}", 
                    promptUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param promptUuid Prompt UUID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String promptUuid, boolean saveToFile) {
        try {
            log.info("Inference Prompt Export → JSON 파일 저장 시작 - promptUuid: {}, saveToFile: {}", promptUuid, saveToFile);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(promptUuid);
            
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
            
            // 파일명 생성: INFERENCE_PROMPT_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("INFERENCE_PROMPT_%s_%s.json", promptUuid, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Inference Prompt Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("Inference Prompt Export → JSON 파일 저장 실패 (IOException) - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            throw new RuntimeException("Inference Prompt Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Inference Prompt Export → JSON 파일 저장 실패 - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            throw new RuntimeException("Inference Prompt Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * ExportResponse 데이터를 Import 형식으로 변환
     */
    private String convertPromptToImportFormatFromExport(PromptExportResponse.PromptExportData exportData) {
        try {
            if (exportData == null) {
                throw new IllegalArgumentException("Export 데이터가 null입니다.");
            }
            
            String name = exportData.getName();
            Integer ptype = exportData.getPtype();
            String desc = exportData.getDesc();
            List<PromptExportResponse.PromptExportMessage> messages = exportData.getMessages();
            List<PromptExportResponse.PromptExportVariable> variables = exportData.getVariables();
            List<PromptExportResponse.PromptExportTag> tags = exportData.getTags();
            
            // 1. messages를 sequence 순서대로 정렬
            List<PromptExportResponse.PromptExportMessage> sortedMessages = new ArrayList<>(
                    messages != null ? messages : new ArrayList<>());
            sortedMessages.sort(Comparator.comparing(msg -> msg.getSequence() != null ? msg.getSequence() : 0));
            
            // Import API 형식에 맞게 필수 필드만 포함 (시스템 생성 필드 제외)
            List<Map<String, Object>> sortedMessagesList = new ArrayList<>();
            for (PromptExportResponse.PromptExportMessage message : sortedMessages) {
                Map<String, Object> messageMap = new LinkedHashMap<>();
                messageMap.put("message", message.getMessage());
                messageMap.put("mtype", message.getMtype());
                sortedMessagesList.add(messageMap);
            }
            
            // 2. variables 변환 (시스템 생성 필드 제외: uuid, variable_uuid, version_id)
            List<Map<String, Object>> variablesList = new ArrayList<>();
            List<PromptExportResponse.PromptExportVariable> variablesList2 = variables != null ? variables : new ArrayList<>();
            for (PromptExportResponse.PromptExportVariable variable : variablesList2) {
                Map<String, Object> variableMap = new LinkedHashMap<>();
                variableMap.put("variable", variable.getVariable());
                variableMap.put("validation", variable.getValidation() != null ? variable.getValidation() : "");
                variableMap.put("token_limit", variable.getTokenLimit() != null ? variable.getTokenLimit() : 0);
                variableMap.put("validation_flag", variable.getValidationFlag() != null ? variable.getValidationFlag() : false);
                variableMap.put("token_limit_flag", variable.getTokenLimitFlag() != null ? variable.getTokenLimitFlag() : false);
                variablesList.add(variableMap);
            }
            
            // 3. tags 변환 (시스템 생성 필드 제외: uuid, tag_uuid, version_id)
            List<Map<String, Object>> tagsList = new ArrayList<>();
            List<PromptExportResponse.PromptExportTag> tagsList2 = tags != null ? tags : new ArrayList<>();
            for (PromptExportResponse.PromptExportTag tag : tagsList2) {
                Map<String, Object> tagMap = new LinkedHashMap<>();
                tagMap.put("tag", tag.getTag());
                tagsList.add(tagMap);
            }
            
            // 4. Import 형식 구성
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("desc", desc != null ? desc : "");
            result.put("messages", sortedMessagesList);
            result.put("name", name);
            if (ptype != null) {
                result.put("ptype", ptype);
            }
            result.put("release", true);
            result.put("tags", tagsList);
            result.put("variables", variablesList);
            
            // JSON으로 변환
            String json = objectMapper.writeValueAsString(result);
            
            return json;
            
        } catch (JsonProcessingException e) {
            log.error("Inference Prompt JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Export 데이터를 Import 형식으로 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Export 데이터를 Import 형식으로 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Export 데이터를 Import 형식으로 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * Prompt 존재 여부 확인
     * 
     * @param promptId Prompt ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String promptId) {
        try {
            sktaiAgentInferencePromptsService.getInferencePrompt(promptId);
            return true;
        } catch (Exception e) {
            log.debug("Prompt 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", promptId, e.getMessage());
            return false;
        }
    }
}


