package com.skax.aiplatform.service.vertica;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Vertica 데이터베이스 샘플 서비스
 *
 * <p>Vertica 데이터베이스를 활용한 분석 쿼리 실행 예제입니다.
 * 실제 비즈니스 로직은 별도 서비스 클래스를 생성하여 구현하세요.</p>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * &#64;RestController
 * &#64;RequestMapping("/api/v1/analytics")
 * public class AnalyticsController {
 *     private final VerticaSampleService verticaSampleService;
 *     
 *     &#64;GetMapping("/test")
 *     public String testConnection() {
 *         return verticaSampleService.testConnection();
 *     }
 * }
 * </pre>
 *
 * <p>⚠️ 중요: dev 프로파일에서만 Bean이 생성됩니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-11-18
 */
@Slf4j
@Service
@ConditionalOnProperty(
    prefix = "vertica.datasource",
    name = "jdbc-url"
)
@ConditionalOnBean(name = "verticaJdbcTemplate")
public class VerticaSampleService {

    private final JdbcTemplate verticaJdbcTemplate;

    /**
     * VerticaSampleService 생성자
     * 
     * @param verticaJdbcTemplate Vertica 전용 JdbcTemplate
     */
    public VerticaSampleService(@Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
        log.info("✅ VerticaSampleService 초기화 완료 - JdbcTemplate: {}", verticaJdbcTemplate.getClass().getSimpleName());
    }

    /**
     * Vertica 데이터베이스 연결 테스트
     *
     * <p>간단한 SELECT 1 쿼리를 실행하여 데이터베이스 연결 상태를 확인합니다.</p>
     *
     * @return 연결 성공 메시지
     */
    public String testConnection() {
        List<Map<String, Object>> result = List.of();
        try {
            result  = verticaJdbcTemplate.queryForList("SELECT * from DM_BASE.dwa_queryone_user");
            log.info("✅ Vertica 연결 테스트 성공: {} rows", result.size());
            return "Vertica 연결 성공: " + result.toString();
        } catch (BadSqlGrammarException e) {
            log.error("SQL 문법 오류: query={}, err={}", result, e.getMessage(), e);
            throw e;

        } catch (CannotGetJdbcConnectionException e) {
            log.error("Vertica 연결 실패: err={}", e.getMessage(), e);
            throw e;

        } catch (DataAccessException e) {
            log.error("JDBC 처리 오류: query={}, err={}", result, e.getMessage(), e);
            throw e;

        }catch (Exception e) {
            log.error("❌ Vertica 연결 테스트 실패", e);
            throw new RuntimeException("Vertica 연결 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 현재 데이터베이스 버전 조회
     *
     * @return Vertica 버전 정보
     */
    public String getVersion() {
        String version = null;
        try {
            version = verticaJdbcTemplate.queryForObject(
                    "SELECT version()", String.class
            );
            log.info("Vertica 버전: {}", version);
            return version;
        } catch (BadSqlGrammarException e) {
            log.error("SQL 문법 오류: query={}, err={}", version, e.getMessage(), e);
            throw e;

        } catch (CannotGetJdbcConnectionException e) {
            log.error("Vertica 연결 실패: err={}", e.getMessage(), e);
            throw e;

        } catch (DataAccessException e) {
            log.error("JDBC 처리 오류: query={}, err={}", version, e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            log.error("Vertica 버전 조회 실패", e);
            throw new RuntimeException("Vertica 버전 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 현재 세션 정보 조회
     *
     * @return 세션 정보 목록
     */
    public List<Map<String, Object>> getSessionInfo() {
        List<Map<String, Object>> sessions = List.of();
        try {
            sessions = verticaJdbcTemplate.queryForList(
                    "SELECT session_id, user_name, client_hostname, client_type " +
                            "FROM v_monitor.current_session"
            );
            log.info("현재 세션 수: {}", sessions.size());
            return sessions;
        } catch (BadSqlGrammarException e) {
            log.error("SQL 문법 오류: query={}, err={}", sessions, e.getMessage(), e);
            throw e;

        } catch (CannotGetJdbcConnectionException e) {
            log.error("Vertica 연결 실패: err={}", e.getMessage(), e);
            throw e;

        } catch (DataAccessException e) {
            log.error("JDBC 처리 오류: query={}, err={}", sessions, e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            log.error("세션 정보 조회 실패", e);
            throw new RuntimeException("세션 정보 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 샘플 분석 쿼리 실행
     *
     * <p>실제 분석 쿼리는 각 도메인 서비스에서 구현하세요.</p>
     *
     * @param query 실행할 SQL 쿼리
     * @return 쿼리 실행 결과
     */
    public List<Map<String, Object>> executeAnalyticsQuery(String query) {
        try {
            log.info("분석 쿼리 실행: {}", query);
            List<Map<String, Object>> results = verticaJdbcTemplate.queryForList(query);
            log.info("쿼리 결과 행 수: {}", results.size());
            return results;
        } catch (BadSqlGrammarException e) {
            log.error("SQL 문법 오류: query={}, err={}", query, e.getMessage(), e);
            throw e;

        } catch (CannotGetJdbcConnectionException e) {
            log.error("Vertica 연결 실패: err={}", e.getMessage(), e);
            throw e;

        } catch (DataAccessException e) {
            log.error("JDBC 처리 오류: query={}, err={}", query, e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            log.error("분석 쿼리 실행 실패: {}", query, e);
            throw new RuntimeException("쿼리 실행 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 테이블 존재 여부 확인
     *
     * @param schemaName 스키마명
     * @param tableName 테이블명
     * @return 테이블 존재 여부
     */
    public boolean tableExists(String schemaName, String tableName) {
        Integer count = 0;
        try {
            count = verticaJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM v_catalog.tables " +
                            "WHERE table_schema = ? AND table_name = ?",
                    Integer.class,
                    schemaName,
                    tableName
            );
            boolean exists = count != null && count > 0;
            log.info("테이블 존재 여부 - {}.{}: {}", schemaName, tableName, exists);
            return exists;
        } catch (BadSqlGrammarException e) {
            log.error("SQL 문법 오류: query={}, err={}", count, e.getMessage(), e);
            throw e;

        } catch (CannotGetJdbcConnectionException e) {
            log.error("Vertica 연결 실패: err={}", e.getMessage(), e);
            throw e;

        } catch (DataAccessException e) {
            log.error("JDBC 처리 오류: query={}, err={}", count, e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            log.error("테이블 존재 확인 실패: {}.{}", schemaName, tableName, e);
            return false;
        }
    }

    /**
     * 테이블의 행 수 조회
     *
     * @param schemaName 스키마명
     * @param tableName 테이블명
     * @return 행 수
     */
    public Long getRowCount(String schemaName, String tableName) {
        String query = null;
        try {
            query = String.format("SELECT COUNT(*) FROM %s.%s", schemaName, tableName);
            Long count = verticaJdbcTemplate.queryForObject(query, Long.class);
            log.info("테이블 행 수 - {}.{}: {}", schemaName, tableName, count);
            return count;
        } catch (BadSqlGrammarException e) {
            log.error("SQL 문법 오류: query={}, err={}", query, e.getMessage(), e);
            throw e;

        } catch (CannotGetJdbcConnectionException e) {
            log.error("Vertica 연결 실패: err={}", e.getMessage(), e);
            throw e;

        } catch (DataAccessException e) {
            log.error("JDBC 처리 오류: query={}, err={}", query, e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            log.error("행 수 조회 실패: {}.{}", schemaName, tableName, e);
            throw new RuntimeException("행 수 조회 실패: " + e.getMessage(), e);
        }
    }
}
