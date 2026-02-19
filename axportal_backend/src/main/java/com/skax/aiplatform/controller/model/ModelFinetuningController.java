package com.skax.aiplatform.controller.model;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricsRead;
import com.skax.aiplatform.common.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetByIdRes;
import com.skax.aiplatform.dto.model.request.ModelFineTuningCreateReq;
import com.skax.aiplatform.dto.model.request.ModelFineTuningStatusUpdateReq;
import com.skax.aiplatform.dto.model.request.ModelFineTuningUpdateReq;
import com.skax.aiplatform.dto.model.response.ModelDetailRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningCreateRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningStatusRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingsRes;
import com.skax.aiplatform.service.data.DataCtlgDataSetService;
import com.skax.aiplatform.service.model.ModelFinetuningService;
import com.skax.aiplatform.service.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/finetuning")
@Tag(name = "파인튜닝 관리", description = "파인튜닝 모델 생성 및 관리 API")
public class ModelFinetuningController {

    private final ModelFinetuningService fineTuningService;
    private final ModelService modelService;
    private final DataCtlgDataSetService dataCtlgDataSetService;

    /**
     * 파인튜닝 모델 생성
     *
     * @param registReq 파인튜닝 모델 생성 요청 DTO
     * @return FineTuningRes 생성된 파인튜닝 모델 정보
     */
    @PostMapping("/trainings")
    @Operation(summary = "파인튜닝 모델 생성", description = "파인튜닝 모델을 생성합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<ModelFineTuningCreateRes> createFineTuningTraining(
            @Valid @RequestBody ModelFineTuningCreateReq registReq) {
        log.error("=== 파인튜닝 모델 생성 API 호출됨 ===");
        log.info("=== 파인튜닝 모델 생성 요청 시작 ===");
        log.info("요청 데이터 - name: '{}', baseModelId: '{}', datasetIds: {}, trainerId: '{}', params: '{}'",
                registReq.getName(), registReq.getBaseModelId(), registReq.getDatasetIds(), registReq.getTrainerId(),
                registReq.getParams());

        // DTO 전체 내용 출력
        log.info("전체 DTO 내용: {}", registReq);

        ModelFineTuningCreateRes regist = fineTuningService.registFineTuningTraining(registReq);
        return AxResponseEntity.created(regist, "파인튜닝 모델 생성 요청이 성공적으로 접수되었습니다.");
    }

    /**
     * 파인튜닝 모델 목록 조회
     *
     * @param pageable 페이지 정보
     * @param sort     정렬 기준
     * @param filter   필터 조건 (레거시, status가 없을 때 사용)
     * @param search   검색어 (레거시, searchKeyword가 없을 때 사용)
     * @return PageResponse<FineTuningRes> 파인튜닝 모델 목록
     */
    @GetMapping("/trainings")
    @Operation(summary = "파인튜닝 모델 목록 조회", description = "파인튜닝 모델 목록을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<PageResponse<ModelFineTuningTrainingsRes>> getFineTuningTrainings(
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
            @Parameter(description = "필터 조건 (레거시)") @RequestParam(required = false) String filter,
            @Parameter(description = "검색어 (레거시)") @RequestParam(required = false) String search) {

        log.info("=== Finetuning trainings endpoint 호출 시작 ===");
        log.info("요청 파라미터 - page: {}, size: {}, sort: {}, filter: {}, search: {}", pageable.getPageNumber(),
                pageable.getPageSize(), sort, filter, search);

        PageResponse<ModelFineTuningTrainingsRes> pageRes = fineTuningService.getFineTuningTrainings(pageable, sort,
                filter, search);

        // 응답 데이터 상세 분석
        if (pageRes.getContent() != null && !pageRes.getContent().isEmpty()) {

            // 첫 번째 항목 정보 로깅
            var firstItem = pageRes.getContent().get(0);
            log.info("첫 번째 항목 - No: {}, ID: {}, Name: {}, Status: {}", firstItem.getNo(), firstItem.getId(),
                    firstItem.getName(), firstItem.getStatus());
        } else {
            log.warn("=== 응답 데이터가 비어있음 ===");
            log.warn("응답 내용: {}", pageRes.getContent());
            log.warn("전체 데이터 수: {}", pageRes.getTotalElements());
            log.warn("전체 페이지 수: {}", pageRes.getTotalPages());
        }

        AxResponseEntity<PageResponse<ModelFineTuningTrainingsRes>> response = AxResponseEntity.okPage(pageRes,
                "파인튜닝 모델 목록을 성공적으로 조회했습니다.");
        log.info("=== Finetuning trainings endpoint 호출 완료 ===");
        return response;
    }

    /**
     * 파인튜닝 모델 상세 조회
     *
     * @param trainingId 파인튜닝 모델 ID
     * @param isDataSet  데이터셋 조회여부
     * @param isMetric   매트릭 조회여부
     * @return FineTuningRes 파인튜닝 모델 상세 정보
     */
    @GetMapping("/trainings/{trainingId}")
    @Operation(summary = "파인튜닝 모델 상세 조회", description = "파인튜닝 모델 상세 정보를 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<ModelFineTuningTrainingRes> getFineTuningTrainingById(
            @PathVariable @Parameter(description = "파인튜닝 모델 ID") String trainingId,
            @RequestParam(defaultValue = "N") @Parameter(description = "데이터셋 조회여부 (Y,N)", example = "Y") String isDataSet,
            @RequestParam(defaultValue = "N") @Parameter(description = "매트릭 조회여부 (Y,N)", example = "Y") String isMetric) {

        ModelFineTuningTrainingRes training = fineTuningService.getFineTuningTraining(trainingId);

        // 베이스모델 아이디로 모델 상세 조회
        ModelDetailRes baseModel = modelService.getModelById(training.getBaseModelId());
        training.setBaseModelDetail(baseModel);

        if (isDataSet.equals("Y")) {
            // 데이터셋 아이디로 데이터셋 상세 조회 (배열)
            List<DataCtlgDataSetByIdRes> datasets = new ArrayList<>();
            for (String datasetId : training.getDatasetIds()) {
                try {
                    DataCtlgDataSetByIdRes dataset = dataCtlgDataSetService.getDatasetById(UUID.fromString(datasetId));
                    datasets.add(dataset);
                } catch (BusinessException e) {
                    log.error("파인튜닝 학습데이터 조회 실패 error: {}", e.getMessage());
                }
            }
            training.setDatasetDetails(datasets);
        }

        if (isMetric.equals("Y")) {
            // 매트릭 정보 조회
            TrainingMetricsRead trainingMetricsRead = fineTuningService.getFineTuningMetricsById(trainingId);
            if (trainingMetricsRead != null) {
                training.setMetricDetails(trainingMetricsRead.getData());
            }
        }

        return AxResponseEntity.ok(training, "파인튜닝 모델 상세를 성공적으로 조회했습니다.");

    }

    /**
     * 파인튜닝 모델 수정
     *
     * @param trainingId 파인튜닝 모델 ID
     * @param updateReq  파인튜닝 모델 수정 요청 DTO
     * @return FineTuningRes 수정된 파인튜닝 모델 정보
     */
    @PutMapping("/trainings/{trainingId}")
    @Operation(summary = "파인튜닝 모델 수정", description = "파인튜닝 모델 정보를 수정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<ModelFineTuningTrainingRes> updateFineTuningTraining(
            @PathVariable @Parameter(description = "파인튜닝 모델 ID") String trainingId,
            @Valid @RequestBody @Parameter(description = "파인튜닝 모델 수정 요청 DTO") ModelFineTuningUpdateReq updateReq) {
        ModelFineTuningTrainingRes update = fineTuningService.updateFineTuningTraining(trainingId, updateReq, null);
        return AxResponseEntity.updated(update, "파인튜닝 모델이 성공적으로 수정되었습니다.");
    }

    /**
     * 파인튜닝 모델 상태만 변경
     *
     * @param trainingId 파인튜닝 모델 ID
     * @param statusReq  파인튜닝 모델 상태 수정 요청 DTO
     * @return FineTuningRes 수정된 파인튜닝 모델 정보
     */
    @PutMapping("/trainings/status/{trainingId}")
    @Operation(summary = "파인튜닝 모델 상태 변경", description = "파인튜닝 모델의 상태값만 변경합니다. (예: stopped, starting, training 등)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "파인튜닝 모델을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<ModelFineTuningTrainingRes> updateFineTuningTrainingStatus(
            @PathVariable @Parameter(description = "파인튜닝 모델 ID", example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String trainingId,
            @Valid @RequestBody @Parameter(description = "상태 변경 요청 DTO") ModelFineTuningStatusUpdateReq statusReq) {

        log.info("파인튜닝 모델 상태 변경 요청 - trainingId: {}, status: {}", trainingId, statusReq.getStatus());

        // status만 포함된 UpdateReq 생성
        ModelFineTuningUpdateReq updateReq = ModelFineTuningUpdateReq.builder().status(statusReq.getStatus()).build();

        ModelFineTuningTrainingRes update = fineTuningService.updateFineTuningTraining(trainingId, updateReq,
                statusReq.getScalingGroup());

        log.info("파인튜닝 모델 상태 변경 완료 - trainingId: {}, newStatus: {}", trainingId, update.getStatus());
        return AxResponseEntity.updated(update, "파인튜닝 모델 상태가 성공적으로 변경되었습니다.");
    }

    /**
     * 파인튜닝 모델 삭제
     *
     * @param trainingId 파인튜닝 모델 ID
     * @return Void
     */
    @DeleteMapping("/trainings/{trainingId}")
    @Operation(summary = "파인튜닝 모델 삭제", description = "파인튜닝 모델을 삭제합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<Void> deleteFineTuningTraining(
            @PathVariable @Parameter(description = "파인튜닝 모델 ID") String trainingId) {
        fineTuningService.deleteFineTuningTraining(trainingId);
        return AxResponseEntity.deleted("파인튜닝 모델이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/trainings/{trainingId}/status")
    @Operation(summary = "파인튜닝 모델 상태 조회", description = "파인튜닝 모델 상태를 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 상태 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<ModelFineTuningStatusRes> getFineTuningTrainingStatus(
            @PathVariable @Parameter(description = "파인튜닝 모델 ID") String trainingId) {
        ModelFineTuningStatusRes status = fineTuningService.getFineTuningTrainingStatus(trainingId);

        return AxResponseEntity.ok(status, "파인튜닝 모델 상태를 성공적으로 조회했습니다.");
    }

    /**
     * 파인튜닝 모델 이벤트 조회
     *
     * @param trainingId 파인튜닝 모델 ID
     * @param last       마지막 이벤트 식별자 (증분 조회용)
     * @return TrainingEventsRead 파인튜닝 모델 이벤트 목록
     */
    @GetMapping("/trainings/{trainingId}/events")
    @Operation(summary = "파인튜닝 모델 이벤트 조회", description = "파인튜닝 모델의 이벤트 목록을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 이벤트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead> getFineTuningTrainingEvents(
            @PathVariable @Parameter(description = "파인튜닝 모델 ID") String trainingId,
            @RequestParam(required = false) @Parameter(description = "마지막 이벤트 식별자 (증분 조회용)") String last) {

        log.info("=== 파인튜닝 모델 이벤트 조회 API 호출됨 ===");
        log.info("요청 파라미터 - trainingId: '{}', last: '{}'", trainingId, last);

        com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead events = fineTuningService
                .getFineTuningTrainingEvents(trainingId, last);

        return AxResponseEntity.ok(events, "파인튜닝 모델 이벤트를 성공적으로 조회했습니다.");
    }

    /**
     * 파인튜닝 모델 Policy 설정
     *
     * @param trainingId  파인튜닝 모델 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/trainings/{training_id}/policy")
    @Operation(summary = "파인튜닝 모델 Policy 설정", description = "파인튜닝 모델의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "파인튜닝 모델 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<List<PolicyRequest>> setFineTuningTrainingPolicy(
            @PathVariable(value = "training_id", required = true) @Parameter(description = "파인튜닝 모델 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String trainingId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("파인튜닝 모델 Policy 설정 요청 - trainingId: {}, memberId: {}, projectName: {}", trainingId, memberId,
                projectName);
        List<PolicyRequest> policy = fineTuningService.setFineTuningTrainingPolicy(trainingId, memberId, projectName);
        return AxResponseEntity.ok(policy, "파인튜닝 모델 Policy가 성공적으로 설정되었습니다.");
    }

}