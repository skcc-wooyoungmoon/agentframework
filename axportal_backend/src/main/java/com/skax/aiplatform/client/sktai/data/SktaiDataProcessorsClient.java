package com.skax.aiplatform.client.sktai.data;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.data.dto.request.ProcessorCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.ProcessorUpdate;
import com.skax.aiplatform.client.sktai.data.dto.response.Processor;
import com.skax.aiplatform.client.sktai.data.dto.response.ProcessorList;
import com.skax.aiplatform.client.sktai.data.dto.response.ProcessorDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * SKTAI Data Processors API Feign Client
 * 
 * <p>SKTAI Data 시스템의 프로세서 관리를 위한 Feign Client입니다.
 * 데이터 전처리 프로세서의 생성, 관리, 실행 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>프로세서 CRUD: 프로세서 생성, 조회, 수정, 삭제</li>
 *   <li>프로세서 목록: 페이징된 프로세서 목록 조회</li>
 *   <li>프로세서 실행: 프로세서 실행 및 결과 조회</li>
 *   <li>프로세서 상태: 실행 상태 및 로그 확인</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-data-processors-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Data Processors", description = "SKTAI 데이터 프로세서 관리 API")
public interface SktaiDataProcessorsClient {
    
    /**
     * 프로세서 목록 조회
     */
    @GetMapping("/api/v1/processors")
    @Operation(summary = "프로세서 목록 조회", description = "페이징된 프로세서 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ProcessorList getProcessors(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );
    
    /**
     * 프로세서 생성
     */
    @PostMapping("/api/v1/processors")
    @Operation(summary = "프로세서 생성", description = "새로운 프로세서를 생성합니다.")
    Processor createProcessor(@RequestBody ProcessorCreate request);
    
    /**
     * 프로세서 상세 조회
     */
    @GetMapping("/api/v1/processors/{processorId}")
    @Operation(summary = "프로세서 상세 조회", description = "프로세서 상세 정보를 조회합니다.")
    ProcessorDetail getProcessor(@PathVariable UUID processorId);
    
    /**
     * 프로세서 수정
     */
    @PutMapping("/api/v1/processors/{processorId}")
    @Operation(summary = "프로세서 수정", description = "프로세서를 수정합니다.")
    Processor updateProcessor(@PathVariable UUID processorId, @RequestBody ProcessorUpdate request);
    
    /**
     * 프로세서 삭제
     */
    @DeleteMapping("/api/v1/processors/{processorId}")
    @Operation(summary = "프로세서 삭제", description = "프로세서를 삭제합니다.")
    void deleteProcessor(@PathVariable UUID processorId);
    
    /**
     * 데이터 프로세서 실행
     * 
     * <p>지정된 프로세서를 실행하여 데이터 처리 작업을 수행합니다.
     * 실행 결과는 비동기로 처리되며, 결과 조회는 별도 API를 통해 확인할 수 있습니다.</p>
     * 
     * @param executeRequest 프로세서 실행 요청 정보
     * @return 실행 결과 정보
     */
    @PostMapping("/api/v1/processors/execute")
    @Operation(
        summary = "데이터 프로세서 실행",
        description = "지정된 프로세서를 실행하여 데이터 처리 작업을 수행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "프로세서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Object executeProcessor(@RequestBody Object executeRequest);
}
