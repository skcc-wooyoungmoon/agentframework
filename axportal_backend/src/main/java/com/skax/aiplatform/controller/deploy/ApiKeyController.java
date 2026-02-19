package com.skax.aiplatform.controller.deploy;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.deploy.request.CreateApiKeyReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyListReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyStaticReq;
import com.skax.aiplatform.dto.deploy.request.UpdateApiKeyQuotaReq;
import com.skax.aiplatform.dto.deploy.response.GetApiKeyRes;
import com.skax.aiplatform.service.deploy.ApiKeyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/apiKeys")
@RequiredArgsConstructor
@Tag(name = "API Key", description = "API Key API")
public class ApiKeyController {

    private final ApiKeyService apigwService;

    @GetMapping
    @Operation(summary = "API Key 목록 조회", description = "API Key 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<GetApiKeyRes>> getApiKeys(GetApiKeyListReq request) throws Exception {
        log.info("API Key 목록 조회 요청: {}", request);
        PageResponse<GetApiKeyRes> response = apigwService.getApiKeys("USER", request);
        log.info("API Key 목록 조회 완료: {}", response);
        return AxResponseEntity.okPage(response, "API Key 목록 조회 성공");
    }

    @GetMapping("/mgmt/admin")
    @Operation(summary = "API Key 목록 조회(관리자용)", description = "API Key 목록을 조회합니다.(관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 목록 조회(관리자용) 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<GetApiKeyRes>> getAdminApiKeys(GetApiKeyListReq request) throws Exception {
        log.info("API Key 목록 조회(관리자용) 요청: {}", request);
        PageResponse<GetApiKeyRes> response = apigwService.getApiKeys("ADMIN", request);
        log.info("API Key 목록 조회(관리자용) 완료: {}", response);
        return AxResponseEntity.okPage(response, "API Key 목록 조회 성공");
    }

    @GetMapping("/{id}")
    @Operation(summary = "API Key 조회", description = "API Key를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<GetApiKeyRes> getApiKey(@PathVariable String id) throws Exception {
        log.info("API Key 조회 요청: {}", id);
        GetApiKeyRes response = apigwService.getApiKey(id);
        log.info("API Key 조회 완료: {}", response);
        return AxResponseEntity.ok(response, "API Key 조회 성공");
    }

    @GetMapping("/{id}/static")
    @Operation(summary = "API Key 상세 통계 조회", description = "API Key를 상세 통계 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 상세 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<ApiStatistics>> getApiKeyStatic(@PathVariable String id, GetApiKeyStaticReq request) {
        log.info("API Key 상세 통계 조회 요청: {}", id, request);
        List<ApiStatistics> response = apigwService.getApiKeyStatic(id, request);
        log.info("API Key 상세 통계 조회 완료: {}", response);
        return AxResponseEntity.ok(response, "API Key 상세 통계 조회 성공");
    }

    @PostMapping
    @Operation(summary = "API Key 생성", description = "API Key를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<GetApiKeyRes> createApiKey(@RequestBody CreateApiKeyReq request) throws Exception {
        log.info("API Key 생성 요청: {}", request);
        GetApiKeyRes response = apigwService.createApiKey(request);
        log.info("API Key 생성 완료: {}", response);
        return AxResponseEntity.ok(response, "API Key 생성 성공");
    }

    @PutMapping("/{id}/quota")
    @Operation(summary = "API Key 할당량 수정", description = "API Key의 할당량을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 할당량 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<GetApiKeyRes> updateApiKeyQuota(@PathVariable String id,
            @RequestBody UpdateApiKeyQuotaReq request) {
        log.info("API Key 할당량 수정 요청: {}", id);
        apigwService.updateApiKeyQuota(id, request);
        log.info("API Key 할당량 수정 완료: {}", id);
        return AxResponseEntity.ok(null, "API Key 수정 성공");
    }

    @PutMapping("/{id}/expire")
    @Operation(summary = "API Key 사용 차단", description = "API Key를 만료합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 사용 차단 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> updateApiKeyExpire(@PathVariable String id) {
        log.info("API Key 사용 차단 요청: {}", id);
        apigwService.updateApiKeyExpire(id);
        log.info("API Key 사용 차단 완료: {}", id);
        return AxResponseEntity.ok(null, "API Key 사용 차단 성공");
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "API Key 차단 해제", description = "API Key의 만료일을 9999년 12월 31일로 설정하여 차단을 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 차단 해제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> restoreApiKey(@PathVariable String id) {
        log.info("API Key 차단 해제 요청: {}", id);
        apigwService.restoreApiKey(id);
        log.info("API Key 차단 해제 완료: {}", id);
        return AxResponseEntity.ok(null, "API Key 차단 해제 성공");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "API Key 삭제", description = "API Key를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API Key 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteApiKey(@PathVariable String id) {
        log.info("API Key 삭제 요청: {}", id);
        apigwService.deleteApiKey(id);
        log.info("API Key 삭제 완료: {}", id);
        return AxResponseEntity.ok(null, "API Key 삭제 성공");
    }

}
