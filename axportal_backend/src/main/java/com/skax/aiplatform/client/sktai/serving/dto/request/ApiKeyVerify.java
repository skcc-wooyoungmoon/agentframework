package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API Key 검증 요청 DTO
 *
 * <p>API Key의 유효성을 검증하기 위한 요청 데이터 구조입니다.</p>
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
    description = "SKTAI API Key 검증 요청 정보",
    example = """
        {
          "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
          "gateway_type": "model"
        }
        """
)
public class ApiKeyVerify {

    /**
     * 프로젝트 ID
     *
     * <p>API Key가 속한 프로젝트의 고유 식별자입니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "프로젝트 ID",
        example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
    )
    private String projectId;

    /**
     * 게이트웨이 타입
     *
     * <p>게이트웨이 타입을 지정합니다 (model 또는 agent).</p>
     */
    @JsonProperty("gateway_type")
    @Schema(
        description = "게이트웨이 타입 (model 또는 agent)",
        example = "model",
        allowableValues = {"model", "agent"}
    )
    private String gatewayType;
}
