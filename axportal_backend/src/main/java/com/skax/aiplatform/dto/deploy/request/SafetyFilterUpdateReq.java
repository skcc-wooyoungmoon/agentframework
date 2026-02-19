package com.skax.aiplatform.dto.deploy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Safety Filter 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Safety Filter 수정 요청")
public class SafetyFilterUpdateReq {

    @Schema(description = "분류", example = "정치경제")
    @Size(max = 255, message = "분류명은 255자 이하여야 합니다.")
    @NotBlank(message = "분류명은 필수입니다.")
    private String filterGroupName;

    @Schema(description = "금지어 목록", example = "[\"금지어1\", \"금지어2\", \"금지어3\"]")
    @NotEmpty(message = "금지어는 최소 하나 이상 필요합니다.")
    private List<String> stopWords;

}
