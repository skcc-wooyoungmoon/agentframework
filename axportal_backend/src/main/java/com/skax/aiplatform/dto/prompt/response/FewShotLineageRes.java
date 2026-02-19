package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Few-Shot Lineage 관계 응답 DTO
 *
 * <p>Few-Shot과 연결된 Lineage 관계 정보를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 *
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Few-Shot Lineage 관계 응답")
public class FewShotLineageRes {

    @Schema(description = "graph ID", example = "7a1dcc26-d104-4d24-9c14-0a0494bcc367")
    private String id;
    
    @Schema(description = "Few-Shot 이름", example = "고객 서비스 Few-Shot")
    private String name;
    
    @Schema(description = "Few-Shot 설명", example = "고객 문의 응답을 위한 Few-Shot 예제")
    private String description;
    
    @Schema(description = "배포 여부", example = "true")
    private Boolean deployed;
    
    @Schema(description = "생성일시", example = "2025-10-19T16:14:33")
    private String createdAt;
    
    @Schema(description = "수정일시", example = "2025-10-19T16:14:33")
    private String updatedAt;
}
