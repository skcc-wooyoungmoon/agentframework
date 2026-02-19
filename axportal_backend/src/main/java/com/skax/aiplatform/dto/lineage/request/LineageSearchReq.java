package com.skax.aiplatform.dto.lineage.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lineage 관계 조회 요청 DTO
 * 
 * <p>BFS 탐색을 통한 Lineage 관계 조회를 위한 요청 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lineage 관계 조회 요청")
public class LineageSearchReq {
    
    @NotBlank(message = "객체 키는 필수입니다.")
    @Schema(description = "탐색을 시작할 객체의 고유 키", example = "fewshot-001", required = true)
    private String objectKey;
    
    @NotBlank(message = "탐색 방향은 필수입니다.")
    @Schema(
        description = "탐색 방향", 
        example = "upstream", 
        allowableValues = {"upstream", "downstream"},
        required = true
    )
    private String direction;

    @Schema(description = "액션 타입", example = "USE", allowableValues = {"USE", "CREATE"})
    private String action;
    
    @Schema(description = "탐색할 최대 깊이 (선택적, 미지정 시 전체 탐색)", example = "3")
    private Integer depth;
    
}
