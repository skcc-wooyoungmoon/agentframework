package com.skax.aiplatform.dto.data.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋 응답 DTO
 * 
 * <p>데이터셋의 기본 정보를 담는 응답 DTO입니다.</p>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 기본 정보")
public class DataSetRes {

    /**
     * 데이터셋 ID
     */
    @Schema(description = "데이터셋 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    /**
     * 프로젝트 ID
     */
    @Schema(description = "프로젝트 ID", example = "project-123")
    private String projectId;

    /**
     * 데이터셋 이름
     */
    @Schema(description = "데이터셋 이름", example = "고객 데이터셋")
    private String name;

    /**
     * 데이터셋 설명
     */
    @Schema(description = "데이터셋 설명", example = "고객 정보 및 구매 이력 데이터")
    private String description;

    /**
     * 데이터셋 타입
     */
    @Schema(description = "데이터셋 타입", example = "TRAINING")
    private String type;

    /**
     * 데이터셋 태그 목록
     */
    @Schema(description = "데이터셋 태그 목록")
    private List<String> tags;

    /**
     * 데이터셋 상태
     */
    @Schema(description = "데이터셋 상태", example = "ACTIVE")
    private String status;

    /**
     * 생성일시
     */
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    /**
     * 수정자
     */
    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    /**
     * 데이터소스 ID
     */
    @Schema(description = "데이터소스 ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID datasourceId;

    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;
}
