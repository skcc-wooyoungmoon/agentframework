package com.skax.aiplatform.dto.prompt.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Few-Shot 태그 정보 응답")
public class FewShotTagListRes {
    @Schema(description = "시스템에서 사용 가능한 모든 Few-Shot 태그 목록")
    private List<String> tags;
}
