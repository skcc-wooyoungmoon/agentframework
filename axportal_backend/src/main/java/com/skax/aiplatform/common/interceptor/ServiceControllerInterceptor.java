package com.skax.aiplatform.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.response.AxResponse;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.entity.auth.GpoPortalResourceMas;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.repository.admin.GpoAuthorityMasRepository;
import com.skax.aiplatform.repository.admin.GpoPortalResourceMasRepository;
import com.skax.aiplatform.repository.admin.RoleRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 서비스 컨트롤러 요청 인터셉터
 *
 * <p>백엔드 서비스 컨트롤러를 호출하는 모든 요청을 가로채서 전처리합니다.
 * 조건에 맞는 요청은 바이패스시키고, 조건에 맞지 않는 요청은 권한 오류를 반환합니다.</p>
 *
 * @author Gadget
 * @version 1.0.0
 * @since 2025-11-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceControllerInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final TokenInfo tokenInfo;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final GpoProjectsRepository gpoProjectsRepository;
    private final RoleRepository roleRepository;
    private final GpoAuthorityMasRepository gpoAuthorityMasRepository;
    private final GpoPortalResourceMasRepository gpoPortalResourceMasRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludedPaths = List.of(
            "/modelGarden/file-import-complete",
            "/modelGarden/vaccine-complete",
            "/modelGarden/vulnerability-complete",
            "/modelGarden/import-complete"
    );

    /**
     * 컨트롤러 실행 전 요청을 가로채서 전처리합니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param handler  실행될 핸들러
     * @return true: 요청을 계속 진행, false: 요청을 중단하고 응답 반환
     * @throws Exception 처리 중 발생한 예외
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String servletPath = request.getServletPath();
        String clientIp = getClientIpAddress(request);
        String memberId = tokenInfo.getUserName();
        String frontendPath = request.getHeader("x-frontend-path"); // 브라우저 현재 url path

        log.debug("[{}] 인터셉터 요청 확인: {} -> [{}] {} (IP: {})", memberId, frontendPath, method, servletPath, clientIp);

        // 관리자계정 모두 허용
        if (memberId != null && memberId.equals("admin")) {
            return true;
        }

        // 예외처리
        for (String path : excludedPaths) {
            if (path.equals(servletPath)) {
                return true;
            }
        }

        if (memberId == null) {
            return false;
        }

        // 사용자 프로젝트 정보 추출
        ProjectUserRole projectUserRole = gpoPrjuserroleRepository.findByMemberIdAndStatusNm(memberId, ProjectUserRoleStatus.ACTIVE);
        long prjSeq = projectUserRole.getProject().getPrjSeq();
        long roleSeq = projectUserRole.getRole().getRoleSeq();

        // 권한 URL 목록 조회
        List<String> authKeys = gpoAuthorityMasRepository.findAuthKeysByRoleSeq(roleSeq);
        authKeys.add("A000001"); // 기본권한 추가

        List<GpoPortalResourceMas> portalResourceList = gpoPortalResourceMasRepository.findAll().stream()
                .filter(resource -> authKeys.contains(resource.getAuthorityId()))
                .toList();

        for (GpoPortalResourceMas portalResource : portalResourceList) {
            if (method.equals(portalResource.getScope()) && pathMatcher.match(portalResource.getResourceUrl(), servletPath)) {
                log.info("권한 허용: {} -> {}", servletPath, portalResource.getResourceUrl());
                return true;
            }
        }

        // 조건에 맞지 않는 경우: 권한 오류 응답
        log.warn("권한 오류: 조건에 맞지 않는 요청: [{}] {}", method, servletPath);

        // 응답 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String prjNm = gpoProjectsRepository.getByPrjSeq(prjSeq).getPrjNm();
        String roleNm = roleRepository.findByRoleSeq(roleSeq).get().getRoleNm();

        // 에러 응답 생성
        AxResponse<Void> errorResponse = AxResponse.failure(
                """
                        접근 권한이 없습니다.
                        포탈 관리자에게 문의해주세요.""",
                "ACCESS_DENIED",
                """
                        %s (%s)
                         : [%s] %s""".formatted(prjNm, roleNm, method, servletPath)
        );

        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        return false;  // 요청 중단
    }

    /**
     * 클라이언트 IP 주소를 추출합니다.
     *
     * @param request HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
