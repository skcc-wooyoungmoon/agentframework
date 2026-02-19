package com.skax.aiplatform.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 체크 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/common/menu-check")
@RequiredArgsConstructor
@Tag(name = "Common 관련", description = "메뉴 체크 API")
public class MenuCheckController {

    /**
     * 메뉴 체크 기본 조회
     */
    @GetMapping
    @Operation(
            summary = "메뉴 체크",
            description = "프론트 메뉴 진입 여부 확인을 위한 기본 엔드포인트입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 체크 성공")
    })
    public AxResponseEntity<String> getMenuCheck() {
        log.info("메뉴 체크 요청");
        return AxResponseEntity.ok("menucheck ok", "메뉴 체크 성공");
    }
}

