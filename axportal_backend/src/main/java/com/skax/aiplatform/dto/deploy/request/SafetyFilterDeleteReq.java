package com.skax.aiplatform.dto.deploy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 세이프티 필터 삭제 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 삭제 요청")
public class SafetyFilterDeleteReq {

    @Schema(description = "삭제할 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "최소 하나의 아이디가 필요합니다.")
    private List<String> filterGroupIds;

}

