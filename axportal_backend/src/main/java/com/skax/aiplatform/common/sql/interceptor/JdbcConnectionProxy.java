package com.skax.aiplatform.common.sql.interceptor;

import com.skax.aiplatform.common.sql.SqlCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * JDBC Connection 프록시를 통한 SQL 주석 삽입
 * DB 모니터링 툴이 실제 실행되는 SQL에서 주석을 볼 수 있도록 처리
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 */
@Slf4j
@Component
public class JdbcConnectionProxy {
    
    /**
     * DataSource를 프록시로 래핑하여 Connection에 주석 삽입 기능 추가
     */
    public DataSource wrapDataSource(DataSource originalDataSource) {
        return (DataSource) Proxy.newProxyInstance(
            DataSource.class.getClassLoader(),
            new Class[]{DataSource.class},
            new DataSourceInvocationHandler(originalDataSource)
        );
    }
    
    /**
     * DataSource 프록시 핸들러
     */
    private static class DataSourceInvocationHandler implements InvocationHandler {
        private final DataSource target;
        
        public DataSourceInvocationHandler(DataSource target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("DataSource 메서드 호출: {}", method.getName());
            Object result = method.invoke(target, args);
            
            // getConnection 메서드인 경우 Connection을 프록시로 래핑
            if ("getConnection".equals(method.getName()) && result instanceof Connection) {
                log.debug("✅ Connection 프록시 래핑 적용");
                return wrapConnection((Connection) result);
            }
            
            return result;
        }
    }
    
    /**
     * Connection을 프록시로 래핑
     */
    private static Connection wrapConnection(Connection originalConnection) {
        return (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class[]{Connection.class},
            new ConnectionInvocationHandler(originalConnection)
        );
    }
    
    /**
     * Connection 프록시 핸들러
     */
    private static class ConnectionInvocationHandler implements InvocationHandler {
        private final Connection target;
        
        public ConnectionInvocationHandler(Connection target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("Connection 메서드 호출: {}", method.getName());
            Object result = method.invoke(target, args);
            
            // prepareStatement 메서드인 경우 SQL에 주석 추가
            if ("prepareStatement".equals(method.getName()) && args.length > 0 && args[0] instanceof String) {
                String originalSql = (String) args[0];
                String commentedSql = addCommentToSql(originalSql);
                
                log.debug("PreparedStatement 생성 - 원본 SQL: {}", originalSql);
                
                // SQL이 변경된 경우 주석이 추가된 SQL로 PreparedStatement 생성
                if (!originalSql.equals(commentedSql)) {
                    args[0] = commentedSql;
                    result = method.invoke(target, args);
                    log.info("🚀 JDBC PreparedStatement 주석 적용됨 - 길이: {} -> {}", 
                            originalSql.length(), commentedSql.length());
                } else {
                    // SQL이 동일한 경우에도 DML 타입 체크
                    String upperSql = originalSql.trim().toUpperCase();
                    if (upperSql.startsWith("UPDATE") || upperSql.startsWith("INSERT") || upperSql.startsWith("DELETE")) {
                        log.info("🔍 DML PreparedStatement 확인됨 - 타입: {}", 
                                upperSql.startsWith("UPDATE") ? "UPDATE" : 
                                upperSql.startsWith("INSERT") ? "INSERT" : "DELETE");
                    }
                }
            }
            
            return result;
        }
    }
    
    /**
     * SQL에 주석 추가
     */
    private static String addCommentToSql(String originalSql) {
        String comment = SqlCommentContext.getCurrentComment();
        
        if (comment == null || comment.trim().isEmpty() || originalSql == null) {
            log.debug("SQL 주석 컨텍스트가 없어 주석 추가 생략");
            return originalSql;
        }
        
        String trimmedSql = originalSql.trim();
        
        // 주석이 있는 경우 주석 뒤의 실제 SQL을 추출하여 타입 판별
        String actualSql = trimmedSql;
        if (trimmedSql.startsWith("/*")) {
            int endIndex = trimmedSql.indexOf("*/");
            if (endIndex != -1 && endIndex + 2 < trimmedSql.length()) {
                actualSql = trimmedSql.substring(endIndex + 2).trim();
            }
        }
        
        String upperSql = actualSql.toUpperCase();
        
        // SQL 타입 확인 (주석 제거 후 실제 SQL로 판별)
        boolean isDmlOperation = upperSql.startsWith("UPDATE") || upperSql.startsWith("INSERT") || upperSql.startsWith("DELETE");
        boolean isSelectOperation = upperSql.startsWith("SELECT");
        
        String sqlType = isDmlOperation ? (upperSql.startsWith("UPDATE") ? "UPDATE" : 
                        upperSql.startsWith("INSERT") ? "INSERT" : "DELETE") : 
                        isSelectOperation ? "SELECT" : "OTHER";
        
        // 이미 우리가 추가한 주석이 있는지 확인
        String expectedComment = "/* " + comment + " */";
        if (trimmedSql.startsWith(expectedComment)) {
            if (isDmlOperation) {
                log.info("🔄 DML 쿼리 JDBC 재확인 - 타입: {}, 주석: {}", sqlType, comment);
                log.debug("{} SQL 확인: {}", sqlType, trimmedSql.length() > 100 ? trimmedSql.substring(0, 100) + "..." : trimmedSql);
            } else {
                log.debug("SELECT 주석 확인됨 - SQL: {}", trimmedSql.length() > 100 ? trimmedSql.substring(0, 100) + "..." : trimmedSql);
            }
            return trimmedSql;
        }
        
        // 기존 Hibernate 주석 제거 후 새 주석 추가
        String cleanedSql = removeHibernateComments(trimmedSql);
        String commentedSql = expectedComment + " " + cleanedSql;
        
        if (isDmlOperation) {
            log.info("✅ JDBC DML SQL 주석 강제 추가 완료 - 타입: {}, 주석: {}", sqlType, comment);
            log.debug("{} 원본: {}", sqlType, cleanedSql.length() > 100 ? cleanedSql.substring(0, 100) + "..." : cleanedSql);
            log.debug("{} 결과: {}", sqlType, commentedSql.length() > 100 ? commentedSql.substring(0, 100) + "..." : commentedSql);
        } else if (isSelectOperation) {
            log.info("✅ JDBC SELECT SQL 주석 추가 완료 - 주석: {}", comment);
            log.debug("SELECT 원본: {}", cleanedSql.length() > 100 ? cleanedSql.substring(0, 100) + "..." : cleanedSql);
            log.debug("SELECT 결과: {}", commentedSql.length() > 100 ? commentedSql.substring(0, 100) + "..." : commentedSql);
        }
        
        return commentedSql;
    }
    
    /**
     * Hibernate 기본 주석 제거
     */
    private static String removeHibernateComments(String sql) {
        if (sql == null) return "";
        
        String cleaned = sql;
        
        // 1. /* insert/update/select for entity ... */ 패턴
        cleaned = cleaned.replaceAll("/\\*\\s*(insert|update|select|delete)\\s+for\\s+entity[^*/]*\\*/\\s*", "");
        
        // 2. /* <criteria> */ 패턴  
        cleaned = cleaned.replaceAll("/\\*\\s*<criteria>\\s*\\*/\\s*", "");
        
        // 3. /* load ... */ 패턴
        cleaned = cleaned.replaceAll("/\\*\\s*load[^*/]*\\*/\\s*", "");
        
        return cleaned.trim();
    }
}