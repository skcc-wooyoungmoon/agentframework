package com.skax.aiplatform.service.model.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.lablup.api.dto.response.GetEndpointResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetSessionLogResponse;
import com.skax.aiplatform.client.lablup.api.service.LablupSessionService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.client.sktai.resrcMgmt.ResrcMgmtClient;
import com.skax.aiplatform.client.sktai.resrcMgmt.ResrcMgmtGpuClient;
import com.skax.aiplatform.client.sktai.serving.dto.request.BackendAiServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.response.BackendAiServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.CreateServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingUpdateResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingsResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.admin.request.ResrcMgmtQueryEnum;
import com.skax.aiplatform.dto.common.response.AssetProjectInfoRes;
import com.skax.aiplatform.dto.deploy.request.CreateApiReq;
import com.skax.aiplatform.dto.deploy.response.CreateApiRes;
import com.skax.aiplatform.dto.model.request.CreateBackendAiModelDeployReq;
import com.skax.aiplatform.dto.model.request.CreateModelDeployReq;
import com.skax.aiplatform.dto.model.request.DeleteModelDeployReq;
import com.skax.aiplatform.dto.model.request.GetInferencePerformanceReq;
import com.skax.aiplatform.dto.model.request.GetModelDeployReq;
import com.skax.aiplatform.dto.model.request.PutModelDeployReq;
import com.skax.aiplatform.dto.model.response.CreateBackendAiModelDeployRes;
import com.skax.aiplatform.dto.model.response.CreateModelDeployRes;
import com.skax.aiplatform.dto.model.response.GetDockerImgUrlRes;
import com.skax.aiplatform.dto.model.response.GetInferencePerformanceRes;
import com.skax.aiplatform.dto.model.response.GetModelDeployRes;
import com.skax.aiplatform.dto.model.response.ModelDeployResourceInfo;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.model.GpoDockerImgUrlMas;
import com.skax.aiplatform.mapper.model.ModelDeployMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.repository.model.GpoDockerImgUrlMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.common.MigService;
import com.skax.aiplatform.service.common.ProjectInfoService;
import com.skax.aiplatform.service.deploy.ApiGwService;
import com.skax.aiplatform.service.model.ModelDeployService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor

public class ModelDeployServiceImpl implements ModelDeployService {

    private final SktaiServingService sktaiServingService;
    private final LablupSessionService lablupSessionService;
    private final SktaiLineageService sktaiLineageService;
    private final ModelDeployMapper modelDeployMapper;

    private final ApiGwService apiGwService;
    private final AdminAuthService adminAuthService;
    private final SktaiAuthService sktaiAuthService;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final GpoDockerImgUrlMasRepository dockerImgUrlMasRepository;

    private final ResrcMgmtClient resrcMgmtClient;
    private final ResrcMgmtGpuClient resrcMgmtGpuClient;
    private final ProjectInfoService projectInfoService;
    @Lazy
    @Autowired
    private MigService migService;

    @Override
    public PageResponse<GetModelDeployRes> getModelDeploy(GetModelDeployReq request) {
        log.info("모델 배포 목록 조회 요청: {}", request);

        List<String> deployModelNames = request.getDeployModelNames();

        String filter = StringUtils.hasText(request.getFilter()) ? request.getFilter() : null;
        String search = StringUtils.hasText(request.getSearch()) ? request.getSearch() : null;

        // 모델 배포 목록 조회
        ServingsResponse servings = sktaiServingService.getServings(request.getPage() + 1, request.getSize(),
                request.getSort(), filter, search);
        log.info("모델 배포 목록 조회 성공: {}", servings);


        // 모델 배포 목록 변환 
        List<GetModelDeployRes> response = servings.getData().stream()
                .map(serving -> modelDeployMapper.toGetModelListDeployRes(serving)).collect(Collectors.toList());

        for (GetModelDeployRes item : response) {
            /////////////////////////////// 공개범위 조회 ///////////////////////////////
            GpoAssetPrjMapMas existing =
                    assetPrjMapMasRepository.findByAsstUrl("/api/v1/servings/" + item.getServingId()).orElse(null);
            String publicStatus = "전체공유";
            if (existing != null && existing.getLstPrjSeq() != null && existing.getLstPrjSeq() > 0) {
                publicStatus = "내부공유";
            }
            item.setPublicStatus(publicStatus);

            /////////////////////////////// 마이그레이션 여부 설정 ///////////////////////////////
            boolean isActive = migService.isActive(item.getServingId());
            log.info("마이그레이션 여부 조회 성공: servingId={}, isActive={}", item.getServingId(), isActive);
            item.setProduction(isActive);

            /////////////////////////////// 가드레일 적용 여부 설정 ///////////////////////////////
            String servingName = item.getName();
            String guardrailApplied;

            if (CollectionUtils.isEmpty(deployModelNames)) {
                deployModelNames = List.of();
            }

            // 현재 서빙이 내 가드레일에 연결되어 있는지 확인
            boolean isLinkedToMyGuardrail = deployModelNames.contains(servingName);

            if (isLinkedToMyGuardrail) {
                // Case 1: 내 가드레일에 적용된 경우
                guardrailApplied = "적용중";
                log.debug("가드레일 적용 상태 - servingName: {}, status: 적용중 (내 가드레일)", servingName);
            } else {
                // Case 2 & 3: 내 가드레일에 적용되지 않은 경우, 다른 가드레일 연결 여부 확인
                boolean isLinkedToOtherGuardrail = false;

                try {
                    List<LineageRelationWithTypes> lineageRelation =
                            sktaiLineageService.getLineageByObjectKeyAndDirection(
                                    servingName,
                                    Direction.DOWNSTREAM,
                                    ActionType.USE.getValue(),
                                    5
                            );

                    if (!CollectionUtils.isEmpty(lineageRelation)) {
                        isLinkedToOtherGuardrail = lineageRelation.stream().anyMatch(relation ->
                                ObjectType.SERVING_MODEL.equals(relation.getSourceType())
                                        && ObjectType.GUARDRAILS.equals(relation.getTargetType()));
                    }
                } catch (Exception e) {
                    log.warn("가드레일 리니지 조회 실패 - servingName: {}, error: {}", servingName, e.getMessage());
                    // 예외 발생 시 isLinkedToOtherGuardrail는 그대로 false 유지
                }

                if (isLinkedToOtherGuardrail) {
                    // Case 2: 다른 가드레일에 적용된 경우
                    guardrailApplied = "적용 불가";
                    log.debug("가드레일 적용 상태 - servingName: {}, status: 적용 불가 (다른 가드레일)", servingName);
                } else {
                    // Case 3: 어떤 가드레일도 적용되지 않은 경우
                    guardrailApplied = "미적용";
                    log.debug("가드레일 적용 상태 - servingName: {}, status: 미적용", servingName);
                }
            }

            item.setGuardrailApplied(guardrailApplied);
        }

        // ADXP Pagination을 PageResponse로 변환
        return PaginationUtils.toPageResponseFromAdxp(servings.getPayload(), response);
    }

    @Override
    public GetModelDeployRes getModelDeployById(String id) {
        log.info("모델 배포 상세 조회 요청: {}", id);

        /////////////////////////////// 모델 배포 상세 조회 ///////////////////////////////
        ServingResponse serving = sktaiServingService.getServing(id);
        GetModelDeployRes response = modelDeployMapper.toGetModelDetailDeployRes(serving);
        log.info("모델 배포 상세 조회 성공: {}", response);

        /////////////////////////////// 마이그레이션 여부 설정 ///////////////////////////////
        boolean isActive = migService.isActive(response.getServingId());
        log.info("마이그레이션 여부 조회 성공: servingId={}, isActive={}", serving.getServingId(), isActive);
        response.setProduction(isActive);

        return response;
    }

    @Override
    public void changeModelDeployStatus(String servingType, String id, String status) {
        log.info("모델 배포 상태 변경 요청: {}, {}", id, status);

        if (status.equals("start")) {
            if (servingType.equals("serverless")) {
                sktaiServingService.startServing(id);
            } else if (servingType.equals("self_hosting")) {
                sktaiServingService.startBackendAiServing(id);
            } else {
                throw new BusinessException(ErrorCode.INVALID_MODEL_DEPLOY_STATUS, "서빙 타입이 올바르지 않습니다.");
            }
        } else if (status.equals("stop")) {
            if (servingType.equals("serverless")) {
                sktaiServingService.stopServing(id);
            } else if (servingType.equals("self_hosting")) {
                sktaiServingService.stopBackendAiServing(id);
            } else {
                throw new BusinessException(ErrorCode.INVALID_MODEL_DEPLOY_STATUS, "서빙 타입이 올바르지 않습니다.");
            }
        } else {
            throw new BusinessException(ErrorCode.INVALID_MODEL_DEPLOY_STATUS, "시작 혹은 중지로만 상태 변경이 가능합니다.");
        }
    }

    @Override
    public void deleteModelDeployBulk(List<DeleteModelDeployReq> deleteRequests) {
        log.info("모델 배포 삭제 요청: {}", deleteRequests);

        for (DeleteModelDeployReq deleteRequest : deleteRequests) {
            log.info("모델 배포 삭제 중: servingId={}, servingType={}",
                    deleteRequest.getServingId(), deleteRequest.getServingType());

            if (deleteRequest.getServingType().equals("self_hosting")) {
                sktaiServingService.deleteBackendAiServing(deleteRequest.getServingId());
            } else {
                sktaiServingService.deleteServing(deleteRequest.getServingId());
            }

            // API 엔드포인트 삭제 try catch 처리
            log.info("API 엔드포인트 삭제 시도: servingId={}", deleteRequest.getServingId());
            apiGwService.deleteApiEndpoint("model", deleteRequest.getServingId());
        }

        log.info("모델 배포 삭제 성공: {}", deleteRequests);
    }

    private void setModelDeployResourcePolicy(String servingId, Long projectId) {
        if (projectId != null) {
            try {
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/servings/" + servingId, projectId);
            } catch (NumberFormatException e) {
                log.warn("projectId 파싱 실패, 현재 그룹 기준으로 정책 설정: servingId={}, projectId={}, error={}", 
                        servingId, projectId, e.getMessage());
                adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/servings/" + servingId);
            }
        } else {
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/servings/" + servingId);
        }
    }

    @Override
    public CreateModelDeployRes createModelDeploy(CreateModelDeployReq request, Long projectId) {
        log.info("모델 배포 생성 요청: projectId={}, request={}", projectId, request);
        // 모델 배포 생성
        ServingCreate servingCreate = modelDeployMapper.toServingCreate(request);
        CreateServingResponse response = sktaiServingService.createServing(servingCreate);
        setModelDeployResourcePolicy(response.getServingId(), projectId);
        log.info("모델 배포 생성 성공: servingId={}, servingName={}", response.getServingId(), response.getName());

        // API 엔드포인트 생성
        CreateApiReq createApiReq = CreateApiReq.builder()
                .type("model")
                .uuid(response.getServingId())
                .name(response.getName())
                .description(response.getDescription()).build();

        CreateApiRes createApiRes = apiGwService.createApiEndpoint(createApiReq);
        log.info("API 엔드포인트 생성 성공: infWorkSeq={}", createApiRes.getInfWorkSeq());

        // CreateServingResponse를 CreateModelDeployRes로 변환
        CreateModelDeployRes createModelDeployRes = convertToCreateModelDeployRes(response);
        return createModelDeployRes;
    }

    /**
     * CreateServingResponse를 CreateModelDeployRes로 변환
     *
     * @param response CreateServingResponse
     * @return CreateModelDeployRes
     */
    private CreateModelDeployRes convertToCreateModelDeployRes(CreateServingResponse response) {
        return CreateModelDeployRes.builder().servingId(response.getServingId()).name(response.getName())
                .description(response.getDescription()).kserveYaml(response.getKserveYaml())
                .isvcName(response.getIsvcName()).projectId(response.getProjectId()).namespace(response.getNamespace())
                .status(response.getStatus()).modelId(response.getModelId()).versionId(response.getVersionId())
                .servingParams(convertServingParams(response.getServingParams()))
                .errorMessage(response.getErrorMessage()).cpuRequest(response.getCpuRequest())
                .cpuLimit(response.getCpuLimit()).gpuRequest(response.getGpuRequest()).gpuLimit(response.getGpuLimit())
                .memRequest(response.getMemRequest()).memLimit(response.getMemLimit())
                .createdBy(response.getCreatedBy()).updatedBy(response.getUpdatedBy())
                .createdAt(response.getCreatedAt()).updatedAt(response.getUpdatedAt())
                .isDeleted(response.getIsDeleted()).safetyFilterInput(response.getSafetyFilterInput())
                .safetyFilterOutput(response.getSafetyFilterOutput()).dataMaskingInput(response.getDataMaskingInput())
                .dataMaskingOutput(response.getDataMaskingOutput()).minReplicas(response.getMinReplicas())
                .maxReplicas(response.getMaxReplicas()).autoscalingClass(response.getAutoscalingClass())
                .autoscalingMetric(response.getAutoscalingMetric()).target(response.getTarget())
                .modelName(response.getModelName()).displayName(response.getDisplayName())
                .modelDescription(response.getModelDescription()).type(response.getType())
                .servingType(response.getServingType()).isPrivate(response.getIsPrivate())
                .isValid(response.getIsValid()).inferenceParam(response.getInferenceParam())
                .quantization(response.getQuantization()).providerName(response.getProviderName())
                .modelVersion(response.getModelVersion()).path(response.getPath())
                .versionPath(response.getVersionPath()).fineTuningId(response.getFineTuningId())
                .versionIsValid(response.getVersionIsValid()).versionIsDeleted(response.getVersionIsDeleted())
                .gpuType(response.getGpuType()).isCustom(response.getIsCustom()).servingMode(response.getServingMode())
                .servingOperator(response.getServingOperator()).build();
    }

    /**
     * CreateServingResponse.ServingParams를 CreateModelDeployRes.ServingParams로 변환
     *
     * @param servingParams CreateServingResponse.ServingParams
     * @return CreateModelDeployRes.ServingParams
     */
    private CreateModelDeployRes.ServingParams convertServingParams(CreateServingResponse.ServingParams servingParams) {
        if (servingParams == null) {
            return null;
        }

        return CreateModelDeployRes.ServingParams.builder()
                .inflightQuantization(servingParams.getInflightQuantization())
                .quantization(servingParams.getQuantization()).dtype(servingParams.getDtype())
                .gpuMemoryUtilization(servingParams.getGpuMemoryUtilization()).loadFormat(servingParams.getLoadFormat())
                .tensorParallelSize(servingParams.getTensorParallelSize()).cpuOffloadGb(servingParams.getCpuOffloadGb())
                .enforceEager(servingParams.getEnforceEager()).maxModelLen(servingParams.getMaxModelLen())
                .vllmUseV1(servingParams.getVllmUseV1()).maxNumSeqs(servingParams.getMaxNumSeqs())
                .limitMmPerPrompt(servingParams.getLimitMmPerPrompt()).tokenizerMode(servingParams.getTokenizerMode())
                .configFormat(servingParams.getConfigFormat()).trustRemoteCode(servingParams.getTrustRemoteCode())
                .hfOverrides(servingParams.getHfOverrides()).mmProcessorKwargs(servingParams.getMmProcessorKwargs())
                .disableMmPreprocessorCache(servingParams.getDisableMmPreprocessorCache())
                .enableAutoToolChoice(servingParams.getEnableAutoToolChoice())
                .toolCallParser(servingParams.getToolCallParser()).toolParserPlugin(servingParams.getToolParserPlugin())
                .chatTemplate(servingParams.getChatTemplate())
                .guidedDecodingBackend(servingParams.getGuidedDecodingBackend())
                .enableReasoning(servingParams.getEnableReasoning()).reasoningParser(servingParams.getReasoningParser())
                .device(servingParams.getDevice()).shmSize(servingParams.getShmSize())
                .customServing(convertCustomServing(servingParams.getCustomServing())).build();
    }

    /**
     * CreateServingResponse.CustomServing을 CreateModelDeployRes.CustomServing으로 변환
     *
     * @param customServing CreateServingResponse.CustomServing
     * @return CreateModelDeployRes.CustomServing
     */
    private CreateModelDeployRes.CustomServing convertCustomServing(CreateServingResponse.CustomServing customServing) {
        if (customServing == null) {
            return null;
        }

        return CreateModelDeployRes.CustomServing.builder().imageUrl(customServing.getImageUrl())
                .useBash(customServing.getUseBash()).command(customServing.getCommand()).args(customServing.getArgs())
                .build();
    }

    @Override
    public void updateModelDeploy(String id, PutModelDeployReq request) {
        log.info("모델 배포 수정 요청: {}, {}", id, request);

        ServingUpdate servingUpdate = modelDeployMapper.toServingUpdate(request);
        log.info("변환 전: {}", request.getServingParams());
        log.info("변환 후: {}", servingUpdate.getServingParams());
        ServingUpdateResponse response = sktaiServingService.updateServing(id, servingUpdate);

        log.info("모델 배포 수정 성공: servingId={}, servingName={}", response.getServingId(), response.getName());
    }

    @Override
    public void updateBackendAiModelDeploy(String id, PutModelDeployReq request) {
        log.info("모델 배포 수정 요청: {}, {}", id, request);

        ServingUpdate servingUpdate = modelDeployMapper.toServingUpdate(request);
        log.info("변환 전: {}", request.getServingParams());
        log.info("변환 후: {}", servingUpdate.getServingParams());
        ServingUpdateResponse response = sktaiServingService.updateBackendAiServing(id, servingUpdate);

        log.info("모델 배포 수정 성공: servingId={}, servingName={}", response.getServingId(), response.getName());
    }

    @Override
    public GetSessionLogResponse getSystemLogById(String id) {
        log.info("모델 배포 시스템 로그 조회 요청: {}", id);
        GetEndpointResponse endpointInfo = getEndpointInfoById(id);
        log.info("모델 배포 엔드포인트 정보: {}", endpointInfo);
        if (endpointInfo == null || endpointInfo.getEndpoint() == null) {
            log.warn("모델 배포 엔드포인트 정보를 찾을 수 없음: {}", id);
            return GetSessionLogResponse.builder()
                    .result(GetSessionLogResponse.Result.builder()
                            .logs("")
                            .build())
                    .build();
        }
        String sessionId = endpointInfo.getEndpoint().getRoutings().get(0).getSession();
        try {
            return lablupSessionService.getSessionLog(sessionId, null, null);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.EXTERNAL_API_NOT_FOUND) {
                log.warn("모델 배포 시스템 로그를 찾을 수 없음: {}, 빈 로그 응답 반환", id);
                return GetSessionLogResponse.builder()
                        .result(GetSessionLogResponse.Result.builder()
                                .logs("")
                                .build())
                        .build();
            }
            throw e;
        }
    }

    @Override
    public GetEndpointResponse getEndpointInfoById(String id) {
        log.info("모델 배포 엔드포인트 정보 조회 요청: {}", id);
        GetEndpointResponse response = lablupSessionService.getEndpoint(id);
        log.info("모델 배포 엔드포인트 정보 조회 성공: {}", response);
        return response;
    }

    @Override
    public CreateBackendAiModelDeployRes createBackendAiModelDeploy(CreateBackendAiModelDeployReq request, Long projectId) {
        log.info("Backend.AI 모델 배포 생성 요청: {}", request);
        BackendAiServingCreate backendAiServingCreate = modelDeployMapper.toBackendAiServingCreate(request);

        BackendAiServingResponse response = sktaiServingService.createBackendAiServing(backendAiServingCreate);
        setModelDeployResourcePolicy(response.getServingId(), projectId);
        log.info("Backend.AI 모델 배포 생성 성공: {}", response);

        // API 엔드포인트 생성
        CreateApiReq createApiReq = CreateApiReq.builder()
                .type("model")
                .uuid(response.getServingId())
                .name(response.getName())
                .description(response.getDescription()).build();
        CreateApiRes createApiRes = apiGwService.createApiEndpoint(createApiReq);
        log.info("API 엔드포인트 생성 성공: infWorkSeq={}", createApiRes.getInfWorkSeq());

        // BackendAiServingResponse를 CreateBackendAiModelDeployRes로 변환
        CreateBackendAiModelDeployRes createBackendAiModelDeployRes = convertToCreateBackendAiModelDeployRes(
                response);
        return createBackendAiModelDeployRes;

    }

    /**
     * BackendAiServingResponse를 CreateBackendAiModelDeployRes로 변환
     *
     * @param response BackendAiServingResponse
     * @return CreateBackendAiModelDeployRes
     */
    private CreateBackendAiModelDeployRes convertToCreateBackendAiModelDeployRes(BackendAiServingResponse response) {
        return CreateBackendAiModelDeployRes.builder().servingId(response.getServingId()).name(response.getName())
                .description(response.getDescription()).isvcName(response.getIsvcName())
                .projectId(response.getProjectId()).status(response.getStatus()).modelId(response.getModelId())
                .versionId(response.getVersionId()).runtime(response.getRuntime())
                .runtimeImage(response.getRuntimeImage()).servingMode(response.getServingMode())
                .servingOperator(response.getServingOperator()).servingParams(response.getServingParams())
                .cpuRequest(response.getCpuRequest()).gpuRequest(response.getGpuRequest())
                .memRequest(response.getMemRequest()).minReplicas(response.getMinReplicas())
                .safetyFilterInput(response.getSafetyFilterInput()).safetyFilterOutput(response.getSafetyFilterOutput())
                .dataMaskingInput(response.getDataMaskingInput()).dataMaskingOutput(response.getDataMaskingOutput())
                .safetyFilterInputGroups(response.getSafetyFilterInputGroups())
                .safetyFilterOutputGroups(response.getSafetyFilterOutputGroups()).createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt()).createdBy(response.getCreatedBy())
                .updatedBy(response.getUpdatedBy()).isDeleted(response.getIsDeleted()).build();
    }

    @Override
    public GetInferencePerformanceRes getInferencePerformance(GetInferencePerformanceReq request) {
        log.info("추론 성능 조회 요청: request={}",
                request);

        /////////////////// 모델 이름 조회
        String deployName = "";

        if(request.getServingId() != null && !request.getServingId().isEmpty()) {
             /////////////////// 서빙 ID로 모델 배포 정보 조회
            try{
                GetModelDeployRes modelDeploy = getModelDeployById(request.getServingId());
                deployName = modelDeploy.getName();
            } catch (BusinessException e) {
                throw new BusinessException(ErrorCode.INVALID_MODEL_DEPLOY_INFO,
                        "모델 배포 정보를 찾을 수 없습니다: " + request.getServingId());
            }
        } else if(request.getModelName() != null && !request.getModelName().isEmpty()){
            // 이름으로 조회
            deployName = request.getModelName();
        } else {
            throw new BusinessException(ErrorCode.INVALID_MODEL_DEPLOY_INFO,
                    "서빙 ID 또는 모델 이름이 모두 없습니다.");
        }

        
        /////////////////// 추론 성능 조회
        //////// 조회 시간 포맷팅
        // 날짜 문자열을 LocalDateTime으로 파싱 (여러 형식 지원)
        // 입력된 시간은 한국 시간(KST)으로 간주하고, UTC로 변환하여 프로메테우스에 전달
        LocalDateTime startDateTime = parseDateTime(request.getStartDate());
        LocalDateTime endDateTime = parseDateTime(request.getEndDate());

        // KST(Asia/Seoul) 시간대로 해석하여 UTC로 변환
        // 사용자가 입력한 시간은 한국 시간이므로, KST → UTC 변환 필요
        ZoneId kstZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime startZoned = startDateTime.atZone(kstZone).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endZoned = endDateTime.atZone(kstZone).withZoneSameInstant(ZoneOffset.UTC);
        
        // 일자를 Unix Timestamp (초 단위)로 변환 (UTC) - time 파라미터에 사용
        // 소수점 포함 형식으로 변환 (나노초까지 포함)
        double startTimestamp = startZoned.toEpochSecond() + startZoned.getNano() / 1_000_000_000.0;
        double endTimestamp = endZoned.toEpochSecond() + endZoned.getNano() / 1_000_000_000.0;

        // 문자열로 변확
        String startDateStr = String.valueOf(startTimestamp);
        String endDateStr = String.valueOf(endTimestamp);
        
        // step 계산: (end - start) / 250 (Prometheus 권장 샘플 수)
        String stepStr = "3600";

        log.info("배포 이름: deployName={}", deployName);
        log.info("=== 시간 비교 ===");
        log.info("요청 시작 시간 (LocalDateTime): {}", startDateTime);
        log.info("요청 종료 시간 (LocalDateTime): {}", endDateTime);
        log.info("프로메테우스 조회 시작 시간 (Unix Timestamp): startDateStr={}", startDateStr);
        log.info("프로메테우스 조회 종료 시간 (Unix Timestamp): endDateStr={}", endDateStr);
        log.info("step: stepStr={}", stepStr);

        //////// 쿼리 실행
        // 프로메테우스 쿼리: TTFT rate 기반 쿼리 (Average 값)
        String ttftQuery = String.format(
                "rate(vllm:time_to_first_token_seconds_sum{model_name=\"%s\"}[1h]) / rate(vllm:time_to_first_token_seconds_count{model_name=\"%s\"}[1h])",
                deployName, deployName);
        log.info("프로메테우스 TTFT 쿼리 실행: query={}", ttftQuery);

        // 프로메테우스 쿼리 실행 (TTFT) - time 파라미터에 종료일자 사용
        Object ttftResponse = null;
        try {
            ttftResponse = resrcMgmtGpuClient.executeQueryRange(ttftQuery, startDateStr, endDateStr, stepStr);
            // ttftResponse = resrcMgmtGpuClient.executeQuery(ttftQuery);
        } catch (FeignException.BadRequest e) {
            // 400 에러: 잘못된 쿼리 파라미터 (예: step=0s)
            log.warn("프로메테우스 TTFT 쿼리 실패 (400 Bad Request): model_name={}, error={}",
                    deployName, e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_MODEL_QUERY_ERROR,
                    "TTFT: 잘못된 쿼리 파라미터입니다. 시작일시와 종료일시가 동일하면 안됩니다. " + e.contentUTF8());
        } catch (FeignException.ServiceUnavailable e) {
            // 503 에러: 프로메테우스 서버 일시적 사용 불가 또는 해당 모델 데이터 없음
            log.warn("프로메테우스 TTFT 쿼리 실패 (503 Service Unavailable): model_name={}, 데이터가 없을 수 있습니다. error={}",
                    deployName, e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_MODEL_QUERY_ERROR,
                    "TTFT: 해당 모델의 데이터가 없거나 프로메테우스 서버가 일시적으로 사용 불가능합니다." + e.getMessage());
        }
        log.info("프로메테우스 TTFT 쿼리 실행 결과 ttftResponse: {}", ttftResponse);

        // 프로메테우스 쿼리: TPOT rate 기반 쿼리 (Mean 값)
        String tpotQuery = String.format(
                "rate(vllm:time_per_output_token_seconds_sum{model_name=\"%s\"}[1h]) / rate(vllm:time_per_output_token_seconds_count{model_name=\"%s\"}[1h])",
                deployName, deployName);
        log.info("프로메테우스 TPOT 쿼리 실행: query={}", tpotQuery);

        // 프로메테우스 쿼리 실행 (TPOT) - time 파라미터에 종료일자 사용
        Object tpotResponse = null;
        try {
            tpotResponse = resrcMgmtGpuClient.executeQueryRange(tpotQuery, startDateStr, endDateStr, stepStr);
            // tpotResponse = resrcMgmtGpuClient.executeQuery(tpotQuery);
        } catch (FeignException.BadRequest e) {
            // 400 에러: 잘못된 쿼리 파라미터 (예: step=0s)
            log.warn("프로메테우스 TPOT 쿼리 실패 (400 Bad Request): model_name={}, error={}",
                    deployName, e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_MODEL_QUERY_ERROR,
                    "TPOT: 잘못된 쿼리 파라미터입니다. 시작일시와 종료일시가 동일하면 안됩니다. " + e.contentUTF8());
        } catch (FeignException.ServiceUnavailable e) {
            // 503 에러: 프로메테우스 서버 일시적 사용 불가 또는 해당 모델 데이터 없음
            log.warn("프로메테우스 TPOT 쿼리 실패 (503 Service Unavailable): model_name={}, 데이터가 없을 수 있습니다. error={}",
                    deployName, e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_MODEL_QUERY_ERROR,
                    "TPOT: 해당 모델의 데이터가 없거나 프로메테우스 서버가 일시적으로 사용 불가능합니다.");
        }
        log.info("프로메테우스 TPOT 쿼리 실행 결과 tpotResponse: {}", tpotResponse);

        // 프로메테우스 쿼리: E2E Request Latency rate 기반 쿼리 (이미지 형식과 동일)
        String e2eQuery = String.format(
                "rate(vllm:e2e_request_latency_seconds_sum{model_name=\"%s\"}[1h]) / rate(vllm:e2e_request_latency_seconds_count{model_name=\"%s\"}[1h])",
                deployName, deployName);
        log.info("프로메테우스 E2E Latency 쿼리 실행: query={}", e2eQuery);

        // 프로메테우스 쿼리 실행 (E2E Latency) - 이미지 형식과 동일하게 step을 숫자로 전달
        Object e2eResponse = null;
        try {
            e2eResponse = resrcMgmtGpuClient.executeQueryRange(e2eQuery, startDateStr, endDateStr, stepStr);
            // e2eResponse = resrcMgmtGpuClient.executeQuery(e2eQuery);
        } catch (FeignException.BadRequest e) {
            // 400 에러: 잘못된 쿼리 파라미터 (예: step=0s)
            log.warn("프로메테우스 E2E Latency 쿼리 실패 (400 Bad Request): model_name={}, error={}",
                    deployName, e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_MODEL_QUERY_ERROR,
                    "E2E Latency: 잘못된 쿼리 파라미터입니다. 시작일시와 종료일시가 동일하면 안됩니다. " + e.contentUTF8());
        } catch (FeignException.ServiceUnavailable e) {
            // 503 에러: 프로메테우스 서버 일시적 사용 불가 또는 해당 모델 데이터 없음
            log.warn("프로메테우스 E2E Latency 쿼리 실패 (503 Service Unavailable): model_name={}, 데이터가 없을 수 있습니다. error={}",
                    deployName, e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_MODEL_QUERY_ERROR,
                    "E2E Latency: 해당 모델의 데이터가 없거나 프로메테우스 서버가 일시적으로 사용 불가능합니다.");
        }
        log.info("프로메테우스 E2E Latency 쿼리 실행 결과 e2eResponse: {}", e2eResponse);

        // 응답 파싱 및 시계열 데이터 추출
        List<GetInferencePerformanceRes.TimeSeriesData> ttftTimeSeries = parseTimeSeriesResponse(ttftResponse);
        List<GetInferencePerformanceRes.TimeSeriesData> tpotTimeSeries = parseTimeSeriesResponse(tpotResponse);
        List<GetInferencePerformanceRes.TimeSeriesData> e2eTimeSeries = parseTimeSeriesResponse(e2eResponse);

        // 응답 생성
        GetInferencePerformanceRes result = GetInferencePerformanceRes.builder()
                .servingId(request.getServingId())
                .timeToFirstToken(GetInferencePerformanceRes.TimeToFirstTokenTimeSeries.builder()
                        .timeSeries(ttftTimeSeries)
                        .build())
                .timePerOutputToken(GetInferencePerformanceRes.TimePerOutputTokenTimeSeries.builder()
                        .timeSeries(tpotTimeSeries)
                        .build())
                .endToEndLatency(GetInferencePerformanceRes.EndToEndLatencyTimeSeries.builder()
                        .timeSeries(e2eTimeSeries)
                        .build())
                .build();

        log.info("추론 성능 조회 성공: servingId={}, ttftTimeSeries size={}, tpotTimeSeries size={}, e2eTimeSeries size={}",
                request.getServingId(), ttftTimeSeries.size(), tpotTimeSeries.size(), e2eTimeSeries.size());
        return result;
    }

    /**
     * 프로메테우스 rate 쿼리 응답을 파싱하여 시계열 데이터를 추출
     *
     * @param response 프로메테우스 응답 (query_range 응답은 matrix 형식)
     * @return 시계열 데이터 리스트 (timestamp와 value)
     */
    @SuppressWarnings("unchecked")
    private List<GetInferencePerformanceRes.TimeSeriesData> parseTimeSeriesResponse(Object response) {
        log.info("프로메테우스 시계열 응답 파싱 시작: response={}", response);
        List<GetInferencePerformanceRes.TimeSeriesData> timeSeriesList = new java.util.ArrayList<>();

        if (response == null) {
            log.warn("프로메테우스 응답이 null입니다.");
            return timeSeriesList;
        }

        Map<String, Object> responseMap = (Map<String, Object>) response;
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

        if (data == null) {
            log.warn("프로메테우스 응답에 data가 없습니다.");
            return timeSeriesList;
        }

        String resultType = (String) data.get("resultType");
        if (resultType == null) {
            log.warn("프로메테우스 응답에 resultType이 없습니다.");
            return timeSeriesList;
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("result");
        if (results == null || results.isEmpty()) {
            log.warn("프로메테우스 응답에 결과가 없습니다.");
            return timeSeriesList;
        }

        // query_range 응답은 matrix 형식 (시계열)
        if ("matrix".equals(resultType)) {
            // 모든 시리즈의 데이터를 하나로 합침 (보통 하나의 시리즈만 반환됨)
            Map<Long, Double> timestampValueMap = new TreeMap<>();
            
            for (Map<String, Object> result : results) {
                List<List<Object>> values = (List<List<Object>>) result.get("values");
                if (values == null || values.isEmpty()) {
                    continue;
                }

                // 각 시점의 데이터 추출
                for (List<Object> valuePair : values) {
                    if (valuePair == null || valuePair.size() < 2) {
                        continue;
                    }

                    // timestamp 추출 (Unix timestamp, 초 단위)
                    Long timestamp = null;
                    Object timestampObj = valuePair.get(0);
                    if (timestampObj instanceof Number) {
                        timestamp = ((Number) timestampObj).longValue();
                    } else if (timestampObj instanceof String) {
                        try {
                            // 문자열이 소수점을 포함할 수 있으므로 double로 파싱 후 long으로 변환
                            timestamp = (long) Double.parseDouble((String) timestampObj);
                        } catch (NumberFormatException e) {
                            log.warn("timestamp 파싱 실패: {}", timestampObj);
                            continue;
                        }
                    }

                    // value 추출 (평균값)
                    Double value = null;
                    Object valueObj = valuePair.get(1);
                    if (valueObj instanceof Number) {
                        value = ((Number) valueObj).doubleValue();
                    } else if (valueObj instanceof String) {
                        try {
                            value = Double.parseDouble((String) valueObj);
                        } catch (NumberFormatException e) {
                            log.warn("value 파싱 실패: {}", valueObj);
                            continue;
                        }
                    }

                    // NaN 또는 Infinite 값은 0.0으로 변환
                    if (value != null && (value.isNaN() || value.isInfinite())) {
                        log.debug("NaN 또는 Infinite 값 감지, 0.0으로 변환: timestamp={}, value={}", timestamp, value);
                        value = 0.0;
                    }

                    if (timestamp != null && value != null) {
                        // 동일한 timestamp가 여러 시리즈에 있으면 평균값 사용
                        timestampValueMap.merge(timestamp, value, (v1, v2) -> (v1 + v2) / 2.0);
                    }
                }
            }

            // 시계열 데이터 리스트 생성 (timestamp 순으로 정렬됨)
            for (Map.Entry<Long, Double> entry : timestampValueMap.entrySet()) {
                Double value = entry.getValue();
                // 최종 결과에서도 NaN 또는 Infinite 값은 0.0으로 변환
                if (value != null && (value.isNaN() || value.isInfinite())) {
                    value = 0.0;
                }
                
                GetInferencePerformanceRes.TimeSeriesData timeSeriesData = 
                        GetInferencePerformanceRes.TimeSeriesData.builder()
                        .timestamp(entry.getKey())
                        .value(value)
                        .build();
                timeSeriesList.add(timeSeriesData);
            }
        }
        // query 응답은 vector 형식 (단일 시점) - 시계열이 아니므로 빈 리스트 반환
        else if ("vector".equals(resultType)) {
            log.warn("vector 형식 응답은 시계열 데이터로 변환할 수 없습니다.");
        }

        log.debug("시계열 데이터 추출 완료: {}개 데이터 포인트", timeSeriesList.size());
        return timeSeriesList;
    }

    /**
     * 날짜 시간 문자열을 LocalDateTime으로 파싱
     * 여러 형식을 지원합니다
     *
     * @param dateTimeStr 날짜 시간 문자열
     * @return LocalDateTime 객체
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "날짜 시간 문자열이 비어있습니다.");
        }

        String trimmed = dateTimeStr.trim();
        // 1. yyyyMMddHHmm 형식 시도 (예: 202511151900)
        if (trimmed.length() == 12 && trimmed.matches("\\d{12}")) {
            try {
                return LocalDateTime.parse(trimmed, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            } catch (DateTimeParseException e) {
                // 다음 형식 시도
            }
        }

        // 2. ISO_OFFSET_DATE_TIME 형식 시도 (예: 2025-11-15T19:00:00+09:00)
        try {
            java.time.OffsetDateTime offsetDateTime = java.time.OffsetDateTime.parse(trimmed);
            return offsetDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            // 다음 형식 시도
        }

        // 3. ISO_LOCAL_DATE_TIME 형식 시도 (예: 2025-11-15T19:00:00)
        try {
            return LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // 다음 형식 시도
        }

        // 4. Instant 형식 시도 (예: 2025-11-15T10:00:00Z)
        try {
            java.time.Instant instant = java.time.Instant.parse(trimmed);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            // 모든 형식 실패
        }

        throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT,
                "지원하지 않는 날짜 형식입니다: " + dateTimeStr +
                        ". 지원 형식: yyyyMMddHHmm (예: 202511151900), ISO 형식 (예: 2025-11-15T19:00:00)");
    }


    private ModelDeployResourceInfo getSessionResourceBySessionId(String sessionId, String modelName,
            String servingId, String status, GetEndpointResponse.Endpoint endpoint) {
        log.debug("세션 자원 데이터 조회 시작 - sessionId: {}, modelName: {}", sessionId, modelName);

        // endpoint에서 resourceSlots, replicas 추출
        Map<String, Object> resourceSlots = endpoint != null ? endpoint.getResourceSlots() : null;
        Integer replicas = endpoint != null ? endpoint.getReplicas() : null;

        // 기본값 설정 (GPU Prometheus 서버 접근 불가 시 사용)
        Double cpuUsage = 0.0;
        Double cpuCore = 0.0;
        Double cpuRequest = 0.0;
        Double cpuUtilization = 0.0;
        Double memoryUsage = 0.0;
        Double memoryRequest = 0.0;
        Double gpuUsage = 0.0;
        Double gpuUtilization = 0.0;

        // 1. CPU 사용량 조회 (Core 단위)
        String cpuUsageQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_CPU_USAGE.getQuery(), sessionId);
        Object cpuUsageResponse = resrcMgmtGpuClient.executeQuery(cpuUsageQuery);
        cpuUsage = extractNumericValue(cpuUsageResponse);

        // 1-1. CPU 요청량 조회 (Core 단위)
        String cpuCoreQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_CPU_REQUESTS.getQuery(), sessionId);
        Object cpuCoreResponse = resrcMgmtGpuClient.executeQuery(cpuCoreQuery);
        cpuCore = extractNumericValue(cpuCoreResponse);

        // 2. CPU 사용률 조회 (%)
        if (cpuCore != null && cpuCore > 0) {
            cpuUtilization = cpuUsage / cpuCore * 100.0;
        } else {
            cpuUtilization = 0.0;
        }

        // 3. Memory 사용량 조회 (GiB 단위)
        String memoryUsageQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_MEMORY_USAGE.getQuery(), sessionId);
        Object memoryUsageResponse = resrcMgmtGpuClient.executeQuery(memoryUsageQuery);
        Double memoryUsageGiB = extractNumericValue(memoryUsageResponse);
        memoryUsage = memoryUsageGiB != null ? memoryUsageGiB : 0.0;

        // 3-1. Memory 요청량 조회 (GiB 단위)
        String memoryRequestQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_MEMORY_REQUESTS.getQuery(),
                sessionId);
        Object memoryRequestResponse = resrcMgmtGpuClient.executeQuery(memoryRequestQuery);
        Double memoryRequestGiB = extractNumericValue(memoryRequestResponse);
        memoryRequest = memoryRequestGiB != null ? memoryRequestGiB : 0.0;

        // 4. GPU 사용량 조회 (메모리 관련부분이라 사용하지 않음)
        String gpuUsageQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_GPU_USAGE.getQuery(), sessionId);
        Object gpuUsageResponse = resrcMgmtGpuClient.executeQuery(gpuUsageQuery);
        gpuUsage = extractNumericValue(gpuUsageResponse);

        // 5. GPU 사용률 조회 (모니터링용;)
        String gpuUtilQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_GPU_UTILIZATION.getQuery(), sessionId);
        Object gpuUtilResponse = resrcMgmtGpuClient.executeQuery(gpuUtilQuery);
        gpuUtilization = extractNumericValue(gpuUtilResponse);


        // 6. 자원 할당량과 제한량 설정 (resourceSlots 우선)
        // 6-1. resourceSlots에서 cpu, cuda.shares 값 추출
        Double cpuFromSlots = null;
        Double gpuFromSlots = null;
        Double memFromSlots = null;
        if (resourceSlots != null) {
            // cpu 값 추출
            Object cpuValue = resourceSlots.get("cpu");
            if (cpuValue != null) {
                try {
                    cpuFromSlots = Double.parseDouble(cpuValue.toString());
                    log.debug("resourceSlots에서 CPU 값 추출 - cpu: {}", cpuFromSlots);
                } catch (NumberFormatException e) {
                    log.warn("resourceSlots CPU 값 파싱 실패 - value: {}", cpuValue);
                }
            }
            // cuda.shares 값 추출 (GPU)
            Object cudaSharesValue = resourceSlots.get("cuda.shares");
            if (cudaSharesValue != null) {
                try {
                    gpuFromSlots = Double.parseDouble(cudaSharesValue.toString());
                    log.debug("resourceSlots에서 GPU(cuda.shares) 값 추출 - cuda.shares: {}", gpuFromSlots);
                } catch (NumberFormatException e) {
                    log.warn("resourceSlots cuda.shares 값 파싱 실패 - value: {}", cudaSharesValue);
                }
            }
            // mem 값 추출 (바이트 단위 -> GiB 변환)
            Object memValue = resourceSlots.get("mem");
            if (memValue != null) {
                try {
                    Double memBytes = Double.parseDouble(memValue.toString());
                    memFromSlots = memBytes / (1024.0 * 1024.0 * 1024.0); // 바이트 -> GiB
                    log.debug("resourceSlots에서 Memory 값 추출 - mem: {} bytes -> {} GiB", memBytes, memFromSlots);
                } catch (NumberFormatException e) {
                    log.warn("resourceSlots mem 값 파싱 실패 - value: {}", memValue);
                }
            }
        }

        // 6-2. cpuRequest 설정 (우선순위: Prometheus > resourceSlots)
        cpuRequest = (cpuFromSlots != null && cpuFromSlots > 0.0) ? cpuFromSlots : 0.0;
        // Double cpuLimit = modelDeploy.getCpuLimit() != null ? modelDeploy.getCpuLimit().doubleValue() : 0.0;

        // 6-3. memoryRequest 설정 (우선순위: Prometheus > resourceSlots)
        if (memoryRequest == null || memoryRequest == 0.0) {
            memoryRequest = (memFromSlots != null && memFromSlots > 0.0) ? memFromSlots : 0.0;
        }
        // Double memoryLimit = modelDeploy.getMemLimit() != null ? modelDeploy.getMemLimit().doubleValue() : 0.0;

        // 6-4. gpuRequest 설정 (resourceSlots에서만 가져옴)
        Double gpuRequest = (gpuFromSlots != null && gpuFromSlots > 0.0) ? gpuFromSlots : 0.0;
        // Double gpuLimit = modelDeploy.getGpuLimit() != null ? modelDeploy.getGpuLimit().doubleValue() : 0.0;

        // 7. GPU 사용량을 사용률 기반으로 계산: 사용량 = 요청량 × (사용률/100)
        if (gpuRequest != null && gpuUtilization != null) {
            gpuUsage = gpuRequest * (gpuUtilization / 100.0);
        }

        ModelDeployResourceInfo sessionResource = ModelDeployResourceInfo.builder()
                .sessionId(sessionId)
                .modelName(modelName)
                .servingId(servingId)
                .status(status)
                .replicas(replicas)
                .cpuUsage(cpuUsage)
                .cpuUtilization(cpuUtilization)
                .cpuRequest(cpuRequest)
                .memoryUsage(memoryUsage)
                .memoryUtilization(null)
                .memoryRequest(memoryRequest)
                .gpuUsage(gpuUsage)
                .gpuUtilization(gpuUtilization)
                .gpuRequest(gpuRequest)
                .build();

        log.debug("세션 자원 데이터 조회 완료 - sessionId: {}, cpuUsage: {}, memoryUsage: {}, gpuUsage: {}",
                sessionId, cpuUsage, memoryUsage, gpuUsage);

        return sessionResource;
    }

    /**
     * Prometheus 응답에서 숫자 값 추출
     *
     * @param response Prometheus 응답
     * @return 숫자 값
     */
    @SuppressWarnings("unchecked")
    private Double extractNumericValue(Object response) {
        if (response == null) {
            return 0.0;
        }

        // Prometheus 응답 구조에 따라 값 추출
        if (response instanceof Map) {
            Map<String, Object> responseMap = (Map<String, Object>) response;
            Object data = responseMap.get("data");

            if (data instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) data;
                Object result = dataMap.get("result");

                if (result instanceof List) {
                    List<?> resultList = (List<?>) result;
                    if (!resultList.isEmpty()) {
                        Object firstResult = resultList.get(0);
                        if (firstResult instanceof Map) {
                            Map<String, Object> firstResultMap = (Map<String, Object>) firstResult;
                            Object value = firstResultMap.get("value");

                            if (value instanceof List) {
                                List<?> valueList = (List<?>) value;
                                if (valueList.size() >= 2) {
                                    Object numericValue = valueList.get(1);
                                    if (numericValue instanceof Number) {
                                        return ((Number) numericValue).doubleValue();
                                    } else if (numericValue instanceof String) {
                                        return Double.parseDouble((String) numericValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0.0;
    }

    @Override
    public ModelDeployResourceInfo getModelDeployResourceInfo(String servingId) {
        log.info("모델 배포 자원 현황 조회 요청: servingId={}", servingId);

        try {
            // 1. servingId로 모델 배포 정보 조회
            GetModelDeployRes modelDeploy = getModelDeployById(servingId);
            if (modelDeploy == null) {
                throw new BusinessException(ErrorCode.EXTERNAL_API_NOT_FOUND,
                        "모델 배포 정보를 찾을 수 없습니다: " + servingId);
            }

            // 2. endpoint 정보 조회하여 sessionId 얻기
            String sessionId = null;
            GetEndpointResponse.Endpoint endpoint = null;
            try {
                GetEndpointResponse endpointInfo = getEndpointInfoById(servingId);
                if (endpointInfo != null && endpointInfo.getEndpoint() != null) {
                    endpoint = endpointInfo.getEndpoint();
                    List<GetEndpointResponse.Routing> routings = endpoint.getRoutings();
                    if (routings != null && !routings.isEmpty()) {
                        sessionId = routings.get(0).getSession();
                        log.info("Endpoint Session 정보 - servingId: {}, sessionId: {}", servingId, sessionId);
                    }
                    log.info("Endpoint 정보 - servingId: {}, replicas: {}, resourceSlots: {}", 
                            servingId, endpoint.getReplicas(), endpoint.getResourceSlots());
                }
            } catch (Exception e) {
                log.warn("Endpoint 정보 조회 실패 - servingId: {}, error: {}", servingId, e.getMessage());
            }

            // 3. sessionId가 없으면 자원 정보를 조회할 수 없음
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("SessionId가 없어 자원 정보를 조회할 수 없습니다 - servingId: {}", servingId);
                // sessionId가 없어도 기본 정보는 반환
                return createEmptyResourceInfo(servingId, modelDeploy);
            }

            // 4. sessionId로 자원 데이터 조회
            ModelDeployResourceInfo sessionResource = getSessionResourceBySessionId(
                    sessionId,
                    modelDeploy.getName(),
                    servingId,
                    modelDeploy.getStatus(),
                    endpoint
            );

            // 5. 프로젝트명 조회 및 설정
            if (servingId != null && !servingId.isBlank()) {
                try {
                    AssetProjectInfoRes assetInfo = projectInfoService.getAssetProjectInfoByUuid(servingId);
                    if (assetInfo != null && assetInfo.getLstPrjNm() != null && !assetInfo.getLstPrjNm().isBlank()) {
                        sessionResource.setProjectName(assetInfo.getLstPrjNm());
                    }
                } catch (Exception ex) {
                    log.debug("모델 프로젝트 정보 조회 실패 - servingId: {}, error: {}", servingId, ex.getMessage());
                }
            }

            log.info("모델 배포 자원 현황 조회 성공: servingId={}, sessionId={}", servingId, sessionId);
            return sessionResource;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("모델 배포 자원 현황 조회 실패: servingId={}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 배포 자원 현황 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SessionId가 없을 때 빈 자원 정보 생성
     *
     * @param servingId   서빙 ID
     * @param modelDeploy 모델 배포 정보
     * @return 빈 자원 정보
     */
    private ModelDeployResourceInfo createEmptyResourceInfo(String servingId, GetModelDeployRes modelDeploy) {
        String projectName = null;
        if (servingId != null && !servingId.isBlank()) {
            try {
                AssetProjectInfoRes assetInfo = projectInfoService.getAssetProjectInfoByUuid(servingId);
                if (assetInfo != null && assetInfo.getLstPrjNm() != null && !assetInfo.getLstPrjNm().isBlank()) {
                    projectName = assetInfo.getLstPrjNm();
                }
            } catch (Exception ex) {
                log.debug("모델 프로젝트 정보 조회 실패 - servingId: {}, error: {}", servingId, ex.getMessage());
            }
        }

        return ModelDeployResourceInfo.builder()
                .sessionId(null)
                .modelName(modelDeploy.getName())
                .servingId(servingId)
                .status(modelDeploy.getStatus())
                .projectName(projectName)
                .cpuUsage(0.0)
                .cpuUtilization(0.0)
                .cpuRequest(0.0)
                .memoryUsage(0.0)
                .memoryUtilization(null)
                .memoryRequest(0.0)
                .gpuUsage(0.0)
                .gpuUtilization(0.0)
                .gpuRequest(0.0)
                .build();
    }

    @Override
    @Transactional
    public List<PolicyRequest> setModelDeployPolicy(String servingId, String memberId, String projectName) {
        log.info("모델 배포 Policy 설정 요청 - servingId: {}, memberId: {}, projectName: {}", servingId, memberId, projectName);

        // servingId 검증
        if (!StringUtils.hasText(servingId)) {
            log.error("모델 배포 Policy 설정 실패 - servingId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "서빙 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("모델 배포 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("모델 배포 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // Policy 설정
            String resourceUrl = "/api/v1/servings/" + servingId;
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(resourceUrl, memberId, projectName);

            log.info("모델 배포 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("모델 배포 Policy 조회 결과가 null - servingId: {}, resourceUrl: {}", servingId, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 배포 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
            }

            // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
            List<PolicyRequest> filteredPolicy = policy.stream()
                    .filter(policyReq -> {
                        if (policyReq.getPolicies() != null) {
                            // policies에 type이 "role"인 항목이 있는지 확인
                            return policyReq.getPolicies().stream()
                                    .noneMatch(p -> "role".equals(p.getType()));
                        }
                        return true; // policies가 null이면 포함
                    })
                    .collect(Collectors.toList());

            log.info("모델 배포 Policy 설정 완료 - servingId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", servingId, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("모델 배포 Policy 설정 실패 (BusinessException) - servingId: {}, errorCode: {}", servingId, e.getErrorCode(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("모델 배포 Policy 설정 실패 (RuntimeException) - servingId: {}, error: {}", servingId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 배포 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("모델 배포 Policy 설정 실패 (Exception) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 배포 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<GetDockerImgUrlRes> getDockerImgUrlBySysUV(String sysUV) {
        log.info("도커 이미지 URL 조회 요청: sysUV={}", sysUV);

        if (!StringUtils.hasText(sysUV)) {
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "SYS_U_V 값은 필수입니다.");
        }

        List<GpoDockerImgUrlMas> entities =
                dockerImgUrlMasRepository.findBySysUVAndDelYnIsZeroOrNull(sysUV);

        List<GetDockerImgUrlRes> response = entities.stream()
                .map(entity -> GetDockerImgUrlRes.builder()
                        .seqNo(entity.getSeqNo())
                        .sysUV(entity.getSysUV())
                        .imgUrl(entity.getImgUrl())
                        .delYn(entity.getDelYn())
                        .fstCreatedAt(entity.getFstCreatedAt())
                        .createdBy(entity.getCreatedBy())
                        .lstUpdatedAt(entity.getLstUpdatedAt())
                        .updatedBy(entity.getUpdatedBy())
                        .build())
                .collect(Collectors.toList());

        log.info("도커 이미지 URL 조회 성공: sysUV={}, count={}", sysUV, response.size());
        return response;
    }

}
