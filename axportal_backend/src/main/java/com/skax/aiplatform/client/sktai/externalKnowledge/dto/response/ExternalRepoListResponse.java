package com.skax.aiplatform.client.sktai.externalKnowledge.dto.response;

import java.util.List;

import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External Knowledge Repository 목록 응답 DTO
 * 
 * <p>ADXP API의 External Knowledge Repository 목록 조회 응답을 담는 객체입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "External Knowledge Repository 목록 응답")
public class ExternalRepoListResponse {

    /**
     * External Knowledge Repository 목록
     * 
     * <p>현재 페이지에 포함된 External Knowledge Repository들의 목록입니다.</p>
     */
    @Schema(description = "External Knowledge Repository 목록")
    private List<ExternalRepoInfo> data;

    /**
     * 페이지네이션 정보
     * 
     * <p>목록 조회에 대한 페이지네이션 메타데이터입니다.
     * 총 개수, 현재 페이지, 페이지 링크 등의 정보를 포함합니다.</p>
     */
    @Schema(description = "페이징 및 메타데이터 정보")
    private Payload payload;

    /**
     * 다음 페이지 존재 여부
     */
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
}



