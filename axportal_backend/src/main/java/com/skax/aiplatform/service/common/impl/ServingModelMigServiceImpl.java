package com.skax.aiplatform.service.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingUpdateResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.model.request.CreateBackendAiModelDeployReq;
import com.skax.aiplatform.dto.model.request.CreateModelDeployReq;
import com.skax.aiplatform.dto.model.response.CreateBackendAiModelDeployRes;
import com.skax.aiplatform.dto.model.response.CreateModelDeployRes;
import com.skax.aiplatform.dto.model.response.GetModelDeployRes;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.common.ServingModelMigService;
import com.skax.aiplatform.service.model.ModelDeployService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServingModelMigServiceImpl implements ServingModelMigService {

    private final ObjectMapper objectMapper;
    private final ModelDeployService modelDeployService;
    private final AdminAuthService adminAuthService;
    private final SktaiServingService sktaiServingService;

    @Override
    public String exportToImportFormat(String servingId) {
        // 1. GetModelDeployRes 조회
        GetModelDeployRes deployRes = modelDeployService.getModelDeployById(servingId);
        log.info("SERVING_MODEL 조회 완료 - servingId: {}, deployRes: {}", servingId, deployRes);

        // 2. Export → Import 형식으로 변환
        try {
            String importJson = objectMapper.writeValueAsString(deployRes);
            return importJson;
        } catch (JsonProcessingException e) {
            log.error("SERVING_MODEL JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INVALID_JSON_FORMAT,
                    "SERVING_MODEL JSON 파싱 실패: " + e.getMessage());
        }
    }

    @Override
    public boolean importFromJsonString(String jsonString, Long projectId) {
        return importFromJsonString(jsonString, projectId, false);
    }
    
    @Override
    public boolean importFromJsonString(String jsonString, Long projectId, boolean isExist) {
        try {
            log.info("SERVING_MODEL JSON 문자열에서 {} 시작 - isExist: {}", isExist ? "Update" : "Import", isExist);

            // GetModelDeployRes로 파싱
            GetModelDeployRes deployRes = objectMapper.readValue(jsonString, GetModelDeployRes.class);
            String servingType = deployRes.getServingType();
            String existingServingId = deployRes.getServingId();

            log.info("JSON 파싱 완료 - servingType: {}, existingServingId: {}", servingType, existingServingId);

            if (isExist && existingServingId != null) {
                // 기존 데이터가 존재하면 Update 수행
                return updateFromJsonString(existingServingId, deployRes, projectId);
            }

            String servingId = null;
            // servingType에 따라 분기 처리
            if ("serverless".equalsIgnoreCase(servingType)) {
                servingId = createServerlessDeploy(deployRes, projectId);
            } else if ("self_hosting".equalsIgnoreCase(servingType)) {
                servingId = createSelfHostingDeploy(deployRes, projectId);
            } else {
                String errorMsg = String.format("지원하지 않는 servingType: %s", servingType);
                log.error(errorMsg);
                return false;
            }
            
            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("SERVING_MODEL JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/servings/" + servingId, projectId);
                log.info("SERVING_MODEL JSON 문자열에서 Import - 권한 설정 완료");
            }

            if(servingId != null) {
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("SERVING_MODEL JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (BusinessException e) {
            log.error("SERVING_MODEL 배포 생성 실패 (BusinessException) - error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("SERVING_MODEL JSON 문자열에서 Import 실패 (예상치 못한 오류) - error: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Update 수행 (기존 SERVING_MODEL이 존재할 때 사용)
     * 
     * @param servingId Serving ID
     * @param deployRes 배포 정보
     * @param projectId 프로젝트 ID (선택사항)
     * @return Update 성공 여부
     */
    private boolean updateFromJsonString(String servingId, GetModelDeployRes deployRes, Long projectId) {
        try {
            log.info("SERVING_MODEL JSON 문자열에서 Update 시작 - servingId: {}", servingId);

            String servingType = deployRes.getServingType();
            
            // Update 전에 serving을 stop
            log.info("SERVING_MODEL Update 전 Stop 시작 - servingId: {}, servingType: {}", servingId, servingType);
            try {
                if ("serverless".equalsIgnoreCase(servingType)) {
                    sktaiServingService.stopServing(servingId);
                } else if ("self_hosting".equalsIgnoreCase(servingType)) {
                    sktaiServingService.stopBackendAiServing(servingId);
                } else {
                    log.warn("지원하지 않는 servingType for stop: {}", servingType);
                }
                log.info("SERVING_MODEL Stop 완료 - servingId: {}", servingId);
            } catch (Exception e) {
                log.warn("SERVING_MODEL Stop 실패 (Update 계속 진행) - servingId: {}, error: {}", servingId, e.getMessage());
                // Stop 실패해도 Update는 계속 진행
            }

            // ServingUpdate 객체 생성
            ServingUpdate updateRequest = ServingUpdate.builder()
                    .description(deployRes.getDescription())
                    .build();
            
            // servingType에 따라 분기 처리
            ServingUpdateResponse response;
            if ("serverless".equalsIgnoreCase(servingType)) {
                response = sktaiServingService.updateServing(servingId, updateRequest);
            } else if ("self_hosting".equalsIgnoreCase(servingType)) {
                response = sktaiServingService.updateBackendAiServing(servingId, updateRequest);
            } else {
                log.error("지원하지 않는 servingType for update: {}", servingType);
                return false;
            }
            
            // Update 후에 serving을 start
            log.info("SERVING_MODEL Update 후 Start 시작 - servingId: {}, servingType: {}", servingId, servingType);
            try {
                if ("serverless".equalsIgnoreCase(servingType)) {
                    sktaiServingService.startServing(servingId);
                } else if ("self_hosting".equalsIgnoreCase(servingType)) {
                    sktaiServingService.startBackendAiServing(servingId);
                } else {
                    log.warn("지원하지 않는 servingType for start: {}", servingType);
                }
                log.info("SERVING_MODEL Start 완료 - servingId: {}", servingId);
            } catch (Exception e) {
                log.error("SERVING_MODEL Start 실패 - servingId: {}, error: {}", servingId, e.getMessage(), e);
                // Start 실패는 에러로 처리하지 않지만 로그는 남김
            }

            if (response == null) {
                log.error("SERVING_MODEL Update 응답이 null입니다 - servingId: {}", servingId);
                return false;
            }

            // projectId가 있을 경우만, 권한 설정 
            if (projectId != null) {
                log.info("SERVING_MODEL JSON 문자열에서 Update - 권한 설정 시작 - projectId: {}", projectId);
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/servings/" + servingId, projectId);
                log.info("SERVING_MODEL JSON 문자열에서 Update - 권한 설정 완료");
            }

            log.info("SERVING_MODEL Update 성공 - servingId: {}", servingId);
            return true;

        } catch (Exception e) {
            log.error("SERVING_MODEL JSON 문자열에서 Update 실패 - servingId: {}, error: {}", 
                    servingId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Serverless 모델 배포 생성
     *
     * @param deployRes 배포 정보
     * @return 생성된 servingId
     */
    private String createServerlessDeploy(GetModelDeployRes deployRes, Long projectId) {
        CreateModelDeployReq createReq = CreateModelDeployReq.builder()
                .name(deployRes.getName())
                .description(deployRes.getDescription())
                .modelId(deployRes.getModelId())
                .safetyFilterInput(deployRes.getSafetyFilterInput() != null ? deployRes.getSafetyFilterInput() : false)
                .safetyFilterOutput(
                        deployRes.getSafetyFilterOutput() != null ? deployRes.getSafetyFilterOutput() : false)
                .dataMaskingInput(false)
                .dataMaskingOutput(false)
                .safetyFilterInputGroups(deployRes.getSafetyFilterInputGroups() != null
                        ? deployRes.getSafetyFilterInputGroups()
                        : java.util.Collections.emptyList())
                .safetyFilterOutputGroups(deployRes.getSafetyFilterOutputGroups() != null
                        ? deployRes.getSafetyFilterOutputGroups()
                        : java.util.Collections.emptyList())
                .isCustom(false)
                .build();

        CreateModelDeployRes response = modelDeployService.createModelDeploy(createReq, projectId);
        log.info("Serverless 모델 배포 생성 완료 - servingId: {}, name: {}",
                response.getServingId(), response.getName());
        return response.getServingId();
    }

    /**
     * Self-hosting 모델 배포 생성
     *
     * @param deployRes 배포 정보
     * @return 생성된 servingId
     */
    private String createSelfHostingDeploy(GetModelDeployRes deployRes, Long projectId) {
        // servingParams가 String(JSON 문자열)인 경우 Object로 파싱
        Object servingParams = null;
        String servingParamsStr = deployRes.getServingParams();
        if (servingParamsStr != null && !servingParamsStr.trim().isEmpty()) {
            try {
                // JSON 문자열을 Map으로 파싱하여 Object로 변환
                servingParams = objectMapper.readValue(servingParamsStr, new TypeReference<Map<String, Object>>() {});
                log.info("servingParams JSON 문자열 파싱 완료");
            } catch (JsonProcessingException e) {
                log.warn("servingParams JSON 문자열 파싱 실패 - 원본 문자열을 그대로 사용합니다. error: {}", e.getMessage());
                // 파싱 실패 시 null로 설정 (빈 객체로 처리됨)
                servingParams = null;
            }
        }

        CreateBackendAiModelDeployReq createBackendReq = CreateBackendAiModelDeployReq.builder()
                .name(deployRes.getName())
                .description(deployRes.getDescription())
                .modelId(deployRes.getModelId())
                .runtime(deployRes.getRuntime())
                .runtimeImage(deployRes.getRuntimeImage())
                .servingMode(deployRes.getServingMode() != null ? deployRes.getServingMode() : "SINGLE_NODE")
                .servingParams(servingParams)
                .cpuRequest(deployRes.getCpuRequest() != null ? deployRes.getCpuRequest() : 1)
                .gpuRequest(deployRes.getGpuRequest() != null ? deployRes.getGpuRequest() : 1)
                .memRequest(deployRes.getMemRequest() != null ? deployRes.getMemRequest() : 1)
                .minReplicas(deployRes.getMinReplicas() != null ? deployRes.getMinReplicas() : 1)
                .safetyFilterInput(deployRes.getSafetyFilterInput() != null ? deployRes.getSafetyFilterInput() : false)
                .safetyFilterOutput(
                        deployRes.getSafetyFilterOutput() != null ? deployRes.getSafetyFilterOutput() : false)
                .dataMaskingInput(false)
                .dataMaskingOutput(false)
                .safetyFilterInputGroups(deployRes.getSafetyFilterInputGroups() != null
                        ? deployRes.getSafetyFilterInputGroups()
                        : java.util.Collections.emptyList())
                .safetyFilterOutputGroups(deployRes.getSafetyFilterOutputGroups() != null
                        ? deployRes.getSafetyFilterOutputGroups()
                        : java.util.Collections.emptyList())
                .resourceGroup(deployRes.getResourceGroup())
                .build();
        log.info("Self-hosting 모델 배포 생성 요청: {}", createBackendReq);

        CreateBackendAiModelDeployRes response = modelDeployService.createBackendAiModelDeploy(createBackendReq, projectId);
        log.info("Self-hosting 모델 배포 생성 완료 - servingId: {}, name: {}",
                response.getServingId(), response.getName());
        return response.getServingId();
    }

    @Override
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields,
            Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        String servingType = jsonNode.path("servingType").asText();
        log.info("MIGRATION :: ServingModel 필드 추출 시작 - servingType: {}", servingType);

        if("serverless".equalsIgnoreCase(servingType)) {
            log.info("MIGRATION :: serverless 모델은 제외");
            return result;
        } 
        log.info("MIGRATION :: ServingModel 필드 추출 시작 - id: {}, fields: {}, jsonNode: {}", id, fields, jsonNode);

        try { 
            for (String field : fields) {
                String fileValue = null;
                if (jsonNode.has(field) && !jsonNode.get(field).isNull()) {
                    fileValue = jsonNode.get(field).asText();
                }

                String dbValue = getValueFromDb.apply(field);

                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("dev", fileValue != null ? fileValue : "");
                fieldMap.put("prod", dbValue != null ? dbValue : "");
                result.put(field, fieldMap);
            }
        } catch (RuntimeException e) {
            log.error("MIGRATION :: ServingModel 필드 추출 실패 (RuntimeException) - id: {}, error: {}", id, e.getMessage(),
                    e);
        } catch (Exception e) {
            log.error("MIGRATION :: ServingModel 필드 추출 실패 - id: {}, error: {}", id, e.getMessage(), e);
        }
        log.info("MIGRATION :: ServingModel 필드 추출 완료 - id: {}, result: {}", id, result);
        return result;
    }
    
    /**
     * ServingModel 존재 여부 확인
     * 
     * @param servingId Serving ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String servingId) {
        try {
            sktaiServingService.getServing(servingId);
            return true;
        } catch (Exception e) {
            log.debug("ServingModel 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", servingId, e.getMessage());
            return false;
        }
    }
}
