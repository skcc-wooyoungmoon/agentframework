package com.skax.aiplatform.dto.resource.response;

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
 * <p>Task Policy 목록을 담는 내부 응답 DTO입니다.</p>
 * 
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task Policy 목록 응답")
public class TaskPolicyListRes {
    
    /**
     * Task Policy 목록
     */
    @JsonProperty("task_policies")
    @Schema(description = "Task Policy 목록")
    private List<TaskPolicyRes> taskPolicies;
    
    /**
     * 전체 정책 개수
     */
    @JsonProperty("total_count")
    @Schema(description = "전체 정책 개수", example = "16")
    private Integer totalCount;
}
