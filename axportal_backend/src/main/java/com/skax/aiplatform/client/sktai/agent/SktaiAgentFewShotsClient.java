package com.skax.aiplatform.client.sktai.agent;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCommentCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCommentUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotTestRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCommentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotDependencyResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotExamplesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotFilterByTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotInternalApiResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotItemsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagListResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTestResponse;
// import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotsResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Agent Few-Shots API Client
 * 
 * <p>SKTAI Agent 시스템의 Few-Shot Learning 관리 기능을 제공하는 Feign Client입니다.
 * AI 모델의 성능 향상을 위한 Few-Shot 예제를 생성, 관리할 수 있습니다.</p>
 * 
 * <h3>제공 기능:</h3>
 * <ul>
 *   <li><strong>Few-Shot CRUD</strong>: Few-Shot 예제 생성, 조회, 수정, 삭제</li>
 *   <li><strong>버전 관리</strong>: Few-Shot 버전 관리 및 이력 추적</li>
 *   <li><strong>예제 관리</strong>: 입력-출력 예제 쌍 관리</li>
 *   <li><strong>태그 시스템</strong>: 분류 및 검색을 위한 태그 관리</li>
 *   <li><strong>복사 기능</strong>: 기존 Few-Shot 복사하여 새로운 세트 생성</li>
 *   <li><strong>성능 최적화</strong>: 페이징, 필터링, 검색 지원</li>
 * </ul>
 * 
 * <h3>Few-Shot Learning이란:</h3>
 * <ul>
 *   <li><strong>정의</strong>: 적은 수의 예제로 AI 모델 성능을 향상시키는 기법</li>
 *   <li><strong>구성</strong>: 입력(input)과 기대 출력(expected output) 쌍</li>
 *   <li><strong>활용</strong>: 프롬프트 엔지니어링, 모델 파인튜닝, 성능 개선</li>
 *   <li><strong>효과</strong>: 적은 데이터로 높은 성능 달성</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // Few-Shot 목록 조회
 * FewShotsResponse fewShots = fewShotsClient.getFewShots("project-123", 1, 10, null, null, null);
 * 
 * // Few-Shot 생성
 * FewShotCreateRequest request = FewShotCreateRequest.builder()
 *     .name("Customer Sentiment Analysis")
 *     .description("고객 리뷰 감정 분석을 위한 Few-Shot 예제")
 *     .examples(Arrays.asList(
 *         FewShotExample.builder()
 *             .input("이 제품은 정말 좋아요!")
 *             .output("positive")
 *             .build(),
 *         FewShotExample.builder()
 *             .input("배송이 너무 늦었습니다.")
 *             .output("negative")
 *             .build()
 *     ))
 *     .tags(Arrays.asList(
 *         FewShotTag.builder().tag("sentiment-analysis").build()
 *     ))
 *     .build();
 * FewShotCreateResponse response = fewShotsClient.createFewShot(request);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-agent-few-shots-client",
    url = "${sktai.api.base-url}/api/v1/agent",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Agent Few-Shots", description = "SKTAI Agent Few-Shot Learning Management API")
public interface SktaiAgentFewShotsClient {
    
    /**
     * Few-Shot 목록 조회
     * 
     * <p>등록된 Few-Shot들의 목록을 조회합니다.
     * 프로젝트별로 필터링하고 페이징, 정렬, 검색 기능을 지원합니다.</p>
     * 
     * @param projectId 프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @param release_only 릴리스 전용 조회 여부
     * @return Few-Shot 목록 응답
     * @since 1.0
     */
    @GetMapping("/few-shots")
    @Operation(
        summary = "Few-Shot 목록 조회",
        description = "등록된 Few-Shot들의 목록을 프로젝트별로 조회합니다. 페이징, 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 목록 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotsResponse getFewShots(
        @Parameter(description = "프로젝트 ID") @RequestParam(defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId,
        @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size,
        @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
        @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
        @Parameter(description = "검색어") @RequestParam(required = false) String search,
        @Parameter(description = "릴리스 전용 조회 여부") @RequestParam(defaultValue = "false") Boolean release_only
    );
    
    /**
     * Few-Shot 생성
     * 
     * <p>새로운 Few-Shot를 생성합니다.
     * 예제 데이터와 태그를 포함하여 Few-Shot을 정의할 수 있습니다.</p>
     * 
     * @param request Few-Shot 생성 요청 데이터
     * @return Few-Shot 생성 응답 (Few-Shot UUID 포함)
     * @since 1.0
     */
    @PostMapping("/few-shots")
    @Operation(
        summary = "Few-Shot 생성",
        description = "새로운 Few-Shot를 생성합니다. 예제 데이터와 태그를 포함하여 정의할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Few-Shot 생성 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotCreateResponse createFewShot(@RequestBody FewShotCreateRequest request);
    
    // /**
    //  * 성능 측정용 Few-Shot 목록 조회
    //  * 
    //  * <p>성능 측정을 위한 전용 엔드포인트입니다. (테스트 전용)</p>
    //  * 
    //  * @param projectId 프로젝트 ID
    //  * @param ignoreOption 무시 옵션 (1: authz)
    //  * @param page 페이지 번호
    //  * @param size 페이지 크기
    //  * @param sort 정렬 기준
    //  * @param filter 필터 조건
    //  * @param search 검색어
    //  * @return Few-Shot 목록 응답
    //  * @since 1.0
    //  */
    // @GetMapping("/few-shots/perf")
    // @Operation(
    //     summary = "성능 측정용 Few-Shot 목록 조회",
    //     description = "성능 측정을 위한 전용 엔드포인트입니다. (테스트 전용)"
    // )
    // @ApiResponses({
    //     @ApiResponse(responseCode = "200", description = "Few-Shot 목록 조회 성공"),
    //     @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    // })
    // FewShotsResponse getFewShotsPerf(
    //     @Parameter(description = "프로젝트 ID") @RequestParam(defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId,
    //     @Parameter(description = "무시 옵션 (1: authz)") @RequestParam(defaultValue = "1") Integer ignoreOption,
    //     @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page,
    //     @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") Integer size,
    //     @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
    //     @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
    //     @Parameter(description = "검색어") @RequestParam(required = false) String search
    // );
    
    /**
     * Few-Shot 상세 조회
     * 
     * <p>특정 Few-Shot의 상세 정보를 조회합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Few-Shot 상세 정보 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 상세 조회",
        description = "특정 Few-Shot의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 상세 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotResponse getFewShot(@Parameter(description = "Few-Shot UUID") @PathVariable String fewShotUuid);
    
    /**
     * Few-Shot 수정
     * 
     * <p>기존 Few-Shot를 수정합니다. 버전업 처리됩니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param request Few-Shot 수정 요청 데이터
     * @return Few-Shot 수정 응답
     * @since 1.0
     */
    @PutMapping("/few-shots/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 수정",
        description = "기존 Few-Shot를 수정합니다. 버전업 처리됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 수정 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotUpdateResponse updateFewShot(
        @Parameter(description = "Few-Shot UUID") @PathVariable String fewShotUuid,
        @RequestBody FewShotUpdateRequest request
    );
    
    /**
     * Few-Shot 삭제
     * 
     * <p>특정 Few-Shot를 삭제합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @since 1.0
     */
    @DeleteMapping("/few-shots/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 삭제",
        description = "특정 Few-Shot를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Few-Shot 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    void deleteFewShot(@Parameter(description = "Few-Shot UUID") @PathVariable String fewShotUuid);
    
    /**
     * Few-Shot 최신 버전 조회
     * 
     * <p>특정 Few-Shot의 최신 버전을 조회합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 최신 Few-Shot 버전 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/versions/{fewShotUuid}/latest")
    @Operation(
        summary = "Few-Shot 최신 버전 조회",
        description = "특정 Few-Shot의 최신 버전을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "최신 버전 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotVersionResponse getLatestFewShotVersion(@Parameter(description = "Few-Shot UUID") @PathVariable String fewShotUuid);
    
    /**
     * Few-Shot 버전 목록 조회
     * 
     * <p>특정 Few-Shot의 모든 버전 목록을 조회합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Few-Shot 버전 목록 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/versions/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 버전 목록 조회",
        description = "특정 Few-Shot의 모든 버전 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "버전 목록 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotVersionsResponse getFewShotVersions(@Parameter(description = "Few-Shot UUID") @PathVariable String fewShotUuid);
    
    /**
     * Few-Shot 예제 조회
     * 
     * <p>특정 버전의 Few-Shot 예제를 조회합니다.</p>
     * 
     * @param versionId Few-Shot 버전 ID
     * @return Few-Shot 예제 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/examples/{versionId}")
    @Operation(
        summary = "Few-Shot 예제 조회",
        description = "특정 버전의 Few-Shot 예제를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예제 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotExamplesResponse getFewShotExamples(@Parameter(description = "Few-Shot 버전 ID") @PathVariable String versionId);
    
    /**
     * Few-Shot 태그 조회 (버전별)
     * 
     * <p>특정 버전의 Few-Shot 태그를 조회합니다.</p>
     * 
     * @param versionId Few-Shot 버전 ID
     * @return Few-Shot 태그 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/tags/{versionId}")
    @Operation(
        summary = "Few-Shot 태그 조회 (버전별)",
        description = "특정 버전의 Few-Shot 태그를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotTagsResponse getFewShotTagsByVersion(@Parameter(description = "Few-Shot 버전 ID") @PathVariable String versionId);
    
    /**
     * Few-Shot 태그 목록 조회
     * 
     * <p>모든 Few-Shot 태그 목록을 조회합니다.</p>
     * 
     * @return Few-Shot 태그 목록 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/list/tags")
    @Operation(
        summary = "Few-Shot 태그 목록 조회",
        description = "모든 Few-Shot 태그 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공")
    })
    FewShotTagListResponse getFewShotTagsList();
    
    /**
     * 태그로 Few-Shot 검색
     * 
     * <p>태그를 기준으로 Few-Shot ID를 검색합니다.</p>
     * 
     * @param filters 필터 조건 (태그)
     * @return 태그로 필터링된 Few-Shot 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/search/tags")
    @Operation(
        summary = "태그로 Few-Shot 검색",
        description = "태그를 기준으로 Few-Shot ID를 검색합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 검색 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotFilterByTagsResponse searchFewShotsByTags(@Parameter(description = "검색할 태그 필터") @RequestParam String filters);
    
    // /**
    //  * Few-Shot 복사
    //  * 
    //  * <p>기존 Few-Shot를 복사하여 새로운 Few-Shot를 생성합니다.</p>
    //  * 
    //  * @param fewShotUuid 복사할 Few-Shot UUID
    //  * @param request Few-Shot 복사 요청 데이터
    //  * @return Few-Shot 생성 응답 (새 Few-Shot UUID 포함)
    //  * @since 1.0
    //  */
    // @PostMapping("/few-shots/copy/{fewShotUuid}")
    // @Operation(
    //     summary = "Few-Shot 복사",
    //     description = "기존 Few-Shot를 복사하여 새로운 Few-Shot를 생성합니다."
    // )
    // @ApiResponses({
    //     @ApiResponse(responseCode = "201", description = "Few-Shot 복사 성공"),
    //     @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    // })
    // FewShotCreateResponse copyFewShot(
    //     @Parameter(description = "복사할 Few-Shot UUID") @PathVariable String fewShotUuid,
    //     @RequestBody FewShotCopyRequest request
    // );
    
    /**
     * Few-Shot 하드 삭제
     * 
     * <p>삭제 마크된 모든 Few-Shot들을 데이터베이스에서 완전히 삭제합니다.</p>
     * 
     * @apiNote 이 작업은 되돌릴 수 없으므로 주의해서 사용해야 합니다.
     * @since 1.0
     */
    @PostMapping("/few-shots/hard-delete")
    @Operation(
        summary = "Few-Shot 하드 삭제",
        description = "삭제 마크된 모든 Few-Shot들을 데이터베이스에서 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    })
    void hardDeleteFewShots();

        /**
     * Few-Shot 목록 조회
     * 
     * <p>등록된 Few-Shot들의 목록을 조회합니다.
     * 프로젝트별로 필터링하고 페이징, 정렬, 검색 기능을 지원합니다.</p>
     * 
     * @param projectId 프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return Few-Shot 목록 응답
     * @since 1.0
     */
    @GetMapping("/few-shots/items/{versionId}")
    @Operation(
        summary = "Few-Shot 아이템 목록 조회",
        description = "등록된 Few-Shot들의 아이템 목록을 버전ID별로 조회합니다. 페이징, 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 아이템 목록 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotItemsResponse getFewShotsItems(
        @Parameter(description = "버전 ID") @PathVariable String versionId,
        @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size,
        @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
        @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
        @Parameter(description = "검색어") @RequestParam(required = false) String search
    );
    
    // ===== 하드 삭제 API =====
    
    /**
     * Few-Shot 하드 삭제
     * 
     * <p>Few-Shot을 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다.
     * 모든 버전과 관련 데이터가 영구적으로 제거됩니다.</p>
     * 
     * @param fewShotUuids 삭제할 Few-Shot UUID 목록
     */
    @DeleteMapping("/few-shots/hard-delete")
    @Operation(
        summary = "Few-Shot 하드 삭제",
        description = "Few-Shot을 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 하드 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    void hardDeleteFewShots(
        @Parameter(description = "삭제할 Few-Shot UUID 목록", required = true) 
        @RequestParam List<String> fewShotUuids
    );
    
    // ===== Excel 템플릿 및 가져오기/내보내기 API =====
    
    /**
     * Few-Shot Excel 템플릿 내보내기
     * 
     * <p>Few-Shot 데이터를 Excel 형태로 가져오기 위한 템플릿을 다운로드합니다.
     * 이 템플릿을 사용하여 대량의 Few-Shot 데이터를 일괄 등록할 수 있습니다.</p>
     * 
     * @return Excel 템플릿 파일
     */
    @GetMapping("/few-shots/export/templates")
    @Operation(
        summary = "Few-Shot Excel 템플릿 내보내기",
        description = "Few-Shot 데이터 일괄 등록을 위한 Excel 템플릿을 다운로드합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Excel 템플릿 내보내기 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Object exportFewShotTemplate();
    
    /**
     * Few-Shot 템플릿 기반 데이터 가져오기
     * 
     * <p>Excel 템플릿을 사용하여 준비된 Few-Shot 데이터를 특정 버전으로 일괄 가져옵니다.
     * 템플릿 형식에 맞춰 작성된 데이터만 정상적으로 처리됩니다.</p>
     * 
     * @param versionId 데이터를 가져올 Few-Shot 버전 ID
     * @param file 업로드할 Excel 파일
     * @return 가져오기 결과
     */
    @PostMapping(value = "/few-shots/import/{versionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Few-Shot 템플릿 기반 데이터 가져오기",
        description = "Excel 템플릿을 사용하여 Few-Shot 데이터를 일괄 가져옵니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "데이터 가져오기 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식"),
        @ApiResponse(responseCode = "422", description = "템플릿 형식 오류")
    })
    Object importFewShotData(
        @Parameter(description = "버전 ID", required = true) @PathVariable String versionId,
        @Parameter(description = "업로드할 Excel 파일", required = true) 
        @RequestPart("file") MultipartFile file
    );
    
    // ===== 의존성 조회 API =====
    
    /**
     * Few-Shot 의존성 조회
     * 
     * <p>Few-Shot의 의존성 정보를 조회합니다.
     * 다른 리소스와의 참조 관계 및 삭제 가능 여부를 확인할 수 있습니다.</p>
     * 
     * @param versionId Few-Shot 버전 ID
     * @return 의존성 정보
     */
    @GetMapping("/few-shots/dependency/{versionId}")
    @Operation(
        summary = "Few-Shot 의존성 조회",
        description = "Few-Shot의 의존성 정보 및 다른 리소스와의 관계를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "의존성 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot 버전을 찾을 수 없음")
    })
    FewShotDependencyResponse getFewShotDependency(
        @Parameter(description = "버전 ID", required = true) @PathVariable String versionId
    );
    
    // ===== 댓글 관리 API =====
    
    /**
     * Few-Shot 댓글 목록 조회
     * 
     * <p>특정 Few-Shot 버전에 대한 댓글 목록을 조회합니다.
     * 페이징을 지원하며 작성일 순으로 정렬됩니다.</p>
     * 
     * @param versionId Few-Shot 버전 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 댓글 목록
     */
    @GetMapping("/few-shots/comments/{versionId}")
    @Operation(
        summary = "Few-Shot 댓글 목록 조회",
        description = "특정 Few-Shot 버전에 대한 댓글 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot 버전을 찾을 수 없음")
    })
    Object getFewShotComments(
        @Parameter(description = "버전 ID", required = true) @PathVariable String versionId,
        @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size
    );
    
    /**
     * Few-Shot 댓글 생성
     * 
     * <p>특정 Few-Shot 버전에 대한 새로운 댓글을 생성합니다.</p>
     * 
     * @param versionId Few-Shot 버전 ID
     * @param request 댓글 생성 요청
     * @return 생성된 댓글 정보
     */
    @PostMapping("/few-shots/comments/{versionId}")
    @Operation(
        summary = "Few-Shot 댓글 생성",
        description = "특정 Few-Shot 버전에 대한 새로운 댓글을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "Few-Shot 버전을 찾을 수 없음")
    })
    FewShotCommentResponse createFewShotComment(
        @Parameter(description = "버전 ID", required = true) @PathVariable String versionId,
        @Parameter(description = "댓글 생성 요청", required = true) 
        @RequestBody FewShotCommentCreateRequest request
    );
    
    /**
     * Few-Shot 댓글 수정
     * 
     * <p>기존 Few-Shot 댓글의 내용을 수정합니다.</p>
     * 
     * @param commentUuid 댓글 UUID
     * @param request 댓글 수정 요청
     * @return 수정된 댓글 정보
     */
    @PutMapping("/few-shots/comments/{commentUuid}")
    @Operation(
        summary = "Few-Shot 댓글 수정",
        description = "기존 Few-Shot 댓글의 내용을 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    FewShotCommentResponse updateFewShotComment(
        @Parameter(description = "댓글 UUID", required = true) @PathVariable String commentUuid,
        @Parameter(description = "댓글 수정 요청", required = true) 
        @RequestBody FewShotCommentUpdateRequest request
    );
    
    /**
     * Few-Shot 댓글 삭제
     * 
     * <p>기존 Few-Shot 댓글을 삭제합니다.</p>
     * 
     * @param commentUuid 댓글 UUID
     */
    @DeleteMapping("/few-shots/comments/{commentUuid}")
    @Operation(
        summary = "Few-Shot 댓글 삭제",
        description = "기존 Few-Shot 댓글을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    void deleteFewShotComment(
        @Parameter(description = "댓글 UUID", required = true) @PathVariable String commentUuid
    );
    
    // ===== Internal API =====
    
    /**
     * Few-Shot Internal API 정보 조회
     * 
     * <p>Few-Shot을 다른 시스템에서 사용할 수 있는 Internal API 정보를 조회합니다.
     * API 엔드포인트, 인증 방법, 사용 예시 등을 제공합니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Internal API 정보
     */
    @GetMapping("/few-shots/api/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot Internal API 정보 조회",
        description = "Few-Shot을 다른 시스템에서 사용할 수 있는 Internal API 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Internal API 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음")
    })
    FewShotInternalApiResponse getFewShotInternalApi(
        @Parameter(description = "Few-Shot UUID", required = true) @PathVariable String fewShotUuid
    );
    
    // ===== 테스트 API =====
    
    /**
     * Few-Shot 테스트 및 통합 예시
     * 
     * <p>Few-Shot의 동작을 테스트하고 통합 예시를 확인합니다.
     * 실제 시나리오에서의 성능과 결과를 미리 확인할 수 있습니다.</p>
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param request 테스트 요청
     * @return 테스트 결과
     */
    @PostMapping("/few-shots/test/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 테스트 및 통합 예시",
        description = "Few-Shot의 동작을 테스트하고 통합 예시를 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "테스트 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 테스트 요청"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음")
    })
    FewShotTestResponse testFewShotIntegration(
        @Parameter(description = "Few-Shot UUID", required = true) @PathVariable String fewShotUuid,
        @Parameter(description = "테스트 요청", required = true) 
        @RequestBody FewShotTestRequest request
    );
    
    /**
     * Few-Shot Import (JSON)
     * 
     * <p>JSON 데이터를 받아서 Few-Shot을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param fewShotId Few-Shot ID (query parameter)
     * @param jsonData JSON 형식의 Few-Shot 데이터
     * @return 생성된 Few-Shot 정보
     */
    @PostMapping("/few-shots/import")
    @Operation(
        summary = "Few-Shot Import (JSON)",
        description = "JSON 데이터를 받아서 Few-Shot을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Few-Shot Import 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    FewShotCreateResponse importFewShot(
        @Parameter(description = "Few-Shot UUID", required = true) @RequestParam("few_shot_uuid") String fewShotUuid,
        @Parameter(description = "JSON 형식의 Few-Shot 데이터", required = true)
        @RequestBody Object jsonData);
}
