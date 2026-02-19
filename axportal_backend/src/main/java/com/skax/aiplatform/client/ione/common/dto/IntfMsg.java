package com.skax.aiplatform.client.ione.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IONE API 공통 메시지 정보 DTO
 * 
 * <p>IONE API Gateway의 모든 응답에서 공통으로 사용되는 메시지 정보 구조입니다.
 * Swagger 문서의 IntfMsg 스키마를 기반으로 구현되었습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>code</strong>: 결과 코드 (예: "ideatec.system.info.response.success")</li>
 *   <li><strong>desc</strong>: 결과 메시지 (예: "정상적으로 처리되었습니다.")</li>
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
    description = "IONE API 공통 메시지 정보",
    example = """
        {
          "code": "ideatec.system.info.response.success",
          "desc": "정상적으로 처리되었습니다."
        }
        """
)
public class IntfMsg {

    /**
     * 결과 코드
     */
    @JsonProperty("code")
    @Schema(description = "결과 코드", example = "ideatec.system.info.response.success")
    private String code;

    /**
     * 결과 메시지
     */
    @JsonProperty("desc")
    @Schema(description = "결과 메시지", example = "정상적으로 처리되었습니다.")
    private String desc;
}