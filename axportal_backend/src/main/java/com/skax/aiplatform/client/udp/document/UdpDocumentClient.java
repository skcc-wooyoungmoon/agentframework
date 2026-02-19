package com.skax.aiplatform.client.udp.document;

import com.skax.aiplatform.client.udp.config.UdpFeignConfig;
import com.skax.aiplatform.client.udp.config.UdpFeignDocumentConfig;
import com.skax.aiplatform.client.udp.document.dto.request.DocumentSearchRequest;
import com.skax.aiplatform.client.udp.document.dto.response.DocumentSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * UDP Document Search API 클라이언트
 * 
 * <p>UDP 시스템의 데이터셋 문서 검색 API를 연동하는 Feign 클라이언트입니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>데이터셋 내 문서 키워드 검색</li>
 *   <li>페이징 및 정렬 지원</li>
 *   <li>검색 조건 필터링</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Tag(name = "UDP Document API", description = "UDP 문서 검색 API")
@FeignClient(
    name = "udp-document-client",
    url = "${udp.api.base-url}",
    configuration = UdpFeignDocumentConfig.class
)
public interface UdpDocumentClient {

    /**
     * 데이터셋 문서 검색
     * 
     * <p>지정된 데이터셋 내에서 키워드를 기반으로 문서를 검색합니다.</p>
     * 
     * @param apiKey x-cruz-api-key 헤더
     * @param requestBody JSON 요청 본문
     * @return 문서 검색 결과
     */
    @Operation(
        summary = "데이터셋 문서 검색",
        description = "지정된 데이터셋 내에서 키워드를 기반으로 문서를 검색합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "문서 검색 성공",
            content = @Content(schema = @Schema(implementation = DocumentSearchResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    })
    @PostMapping(
        value = "/portal/udp/api/doc/lists/v2",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    DocumentSearchResponse searchDocuments(
        @Parameter(description = "게이트웨이 API 키", required = true)
        @RequestHeader("x-cruz-api-key") String apiKey,
        
        @Parameter(description = "JSON 요청 본문", required = true)
        @RequestBody DocumentSearchRequest requestBody
    );
}