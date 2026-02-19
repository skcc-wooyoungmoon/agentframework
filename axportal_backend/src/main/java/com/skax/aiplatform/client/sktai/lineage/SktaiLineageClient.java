package com.skax.aiplatform.client.sktai.lineage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageObjectCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * SKTAI Lineage 관리 Feign Client
 * 
 * <p>SKTAI 시스템의 Lineage 관계 관리를 위한 Feign Client입니다.
 * 객체 간의 의존성과 데이터 흐름을 추적하고 관리하는 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Lineage 생성</strong>: 객체 간 새로운 관계 생성</li>
 *   <li><strong>Lineage 삭제</strong>: 특정 객체의 모든 관계 삭제</li>
 *   <li><strong>BFS 탐색</strong>: 방향성 있는 관계 탐색 (상위/하위)</li>
 *   <li><strong>공유 Lineage</strong>: 여러 객체 간 공통 관계 탐색</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><code>POST /api/v1/lineages</code>: Lineage 생성</li>
 *   <li><code>DELETE /api/v1/lineages/{source_key}</code>: Lineage 삭제</li>
 *   <li><code>GET /api/v1/lineages/{object_key}/{direction}</code>: BFS 탐색</li>
 *   <li><code>POST /api/v1/lineages/shared</code>: 공유 Lineage 조회</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@FeignClient(
    name = "sktai-lineage-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Lineage", description = "SKTAI Lineage 관계 관리 API")
public interface SktaiLineageClient {
    
    /**
     * Lineage 생성
     * 
     * <p>소스 객체와 타겟 객체 간의 새로운 Lineage 관계를 생성합니다.
     * 액션 타입(USE/CREATE)에 따라 의존성 방향과 성격이 결정됩니다.</p>
     * 
     * @param request Lineage 생성 요청 정보 (source_key, target_key, action 포함)
     * @return 생성 결과 (성공 시 빈 응답)
     * @throws BusinessException 생성 실패 시
     */
    @PostMapping(
        value = "/api/v1/lineages",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Lineage 관계 생성",
        description = "소스 객체와 타겟 객체 간의 새로운 Lineage 관계를 생성합니다. " +
                     "액션 타입(USE/CREATE)을 통해 관계의 성격을 정의할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lineage 생성 성공"
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = com.skax.aiplatform.client.sktai.lineage.dto.response.HTTPValidationError.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류"
        )
    })
    void createLineage(
        @Parameter(description = "Lineage 생성 요청 정보", required = true)
        @RequestBody LineageCreate request
    );
    
    /**
     * Lineage 삭제
     * 
     * <p>특정 소스 키에 연결된 모든 Lineage 관계를 삭제합니다.
     * 해당 객체가 소스로 참여하는 모든 관계가 제거됩니다.</p>
     * 
     * @param sourceKey 삭제할 소스 객체의 고유 키
     * @return 삭제 결과 (성공 시 빈 응답)
     * @throws BusinessException 삭제 실패 시
     */
    @DeleteMapping("/api/v1/lineages/{source_key}")
    @Operation(
        summary = "Lineage 관계 삭제",
        description = "특정 소스 키에 연결된 모든 Lineage 관계를 삭제합니다. " +
                     "해당 객체가 소스로 참여하는 모든 의존성 관계가 제거됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lineage 삭제 성공"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "소스 키에 해당하는 Lineage가 존재하지 않음"
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = com.skax.aiplatform.client.sktai.lineage.dto.response.HTTPValidationError.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류"
        )
    })
    void deleteLineage(
        @Parameter(description = "삭제할 소스 객체의 고유 키", required = true, example = "a0f49edd-6766-4758-92a3-13c066648bc0")
        @PathVariable("source_key") String sourceKey
    );
    
    /**
     * BFS를 통한 Lineage 탐색
     * 
     * <p>지정된 객체를 시작점으로 하여 BFS(너비 우선 탐색) 알고리즘을 사용해
     * 연결된 모든 Lineage 관계를 탐색합니다. 방향에 따라 상위 또는 하위 의존성을 조회할 수 있습니다.</p>
     * 
     * @param objectKey 탐색을 시작할 객체의 고유 키
     * @param direction 탐색 방향 (UP: 상위 의존성, DOWN: 하위 의존성)
     * @param depth 탐색할 최대 깊이 (선택적, 미지정 시 모든 깊이 탐색)
     * @return 탐색된 Lineage 관계 목록 (타입 정보 포함)
     * @throws BusinessException 탐색 실패 시
     */
    @GetMapping("/api/v1/lineages/{object_key}/{direction}")
    @Operation(
        summary = "BFS Lineage 탐색",
        description = "지정된 객체를 시작점으로 BFS 알고리즘을 사용하여 연결된 모든 Lineage 관계를 탐색합니다. " +
                     "방향 파라미터로 상위 또는 하위 의존성을 선택적으로 조회할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lineage 탐색 성공",
            content = @Content(schema = @Schema(implementation = LineageRelationWithTypes.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "객체 키에 해당하는 Lineage가 존재하지 않음"
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "유효성 검증 실패 (잘못된 방향 또는 깊이 값)",
            content = @Content(schema = @Schema(implementation = com.skax.aiplatform.client.sktai.lineage.dto.response.HTTPValidationError.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류"
        )
    })
    List<LineageRelationWithTypes> getLineageByObjectKeyAndDirection(
        @Parameter(description = "탐색할 객체의 고유 키", required = true, example = "a0f49edd-6766-4758-92a3-13c066648bc0")
        @PathVariable("object_key") String objectKey,
        
        @Parameter(description = "탐색 방향 (upstream: 상위 의존성, downstream: 하위 의존성)", required = true, example = "upstream")
        @PathVariable("direction") String direction,
        
        @Parameter(description = "액션 타입 (USE/CREATE)", required = true, example = "USE")
        @RequestParam(value = "action", required = true) String action,
        
        @Parameter(description = "탐색할 최대 깊이 (선택적, 미지정 시 전체 탐색)", example = "3")
        @RequestParam(value = "depth", required = false) Integer depth
    );
    
    /**
     * 공유 Lineage 조회
     * 
     * <p>여러 객체 간의 공통 Lineage 관계를 조회합니다.
     * 지정된 객체들이 공유하는 의존성이나 공통 조상/후손을 찾을 때 사용합니다.</p>
     * 
     * @param request 공유 Lineage 조회 요청 (여러 객체 키 포함)
     * @return 공유되는 Lineage 관계 목록
     * @throws BusinessException 조회 실패 시
     */
    @PostMapping(
        value = "/api/v1/lineages/shared",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "공유 Lineage 조회",
        description = "여러 객체 간의 공통 Lineage 관계를 조회합니다. " +
                     "지정된 객체들이 공유하는 의존성이나 공통 조상/후손을 찾을 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "공유 Lineage 조회 성공",
            content = @Content(schema = @Schema(implementation = LineageRelationWithTypes.class))
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = com.skax.aiplatform.client.sktai.lineage.dto.response.HTTPValidationError.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류"
        )
    })
    List<LineageRelationWithTypes> getSharedLineage(
        @Parameter(description = "공유 Lineage 조회 요청 정보", required = true)
        @RequestBody LineageObjectCreate request
    );
}