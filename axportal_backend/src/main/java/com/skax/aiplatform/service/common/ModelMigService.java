package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelImportRequest;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelExportResponse;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelImportResponse;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelsService;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Model 마이그레이션 서비스
 * 
 * <p>Model 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelMigService {
    
    private final SktaiModelsService sktaiModelsService;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>Model Export API를 호출하고 Import 형식으로 변환합니다.</p>
     * 
     * @param modelId Model ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String modelId, String projectId) {
        try {
            log.info("Model Export → Import 형식 변환 시작 - modelId: {}", modelId);
            
            // Export API 호출
            ModelExportResponse exportResponse = sktaiModelsService.exportModel(modelId);
            if (exportResponse == null) {
                throw new RuntimeException("Model Export 데이터를 찾을 수 없습니다: " + modelId);
            }


            // 현재 프로젝트로 policy 조회 후 import 형식에 포함
            // List<PolicyRequest> policyRequests = null;
            // if (projectId != null && !projectId.trim().isEmpty()) {
            //     try {
            //         Long projectSeq = Long.parseLong(projectId);
            //         policyRequests = adminAuthService.getPolicyRequestsByCurrentProjectSequence(projectSeq);
            //         log.info("Policy 조회 완료 - projectId: {}, policyCount: {}", projectId,
            //                 policyRequests != null ? policyRequests.size() : 0);
            //     } catch (Exception e) {
            //         log.warn("Policy 조회 실패 (계속 진행) - projectId: {}, error: {}", projectId, e.getMessage());
            //         // Policy 조회 실패해도 Export는 계속 진행
            //     }
            // }

            
            // Export → Import 형식으로 변환 (동일한 구조이므로 직접 매핑)
            String json = objectMapper.writeValueAsString(exportResponse);
            ModelImportRequest importRequest = objectMapper.readValue(json, ModelImportRequest.class);
            
            // 조회된 policyRequests를 importRequest에 설정
            // if (policyRequests != null) {
            //     importRequest.setPolicy(policyRequests);
            //     log.info("Policy를 ImportRequest에 설정 완료 - policyCount: {}", policyRequests.size());
            // }
            
            String importJson = objectMapper.writeValueAsString(importRequest);
            
            log.info("Model Export → Import - importJson: {}", importJson);
            log.info("Model Export → Import 형식 변환 완료 - modelId: {}, jsonLength: {}", modelId, importJson.length());
            
            return importJson;
            
        } catch (JsonProcessingException e) {
            log.error("Model JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Model Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (FeignException e) {
            log.error("Model API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Model Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Model Export → Import 형식 변환 실패 - modelId: {}, error: {}", modelId, e.getMessage(), e);
            throw new RuntimeException("Model Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param modelId Model ID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String modelId, String projectId) {
        try {
            log.info("Model Export → Import 거래 시작 - modelId: {}", modelId);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(modelId, projectId);
            
            // 2. JSON을 ModelImportRequest로 변환
            ModelImportRequest importRequest = objectMapper.readValue(importJson, ModelImportRequest.class);
            
            // 3. Import 거래 호출
            ModelImportResponse response = sktaiModelsService.importModel(importRequest);
            
            boolean success = response != null && response.getId() != null;
            
            log.info("Model Export → Import 거래 완료 - modelId: {}, success: {}", modelId, success);
            
            return success;
            
        } catch (JsonProcessingException e) {
            log.error("Model JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (FeignException e) {
            log.error("Model API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Model Export → Import 거래 실패 - modelId: {}, error: {}", modelId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     * 
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String importJson, Long projectId) {
        return importFromJsonString(importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     * 
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Model JSON 문자열에서 {} 시작 - projectId: {}, isExist: {}", 
                    isExist ? "Update" : "Import", projectId, isExist);
            
            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(importJson, projectId);
            }
            
            // 기존 데이터가 없으면 Import 수행
            ModelImportRequest importRequest = objectMapper.readValue(importJson, ModelImportRequest.class);
            log.info("Model JSON 문자열에서 Import 요청: {}", importRequest);
            
            // List<PolicyRequest> policyRequests = importRequest.getPolicy();
            // log.info("Policy 조회 완료 - policyCount: {}", policyRequests.size());

            ModelImportResponse response = sktaiModelsService.importModel(importRequest);
            log.info("Model JSON 문자열에서 Import 응답: {}", response);

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Model JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/models/" + response.getId(), projectId);
                
                // endpoints에 대한 권한 설정 (루트 레벨 우선, 없으면 model 내부 확인)
                List<ModelImportResponse.Endpoint> endpoints = response.getEndpoints();
                log.info("Model JSON 문자열에서 Import - endpoints: {}", endpoints);
                
                if (endpoints != null && !endpoints.isEmpty()) {
                    log.info("Model JSON 문자열에서 Import - Endpoint 권한 설정 시작");
                    
                    String modelId = response.getId();
                    // endpoint 목록에 대한 권한 설정
                    adminAuthService.setResourcePolicyByProjectSequence("/api/v1/models/" + modelId + "/endpoints", projectId);
                    
                    // 각 endpoint에 대한 권한 설정
                    for (ModelImportResponse.Endpoint endpoint : endpoints) {
                        if (endpoint.getId() != null && !endpoint.getId().isEmpty()) {
                            adminAuthService.setResourcePolicyByProjectSequence(
                                    "/api/v1/models/" + modelId + "/endpoints/" + endpoint.getId(), projectId);
                            log.debug("Model JSON 문자열에서 Endpoint 권한 설정 완료 - endpointId: {}", endpoint.getId());
                        }
                    }
                    log.info("Model JSON 문자열에서 Import - Endpoint 권한 설정 완료");
                }
                
                log.info("Model JSON 문자열에서 Import - 권한 설정 완료");
            }
             
            boolean success = response != null && response.getId() != null;
            log.info("Model JSON 문자열에서 Import 완료 - success: {}", success);
            
            return success;
            
        } catch (JsonProcessingException e) {
            log.error("Model JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (FeignException e) {
            log.error("Model API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Model JSON 문자열에서 Import 실패 - error: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 Model이 존재할 때 사용)
     * 
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String importJson, Long projectId) {
        try {
            log.info("Model JSON 문자열에서 Update 시작 - jsonLength: {}",
                    importJson != null ? importJson.length() : 0);

            // Import JSON에서 model 정보와 id 추출
            ModelImportRequest importRequest = objectMapper.readValue(importJson, ModelImportRequest.class);
            
            // model.id 추출
            String modelId = null;
            if (importRequest.getModel() != null) {
                modelId = importRequest.getModel().getId();
            }
            
            if (modelId == null || modelId.isEmpty()) {
                log.error("Model Update - modelId를 찾을 수 없습니다");
                return false;
            }
            
            // ModelUpdate 객체 생성 (model 정보에서 추출)
            ModelUpdate updateRequest = objectMapper.convertValue(importRequest.getModel(), ModelUpdate.class);
            
            // editModel 호출 (PUT)
            ModelRead response = sktaiModelsService.editModel(modelId, updateRequest);

            if (response == null) {
                log.error("Model Update 응답이 null입니다 - modelId: {}", modelId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Model JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/models/" + modelId, projectId);
                log.info("Model JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Model Update 성공 - modelId: {}", modelId);
            return true;

        } catch (FeignException e) {
            log.error("Model API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Model JSON 문자열에서 Update 실패 - error: {}", 
                    e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param modelId Model ID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String modelId, String projectId, boolean saveToFile) {
        try {
            log.info("Model Export → JSON 파일 저장 시작 - modelId: {}, saveToFile: {}", modelId, saveToFile);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(modelId, projectId);
            
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
            
            // 파일명 생성: MODEL_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("MODEL_%s_%s.json", modelId, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Model Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("Model Export → JSON 파일 저장 실패 (IOException) - modelId: {}, error: {}", modelId, e.getMessage(), e);
            throw new RuntimeException("Model Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Model Export → JSON 파일 저장 실패 - modelId: {}, error: {}", modelId, e.getMessage(), e);
            throw new RuntimeException("Model Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields,
            Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        log.info("MIGRATION :: Model 필드 추출 시작");
        log.info("MIGRATION :: Model 필드 추출 시작 - id: {}, fields: {}, jsonNode: {}", id, fields, jsonNode);

        String servingType = jsonNode.path("model").path("serving_type").asText();
        log.info("MIGRATION :: Model 필드 추출 시작 - servingType: {}", servingType);

        if("self-hosting".equalsIgnoreCase(servingType)) {
            log.info("MIGRATION :: self-hosting 모델은 제외");
            return result;
        } 

        try { 
            for (String field : fields) {
                String fileValue = null;
                
                // 중첩 필드 처리 (예: endpoints.url, endpoints.key)
                if (field.contains(".")) {
                    String[] parts = field.split("\\.", 2);
                    String parentField = parts[0];  // endpoints
                    String childField = parts[1];   // url 또는 key
                    
                    if (jsonNode.has(parentField) && !jsonNode.get(parentField).isNull()) {
                        JsonNode parentNode = jsonNode.get(parentField);
                        // 배열인 경우 첫 번째 요소에서 추출
                        if (parentNode.isArray() && parentNode.size() > 0) {
                            JsonNode firstElement = parentNode.get(0);
                            log.info("MIGRATION :: Model 중첩 필드 추출 - parentField: {}, childField: {}, firstElement: {}", 
                                    parentField, childField, firstElement);
                            if (firstElement.has(childField) && !firstElement.get(childField).isNull()) {
                                fileValue = firstElement.get(childField).asText();
                                log.info("MIGRATION :: Model 중첩 필드 추출 성공 - childField: {}, fileValue: [{}],", 
                                        childField, fileValue);
                            } 
                        }
                        // 객체인 경우 직접 추출
                        else if (parentNode.isObject() && parentNode.has(childField)) {
                            fileValue = parentNode.get(childField).asText();
                        }
                    }
                } else {
                    // 일반 필드 처리 (기존 로직)
                    if (jsonNode.has(field) && !jsonNode.get(field).isNull()) {
                        JsonNode fieldNode = jsonNode.get(field);
                        // 배열이나 객체인 경우 toString(), primitive 값인 경우 asText() 사용
                        if (fieldNode.isArray() || fieldNode.isObject()) {
                            fileValue = fieldNode.toString();
                        } else {
                            fileValue = fieldNode.asText();
                        }
                    }
                }
                log.info("MIGRATION :: Model 필드 추출 - field: {}, fileValue: {}", field, fileValue);

                String dbValue = getValueFromDb.apply(field);

                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("dev", fileValue != null ? fileValue : "");
                fieldMap.put("prod", dbValue != null ? dbValue : "");
                result.put(field, fieldMap);
            }
        } catch (RuntimeException e) {
            log.error("MIGRATION :: Model 필드 추출 실패 (RuntimeException) - id: {}, error: {}", id, e.getMessage(),
                    e);
        } catch (Exception e) {
            log.error("MIGRATION :: Model 필드 추출 실패 - id: {}, error: {}", id, e.getMessage(), e);
        }
        log.info("MIGRATION :: Model 필드 추출 완료 - id: {}, result: {}", id, result);
        return result;
    }
    
    /**
     * Model 존재 여부 확인
     * 
     * @param modelId Model ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String modelId) {
        try {
            sktaiModelsService.readModel(modelId);
            return true;
        } catch (Exception e) {
            log.debug("Model 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", modelId, e.getMessage());
            return false;
        }
    }
}


