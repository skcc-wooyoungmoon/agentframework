package com.skax.aiplatform.client.sktai.data;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.data.dto.request.GeneratorCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.GeneratorUpdate;
import com.skax.aiplatform.client.sktai.data.dto.response.Generator;
import com.skax.aiplatform.client.sktai.data.dto.response.GeneratorList;
import com.skax.aiplatform.client.sktai.data.dto.response.GeneratorDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Data Generators API Feign Client
 * 
 * <p>SKTAI Data 시스템의 생성기 관리를 위한 Feign Client입니다.
 * AI 모델 기반 데이터 생성기의 관리 및 실행 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>생성기 CRUD: 생성기 생성, 조회, 수정, 삭제</li>
 *   <li>생성기 목록: 페이징된 생성기 목록 조회</li>
 *   <li>생성기 실행: 생성기 실행 및 결과 조회</li>
 *   <li>생성기 설정: 모델 파라미터 및 설정 관리</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-data-generators-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Data Generators", description = "SKTAI 데이터 생성기 관리 API")
public interface SktaiDataGeneratorsClient {
    
    /**
     * 생성기 목록 조회
     */
    @GetMapping("/api/v1/generators")
    @Operation(summary = "생성기 목록 조회", description = "페이징된 생성기 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    GeneratorList getGenerators(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );
    
    /**
     * 생성기 생성
     */
    @PostMapping("/api/v1/generators")
    @Operation(summary = "생성기 생성", description = "새로운 생성기를 생성합니다.")
    Generator createGenerator(@RequestBody GeneratorCreate request);
    
    /**
     * 생성기 상세 조회
     */
    @GetMapping("/api/v1/generators/{generatorId}")
    @Operation(summary = "생성기 상세 조회", description = "생성기 상세 정보를 조회합니다.")
    GeneratorDetail getGenerator(@PathVariable String generatorId);
    
    /**
     * 생성기 수정
     */
    @PutMapping("/api/v1/generators/{generatorId}")
    @Operation(summary = "생성기 수정", description = "생성기를 수정합니다.")
    Generator updateGenerator(@PathVariable String generatorId, @RequestBody GeneratorUpdate request);
    
    /**
     * 생성기 삭제
     */
    @DeleteMapping("/api/v1/generators/{generatorId}")
    @Operation(summary = "생성기 삭제", description = "생성기를 삭제합니다.")
    void deleteGenerator(@PathVariable String generatorId);
}
