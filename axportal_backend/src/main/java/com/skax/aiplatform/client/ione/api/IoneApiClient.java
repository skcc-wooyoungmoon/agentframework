package com.skax.aiplatform.client.ione.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.skax.aiplatform.client.ione.api.dto.request.ApiDeleteRequest;
import com.skax.aiplatform.client.ione.api.dto.request.ApiRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.request.ApiUpdateRequest;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupDeleteRequest;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupUpdateRequest;
import com.skax.aiplatform.client.ione.api.dto.response.ApiDeleteResponse;
import com.skax.aiplatform.client.ione.api.dto.response.ApiInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.ApiListResultWithPagination;
import com.skax.aiplatform.client.ione.api.dto.response.ApiRegistResponse;
import com.skax.aiplatform.client.ione.api.dto.response.ApiServerGroupInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.ApiServerGroupListResult;
import com.skax.aiplatform.client.ione.api.dto.response.CommonResult;
import com.skax.aiplatform.client.ione.api.dto.response.PublishWorkInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.PublishWorkListResult;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupDeleteResponse;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupListResult;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupRegistResult;
import com.skax.aiplatform.client.ione.common.dto.InfRequestBody;
import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.config.IoneFeignConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * iONE API Common 클라이언트
 * 
 * <p>iONE Gateway 등록된 API 정보 관리 API와의 통신을 담당하는 Feign Client입니다.
 * API 등록, 수정, 삭제, 조회 및 업무 코드 관리, 작업 요청 관리 기능을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@FeignClient(
    name = "ione-api-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneApiClient {

    // ========== API 목록/정보 조회 ==========
    
    /**
     * [API-COM-001] API 목록 조회
     * 
     * @return API 목록 결과
     */
    @GetMapping("/admin/intf/v1/system/api/list.idt")
    @Operation(summary = "[API-COM-001] API 목록 조회", description = "Gateway에 등록된 API 목록을 조회합니다.")
    ApiListResultWithPagination getApiList();

    /**
     * [API-COM-002] API 정보 조회
     * 
     * @param apiId 조회할 API ID
     * @return API 상세 정보
     */
    @GetMapping("/admin/intf/v1/system/api/{apiId}/get.idt")
    @Operation(summary = "[API-COM-002] API 정보 조회", description = "API ID로 해당 API에 대한 상세 정보를 조회합니다.")
    InfResponseBody<ApiInfoResult> getApiInfo(@Parameter(description = "조회할 API ID") @PathVariable("apiId") String apiId);

    // ========== API 등록/수정/삭제 ==========
    
    /**
     * [API-COM-003] API 등록
     * 
     * @param request API 등록 요청 정보
     * @return 등록 결과
     */
    @PostMapping("/admin/intf/v1/system/api/publish/api/regist.idt")
    @Operation(summary = "[API-COM-003] API 등록", description = "새로운 API를 Gateway에 등록합니다.")
    InfResponseBody<ApiRegistResponse> registApi(@RequestBody InfRequestBody<ApiRegistRequest> request);

    /**
     * [API-COM-004] API 수정
     * 
     * @param request API 수정 요청 정보
     * @return 수정 결과
     */
    @PatchMapping("/admin/intf/v1/system/api/publish/api/update.idt")
    @Operation(summary = "[API-COM-004] API 수정", description = "등록된 API 정보를 수정합니다.")
    CommonResult updateApi(@RequestBody ApiUpdateRequest request);

    /**
     * [API-COM-005] API 삭제
     * 
     * @param request API 삭제 요청 정보
     * @return 삭제 결과
     */
    @PostMapping("/admin/intf/v1/system/api/publish/api/delete.idt")
    @Operation(summary = "[API-COM-005] API 삭제", description = "등록된 API를 삭제합니다.")
    InfResponseBody<ApiDeleteResponse> deleteApi(@RequestBody InfRequestBody<ApiDeleteRequest> request);

    // ========== API 서버 그룹 관리 ==========
    
    /**
     * [API-COM-006] API 서버 그룹 목록 조회
     * 
     * @return API 서버 그룹 목록
     */
    @GetMapping("/admin/intf/v1/system/api/svrgrp/list.idt")
    @Operation(summary = "[API-COM-006] API 서버 그룹 목록 조회", description = "API 서버 그룹 목록을 조회합니다.")
    ApiServerGroupListResult getApiServerGroupList();

    /**
     * [API-COM-007] API 서버 그룹 정보 조회
     * 
     * @param apiSvrGrpId API 서버 그룹 ID
     * @return API 서버 그룹 상세 정보
     */
    @GetMapping("/admin/intf/v1/system/api/svrgrp/{apiSvrGrpId}/get.idt")
    @Operation(summary = "[API-COM-007] API 서버 그룹 정보 조회", description = "API 서버 그룹 상세 정보를 조회합니다.")
    ApiServerGroupInfoResult getApiServerGroupInfo(@Parameter(description = "API 서버 그룹 ID") @PathVariable("apiSvrGrpId") String apiSvrGrpId);

    // ========== 업무 코드 관리 ==========
    
    /**
     * [API-COM-008] 업무 코드 등록
     * 
     * @param request 업무 코드 등록 요청 정보
     * @return 등록 결과
     */
    @PostMapping("/admin/intf/v1/system/api/workGrp/regist.idt")
    @Operation(summary = "[API-COM-008] 업무 코드 등록", description = "새로운 업무 코드를 등록합니다.")
    InfResponseBody<WorkGroupRegistResult> registWorkGroup(@RequestBody InfRequestBody<WorkGroupRegistRequest> request);

    /**
     * [API-COM-009] 업무 코드 조회
     * 
     * @return 업무 코드 목록
     */
    @GetMapping("/admin/intf/v1/system/api/workGrp/list.idt")
    @Operation(summary = "[API-COM-009] 업무 코드 조회", description = "업무 코드 목록을 조회합니다.")
    WorkGroupListResult getWorkGroupList();

    /**
     * [API-COM-010] 업무 코드 삭제
     * 
     * @param request 업무 코드 삭제 요청 정보
     * @return 삭제 결과
     */
    @PostMapping("/admin/intf/v1/system/api/workGrp/delete.idt")
    @Operation(summary = "[API-COM-010] 업무 코드 삭제", description = "업무 코드를 삭제합니다.")
    InfResponseBody<WorkGroupDeleteResponse> deleteWorkGroup(@RequestBody InfRequestBody<WorkGroupDeleteRequest> request);

    /**
     * [API-COM-011] 업무 코드 수정
     * 
     * @param request 업무 코드 수정 요청 정보
     * @return 수정 결과
     */
    @PutMapping("/admin/intf/v1/system/api/workGrp/update.idt")
    @Operation(summary = "[API-COM-011] 업무 코드 수정", description = "업무 코드 정보를 수정합니다.")
    CommonResult updateWorkGroup(@RequestBody WorkGroupUpdateRequest request);

    // ========== 작업 요청 관리 ==========
    
    /**
     * [API-COM-012] 작업 재 요청
     * 
     * @param infWorkSeq 작업 순번
     * @return 재 요청 결과
     */
    @PostMapping("/admin/intf/v1/system/api/publish/{infWorkSeq}/rePub.idt")
    @Operation(summary = "[API-COM-012] 작업 재 요청", description = "실패한 작업을 재 요청합니다.")
    InfResponseBody<ApiRegistResponse> republishWork(@PathVariable("infWorkSeq") String infWorkSeq, @RequestBody InfRequestBody<Void> request);

    /**
     * [API-COM-013] 작업 요청 취소
     * 
     * @param infWorkSeq 작업 순번
     * @return 취소 결과
     */
    @PostMapping("/admin/intf/v1/system/api/publish/{infWorkSeq}/cancle.idt")
    @Operation(summary = "[API-COM-013] 작업 요청 취소", description = "대기 중인 작업 요청을 취소합니다.")
    CommonResult cancelWork(@Parameter(description = "작업 순번") @PathVariable("infWorkSeq") String infWorkSeq);

    /**
     * [API-COM-014] 작업 요청 결과 목록 조회
     * 
     * @return 작업 요청 결과 목록
     */
    @GetMapping("/admin/intf/v1/system/api/publish/api/list.idt")
    @Operation(summary = "[API-COM-014] 작업 요청 결과 목록 조회", description = "작업 요청 결과 목록을 조회합니다.")
    PublishWorkListResult getPublishWorkList();

    /**
     * [API-COM-015] 작업 요청 결과 조회
     * 
     * @param infWorkSeq 작업 순번
     * @return 작업 요청 결과 상세 정보
     */
    @GetMapping("/admin/intf/v1/system/api/publish/api/{infWorkSeq}/get.idt")
    @Operation(summary = "[API-COM-015] 작업 요청 결과 조회", description = "작업 요청 결과 상세 정보를 조회합니다.")
    InfResponseBody<PublishWorkInfoResult> getPublishWorkInfo(@Parameter(description = "작업 순번") @PathVariable("infWorkSeq") String infWorkSeq);
}
