package com.skax.aiplatform.client.ione.apikey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.ione.common.dto.IntfResultBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IONE Open API Key 삭제 응답 DTO
 * 
 * <p>IONE API Gateway에서 기존 Open API Key를 삭제하는 응답 데이터 구조입니다.
 * 실제 IONE API Swagger 문서의 DelApiKeyResult 스키마를 기반으로 구현되었습니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "IONE Open API Key 삭제 응답",
    example = """
        {
          "result": {
            "success": true,
            "msg": {
              "code": "ideatec.system.info.response.success",
              "desc": "정상적으로 처리되었습니다."
            }
          },
          "data": "N",
          "success": true
        }
        """
)
public class DelApiKeyResult {

    /**
     * 처리 결과 정보
     */
    @JsonProperty("result")
    @Schema(description = "처리 결과 정보")
    private IntfResultBody result;

    /**
     * 삭제 여부 (Y/N)
     */
    @JsonProperty("data")
    @Schema(
        description = "삭제 여부 (Y: 삭제됨, N: 삭제되지 않음)",
        example = "N",
        allowableValues = {"Y", "N"}
    )
    private String data;

    /**
     * 성공 여부 (쓰기 전용)
     */
    @JsonProperty("success")
    @Schema(description = "성공 여부", accessMode = Schema.AccessMode.WRITE_ONLY)
    private Boolean success;

    /**
     * API 호출 성공 여부 확인
     * 
     * @return 성공 여부
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(this.success) || 
               (this.result != null && Boolean.TRUE.equals(this.result.getSuccess()));
    }

    /**
     * API Key 삭제 여부 확인
     * 
     * @return 삭제된 경우 true, 아니면 false
     */
    public boolean isDeleted() {
        return isSuccess() && "Y".equals(this.data);
    }

    /**
     * 에러 메시지 조회
     * 
     * @return 에러 메시지 (성공 시 null)
     */
    public String getErrorMessage() {
        if (isSuccess()) {
            return null;
        }
        
        if (this.result != null && this.result.getMsg() != null) {
            return this.result.getMsg().getDesc();
        }
        
        return "알 수 없는 오류가 발생했습니다.";
    }
}