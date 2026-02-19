package com.skax.aiplatform.client.lablup.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup 세션 로그 조회 응답 DTO
 * 
 * <p>
 * Backend.AI 세션(컨테이너)의 로그 조회 결과를 담는 응답 데이터 구조입니다.
 * 실제 Lablup API 응답 구조에 맞춰 result 객체로 래핑되어 있습니다.
 * </p>
 * 
 * <h3>실제 API 응답 구조:</h3>
 * 
 * <pre>
 * {
 *   "result": {
 *     "logs": "Kernel started at: 2025-10-21T05:10:18+00:00\r\n..."
 *   }
 * }
 * </pre>
 * 
 * <h3>사용 예시:</h3>
 * 
 * <pre>
 * GetSessionLogResponse response = lablupSessionClient.getSessionLog(sessionId, ownerAccessKey, kernelId);
 * String logs = response.getResult().getLogs();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lablup 세션 로그 조회 응답 정보", example = """
        {
          "result": {
            "logs": "Kernel started at: 2025-10-21T05:10:18+00:00\\r\\nLOCAL_USER_ID=1100\\r\\n..."
          }
        }
        """)
public class GetSessionLogResponse {

    /**
     * 실제 로그 데이터를 포함하는 결과 객체
     * 
     * <p>
     * Lablup API의 실제 응답 구조에 맞춰 result 객체로 래핑되어 있습니다.
     * </p>
     */
    @Schema(description = "로그 조회 결과 데이터", example = """
            {
              "logs": "Kernel started at: 2025-10-21T05:10:18+00:00\\r\\nLOCAL_USER_ID=1100\\r\\n..."
            }
            """)
    private Result result;

    /**
     * 로그 조회 결과 데이터
     * 
     * <p>
     * 실제 컨테이너 로그 내용을 담는 내부 클래스입니다.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "로그 데이터")
    public static class Result {

        /**
         * 컨테이너 로그 내용
         * 
         * <p>
         * 세션(컨테이너)에서 생성된 전체 로그를 하나의 문자열로 제공합니다.
         * 각 로그 라인은 \r\n으로 구분되어 있습니다.
         * </p>
         * 
         * @implNote 실제 Lablup API는 로그를 문자열로 반환하며, 라인 구분자는 \r\n입니다.
         */
        @Schema(description = "컨테이너 로그 내용 (전체 문자열)", example = "Kernel started at: 2025-10-21T05:10:18+00:00\\r\\nLOCAL_USER_ID=1100\\r\\n...")
        private String logs;
    }
}