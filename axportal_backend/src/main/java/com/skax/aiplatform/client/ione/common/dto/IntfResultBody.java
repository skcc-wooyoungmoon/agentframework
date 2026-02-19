package com.skax.aiplatform.client.ione.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IONE API 공통 응답 결과 본문 DTO
 * 
 * <p>IONE API Gateway의 모든 응답에서 공통으로 사용되는 결과 정보 구조입니다.
 * Swagger 문서의 IntfResultBody 스키마를 기반으로 구현되었습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>success</strong>: 처리 결과 (true: 성공, false: 실패)</li>
 *   <li><strong>msg</strong>: 메시지 정보 (코드와 설명)</li>
 * </ul>
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
    description = "IONE API 공통 응답 결과 본문",
    example = """
        {
          "success": true,
          "msg": {
            "code": "ideatec.system.info.response.success",
            "desc": "정상적으로 처리되었습니다."
          }
        }
        """
)
public class IntfResultBody {

    /**
     * 처리 결과
     */
    @JsonProperty("success")
    @Schema(description = "처리 결과 (true: 성공, false: 실패)", example = "true", required = true)
    private Boolean success;

    /**
     * 메시지 정보
     */
    @JsonProperty("msg")
    @Schema(description = "메시지 정보")
    private IntfMsg msg;
}