package com.skax.aiplatform.dto.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalKnowledgeChunksReq {
    private String indexName;
    private String docPathAnony;
    private Integer page; // 1-based
    private Integer countPerPage;
}


