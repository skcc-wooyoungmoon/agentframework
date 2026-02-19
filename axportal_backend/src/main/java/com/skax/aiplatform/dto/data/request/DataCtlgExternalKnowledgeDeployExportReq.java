package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 지식 이행 Export 요청 DTO
 * 
 * <p>지식을 이행하기 위해 Export하는 요청 데이터를 담는 DTO입니다.</p>
 * 
 * @author system
 * @since 2025-01-20
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지식 이행 Export 요청")
public class DataCtlgExternalKnowledgeDeployExportReq {

    /**
     * 외부 지식 ID (ex_kwlg_id)
     * DB에 있으면 기본지식, 없으면 커스텀지식으로 처리됩니다.
     */
    @Schema(description = "외부 지식 ID (ex_kwlg_id)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private String exKwlgId;
}

