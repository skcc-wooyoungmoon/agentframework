package com.skax.aiplatform.common.context;

import lombok.extern.slf4j.Slf4j;

/**
 * Admin 작업 컨텍스트 관리
 * 
 * <p>ThreadLocal을 사용하여 현재 스레드가 Admin 권한으로 동작 중인지 추적합니다.
 * Admin 모드일 때 SktaiRequestInterceptor는 하드코딩된 Admin 계정의 토큰을 사용합니다.</p>
 * 
 * @author Jongtae Park
 * @since 2025-10-08
 * @version 1.0.0
 */
@Slf4j
public class AdminContext {

    private static final ThreadLocal<Boolean> ADMIN_MODE = new ThreadLocal<>();
    private static final ThreadLocal<String> ADMIN_USERNAME = new ThreadLocal<>();

    /**
     * Admin 모드 활성화
     * 
     * @param adminUsername Admin 계정 사용자명
     */
    public static void setAdminMode(String adminUsername) {
        ADMIN_MODE.set(Boolean.TRUE);
        ADMIN_USERNAME.set(adminUsername);
        log.debug("Admin 모드 활성화: {}", adminUsername);
    }

    /**
     * Admin 모드 확인
     * 
     * @return Admin 모드 여부
     */
    public static boolean isAdminMode() {
        return Boolean.TRUE.equals(ADMIN_MODE.get());
    }

    /**
     * Admin 사용자명 조회
     * 
     * @return Admin 사용자명 (Admin 모드가 아니면 null)
     */
    public static String getAdminUsername() {
        return ADMIN_USERNAME.get();
    }

    /**
     * Admin 모드 해제 및 정리
     */
    public static void clear() {
        ADMIN_MODE.remove();
        ADMIN_USERNAME.remove();
        log.debug("Admin 모드 종료");
    }
}
