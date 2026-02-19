package com.skax.aiplatform.controller.prompt;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.FewShotCreateReq;
import com.skax.aiplatform.dto.prompt.request.FewShotUpdateReq;
import com.skax.aiplatform.dto.prompt.response.FewShotCreateRes;
import com.skax.aiplatform.dto.prompt.response.FewShotItemRes;
import com.skax.aiplatform.dto.prompt.response.FewShotLineageRes;
import com.skax.aiplatform.dto.prompt.response.FewShotRes;
import com.skax.aiplatform.dto.prompt.response.FewShotTagListRes;
import com.skax.aiplatform.dto.prompt.response.FewShotTagRes;
import com.skax.aiplatform.dto.prompt.response.FewShotVerRes;
import com.skax.aiplatform.service.prompt.FewShotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Few-Shot 관리 컨트롤러
 * 
 * <p>Few-Shot 예제 데이터 관리 API 엔드포인트를 제공합니다.
 * Few-Shot 생성, 조회, 수정, 삭제 및 관련 기능을 포함합니다.</p>

 */
@Slf4j
@RestController
@RequestMapping("/fewShot")
@RequiredArgsConstructor
@Tag(name = "Few-Shot Management", description = "Few Shot 관리 API")
public class FewShotController {

    private final FewShotService fewShotService;

    /**
     * Few-Shot 목록 조회
     * 
     * @param projectId 프로젝트 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @param release_only 릴리스 전용 조회 여부
     * @return Few-Shot 응답 (페이지네이션 포함)
     */
    @GetMapping
    @Operation(
        summary = "Few-Shot 목록 조회",
        description = "프로젝트별 Few-Shot 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<FewShotRes>> getFewShotList(
            @RequestParam(value = "project_id", defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") 
            @Parameter(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId,
            @RequestParam(value = "page", defaultValue = "1") 
            @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") 
            @Parameter(description = "페이지 크기", example = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "created_at,desc") 
            @Parameter(description = "정렬 조건", example = "created_at,desc") String sort,
            @RequestParam(value = "filter", defaultValue = "") 
            @Parameter(description = "필터 조건", example = "") String filter,
            @RequestParam(value = "search", defaultValue = "") 
            @Parameter(description = "검색 키워드", example = "검색어") String search,
            @RequestParam(value = "release_only", defaultValue = "false") 
            @Parameter(description = "릴리스 전용 조회 여부", example = "false") Boolean release_only) {
        
        log.info("Few-Shot 목록 조회 요청: projectId={}, page={}, size={}, release_only={}", projectId, page, size, release_only);
        
        PageResponse<FewShotRes> fewShotRes = fewShotService.getFewShotList(projectId, page, size, sort, filter, search, release_only);
        
        log.info("Few-Shot 목록 조회 완료");
        
        return AxResponseEntity.ok(fewShotRes, "Few-Shot 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 새로운 Few-Shot 생성
     * 
     * @param request Few-Shot 생성 요청
     * @return 생성된 Few-Shot 정보
     */
    @PostMapping
    @Operation(
        summary = "새로운 Few-Shot 생성",
        description = "새로운 Few-Shot 예제를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Few-Shot 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<FewShotCreateRes> createFewShot(
            @Valid @RequestBody FewShotCreateReq request) {
        
        log.info("새로운 Few-Shot 생성 요청: name={}", request.getName());
        
        FewShotCreateRes fewShotRes = fewShotService.createFewShot(request);
        
        log.info("Few-Shot 생성 완료: name={}", request.getName());
        
        return AxResponseEntity.created(fewShotRes, "새로운 Few-Shot이 성공적으로 생성되었습니다.");
    }

    /**
     * Few-Shot 상세 정보 조회
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Few-Shot 상세 정보
     */
    @GetMapping("/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 상세 정보 조회",
        description = "UUID를 통해 특정 Few-Shot의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<FewShotRes> getFewShotById(
            @PathVariable("fewShotUuid") 
            @Parameter(description = "Few-Shot UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String fewShotUuid) {
        
        log.info("Few-Shot 상세 조회 요청: fewShotUuid={}", fewShotUuid);
        
        FewShotRes fewShotRes = fewShotService.getFewShotById(fewShotUuid);
        
        log.info("Few-Shot 상세 조회 완료: fewShotUuid={}", fewShotUuid);

        return AxResponseEntity.ok(fewShotRes, "Few-Shot 정보를 성공적으로 조회했습니다.");
    }

    /**
     * Few-Shot 정보 수정
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param request Few-Shot 수정 요청
     * @return 수정된 Few-Shot 정보
     */
    @PutMapping("/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 정보 수정",
        description = "기존 Few-Shot의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> updateFewShotById(
            @PathVariable("fewShotUuid") 
            @Parameter(description = "Few-Shot UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String fewShotUuid,
            @Valid @RequestBody FewShotUpdateReq request) {
        
        log.info("c: fewShotUuid={}, newName={}", fewShotUuid, request.getNewName());
        
        fewShotService.updateFewShotById(fewShotUuid, request);
        
        return AxResponseEntity.ok(null, "Few-Shot 정보가 성공적으로 수정되었습니다.");
    }

    /**
     * Few-Shot 삭제
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 삭제",
        description = "특정 Few-Shot을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Few-Shot 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteFewShotById(
            @PathVariable("fewShotUuid") 
            @Parameter(description = "Few-Shot UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String fewShotUuid) {
        
        log.info("Few-Shot 삭제 요청: fewShotUuid={}", fewShotUuid);
        
        fewShotService.deleteFewShotById(fewShotUuid);
        
        log.info("Few-Shot 삭제 완료: fewShotUuid={}", fewShotUuid);
        
        return AxResponseEntity.ok(null, "Few-Shot이 성공적으로 삭제되었습니다.");
    }

    /**
     * Few-Shot 최신 버전 조회
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 최신 버전 정보
     */
    @GetMapping("/versions/{fewShotUuid}/latest")
    @Operation(
        summary = "Few-Shot 최신 버전 조회",
        description = "특정 Few-Shot의 최신 버전 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "최신 버전 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<FewShotVerRes> getLtstFewShotVerById(
            @PathVariable("fewShotUuid") 
            @Parameter(description = "Few-Shot UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String fewShotUuid) {
        
        log.info("Few-Shot 최신 버전 조회 요청: fewShotUuid={}", fewShotUuid);
        
        FewShotVerRes response = fewShotService.getLtstFewShotVerById(fewShotUuid);
        
        log.info("Few-Shot 최신 버전 조회 완료: fewShotUuid={}", fewShotUuid);
        
        return AxResponseEntity.ok(response, "Few-Shot 최신 버전을 성공적으로 조회했습니다.");
    }

    /**
     * Few-Shot 버전 목록 조회
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 버전 목록
     */
    @GetMapping("/versions/{fewShotUuid}")
    @Operation(
        summary = "Few-Shot 버전 목록 조회",
        description = "특정 Few-Shot의 모든 버전 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "버전 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<FewShotVerRes>> getFewShotVerListById(
            @PathVariable("fewShotUuid") 
            @Parameter(description = "Few-Shot UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String fewShotUuid) {
        
        log.info("Few-Shot 버전 목록 조회 요청: fewShotUuid={}", fewShotUuid);
        
        List<FewShotVerRes> fewShotVerRes = fewShotService.getFewShotVerListById(fewShotUuid);
        
        log.info("Few-Shot 버전 목록 조회 완료: fewShotUuid={}", fewShotUuid);
        
        return AxResponseEntity.ok(fewShotVerRes, "특정 Few-Shot 버전 목록을 성공적으로 조회했습니다."); 
    }

    /**
     * Few-Shot 아이템 목록 조회
     * 
     * @param versionId 버전 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return Few-Shot 아이템 목록
     */
    @GetMapping("/items/{verId}")
    @Operation(
        summary = "Few-Shot 아이템 목록 조회",
        description = "특정 버전의 Few-Shot 아이템 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "아이템 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "버전을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<FewShotItemRes>> getFewShotItemListById(
            @PathVariable("verId") 
            @Parameter(description = "버전 ID", example = "731203b5-c0e2-455b-a6bc-5fd710ec872c") String versionId,
            @RequestParam(value = "page", defaultValue = "1") 
            @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "100") 
            @Parameter(description = "페이지 크기", example = "100") Integer size,
            @RequestParam(value = "sort", required = false) 
            @Parameter(description = "정렬 조건") String sort,
            @RequestParam(value = "filter", required = false) 
            @Parameter(description = "필터 조건") String filter,
            @RequestParam(value = "search", required = false) 
            @Parameter(description = "검색 키워드") String search) {
        
        log.info("Few-Shot 아이템 목록 조회 요청: versionId={}, page={}, size={}", versionId, page, size);
        
        List<FewShotItemRes> fewShotItemRes = fewShotService.getFewShotItemListById(versionId, page, size, sort, filter, search);
        
        log.info("Few-Shot 아이템 목록 조회 완료: versionId={}", versionId);
        
        return AxResponseEntity.ok(fewShotItemRes, "특정 Few-Shot 아이템 목록을 성공적으로 조회했습니다."); 
    }

    /**
     * Few-Shot 태그 조회 (버전별)
     * 
     * @param versionId 버전 ID
     * @return 태그 목록
     */
    @GetMapping("/tags/{verId}")
    @Operation(
        summary = "Few-Shot 태그 조회 (버전별)",
        description = "특정 버전의 Few-Shot 태그를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 조회 성공"),
        @ApiResponse(responseCode = "404", description = "버전을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<FewShotTagRes>> getFewShotTagsByVerId(
            @PathVariable("verId") 
            @Parameter(description = "버전 ID", example = "731203b5-c0e2-455b-a6bc-5fd710ec872c") String versionId) {
        
        log.info("Few-Shot 태그 조회 (버전별) 요청: versionId={}", versionId);
        
        List<FewShotTagRes> fewShotTagRes = fewShotService.getFewShotTagsByVerId(versionId);
        
        log.info("Few-Shot 태그 조회 (버전별) 완료: versionId={}", versionId);
        
        return AxResponseEntity.ok(fewShotTagRes, "Few-Shot 태그 조회 (버전별)목록을 성공적으로 조회했습니다."); 
    }

    /**
     * Few-Shot 태그 목록 조회
     * 
     * @return 모든 태그 목록
     */
    @GetMapping("/list/tags")
    @Operation(
        summary = "Few-Shot 태그 목록 조회",
        description = "모든 Few-Shot 태그 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<FewShotTagListRes> getFewShotTagList() {
        FewShotTagListRes response = fewShotService.getFewShotTagList();
        return AxResponseEntity.ok(response, "Few-Shot 태그 목록을 성공적으로 조회했습니다.");
    }

    /**
     * Few-Shot의 Lineage 관계 조회 (페이징 처리)
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (기본 6개)
     * @return Few-Shot의 Lineage 관계 목록 (페이징 포함)
     */
    @GetMapping("/{fewShotUuid}/lineage")
    @Operation(
        summary = "Few-Shot Lineage 관계 조회",
        description = "특정 Few-Shot의 Lineage 관계를 페이징하여 조회합니다. (AGENT_GRAPH 타입만)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lineage 관계 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Few-Shot을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<FewShotLineageRes>> getFewShotLineageRelations(
            @PathVariable("fewShotUuid") 
            @Parameter(description = "Few-Shot UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String fewShotUuid,
            @RequestParam(value = "page", defaultValue = "1") 
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "6") 
            @Parameter(description = "페이지 크기", example = "6") Integer size) {
        
        log.info("Few-Shot Lineage 관계 조회 요청: fewShotUuid={}, page={}, size={}", fewShotUuid, page, size);
        
        PageResponse<FewShotLineageRes> response = fewShotService.getFewShotLineageRelations(fewShotUuid, page, size);
        
        log.info("Few-Shot Lineage 관계 조회 완료: fewShotUuid={}", fewShotUuid);
        
        return AxResponseEntity.ok(response, "Few-Shot의 Lineage 관계를 성공적으로 조회했습니다.");
    }

    /**
     * Few-Shot Policy 설정
     *
     * @param fewShotUuid  Few-Shot UUID (필수)
     * @param memberId    멤버 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/{fewShotUuid}/policy")
    @Operation(summary = "Few-Shot Policy 설정", description = "Few-Shot의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Few-Shot Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<List<PolicyRequest>> setFewShotPolicy(
            @PathVariable(value = "fewShotUuid", required = true) @Parameter(description = "Few-Shot UUID", required = true, example = "bf63869d-df14-44f7-9a73-9ad9c014575c") String fewShotUuid,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("Few-Shot Policy 설정 요청 - fewShotUuid: {}, memberId: {}, projectName: {}", fewShotUuid, memberId,
                projectName);
        List<PolicyRequest> policy = fewShotService.setFewShotPolicy(fewShotUuid, memberId, projectName);
        return AxResponseEntity.ok(policy, "Few-Shot Policy가 성공적으로 설정되었습니다.");
    }
}

