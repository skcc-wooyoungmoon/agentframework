package com.skax.aiplatform.client.sktai.agent.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.SktaiAgentGraphsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphCopyRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphExecuteRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphInfoUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphSaveRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphAppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphDetailResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphExecuteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphNodeInfoResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplateApiResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplatesApiResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplatesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphsResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Agent Graphs API 서비스
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentGraphsService {

    private final SktaiAgentGraphsClient sktaiAgentGraphsClient;

    public GraphsResponse getGraphs(String projectId, Integer page, Integer size, String sort, String filter,
            String search) {
        try {
            log.debug("Graphs 목록 조회 요청 - projectId: {}, page: {}, size: {}", projectId, page, size);

            GraphsResponse response = sktaiAgentGraphsClient.getGraphs(projectId, page, size, sort, filter, search);

            return response;

        } catch (BusinessException e) {
            log.error("Graphs 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graphs 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                log.error("원인 예외: {}", e.getCause().getMessage());
            }
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graphs 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphResponse getGraph(String graphUuid) {
        try {
            log.info("Graph 상세 조회 요청 - graphUuid: {}", graphUuid);

            GraphDetailResponse response = sktaiAgentGraphsClient.getGraph(graphUuid);

            log.info("Graph 상세 조회 성공 - graphUuid: {}", graphUuid);

            // data 래퍼에서 실제 GraphResponse 추출
            if (response != null && response.getData() != null) {
                GraphResponse graphResponse = response.getData();

                // // 🔥 기존 엣지 가공 로직 주석처리 - 원본 데이터 그대로 전달
                /*
                 * if (graphResponse.getEdges() != null) {
                 * ObjectMapper objectMapper = new ObjectMapper();
                 * List<Object> processedEdges = new ArrayList<>();
                 * 
                 * for (Object edgeObj : graphResponse.getEdges()) {
                 * try {
                 * // Object를 Map으로 변환
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> edgeMap = objectMapper.convertValue(edgeObj, Map.class);
                 * 
                 * // sourceHandle 필드가 없고 source_handle이 있으면 sourceHandle 추가
                 * if (!edgeMap.containsKey("sourceHandle") &&
                 * edgeMap.containsKey("source_handle")) {
                 * Object sourceHandleValue = edgeMap.get("source_handle");
                 * if (sourceHandleValue != null) {
                 * edgeMap.put("sourceHandle", sourceHandleValue);
                 * }
                 * }
                 * // sourceHandle 필드가 없으면 추론하여 추가
                 * if (!edgeMap.containsKey("sourceHandle")) {
                 * Object conditionLabel = edgeMap.get("condition_label");
                 * String sourceId = (String) edgeMap.get("source");
                 * 
                 * // 🔥 data.category.id에서도 condition_label 추론 시도
                 * if (conditionLabel == null && edgeMap.containsKey("data")
                 * && edgeMap.get("data") != null) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> dataMap = objectMapper.convertValue(edgeMap.get("data"),
                 * Map.class);
                 * if (dataMap.containsKey("category") && dataMap.get("category") != null) {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> categoryMap = objectMapper
                 * .convertValue(dataMap.get("category"), Map.class);
                 * Object categoryId = categoryMap.get("id");
                 * if (categoryId != null && !categoryId.toString().isEmpty()) {
                 * conditionLabel = categoryId;
                 * }
                 * }
                 * // data.condition_label도 확인
                 * if (conditionLabel == null && dataMap.containsKey("condition_label")) {
                 * conditionLabel = dataMap.get("condition_label");
                 * }
                 * } catch (Exception e) {
                 * // data 변환 실패 시 무시
                 * }
                 * }
                 * 
                 * // 🔥 Condition 노드 엣지: condition_label에서 구체적인 handle ID 생성
                 * if (sourceId != null && graphResponse.getNodes() != null) {
                 * for (Object nodeObj : graphResponse.getNodes()) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> nodeMap = objectMapper.convertValue(nodeObj, Map.class);
                 * if (sourceId.equals(String.valueOf(nodeMap.get("id")))) {
                 * String nodeType = (String) nodeMap.get("type");
                 * 
                 * // Condition 노드: condition_label 또는 노드의 conditions 배열에서 handle ID 생성
                 * if ("condition".equals(nodeType)) {
                 * if (conditionLabel != null
                 * && !conditionLabel.toString().isEmpty()) {
                 * String conditionLabelStr = conditionLabel.toString();
                 * // condition_label이 구체적인 condition ID인 경우 (예:
                 * // "5df44f96-condition-1")
                 * if (conditionLabelStr.contains("-condition-")
                 * && !conditionLabelStr.equals("condition-else")) {
                 * edgeMap.put("sourceHandle", "handle-" + conditionLabelStr);
                 * } else if (conditionLabelStr.contains("else")
                 * || conditionLabelStr.contains("ELSE")) {
                 * edgeMap.put("sourceHandle", "handle-condition-else");
                 * } else if (conditionLabelStr.contains("-condition-")) {
                 * edgeMap.put("sourceHandle", "handle-" + conditionLabelStr);
                 * }
                 * } else {
                 * // condition_label이 없으면 노드의 conditions 배열에서 추론
                 * try {
                 * Map<String, Object> nodeData = (Map<String, Object>) nodeMap
                 * .get("data");
                 * if (nodeData != null) {
                 * Object conditionsObj = nodeData.get("conditions");
                 * if (conditionsObj instanceof List) {
                 * 
                 * @SuppressWarnings("unchecked")
                 * List<Map<String, Object>> conditions = (List<Map<String, Object>>)
                 * conditionsObj;
                 * // 첫 번째 condition 사용
                 * if (!conditions.isEmpty()) {
                 * Map<String, Object> firstCondition = conditions
                 * .get(0);
                 * Object conditionId = firstCondition.get("id");
                 * if (conditionId != null) {
                 * edgeMap.put("sourceHandle",
                 * "handle-" + conditionId.toString());
                 * }
                 * }
                 * }
                 * // default_condition 확인
                 * Object defaultCondition = nodeData
                 * .get("default_condition");
                 * if (defaultCondition != null
                 * && !edgeMap.containsKey("sourceHandle")) {
                 * String defaultConditionStr = defaultCondition
                 * .toString();
                 * if (defaultConditionStr.contains("else")) {
                 * edgeMap.put("sourceHandle",
                 * "handle-condition-else");
                 * } else {
                 * edgeMap.put("sourceHandle",
                 * "handle-" + defaultConditionStr);
                 * }
                 * }
                 * }
                 * } catch (Exception e) {
                 * // 조건 추론 실패 시 기본값
                 * edgeMap.put("sourceHandle", "handle-condition-else");
                 * }
                 * }
                 * break;
                 * }
                 * 
                 * // Input 노드: sourceHandle을 "input_right"로 설정
                 * if ("input__basic".equals(nodeType)) {
                 * edgeMap.put("sourceHandle", "input_right");
                 * break;
                 * }
                 * 
                 * // Generator 노드: sourceHandle을 "gen_right"로 설정
                 * if ("agent__generator".equals(nodeType)) {
                 * edgeMap.put("sourceHandle", "gen_right");
                 * break;
                 * }
                 * 
                 * // 🔥 Reviewer 노드: condition_label 또는 data.category.id에서 추론 (React Flow
                 * // 형식으로 직접 설정)
                 * if ("agent__reviewer".equals(nodeType)) {
                 * if (conditionLabel != null) {
                 * String conditionLabelStr = conditionLabel.toString();
                 * if ("pass".equals(conditionLabelStr)
                 * || conditionLabelStr.contains("pass")) {
                 * edgeMap.put("sourceHandle", "reviewer_pass");
                 * } else if ("fail".equals(conditionLabelStr)
                 * || conditionLabelStr.contains("fail")) {
                 * edgeMap.put("sourceHandle", "reviewer_fail");
                 * }
                 * } else {
                 * // condition_label이 없으면 data.category.id 확인
                 * try {
                 * Map<String, Object> nodeData = (Map<String, Object>) nodeMap
                 * .get("data");
                 * if (nodeData != null && edgeMap.containsKey("data")
                 * && edgeMap.get("data") != null) {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> edgeDataMap = objectMapper
                 * .convertValue(edgeMap.get("data"), Map.class);
                 * if (edgeDataMap.containsKey("category")
                 * && edgeDataMap.get("category") != null) {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> categoryMap = objectMapper
                 * .convertValue(edgeDataMap.get("category"),
                 * Map.class);
                 * Object categoryId = categoryMap.get("id");
                 * if (categoryId != null) {
                 * String categoryIdStr = categoryId.toString();
                 * if ("pass".equals(categoryIdStr)
                 * || categoryIdStr.contains("pass")
                 * || categoryIdStr
                 * .equals("condition-pass")) {
                 * edgeMap.put("sourceHandle",
                 * "reviewer_pass");
                 * } else if ("fail".equals(categoryIdStr)
                 * || categoryIdStr.contains("fail")
                 * || categoryIdStr
                 * .equals("condition-fail")) {
                 * edgeMap.put("sourceHandle",
                 * "reviewer_fail");
                 * }
                 * }
                 * }
                 * }
                 * } catch (Exception e) {
                 * // category 추론 실패 시 target 노드로 추론
                 * }
                 * }
                 * // target 노드로 추론 (위에서 설정되지 않은 경우)
                 * if (!edgeMap.containsKey("sourceHandle")) {
                 * String targetId = (String) edgeMap.get("target");
                 * if (targetId != null && graphResponse.getNodes() != null) {
                 * for (Object targetNodeObj : graphResponse.getNodes()) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> targetNodeMap = objectMapper
                 * .convertValue(targetNodeObj, Map.class);
                 * if (targetId.equals(
                 * String.valueOf(targetNodeMap.get("id")))) {
                 * String targetNodeType = (String) targetNodeMap
                 * .get("type");
                 * // output__chat이면 pass, condition이면 fail
                 * if ("output__chat".equals(targetNodeType)) {
                 * edgeMap.put("sourceHandle",
                 * "reviewer_pass");
                 * } else if ("condition".equals(targetNodeType)) {
                 * edgeMap.put("sourceHandle",
                 * "reviewer_fail");
                 * }
                 * break;
                 * }
                 * } catch (Exception e) {
                 * // 노드 변환 실패 시 무시
                 * }
                 * }
                 * }
                 * }
                 * break;
                 * }
                 * }
                 * } catch (Exception e) {
                 * // 노드 변환 실패 시 무시
                 * }
                 * }
                 * }
                 * 
                 * // 🔥 Reviewer 엣지: condition_label 또는 target 노드 타입에서 추론 (위에서 처리되지 않은 경우,
                 * React
                 * // Flow 형식으로 직접 설정)
                 * if (!edgeMap.containsKey("sourceHandle")) {
                 * String targetId = (String) edgeMap.get("target");
                 * 
                 * if (conditionLabel != null) {
                 * String conditionLabelStr = conditionLabel.toString();
                 * if ("pass".equals(conditionLabelStr) || conditionLabelStr.contains("pass")) {
                 * edgeMap.put("sourceHandle", "reviewer_pass");
                 * } else if ("fail".equals(conditionLabelStr)
                 * || conditionLabelStr.contains("fail")) {
                 * edgeMap.put("sourceHandle", "reviewer_fail");
                 * }
                 * } else if (targetId != null && graphResponse.getNodes() != null) {
                 * // condition_label이 없으면 target 노드 타입으로 추론
                 * for (Object nodeObj : graphResponse.getNodes()) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> nodeMap = objectMapper.convertValue(nodeObj,
                 * Map.class);
                 * if (targetId.equals(String.valueOf(nodeMap.get("id")))) {
                 * String targetNodeType = (String) nodeMap.get("type");
                 * // output__chat이면 pass, condition이면 fail
                 * if ("output__chat".equals(targetNodeType)) {
                 * edgeMap.put("sourceHandle", "reviewer_pass");
                 * } else if ("condition".equals(targetNodeType)) {
                 * edgeMap.put("sourceHandle", "reviewer_fail");
                 * }
                 * break;
                 * }
                 * } catch (Exception e) {
                 * // 노드 변환 실패 시 무시
                 * }
                 * }
                 * }
                 * }
                 * 
                 * // 🔥 Condition 노드의 여러 엣지 처리: target이 다른 generator면 첫 번째 condition, 같은
                 * // generator면 else
                 * if (!edgeMap.containsKey("sourceHandle") && sourceId != null
                 * && graphResponse.getNodes() != null) {
                 * for (Object nodeObj : graphResponse.getNodes()) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> nodeMap = objectMapper.convertValue(nodeObj, Map.class);
                 * if (sourceId.equals(String.valueOf(nodeMap.get("id")))
                 * && "condition".equals(nodeMap.get("type"))) {
                 * Map<String, Object> nodeData = (Map<String, Object>) nodeMap
                 * .get("data");
                 * if (nodeData != null) {
                 * Object conditionsObj = nodeData.get("conditions");
                 * if (conditionsObj instanceof List) {
                 * 
                 * @SuppressWarnings("unchecked")
                 * List<Map<String, Object>> conditions = (List<Map<String, Object>>)
                 * conditionsObj;
                 * // 첫 번째 condition 사용
                 * if (!conditions.isEmpty()) {
                 * Map<String, Object> firstCondition = conditions.get(0);
                 * Object conditionId = firstCondition.get("id");
                 * if (conditionId != null) {
                 * edgeMap.put("sourceHandle",
                 * "handle-" + conditionId.toString());
                 * }
                 * }
                 * }
                 * }
                 * break;
                 * }
                 * } catch (Exception e) {
                 * // 노드 변환 실패 시 무시
                 * }
                 * }
                 * }
                 * }
                 * 
                 * // 🔥 정상 케이스 형식에 맞춰 엣지 처리
                 * // 1. generator/union 엣지: data.condition_label 필드 제거
                 * // 2. reviewer 엣지: label과 data.category.id를
                 * "condition-pass"/"condition-fail"로
                 * // 수정
                 * String sourceId = (String) edgeMap.get("source");
                 * if (sourceId != null && graphResponse.getNodes() != null) {
                 * for (Object nodeObj : graphResponse.getNodes()) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> nodeMap = objectMapper.convertValue(nodeObj, Map.class);
                 * if (sourceId.equals(String.valueOf(nodeMap.get("id")))) {
                 * String nodeType = (String) nodeMap.get("type");
                 * 
                 * // 🔥 각 노드 타입에 맞는 sourceHandle 설정
                 * if (!edgeMap.containsKey("sourceHandle")) {
                 * if ("input__basic".equals(nodeType)) {
                 * edgeMap.put("sourceHandle", "input_right");
                 * } else if ("agent__generator".equals(nodeType)
                 * || "union".equals(nodeType)) {
                 * edgeMap.put("sourceHandle", "gen_right");
                 * }
                 * // Condition과 Reviewer는 위에서 이미 처리됨
                 * }
                 * 
                 * // 🔥 target 노드 타입에 맞는 targetHandle 설정
                 * String targetId = (String) edgeMap.get("target");
                 * if (targetId != null && graphResponse.getNodes() != null
                 * && !edgeMap.containsKey("targetHandle")) {
                 * for (Object targetNodeObj : graphResponse.getNodes()) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> targetNodeMap = objectMapper
                 * .convertValue(targetNodeObj, Map.class);
                 * if (targetId.equals(String.valueOf(targetNodeMap.get("id")))) {
                 * String targetNodeType = (String) targetNodeMap.get("type");
                 * if ("condition".equals(targetNodeType)) {
                 * edgeMap.put("targetHandle", "condition_left");
                 * } else if ("agent__generator".equals(targetNodeType)
                 * || "union".equals(targetNodeType)) {
                 * edgeMap.put("targetHandle", "gen_left");
                 * } else if ("output__chat".equals(targetNodeType)
                 * || "output__formatter".equals(targetNodeType)) {
                 * edgeMap.put("targetHandle", "output_formatter_left");
                 * } else if ("agent__reviewer".equals(targetNodeType)) {
                 * edgeMap.put("targetHandle", "gen_left");
                 * }
                 * break;
                 * }
                 * } catch (Exception e) {
                 * // 노드 변환 실패 시 무시
                 * }
                 * }
                 * }
                 * 
                 * // generator/union 엣지: data.condition_label 필드 제거 (엣지 레벨은 유지 - 정상 케이스에 있음)
                 * if ("agent__generator".equals(nodeType) || "union".equals(nodeType)) {
                 * // data.condition_label 제거
                 * if (edgeMap.containsKey("data") && edgeMap.get("data") != null) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> dataMap = objectMapper
                 * .convertValue(edgeMap.get("data"), Map.class);
                 * if (dataMap.containsKey("condition_label")) {
                 * log.debug(
                 * "🔍 generator/union 엣지에서 data.condition_label 제거: edgeId={}, condition_label={}"
                 * ,
                 * edgeMap.get("id"), dataMap.get("condition_label"));
                 * dataMap.remove("condition_label");
                 * edgeMap.put("data", dataMap);
                 * }
                 * } catch (Exception e) {
                 * log.warn("data 변환 실패: {}", e.getMessage());
                 * }
                 * }
                 * }
                 * 
                 * // reviewer 엣지: label과 data.category.id를 "condition-pass"/"condition-fail"로
                 * // 수정
                 * if ("agent__reviewer".equals(nodeType)) {
                 * Object conditionLabel = edgeMap.get("condition_label");
                 * if (conditionLabel != null) {
                 * String conditionLabelStr = conditionLabel.toString();
                 * String newLabel = null;
                 * String newCategoryId = null;
                 * 
                 * if ("pass".equals(conditionLabelStr)
                 * || conditionLabelStr.contains("pass")) {
                 * newLabel = "condition-pass";
                 * newCategoryId = "condition-pass";
                 * } else if ("fail".equals(conditionLabelStr)
                 * || conditionLabelStr.contains("fail")) {
                 * newLabel = "condition-fail";
                 * newCategoryId = "condition-fail";
                 * }
                 * 
                 * if (newLabel != null && newCategoryId != null) {
                 * log.debug(
                 * "🔍 reviewer 엣지 label/category 수정: edgeId={}, oldLabel={}, newLabel={}, newCategoryId={}"
                 * ,
                 * edgeMap.get("id"), edgeMap.get("label"), newLabel,
                 * newCategoryId);
                 * // label 수정
                 * edgeMap.put("label", newLabel);
                 * 
                 * // data.category.id 수정
                 * if (edgeMap.containsKey("data")
                 * && edgeMap.get("data") != null) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> dataMap = objectMapper
                 * .convertValue(edgeMap.get("data"), Map.class);
                 * if (dataMap.containsKey("category")
                 * && dataMap.get("category") != null) {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> categoryMap = objectMapper
                 * .convertValue(dataMap.get("category"),
                 * Map.class);
                 * categoryMap.put("id", newCategoryId);
                 * categoryMap.put("category", newCategoryId);
                 * dataMap.put("category", categoryMap);
                 * edgeMap.put("data", dataMap);
                 * }
                 * } catch (Exception e) {
                 * log.warn("reviewer 엣지 data.category 수정 실패: {}",
                 * e.getMessage());
                 * }
                 * }
                 * }
                 * }
                 * }
                 * 
                 * break;
                 * }
                 * } catch (Exception e) {
                 * // 노드 변환 실패 시 무시
                 * }
                 * }
                 * }
                 * 
                 * processedEdges.add(edgeMap);
                 * } catch (Exception e) {
                 * log.warn("Edge 처리 중 오류 발생 (원본 유지): {}", e.getMessage());
                 * processedEdges.add(edgeObj); // 변환 실패 시 원본 유지
                 * }
                 * }
                 * 
                 * graphResponse.setEdges(processedEdges);
                 * }
                 */

                // // 🔥 기존 노드 가공 로직 주석처리 - 원본 데이터 그대로 전달
                /*
                 * if (graphResponse.getNodes() != null) {
                 * ObjectMapper objectMapper = new ObjectMapper();
                 * List<Object> processedNodes = new ArrayList<>();
                 * 
                 * for (Object nodeObj : graphResponse.getNodes()) {
                 * try {
                 * // Object를 Map으로 변환
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> nodeMap = objectMapper.convertValue(nodeObj, Map.class);
                 * 
                 * // source_position이 null이면 "right"로 설정 (항상 설정)
                 * if (!nodeMap.containsKey("source_position") || nodeMap.get("source_position")
                 * == null) {
                 * nodeMap.put("source_position", "right");
                 * }
                 * 
                 * // target_position이 null이면 "left"로 설정 (항상 설정)
                 * if (!nodeMap.containsKey("target_position") || nodeMap.get("target_position")
                 * == null) {
                 * nodeMap.put("target_position", "left");
                 * }
                 * 
                 * // style이 null이면 {}로 설정 (항상 설정)
                 * Object styleValue = nodeMap.get("style");
                 * if (styleValue == null) {
                 * nodeMap.put("style", new HashMap<>());
                 * }
                 * 
                 * // data.fewshot_id가 null이면 ""로 설정
                 * if (nodeMap.containsKey("data") && nodeMap.get("data") != null) {
                 * try {
                 * 
                 * @SuppressWarnings("unchecked")
                 * Map<String, Object> dataMap = objectMapper.convertValue(nodeMap.get("data"),
                 * Map.class);
                 * if (!dataMap.containsKey("fewshot_id") || dataMap.get("fewshot_id") == null)
                 * {
                 * dataMap.put("fewshot_id", "");
                 * }
                 * nodeMap.put("data", dataMap);
                 * } catch (Exception e) {
                 * // data 변환 실패 시 무시
                 * }
                 * }
                 * 
                 * processedNodes.add(nodeMap);
                 * } catch (Exception e) {
                 * log.warn("Node 처리 중 오류 발생 (원본 유지): {}", e.getMessage());
                 * processedNodes.add(nodeObj); // 변환 실패 시 원본 유지
                 * }
                 * }
                 * 
                 * graphResponse.setNodes(processedNodes);
                 * }
                 */

                return graphResponse;
            } else {
                log.warn("Graph 상세 조회 응답에서 data가 null입니다 - graphUuid: {}", graphUuid);
                return null;
            }
        } catch (BusinessException e) {
            log.error("Graph 상세 조회 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 상세 조회 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphCreateResponse createGraph(GraphCreateRequest request) {
        try {
            log.debug("Graph 생성 요청");
            GraphCreateResponse response = sktaiAgentGraphsClient.createGraph(request);
            log.debug("Graph 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Graph 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 생성에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphCreateResponse createGraph(Map<String, Object> requestData) {
        try {
            log.info("Graph 생성 요청 (Map 데이터): {}", requestData);

            // Map을 GraphCreateRequest로 변환
            GraphCreateRequest request = convertMapToGraphCreateRequest(requestData);
            log.info("변환된 GraphCreateRequest: {}", request);

            GraphCreateResponse response = sktaiAgentGraphsClient.createGraph(request);
            log.info("Graph 생성 성공: {}", response);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 생성 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Map 데이터를 GraphCreateRequest로 변환
     */
    private GraphCreateRequest convertMapToGraphCreateRequest(Map<String, Object> requestData) {
        try {
            log.info("Map 데이터를 GraphCreateRequest로 변환 시작: {}", requestData);

            // 직접 객체 생성으로 변환
            GraphCreateRequest request = GraphCreateRequest.builder()
                    .name((String) requestData.get("name"))
                    .description((String) requestData.get("description"))
                    .graph(requestData.get("graph"))
                    .templateId((String) requestData.get("template_id"))
                    .build();

            log.info("변환 완료: {}", request);
            return request;
        } catch (BusinessException e) {
            log.error("Map을 GraphCreateRequest로 변환 실패 (BusinessException): {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Map을 GraphCreateRequest로 변환 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청 데이터 변환에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphUpdateOrDeleteResponse updateGraph(String graphUuid, GraphUpdateRequest request) {
        try {
            log.debug("Graph 수정 요청 - graphUuid: {}", graphUuid);
            GraphUpdateOrDeleteResponse response = sktaiAgentGraphsClient.updateGraph(graphUuid, request);
            log.debug("Graph 수정 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 수정 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 수정 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 수정에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphUpdateOrDeleteResponse deleteGraph(String graphUuid) {
        try {
            log.debug("Graph 삭제 요청 - graphUuid: {}", graphUuid);
            GraphUpdateOrDeleteResponse response = sktaiAgentGraphsClient.deleteGraph(graphUuid);
            log.debug("Graph 삭제 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 삭제 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 삭제 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    public void hardDeleteGraph() {
        try {
            log.debug("Graph 영구 삭제 요청");
            sktaiAgentGraphsClient.hardDeleteGraph();
            log.debug("Graph 영구 삭제 성공");
        } catch (BusinessException e) {
            log.error("Graph 영구 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 영구 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 영구 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphTemplatesResponse getGraphTemplates() {
        try {
            log.debug("Graph 템플릿 목록 조회 요청");
            GraphTemplatesApiResponse apiResponse = sktaiAgentGraphsClient.getGraphTemplates();
            log.debug("Graph 템플릿 목록 조회 성공: {}", apiResponse);

            // API 응답을 기존 DTO 형식으로 변환
            GraphTemplatesResponse response = convertApiResponseToGraphTemplatesResponse(apiResponse);
            log.debug("Graph 템플릿 목록 변환 완료: {}", response);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 템플릿 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 템플릿 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 템플릿 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API 응답을 GraphTemplatesResponse로 변환
     */
    private GraphTemplatesResponse convertApiResponseToGraphTemplatesResponse(GraphTemplatesApiResponse apiResponse) {
        if (apiResponse == null || apiResponse.getData() == null) {
            log.warn("API 응답이 null이거나 data가 null입니다");
            return GraphTemplatesResponse.builder()
                    .templates(null)
                    .totalCount(0)
                    .categories(null)
                    .build();
        }

        // API 응답의 data를 GraphTemplate 리스트로 변환
        List<GraphTemplatesResponse.GraphTemplate> templates = new ArrayList<>();
        for (GraphTemplatesApiResponse.GraphTemplateInfo apiTemplate : apiResponse.getData()) {
            GraphTemplatesResponse.GraphTemplate template = GraphTemplatesResponse.GraphTemplate.builder()
                    .templateId(apiTemplate.getTemplateId())
                    .name(apiTemplate.getTemplateName())
                    .description(apiTemplate.getTemplateDescription())
                    .category("general") // 기본 카테고리
                    .version("1.0") // 기본 버전
                    .tags(List.of("template")) // 기본 태그
                    .createdAt(java.time.LocalDateTime.now()) // 현재 시간
                    .usageCount(0) // 기본 사용 횟수
                    .build();
            templates.add(template);
        }

        return GraphTemplatesResponse.builder()
                .templates(templates)
                .totalCount(templates.size())
                .categories(List.of("general")) // 기본 카테고리
                .build();
    }

    public GraphCreateResponse createGraphFromTemplate(String templateId, GraphCreateRequest request) {
        try {
            log.debug("템플릿으로 Graph 생성 요청 - templateId: {}", templateId);
            GraphCreateResponse response = sktaiAgentGraphsClient.createGraphFromTemplate(templateId, request);
            log.debug("템플릿으로 Graph 생성 성공 - templateId: {}", templateId);
            return response;
        } catch (BusinessException e) {
            log.error("템플릿으로 Graph 생성 실패 (BusinessException) - templateId: {}, message: {}", templateId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("템플릿으로 Graph 생성 실패 (예상치 못한 오류) - templateId: {}", templateId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "템플릿으로 Graph 생성에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphResponse getGraphTemplate(String templateId) {
        try {
            log.info("🔍 Graph 템플릿 상세 조회 요청 - templateId: {}", templateId);

            // 새로운 DTO를 사용하여 API 응답 받기
            GraphTemplateApiResponse apiResponse = sktaiAgentGraphsClient.getGraphTemplate(templateId);
            log.info("🔍 Graph 템플릿 상세 조회 성공 - templateId: {}", templateId);
            log.info("🔍 API 응답: {}", apiResponse);

            if (apiResponse != null && apiResponse.getData() != null) {
                GraphTemplateApiResponse.GraphTemplateData data = apiResponse.getData();
                log.info("🔍 템플릿 데이터: {}", data);

                // GraphResponse 객체로 변환
                GraphResponse response = new GraphResponse();
                response.setId(data.getId());
                response.setName(data.getName());
                response.setDescription(data.getDescription());
                response.setType(data.getType());
                response.setCategory(data.getCategory());
                response.setStatus(data.getStatus());

                // Type safety를 위해 null 체크와 함께 안전한 캐스팅 수행
                if (data.getNodes() != null) {
                    response.setNodes(new ArrayList<>(data.getNodes()));
                } else {
                    response.setNodes(new ArrayList<>());
                }

                if (data.getEdges() != null) {
                    response.setEdges(new ArrayList<>(data.getEdges()));
                } else {
                    response.setEdges(new ArrayList<>());
                }

                response.setConfig(data.getConfig());
                response.setCreatedAt(data.getCreatedAt());
                response.setUpdatedAt(data.getUpdatedAt());
                response.setCreatedBy(data.getCreatedBy());
                response.setUpdatedBy(data.getUpdatedBy());

                log.info("🔍 GraphResponse 변환 완료: {}", response);
                return response;
            } else {
                log.warn("⚠️ API 응답 또는 데이터가 null입니다 - templateId: {}", templateId);
                return new GraphResponse();
            }
        } catch (BusinessException e) {
            log.error("❌ Graph 템플릿 상세 조회 실패 (BusinessException) - templateId: {}, message: {}", templateId,
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("❌ Graph 템플릿 상세 조회 실패 (예상치 못한 오류) - templateId: {}", templateId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 템플릿 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 템플릿 데이터를 Map 형태로 반환하는 메서드
     * 
     * @param templateId 템플릿 ID
     * @return 템플릿 데이터 (Map 형태)
     */
    public Map<String, Object> getTemplate(String templateId) {
        try {
            log.debug("템플릿 데이터 조회 요청 - templateId: {}", templateId);
            GraphTemplateApiResponse response = sktaiAgentGraphsClient.getGraphTemplate(templateId);

            if (response == null) {
                log.warn("템플릿 데이터가 null입니다 - templateId: {}", templateId);
                return new HashMap<>();
            }

            // GraphResponse를 Map으로 변환 (Type safety 고려)
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> templateData = objectMapper.convertValue(response, Map.class);

            log.debug("템플릿 데이터 조회 성공 - templateId: {}, 데이터: {}", templateId, templateData);
            return templateData;

        } catch (BusinessException e) {
            log.error("템플릿 데이터 조회 실패 (BusinessException) - templateId: {}, message: {}", templateId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("템플릿 데이터 조회 실패 (예상치 못한 오류) - templateId: {}", templateId, e);
            // 실패 시 빈 Map 반환 (예외를 던지지 않음)
            return new HashMap<>();
        }
    }

    public GraphAppResponse getGraphAppInfo(String graphUuid) {
        try {
            log.debug("Graph App ID 조회 요청 - graphUuid: {}", graphUuid);
            GraphAppResponse response = sktaiAgentGraphsClient.getGraphAppInfo(graphUuid);
            log.debug("Graph App ID 조회 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 노드 정보 조회 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 노드 정보 조회 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 노드 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphUpdateOrDeleteResponse updateGraphInfo(String graphUuid, GraphInfoUpdateRequest request) {
        try {
            log.debug("Graph 정보 업데이트 요청 - graphUuid: {}", graphUuid);
            GraphUpdateOrDeleteResponse response = sktaiAgentGraphsClient.updateGraphInfo(graphUuid, request);
            log.debug("Graph 정보 업데이트 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 정보 업데이트 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 정보 업데이트 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 정보 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphUpdateOrDeleteResponse saveGraph(String graphUuid, GraphSaveRequest request) {
        // 🆕 강제 테스트 로깅 - 이 로그가 보이면 코드가 적용된 것
        log.info("🚨🚨🚨 강제 테스트 로깅 - saveGraph 메서드 호출됨! 🚨🚨🚨");
        log.info("🚨🚨🚨 현재 시간: {} 🚨🚨🚨", new java.util.Date());

        try {
            log.info("🔍 Graph 전체 저장 요청 시작 - graphUuid: {}", graphUuid);
            log.info("🔍 저장 요청 데이터 상세:");
            log.info("  - name: {}", request.getName());
            log.info("  - description: {}", request.getDescription());
            log.info("  - graph: {}", request.getGraph() != null ? "존재함" : "null");

            if (request.getGraph() != null) {
                log.info("  - nodes 개수: {}",
                        request.getGraph().getNodes() != null ? request.getGraph().getNodes().size() : 0);
                log.info("  - edges 개수: {}",
                        request.getGraph().getEdges() != null ? request.getGraph().getEdges().size() : 0);

                // 노드 상세 정보 로깅
                if (request.getGraph().getNodes() != null && !request.getGraph().getNodes().isEmpty()) {
                    log.info("🔍 노드 상세 정보:");
                    request.getGraph().getNodes().forEach(node -> {
                        log.info("    - 노드 ID: {}, 타입: {}, 데이터: {}",
                                node.getId(), node.getType(), node.getData());
                    });
                }

                // 🔥 엣지 상세 정보 로깅 (sourceHandle 필드 확인)
                if (request.getGraph().getEdges() != null && !request.getGraph().getEdges().isEmpty()) {
                    log.info("🔍 엣지 상세 정보 (sourceHandle 필드 확인):");
                    request.getGraph().getEdges().forEach(edge -> {
                        // log.info(
                        //         "    - 엣지 ID: {}, 소스: {}, 타겟: {}, 타입: {}, source_handle: {}, sourceHandle: {}, condition_label: {}",
                        //         edge.getId(), edge.getSource(), edge.getTarget(), edge.getType(),
                        //         edge.getSourceHandle(), edge.getSourceHandleFrontend(), edge.getConditionLabel());
                    });
                }
            }

            log.info("🚀 SKT AI Platform API 호출 시작...");

            // 🆕 Feign 클라이언트 설정 정보 로깅
            log.info("🔍 Feign 클라이언트 설정 정보:");
            log.info("  - Feign Client Name: {}", sktaiAgentGraphsClient.getClass().getSimpleName());
            log.info("  - Base URL: ${sktai.api.base-url}/api/v1/agent");
            log.info("  - Target URL: /agents/graphs/{}", graphUuid);
            log.info("  - Full URL: https://aip-stg.sktai.io/api/v1/agent/agents/graphs/{}", graphUuid);

            // 🆕 요청 데이터 JSON 변환 시도
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String requestJson = objectMapper.writeValueAsString(request);
                log.info("🔍 요청 데이터 JSON: {}", requestJson);
            } catch (BusinessException e) {
                log.warn("🔍 요청 데이터 JSON 변환 실패 (BusinessException): {}", e.getMessage());
                throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
            } catch (Exception jsonError) {
                log.warn("🔍 요청 데이터 JSON 변환 실패 (예상치 못한 오류): {}", jsonError.getMessage());
            }

            GraphUpdateOrDeleteResponse response = sktaiAgentGraphsClient.saveGraph(graphUuid, request);
            log.info("✅ Graph 전체 저장 성공 - graphUuid: {}, response: {}", graphUuid, response);
            return response;
        } catch (BusinessException e) {
            log.error("🔍 Graph 전체 저장 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (FeignException e) {
            log.error("🔍 Graph 전체 저장 실패 (FeignException) - graphUuid: {}, status: {}, reason: {}, content: {}",
                    graphUuid, e.status(), e.getMessage(), e.contentUTF8());

            // FeignException 상세 정보 로깅
            log.error("🔍 FeignException 상세 정보:");
            log.error("  - HTTP Status: {}", e.status());
            log.error("  - Error Message: {}", e.getMessage());
            log.error("  - Response Content: {}", e.contentUTF8());
            log.error("  - Request URL: {}", e.request() != null ? e.request().url() : "unknown");
            log.error("  - Request Method: {}", e.request() != null ? e.request().httpMethod() : "unknown");
            log.error("  - Request Headers: {}", e.request() != null ? e.request().headers() : "unknown");

            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    String.format("Graph 전체 저장에 실패했습니다: HTTP %d - %s", e.status(), e.getMessage()));
        } catch (Exception e) {
            log.error("🔍 Graph 전체 저장 실패 (일반 예외) - graphUuid: {}, request: {}, error: {}", graphUuid, request,
                    e.getMessage(), e);

            // 일반 예외 상세 정보 로깅
            log.error("🔍 일반 예외 상세 정보:");
            log.error("  - Exception Type: {}", e.getClass().getSimpleName());
            log.error("  - Error Message: {}", e.getMessage());
            log.error("  - Stack Trace: ", e);

            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 전체 저장에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphExecuteResponse executeGraphQuery(String graphUuid, GraphExecuteRequest request) {
        try {
            log.debug("Graph Query 실행 요청 - graphUuid: {}", graphUuid);
            GraphExecuteResponse response = sktaiAgentGraphsClient.executeGraphQuery(graphUuid, request);
            log.debug("Graph Query 실행 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph Query 실행 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph Query 실행 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph Query 실행에 실패했습니다: " + e.getMessage());
        }
    }

    public feign.Response executeGraphStreamResponse(GraphExecuteRequest request) {
        try {
            log.debug("Graph Stream 실행 요청 (feign.Response) - graphId: {}", request.getGraphId());
            feign.Response response = sktaiAgentGraphsClient.executeGraphStream(request);
            log.debug("Graph Stream 실행 성공 (feign.Response) - graphId: {}, status: {}",
                    request.getGraphId(), response.status());
            return response;
        } catch (BusinessException e) {
            log.error("Graph Stream 실행 실패 (BusinessException) - graphId: {}, message: {}", request.getGraphId(),
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Graph Stream 실행 실패 (예상치 못한 오류) - graphId: {}", request.getGraphId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph Stream 실행에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphExecuteResponse executeGraphTest(String graphUuid, GraphExecuteRequest request) {
        try {
            log.debug("Graph Test 실행 요청 - graphUuid: {}", graphUuid);
            GraphExecuteResponse response = sktaiAgentGraphsClient.executeGraphTest(graphUuid, request);
            log.debug("Graph Test 실행 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph Test 실행 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph Test 실행 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph Test 실행에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphNodeInfoResponse getGraphNodeInfo() {
        try {
            log.debug("Graph Node 정보 조회 요청");
            GraphNodeInfoResponse response = sktaiAgentGraphsClient.getGraphNodeInfo();
            log.debug("Graph Node 정보 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Graph Node 정보 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph Node 정보 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph Node 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public List<String> getReservedVariables() {
        try {
            log.debug("Graph 예약 변수 조회 요청");
            List<String> response = sktaiAgentGraphsClient.getReservedVariables();
            log.debug("Graph 예약 변수 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Graph 예약 변수 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 예약 변수 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 예약 변수 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public GraphCreateResponse copyGraph(String graphUuid, GraphCopyRequest request) {
        try {
            log.debug("Graph 복사 요청 - graphUuid: {}", graphUuid);
            GraphCreateResponse response = sktaiAgentGraphsClient.copyGraph(graphUuid, request);
            log.debug("Graph 복사 성공 - graphUuid: {}", graphUuid);
            return response;
        } catch (BusinessException e) {
            log.error("Graph 복사 실패 (BusinessException) - graphUuid: {}, message: {}", graphUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Graph 복사 실패 (예상치 못한 오류) - graphUuid: {}", graphUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph 복사에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Graph Import (JSON)
     * 
     * <p>
     * JSON 문자열을 받아서 Graph를 생성합니다.
     * 마이그레이션 등에서 사용됩니다.
     * </p>
     * 
     * @param graphId Graph ID
     * @param json    JSON 문자열
     * @return 생성된 Graph 정보
     */
    public GraphCreateResponse importGraph(String graphId, String json) {
        try {
            log.info("Graph Import 요청 - graphId: {}, jsonLength: {}", graphId, json != null ? json.length() : 0);

            // 파라미터 검증
            if (graphId == null || graphId.trim().isEmpty()) {
                throw new IllegalArgumentException("Graph ID (agent_id)는 필수입니다");
            }

            // JSON 문자열을 Object로 변환 (Tool Import와 동일한 방식)
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonData = objectMapper.readValue(json, Object.class);

            // 서버 API는 agent_id를 query parameter로 받음
            // graphId를 agent_id로 전달
            log.debug("Graph Import JSON 변환 완료 - agentId: {}", graphId);
            GraphCreateResponse response = sktaiAgentGraphsClient.importGraph(graphId, jsonData);

            return response;
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Graph Import에 실패했습니다: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Graph Import에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException re) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Graph Import에 실패했습니다: " + re.getMessage());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Graph Import에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Phoenix Trace Project 식별자 조회
     *
     * @param type 리소스 타입 (graph/app)
     * @param id   그래프 또는 앱 ID
     * @return 디코딩된 프로젝트 ID, 없으면 null
     */
    public String getPhoenixProjectIdentifier(String type, String id) {
        try {
            log.info("Phoenix Trace Project 조회 요청 - type: {}, id: {}", type, id);
            Map<String, Object> response = sktaiAgentGraphsClient.getTraceProject(type, id);

            if (response == null || response.isEmpty()) {
                log.warn("Phoenix Trace Project 응답 데이터가 없습니다.");
                return null;
            }

            Object rawData = response.get("data");
            if (!(rawData instanceof String)) {
                if (rawData == null) {
                    log.warn("Phoenix Trace Project 응답 데이터가 null입니다.");
                    return null;
                }
                log.warn("Phoenix Trace Project 응답 타입이 String이 아닙니다: {}", rawData.getClass().getSimpleName());
                return null;
            }

            String projectId = ((String) rawData).trim();
            if (projectId.isEmpty()) {
                log.warn("Phoenix Trace Project 응답이 빈 문자열입니다.");
                return null;
            }

            // Base64 인코딩된 값을 그대로 반환 (프론트엔드에서 URL 구성 시 사용)
            log.info("Phoenix Trace Project ID 수신 (Base64 인코딩된 값): {}", projectId);
            return projectId;
        } catch (BusinessException e) {
            log.error("Phoenix Trace Project 조회 실패 (BusinessException) - type: {}, id: {}, message: {}", type, id,
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Phoenix Trace Project 조회 실패 (예상치 못한 오류) - type: {}, id: {}", type, id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Phoenix Trace Project 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Agent Graph Export (Python 코드 조회)
     *
     * @param graphId        그래프 ID
     * @param credentialType 인증 타입 (token/password)
     * @return Python 코드가 포함된 응답 Map
     */
    public Map<String, Object> exportGraphCode(String graphId, String credentialType) {
        try {
            log.info("Agent Graph Export 요청 - graphId: {}, credentialType: {}", graphId, credentialType);
            Map<String, Object> response = sktaiAgentGraphsClient.exportGraphCode(graphId, credentialType);

            if (response == null || response.isEmpty()) {
                log.warn("Agent Graph Export 응답 데이터가 없습니다.");
                return new HashMap<>();
            }

            log.info("Agent Graph Export 성공 - graphId: {}", graphId);
            return response;
        } catch (BusinessException e) {
            log.error("Agent Graph Export 실패 (BusinessException) - graphId: {}, message: {}", graphId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Agent Graph Export 실패 (예상치 못한 오류) - graphId: {}", graphId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent Graph Export에 실패했습니다: " + e.getMessage());
        }
    }
}
