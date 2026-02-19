package com.skax.aiplatform.client.shinhan.dto;

import com.skax.aiplatform.dto.common.response.SwingResCommon;
import com.skax.aiplatform.dto.common.response.ApprovalResData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRes {
    private SwingResCommon common;
    private ApprovalResData data;
}
