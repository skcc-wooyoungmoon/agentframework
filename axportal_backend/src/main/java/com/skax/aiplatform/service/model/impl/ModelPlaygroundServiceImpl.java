package com.skax.aiplatform.service.model.impl;

import com.skax.aiplatform.client.sktai.modelgateway.dto.request.ChatCompletionsRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.service.SktaiModelGatewayService;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeysResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponse;
import com.skax.aiplatform.dto.model.request.ModelPlaygroundChatReq;
import com.skax.aiplatform.dto.model.response.ModelPlaygroundChatRes;
import com.skax.aiplatform.mapper.model.ModelPlaygroundMapper;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.model.ModelPlaygroundService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * 모델 플레이그라운드 서비스 구현체
 *
 * <p>
 * 플레이그라운드에서 AI 모델과의 상호작용을 처리하는 서비스 구현체입니다.
 * </p>
 *
 * @author System
 * @version 1.0.0
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelPlaygroundServiceImpl implements ModelPlaygroundService {

    private static final String ADMIN_USERNAME = "admin";

    private final SktaiModelGatewayService sktaiModelGatewayService;
    private final SktaiServingService sktaiServingService;
    private final AdminAuthService adminAuthService;
    private final ModelPlaygroundMapper modelPlaygroundMapper;

    @Override
    @Transactional
    public ModelPlaygroundChatRes createChatCompletion(ModelPlaygroundChatReq request) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service ModelPlaygroundServiceImpl.createChatCompletion ]");
        log.info("Request DTO : {}", request);
        log.info("-----------------------------------------------------------------------------------------");

        try {
            // ModelPlaygroundChatReq를 ChatCompletionsRequest로 변환
            ChatCompletionsRequest chatRequest = modelPlaygroundMapper.toChatCompletionsRequest(request);

            log.info("SKTAI Model Gateway 호출 - 모델: {}, 시스템 프롬프트: {}, 사용자 프롬프트: {}", request.getModel(),
                    request.getSystemPrompt() != null
                            ? request.getSystemPrompt().substring(0, Math.min(50, request.getSystemPrompt().length()))
                                    + "..."
                            : "null",
                    request.getUserPrompt() != null
                            ? request.getUserPrompt().substring(0, Math.min(50, request.getUserPrompt().length()))
                                    + "..."
                            : "null");

            AdminContext.setAdminMode(ADMIN_USERNAME);
            adminAuthService.ensureAdminToken();

            String apiKeyFilter = "is_active:true,is_master:true,gateway_type:model,tag[]:master-key";
            ApiKeysResponse apiKeysResponse = sktaiServingService.getServingApiKeys(request.getServingId(), 1, 1, null,
                    apiKeyFilter, null);

            // API 키 조회 실패 체크
            if (apiKeysResponse.getData() == null || apiKeysResponse.getData().isEmpty()
                    || apiKeysResponse.getData().get(0).getApiKey() == null
                    || apiKeysResponse.getData().get(0).getApiKey().isEmpty()) {
                log.error("API 키 조회 실패 - servingId: {}, data가 없거나 첫 번째 API 키 값이 없습니다", request.getServingId());
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 조회 실패");
            }

            String authorization = "Bearer " + apiKeysResponse.getData().get(0).getApiKey();
            log.info("API 키 응답에서 첫 번째 API 키 사용 - servingId: {}", request.getServingId());
            // SKTAI Model Gateway API 호출
            AxResponse<ChatCompletionsResponse> response = sktaiModelGatewayService.createChatCompletion(authorization,
                    chatRequest);

            if (!response.isSuccess()) {
                log.error("SKTAI Model Gateway 호출 실패: {}", response.getMessage());
                // 보안을 위해 상세 정보는 로그에만 기록하고, 일반적인 메시지만 포함한 예외 발생
                throw new RuntimeException("채팅 완성 생성에 실패했습니다.");
            }

            // ChatCompletionsResponse를 ModelPlaygroundChatRes로 변환
            ModelPlaygroundChatRes result = modelPlaygroundMapper.toModelPlaygroundChatRes(response.getData());
            result.setError(null);

            log.info("모델 플레이그라운드 채팅 완성 생성 성공 - 응답 ID: {}", result.getId());

            return result;

        } catch (FeignException e) {
            // 타임아웃 예외 감지
            if (isTimeoutException(e)) {
                log.warn("SKTAI Model Gateway API 호출 타임아웃 발생 - 모델: {}", request.getModel());

                // 에러 메시지를 설정한 빈 응답 반환
                ModelPlaygroundChatRes errorResponse = ModelPlaygroundChatRes.builder()
                        .id("error-" + System.currentTimeMillis()).model(request.getModel())
                        .error("요청 시간이 초과되었습니다. 다시 시도해주세요.").build();

                return errorResponse;
            }

            log.error("SKTAI Model Gateway API 호출 실패: {}", e.getMessage(), e);
            log.debug("SKTAI Model Gateway API 호출 실패 상세: {}", e.contentUTF8());

            // 일반 에러는 사용자 친화적인 메시지만 반환 (보안을 위해 상세 정보는 로그에만 기록)
            ModelPlaygroundChatRes errorResponse = ModelPlaygroundChatRes.builder()
                    .id("error-" + System.currentTimeMillis())
                    .model(request.getModel())
                    .error("채팅 완성 생성에 실패했습니다. 잠시 후 다시 시도해주세요.")
                    .build();

            return errorResponse;
        } catch (Exception e) {
            // 타임아웃 예외 감지
            if (isTimeoutException(e)) {
                log.warn("모델 플레이그라운드 채팅 완성 생성 타임아웃 발생 - 모델: {}", request.getModel());

                // 에러 메시지를 설정한 빈 응답 반환
                ModelPlaygroundChatRes errorResponse = ModelPlaygroundChatRes.builder()
                        .id("error-" + System.currentTimeMillis()).model(request.getModel())
                        .error("요청 시간이 초과되었습니다. 다시 시도해주세요.").build();

                return errorResponse;
            }

            log.error("모델 플레이그라운드 채팅 완성 생성 중 오류 발생", e);

            // 일반 에러는 사용자 친화적인 메시지만 반환 (보안을 위해 상세 정보는 로그에만 기록)
            ModelPlaygroundChatRes errorResponse = ModelPlaygroundChatRes.builder()
                    .id("error-" + System.currentTimeMillis())
                    .model(request.getModel())
                    .error("채팅 완성 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                    .build();

            return errorResponse;
        } finally {
            AdminContext.clear();
        }
    }

    /**
     * 타임아웃 예외인지 확인
     *
     * @param e 예외 객체
     * @return 타임아웃 예외 여부
     */
    private boolean isTimeoutException(Exception e) {
        // FeignException의 경우 상태 코드 확인
        if (e instanceof FeignException) {
            FeignException feignException = (FeignException) e;
            // 504 Gateway Timeout
            if (feignException.status() == 504) {
                return true;
            }
        }

        // SocketTimeoutException, TimeoutException 확인
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof SocketTimeoutException || cause instanceof TimeoutException
                    || cause instanceof ConnectException) {
                return true;
            }
            cause = cause.getCause();
        }

        // 예외 메시지에 타임아웃 관련 키워드가 포함되어 있는지 확인
        String message = e.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            return lowerMessage.contains("timeout") || lowerMessage.contains("read timeout")
                    || lowerMessage.contains("connect timeout") || lowerMessage.contains("connection timed out");
        }

        return false;
    }
}
