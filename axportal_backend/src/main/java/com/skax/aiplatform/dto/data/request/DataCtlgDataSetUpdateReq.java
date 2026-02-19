package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 수정 요청 DTO
 * 
 * <p>Controller에서 Service로 전달하는 데이터셋 수정 요청 DTO입니다.
 * OpenAPI 명세에 따라 description과 project_id가 필수 필드입니다.</p>
 * 
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 수정 요청")
public class DataCtlgDataSetUpdateReq {
    
    /**
     * 데이터셋 설명
     */
    @Schema(description = "데이터셋 설명", example = "AI 모델 학습을 위한 수정된 데이터셋", required = true)
    private String description;
    
    /**
     * 프로젝트 ID
     */
    @NotBlank(message = "프로젝트 ID는 필수입니다")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", required = true)
    private String projectId;
    
    /**
     * 정책 설정
     */
    @Schema(description = "정책 설정", example = "{\"retention\": \"30d\", \"access\": \"private\"}")
    private Object policy;
    
}