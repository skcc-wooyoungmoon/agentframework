package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터 벡터 데이터베이스 생성 응답")
public class DataToolVectorDBCreateRes {
    @Schema(description = "데이터 벡터 데이터베이스 ID")
    private String vectorDbId;
}