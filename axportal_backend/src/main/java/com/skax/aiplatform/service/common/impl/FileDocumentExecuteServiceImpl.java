package com.skax.aiplatform.service.common.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.dto.common.request.FileDocumentRequest;
import com.skax.aiplatform.dto.common.response.FileDocumentResponse;
import com.skax.aiplatform.service.common.FileDocumentExecuteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 파일 다큐먼트 실행 서비스 구현체
 * 
 * <p>
 * 파일 다큐먼트를 안전하게 실행하고 결과를 반환합니다.
 * </p>
 * 
 * @author Generated
 * @since 2025-01-XX
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDocumentExecuteServiceImpl implements FileDocumentExecuteService {

    private final JdbcTemplate jdbcTemplate;

    // 허용되지 않는 파일 다큐먼트 키워드
    private static final Set<String> FORBIDDEN_KEYWORDS = Set.of(
            "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER",
            "TRUNCATE", "GRANT", "REVOKE", "EXEC", "EXECUTE", "CALL");

    // 허용되지 않는 파일 다큐먼트 키워드 (대소문자 구분 없이)
    private static final Set<String> FORBIDDEN_KEYWORDS_LOWER = new HashSet<>();

    static {
        FORBIDDEN_KEYWORDS.forEach(keyword -> FORBIDDEN_KEYWORDS_LOWER.add(keyword.toLowerCase()));
    }

    // 기본 설정값
    private static final int DEFAULT_MAX_ROWS = 1000;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    @Override
    @Transactional(readOnly = true)
    public FileDocumentResponse executeFileDocument(FileDocumentRequest request) {
        long startTime = System.currentTimeMillis();
        String receivedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            String fileDocument = request.getFileDocument().trim();

            // 1. 파일 다큐먼트 검증
            validateFileDocument(fileDocument);

            // 2. 타임아웃 설정
            int timeout = DEFAULT_TIMEOUT_SECONDS;
            jdbcTemplate.setQueryTimeout(timeout);

            // 3. 파일 다큐먼트 실행 및 결과 조회
            List<Map<String, Object>> resultList = new ArrayList<>();
            List<String> columns = new ArrayList<>();

            // RowMapper를 사용하여 결과 처리
            List<Map<String, Object>> allResults = jdbcTemplate.query(fileDocument,
                    new RowMapper<Map<String, Object>>() {
                        private boolean firstRow = true;

                        @Override
                        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
                            // 첫 번째 행에서 컬럼 정보 추출
                            if (firstRow) {
                                ResultSetMetaData metaData = rs.getMetaData();
                                int columnCount = metaData.getColumnCount();
                                for (int i = 1; i <= columnCount; i++) {
                                    columns.add(metaData.getColumnLabel(i));
                                }
                                firstRow = false;
                            }

                            // 행 데이터 추출
                            Map<String, Object> row = new LinkedHashMap<>();
                            for (String column : columns) {
                                row.put(column, rs.getObject(column));
                            }
                            return row;
                        }
                    });

            // 최대 행 수 제한
            int maxRows = DEFAULT_MAX_ROWS;
            if (allResults.size() > maxRows) {
                resultList = allResults.subList(0, maxRows);
                log.warn("⚠️ 결과 행 수가 제한을 초과했습니다. {}행 중 {}행만 반환합니다.",
                        allResults.size(), maxRows);
            } else {
                resultList = allResults;
            }

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("✅ 파일 다큐먼트 실행 성공 - 실행 시간: {}ms, 결과 행 수: {}", executionTime, resultList.size());

            return FileDocumentResponse.builder()
                    .receivedSql(fileDocument)
                    .receivedAt(receivedAt)
                    .sqlLength(fileDocument.length())
                    .message("파일 다큐먼트를 성공적으로 실행했습니다.")
                    .data(resultList)
                    .columns(columns)
                    .rowCount(resultList.size())
                    .executionTimeMs(executionTime)
                    .executedSql(fileDocument)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ 파일 다큐먼트 검증 실패: {}", e.getMessage());
            throw e;
        } catch (BadSqlGrammarException e) {
            log.error("❌ 파일 다큐먼트 문법 오류: {}", e.getMessage(), e);
            throw new RuntimeException("파일 다큐먼트 문법 오류: " + e.getMessage(), e);
        } catch (CannotGetJdbcConnectionException e) {
            log.error("❌ 데이터베이스 연결 실패: {}", e.getMessage(), e);
            throw new RuntimeException("데이터베이스 연결 실패: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("❌ 파일 다큐먼트 실행 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 다큐먼트 실행 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ 파일 다큐먼트 실행 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("파일 다큐먼트 실행 중 오류가 발생했습니다: " + e.getMessage(), e);
        } finally {
            // 타임아웃 초기화
            jdbcTemplate.setQueryTimeout(0);
        }
    }

    /**
     * 파일 다큐먼트 검증
     * 
     * <p>
     * 위험한 키워드를 포함한 파일 다큐먼트는 거부합니다.
     * </p>
     * 
     * @param 검증할 파일 다큐먼트
     * @throws IllegalArgumentException 잘못된 파일 다큐먼트인 경우
     */
    private void validateFileDocument(String fileDocument) {
        if (fileDocument == null || fileDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 다큐먼트가 비어있습니다.");
        }

        String fileDocumentUpper = fileDocument.toUpperCase().trim();

        // SELECT로 시작하는지 확인
        if (!fileDocumentUpper.startsWith("SELECT")) {
            throw new IllegalArgumentException("S 파일 다큐먼트만 실행 가능합니다.");
        }

        // 위험한 키워드 포함 여부 확인
        String[] words = fileDocumentUpper.split("\\s+");
        for (String word : words) {
            // 세미콜론 제거 후 확인
            String cleanWord = word.replaceAll("[;]", "");
            if (FORBIDDEN_KEYWORDS_LOWER.contains(cleanWord.toLowerCase())) {
                throw new IllegalArgumentException(
                        String.format("허용되지 않는 파일 다큐먼트 키워드가 포함되어 있습니다: %s", cleanWord));
            }
        }

        // 세미콜론으로 구분된 여러 파일 다큐먼트 실행 방지
        String[] statements = fileDocument.split(";");
        if (statements.length > 2 || (statements.length == 2 && !statements[1].trim().isEmpty())) {
            throw new IllegalArgumentException("여러 파일 다큐먼트를 한 번에 실행할 수 없습니다.");
        }

        // 주석을 통한 우회 시도 방지 (기본적인 검증)
        if (fileDocument.contains("--") || fileDocument.contains("/*")) {
            log.warn("⚠️ 파일 다큐먼트에 주석이 포함되어 있습니다: {}", fileDocument);
        }
    }
}
