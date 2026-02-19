package com.skax.aiplatform.dto.deploy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 배포 정보 응답 DTO
 * 
 * <p>Agent의 배포 정보(네임스페이스, 서비스명)를 담는 응답 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-24
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent 배포 정보 응답")
public class AgentDeployInfoRes {

    /**
     * Agent ID
     */
    @Schema(description = "Agent ID", example = "agent-12345")
    private String agentId;

    /**
     * 네임스페이스
     */
    @Schema(description = "Kubernetes 네임스페이스", example = "ns-24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String namespace;

    /**
     * 서비스 이름 (isvcName)
     */
    @Schema(description = "Inference Service 이름", example = "svc-86cf6a62")
    private String isvcName;

    /**
     * 배포 상태
     */
    @Schema(description = "배포 상태", example = "Available")
    private String status;

    /**
     * 배포 ID
     */
    @Schema(description = "배포 ID", example = "deploy-67890")
    private String deployId;

    /**
     * 배포 일시
     */
    @Schema(description = "배포 일시", example = "2025-11-13 10:00:00")
    private String deployDt;
}
