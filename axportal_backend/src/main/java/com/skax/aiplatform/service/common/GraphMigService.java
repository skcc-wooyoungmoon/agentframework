package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphSaveRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGraphsService;
import com.skax.aiplatform.converter.common.MigDataConverter;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Graph 마이그레이션 서비스
 * 
 * <p>
 * Graph 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphMigService {

    private final SktaiAgentGraphsService sktaiAgentGraphsService;
    private final MigDataConverter migDataConverter;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;

    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>
     * Graph를 조회하고 Import 형식으로 변환합니다.
     * </p>
     * 
     * @param graphId Graph ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String graphId) {
        try {
            log.info("Graph Export → Import 형식 변환 시작 - graphId: {}", graphId);

            // Graph 조회
            GraphResponse graph = sktaiAgentGraphsService.getGraph(graphId);
            if (graph == null) {
                throw new RuntimeException("Graph를 찾을 수 없습니다: " + graphId);
            }

            // Import 형식으로 변환
            String importJson = migDataConverter.convertGraphToImportFormat(graph);

            log.info("Graph Export → Import 형식 변환 완료 - graphId: {}, jsonLength: {}", graphId, importJson.length());

            return importJson;

        } catch (FeignException e) {
            log.error("Graph API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Graph Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Graph Export → Import 형식 변환 실패 - graphId: {}, error: {}", graphId, e.getMessage(), e);
            throw new RuntimeException("Graph Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Export 형태를 만드는 것 (Graph 데이터 직접 전달)
     * 
     * <p>
     * 이미 조회한 Graph 데이터를 Import 형식으로 변환합니다.
     * </p>
     * 
     * @param graphData Graph 데이터
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(GraphResponse graphData) {
        try {
            log.info("Graph Export → Import 형식 변환 시작 (Graph 데이터 직접 전달)");

            if (graphData == null) {
                throw new IllegalArgumentException("Graph 데이터가 null입니다.");
            }

            // Import 형식으로 변환
            String importJson = migDataConverter.convertGraphToImportFormat(graphData);

            log.info("Graph Export → Import 형식 변환 완료 - jsonLength: {}", importJson.length());

            return importJson;

        } catch (RuntimeException e) {
            log.error("Graph Export → Import 형식 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Graph Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 문자열로부터 Import 수행 (기존 메서드 - 하위 호환성 유지)
     * 
     * @param graphId    Graph ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String graphId, String importJson, Long projectId) {
        return importFromJsonString(graphId, importJson, projectId, false);
    }
    
    /**
     * JSON 문자열로부터 Import 또는 Update 수행
     * 
     * @param graphId    Graph ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return Import/Update 성공 여부
     */
    public boolean importFromJsonString(String graphId, String importJson, Long projectId, boolean isExist) {
        try {
            log.info("Graph JSON 문자열에서 {} 시작 - graphId: {}, jsonLength: {}, isExist: {}", 
                    isExist ? "Update" : "Import", graphId, importJson != null ? importJson.length() : 0, isExist);

            if (isExist) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(graphId, importJson, projectId);
            }

            // 기존 데이터가 없으면 Import 수행
            // GraphTestController와 동일하게 JSON 문자열을 그대로 전달
            // (convertGraphToImportFormat에서 이미 id 필드가 제거되고 올바른 형식으로 변환됨)
            GraphCreateResponse response = sktaiAgentGraphsService.importGraph(graphId, importJson);

            if (response == null) {
                log.error("Graph Import 응답이 null입니다 - graphId: {}", graphId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Graph JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/graphs/" + graphId, projectId);
                log.info("Graph JSON 문자열에서 Import - 권한 설정 완료");
            }

            Integer responseCode = response.getCode();
            String responseDetail = response.getDetail();
            
            log.info("Graph Import 응답 - graphId: {}, code: {}, detail: {}", 
                    graphId, responseCode, responseDetail);
            
            // 성공 코드: 1 (Validated)
            boolean success = responseCode != null && responseCode.equals(1);
            
            if (!success) {
                log.warn(" Graph Import 실패 - graphId: {}, code: {}, detail: {}", 
                        graphId, responseCode, responseDetail);
            }
            
            log.info("Graph JSON 문자열에서 Import 완료 - graphId: {}, success: {}", graphId, success);
            
            return success;
            
        } catch (FeignException e) {
            log.error("Graph API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Graph JSON 문자열에서 Import 실패 - graphId: {}, error: {}", 
                    graphId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * JSON 문자열로부터 Update 수행 (기존 Graph가 존재할 때 사용)
     * 
     * @param graphId    Graph ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    public boolean updateFromJsonString(String graphId, String importJson, Long projectId) {
        try {
            log.info("Graph JSON 문자열에서 Update 시작 - graphId: {}, jsonLength: {}", graphId,
                    importJson != null ? importJson.length() : 0);

            // JSON 문자열을 GraphSaveRequest로 변환
            GraphSaveRequest saveRequest = objectMapper.readValue(importJson, GraphSaveRequest.class);
            
            // saveGraph 호출 (PUT)
            GraphUpdateOrDeleteResponse response = sktaiAgentGraphsService.saveGraph(graphId, saveRequest);

            if (response == null) {
                log.error("Graph Update 응답이 null입니다 - graphId: {}", graphId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("Graph JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/graphs/" + graphId, projectId);
                log.info("Graph JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("Graph Update 성공 - graphId: {}", graphId);
            return true;

        } catch (FeignException e) {
            log.error("Graph API 호출 실패 (Update) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Graph JSON 문자열에서 Update 실패 - graphId: {}, error: {}", 
                    graphId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>
     * Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.
     * </p>
     * 
     * @param graphId    Graph ID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String graphId, boolean saveToFile) {
        try {
            log.info("Graph Export → JSON 파일 저장 시작 - graphId: {}, saveToFile: {}", graphId, saveToFile);

            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(graphId);

            // 2. JSON 파일로 저장 (조건 처리)
            if (!saveToFile) {
                log.info("파일 저장 옵션이 false이므로 파일 저장을 건너뜁니다.");
                return null;
            }

            // 저장 디렉토리 생성 (Windows: C:\gapdat\migration, Linux: /gapdat/migration)
            String baseDir = System.getProperty("os.name").toLowerCase().contains("win")
                    ? "C:\\gapdat\\migration"
                    : "/gapdat/migration";
            Path exportDir = Paths.get(baseDir);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
                log.info("디렉토리 생성 완료 - 경로: {}", exportDir.toAbsolutePath());
            }

            // 파일명 생성: GRAPH_{graphId}.json
            String fileName = String.format("GRAPH_%s.json", graphId);
            Path filePath = exportDir.resolve(fileName);

            // JSON 파일 저장 (UTF-8 인코딩)
            try (FileWriter writer = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
                writer.write(importJson);
                writer.flush();
            }

            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Graph Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);

            return absolutePath;

        } catch (IOException e) {
            log.error("Graph Export → JSON 파일 저장 실패 (IOException) - graphId: {}, error: {}", graphId, e.getMessage(),
                    e);
            throw new RuntimeException("Graph Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Graph Export → JSON 파일 저장 실패 - graphId: {}, error: {}", graphId, e.getMessage(), e);
            throw new RuntimeException("Graph Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * GRAPH 타입 필드 추출
     * 
     * <p>nodes 내부의 type: "agent__app" 노드에서 agent_app_id를 추출합니다.</p>
     * 
     * @param jsonNode       JSON 노드
     * @param id             파일 ID
     * @param fields         추출할 필드 목록 (사용하지 않음)
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return 추출된 필드 Map
     */
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields,
            Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // nodes 내부에서 type: "agent__app"인 노드들의 agent_app_id 추출
            Map<String, Object> agentAppNodes = extractAgentAppNodes(jsonNode, id, getValueFromDb);
            if (agentAppNodes != null && !agentAppNodes.isEmpty()) {
                result.put("agent_app_nodes", agentAppNodes);
            }
            
        } catch (RuntimeException e) {
            log.error("Graph 필드 추출 실패 (RuntimeException) - id: {}, error: {}", id, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Graph 필드 추출 실패 - id: {}, error: {}", id, e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * nodes 내부에서 type: "agent__app"인 노드들의 agent_app_id 추출
     * 
     * @param jsonNode JSON 노드
     * @param id 파일 ID
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return agent_app_nodes Map (dev/prod 구조)
     */
    private Map<String, Object> extractAgentAppNodes(JsonNode jsonNode, String id, Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 파일에서 agent_app_id 추출
            List<Map<String, String>> devAgentAppNodes = new ArrayList<>();
            // 중복 체크를 위한 Set (agent_app_id 기준)
            java.util.Set<String> addedAgentAppIds = new java.util.HashSet<>();
            
            // graph.nodes 구조 확인 (graph 객체 내부의 nodes 배열)
            JsonNode nodesArray = null;
            if (jsonNode.has("graph") && jsonNode.get("graph").isObject()) {
                JsonNode graphNode = jsonNode.get("graph");
                if (graphNode.has("nodes") && graphNode.get("nodes").isArray()) {
                    nodesArray = graphNode.get("nodes");
                    log.debug("graph.nodes에서 노드 배열 찾음 - id: {}, 노드 개수: {}", id, nodesArray.size());
                }
            }
            
            // graph.nodes가 없으면 최상위 nodes 확인 (하위 호환성)
            if (nodesArray == null && jsonNode.has("nodes") && jsonNode.get("nodes").isArray()) {
                nodesArray = jsonNode.get("nodes");
                log.debug("최상위 nodes에서 노드 배열 찾음 - id: {}, 노드 개수: {}", id, nodesArray.size());
            }
            
            if (nodesArray != null) {
                for (JsonNode node : nodesArray) {
                    if (node.has("type") && "agent__app".equals(node.get("type").asText())) {
                        Map<String, String> agentAppNode = new HashMap<>();
                        String agentAppId = null;
                        
                        // node.data.agent_app_id 추출
                        if (node.has("data") && node.get("data").isObject()) {
                            JsonNode dataNode = node.get("data");
                            if (dataNode.has("agent_app_id") && !dataNode.get("agent_app_id").isNull()) {
                                agentAppId = dataNode.get("agent_app_id").asText();
                                agentAppNode.put("agent_app_id", agentAppId);
                            }
                        }
                        
                        // data가 없으면 직접 노드에서 찾기 (하위 호환성)
                        if (agentAppId == null) {
                            if (node.has("agent_app_id") && !node.get("agent_app_id").isNull()) {
                                agentAppId = node.get("agent_app_id").asText();
                                agentAppNode.put("agent_app_id", agentAppId);
                            }
                        }
                        
                        // agent_app_id가 있고, 중복이 아닐 때만 추가
                        if (agentAppId != null && !agentAppId.isEmpty() && !addedAgentAppIds.contains(agentAppId)) {
                            devAgentAppNodes.add(agentAppNode);
                            addedAgentAppIds.add(agentAppId);
                            log.debug("agent__app 노드 추출 완료 - id: {}, agent_app_id: {}", 
                                    id, agentAppId);
                        } else if (agentAppId != null && addedAgentAppIds.contains(agentAppId)) {
                            log.debug("agent__app 노드 중복 제외 - id: {}, agent_app_id: {} (이미 추가됨)", 
                                    id, agentAppId);
                        }
                    }
                }
            } else {
                log.warn("nodes 배열을 찾을 수 없음 - id: {}, jsonNode keys: {}", id, jsonNode.fieldNames());
            }
            
            // DB에서 agent_app_nodes 조회
            String dbValueStr = getValueFromDb.apply("agent_app_nodes");
            List<Map<String, String>> prodAgentAppNodes = new ArrayList<>();
            if (dbValueStr != null && !dbValueStr.isEmpty()) {
                try {
                    JsonNode dbJsonNode = objectMapper.readTree(dbValueStr);
                    if (dbJsonNode.isArray()) {
                        for (JsonNode item : dbJsonNode) {
                            Map<String, String> agentAppNode = new HashMap<>();
                            if (item.has("agent_app_id")) {
                                agentAppNode.put("agent_app_id", item.get("agent_app_id").asText());
                            }
                            if (!agentAppNode.isEmpty()) {
                                prodAgentAppNodes.add(agentAppNode);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("DB agent_app_nodes JSON 파싱 실패 - id: {}, error: {}", id, e.getMessage());
                }
            }
            
            // dev/prod 구조로 저장
            result.put("dev", devAgentAppNodes);
            result.put("prod", prodAgentAppNodes);
            
        } catch (Exception e) {
            log.warn("agent_app_nodes 추출 실패 - id: {}, error: {}", id, e.getMessage());
        }
        
        return result;
    }

    public static JsonNode search(JsonNode node, String targetKey) {

        if (node.isObject()) {

            for (var graphNode : node.properties()) {

                if (graphNode.getKey().equals(targetKey)) {
                    return graphNode.getValue();
                }

                // 재귀 탐색
                JsonNode found = search(graphNode.getValue(), targetKey);
                if (found != null)
                    return found;
            }

        }

        if (node.isArray()) {
            for (JsonNode item : node) {
                JsonNode found = search(item, targetKey);
                if (found != null)
                    return found;
            }

        }

        return null; // 못 찾으면 null
    }
    
    /**
     * Graph 존재 여부 확인
     * 
     * @param graphId Graph ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String graphId) {
        try {
            sktaiAgentGraphsService.getGraph(graphId);
            return true;
        } catch (Exception e) {
            // 403 권한 오류나 404 Not Found 등은 리소스가 존재하지 않는 것으로 간주
            log.debug("Graph 존재 확인 실패 (리소스 없음 또는 권한 없음) - id: {}, error: {}", graphId, e.getMessage());
            return false;
        }
    }

}