package com.skax.aiplatform.client.udp.gateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문서 검색 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchResponse {
    
    @JsonProperty("total_count")
    private Long totalCount;
    
    @JsonProperty("page")
    private Long page;
    
    @JsonProperty("result_lists")
    private List<DocumentInfo> resultLists;
}

