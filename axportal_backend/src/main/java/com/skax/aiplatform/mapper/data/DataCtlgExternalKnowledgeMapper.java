package com.skax.aiplatform.mapper.data;

import com.skax.aiplatform.dto.data.request.ExternalKnowledgeChunksReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeFilesReq;
import org.springframework.stereotype.Component;

@Component
public class DataCtlgExternalKnowledgeMapper {

    // 단순 패스스루 변환
    public String toIndexName(ExternalKnowledgeFilesReq req) {
        return req.getIndexName();
    }

    public Integer toPage(ExternalKnowledgeFilesReq req) {
        return req.getPage();
    }

    public Integer toCountPerPage(ExternalKnowledgeFilesReq req) {
        return req.getCountPerPage();
    }

    public String toIndexName(ExternalKnowledgeChunksReq req) {
        return req.getIndexName();
    }

    public String toDocPathAnony(ExternalKnowledgeChunksReq req) {
        return req.getDocPathAnony();
    }

    public Integer toPage(ExternalKnowledgeChunksReq req) {
        return req.getPage();
    }

    public Integer toCountPerPage(ExternalKnowledgeChunksReq req) {
        return req.getCountPerPage();
    }

    // 단순 파라미터를 요청 DTO로 구성
    public ExternalKnowledgeFilesReq toFilesReq(String indexName, Integer page, Integer countPerPage) {
        return ExternalKnowledgeFilesReq.builder()
                .indexName(indexName)
                .page(page)
                .countPerPage(countPerPage)
                .build();
    }

    public ExternalKnowledgeChunksReq toChunksReq(String indexName, String docPathAnony, Integer page,
            Integer countPerPage) {
        return ExternalKnowledgeChunksReq.builder()
                .indexName(indexName)
                .docPathAnony(docPathAnony)
                .page(page)
                .countPerPage(countPerPage)
                .build();
    }
}
