package com.skax.aiplatform.client.ione.apikey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.ione.common.dto.IntfResultBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IONE Open API Key 단건 조회 응답 DTO
 * 
 * <p>IONE API Gateway에서 특정 Open API Key를 조회하는 응답 데이터 구조입니다.
 * 실제 IONE API Swagger 문서의 GetApiKeyResult 스키마를 기반으로 구현되었습니다.</p>
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
    description = "IONE Open API Key 단건 조회 응답",
    example = """
        {
          "result": {
            "success": true,
            "msg": {
              "code": "ideatec.system.info.response.success",
              "desc": "정상적으로 처리되었습니다."
            }
          },
          "data": {
            "openApiKey": "202501081856019122HEHDKS017641M4VU62Q4T7ZIUEHFM543",
            "scope": "[\\"*\\"]",
            "delYn": "N"
          },
          "success": true
        }
        """
)
public class GetApiKeyResult {

    /**
     * 처리 결과 정보
     */
    @JsonProperty("result")
    @Schema(description = "처리 결과 정보")
    private IntfResultBody result;

    /**
     * 조회한 Open API Key 상세 정보
     */
    @JsonProperty("data")
    @Schema(description = "조회한 Open API Key 상세 정보")
    private IntfOpenApiKeyVo data;

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