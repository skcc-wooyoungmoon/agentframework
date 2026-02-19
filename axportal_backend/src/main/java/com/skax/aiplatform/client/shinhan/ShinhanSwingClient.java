package com.skax.aiplatform.client.shinhan;

import com.skax.aiplatform.client.shinhan.config.ShinhanClientConfig;
import com.skax.aiplatform.client.shinhan.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "shinhan-approval-client",
        url = "${gw.url}",
        configuration = ShinhanClientConfig.class
)
public interface ShinhanSwingClient {

    @Operation(summary = "승인요청", description = "승인요청을 보냅니다.")
    @PostMapping(value = "/cap/v1/request-approval", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApprovalRes approvalRequest(@RequestBody ApprovalReq approvalReq);

    @Operation(summary = "승인요청", description = "승인요청을 보냅니다.")
    @PostMapping(value = "/cap/v1/cancel-approval", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApprovalRes cancelRequest(@RequestBody ApprovalReq approvalReq);

    @Operation(summary = "SMS 인증코드 발급 요청", description = "SMS 인증코드 발급 요청을 보냅니다.")
    @PostMapping(value = "/cau/v1/owned-media-auth", consumes = MediaType.APPLICATION_JSON_VALUE)
    SmsAuthRes smsAuthCodeRequest(@RequestBody SmsAuthReq smsAuthReq);

    @Operation(summary = "SMS 인증코드 확인 요청", description = "SMS 인증코드 확인 요청을 보냅니다.")
    @PostMapping(value = "/cau/v1/check-auth", consumes = MediaType.APPLICATION_JSON_VALUE)
    SmsAuthCheckRes smsAuthCodeCheckRequest(@RequestBody SmsAuthCheckReq smsAuthCheckReq);
}
