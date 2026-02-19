package com.skax.aiplatform.service.menu.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.dto.menu.response.MenuViewRes;
import com.skax.aiplatform.entity.GpoMenuVisibleMas;
import com.skax.aiplatform.repository.menu.MenuRepository;
import com.skax.aiplatform.service.menu.MenuService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 Service 구현체
 * 
 * <p>
 * 메뉴 관련 비즈니스 로직을 구현합니다.
 * 프론트엔드용 메뉴 목록 조회만 제공합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    /**
     * 프론트엔드용 활성화된 메뉴 목록 조회 (계층 구조)
     * visible과 active가 true인 메뉴만 조회하여 프론트엔드 구조로 변환
     * 
     * @return 프론트엔드 구조로 변환된 활성화된 메뉴 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<MenuViewRes> getActiveMenus() {
        log.info("메뉴 목록 조회 시작");
        
        List<GpoMenuVisibleMas> menus = menuRepository.findAllActiveMenus();
        log.info("메뉴 목록 조회 결과: {}", menus);
        if (menus.isEmpty()) {
            log.warn("메뉴가 존재하지 않습니다.");
            return Collections.emptyList();
        }

        // Menu 엔티티를 트리 구조로 먼저 구성
        List<GpoMenuVisibleMas> menuTree = buildMenuEntityTree(menus);
        
        // Menu 엔티티 트리를 MenuFrontendRes로 변환
        List<MenuViewRes> frontendTree = menuTree.stream()
                .map(this::convertMenuTreeToFrontend)
                .collect(Collectors.toList());
        
        log.info("메뉴 목록 조회 완료: 총 {}개", menus.size());
        return frontendTree;
    }

    /**
     * Menu 엔티티 리스트를 계층 구조(트리)로 변환
     * 
     * @param menus 평면 Menu 엔티티 리스트
     * @return 계층 구조로 구성된 Menu 엔티티 리스트
     */
    private List<GpoMenuVisibleMas> buildMenuEntityTree(List<GpoMenuVisibleMas> menus) {
        // ID를 키로 하는 맵 생성
        Map<Long, GpoMenuVisibleMas> menuMap = menus.stream()
                .collect(Collectors.toMap(GpoMenuVisibleMas::getId, menu -> menu));

        // 최상위 메뉴 리스트
        List<GpoMenuVisibleMas> rootMenus = new ArrayList<>();

        // 각 메뉴를 부모에 연결
        for (GpoMenuVisibleMas menu : menus) {
            if (menu.getParentId() == null) {
                // 최상위 메뉴
                rootMenus.add(menu);
            } else {
                // 하위 메뉴 - 부모에 추가
                GpoMenuVisibleMas parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }

        // 정렬 (orderNo 기준)
        sortMenuEntities(rootMenus);
        
        return rootMenus;
    }

    /**
     * Menu 엔티티 리스트를 orderNo 기준으로 정렬 (재귀)
     * 
     * @param menus 메뉴 리스트
     */
    private void sortMenuEntities(List<GpoMenuVisibleMas> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }

        menus.sort(Comparator.comparing(GpoMenuVisibleMas::getOrderNo, Comparator.nullsLast(Comparator.naturalOrder())));

        // 각 메뉴의 children도 정렬
        for (GpoMenuVisibleMas menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                sortMenuEntities(menu.getChildren());
            }
        }
    }

    /**
     * Menu 엔티티 트리를 MenuFrontendRes로 변환 (재귀)
     * 
     * @param menu Menu 엔티티
     * @return MenuFrontendRes DTO
     */
    private MenuViewRes convertMenuTreeToFrontend(GpoMenuVisibleMas menu) {
        MenuViewRes.MenuViewResBuilder builder = MenuViewRes.builder()
                .id(menu.getCode())  // 프론트엔드 id는 code 값 사용
                .label(menu.getName())  // 프론트엔드 label은 name 값 사용
                .icon(menu.getIcon())
                .path(menu.getIsExternal() == 0 && menu.getPath() != null ? menu.getPath() : "")  // 빈 문자열 처리
                .href(menu.getIsExternal() == 1 && menu.getPath() != null ? menu.getPath() : "")  // 빈 문자열 처리
                .auth(menu.getAuth());  // 권한 코드

        // children 변환 (재귀)
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            List<MenuViewRes> children = menu.getChildren().stream()
                    .map(this::convertMenuTreeToFrontend)
                    .collect(Collectors.toList());
            builder.children(children);
        } else {
            builder.children(new ArrayList<>());
        }

        return builder.build();
    }
}

