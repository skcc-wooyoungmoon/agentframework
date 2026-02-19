package com.skax.aiplatform.client.sktai.knowledge;

import com.skax.aiplatform.client.sktai.knowledge.dto.response.*;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Knowledge Custom Scripts API 클라이언트
 * 
 * <p>SKTAI Knowledge API의 Custom Scripts 관리 기능을 제공하는 Feign Client입니다.
 * 사용자 정의 로더(Loader) 및 스플리터(Splitter) 스크립트를 관리하고 테스트할 수 있는 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Custom Script 관리</strong>: 사용자 정의 스크립트 등록, 조회, 수정, 삭제</li>
 *   <li><strong>Script 타입</strong>: Loader 및 Splitter 스크립트 지원</li>
 *   <li><strong>Script 테스트</strong>: 실제 문서를 사용한 스크립트 동작 테스트</li>
 *   <li><strong>정책 관리</strong>: 스크립트별 접근 권한 및 정책 설정</li>
 * </ul>
 * 
 * <h3>Custom Script 유형:</h3>
 * <ul>
 *   <li><strong>Loader Script</strong>: 문서 파일을 읽어 텍스트로 변환하는 사용자 정의 로직</li>
 *   <li><strong>Splitter Script</strong>: 문서를 청크(chunk)로 분할하는 사용자 정의 로직</li>
 * </ul>
 * 
 * <h3>테스트 기능:</h3>
 * <ul>
 *   <li><strong>Loader 테스트</strong>: 실제 문서 파일로 로더 스크립트 동작 검증</li>
 *   <li><strong>Splitter 테스트</strong>: 문서 분할 로직 검증 및 결과 확인</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><strong>POST /api/v1/knowledge/custom_scripts</strong>: Custom Script 등록</li>
 *   <li><strong>GET /api/v1/knowledge/custom_scripts</strong>: Custom Script 목록 조회</li>
 *   <li><strong>GET /api/v1/knowledge/custom_scripts/{script_id}</strong>: Custom Script 상세 조회</li>
 *   <li><strong>PUT /api/v1/knowledge/custom_scripts/{script_id}</strong>: Custom Script 수정</li>
 *   <li><strong>DELETE /api/v1/knowledge/custom_scripts/{script_id}</strong>: Custom Script 삭제</li>
 *   <li><strong>POST /api/v1/knowledge/custom_scripts/{script_id}/test/loader</strong>: Loader Script 테스트</li>
 *   <li><strong>POST /api/v1/knowledge/custom_scripts/{script_id}/test/splitter</strong>: Splitter Script 테스트</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Tag(name = "SKTAI Knowledge Custom Scripts", description = "SKTAI Knowledge Custom Scripts 관리 API")
@FeignClient(
    name = "sktai-knowledge-custom-scripts-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiCustomScriptsClient {

    // =========================================
    // Custom Script 기본 관리
    // =========================================

    /**
     * Custom Script 목록 조회
     * 
     * <p>등록된 Custom Script 목록을 페이징 형태로 조회합니다.
     * 스크립트 타입, 검색어 등으로 필터링하여 원하는 스크립트를 찾을 수 있습니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건 (예: "name,asc")
     * @param filter 필터 조건
     * @param search 검색어 (이름 및 설명에서 검색)
     * @param customScriptType 스크립트 타입 필터 (loader/splitter)
     * @return 페이징된 Custom Script 목록
     */
    @Operation(
        summary = "Custom Script 목록 조회",
        description = "등록된 Custom Script 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Custom Script 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = CustomScriptsResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/custom_scripts")
    CustomScriptsResponse getCustomScripts(
        @Parameter(description = "페이지 번호 (1부터 시작)")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수")
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        
        @Parameter(description = "정렬 조건 (예: 'name,asc')")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어 (이름 및 설명에서 검색)")
        @RequestParam(value = "search", required = false) String search,
        
        @Parameter(description = "스크립트 타입 필터")
        @RequestParam(value = "custom_script_type", required = false) String customScriptType
    );

    /**
     * Custom Script 등록
     * 
     * <p>새로운 Custom Script를 등록합니다.
     * 스크립트 파일과 함께 이름, 설명, 타입 등의 메타데이터를 함께 제공해야 합니다.</p>
     * 
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>스크립트 파일은 Python 파일(.py)이어야 합니다</li>
     *   <li>Loader 스크립트는 특정 함수 시그니처를 준수해야 합니다</li>
     *   <li>Splitter 스크립트는 정의된 인터페이스를 구현해야 합니다</li>
     * </ul>
     * 
     * @param name 스크립트 이름
     * @param description 스크립트 설명
     * @param scriptType 스크립트 타입 (loader/splitter)
     * @param script 스크립트 파일
     * @return 등록된 Custom Script ID
     */
    
    @Operation(
        summary = "Custom Script 등록",
        description = "새로운 Custom Script를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Custom Script 등록 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "리소스 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류"),
        @ApiResponse(responseCode = "502", description = "서버 오류"),
        @ApiResponse(responseCode = "503", description = "서버 오류"),
        @ApiResponse(responseCode = "504", description = "서버 오류"),

    })
    @PostMapping(value = "/api/v1/knowledge/custom_scripts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Object createCustomScript(
        @Parameter(description = "스크립트 이름", required = true)
        @RequestPart("name") String name,
        
        @Parameter(description = "스크립트 설명", required = true)
        @RequestPart("description") String description,
        
        @Parameter(description = "스크립트 타입 (loader/splitter)", required = true)
        @RequestPart("script_type") String script_type,
        
        @Parameter(description = "스크립트 파일", required = true)
        @RequestPart("script") MultipartFile script,

        @Parameter(description = "스크립트 정책", required = false)
        @RequestPart(value = "policy", required = false) String policy
    );

    /**
     * Custom Script 상세 조회
     * 
     * <p>특정 Custom Script의 상세 정보를 조회합니다.
     * 스크립트 메타데이터, 생성 정보, 스크립트 내용 등을 포함합니다.</p>
     * 
     * @param scriptId Custom Script ID (UUID)
     * @return Custom Script 상세 정보
     */
    @Operation(
        summary = "Custom Script 상세 조회",
        description = "특정 Custom Script의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Custom Script 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/custom_scripts/{script_id}")
    CustomScriptDetailResponse getCustomScript(
        @Parameter(description = "Custom Script ID (UUID 형식)", required = true)
        @PathVariable("script_id") String scriptId
    );

    /**
     * Custom Script 수정
     * 
     * <p>기존 Custom Script의 정보를 수정합니다.
     * 이름, 설명, 스크립트 파일 등을 업데이트할 수 있습니다.</p>
     * 
     * <p><strong>참고:</strong> 스크립트 타입은 변경할 수 없습니다.</p>
     * 
     * @param scriptId Custom Script ID (UUID)
     * @param name 수정할 스크립트 이름 (선택적)
     * @param description 수정할 스크립트 설명 (선택적)
     * @param script 수정할 스크립트 파일 (선택적)
     * @return 수정 처리 결과
     */
    @Operation(
        summary = "Custom Script 수정",
        description = "기존 Custom Script의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Custom Script 수정 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping(value = "/api/v1/knowledge/custom_scripts/{script_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void updateCustomScript(
        @Parameter(description = "Custom Script ID (UUID 형식)", required = true)
        @PathVariable("script_id") String scriptId,
        
        @Parameter(description = "수정할 스크립트 이름")
        @RequestPart(value = "name", required = false) String name,
        
        @Parameter(description = "수정할 스크립트 설명")
        @RequestPart(value = "description", required = false) String description,
        
        @Parameter(description = "수정할 스크립트 파일")
        @RequestPart(value = "script", required = false) MultipartFile script
    );

    /**
     * Custom Script 삭제
     * 
     * <p>특정 Custom Script를 삭제합니다.
     * 삭제된 스크립트는 더 이상 Document 처리에 사용할 수 없습니다.</p>
     * 
     * <p><strong>주의:</strong> 현재 사용 중인 스크립트는 삭제할 수 없습니다.</p>
     * 
     * @param scriptId Custom Script ID (UUID)
     */
    @Operation(
        summary = "Custom Script 삭제",
        description = "특정 Custom Script를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Custom Script 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/custom_scripts/{script_id}")
    void deleteCustomScript(
        @Parameter(description = "Custom Script ID (UUID 형식)", required = true)
        @PathVariable("script_id") String scriptId
    );

    // =========================================
    // Custom Script 테스트
    // =========================================

    /**
     * Custom Loader Script 테스트
     * 
     * <p>Custom Loader Script의 동작을 실제 문서 파일로 테스트합니다.
     * 스크립트가 문서를 올바르게 로드하고 텍스트를 추출하는지 검증할 수 있습니다.</p>
     * 
     * <p><strong>테스트 과정:</strong></p>
     * <ol>
     *   <li>제공된 문서 파일을 스크립트로 처리</li>
     *   <li>텍스트 추출 결과 및 메타데이터 확인</li>
     *   <li>처리 시간 및 오류 여부 검사</li>
     *   <li>결과 통계 및 상세 정보 반환</li>
     * </ol>
     * 
     * @param documentFile 테스트할 문서 파일
     * @param fileMetadata 파일 메타데이터 (JSON 형태, 선택적)
     * @param loaderScriptFile 테스트할 Loader 스크립트 파일
     * @return 테스트 결과 및 처리 통계
     */
    @Operation(
        summary = "Custom Loader Script 테스트",
        description = "Custom Loader Script의 동작을 실제 문서로 테스트합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Loader Script 테스트 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping(value = "/api/v1/knowledge/custom_scripts/test/loader", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ScriptTestResponse testLoaderScript(
        @Parameter(description = "테스트할 문서 파일", required = true)
        @RequestPart("document_file") MultipartFile documentFile,
        
        @Parameter(description = "파일 메타데이터 (JSON 형태)")
        @RequestPart(value = "file_metadata", required = false) String fileMetadata,
        
        @Parameter(description = "테스트할 Loader 스크립트 파일", required = true)
        @RequestPart("loader_script_file") MultipartFile loaderScriptFile
    );

    /**
     * Custom Splitter Script 테스트
     * 
     * <p>Custom Splitter Script의 동작을 실제 문서 파일로 테스트합니다.
     * 문서 로딩 후 스플리터 스크립트가 문서를 적절히 청크로 분할하는지 검증할 수 있습니다.</p>
     * 
     * <p><strong>테스트 과정:</strong></p>
     * <ol>
     *   <li>지정된 로더로 문서 파일 처리</li>
     *   <li>Splitter 스크립트로 문서 분할</li>
     *   <li>생성된 청크의 품질 및 분할 결과 분석</li>
     *   <li>청크 크기, 개수, 중복도 등 통계 제공</li>
     * </ol>
     * 
     * @param documentFile 테스트할 문서 파일
     * @param fileMetadata 파일 메타데이터 (JSON 형태, 선택적)
     * @param loaderType 사용할 로더 타입
     * @param customLoaderId 커스텀 로더 ID (loaderType이 CustomLoader인 경우 필수)
     * @param toolId 데이터 수집 도구 ID (loaderType이 DataIngestionTool인 경우 필수)
     * @param splitterScriptFile 테스트할 Splitter 스크립트 파일
     * @return 테스트 결과 및 분할 통계
     */
    @Operation(
        summary = "Custom Splitter Script 테스트",
        description = "Custom Splitter Script의 동작을 실제 문서로 테스트합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Splitter Script 테스트 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping(value = "/api/v1/knowledge/custom_scripts/test/splitter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ScriptTestResponse testSplitterScript(
        @Parameter(description = "테스트할 문서 파일", required = true)
        @RequestPart("document_file") MultipartFile documentFile,
        
        @Parameter(description = "파일 메타데이터 (JSON 형태)")
        @RequestPart(value = "file_metadata", required = false) String fileMetadata,
        
        @Parameter(description = "사용할 로더 타입", required = true)
        @RequestPart("loader_type") String loaderType,
        
        @Parameter(description = "커스텀 로더 ID (loaderType이 CustomLoader인 경우 필수)")
        @RequestPart(value = "custom_loader_id", required = false) String customLoaderId,
        
        @Parameter(description = "데이터 수집 도구 ID (loaderType이 DataIngestionTool인 경우 필수)")
        @RequestPart(value = "tool_id", required = false) String toolId,
        
        @Parameter(description = "테스트할 Splitter 스크립트 파일", required = true)
        @RequestPart("splitter_script_file") MultipartFile splitterScriptFile
    );
}
