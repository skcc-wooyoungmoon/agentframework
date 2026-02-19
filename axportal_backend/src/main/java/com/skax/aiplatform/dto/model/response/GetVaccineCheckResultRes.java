package com.skax.aiplatform.dto.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 가든 백신검사 결과 응답")
public class GetVaccineCheckResultRes {
    
    @Schema(description = "모델 이름")
    private String modelName;

    @Schema(description = "모델 라이센스")
    private String license;

    @Schema(description = "첫 번째 검사 결과 상세 내용")    
    private String fistChkDtl;

    @Schema(description = "두 번째 검사 결과 상세 내용")
    private String secndChkDtl;
    
    @Schema(description = "취약점 점검 요약 내용")
    private String vanbBrSmry;

    @Schema(description = "취약점 점검 결재 요청자")
    private String checkBy;

    @Schema(description = "취약점 점검 결재 요청일시")
    private String checkAt;

    @Schema(description = "취약점 점검 결과 상태")
    private String checkStatus;
}

