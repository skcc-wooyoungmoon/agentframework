package com.skax.aiplatform.common.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.skax.aiplatform.common.constant.Constants;
import com.skax.aiplatform.common.util.TraceUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 로깅 인터셉터 (AOP)
 * 
 * <p>
 * 컨트롤러, 서비스, 레포지토리 메서드의 실행을 자동으로 추적하고 로깅합니다.
 * 메서드 실행 시간, 파라미터, 결과를 구조화된 형태로 기록합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingInterceptor {

    private final UserActivityLogger userActivityLogger;
    private final HttpInfoExtractor httpInfoExtractor;
    private final LogValueFormatter logValueFormatter;

    // 사용자 활동 로그 중복 방지를 위한 ThreadLocal
    private static final ThreadLocal<Boolean> isFeignClientCallExpected = new ThreadLocal<>();

    /**
     * 컨트롤러 메서드 실행 로깅
     * 
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.skax.aiplatform.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        // 사용자 이용 현황 관련 API는 로깅하지 않음 (무한 루프 방지)
        if (isUserUsageMgmtController(joinPoint)) {
            return joinPoint.proceed();
        }
        return logMethodExecution(joinPoint, Constants.Logging.CATEGORY_CONTROLLER);
    }

    /**
     * 서비스 메서드 실행 로깅
     * 
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.skax.aiplatform.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, Constants.Logging.CATEGORY_SERVICE);
    }

    /**
     * 레포지토리 메서드 실행 로깅
     * 
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.skax.aiplatform.repository..*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, Constants.Logging.CATEGORY_REPOSITORY);
    }

    /**
     * Feign Client 메서드 실행 로깅
     * 
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.skax.aiplatform.client..*(..))")
    public Object logFeignClient(ProceedingJoinPoint joinPoint) throws Throwable {
        return logApiCall(joinPoint);
    }

    /**
     * 메서드 실행 로깅 공통 로직
     * 
     * @param joinPoint 조인 포인트
     * @param layer     레이어 타입 (CONTROLLER, SERVICE, REPOSITORY)
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 새로운 스팬 ID 생성
        String parentSpanId = TraceUtils.getSpanId();
        String currentSpanId = TraceUtils.generateSpanId();
        TraceUtils.setSpanId(currentSpanId);
        TraceUtils.setParentSpanId(parentSpanId);

        long startTime = System.currentTimeMillis();
        String resultStatus = "SUCCESS";
        String errorMessage = null;

        // 메서드 시작 로그
        log.info("{}|{}|{}|{}|args={}",
                layer,
                Constants.Logging.METHOD_START,
                className,
                methodName,
                logValueFormatter.formatArgs(args));

        try {
            // 메서드 실행
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;

            // 메서드 완료 로그
            log.info("{}|{}|{}|{}|duration={}ms|result={}",
                    layer,
                    Constants.Logging.METHOD_END,
                    className,
                    methodName,
                    duration,
                    logValueFormatter.formatResult(result));

            // Controller 레이어인 경우 사용자 활동 로그 저장 (성공)
            if (Constants.Logging.CATEGORY_CONTROLLER.equals(layer)
                    && shouldLogControllerActivity(className, methodName)) {
                try {
                    // Feign Client 호출이 예상되는 경우 Controller 로그는 건너뛰고 플래그만 설정
                    if (willCallFeignClient(className, methodName)) {
                        isFeignClientCallExpected.set(true);
                        log.debug("Feign Client 호출 예상으로 Controller 로그 건너뜀: {}.{}", className, methodName);
                    } else {
                        // Feign Client 호출이 없는 Controller만 로깅
                        String httpStatusCode = httpInfoExtractor.getHttpStatusCode(result, null);
                        userActivityLogger.saveControllerLog(className, methodName, resultStatus, null, httpStatusCode,
                                args, result);
                        log.debug("Feign Client 호출이 없는 Controller 로그 저장: {}.{}", className, methodName);
                    }
                } catch (DataAccessException loggingException) {
                    // 로깅 실패 시에도 원본 API 호출에는 영향을 주지 않음
                    log.warn("Controller 로깅 실패 (DataAccessException) (시스템 자동 로깅): controller={}, method={}, error={}",
                            className, methodName, loggingException.getMessage());
                } catch (NullPointerException loggingException) {
                    log.warn("Controller 로깅 실패 (NullPointerException) (시스템 자동 로깅): controller={}, method={}, error={}",
                            className, methodName, loggingException.getMessage());
                } catch (RuntimeException loggingException) {
                    log.warn("Controller 로깅 실패 (RuntimeException) (시스템 자동 로깅): controller={}, method={}, error={}",
                            className, methodName, loggingException.getMessage());
                }
            }

            return result;

        } catch (Exception throwable) {
            long duration = System.currentTimeMillis() - startTime;
            resultStatus = "ERROR";
            errorMessage = throwable.getMessage();

            // 특별한 예외 타입 감지 및 상세 로깅
            String exceptionType = throwable.getClass().getSimpleName();
            String errorCode = getErrorCodeForException(throwable);

            // 메서드 오류 로그 (더 상세한 정보 포함)
            TraceUtils.logError("메서드 실행 중 오류 발생", throwable,
                    "layer", layer,
                    "class", className,
                    "method", methodName,
                    "duration", duration + "ms",
                    "exceptionType", exceptionType,
                    "errorCode", errorCode,
                    "exceptionMessage", throwable.getMessage());

            // Controller 레이어인 경우 사용자 활동 로그 저장 (실패)
            if (Constants.Logging.CATEGORY_CONTROLLER.equals(layer)
                    && shouldLogControllerActivity(className, methodName)) {
                try {
                    // Feign Client 호출이 예상되는 경우 Controller 로그는 건너뛰고 플래그만 설정
                    if (willCallFeignClient(className, methodName)) {
                        isFeignClientCallExpected.set(true);
                        log.debug("Feign Client 호출 예상으로 Controller 에러 로그 건너뜀: {}.{}", className, methodName);
                    } else {
                        // Feign Client 호출이 없는 Controller만 로깅
                        String httpStatusCode = httpInfoExtractor.getHttpStatusCode(null, throwable);
                        userActivityLogger.saveControllerLog(className, methodName, resultStatus, errorMessage,
                                httpStatusCode, args, throwable);
                        log.debug("Feign Client 호출이 없는 Controller 에러 로그 저장: {}.{}", className, methodName);
                    }
                } catch (DataAccessException loggingException) {
                    // 로깅 실패 시에도 원본 API 호출에는 영향을 주지 않음
                    log.warn(
                            "Controller 에러 로깅 실패 (DataAccessException) (시스템 자동 로깅): controller={}, method={}, error={}",
                            className, methodName, loggingException.getMessage());
                } catch (NullPointerException loggingException) {
                    log.warn(
                            "Controller 에러 로깅 실패 (NullPointerException) (시스템 자동 로깅): controller={}, method={}, error={}",
                            className, methodName, loggingException.getMessage());
                } catch (RuntimeException loggingException) {
                    log.warn("Controller 에러 로깅 실패 (RuntimeException) (시스템 자동 로깅): controller={}, method={}, error={}",
                            className, methodName, loggingException.getMessage());
                }
            }

            throw throwable;

        } catch (Error error) {
            long duration = System.currentTimeMillis() - startTime;
            resultStatus = "ERROR";
            errorMessage = error.getMessage();

            String exceptionType = error.getClass().getSimpleName();
            String errorCode = exceptionType;

            TraceUtils.logError("메서드 실행 중 심각한 오류(Error) 발생", error,
                    "layer", layer,
                    "class", className,
                    "method", methodName,
                    "duration", duration + "ms",
                    "exceptionType", exceptionType,
                    "errorCode", errorCode,
                    "exceptionMessage", error.getMessage());

            throw error;

        } finally {
            // 스팬 ID 복원
            TraceUtils.setSpanId(parentSpanId);
            TraceUtils.removeParentSpanId();

            // Feign Client 호출 플래그 정리
            if (Constants.Logging.CATEGORY_CONTROLLER.equals(layer)) {
                isFeignClientCallExpected.remove();
            }
        }
    }

    /**
     * 예외 타입에 따른 에러 코드 반환
     * 
     * @param throwable 발생한 예외
     * @return 해당하는 에러 코드
     */
    private String getErrorCodeForException(Throwable throwable) {
        String exceptionType = throwable.getClass().getSimpleName();

        switch (exceptionType) {
            case "ClassCastException":
                return Constants.Exception.AUTH_004; // Principal 타입 오류
            case "AuthenticationException":
                return Constants.Exception.AUTH_001; // 인증 실패
            case "AccessDeniedException":
                return Constants.Exception.AUTH_002; // 권한 부족
            case "ValidationException":
                return Constants.Exception.VAL_001; // 입력값 검증 실패
            case "BusinessException":
                return Constants.Exception.BIZ_001; // 비즈니스 로직 오류
            default:
                return "UNKNOWN"; // 알 수 없는 오류
        }
    }

    /**
     * API 호출 로깅 (Feign Client)
     * 
     * @param joinPoint 조인 포인트
     * @return API 호출 결과
     * @throws Throwable API 호출 중 발생한 예외
     */
    private Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // API 호출 시작 로그
        TraceUtils.logApiCallStart(className, methodName, logValueFormatter.formatArgs(args));

        long startTime = System.currentTimeMillis();
        String resultStatus = "SUCCESS";
        String errorMessage = null;

        try {
            // API 호출 실행
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;

            // API 호출 완료 로그
            TraceUtils.logApiCallEnd(className, methodName, duration, logValueFormatter.formatResult(result));

            // 사용자 활동 로그 저장 (성공)
            try {
                String httpStatusCode = httpInfoExtractor.getHttpStatusCode(result, null);
                userActivityLogger.saveFeignClientLog(className, methodName, args, resultStatus, null, httpStatusCode,
                        result);
            } catch (DataAccessException loggingException) {
                // 로깅 실패 시에도 원본 API 호출에는 영향을 주지 않음
                log.warn("Feign Client 로깅 실패 (DataAccessException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            } catch (NullPointerException loggingException) {
                log.warn("Feign Client 로깅 실패 (NullPointerException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            } catch (RuntimeException loggingException) {
                log.warn("Feign Client 로깅 실패 (RuntimeException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            }

            return result;

        } catch (RuntimeException runtimeException) {
            long duration = System.currentTimeMillis() - startTime;
            resultStatus = "ERROR";
            errorMessage = runtimeException.getMessage();

            // 예외 타입 및 에러 코드 감지
            String exceptionType = runtimeException.getClass().getSimpleName();
            String errorCode = getErrorCodeForException(runtimeException);

            // API 호출 오류 로그 (상세 정보 포함)
            TraceUtils.logError("API 호출 중 오류 발생", runtimeException,
                    "client", className,
                    "method", methodName,
                    "duration", duration + "ms",
                    "exceptionType", exceptionType,
                    "errorCode", errorCode,
                    "exceptionMessage", runtimeException.getMessage());

            // 사용자 활동 로그 저장 (실패)
            try {
                String httpStatusCode = httpInfoExtractor.getHttpStatusCode(null, runtimeException);
                userActivityLogger.saveFeignClientLog(className, methodName, args, resultStatus, errorMessage,
                        httpStatusCode, runtimeException);
            } catch (DataAccessException loggingException) {
                // 로깅 실패 시에도 원본 API 호출에는 영향을 주지 않음
                log.warn("Feign Client 에러 로깅 실패 (DataAccessException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            } catch (NullPointerException loggingException) {
                log.warn("Feign Client 에러 로깅 실패 (NullPointerException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            } catch (RuntimeException loggingException) {
                log.warn("Feign Client 에러 로깅 실패 (RuntimeException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            }

            throw runtimeException;
        } catch (Exception checkedException) {
            long duration = System.currentTimeMillis() - startTime;
            resultStatus = "ERROR";
            errorMessage = checkedException.getMessage();

            // 예외 타입 및 에러 코드 감지
            String exceptionType = checkedException.getClass().getSimpleName();
            String errorCode = getErrorCodeForException(checkedException);

            // API 호출 오류 로그 (상세 정보 포함)
            TraceUtils.logError("API 호출 중 checked exception 발생", checkedException,
                    "client", className,
                    "method", methodName,
                    "duration", duration + "ms",
                    "exceptionType", exceptionType,
                    "errorCode", errorCode,
                    "exceptionMessage", checkedException.getMessage());

            // 사용자 활동 로그 저장 (실패)
            try {
                String httpStatusCode = httpInfoExtractor.getHttpStatusCode(null, checkedException);
                userActivityLogger.saveFeignClientLog(className, methodName, args, resultStatus, errorMessage,
                        httpStatusCode, checkedException);
            } catch (DataAccessException loggingException) {
                // 로깅 실패 시에도 원본 API 호출에는 영향을 주지 않음
                log.warn("Feign Client 에러 로깅 실패 (DataAccessException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            } catch (NullPointerException loggingException) {
                log.warn("Feign Client 에러 로깅 실패 (NullPointerException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            } catch (RuntimeException loggingException) {
                log.warn("Feign Client 에러 로깅 실패 (RuntimeException) (시스템 자동 로깅): client={}, method={}, error={}",
                        className, methodName, loggingException.getMessage());
            }

            throw checkedException;
        } catch (Error error) {
            long duration = System.currentTimeMillis() - startTime;
            resultStatus = "ERROR";
            errorMessage = error.getMessage();

            // 심각한 시스템 오류 로그
            log.error("API 호출 중 심각한 시스템 오류 발생: client={}, method={}, duration={}ms, error={}",
                    className, methodName, duration, error.getMessage(), error);

            // Error는 다시 던져서 상위로 전파
            throw error;
        }
    }

    /**
     * Controller에서 Feign Client를 호출할지 판단
     * 
     * @param className  Controller 클래스명
     * @param methodName 메서드명
     * @return Feign Client 호출 여부
     */
    private boolean willCallFeignClient(String className, String methodName) {
        // 메서드명으로 더 정확한 판단
        if (methodName.contains("sktai") ||
                methodName.contains("external") ||
                methodName.contains("gateway") ||
                methodName.contains("inference") ||
                methodName.contains("invoke") ||
                methodName.contains("chat") ||
                methodName.contains("completion")) {
            return true;
        }

        // 특정 외부 API 연동 Controller들
        if (className.contains("Agent") ||
                className.contains("Model") ||
                className.contains("Knowledge") ||
                className.contains("Evaluation") ||
                className.contains("Infer") ||
                className.contains("Prompt")) {
            return true;
        }

        // AuthController와 LoginController는 대부분 외부 인증 서비스 호출
        if (className.contains("Auth") || className.contains("Login")) {
            return true;
        }

        // UserController는 메서드명으로 구분
        if (className.contains("User")) {
            // 외부 인증 관련 메서드만 Feign Client 호출
            return methodName.contains("getCurrentUser") ||
                    methodName.contains("getUserInfo") ||
                    methodName.contains("getUsersMe") ||
                    methodName.contains("authenticate") ||
                    methodName.contains("login") ||
                    methodName.contains("logout") ||
                    methodName.contains("token") ||
                    methodName.contains("refresh") ||
                    methodName.contains("verify") ||
                    methodName.contains("validate") ||
                    methodName.equals("me");
        }

        // DataController는 메서드명으로 구분
        if (className.contains("Data")) {
            // 외부 데이터 처리 관련 메서드만 Feign Client 호출
            return methodName.contains("generate") ||
                    methodName.contains("process") ||
                    methodName.contains("transform");
        }

        return false;
    }

    /**
     * Controller 활동 로그 저장 여부 판단
     * 
     * @param className  Controller 클래스명
     * @param methodName 메서드명
     * @return 로그 저장 여부
     */
    private boolean shouldLogControllerActivity(String className, String methodName) {
        // Health check나 시스템 체크는 로깅하지 않음
        if (className.contains("Health") || methodName.equals("health") || methodName.equals("status")) {
            return false;
        }

        // 사용자 활동 로그 조회는 로깅하지 않음 (무한 루프 방지 및 불필요한 로그 생성 방지)
        if (className.contains("UserActivityLog")) {
            return false;
        }

        // 모든 Controller API 호출을 로깅 (사용자 요청의 진입점)
        return true;
    }

    /**
     * 사용자 이용 현황 관련 Controller인지 확인
     * 
     * @param joinPoint 조인 포인트
     * @return 사용자 이용 현황 관련 Controller 여부
     */
    private boolean isUserUsageMgmtController(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // UserUsageMgmtController 클래스인지 확인
        if (className.contains("UserUsageMgmtController")) {
            return true;
        }

        // URL 경로로도 확인
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String uri = request.getRequestURI().toLowerCase();
                if (uri.contains("/user-usage-mgmt") || uri.contains("/user-activity-logs")) {
                    return true;
                }
            }
        } catch (ClassCastException e) {
            log.debug("URL 확인 중 오류 발생 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("URL 확인 중 오류 발생 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("URL 확인 중 오류 발생 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("URL 확인 중 오류 발생 (RuntimeException): {}", e.getMessage());
        }

        return false;
    }
}
