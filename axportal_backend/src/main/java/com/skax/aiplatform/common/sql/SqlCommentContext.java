package com.skax.aiplatform.common.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * SQL 주석 컨텍스트 관리 클래스
 * 
 * ThreadLocal을 사용하여 Service to Repository 호출 체인을 추적하고,
 * 각 SQL 쿼리에 자동으로 호출 컨텍스트를 주석으로 추가합니다.
 * 
 * 주석 형식: ServiceImpl.method.Repository.method
 * 
 * 특징:
 * - Service 한 메서드에서 여러 Repository 호출 지원
 * - 트랜잭션 커밋 시점까지 컨텍스트 유지 (JPA Write Behind 지원)
 * - Hibernate 기본 주석 제거
 * - Thread-safe 처리
 *
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Slf4j
public class SqlCommentContext {
    
    private static final ThreadLocal<String> SERVICE_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_REPOSITORY = new ThreadLocal<>();
    private static final ThreadLocal<String> LAST_REPOSITORY = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> TRANSACTION_SYNC_REGISTERED = new ThreadLocal<>();
    
    /**
     * Service 컨텍스트 설정
     * 
     * @param serviceClass Service 구현 클래스명
     * @param methodName Service 메서드명
     */
    public static void setServiceContext(String serviceClass, String methodName) {
        String context = serviceClass + "." + methodName;
        SERVICE_CONTEXT.set(context);
        
        // 트랜잭션이 활성화되어 있다면 트랜잭션 동기화 등록
        registerTransactionSynchronization();
        
        // Service 컨텍스트 설정 완료
    }
    
    /**
     * 트랜잭션 동기화 등록 (컨텍스트를 트랜잭션 커밋 시점까지 유지)
     */
    private static void registerTransactionSynchronization() {
        if (TransactionSynchronizationManager.isActualTransactionActive() &&
            !Boolean.TRUE.equals(TRANSACTION_SYNC_REGISTERED.get())) {
            
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    clearAllContext();
                    TRANSACTION_SYNC_REGISTERED.remove();
                }
            });
            
            TRANSACTION_SYNC_REGISTERED.set(true);
            // 트랜잭션 동기화 등록됨
        }
    }
    
    /**
     * Repository 컨텍스트 추가
     * 
     * @param repositoryClass Repository 클래스명
     * @param methodName Repository 메서드명
     */
    public static void addRepositoryContext(String repositoryClass, String methodName) {
        String repoContext = repositoryClass + "." + methodName;
        
        // 현재 Repository 컨텍스트를 마지막 Repository로 백업
        String currentRepo = CURRENT_REPOSITORY.get();
        if (currentRepo != null) {
            LAST_REPOSITORY.set(currentRepo);
        }
        
        CURRENT_REPOSITORY.set(repoContext);
        
        // 트랜잭션 동기화가 아직 등록되지 않았다면 등록
        registerTransactionSynchronization();
        
        // Repository 컨텍스트 설정 완료
    }
    
    /**
     * 현재 SQL 주석 생성
     * 
     * @return SQL 주석 문자열 (형식: ServiceImpl.method.Repository.method)
     */
    public static String getCurrentComment() {
        String serviceContext = SERVICE_CONTEXT.get();
        String repositoryContext = CURRENT_REPOSITORY.get();
        
        // 현재 Repository 컨텍스트가 없으면 마지막 Repository 컨텍스트 사용
        if (repositoryContext == null) {
            repositoryContext = LAST_REPOSITORY.get();
        }
        
        if (serviceContext != null && repositoryContext != null) {
            return serviceContext + "." + repositoryContext;
        }
        
        return null;
    }
    
    /**
     * Service 컨텍스트 정리 (메서드 종료 시)
     * 트랜잭션이 활성화되어 있다면 즉시 정리하지 않음
     */
    public static void clearServiceContext() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // 트랜잭션 활성 중 - Service 컨텍스트 유지
            return;
        }
        
        clearAllContext();
    }
    
    /**
     * 모든 컨텍스트 강제 정리
     */
    private static void clearAllContext() {
        SERVICE_CONTEXT.remove();
        CURRENT_REPOSITORY.remove();
        LAST_REPOSITORY.remove();
        TRANSACTION_SYNC_REGISTERED.remove();
    }
    
    /**
     * Repository 컨텍스트 정리 (Repository 메서드 종료 시)
     */
    public static void clearRepositoryContext() {
        CURRENT_REPOSITORY.remove();
    }
    
    /**
     * Service 컨텍스트가 설정되어 있는지 확인
     * 
     * @return Service 컨텍스트 존재 여부
     */
    public static boolean hasServiceContext() {
        return SERVICE_CONTEXT.get() != null;
    }
    
    /**
     * 현재 Service 컨텍스트 반환
     * 
     * @return Service 컨텍스트
     */
    public static String getServiceContext() {
        return SERVICE_CONTEXT.get();
    }
    
    /**
     * 현재 컨텍스트 상태 확인 (디버깅용)
     * 
     * @return 컨텍스트 정보
     */
    public static String getContextInfo() {
        return String.format("Service: %s, Repository: %s, Last Repository: %s", 
            SERVICE_CONTEXT.get(), CURRENT_REPOSITORY.get(), LAST_REPOSITORY.get());
    }
}