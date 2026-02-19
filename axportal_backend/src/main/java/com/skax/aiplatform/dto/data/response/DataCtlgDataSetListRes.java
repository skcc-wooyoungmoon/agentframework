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
 * 데이터셋 목록 조회 응답 DTO (Service → Controller)
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 목록 조회 응답")
public class DataCtlgDataSetListRes {

    /**
     * 데이터셋 정보 DTO
     */

    @Schema(description = "데이터셋 ID", format = "uuid")
    private UUID id;

    @Schema(description = "데이터셋 이름", example = "FT #1 (summary)")
    private String name;

    @Schema(description = "데이터셋 타입", example = "supervised_finetuning")
    private String type;

    @Schema(description = "데이터셋 설명", example = "Supervised Fine Tuning dataset (Summary Task)")
    private String description;

    @Schema(description = "데이터셋 태그 목록")
    private List<DataCtlgDatasetTagRes> tags;

    @Schema(description = "데이터셋 상태", example = "completed")
    private String status;

    @Schema(description = "프로젝트 ID", format = "uuid")
    private UUID projectId;

    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    @Schema(description = "데이터소스 ID", format = "uuid")
    private UUID datasourceId;

    @Schema(description = "데이터소스 파일 목록")
    private List<String> datasourceFiles;

    @Schema(description = "파일 경로", example = "data/dataset/upload/7c3e975c-69a3-4c9a-a881-f8143698de6f/FT #1 (summary).json")
    private String filePath;

    @Schema(description = "프로세서 정보")
    private Object processor;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "생성자", example = "skcc_demo")
    private String createdBy;

    @Schema(description = "수정자", example = "skcc_demo")
    private String updatedBy;

    @Schema(description = "공개 여부", example = "전체공유")
    private String publicStatus;

    /**
     * 최초 project seq
     */
    @Schema(description = "최초 project seq")
    private Integer fstPrjSeq;
    /**
     * 최종 project seq
     */
    @Schema(description = "최종 project seq")
    private Integer lstPrjSeq;
}