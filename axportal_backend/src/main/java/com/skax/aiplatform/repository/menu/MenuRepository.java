package com.skax.aiplatform.repository.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skax.aiplatform.entity.GpoMenuVisibleMas;

/**
 * 메뉴 Repository
 * 
 * <p>
 * 메뉴 데이터 액세스를 담당하는 Repository입니다.
 * </p>
 */
public interface MenuRepository extends JpaRepository<GpoMenuVisibleMas, Long> {
    /**
     * 활성화된 모든 메뉴 조회 (계층 구조)
     * visible과 active가 true인 메뉴만 조회
     * 
     * @return 활성화된 메뉴 목록
     */
    @Query("SELECT m FROM GpoMenuVisibleMas m WHERE m.visible = 1 AND m.active = 1 ORDER BY m.depth ASC, m.orderNo ASC")
    List<GpoMenuVisibleMas> findAllActiveMenus();
}

