package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Custom Runtime 생성 요청 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model Custom Runtime 생성 요청 정보")
public class ModelCustomRuntimeCreate {
    
    @JsonProperty("model_id")
    @Schema(description = "커스텀 모델 ID", required = true, format = "uuid")
    private String modelId;
    
    @JsonProperty("image_url")
    @Schema(description = "커스텀 Docker 이미지 URL", required = true, maxLength = 1000)
    private String imageUrl;
    
    @JsonProperty("use_bash")
    @Schema(description = "bash 사용 여부", example = "false")
    @Builder.Default
    private Boolean useBash = false;
    
    @JsonProperty("command")
    @Schema(description = "컨테이너 실행 명령어 배열")
    private List<String> command;
    
    @JsonProperty("args")
    @Schema(description = "컨테이너 실행 인수 배열")
    private List<String> args;
}
