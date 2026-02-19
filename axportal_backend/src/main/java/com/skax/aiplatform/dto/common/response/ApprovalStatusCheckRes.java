package com.skax.aiplatform.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStatusCheckRes {
    private boolean inProgress;
}
