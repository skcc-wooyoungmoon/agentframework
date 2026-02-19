package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 자원 관리 검색 요청 DTO
 * 
 * @author SonMunWoo
 * @since 2025-09-27
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자원 관리 검색 요청")
public class ResrcMgmtSearchReq {
    
    @Schema(description = "자원명", example = "agent-deploy")
    private String resrcName;
    
    @Schema(description = "자원 타입", example = "agent-deploy", allowableValues = {"agent-deploy", "model-deploy"})
    private String resrcType;
    
    @Schema(description = "프로젝트 ID", example = "project-001")
    private String projectId;
    
    @Schema(description = "상태", example = "active", allowableValues = {"active", "stopped", "error"})
    private String status;
    
    @Schema(description = "검색 시작일", example = "2024-01-01T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "검색 종료일", example = "2024-12-31T23:59:59")
    private LocalDateTime endDate;
}
