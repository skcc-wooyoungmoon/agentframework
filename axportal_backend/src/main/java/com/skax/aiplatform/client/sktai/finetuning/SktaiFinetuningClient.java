package com.skax.aiplatform.client.sktai.finetuning;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.*;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Fine-tuning API Feign Client
 *
 * <p>
 * SKTAI Fine-tuning 시스템과의 통신을 담당하는 Feign Client 인터페이스입니다.
 * Training 및 Trainer 관리 기능을 제공하며, OAuth2 인증을 통해 보안이 적용됩니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Training 관리</strong>: 생성, 조회, 수정, 삭제, 상태 관리</li>
 * <li><strong>Training 모니터링</strong>: 이벤트 조회, 메트릭 관리</li>
 * <li><strong>Trainer 관리</strong>: 생성, 조회, 수정, 삭제</li>
 * <li><strong>실시간 추적</strong>: SSE 기반 이벤트 스트리밍</li>
 * </ul>
 *
 * <h3>인증 방식:</h3>
 * <ul>
 * <li><strong>OAuth2</strong>: Bearer Token 기반 인증</li>
 * <li><strong>Client Secret</strong>: 클라이언트 자격증명 인증</li>
 * </ul>
 *
 * <h3>Base URL:</h3>
 * <p>
 * ${sktai.api.base-url}/api/v1/finetuning
 * </p>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktaiClientConfig Feign 클라이언트 설정
 * @since 2025-08-15
 */
@FeignClient(name = "sktai-finetuning-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
@Tag(name = "SKTAI Fine-tuning API", description = "SKTAI Fine-tuning 모델 훈련 및 관리 API")
public interface SktaiFinetuningClient {

    // ========== Training Management APIs ==========

    /**
     * Training 신규 생성
     *
     * <p>
     * 새로운 Fine-tuning Training을 생성합니다.
     * 프로젝트, 데이터셋, 모델 설정을 포함한 Training 정보를 등록합니다.
     * </p>
     *
     * @param request Training 생성 요청 정보 (프로젝트 ID, 이름, 설정 등 포함)
     * @return 생성된 Training 상세 정보
     */
    @PostMapping(value = "/api/v1/backend-ai/finetuning/trainings", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Training 신규 생성", description = "새로운 Fine-tuning Training을 생성합니다. 프로젝트, 데이터셋, 모델 설정을 포함한 Training 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training 생성 성공", content = @Content(schema = @Schema(implementation = TrainingRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "409", description = "Training 이름 중복")})
    TrainingRead createTraining(@RequestBody TrainingCreate request);

    /**
     * Training 목록 조회
     *
     * <p>
     * 조건에 맞는 Training 목록을 페이지네이션으로 조회합니다.
     * 프로젝트별 필터링, 검색, 정렬 옵션을 제공합니다.
     * </p>
     *
     * @param page   페이지 번호 (1부터 시작, 기본값: 1)
     * @param size   페이지 크기 (기본값: 10)
     * @param sort   정렬 조건 (예: "created_at:desc")
     * @param _filter 필터링 조건 (JSON 형태)
     * @param search 검색 키워드
     * @return 페이지네이션된 Training 목록
     */
    @GetMapping("/api/v1/backend-ai/finetuning/trainings")
    @Operation(summary = "Training 목록 조회", description = "조건에 맞는 Training 목록을 페이지네이션으로 조회합니다. 프로젝트별 필터링, 검색, 정렬 옵션을 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training 목록 조회 성공", content = @Content(schema = @Schema(implementation = TrainingsRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 쿼리 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")})
    TrainingsRead getTrainings(
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기", example = "10") @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "정렬 조건 (필드명:방향)", example = "created_at:desc") @RequestParam(value = "sort", required = false) String sort,

            @Parameter(description = "필터링 조건 (JSON 형태)", example = "{\"project_id\": \"proj-123\"}") @RequestParam(value = "_filter", required = false) String _filter,

            @Parameter(description = "검색 키워드", example = "GPT") @RequestParam(value = "search", required = false) String search);

    /**
     * Training ID로 상세 조회
     *
     * <p>
     * 지정된 Training ID에 해당하는 Training의 상세 정보를 조회합니다.
     * 설정, 상태, 진행률 등 모든 정보를 포함합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @return Training 상세 정보
     */
    @GetMapping("/api/v1/backend-ai/finetuning/trainings/{training_id}")
    @Operation(summary = "Training ID로 상세 조회", description = "지정된 Training ID에 해당하는 Training의 상세 정보를 조회합니다. 설정, 상태, 진행률 등 모든 정보를 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training 조회 성공", content = @Content(schema = @Schema(implementation = TrainingRead.class))),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "접근 권한 부족")})
    TrainingRead getTraining(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId);

    /**
     * Training 정보 수정
     *
     * <p>
     * 기존 Training의 설정이나 메타데이터를 수정합니다.
     * 진행 중인 Training의 경우 제한된 필드만 수정 가능합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    Training 수정 요청 정보
     * @return 수정된 Training 상세 정보
     */
    @PutMapping(value = "/api/v1/backend-ai/finetuning/trainings/{training_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Training 정보 수정", description = "기존 Training의 설정이나 메타데이터를 수정합니다. 진행 중인 Training의 경우 제한된 필드만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training 수정 성공", content = @Content(schema = @Schema(implementation = TrainingRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "수정 불가능한 상태")})
    TrainingRead updateTraining(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId,

            @RequestBody TrainingUpdate request);

    /**
     * Training 상태만 수정
     *
     * <p>
     * Training의 상태값만 수정합니다.
     * 빠른 상태 변경을 위한 전용 API입니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    변경할 상태 정보
     * @return 수정된 Training 상세 정보
     */
    @PutMapping(value = "/api/v1/backend-ai/finetuning/trainings/{training_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Training 상태만 수정", description = "Training의 상태값만 수정합니다. 빠른 상태 변경을 위한 전용 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training 상태 수정 성공", content = @Content(schema = @Schema(implementation = TrainingRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 상태값"),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "상태 변경 불가능")})
    TrainingRead updateTrainingStatus(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId,
            @Parameter(description = "Backend.ai scaling group for resource allocation") @RequestParam(value = "scaling_group", required = false) String scalingGroup,
            @Parameter(description = "Backend.ai target agents (comma-separated list)") @RequestParam(value = "agent_list", required = false) String agentList,
            @Parameter(description = "변경할 상태 정보") @RequestBody TrainingStatusUpdate request);

    /**
     * Training 삭제 (Soft Delete)
     *
     * <p>
     * 지정된 Training을 소프트 삭제합니다.
     * 실제 데이터는 유지되며, 상태만 삭제로 변경됩니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     */
    @DeleteMapping("/api/v1/backend-ai/finetuning/trainings/{training_id}")
    @Operation(summary = "Training 삭제 (Soft Delete)", description = "지정된 Training을 소프트 삭제합니다. 실제 데이터는 유지되며, 상태만 삭제로 변경됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Training 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제 불가능한 상태")})
    void deleteTraining(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId);

    /**
     * Training 완전 삭제 (Hard Delete)
     *
     * <p>
     * 지정된 Training을 완전히 삭제합니다.
     * 모든 관련 데이터가 영구적으로 제거되므로 주의가 필요합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     */
    @DeleteMapping("/api/v1/finetuning/trainings/{training_id}/hard")
    @Operation(summary = "Training 완전 삭제 (Hard Delete)", description = "지정된 Training을 완전히 삭제합니다. 모든 관련 데이터가 영구적으로 제거되므로 주의가 필요합니다.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Training 완전 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제 불가능한 상태")})
    void hardDeleteTraining(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId);

    /**
     * Training 상태 조회
     *
     * <p>
     * 지정된 Training의 현재 상태를 조회합니다.
     * 진행률, 상태 코드, 메시지 등을 포함합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @return Training 상태 정보
     */
    @GetMapping("/api/v1/backend-ai/finetuning/trainings/{training_id}/status")
    @Operation(summary = "Training 상태 조회", description = "지정된 Training의 현재 상태를 조회합니다. 진행률, 상태 코드, 메시지 등을 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training 상태 조회 성공", content = @Content(schema = @Schema(implementation = TrainingStatusRead.class))),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음")})
    TrainingStatusRead getTrainingStatus(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId);

    /**
     * Training 이벤트 조회
     *
     * <p>
     * 지정된 Training의 이벤트 목록을 조회합니다.
     * 실시간 모니터링을 위한 이벤트 스트림을 제공합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @param last       마지막 이벤트 식별자 (증분 조회용)
     * @return Training 이벤트 목록
     */
    @GetMapping("/api/v1/backend-ai/finetuning/trainings/{training_id}/events")
    @Operation(summary = "Training 이벤트 조회", description = "지정된 Training의 이벤트 목록을 조회합니다. 실시간 모니터링을 위한 이벤트 스트림을 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training 이벤트 조회 성공", content = @Content(schema = @Schema(implementation = TrainingEventsRead.class))),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음")})
    TrainingEventsRead getTrainingEvents(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId,

            @Parameter(description = "마지막 이벤트 식별자 (증분 조회용)", example = "2025-08-15T11:30:00.000Z") @RequestParam(value = "last", required = false) String last);

    /**
     * Training 작업 콜백 처리
     *
     * <p>
     * Task Manager로부터 Training 작업 상태 변경 알림을 처리합니다.
     * 내부 시스템 간 통신을 위한 콜백 엔드포인트입니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    작업 콜백 정보
     */
    @PostMapping(value = "/api/v1/finetuning/trainings/{training_id}/task/callback", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Training 작업 콜백 처리", description = "Task Manager로부터 Training 작업 상태 변경 알림을 처리합니다. 내부 시스템 간 통신을 위한 콜백 엔드포인트입니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "콜백 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 콜백 데이터"),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음")})
    void trainingTaskCallback(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId,

            @RequestBody TrainingTaskCallback request);

    // ========== Training Metrics APIs ==========

    /**
     * Training 메트릭 생성
     *
     * <p>
     * Training 진행 중 수집된 메트릭을 등록합니다.
     * 손실값, 정확도 등 성능 지표를 기록합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    메트릭 생성 요청 정보
     * @return 생성된 메트릭 정보
     */
    @PostMapping(value = "/api/v1/backend-ai/finetuning/trainings/{training_id}/metrics", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Training 메트릭 생성", description = "Training 진행 중 수집된 메트릭을 등록합니다. 손실값, 정확도 등 성능 지표를 기록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "메트릭 생성 성공", content = @Content(schema = @Schema(implementation = TrainingMetricRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 메트릭 데이터"),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음")})
    TrainingMetricRead createTrainingMetric(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId,

            @RequestBody TrainingMetricCreate request);

    /**
     * Training 메트릭 목록 조회
     *
     * <p>
     * 지정된 Training의 메트릭 목록을 페이지네이션으로 조회합니다.
     * 메트릭 타입별 필터링과 시간순 정렬을 지원합니다.
     * </p>
     *
     * @param trainingId Training 고유 식별자
     * @param page       페이지 번호 (기본값: 1)
     * @param size       페이지 크기 (기본값: 20)
     * @param sort       정렬 조건 (기본값: "step:asc")
     * @param filter     필터링 조건 (메트릭 타입별)
     * @return 페이지네이션된 메트릭 목록
     */
    @GetMapping("/api/v1/backend-ai/finetuning/trainings/{training_id}/metrics")
    @Operation(summary = "Training 메트릭 목록 조회", description = "지정된 Training의 메트릭 목록을 페이지네이션으로 조회합니다. 메트릭 타입별 필터링과 시간순 정렬을 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메트릭 목록 조회 성공", content = @Content(schema = @Schema(implementation = TrainingMetricsRead.class))),
            @ApiResponse(responseCode = "404", description = "Training을 찾을 수 없음")})
    TrainingMetricsRead getTrainingMetrics(
            @Parameter(description = "Training 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("training_id") String trainingId,

            @Parameter(description = "페이지 번호", example = "1") @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기", example = "20") @RequestParam(value = "size", defaultValue = "20") Integer size,

            @Parameter(description = "정렬 조건", example = "step:asc") @RequestParam(value = "sort", defaultValue = "step:asc") String sort,

            @Parameter(description = "필터링 조건", example = "{\"type\": \"train\"}") @RequestParam(value = "filter", required = false) String filter);

    // ========== Trainer Management APIs ==========

    /**
     * Trainer 신규 생성
     *
     * <p>
     * 새로운 Trainer를 생성합니다.
     * Trainer는 Fine-tuning 작업을 수행하는 워커 인스턴스입니다.
     * </p>
     *
     * @param request Trainer 생성 요청 정보
     * @return 생성된 Trainer 정보
     */
    @PostMapping(value = "/api/v1/finetuning/trainers", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Trainer 신규 생성", description = "새로운 Trainer를 생성합니다. Trainer는 Fine-tuning 작업을 수행하는 워커 인스턴스입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer 생성 성공", content = @Content(schema = @Schema(implementation = TrainerRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "Trainer 이름 중복")})
    TrainerRead createTrainer(@RequestBody TrainerCreate request);

    /**
     * Trainer 목록 조회
     *
     * <p>
     * 등록된 Trainer 목록을 페이지네이션으로 조회합니다.
     * 상태별 필터링과 검색 기능을 제공합니다.
     * </p>
     *
     * @param page   페이지 번호 (기본값: 1)
     * @param size   페이지 크기 (기본값: 10)
     * @param sort   정렬 조건
     * @param filter 필터링 조건
     * @param search 검색 키워드
     * @return 페이지네이션된 Trainer 목록
     */
    @GetMapping("/api/v1/finetuning/trainers")
    @Operation(summary = "Trainer 목록 조회", description = "등록된 Trainer 목록을 페이지네이션으로 조회합니다. 상태별 필터링과 검색 기능을 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer 목록 조회 성공", content = @Content(schema = @Schema(implementation = TrainersRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 쿼리 파라미터")})
    TrainersRead getTrainers(
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기", example = "10") @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "정렬 조건", example = "created_at:desc") @RequestParam(value = "sort", required = false) String sort,

            @Parameter(description = "필터링 조건", example = "{\"status\": \"active\"}") @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색 키워드", example = "GPU") @RequestParam(value = "search", required = false) String search);

    /**
     * Trainer ID로 상세 조회
     *
     * <p>
     * 지정된 Trainer ID에 해당하는 Trainer의 상세 정보를 조회합니다.
     * 성능 스펙, 상태, 작업 이력 등을 포함합니다.
     * </p>
     *
     * @param trainerId Trainer 고유 식별자
     * @return Trainer 상세 정보
     */
    @GetMapping("/api/v1/finetuning/trainers/{trainer_id}")
    @Operation(summary = "Trainer ID로 상세 조회", description = "지정된 Trainer ID에 해당하는 Trainer의 상세 정보를 조회합니다. 성능 스펙, 상태, 작업 이력 등을 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer 조회 성공", content = @Content(schema = @Schema(implementation = TrainerRead.class))),
            @ApiResponse(responseCode = "404", description = "Trainer를 찾을 수 없음")})
    TrainerRead getTrainer(
            @Parameter(description = "Trainer 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("trainer_id") String trainerId);

    /**
     * Trainer 정보 수정
     *
     * <p>
     * 기존 Trainer의 설정이나 메타데이터를 수정합니다.
     * 작업 중인 Trainer의 경우 제한된 필드만 수정 가능합니다.
     * </p>
     *
     * @param trainerId Trainer 고유 식별자
     * @param request   Trainer 수정 요청 정보
     * @return 수정된 Trainer 정보
     */
    @PutMapping(value = "/api/v1/finetuning/trainers/{trainer_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Trainer 정보 수정", description = "기존 Trainer의 설정이나 메타데이터를 수정합니다. 작업 중인 Trainer의 경우 제한된 필드만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer 수정 성공", content = @Content(schema = @Schema(implementation = TrainerRead.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "Trainer를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "수정 불가능한 상태")})
    TrainerRead updateTrainer(
            @Parameter(description = "Trainer 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("trainer_id") String trainerId,

            @RequestBody TrainerUpdate request);

    /**
     * Trainer 삭제
     *
     * <p>
     * 지정된 Trainer를 삭제합니다.
     * 진행 중인 작업이 있는 경우 삭제가 거부됩니다.
     * </p>
     *
     * @param trainerId Trainer 고유 식별자
     */
    @DeleteMapping("/api/v1/finetuning/trainers/{trainer_id}")
    @Operation(summary = "Trainer 삭제", description = "지정된 Trainer를 삭제합니다. 진행 중인 작업이 있는 경우 삭제가 거부됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Trainer 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Trainer를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제 불가능한 상태 (작업 진행 중)")})
    void deleteTrainer(
            @Parameter(description = "Trainer 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("trainer_id") String trainerId);
}
