package com.skax.aiplatform.dto.data.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인덱싱 설정
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "인덱싱 설정")

public class DataCtlgIndexingConfig {

    /**
     * 메타데이터 필드 목록
     */
    @Schema(description = "메타데이터 필드 목록")
    private List<DataCtlgMetadataField> metadataFields;
}
