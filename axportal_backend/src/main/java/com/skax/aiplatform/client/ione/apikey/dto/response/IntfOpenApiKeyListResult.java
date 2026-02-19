package com.skax.aiplatform.client.ione.apikey.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.ione.common.dto.IntfResultBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IONE Open API Key 목록 조회 응답 DTO
 * 
 * <p>IONE API Gateway에서 발급된 Open API Key 목록을 조회하는 응답 데이터 구조입니다.
 * 실제 IONE API Swagger 문서의 IntfOpenApiKeyListResult 스키마를 기반으로 구현되었습니다.</p>
 * 
 * <h3>Swagger 스키마 구조:</h3>
 * <pre>{@code
 * {
 *   "result": {
 *     "success": true,
 *     "msg": {
 *       "code": "ideatec.system.info.response.success",
 *       "desc": "정상적으로 처리되었습니다."
 *     }
 *   },
 *   "pageNum": 1,
 *   "pageSize": 10,
 *   "totalCount": 10,
 *   "data": [ API Key 목록 ],
 *   "success": true
 * }
 * }</pre>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 4.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "IONE Open API Key 목록 조회 응답",
    example = """
        {
          "result": {
            "success": true,
            "msg": {
              "code": "ideatec.system.info.response.success",
              "desc": "정상적으로 처리되었습니다."
            }
          },
          "pageNum": 1,
          "pageSize": 10,
          "totalCount": 10,
          "data": [],
          "success": true
        }
        """
)
public class IntfOpenApiKeyListResult {
    
    /**
     * 처리 결과 정보
     */
    @JsonProperty("result")
    @Schema(description = "처리 결과 정보")
    private IntfResultBody result;
    
    /**
     * 페이지 번호
     */
    @JsonProperty("pageNum")
    @Schema(description = "페이지 번호", example = "1")
    private Integer pageNum;
    
    /**
     * 페이지 크기
     */
    @JsonProperty("pageSize")
    @Schema(description = "페이지 크기", example = "10")
    private Integer pageSize;
    
    /**
     * 전체 API Key 개수
     */
    @JsonProperty("totalCount")
    @Schema(description = "전체 API Key 개수", example = "10")
    private Integer totalCount;
    
    /**
     * API Key 목록
     */
    @JsonProperty("data")
    @Schema(description = "API Key 목록")
    private List<IntfOpenApiKeyVo> data;

    /**
     * 성공 여부 (쓰기 전용)
     */
    @JsonProperty("success")
    @Schema(description = "성공 여부", accessMode = Schema.AccessMode.WRITE_ONLY)
    private Boolean success;
    
    /**
     * API Key 목록 조회 (하위 호환성)
     * 
     * @return API Key 목록 (null인 경우 빈 리스트 반환)
     */
    public List<IntfOpenApiKeyVo> getApiKeyList() {
        return this.data;
    }
    
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