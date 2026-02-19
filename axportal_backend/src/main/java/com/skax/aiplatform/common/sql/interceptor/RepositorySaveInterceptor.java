package com.skax.aiplatform.common.sql.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.skax.aiplatform.common.sql.SqlCommentContext;

import lombok.extern.slf4j.Slf4j;

/**
 * JPA Repository save/update 메서드 AOP 인터셉터
 * 실제 DB 저장 시점에서 SQL 주석을 확실히 적용하기 위한 최후 수단
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class RepositorySaveInterceptor {

    /**
     * save, saveAll, saveAndFlush 메서드 인터셉트
     */
    @Around("execution(* org.springframework.data.repository.CrudRepository.save(..)) || " +
            "execution(* org.springframework.data.repository.CrudRepository.saveAll(..)) || " +
            "execution(* org.springframework.data.jpa.repository.JpaRepository.saveAndFlush(..))")
    public Object interceptSaveOperations(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String targetClass = joinPoint.getTarget().getClass().getSimpleName();

        log.info("🔄 Repository {} 작업 시작: {} - 컨텍스트: {}",
                methodName, targetClass, SqlCommentContext.getContextInfo());

        // 현재 컨텍스트 강화
        String currentComment = SqlCommentContext.getCurrentComment();
        if (currentComment != null) {
            // 실제 Repository 메서드명으로 컨텍스트 업데이트
            String repositoryName = targetClass.replace("$Proxy", "").replace("$", "");
            String enhancedComment = currentComment.replaceAll("MemberRepository\\.\\w+",
                    repositoryName + "." + methodName);

            log.info("📝 Repository 컨텍스트 강화: {} → {}", currentComment, enhancedComment);

            // 임시로 강화된 컨텍스트 설정 (save 작업 동안만)
            SqlCommentContext.addRepositoryContext(repositoryName, methodName);
        }

        try {
            // 실제 save 작업 실행
            Object result = joinPoint.proceed();

            log.info("✅ Repository {} 작업 완료: {} - 엔티티: {}",
                    methodName, targetClass,
                    result != null ? result.getClass().getSimpleName() : "null");

            return result;

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("❌ Repository {} 작업 실패 (잘못된 입력값): {}", methodName, targetClass);
            throw e;
        } catch (RuntimeException e) {
            log.error("❌ Repository {} 작업 실패 (런타임 오류): {}", methodName, targetClass);
            throw e;
        } catch (Throwable t) {
            log.error("❌ Repository {} 작업 실패 (예상치 못한 오류): {}", methodName, targetClass);
            throw new RuntimeException("Repository 작업 실패", t);
        }
    }

    /**
     * flush 메서드 인터셉트 (명시적 flush 호출 시)
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.flush())")
    public Object interceptFlushOperations(ProceedingJoinPoint joinPoint) throws Throwable {

        String targetClass = joinPoint.getTarget().getClass().getSimpleName();

        log.info("🔄 Repository flush 시작: {} - 컨텍스트: {}",
                targetClass, SqlCommentContext.getContextInfo());

        try {
            Object result = joinPoint.proceed();
            log.info("✅ Repository flush 완료: {}", targetClass);
            return result;

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("❌ Repository flush 실패 (잘못된 입력값): {}", targetClass);
            throw e;
        } catch (RuntimeException e) {
            log.error("❌ Repository flush 실패 (런타임 오류): {}", targetClass);
            throw e;
        } catch (Throwable t) {
            log.error("❌ Repository flush 실패 (예상치 못한 오류): {}", targetClass);
            throw new RuntimeException("Repository flush 실패", t);
        }
    }
}