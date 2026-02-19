package com.skax.aiplatform.dto.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteModelCtlgBulkRes {
    private int totalCount;
    private int successCount;
    private int failCount;
}
