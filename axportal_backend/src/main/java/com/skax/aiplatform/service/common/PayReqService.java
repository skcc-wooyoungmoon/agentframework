package com.skax.aiplatform.service.common;

import com.skax.aiplatform.dto.common.request.ApprovalCallBakReq;
import com.skax.aiplatform.dto.common.request.ApprovalInfo;
import com.skax.aiplatform.dto.common.request.PayApprovalReq;

public interface PayReqService {

    int approvalRequest(ApprovalInfo approvalInfo);

    int approval(PayApprovalReq approvalInfo);

    int approvalCallBack(ApprovalCallBakReq approvalCallBakReq);

    void cancelRequests(String approvaDocumentId);

    void cancelRequest(String gyljId, String approvaDocumentId);

    boolean isApprovalInProgress(String approvalUniqueKey);
}
