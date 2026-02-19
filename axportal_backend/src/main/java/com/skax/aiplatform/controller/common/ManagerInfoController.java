package com.skax.aiplatform.controller.common;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.common.response.ManagerInfoRes;
import com.skax.aiplatform.service.common.ManagerInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자 정보 조회 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/common/manager-info")
@RequiredArgsConstructor
@Tag(name = "Common 관련", description = "관리자 정보 조회 API")
public class ManagerInfoController {

    private final ManagerInfoService managerInfoService;

    /**
     * 관리자 정보 조회
     * 
     * @param type 조회 타입 ("memberId" 또는 "uuid")
     * @param createdBy 생성자 ID (memberId 또는 uuid, optional)
     * @param updatedBy 수정자 ID (memberId 또는 uuid, optional)
     * @return 관리자 정보
     */
    @GetMapping
    @Operation(
            summary = "관리자 정보 조회",
            description = "createdBy 또는 updatedBy로 관리자 정보를 조회합니다. createdBy가 있으면 우선적으로 조회하고, 없으면 updatedBy로 조회합니다. createdBy, createdAt, updatedBy, updatedAt 정보를 포함합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "관리자를 찾을 수 없음")
    })
    public AxResponseEntity<ManagerInfoRes> getManagerInfo(
            @Parameter(description = "조회 타입 (memberId 또는 uuid)", required = true, example = "memberId")
            @RequestParam String type,
            @Parameter(description = "생성자 ID (memberId 또는 uuid, optional)", example = "SGO1032949")
            @RequestParam(required = false) String value) {
        
        log.info("관리자 정보 조회 요청 - type: {}, value: {}", type, value);
        
        ManagerInfoRes managerInfo = managerInfoService.getManagerInfo(type, value);
        
        return AxResponseEntity.ok(managerInfo, "관리자 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 관리자 정보 bulk 조회
     * 
     * @param type 조회 타입 ("memberId" 또는 "uuid")
     * @param values 조회 값 목록 (memberId 또는 uuid 목록)
     * @return 관리자 정보 목록
     */
    @GetMapping("/bulk")
    @Operation(
            summary = "관리자 정보 bulk 조회",
            description = "여러 개의 memberId 또는 uuid로 관리자 정보를 한 번에 조회합니다. 파라미터 형식: type=memberId&values=value1&values=value2"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자 정보 bulk 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public AxResponseEntity<List<ManagerInfoRes>> getManagerInfoBulk(
            @Parameter(description = "조회 타입 (memberId 또는 uuid)", required = true, example = "memberId")
            @RequestParam String type,
            @Parameter(description = "조회 값 목록 (memberId 또는 uuid 목록)", required = true, example = "SGO1032949")
            @RequestParam List<String> values) {
        
        log.info("관리자 정보 bulk 조회 요청 - type: {}, values count: {}", type, values != null ? values.size() : 0);
        
        List<ManagerInfoRes> managerInfoList = managerInfoService.getManagerInfoBulk(type, values);
        
        return AxResponseEntity.ok(managerInfoList, "관리자 정보를 성공적으로 조회했습니다.");
    }
}


