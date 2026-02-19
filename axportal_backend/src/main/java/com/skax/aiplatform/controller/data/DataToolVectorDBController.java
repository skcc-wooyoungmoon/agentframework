package com.skax.aiplatform.controller.data;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.response.*;
import com.skax.aiplatform.dto.data.request.*;
import com.skax.aiplatform.service.data.DataToolVectorDBService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * 데이터 도구 - 벡터 DB 관리 컨트롤러
 *
 * <p>데이터 도구 - 벡터 DB 관리 API 엔드포인트를 제공합니다.
 * 데이터 도구의 벡터 DB 목록 및 상세 정보 조회 기능을 포함합니다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/dataTool/vectorDb")
@RequiredArgsConstructor
@Tag(name = "Data-tool VectorDB Management", description = "데이터 도구 - VectorDB 관리 API")
public class DataToolVectorDBController {

    private final DataToolVectorDBService dataToolVectorDBService;

    /**
     * 데이터 도구 벡터 DB 목록 조회
     *
     * @param pageable 페이지 정보
     * @param sort 정렬 조건
     * @return Data-Tools Processors 응답 (페이지네이션 포함)
     */
    @GetMapping
    @Operation(
            summary = "Data-tool VectorDBs 목록 조회",
            description = "Data-tool VectorDBs 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data-tool VectorDBs 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<DataToolVectorDBRes>> getVectorDBList(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "created_at,desc")
            @Parameter(description = "정렬 조건") String sort,
            @RequestParam(value = "filter", required = false)
            @Parameter(description = "필터 조건") String filter,
            @RequestParam(value = "search", required = false)
            @Parameter(description = "검색 키워드") String search
            ) {
       
        PageResponse<DataToolVectorDBRes> dataToolVectorDBRes = dataToolVectorDBService.getVectorDBList(page, size, sort, filter, search);
        return AxResponseEntity.okPage(dataToolVectorDBRes, "Data-tool VectorDBs 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 벡터 DB 상세 조회
     *
     * @param vectorDbId 벡터 DB ID
     * @return 벡터 DB 상세 정보
     */
    @GetMapping("/{vectorDbId}")
    @Operation(
            summary = "벡터 DB 상세 조회",
            description = "UUID 기반으로 특정 벡터 DB 상세 정보를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "벡터 DB 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 벡터 DB가 존재하지 않음")
    })
    public AxResponseEntity<DataToolVectorDBDetailRes> getVectorDBById(
            @Parameter(description = "벡터 DB ID(UUID)", required = true)
            @PathVariable("vectorDbId") String vectorDbId
    ) {
        DataToolVectorDBDetailRes dataToolVectorDBResById = dataToolVectorDBService.getVectorDBById(vectorDbId);
        return AxResponseEntity.ok(dataToolVectorDBResById, "벡터 DB를 성공적으로 조회했습니다.");
    }

    /**
     * 벡터 DB 삭제
     *
     * @param vectorDbId 벡터 DB ID
     */
    @DeleteMapping("/{vectorDbId}")
    @Operation(
            summary = "벡터 DB 삭제",
            description = "UUID 기반으로 특정 벡터 DB를 삭제한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "벡터 DB 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 벡터 DB가 존재하지 않음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteVectorDB(
            @Parameter(description = "벡터 DB ID(UUID)", required = true)
            @PathVariable("vectorDbId") String vectorDbId
    ) {
        dataToolVectorDBService.deleteVectorDB(vectorDbId);
        return AxResponseEntity.ok(null, "벡터 DB를 성공적으로 삭제했습니다.");
    }

    /**
     * 벡터 DB 생성
     *
     * @param request 벡터 DB 생성 요청 정보
     */
    @PostMapping
    @Operation(
            summary = "벡터 DB 생성",
            description = "벡터 DB를 생성한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "벡터 DB 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 벡터 DB가 존재하지 않음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<DataToolVectorDBCreateRes> createVectorDB(
            @Parameter(description = "벡터 DB 생성 요청 정보", required = true)
            @RequestBody DataToolVectorDBCreateReq request
    ) {
        DataToolVectorDBCreateRes dataToolVectorDBCreateRes = dataToolVectorDBService.createVectorDB(request);
        return AxResponseEntity.ok(dataToolVectorDBCreateRes, "벡터 DB를 성공적으로 생성했습니다.");
    }

   /**
     * 벡터 DB 생성
     *
     * @param request 벡터 DB 생성 요청 정보
     */
    @PutMapping("/{vectorDbId}")
    @Operation(
            summary = "벡터 DB 수정",
            description = "UUID 기반으로 특정 벡터 DB를 수정한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "벡터 DB 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 벡터 DB가 존재하지 않음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> updateVectorDB(
            @Parameter(description = "벡터 DB ID(UUID)", required = true)
            @PathVariable("vectorDbId") String vectorDbId,
            @Parameter(description = "벡터 DB 수정 요청 정보", required = true)
            @RequestBody DataToolVectorDBUpdateReq request
    ) {
        dataToolVectorDBService.updateVectorDB(vectorDbId, request);
        return AxResponseEntity.ok(null, "벡터 DB를 성공적으로 수정했습니다.");
    }

    /**
     * 벡터 DB 연결 정보 조회
     * @return 벡터 DB 연결 정보 정보
     */
    @GetMapping("/connetionArgs")
    @Operation(
            summary = "벡터 DB 연결 정보 조회",
            description = "벡터 DB 연결 정보 정보를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "벡터 DB 연결 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 벡터 DB 연결 정보가 존재하지 않음")
    })
    public AxResponseEntity<List<DataArgRes>> getConnectionArgs() {
        List<DataArgRes> dataArgsRes = dataToolVectorDBService.getConnectionArgs();
        return AxResponseEntity.ok(dataArgsRes, "벡터 DB 연결 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 벡터디비 Policy 설정
     *
     * @param vectorDBId  벡터디비 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/{vectordb_id}/policy")
    @Operation(summary = "벡터디비 Policy 설정", description = "벡터디비의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "벡터디비 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<List<PolicyRequest>> setVectorDBPolicy(
            @PathVariable(value = "vectordb_id", required = true) @Parameter(description = "벡터디비 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String vectorDBId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("벡터디비 Policy 설정 요청 - vectorDBId: {}, memberId: {}, projectName: {}", vectorDBId, memberId,
                projectName);
        List<PolicyRequest> policy = dataToolVectorDBService.setVectorDBPolicy(vectorDBId, memberId, projectName);
        return AxResponseEntity.ok(policy, "벡터디비 Policy가 성공적으로 설정되었습니다.");
    }
}
