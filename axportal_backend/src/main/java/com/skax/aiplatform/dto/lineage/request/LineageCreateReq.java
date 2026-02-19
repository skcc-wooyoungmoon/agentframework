package com.skax.aiplatform.dto.lineage.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lineage 관계 생성 요청 DTO
 * 
 * <p>두 객체 간의 새로운 Lineage 관계를 생성하기 위한 요청 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lineage 관계 생성 요청")
public class LineageCreateReq {
    
    @NotBlank(message = "소스 키는 필수입니다.")
    @Schema(description = "소스 객체의 고유 키", required = true)
    private String sourceKey;
    
    @NotBlank(message = "타겟 키는 필수입니다.")
    @Schema(description = "타겟 객체의 고유 키", required = true)
    private String targetKey;
    
    @NotBlank(message = "액션 타입은 필수입니다.")
    @Schema(
        description = "액션 타입", 
        example = "USE", 
        allowableValues = {"USE", "CREATE"},
        required = true
    )
    private String action;
    
    @Schema(
        description = "소스 객체 타입", 
        example = "KNOWLEDGE",
        required = false
    )
    private String sourceType;
    
    @Schema(
        description = "타겟 객체 타입", 
        example = "VECTOR_DB",
        required = false
    )
    private String targetType;
    
}
