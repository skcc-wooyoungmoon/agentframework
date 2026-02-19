package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 지식 이행 Import 응답 DTO
 * 
 * <p>지식 이행 Import 결과를 담는 DTO입니다.</p>
 * 
 * @author system
 * @since 2025-01-20
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지식 이행 Import 응답")
public class DataCtlgExternalKnowledgeDeployImportRes {

    /**
     * 새로 생성된 지식 ID
     */
    @Schema(description = "새로 생성된 지식 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String kwlgId;

    /**
     * 새로 생성된 외부 지식 ID (ADXP repo_id)
     */
    @Schema(description = "새로 생성된 외부 지식 ID", example = "11111111-1111-1111-1111-111111111111")
    private String exKwlgId;

    /**
     * 인덱스명
     */
    @Schema(description = "인덱스명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
    private String idxNm;
}

