package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Task Policy List Response DTO
 * 
 * <p>Task Policy 목록을 담는 응답 DTO입니다.
 * 시스템에 등록된 모든 Task Policy 정보를 포함합니다.</p>
 * 
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task Policy 목록 응답")
public class TaskPolicyListResponse {
    
    /**
     * Task Policy 목록
     * 
     * <p>시스템에 등록된 모든 Task Policy 정보를 담는 리스트입니다.
     * 각 정책은 task_type, size, cpu, memory, gpu 정보를 포함합니다.</p>
     */
    @JsonProperty("task_policies")
    @Schema(description = "Task Policy 목록")
    private List<TaskPolicyResponse> taskPolicies;
    
    /**
     * 전체 정책 개수
     * 
     * <p>시스템에 등록된 Task Policy의 총 개수입니다.</p>
     */
    @JsonProperty("total_count")
    @Schema(description = "전체 정책 개수", example = "16")
    private Integer totalCount;
}