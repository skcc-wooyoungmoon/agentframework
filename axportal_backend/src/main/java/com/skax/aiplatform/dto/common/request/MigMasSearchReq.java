package com.skax.aiplatform.dto.common.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운영 이행 관리 조회 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "운영 이행 관리 조회 요청")
public class MigMasSearchReq {
    
    /**
     * 페이지 번호 (1부터 시작)
     */
    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    @Builder.Default
    private Integer page = 1;
    
    /**
     * 페이지 크기 (12, 36, 60)
     */
    @Schema(description = "페이지 크기", example = "12", allowableValues = {"12", "36", "60"}, defaultValue = "12")
    @Builder.Default
    private Integer size = 12;
    
    /**
     * 조회 시작일시 (fst_created_at >= startDate)
     */
    @Schema(description = "조회 시작일시", example = "2025-06-29T00:00:00")
    private LocalDateTime startDate;
    
    /**
     * 조회 종료일시 (fst_created_at <= endDate)
     */
    @Schema(description = "조회 종료일시", example = "2025-06-30T23:59:59")
    private LocalDateTime endDate;
    
    /**
     * 이행 대상 검색어 (asst_nm LIKE)
     */
    @Schema(description = "이행 대상 검색어", example = "서비스")
    private String asstNm;
    
    /**
     * 이행 분류 필터 (asst_g)
     */
    @Schema(description = "이행 분류", example = "KNOWLEDGE")
    private String asstG;

    /**
     * 프로젝트 시퀀스 필터 (prj_seq)
     */
    @Schema(description = "프로젝트 시퀀스", example = "1")
    private Integer prjSeq;
}

