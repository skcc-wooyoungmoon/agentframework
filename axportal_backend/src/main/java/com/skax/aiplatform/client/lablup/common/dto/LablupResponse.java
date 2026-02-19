package com.skax.aiplatform.client.lablup.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup API 공통 응답 DTO
 * 
 * <p>Lablup API로부터 받은 응답 데이터를 담는 구조입니다.
 * 모든 Lablup API 응답에 공통으로 사용되는 포맷을 정의합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>success</strong>: 요청 성공 여부</li>
 *   <li><strong>message</strong>: 응답 메시지</li>
 *   <li><strong>data</strong>: 실제 응답 데이터</li>
 *   <li><strong>error</strong>: 에러 정보 (실패 시)</li>
 * </ul>
 *
 * @param <T> 응답 데이터 타입
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Lablup API 공통 응답 정보",
    example = """
        {
          "success": true,
          "message": "요청이 성공적으로 처리되었습니다",
          "data": {},
          "error": null
        }
        """
)
public class LablupResponse<T> {
    
    /**
     * 요청 성공 여부
     * 
     * <p>API 요청이 성공적으로 처리되었는지를 나타냅니다.</p>
     */
    @JsonProperty("success")
    @Schema(description = "요청 성공 여부", example = "true")
    private Boolean success;
    
    /**
     * 응답 메시지
     * 
     * <p>API 요청 처리 결과에 대한 메시지입니다.</p>
     */
    @JsonProperty("message")
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다")
    private String message;
    
    /**
     * 응답 데이터
     * 
     * <p>API 요청에 대한 실제 응답 데이터입니다.</p>
     */
    @JsonProperty("data")
    @Schema(description = "응답 데이터")
    private T data;
    
    /**
     * 에러 정보
     * 
     * <p>요청 실패 시 에러에 대한 상세 정보입니다.</p>
     */
    @JsonProperty("error")
    @Schema(description = "에러 정보 (실패 시)")
    private String error;
}