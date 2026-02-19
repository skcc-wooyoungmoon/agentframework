package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.skax.aiplatform.dto.lineage.response.LineageRelationRes;

/**
 * Few-Shot 응답 DTO
 * 
 * <p>Few-Shot 정보를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-11
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Few-Shot 응답")
public class FewShotRes {

    @Schema(description = "Few-Shot UUID")
    private String uuid;
    
    @Schema(description = "Few-Shot 이름")
    private String name;
    
    @Schema(description = "의존성 목록")
    private List<String> dependency;
    
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @Schema(description = "릴리즈 버전")
    private Integer releaseVersion;
    
    @Schema(description = "최신 버전")
    private Integer latestVersion;
    
    @Schema(description = "태그 목록")
    private List<String> tags;
    
    @Schema(description = "히트율")
    private Double hitRate;

    @Schema(description = "연결된 에이전트 수")
    private Integer connectedAgentCount;

    @Schema(description = "연결된 에이전트 배포 여부")
    private Boolean deployed;

    @Schema(description = "공개범위")
    private String publicStatus;
    
    @Schema(description = "연결된 AGENT_GRAPH Lineage 관계 목록")
    private List<LineageRelationRes> agentGraphRelations;

    @Schema(description = "생성자 ID")
    private String createdBy;
}