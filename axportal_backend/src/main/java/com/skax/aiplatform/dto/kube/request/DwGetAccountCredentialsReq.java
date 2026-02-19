package com.skax.aiplatform.dto.kube.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwGetAccountCredentialsReq {
    //@NotBlank(message = "Emp No 및 Account ID 는 필수입니다")
    private String empNo;
    private String accountId;
}
