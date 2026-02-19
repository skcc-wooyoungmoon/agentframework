package com.skax.aiplatform.common.sql.aspect;

import com.skax.aiplatform.common.sql.SqlCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Repository 메서드 SQL 주석 추적 Aspect
 * 
 * Repository 클래스의 모든 메서드를 인터셉트하여
 * SQL 주석 컨텍스트에 Repository 정보를 추가합니다.
 * 
 * 주요 기능:
 * - 커스텀 Repository와 JPA Repository 메서드 모두 추적
 * - 메서드 실행 시마다 Repository 컨텍스트 갱신
 * - Service에서 여러 Repository 호출 시 각각 추적
 * - 중복 인터셉션 방지로 성능 최적화
 *
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.1
 */
@Aspect
@Component
@Slf4j
public class RepositorySqlCommentAspect {
    
    /**
     * Repository 메서드 실행 추적 (우선순위 기반 단일 Pointcut)
     * 
     * 프로젝트 내 Repository만 추적하여 중복 실행 방지:
     * - 커스텀 Repository: com.skax.aiplatform.repository 패키지 내 *Repository 클래스
     * 
     * @param joinPoint 메서드 실행 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.skax.aiplatform.repository..*Repository.*(..))")
    public Object trackRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return executeWithRepositoryContext(joinPoint);
    }
    
    // 중복 실행 방지를 위한 ThreadLocal 플래그
    private static final ThreadLocal<Boolean> REPOSITORY_CONTEXT_ACTIVE = new ThreadLocal<>();
    
    /**
     * Repository 컨텍스트로 메서드 실행
     * 
     * @param joinPoint 메서드 실행 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    private Object executeWithRepositoryContext(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // 이미 Repository 컨텍스트가 활성화되어 있으면 중복 실행 방지
        if (Boolean.TRUE.equals(REPOSITORY_CONTEXT_ACTIVE.get())) {
            return joinPoint.proceed();
        }
        
        // Repository 클래스명과 메서드명 추출
        String tempClassName = joinPoint.getTarget().getClass().getSimpleName();
        
        // JDK Proxy의 경우 인터페이스명 사용
        if (tempClassName.contains("$Proxy")) {
            Class<?>[] interfaces = joinPoint.getTarget().getClass().getInterfaces();
            if (interfaces.length > 0) {
                tempClassName = interfaces[0].getSimpleName();
            }
        }
        
        final String className = tempClassName;
        final String methodName = joinPoint.getSignature().getName();
        
        try {
            // 중복 실행 방지 플래그 설정
            REPOSITORY_CONTEXT_ACTIVE.set(true);
            
            // Repository 컨텍스트 설정
            SqlCommentContext.addRepositoryContext(className, methodName);
            log.debug("Repository Context: {}.{}", className, methodName);
            
            // 원본 메서드 실행
            Object result = joinPoint.proceed();
            return result;
            
        } catch (IllegalArgumentException e) {
            log.error("Repository 실행 실패 (IllegalArgumentException) - {}.{}: 잘못된 인자", className, methodName, e);
            throw e;
        } catch (NullPointerException e) {
            log.error("Repository 실행 실패 (NullPointerException) - {}.{}: 필수 값 누락", className, methodName, e);
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("Repository 실행 실패 (DataAccessException) - {}.{}: 데이터 액세스 오류", className, methodName, e);
            throw e;
        } catch (Exception e) {
            log.error("Repository 실행 실패 (예상치 못한 오류) - {}.{}", className, methodName, e);
            throw e;
        } finally {
            // 중복 실행 방지 플래그 정리
            REPOSITORY_CONTEXT_ACTIVE.remove();
        }
    }
}