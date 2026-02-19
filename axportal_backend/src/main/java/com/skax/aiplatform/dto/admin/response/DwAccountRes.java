package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DW 계정 정보 응답 DTO
 */
@Schema(description = "DW 계정 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DwAccountRes {

    @Schema(description = "계정 ID", example = "ANA_USR")
    private String accountId;

    @Schema(description = "계정 역할", example = "analysis")
    private String role;

}
