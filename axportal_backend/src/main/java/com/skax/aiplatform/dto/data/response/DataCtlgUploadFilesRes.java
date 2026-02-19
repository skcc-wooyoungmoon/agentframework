package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터 카탈로그 파일 업로드 후, 저장 정보")
public class DataCtlgUploadFilesRes {
    private List<Item> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private String fileName;
        private String tempFilePath;
        private Map<String, Object> fileMetadata;
        private Map<String, Object> knowledgeConfig;
    }
}
