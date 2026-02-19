package com.skax.aiplatform.client.deepsecurity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeepSecurity 요청 DTO
 * 
 * <p>DeepSecurity API의 /api/v1/models/deepsecurity 엔드포인트에 대한 요청 데이터 구조입니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>uid</strong>: 사용자 ID</li>
 *   <li><strong>name</strong>: 모델 이름</li>
 *   <li><strong>filename</strong>: 모델 파일명</li>
 * </ul>
 *
 * @author system
 * @since 2025-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Scan 요청 정보")
public class ScanRequest {
    
    @JsonProperty("uid")
    @Schema(description = "사용자 ID", example = "123456", required = true)
    private String uid;
    
    @JsonProperty("name")
    @Schema(description = "모델 이름", example = "openai/clip-vit-base-patch32", required = true)
    private String name;

    @JsonProperty("filename")
    @Schema(description = "모델 파일명", example = "meta-llama/Llama-3.1-8b-Instruct", required = true)
    private String filename;

    @JsonProperty("split_count")
    @Schema(description = "파일 분할 개수", example = "1")
    private Integer splitCount;
}