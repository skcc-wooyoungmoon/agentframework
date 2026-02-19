package com.skax.aiplatform.client.sktai.history;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.history.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * SKTAI History API Feign Client
 *
 * <p>SKTAI 시스템의 모델, 에이전트, 문서 지능 서비스의 사용 이력과 통계를 조회하는 History API와의 통신을 담당하는 Feign Client입니다.
 * 시스템 모니터링, 사용 패턴 분석, 성능 추적을 위한 다양한 이력 데이터를 제공합니다.</p>
 *
 * <h3>주요 기능 영역:</h3>
 * <ul>
 *   <li><strong>모델 이력 관리</strong>: 모델 사용 이력 조회 및 통계 분석</li>
 *   <li><strong>에이전트 이력 관리</strong>: 에이전트 사용 이력 조회 및 통계 분석</li>
 *   <li><strong>문서 지능 통계</strong>: 문서 지능 서비스 사용 통계 분석</li>
 *   <li><strong>성능 모니터링</strong>: 서비스별 성능 지표 및 사용 패턴 추적</li>
 * </ul>
 *
 * <h3>API 엔드포인트 구성:</h3>
 * <ul>
 *   <li><strong>Model History</strong>: 모델 사용 이력 목록 조회 (1개 엔드포인트)</li>
 *   <li><strong>Model Stats</strong>: 모델 사용 통계 조회 (2개 엔드포인트 - 일반/테스트)</li>
 *   <li><strong>Agent History</strong>: 에이전트 사용 이력 목록 조회 (1개 엔드포인트)</li>
 *   <li><strong>Agent Stats</strong>: 에이전트 사용 통계 조회 (2개 엔드포인트 - 일반/테스트)</li>
 *   <li><strong>Doc Intelligence Stats</strong>: 문서 지능 사용 통계 조회 (1개 엔드포인트)</li>
 * </ul>
 *
 * <h3>공통 기능:</h3>
 * <ul>
 *   <li><strong>페이징 지원</strong>: 대용량 데이터의 효율적인 조회</li>
 *   <li><strong>필터링</strong>: 다양한 조건으로 데이터 필터링</li>
 *   <li><strong>검색</strong>: 키워드 기반 데이터 검색</li>
 *   <li><strong>정렬</strong>: 사용자 정의 정렬 기준 지원</li>
 *   <li><strong>날짜 범위</strong>: 특정 기간 데이터 조회</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 2.0
 * @see SktaiClientConfig Feign 클라이언트 설정
 */
@FeignClient(
    name = "sktai-history-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI History API", description = "SKTAI 모델/에이전트/문서지능 사용 이력 및 통계 관리")
public interface SktaiHistoryClient {

    /**
     * 모델 사용 이력 목록 조회
     *
     * <p>SKTAI 시스템의 모델 사용 이력을 조회합니다.
     * 모델 실행, API 호출, 성능 지표 등의 상세한 사용 기록을 제공합니다.</p>
     *
     * <h4>포함되는 이력 정보:</h4>
     * <ul>
     *   <li><strong>모델 실행 기록</strong>: 모델 호출 시간, 입력/출력 데이터, 처리 시간</li>
     *   <li><strong>성능 지표</strong>: 응답 시간, 처리량, 정확도, 리소스 사용량</li>
     *   <li><strong>오류 정보</strong>: 실패한 요청, 오류 메시지, 원인 분석</li>
     *   <li><strong>사용자 정보</strong>: 요청 사용자, 프로젝트, API 키</li>
     * </ul>
     *
     * <h4>필터링 옵션:</h4>
     * <ul>
     *   <li><strong>날짜 범위</strong>: 시작일/종료일 기반 필터링</li>
     *   <li><strong>성공/실패</strong>: 정상 처리 또는 오류 로그만 조회</li>
     *   <li><strong>모델별</strong>: 특정 모델의 사용 이력만 조회</li>
     *   <li><strong>사용자별</strong>: 특정 사용자/프로젝트의 사용 이력</li>
     * </ul>
     *
     * @param fields 응답에 포함할 필드 목록 (콤마로 구분된 문자열)
     * @param errorLogs 오류 로그 조회 여부 (true: 오류만, false: 성공만, null: 전체)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate 조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param page 페이지 번호 (1부터 시작, 필수)
     * @param size 페이지당 항목 수 (필수)
     * @param filter 필터 조건 (key:value,... 형식)
     * @param search 검색 조건 (key:*value*,... 형식)
     * @param sort 정렬 기준 (필드명,정렬방향 형식, 예: request_time,asc)
     * @return 모델 사용 이력 목록과 페이징 정보
     */
    @Operation(
        summary = "모델 사용 이력 목록 조회",
        description = "모델 사용 이력을 다양한 조건으로 조회합니다. 성능 지표, 오류 정보, 사용자 정보를 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/history/model/list")
    ModelHistoryRead getModelHistoryList(
        @Parameter(description = "필드 선택 (콤마 구분)", example = "fields")
        @RequestParam(value = "fields", required = false) String fields,

        @Parameter(description = "오류 로그만 조회 여부", example = "false")
        @RequestParam(value = "error_logs", required = false) Boolean errorLogs,

        @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-01")
        @RequestParam("from_date") String fromDate,

        @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-30")
        @RequestParam("to_date") String toDate,

        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(value = "page", required = false) Integer page,

        @Parameter(description = "페이지당 항목 수", example = "20")
        @RequestParam(value = "size", required = false) Integer size,

        @Parameter(description = "필터 (key:value,...)", example = "agent_app_id:f6e129f6-c09e-46c8-b8eb-59c")
        @RequestParam(value = "filter", required = false) String filter,

        @Parameter(description = "검색 (key:*value*...)", example = "request_time:*2025-07*")
        @RequestParam(value = "search", required = false) String search,

        @Parameter(description = "정렬 (field,order)", example = "request_time,asc")
        @RequestParam(value = "sort", required = false) String sort
    );

    /**
     * 모델 사용 통계 조회 (테스트)
     *
     * <p>모델 사용량에 대한 통계 정보를 조회합니다. (테스트 버전)
     * 지정된 그룹핑 기준으로 집계된 모델 사용 데이터를 제공합니다.</p>
     *
     * <h4>그룹핑 가능한 필드:</h4>
     * <ul>
     *   <li><strong>project_id</strong>: 프로젝트별 집계</li>
     *   <li><strong>app_id</strong>: 애플리케이션별 집계</li>
     *   <li><strong>model_type</strong>: 모델 타입별 집계</li>
     *   <li><strong>model_id</strong>: 개별 모델별 집계</li>
     *   <li><strong>user</strong>: 사용자별 집계</li>
     *   <li><strong>company</strong>: 회사별 집계</li>
     *   <li><strong>department</strong>: 부서별 집계</li>
     * </ul>
     *
     * <h4>제공되는 통계 정보:</h4>
     * <ul>
     *   <li><strong>사용량</strong>: 총 요청 수, 성공/실패 건수</li>
     *   <li><strong>성능</strong>: 평균 응답 시간, 처리량</li>
     *   <li><strong>리소스</strong>: CPU/메모리 사용량, 토큰 소비량</li>
     *   <li><strong>비용</strong>: 사용량 기반 비용 계산</li>
     * </ul>
     *
     * @param groupBy 그룹핑 필드 (콤마로 구분된 문자열, 필수)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate 조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param filter 필터 조건 (key:value,... 형식)
     * @param search 검색 조건 (key:*value*,... 형식)
     * @return 모델 사용 통계 데이터
     */
    @Operation(
        summary = "모델 사용 통계 조회 (테스트)",
        description = "모델 사용량에 대한 통계를 그룹핑하여 집계합니다. 테스트 버전입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/history/model/stats/test")
    ModelStatsRead getModelStatsTest(
        @Parameter(description = "그룹핑 필드 (콤마로 구분)", required = true,
                   example = "project_id,app_id,model_type,model_id,model_serving_id,agent_app_serving_id,api_key,company,department,user")
        @RequestParam(value = "group_by") String groupBy,

        @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-03-01")
        @RequestParam(value = "from_date") String fromDate,

        @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-03-03")
        @RequestParam(value = "to_date") String toDate,

        @Parameter(description = "필터 조건 (key:value,... 형식)")
        @RequestParam(value = "filter", required = false) String filter,

        @Parameter(description = "검색 조건 (key:*value*,... 형식)")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * 모델 사용 통계 조회
     *
     * <p>모델 사용량에 대한 통계 정보를 조회합니다.
     * 지정된 그룹핑 기준으로 집계된 모델 사용 데이터를 제공합니다.</p>
     *
     * <h4>그룹핑 가능한 필드:</h4>
     * <ul>
     *   <li><strong>project_id</strong>: 프로젝트별 집계</li>
     *   <li><strong>app_id</strong>: 애플리케이션별 집계</li>
     *   <li><strong>model_type</strong>: 모델 타입별 집계</li>
     *   <li><strong>model_id</strong>: 개별 모델별 집계</li>
     *   <li><strong>user</strong>: 사용자별 집계</li>
     *   <li><strong>company</strong>: 회사별 집계</li>
     *   <li><strong>department</strong>: 부서별 집계</li>
     * </ul>
     *
     * <h4>제공되는 통계 정보:</h4>
     * <ul>
     *   <li><strong>사용량</strong>: 총 요청 수, 성공/실패 건수</li>
     *   <li><strong>성능</strong>: 평균 응답 시간, 처리량</li>
     *   <li><strong>리소스</strong>: CPU/메모리 사용량, 토큰 소비량</li>
     *   <li><strong>비용</strong>: 사용량 기반 비용 계산</li>
     * </ul>
     *
     * @param groupBy 그룹핑 필드 (콤마로 구분된 문자열, 필수)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate 조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param filter 필터 조건 (key:value,... 형식)
     * @param search 검색 조건 (key:*value*,... 형식)
     * @return 모델 사용 통계 데이터
     */
    @Operation(
        summary = "모델 사용 통계 조회",
        description = "모델 사용량에 대한 통계를 그룹핑하여 집계합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/history/model/stats")
    ModelStatsRead getModelStats(
        @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-01")
        @RequestParam("start_date") String startDate,

        @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-30")
        @RequestParam("end_date") String endDate,

        @Parameter(description = "집계 단위 (daily, weekly, monthly)", example = "daily")
        @RequestParam(value = "granularity", required = false) String granularity,

        @Parameter(description = "프로젝트 ID 필터")
        @RequestParam(value = "project_id", required = false) String projectId
    );

    /**
     * Agent History 목록 조회
     *
     * <p>Agent 앱의 실행 이력을 조회합니다. 시간 범위, 프로젝트, 앱 ID, 필드 선택,
     * 오류 로그 여부, 추가 히스토리 옵션, 필터/검색/정렬 등을 지원합니다.</p>
     *
     * @param fromDate 시작 날짜 (YYYY-MM-DD)
     * @param toDate 종료 날짜 (YYYY-MM-DD)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param projectId 프로젝트 ID (선택사항)
     * @param agentAppId Agent 앱 ID (선택사항)
     * @return 페이징된 Agent History 목록
     */
    @Operation(
            summary = "Agent History 목록 조회",
            description = "Agent 앱의 실행 이력을 조회합니다. 시간 범위, 프로젝트, 앱 ID, 필드/필터/검색/정렬을 지원합니다."
    )
    @GetMapping(value = "/api/v1/history/agent/list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    AgentHistoryResponse getAgentHistoryList(
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam("from_date") String fromDate,

            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam("to_date") String toDate,

            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "필드 선택 (콤마 구분)", example = "fields")
            @RequestParam(value = "fields", required = false) String fields,

            @Parameter(description = "오류 로그만 조회 여부", example = "false")
            @RequestParam(value = "error_logs", required = false) Boolean errorLogs,

            @Parameter(description = "추가 히스토리 옵션 (콤마 구분)", example = "tracing,model,retrieval")
            @RequestParam(value = "additional_history_option", required = false) String additionalHistoryOption,

            @Parameter(description = "필터 (key:value,...)", example = "agent_app_id:f6e129f6-c09e-46c8-b8eb-59c")
            @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색 (key:*value*...)", example = "request_time:*2025-07*")
            @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "정렬 (field,order)", example = "request_time,asc")
            @RequestParam(value = "sort", required = false) String sort
    );

    /**
     * 에이전트 사용 통계 조회 (테스트)
     *
     * <p>에이전트 사용량에 대한 통계 정보를 조회합니다. (테스트 버전)
     * 지정된 그룹핑 기준으로 집계된 에이전트 사용 데이터를 제공합니다.</p>
     *
     * <h4>그룹핑 가능한 필드:</h4>
     * <ul>
     *   <li><strong>project_id</strong>: 프로젝트별 집계</li>
     *   <li><strong>app_id</strong>: 애플리케이션별 집계</li>
     *   <li><strong>agent_app_id</strong>: 에이전트 애플리케이션별 집계</li>
     *   <li><strong>agent_app_version</strong>: 에이전트 버전별 집계</li>
     *   <li><strong>serving_type</strong>: 서빙 타입별 집계</li>
     *   <li><strong>user</strong>: 사용자별 집계</li>
     *   <li><strong>company</strong>: 회사별 집계</li>
     *   <li><strong>department</strong>: 부서별 집계</li>
     * </ul>
     *
     * @param groupBy 그룹핑 필드 (콤마로 구분된 문자열, 필수)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate 조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param filter 필터 조건 (key:value,... 형식)
     * @param search 검색 조건 (key:*value*,... 형식)
     * @return 에이전트 사용 통계 데이터
     */
    @Operation(
        summary = "에이전트 사용 통계 조회 (테스트)",
        description = "에이전트 사용량에 대한 통계를 그룹핑하여 집계합니다. 테스트 버전입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/history/agent/stats/test")
    AgentStatsRead getAgentStatsTest();

    /**
     * 에이전트 사용 통계 조회
     *
     * <p>에이전트 사용량에 대한 통계 정보를 조회합니다.
     * 지정된 그룹핑 기준으로 집계된 에이전트 사용 데이터를 제공합니다.</p>
     *
     * <h4>그룹핑 가능한 필드:</h4>
     * <ul>
     *   <li><strong>project_id</strong>: 프로젝트별 집계</li>
     *   <li><strong>app_id</strong>: 애플리케이션별 집계</li>
     *   <li><strong>agent_app_id</strong>: 에이전트 애플리케이션별 집계</li>
     *   <li><strong>agent_app_version</strong>: 에이전트 버전별 집계</li>
     *   <li><strong>serving_type</strong>: 서빙 타입별 집계</li>
     *   <li><strong>user</strong>: 사용자별 집계</li>
     *   <li><strong>company</strong>: 회사별 집계</li>
     *   <li><strong>department</strong>: 부서별 집계</li>
     * </ul>
     *
     * @param groupBy 그룹핑 필드 (콤마로 구분된 문자열, 필수)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate 조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param filter 필터 조건 (key:value,... 형식)
     * @param search 검색 조건 (key:*value*,... 형식)
     * @return 에이전트 사용 통계 데이터
     */
    @Operation(
        summary = "에이전트 사용 통계 조회",
        description = "에이전트 사용량에 대한 통계를 그룹핑하여 집계합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/history/agent/stats")
    AgentStatsRead getAgentStats(
        @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-01")
        @RequestParam("start_date") String startDate,

        @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-30")
        @RequestParam("end_date") String endDate,

        @Parameter(description = "집계 단위 (daily, weekly, monthly)", example = "daily")
        @RequestParam(value = "granularity", required = false) String granularity,

        @Parameter(description = "프로젝트 ID 필터")
        @RequestParam(value = "project_id", required = false) String projectId
    );

    /**
     * 문서 지능 사용 통계 조회
     *
     * <p>문서 지능 서비스 사용량에 대한 통계 정보를 조회합니다.
     * 도구별, 모델별, 사용자별로 집계된 문서 처리 통계를 제공합니다.</p>
     *
     * <h4>그룹핑 가능한 필드:</h4>
     * <ul>
     *   <li><strong>project_id</strong>: 프로젝트별 집계</li>
     *   <li><strong>tool_id</strong>: 도구별 집계</li>
     *   <li><strong>model_type</strong>: 모델 타입별 집계</li>
     *   <li><strong>user</strong>: 사용자별 집계</li>
     * </ul>
     *
     * <h4>제공되는 통계 정보:</h4>
     * <ul>
     *   <li><strong>처리량</strong>: 총 문서 처리 수, 성공/실패 건수</li>
     *   <li><strong>성능</strong>: 평균 처리 시간, 처리량</li>
     *   <li><strong>품질</strong>: 정확도, 오류율</li>
     *   <li><strong>사용량</strong>: 도구별/모델별 사용 빈도</li>
     * </ul>
     *
     * @param groupBy 그룹핑 필드 (콤마로 구분된 문자열, 기본값: project_id,tool_id,model_type,user)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate 조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param filter 필터 조건 (key:value,... 형식)
     * @param search 검색 조건 (key:*value*,... 형식)
     * @return 문서 지능 사용 통계 데이터
     */
    @Operation(
        summary = "문서 지능 사용 통계 조회",
        description = "문서 지능 서비스 사용량에 대한 통계를 조회합니다. 도구별, 모델별, 사용자별 집계를 제공합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/history/doc-intelligence/stats")
    DocIntelligenceStatsRead getDocIntelligenceStats(
        @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-01")
        @RequestParam("start_date") String startDate,

        @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-09-30")
        @RequestParam("end_date") String endDate,

        @Parameter(description = "집계 단위 (daily, weekly, monthly)", example = "daily")
        @RequestParam(value = "granularity", required = false) String granularity,

        @Parameter(description = "프로젝트 ID 필터")
        @RequestParam(value = "project_id", required = false) String projectId
    );
}
