package com.skax.aiplatform.common.interceptor;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skax.aiplatform.dto.admin.request.UserUsageMgmtReq;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtRes;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.service.admin.UserUsageMgmtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 활동 로그 저장 담당 클래스
 *
 * <p>
 * Feign Client 및 Controller의 사용자 활동을 DB에 저장합니다.
 * </p>
 *
 * @author sonmunwoo
 * @version 1.0.0
 * @since 2025-10-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivityLogger {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    private final UserUsageMgmtService userUsageMgmtService;
    private final HttpInfoExtractor httpInfoExtractor;
    private final MenuPathResolver menuPathResolver;
    private final ProjectUserRoleRepository projectUserRoleRepository;

    /**
     * Feign Client 사용자 활동 로그 저장 (중복 방지)
     *
     * @param className      Feign Client 클래스명
     * @param methodName     호출된 메서드명
     * @param args           메서드 파라미터들
     * @param result         호출 결과 (SUCCESS/ERROR)
     * @param errorMessage   에러 메시지 (에러 발생 시)
     * @param httpStatusCode HTTP 상태 코드
     * @param responseObject 응답 객체 (POST 시 ID 추출용)
     */
    public void saveFeignClientLog(String className, String methodName, Object[] args,
            String result, String errorMessage, String httpStatusCode, Object responseObject) {
        try {
            // UserUsageMgmt 관련 FeignClient는 로깅하지 않음 (Controller만 로깅)
            if (isUserUsageMgmtFeignClient(className)) {
                log.debug("UserUsageMgmt 관련 FeignClient이므로 사용자 활동 로그 건너뜀: {}.{}", className, methodName);
                return;
            }

            // Prometheus 관련 FeignClient는 로깅하지 않음 (ResrcMgmtController만 로깅)
            if (isPrometheusFeignClient(className)) {
                log.debug("Prometheus 관련 FeignClient이므로 사용자 활동 로그 건너뜀: {}.{}", className, methodName);
                return;
            }

            // Feign Client는 항상 로깅 (Controller와 동시 호출 시 Feign Client 우선)
            log.debug("Feign Client 로그 저장: {}.{}", className, methodName);

            // 인터셉터, 프록시 클래스는 로깅하지 않음 (실제 서비스 클래스만 로깅)
            if (shouldSkipFeignClientLogging(className)) {
                log.debug("인터셉터/프록시 클래스이므로 사용자 활동 로그 건너뜀: {}.{}", className, methodName);
                return;
            }

            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("인증되지 않은 사용자의 API 호출로 로그 저장을 건너뜁니다.");
                return;
            }

            String userName = getCurrentUserName(authentication);
            String apiEndpoint = httpInfoExtractor.getApiEndpoint(className, methodName);
            String action = menuPathResolver.getActionFromMethod(methodName, "");
            String resourceType = menuPathResolver.getResourceTypeFromClient(className);

            // targetAsset 생성 (POST/PUT/DELETE일 경우 Client/ID 형식)
            String targetAsset = buildTargetAsset(className, methodName, action, args, responseObject);

            // 로그인 관련 호출인 경우 파라미터에서 사용자 정보 추출
            String actualUserName = userName;
            String actualRoleName = getUserRole(authentication);

            if (isLoginCall(className, methodName, apiEndpoint)) {
                String[] extractedInfo = extractUserInfoFromLoginParams(args);
                if (extractedInfo[0] != null) {
                    actualUserName = extractedInfo[0];
                }
                if (extractedInfo[1] != null) {
                    actualRoleName = extractedInfo[1];
                }
            }

            // 프론트엔드 URL 경로 추출 (한글 변환하지 않고 그대로 사용)
            String frontendPath = httpInfoExtractor.getFrontendPath();

            // 프로젝트명과 역할명을 DB에서 조회
            String[] projectAndRole = getProjectAndRoleName(actualUserName);
            String projectName = projectAndRole[0];
            String roleName = projectAndRole[1] != null ? projectAndRole[1] : actualRoleName;

            // POST/PUT/DELETE 요청/응답 데이터 추출
            String requestContent = extractRequestContent(action, args);
            String responseContent = extractResponseContent(action, responseObject);

            // 요청/응답 내용을 1000자씩 분할하여 detail 필드에 저장
            String[] requestDetails = splitContentIntoChunks(requestContent, 1000, 4);
            String[] responseDetails = splitContentIntoChunks(responseContent, 1000, 4);

            // 사용자 사용량 관리 요청 객체 생성
            // requestContent와 responseContent는 저장하지 않음 (detail 필드에만 저장)
            UserUsageMgmtReq logRequest = UserUsageMgmtReq.builder()
                    .userName(actualUserName)
                    .projectName(projectName)
                    .roleName(roleName)
                    .menuPath(frontendPath) // 프론트엔드 URL을 그대로 저장 (한글 변환 안함)
                    .action(action)
                    .targetAsset(targetAsset)
                    .resourceType(resourceType)
                    .apiEndpoint(apiEndpoint)
                    .errCode(normalizeHttpStatusCode(httpStatusCode))
                    .clientIp(httpInfoExtractor.getClientIpAddress())
                    .userAgent(httpInfoExtractor.getUserAgent())
                    .requestContent(null) // 저장하지 않음
                    .firstRequestDetail(requestDetails[0])
                    .secondRequestDetail(requestDetails[1])
                    .thirdRequestDetail(requestDetails[2])
                    .fourthRequestDetail(requestDetails[3])
                    .responseContent(null) // 저장하지 않음
                    .firstResponseDetail(responseDetails[0])
                    .secondResponseDetail(responseDetails[1])
                    .thirdResponseDetail(responseDetails[2])
                    .fourthResponseDetail(responseDetails[3])
                    .build();

            // 비동기로 사용자 사용량 관리 저장 (트랜잭션 분리)
            UserUsageMgmtRes savedLog = userUsageMgmtService.createUserUsageMgmt(logRequest);

            // /api/auth/users/me 호출 시 로그인 로그에 사용자 정보 업데이트 (저장 성공한 경우만)
            if (isUserInfoCall(className, methodName) && savedLog != null && savedLog.getId() != null) {
                updateLoginLogWithUserInfo(Long.valueOf(savedLog.getId()), userName, result);
            }

            log.debug("사용자 사용량 관리 저장 완료: user={}, client={}, method={}, result={}",
                    userName, className, methodName, result);

        } catch (RuntimeException innerException) {
            log.warn("사용자 사용량 관리 저장 실패 (시스템 자동 로깅): client={}, method={}, error={}",
                    className, methodName, innerException.getMessage());
        } catch (Throwable t) {
            log.error("사용자 사용량 관리 저장 중 심각한 예외 발생 (시스템 자동 로깅): client={}, method={}, error={}",
                    className, methodName, t.getMessage(), t);
        }
    }

    /**
     * Controller 사용자 활동 로그 저장
     *
     * @param className      Controller 클래스명
     * @param methodName     호출된 메서드명
     * @param result         호출 결과 (SUCCESS/ERROR)
     * @param errorMessage   에러 메시지 (에러 발생 시)
     * @param httpStatusCode HTTP 상태 코드
     * @param args           메서드 파라미터들 (PUT/DELETE 시 ID 추출용)
     * @param responseObject 응답 객체 (POST 시 ID 추출용)
     */
    public void saveControllerLog(String className, String methodName,
            String result, String errorMessage, String httpStatusCode,
            Object[] args, Object responseObject) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("인증되지 않은 사용자의 Controller 호출로 로그 저장을 건너뜁니다.");
                return;
            }

            String userName = getCurrentUserName(authentication);
            String apiEndpoint = httpInfoExtractor.getControllerEndpoint(className, methodName);
            String action = menuPathResolver.getActionFromControllerMethod(methodName, "");
            String resourceType = menuPathResolver.getResourceTypeFromController(className);

            // targetAsset 생성 (POST/PUT/DELETE일 경우 Controller/ID 형식)
            String targetAsset = buildTargetAsset(className, methodName, action, args, responseObject);

            // Controller.MenuCheckController.getMenuCheck일 경우 targetAsset(HMK_NM)에 'Y' 설정
            if ("Controller.MenuCheckController.getMenuCheck".equals(apiEndpoint)) {
                targetAsset = "Y";
            }

            // 프론트엔드 URL 경로 추출 (한글 변환하지 않고 그대로 사용)
            String frontendPath = httpInfoExtractor.getFrontendPath();

            // 프로젝트명과 역할명을 DB에서 조회
            String[] projectAndRole = getProjectAndRoleName(userName);
            String projectName = projectAndRole[0];
            String roleName = projectAndRole[1] != null ? projectAndRole[1] : getUserRole(authentication);

            // POST/PUT/DELETE 요청/응답 데이터 추출
            String requestContent = extractRequestContent(action, args);
            String responseContent = extractResponseContent(action, responseObject);

            // 요청/응답 내용을 1000자씩 분할하여 detail 필드에 저장
            String[] requestDetails = splitContentIntoChunks(requestContent, 1000, 4);
            String[] responseDetails = splitContentIntoChunks(responseContent, 1000, 4);

            // 사용자 사용량 관리 요청 객체 생성
            // requestContent와 responseContent는 저장하지 않음 (detail 필드에만 저장)
            UserUsageMgmtReq logRequest = UserUsageMgmtReq.builder()
                    .userName(userName)
                    .projectName(projectName)
                    .roleName(roleName)
                    .menuPath(frontendPath) // 프론트엔드 URL을 그대로 저장 (한글 변환 안함)
                    .action(action)
                    .targetAsset(targetAsset)
                    .resourceType(resourceType)
                    .apiEndpoint(apiEndpoint)
                    .errCode(normalizeHttpStatusCode(httpStatusCode))
                    .clientIp(httpInfoExtractor.getClientIpAddress())
                    .userAgent(httpInfoExtractor.getUserAgent())
                    .requestContent(null) // 저장하지 않음
                    .firstRequestDetail(requestDetails[0])
                    .secondRequestDetail(requestDetails[1])
                    .thirdRequestDetail(requestDetails[2])
                    .fourthRequestDetail(requestDetails[3])
                    .responseContent(null) // 저장하지 않음
                    .firstResponseDetail(responseDetails[0])
                    .secondResponseDetail(responseDetails[1])
                    .thirdResponseDetail(responseDetails[2])
                    .fourthResponseDetail(responseDetails[3])
                    .build();

            // 비동기로 사용자 사용량 관리 저장 (트랜잭션 분리)
            userUsageMgmtService.createUserUsageMgmtAsync(logRequest);

            log.debug("Controller 사용자 사용량 관리 저장 완료: user={}, controller={}, method={}, result={}",
                    userName, className, methodName, result);

        } catch (RuntimeException e) {
            log.warn("Controller 사용자 사용량 관리 저장 실패 (시스템 자동 로깅): controller={}, method={}, error={}",
                    className, methodName, e.getMessage());
        } catch (Throwable t) {
            log.error("Controller 사용자 사용량 관리 저장 중 심각한 예외 발생 (시스템 자동 로깅): controller={}, method={}, error={}",
                    className, methodName, t.getMessage(), t);
        }
    }

    /**
     * Feign Client 로깅을 건너뛸지 판단
     */
    private boolean shouldSkipFeignClientLogging(String className) {
        if (className.startsWith("$Proxy") ||
                className.contains("Interceptor") ||
                className.contains("RequestInterceptor") ||
                className.contains("ErrorDecoder") ||
                className.contains("FeignConfig") ||
                className.contains("Configuration") ||
                className.contains("SktaiToolsService") ||
                className.contains("SktaiServingService") ||
                className.contains("$$") ||
                className.contains("CGLIB") ||
                className.contains("Enhancer")) {
            return true;
        }
        return false;
    }

    /**
     * 현재 사용자명 추출
     */
    private String getCurrentUserName(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return "UNKNOWN_USER";
    }

    /**
     * 사용자 역할 추출 (한글)
     */
    private String getUserRole(Authentication authentication) {
        String roleId = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        return convertRoleToKorean(roleId);
    }

    /**
     * 영어 Role ID를 한글 역할명으로 변환
     */
    private String convertRoleToKorean(String roleId) {
        return switch (roleId) {
            case "ROLE_ADMIN" -> "관리자";
            case "ROLE_MANAGER" -> "매니저";
            case "ROLE_USER" -> "사용자";
            case "ROLE_DEVELOPER" -> "개발자";
            case "ROLE_OPERATOR" -> "운영자";
            default -> roleId;
        };
    }

    /**
     * 현재 사용자의 활성 프로젝트명과 역할명 조회
     *
     * <p>
     * 사용자의 활성 프로젝트와 역할을 DB에서 조회합니다.
     * 조회 실패 시 기본값을 반환합니다.
     * </p>
     *
     * @param userName 사용자 ID
     * @return [0]: 프로젝트명, [1]: 역할명
     */
    private String[] getProjectAndRoleName(String userName) {
        try {
            // 사용자의 활성 프로젝트명과 역할명 조회 (여러 개일 경우 첫 번째만 사용)
            List<Object[]> results = projectUserRoleRepository.findActiveProjectAndRoleByMemberId(userName);
            if (results != null && !results.isEmpty()) {
                Object[] result = results.get(0); // 첫 번째 결과만 사용
                return new String[] {
                        (String) result[0], // 프로젝트명
                        (String) result[1] // 역할명
                };
            }
            return new String[] { "Ax_Portal", null }; // 활성 프로젝트 없으면 기본값
        } catch (DataAccessException e) {
            log.debug("프로젝트/역할 조회 실패 (데이터베이스 오류), 기본값 사용: user={}, error={}", userName, e.getMessage());
            return new String[] { "Ax_Portal", null }; // 조회 실패 시 기본값
        } catch (RuntimeException e) {
            log.debug("프로젝트/역할 조회 실패 (런타임 오류), 기본값 사용: user={}, error={}", userName, e.getMessage());
            return new String[] { "Ax_Portal", null }; // 조회 실패 시 기본값
        }
    }

    /**
     * 타겟 자산 정보 생성
     *
     * <p>
     * POST/PUT/DELETE 요청에 대해 '상세조회 API명/ID' 형식으로 생성합니다.
     * </p>
     *
     * <ul>
     * <li>POST: 응답에서 ID 추출 → 상세조회API명/ID (예: "/api/v1/data/dataset/123")</li>
     * <li>PUT: 파라미터에서 ID 추출 → 상세조회API명/ID (예: "/api/v1/data/dataset/456")</li>
     * <li>DELETE: 파라미터에서 ID 추출 → 상세조회API명/ID (예: "/api/v1/data/dataset/789")</li>
     * <li>GET: 빈 값</li>
     * </ul>
     *
     * @param className      클래스명 (Controller 또는 FeignClient)
     * @param methodName     메서드명
     * @param action         HTTP 액션 (POST, PUT, DELETE, GET 등)
     * @param args           메서드 파라미터 (PUT/DELETE 시 ID 추출)
     * @param responseObject 응답 객체 (POST 시 ID 추출)
     * @return 상세조회 API 경로/ID (예: "/api/v1/data/dataset/123") 또는 빈 값
     */
    private String buildTargetAsset(String className, String methodName, String action,
            Object[] args, Object responseObject) {
        log.info("=== buildTargetAsset 호출 시작 ===");
        log.info("className: {}", className);
        log.info("methodName: {}", methodName);
        log.info("action: {}", action);
        log.info("args 개수: {}", args != null ? args.length : 0);
        log.info("responseObject 타입: {}", responseObject != null ? responseObject.getClass().getName() : "null");

        // 로그인/로그아웃은 빈 값
        if ((className.contains("Auth") && (methodName.equalsIgnoreCase("login") || methodName.equalsIgnoreCase(
                "logout"))) ||
                (className.equals("SktaiAuthService") && (methodName.equals("login") || methodName.equals("logout"))) ||
                methodName.equalsIgnoreCase("login") || methodName.equalsIgnoreCase("logout")) {
            log.info("로그인/로그아웃 메서드 -> 빈 값 반환");
            return "";
        }

        // GET이나 기타 액션은 빈 값
        if (action == null || (!action.equals("POST") && !action.equals("PUT") && !action.equals("DELETE"))) {
            log.info("GET 또는 기타 액션 -> 빈 값 반환");
            return "";
        }

        String resourceId = null;

        try {
            // POST: 응답 객체에서 ID 추출
            if ("POST".equals(action)) {
                log.info("POST 액션: 응답 객체에서 ID 추출 시도");
                if (responseObject != null && !(responseObject instanceof Throwable)) {
                    resourceId = extractIdFromResponse(responseObject);
                    log.info("POST ID 추출 결과: {}", resourceId);
                    if (resourceId != null && !resourceId.isEmpty()) {
                        // POST: 응답 ID + 상세조회 API 경로 생성
                        String detailApiPath = buildDetailApiPath(className, methodName, resourceId);
                        log.info("POST targetAsset 생성 성공: {}", detailApiPath);
                        return detailApiPath;
                    } else {
                        log.warn("POST ID 추출 실패: responseObject가 있지만 ID를 찾을 수 없음");
                    }
                } else {
                    log.warn("POST responseObject가 null");
                }
            }

            // PUT: 파라미터에서 ID 추출
            if ("PUT".equals(action)) {
                log.info("PUT 액션: 파라미터에서 ID 추출 시도");
                if (args != null && args.length > 0) {
                    log.info("PUT args[0] 타입: {}, 값: {}", args[0] != null ? args[0].getClass().getName() : "null",
                            args[0]);
                    resourceId = extractIdFromArgs(args);
                    log.info("PUT ID 추출 결과: {}", resourceId);
                    if (resourceId != null && !resourceId.isEmpty()) {
                        // PUT: 파라미터 ID + 상세조회 API 경로 생성
                        String detailApiPath = buildDetailApiPath(className, methodName, resourceId);
                        log.info("PUT targetAsset 생성 성공: {}", detailApiPath);
                        return detailApiPath;
                    } else {
                        log.warn("PUT ID 추출 실패: args가 있지만 ID를 찾을 수 없음");
                    }
                } else {
                    log.warn("PUT args가 null이거나 비어있음");
                }
            }

            // DELETE: 파라미터에서 ID 추출
            if ("DELETE".equals(action)) {
                log.info("DELETE 액션: 파라미터에서 ID 추출 시도");
                if (args != null && args.length > 0) {
                    log.info("DELETE args[0] 타입: {}, 값: {}", args[0] != null ? args[0].getClass().getName() : "null",
                            args[0]);
                    resourceId = extractIdFromArgs(args);
                    log.info("DELETE ID 추출 결과: {}", resourceId);
                    if (resourceId != null && !resourceId.isEmpty()) {
                        // DELETE: 파라미터 ID + 상세조회 API 경로 생성
                        String detailApiPath = buildDetailApiPath(className, methodName, resourceId);
                        log.info("DELETE targetAsset 생성 성공: {}", detailApiPath);
                        return detailApiPath;
                    } else {
                        log.warn("DELETE ID 추출 실패: args가 있지만 ID를 찾을 수 없음");
                    }
                } else {
                    log.warn("DELETE args가 null이거나 비어있음");
                }
            }

        } catch (IllegalArgumentException e) {
            log.error("targetAsset 생성 중 오류 발생 (잘못된 인자): {}", e.getMessage(), e);
            log.error("오류 상세: action={}, className={}, methodName={}", action, className, methodName);
        } catch (RuntimeException e) {
            log.error("targetAsset 생성 중 오류 발생: {}", e.getMessage(), e);
            log.error("오류 상세: action={}, className={}, methodName={}", action, className, methodName);
        }

        // ID 추출 실패 시 빈 값
        log.warn("=== targetAsset 생성 실패: ID 추출 실패, 빈 값 저장 ===");
        log.warn("action={}, className={}, methodName={}", action, className, methodName);
        return "";
    }

    /**
     * 상세조회 API 경로 생성
     *
     * <p>
     * 리소스 ID를 포함한 상세조회 API 경로를 생성합니다.
     * </p>
     *
     * <h3>생성 규칙:</h3>
     * <ul>
     * <li>POST: /api/v1/data/dataset + ID → /api/v1/data/dataset/123</li>
     * <li>PUT: /api/v1/data/dataset/{id} → /api/v1/data/dataset/456</li>
     * <li>DELETE: /api/v1/data/dataset/{id} → /api/v1/data/dataset/789</li>
     * </ul>
     *
     * @param className  클래스명
     * @param methodName 메서드명
     * @param resourceId 리소스 ID
     * @return 상세조회 API 경로 (예: "/api/v1/data/dataset/123")
     */
    private String buildDetailApiPath(String className, String methodName, String resourceId) {
        // 현재 API 엔드포인트 기반으로 상세조회 경로 생성
        String currentEndpoint = httpInfoExtractor.getApiEndpoint(className, methodName);

        // PUT/DELETE: 이미 ID가 포함된 경로인 경우 (/{id} 또는 /123 형태)
        if (currentEndpoint != null && currentEndpoint.matches(".*/(\\d+|\\{id\\}).*")) {
            String detailPath = currentEndpoint.replaceAll("/(\\d+|\\{id\\})", "/" + resourceId);
            log.debug("상세조회 API 생성 (PUT/DELETE): {} -> {}", currentEndpoint, detailPath);
            return detailPath;
        }

        // POST: 현재 엔드포인트에 ID 추가하여 상세조회 경로 생성
        if (currentEndpoint != null) {
            // 쿼리 파라미터 제거
            if (currentEndpoint.contains("?")) {
                currentEndpoint = currentEndpoint.split("\\?")[0];
            }

            // 마지막 슬래시 처리
            String detailPath;
            if (currentEndpoint.endsWith("/")) {
                detailPath = currentEndpoint + resourceId;
            } else {
                detailPath = currentEndpoint + "/" + resourceId;
            }
            log.debug("상세조회 API 생성 (POST): {} + ID -> {}", currentEndpoint, detailPath);
            return detailPath;
        }

        // 엔드포인트를 찾을 수 없는 경우 클래스명 기반 추론
        String inferredPath = inferDetailApiPath(className, resourceId);
        log.debug("상세조회 API 추론: className={} -> {}", className, inferredPath);
        return inferredPath;
    }

    /**
     * 클래스명 기반 상세조회 API 경로 추론
     *
     * @param className  클래스명
     * @param resourceId 리소스 ID
     * @return 추론된 상세조회 API 경로
     */
    private String inferDetailApiPath(String className, String resourceId) {
        String cleanName = className.replace("Controller", "").replace("Client", "").toLowerCase();

        // Controller/Client명에서 리소스 경로 추론
        if (cleanName.contains("dataset")) {
            return "/api/v1/data/dataset/" + resourceId;
        } else if (cleanName.contains("knowledge")) {
            return "/api/v1/knowledge/" + resourceId;
        } else if (cleanName.contains("model")) {
            return "/api/v1/model/" + resourceId;
        } else if (cleanName.contains("agent")) {
            return "/api/v1/agent/" + resourceId;
        } else if (cleanName.contains("project")) {
            return "/api/v1/project/" + resourceId;
        } else if (cleanName.contains("notice")) {
            return "/api/v1/notice/" + resourceId;
        } else if (cleanName.contains("user")) {
            return "/api/v1/user/" + resourceId;
        } else if (cleanName.contains("role")) {
            return "/api/v1/role/" + resourceId;
        }

        // 기본 형식
        return "/api/v1/" + cleanName + "/" + resourceId;
    }

    /**
     * 응답 객체에서 ID 추출 (POST 용)
     *
     * @param responseObject 응답 객체
     * @return 추출된 ID
     */
    private String extractIdFromResponse(Object responseObject) {
        if (responseObject == null) {
            log.info("extractIdFromResponse: responseObject가 null");
            return null;
        }

        // 예외 객체인 경우 ID 추출 불가
        if (responseObject instanceof Throwable) {
            log.info("extractIdFromResponse: responseObject가 예외 객체이므로 ID 추출 불가");
            return null;
        }

        log.info("extractIdFromResponse: responseObject 타입 = {}", responseObject.getClass().getName());

        try {
            // AxResponseEntity인 경우 body에서 추출
            if (responseObject.getClass().getName().contains("AxResponseEntity")) {
                log.info("AxResponseEntity 감지, body 추출 시도");
                try {
                    java.lang.reflect.Method getBodyMethod = responseObject.getClass().getMethod("getBody");
                    Object body = getBodyMethod.invoke(responseObject);
                    if (body != null) {
                        log.info("body 추출 성공, 타입: {}", body.getClass().getName());
                        // AxResponse인 경우 data 필드에서 추출
                        if (body.getClass().getName().contains("AxResponse")) {
                            log.info("AxResponse 감지, data 필드 추출 시도");
                            try {
                                java.lang.reflect.Method getDataMethod = body.getClass().getMethod("getData");
                                Object data = getDataMethod.invoke(body);
                                if (data != null) {
                                    log.info("data 추출 성공, 타입: {}", data.getClass().getName());
                                    String id = extractIdFromObject(data);
                                    log.info("data에서 ID 추출 결과: {}", id);
                                    return id;
                                } else {
                                    log.warn("data가 null");
                                }
                            } catch (NoSuchMethodException e) {
                                log.warn("AxResponse getData 메서드 없음: {}", e.getMessage());
                            } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                                log.warn("AxResponse data 추출 실패 (리플렉션 오류): {}", e.getMessage());
                            } catch (RuntimeException e) {
                                log.warn("AxResponse data 추출 실패: {}", e.getMessage());
                            }
                        }
                        String id = extractIdFromObject(body);
                        log.info("body에서 ID 추출 결과: {}", id);
                        return id;
                    } else {
                        log.warn("body가 null");
                    }
                } catch (NoSuchMethodException e) {
                    log.warn("AxResponseEntity getBody 메서드 없음: {}", e.getMessage());
                } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                    log.warn("AxResponseEntity body 추출 실패 (리플렉션 오류): {}", e.getMessage());
                } catch (RuntimeException e) {
                    log.warn("AxResponseEntity body 추출 실패: {}", e.getMessage());
                }
            }

            // 일반 객체에서 ID 추출
            log.info("일반 객체에서 ID 추출 시도");
            String id = extractIdFromObject(responseObject);
            log.info("일반 객체에서 ID 추출 결과: {}", id);
            return id;

        } catch (RuntimeException e) {
            log.error("응답에서 ID 추출 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 객체에서 ID 추출 (리플렉션 사용)
     *
     * @param obj 객체
     * @return ID 문자열
     */
    private String extractIdFromObject(Object obj) {
        if (obj == null) {
            log.info("extractIdFromObject: obj가 null");
            return null;
        }

        log.info("extractIdFromObject: obj 타입 = {}", obj.getClass().getName());

        try {
            // 1. getId() 메서드 시도
            try {
                java.lang.reflect.Method getIdMethod = obj.getClass().getMethod("getId");
                Object id = getIdMethod.invoke(obj);
                if (id != null) {
                    log.info("getId() 메서드로 ID 추출 성공: {}", id);
                    return String.valueOf(id);
                } else {
                    log.info("getId() 메서드는 있지만 결과가 null");
                }
            } catch (NoSuchMethodException e) {
                log.info("getId() 메서드 없음");
            } catch (IllegalAccessException e) {
                log.debug("getId() 메서드 호출 실패 (접근 권한 오류): {}", e.getMessage());
            } catch (java.lang.reflect.InvocationTargetException e) {
                log.debug("getId() 메서드 호출 실패 (호출 대상 오류): {}", e.getMessage());
            }

            // 2. getSeq() 메서드 시도
            try {
                java.lang.reflect.Method getSeqMethod = obj.getClass().getMethod("getSeq");
                Object seq = getSeqMethod.invoke(obj);
                if (seq != null) {
                    log.info("getSeq() 메서드로 ID 추출 성공: {}", seq);
                    return String.valueOf(seq);
                } else {
                    log.info("getSeq() 메서드는 있지만 결과가 null");
                }
            } catch (NoSuchMethodException e) {
                log.info("getSeq() 메서드 없음");
            } catch (IllegalAccessException e) {
                log.debug("getSeq() 메서드 호출 실패 (접근 권한 오류): {}", e.getMessage());
            } catch (java.lang.reflect.InvocationTargetException e) {
                log.debug("getSeq() 메서드 호출 실패 (호출 대상 오류): {}", e.getMessage());
            }

            // 3. 모든 getter 메서드에서 *Id, *Seq 패턴 찾기
            java.lang.reflect.Method[] methods = obj.getClass().getMethods();
            for (java.lang.reflect.Method method : methods) {
                String methodName = method.getName();
                // get으로 시작하고 Id나 Seq로 끝나는 메서드
                if (methodName.startsWith("get") &&
                        (methodName.endsWith("Id") || methodName.endsWith("Seq")) &&
                        method.getParameterCount() == 0) {
                    try {
                        Object value = method.invoke(obj);
                        if (value != null) {
                            log.info("{}() 메서드로 ID 추출 성공: {}", methodName, value);
                            return String.valueOf(value);
                        }
                    } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                        log.debug("{}() 호출 실패 (리플렉션 오류): {}", methodName, e.getMessage());
                    } catch (RuntimeException e) {
                        log.debug("{}() 호출 실패: {}", methodName, e.getMessage());
                    }
                }
            }
            log.info("*Id, *Seq 패턴의 getter 메서드 없음");

            // 4. 필드에서 직접 추출 시도 (id, seq, *Id, *Seq 패턴)
            java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                String fieldName = field.getName().toLowerCase();
                // id 또는 seq로 끝나는 필드
                if (fieldName.equals("id") || fieldName.equals("seq") ||
                        fieldName.endsWith("id") || fieldName.endsWith("seq")) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (value != null) {
                            log.info("{} 필드에서 ID 추출 성공: {}", field.getName(), value);
                            return String.valueOf(value);
                        }
                    } catch (IllegalAccessException e) {
                        log.debug("{} 필드 접근 실패 (접근 권한 오류): {}", field.getName(), e.getMessage());
                    } catch (RuntimeException e) {
                        log.debug("{} 필드 접근 실패: {}", field.getName(), e.getMessage());
                    }
                }
            }
            log.info("*Id, *Seq 패턴의 필드 없음");

            // 5. toString()에서 id= 패턴 찾기 (마지막 수단)
            String objStr = obj.toString();
            log.info("toString() 결과: {}", objStr.length() > 200 ? objStr.substring(0, 200) + "..." : objStr);
            if (objStr.contains("id=")) {
                String[] parts = objStr.split("id=");
                if (parts.length > 1) {
                    String idPart = parts[1];
                    // 쉼표나 괄호 전까지 추출
                    if (idPart.contains(",")) {
                        idPart = idPart.split(",")[0];
                    }
                    if (idPart.contains(")")) {
                        idPart = idPart.split("\\)")[0];
                    }
                    if (idPart.contains(" ")) {
                        idPart = idPart.split(" ")[0];
                    }
                    log.info("toString()에서 id= 패턴으로 ID 추출 성공: {}", idPart.trim());
                    return idPart.trim();
                }
            } else {
                log.info("toString()에 id= 패턴 없음");
            }

        } catch (ClassCastException e) {
            log.error("객체에서 ID 추출 중 오류 (ClassCastException)", e);
        } catch (NullPointerException e) {
            log.error("객체에서 ID 추출 중 오류 (NullPointerException)", e);
        } catch (RuntimeException e) {
            log.error("객체에서 ID 추출 중 오류 (RuntimeException)", e);
        }

        log.warn("객체에서 ID 추출 실패");
        return null;
    }

    /**
     * 파라미터에서 ID 추출 (PUT/DELETE 용)
     *
     * @param args 메서드 파라미터 배열
     * @return 추출된 ID
     */
    private String extractIdFromArgs(Object[] args) {
        if (args == null || args.length == 0) {
            log.info("extractIdFromArgs: args가 null이거나 비어있음");
            return null;
        }

        log.info("extractIdFromArgs: args 개수 = {}", args.length);

        try {
            // 모든 파라미터 로깅
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    log.info("args[{}] 타입: {}, 값: {}", i, args[i].getClass().getName(), args[i]);
                } else {
                    log.info("args[{}] null", i);
                }
            }

            // 첫 번째 파라미터가 보통 ID (Long, String, Integer 등)
            Object firstArg = args[0];

            if (firstArg == null) {
                log.info("첫 번째 파라미터가 null");
                return null;
            }

            // Long, Integer, String 타입이면 ID로 간주
            if (firstArg instanceof Long ||
                    firstArg instanceof Integer ||
                    firstArg instanceof String) {
                log.info("첫 번째 파라미터가 숫자/문자열 타입, ID로 사용: {}", firstArg);
                return String.valueOf(firstArg);
            }

            // PathVariable로 전달된 경우 toString() 사용
            String argStr = firstArg.toString();
            log.info("첫 번째 파라미터 toString(): {}", argStr);

            // 숫자로만 구성되어 있으면 ID로 간주
            if (argStr.matches("\\d+")) {
                log.info("숫자 패턴 매칭, ID로 사용: {}", argStr);
                return argStr;
            } else {
                log.info("숫자 패턴 매칭 실패");
            }

        } catch (IllegalArgumentException e) {
            log.debug("파라미터에서 ID 추출 실패 (잘못된 인자): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("파라미터에서 ID 추출 실패: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 사용자 정보 조회 호출인지 판단
     */
    private boolean isUserInfoCall(String className, String methodName) {
        return (className.contains("User") &&
                (methodName.contains("getCurrentUser") ||
                        methodName.contains("getUsersMe") ||
                        methodName.equals("me")))
                ||
                methodName.contains("getUserInfo");
    }

    /**
     * 로그인 호출인지 판단
     */
    private boolean isLoginCall(String className, String methodName, String apiEndpoint) {
        return apiEndpoint.contains("/auth/login") ||
                methodName.contains("login") ||
                methodName.contains("authenticate");
    }

    /**
     * 로그인 파라미터에서 사용자 정보 추출
     */
    private String[] extractUserInfoFromLoginParams(Object[] args) {
        String[] result = new String[2]; // [userName, roleName]

        if (args == null || args.length == 0) {
            return result;
        }

        try {
            for (Object arg : args) {
                if (arg != null) {
                    String argStr = arg.toString();

                    if (argStr.contains("username") || argStr.contains("userId")) {
                        result[0] = extractUsernameFromString(argStr);
                    }

                    if (argStr.contains("role") || argStr.contains("authority")) {
                        result[1] = extractRoleFromString(argStr);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            log.debug("로그인 파라미터에서 사용자 정보 추출 실패 (IllegalArgumentException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("로그인 파라미터에서 사용자 정보 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("로그인 파라미터에서 사용자 정보 추출 실패 (RuntimeException): {}", e.getMessage());
        }

        return result;
    }

    /**
     * 문자열에서 사용자명 추출
     */
    private String extractUsernameFromString(String str) {
        try {
            if (str.contains("username=")) {
                String[] parts = str.split("username=");
                if (parts.length > 1) {
                    String value = parts[1];
                    if (value.contains(",")) {
                        value = value.split(",")[0];
                    }
                    return value.trim().replace("\"", "");
                }
            }

            if (str.contains("userId=")) {
                String[] parts = str.split("userId=");
                if (parts.length > 1) {
                    String value = parts[1];
                    if (value.contains(",")) {
                        value = value.split(",")[0];
                    }
                    return value.trim().replace("\"", "");
                }
            }
        } catch (IllegalArgumentException e) {
            log.debug("사용자명 추출 중 오류 (잘못된 인자): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("사용자명 추출 중 오류: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 문자열에서 역할명 추출
     */
    private String extractRoleFromString(String str) {
        try {
            if (str.contains("ROLE_ADMIN")) {
                return "ROLE_ADMIN";
            } else if (str.contains("ROLE_MANAGER")) {
                return "ROLE_MANAGER";
            } else if (str.contains("ROLE_USER")) {
                return "ROLE_USER";
            }
        } catch (IllegalArgumentException e) {
            log.debug("역할명 추출 중 오류 (잘못된 인자): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("역할명 추출 중 오류: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 로그인 로그에 사용자 정보 업데이트
     */
    private void updateLoginLogWithUserInfo(Long currentLogId, String userName, Object apiResult) {
        try {
            String userInfo = extractUserInfoFromResult(apiResult);
            if (userInfo != null && !userInfo.isEmpty()) {
                userUsageMgmtService.updateNearestLoginLogWithUserInfo(currentLogId, userName, userInfo);
                log.debug("로그인 로그 업데이트 요청: currentLogId={}, user={}, info={}", currentLogId, userName, userInfo);
            }
        } catch (DataAccessException e) {
            log.warn("로그인 로그 업데이트 중 오류 (DataAccessException): user={}, currentLogId={}, error={}", userName,
                    currentLogId, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("로그인 로그 업데이트 중 오류 (IllegalArgumentException): user={}, currentLogId={}, error={}", userName,
                    currentLogId, e.getMessage());
        } catch (NullPointerException e) {
            log.warn("로그인 로그 업데이트 중 오류 (NullPointerException): user={}, currentLogId={}, error={}", userName,
                    currentLogId, e.getMessage());
        } catch (RuntimeException e) {
            log.warn("로그인 로그 업데이트 중 오류 (RuntimeException): user={}, currentLogId={}, error={}", userName, currentLogId,
                    e.getMessage());
        }
    }

    /**
     * API 결과에서 사용자 정보 추출
     */
    private String extractUserInfoFromResult(Object result) {
        if (result == null) {
            return null;
        }

        String resultStr = result.toString();

        if (resultStr.contains("username") || resultStr.contains("userId") || resultStr.contains("authorities")) {
            if (resultStr.length() > 200) {
                return resultStr.substring(0, 200) + "...";
            }
            return resultStr;
        }

        return "UserInfoReceived";
    }

    /**
     * UserUsageMgmt 관련 FeignClient인지 확인
     */
    private boolean isUserUsageMgmtFeignClient(String className) {
        return className.contains("UserUsageMgmt") ||
                className.contains("UserActivityLog") ||
                className.contains("UserMgmt");
    }

    /**
     * Prometheus 관련 FeignClient인지 확인
     */
    private boolean isPrometheusFeignClient(String className) {
        return className.contains("ResrcMgmt") ||
                className.contains("Prometheus") ||
                className.contains("Metrics") ||
                className.contains("Resource");
    }

    /**
     * HTTP 상태 코드 정규화
     *
     * <p>
     * 201 (Created) 상태 코드를 200 (OK)으로 변환합니다.
     * 로그 저장 시 성공 응답을 일관되게 200으로 기록하기 위함입니다.
     * </p>
     *
     * @param httpStatusCode HTTP 상태 코드
     * @return 정규화된 HTTP 상태 코드 (201 → 200, 나머지는 그대로)
     */
    private String normalizeHttpStatusCode(String httpStatusCode) {
        if ("201".equals(httpStatusCode)) {
            return "200";
        }
        return httpStatusCode;
    }

    /**
     * 요청 데이터 추출 및 길이 제한 (최대 4000자)
     *
     * @param action HTTP 액션 (POST, PUT, DELETE, GET)
     * @param args   메서드 파라미터
     * @return 요청 데이터 (최대 700자)
     */
    private String extractRequestContent(String action, Object[] args) {
        // 실제 HTTP 메서드 확인
        String httpMethod = httpInfoExtractor.getHttpMethod();
        if (httpMethod == null) {
            // HTTP 메서드를 가져올 수 없는 경우 action으로 판단
            httpMethod = action;
        }

        // POST, PUT, DELETE, GET 모두 처리
        if (!("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "DELETE".equals(httpMethod)
                || "GET".equals(httpMethod))) {
            return null;
        }

        if (args == null || args.length == 0) {
            return null;
        }

        try {
            // 첫 번째 파라미터를 요청 데이터로 간주 (일반적으로 RequestBody)
            Object requestData = args[0];
            if (requestData == null) {
                return null;
            }

            String requestStr = convertObjectToString(requestData);
            return truncateToMaxLength(requestStr, 4000);
        } catch (RuntimeException e) {
            log.debug("요청 데이터 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 응답 데이터 추출 및 길이 제한 (최대 4000자)
     *
     * @param action         HTTP 액션 (POST, PUT, DELETE, GET)
     * @param responseObject 응답 객체
     * @return 응답 데이터 (최대 4000자)
     */
    private String extractResponseContent(String action, Object responseObject) {
        // 실제 HTTP 메서드 확인
        String httpMethod = httpInfoExtractor.getHttpMethod();
        if (httpMethod == null) {
            // HTTP 메서드를 가져올 수 없는 경우 action으로 판단
            httpMethod = action;
        }

        // // POST, PUT, DELETE, GET 모두 처리
        // if (!("POST".equals(httpMethod) || "PUT".equals(httpMethod) ||
        // "DELETE".equals(httpMethod) || "GET".equals(httpMethod))) {
        // return null;
        // }

        if (responseObject == null) {
            return null;
        }

        try {
            String responseStr = convertObjectToString(responseObject);
            return truncateToMaxLength(responseStr, 4000);
        } catch (RuntimeException e) {
            log.debug("응답 데이터 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 객체를 문자열로 변환
     *
     * @param obj 변환할 객체
     * @return 문자열 표현
     */
    private String convertObjectToString(Object obj) {
        if (obj == null) {
            return null;
        }

        // 예외 객체(Throwable)인 경우 바로 toString() 사용
        if (obj instanceof Throwable) {
            return obj.toString();
        }

        try {
            // AxResponseEntity인 경우 body에서 데이터 추출
            if (obj instanceof com.skax.aiplatform.common.response.AxResponseEntity) {
                com.skax.aiplatform.common.response.AxResponseEntity<?> responseEntity = (com.skax.aiplatform.common.response.AxResponseEntity<?>) obj;
                com.skax.aiplatform.common.response.AxResponse<?> response = responseEntity.getBody();
                if (response != null && response.getData() != null) {
                    obj = response.getData();
                }
            }
            // AxResponse인 경우 data 추출
            else if (obj instanceof com.skax.aiplatform.common.response.AxResponse) {
                com.skax.aiplatform.common.response.AxResponse<?> response = (com.skax.aiplatform.common.response.AxResponse<?>) obj;
                if (response.getData() != null) {
                    obj = response.getData();
                }
            }
            // ResponseEntity인 경우 body 추출
            else if (obj instanceof org.springframework.http.ResponseEntity) {
                org.springframework.http.ResponseEntity<?> responseEntity = (org.springframework.http.ResponseEntity<?>) obj;
                Object body = responseEntity.getBody();
                if (body != null) {
                    obj = body;
                }
            }

            // Jackson ObjectMapper를 사용하여 JSON 문자열로 변환
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // JSON 변환 실패 시 toString() 사용
            log.debug("JSON 변환 실패 (JsonProcessingException), toString() 사용: {}", e.getMessage());
            return obj.toString();
        } catch (ClassCastException e) {
            // JSON 변환 실패 시 toString() 사용
            log.debug("JSON 변환 실패 (ClassCastException), toString() 사용: {}", e.getMessage());
            return obj.toString();
        } catch (RuntimeException e) {
            // JSON 변환 실패 시 toString() 사용
            log.debug("JSON 변환 실패 (RuntimeException), toString() 사용: {}", e.getMessage());
            return obj.toString();
        }
    }

    /**
     * 문자열을 최대 길이로 자르기
     *
     * @param str       원본 문자열
     * @param maxLength 최대 길이
     * @return 잘린 문자열 (null이거나 빈 문자열인 경우 그대로 반환)
     */
    private String truncateToMaxLength(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        if (str.length() <= maxLength) {
            return str;
        }

        return str.substring(0, maxLength);
    }

    /**
     * 내용을 지정된 크기로 분할하여 배열로 반환
     *
     * @param content    분할할 내용
     * @param chunkSize  각 청크의 크기 (문자 수)
     * @param chunkCount 청크 개수
     * @return 분할된 내용 배열
     */
    private String[] splitContentIntoChunks(String content, int chunkSize, int chunkCount) {
        String[] result = new String[chunkCount];
        if (content == null || content.isEmpty() || chunkSize <= 0 || chunkCount <= 0) {
            return result;
        }

        int length = content.length();
        for (int i = 0; i < chunkCount; i++) {
            int start = i * chunkSize;
            if (start >= length) {
                break;
            }
            int end = Math.min(start + chunkSize, length);
            String part = content.substring(start, end);
            result[i] = part.isEmpty() ? null : part;
        }
        return result;
    }

}
