package com.skax.aiplatform.dto.common.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운영 이행 관리 조회 응답 DTO (조인 결과)
 * 
 * <p>GPO_MIG_MAS와 GPO_MIG_ASST_MAP_MAS를 조인한 결과를 반환합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "운영 이행 관리 조회 응답 (조인 결과)")
public class MigMasWithMapRes {
    
    // ========== GPO_MIG_MAS 컬럼 ==========
    
    /**
     * 시퀀스 번호 (MAS)
     */
    @Schema(description = "시퀀스 번호", example = "1")
    private Long masSeqNo;
    
    /**
     * UUID (MAS)
     */
    @Schema(description = "UUID", example = "0589176a-f80c-49e8-b3df-aceaf06c53b7")
    private String masUuid;
    
    /**
     * 어시스트 그룹 (MAS)
     */
    @Schema(description = "이행 분류", example = "KNOWLEDGE")
    private String masAsstG;
    
    /**
     * 어시스트 명 (MAS)
     */
    @Schema(description = "이행 대상", example = "서비스")
    private String masAsstNm;
    
    /**
     * 프로젝트 시퀀스
     */
    @Schema(description = "프로젝트 시퀀스", example = "1")
    private Integer masPrjSeq;
    
    /**
     * GPO 프로젝트 명
     */
    @Schema(description = "GPO 프로젝트 명", example = "프로젝트명")
    private String masGpoPrjNm;
    
    /**
     * 마이그레이션 파일 경로
     */
    @Schema(description = "파일 경로", example = "/gapdat/migration/...")
    private String masMigFilePath;
    
    /**
     * 마이그레이션 파일 명
     */
    @Schema(description = "파일 명들", example = "file1.json,file2.json")
    private String masMigFileNm;
    
    /**
     * 프로그램 설명 내용
     */
    @Schema(description = "프로그램 설명 내용")
    private String masPgmDescCtnt;
    
    /**
     * 삭제 여부
     */
    @Schema(description = "삭제 여부", example = "0")
    private Integer masDelYn;
    
    /**
     * 최초 생성일시
     */
    @Schema(description = "이행 요청일시", example = "2025-06-29T18:32:43")
    private LocalDateTime masFstCreatedAt;
    
    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "user123")
    private String masCreatedBy;
    
    // ========== GPO_MIG_ASST_MAP_MAS 컬럼 ==========
    
    /**
     * 시퀀스 번호 (MAP)
     */
    @Schema(description = "매핑 시퀀스 번호", example = "1")
    private Long mapSeqNo;
    
    /**
     * 마이그레이션 시퀀스 번호
     */
    @Schema(description = "마이그레이션 시퀀스 번호", example = "1")
    private Long mapMigSeqNo;
    
    /**
     * 마이그레이션 UUID (MAP)
     */
    @Schema(description = "마이그레이션 UUID", example = "0589176a-f80c-49e8-b3df-aceaf06c53b7")
    private String mapMigUuid;
    
    /**
     * 어시스트 UUID
     */
    @Schema(description = "어시스트 UUID", example = "asst-uuid-123")
    private String mapAsstUuid;
    
    /**
     * 어시스트 그룹 (MAP)
     */
    @Schema(description = "매핑 어시스트 그룹", example = "KNOWLEDGE")
    private String mapAsstG;
    
    /**
     * 어시스트 명 (MAP)
     */
    @Schema(description = "매핑 어시스트 명", example = "매핑 어시스트")
    private String mapAssetNm;
    
    /**
     * 마이그레이션 매핑 명
     */
    @Schema(description = "마이그레이션 매핑 명", example = "매핑명")
    private String mapMigMapNm;
    
    /**
     * 개발 상세 내용
     */
    @Schema(description = "개발 상세 내용")
    private String mapDvlpDtlCtnt;

    /**
     * 운영 상세 내용
     */
    @Schema(description = "운영 상세 내용")
    private String mapUnyungDtlCtnt;
}

