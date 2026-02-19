package com.skax.aiplatform.common.sql.aspect;

import com.skax.aiplatform.common.sql.SqlCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Service 메서드 SQL 주석 추적 Aspect
 * 
 * ServiceImpl 클래스의 모든 public 메서드를 인터셉트하여
 * SQL 주석 컨텍스트에 Service 정보를 설정합니다.
 * 
 * 주요 기능:
 * - ServiceImpl 클래스의 모든 public 메서드 추적
 * - 메서드 실행 전후로 컨텍스트 설정/정리
 * - 중첩 Service 호출 시 최상위 Service 정보 유지
 * - 예외 발생 시에도 컨텍스트 정리 보장
 *
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class ServiceSqlCommentAspect {
    
    /**
     * ServiceImpl 클래스의 public 메서드 실행 추적
     * 
     * @param joinPoint 메서드 실행 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.skax.aiplatform.service..impl.*ServiceImpl.*(..))")
    public Object trackServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // Service 클래스명과 메서드명 추출
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        // 이미 Service 컨텍스트가 설정되어 있다면 중첩 호출이므로 그대로 진행
        boolean shouldSetContext = !SqlCommentContext.hasServiceContext();
        
        try {
            if (shouldSetContext) {
                // Service 컨텍스트 설정
                SqlCommentContext.setServiceContext(className, methodName);
                log.debug("Service Context: {}.{}", className, methodName);
            }
            
            // 원본 메서드 실행
            Object result = joinPoint.proceed();
            
            if (shouldSetContext) {
                log.debug("Service Completed: {}.{}", className, methodName);
            }
            
            return result;
            
        } catch (IllegalArgumentException e) {
            if (shouldSetContext) {
                log.error("Service 실행 실패 (IllegalArgumentException) - {}.{}: 잘못된 인자", className, methodName, e);
            }
            throw e;
        } catch (NullPointerException e) {
            if (shouldSetContext) {
                log.error("Service 실행 실패 (NullPointerException) - {}.{}: 필수 값 누락", className, methodName, e);
            }
            throw e;
        } catch (com.skax.aiplatform.common.exception.BusinessException e) {
            if (shouldSetContext) {
                log.error("Service 실행 실패 (BusinessException) - {}.{}: 비즈니스 로직 오류", className, methodName, e);
            }
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {
            if (shouldSetContext) {
                log.error("Service 실행 실패 (DataAccessException) - {}.{}: 데이터 액세스 오류", className, methodName, e);
            }
            throw e;
        } catch (Exception e) {
            if (shouldSetContext) {
                log.error("Service 실행 실패 (예상치 못한 오류) - {}.{}", className, methodName, e);
            }
            throw e;
        } finally {
            if (shouldSetContext) {
                log.debug("Service Context Clear: {}.{}", className, methodName);
                SqlCommentContext.clearServiceContext();
            }
        }
    }
}