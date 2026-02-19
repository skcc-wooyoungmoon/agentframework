package com.skax.aiplatform.controller.prompt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.WorkFlowBatchDeleteReq;
import com.skax.aiplatform.dto.prompt.request.WorkFlowCreateReq;
import com.skax.aiplatform.dto.prompt.request.WorkFlowUpdateReq;
import com.skax.aiplatform.dto.prompt.response.WorkFlowCreateRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowDeleteRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowVerListByIdRes;
import com.skax.aiplatform.service.prompt.WorkFlowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 워크플로우 컨트롤러 (AxResponseEntity 적용)
 *
 * <p>워크플로우를 관리합니다.
 * 워크플로우 조회 항목: 프롬프트, 태그, 버전
 * 워크플로우 수정, 삭제를 포함합니다.
 * AxResponseEntity를 통해 통합된 응답 형식을 제공합니다.</p>
 *
 * @author yunyoseob
 * @version 0.0.1
 * @since 2025-09-17
 */
@Slf4j
@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
@Tag(name = "WorkFlow Management", description = "워크플로우 관리 API")
public class WorkFlowController {
    private final WorkFlowService workFlowService;

    /**
     * 새로운 워크플로우 생성
     *
     * @param request 워크플로우 생성 요청
     * @return 생성된 워크플로우 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "새로운 워크플로우 생성",
            description = "새로운 워크플로우를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "워크플로우 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<WorkFlowCreateRes> createWorkFlow(
            @Valid @RequestPart("form") WorkFlowCreateReq request,
            @RequestPart(name = "xmlFile", required = false) MultipartFile xmlFile) throws IOException {

        if (xmlFile != null && !xmlFile.isEmpty()) {
            request.setXmlText(readXmlText(xmlFile));
        }

        if (request.getXmlText() == null || request.getXmlText().isBlank()) {
            throw new IllegalArgumentException("xmlText or xmlFile required");
        }

        log.info("새로운 워크플로우 생성 요청: name={}", request.getWorkflowName());
        WorkFlowCreateRes workFlowCreateRes = workFlowService.createWorkflow(request);
        log.info("워크플로우 생성 완료: name={}", request.getWorkflowName());
        return AxResponseEntity.created(workFlowCreateRes, "새로운 워크플로우가 성공적으로 생성되었습니다.");
    }

    /**
     * 워크플로우 목록 조회
     * <p>
     * 조회기간: 생성일시 기준 fromDate, toDate
     * 조회조건: 이름, 태그
     *
     * @param pageable  페이징 정보
     * @param sort      정렬 기준
     * @param search    검색어
     * @return 데이터셋 목록
     */
    @GetMapping
    @Operation(
            summary = "워크플로우 목록",
            description = "워크플로우 목록을 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 목록 조회 성공")
    })
    public AxResponseEntity<PageResponse<WorkFlowRes>> getWorkFlowList(
            @PageableDefault(size = 10) Pageable pageable,
            @Parameter(description = "검색어")
            @RequestParam(required = false) String search,
            @Parameter(description = "태그")
            @RequestParam(required = false) String tag,
            @Parameter(description = "정렬 기준")
            @RequestParam(value = "sort", defaultValue = "created_at,desc") String sort
    ) {
        log.info("Controller: 워크플로우 목록 조회 API 호출 - page: {}, size: {}, search: {}, sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), search, sort);
        // getWorkFlowList
        PageResponse<WorkFlowRes> workFlowList = workFlowService.getWorkFlowList(pageable, search, tag, sort);
        return AxResponseEntity.okPage(workFlowList, "워크플로우 목록을 성공적으로 조회했습니다.");
    }

    private String readXmlText(MultipartFile file) throws IOException {
        try (var r = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8), 8192)) {
            var sb = new StringBuilder(100_000);
            var buf = new char[8192];
            int n;
            while ((n = r.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        }
    }

    /**
     * 워크플로우 태그 목록 조회
     * <p>
     *
     * @return 워크플로우 태그 목록
     */
    @GetMapping("/tags")
    @Operation(
            summary = "워크플로우 태그 목록",
            description = "워크플로우 태그 목록을 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 목록 조회 성공")
    })
    public AxResponseEntity<List<String>> getWorkFlowTagList() {
        log.info("Controller: 워크플로우 태그 목록 조회 API 호출");
        List<String> workFlowTagList = workFlowService.getWorkFlowTagList();
        return AxResponseEntity.ok(workFlowTagList, "워크플로우 태그 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 워크플로우 버전 목록 조회
     *
     * @param workFlowId 워크플로우 ID(UUID)
     * @return 워크플로우 버전 목록
     */
    @GetMapping("versions/{workFlowId}")
    @Operation(
            summary = "워크플로우 버전 목록 조회",
            description = "UUID 기반으로 특정 워크플로우의 버전 목록 정보를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 버전 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 워크플로우 버전 목록이 존재하지 않음")
    })
    public AxResponseEntity<WorkFlowVerListByIdRes> getWorkFlowVerListById(
            @Parameter(description = "워크플로우 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable("workFlowId") String workFlowId
    ) {
        log.info("Controller: 워크플로우 버전 목록 조회 API 호출 - workFlowId: {}", workFlowId);
        WorkFlowVerListByIdRes workFlowVerListById = workFlowService.getWorkFlowVerListById(workFlowId);
        return AxResponseEntity.ok(workFlowVerListById, "워크플로우 버전 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 워크플로우 최신 버전 조회
     *
     * @param workFlowId 워크플로우 ID(UUID)
     * @return 워크플로우 최신 버전 정보
     */
    @GetMapping("versions/{workFlowId}/latest")
    @Operation(
            summary = "워크플로우 최신 버전 조회",
            description = "UUID 기반으로 특정 워크플로우의 최신 버전 정보를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 버전 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 워크플로우 최신 버전이 존재하지 않음")
    })
    public AxResponseEntity<WorkFlowRes> getWorkFlowLatestVerById(
            @Parameter(description = "워크플로우 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable("workFlowId") String workFlowId
    ) {
        log.info("Controller: 워크플로우 최신 버전 조회 API 호출 - workFlowId: {}", workFlowId);
        WorkFlowRes workFlowRes = workFlowService.getWorkFlowLatestVerById(workFlowId);
        return AxResponseEntity.ok(workFlowRes, "워크플로우 버전 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 워크플로우 특정 버전 조회
     *
     * @param workFlowId 워크플로우 ID(UUID)
     * @param versionNo  워크플로우 버전 번호
     * @return 워크플로우 특정 버전 정보
     */
    @GetMapping("versions/{workFlowId}/{versionNo}")
    @Operation(
            summary = "워크플로우 특정 버전 조회",
            description = "UUID 기반으로 특정 워크플로우의 특정 버전 정보를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 특정 버전 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 워크플로우 해당 버전이 존재하지 않음")
    })
    public AxResponseEntity<WorkFlowRes> getWorkFlowVerById(
            @Parameter(description = "워크플로우 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable("workFlowId") String workFlowId,
            @Parameter(description = "워크플로우 버전 번호", example = "3", required = true)
            @PathVariable("versionNo") Integer versionNo
    ) {
        log.info("Controller: 워크플로우 특정 버전 조회 API 호출 - workFlowId: {}, versionNo: {}", workFlowId, versionNo);
        WorkFlowRes workFlowRes = workFlowService.getWorkFlowVerById(workFlowId, versionNo);
        return AxResponseEntity.ok(workFlowRes, "워크플로우 특정 버전의 정보를 성공적으로 조회했습니다.");
    }


    /**
     * 새로운 워크플로우 생성
     *
     * @param request 워크플로우 생성 요청
     * @return 생성된 워크플로우 정보
     */
    @PutMapping(value = "/{workflowId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "워크플로우 정보를 수정합니다.",
            description = "기존 workflowId의 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "워크플로우 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "워크플로우를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> updateWorkFlow(
            @PathVariable("workflowId")
            @Parameter(description = "워크플로우 ID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c")
            String workflowId,
            @Valid @RequestPart("form") WorkFlowUpdateReq request,
            @RequestPart(name = "xmlFile", required = false) MultipartFile xmlFile) throws IOException {

        if (xmlFile != null && !xmlFile.isEmpty()) {
            request.setXmlText(readXmlText(xmlFile));
        }

        if (request.getXmlText() == null || request.getXmlText().isBlank()) {
            throw new IllegalArgumentException("xmlText or xmlFile required");
        }

        log.info("워크플로우 수정 요청: workFlowId={}, name={}", workflowId, request.getWorkflowName());
        workFlowService.updateWorkFlow(workflowId, request);
        return AxResponseEntity.ok(null, "워크플로우가 성공적으로 수정되었습니다.");
    }

    /**
     * 워크플로우 삭제
     *
     * @param workflowId 워크플로우 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{workflowId}")
    @Operation(
            summary = "워크플로우 삭제",
            description = "특정 워크플로우을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "워크플로우을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteWorkFlowById(
            @PathVariable("workflowId")
            @Parameter(description = "워크플로우 UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c")
            String workflowId) {

        log.info("워크플로우 삭제 요청: workflowId={}", workflowId);

        workFlowService.deleteWorkFlowById(workflowId);

        log.info("워크플로우 삭제 완료: workflowId={}", workflowId);

        return AxResponseEntity.ok(null, "워크플로우가 성공적으로 삭제되었습니다.");
    }

    /**
     * 여러 워크플로우 일괄 삭제
     *
     * @param request 워크플로우 ID 목록
     * @return 삭제 결과 (총 개수, 성공 개수, 실패 개수)
     */
    @PostMapping("/batch/delete")
    @Operation(
            summary = "워크플로우 일괄 삭제",
            description = "여러 워크플로우를 한 번에 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 일괄 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<WorkFlowDeleteRes> deleteWorkFlowsByIds(
            @Valid @RequestBody WorkFlowBatchDeleteReq request) {

        log.info("워크플로우 일괄 삭제 요청: ids={}", request.getIds());

        WorkFlowDeleteRes result = workFlowService.deleteWorkFlowsByIds(request);

        log.info("워크플로우 일괄 삭제 완료: total={}, success={}, fail={}", 
                result.getTotalCount(), result.getSuccessCount(), result.getFailCount());

        return AxResponseEntity.ok(result, 
                String.format("%d개 중 %d개 삭제 완료", result.getTotalCount(), result.getSuccessCount()));
    }

    /**
     * 워크플로우 공개 설정
     *
     * @param workflowId 워크플로우 ID
     * @return 공개 설정 완료 응답
     */
    @PostMapping("/policy/{workflowId}/public")
    @Operation(
            summary = "워크플로우 공개 설정",
            description = "특정 워크플로우를 공개로 설정합니다 (prj_seq를 -999로 업데이트)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "워크플로우 공개 설정 성공"),
            @ApiResponse(responseCode = "404", description = "워크플로우를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> makeWorkFlowPublic(
            @PathVariable("workflowId")
            @Parameter(description = "워크플로우 UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c")
            String workflowId) {

        log.info("워크플로우 공개 설정 요청: workflowId={}", workflowId);

        workFlowService.makeWorkFlowPublic(workflowId);

        log.info("워크플로우 공개 설정 완료: workflowId={}", workflowId);

        return AxResponseEntity.ok(null, "워크플로우가 성공적으로 공개 설정되었습니다.");
    }

}
