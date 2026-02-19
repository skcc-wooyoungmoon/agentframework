package com.skax.aiplatform.controller.deploy;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.deploy.request.CreateApiReq;
import com.skax.aiplatform.dto.deploy.response.CreateApiRes;
import com.skax.aiplatform.dto.deploy.response.GetApiEndpointStatus;
import com.skax.aiplatform.service.deploy.ApiGwService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api-gw")
@RequiredArgsConstructor
@Tag(name = "API Gateway 관리", description = "API Gateway 관리 API")
public class ApiGwController {

    private final ApiGwService apiGwService;

    @PostMapping("/endpoint")
    @Operation(summary = "[임시용 사용금지] API 엔드포인트 생성", description = "API 엔드포인트를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 엔드포인트 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<CreateApiRes> createApiEndpoint(@RequestBody CreateApiReq request) {
        log.info("API 엔드포인트 생성 요청: {}", request);
        CreateApiRes response = apiGwService.createApiEndpoint(request);
        log.info("API 엔드포인트 생성 완료: {}", response);

        return AxResponseEntity.ok(response, "API 엔드포인트 생성 성공");
    }


    @GetMapping("/endpoint/{apiId}/check")
    @Operation(summary = "API 엔드포인트 발급 상태 조회", description = "API 엔드포인트를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 엔드포인트 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<GetApiEndpointStatus> checkApiEndpoint(@PathVariable String apiId) {
        log.info("API 엔드포인트 조회 요청: {}", apiId);
        GetApiEndpointStatus result = apiGwService.checkApiEndpoint(apiId);
        log.info("API 엔드포인트 조회 완료: {}", result);
        
        return AxResponseEntity.ok(result, "API 엔드포인트 조회 성공");
    }

    @PostMapping("/enpoint/{apiId}/retry")
    @Operation(summary = "API 엔드포인트 등록 재요청", description = "API 엔드포인트를 등록 재요청합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 엔드포인트 등록 재요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> postRetryApiEndpoint(@PathVariable String apiId) {
        log.info("API 엔드포인트 등록 재요청 요청: {}", apiId);
        apiGwService.postRetryApiEndpoint(apiId);
        log.info("API 엔드포인트 등록 재요청 완료: {}", apiId);
        return AxResponseEntity.ok(null, "API 엔드포인트 등록 재요청 성공");
    }

    @GetMapping("/statistics/{servingId}")
    @Operation(summary = "API 엔드포인트 통계", description = "API 엔드포인트를 통계합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 엔드포인트 통계 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<ApiStatistics>> getApiEndpointStatistics(@PathVariable String servingId, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        log.info("API 엔드포인트 통계 요청: {}", servingId);
        List<ApiStatistics> result = apiGwService.getApiEndpointStatistics(servingId, startDate, endDate);
        log.info("API 엔드포인트 통계 완료: {}", result);
        
        return AxResponseEntity.ok(result, "API 엔드포인트 통계 성공");
    }

}
