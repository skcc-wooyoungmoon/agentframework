package com.skax.aiplatform.controller.model;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.model.request.ModelPlaygroundChatReq;
import com.skax.aiplatform.dto.model.response.ModelPlaygroundChatRes;
import com.skax.aiplatform.service.model.ModelPlaygroundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 모델 플레이그라운드 컨트롤러
 * 
 * <p>
 * 플레이그라운드에서 AI 모델과의 상호작용을 위한 API를 제공합니다.
 * </p>
 * 
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/model-playground")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS })
@Tag(name = "Model Playground", description = "모델 플레이그라운드 관리 API")
public class ModelPlaygroundController {

    private final ModelPlaygroundService modelPlaygroundService;

    @PostMapping("/chat")
    @Operation(summary = "모델과 채팅 완성 생성", description = "선택된 모델과 시스템/사용자 프롬프트를 사용하여 채팅 완성을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 완성 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<ModelPlaygroundChatRes> createChatCompletion(
            @RequestBody ModelPlaygroundChatReq request) {

        log.info("모델 플레이그라운드 채팅 요청 - 모델: {}, 시스템 프롬프트: {}, 사용자 프롬프트: {}",
                request.getModel(),
                request.getSystemPrompt() != null ? "있음" : "없음",
                request.getUserPrompt() != null ? "있음" : "없음");

        ModelPlaygroundChatRes response = modelPlaygroundService.createChatCompletion(request);

        log.info("모델 플레이그라운드 채팅 완료 - 응답 ID: {}", response.getId());

        return AxResponseEntity.ok(response, "채팅 완성이 성공적으로 생성되었습니다.");
    }
}
