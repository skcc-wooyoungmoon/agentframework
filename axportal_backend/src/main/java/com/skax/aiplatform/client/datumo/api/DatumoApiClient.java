package com.skax.aiplatform.client.datumo.api;

import com.skax.aiplatform.client.datumo.api.dto.request.LoginRequest;
import com.skax.aiplatform.client.datumo.api.dto.response.LoginResponse;
import com.skax.aiplatform.client.datumo.api.dto.response.TaskListResponse;
import com.skax.aiplatform.client.datumo.config.DatumoClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Datumo API Feign Client
 *
 * <p>Datumo 시스템과의 API 통신을 담당하는 Feign Client입니다.
 * 로그인 인증과 Task 관리 기능을 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>인증</strong>: 로그인을 통한 액세스 토큰 발급</li>
 *   <li><strong>Task 관리</strong>: Task 목록 조회 (검색, 페이징 지원)</li>
 * </ul>
 *
 * <h3>Base URL:</h3>
 * <p>http://eval-public-shinhan.datumo.com</p>
 *
 * <h3>인증 방식:</h3>
 * <p>Bearer Token (로그인 후 발급받은 accessToken 사용)</p>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-02
 */
@Tag(name = "Datumo API", description = "Datumo 시스템 연동 API")
@FeignClient(
        name = "datumo-api-client",
        url = "${datumo.api.base-url}",
        configuration = DatumoClientConfig.class
)
public interface DatumoApiClient {

    /**
     * Datumo 로그인
     *
     * <p>Datumo 시스템에 로그인하여 API 호출에 필요한 액세스 토큰을 발급받습니다.
     * 발급받은 토큰은 이후 모든 API 호출에서 Authorization 헤더에 포함해야 합니다.</p>
     *
     * <h3>요청 예시:</h3>
     * <pre>
     * LoginRequest request = LoginRequest.builder()
     *     .loginId("shinhan_admin")
     *     .password("shinhanadmin12!")
     *     .build();
     *
     * LoginResponse response = datumoApiClient.login(request);
     * String accessToken = response.getAccessToken();
     * </pre>
     *
     * <h3>주의사항:</h3>
     * <ul>
     *   <li>유효한 사용자 계정이 필요합니다</li>
     *   <li>토큰에는 만료 시간이 있으므로 주기적으로 갱신해야 합니다</li>
     *   <li>보안을 위해 토큰은 안전한 곳에 저장해야 합니다</li>
     * </ul>
     *
     * @param request 로그인 요청 정보 (사용자 ID, 비밀번호)
     * @return 로그인 응답 정보 (액세스 토큰, 토큰 타입, 만료 시간)
     * @throws com.skax.aiplatform.common.exception.BusinessException 로그인 실패 시
     */
    @Operation(
            summary = "Datumo 시스템 로그인",
            description = "Datumo 시스템에 로그인하여 API 호출용 액세스 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (잘못된 사용자 정보)",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수 파라미터 누락)",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/api/v2/login")
    LoginResponse login(
            @Parameter(description = "로그인 요청 정보", required = true)
            @RequestBody LoginRequest request
    );

    /**
     * Task 목록 조회
     *
     * <p>Datumo 시스템에서 지정된 프로젝트의 Task 목록을 조회합니다.
     * 카테고리별 필터링, 검색어 기반 검색, 페이징을 지원합니다.</p>
     *
     * <h3>요청 예시:</h3>
     * <pre>
     * // 기본 조회 (검색어 없음)
     * TaskListResponse response = datumoApiClient.getTaskList(1L, "JUDGE", 1, 12, null);
     *
     * // 검색어 포함 조회
     * TaskListResponse response = datumoApiClient.getTaskList(1L, "JUDGE", 1, 12, "RAGAS");
     *
     * List&lt;TaskInfo&gt; tasks = response.getTasks();
     * Integer totalCount = response.getTotalDataCount();
     * </pre>
     *
     * <h3>주요 파라미터:</h3>
     * <ul>
     *   <li><strong>projectId</strong>: 조회할 프로젝트 ID (필수)</li>
     *   <li><strong>category</strong>: Task 카테고리 (예: JUDGE, EVALUATION)</li>
     *   <li><strong>page</strong>: 페이지 번호 (1부터 시작)</li>
     *   <li><strong>pageSize</strong>: 페이지당 항목 수</li>
     *   <li><strong>search</strong>: 검색어 (선택적)</li>
     * </ul>
     *
     * <h3>응답 정보:</h3>
     * <ul>
     *   <li><strong>totalDataCount</strong>: 전체 데이터 개수</li>
     *   <li><strong>totalPageCount</strong>: 전체 페이지 수</li>
     *   <li><strong>tasks</strong>: Task 목록 (현재 페이지)</li>
     * </ul>
     *
     * <h3>인증 요구사항:</h3>
     * <p>이 API를 호출하기 전에 로그인을 통해 획득한 액세스 토큰을
     * Authorization 헤더에 포함해야 합니다.</p>
     *
     * @param projectId 조회할 프로젝트 ID
     * @param category  Task 카테고리 (JUDGE, EVALUATION 등)
     * @param page      페이지 번호 (1부터 시작)
     * @param pageSize  페이지당 항목 수
     * @param search    검색어 (선택적, Task 이름이나 설명에서 검색)
     * @return Task 목록 조회 결과 (페이징 정보 포함)
     * @throws com.skax.aiplatform.common.exception.BusinessException API 호출 실패 시
     */
    @Operation(
            summary = "Task 목록 조회",
            description = "지정된 프로젝트의 Task 목록을 조회합니다. 카테고리별 필터링, 검색어 기반 검색, 페이징을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = TaskListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음 (프로젝트 접근 권한 부족)",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음 (존재하지 않는 프로젝트)",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 파라미터)",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/api/shinhan/task-list")
    TaskListResponse getTaskList(
            @Parameter(description = "조회할 프로젝트 ID", required = true, example = "/public")
            @RequestParam("group") String group,

            @Parameter(description = "조회할 프로젝트 ID", required = true, example = "1")
            @RequestParam("projectId") String projectId,

            @Parameter(description = "Task 카테고리", required = true, example = "JUDGE")
            @RequestParam("category") String category,

            @Parameter(description = "페이지 번호 (1부터 시작)", required = true, example = "1")
            @RequestParam("page") Integer page,

            @Parameter(description = "페이지당 항목 수", required = true, example = "12")
            @RequestParam("pageSize") Integer pageSize,

            @Parameter(description = "검색어 (Task 이름이나 설명에서 검색)", required = false, example = "RAGAS")
            @RequestParam(value = "search", required = false) String search
    );
}