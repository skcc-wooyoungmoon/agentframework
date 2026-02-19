package com.skax.aiplatform.dto.kube.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwGetAccountCredentialsRes {
    private String accountId;
    private String password;
    private String host;
    private String port;
    private String database;
    private String sessionLabel;
    private String accountRole;
}
