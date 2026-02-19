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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBUpdateResponse;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectordbImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBDetailResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectordbImportResponse;
import com.skax.aiplatform.client.sktai.knowledge.service.SktaiVectorDbsService;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Vector DB 마이그레이션 서비스
 * 
 * <p>Vector DB 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDbMigService {
    
    private final SktaiVectorDbsService sktaiVectorDbsService;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>Vector DB를 조회하고 Import 형식으로 변환합니다.</p>
     * 
     * @param vectorDbId Vector DB ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String vectorDbId) {
        try {
            log.info("Vector DB Export → Import 형식 변환 시작 - vectorDbId: {}", vectorDbId);
            
            // Vector DB 조회
            VectorDBDetailResponse vectorDb = sktaiVectorDbsService.getVectorDb(vectorDbId);
            if (vectorDb == null) {
                throw new RuntimeException("Vector DB를 찾을 수 없습니다: " + vectorDbId);
            }
            
            // Import 형식으로 변환
            VectordbImportRequest importRequest = convertVectorDbToImportRequest(vectorDb, vectorDbId);
            String importJson = objectMapper.writeValueAsString(importRequest);
            
            log.info("Vector DB Export → Import 형식 변환 완료 - vectorDbId: {}, jsonLength: {}", vectorDbId, importJson.length());
            
            return importJson;
            
        } catch (JsonProcessingException e) {
            log.error("Vector DB JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Vector DB Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (FeignException e) {
            log.error("Vector DB API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Vector DB Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Vector DB Export → Import 형식 변환 실패 - vectorDbId: {}, error: {}", vectorDbId, e.getMessage(), e);
            throw new RuntimeException("Vector DB Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param vectorDbId Vector DB ID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String vectorDbId) {
        try {
            log.info("Vector DB Export → Import 거래 시작 - vectorDbId: {}", vectorDbId);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(vectorDbId);
            
            // 2. JSON을 VectordbImportRequest로 변환
            VectordbImportRequest importRequest = objectMapper.readValue(importJson, VectordbImportRequest.class);
            
            // 3. Import 거래 호출
            VectordbImportResponse response = sktaiVectorDbsService.importVectorDatabase(importRequest);
            
            boolean success = response != null && response.getVectorDbId() != null;
            
            if (response != null && response.getVectorDbId() != null) {
                log.info("Vector DB Import 성공 - vectorDbId: {}, 생성된 UUID: {}", 
                        vectorDbId, response.getVectorDbId());
            } else {
                log.warn("Vector DB Import 응답이 null입니다 - vectorDbId: {}", vectorDbId);
            }
            
            log.info("Vector DB Export → Import 거래 완료 - vectorDbId: {}, success: {}", vectorDbId, success);
            
            return success;
            
        } catch (JsonProcessingException e) {
            log.error("Vector DB JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (FeignException e) {
            log.error("Vector DB Export → Import 거래 실패 - vectorDbId: {}, error: {}", vectorDbId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Vector DB Export → Import 거래 실패 - vectorDbId: {}, error: {}", vectorDbId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행
     * 
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String importJson, Long projectId, Boolean isExist) {
        try {

            log.info("Vector DB JSON 문자열에서 Import 시작");
            VectordbImportRequest importRequest = objectMapper.readValue(importJson, VectordbImportRequest.class);

            if (isExist) {
                VectorDBUpdate vectorDBUpdate = objectMapper.convertValue(importRequest, VectorDBUpdate.class);
                VectorDBUpdateResponse response = sktaiVectorDbsService.updateVectorDb(importRequest.getId(), vectorDBUpdate);

                // projectId가 있을 경우만, 권한 설정
                if (projectId != null) {
                    log.info("Vector DB JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                    adminAuthService.setResourcePolicyByProjectSequence("/api/v1/knowledge/vectordbs/" + response.getVectorDbId(), projectId);
                    log.info("Vector DB JSON 문자열에서 Import - 권한 설정 완료");
                }

                boolean success = response != null && response.getVectorDbId() != null;
                log.info("Vector DB JSON 문자열에서 Import 완료 - success: {}", success);
                return success;
            } else {

                boolean isDefault = (Boolean) importRequest.getIsDefault();

                if (isDefault) {
                    importRequest.setIsDefault(false);
                }

                VectordbImportResponse response = sktaiVectorDbsService.importVectorDatabase(importRequest);

                // projectId가 있을 경우만, 권한 설정
                if (projectId != null) {
                    log.info("Vector DB JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                    adminAuthService.setResourcePolicyByProjectSequence("/api/v1/knowledge/vectordbs/" + response.getVectorDbId(), projectId);
                    log.info("Vector DB JSON 문자열에서 Import - 권한 설정 완료");
                }

                if (isDefault) {
                    importRequest.setIsDefault(true);
                    VectorDBUpdate vectorDBUpdate = objectMapper.convertValue(importRequest, VectorDBUpdate.class);
                    sktaiVectorDbsService.updateVectorDb(importRequest.getId(), vectorDBUpdate);
                }

                boolean success = response != null && response.getVectorDbId() != null;

                log.info("Vector DB JSON 문자열에서 Import 완료 - success: {}", success);

                return success;
            }

        } catch (JsonProcessingException e) {
            log.error("Vector DB JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (FeignException e) {
            log.error("Vector DB API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Vector DB JSON 문자열에서 Import 실패 - error: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param vectorDbId Vector DB ID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String vectorDbId, boolean saveToFile) {
        try {
            log.info("Vector DB Export → JSON 파일 저장 시작 - vectorDbId: {}, saveToFile: {}", vectorDbId, saveToFile);
            
            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(vectorDbId);
            
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
            
            // 파일명 생성: VECTOR_DB_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("VECTOR_DB_%s_%s.json", vectorDbId, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Vector DB Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("Vector DB Export → JSON 파일 저장 실패 - vectorDbId: {}, error: {}", vectorDbId, e.getMessage(), e);
            throw new RuntimeException("Vector DB Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Vector DB Export → JSON 파일 저장 실패 - vectorDbId: {}, error: {}", vectorDbId, e.getMessage(), e);
            throw new RuntimeException("Vector DB Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * VectorDBDetailResponse를 VectordbImportRequest로 변환
     * 
     * <p>GET 응답에서 createdBy, createdAt, updatedBy, updatedAt 필드를 제거하고
     * 나머지 필드만 Import 요청에 포함합니다.</p>
     * 
     * @param vectorDb Vector DB 상세 정보
     * @param vectorDbId Vector DB ID (body에 포함)
     * @return Import 요청 데이터
     */
    @SuppressWarnings("unchecked")
    private VectordbImportRequest convertVectorDbToImportRequest(VectorDBDetailResponse vectorDb, String vectorDbId) {
        try {
            if (vectorDb == null) {
                throw new IllegalArgumentException("Vector DB 데이터가 null입니다.");
            }
            
            // VectorDBDetailResponse를 Map으로 변환 (실제 API 응답에 메타데이터 필드가 포함될 수 있음)
            Map<String, Object> vectorDbMap = objectMapper.convertValue(vectorDb, Map.class);
            
            // 메타데이터 필드 제거 (createdBy, createdAt, updatedBy, updatedAt, project_id)
            List<String> removedFields = new ArrayList<>();
            if (vectorDbMap.containsKey("created_by")) {
                vectorDbMap.remove("created_by");
                removedFields.add("created_by");
            }
            if (vectorDbMap.containsKey("created_at")) {
                vectorDbMap.remove("created_at");
                removedFields.add("created_at");
            }
            if (vectorDbMap.containsKey("updated_by")) {
                vectorDbMap.remove("updated_by");
                removedFields.add("updated_by");
            }
            if (vectorDbMap.containsKey("updated_at")) {
                vectorDbMap.remove("updated_at");
                removedFields.add("updated_at");
            }
            if (vectorDbMap.containsKey("project_id")) {
                vectorDbMap.remove("project_id");
                removedFields.add("project_id");
            }
            
            log.debug("제거된 필드: {}", removedFields);
            
            // Import Request에 필요한 필드만 추출
            VectordbImportRequest.VectordbImportRequestBuilder builder = VectordbImportRequest.builder();
            
            // id 필드 추가 (path variable의 vectorDbId를 body의 id로 설정)
            builder.id(vectorDbId);
            
            // name, type, isDefault 설정
            if (vectorDbMap.containsKey("name")) {
                builder.name((String) vectorDbMap.get("name"));
            }
            if (vectorDbMap.containsKey("type")) {
                builder.type((String) vectorDbMap.get("type"));
            }
            if (vectorDbMap.containsKey("is_default")) {
                builder.isDefault((Boolean) vectorDbMap.get("is_default"));
            } else {
                builder.isDefault(false);
            }
            
            // connectionInfo를 Map으로 변환
            if (vectorDbMap.containsKey("connection_info")) {
                Object connectionInfoObj = vectorDbMap.get("connection_info");
                if (connectionInfoObj instanceof Map) {
                    builder.connectionInfo((Map<String, Object>) connectionInfoObj);
                } else {
                    Map<String, Object> connectionInfoMap = objectMapper.convertValue(connectionInfoObj, Map.class);
                    builder.connectionInfo(connectionInfoMap);
                }
            }
            
            return builder.build();
            
        } catch (RuntimeException e) {
            log.error("Vector DB를 Import 형식으로 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Vector DB를 Import 형식으로 변환 실패: " + e.getMessage(), e);
        } catch (Exception e) { 
            log.error("Vector DB를 Import 형식으로 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Vector DB를 Import 형식으로 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * VECTOR_DB 타입 필드 추출
     * 
     * @param jsonNode JSON 노드
     * @param id 파일 ID
     * @param fields 추출할 필드 목록
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return 추출된 필드 Map (connection_info 포함)
     */
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields, Function<String, String> getValueFromDb) {
        Map<String, Map<String, String>> connectionInfo = new HashMap<>();
        
        JsonNode connInfoNode = null;
        if (jsonNode.has("connection_info") && jsonNode.get("connection_info").isObject()) {
            connInfoNode = jsonNode.get("connection_info");
        }
        
        for (String field : fields) {
            String fileValue = null;
            if (connInfoNode != null && connInfoNode.has(field) && !connInfoNode.get(field).isNull()) {
                fileValue = connInfoNode.get(field).asText();
            }
            
            String dbValue = getValueFromDb.apply(field);
            
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("dev", fileValue != null ? fileValue : "");
            fieldMap.put("prod", dbValue != null ? dbValue : "");
            connectionInfo.put(field, fieldMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("connection_info", connectionInfo);
        return result;
    }
    
    /**
     * VectorDB 존재 여부 확인
     * 
     * @param vectorDbId VectorDB ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String vectorDbId) {
        try {
            sktaiVectorDbsService.getVectorDb(vectorDbId);
            return true;
        } catch (Exception e) {
            log.debug("VectorDB 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", vectorDbId, e.getMessage());
            return false;
        }
    }
}

