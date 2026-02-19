package com.skax.aiplatform.dto.agent.response;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 에이전트 빌더 응답 DTO
 * 
 * <p>
 * 에이전트 빌더 정보를 담는 응답 DTO입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-19
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "에이전트 빌더 응답")
public class AgentBuilderRes {

    @Schema(description = "그래프 UUID", example = "e898ebdd-6dae-477d-8fd8-463919d324df")
    private String id;

    @Schema(description = "에이전트 이름", example = "고객 상담 챗봇")
    private String name;

    @Schema(description = "에이전트 설명", example = "고객 문의에 대한 자동 응답 챗봇")
    private String description;

    @Schema(description = "에이전트 타입", example = "CHATBOT")
    private String type;

    @Schema(description = "카테고리", example = "CUSTOMER_SERVICE")
    private String category;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "생성 시간", example = "2024-01-15T10:30:00")
    private String createdAt;

    @Schema(description = "수정 시간", example = "2024-01-15T10:30:00")
    private String updatedAt;

    @Schema(description = "생성자", example = "user@example.com")
    private String createdBy;

    @Schema(description = "수정자", example = "user@example.com")
    private String updatedBy;

    @Schema(description = "노드 수", example = "5")
    private Integer nodeCount;

    @Schema(description = "엣지 수", example = "4")
    private Integer edgeCount;

    @Schema(description = "노드 목록")
    private List<Map<String, Object>> nodes;

    @Schema(description = "엣지 목록")
    private List<Map<String, Object>> edges;

    @Schema(description = "Phoenix 프로젝트 ID", example = "UHJvamVjdDoyMg==")
    private String phoenixProjectId;

    @Schema(description = "배포 상태", example = "개발")
    private String deploymentStatus;

    @Schema(description = "공개 범위", example = "전체공유")
    private String publicStatus;

    @Schema(description = "최초 생성 프로젝트 SEQ")
    private Integer fstPrjSeq;

    @Schema(description = "최종 변경 프로젝트 SEQ")
    private Integer lstPrjSeq;
}