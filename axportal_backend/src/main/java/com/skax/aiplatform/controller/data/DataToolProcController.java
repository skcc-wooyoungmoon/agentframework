package com.skax.aiplatform.controller.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.response.DataToolProcDetailRes;
import com.skax.aiplatform.dto.data.response.DataToolProcRes;
import com.skax.aiplatform.service.data.DataToolProcService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터 도구 - 프로세서 관리 컨트롤러
 *
 * <p>데이터 도구 - 프로세서 관리 API 엔드포인트를 제공합니다.
 * 데이터 도구의 프로세서 목록 및 상세 정보 조회 기능을 포함합니다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/dataTool/processors")
@RequiredArgsConstructor
@Tag(name = "Data-tool Processors Management", description = "데이터 도구 - 프로세서 관리 API")
public class DataToolProcController {

    private final DataToolProcService dataToolProcService;

    /**
     * 데이터 도구 프로세서 목록 조회
     *
     * @param pageable 페이지 정보
     * @param sort 정렬 조건
     * @return Data-Tools Processors 응답 (페이지네이션 포함)
     */
    @GetMapping
    @Operation(
            summary = "Data-tool Processors 목록 조회",
            description = "Data-tool Processors 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data-tool Processors 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<DataToolProcRes>> getProcList(
            @PageableDefault(size = 12) Pageable pageable,
            @RequestParam(value = "sort", required = false, defaultValue = "created_at,desc")
            @Parameter(description = "정렬 조건") String sort,
            @RequestParam(value = "filter", required = false)
            @Parameter(description = "필터 조건") String filter,
            @RequestParam(value = "search", required = false)
            @Parameter(description = "검색 키워드") String search
            ) {
        log.info("Data-tool Processors 목록 조회 요청: page={}, size={}, sort={}, filter={}, search={}", pageable.getPageNumber(), pageable.getPageSize(), sort, filter, search);
        PageResponse<DataToolProcRes> dataToolProcRes = dataToolProcService.getProcList(pageable, sort, filter, search);
        log.info("Data-tool Processors 목록 조회 완료");
        return AxResponseEntity.okPage(dataToolProcRes, "Data-tool Processors 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 프로세서 상세 조회
     *
     * @param processorId 프로세서 ID
     * @return 프로세서 상세 정보
     */
    @GetMapping("/{processorId}")
    @Operation(
            summary = "프로세서 상세 조회",
            description = "UUID 기반으로 특정 프로세서 상세 정보를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로세서 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 프로세서가 존재하지 않음")
    })
    public AxResponseEntity<DataToolProcDetailRes> getDataProcById(
            @Parameter(description = "프로세서 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable("processorId") String processorId
    ) {
        log.info("Controller: 프로세서 상세 조회 API 호출 - processorId: {}", processorId);
        DataToolProcDetailRes dataToolProcResById = dataToolProcService.getProcById(processorId);
        return AxResponseEntity.ok(dataToolProcResById, "프로세서를 성공적으로 조회했습니다.");
    }
}
