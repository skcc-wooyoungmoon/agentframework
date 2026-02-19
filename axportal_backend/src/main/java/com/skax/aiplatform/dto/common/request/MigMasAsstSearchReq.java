package com.skax.aiplatform.dto.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운영 이행이력 추가정보 조회 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "운영 이행이력 추가정보 조회 요청")
public class MigMasAsstSearchReq {

    /**
     * SEQUENCE 번호
     */
    @Schema(description = "SEQUENCE 번호", example = "1")
    private Integer sequence;

    /**
     * UUID
     */
    @Schema(description = "UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuid;

    /**
     * 이행 대상 검색어 (asst_nm LIKE)
     */
    // @Schema(description = "이행 대상 검색어", example = "서비스")
    // private String asstNm;

    /**
     * 이행 분류 필터 (asst_g)
     */
    @Schema(description = "이행 분류", example = "KNOWLEDGE")
    private String asstG;
}
