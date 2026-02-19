package com.skax.aiplatform.controller.model;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.CreateModelCtlgReq;
import com.skax.aiplatform.dto.model.request.DeleteModelCtlgBulkReq;
import com.skax.aiplatform.dto.model.request.GetModelCtlgReq;
import com.skax.aiplatform.dto.model.request.GetUpdateModelCtlgReq;
import com.skax.aiplatform.dto.model.response.GetModelCtlgRes;
import com.skax.aiplatform.dto.model.response.GetModelPrvdRes;
import com.skax.aiplatform.dto.model.response.GetModelTagsRes;
import com.skax.aiplatform.dto.model.response.GetModelTypesRes;
import com.skax.aiplatform.service.model.ModelCtlgService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/modelCtlg")
@RequiredArgsConstructor
@Tag(name = "모델 카탈로그", description = "모델 카테고리 관리 API")
public class ModelCtlgController {

    private final ModelCtlgService modelCtlgService;

    @GetMapping
    @Operation(summary = "모델 카탈로그 목록 조회", description = "모델 카탈로그 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 카탈로그 조회 성공"),
    })
    public AxResponseEntity<PageResponse<GetModelCtlgRes>> getModelCtlg(GetModelCtlgReq modelCtlgReq) {
        log.info("모델 카탈로그 목록 조회 요청: {}", modelCtlgReq);

        PageResponse<GetModelCtlgRes> modelCtlg = modelCtlgService.getModelCtlg(modelCtlgReq);

        log.info("모델 카탈로그 목록 조회 성공: 총 {}건, 현재 페이지 {}건", modelCtlg.getTotalElements(),
                modelCtlg.getPageable().getPage());

        return AxResponseEntity.okPage(modelCtlg, "모델 카탈로그 목록을 성공적으로 조회했습니다.");
    }

    @PostMapping
    @Operation(summary = "모델 카탈로그 생성", description = "모델 카탈로그를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 카탈로그 생성 성공"),
    })
    public AxResponseEntity<GetModelCtlgRes> createModelCtlg(@RequestBody CreateModelCtlgReq createModelCtlgReq) {
        log.info("모델 카탈로그 생성 요청: {}", createModelCtlgReq);

        GetModelCtlgRes modelCtlg = modelCtlgService.createModelCtlg(createModelCtlgReq);

        return AxResponseEntity.ok(modelCtlg, "모델 카탈로그를 성공적으로 생성했습니다.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "모델 상세 정보 조회", description = "모델 상세 정보 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 상세 정보 조회 성공"),
    })
    public AxResponseEntity<GetModelCtlgRes> getModelCtlgById(@PathVariable String id) {
        log.info("모델 상세 정보 조회 요청: {}", id);

        GetModelCtlgRes modelCtlg = modelCtlgService.getModelCtlgById(id);

        log.info("모델 상세 정보 조회 성공: {}", modelCtlg);

        return AxResponseEntity.ok(modelCtlg, "모델 상세 정보를 성공적으로 조회했습니다.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "모델 상세 정보 수정", description = "모델 상세 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 상세 정보 수정 성공"),
    })
    public AxResponseEntity<GetModelCtlgRes> updateModelCtlgById(@PathVariable String id,
            @RequestBody GetUpdateModelCtlgReq updateModelCtlgReq) {
        log.info("모델 상세 정보 수정 요청: {}", id);

        GetModelCtlgRes modelCtlg = modelCtlgService.updateModelCtlgById(id, updateModelCtlgReq);

        log.info("모델 상세 정보 수정 성공: {}", modelCtlg);

        return AxResponseEntity.ok(modelCtlg, "모델 상세 정보를 성공적으로 수정했습니다.");
    }

    @DeleteMapping("")
    @Operation(summary = "모델 상세 정보 삭제", description = "모델 상세 정보를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 상세 정보 삭제 성공"),
    })
    public AxResponseEntity<Void> deleteModelCtlgBulk(@RequestBody DeleteModelCtlgBulkReq deleteModelCtlgBulkReq) {
        log.info("모델 상세 정보 삭제 요청: {}", deleteModelCtlgBulkReq);

        modelCtlgService.deleteModelCtlgBulk(deleteModelCtlgBulkReq);

        log.info("모델 상세 정보 삭제 성공");

        return AxResponseEntity.ok(null, "모델 상세 정보를 성공적으로 삭제했습니다.");
    }

    @GetMapping("/providers")
    @Operation(summary = "모델 공급사 목록 조회", description = "모델 공급사 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 공급사 목록 조회 성공"),
    })
    public AxResponseEntity<PageResponse<GetModelPrvdRes>> getModelProviders() {
        log.info("모델 공급사 목록 조회 요청: {}");

        PageResponse<GetModelPrvdRes> modelCtlg = modelCtlgService.getModelProviders();

        log.info("모델 공급사 목록 조회 성공: 총 {}건, 현재 페이지 {}건", modelCtlg.getTotalElements(), modelCtlg.getPageable().getPage());

        return AxResponseEntity.ok(modelCtlg, "모델 공급사 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/types")
    @Operation(summary = "모델 타입 목록 조회", description = "모델 타입 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 타입 목록 조회 성공"),
    })
    public AxResponseEntity<GetModelTypesRes> getModelTypes() {
        log.info("모델 타입 목록 조회 요청");

        GetModelTypesRes modelTypes = modelCtlgService.getModelTypes();

        log.info("모델 타입 목록 조회 성공: {}", modelTypes);

        return AxResponseEntity.ok(modelTypes, "모델 타입 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/tags")
    @Operation(summary = "모델 태그 목록 조회", description = "모델 태그 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 태그 목록 조회 성공"),
    })
    public AxResponseEntity<GetModelTagsRes> getModelTags() {
        log.info("모델 태그 목록 조회 요청");

        GetModelTagsRes modelTags = modelCtlgService.getModelTags();

        log.info("모델 태그 목록 조회 성공: 총 {}건, 현재 페이지 {}건", modelTags.getTags().size(), modelTags.getTags().size());

        return AxResponseEntity.ok(modelTags, "모델 태그 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 모델 Policy 설정
     *
     * @param id          모델 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     */
    @PostMapping("/{id}/policy")
    @Operation(summary = "모델 Policy 설정", description = "모델의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "모델 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<Void> setModelPolicy(
            @PathVariable(value = "id", required = true) @Parameter(description = "모델 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String id,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("모델 Policy 설정 요청 - id: {}, memberId: {}, projectName: {}", id, memberId, projectName);
        modelCtlgService.setModelPolicy(id, memberId, projectName);
        return AxResponseEntity.ok(null, "모델 Policy를 성공적으로 설정했습니다.");
    }

    /**
     * 모델 엔드포인트 Policy 설정
     *
     * @param id          모델 ID (필수)
     * @param endpointId  모델 엔드포인트 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     */
    @PostMapping("/{id}/policy/{endpoint_id}")
    @Operation(summary = "모델 엔드포인트 Policy 설정", description = "모델 엔드포인트의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "모델 엔드포인트 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<Void> setModelEndpointPolicy(
            @PathVariable(value = "id", required = true) @Parameter(description = "모델 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String id,
            @PathVariable(value = "endpoint_id", required = true) @Parameter(description = "모델 엔드포인트 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String endpointId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("모델 엔드포인트 Policy 설정 요청 - id: {}, endpoint_id: {}, memberId: {}, projectName: {}", id, endpointId,
                memberId, projectName);
        modelCtlgService.setModelEndpointPolicy(id, endpointId, memberId, projectName);
        return AxResponseEntity.ok(null, "모델 엔드포인트 Policy를 성공적으로 설정했습니다.");
    }

}
