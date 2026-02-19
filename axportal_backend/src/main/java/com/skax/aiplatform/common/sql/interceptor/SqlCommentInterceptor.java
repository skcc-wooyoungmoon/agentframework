package com.skax.aiplatform.common.sql.interceptor;

import com.skax.aiplatform.common.sql.SqlCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

/**
 * SQL 쿼리에 주석을 추가하는 Hibernate StatementInspector
 * 
 * <p>
 * 실행되는 SQL 쿼리에 ServiceImpl.method.Repository.method 형식의 주석을 자동으로 추가합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Slf4j
@Component
public class SqlCommentInterceptor implements StatementInspector {

    /**
     * SQL 쿼리 실행 전 주석을 추가하여 수정된 쿼리를 반환
     * 
     * @param sql 원본 SQL 쿼리
     * @return 주석이 추가된 SQL 쿼리
     */
    @Override
    public String inspect(String sql) {
        String sqlType = getSqlType(sql);
        String currentComment = SqlCommentContext.getCurrentComment();

        // UPDATE/INSERT의 경우 Entity 기반으로 Repository 추론 시도
        if (("UPDATE".equals(sqlType) || "INSERT".equals(sqlType)) && currentComment != null) {
            String entityBasedComment = inferRepositoryFromSql(sql, currentComment);
            if (entityBasedComment != null) {
                currentComment = entityBasedComment;
            }
        }

        if (currentComment != null && !currentComment.trim().isEmpty()) {
            // Hibernate 기본 주석을 완전히 제거하고 커스텀 주석으로 교체
            String cleanedSql = removeHibernateComments(sql);
            String commentedSql = String.format("/* %s */ %s", currentComment, cleanedSql);

            log.info("SQL Comment: {} [{}]", currentComment, sqlType);
            return commentedSql;
        }

        log.warn("⚠️ SQL 주석 컨텍스트 없음 [{}]", sqlType);
        return sql;
    }

    /**
     * SQL 타입을 확인 (SELECT, INSERT, UPDATE, DELETE)
     * 
     * @param sql SQL 쿼리
     * @return SQL 타입
     */
    private String getSqlType(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "UNKNOWN";
        }

        String trimmedSql = sql.trim().toUpperCase();
        if (trimmedSql.startsWith("SELECT"))
            return "SELECT";
        if (trimmedSql.startsWith("INSERT"))
            return "INSERT";
        if (trimmedSql.startsWith("UPDATE"))
            return "UPDATE";
        if (trimmedSql.startsWith("DELETE"))
            return "DELETE";

        return "OTHER";
    }

    /**
     * Hibernate가 자동으로 생성하는 주석을 제거
     * 
     * @param sql 원본 SQL
     * @return 주석이 제거된 SQL
     */
    private String removeHibernateComments(String sql) {
        if (sql == null) {
            return sql;
        }

        String result = sql;

        // Hibernate 자동 주석 패턴 제거
        result = result.replaceAll("/\\*\\s*insert\\s+for\\s+[^*/]+\\*/", "");
        result = result.replaceAll("/\\*\\s*update\\s+for\\s+[^*/]+\\*/", "");
        result = result.replaceAll("/\\*\\s*delete\\s+for\\s+[^*/]+\\*/", "");
        result = result.replaceAll("/\\*\\s*select\\s+for\\s+[^*/]+\\*/", "");
        result = result.replaceAll("/\\*\\s*[^*/]*entity[^*/]*\\*/", "");

        // 연속된 공백 정리
        return result.replaceAll("\\s+", " ").trim();
    }

    /**
     * SQL에서 Entity 정보를 추출하여 해당하는 Repository 추론
     * 
     * @param sql             SQL 쿼리
     * @param originalComment 원본 주석
     * @return 추론된 Repository 주석
     */
    private String inferRepositoryFromSql(String sql, String originalComment) {
        try {
            // 1. Hibernate 주석에서 Entity 클래스명 추출
            String entityClass = extractEntityFromHibernateComment(sql);
            if (entityClass != null) {
                String repositoryName = inferRepositoryName(entityClass);
                if (repositoryName != null) {
                    // Service 컨텍스트는 유지하고 Repository만 교체
                    String serviceContext = SqlCommentContext.getServiceContext();
                    if (serviceContext != null) {
                        return serviceContext + "." + repositoryName + ".save";
                    }
                }
            }

            // 2. 테이블명에서 직접 Repository 추론
            String tableName = extractTableNameFromSql(sql);
            if (tableName != null) {
                String repositoryName = inferRepositoryFromTableName(tableName);
                if (repositoryName != null) {
                    String serviceContext = SqlCommentContext.getServiceContext();
                    if (serviceContext != null) {
                        String operation = getSqlType(sql).toLowerCase();
                        return serviceContext + "." + repositoryName + "." + operation;
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.debug("Repository 추론 실패 (잘못된 입력값)");
        } catch (Exception e) {
            log.debug("Repository 추론 실패 (예상치 못한 오류)");
        }

        return originalComment;
    }

    /**
     * Hibernate 주석에서 Entity 클래스명 추출
     * 
     * @param sql SQL 쿼리
     * @return Entity 클래스명
     */
    private String extractEntityFromHibernateComment(String sql) {
        // /* update for com.skax.aiplatform.entity.GpoUsersMas */ 패턴에서 추출
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "/\\*\\s*(?:insert|update|delete)\\s+for\\s+([^*/]+)\\*/");
        java.util.regex.Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String fullEntityName = matcher.group(1).trim();
            // 클래스명만 추출 (패키지 제거)
            return fullEntityName.substring(fullEntityName.lastIndexOf('.') + 1);
        }

        return null;
    }

    /**
     * Entity 클래스명으로부터 Repository명 추론
     * 
     * @param entityClassName Entity 클래스명
     * @return Repository명
     */
    private String inferRepositoryName(String entityClassName) {
        // Entity명에서 Repository명 생성 규칙
        if (entityClassName.endsWith("Mas")) {
            // GpoUsersMas -> MemberRepository
            // GpoTokensMas -> TokenRepository
            String baseName = entityClassName.replace("Gpo", "").replace("Mas", "");

            // 특별한 매핑 규칙
            if ("Users".equals(baseName)) {
                return "MemberRepository";
            } else if ("Tokens".equals(baseName)) {
                return "TokenRepository";
            } else if ("Prjuserrole".equals(baseName)) {
                return "GpoPrjuserroleRepository";
            } else {
                return baseName + "Repository";
            }
        } else if (entityClassName.startsWith("Gpo")) {
            // GpoUsers -> MemberRepository
            String baseName = entityClassName.replace("Gpo", "");
            if ("Users".equals(baseName)) {
                return "MemberRepository";
            } else if ("Tokens".equals(baseName)) {
                return "TokenRepository";
            } else {
                return baseName + "Repository";
            }
        } else {
            // 기본 규칙: Entity -> EntityRepository
            return entityClassName + "Repository";
        }
    }

    /**
     * SQL에서 테이블명 추출
     * 
     * @param sql SQL 쿼리
     * @return 테이블명
     */
    private String extractTableNameFromSql(String sql) {
        try {
            String upperSql = sql.toUpperCase().trim();

            if (upperSql.startsWith("UPDATE")) {
                // UPDATE table_name SET ... 패턴
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                        "UPDATE\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+SET",
                        java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(sql);
                if (matcher.find()) {
                    return matcher.group(1).toLowerCase();
                }
            } else if (upperSql.startsWith("INSERT")) {
                // INSERT INTO table_name ... 패턴
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                        "INSERT\\s+INTO\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
                        java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(sql);
                if (matcher.find()) {
                    return matcher.group(1).toLowerCase();
                }
            } else if (upperSql.startsWith("DELETE")) {
                // DELETE FROM table_name ... 패턴
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                        "DELETE\\s+FROM\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
                        java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(sql);
                if (matcher.find()) {
                    return matcher.group(1).toLowerCase();
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.debug("테이블명 추출 실패 (잘못된 입력값)");
        } catch (Exception e) {
            log.debug("테이블명 추출 실패 (예상치 못한 오류)");
        }

        return null;
    }

    /**
     * 테이블명으로부터 Repository명 추론
     * 
     * @param tableName 테이블명
     * @return Repository명
     */
    private String inferRepositoryFromTableName(String tableName) {
        if (tableName == null) {
            return null;
        }

        // 테이블명 기반 Repository 매핑
        switch (tableName.toLowerCase()) {
            case "gpo_users_mas":
                return "MemberRepository";
            case "gpo_tokens_mas":
                return "TokenRepository";
            case "gpo_prjuserrole_mas":
                return "GpoPrjuserroleRepository";
            case "gpo_grpco_jkw_mas":
                return "MemberRepository"; // 조직정보도 MemberRepository에서 관리
            default:
                // 기본 패턴 변환: gpo_table_mas -> TableRepository
                String cleanName = tableName.replace("gpo_", "").replace("_mas", "");
                String[] parts = cleanName.split("_");
                StringBuilder repoName = new StringBuilder();
                for (String part : parts) {
                    repoName.append(Character.toUpperCase(part.charAt(0)))
                            .append(part.substring(1).toLowerCase());
                }
                return repoName.toString() + "Repository";
        }
    }
}