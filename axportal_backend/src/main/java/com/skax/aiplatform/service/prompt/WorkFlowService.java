package com.skax.aiplatform.service.prompt;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.WorkFlowBatchDeleteReq;
import com.skax.aiplatform.dto.prompt.request.WorkFlowCreateReq;
import com.skax.aiplatform.dto.prompt.request.WorkFlowUpdateReq;
import com.skax.aiplatform.dto.prompt.response.WorkFlowCreateRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowDeleteRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowVerListByIdRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkFlowService {
    PageResponse<WorkFlowRes> getWorkFlowList(Pageable pageable, String search, String tag, String sort);

    List<String> getWorkFlowTagList();

    WorkFlowCreateRes createWorkflow(WorkFlowCreateReq request);

    WorkFlowVerListByIdRes getWorkFlowVerListById(String workFlowId);

    WorkFlowRes getWorkFlowLatestVerById(String workFlowId);

    WorkFlowRes getWorkFlowVerById(String workFlowId, Integer version);

    void updateWorkFlow(String workflowId, WorkFlowUpdateReq request);

    void deleteWorkFlowById(String workflowId);
    
    WorkFlowDeleteRes deleteWorkFlowsByIds(WorkFlowBatchDeleteReq request);

    void makeWorkFlowPublic(String workflowId);
}
