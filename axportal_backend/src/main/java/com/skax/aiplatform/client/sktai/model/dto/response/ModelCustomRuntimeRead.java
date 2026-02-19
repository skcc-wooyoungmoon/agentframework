package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Model Custom Runtime 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model Custom Runtime 응답 정보")
public class ModelCustomRuntimeRead {
    
    @JsonProperty("id")
    @Schema(description = "Custom Runtime ID", format = "uuid")
    private String id;
    
    @JsonProperty("model_id")
    @Schema(description = "커스텀 모델 ID", format = "uuid")
    private String modelId;
    
    @JsonProperty("image_url")
    @Schema(description = "커스텀 Docker 이미지 URL")
    private String imageUrl;
    
    @JsonProperty("use_bash")
    @Schema(description = "bash 사용 여부")
    private Boolean useBash;
    
    @JsonProperty("command")
    @Schema(description = "컨테이너 실행 명령어 배열")
    private List<String> command;
    
    @JsonProperty("args")
    @Schema(description = "컨테이너 실행 인수 배열")
    private List<String> args;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 일시", format = "date-time")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 일시", format = "date-time")
    private LocalDateTime updatedAt;
}
