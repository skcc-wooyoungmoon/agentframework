package com.skax.aiplatform.service.model;

import com.skax.aiplatform.dto.model.request.ModelPlaygroundChatReq;
import com.skax.aiplatform.dto.model.response.ModelPlaygroundChatRes;

/**
 * 모델 플레이그라운드 서비스 인터페이스
 * 
 * <p>
 * 플레이그라운드에서 AI 모델과의 상호작용을 위한 서비스 인터페이스입니다.
 * </p>
 * 
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
public interface ModelPlaygroundService {

    /**
     * 모델과 채팅 완성 생성
     * 
     * @param request 채팅 요청 데이터
     * @return 채팅 완성 응답
     */
    ModelPlaygroundChatRes createChatCompletion(ModelPlaygroundChatReq request);
}

