package com.skax.aiplatform.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.UserUsageMgmtExportDataReq;
import com.skax.aiplatform.dto.admin.request.UserUsageMgmtHeaderInfoReq;
import com.skax.aiplatform.dto.admin.response.ProjectRes;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtRes;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtStatsRes;
import com.skax.aiplatform.service.admin.UserUsageMgmtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/user-usage-mgmt")
@RequiredArgsConstructor
@Tag(name = "UserUsageMgmtController", description = "사용자 사용량 관리 API")
public class UserUsageMgmtController {

    private final UserUsageMgmtService userUsageMgmtService;

    @GetMapping
    @Operation(summary = "사용자 사용량 관리 조회", description = "사용자 사용량 관리를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 사용량 관리 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자 사용량 관리를 찾을 수 없음")
    })
    public AxResponseEntity<PageResponse<UserUsageMgmtRes>> getUserUsageMgmts(
        @RequestParam(value = "dateType", required = false) 
        @Parameter(description = "날짜 타입", example = "created") String dateType,
        @RequestParam(value = "projectName", required = false) 
        @Parameter(description = "프로젝트명", example = "project_000001") String projectName,
        @RequestParam(value = "result", required = false) 
        @Parameter(description = "결과", example = "SUCCESS") String result,
        @RequestParam(value = "searchType", required = false) 
        @Parameter(description = "검색 타입", example = "userId") String searchType,
        @RequestParam(value = "searchValue", required = false) 
        @Parameter(description = "검색 값", example = "user123") String searchValue,
        @RequestParam(value = "fromDate", required = false) 
        @Parameter(description = "시작 날짜", example = "2025-01-01") String fromDate,
        @RequestParam(value = "toDate", required = false) 
        @Parameter(description = "종료 날짜", example = "2025-12-31") String toDate,
        @PageableDefault(size = 20) Pageable pageable) {
        log.info("사용자 사용량 관리 조회 요청 - dateType: {}, projectName: {}, result: {}, searchType: {}, searchValue: {}, fromDate: {}, toDate: {}, page: {}, size: {}", 
                dateType, projectName, result, searchType, searchValue, fromDate, toDate, pageable.getPageNumber(), pageable.getPageSize());

        Page<UserUsageMgmtRes> userUsageMgmts = userUsageMgmtService.getUserUsageMgmts(
            dateType, projectName, result, searchType, searchValue, fromDate, toDate, pageable);
        PageResponse<UserUsageMgmtRes> pageResponse = PageResponse.from(userUsageMgmts);

        return AxResponseEntity.ok(pageResponse, "사용자 사용량 관리를 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 사용량 관리 상세 조회", description = "특정 사용자 사용량 관리의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 사용량 관리 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자 사용량 관리를 찾을 수 없음")
    })
    public AxResponseEntity<UserUsageMgmtRes> getUserUsageMgmtById(
        @PathVariable @Parameter(description = "사용자 사용량 관리 ID", example = "1") Long id) {
        log.info("사용자 사용량 관리 상세 조회 요청 - id: {}", id);

        UserUsageMgmtRes userUsageMgmt = userUsageMgmtService.getUserUsageMgmtById(id);

        return AxResponseEntity.ok(userUsageMgmt, "사용자 사용량 관리 상세 정보를 성공적으로 조회했습니다.");
    }



    @PostMapping("/export/data")
    @Operation(summary = "커스텀 데이터 Excel 내보내기", description = "커스텀 데이터를 Excel 파일로 내보냅니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 데이터 Excel 내보내기 성공")
    public ResponseEntity<byte[]> exportUserUsageMgmtsWithData(
        @RequestBody UserUsageMgmtExportDataReq request) {
        List<UserUsageMgmtHeaderInfoReq> headerInfos =
                request.getHeaders() != null ? request.getHeaders() : Collections.emptyList();
        List<Map<String, Object>> requestData =
                request.getData() != null ? request.getData() : Collections.emptyList();

        log.info("커스텀 데이터 Excel 내보내기 요청 - 헤더: {}개, 데이터: {}건",
                headerInfos.size(), requestData.size());


        // Convert headers to Map format
        List<Map<String, Object>> headerMaps = headerInfos.stream()
            .map(header -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("key", header.getHeaderName());
                map.put("value", header.getField());
                return map;
            })
            .collect(java.util.stream.Collectors.toList());
        
        byte[] excelData = userUsageMgmtService.exportUserUsageMgmtsWithCustomData(
            headerMaps, requestData);
        
        String fileName = "custom_user_usage_mgmt_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + 
            java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
            .headers(headers)
            .body(excelData);
    }

    @GetMapping("/stats")
    @Operation(
        summary = "사용자 사용량 관리 통계 조회",
        description = "searchType에 따라 로그인 성공 건수, API 통계, 실패 요약, 메뉴 사용량을 조회합니다. (month: 월별, week: 주별, day: 일별)"
    )
    @ApiResponse(responseCode = "200", description = "사용자 사용량 관리 통계 조회 성공")
    @ApiResponse(responseCode = "404", description = "사용자 사용량 관리 통계를 찾을 수 없음")
    public AxResponseEntity<UserUsageMgmtStatsRes> getUserUsageMgmtStats(
        @RequestParam @Parameter(description = "조회 조건", example = "month") String searchType,
        @RequestParam @Parameter(description = "선택된 날짜 (month: YYYY-MM, week/day: YYYY-MM-DD)", example = "2025-01-22") String selectedDate,
        @RequestParam @Parameter(description = "선택된 프로젝트", example = "project_000001") String projectType) {
        log.info("사용자 사용량 관리 통계 조회 요청 - searchType: {}, selectedDate: {}, projectType: {}",
                searchType, selectedDate, projectType);

        UserUsageMgmtStatsRes stats = userUsageMgmtService.getUserUsageMgmtStats(searchType, selectedDate, projectType);

        return AxResponseEntity.ok(stats, "사용자 사용량 관리 통계를 성공적으로 조회했습니다.");
    }

    /**
     * 전체 프로젝트 목록 조회
     * 
     * @return 프로젝트 목록
     * @author sonmunwoo
     * @since 2025-10-21
     */
    @GetMapping("/common/projects")
    @Operation(summary = "프로젝트 목록 조회", description = "전체 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public AxResponseEntity<List<ProjectRes>> getProjects() {
        log.info("프로젝트 목록 조회 요청");

        List<ProjectRes> projects = userUsageMgmtService.getAllProjects();

        return AxResponseEntity.ok(projects, "프로젝트 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/common/projects/search")
    @Operation(summary = "프로젝트 목록 검색", description = "프로젝트명을 기준으로 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public AxResponseEntity<List<ProjectRes>> getProjectsByName(
            @RequestParam("projectName")
            @Parameter(description = "검색할 프로젝트명", example = "project_000001") String projectName) {
        log.info("프로젝트 목록 검색 요청 - projectName: {}", projectName);

        List<ProjectRes> projects = userUsageMgmtService.getProjectsByName(projectName);

        return AxResponseEntity.ok(projects, "프로젝트 목록을 성공적으로 조회했습니다.");
    }

}
