package com.skax.aiplatform.service.model.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingsResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.model.request.GetModelServingReq;
import com.skax.aiplatform.service.model.ModelServingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 모델 서빙 서비스 구현체
 *
 * <p>SKTAI 서빙 API 연동을 통해 모델 서빙 목록을 조회합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServingServiceImpl implements ModelServingService {

    private final SktaiServingService sktaiServingService;

    @Override
    public PageResponse<ServingResponse> getServingModels(GetModelServingReq request) {
        log.info("모델 서빙 목록 조회 요청: {}", request);

        String filter = StringUtils.hasText(request.getFilter()) ? request.getFilter() : null;
        String search = StringUtils.hasText(request.getSearch()) ? request.getSearch() : null;
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;

        try {
            ServingsResponse servings = sktaiServingService.getServings(page + 1, size, request.getSort(), filter, search);

            List<ServingResponse> content = servings != null && servings.getData() != null
                    ? servings.getData()
                    : Collections.emptyList();

            // ADXP Pagination을 PageResponse로 변환
            return PaginationUtils.toPageResponseFromAdxp(servings != null ? servings.getPayload() : null, content);
        } catch (BusinessException e) {
            log.error("모델 서빙 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("모델 서빙 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
}

