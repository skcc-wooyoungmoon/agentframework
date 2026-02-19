package com.skax.aiplatform.client.udp.embedding;

import com.skax.aiplatform.client.udp.config.UdpFeignConfig;
import com.skax.aiplatform.client.udp.embedding.dto.request.KtEmbeddingRequest;
import com.skax.aiplatform.client.udp.embedding.dto.response.KtEmbeddingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * UDP KT 임베딩 추론 API 클라이언트
 * 
 * <p>UDP 시스템의 KT 임베딩 추론 API를 연동하는 Feign 클라이언트입니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>텍스트 임베딩 생성</li>
 *   <li>벡터 변환 및 추론</li>
 *   <li>유사도 계산을 위한 벡터 표현</li>
 * </ul>
 * 
 * <h3>임베딩 모델:</h3>
 * <p>KT에서 제공하는 한국어 특화 임베딩 모델을 사용하여
 * 한국어 텍스트에 대한 높은 품질의 벡터 표현을 생성합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@FeignClient(
    name = "udp-kt-embedding-client",
    url = "${udp.api.base-url}",
    configuration = UdpFeignConfig.class
)
@Tag(name = "UDP KT Embedding API", description = "UDP KT 임베딩 추론 API")
public interface UdpKtEmbeddingClient {

    /**
     * KT 임베딩 추론
     * 
     * <p>입력된 텍스트를 KT 임베딩 모델을 사용하여 벡터로 변환합니다.
     * 생성된 벡터는 텍스트 유사도 계산, 검색, 클러스터링 등에 활용할 수 있습니다.</p>
     * 
     * @param request 임베딩 생성 요청 데이터
     * @return 생성된 임베딩 벡터 및 메타데이터
     */
    @PostMapping(value = "/api/v1/kt/embedding/inference", 
                 consumes = "application/json", 
                 produces = "application/json")
    @Operation(
        summary = "KT 임베딩 추론",
        description = "입력된 텍스트를 KT 임베딩 모델을 사용하여 벡터로 변환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "임베딩 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "429", description = "요청 한도 초과"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    KtEmbeddingResponse generateEmbedding(
        @Parameter(description = "임베딩 생성 요청 데이터", required = true)
        @RequestBody KtEmbeddingRequest request
    );
}