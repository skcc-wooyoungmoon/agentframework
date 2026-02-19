package com.skax.aiplatform.dto.data.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 데이터셋 태그 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 태그 요청")
public class DataCtlgDataSetTagUpdateReq {

    @Schema(description = "데이터셋 ID", example = "bcf3034f-0c33-45f3-9b3a-7cd0f9f5adcd")
    private UUID datasetId;

    @Schema(description = "수정할 태그 목록")
    private List<DataCtlgDataSetTag> tags;
}