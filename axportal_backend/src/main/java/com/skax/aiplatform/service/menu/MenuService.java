package com.skax.aiplatform.service.menu;

import java.util.List;

import com.skax.aiplatform.dto.menu.response.MenuViewRes;

/**
 * 메뉴 Service 인터페이스
 * 
 * <p>
 * 메뉴 관련 비즈니스 로직을 담당하는 Service입니다.
 * 프론트엔드용 메뉴 목록 조회만 제공합니다.
 * </p>
 */
public interface MenuService {

    /**
     * 메뉴 목록 조회 (계층 구조)
     * 
     * @return 메뉴 목록
     */
    List<MenuViewRes> getActiveMenus();
}

