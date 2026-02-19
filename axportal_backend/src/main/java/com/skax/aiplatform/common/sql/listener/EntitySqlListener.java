package com.skax.aiplatform.common.sql.listener;

import com.skax.aiplatform.common.sql.SqlCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

/**
 * JPA Entity 이벤트 리스너
 * 엔티티 수준에서 SQL 작업을 추적하고 로깅합니다.
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 */
@Slf4j
@Component
public class EntitySqlListener {
    
    @PrePersist
    public void prePersist(Object entity) {
        logSqlOperation("INSERT", entity, "시작");
    }
    
    @PostPersist
    public void postPersist(Object entity) {
        logSqlOperation("INSERT", entity, "완료");
    }
    
    @PreUpdate
    public void preUpdate(Object entity) {
        logSqlOperation("UPDATE", entity, "시작");
    }
    
    @PostUpdate
    public void postUpdate(Object entity) {
        logSqlOperation("UPDATE", entity, "완료");
    }
    
    @PreRemove
    public void preRemove(Object entity) {
        logSqlOperation("DELETE", entity, "시작");
    }
    
    @PostRemove
    public void postRemove(Object entity) {
        logSqlOperation("DELETE", entity, "완료");
    }
    
    @PostLoad
    public void postLoad(Object entity) {
        logSqlOperation("SELECT", entity, "완료");
    }
    
    /**
     * SQL 작업 로깅
     */
    private void logSqlOperation(String operation, Object entity, String phase) {
        try {
            String entityName = entity.getClass().getSimpleName();
            String comment = SqlCommentContext.getCurrentComment();
            
            if (comment != null && !comment.trim().isEmpty()) {
                String tableName = getTableName(entityName);
                
                log.info("🔍 JPA Entity 이벤트 - 작업: {}, 엔티티: {}, 테이블: {}, 단계: {}, 컨텍스트: {}", 
                    operation, entityName, tableName, phase, comment);
                
                // UPDATE/INSERT 작업에 대해 추가 정보 제공
                if (("INSERT".equals(operation) || "UPDATE".equals(operation)) && "완료".equals(phase)) {
                    log.info("✅ {} 작업 완료 - 테이블: {}, 컨텍스트: {}", operation, tableName, comment);
                }
            }
            
        } catch (NullPointerException e) {
            log.debug("Entity 이벤트 로깅 실패 (NullPointerException) - 엔티티 정보 누락: {}", operation);
        } catch (IllegalArgumentException e) {
            log.debug("Entity 이벤트 로깅 실패 (IllegalArgumentException) - 잘못된 엔티티 정보: {}", operation);
        } catch (Exception e) {
            log.debug("Entity 이벤트 로깅 실패 (예상치 못한 오류) - 작업: {}", operation, e);
        }
    }
    
    /**
     * 엔티티명으로부터 테이블명 추출
     */
    private String getTableName(String entityName) {
        try {
            // 엔티티명 -> 테이블명 변환 로직
            if (entityName.startsWith("Gpo")) {
                // GpoUsersMas -> gpo_users_mas
                return entityName.replaceAll("([A-Z])", "_$1")
                               .toLowerCase()
                               .replaceFirst("^_", "");
            }
            
            // 일반적인 카멜케이스 -> 스네이크케이스 변환
            return entityName.replaceAll("([A-Z])", "_$1")
                            .toLowerCase()
                            .replaceFirst("^_", "");
        } catch (NullPointerException e) {
            log.debug("테이블명 변환 실패 (NullPointerException) - entityName이 null");
            return "unknown_table";
        } catch (java.util.regex.PatternSyntaxException e) {
            log.debug("테이블명 변환 실패 (PatternSyntaxException) - 정규식 오류: {}", entityName);
            return entityName.toLowerCase();
        } catch (Exception e) {
            log.debug("테이블명 변환 실패 (예상치 못한 오류) - entityName: {}", entityName, e);
            return entityName != null ? entityName.toLowerCase() : "unknown_table";
        }
    }
}