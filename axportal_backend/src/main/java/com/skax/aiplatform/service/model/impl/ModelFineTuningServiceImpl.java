package com.skax.aiplatform.service.model.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingCreate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingStatusUpdate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingUpdate;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricsRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingStatusRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingsRead;
import com.skax.aiplatform.client.sktai.finetuning.service.SktaiFinetuningService;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.model.request.FineTuningEnum;
import com.skax.aiplatform.dto.model.request.ModelFineTuningCreateReq;
import com.skax.aiplatform.dto.model.request.ModelFineTuningUpdateReq;
import com.skax.aiplatform.dto.model.response.ModelFineTuningCreateRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningStatusRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingsRes;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.model.ModelFinetuningMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.model.ModelFinetuningService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelFineTuningServiceImpl implements ModelFinetuningService {

    private static final String ADMIN_USERNAME = "admin";

    private final SktaiFinetuningService sktaiFineTuningService;
    private final AdminAuthService adminAuthService;
    private final SktaiAuthService sktaiAuthService;
    private final ModelFinetuningMapper finetuningMapper;
    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;

    /**
     * 파인튜닝 모델 생성
     */
    @Override
    public ModelFineTuningCreateRes registFineTuningTraining(ModelFineTuningCreateReq registReq) {
        log.info("파인튜닝 트레이닝 생성 요청 - name: {}", registReq.getName());

        // 필수 필드 검증
        validateCreateRequest(registReq);

        // ModelFineTuningCreateReq를 TrainingCreate로 변환
        TrainingCreate trainingCreate = convertToTrainingCreate(registReq);

        try {
            TrainingRead sktaiResult = sktaiFineTuningService.createTraining(trainingCreate);
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/backend-ai/finetuning/trainings/" + sktaiResult.getId());

            ModelFineTuningCreateRes response = finetuningMapper.toCreateResponse(sktaiResult);
            log.info("파인튜닝 트레이닝 생성 성공 - id: {}", response.getId());
            return response;

        } catch (BusinessException e) {
            log.error("파인튜닝 트레이닝 생성 실패 (BusinessException) - errorCode: {}", e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 생성에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 트레이닝 생성 실패 (RuntimeException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파인튜닝 모델 목록 조회
     */
    @Override
    public PageResponse<ModelFineTuningTrainingsRes> getFineTuningTrainings(Pageable pageable, String sort, String filter, String search) {
        log.info("=== Fine-tuning Training List Query START ===");
        log.info("Request parameters - page: {}, size: {}, sort: {}, filter: {}, search: {}", pageable.getPageNumber(), pageable.getPageSize(), sort, filter, search);

        try {
            // SKTAI API는 1부터 시작하는 페이지 번호 사용
            int sktaiPage = pageable.getPageNumber();
            int sktaiSize = pageable.getPageSize();

            log.info("SKTAI API call parameters - page: {}, size: {}, sort: {}, filter: {}, search: {}", sktaiPage, sktaiSize, sort, filter, search);

            // SKTAI API 호출 전 상세 로깅
            log.info("=== SKTAI API 호출 전 상태 ===");
            log.info("Spring Pageable - page: {}, size: {}, sort: {}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
            log.info("SKTAI 변환 파라미터 - page: {}, size: {}", sktaiPage, sktaiSize);
            log.info("필터 조건 - filter: '{}', search: '{}'", filter != null ? filter : "null", search != null ? search : "null");
            log.info("정렬 조건 - sort: '{}'", sort != null ? sort : "null");

            TrainingsRead sktaiResult = sktaiFineTuningService.getTrainings(sktaiPage, sktaiSize, sort, filter, search);

            log.info("SKTAI API response received - response object: {}", sktaiResult);
            log.info("SKTAI API response data size: {}", sktaiResult.getData() != null ? sktaiResult.getData().size() : "null");
            log.info("SKTAI API response payload: {}", sktaiResult.getPayload() != null ? sktaiResult.getPayload() : "null");

            // SKTAI 응답 상세 분석
            log.info("=== SKTAI API 응답 상세 분석 ===");
            if (sktaiResult.getData() != null) {
                log.info("데이터 배열 존재 - 크기: {}", sktaiResult.getData().size());
                if (sktaiResult.getData().isEmpty()) {
                    log.warn("데이터 배열이 비어있음 - SKTAI 시스템에 해당 조건의 데이터가 없음");
                } else {
                    log.info("데이터 배열에 {}개 항목 존재", sktaiResult.getData().size());
                    // 첫 번째 항목의 기본 정보 로깅
                    var firstItem = sktaiResult.getData().get(0);
                    log.info("첫 번째 항목 정보 - ID: {}, Name: {}, Status: {}", firstItem.getId(), firstItem.getName(), firstItem.getStatus());
                }
            } else {
                log.error("데이터 배열이 null - SKTAI API 응답 구조 문제");
            }

            if (sktaiResult.getPayload() != null) {
                log.info("페이로드 존재");
                Pagination pagination = sktaiResult.getPayload().getPagination();
                if (pagination != null) {
                    log.info("페이지네이션 정보 - page: {}, total: {}, itemsPerPage: {}, lastPage: {}, from: {}, to: {}", pagination.getPage(), pagination.getTotal(), pagination.getItemsPerPage(), pagination.getLastPage(), pagination.getFrom(), pagination.getTo());

                    // 페이지네이션 상세 분석
                    if (pagination.getTotal() != null) {
                        if (pagination.getTotal() == 0) {
                            log.warn("전체 데이터 수가 0 - SKTAI 시스템에 파인튜닝 데이터가 전혀 없음");
                        } else if (pagination.getTotal() > 0) {
                            log.info("전체 데이터 수: {}개", pagination.getTotal());
                            if (pagination.getPage() > pagination.getLastPage()) {
                                log.warn("요청한 페이지({})가 마지막 페이지({})를 초과함", pagination.getPage(), pagination.getLastPage());
                            }
                        }
                    } else {
                        log.error("페이지네이션 total이 null - SKTAI API 응답 구조 문제");
                    }
                } else {
                    log.error("페이로드에 페이지네이션 정보가 없음 - SKTAI API 응답 구조 문제");
                }
            } else {
                log.error("페이로드가 null - SKTAI API 응답 구조 문제");
            }

            // 파인튜닝 목록을 응답 DTO로 변환
            List<ModelFineTuningTrainingsRes> content = sktaiResult.getData().stream().map(finetuningMapper::toResponse).collect(Collectors.toList());

            log.info("Converted fine-tuning list size: {}", content.size());

            // 데이터가 비어있는 경우 로깅
            if (content.isEmpty()) {
                log.warn("No fine-tuning data returned from SKTAI API. Possible reasons:");
                log.warn("1. No fine-tuning models created yet in the system");
                log.warn("2. Requested page has no data (page: {}, size: {})", sktaiPage, sktaiSize);
                log.warn("3. No data matches filter/search criteria (filter: {}, search: {})", filter, search);
                log.warn("4. SKTAI system data issue");
            } else {

                for (ModelFineTuningTrainingsRes item : content) {
                    // 공개 여부 설정 (lst_prj_seq 값에 따라)
                    GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/backend-ai/finetuning/trainings/" + item.getId()).orElse(null);
                    String publicStatus = "전체공유";
                    if (existing != null && existing.getLstPrjSeq() != null && existing.getLstPrjSeq() > 0) {
                        publicStatus = "내부공유";
                    }
                    item.setPublicStatus(publicStatus);
                }
            }

            // ADXP Pagination을 PageResponse로 변환
            PageResponse<ModelFineTuningTrainingsRes> response = PaginationUtils.toPageResponseFromAdxp(sktaiResult.getPayload(), content);

            for (int index = 0; index < content.size(); index++) {
                content.get(index).setNo(index + 1);
                switch (index % 3) {
                    case 0:
                        content.get(index).setTuningType(FineTuningEnum.tuningType.LoRa);
                        content.get(index).setModelType(FineTuningEnum.modelType.MT01);

                        break;
                    case 1:
                        content.get(index).setTuningType(FineTuningEnum.tuningType.Full);
                        content.get(index).setModelType(FineTuningEnum.modelType.MT02);
                        break;
                    case 2:
                        content.get(index).setTuningType(FineTuningEnum.tuningType.QLoRa);
                        content.get(index).setModelType(FineTuningEnum.modelType.MT03);
                        break;

                    default:
                        break;
                }

            }

            log.info("=== Fine-tuning Training List Query END ===");
            log.info("Final response - Total {} (all: {} items), page: {}/{}", content.size(), response.getTotalElements(), response.getPageable().getPage(), response.getPageable().getSize());

            return response;

        } catch (BusinessException e) {
            // 이미 처리된 비즈니스 예외는 그대로 전파
            log.error("파인튜닝 트레이닝 목록 조회 중 비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("파인튜닝 트레이닝 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 목록 조회에 실패했습니다: " + e.getMessage());
        }

    }

    /**
     * 파인튜닝 모델 상세 조회
     */
    @Override
    public ModelFineTuningTrainingRes getFineTuningTraining(String trainingId) {
        log.info("파인튜닝 트레이닝 상세 조회 요청 - datasetId: {}", trainingId);
        try {
            TrainingRead sktaiResult = sktaiFineTuningService.getTraining(trainingId);
            ModelFineTuningTrainingRes response = finetuningMapper.toDetailResponse(sktaiResult);

            log.info("파인튜닝 트레이닝 상세 조회 성공 - datasetId: {}", trainingId);
            return response;

        } catch (BusinessException e) {
            log.error("파인튜닝 트레이닝 상세 조회 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 상세 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 트레이닝 상세 조회 실패 (RuntimeException) - trainingId: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 상세 조회에 실패했습니다: " + e.getMessage());
        }

    }

    @Override
    public ModelFineTuningTrainingRes updateFineTuningTraining(String trainingId, ModelFineTuningUpdateReq updateReq, String scalingGroup) {
        log.info("파인튜닝 트레이닝 수정 요청 - trainingId: {}", trainingId);

        try {
            // status만 있는 경우 전용 API 사용
            if (updateReq.getStatus() != null && updateReq.getName() == null && updateReq.getProgress() == null && updateReq.getResource() == null && updateReq.getDatasetIds() == null && updateReq.getParams() == null && updateReq.getEnvs() == null && updateReq.getDescription() == null) {

                String targetScalingGroup = StringUtils.hasText(scalingGroup) ? scalingGroup : null;

                String resourceUrl = "/api/v1/backend-ai/finetuning/trainings/" + trainingId;

                List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

                // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
                policy = policy.stream()
                        .filter(policyReq -> {
                            if (policyReq.getPolicies() != null) {
                                // policies에 type이 "role"인 항목이 있는지 확인
                                return policyReq.getPolicies().stream()
                                        .noneMatch(p -> "role".equals(p.getType()));
                            }
                            return true; // policies가 null이면 포함
                        })
                        .collect(Collectors.toList());

                log.info("상태만 변경하는 전용 API 사용 - trainingId: {}, status: {}", trainingId, updateReq.getStatus());
                TrainingStatusUpdate statusUpdate = TrainingStatusUpdate.builder().status(updateReq.getStatus()).policy(policy).build();

                TrainingRead updatedTraining;
                try {
                    AdminContext.setAdminMode(ADMIN_USERNAME);
                    adminAuthService.ensureAdminToken();
                    updatedTraining = sktaiFineTuningService.updateTrainingStatus(trainingId, statusUpdate, targetScalingGroup, null);
                } catch (BusinessException e) {
                    log.error("파인튜닝 트레이닝 상태 변경 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
                    throw e;
                } catch (RuntimeException e) {
                    log.error("파인튜닝 트레이닝 상태 변경 실패 (RuntimeException) - trainingId: {}, error: {}", trainingId, e.getMessage(), e);
                    throw e;
                } finally {
                    AdminContext.clear();
                }

                ModelFineTuningTrainingRes response = finetuningMapper.toDetailResponse(updatedTraining);

                log.info("파인튜닝 트레이닝 상태 변경 성공 - trainingId: {}, status: {}", trainingId, updateReq.getStatus());
                return response;
            }

            // 전체 수정인 경우 기존 API 사용
            TrainingUpdate sktaiUpdateReq = finetuningMapper.toSktaiUpdateTrainingRequest(updateReq);
            sktaiFineTuningService.updateTraining(trainingId, sktaiUpdateReq);

            // 업데이트 후 최신 정보 재조회
            TrainingRead updatedTraining = sktaiFineTuningService.getTraining(trainingId);
            ModelFineTuningTrainingRes response = finetuningMapper.toDetailResponse(updatedTraining);

            log.info("파인튜닝 트레이닝 수정 성공 - trainingId: {}", trainingId);
            return response;

        } catch (BusinessException e) {
            log.error("파인튜닝 트레이닝 수정 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 수정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 트레이닝 수정 실패 (RuntimeException) - trainingId: {}, error: {}", trainingId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 수정에 실패했습니다: " + e.getMessage());
        }

    }

    @Override
    public void deleteFineTuningTraining(String trainingId) {
        log.info("파인튜닝 트레이닝 삭제 요청 - trainingId: {}", trainingId);

        try {
            sktaiFineTuningService.deleteTraining(trainingId);
            log.info("파인튜닝 트레이닝 삭제 성공 - trainingId: {}", trainingId);
        } catch (BusinessException e) {
            log.error("파인튜닝 트레이닝 삭제 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 삭제에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 트레이닝 삭제 실패 (RuntimeException) - trainingId: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 삭제에 실패했습니다: " + e.getMessage());
        }

    }

    @Override
    public ModelFineTuningStatusRes getFineTuningTrainingStatus(String trainingId) {
        log.info("파인튜닝 트레이닝 상태 조회 요청 - trainingId: {}", trainingId);

        try {
            TrainingStatusRead status = sktaiFineTuningService.getTrainingStatus(trainingId);
            ModelFineTuningStatusRes response = finetuningMapper.toTrainingStatusResponse(status);

            log.info("파인튜닝 트레이닝 상태 조회 성공 - trainingId: {}, status: {}", trainingId, status.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("파인튜닝 트레이닝 상태 조회 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 상태 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 트레이닝 상태 조회 실패 (RuntimeException) - trainingId: {}, error: {}", trainingId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 상태 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainerRead getTrainerById(String trainerId) {
        log.info("트레이너 상세 조회 요청 - trainerId: {}", trainerId);

        try {
            com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainerRead trainer = sktaiFineTuningService.getTrainer(trainerId);

            log.info("트레이너 상세 조회 성공 - trainerId: {}", trainerId);
            return trainer;

        } catch (BusinessException e) {
            log.error("트레이너 상세 조회 실패 (BusinessException) - trainerId: {}, errorCode: {}", trainerId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "트레이너 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("트레이너 상세 조회 실패 (RuntimeException) - trainerId: {}", trainerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "트레이너 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파인튜닝 생성 요청 검증
     *
     * <p>
     * 필수 필드와 비즈니스 규칙을 검증합니다.
     * </p>
     *
     * @param request 생성 요청 DTO
     * @throws BusinessException 검증 실패 시
     */
    private void validateCreateRequest(ModelFineTuningCreateReq request) {
        log.info("파인튜닝 트레이닝 생성 요청 검증 시작");
        log.info("요청 데이터 - name: '{}', baseModelId: '{}', datasetIds: {}, trainerId: '{}', params: '{}'", request.getName(), request.getBaseModelId(), request.getDatasetIds(), request.getTrainerId(), request.getParams());

        // 전체 요청 객체 상세 로깅
        log.info("=== 전체 요청 객체 상세 정보 ===");
        log.info("request 객체: {}", request);
        log.info("name 필드 값: '{}' (null 여부: {})", request.getName(), request.getName() == null);
        if (request.getName() != null) {
            log.info("name 필드 길이: {}, trim 후: '{}'", request.getName().length(), request.getName().trim());
        }

        // name 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.error("트레이닝 이름이 null이거나 비어있음: '{}'", request.getName());
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "트레이닝 이름은 필수입니다");
        }

        // baseModelId 검증
        if (request.getBaseModelId() == null || request.getBaseModelId().trim().isEmpty()) {
            log.error("기본 모델 ID가 null이거나 비어있음: '{}'", request.getBaseModelId());
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "기본 모델 ID는 필수입니다");
        }

        // datasetIds 검증
        if (request.getDatasetIds() == null || request.getDatasetIds().isEmpty()) {
            log.error("데이터셋 ID 목록이 null이거나 비어있음: {}", request.getDatasetIds());
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "데이터셋 ID 목록은 필수입니다");
        }

        // trainerId 검증
        if (request.getTrainerId() == null || request.getTrainerId().trim().isEmpty()) {
            log.error("트레이너 ID가 null이거나 비어있음: '{}'", request.getTrainerId());
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "트레이너 ID는 필수입니다");
        }

        // params 검증
        if (request.getParams() == null || request.getParams().trim().isEmpty()) {
            log.error("파라미터가 null이거나 비어있음: '{}'", request.getParams());
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "파라미터는 필수입니다");
        }

        log.info("파인튜닝 트레이닝 생성 요청 검증 완료 - name: '{}'", request.getName());
    }

    /**
     * ModelFineTuningCreateReq를 TrainingCreate로 변환
     */
    private TrainingCreate convertToTrainingCreate(ModelFineTuningCreateReq request) {
        log.info("=== DTO 변환 시작 ===");
        log.info("원본 요청 데이터: {}", request);

        // projectId가 "default-project"인 경우 null로 변환 (UUID 형식이 아니므로)
        String projectId = request.getProjectId();
        if ("default-project".equals(projectId)) {
            log.info("projectId가 'default-project'이므로 null로 변환");
            projectId = null;
        }

        TrainingCreate trainingCreate = TrainingCreate.builder().name(request.getName()).status(request.getStatus()).prevStatus(request.getPrevStatus()).progress(request.getProgress()).resource(request.getResource()).datasetIds(request.getDatasetIds()).baseModelId(request.getBaseModelId()).params(request.getParams()).envs(request.getEnvs()).description(request.getDescription()).projectId(projectId).taskId(request.getTaskId()).trainerId(request.getTrainerId()).policy(request.getPolicy()).isAutoModelCreation(true).build();

        log.info("=== DTO 변환 완료 ===");
        log.info("변환된 TrainingCreate: {}", trainingCreate);
        log.info("변환된 필드 값들:");
        log.info("  - name: '{}'", trainingCreate.getName());
        log.info("  - baseModelId: '{}'", trainingCreate.getBaseModelId());
        log.info("  - datasetIds: {}", trainingCreate.getDatasetIds());
        log.info("  - trainerId: '{}'", trainingCreate.getTrainerId());
        log.info("  - params: '{}'", trainingCreate.getParams());
        log.info("  - resource: {}", trainingCreate.getResource());
        log.info("  - projectId: '{}'", trainingCreate.getProjectId());

        return trainingCreate;
    }

    @Override
    public TrainingMetricsRead getFineTuningMetricsById(String trainingId) {
        log.info("파인튜닝 상세정보 조회(다중 ID, 매트릭뷰) - trainingId: {}", trainingId);
        try {
            return sktaiFineTuningService.getTrainingMetrics(trainingId, 1, 10, "", "");

        } catch (BusinessException e) {
            log.error("파인튜닝 트레이닝 상세 조회 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 상세 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 트레이닝 상세 조회 실패 (RuntimeException) - trainingId: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 트레이닝 상세 조회에 실패했습니다: " + e.getMessage());
        }

    }

    /**
     * 파인튜닝 모델 이벤트 조회
     */
    @Override
    public com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead getFineTuningTrainingEvents(String trainingId, String last) {
        log.info("파인튜닝 모델 이벤트 조회 요청 - trainingId: {}, last: {}", trainingId, last);

        try {
            com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead events = sktaiFineTuningService.getTrainingEvents(trainingId, last);

            log.info("파인튜닝 모델 이벤트 조회 성공 - trainingId: {}, events: {}", trainingId, events.getData() != null ? events.getData().size() : 0);
            return events;

        } catch (BusinessException e) {
            log.error("Training 이벤트 조회 실패 - id: {}, 빈 응답 반환", trainingId);
            // 빈 응답 객체 반환
            return com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead.builder().data(java.util.Collections.emptyList()).last("").build();
        } catch (Exception e) {
            log.error("파인튜닝 모델 이벤트 조회 실패 - trainingId: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 모델 이벤트 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파인튜닝 모델 Policy 설정
     */
    @Override
    public List<PolicyRequest> setFineTuningTrainingPolicy(String trainingId, String memberId, String projectName) {
        log.info("파인튜닝 모델 Policy 설정 요청 - trainingId: {}, memberId: {}, projectName: {}", trainingId, memberId, projectName);

        // trainingId 검증
        if (!StringUtils.hasText(trainingId)) {
            log.error("파인튜닝 모델 Policy 설정 실패 - trainingId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "파인튜닝 모델 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("파인튜닝 모델 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("파인튜닝 모델 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            String resourceUrl = "/api/v1/backend-ai/finetuning/trainings/" + trainingId;

            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(resourceUrl, memberId, projectName);
            log.info("파인튜닝 모델 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("파인튜닝 모델 Policy 조회 결과가 null - trainingId: {}, resourceUrl: {}", trainingId, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 모델 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
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

            log.info("파인튜닝 모델 Policy 설정 완료 - trainingId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", trainingId, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("파인튜닝 모델 Policy 설정 실패 (BusinessException) - trainingId: {}, errorCode: {}", trainingId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 모델 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("파인튜닝 모델 Policy 설정 실패 (RuntimeException) - trainingId: {}, error: {}", trainingId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 모델 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("파인튜닝 모델 Policy 설정 실패 (Exception) - trainingId: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "파인튜닝 모델 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }
}
