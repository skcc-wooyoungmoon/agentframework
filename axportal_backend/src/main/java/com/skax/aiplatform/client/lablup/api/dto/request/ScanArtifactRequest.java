package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 스캔 요청 DTO
 * 
 * <p>아티팩트 레지스트리에서 아티팩트를 스캔하기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanArtifactRequest {

    @Builder.Default
    @JsonProperty("limit")
    @Schema(description = "한 번에 조회할 아티팩트 수", example = "12")
    private Integer limit = 50;

    @Schema(description = "아티팩트 레지스트리 ID", example = "1234567890")
    @JsonProperty("registry_id")
    private String registryId;
    
    @Builder.Default
    @Schema(description = "아티팩트 타입", example = "MODAL, PACKAGE, IMAGE")
    @JsonProperty("artifact_type")
    private String artifactType = "MODAL";

    @Schema(description = "검색 키워드", example = "model")
    @JsonProperty("search")
    private String search;
    
    // /**
    //  * 스캔 대상 아티팩트 레지스트리 URL
    //  */
    // private String registryUrl;
    
    // /**
    //  * 아티팩트 이름
    //  */
    // private String artifactName;
    
    // /**
    //  * 스캔 타입 (vulnerability, license, quality)
    //  */
    // private String scanType;
    
    // /**
    //  * 스캔 옵션
    //  */
    // private ScanOptions options;
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class ScanOptions {
    //     /**
    //      * 심도 있는 스캔 여부
    //      */
    //     private boolean deepScan;
        
    //     /**
    //      * 병렬 스캔 수행 여부
    //      */
    //     private boolean parallel;
        
    //     /**
    //      * 스캔 제한 시간 (초)
    //      */
    //     private int timeoutSeconds;
    // }
}