package com.skax.aiplatform.client.udp.gateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 문서 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfo {
    
    @JsonProperty("dataset_cd")
    private String datasetCd;
    
    @JsonProperty("dataset_name")
    private String datasetName;
    
    @JsonProperty("doc_uuid")
    private String docUuid;
    
    @JsonProperty("doc_title")
    private String docTitle;
    
    @JsonProperty("doc_keyword")
    private String docKeyword;
    
    @JsonProperty("doc_summary")
    private String docSummary;
    
    @JsonProperty("create_date")
    private String createDate;
    
    @JsonProperty("last_mod_date")
    private String lastModDate;
    
    @JsonProperty("doc_path_anonym_md")
    private String docPathAnonymMd;
    
    @JsonProperty("attach_doc_uuids")
    private List<String> attachDocUuids;
    
    @JsonProperty("attach_parent_doc_uuid")
    private String attachParentDocUuid;
    
    @JsonProperty("origin_metadata")
    private Map<String, Object> originMetadata;
}

