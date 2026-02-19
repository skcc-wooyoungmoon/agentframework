package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Shared Agent Backends 목록 응답 DTO
 *
 * <p>Shared Agent Backend 목록 조회 시 반환되는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-09-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Shared Agent Backends 목록 응답",
    example = """
        [
          {
            "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
            "shared_agent_backend_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            "status": "Ready"
          }
        ]
        """
)
public class SharedAgentBackendsResponse {

    /**
     * Shared Agent Backend 목록 데이터
     */
    @JsonProperty("data")
    @Schema(description = "Shared Agent Backend 목록")
    private List<SharedAgentBackendRead> data;
}
