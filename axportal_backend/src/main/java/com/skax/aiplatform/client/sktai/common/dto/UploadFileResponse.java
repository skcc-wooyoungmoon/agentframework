package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 업로드 파일 정보 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "업로드 파일 정보")
public class UploadFileResponse {
    
    @JsonProperty("file_id")
    @Schema(description = "파일 ID")
    private String fileId;
    
    @JsonProperty("filename")
    @Schema(description = "파일명")
    private String filename;
    
    @JsonProperty("size")
    @Schema(description = "파일 크기")
    private Long size;
    
    @JsonProperty("content_type")
    @Schema(description = "컨텐츠 타입")
    private String contentType;
    
    @JsonProperty("upload_url")
    @Schema(description = "업로드 URL")
    private String uploadUrl;
}
