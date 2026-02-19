package com.skax.aiplatform.controller.model;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.lablup.api.dto.response.GetEndpointResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetSessionLogResponse;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
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
import com.skax.aiplatform.service.model.ModelDeployService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/modelDeploy")
@RequiredArgsConstructor
@Tag(name = "ModelDeploy", description = "모델 배포 API")
public class ModelDeployController {

    private final ModelDeployService modelDeployService;

    @GetMapping
    @Operation(summary = "모델 배포 목록 조회", description = "모델 배포 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 목록 조회 성공"),
    })
    public AxResponseEntity<PageResponse<GetModelDeployRes>> getModelDeploy(GetModelDeployReq request) {
        log.info("모델 배포 목록 조회 요청: {}", request);

        PageResponse<GetModelDeployRes> response = modelDeployService.getModelDeploy(request);

        log.info("모델 배포 목록 조회 성공: 총 {}건, 현재 페이지 {}건", response.getTotalElements(), response.getPageable().getPage());

        return AxResponseEntity.okPage(response, "모델 배포 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "모델 배포 상세 조회", description = "모델 배포 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 상세 조회 성공"),
    })
    public AxResponseEntity<GetModelDeployRes> getModelDeployById(@PathVariable String id) {
        log.info("모델 배포 상세 조회 요청: {}", id);

        GetModelDeployRes response = modelDeployService.getModelDeployById(id);

        log.info("모델 배포 상세 조회 성공: {}", response);

        return AxResponseEntity.ok(response, "모델 배포 상세 정보를 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}/system-log")
    @Operation(summary = "모델 배포 시스템 로그 조회", description = "모델 배포에 대한 시스템(컨테이너) 로그를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 시스템 로그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "로그 대상 세션을 찾을 수 없음")
    })
    public AxResponseEntity<GetSessionLogResponse> getModelDeploySystemLog(@PathVariable String id) {
        log.info("모델 배포 시스템 로그 조회 요청: {}", id);

        GetSessionLogResponse response = modelDeployService.getSystemLogById(id);

        log.info("모델 배포 시스템 로그 조회 성공 - sessionStatus: {}", response != null ? response.getResult() : null);

        return AxResponseEntity.ok(response, "모델 배포 시스템 로그를 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}/endpoint-info")
    @Operation(summary = "모델 배포 엔드포인트 정보 조회", description = "모델 배포의 Backend.AI 엔드포인트 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엔드포인트 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "엔드포인트를 찾을 수 없음")
    })
    public AxResponseEntity<GetEndpointResponse> getModelDeployEndpointInfo(@PathVariable String id) {
        log.info("모델 배포 엔드포인트 정보 조회 요청: {}", id);

        GetEndpointResponse response = modelDeployService.getEndpointInfoById(id);

        log.info("모델 배포 엔드포인트 정보 조회 성공 - endpointStatus: {}",
                response != null && response.getEndpoint() != null
                        ? response.getEndpoint().getStatus()
                        : null);

        return AxResponseEntity.ok(response, "모델 배포 엔드포인트 정보를 성공적으로 조회했습니다.");
    }

    @PostMapping("/{id}/{status}")
    @Operation(summary = "모델 배포 상태 변경", description = "모델 배포 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 잘못된 상태 변경"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> changeModelDeployStatus(@PathVariable String id, @PathVariable String status) {
        log.info("모델 배포 상태 변경 요청: {}, {}", id, status);

        modelDeployService.changeModelDeployStatus("serverless", id, status);

        String finalStatus = status.equals("start") ? "시작" : "중지";

        return AxResponseEntity.ok(null, "모델 배포 상태를 성공적으로 " + finalStatus + "했습니다.");
    }

    @DeleteMapping("/bulk")
    @Operation(summary = "모델 배포 삭제", description = "모델 배포를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 삭제 성공"),
    })
    public AxResponseEntity<Void> deleteModelDeployBulk(@RequestBody @Valid List<DeleteModelDeployReq> deleteRequests) {
        log.info("모델 배포 삭제 요청: {}", deleteRequests);
        modelDeployService.deleteModelDeployBulk(deleteRequests);
        return AxResponseEntity.ok(null, "모델 배포를 성공적으로 삭제했습니다.");
    }

    @PostMapping
    @Operation(summary = "모델 배포 생성", description = "새로운 모델 배포를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<CreateModelDeployRes> createModelDeploy(@RequestBody CreateModelDeployReq request) {
        log.info("모델 배포 생성 요청: {}", request);

        // 서비스 계층을 통해 모델 배포 생성
        CreateModelDeployRes response = modelDeployService.createModelDeploy(request, null);

        log.info("모델 배포 생성 성공: servingId={}, servingName={}",
                response.getServingId(), response.getName());

        return AxResponseEntity.ok(response, "모델 배포를 성공적으로 생성했습니다.");
    }

    @PostMapping("/backend-ai")
    @Operation(summary = "Backend.AI 연동 기반 모델 배포 생성", description = "Backend.AI 시스템과 연동하여 새로운 모델 배포를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Backend.AI 모델 배포 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<CreateBackendAiModelDeployRes> createBackendAiModelDeploy(
            @RequestBody CreateBackendAiModelDeployReq request) {
        log.info("Backend.AI 모델 배포 생성 요청: {}", request);

        CreateBackendAiModelDeployRes response = modelDeployService.createBackendAiModelDeploy(request, null);

        log.info("Backend.AI 모델 배포 생성 성공: servingId={}, servingName={}",
                response.getServingId(), response.getName());

        return AxResponseEntity.ok(response, "Backend.AI 모델 배포를 성공적으로 생성했습니다.");
        
    }

    @PutMapping("/{id}")
    @Operation(summary = "모델 배포 수정", description = "모델 배포를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 수정 성공"),
    })
    public AxResponseEntity<Void> updateModelDeploy(@PathVariable String id, @RequestBody PutModelDeployReq request) {
        log.info("모델 배포 수정 요청: {}, {}", id, request);

        modelDeployService.updateModelDeploy(id, request);

        return AxResponseEntity.ok(null, "모델 배포를 성공적으로 수정했습니다.");
    }

    @PutMapping("/backend-ai/{id}")
    @Operation(summary = "모델 배포 수정", description = "모델 배포를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 수정 성공"),
    })
    public AxResponseEntity<Void> updateBackendAiModelDeploy(@PathVariable String id,
            @RequestBody PutModelDeployReq request) {
        log.info("모델 배포 수정 요청: {}, {}", id, request);

        modelDeployService.updateBackendAiModelDeploy(id, request);

        return AxResponseEntity.ok(null, "모델 배포를 성공적으로 수정했습니다.");
    }

    @PostMapping("/backend-ai/{id}/{status}")
    @Operation(summary = "모델 배포 상태 변경(Backend.AI)", description = "모델 배포 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 잘못된 상태 변경"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> changeModelDeployStatusBackendAi(@PathVariable String id,
            @PathVariable String status) {
        log.info("모델 배포 상태 변경 요청: {}, {}", id, status);

        modelDeployService.changeModelDeployStatus("self_hosting", id, status);

        String finalStatus = status.equals("start") ? "시작" : "중지";

        return AxResponseEntity.ok(null, "모델 배포 상태를 성공적으로 " + finalStatus + "했습니다.");
    }

    @GetMapping("inference-performance")
    @Operation(summary = "추론 성능 조회", description = "모델 배포의 추론 성능을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추론 성능 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<GetInferencePerformanceRes> getInferencePerformance( 
            @RequestParam(required = false) @Schema(description = "서빙 ID", requiredMode = RequiredMode.NOT_REQUIRED) String servingId,
            @RequestParam(required = false) @Schema(description = "모델 이름", requiredMode = RequiredMode.NOT_REQUIRED) String modelName,
            @RequestParam @Schema(description = "조회 시작일시", requiredMode = RequiredMode.REQUIRED) String startDate,
            @RequestParam @Schema(description = "조회 종료일시", requiredMode = RequiredMode.REQUIRED) String endDate) {
        log.info("추론 성능 조회 요청: servingId={}, modelName={}, startDate={}, endDate={}", servingId, modelName, startDate, endDate);

        // 요청 DTO 생성
        GetInferencePerformanceReq request = GetInferencePerformanceReq.builder()
                .servingId(servingId)
                .modelName(modelName)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        GetInferencePerformanceRes response = modelDeployService.getInferencePerformance(request);

        log.info("추론 성능 조회 성공: response={}", response);
        return AxResponseEntity.ok(response, "추론 성능을 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}/resource-info")
    @Operation(summary = "모델 배포 자원 현황 조회", description = "servingId를 통해 모델 배포의 자원 현황(CPU, Memory, GPU)을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 배포 자원 현황 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "모델 배포를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<ModelDeployResourceInfo> getModelDeployResourceInfo(
            @PathVariable @Schema(description = "서빙 ID", required = true) String id) {
        log.info("모델 배포 자원 현황 조회 요청: servingId={}", id);

        ModelDeployResourceInfo response = modelDeployService.getModelDeployResourceInfo(id);

        log.info("모델 배포 자원 현황 조회 성공: servingId={}", id);
        return AxResponseEntity.ok(response, "모델 배포 자원 현황을 성공적으로 조회했습니다.");
        
    }

    /**
     * 모델 배포 Policy 설정
     *
     * @param servingId   서빙 ID (필수)
     * @param memberId    멤버 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/{servingId}/policy")
    @Operation(summary = "모델 배포 Policy 설정", description = "모델 배포의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "모델 배포 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<List<PolicyRequest>> setModelDeployPolicy(
            @PathVariable(value = "servingId", required = true) @Parameter(description = "서빙 ID", required = true, example = "bf63869d-df14-44f7-9a73-9ad9c014575c") String servingId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("모델 배포 Policy 설정 요청 - servingId: {}, memberId: {}, projectName: {}", servingId, memberId,
                projectName);
        List<PolicyRequest> policy = modelDeployService.setModelDeployPolicy(servingId, memberId, projectName);
        return AxResponseEntity.ok(policy, "모델 배포 Policy가 성공적으로 설정되었습니다.");
    }

    @GetMapping("/docker-img-url")
    @Operation(summary = "도커 이미지 URL 조회", description = "SYS_U_V 값으로 도커 이미지 URL을 조회합니다. (DEL_YN = 0인 경우만)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도커 이미지 URL 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
    })
    public AxResponseEntity<List<GetDockerImgUrlRes>> getDockerImgUrl(
            @RequestParam(value = "sysUV", required = true) @Parameter(description = "SYSTEM 유형값", required = true) @Schema(description = "SYSTEM 유형값", requiredMode = RequiredMode.REQUIRED) String sysUV) {
        log.info("도커 이미지 URL 조회 요청: sysUV={}", sysUV);

        List<GetDockerImgUrlRes> response = modelDeployService.getDockerImgUrlBySysUV(sysUV);

        log.info("도커 이미지 URL 조회 성공: sysUV={}, count={}", sysUV, response.size());
        return AxResponseEntity.ok(response, "도커 이미지 URL을 성공적으로 조회했습니다.");
    }
}
