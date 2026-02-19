package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Serving API 키 검증 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 API 키 검증 요청에 대한 응답 구조입니다.
 * API 키의 유효성을 확인하고 검증 결과를 반환합니다.</p>
 * 
 * <h3>응답 형태:</h3>
 * <ul>
 *   <li><strong>성공 시</strong>: {"detail": "success"}</li>
 *   <li><strong>실패 시</strong>: {"detail": "error msg"}</li>
 * </ul>
 * 
 * <h3>HTTP 상태 코드:</h3>
 * <ul>
 *   <li><strong>200</strong>: 검증 성공</li>
 *   <li><strong>401</strong>: 검증 실패</li>
 *   <li><strong>422</strong>: 요청 데이터 오류</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ApiKeyVerifyResponse response = servingService.verifyApiKey(verifyRequest);
 * if (response.isSuccess()) {
 *     log.info("API 키 검증 성공: {}", response.getDetail());
 * } else {
 *     log.warn("API 키 검증 실패: {}", response.getDetail());
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Serving API 키 검증 응답 정보",
    example = """
        {
          "detail": "success"
        }
        """
)
public class ApiKeyVerifyResponse {
    
    /**
     * 검증 결과 상세 정보
     * 
     * <p>API 키 검증 결과에 대한 상세 메시지입니다.
     * 성공 시 "success", 실패 시 구체적인 오류 메시지가 반환됩니다.</p>
     * 
     * @implNote SKTAI API는 항상 "detail" 필드로 응답 메시지를 전달합니다.
     */
    @JsonProperty("detail")
    @Schema(
        description = "API 키 검증 결과 상세 정보", 
        example = "success",
        allowableValues = {"success", "Invalid API key", "Expired API key", "Unauthorized access"}
    )
    private String detail;
    
    /**
     * 검증 성공 여부 확인
     * 
     * <p>detail 필드의 값이 "success"인지 확인하여 검증 성공 여부를 판단합니다.</p>
     * 
     * @return 검증 성공 여부
     */
    public boolean isSuccess() {
        return "success".equals(detail);
    }
    
    /**
     * 검증 실패 여부 확인
     * 
     * <p>검증이 실패했는지 확인합니다.</p>
     * 
     * @return 검증 실패 여부
     */
    public boolean isFailure() {
        return !isSuccess();
    }
    
    /**
     * 성공 응답 생성
     * 
     * @return 성공 응답 객체
     */
    public static ApiKeyVerifyResponse success() {
        return ApiKeyVerifyResponse.builder()
            .detail("success")
            .build();
    }
    
    /**
     * 실패 응답 생성
     * 
     * @param errorMessage 오류 메시지
     * @return 실패 응답 객체
     */
    public static ApiKeyVerifyResponse failure(String errorMessage) {
        return ApiKeyVerifyResponse.builder()
            .detail(errorMessage)
            .build();
    }
}
