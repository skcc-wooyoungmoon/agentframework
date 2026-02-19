package com.skax.aiplatform.service.model.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyItem;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelEndpointCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelTagRequest;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelEndpointRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelEndpointsRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelProvidersRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelsRead;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelProvidersService;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.model.request.CreateModelCtlgReq;
import com.skax.aiplatform.dto.model.request.DeleteModelCtlgBulkReq;
import com.skax.aiplatform.dto.model.request.DeleteModelCtlgBulkReq.DeleteModelCtlgBulkItem;
import com.skax.aiplatform.dto.model.request.GetModelCtlgReq;
import com.skax.aiplatform.dto.model.request.GetUpdateModelCtlgReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.dto.model.response.DeleteModelCtlgBulkRes;
import com.skax.aiplatform.dto.model.response.GetModelCtlgRes;
import com.skax.aiplatform.dto.model.response.GetModelPrvdRes;
import com.skax.aiplatform.dto.model.response.GetModelTagsRes;
import com.skax.aiplatform.dto.model.response.GetModelTypesRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.model.GpoModelMngMas;
import com.skax.aiplatform.enums.ModelGardenStatus;
import com.skax.aiplatform.mapper.model.ModelCtlgMapper;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.deploy.GpoMigMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.repository.model.GpoModelMngMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.model.ModelCtlgService;
import com.skax.aiplatform.service.model.ModelGardenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModelCtlgServiceImpl implements ModelCtlgService {
    private final SktaiModelsService sktaiModelService;
    private final SktaiModelProvidersService sktaiModelProvidersService;
    private final AdminAuthService adminAuthService;
    private final ModelCtlgMapper modelCtlgMapper;
    private final ModelGardenService modelGardenService;
    private final SktaiLineageService sktaiLineageService;
    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final GpoModelMngMasRepository gpoModelMngMasRepository;
    private final SktaiAuthService sktaiAuthService;
    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final GpoMigMasRepository gpoMigMasRepository;

    public ModelCtlgServiceImpl(SktaiModelsService sktaiModelService,
            SktaiModelProvidersService sktaiModelProvidersService, AdminAuthService adminAuthService,
            ModelCtlgMapper modelCtlgMapper, @Lazy ModelGardenService modelGardenService,
            SktaiLineageService sktaiLineageService,
            GpoAssetPrjMapMasRepository assetPrjMapMasRepository, GpoModelMngMasRepository gpoModelMngMasRepository,
            SktaiAuthService sktaiAuthService,
            GpoUsersMasRepository gpoUsersMasRepository, GpoMigMasRepository gpoMigMasRepository) {
        this.sktaiModelService = sktaiModelService;
        this.sktaiModelProvidersService = sktaiModelProvidersService;
        this.adminAuthService = adminAuthService;
        this.modelCtlgMapper = modelCtlgMapper;
        this.modelGardenService = modelGardenService;
        this.sktaiLineageService = sktaiLineageService;
        this.assetPrjMapMasRepository = assetPrjMapMasRepository;
        this.gpoModelMngMasRepository = gpoModelMngMasRepository;
        this.sktaiAuthService = sktaiAuthService;
        this.gpoUsersMasRepository = gpoUsersMasRepository;
        this.gpoMigMasRepository = gpoMigMasRepository;
    }

    @Override
    public PageResponse<GetModelCtlgRes> getModelCtlg(GetModelCtlgReq request) {
        log.info("모델 카탈로그 목록 조회 요청: {}", request);

        String filter = StringUtils.hasText(request.getFilter()) ? request.getFilter() : null;
        String search = StringUtils.hasText(request.getSearch()) ? request.getSearch() : null;

        // 모델 카탈로그 목록 조회
        ModelsRead models = sktaiModelService.readModels(request.getPage() + 1, request.getSize(), request.getSort(),
                filter, search, request.getIds());
        log.info("========== 모델 카탈로그 목록 조회 성공: {}", models);

        // 모델 카탈로그 목록 변환
        List<GetModelCtlgRes> response = models.getData().stream()
                .map(model -> modelCtlgMapper.toGetModelCtlgRes(model, null)).collect(Collectors.toList());

        // 각 모델에 대해 Lineage 조회
        for (GetModelCtlgRes modelCtlg : response) {
            try {
                // 파인튜닝으로 생성된 모델관리 확인 (내부에서 모든 예외를 처리하므로 예외가 전파되지 않음)
                processFinetuningModelMapping(modelCtlg);

                // 공개범위 조회
                GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                        .findByAsstUrl("/api/v1/models/" + modelCtlg.getId()).orElse(null);
                String publicStatus = "전체공유";
                if (existing != null && existing.getLstPrjSeq() != null && existing.getLstPrjSeq() > 0) {
                    publicStatus = "내부공유";
                }
                modelCtlg.setPublicStatus(publicStatus);

                // 모델 ID로 Lineage 조회 (upstream 방향으로 - 모델에 들어오는 관계)
                List<LineageRelationWithTypes> lineageRelations = sktaiLineageService.getLineageByObjectKeyAndDirection(
                        modelCtlg.getId(), Direction.UPSTREAM, ActionType.USE.getValue(), 1);
                log.debug("모델 {} Lineage 조회 완료: {}건", modelCtlg.getId(),
                        lineageRelations != null ? lineageRelations.size() : 0);
                log.debug("모델 {} Lineage 정보: {}", modelCtlg.getId(), lineageRelations);

                /////////////////////////////// 배포 상태 설정 ///////////////////////////////
                String deployStatus = "";
                ///// 개발 배포 여부
                // source_type이 SERVING_MODEL인 것이 하나라도 있으면 deployStatus를 DEV로 설정
                if (lineageRelations != null && !lineageRelations.isEmpty()) {
                    boolean hasServingModel = lineageRelations.stream()
                            .anyMatch(relation -> ObjectType.SERVING_MODEL.equals(relation.getSourceType()));
                    if (hasServingModel) {
                        deployStatus += "DEV";
                        log.debug("모델 {} deployStatus를 DEV로 설정 (SERVING_MODEL Lineage 발견): {}", modelCtlg.getId(),
                                modelCtlg.getDeployStatus());
                    }
                }
                ///// 운영 배포 여부
                boolean isActive = gpoMigMasRepository.findByPgmDescCtnt(modelCtlg.getId()).isPresent();
                log.info("마이그레이션 여부 조회 성공: modelId={}, isActive={}", modelCtlg.getId(), isActive);
                if (isActive) {
                    deployStatus += ", PROD";
                }
                modelCtlg.setDeployStatus(deployStatus);
            } catch (BusinessException e) {
                log.warn("모델 {} Lineage 조회 실패 (BusinessException) - errorCode: {}", modelCtlg.getId(), e.getErrorCode(),
                        e);
                // Lineage 조회 실패 시에도 모델 정보는 정상 반환
            } catch (RuntimeException e) {
                log.warn("모델 {} Lineage 조회 실패 (RuntimeException): {}", modelCtlg.getId(), e.getMessage(), e);
                // Lineage 조회 실패 시에도 모델 정보는 정상 반환
            }
        }

        // ADXP Pagination을 PageResponse로 변환
        return PaginationUtils.toPageResponseFromAdxp(models.getPayload(), response);
    }

    @Override
    public GetModelCtlgRes createModelCtlg(CreateModelCtlgReq request) {
        log.info("모델 카탈로그 생성 요청: {}", request);

        // SKTAI 모델 생성 요청 객체 생성
        ModelCreate sktRequest = modelCtlgMapper.toModelCreate(request);

        // 모델 카탈로그 생성
        ModelRead response = sktaiModelService.registerModel(sktRequest);
        log.info("========== 모델 카탈로그 생성 성공: {}", response);

        // 권한 설정
        adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/models/" + response.getId());

        if (request.getServingType().equals("serverless")) {
            Long modelGardenId = request.getModelGardenId();
            if (modelGardenId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "serverless 모델은 모델가든 ID가 필요합니다.");
            }

            GpoModelMngMas modelMngMas = GpoModelMngMas.builder()
                    .useGnynModelSeqNo(modelGardenId) // 모델 가든 id
                    .modelMngId(response.getId()) // 생성된 모델 카탈로그 id
                    .build();
            gpoModelMngMasRepository.save(modelMngMas);
            log.info("========== 모델 관리 정보 생성 성공: {}", modelMngMas);
        }

        // 엔드포인트가 있는 경우 엔드포인트 등록
        if (request.getEndpoint() != null) {
            log.info("모델 엔드포인트 등록 시작 - modelId: {}, identifier: {}", response.getId(),
                    request.getEndpoint().getIdentifier());

            try {
                ModelEndpointRead endpointResponse = sktaiModelService.registerModelEndpoint(response.getId(),
                        request.getEndpoint());
                adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/models/" + response.getId() + "/endpoints");
                adminAuthService.setResourcePolicyByCurrentGroup(
                        "/api/v1/models/" + response.getId() + "/endpoints/" + endpointResponse.getId());
                log.info("모델 엔드포인트 등록 성공 - modelId: {}, endpointId: {}, identifier: {}", response.getId(),
                        endpointResponse.getId(), endpointResponse.getIdentifier());
            } catch (BusinessException e) {
                log.error("모델 엔드포인트 등록 실패 (BusinessException) - modelId: {}, identifier: {}, errorCode: {}",
                        response.getId(), request.getEndpoint().getIdentifier(), e.getErrorCode(), e);
                // 엔드포인트 등록 실패 시에도 모델 생성은 성공으로 처리하고 계속 진행
            } catch (RuntimeException e) {
                log.error("모델 엔드포인트 등록 실패 (RuntimeException) - modelId: {}, identifier: {}", response.getId(),
                        request.getEndpoint().getIdentifier(), e);
                // 엔드포인트 등록 실패 시에도 모델 생성은 성공으로 처리하고 계속 진행
            }
        }

        // 모델 카탈로그 생성 변환
        return modelCtlgMapper.toGetModelCtlgRes(response, null);
    }

    @Override
    public GetModelCtlgRes getModelCtlgById(String id) {
        log.info("모델 상세 조회 요청: {}", id);

        // 모델 상세 조회
        ModelRead response = sktaiModelService.readModel(id);
        log.info("========== 모델 상세 조회 성공: {}", response);

        GetModelCtlgRes modelCtlgRes = modelCtlgMapper.toGetModelCtlgRes(response, null);
        // 파인튜닝 모델 매핑 처리 (내부에서 모든 예외를 처리하므로 예외가 전파되지 않음)
        processFinetuningModelMapping(modelCtlgRes);

        // Lineage 조회 및 deployStatus 설정
        try {
            // 모델 ID로 Lineage 조회 (upstream 방향으로 - 모델에 들어오는 관계)
            List<LineageRelationWithTypes> lineageRelations = sktaiLineageService.getLineageByObjectKeyAndDirection(id,
                    Direction.UPSTREAM, ActionType.USE.getValue(), 1);
            log.debug("모델 {} Lineage 조회 완료: {}건", id, lineageRelations != null ? lineageRelations.size() : 0);
            log.debug("모델 {} Lineage 정보: {}", id, lineageRelations);

            // source_type이 SERVING_MODEL인 것이 하나라도 있으면 deployStatus를 DEV로 설정
            if (lineageRelations != null && !lineageRelations.isEmpty()) {
                boolean hasServingModel = lineageRelations.stream()
                        .anyMatch(relation -> ObjectType.SERVING_MODEL.equals(relation.getSourceType()));
                if (hasServingModel) {
                    modelCtlgRes.setDeployStatus("DEV");
                    log.debug("모델 {} deployStatus를 DEV로 설정 (SERVING_MODEL Lineage 발견): {}", id,
                            modelCtlgRes.getDeployStatus());
                }
            }
        } catch (BusinessException e) {
            log.warn("모델 {} Lineage 조회 실패 (BusinessException) - errorCode: {}", id, e.getErrorCode(), e);
            // Lineage 조회 실패 시에도 모델 정보는 정상 반환
        } catch (RuntimeException e) {
            log.warn("모델 {} Lineage 조회 실패 (RuntimeException): {}", id, e.getMessage(), e);
            // Lineage 조회 실패 시에도 모델 정보는 정상 반환
        }

        try {
            ModelEndpointsRead endpoints = sktaiModelService.readModelEndpoints(id, 1, 1, null, null, null);
            List<ModelEndpointRead> data = endpoints.getData();

            // 첫 번째 엔드포인트가 존재하는 경우에만 설정
            if (!data.isEmpty()) {
                ModelEndpointRead firstEndpoint = data.get(0);
                modelCtlgRes.setEndpointId(firstEndpoint.getId());
                modelCtlgRes.setUrl(firstEndpoint.getUrl());
                modelCtlgRes.setIdentifier(firstEndpoint.getIdentifier());
                modelCtlgRes.setKey(firstEndpoint.getKey());
            }
        } catch (BusinessException e) {
            log.warn("모델 엔드포인트 조회 실패 (BusinessException) - modelId: {}, errorCode: {}, 엔드포인트 정보 없이 진행", id,
                    e.getErrorCode(), e);
            // 엔드포인트 조회 실패 시에도 모델 정보는 정상 반환
        } catch (RuntimeException e) {
            log.warn("모델 엔드포인트 조회 실패 (RuntimeException) - modelId: {}, 엔드포인트 정보 없이 진행: {}", id, e.getMessage(), e);
            // 엔드포인트 조회 실패 시에도 모델 정보는 정상 반환
        }

        return modelCtlgRes;
    }

    @Override
    public GetModelCtlgRes updateModelCtlgById(String id, GetUpdateModelCtlgReq request) {
        log.info("모델 상세 정보 수정 요청: {}", id);

        // 0. 모델 현재 상세 정보 조회
        ModelRead currentModel = sktaiModelService.readModel(id);
        log.info("========== 모델 현재 상세 정보 조회 성공: {}", currentModel);

        // Request 변환
        ModelUpdate sktRequest = modelCtlgMapper.toModelUpdate(request);

        // 1. 모델 상세 정보 수정
        ModelRead response = sktaiModelService.editModel(id, sktRequest);
        log.info("========== 모델 상세 정보 수정 성공: {}", response);

        if ("serverless".equals(request.getServingType())) {
            sktaiModelService.removeTagsFromModel(id, request.getOriginTags().stream()
                    .map(tag -> ModelTagRequest.builder().name(tag.getName()).build()).toArray(ModelTagRequest[]::new));
            sktaiModelService.addTagsToModel(id, request.getTags().stream()
                    .map(tag -> ModelTagRequest.builder().name(tag.getName()).build()).toArray(ModelTagRequest[]::new));

            if (StringUtils.hasText(request.getEndpointId())) {
                sktaiModelService.removeModelEndpoint(id, request.getEndpointId());
            }

            try {
                ModelEndpointCreate endpoint = ModelEndpointCreate.builder().url(request.getUrl())
                        .identifier(request.getIdentifier()).key(request.getKey()).build();
                ModelEndpointRead endpointResponse = sktaiModelService.registerModelEndpoint(response.getId(),
                        endpoint);
                adminAuthService.setResourcePolicyByCurrentGroup(
                        "/api/v1/models/" + response.getId() + "/endpoints/" + endpointResponse.getId());
                log.info("모델 엔드포인트 등록 성공 - modelId: {}, endpointId: {}, identifier: {}", response.getId(),
                        endpointResponse.getId(), endpointResponse.getIdentifier());
            } catch (BusinessException e) {
                log.error("모델 엔드포인트 등록 실패 (BusinessException) - modelId: {}, identifier: {}, errorCode: {}",
                        response.getId(), request.getIdentifier(), e.getErrorCode(), e);
                // 엔드포인트 등록 실패 시에도 모델 수정은 성공으로 처리하고 계속 진행
            }
        }

        // 모델 상세 변환
        return modelCtlgMapper.toGetModelCtlgRes(response, null);
    }

    @Override
    public DeleteModelCtlgBulkRes deleteModelCtlgBulk(DeleteModelCtlgBulkReq request) {
        log.info("모델 상세 정보 삭제 요청: {}", request);

        DeleteModelCtlgBulkRes response = DeleteModelCtlgBulkRes.builder().totalCount(request.getItems().size())
                .successCount(0).failCount(0).build();

        // 모델 상세 정보 삭제
        try {
            for (DeleteModelCtlgBulkItem item : request.getItems()) {
                sktaiModelService.removeModel(item.getId());
                log.info("========== 모델 상세 정보 삭제 성공: {}", item.getId());
                response.setSuccessCount(response.getSuccessCount() + 1);
            }
        } catch (BusinessException e) {
            log.error("모델 상세 정보 삭제 실패 (BusinessException) - errorCode: {}", e.getErrorCode(), e);
            response.setFailCount(response.getFailCount() + 1);
            throw new BusinessException(ErrorCode.MODEL_CTLG_DELETE_FAILED, e.getMessage());
        }

        for (DeleteModelCtlgBulkItem item : request.getItems()) {
            if (item.getType().equals("self-hosting")) {
                // self_hosting의 경우 모델 가든 정보 변경 필요
                try {
                    modelGardenService.updateModelGarden(ModelGardenService.UPDATE_FIND_TYPE.MODEL_CTLG_ID,
                            item.getId(),
                            UpdateModelGardenReq.builder().statusNm(ModelGardenStatus.IMPORT_COMPLETED_UNREGISTERED.name())
                                    .build());
                } catch (Exception e) {
                    log.warn("모델 가든 정보를 찾을 수 없음: {} - {}", item.getId(), e.getMessage(), e);
                }
            } else {
                // serverless 삭제시 모델 관리 정보 삭제
                log.error("모델 관리 정보 삭제 시작: {}", item.getId());
                gpoModelMngMasRepository.findByModelMngId(item.getId())
                        .ifPresentOrElse(modelMngMas -> {
                            gpoModelMngMasRepository.delete(modelMngMas);
                            log.error("모델 관리 정보 삭제 성공: {}", item.getId());
                        }, () -> {
                            log.warn("모델 관리 정보를 찾을 수 없어 삭제할 수 없습니다: {}", item.getId());
                        });
                response.setSuccessCount(response.getSuccessCount() + 1);
            }
        }

        return response;
    }

    @Override
    public PageResponse<GetModelPrvdRes> getModelProviders() {
        log.info("모델 공급사 목록 조회 요청 : {}");

        // 모델 공급사 목록 조회
        ModelProvidersRead providers = sktaiModelProvidersService.readModelProviders(null, null, null, null, null);
        log.info("========== 모델 공급사 목록 조회 성공: {}", providers);

        // 모델 공급사 목록 변환
        List<GetModelPrvdRes> modelPrvdList = providers.getData().stream().map(modelCtlgMapper::toGetModelPrvdRes)
                .collect(Collectors.toList());

        // ADXP Pagination을 PageResponse로 변환
        return PaginationUtils.toPageResponseFromAdxp(providers.getPayload(), modelPrvdList);
    }

    @Override
    public GetModelTypesRes getModelTypes() {
        log.info("모델 타입 목록 조회 요청");

        // 모델 타입 목록을 가져오기
        List<String> sktaiTypesResponse = sktaiModelService.readModelTypes();

        log.info("========== SKTAI 모델 타입 목록 조회 성공: {}", sktaiTypesResponse);

        // types 리스트 생성
        GetModelTypesRes modelTypes = new GetModelTypesRes();
        modelTypes.setTypes(sktaiTypesResponse);

        return modelTypes;
    }

    @Override
    public GetModelTagsRes getModelTags() {
        log.info("모델 태그 목록 조회 요청");

        // 모델 태그 목록 가져오기
        List<String> sktaiTags = sktaiModelService.readModelTags();
        log.info("========== SKTAI 모델 태그 목록 조회 성공: {}", sktaiTags);

        GetModelTagsRes modelTagsRes = new GetModelTagsRes();
        modelTagsRes.setTags(sktaiTags);

        log.info("========== 최종: {}", modelTagsRes);
        return modelTagsRes;
    }

    /**
     * 파인튜닝으로 생성된 모델의 매핑 정보를 처리합니다.
     * Policy에서 프로젝트 정보를 추출하여 GpoAssetPrjMapMas를 생성합니다.
     *
     * @param modelCtlg 모델 카탈로그 정보
     */
    private void processFinetuningModelMapping(GetModelCtlgRes modelCtlg) {
        try {
            if (modelCtlg.getTrainingId() == null) {
                return;
            }

            GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/models/" + modelCtlg.getId())
                    .orElse(null);
            if (existing != null) {
                return;
            }

            String resourceUrl = "/api/v1/backend-ai/finetuning/trainings/" + modelCtlg.getTrainingId();

            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy에서 조건에 맞는 pattern 추출 및 P 뒤 숫자 추출
            if (policy == null || policy.isEmpty()) {
                return;
            }

            for (PolicyRequest item : policy) {
                // scopes에 "GET", "POST", "PUT", "DELETE"가 포함되어 있는지 확인
                if (item.getScopes() == null || !item.getScopes().contains("GET") || !item.getScopes().contains("POST")
                        || !item.getScopes().contains("PUT") || !item.getScopes().contains("DELETE")) {
                    continue;
                }

                // policies에서 type이 "regex", logic이 "POSITIVE", targetClaim이 "current_group"인 항목
                // 찾기
                if (item.getPolicies() == null) {
                    continue;
                }

                for (PolicyItem policyItem : item.getPolicies()) {
                    if (!"regex".equals(policyItem.getType()) || !"POSITIVE".equals(policyItem.getLogic())
                            || !"current_group".equals(policyItem.getTargetClaim())) {
                        continue;
                    }

                    String pattern = policyItem.getPattern();
                    if (pattern == null) {
                        continue;
                    }

                    // P 뒤에 오는 숫자 추출
                    // 케이스 1: "^/P\\-999_R\\-199$" (JSON) -> "^/P\-999_R\-199$" (Java String) ->
                    // "-999" 추출
                    // 케이스 2: "^/P211_R.+$" (JSON) -> "^/P211_R.+$" (Java String) -> "211" 추출
                    // 케이스 3: "^/P-999_R.+$" (이스케이프 없는 하이픈) -> "-999" 추출
                    // API에서 받은 JSON: "^/P\\-999_R\\-199$" (JSON에서 \\는 하나의 \)
                    // Jackson 역직렬화 후 Java String: "^/P\-999_R\-199$" (실제 문자열 값)
                    // 정규식에서 \-는 리터럴 하이픈이므로, P\-를 찾으려면 P\\- 패턴 사용
                    // Java 문자열 리터럴에서 \\\\-는 정규식 \\-가 되고, 이것은 리터럴 \-를 의미
                    // AdminAuthServiceImpl에서 음수일 때만 P\- 형태로 생성되므로, P\- 또는 P- 다음 숫자는 음수로 처리
                    Pattern numberPattern = Pattern.compile("P[\\\\-](-?\\d+)");
                    Matcher matcher = numberPattern.matcher(pattern);
                    boolean isNegative = false;

                    if (matcher.find()) {
                        // P\- 또는 P- 패턴으로 매칭 성공 (P\-999 또는 P-999 형태)
                        String extractedNumber = matcher.group(1);
                        // 추출된 숫자에 하이픈이 포함되어 있지 않으면 음수로 처리
                        // (하이픈이 포함되어 있으면 이미 음수로 파싱됨)
                        if (!extractedNumber.startsWith("-")) {
                            // P\- 또는 P- 다음 숫자는 음수로 처리
                            isNegative = true;
                        }
                    } else {
                        // P\- 또는 P- 패턴이 없으면 P 다음에 바로 숫자가 오는 경우 시도 (P211 형태)
                        numberPattern = Pattern.compile("P(-?\\d+)");
                        matcher = numberPattern.matcher(pattern);
                        if (!matcher.find()) {
                            log.warn("패턴에서 숫자를 추출할 수 없습니다. pattern: [{}]", pattern);
                            continue;
                        }
                    }

                    long projectSeq = Long.parseLong(matcher.group(1));
                    if (isNegative) {
                        projectSeq = -projectSeq;
                    }
                    log.info("추출된 숫자: {}", projectSeq);

                    String asstUrl = "/api/v1/models/" + modelCtlg.getId();

                    // GpoAssetPrjMapMas 생성
                    GpoAssetPrjMapMas mapping = GpoAssetPrjMapMas.builder().asstUrl(asstUrl)
                            .fstPrjSeq(Math.toIntExact(projectSeq)).lstPrjSeq(Math.toIntExact(projectSeq)).build();

                    // createdBy, updatedBy를 직접 설정 (JPA Auditing 우회)
                    String createdByMemberId = null;
                    String updatedByMemberId = null;

                    if (modelCtlg.getCreatedBy() != null) {
                        GpoUsersMas createdUser = gpoUsersMasRepository.findByUuid(modelCtlg.getCreatedBy())
                                .orElse(null);
                        if (createdUser != null) {
                            createdByMemberId = createdUser.getMemberId();
                        }
                    }

                    if (modelCtlg.getUpdatedBy() != null) {
                        GpoUsersMas updatedUser = gpoUsersMasRepository.findByUuid(modelCtlg.getUpdatedBy())
                                .orElse(null);
                        if (updatedUser != null) {
                            updatedByMemberId = updatedUser.getMemberId();
                        }
                    }

                    // Reflection을 사용하여 createdBy, updatedBy 필드 직접 설정
                    if (createdByMemberId != null) {
                        setFieldValue(mapping, "createdBy", createdByMemberId);
                    }
                    if (updatedByMemberId != null) {
                        setFieldValue(mapping, "updatedBy", updatedByMemberId);
                    }

                    assetPrjMapMasRepository.save(mapping);
                    return; // 첫 번째 매칭되는 항목만 처리하고 종료
                }
            }
        } catch (BusinessException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 비즈니스 예외 발생 (계속 진행): modelId={}, errorCode={}, message={}", modelCtlg.getId(),
                    e.getErrorCode(), e.getMessage());
        } 
    }

    /**
     * Reflection을 사용하여 엔티티의 필드 값을 설정합니다.
     * JPA Auditing을 우회하여 createdBy, updatedBy 등을 직접 설정할 때 사용합니다.
     *
     * @param entity    대상 엔티티 객체
     * @param fieldName 설정할 필드명
     * @param value     설정할 값
     */
    private void setFieldValue(Object entity, String fieldName, Object value) {
        try {
            Class<?> clazz = entity.getClass();
            // 상속 구조를 따라가며 필드 찾기 (AuditableEntity의 createdBy, updatedBy 필드)
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(entity, value);
                    return;
                } catch (NoSuchFieldException e) {
                    // 현재 클래스에 필드가 없으면 부모 클래스로 이동
                    clazz = clazz.getSuperclass();
                }
            }
            log.warn("필드를 찾을 수 없습니다: {}", fieldName);
        } catch (Exception e) {
            log.error("필드 설정 중 예상치 못한 오류 발생: fieldName={}, exceptionType={}, message={}", fieldName,
                    e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    @Override
    public void setModelPolicy(String id, String memberId, String projectName) {
        log.info("모델 Policy 설정 요청 - id: {}, memberId: {}, projectName: {}", id, memberId, projectName);

        // id 검증
        if (!StringUtils.hasText(id)) {
            log.error("모델 Policy 설정 실패 - id가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "모델 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("모델 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("모델 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            String resourceUrl = "/api/v1/models/" + id;
            String endpointsResourceUrl = "/api/v1/models/" + id + "/endpoints";

            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(resourceUrl, memberId, projectName);
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(endpointsResourceUrl, memberId, projectName);
            log.info("모델 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId,
                    projectName);

        } catch (BusinessException e) {
            log.error("모델 Policy 설정 실패 (BusinessException) - id: {}, errorCode: {}", id, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 Policy 설정에 실패했습니다: " + e.getMessage());
        } 
    }

    @Override
    public void setModelEndpointPolicy(String id, String endpointId, String memberId, String projectName) {
        log.info("모델 엔드포인트 Policy 설정 요청 - id: {}, endpointId: {}, memberId: {}, projectName: {}", id, endpointId,
                memberId, projectName);

        // id 검증
        if (!StringUtils.hasText(id)) {
            log.error("모델 엔드포인트 Policy 설정 실패 - id가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "모델 ID는 필수입니다");
        }

        // endpointId 검증
        if (!StringUtils.hasText(endpointId)) {
            log.error("모델 엔드포인트 Policy 설정 실패 - endpointId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "모델 엔드포인트 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("모델 엔드포인트 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("모델 엔드포인트 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            String resourceUrl = "/api/v1/models/" + id + "/endpoints/" + endpointId;

            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(resourceUrl, memberId, projectName);
            log.info("모델 엔드포인트 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId,
                    projectName);
        } catch (BusinessException e) {
            log.error("모델 엔드포인트 Policy 설정 실패 (BusinessException) - id: {}, endpointId: {}, errorCode: {}", id,
                    endpointId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 엔드포인트 Policy 설정에 실패했습니다: " + e.getMessage());
        } 
    }
}