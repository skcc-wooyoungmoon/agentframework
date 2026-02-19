package com.skax.aiplatform.dto.common.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayApprovalRes {
    String approval_id;
    String status;
    String message;

}
