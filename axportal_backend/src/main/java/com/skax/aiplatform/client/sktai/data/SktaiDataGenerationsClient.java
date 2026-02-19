package com.skax.aiplatform.client.sktai.data;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.data.dto.request.GenerationCreate;
import com.skax.aiplatform.client.sktai.data.dto.response.Generation;
import com.skax.aiplatform.client.sktai.data.dto.response.GenerationList;
import com.skax.aiplatform.client.sktai.data.dto.response.GenerationDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * SKTAI Data Generations API Feign Client
 * 
 * <p>SKTAI Data 시스템의 데이터 생성 작업 관리를 위한 Feign Client입니다.
 * AI 기반 데이터 생성 작업의 생성, 조회, 수정, 삭제 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>생성 작업 CRUD: 생성 작업 생성, 조회, 수정, 삭제</li>
 *   <li>생성 작업 목록: 페이징된 생성 작업 목록 조회</li>
 *   <li>생성 결과 조회: 생성 작업 결과 및 상태 조회</li>
 *   <li>생성 작업 모니터링: 진행 상황 및 로그 확인</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-data-generations-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Data Generations", description = "SKTAI 데이터 생성 작업 관리 API")
public interface SktaiDataGenerationsClient {
    
    /**
     * 생성 작업 목록 조회
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 생성 작업 목록
     */
    @GetMapping("/api/v1/generations")
    @Operation(
        summary = "생성 작업 목록 조회",
        description = "페이징된 생성 작업 목록을 조회합니다. 검색 및 필터링 기능을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    GenerationList getGenerations(
        @Parameter(description = "페이지 번호", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지 크기", example = "10")
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        
        @Parameter(description = "정렬 기준 (예: created_at:desc)")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어")
        @RequestParam(value = "search", required = false) String search
    );
    
    /**
     * 새로운 생성 작업 생성
     * 
     * @param request 생성 작업 생성 요청
     * @return 생성된 생성 작업 정보
     */
    @PostMapping("/api/v1/generations")
    @Operation(
        summary = "생성 작업 생성",
        description = "새로운 데이터 생성 작업을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Generation createGeneration(
        @Parameter(description = "생성 작업 생성 요청", required = true)
        @RequestBody GenerationCreate request
    );
    
    /**
     * 생성 작업 상세 조회
     * 
     * @param generationId 생성 작업 ID
     * @return 생성 작업 상세 정보
     */
    @GetMapping("/api/v1/generations/{generationId}")
    @Operation(
        summary = "생성 작업 상세 조회",
        description = "지정된 생성 작업의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "생성 작업을 찾을 수 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    GenerationDetail getGeneration(
        @Parameter(description = "생성 작업 ID", required = true)
        @PathVariable UUID generationId
    );
    
    /**
     * 생성 작업 수정
     * 
     * @param generationId 생성 작업 ID
     * @param request 생성 작업 수정 요청
     * @return 수정된 생성 작업 정보
     */
    @PutMapping("/api/v1/generations/{generationId}")
    @Operation(
        summary = "생성 작업 수정",
        description = "지정된 생성 작업을 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "생성 작업을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Generation updateGeneration(
        @Parameter(description = "생성 작업 ID", required = true)
        @PathVariable UUID generationId,
        
        @Parameter(description = "생성 작업 수정 요청", required = true)
        @RequestBody GenerationCreate request
    );
    
    /**
     * 생성 작업 삭제
     * 
     * @param generationId 생성 작업 ID
     */
    @DeleteMapping("/api/v1/generations/{generationId}")
    @Operation(
        summary = "생성 작업 삭제",
        description = "지정된 생성 작업을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "생성 작업을 찾을 수 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void deleteGeneration(
        @Parameter(description = "생성 작업 ID", required = true)
        @PathVariable UUID generationId
    );
}
