package com.skax.aiplatform.mapper.common;

import com.skax.aiplatform.dto.common.response.ApprovalCallbackInfo;
import com.skax.aiplatform.dto.common.response.ApprovalCancelInfo;
import com.skax.aiplatform.dto.common.response.ApprovalProjectInfo;
import com.skax.aiplatform.dto.common.response.ApprovalUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApprovalMapper {
    ApprovalCallbackInfo findApprovalCallbackInfo(@Param("gyljRespId") String gyljRespId);

    List<ApprovalCancelInfo> findApprovalCancelInfo(@Param("documentId") String documentId);

    ApprovalUserInfo findApprovalUsrInfo(@Param("projectId") String projectId);

    ApprovalProjectInfo findApprovalProjectInfo(Map<String, Object> params);

}
