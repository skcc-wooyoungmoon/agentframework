package com.skax.aiplatform.client.sktai.modelgateway.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.modelgateway.SktaiModelGatewayClient;
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
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.CompletionsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.EmbeddingsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ImagesResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ModelListResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Model Gateway 서비스
 * 
 * <p>SKTAI Model Gateway API를 통해 다양한 AI 모델 기능을 제공하는 서비스입니다.
 * 텍스트 처리, 이미지 생성, 오디오 처리, 검색 최적화 등의 기능을 통합적으로 관리합니다.</p>
 * 
 * <h3>주요 서비스 영역:</h3>
 * <ul>
 *   <li><strong>모델 관리</strong>: AI 모델 목록 조회 및 메타데이터 관리</li>
 *   <li><strong>텍스트 AI</strong>: 채팅 완성, 텍스트 완성, 임베딩 생성</li>
 *   <li><strong>멀티모달 AI</strong>: 이미지 생성, 오디오 처리</li>
 *   <li><strong>검색 AI</strong>: 유사도 스코어링, 리랭킹, 응답 생성</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiModelGatewayService {

    private final SktaiModelGatewayClient sktaiModelGatewayClient;

    // ================================
    // 모델 관리 API (1개)
    // ================================

    /**
     * 사용 가능한 AI 모델 목록 조회
     * 
     * @return 모델 목록 응답
     */
    public AxResponse<ModelListResponse> getModels() {
        try {
            log.info("SKTAI Model Gateway: 모델 목록 조회 요청");
            
            ModelListResponse response = sktaiModelGatewayClient.getModels();
            
            log.info("SKTAI Model Gateway: 모델 목록 조회 성공, 모델 수: {}", 
                    response.getData() != null ? response.getData().size() : 0);
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 모델 목록 조회 실패 (BusinessException) - message: {}", 
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 모델 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 텍스트 처리 API (3개)
    // ================================

    /**
     * 채팅 완성 생성
     * 
     * @param request 채팅 완성 요청
     * @return 채팅 완성 응답
     */
    public AxResponse<ChatCompletionsResponse> createChatCompletion(String authorization, ChatCompletionsRequest request) {
        try {
            log.info("SKTAI Model Gateway: 채팅 완성 생성 요청 - 모델: {}, 메시지 수: {}", 
                    request.getModel(), 
                    request.getMessages() != null ? request.getMessages().size() : 0);
            
            // 현재 사용자 memberId 가져오기 (aip-user 헤더용)
            String aipUser = getCurrentMemberId();
            
            ChatCompletionsResponse response = sktaiModelGatewayClient.createChatCompletion(authorization, aipUser, request);
            
            log.info("SKTAI Model Gateway: 채팅 완성 생성 성공 - 응답 ID: {}", 
                    response.getId());
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 채팅 완성 생성 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 채팅 완성 생성 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "채팅 완성 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 텍스트 완성 생성
     * 
     * @param request 텍스트 완성 요청
     * @return 완성 응답
     */
    public AxResponse<CompletionsResponse> createCompletion(CompletionsRequest request) {
        try {
            log.info("SKTAI Model Gateway: 텍스트 완성 생성 요청 - 모델: {}", 
                    request.getModel());
            
            CompletionsResponse response = sktaiModelGatewayClient.createCompletion(request);
            
            log.info("SKTAI Model Gateway: 텍스트 완성 생성 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 텍스트 완성 생성 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 텍스트 완성 생성 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "텍스트 완성 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 임베딩 생성
     * 
     * @param request 임베딩 요청
     * @return 임베딩 응답
     */
    public AxResponse<EmbeddingsResponse> createEmbeddings(EmbeddingsRequest request) {
        try {
            log.info("SKTAI Model Gateway: 임베딩 생성 요청 - 모델: {}, 입력 타입: {}", 
                    request.getModel(), 
                    request.getInput() != null ? request.getInput().getClass().getSimpleName() : "null");
            
            EmbeddingsResponse response = sktaiModelGatewayClient.createEmbeddings(request);
            
            log.info("SKTAI Model Gateway: 임베딩 생성 성공 - 벡터 수: {}", 
                    response.getData() != null ? response.getData().size() : 0);
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 임베딩 생성 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 임베딩 생성 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "임베딩 생성에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 이미지 생성 API (1개)
    // ================================

    /**
     * AI 이미지 생성
     * 
     * @param request 이미지 생성 요청
     * @return 이미지 생성 응답
     */
    public AxResponse<ImagesResponse> generateImages(ImagesRequest request) {
        try {
            log.info("SKTAI Model Gateway: 이미지 생성 요청 - 프롬프트: {}, 개수: {}", 
                    request.getPrompt() != null ? request.getPrompt().substring(0, Math.min(50, request.getPrompt().length())) + "..." : "null", 
                    request.getN());
            
            ImagesResponse response = sktaiModelGatewayClient.generateImages(request);
            
            log.info("SKTAI Model Gateway: 이미지 생성 성공 - 생성된 이미지 수: {}", 
                    response.getData() != null ? response.getData().size() : 0);
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 이미지 생성 실패 (BusinessException) - message: {}", 
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 이미지 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "이미지 생성에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 오디오 처리 API (3개)
    // ================================

    /**
     * 음성 합성 (Text-to-Speech)
     * 
     * @param request 음성 합성 요청
     * @return 생성된 오디오 데이터
     */
    public AxResponse<AudioSpeechResponse> createSpeech(AudioSpeechRequest request) {
        try {
            log.info("SKTAI Model Gateway: 음성 합성 요청 - 모델: {}, 음성: {}", 
                    request.getModel(), 
                    request.getVoice());
            
            AudioSpeechResponse audioData = sktaiModelGatewayClient.createSpeech(request);
            
            log.info("SKTAI Model Gateway: 음성 합성 성공");
            
            return AxResponse.success(audioData);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 음성 합성 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 음성 합성 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "음성 합성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 음성 인식 (Speech-to-Text)
     * 
     * @param file 오디오 파일
     * @param model 사용할 모델
     * @param language 언어 코드 (선택적)
     * @param prompt 프롬프트 (선택적)
     * @param responseFormat 응답 형식 (선택적)
     * @param stream 스트리밍 여부 (선택적)
     * @return 텍스트 변환 응답
     */
    public AxResponse<Object> createTranscription(
            MultipartFile file, 
            String model, 
            String prompt,
            String language, 
            String responseFormat, 
            String stream) {
        try {
            log.info("SKTAI Model Gateway: 음성 인식 요청 - 모델: {}, 언어: {}", 
                    model, language);
            
            Object response = sktaiModelGatewayClient.transcribeAudio(
                    file, model, prompt, language, responseFormat, stream);
            
            log.info("SKTAI Model Gateway: 음성 인식 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 음성 인식 실패 (BusinessException) - 모델: {}, message: {}", 
                    model, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 음성 인식 실패 (예상치 못한 오류) - 모델: {}", model, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "음성 인식에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 음성 번역 (Audio Translation)
     * 
     * @param file 오디오 파일
     * @param model 사용할 모델
     * @param prompt 프롬프트 (선택적)
     * @param responseFormat 응답 형식 (선택적)
     * @return 번역 응답
     */
    public AxResponse<Object> createTranslation(
            MultipartFile file, 
            String model, 
            String prompt, 
            String responseFormat) {
        try {
            log.info("SKTAI Model Gateway: 음성 번역 요청 - 모델: {}", model);
            
            Object response = sktaiModelGatewayClient.translateAudio(
                    file, model, prompt, responseFormat);
            
            log.info("SKTAI Model Gateway: 음성 번역 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 음성 번역 실패 (BusinessException) - 모델: {}, message: {}", 
                    model, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 음성 번역 실패 (예상치 못한 오류) - 모델: {}", model, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "음성 번역에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 검색 최적화 API (2개)
    // ================================

    /**
     * 텍스트 유사도 스코어링
     * 
     * @param request 스코어링 요청
     * @return 스코어링 응답
     */
    public AxResponse<Object> scoreTexts(ScoreRequest request) {
        try {
            log.info("SKTAI Model Gateway: 텍스트 스코어링 요청 - 모델: {}", 
                    request.getModel());
            
            Object response = sktaiModelGatewayClient.calculateScore(request);
            
            log.info("SKTAI Model Gateway: 텍스트 스코어링 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 텍스트 스코어링 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 텍스트 스코어링 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "텍스트 스코어링에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 검색 결과 리랭킹
     * 
     * @param apiVersion API 버전
     * @param request 리랭킹 요청
     * @return 리랭킹 응답
     */
    public AxResponse<Object> rerankDocuments(String apiVersion, RerankRequest request) {
        try {
            log.info("SKTAI Model Gateway: 문서 리랭킹 요청 - API 버전: {}, 모델: {}, 문서 수: {}", 
                    apiVersion,
                    request.getModel(), 
                    request.getDocuments() != null ? request.getDocuments().size() : 0);
            
            Object response = sktaiModelGatewayClient.rerankDocuments(apiVersion, request);
            
            log.info("SKTAI Model Gateway: 문서 리랭킹 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 문서 리랭킹 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 문서 리랭킹 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "문서 리랭킹에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 고급 기능 API (2개)
    // ================================

    /**
     * 커스텀 모델 요청 처리
     * 
     * @param apiPath API 경로
     * @param request 커스텀 요청
     * @return 커스텀 응답
     */
    public AxResponse<Object> customRequest(String apiPath, CustomRequest request) {
        try {
            log.info("SKTAI Model Gateway: 커스텀 요청 - API 경로: {}", apiPath);
            
            Object response = sktaiModelGatewayClient.callCustomEndpoint(apiPath, request);
            
            log.info("SKTAI Model Gateway: 커스텀 요청 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 커스텀 요청 실패 (BusinessException) - API 경로: {}, message: {}", 
                    apiPath, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 커스텀 요청 실패 (예상치 못한 오류) - API 경로: {}", apiPath, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "커스텀 요청 처리에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 고급 응답 생성
     * 
     * @param request 응답 생성 요청
     * @return 응답 생성 결과
     */
    public AxResponse<Object> generateResponses(ResponsesRequest request) {
        try {
            log.info("SKTAI Model Gateway: 응답 생성 요청 - 모델: {}", 
                    request.getModel());
            
            Object response = sktaiModelGatewayClient.createResponse(request);
            
            log.info("SKTAI Model Gateway: 응답 생성 성공");
            
            return AxResponse.success(response);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 응답 생성 실패 (BusinessException) - 모델: {}, message: {}", 
                    request.getModel(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 응답 생성 실패 (예상치 못한 오류) - 모델: {}", 
                    request.getModel(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "응답 생성에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 유틸리티 메서드
    // ================================

    /**
     * 현재 사용자 memberId 조회
     * 
     * @return 현재 사용자 memberId (인증되지 않은 경우 빈 문자열 반환)
     */
    private String getCurrentMemberId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                String memberId = authentication.getName();
                log.debug("현재 사용자 memberId 조회 성공: {}", memberId);
                return memberId;
            }
        } catch (SecurityException e) {
            log.warn("현재 사용자 정보를 가져올 수 없습니다 (SecurityException): {}", e.getMessage());
        } catch (Exception e) {
            log.warn("현재 사용자 정보를 가져올 수 없습니다: {}", e.getMessage());
        }
        log.debug("현재 사용자 정보 없음 - aip-user 헤더를 빈 문자열로 설정");
        return "";
    }

    /**
     * 모델 사용 가능 여부 확인
     * 
     * @param modelId 확인할 모델 ID
     * @return 사용 가능 여부
     */
    public AxResponse<Boolean> isModelAvailable(String modelId) {
        try {
            log.info("SKTAI Model Gateway: 모델 사용 가능성 확인 - 모델: {}", modelId);
            
            ModelListResponse modelList = sktaiModelGatewayClient.getModels();
            boolean isAvailable = modelList.getData() != null && 
                                modelList.getData().stream()
                                    .anyMatch(model -> modelId.equals(model.getId()));
            
            log.info("SKTAI Model Gateway: 모델 사용 가능성 확인 완료 - 모델: {}, 사용 가능: {}", 
                    modelId, isAvailable);
            
            return AxResponse.success(isAvailable);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI Model Gateway: 모델 사용 가능성 확인 실패 (BusinessException) - 모델: {}, message: {}", 
                    modelId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Model Gateway: 모델 사용 가능성 확인 실패 (예상치 못한 오류) - 모델: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "모델 사용 가능성 확인에 실패했습니다: " + e.getMessage());
        }
    }
}
