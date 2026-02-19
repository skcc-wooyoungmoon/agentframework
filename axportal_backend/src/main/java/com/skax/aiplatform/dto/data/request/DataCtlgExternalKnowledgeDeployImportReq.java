package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 지식 이행 Import 요청 DTO
 * 
 * <p>지식을 이행하기 위해 Import하는 요청 데이터를 담는 DTO입니다.</p>
 * 
 * @author system
 * @since 2025-01-20
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지식 이행 Import 요청")
public class DataCtlgExternalKnowledgeDeployImportReq {

    /**
     * DB 지식 정보 (Export에서 받은 정보)
     */
    @Schema(description = "DB 지식 정보", required = true)
    private KnowledgeInfo knowledgeInfo;

    /**
     * ADXP API 응답 (Export에서 받은 JSON 전체)
     */
    @Schema(description = "ADXP API 응답 (JSON 전체)", required = true)
    private Map<String, Object> adxpResponse;

    /**
     * 벡터DB 정보 (Export에서 받은 Import 형식)
     */
    @Schema(description = "벡터DB 정보 (Import 형식)")
    private Map<String, Object> vectorDbInfo;

    /**
     * 지식 정보 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeInfo {
        private String kwlgId;
        private String kwlgNm;
        private String exKwlgId;
        private String chunkId;
        private String prmtCtnt;
        private String modelId;
        private String dataSetId;
        private String dataSetNm;
        private String idxNm;
        private String consumerGrpNm;
        private BigDecimal fileLoadJinhgRt;
        private BigDecimal chunkJinhgRt;
        private BigDecimal dbLoadJinhgRt;
        private BigDecimal dvlpSynchYn;
        private BigDecimal unyungSynchYn;
        private String kafkaCntrStatus;
        private String dataPipelineExeId;
        private String dataPipelineLoadStatus;
        private String dataPipelineSynchStatus;
        private LocalDateTime idxMkSttAt;
        private LocalDateTime idxMkEndAt;
        private LocalDateTime fstCreatedAt;
        private String createdBy;
        private LocalDateTime lstUpdatedAt;
        private String updatedBy;
    }
}

