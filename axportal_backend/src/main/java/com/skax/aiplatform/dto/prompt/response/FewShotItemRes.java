package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Few-Shot Item 응답 DTO
 * 
 * <p>Few-Shot Item목록을 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-20
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Few-Shot 응답")
public class FewShotItemRes {
    @Schema(description = "Few-Shot UUID")
    private String uuid;
        
    @Schema(description = "Few-Shot 아이템 순서")
    private Integer itemSequence;
        
    @Schema(description = "아이템 내용")
    private String item;
        
    @Schema(description = "버전 ID")
    private String versionId;

    @Schema(description = "아이템 타입")
    private String itemType;
}
