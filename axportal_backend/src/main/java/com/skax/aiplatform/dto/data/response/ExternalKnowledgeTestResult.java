package com.skax.aiplatform.dto.data.response;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External Knowledge 테스트 결과 DTO.
 *
 * <p>ADXP External Knowledge 테스트 API 응답을 단순화하여
 * 프런트엔드에 전달하기 위한 구조입니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "External Knowledge 테스트 결과")
public class ExternalKnowledgeTestResult {

    /**
     * 테스트 성공 여부.
     */
    @Schema(description = "테스트 성공 여부", example = "true")
    private boolean success;

    /**
     * ADXP에서 내려주는 상태 문자열 (success / error 등).
     */
    @Schema(description = "ADXP 응답 상태", example = "success")
    private String status;

    /**
     * 사용자에게 노출할 메시지.
     */
    @Schema(description = "사용자 안내 메시지", example = "External Repository 테스트가 완료되었습니다.")
    private String message;

    /**
     * ADXP detail 필드 원본 (성공 시 리스트, 실패 시 문자열 등).
     */
    @Schema(description = "ADXP detail 필드 원본")
    private Object detail;

    /**
     * detail이 문자열 형태인지 여부를 손쉽게 판별하기 위한 헬퍼.
     *
     * @return detail이 문자열이면 true
     */
    public boolean isDetailString() {
        return detail instanceof CharSequence;
    }

    /**
     * detail 문자열을 안전하게 반환 (문자열이 아니면 null).
     */
    public String detailAsString() {
        return isDetailString() ? Objects.toString(detail, null) : null;
    }
}


