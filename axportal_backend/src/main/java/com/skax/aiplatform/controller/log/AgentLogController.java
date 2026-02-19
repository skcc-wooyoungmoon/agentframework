package com.skax.aiplatform.controller.log;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.log.response.AgentLogRes;
import com.skax.aiplatform.service.log.AgentLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/agentLog")
@RequiredArgsConstructor
@Tag(name = "Agent Log Management", description = "Agent Log 관리 API")
public class AgentLogController {

    private final AgentLogService agentLogService;

    /**
     * Agent History 목록 조회
     * 
     * @param fromDate 시작 날짜 (YYYY-MM-DD)
     * @param toDate 종료 날짜 (YYYY-MM-DD)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param fields 필드 선택 (콤마 구분)
     * @param errorLogs 오류 로그만 조회 여부
     * @param additionalHistoryOption 추가 히스토리 옵션 (콤마 구분)
     * @param filter 필터 (key:value,...)
     * @param search 검색 (key:*value*...)
     * @param sort 정렬 (field,order)
     * @return Agent History 목록 (페이지네이션 포함)
     */
    @GetMapping("/history/agentList")
    @Operation(
        summary = "Agent History 목록 조회",
        description = "Agent 앱의 실행 이력을 조회합니다. 시간 범위, 필드/필터/검색/정렬을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent History 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<AgentLogRes>> getAgentLogList(

            @RequestParam(value = "fromDate", required = true)
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", example = "2025-03-01") String fromDate,
            
            @RequestParam(value = "toDate", required = true)
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", example = "2025-09-03") String toDate,
            
            @RequestParam(value = "page", defaultValue = "1")
            @Parameter(description = "페이지 번호", example = "1") Integer page,
            
            @RequestParam(value = "size", defaultValue = "12")
            @Parameter(description = "페이지 크기", example = "12") Integer size,
            
            @RequestParam(value = "fields", required = false)
            @Parameter(description = "필드 선택 (콤마 구분)", example = "request_time,response_time") String fields,
            
            @RequestParam(value = "errorLogs", required = false)
            @Parameter(description = "오류 로그만 조회 여부", example = "false") Boolean errorLogs,
            
            @RequestParam(value = "additionalHistoryOption", required = false)

            @Parameter(description = "추가 히스토리 옵션 (콤마 구분)", example = "tracing,model,retrieval") String additionalHistoryOption,
            
            @RequestParam(value = "filter", required = false)
            @Parameter(description = "필터 (key:value,...)", example = "agent_app_id:f6e129f6-c09e-46c8-b8eb-59c") String filter,
            
            @RequestParam(value = "search", required = false)
            @Parameter(description = "검색 (key:*value*...)") String search,
            
            @RequestParam(value = "sort", required = false)
            @Parameter(description = "정렬 (field,order)", example = "request_time,asc") String sort) {
        
        log.info("Agent History 목록 조회 요청: fromDate={}, toDate={}, page={}, size={}", fromDate, toDate, page, size);
    
        // 오류 로그만 트랜젝션 세션 강제설정
        if(errorLogs){
            // 트랜젝션 세션 강제설정
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        }
        
        PageResponse<AgentLogRes> agentLogRes = agentLogService.getAgentLogList(
            fromDate, toDate, page, size, fields, errorLogs, 
            additionalHistoryOption, filter, search, sort);
        
        log.info("Agent History 목록 조회 완료");
        
        return AxResponseEntity.ok(agentLogRes, "Agent History 목록을 성공적으로 조회했습니다.");
    }
}