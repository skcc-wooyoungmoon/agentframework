package com.skax.aiplatform.dto.common.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운영 이행 관리 조회 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "운영 이행 관리 조회 응답")
public class MigMasRes {
    
    /**
     * 시퀀스 번호
     */
    @Schema(description = "시퀀스 번호", example = "1")
    private Long seqNo;
    
    /**
     * UUID
     */
    @Schema(description = "UUID", example = "0589176a-f80c-49e8-b3df-aceaf06c53b7")
    private String uuid;
    
    /**
     * 어시스트 그룹 (이행 분류)
     */
    @Schema(description = "이행 분류", example = "KNOWLEDGE")
    private String asstG;
    
    /**
     * 어시스트 명 (이행 대상)
     */
    @Schema(description = "이행 대상", example = "서비스")
    private String asstNm;
    
    /**
     * 프로젝트 시퀀스
     */
    @Schema(description = "프로젝트 시퀀스", example = "1")
    private Integer prjSeq;
    
    /**
     * GPO 프로젝트 명
     */
    @Schema(description = "GPO 프로젝트 명", example = "프로젝트명")
    private String gpoPrjNm;
    
    /**
     * 파일 경로
     */
    @Schema(description = "파일 경로", example = "/gapdat/migration/...")
    private String filePath;
    
    /**
     * 파일 명들
     */
    @Schema(description = "파일 명들", example = "file1.json,file2.json")
    private String fileNms;
    
    /**
     * 프로그램 설명 내용
     */
    @Schema(description = "프로그램 설명 내용")
    private String pgmDescCtnt;
    
    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "0")
    private Integer delYn;
    
    /**
     * 최초 생성일시 (이행 요청일시)
     */
    @Schema(description = "이행 요청일시", example = "2025-06-29T18:32:43")
    private LocalDateTime fstCreatedAt;
    
    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "user123")
    private String createdBy;
}

