package com.skax.aiplatform.controller.model;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.GetModelServingReq;
import com.skax.aiplatform.service.model.ModelServingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 모델 서빙 컨트롤러
 *
 * <p>서빙 모델 목록 조회 API를 제공합니다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/modelServing")
@RequiredArgsConstructor
@Validated
@Tag(name = "Model Serving Management", description = "모델 서빙 관리 API")
public class ModelServingController {

    private final ModelServingService modelServingService;

    /**
     * 모델 서빙 목록 조회
     *
     * @param request 페이지 및 필터 정보
     * @return 모델 서빙 목록
     */
    @GetMapping
    @Operation(summary = "모델 서빙 목록 조회", description = "등록된 모델 서빙 목록을 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<PageResponse<ServingResponse>> getServingModels(@Valid GetModelServingReq request) {
        log.info("모델 서빙 목록 조회 API 호출 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                request.getPage(), request.getSize(), request.getSort(), request.getFilter(), request.getSearch());

        PageResponse<ServingResponse> response = modelServingService.getServingModels(request);

        log.info("모델 서빙 목록 조회 API 완료 - 응답 데이터 수: {}",
                response.getContent() != null ? response.getContent().size() : 0);

        return AxResponseEntity.ok(response, "모델 서빙 목록 조회가 완료되었습니다.");
    }
}

