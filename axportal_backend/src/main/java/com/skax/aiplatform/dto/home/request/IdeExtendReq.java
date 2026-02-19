package com.skax.aiplatform.dto.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "IDE 사용 기간 연장 요청")
public class IdeExtendReq {

    @Schema(description = "연장 기간 (일)", example = "7")
    private Integer extendDays;

}
