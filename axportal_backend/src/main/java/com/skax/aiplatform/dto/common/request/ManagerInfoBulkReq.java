package com.skax.aiplatform.dto.common.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerInfoBulkReq {
    @Schema(description = "조회 타입 (memberId 또는 uuid)", required = true, example = "memberId")
    private String type;

    @Schema(description = "조회 값 목록 (memberId 또는 uuid 목록)", required = true, example = "[\"SGO1032949\", \"SGO1032950\"]")
    private List<String> values;
}
