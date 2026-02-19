package com.skax.aiplatform.controller.menu;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.menu.response.MenuViewRes;
import com.skax.aiplatform.service.menu.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 Controller
 * 
 * <p>
 * 프론트엔드용 메뉴 조회 API를 제공하는 Controller입니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/common/menus")
@RequiredArgsConstructor
@Tag(name = "MenuController", description = "메뉴 조회 API")
public class MenuController {

    private final MenuService menuService;

    /**
     * 메뉴 목록 조회 (계층 구조)
     * 
     * @return 메뉴 목록
     */
    @GetMapping
    @Operation(summary = "메뉴 목록 조회", description = "메뉴 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<MenuViewRes>> getActiveMenus() {
        log.info("메뉴 목록 조회 요청");
        
        List<MenuViewRes> menus = menuService.getActiveMenus(); 
        
        log.info("메뉴 목록 조회 성공: {}개", menus.size());
        return AxResponseEntity.ok(menus, "메뉴 목록을 조회했습니다.");
    }
}

