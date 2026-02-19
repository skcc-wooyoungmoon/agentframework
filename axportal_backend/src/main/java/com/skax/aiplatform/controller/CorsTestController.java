package com.skax.aiplatform.controller;

import com.skax.aiplatform.common.response.AxResponse;
import com.skax.aiplatform.common.response.AxResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * CORS 테스트 컨트롤러
 * 
 * <p>CORS 설정이 올바르게 작동하는지 테스트하기 위한 컨트롤러입니다.
 * 완전 개방형 CORS 설정으로 모든 접근을 허용합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-12
 * @version 2.0.0 - 완전 개방형 CORS 설정 적용
 */
@Slf4j
@RestController
@RequestMapping("/cors")
@Tag(name = "CORS Test", description = "CORS 설정 테스트 API")
public class CorsTestController {

    /**
     * CORS GET 테스트
     * 
     * @return CORS 테스트 결과
     */
    @GetMapping("/test")
    @Operation(summary = "CORS GET 테스트", description = "GET 메서드 CORS 테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CORS 테스트 성공"),
            @ApiResponse(responseCode = "403", description = "CORS 정책 위반")
    })
    public AxResponseEntity<Map<String, Object>> testGet() {
        log.info("CORS GET Test Request");

        Map<String, Object> testData = Map.of(
                "method", "GET",
                "message", "CORS GET Test Success",
                "timestamp", LocalDateTime.now(),
                "headers", "All headers allowed"
        );

        return AxResponseEntity.ok(testData, "CORS GET Test Success");
    }

    /**
     * CORS POST 테스트
     * 
     * @param request 요청 데이터
     * @return CORS 테스트 결과
     */
    @PostMapping("/test")
    @Operation(summary = "CORS POST 테스트", description = "POST 메서드 CORS 테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CORS 테스트 성공"),
            @ApiResponse(responseCode = "403", description = "CORS 정책 위반")
    })
    public AxResponseEntity<Map<String, Object>> testPost(@RequestBody Map<String, Object> request) {
        log.info("CORS POST Test Request: {}", request);

        Map<String, Object> testData = Map.of(
                "method", "POST",
                "message", "CORS POST Test Success",
                "timestamp", LocalDateTime.now(),
                "receivedData", request,
                "headers", "All headers allowed"
        );

        return AxResponseEntity.ok(testData, "CORS POST Test Success");
    }

    /**
     * CORS PUT 테스트
     * 
     * @param request 요청 데이터
     * @return CORS 테스트 결과
     */
    @PutMapping("/test")
    @Operation(summary = "CORS PUT 테스트", description = "PUT 메서드 CORS 테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CORS 테스트 성공"),
            @ApiResponse(responseCode = "403", description = "CORS 정책 위반")
    })
    public AxResponseEntity<Map<String, Object>> testPut(@RequestBody Map<String, Object> request) {
        log.info("CORS PUT Test Request: {}", request);

        Map<String, Object> testData = Map.of(
                "method", "PUT",
                "message", "CORS PUT Test Success",
                "timestamp", LocalDateTime.now(),
                "receivedData", request
        );

        return AxResponseEntity.ok(testData, "CORS PUT Test Success");
    }

    /**
     * CORS DELETE method test
     * 
     * @return CORS test result
     */
    @DeleteMapping("/test")
    @Operation(summary = "CORS DELETE Test", description = "DELETE method CORS test")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CORS Test Success"),
            @ApiResponse(responseCode = "403", description = "CORS Policy Violation")
    })
    public AxResponseEntity<Map<String, Object>> testDelete() {
        log.info("CORS DELETE Test Request");

        Map<String, Object> testData = Map.of(
                "method", "DELETE",
                "message", "CORS DELETE Test Success",
                "timestamp", LocalDateTime.now()
        );

        return AxResponseEntity.ok(testData, "CORS DELETE Test Success");
    }

    /**
     * CORS custom header test
     * 
     * @param customHeader custom header value
     * @param anotherHeader another custom header value
     * @return CORS test result
     */
    @GetMapping("/headers")
    @Operation(summary = "CORS Custom Header Test", description = "Custom header CORS test")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CORS Header Test Success"),
            @ApiResponse(responseCode = "403", description = "CORS Policy Violation")
    })
    public ResponseEntity<AxResponse<Map<String, Object>>> testHeaders(
            @RequestHeader(value = "X-Custom-Header", required = false) String customHeader,
            @RequestHeader(value = "X-Another-Header", required = false) String anotherHeader) {
        
        log.info("CORS header test request - X-Custom-Header: {}, X-Another-Header: {}", 
                customHeader, anotherHeader);

        Map<String, Object> testData = Map.of(
                "method", "GET",
                "message", "CORS Custom Header Test Success",
                "timestamp", LocalDateTime.now(),
                "customHeader", customHeader != null ? customHeader : "None",
                "anotherHeader", anotherHeader != null ? anotherHeader : "None"
        );

        return ResponseEntity.ok()
                .header("X-Response-Header", "CORS-Test-Response-Header")
                .header("X-User-Id", "test-user-123")
                .body(AxResponse.success(testData, "CORS Custom Header Test Success"));
    }

    /**
     * CORS Preflight test OPTIONS handler
     * 
     * @return empty response (handled automatically by browser)
     */
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    @Operation(summary = "CORS Preflight", description = "CORS Preflight request handler")
    public ResponseEntity<Void> handlePreflight() {
        log.info("CORS Preflight request handling");
        return ResponseEntity.ok().build();
    }
}
