package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Task Resource Response DTO
 * 
 * <p>
 * 태스크 리소스 정보를 담는 응답 DTO입니다.
 * 노드 리소스, 네임스페이스 리소스, 태스크 정책, 태스크 할당량 정보를 포함합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "태스크 리소스 정보")
public class TaskResourceResponse {

    /**
     * 노드 리소스 목록
     * 
     * <p>
     * 각 노드별 리소스 사용량과 할당량 정보를 포함합니다.
     * </p>
     */
    @JsonProperty("node_resource")
    @Schema(description = "노드 리소스 목록")
    private List<NodeResource> nodeResource;

    /**
     * 네임스페이스 리소스 정보
     * 
     * <p>
     * 네임스페이스별 리소스 할당량과 사용량 정보를 포함합니다.
     * </p>
     */
    @JsonProperty("namespace_resource")
    @Schema(description = "네임스페이스 리소스 정보")
    private NamespaceResource namespaceResource;

    /**
     * 태스크 정책 정보
     * 
     * <p>
     * 각 크기별 태스크 정책과 리소스 할당량을 포함합니다.
     * </p>
     */
    @JsonProperty("task_policy")
    @Schema(description = "태스크 정책 정보")
    private TaskPolicy taskPolicy;

    /**
     * 태스크 할당량 정보
     * 
     * <p>
     * 태스크의 총 할당량과 현재 사용량을 포함합니다.
     * </p>
     */
    @JsonProperty("task_quota")
    @Schema(description = "태스크 할당량 정보")
    private TaskQuota taskQuota;
}
