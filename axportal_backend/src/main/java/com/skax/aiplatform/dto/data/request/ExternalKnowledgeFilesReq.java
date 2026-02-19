package com.skax.aiplatform.dto.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalKnowledgeFilesReq {
    private String indexName;
    private Integer page; // 1-based
    private Integer countPerPage;
    private String search;
    private String uuid;
}
