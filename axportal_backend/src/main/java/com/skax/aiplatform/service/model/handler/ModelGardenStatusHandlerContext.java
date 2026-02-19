package com.skax.aiplatform.service.model.handler;

import java.util.function.Function;

import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.enums.ModelGardenStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * 모델 가든 상태별 처리 시 핸들러에 전달하는 컨텍스트.
 */
@Getter
@Builder
public class ModelGardenStatusHandlerContext {
    private final ModelGardenStatus status;
    private final String currentUser;
    private final UpdateModelGardenReq request;
    /** dplyTyp 반환 시 변환: slfhosting → self-hosting */
    private final Function<String, String> servingTypeFormatter;
}
