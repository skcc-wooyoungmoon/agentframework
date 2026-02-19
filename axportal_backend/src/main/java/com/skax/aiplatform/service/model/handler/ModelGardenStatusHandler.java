package com.skax.aiplatform.service.model.handler;

import com.skax.aiplatform.entity.model.GpoUseGnynModelMas;

/**
 * 모델 가든 상태별 업데이트 처리 핸들러.
 * 컨텍스트의 status에 따라 내부에서 분기 처리한다.
 */
public interface ModelGardenStatusHandler {

    void handle(GpoUseGnynModelMas existing, ModelGardenStatusHandlerContext ctx);
}
