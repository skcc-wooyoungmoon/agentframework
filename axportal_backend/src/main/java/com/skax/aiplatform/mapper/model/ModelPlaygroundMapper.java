package com.skax.aiplatform.mapper.model;

import com.skax.aiplatform.client.sktai.modelgateway.dto.request.ChatCompletionsRequest;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse.Choice;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse.Message;
import com.skax.aiplatform.client.sktai.modelgateway.dto.response.ChatCompletionsResponse.Usage;
import com.skax.aiplatform.dto.model.request.ModelPlaygroundChatReq;
import com.skax.aiplatform.dto.model.response.ModelPlaygroundChatRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모델 플레이그라운드 매퍼
 * 
 * <p>
 * 플레이그라운드 관련 DTO와 SKTAI 클라이언트 DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.
 * </p>
 * 
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelPlaygroundMapper {

    /**
     * ModelPlaygroundChatReq를 ChatCompletionsRequest로 변환
     * 
     * @param request 플레이그라운드 채팅 요청
     * @return SKTAI 클라이언트 채팅 요청
     */
    @Mapping(target = "model", source = "model")
    @Mapping(target = "messages", expression = "java(createMessages(request))")
    ChatCompletionsRequest toChatCompletionsRequest(ModelPlaygroundChatReq request);

    /**
     * ChatCompletionsResponse를 ModelPlaygroundChatRes로 변환
     * 
     * @param response SKTAI 클라이언트 채팅 응답
     * @return 플레이그라운드 채팅 응답
     */
    @Mapping(target = "choices", source = "choices")
    @Mapping(target = "usage", source = "usage")
    @Mapping(target = "createdAt", expression = "java(convertCreatedTime(response.getCreated()))")
    @Mapping(target = "stream", constant = "false")
    ModelPlaygroundChatRes toModelPlaygroundChatRes(ChatCompletionsResponse response);

    /**
     * Choice를 ModelPlaygroundChatRes.ChatMessage로 변환
     */
    @Mapping(target = "message", source = "message")
    ModelPlaygroundChatRes.ChatMessage toChatMessage(Choice choice);

    /**
     * Message를 ModelPlaygroundChatRes.ChatMessage.MessageContent로 변환
     */
    @Mapping(target = "role", source = "role")
    @Mapping(target = "content", source = "content")
    ModelPlaygroundChatRes.ChatMessage.MessageContent toMessageContent(Message message);

    /**
     * Usage를 ModelPlaygroundChatRes.TokenUsage로 변환
     */
    @Mapping(target = "promptTokens", source = "promptTokens")
    @Mapping(target = "completionTokens", source = "completionTokens")
    @Mapping(target = "totalTokens", source = "totalTokens")
    ModelPlaygroundChatRes.TokenUsage toTokenUsage(Usage usage);

    /**
     * 메시지 리스트 생성 헬퍼 메서드
     */
    default List<Object> createMessages(ModelPlaygroundChatReq request) {
        if (request == null) {
            return null;
        }

        List<Object> messages = new java.util.ArrayList<>();

        // 시스템 프롬프트가 있으면 추가
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().trim().isEmpty()) {
            messages.add(Message.builder()
                    .role("system")
                    .content(request.getSystemPrompt())
                    .build());
        }

        // 사용자 프롬프트 추가
        if (request.getUserPrompt() != null && !request.getUserPrompt().trim().isEmpty()) {
            messages.add(Message.builder()
                    .role("user")
                    .content(request.getUserPrompt())
                    .build());
        }

        return messages;
    }

    /**
     * 생성 시간 변환 헬퍼 메서드
     */
    default LocalDateTime convertCreatedTime(Long created) {
        if (created == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.ofEpochSecond(created, 0, java.time.ZoneOffset.UTC);
    }
}
