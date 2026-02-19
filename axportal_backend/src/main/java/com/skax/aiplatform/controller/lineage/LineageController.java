package com.skax.aiplatform.controller.lineage;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.lineage.request.LineageCreateReq;
import com.skax.aiplatform.dto.lineage.request.LineageSearchReq;
import com.skax.aiplatform.dto.lineage.response.LineageRelationRes;
import com.skax.aiplatform.service.lineage.LineageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Lineage 관리 컨트롤러
 * 
 * <p>Lineage 관계 관리를 위한 API 엔드포인트를 제공합니다.
 * 객체 간의 의존성과 데이터 흐름을 추적하고 관리하는 기능을 포함합니다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/lineage")
@RequiredArgsConstructor
@Tag(name = "Lineage Management", description = "Lineage 관계 관리 API")
public class LineageController {

    private final LineageService lineageService;

    /**
     * Lineage 관계 생성
     * 
     * @param request Lineage 생성 요청
     * @return 생성 결과
     */
    @PostMapping
    @Operation(
        summary = "Lineage 관계 생성",
        description = "두 객체 간의 의존성 관계를 생성합니다. " +
                     "USE: 소스가 타겟을 사용하는 관계, CREATE: 소스가 타겟을 생성하는 관계"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lineage 관계 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> createLineage(
            @RequestBody 
            @Parameter(description = "Lineage 생성 요청") LineageCreateReq request) {
        
        log.info("Lineage 관계 생성 요청: sourceKey={}, targetKey={}, action={}", 
                request.getSourceKey(), request.getTargetKey(), request.getAction());
        
        lineageService.createLineage(request);
        
        log.info("Lineage 관계 생성 완료: sourceKey={}, targetKey={}", 
                request.getSourceKey(), request.getTargetKey());
        
        return AxResponseEntity.ok(null, "Lineage 관계가 성공적으로 생성되었습니다.");
    }

    /**
     * Lineage 관계 조회 (BFS 탐색)
     * 
     * @param objectKey 탐색할 객체 키
     * @param direction 탐색 방향 (UP/DOWN)
     * @param depth 탐색 깊이 (선택적)
     * @return Lineage 관계 목록
     */
    @GetMapping("/{objectKey}/{direction}")
    @Operation(
        summary = "Lineage 관계 조회 (BFS 탐색)",
        description = "지정된 객체를 시작점으로 BFS 알고리즘을 사용하여 " +
                     "연결된 모든 Lineage 관계를 탐색합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lineage 관계 조회 성공"),
        @ApiResponse(responseCode = "404", description = "객체를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<LineageRelationRes>> getLineage(
            @PathVariable("objectKey") 
            @Parameter(description = "탐색할 객체 키", example = "fewshot-001") String objectKey,
            @PathVariable("direction") 
            @Parameter(description = "탐색 방향 (upstream: 상위 의존성, downstream: 하위 의존성)", example = "upstream") String direction,
            @RequestParam(value = "action", required = false) 
            @Parameter(description = "액션 타입", example = "USE") String action,
            @RequestParam(value = "depth", required = false) 
            @Parameter(description = "탐색 깊이 (선택적)", example = "5") Integer depth) {
        
        log.info("Lineage 관계 조회 요청: objectKey={}, direction={}, action={}, depth={}", objectKey, direction, action, depth);
        
        LineageSearchReq request = LineageSearchReq.builder()
                .objectKey(objectKey)
                .direction(direction)
                .depth(depth)
                .build();
        
        List<LineageRelationRes> lineageRelations = lineageService.getLineage(request);
        
        log.info("Lineage 관계 조회 완료: objectKey={}, relationsCount={}", objectKey, lineageRelations.size());
        
        return AxResponseEntity.ok(lineageRelations, "Lineage 관계를 성공적으로 조회했습니다.");
    }

    /**
     * 공유 Lineage 등록
     * 
     * @param objectKeys 객체 키 목록
     * @return 공유 Lineage 관계 목록
     */
    @PostMapping("/shared")
    @Operation(
        summary = "공유 Lineage 조회",
        description = "여러 객체 간의 공통 Lineage 관계를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "공유 Lineage 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<LineageRelationRes>> getSharedLineage(
            @RequestBody 
            @Parameter(description = "객체 키 목록", example = "[\"fewshot-001\", \"fewshot-002\", \"fewshot-003\"]") 
            List<String> objectKeys) {
        
        log.info("공유 Lineage 조회 요청: objectKeys={}", objectKeys);
        
        List<LineageRelationRes> sharedLineage = lineageService.getSharedLineage(objectKeys);
        
        log.info("공유 Lineage 조회 완료: objectKeys={}, sharedRelationsCount={}", objectKeys, sharedLineage.size());
        
        return AxResponseEntity.ok(sharedLineage, "공유 Lineage 관계를 성공적으로 조회했습니다.");
    }

    /**
     * Lineage 관계 삭제
     * 
     * @param sourceKey 삭제할 소스 객체 키
     * @return 삭제 결과
     */
    @DeleteMapping("/{sourceKey}")
    @Operation(
        summary = "Lineage 관계 삭제",
        description = "특정 객체를 소스로 하는 모든 Lineage 관계를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lineage 관계 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "객체를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteLineage(
            @PathVariable("sourceKey") 
            @Parameter(description = "삭제할 소스 객체 키", example = "fewshot-001") String sourceKey) {
        
        log.info("Lineage 관계 삭제 요청: sourceKey={}", sourceKey);
        
        lineageService.deleteLineage(sourceKey);
        
        log.info("Lineage 관계 삭제 완료: sourceKey={}", sourceKey);
        
        return AxResponseEntity.ok(null, "Lineage 관계가 성공적으로 삭제되었습니다.");
    }

}
