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
 * 세이프티 필터 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 생성 요청")
public class SafetyFilterCreateReq {

    @Schema(description = "분류명", example = "욕설")
    @Size(max = 255, message = "분류명은 255자 이하여야 합니다.")
    @NotBlank(message = "분류명은 필수입니다.")
    private String filterGroupName;

    @Schema(description = "금지어 목록", example = "[\"비속어1\", \"비속어2\", \"비속어3\"]")
    @NotEmpty(message = "금지어는 최소 하나 이상 필요합니다.")
    private List<String> stopWords;

}

