package com.skax.aiplatform.dto.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 모델 배포 세션 정보 응답 DTO
 * 
 * <p>모델 배포 정보에 세션 ID를 추가한 응답 DTO입니다.</p>
 * 
 * @author sonmunwoo
 * @since 2025-10-29
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "모델 배포 세션 정보 응답")
public class GetModelDeploySessionRes extends GetModelDeployRes {
    
    @Schema(description = "세션 ID", example = "c182a9d3-147e-4ee1-a465-c3646d7a1758")
    private String sessionId;
}
