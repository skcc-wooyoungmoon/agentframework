package com.skax.aiplatform.controller.eval;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.skax.aiplatform.client.datumo.api.dto.response.TaskListResponse;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.service.eval.EvalService;

/**
 * Evaluation Controller
 *
 * <p>
 * 평가(Evaluation) 관련 API를 제공하는 컨트롤러입니다.
 * Datumo 시스템과 연동하여 평가 Task 목록을 조회하는 기능을 포함합니다.
 * </p>
 *
 * @author System
 * @since 2025-01-27
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/eval")
@Tag(name = "평가 관리", description = "평가 Task 관리 API")
public class EvalController {

    private final EvalService evalService;

    /**
     * Task 목록 조회
     *
     * <p>
     * Datumo 시스템에 로그인하여 지정된 조건으로 Task 목록을 조회합니다.
     * 하드코딩된 인증 정보를 사용하여 자동으로 로그인을 수행합니다.
     * </p>
     *
     * @param group     그룹
     * @param category  Task 카테고리 (예: JUDGE, EVALUATION 등)
     * @param page      페이지 번호 (1부터 시작, 기본값: 1)
     * @param pageSize  페이지당 항목 수 (기본값: 12)
     * @param search    검색어 (선택적)
     * @return Task 목록 조회 결과
     */
    @GetMapping("/tasks")
    @Operation(summary = "Task 목록 조회", description = "Datumo 시스템에서 평가 Task 목록을 조회합니다. 하드코딩된 인증 정보로 자동 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public AxResponseEntity<TaskListResponse> getTaskList(
            @Parameter(description = "그룹", required = true, example = "435") @RequestParam String group,

            @Parameter(description = "Task 카테고리", required = true, example = "JUDGE") @RequestParam String category,

            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "페이지당 항목 수", example = "12") @RequestParam(defaultValue = "12") Integer pageSize,

            @Parameter(description = "검색어", example = "RAGAS") @RequestParam(required = false) String search,

            @Parameter(description = "Authorization 헤더 (Bearer 토큰)", required = true) @RequestHeader("Authorization") String authorization) {

        log.info("Task 목록 조회 API 호출 - 그룹: {}, 카테고리: {}, 페이지: {}/{}, 검색어: {}",
                group, category, page, pageSize, search);

        // Authorization 헤더에서 Bearer 토큰 추출
        String accessToken = extractBearerToken(authorization);

        TaskListResponse response = evalService.getTaskList(accessToken, group, category, page, pageSize, search);

        log.info("Task 목록 조회 API 응답 - 전체 데이터: {}, 전체 페이지: {}",
                response.getTotalDataCount(), response.getTotalPageCount());

        return AxResponseEntity.success(response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰을 추출합니다.
     *
     * @param authorization Authorization 헤더 값 (예: "Bearer
     *                      eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
     * @return 추출된 토큰 값
     * @throws IllegalArgumentException Authorization 헤더가 Bearer 형식이 아닌 경우
     */
    private String extractBearerToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            throw new IllegalArgumentException("Authorization 헤더가 비어있습니다.");
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더는 'Bearer ' 형식이어야 합니다.");
        }

        String token = authorization.substring(7).trim(); // "Bearer " (7자) 제거
        if (token.isEmpty()) {
            throw new IllegalArgumentException("토큰 값이 비어있습니다.");
        }

        return token;
    }
}
