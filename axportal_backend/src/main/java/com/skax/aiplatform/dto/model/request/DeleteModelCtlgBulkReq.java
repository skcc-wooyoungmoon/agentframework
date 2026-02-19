package com.skax.aiplatform.dto.model.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteModelCtlgBulkReq {

    @Schema(description = "삭제 항목 목록", example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"123e4567-e89b-12d3-a456-426614174001\"]")
    private List<DeleteModelCtlgBulkItem> items;  
    

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteModelCtlgBulkItem {
        @Schema(description = "삭제 타입", example = "model", allowableValues = {"self-hosting", "serverless"})
        private String type;

        @Schema(description = "삭제 대상 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private String id;
    }
}
