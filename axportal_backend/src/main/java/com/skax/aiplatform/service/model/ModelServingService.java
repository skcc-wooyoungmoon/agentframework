package com.skax.aiplatform.service.model;

import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.GetModelServingReq;

/**
 * 모델 서빙 서비스
 *
 * <p>SKTAI 서빙 API를 통해 모델 서빙 목록을 조회합니다.</p>
 *
 * @author
 * @since 2025-11-10
 */
public interface ModelServingService {

    /**
     * 모델 서빙 목록 조회
     *
     * @param request 페이지 및 필터 정보
     * @return 모델 서빙 목록
     */
    PageResponse<ServingResponse> getServingModels(GetModelServingReq request);
}

