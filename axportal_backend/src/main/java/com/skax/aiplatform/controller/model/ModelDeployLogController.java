package com.skax.aiplatform.controller.model;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.log.response.ModelHistoryRecordRes;
import com.skax.aiplatform.dto.log.response.ModelHistoryRes;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.service.model.ModelDeployLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 모델 배포 로그 컨트롤러
 *
 * <p>
 * 모델 배포와 관련된 사용 이력 및 로그를 조회하는 API를 제공합니다.
 * SKTAI History API를 통해 모델 사용 이력을 조회합니다.
 * </p>
 *
 * @author System
 * @version 1.0.0
 * @since 2025-01-27
 */
@Slf4j
@RestController
@RequestMapping("/modelDeployLog")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS })
@Tag(name = "Model Deploy Log", description = "모델 배포 로그 관리 API")
public class ModelDeployLogController {

    private final ModelDeployLogService modelDeployLogService;

    @GetMapping("/history")
    @Operation(summary = "모델 사용 이력 조회", description = "모델 사용 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 사용 이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<ModelHistoryRecordRes>> getModelHistoryList(
            @Parameter(description = "필드 선택 (콤마 구분)", example = "fields") @RequestParam(value = "fields", required = false) String fields,

            @Parameter(description = "오류 로그만 조회 여부", example = "false") @RequestParam(value = "error_logs", required = false) Boolean errorLogs,

            @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-01") @RequestParam("from_date") String fromDate,

            @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-30") @RequestParam("to_date") String toDate,

            @Parameter(description = "페이지 번호 (1부터 시작)", required = true, example = "1") @RequestParam("page") Integer page,

            @Parameter(description = "페이지당 항목 수", required = true, example = "20") @RequestParam("size") Integer size,

            @Parameter(description = "필터 (key:value,...)", example = "project_id:24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색 (key:*value*...)", example = "request_time:*2025-07*") @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "정렬 (field,order)", example = "request_time,asc") @RequestParam(value = "sort", required = false) String sort) {

        log.info("모델 사용 이력 조회 요청 - 필드: {}, 오류로그: {}, 시작일: {}, 종료일: {}, 페이지: {}, 크기: {}, 필터: {}, 검색: {}, 정렬: {}",
                fields, errorLogs, fromDate, toDate, page, size, filter, search, sort);

        PageResponse<ModelHistoryRecordRes> response = modelDeployLogService.getModelHistoryList(
                fields, errorLogs, fromDate, toDate, page, size, filter, search, sort);

        return AxResponseEntity.okPage(response, "모델 사용 이력을 성공적으로 조회했습니다.");
    }

    @GetMapping("/history/download")
    @Operation(summary = "모델 사용 이력 CSV 다운로드", description = "모델 사용 이력을 CSV 파일로 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<byte[]> downloadModelHistoryCsv(
            @Parameter(description = "필드 선택 (콤마 구분)", example = "fields") @RequestParam(value = "fields", required = false) String fields,

            @Parameter(description = "오류 로그만 조회 여부", example = "false") @RequestParam(value = "error_logs", required = false) Boolean errorLogs,

            @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-01") @RequestParam("from_date") String fromDate,

            @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-30") @RequestParam("to_date") String toDate,

            @Parameter(description = "필터 (key:value,...)", example = "project_id:24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색 (key:*value*...)", example = "request_time:*2025-07*") @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "정렬 (field,order)", example = "request_time,asc") @RequestParam(value = "sort", required = false) String sort) {

        log.info("모델 사용 이력 CSV 다운로드 요청 - 필드: {}, 오류로그: {}, 시작일: {}, 종료일: {}, 필터: {}, 검색: {}, 정렬: {}",
                fields, errorLogs, fromDate, toDate, filter, search, sort);

        // page=1, size=5000으로 고정하여 모든 데이터 조회
        PageResponse<ModelHistoryRecordRes> pageResponse = modelDeployLogService.getModelHistoryList(
                fields, errorLogs, fromDate, toDate, 1, 5000, filter, search, sort);

        // PageResponse에서 데이터 추출하여 ModelHistoryRes 생성
        ModelHistoryRes response = ModelHistoryRes.builder()
                .data(pageResponse.getContent())
                .build();

        // CSV 데이터 생성
        byte[] csvData = modelDeployLogService.generateCsvData(response);

        // 파일명 생성 (현재 날짜 포함)
        String fileName = String.format("model_history_%s_to_%s.csv", fromDate, toDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(csvData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}
