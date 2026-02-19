package com.skax.aiplatform.dto.home.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeDeleteReq {
    private String ideId;
    private String userId;
    private String ide;
    private String ingressUrl;
}
