package com.skax.aiplatform.client.sktai.modelgateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.AudioSpeechRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.ChatCompletionsRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.CompletionsRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.CustomRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.EmbeddingsRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.ImagesRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.RerankRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.ResponsesRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.request.ScoreRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.AudioSpeechResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.AudioTranscriptionResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.CompletionsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.CustomEndpointResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.EmbeddingsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ImagesResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ModelListResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.RerankResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ResponseGenerationResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ScoreResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Model Gateway API Feign Client
 *
 * <p>SKTAI Model Gateway 시스템과의 통신을 담당하는 Feign 클라이언트입니다.
 * AI 모델 추론, 임베딩, 이미지 생성, 오디오 처리 등 다양한 AI 기능을 제공합니다.</p>
 *
 * <h3>주요 기능 영역:</h3>
 * <ul>
 *   <li><strong>모델 관리</strong>: 사용 가능한 AI 모델 목록 조회</li>
 *   <li><strong>텍스트 처리</strong>: 채팅 완성, 텍스트 완성, 임베딩 생성</li>
 *   <li><strong>이미지 처리</strong>: AI 기반 이미지 생성</li>
 *   <li><strong>오디오 처리</strong>: 음성 합성, 음성 인식, 번역</li>
 *   <li><strong>검색 최적화</strong>: 텍스트 유사도, 리랭킹, 스코어링</li>
 *   <li><strong>고급 기능</strong>: 커스텀 모델, 응답 생성</li>
 * </ul>
 *
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li>/api/v1/gateway/models: 모델 목록</li>
 *   <li>/api/v1/gateway/chat/completions: 채팅 완성</li>
 *   <li>/api/v1/gateway/completions: 텍스트 완성</li>
 *   <li>/api/v1/gateway/embeddings: 임베딩 생성</li>
 *   <li>/api/v1/gateway/images/generations: 이미지 생성</li>
 *   <li>/api/v1/gateway/audio/*: 오디오 처리</li>
 *   <li>/api/v1/gateway/score: 유사도 스코어링</li>
 *   <li>/api/v1/gateway/rerank: 검색 결과 리랭킹</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-08-15
 */
@FeignClient(
        name = "sktai-model-gateway-client",
        url = "${sktai.api.base-url}",
        configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Model Gateway", description = "AI 모델 게이트웨이 서비스 API")
public interface SktaiModelGatewayClient {

    /**
     * 사용 가능한 AI 모델 목록 조회
     *
     * <p>SKTAI 플랫폼에서 제공하는 모든 AI 모델의 목록과 메타데이터를 조회합니다.
     * 각 모델의 ID, 타입, 소유자, 생성일 등의 정보를 확인할 수 있습니다.</p>
     *
     * @return 사용 가능한 모델 목록과 상세 정보
     */
    @GetMapping("/api/v1/gateway/models")
    @Operation(
            summary = "AI 모델 목록 조회",
            description = "사용 가능한 모든 AI 모델의 목록과 메타데이터를 조회합니다. " +
                    "모델 선택과 권한 확인에 필요한 정보를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ModelListResponse getModels();

    /**
     * 채팅 완성 요청
     *
     * <p>대화형 AI 모델을 사용하여 채팅 형태의 응답을 생성합니다.
     * 시스템 프롬프트, 사용자 메시지, 이전 대화 기록을 바탕으로 지능적인 응답을 제공합니다.</p>
     *
     * @param request 채팅 완성 요청 데이터 (메시지, 모델, 파라미터 등)
     * @return AI가 생성한 채팅 응답과 사용량 정보
     */
    @PostMapping("/api/v1/gateway/chat/completions")
    @Operation(
            summary = "채팅 완성 생성",
            description = "대화형 AI 모델을 사용하여 채팅 응답을 생성합니다. " +
                    "GPT-4, Claude 등의 모델을 활용하여 자연스러운 대화를 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 완성 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ChatCompletionsResponse createChatCompletion(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "aip-user 헤더", required = false)
            @RequestHeader(value = "aip-user", required = false) String aipUser,
            @RequestBody ChatCompletionsRequest request
    );

    /**
     * 텍스트 완성 요청
     *
     * <p>주어진 프롬프트를 기반으로 텍스트를 자동 완성합니다.
     * 창의적 글쓰기, 코드 생성, 텍스트 확장 등에 활용할 수 있습니다.</p>
     *
     * @param request 텍스트 완성 요청 데이터 (프롬프트, 모델, 생성 파라미터 등)
     * @return 완성된 텍스트와 생성 정보
     */
    @PostMapping("/api/v1/gateway/completions")
    @Operation(
            summary = "텍스트 완성 생성",
            description = "주어진 프롬프트를 기반으로 텍스트를 자동 완성합니다. " +
                    "창의적 글쓰기, 코드 생성, 문서 작성 등에 활용할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "텍스트 완성 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    CompletionsResponse createCompletion(@RequestBody CompletionsRequest request);

    /**
     * 텍스트 임베딩 생성
     *
     * <p>입력 텍스트를 고차원 벡터로 변환합니다.
     * 유사도 검색, 클러스터링, 분류, 추천 시스템 등에 활용할 수 있습니다.</p>
     *
     * @param request 임베딩 요청 데이터 (텍스트, 모델, 인코딩 형식 등)
     * @return 임베딩 벡터와 토큰 사용량 정보
     */
    @PostMapping("/api/v1/gateway/embeddings")
    @Operation(
            summary = "텍스트 임베딩 생성",
            description = "텍스트를 고차원 벡터로 변환하여 의미적 표현을 생성합니다. " +
                    "검색, 유사도 계산, 클러스터링 등에 활용할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임베딩 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    EmbeddingsResponse createEmbeddings(@RequestBody EmbeddingsRequest request);

    /**
     * AI 이미지 생성
     *
     * <p>텍스트 설명을 바탕으로 AI가 이미지를 생성합니다.
     * DALL-E, Stable Diffusion 등의 모델을 활용하여 고품질 이미지를 제작합니다.</p>
     *
     * @param request 이미지 생성 요청 데이터 (프롬프트, 모델, 크기, 품질 등)
     * @return 생성된 이미지 URL/데이터와 안전성 검사 결과
     */
    @PostMapping("/api/v1/gateway/images/generations")
    @Operation(
            summary = "AI 이미지 생성",
            description = "텍스트 프롬프트를 기반으로 AI가 이미지를 생성합니다. " +
                    "DALL-E, Stable Diffusion 등을 활용하여 고품질 이미지를 제작할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ImagesResponse generateImages(@RequestBody ImagesRequest request);

    /**
     * 음성 합성 (Text-to-Speech)
     *
     * <p>입력 텍스트를 자연스러운 음성으로 변환합니다.
     * 다양한 음성 캐릭터와 언어를 지원하며, 속도 조절이 가능합니다.</p>
     *
     * @param request 음성 합성 요청 데이터 (텍스트, 모델, 음성, 속도 등)
     * @return 생성된 오디오 파일 (바이너리 데이터)
     */
    @PostMapping(value = "/api/v1/gateway/audio/speech", produces = {"application/json", "audio/mpeg"})
    @Operation(
            summary = "음성 합성 (TTS)",
            description = "텍스트를 자연스러운 음성으로 변환합니다. " +
                    "다양한 음성 캐릭터와 오디오 포맷을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "음성 합성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AudioSpeechResponse createSpeech(@RequestBody AudioSpeechRequest request);

    /**
     * 음성 인식 (Speech-to-Text)
     *
     * <p>오디오 파일을 텍스트로 변환합니다.
     * 다양한 언어를 지원하며, 타임스탬프와 화자 분리 기능을 제공할 수 있습니다.</p>
     *
     * @param file           변환할 오디오 파일
     * @param model          사용할 음성 인식 모델
     * @param prompt         음성 인식 힌트 (선택적)
     * @param language       오디오 언어 (선택적)
     * @param responseFormat 응답 형식 (선택적)
     * @param stream         스트리밍 여부 (선택적)
     * @return 인식된 텍스트와 메타데이터
     */
    @PostMapping(value = "/api/v1/gateway/audio/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "음성 인식 (STT)",
            description = "오디오 파일을 텍스트로 변환합니다. " +
                    "다양한 언어와 오디오 포맷을 지원하며, 타임스탬프 정보를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "음성 인식 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AudioTranscriptionResponse transcribeAudio(
            @Parameter(description = "변환할 오디오 파일", required = true)
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "사용할 음성 인식 모델", required = true)
            @RequestPart("model") String model,

            @Parameter(description = "음성 인식 힌트 (맥락 제공)")
            @RequestPart(value = "prompt", required = false) String prompt,

            @Parameter(description = "오디오 언어 (ISO 639-1 코드)")
            @RequestPart(value = "language", required = false) String language,

            @Parameter(description = "응답 형식 (json, text, srt, verbose_json 등)")
            @RequestPart(value = "response_format", required = false) String responseFormat,

            @Parameter(description = "스트리밍 응답 여부")
            @RequestPart(value = "stream", required = false) String stream
    );

    /**
     * 음성 번역 (Speech Translation)
     *
     * <p>다국어 오디오 파일을 영어 텍스트로 번역합니다.
     * 음성 인식과 번역을 동시에 수행하는 원스톱 서비스입니다.</p>
     *
     * @param file           번역할 오디오 파일
     * @param model          사용할 음성 번역 모델
     * @param prompt         번역 힌트 (선택적)
     * @param responseFormat 응답 형식 (선택적)
     * @return 번역된 영어 텍스트
     */
    @PostMapping(value = "/api/v1/gateway/audio/translations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "음성 번역",
            description = "다국어 오디오를 영어 텍스트로 번역합니다. " +
                    "음성 인식과 번역을 동시에 수행하여 효율적인 다국어 처리를 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "음성 번역 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AudioTranscriptionResponse translateAudio(
            @Parameter(description = "번역할 오디오 파일", required = true)
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "사용할 음성 번역 모델", required = true)
            @RequestPart("model") String model,

            @Parameter(description = "번역 힌트 (맥락 제공)")
            @RequestPart(value = "prompt", required = false) String prompt,

            @Parameter(description = "응답 형식 (json, text, verbose_json 등)")
            @RequestPart(value = "response_format", required = false) String responseFormat
    );

    /**
     * 텍스트 유사도 스코어링
     *
     * <p>두 텍스트 간의 의미적 유사도를 측정합니다.
     * 검색 결과 평가, 문서 매칭, 중복 탐지 등에 활용할 수 있습니다.</p>
     *
     * @param request 유사도 스코어링 요청 데이터 (모델, 비교할 텍스트들)
     * @return 유사도 스코어와 메타데이터
     */
    @PostMapping("/api/v1/gateway/score")
    @Operation(
            summary = "텍스트 유사도 스코어링",
            description = "두 텍스트 간의 의미적 유사도를 측정합니다. " +
                    "검색 품질 평가, 문서 매칭, 중복 탐지 등에 활용할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유사도 스코어링 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ScoreResponse calculateScore(@RequestBody ScoreRequest request);

    /**
     * 검색 결과 리랭킹
     *
     * <p>주어진 쿼리에 대해 여러 문서의 관련도를 재평가하여 순위를 재정렬합니다.
     * 검색 품질 향상과 정확한 정보 제공에 중요한 역할을 합니다.</p>
     *
     * @param apiVersion API 버전 (예: "v1")
     * @param request    리랭킹 요청 데이터 (쿼리, 문서 목록, 모델 등)
     * @return 재정렬된 문서 목록과 관련도 스코어
     */
    @PostMapping("/api/v1/gateway/{api_version}/rerank")
    @Operation(
            summary = "검색 결과 리랭킹",
            description = "쿼리에 대한 문서들의 관련도를 재평가하여 순위를 재정렬합니다. " +
                    "검색 품질을 크게 향상시킬 수 있는 핵심 기능입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리랭킹 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    RerankResponse rerankDocuments(
            @Parameter(description = "API 버전", required = true, example = "v1")
            @PathVariable("api_version") String apiVersion,

            @RequestBody RerankRequest request
    );

    /**
     * 고급 응답 생성
     *
     * <p>복잡한 추론과 구조화된 응답이 필요한 작업을 수행합니다.
     * 일반 채팅보다 더 정교한 분석과 추론 능력을 제공합니다.</p>
     *
     * @param request 고급 응답 생성 요청 데이터 (입력, 지시사항, 파라미터 등)
     * @return 구조화된 응답과 추론 과정 정보
     */
    @PostMapping("/api/v1/gateway/responses")
    @Operation(
            summary = "고급 응답 생성",
            description = "복잡한 추론과 구조화된 응답이 필요한 작업을 수행합니다. " +
                    "일반 채팅보다 더 정교한 분석과 추론 능력을 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고급 응답 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ResponseGenerationResponse createResponse(@RequestBody ResponsesRequest request);

    /**
     * 커스텀 모델 엔드포인트
     *
     * <p>사용자 정의 API 경로를 통해 특수 모델이나 실험적 기능에 접근합니다.
     * 표준 API로 제공되지 않는 고급 기능을 활용할 수 있습니다.</p>
     *
     * @param apiPath 커스텀 API 경로
     * @param request 커스텀 요청 데이터 (모델별 파라미터)
     * @return 모델별 응답 데이터
     */
    @PostMapping("/api/v1/gateway/custom/{api_path}")
    @Operation(
            summary = "커스텀 모델 엔드포인트",
            description = "사용자 정의 API 경로를 통해 특수 모델이나 실험적 기능에 접근합니다. " +
                    "표준 API로 제공되지 않는 고급 기능을 활용할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "커스텀 API 호출 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "API 경로를 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    CustomEndpointResponse callCustomEndpoint(
            @Parameter(description = "커스텀 API 경로", required = true, example = "stable-diffusion")
            @PathVariable("api_path") String apiPath,

            @RequestBody CustomRequest request
    );
}
