package com.skax.aiplatform.converter.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 마이그레이션 데이터 변환기
 * 
 * <p>
 * 마이그레이션 관련 데이터 변환 로직을 담당하는 컴포넌트입니다.
 * JSON 변환, 메타데이터 필드 제거, Graph Import 형식 변환 등을 처리합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MigDataConverter {

    private final ObjectMapper objectMapper;

    /**
     * Graph 데이터를 Import 형식으로 변환
     * 
     * <p>
     * 변환 규칙:
     * 1. id, project_id, created_at, updated_at, created_by, updated_by 제거
     * 2. name, description은 최상위에 유지
     * 3. edges, nodes를 graph 객체로 묶기
     * 
     * @param data Graph 데이터 (Map 또는 GraphResponse)
     * @return Import 형식의 JSON 문자열
     */
    @SuppressWarnings("unchecked")
    public String convertGraphToImportFormat(Object data) {
        try {
            // 파라미터 검증
            if (data == null) {
                log.warn("convertGraphToImportFormat: data is null");
                throw new IllegalArgumentException("변환할 데이터가 없습니다");
            }

            log.info("Graph Import 형식으로 변환 시작");

            // Object를 Map으로 변환
            Map<String, Object> map = objectMapper.convertValue(data, Map.class);

            if (map == null || map.isEmpty()) {
                log.warn("convertGraphToImportFormat: converted map is null or empty");
                throw new IllegalArgumentException("변환된 데이터가 비어있습니다");
            }

            log.debug("변환 전 Graph 데이터 필드: {}", map.keySet());

            // 0. id 필드 제거 (query parameter의 agent_id와 충돌 방지)
            if (map.containsKey("id")) {
                map.remove("id");
                log.debug("id 필드 제거 (query parameter의 agent_id 사용)");
            }

            // 1. name, description, project_id 추출
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            Object projectId = map.get("project_id");

            // 2. edges, nodes 추출
            Object edges = map.get("edges");
            Object nodes = map.get("nodes");

            // 3. tags 추출 (없으면 빈 리스트)
            Object tags = map.get("tags");
            if (tags == null) {
                tags = new ArrayList<>();
            }

            // 4. 새로운 구조 생성
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("name", name);
            result.put("description", description);
            if (projectId != null) {
                result.put("project_id", projectId);
            }
            result.put("tags", tags);

            // 5. graph 객체 생성
            Map<String, Object> graph = new LinkedHashMap<>();
            if (edges != null) {
                graph.put("edges", edges);
            }
            if (nodes != null) {
                graph.put("nodes", nodes);
            }
            result.put("graph", graph);

            // 5. JSON으로 변환
            String json = objectMapper.writeValueAsString(result);

            log.info("Graph Import 형식으로 변환 완료 - jsonLength: {}", json.length());
            log.debug("변환된 JSON: {}", json);

            return json;

        } catch (IllegalArgumentException e) {
            log.error("convertGraphToImportFormat: Invalid argument - error: {}", e.getMessage());
            throw e;
        } catch (ClassCastException e) {
            log.error("convertGraphToImportFormat: Type casting failed - error: {}", e.getMessage(), e);
            throw new RuntimeException("데이터 타입 변환에 실패했습니다", e);
        } catch (NullPointerException e) {
            log.error("convertGraphToImportFormat: Null pointer encountered - error: {}", e.getMessage(), e);
            throw new RuntimeException("필수 데이터가 누락되었습니다", e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("convertGraphToImportFormat: JSON processing failed - error: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 변환에 실패했습니다", e);
        } catch (Exception e) {
            log.error("convertGraphToImportFormat: Unexpected error - error: {}", e.getMessage(), e);
            throw new RuntimeException("Graph Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Tool 데이터를 Import 형식으로 변환
     * 
     * <p>
     * 변환 규칙:
     * 1. id, project_id, created_at, updated_at, created_by, updated_by 제거
     * 2. name, description, tool_type 등 필수 필드 유지
     * 3. ToolResponse의 data 필드 내용을 최상위로 이동
     * 
     * @param data Tool 데이터 (ToolResponse)
     * @return Import 형식의 JSON 문자열
     */
    @SuppressWarnings("unchecked")
    public String convertToolToImportFormat(Object data) {
        try {
            log.info("Tool Import 형식으로 변환 시작");

            // 파라미터 검증
            if (data == null) {
                log.warn("convertToolToImportFormat: data is null");
                throw new IllegalArgumentException("변환할 데이터가 없습니다");
            }

            // Object를 Map으로 변환
            Map<String, Object> map = objectMapper.convertValue(data, Map.class);

            if (map == null) {
                log.warn("convertToolToImportFormat: converted map is null");
                throw new IllegalArgumentException("변환된 데이터가 없습니다");
            }

            log.debug("변환 전 Tool 데이터 필드: {}", map.keySet());

            // ToolResponse의 경우 data 필드에서 실제 Tool 정보 추출
            Object dataField = map.get("data");
            if (dataField instanceof Map) {
                Map<String, Object> toolData = (Map<String, Object>) dataField;

                // 메타데이터 필드 제거
                List<String> removedFields = new ArrayList<>();
                removeMetadataFields(toolData, removedFields);

                // project_id 제거
                if (toolData.containsKey("project_id")) {
                    toolData.remove("project_id");
                    if (removedFields != null) {
                        removedFields.add("project_id");
                    }
                }

                log.debug("제거된 필드: {}", removedFields);
                log.debug("변환 후 Tool 데이터 필드: {}", toolData.keySet());

                // JSON으로 변환
                String json = objectMapper.writeValueAsString(toolData);

                log.info("Tool Import 형식으로 변환 완료 - jsonLength: {}", json.length());
                log.debug("변환된 JSON: {}", json);

                return json;
            } else {
                // data 필드가 없는 경우 전체 Map에서 메타데이터 제거
                List<String> removedFields = new ArrayList<>();
                removeMetadataFields(map, removedFields);

                if (map.containsKey("project_id")) {
                    map.remove("project_id");
                    if (removedFields != null) {
                        removedFields.add("project_id");
                    }
                }

                String json = objectMapper.writeValueAsString(map);

                if (json == null || json.isEmpty()) {
                    log.error("Tool Import 형식 변환 실패 - 변환된 JSON이 비어있습니다");
                    throw new IllegalStateException("변환된 JSON이 비어있습니다");
                }

                log.info("Tool Import 형식으로 변환 완료 - jsonLength: {}", json.length());
                log.debug("변환된 JSON: {}", json);

                return json;
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Tool Import 형식 변환 실패 (JSON 처리 오류)");
            throw new RuntimeException("Tool Import 형식 변환 실패: JSON 처리 오류", e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Tool Import 형식 변환 실패 (잘못된 입력값)");
            throw e;
        } catch (Exception e) {
            log.error("Tool Import 형식 변환 실패 (예상치 못한 오류)");
            throw new RuntimeException("Tool Import 형식 변환 실패", e);
        }
    }

    /**
     * 객체를 JSON으로 변환하고 메타데이터 필드 제거
     * MODEL 타입의 경우 메타데이터 필드 제거를 하지 않습니다.
     * 
     * @param data       변환할 객체
     * @param targetType 객체 타입
     * @return JSON 문자열
     */
    @SuppressWarnings("unchecked")
    public String convertToJsonAndRemoveMetadata(Object data, ObjectType targetType) {
        try {
            // 파라미터 검증
            if (data == null) {
                log.warn("convertToJsonAndRemoveMetadata: data is null");
                throw new IllegalArgumentException("변환할 데이터가 없습니다");
            }
            if (targetType == null) {
                log.warn("convertToJsonAndRemoveMetadata: targetType is null");
                throw new IllegalArgumentException("객체 타입이 지정되지 않았습니다");
            }

            // Object를 Map으로 변환
            Map<String, Object> map = objectMapper.convertValue(data, Map.class);

            if (map == null) {
                log.warn("convertToJsonAndRemoveMetadata: converted map is null");
                throw new IllegalArgumentException("변환된 데이터가 없습니다");
            }

            // MODEL 타입이 아닌 경우에만 메타데이터 필드 제거
            if (targetType == ObjectType.MODEL) {
                log.info("MODEL 타입이므로 메타데이터 필드 제거를 건너뜁니다.");
            } else {
                // 메타데이터 필드 제거 전 필드 개수 기록
                int beforeCount = map.size();
                log.debug("제거 전 최상위 필드 개수: {}", beforeCount);

                // 메타데이터 필드 제거
                List<String> removedFields = new ArrayList<>();
                removeMetadataFields(map, removedFields);

                // 제거 후 필드 목록 로깅
                int afterCount = map.size();
                log.info("제거 후 최상위 필드 개수: {} (제거됨: {})", afterCount, beforeCount - afterCount);
                log.debug("제거 후 최상위 필드 목록: {}", map.keySet());

                // 제거된 필드 로깅
                if (!removedFields.isEmpty()) {
                    log.info("제거된 필드 목록: {}", removedFields);
                } else {
                    log.debug("제거된 필드 없음");
                }
            }

            // JSON 변환은 한 번만 수행 (메타데이터 제거 후)
            String resultJson = objectMapper.writeValueAsString(map);
            log.debug("변환된 JSON 길이: {}", resultJson.length());

            return resultJson;

        } catch (IllegalArgumentException e) {
            log.error("convertToJsonAndRemoveMetadata: Invalid argument - error: {}", e.getMessage());
            throw e;
        } catch (ClassCastException e) {
            log.error("convertToJsonAndRemoveMetadata: Type casting failed - error: {}", e.getMessage(), e);
            throw new RuntimeException("데이터 타입 변환에 실패했습니다", e);
        } catch (NullPointerException e) {
            log.error("convertToJsonAndRemoveMetadata: Null pointer encountered - error: {}", e.getMessage(), e);
            throw new RuntimeException("필수 데이터가 누락되었습니다", e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("convertToJsonAndRemoveMetadata: JSON processing failed - error: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 변환에 실패했습니다", e);
        } catch (Exception e) {
            log.error("convertToJsonAndRemoveMetadata: Unexpected error - error: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Map에서 메타데이터 필드 제거 (재귀적으로 처리)
     * 
     * @param map           제거할 Map
     * @param removedFields 제거된 필드 목록을 저장할 리스트 (최상위 레벨에서만 사용)
     */
    @SuppressWarnings("unchecked")
    public void removeMetadataFields(Map<String, Object> map, List<String> removedFields) {
        try {
            if (map == null) {
                log.debug("removeMetadataFields: map is null, returning");
                return;
            }

            // 제거할 필드 목록
            Set<String> metadataFields = Set.of(
                    "id", "uuid",
                    "createdBy", "created_by",
                    "updatedBy", "updated_by",
                    "createdAt", "created_at",
                    "updatedAt", "updated_at");

            // 메타데이터 필드 제거 및 제거된 필드 기록
            for (String field : metadataFields) {
                try {
                    if (map.containsKey(field)) {
                        map.remove(field);
                        // 최상위 레벨에서만 제거된 필드 기록
                        if (removedFields != null && !removedFields.contains(field)) {
                            removedFields.add(field);
                        }
                        log.debug("메타데이터 필드 제거됨: {}", field);
                    }
                } catch (UnsupportedOperationException e) {
                    log.warn("removeMetadataFields: Cannot remove field '{}' from immutable map - error: {}", field,
                            e.getMessage());
                    // 불변 맵인 경우 계속 진행
                } catch (Exception e) {
                    log.warn("removeMetadataFields: Failed to remove field '{}' - error: {}", field, e.getMessage());
                    // 필드 제거 실패 시 계속 진행
                }
            }

            // 중첩된 Map이나 List도 재귀적으로 처리
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                try {
                    Object value = entry.getValue();

                    if (value instanceof Map) {
                        removeMetadataFields((Map<String, Object>) value, null); // 중첩 레벨에서는 removedFields 전달 안함
                    } else if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        for (Object item : list) {
                            try {
                                if (item instanceof Map) {
                                    removeMetadataFields((Map<String, Object>) item, null); // 중첩 레벨에서는 removedFields 전달
                                                                                            // 안함
                                }
                            } catch (ClassCastException e) {
                                log.warn("removeMetadataFields: Type casting failed for list item - error: {}",
                                        e.getMessage());
                                // 타입 변환 실패 시 해당 항목은 건너뜀
                            } catch (Exception e) {
                                log.warn("removeMetadataFields: Failed to process list item - error: {}",
                                        e.getMessage());
                                // 항목 처리 실패 시 계속 진행
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    log.warn("removeMetadataFields: Type casting failed for key '{}' - error: {}", entry.getKey(),
                            e.getMessage());
                    // 타입 변환 실패 시 해당 엔트리는 건너뜀
                } catch (NullPointerException e) {
                    log.warn("removeMetadataFields: Null value encountered for key '{}'", entry.getKey());
                    // null 값인 경우 건너뜀
                } catch (Exception e) {
                    log.warn("removeMetadataFields: Failed to process entry '{}' - error: {}", entry.getKey(),
                            e.getMessage());
                    // 엔트리 처리 실패 시 계속 진행
                }
            }

        } catch (UnsupportedOperationException e) {
            log.warn("removeMetadataFields: Cannot iterate over immutable map - error: {}", e.getMessage());
            // 불변 맵인 경우 graceful하게 처리
        } catch (NullPointerException e) {
            log.warn("removeMetadataFields: Null pointer encountered - error: {}", e.getMessage());
            // null 발생 시 graceful하게 처리
        } catch (Exception e) {
            log.warn("removeMetadataFields: Unexpected error - error: {}", e.getMessage());
            // 예외 발생 시에도 graceful하게 처리 (호출자에게 영향 없음)
        }
    }
}
