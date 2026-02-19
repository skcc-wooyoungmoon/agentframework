package com.skax.aiplatform.service.auth;

import com.skax.aiplatform.common.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class UserContextService {

    public String getCurrentUsername() {
        return UserContext.getCurrentUsername();
    }

    public String getAuthUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자 이름 초기화
        String username = null;

        // 인증 객체가 존재하고 인증되었으면 사용자 정보 추출
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {

            // Principal 객체에서 사용자 정보 추출
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                // UserDetails인 경우 사용자명 가져오기
                username = userDetails.getUsername();
            } else if (principal instanceof String) {
                // 문자열 형태인 경우 그대로 사용
                username = (String) principal;
            }
        }
        return username;
    }

}
