package com.skax.aiplatform.controller.sample;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.service.vertica.VerticaSampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Vertica 테스트 컨트롤러
 *
 * <p>Vertica 데이터베이스 연결 및 기본 쿼리 실행 테스트를 위한 컨트롤러입니다.
 * 개발 환경(elocal, edev)에서만 활성화됩니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-11-18
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vertica")
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "vertica.datasource",
    name = "jdbc-url"
)
@ConditionalOnBean(VerticaSampleService.class)
@Tag(name = "Vertica Test", description = "Vertica 데이터베이스 테스트 API")
public class VerticaTestController {

    private final VerticaSampleService verticaSampleService;

    /**
     * Vertica 연결 테스트
     *
     * @return 연결 테스트 결과
     */
    @GetMapping("/test")
    @Operation(summary = "Vertica 연결 테스트", description = "Vertica 데이터베이스 연결 상태를 확인합니다.")
    public AxResponseEntity<String> testConnection() {
        log.info("🔍 Vertica 연결 테스트 요청");
        String result = verticaSampleService.testConnection();
        return AxResponseEntity.ok(result, "Vertica 연결 테스트 성공");
    }

    /**
     * Vertica 버전 조회
     *
     * @return Vertica 버전 정보
     */
    @GetMapping("/version")
    @Operation(summary = "Vertica 버전 조회", description = "Vertica 데이터베이스 버전을 조회합니다.")
    public AxResponseEntity<String> getVersion() {
        log.info("🔍 Vertica 버전 조회 요청");
        String version = verticaSampleService.getVersion();
        return AxResponseEntity.ok(version, "Vertica 버전 조회 성공");
    }

    /**
     * 현재 세션 정보 조회
     *
     * @return 세션 정보 목록
     */
    @GetMapping("/sessions")
    @Operation(summary = "세션 정보 조회", description = "현재 Vertica 데이터베이스 세션 정보를 조회합니다.")
    public AxResponseEntity<List<Map<String, Object>>> getSessionInfo() {
        log.info("🔍 Vertica 세션 정보 조회 요청");
        List<Map<String, Object>> sessions = verticaSampleService.getSessionInfo();
        return AxResponseEntity.ok(sessions, "세션 정보 조회 성공");
    }

    /**
     * 테이블 존재 여부 확인
     *
     * @param schema 스키마명
     * @param table 테이블명
     * @return 테이블 존재 여부
     */
    @GetMapping("/table-exists")
    @Operation(summary = "테이블 존재 확인", description = "지정된 테이블의 존재 여부를 확인합니다.")
    public AxResponseEntity<Boolean> checkTableExists(
            @RequestParam String schema,
            @RequestParam String table) {
        log.info("🔍 테이블 존재 확인 요청 - schema: {}, table: {}", schema, table);
        boolean exists = verticaSampleService.tableExists(schema, table);
        return AxResponseEntity.ok(exists, "테이블 존재 여부 확인 성공");
    }

    /**
     * 테이블 행 수 조회
     *
     * @param schema 스키마명
     * @param table 테이블명
     * @return 행 수
     */
    @GetMapping("/row-count")
    @Operation(summary = "테이블 행 수 조회", description = "지정된 테이블의 전체 행 수를 조회합니다.")
    public AxResponseEntity<Long> getRowCount(
            @RequestParam String schema,
            @RequestParam String table) {
        log.info("🔍 테이블 행 수 조회 요청 - schema: {}, table: {}", schema, table);
        Long count = verticaSampleService.getRowCount(schema, table);
        return AxResponseEntity.ok(count, "테이블 행 수 조회 성공");
    }
}
