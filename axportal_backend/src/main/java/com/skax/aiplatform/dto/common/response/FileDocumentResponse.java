package com.skax.aiplatform.dto.common.response;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 응답 DTO
 * 
 * <p>
 * 수신 및 실행 결과를 담는 응답 DTO입니다.
 * </p>
 * 
 * @author Generated
 * @since 2025-01-XX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "fileDocument 응답")
public class FileDocumentResponse {

    @Schema(description = "수신된 fileDocument", example = "SELECT * FROM users LIMIT 10")
    private String receivedSql;

    @Schema(description = "수신 시간", example = "2025-01-14T12:00:00")
    private String receivedAt;

    @Schema(description = "fileDocument 길이", example = "30")
    private Integer sqlLength;

    @Schema(description = "처리 상태 메시지", example = "fileDocument 를 성공적으로 수신했습니다.")
    private String message;

    // 실행 결과 필드
    @Schema(description = "fileDocument 실행 결과 데이터 (행 리스트)", example = "[{\"id\": 1, \"name\": \"John\"}]")
    private List<Map<String, Object>> data;

    @Schema(description = "컬럼명 리스트", example = "[\"id\", \"name\", \"email\"]")
    private List<String> columns;

    @Schema(description = "조회된 행 수", example = "10")
    private Integer rowCount;

    @Schema(description = "실행 시간 (밀리초)", example = "150")
    private Long executionTimeMs;

    @Schema(description = "실행된 fileDocument", example = "SELECT * FROM users LIMIT 10")
    private String executedSql;
}
