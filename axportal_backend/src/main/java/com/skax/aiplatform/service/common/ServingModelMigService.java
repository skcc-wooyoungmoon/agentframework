package com.skax.aiplatform.service.common;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

public interface ServingModelMigService {


    /**
     * Export to Import Format
     * @param modelId
     * @return
     */
    String exportToImportFormat(String modelId);

    /**
     * Import from JSON String
     * @param jsonString
     * @param projectId
     * @return 성공시 true, 실패시 false
     */
    boolean importFromJsonString(String jsonString, Long projectId);
    
    /**
     * Import or Update from JSON String
     * @param jsonString
     * @param projectId
     * @param isExist 기존 데이터 존재 여부 (true: update, false: import)
     * @return 성공시 true, 실패시 false
     */
    boolean importFromJsonString(String jsonString, Long projectId, boolean isExist);

    /**
     * Extract fields
     * @param jsonNode
     * @param id
     * @param fields
     * @param getValueFromDb
     * @return
     */
    Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields, Function<String, String> getValueFromDb);
    
    /**
     * ServingModel 존재 여부 확인
     * 
     * @param servingId Serving ID
     * @return 존재하면 true, 없으면 false
     */
    boolean checkIfExists(String servingId);
}
